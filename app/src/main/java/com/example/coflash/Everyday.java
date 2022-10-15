package com.example.coflash;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.coflash.ui.main.Upload;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.security.SecureRandom;

public class Everyday extends Activity{
    private ImageView left,right,paper;
    private TextView sentence;
    private ImageButton close;
    DatabaseReference myRef;
    Uri uri;
    String uid;
    private FirebaseDatabase db;
    SecureRandom randomNumbers=new SecureRandom();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_everyday);

        left=(ImageView)findViewById(R.id.left);
        right = (ImageView)findViewById(R.id.right);
        paper = (ImageView)findViewById(R.id.paper);
        sentence = findViewById(R.id.sentence);
        close = (ImageButton)findViewById(R.id.close);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }
        //連線資料庫
        FirebaseDatabase database = FirebaseDatabase.getInstance();//連線數據庫
        myRef = database.getReference("DB");
        int n=randomNumbers.nextInt(14)+1;
        String index=Integer.toString(n);

        myRef.child("sentence").child(index).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("輸出的字"+snapshot.getValue());
                sentence.setText(String.valueOf(snapshot.getValue()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openClose();
            }
        });
    }
    private void openClose() {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
