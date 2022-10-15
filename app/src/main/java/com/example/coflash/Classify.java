package com.example.coflash;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ImageButton;
import android.util.Log;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.example.coflash.ui.main.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classify extends Activity {

    private RecyclerView RV,RV_classify;
    List<String> classify_string=new ArrayList<>();
    Context context = this,ncontext;
    ImageAdapter imageAdapter;
    DatabaseReference myRef2,myRef;
    private ImageButton back;
    private TextView text;
    int x_last = 0;
    private String uid;
    public static String pos_classify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);

        text=(TextView)findViewById(R.id.text);
        back=(ImageButton)findViewById(R.id.back);

        //連線資料庫
        FirebaseDatabase database = FirebaseDatabase.getInstance();//連線數據庫
        myRef = database.getReference("DB");
        myRef2 = myRef.child("push");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }

        if(Home.status_classify==1) {
            pos_classify = Home.pos_classify;
            text.setText("#" + pos_classify);
        }else if(Home.status_classify==2){
            pos_classify = ClassifyTag.pos_classify;
            text.setText("#" + pos_classify);
        }else if(Home.status_classify==5){
            pos_classify = Tag.pos_classify;
            text.setText("#" + pos_classify);
        }else{
            text.setText("#" + pos_classify);
        }

        //橫向tag
        RV_classify=(RecyclerView)findViewById(R.id.RV_classify);
        //RV_classify.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV_classify.setLayoutManager(linearLayoutManager);
        classify_string.add("0");
        classify_string.add("1");
        classify_string.add("2");
        classify_string.add("3");
        classify_string.add("4");
        classify_string.add("5");
        classify_string.add("6");
        myRef.child("user").child(uid).child("tag").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String s=String.valueOf(ds.getKey());
                    classify_string.add(s);
                }
                setup_classify();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //推播訊息RV
        RV=(RecyclerView)findViewById(R.id.RV);
        RV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //設置Adapter以及點擊回饋
        imageAdapter = new ImageAdapter(new ImageAdapter.OnItemClick() {
            @Override
            public void onItemClick(Myinfo.PlusString data, Myinfo.PlusString myData) {
                /*Toast.makeText(Home.this
                        , "選擇了"+myData.getTitle()+"的 "+data.getNesTitle()
                        , Toast.LENGTH_SHORT).show();*/
                Toast.makeText(Classify.this
                        , "內嵌成功YA"
                        , Toast.LENGTH_SHORT).show();
            }
        });

        firebase_select(myRef2);
        java.util.List list;

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Back();
            }
        });
    }
    public void setup_classify(){
        ClassifyAdapter classifyadapter=new ClassifyAdapter(context,classify_string,R.layout.classify_cardview,R.layout.tagsgroup_cardview);
        RV_classify.setAdapter(classifyadapter);

        classifyadapter.setOnItemClickListener(new ClassifyAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                record(position);
            }
        });
    }
    public void record(int position){
        switch(position){
            case 0:
                pos_classify="食";
                openClassify();
                break;
            case 1:
                pos_classify="衣";
                openClassify();
                break;
            case 2:
                pos_classify="住";
                openClassify();
                break;
            case 3:
                pos_classify="行";
                openClassify();
                break;
            case 4:
                pos_classify="育";
                openClassify();
                break;
            case 5:
                pos_classify="樂";
                openClassify();
                break;
            case 6:
                pos_classify="其它";
                openClassify();
                break;
            case 7:
                pos_classify=classify_string.get(7);
                openClassifyTag();
                break;
            case 8:
                pos_classify=classify_string.get(8);
                openClassifyTag();
                break;
            case 9:
                pos_classify=classify_string.get(9);
                openClassifyTag();
                break;
            case 10:
                pos_classify=classify_string.get(10);
                openClassifyTag();
                break;
            case 11:
                pos_classify=classify_string.get(11);
                openClassifyTag();
                break;
            default:
                pos_classify="找不到";
                break;
        }
        Home.status_classify=0;
    }
    List list;
    public List<Map<String, Object>> getItem() {
        return list;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        //讓父佈局不攔截
        RV.requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
    //讀取資料方法
    private void firebase_select(DatabaseReference db) {
        final List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
        final List<Myinfo.PlusString> plus=new ArrayList<>();
        final List<Upload> uploadString=new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //int x_sum=(int)snapshot.getChildrenCount();    獲取分支總數
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Myinfo.PlusString push_data = ds.getValue(Myinfo.PlusString.class);
                    Upload[] upload = {ds.getValue(Upload.class)};
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("id", ds.getKey());
                    item.put("classification", push_data.getClassification());
                    item.put("tag", push_data.getTag());
                    item.put("title", push_data.getTitle());
                    item.put("name", push_data.getName());
                    item.put("url", upload[0].getImageUrl());
                    items.add(item);
                    push_data = new Myinfo.PlusString(Integer.parseInt(ds.getKey()), push_data.getClassification(), push_data.getTag(), push_data.getTitle(), push_data.getName());
                    //讀資料庫的url資料，放進upload裡
                    myRef2.child(ds.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                upload[0] = new Upload(String.valueOf(task.getResult().child("imageName").getValue()), String.valueOf(task.getResult().child("imageName").getValue()));
                            }
                        }
                    });
                    if (pos_classify.equals(push_data.getClassification()) && push_data.getClassification()!=null) {
                        plus.add(push_data);
                        uploadString.add(upload[0]);
                        x_last = Integer.parseInt(ds.getKey());   //抓取最後一筆key值
                    }
                }
                if (plus.size() > 0) {
                    list = items;
                    ImageAdapter imageadapter = new ImageAdapter(context, uploadString, plus);
                    RV.setAdapter(imageadapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void Back() {
        finish();
    }
    private void openClassify() {
        Intent intent = new Intent(this, Classify.class);
        startActivity(intent);
    }
    private void openClassifyTag() {
        Intent intent = new Intent(this, ClassifyTag.class);
        startActivity(intent);
    }

}
