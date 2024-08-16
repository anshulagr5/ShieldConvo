package com.anshul.shieldconvo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText old,newpwd,cnfpwd;
    Button save;
    String oldpassword,newpassword,cnfpassword;
    String pwd= AESActivity.pwdtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        old= findViewById(R.id.old);


        newpwd= findViewById(R.id.newp);


        cnfpwd= findViewById(R.id.cnfpwd);


        save= findViewById(R.id.save);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        save.setOnClickListener(view -> {
            oldpassword=old.getText().toString();
            newpassword=newpwd.getText().toString();
            cnfpassword=cnfpwd.getText().toString();
            if(pwd.equals(oldpassword))
            {

                if(newpassword.equals(cnfpassword))
                {
                    AESActivity.pwdtext=newpassword;
                    System.out.println("updated successfully");
                    Toast.makeText(ResetPasswordActivity.this,"Updated Successfully!!",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(ResetPasswordActivity.this," password does not match",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                System.out.println(pwd);
                Toast.makeText(ResetPasswordActivity.this," password does not match",Toast.LENGTH_SHORT).show();

            }
        });

    }
}