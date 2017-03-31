package com.example.android.wallpics;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    private static int GALLERY=1;
    private Integer imgCount;


    private FirebaseAuth mAuth;
    private AuthStateListener mAuthListener;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private DatabaseReference databaseImageCount;

    private ProgressDialog progress;
    private GridView mGridView;
    private GridAdapter adapter;
    public ArrayList<String> imageList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress= new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("Main Activity:", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    startActivity(new Intent(MainActivity.this,SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
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
                String url=dataSnapshot.getKey();
                int count=imageList.indexOf(url);
                imageList.remove(url);
                imgCount--;
                StorageReference ref = mStorage.child("MyImages").child(count+"");
                ref.delete();
                databaseImageCount.setValue(imgCount);
                mGridView.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,ImageActivity.class);
                String url=imageList.get(position);
                intent.putExtra("download",url);
                intent.putExtra("imageList", imageList);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.upload_menu:
                Intent pick = new Intent(Intent.ACTION_PICK);
                pick.setType("image/*");
                Intent chooser = new Intent(Intent.createChooser(pick,"Select Image From"));
                startActivityForResult(chooser,GALLERY);
                return true;
            case R.id.sign_out:
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this,SignInActivity.class));
                finish();
                return true;
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
            imgCount++;
            StorageReference ref = mStorage.child("MyImages").child(imgCount+"");
            ref.putFile(dataUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                    databaseImageCount.setValue(imgCount);
                    DatabaseReference dbRef=mDatabase.child(""+imgCount);
                    dbRef.setValue(downloadUrl);
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
