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
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import javax.crypto.Cipher;

public class RSA_Activity extends AppCompatActivity {

    String temp;
    TextView output;
    EditText input;
    Button enc,dec,clear,send;
    String tosend="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa);

        output= findViewById(R.id.output_text);
        input= findViewById(R.id.input_text);
        enc= findViewById(R.id.encrypt);
        dec= findViewById(R.id.decrypt);
        clear= findViewById(R.id.clear_button);
        send= findViewById(R.id.send);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // generate a new public/private key pair to test with (note. you should only do this once and keep them!)
        //getting key pair
        KeyPair kp = getKeyPair();

        //public key
        PublicKey publicKey = kp.getPublic();
        final byte[] publicKeyBytes = publicKey.getEncoded();
        final String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));

        //private key
        PrivateKey privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        final String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));


        //encrypt button
        enc.setOnClickListener(view -> {
            temp=input.getText().toString();
            String encrypted = encryptRSAToString(temp, publicKeyBytesBase64);
            output.setText(encrypted);
            tosend=encrypted;
        });

        //decrypt button
        dec.setOnClickListener(view -> {
            temp=input.getText().toString();
            String decrypted = decryptRSAToString(temp, privateKeyBytesBase64);
            output.setText(decrypted);
            tosend=decrypted;
        });

        //clear button
        clear.setOnClickListener(view -> {
            try{
                input.setText(" ");
                output.setText("");
                //  Toast.makeText( this,"Cleared Successfully!!!",Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });

        //send button
        send.setOnClickListener(view -> {
            if(!tosend.isEmpty()) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, tosend);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
            else
            {
                Toast.makeText(RSA_Activity.this,"empty output",Toast.LENGTH_SHORT).show();
            }
        });

    }


    public static KeyPair getKeyPair() {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kp;
    }

    //Encryption
    public static String encryptRSAToString(String clearText, String publicKey) {
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            //this function is used converting back to Public key
            //Format for storing public key
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT)); //First decode public key as it is in base64
            Key key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            //As we are sending small bits of data then attacker can detect
            //while using the technique of OAEP padding . It add padding-Boggus data to message to complete block
            // such it become impossible  to crack
            //MGF- Mask Generating Function - it takes arbitrary size input and give arbitrary size output
            //OAEP - it takes two hash as input one is SHA and other is MGF
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64.replaceAll("([\\r\\n])", "");//new line or carriage return replace kar and send kr
    }

    public static String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }

    public void getspeechinput(View view) {

        Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, 1001);
        }
        catch (Exception e) {
            Toast.makeText(RSA_Activity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                input.setText(res.get(0));
            }
        }
    }
}