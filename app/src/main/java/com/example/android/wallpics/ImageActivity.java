package com.example.android.wallpics;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    private ArrayList<String> imageList=(ArrayList<String>) getIntent().getSerializableExtra("mylist");
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_image);
        String url=getIntent().getExtras().getString("download");
        Animation out= AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation in= AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        ImageSwitcher bigImage=(ImageSwitcher) findViewById(R.id.big_image);
        bigImage.setFactory((ViewSwitcher.ViewFactory) this);
        bigImage.setInAnimation(in);
        bigImage.setOutAnimation(out);
        Uri imageUri=Uri.parse(url);
        imageList.indexOf(url);
        ProgressBar bar = new ProgressBar(this);
        bar.setVisibility(View.VISIBLE);
        Glide.with(getApplicationContext()).load(imageUri).into((ImageView) bigImage.getCurrentView());
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
                },5000);
            }
        });
        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        Button pre=(Button) findViewById(R.id.pre);
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bar.setVisibility(View.GONE);
    }
}
