package com.example.android.wallpics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog signingUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        signingUp = new ProgressDialog(this);
        final EditText newName = (EditText) findViewById(R.id.name);
        final EditText newMail = (EditText) findViewById(R.id.new_mail);
        final EditText newPass = (EditText) findViewById(R.id.new_pass);
        final EditText conPass = (EditText) findViewById(R.id.con_pass);
        Button signUp = (Button) findViewById(R.id.sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signingUp.setMessage(getString(R.string.signing_up_message));
                signingUp.show();
                final String name = newName.getText().toString().trim();
                String mail = newMail.getText().toString().trim();
                String pass = newPass.getText().toString();
                String cPass = conPass.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mail) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(cPass)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (Objects.equals(pass, cPass)) {
                            mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null)
                                            user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build());
                                        signingUp.dismiss();
                                        Toast.makeText(SignUpActivity.this, "Welcome To WallPics, " + name + "!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    } else
                                        Toast.makeText(SignUpActivity.this, "Sign Up Failed...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            signingUp.dismiss();
                            Toast.makeText(SignUpActivity.this, R.string.signing_up_mismatch, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    signingUp.dismiss();
                    Toast.makeText(SignUpActivity.this, R.string.empty_signing_up, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
