package com.example.android.wallpics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passText;
    private Button signInBtn;

    private FirebaseAuth mAuth;

    private static final String TAG="SignInActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth=FirebaseAuth.getInstance();

        emailText=(EditText) findViewById(R.id.email_field);
        passText=(EditText) findViewById(R.id.password_field);
        signInBtn=(Button) findViewById(R.id.sign_in_button);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailText.getText().toString();
                String pass=passText.getText().toString();
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(SignInActivity.this,MainActivity.class));
                            finish();
                        }
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Sign In Failed",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}
