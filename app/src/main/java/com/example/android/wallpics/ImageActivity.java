package com.example.android.wallpics;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity{
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
        final CardView view=(CardView) findViewById(R.id.card_view);
        transparency(view);

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
                    transparency(view);
                String imgName = "WallPics" + System.currentTimeMillis() + ".jpg";
                final ProgressDialog dialog=new ProgressDialog(ImageActivity.this);
                dialog.setMessage("Preparing to Send...");
                dialog.show();
                DownloadManager.Request download = downloadImage(imgName);
                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(download);
                BroadcastReceiver onComplete=new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        long downloadId=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,1);
                        String action=intent.getAction();
                        if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                        {
                            Log.e(TAG, "onReceive: "+manager.getUriForDownloadedFile(downloadId) );
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                Intent sendIntent=new Intent(Intent.ACTION_SEND);
                                sendIntent.setType("image/jpg");
                                sendIntent.putExtra(Intent.EXTRA_STREAM,manager.getUriForDownloadedFile(downloadId));
                                sendIntent.putExtra(Intent.EXTRA_TEXT,"Sent Via WallPics!");
                                dialog.dismiss();
                                startActivity(Intent.createChooser(sendIntent,"Send Via:"));
                            }
                        }
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });

        ImageButton download=(ImageButton) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transparency(view);
                    Toast.makeText(ImageActivity.this, "Downloading...", Toast.LENGTH_SHORT).show();
                    String imgName = "WallPics" + System.currentTimeMillis() + ".jpg";
                    DownloadManager.Request download = downloadImage(imgName);
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    download.allowScanningByMediaScanner();
                    download.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    manager.enqueue(download);

            }
        });

        ImageButton setButton=(ImageButton) findViewById(R.id.set_button);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    transparency(view);
                    String imgName = "WallPics" + System.currentTimeMillis() + ".jpg";
               final ProgressDialog d=new ProgressDialog(ImageActivity.this, DialogInterface.BUTTON_NEGATIVE);
                d.setMessage("Setting WallPic...");
                d.show();
                    DownloadManager.Request download = downloadImage(imgName);
                    final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(download);
                BroadcastReceiver onComplete=new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        long downloadId=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,1);
                        String action=intent.getAction();
                        if(action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                        {
                            Log.e(TAG, "onReceive: "+manager.getUriForDownloadedFile(downloadId) );
                            WallpaperManager wManager=WallpaperManager.getInstance(ImageActivity.this);
                            Intent wIntent= null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                                wIntent = new Intent(wManager.getCropAndSetWallpaperIntent(manager.getUriForDownloadedFile(downloadId)));
                                d.dismiss();
                            }
                            startActivity(wIntent);
                        }
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });}
        else
            setButton.setVisibility(View.GONE);

    }

    private DownloadManager.Request downloadImage(String imgName)
    {
        DownloadManager.Request download=new DownloadManager.Request(imageUri);
        download.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE| DownloadManager.Request.NETWORK_WIFI);
        download.setAllowedOverRoaming(true);
        download.setDestinationInExternalPublicDir("",imgName);
        return download;
    }

    private void transparency(final CardView card)
    {
        card.setAlpha(1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                float i=(float)0.3;
                card.setAlpha(i);
            }
        },3000);
    }

}
