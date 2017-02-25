package com.example.android.wallpics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static int GALLERY=1;
    private int imgCount=0;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog progress;
    private GridView mGridView;
    private GridAdapter adapter;
    private ArrayList<String> imageList= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase=FirebaseDatabase.getInstance().getReferenceFromUrl("https://wallpics-ace58.firebaseio.com/MyImages");
        adapter = new GridAdapter(this,imageList);
        mGridView=(GridView) findViewById(R.id.gridview);
        mGridView.setAdapter(adapter);
        progress= new ProgressDialog(this);
        Button uploadButton=(Button) findViewById(R.id.upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pick = new Intent(Intent.ACTION_PICK);
                pick.setType("image/*");
                Intent chooser = new Intent(Intent.createChooser(pick,"Select Image From"));
                startActivityForResult(chooser,GALLERY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY && resultCode==RESULT_OK)
        {
            progress.setMessage("Uploading...");
            progress.show();
            Uri dataUri = data.getData();
            StorageReference ref = mStorage.child("MyImages").child(dataUri.getLastPathSegment());
            ref.putFile(dataUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                    imgCount++;
                    DatabaseReference dbRef=mDatabase.push();
                    dbRef.setValue(downloadUrl);
                    imageList.add(downloadUrl);
                    mGridView.setAdapter(adapter);
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
