package com.anshul.shieldconvo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MD5Activity extends AppCompatActivity {


    ///              THIS TECHNIQUE IS USED FOR GETTING A SECURE PASSWORD
    ///this is hashing not ENCRYPTION
    ///we cannot decrypr this, the only thing we can do is just try to match a millions of inputs and wait for the correct one
    //generrally use for checking data loss and for making passsword

    String password="";
    Button hash_button,clear;
    EditText input_text;
    TextView output_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md5);


        input_text=findViewById(R.id.input_text);
        output_text=findViewById(R.id.output_text);
        hash_button=findViewById(R.id.clear_button);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Hash Button
        hash_button.setOnClickListener(view -> {
            password=input_text.getText().toString();
            if(!password.isEmpty())
                output_text.setText(hashPassword(password));
            else
                Toast.makeText(MD5Activity.this,"Empty output",Toast.LENGTH_SHORT).show();
        });
    }

    public String hashPassword(String password)
    {
        String generatedPassword;
        try{
            MessageDigest md=MessageDigest.getInstance("MD5");
            //Returns a MessageDigest object that implements the specified digest algorithm.
            md.update(password.getBytes());
            //Updates the digest using the specified array of bytes.
            byte[] bytes=md.digest();
            //Completes the hash computation by performing final operations such as padding.
            StringBuilder sb=new StringBuilder();
            for (byte aByte : bytes) {
                //converting byte to string
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 32).substring(1));

            }
            generatedPassword=sb.toString();

            return generatedPassword;

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    //speech recognition
    public void getspeechinput(View view) {

        Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, 1001);
        }
        catch (Exception e) {
            Toast
                    .makeText(MD5Activity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                input_text.setText(res.get(0));
            }
        }
    }
}