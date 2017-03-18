package com.example.android.wallpics;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        String url=getIntent().getExtras().getString("download");
        ImageView bigImage=(ImageView) findViewById(R.id.big_image);
        Uri imageUri=Uri.parse(url);
        ProgressBar bar = new ProgressBar(this);
        bar.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext()).load(imageUri).into(bigImage);
        bar.setVisibility(View.GONE);
    }
}
