package com.mwebia.talkie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button signIn;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialise variables;
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        signIn = findViewById(R.id.signIn);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logIn();
            }
        });

        findViewById(R.id.openSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }



    private void logIn() {

        findViewById(R.id.signin_progressbar).setVisibility(View.INVISIBLE);
        signIn.setEnabled(false);
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (email.getText().toString().trim().isEmpty()) {

                    email.setError("Email is required");

                }else if (password.getText().toString().trim().isEmpty()) {

                    password.setError("Enter Your password");

                }else {

                    String user_email = "";
                    String user_password = "";

                    try {

                        user_email = URLEncoder.encode(email.getText().toString().trim(),"UTF-8");
                        user_password = URLEncoder.encode(password.getText().toString().trim(),"UTF-8");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }
                    String url = "https://penguinsocialappdemo.000webhostapp.com/login.php?email="+user_email+
                            "&password="+user_password;
                    try {

                        new AccessDatabase(LoginActivity.this).execute(url);

                    }catch (Exception ex){

                        ex.printStackTrace();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                Log.i("firebase login error",e.getMessage());
                findViewById(R.id.signin_progressbar).setVisibility(View.INVISIBLE);
                signIn.setEnabled(true);
            }
        });

    }
}
