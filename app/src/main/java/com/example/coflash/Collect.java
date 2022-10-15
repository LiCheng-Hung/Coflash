//收藏
//注意：如果要刪前幾則貼文可能需要連uid的收藏一起刪，或想其他寫法
package com.example.coflash;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.MotionEvent;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.coflash.ui.main.Upload;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collect extends Activity {

    private ImageButton home,tag,plus,collect,myinfo;

    private TextView tv;
    private RecyclerView RV;
    private FirebaseDatabase db;
    Context context = this,ncontext;
    int x_last = 0;
    private Object List;
    TextView id,tv1,tv2,tv3; //記得宣告，不然layout會找不到，字就顯示不出來
    ImageView imageView,good_view,bad_view,heart_view;
    //讀取圖片
    Uri uri;
    String data_list,uid;
    StorageReference storageReference,pic_storage;
    int PICK_CONTACT_REQUEST=1;
    DatabaseReference myRef2,myRef;
    public static final float ALPHA_FULL = 1.0f;
    String plusid;
    ArrayList<Integer> afterlist=new ArrayList<Integer>();


    ImageAdapter imageAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        home = findViewById(R.id.under_home);
        tag = findViewById(R.id.under_tag);
        plus = findViewById(R.id.under_plus);
        collect = findViewById(R.id.under_collect);
        myinfo = findViewById(R.id.under_myinfo);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }

        //連線資料庫
        FirebaseDatabase database = FirebaseDatabase.getInstance();//連線數據庫
        myRef = database.getReference("DB");
        myRef2 = myRef.child("push");
        //Filter filter=new Filter(uid,Home.originallist);
        //afterlist=filter.list;

        myRef.child("user").child(uid).child("collect_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String s=String.valueOf(ds.getKey());
                    System.out.println("新的查詢="+s);
                    afterlist.add(Integer.parseInt(ds.getKey()));
                }
                firebase_select(myRef2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




        //推播訊息RV
        RV=(RecyclerView)findViewById(R.id.RV);
        RV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //RV.setLayoutManager(new LinearLayoutManager(this));

        //設置Adapter以及點擊回饋
        imageAdapter = new ImageAdapter(new ImageAdapter.OnItemClick() {
            @Override
            public void onItemClick(Myinfo.PlusString data, Myinfo.PlusString myData) {
                /*Toast.makeText(Home.this
                        , "選擇了"+myData.getTitle()+"的 "+data.getNesTitle()
                        , Toast.LENGTH_SHORT).show();*/
                Toast.makeText(Collect.this
                        , "內嵌成功YA"
                        , Toast.LENGTH_SHORT).show();
            }
        });
        // RV.setAdapter(imageAdapter);


        id=(TextView)findViewById(R.id.id);
        tv1=(TextView)findViewById(R.id.tv1);
        //tv2=(TextView)findViewById(R.id.tv2);
        tv3=(TextView)findViewById(R.id.tv3);
        imageView=(ImageView)findViewById(R.id.imageView);
        good_view=(ImageView)findViewById(R.id.good);
        bad_view=(ImageView)findViewById(R.id.bad);
        heart_view=(ImageView)findViewById(R.id.heart);




        //firebase_select(myRef2);
        java.util.List list;


        collect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                firebase_select(myRef2);    //點擊HOME icon時重新讀取資料庫+資料更新(onDataChage)
                openCollect();
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
        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openHome();
            }
        });
        myinfo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openMyinfo();
            }
        });
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
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(afterlist.size()>0) {
//                        System.out.println("afterlist.get(0)"+afterlist.get(0));
//                        System.out.println("Integer.parseInt(ds.getKey())"+Integer.parseInt(ds.getKey()));
                        while(afterlist.get(0) < Integer.parseInt(ds.getKey())){
                            afterlist.remove(0);
                            System.out.println("afterlist.get(0)刪除");
                            if(afterlist.size()<1){
                                break;
                            }
                        }
                        if(afterlist.size()>0){
                            if (afterlist.get(0) == Integer.parseInt(ds.getKey())){
                                Myinfo.PlusString push_data = ds.getValue(Myinfo.PlusString.class);
                                Upload[] upload = {ds.getValue(Upload.class)};
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("id", ds.getKey());
                                item.put("classification", push_data.getClassification());
                                item.put("tag", push_data.getTag());
                                item.put("title", push_data.getTitle());
                                item.put("name",push_data.getName());
                                item.put("url", upload[0].getImageUrl());
                                items.add(item);
                                push_data = new Myinfo.PlusString(Integer.parseInt(ds.getKey()), push_data.getClassification(), push_data.getTag(), push_data.getTitle(),push_data.getName());
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
                                plus.add(push_data);
                                uploadString.add(upload[0]);
                                x_last = Integer.parseInt(ds.getKey());   //抓取最後一筆key值
                            }
                        }
                    }
                }
                if(plus.size()>0) {
                    Collections.reverse(uploadString);
                    Collections.reverse(plus);
                    Collections.reverse(items);
                    System.out.println("plus.size()="+plus.size());
                    ImageAdapter imageadapter = new ImageAdapter(context, uploadString, plus);
                    RV.setAdapter(imageadapter);
                    recyclerViewAction(RV, items, imageadapter);//用來管理滑動
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void recyclerViewAction(RecyclerView RV, final List<Map<String,Object>> items, final ImageAdapter imageadapter) {//用來管理滑動
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//用來定義哪些方向可以用
                return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView//定義item是怎么移動的，從哪移動到哪，並且可以修改被移動所遮擋的其他view的行為，這里主要實現的是拖拽
                    , @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder targe) {
                int position = viewHolder.getAdapterPosition();

                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {////管理滑動情形
                int position = viewHolder.getAdapterPosition();
                plusid=String.valueOf(items.get(position).get("id"));//貼文的id
                switch (direction) {
                    case ItemTouchHelper.LEFT://按倒讚
                        myRef.child("push").child(plusid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    String numbertext = String.valueOf(task.getResult().child("badnumber").getValue());//取得貼文倒讚的數量
                                    if(task.getResult().child("gooduid").child(uid).getValue() != null){//這個uid之前已按過讚了
                                        //要在good那邊-1和移除uid
                                        myRef.child("push").child(plusid).child("gooduid").child(uid).removeValue();
                                        String goodnumbertext = String.valueOf(task.getResult().child("goodnumber").getValue());//取得貼文讚的數量
                                        int numm = Integer.parseInt(goodnumbertext);//轉數字型態
                                        numm--;//減1
                                        String aftergoodnumbertext = Integer.toString(numm);//轉文字型態
                                        myRef.child("push").child(plusid).child("goodnumber").setValue(aftergoodnumbertext);//存進資料庫
                                    }
                                    myRef.child("push").child(plusid).child("baduid").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//連結到資料庫的baduid裡面
                                        @Override
                                        public void onComplete(Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            } else {
                                                if (task.getResult().child(uid).getValue() != null) {//代表之前這個uid已按過倒讚，要-1
                                                    myRef.child("push").child(plusid).child("baduid").child(uid).removeValue();
                                                    int number = Integer.parseInt(numbertext);//轉數字型態
                                                    number--;//加1
                                                    String afternumbertext = Integer.toString(number);//轉文字型態
                                                    myRef.child("push").child(plusid).child("badnumber").setValue(afternumbertext);//存進資料庫
                                                } else {//之前未按過倒讚，要+1
                                                    myRef.child("push").child(plusid).child("baduid").child(uid).setValue(0);
                                                    if (numbertext != "null") {//是否以前有其他uid按過倒讚
                                                        int number = Integer.parseInt(numbertext);//轉數字型態
                                                        number++;//加1
                                                        String afternumbertext = Integer.toString(number);//轉文字型態
                                                        myRef.child("push").child(plusid).child("badnumber").setValue(afternumbertext);//存進資料庫
                                                    } else {
                                                        int number = 1;
                                                        String afternumbertext = Integer.toString(number);//轉文字型態
                                                        myRef.child("push").child(plusid).child("badnumber").setValue(afternumbertext);//存進資料庫
                                                    }
                                                }
                                            }
                                            imageadapter.notifyItemChanged(viewHolder.getAdapterPosition());//可以用來只更新單一物件item//讓圖片回來原位置
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case ItemTouchHelper.RIGHT://按讚
                        myRef.child("push").child(plusid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    String numbertext = String.valueOf(task.getResult().child("goodnumber").getValue());//取得貼文讚的數量
                                    if(task.getResult().child("baduid").child(uid).getValue() != null){//這個uid之前已按過倒讚了
                                        //要在bad那邊-1和移除uid
                                        myRef.child("push").child(plusid).child("baduid").child(uid).removeValue();
                                        String badnumbertext = String.valueOf(task.getResult().child("badnumber").getValue());//取得貼文倒讚的數量
                                        int num = Integer.parseInt(badnumbertext);//轉數字型態
                                        num--;//加1
                                        String afterbadnumbertext = Integer.toString(num);//轉文字型態
                                        myRef.child("push").child(plusid).child("badnumber").setValue(afterbadnumbertext);//存進資料庫
                                    }
                                    myRef.child("push").child(plusid).child("gooduid").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            } else {
                                                if (task.getResult().child(uid).getValue() != null) {//代表之前已按讚，要-1
                                                    myRef.child("push").child(plusid).child("gooduid").child(uid).removeValue();
                                                    int number = Integer.parseInt(numbertext);//轉數字型態
                                                    number--;//加1
                                                    String afternumbertext = Integer.toString(number);//轉文字型態
                                                    myRef.child("push").child(plusid).child("goodnumber").setValue(afternumbertext);//存進資料庫
                                                } else {//之前未按讚，要+1
                                                    myRef.child("push").child(plusid).child("gooduid").child(uid).setValue(0);
                                                    if (numbertext != "null") {
                                                        int number = Integer.parseInt(numbertext);//轉數字型態
                                                        number++;//加1
                                                        String afternumbertext = Integer.toString(number);//轉文字型態
                                                        myRef.child("push").child(plusid).child("goodnumber").setValue(afternumbertext);//存進資料庫
                                                    } else {
                                                        int number = 1;
                                                        String afternumbertext = Integer.toString(number);//轉文字型態
                                                        myRef.child("push").child(plusid).child("goodnumber").setValue(afternumbertext);//存進資料庫
                                                    }
                                                }
                                            }
                                            imageadapter.notifyItemChanged(viewHolder.getAdapterPosition());//可以用來只更新單一物件item//讓圖片回來原位置
                                        }
                                    });
                                }
                            }
                        });
                        break;
                }
            }
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //  super.clearView(recyclerView, viewHolder);
                //重置改變，防止由於複用而導致的顯示問題
                viewHolder.itemView.setScrollX(0);
            }
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {//左右滑底圖
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    Bitmap icon;

                    if (dX > 0) {

                        icon = BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.good);

                        /* Set your color for positive displacement */
                        p.setARGB(255, 0, 255, 0);

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);

                        // Set the image icon for Right swipe
                        c.drawBitmap(icon,
                                (float) itemView.getLeft() + convertDpToPx(16),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                p);
                    } else {
                        icon = BitmapFactory.decodeResource(
                                context.getResources(), R.drawable.bad);

                        /* Set your color for negative displacement */
                        p.setARGB(255, 255, 0, 0);

                        // Draw Rect with varying left side, equal to the item's right side
                        // plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                        //Set the image icon for Left swipe
                        c.drawBitmap(icon,
                                (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight())/2,
                                p);
                    }

                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            private int convertDpToPx(int dp){
                return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            }
        });
        helper.attachToRecyclerView(RV);

    }

    //開啟介面
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
//        Intent intent=new Intent(this,Collect.class);
//        startActivity(intent);
    }
    private void openMyinfo() {
        Intent intent = new Intent(this, Myinfo.class);
        finish();
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode==PICK_CONTACT_REQUEST){
            uri=data.getData();
            ContentResolver contentResoler=getContentResolver();//取得檔案的附檔名方法:ContentResolver內容解析器、MimeTypeMap模仿類型圖
            MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
            data_list=mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(uri));

        }
        super.onActivityResult(requestCode,resultCode,data);
    }

}
