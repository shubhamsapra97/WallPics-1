package com.example.android.wallpics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    private EditText emailText;
    private EditText passText;
    private Button signInBtn;
    private ProgressDialog logInProgress;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleBtn;
    private static final int RC_SIGN_IN=1;

    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener;

    private static final String TAG="SignInActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth=FirebaseAuth.getInstance();
        logInProgress=new ProgressDialog(this);
        mAuthListener=new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("SignIn Activity:", "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(SignInActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        emailText=(EditText) findViewById(R.id.email_field);
        passText=(EditText) findViewById(R.id.password_field);
        mGoogleBtn=(SignInButton) findViewById(R.id.google_btn);
        signInBtn=(Button) findViewById(R.id.sign_in_button);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInProgress.setMessage("Signing In...");
                logInProgress.show();
                String email=emailText.getText().toString().trim();
                String pass=passText.getText().toString().trim();
                if(TextUtils.isEmpty(email)&&TextUtils.isEmpty(pass))
                {
                    Toast.makeText(SignInActivity.this, "Fill up the details to continue...", Toast.LENGTH_SHORT).show();
                    logInProgress.dismiss();
                }
                else if (!TextUtils.isEmpty(email)&&TextUtils.isEmpty(pass))
                {
                    Toast.makeText(SignInActivity.this, "Enter your password in the required field...", Toast.LENGTH_SHORT).show();
                    logInProgress.dismiss();
                }
                else if (!TextUtils.isEmpty(pass)&&TextUtils.isEmpty(email))
                {
                    Toast.makeText(SignInActivity.this, "Enter your email id in the required field...", Toast.LENGTH_SHORT).show();
                    logInProgress.dismiss();
                }
                else
                {
                    mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                logInProgress.dismiss();
                                startActivity(new Intent(SignInActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                            if(!task.isSuccessful()){
                                logInProgress.dismiss();
                                Toast.makeText(getApplicationContext(),"Sign In Failed",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient=new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(SignInActivity.this, "Check Your Connection and try again later...", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                logInProgress.setMessage("Signing In...");
                logInProgress.show();
            }
        });
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                logInProgress.dismiss();
                Toast.makeText(SignInActivity.this, "Google Sign In Failed...", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        logInProgress.dismiss();

                        if (task.isSuccessful()) {
                            startActivity(new Intent(SignInActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                        else
                        {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }
}
