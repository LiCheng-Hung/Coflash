package com.example.coflash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class Notice extends Activity {
    private ImageButton home,tag,plus,collect,myinfo,back;

    //RecyclerView
    RecyclerView rv_notice;
    NoticeAdapter noticeAdapter;

    //資料庫
    DatabaseReference myRef, myRef2;
    String uid;
    List<Map<String,Object>> notices = new ArrayList<Map<String,Object>>();
//    List<Map<String,Object>> Notices = new ArrayList<Map<String,Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        home = findViewById(R.id.under_home);
        tag = findViewById(R.id.under_tag);
        plus = findViewById(R.id.under_plus);
        collect = findViewById(R.id.under_collect);
        myinfo = findViewById(R.id.under_myinfo);
        back = findViewById(R.id.back);

        //資料庫連線
        FirebaseDatabase database = FirebaseDatabase.getInstance();//連線數據庫
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }
        myRef = database.getReference("DB");
        myRef2 = myRef.child("user").child(uid).child("Notice");
        firebase_select(myRef2);

        rv_notice = findViewById(R.id.rv_notice);
        rv_notice.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));



        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHome();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
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
    }

    private void firebase_select(DatabaseReference db) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String,Object> item= (Map<String, Object>) ds.getValue();
                    item.put("id", String.valueOf(ds.getKey()));
                    myRef2.child(String.valueOf(ds.getKey())).child("isRead").setValue(true);
                    notices.add(item);
                }
                Collections.reverse(notices);
                noticeAdapter = new NoticeAdapter(Notice.this, notices);
                rv_notice.setAdapter(noticeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //開啟介面
    private void openHome(){
        finish();
    }
    private void openTag(){
        Intent intent=new Intent(this,Tag.class);
        startActivity(intent);
    }
    private void openPlus(){
        Intent intent=new Intent(this,Plus.class);
        startActivity(intent);
    }
    private void openCollect(){
        Intent intent=new Intent(this,Collect.class);
        startActivity(intent);
    }
    private void openMyinfo() {
        Intent intent = new Intent(this, Myinfo.class);
        startActivity(intent);
    }

    //RecyclerView
    public class NoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context mContext;
        private List<Map<String,Object>> mnotice = new ArrayList<>();
        private LayoutInflater layoutInflater;


        // Create constructor
        public NoticeAdapter(Context context, List<Map<String,Object>> noticeStrings)
        {
            mContext = context;
            mnotice = noticeStrings;
            layoutInflater=LayoutInflater.from(mContext);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(Notice.this)
                            .inflate(R.layout.layout_notice, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Map<String,Object> noticeCurrent = mnotice.get(position);
            String  type = (String) noticeCurrent.get("type");
            if(type.equals("location")){
                String str1 = "距離你" + (String) noticeCurrent.get("content") + "有新活動！";
                String str2 = (String) noticeCurrent.get("title");
                ((ViewHolder)holder).tv_tag.setText(str1);
                ((ViewHolder)holder).tv_title.setText(str2);
                ((ViewHolder)holder).imgV_type.setImageResource(R.drawable.address);
            }
            else if(type.equals("time")){
                String str1 = "你收藏的貼文在" + (String) noticeCurrent.get("content") + "就要消失囉！";
                String str2 = (String) noticeCurrent.get("title");
                ((ViewHolder)holder).tv_tag.setText(str1);
                ((ViewHolder)holder).tv_title.setText(str2);
                ((ViewHolder)holder).imgV_type.setImageResource(R.drawable.time);
            }
            else{
                String str1 = "你追蹤的「" + (String) noticeCurrent.get("content") + "」有新貼文了！";
                String str2 = (String) noticeCurrent.get("title");
                ((ViewHolder)holder).tv_tag.setText(str1);
                ((ViewHolder)holder).tv_title.setText(str2);
            }

        }

        @Override
        public int getItemCount() {
            return mnotice.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_tag, tv_title;
            public ImageView imgV_type;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_tag = itemView.findViewById(R.id.tv_tag);
                tv_title = itemView.findViewById(R.id.tv_title);
                imgV_type = itemView.findViewById(R.id.imgV_type);
            }
        }
    }
}