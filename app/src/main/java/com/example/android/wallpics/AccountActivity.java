package com.example.android.wallpics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
        ImageView profileImage = (ImageView) findViewById(R.id.profile_image);
        TextView profileName = (TextView) findViewById(R.id.profile_name);
        String name = "User not Signed In";
        if (cUser != null) {
            Glide.with(getApplicationContext()).load(cUser.getPhotoUrl()).into((ImageView) findViewById(R.id.profile_image));
            name = cUser.getDisplayName();
        } else
            Glide.with(getApplicationContext()).load(R.drawable.image).into(profileImage);
        profileName.setText(name);
    }

}
