package com.example.android.wallpics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog signingUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth=FirebaseAuth.getInstance();

        signingUp=new ProgressDialog(this);
        final EditText newName = (EditText) findViewById(R.id.name);
        final EditText newMail = (EditText) findViewById(R.id.new_mail);
        final EditText newPass = (EditText) findViewById(R.id.new_pass);
        final EditText conPass = (EditText) findViewById(R.id.con_pass);



        Button signUp = (Button) findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signingUp.setMessage("Signing You Up...");
                signingUp.show();
                final String name = newName.getText().toString().trim();
                String mail = newMail.getText().toString().trim();
                String pass = newPass.getText().toString();
                String cPass = conPass.getText().toString();
                Log.i("SignUp", "onClick: "+mail+"  "+pass+"  ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals(pass, cPass)) {
                        mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                signingUp.dismiss();
                                Toast.makeText(SignUpActivity.this, "Welcome To WallPics, " + name + "!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        });
                    } else {
                        signingUp.dismiss();
                        Toast.makeText(SignUpActivity.this, "Your Passwords Doesn't Match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
