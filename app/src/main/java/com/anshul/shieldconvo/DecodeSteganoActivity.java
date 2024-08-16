package com.anshul.shieldconvo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;

import java.io.IOException;

public class DecodeSteganoActivity extends AppCompatActivity implements TextDecodingCallback {

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Decode Class";
    // UI components
    private TextView decoded_message;
    private ImageView imageView;
    private EditText secret_key;
    // Objects needed for decoding
    private TextDecoding textDecoding;
    private ImageSteganography imageSteganography;
    private Uri filepath;
    private Bitmap original_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_stegano);

        decoded_message = findViewById(R.id.message);
        imageView = findViewById(R.id.imageview);
        secret_key = findViewById(R.id.secret_key);

        Button choose_image_button = findViewById(R.id.choose_image_button);
        Button decode_button = findViewById(R.id.decode_button);

        // Choose image button
        choose_image_button.setOnClickListener(view -> ImageChooser());

        // Decode button
        decode_button.setOnClickListener(view -> {
            decoded_message.setText("");
            if (filepath != null) {
                // ImageSteganography Object instantiation
                // passing secret key
                imageSteganography = new ImageSteganography(secret_key.getText().toString(), original_image);

                // TextDecoding object Instantiation
                textDecoding = new TextDecoding(DecodeSteganoActivity.this, DecodeSteganoActivity.this);

                // Executing the decoding
                textDecoding.execute(imageSteganography);
            }
        });
    }

    private void ImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Image set to imageView
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

    // Override method of TextDecodingCallback
    @Override
    public void onStartTextEncoding() {
        // Whatever you want to do at the start of text decoding
        Toast.makeText(this, "Decoding Starting", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        if (result != null && result.isDecoded()) {
            decoded_message.setText(result.getMessage());
        } else {
            decoded_message.setText(R.string.no_image_msg);
        }
    }

    // Decoding Completed

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
