package com.example.android.wallpics;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.VideoView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<GridItem> imageList= new ArrayList<GridItem>();
        imageList.add(new GridItem("www.google.co.in/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=0ahUKEwiL3b-hjJnSAhVIpI8KHc2FBzsQjRwIBw&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FSunset&psig=AFQjCNFrDck80EmE6YHTzvG2s74SahIzWw&ust=1487488401043378"));
        GridAdapter adapter = new GridAdapter(this, imageList);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
    }
}
