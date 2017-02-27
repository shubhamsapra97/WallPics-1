package com.example.android.wallpics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {


    private static int GALLERY=1;
    private Integer imgCount;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseImageCount;

    private ProgressDialog progress;
    private GridView mGridView;
    private GridAdapter adapter;
    private ArrayList<String> imageList= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress= new ProgressDialog(this);

        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase=FirebaseDatabase.getInstance().getReferenceFromUrl("https://wallpics-ace58.firebaseio.com/MyImages");
        databaseImageCount=FirebaseDatabase.getInstance().getReference().child("ImageCount");
        databaseImageCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imgCount = dataSnapshot.getValue(Integer.class);
                if(imgCount==null)
                    imgCount=0;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new GridAdapter(this,imageList);
        mGridView=(GridView) findViewById(R.id.gridview);
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String url= dataSnapshot.getValue(String.class);
                imageList.add(url);
                mGridView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String url= dataSnapshot.getValue(String.class);
                imageList.add(url);
                mGridView.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mGridView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.upload_menu)
        {
            Intent pick = new Intent(Intent.ACTION_PICK);
            pick.setType("image/*");
            Intent chooser = new Intent(Intent.createChooser(pick,"Select Image From"));
            startActivityForResult(chooser,GALLERY);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GALLERY && resultCode==RESULT_OK)
        {
            progress.setMessage("Uploading...");
            progress.show();
            Uri dataUri = data.getData();
            StorageReference ref = mStorage.child("MyImages").child(imgCount+"");
            ref.putFile(dataUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                    imgCount++;
                    databaseImageCount.setValue(imgCount);
                    DatabaseReference dbRef=mDatabase.child("image"+imgCount);
                    dbRef.setValue(downloadUrl);
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
