package com.anshul.shieldconvo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESActivity extends AppCompatActivity {

    EditText input_text, password_text;
    TextView output_text;
    Button enc, dec,clear,reset,send;
    String outputstring="";
    String AES = "AES";
    public  static String pwdtext="qwerty";
    String inptext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aesactivity);

        input_text = findViewById(R.id.input_text);
        //  password_text = (EditText) findViewById(R.id.password_text);
        output_text = findViewById(R.id.output_text);
        enc = findViewById(R.id.encrypt);
        dec = findViewById(R.id.decrypt);
        clear = findViewById(R.id.clear_button);
        reset = findViewById(R.id.reset);
        send = findViewById(R.id.send);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //encrypt button
        enc.setOnClickListener(view -> {
            try {
                inptext = input_text.getText().toString();
                //  pwdtext = password_text.getText().toString();
                outputstring = encrypt(inptext, pwdtext);
                output_text.setText(outputstring);
                //make a toast her e to say encypted successfully
                //  Toast.makeText(MainActivity.this,"Encrypted Successfully!!!",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        //Decrypt Button
        dec.setOnClickListener(view -> {
            try {
                inptext = input_text.getText().toString();
                // pwdtext = password_text.getText().toString();
                outputstring = decrypt(inptext, pwdtext);// outputstring was there in place of inptext
                output_text.setText(outputstring);
                //make a toast her e to say deccypted successfully
                //  Toast.makeText(this,"decrypted Successfully!!!",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        //clear button
        clear.setOnClickListener(view -> {
            try{
                input_text.setText(" ");
                output_text.setText("");
                input_text.setHint("enter message");
                //  Toast.makeText( this,"Cleared Successfully!!!",Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        //reset key
        reset.setOnClickListener(view -> {
            try{
                Intent intent = new Intent( AESActivity.this, ResetPasswordActivity.class );
                startActivity(intent);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        //send button
        send.setOnClickListener(view -> {
            if(!outputstring.isEmpty()){
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, outputstring);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);}
            else
            {
                Toast.makeText(AESActivity.this,"empty output",Toast.LENGTH_SHORT).show();
            }
        });

    }


    private String encrypt(String data, String password_text) throws Exception {
        SecretKeySpec key = generateKey(password_text);

        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");//creating an object
        c.init(Cipher.ENCRYPT_MODE, key);//initialisation
        byte[] encVal = c.doFinal(data.getBytes(StandardCharsets.UTF_8));
        //Encrypts or decrypts data in a single-part operation, or finishes a multiple-part operation
        //it is binary to text encoding scheme
        //Base64-encode the given data and return a newly allocated String with the result.
        //Essentially each 6 bits of the input is encoded in a 64-character alphabet.
        //The "standard" alphabet uses A-Z, a-z, 0-9 and + and /, with = as a padding character. There are URL-safe variants.
        return Base64.encodeToString(encVal, Base64.DEFAULT);

    }

    private String decrypt(String data, String password_text) throws Exception {
        SecretKeySpec key = generateKey(password_text);
        Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedvalue = Base64.decode(data, Base64.DEFAULT);  //Previously in base64 format, So decode it
        byte[] decvalue = c.doFinal(decodedvalue);                      //final decoding
        return new String(decvalue, StandardCharsets.UTF_8);
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");//for using hash function SHA-256 gives hash value
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);//process kr bytes array ko
        byte[] key = digest.digest();////Completes the hash computation by performing final operations such as padding.
        return new SecretKeySpec(key, "AES");
    }

    //Speech Recognition
    public void getspeechinput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
        try {
            startActivityForResult(intent, 1001);
        }
        catch (Exception e) {
            Toast.makeText(AESActivity.this, " " + e.getMessage(),Toast.LENGTH_SHORT).show();
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