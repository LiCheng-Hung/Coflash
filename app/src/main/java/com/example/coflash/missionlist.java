package com.example.coflash;

import android.app.Activity;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class missionlist extends Activity {

    private ImageButton back, ibtn_1, ibtn_2, ibtn_3;
    private ImageView reach_1, reach_2, reach_3;
    private TextView tv_1, tv_2, tv_3;

    String uid;
    DatabaseReference myRef, myRef2, myRef3;

    boolean heart[] = new boolean[2];
    boolean plus[] = new boolean[2];
    boolean thumb[] = new boolean[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missionlist);

        back = findViewById(R.id.back);
        ibtn_1 = findViewById(R.id.ibtn_1);
        ibtn_2 = findViewById(R.id.ibtn_2);
        ibtn_3 = findViewById(R.id.ibtn_3);
        reach_1 = findViewById(R.id.reach_1);
        reach_2 = findViewById(R.id.reach_2);
        reach_3 = findViewById(R.id.reach_3);
        tv_1 = findViewById(R.id.tv_1);
        tv_2 = findViewById(R.id.tv_2);
        tv_3 = findViewById(R.id.tv_3);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }

        //連線資料庫
        FirebaseDatabase database = FirebaseDatabase.getInstance();//連線數據庫
        myRef = database.getReference("DB");
        myRef2 = myRef.child("user").child(uid).child("mission");
        myRef3 = myRef.child("user").child(uid);

        firebase_select();

        ibtn_1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long res_diamond =  (long) snapshot.child("diamond").getValue() + 5;
                        long res_experience = (long) snapshot.child("experience").getValue() + 5;
                        myRef3.child("diamond").setValue(res_diamond);
                        myRef3.child("experience").setValue(res_experience);
                        myRef2.child("heart").child("isTake").setValue(true);
                        ibtn_1.setEnabled(false);
                        reach_1.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        ibtn_2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long res_diamond =  (long) snapshot.child("diamond").getValue() + 5;
                        long res_experience = (long) snapshot.child("experience").getValue() + 5;
                        myRef3.child("diamond").setValue(res_diamond);
                        myRef3.child("experience").setValue(res_experience);
                        myRef2.child("plus").child("isTake").setValue(true);
                        ibtn_2.setEnabled(false);
                        reach_2.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
        ibtn_3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long res_diamond =  (long) snapshot.child("diamond").getValue() + 5;
                        long res_experience = (long) snapshot.child("experience").getValue() + 5;
                        myRef3.child("diamond").setValue(res_diamond);
                        myRef3.child("experience").setValue(res_experience);
                        myRef2.child("thumb").child("isTake").setValue(true);
                        ibtn_3.setEnabled(false);
                        reach_3.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //讀取資料方法
    private void firebase_select() {
        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                heart[0] = (boolean) snapshot.child("heart").child("isFinish").getValue();
                heart[1] = (boolean) snapshot.child("heart").child("isTake").getValue();
                plus[0] = (boolean) snapshot.child("plus").child("isFinish").getValue();
                plus[1] = (boolean) snapshot.child("plus").child("isTake").getValue();
                thumb[0] = (boolean) snapshot.child("thumb").child("isFinish").getValue();
                thumb[1] = (boolean) snapshot.child("thumb").child("isTake").getValue();
                if(heart[0]){
                    tv_1.setText("雙擊心心 (1/1)");
                    if(heart[1]){
                        ibtn_1.setEnabled(false);
                        reach_1.setVisibility(View.VISIBLE);
                    }
                    else{
                        ibtn_1.setEnabled(true);
                    }
                }
                else{
                    ibtn_1.setEnabled(false);
                }

                if(plus[0]){
                    tv_2.setText("推播大使 (1/1)");
                    if(plus[1]){
                        ibtn_2.setEnabled(false);
                        reach_2.setVisibility(View.VISIBLE);
                    }
                    else{
                        ibtn_2.setEnabled(true);
                    }
                }
                else{
                    ibtn_2.setEnabled(false);
                }

                if(thumb[0]){
                    tv_3.setText("好讚友 (1/1)");
                    if(thumb[1]){
                        ibtn_3.setEnabled(false);
                        reach_3.setVisibility(View.VISIBLE);
                    }
                    else{
                        ibtn_3.setEnabled(true);
                    }
                }
                else{
                    ibtn_3.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}