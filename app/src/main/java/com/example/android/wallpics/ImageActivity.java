package com.example.android.wallpics;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    Uri imageUri;
    String url;
    Integer imgCount;

    private static final String TAG="Image Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_image);


        url = getIntent().getExtras().getString("download");
        final ArrayList<String> images = getIntent().getExtras().getStringArrayList("imageList");

        final ImageView bigImage = (ImageView) findViewById(R.id.full_image);
        if (images != null) {
            imageUri = Uri.parse(url);
        }

        DatabaseReference databaseImageCount = FirebaseDatabase.getInstance().getReference().child("ImageCount");
        databaseImageCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgCount = dataSnapshot.getValue(Integer.class);
                Log.i("ImageActivity: ", "onDataChange: " + imgCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button next = (Button) findViewById(R.id.next);
        next.setAlpha(0);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images != null && (images.indexOf(url) > 0)) {
                    url = images.get(images.indexOf(url) - 1);
                    imageUri = Uri.parse(url);
                    Glide.with(getApplicationContext()).load(imageUri).into(bigImage);
                } else
                    Toast.makeText(ImageActivity.this, "Change your way, This is the first one here.", Toast.LENGTH_SHORT).show();
            }
        });
        Button pre = (Button) findViewById(R.id.pre);
        pre.setAlpha(0);
        pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images != null) {
                    if (images.indexOf(url) < imgCount - 1) {
                        url = images.get(images.indexOf(url) + 1);
                        imageUri = Uri.parse(url);
                        Glide.with(getApplicationContext()).load(imageUri).into(bigImage);
                    } else
                        Toast.makeText(ImageActivity.this, "Change your way, This is the last one here.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Glide.with(getApplicationContext()).load(imageUri).into(bigImage);
        Log.i(TAG, "onCreate: "+imageUri);
        bigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                        }
                        else {
                            getWindow().getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
                        }
                    }
                }, 3000);
            }
        });
        ImageButton share=(ImageButton) findViewById(R.id.share_button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: shared");
            }
        });
        ImageButton download=(ImageButton) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap=BitmapFactory.decodeFile(imageUri.getPath());
                File folder = new File(Environment.getExternalStorageDirectory()+
                        "/WallPics/");
                if(!folder.exists())
                    folder.mkdir();
                File image= null;
                if (images != null)
                    image = new File(folder, "WallPics" + images.indexOf(url) + ".jpg");
                try {
                    FileOutputStream fileOS=new FileOutputStream(image);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOS);
                    fileOS.flush();
                    fileOS.close();
                    Toast.makeText(ImageActivity.this, "Image Downloaded Successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        ImageButton setButton=(ImageButton) findViewById(R.id.set_button);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager manager=WallpaperManager.getInstance(getApplicationContext());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                 Intent setWall=new Intent(manager.getCropAndSetWallpaperIntent(Uri.parse(url)));
                    startActivity(Intent.createChooser(setWall,"Set as:"));
                }
            }
        });}
        else
            setButton.setVisibility(View.GONE);
    }
}
