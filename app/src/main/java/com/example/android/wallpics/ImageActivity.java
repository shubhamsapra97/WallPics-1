package com.example.android.wallpics;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    Uri imageUri;
    String url;
    int i=0;
    private DatabaseReference databaseImageCount;
    Integer imgCount;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_image);
        url=getIntent().getExtras().getString("download");
        final ArrayList<String> images=getIntent().getExtras().getStringArrayList("imageList");
        final ImageView bigImage=(ImageView) findViewById(R.id.full_image);
        if (images != null) {
            imageUri=Uri.parse(images.get(images.indexOf(url)));
        }
        ProgressBar bar = new ProgressBar(this);
        bar.setVisibility(View.VISIBLE);
        databaseImageCount= FirebaseDatabase.getInstance().getReference().child("ImageCount");
        databaseImageCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgCount = dataSnapshot.getValue(Integer.class);
                Log.i("ImageActivity: ", "onDataChange: "+imgCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Button next=(Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images != null) {
                    i++;
                    imageUri=Uri.parse(images.get(images.indexOf(url)+i));
                    Log.i("ImageActivity: ", "onClick: "+i);
                }
                Glide.with(getApplicationContext()).load(imageUri).into(bigImage);
            }
        });
        Glide.with(getApplicationContext()).load(imageUri).into(bigImage);
        bigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().getDecorView().setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
                    }
                },3000);
            }
        });
        bar.setVisibility(View.GONE);
    }
}
