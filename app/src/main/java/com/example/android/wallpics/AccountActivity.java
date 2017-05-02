package com.example.android.wallpics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FirebaseUser cUser= FirebaseAuth.getInstance().getCurrentUser();
        ImageView profileImage=(ImageView) findViewById(R.id.profile_image);
        if (cUser != null) {
            Glide.with(getApplicationContext()).load(cUser.getPhotoUrl()).into((ImageView)findViewById(R.id.profile_image));
        }
        else
            Glide.with(getApplicationContext()).load(R.drawable.image).into(profileImage);
    }
}
