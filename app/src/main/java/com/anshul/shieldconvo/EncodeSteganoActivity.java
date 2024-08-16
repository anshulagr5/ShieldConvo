package com.anshul.shieldconvo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextEncoding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncodeSteganoActivity extends AppCompatActivity implements TextEncodingCallback {

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Encode Class";
    //Created variables for UI
    private TextView whether_encoded;
    private ImageView imageView;
    private EditText message;
    private EditText secret_key;
    //Objects needed for encoding
    private TextEncoding textEncoding;
    private ImageSteganography imageSteganography;
    private ProgressDialog save;
    private Uri filepath;
    private File encodePath;
    Boolean isSave=false,isEncoded=false;
    //Bitmaps
    private Bitmap original_image;
    private Bitmap encoded_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_stegano);

        whether_encoded = findViewById(R.id.whether_encoded);

        imageView = findViewById(R.id.imageview);

        message = findViewById(R.id.message);
        secret_key = findViewById(R.id.secret_key);

        Button choose_image_button = findViewById(R.id.choose_image_button);
        Button encode_button = findViewById(R.id.encode_button);
        Button save_image_button = findViewById(R.id.save_image_button);
        Button img_send_button= findViewById(R.id.ImageSend);

        //Choose image button
        choose_image_button.setOnClickListener(view -> ImageChooser());

        img_send_button.setOnClickListener(view -> imageSend());

        encode_button.setOnClickListener(view -> {
            whether_encoded.setText("");
            if (filepath != null) {
                if (message.getText() != null) {

                    //ImageSteganography Object instantiation
                    //passing message and secret key
                    imageSteganography = new ImageSteganography(message.getText().toString(),
                            secret_key.getText().toString(),
                            original_image);
                    //TextEncoding object Instantiation
                    textEncoding = new TextEncoding(EncodeSteganoActivity.this, EncodeSteganoActivity.this);
                    //Executing the encoding
                    textEncoding.execute(imageSteganography);
                }
            }
        });

        save_image_button.setOnClickListener(view -> {
            if(!isEncoded){
                Toast.makeText(EncodeSteganoActivity.this, "Encode First", Toast.LENGTH_SHORT).show();
                return;
            }
            final Bitmap imgToSave = encoded_image;

            //Running new thread to save into internal storage
            Thread PerformEncoding = new Thread(() -> saveToInternalStorage(imgToSave));
            save = new ProgressDialog(EncodeSteganoActivity.this);
            save.setMessage("Saving, Please Wait...");
            save.setTitle("Saving Image");
            save.setIndeterminate(false);
            save.setCancelable(false);
            save.show();
            PerformEncoding.start();
        });

        checkAndRequestPermissions();
    }

    private void ImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    //share image button
    private void imageSend(){

        if(isSave && isEncoded){
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(EncodeSteganoActivity.this, BuildConfig.APPLICATION_ID + ".provider",encodePath));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share Image"));
            }catch (Exception e){
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Save or Encode Image First", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    //choose image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepath = data.getData();
            try {
                original_image = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                imageView.setImageBitmap(original_image);
            } catch (IOException e) {
                Log.d(TAG, "Error : " + e);
            }
        }

    }
    // Override method of TextEncodingCallback

    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do at the start of text encoding
        Toast.makeText(this, "Encoding Starting", Toast.LENGTH_SHORT).show();
    }

    //Encoding Completed
    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {

        //By the end of textEncoding

        if (result != null && result.isEncoded()) {
            isEncoded=true;
            encoded_image = result.getEncoded_image();
            whether_encoded.setText("Encoded");
            imageView.setImageBitmap(encoded_image);
        }
    }
    //Internal storage save
    private void saveToInternalStorage(Bitmap bitmapImage) {
        OutputStream fOut;
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String imageFileName = "Encoded_" + timeStamp + ".PNG";

        try {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (imageUri != null) {
                fOut = resolver.openOutputStream(imageUri);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                if (fOut != null) {
                    fOut.flush();
                    fOut.close();
                }
                isSave = true;
                encodePath = new File(imageUri.getPath());
                whether_encoded.post(() -> save.dismiss());
            } else {
                throw new FileNotFoundException("Failed to create new MediaStore record.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Permission Checking
    private void checkAndRequestPermissions() {
        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int manageExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (manageExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission " + permissions[i] + " denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}