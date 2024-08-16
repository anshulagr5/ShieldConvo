package com.anshul.shieldconvo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.biometric.BiometricPrompt;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.biometric.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {


    AlertDialog.Builder builder;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    LinearLayout mMainLayout;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        builder = new AlertDialog.Builder(this);

        Objects.requireNonNull(getSupportActionBar()).hide();

        mMainLayout= findViewById(R.id.main_layout);

        BiometricManager biometricManager=BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Device doesn't have fingerprint", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Hardware Not Working", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No fingerprint Assigned", Toast.LENGTH_SHORT).show();
                break;

        }

        Executor executor= ContextCompat.getMainExecutor(this);
        biometricPrompt=new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                mMainLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        promptInfo=new BiometricPrompt.PromptInfo.Builder().setTitle("Shield Convo")
                .setDescription("Use Fingerprint to authenticate")
                .setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);



    }
    public void stego(View view)
    {
        builder.setMessage("Do you want to use Steganography ?")
                .setCancelable(false)
                .setPositiveButton("Encode", (dialog, id) -> {

                    Intent intent = new Intent( MainActivity.this, EncodeSteganoActivity.class  );
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Now You Can Encode The Image",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Decode", (dialog, id) -> {
                    Intent intent = new Intent( MainActivity.this, DecodeSteganoActivity.class  );
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Now You Can Decode The Image",
                            Toast.LENGTH_SHORT).show();
                }).setNeutralButton("Exit", (dialog, i) -> dialog.cancel());

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Steganography");
        alert.show();
    }

    public void aes(View view)
    {
        Intent intent = new Intent( MainActivity.this, AESActivity.class);
        startActivity(intent);
    }
    public void des(View view)
    {
        Intent intent = new Intent( MainActivity.this, DESActivity.class  );
        startActivity(intent);
    }
    public void rsa(View view)
    {
        Intent intent = new Intent( MainActivity.this, RSA_Activity.class  );
        startActivity(intent);
    }
    public void md5(View view)
    {
        Intent intent = new Intent( MainActivity.this, MD5Activity.class  );
        startActivity(intent);
    }
}