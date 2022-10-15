package com.example.coflash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;

public class Myinfo extends Activity {

    private Button title, mission,push,follow,setting;
    private ImageButton home,tag,plus,collect,myinfo, diamond;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button signOutButton;
    private TextView user_name,user_title,user_diamond,user_EX;
    private ImageView user_photo;
    String uid,urll;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private final Context mContext = this;

    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        home = findViewById(R.id.under_home);
        tag = findViewById(R.id.under_tag);
        plus = findViewById(R.id.under_plus);
        collect = findViewById(R.id.under_collect);
        myinfo = findViewById(R.id.under_myinfo);
        diamond = findViewById(R.id.diamond);
        title = findViewById(R.id.title);
        mission = findViewById(R.id.mission);
        push = findViewById(R.id.push);
        follow = findViewById(R.id.follow);
        setting = findViewById(R.id.setting);
        signOutButton = findViewById(R.id.signOutButton);
        user_name=findViewById(R.id.uName);
        user_title=findViewById(R.id.uTitle);
        user_diamond=findViewById(R.id.diamond_number);
        user_EX=findViewById(R.id.EX_number);
        user_photo=findViewById(R.id.uPhoto);

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHome();
            }
        });
        tag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openTag();
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openPlus();
            }
        });
        collect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openCollect();
            }
        });
        myinfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMyinfo();
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openmytitle();
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openmissionlist();
            }
        });
        push.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openpushlist();
            }
        });
        follow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openfollowlist();
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                opensettinglist();
            }
        });
        diamond.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openaddcoin();
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("DB");
        firebase_select();
    }

    private void firebase_select() {
        myRef.child("user").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    user_name.setText(String.valueOf(task.getResult().child("name").getValue()));//成功找到user_name
                    user_title.setText(String.valueOf(task.getResult().child("title").getValue()));
                    user_diamond.setText(String.valueOf(task.getResult().child("diamond").getValue()));
                    user_EX.setText(String.valueOf(task.getResult().child("experience").getValue()));
                    urll=String.valueOf(task.getResult().child("url").getValue());
                    Glide.with(mContext).load(urll).into(user_photo);
                }
            }
        });
    }

    private void openHome(){
//        Intent intent=new Intent(this, Home.class);
        finish();
//        startActivity(intent);
    }
    private void openTag(){
        Intent intent=new Intent(this,Tag.class);
        finish();
        startActivity(intent);
    }
    private void openPlus(){
        Intent intent=new Intent(this,Plus.class);
        finish();
        startActivity(intent);
    }
    private void openCollect(){
        Intent intent=new Intent(this,Collect.class);
        finish();
        startActivity(intent);
    }
    private void openMyinfo() {
//        Intent intent = new Intent(this, Myinfo.class);
//        startActivity(intent);
    }
    private void openmytitle() {
        Intent intent = new Intent(this, mytitle.class);
        startActivity(intent);
    }
    private void openmissionlist() {
        Intent intent = new Intent(this, missionlist.class);
        startActivity(intent);
    }
    private void openpushlist() {
        Intent intent = new Intent(this, Pushlist.class);
        startActivity(intent);
    }
    private void openfollowlist() {
        Intent intent = new Intent(this, follow.class);
        startActivity(intent);
    }
    private void opensettinglist() {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }
    private void openaddcoin() {
        Intent intent = new Intent(this, addcoin.class);
        startActivity(intent);
    }
    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(Myinfo.this,MainActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebase_select();
    }

    public static class PlusString {
        private String classification;
        private String tag; //tag一次最多選五個
        private String title;
        private String author;  //使用者ID
        private String name;    //使用者名稱
        //0911
        private String addr,push_context;
        private int id;

        public PlusString(){
        }

        public PlusString(String text_1,String text_2,String text_3){
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
        }
        public PlusString(String text_1,String text_2,String text_3,String name){
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
            this.name=name;
        }
        public PlusString(String text_1,String text_2,String text_3,String author,String name){
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
            this.author=author;
            this.name=name;
        }
        public PlusString(int id,String text_1,String text_2,String text_3){
            this.id=id;
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
        }
        public PlusString(int id,String text_1,String text_2,String text_3,String name){
            this.id=id;
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
            this.name=name;
        }
        public PlusString(int id,String text_1,String text_2,String text_3,String author,String name){
            this.id=id;
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
            this.author=author;
            this.name=name;
        }
        //0911
        public PlusString(String text_1,String text_2,String text_3,String author,String name,String addr,String push_context){
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
            this.author=author;
            this.name=name;
            this.addr=addr;
            this.push_context=push_context;
        }
        public PlusString(int id,String text_1,String text_2,String text_3,String author,String name,String addr,String push_context){
            this.id=id;
            this.classification=text_1;
            this.tag=text_2;
            this.title=text_3;
            this.author=author;
            this.name=name;
            this.addr=addr;
            this.push_context=push_context;
        }

        public int getId(){
            return id;
        }
        public String getClassification(){
            return classification;
        }
        public String getTag(){
            return tag;
        }
        public String getTitle(){
            return title;
        }
        public String getAuthor(){return author;}
        public String getName(){return name;}
        //0911
        public String getAddr(){return addr;}
        public String getPushContext(){return push_context;}

//        public Long getPushLength(){
//            return push_Length;
//        }
//        public Long getPushTimepoint(){
//            return push_Timepoint;
//        }

    }
}