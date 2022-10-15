package com.example.coflash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.MotionEvent;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Home extends Activity{

    private ImageButton home,tag,plus,collect,myinfo;
    private ImageView label,everyday;

    private Button btn_address1,btn_address2;
    private TextView tv, noticeCount;
    private RecyclerView RV,RV_classify;
    private FirebaseDatabase db;
    Context context = this,ncontext;
    int x_last = 0;
    private Object List;
    TextView id,tv1,tv2,tv3; //記得宣告，不然layout會找不到，字就顯示不出來
    ImageView imageView,good_view,bad_view;
    //讀取圖片
    Uri uri;
    String data_list,uid;
    StorageReference storageReference,pic_storage;
    int PICK_CONTACT_REQUEST=1;
    DatabaseReference myRef, myRef2, myRef3;
    public static final float ALPHA_FULL = 1.0f;
    String plusid;
    List<String> classify_string=new ArrayList<>();
    public static String pos_classify;//上傳的東西(classify功能用)
    public static int status_classify;//參數狀態(classify功能用)

    ImageAdapter imageAdapter;
    //地址
    LatLng latLng, currentLatLng;
    String str1;
    Geocoder geocoder;
    private static final int ACTIVITY_REPORT = 1000;
    private static final int ACTIVITY_REPORT_2 = 500;

    //更新時間
    int timeDistance;

    //0911
    private PopupWindow popupWindow;
    private ImageButton btnConfirm;
    private ImageView photo;
    private TextView push_title,user_name,addr,push_context,user_title;
    private List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
    private RecyclerView tagRv;
    private PopupWindowAdapter popupWindowAdapter;

    //距離通知
    private static final double EARTH_RADIUS = 6378.137;
    int notion_last = 0;
    String oldAddress="";
    String str3 = "";

    //到期通知
    ArrayList<Integer> afterlist=new ArrayList<Integer>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        home = findViewById(R.id.under_home);
        tag = findViewById(R.id.under_tag);
        plus = findViewById(R.id.under_plus);
        collect = findViewById(R.id.under_collect);
        myinfo = findViewById(R.id.under_myinfo);
        label = findViewById(R.id.label);
        btn_address1 = findViewById(R.id.btn_address1);
        btn_address2 = findViewById(R.id.btn_address2);
        noticeCount = findViewById(R.id.noticeCount);
        everyday = (ImageView)findViewById(R.id.everyday);

        //推播訊息RV
        RV=(RecyclerView)findViewById(R.id.RV);
        RV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        id=(TextView)findViewById(R.id.id);
        tv1=(TextView)findViewById(R.id.tv1);
        //tv2=(TextView)findViewById(R.id.tv2);
        tv3=(TextView)findViewById(R.id.tv3);
        imageView=(ImageView)findViewById(R.id.imageView);
        good_view=(ImageView)findViewById(R.id.good);
        bad_view=(ImageView)findViewById(R.id.bad);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }
        //連線資料庫
        FirebaseDatabase database = FirebaseDatabase.getInstance();//連線數據庫
        myRef = database.getReference("DB");
        myRef2 = myRef.child("push");

        myRef3 = myRef.child("user").child(uid).child("currentLatLng");
        if(latLng == null){
            firebase_select_userLatlng(myRef3);
        }
        firebase_update_date();

        //分類classify的部分
        RV_classify=(RecyclerView)findViewById(R.id.RV_classify);
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
        myRef.child("user").child(uid).child("tag_love").addListenerForSingleValueEvent(new ValueEventListener() {
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

        firebase_select(myRef2);
        java.util.List list;

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                firebase_select(myRef2);    //點擊HOME icon時重新讀取資料庫+資料更新(onDataChage)
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
        label.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openLabel();
            }
        });
        btn_address1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChangeaddress();
            }
        });
        btn_address2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openChangeaddress();
            }
        });
        everyday.setOnTouchListener(imgListener);
        everyday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openEveryday();
            }
        });

        initPopupWindow();

    }
    //0911 初始化PopupWindow
    private void initPopupWindow() {
        View view = LayoutInflater.from(context) .inflate(R.layout.popupwindow_layout, null);
        popupWindow = new PopupWindow(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        photo=(ImageView) view.findViewById(R.id.photo);
        btnConfirm = (ImageButton) view.findViewById(R.id.btnConform);
        user_name=(TextView) view.findViewById(R.id.user_name);
        addr=(TextView)view.findViewById(R.id.addr);
        push_context=(TextView) view.findViewById(R.id.push_context);
        push_title= (TextView) view.findViewById(R.id.push_title);
        tagRv=(RecyclerView) view.findViewById(R.id.tagRv);
        user_title=(TextView) view.findViewById(R.id.user_title);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                darkenBackground(1f);   //背景變回原本亮度
            }
        });
        popupWindow.setTouchable(true);//0918
        popupWindow.setFocusable(true); //點一下消失，聚焦於popupWindow的操作
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {  //0918 popupWindow關閉後，背景顏色恢復
            @Override
            public void onDismiss() {
                darkenBackground(1f);
            }
        });

    }

    //彈出PopupWindow後讓背景變暗
    private void darkenBackground(float bgcolor){
        WindowManager.LayoutParams lp=this.getWindow().getAttributes();
        lp.alpha=bgcolor;
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        this.getWindow().setAttributes(lp);
    }
    List<String> tags=new ArrayList<>();    //tag list
    String tagStr;
    private  float downX,downY=0;
    private float moveX,moveY=0;
    private  long currentMs,moveTime=0;
    private float lastX=0;
    private void showPopupWindow(int position){
        darkenBackground(0.5f); //背景變暗
        Glide.with(context).load(items.get(position).get("url")).centerCrop().into(photo);
        //Glide.with(context).load(items.get(position).get("url")).apply(RequestOptions.bitmapTransform(new RoundedCorners(30))).into(photo);
        push_title.setText(items.get(position).get("title").toString());    //推播標題
        user_name.setText(items.get(position).get("user_name").toString()); //使用者名稱
        addr.setText(items.get(position).get("pushplace").toString());  //推播地點
        push_context.setText(items.get(position).get("push_context").toString());   //推播內文
        user_title.setText(items.get(position).get("user_title").toString());
        //tag
        tags=new ArrayList<>();
        tagStr=items.get(position).get("tag").toString();
        for(String s:tagStr.split("#")){
            if(!(s.equals("")) && !(s.equals(" "))) //不為空時
                tags.add(s.trim());
        }
        popupWindowAdapter=new PopupWindowAdapter(context,tags);
        tagRv.setAdapter(popupWindowAdapter);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:   //手指的初次觸摸
                        downX=event.getX();
                        downY=event.getY();
                        currentMs=System.currentTimeMillis();
                        lastX=event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:   //手指滑動
                        downX=event.getX();
                        downY=event.getY();
                        moveX += Math.abs(event.getRawX() - downX);//x軸移動距離
                        moveY += Math.abs(event.getRawY() - downY);//y軸移動距離
                        break;
                    case MotionEvent.ACTION_UP: //抬起
                        moveTime=System.currentTimeMillis()-currentMs;

                        //判斷是滑動還是點擊操作、判斷是否繼續傳遞信號
                        if(moveTime<300 && moveX<20 && moveY<20) {   //點擊事件
                            return false;
                        }else{  //TODO:tag滑動和頁面滑動會一起判斷
                            //滑動事件
                            if(event.getX()-lastX<0){   //從右到左滑(左滑)    //position從0開始
                                ((LinearLayoutManager)RV.getLayoutManager()).scrollToPositionWithOffset((Math.abs(position+1))%items.size(),0); //定位到指定項如果該項可以置頂就將其置頂顯示
                                showPopupWindow((Math.abs(position+1))%items.size());
                            }else if(event.getX()-lastX>0){
                                ((LinearLayoutManager)RV.getLayoutManager()).scrollToPositionWithOffset((Math.abs(position-1+items.size()))%items.size(),0);
                                showPopupWindow((Math.abs(position-1+items.size()))%items.size());
                            }else{
                                return true;
                            }
                            moveY=0;
                            moveX=0;
                            return true;
                        }
                    default:
                        break;
                }
                return false;
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
        }
        status_classify=1;
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
        //final List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
        final List<Myinfo.PlusString> plus=new ArrayList<>();
        final List<Upload> uploadString=new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //int x_sum=(int)snapshot.getChildrenCount();    獲取分支總數
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Myinfo.PlusString push_data=ds.getValue(Myinfo.PlusString.class);
                    Upload[] upload = {ds.getValue(Upload.class)};
                    Map<String,Object> item=new HashMap<String,Object>();
                    item.put("id",ds.getKey());
                    item.put("classification", push_data.getClassification());
                    item.put("tag",push_data.getTag());
                    item.put("title",push_data.getTitle());
                    item.put("url", upload[0].getImageUrl());
                    //0911
                    item.put("user_name",push_data.getName());
                    System.out.println(push_data.getPushContext());
                    item.put("pushplace",ds.child("pushplace").child("address").getValue()==null?"沒有地點":ds.child("pushplace").child("address").getValue());
                    LatLng temLatlng = new LatLng((double)ds.child("pushplace").child("latitude").getValue(), (double)ds.child("pushplace").child("longitude").getValue());
                    item.put("pushLatlng", temLatlng);
                    item.put("push_context",ds.child("word").getValue()==null?"沒有內文":ds.child("word").getValue());

                    myRef.child("user").child(push_data.getAuthor()).child("title").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting user_title", task.getException());
                            } else {
                               item.put("user_title",task.getResult().getValue());
                            }
                        }
                    });
                    //TODO:新增item.put(word.getText());
                    //0911
                    push_data=new Myinfo.PlusString(Integer.parseInt(ds.getKey()),push_data.getClassification(),push_data.getTag(),push_data.getTitle(),
                            push_data.getAuthor(),push_data.getName(), push_data.getAddr(),push_data.getPushContext());
                    items.add(item);

                    //讀資料庫的url資料，放進upload裡
                    myRef2.child(ds.getKey()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                upload[0] =new Upload(String.valueOf(task.getResult().child("imageName").getValue()),String.valueOf(task.getResult().child("imageName").getValue()));
                            }
                        }
                    });

                    plus.add(push_data);
                    uploadString.add(upload[0]);
                    x_last = Integer.parseInt(ds.getKey());   //抓取最後一筆key值

                }
                Collections.reverse(uploadString);
                Collections.reverse(plus);
                Collections.reverse(items);
                list = items;
                imageAdapter=new ImageAdapter(context,uploadString,plus);

                RV.addOnItemTouchListener(new RecyclerItemClickListener(context,RV,new RecyclerItemClickListener.OnItemClickListener(){
                    @Override
                    public void onItemClick(View view,int position){
                        System.out.println("onItemClick!");
                    }

                    @Override
                    public void onLongItemClick(View parent, int position) {
                        //if(imageAdapter.getLongPress()) {
                            popupWindow.showAtLocation(parent, Gravity.CENTER_HORIZONTAL, 0, 0);
                            showPopupWindow(position);

                        //}
                    }
                }
                ));
                RV.setAdapter(imageAdapter);
                recyclerViewAction(RV, items, imageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void recyclerViewAction(RecyclerView RV, final List<Map<String,Object>> items, final ImageAdapter imageadapter) {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//用來定義哪些方向可以用
                return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView//定義item是怎么移動的，從哪移動到哪，並且可以修改被移動所遮擋的其他view的行為，這里主要實現的是拖拽
                    , @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder targe) {


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
                                                    myRef.child("user").child(uid).child("mission").child("thumb").child("isFinish").setValue(true);
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
    //最新用戶選擇位置
    @Override
    protected void onRestart() {
        super.onRestart();
        myRef.child("user").child(uid).child("currentLatLng").child("title").setValue(str1);
        myRef.child("user").child(uid).child("currentLatLng").child("LatLng").setValue(latLng);
        noticeCount.setVisibility(View.GONE);
        firebase_select_userLatlng(myRef3);
    }
    private void firebase_select_userLatlng(DatabaseReference db) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //int x_sum=(int)snapshot.getChildrenCount();    獲取分支總數
                try{
                    str1 = String.valueOf(snapshot.child("title").getValue());
                    latLng = new LatLng((double) snapshot.child("LatLng").child("latitude").getValue(), (double) snapshot.child("LatLng").child("longitude").getValue());
                    geocoder = new Geocoder(Home.this, Locale.TRADITIONAL_CHINESE);
                    List<Address> address;
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String str2 = address.get(0).getAdminArea() + ", " + address.get(0).getPostalCode();
                    str3 = address.get(0).getAddressLine(0);
                    btn_address1.setText(str1);
                    btn_address2.setText(str2);
                    if(!oldAddress.equals(str3)){
                        for (Map<String, Object> item : items){
                            LatLng temLatlng = (LatLng) item.get("pushLatlng");
                            double distance = getDistance(latLng, temLatlng);
                            DecimalFormat dt = new DecimalFormat("0.#");

                            if(distance <= 5000){
                                if(distance >= 1000){
                                    distance = distance / 1000;
                                    String finalDistance = dt.format(distance);
                                    myRef.child("user").child(uid).child("Notice").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            notion_last = (int) snapshot.getChildrenCount();
                                            notion_last += 1;
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("content").setValue(String.valueOf(finalDistance) + "公里");
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("title").setValue(item.get("title").toString());
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("isRead").setValue(false);
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("type").setValue("location");
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                                else{
                                    String finalDistance1 = dt.format(distance);
                                    myRef.child("user").child(uid).child("Notice").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            notion_last = (int) snapshot.getChildrenCount();
                                            notion_last += 1;
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("content").setValue(String.valueOf(finalDistance1) + "公尺");
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("title").setValue(item.get("title").toString());
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("isRead").setValue(false);
                                            myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("type").setValue("location");
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                                firebase_select_noticeCount();
                            }
                        }
                    }
                    oldAddress = str3;
                } catch (Exception e) {
                    Toast.makeText(Home.this, "尚未設定所在地", Toast.LENGTH_SHORT).show();
                    openChangeaddress();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //更新登入時間及任務完成狀況
    private void firebase_update_date() {
        myRef.child("user").child(uid).child("mission").child("last_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    Long res = (Long) snapshot.getValue();
                    Long now_Time = new Date().getTime();
                    timeDistance = differentDays(new Date(res), new Date(now_Time));
                    if(timeDistance > 0){
                        myRef.child("user").child(uid).child("mission").child("heart").child("isFinish").setValue(false);
                        myRef.child("user").child(uid).child("mission").child("heart").child("isTake").setValue(false);
                        myRef.child("user").child(uid).child("mission").child("plus").child("isFinish").setValue(false);
                        myRef.child("user").child(uid).child("mission").child("plus").child("isTake").setValue(false);
                        myRef.child("user").child(uid).child("mission").child("thumb").child("isFinish").setValue(false);
                        myRef.child("user").child(uid).child("mission").child("thumb").child("isTake").setValue(false);
                        myRef.child("user").child(uid).child("mission").child("last_time").setValue(now_Time);
                    }
                    myRef.child("user").child(uid).child("collect_id").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                afterlist.add(Integer.parseInt(ds.getKey()));
                            }
                            myRef.child("push").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        if(!afterlist.isEmpty()) {
                                            while(afterlist.get(0) < Integer.parseInt(ds.getKey())){
                                                afterlist.remove(0);
                                                if(afterlist.isEmpty()){
                                                    break;
                                                }
                                            }
                                            if(!afterlist.isEmpty()){
                                                if(afterlist.get(0) == Integer.parseInt(ds.getKey())){
                                                    Long push_Length = (Long) ds.child("pushdate").child("push_Length").getValue();
                                                    Long push_Timepoint = (Long) ds.child("pushdate").child("push_Timepoint").getValue();
                                                    String push_Titile = (String) ds.child("title").getValue();
                                                    Long push_gap = push_Timepoint + push_Length - now_Time;
                                                    if(push_gap <= 43200000 && push_gap > 0){
                                                        int push_gaphour = (int) (push_gap / 3600000);
                                                        if(push_gaphour < 12){
                                                            push_gaphour ++;
                                                        }
                                                        int finalPush_gaphour = push_gaphour;
                                                        myRef.child("user").child(uid).child("Notice").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                notion_last = (int) snapshot.getChildrenCount();
                                                                notion_last += 1;
                                                                myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("content").setValue(String.valueOf(finalPush_gaphour) + "小時內");
                                                                myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("title").setValue(push_Titile);
                                                                myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("isRead").setValue(false);
                                                                myRef.child("user").child(uid).child("Notice").child(String.valueOf(notion_last)).child("type").setValue("time");
                                                            }
                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                catch (Exception e){
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void firebase_select_pushTime(){

//                            System.out.println(afterlist);
        if(!afterlist.isEmpty()) {
            System.out.println("在這");
            System.out.println(afterlist);
        }
    }

    //計算有幾則未讀通知
    private void firebase_select_noticeCount() {
        myRef.child("user").child(uid).child("Notice").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String,Object> item= (Map<String, Object>) ds.getValue();
                    boolean isRead = Boolean.valueOf(String.valueOf(item.get("isRead")));
                    if(!isRead){
                        count ++;
                    }
                }
                if(count>0 && count<=99){
                    noticeCount.setVisibility(View.VISIBLE);
                    noticeCount.setText(String.valueOf(count));
                }
                else if(count>99){
                    noticeCount.setVisibility(View.VISIBLE);
                    noticeCount.setText("99+");
                    noticeCount.setTextSize(10);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    //開啟介面
    private void openHome(){
        Intent intent=new Intent(this, Home.class);
        finish();
        startActivity(intent);
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
    private void openLabel() {
        Intent intent = new Intent(this, Notice.class);
        startActivity(intent);
    }
    private void openChangeaddress() {
        Intent intent = new Intent(this, Changeaddress.class);
        startActivityForResult(intent, ACTIVITY_REPORT);
    }
    private void openClassify() {
        Intent intent = new Intent(this, Classify.class);
        startActivity(intent);
    }
    private void openClassifyTag() {
        Intent intent = new Intent(this, ClassifyTag.class);
        startActivity(intent);
    }
    private void openEveryday() {
        Intent intent = new Intent(this, Everyday.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode , int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            Toast.makeText(Home.this, "有到", Toast.LENGTH_SHORT).show();
            if (requestCode == ACTIVITY_REPORT) {
                latLng = data.getParcelableExtra("address_user_selected");
                str1 = data.getStringExtra("addressName_user_selected");
//                Toast.makeText(Home.this, str1, Toast.LENGTH_SHORT).show();
                geocoder = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
                try {
                    List<Address> address;
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String str2 = address.get(0).getAdminArea() + ", " + address.get(0).getPostalCode();
                    btn_address1.setText(str1);
                    btn_address2.setText(str2);
                } catch (Exception e) {
                }
            }
            else if (requestCode == ACTIVITY_REPORT_2) {
                latLng = data.getParcelableExtra("address_user_selected");
            }
        }
    }

    private static int differentDays(Date date1, Date date2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        Calendar cal_past = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_past.setTime(date1);
        cal_now.setTime(date2);
        int day_past = cal_past.get(Calendar.DAY_OF_YEAR);
        int day_now = cal_now.get(Calendar.DAY_OF_YEAR);
        int year_past = cal_past.get(Calendar.YEAR);
        int year_now = cal_now.get(Calendar.YEAR);
        if (year_past < year_now){
            int timeDistance = 1;
            return timeDistance;
        }
        else if(year_past > year_now){
            int timeDistance = -1;
            return timeDistance;
        }else{
            return day_now-day_past;
        }
    }
    public static double getDistance(LatLng start,
                                      LatLng end) {
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;

        return d*1000;
    }/*
    public static boolean checkLongPress(MotionEvent e) {
        float ms = 0, upTime = 0;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:   //手指的初次觸摸
                ms = System.currentTimeMillis();
                System.out.println("ms:"+ms);
                break;
            case MotionEvent.ACTION_MOVE:   //手指滑動
                System.out.println("move"+ms);
                break;
            case MotionEvent.ACTION_UP: //抬起
                upTime = System.currentTimeMillis() - ms;
                System.out.println("timeRRR:"+upTime);
                break;
            default:
                break;
        }
        //判斷是滑動還是點擊操作、判斷是否繼續傳遞信號
        if (upTime < 300) {   //點擊事件
            return true;
        }
        return false;

    }*/


    //0930  TODO:點及各種按鈕時(EG:TAG、收藏)會跳懸浮窗
    public static class  RecyclerItemClickListener  implements  RecyclerView.OnItemTouchListener  {
        private OnItemClickListener mListener;
        GestureDetector mGestureDetector;


        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());

            //if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {

            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
                return true ;
            }
            return false ;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
           // mListener.onItemClick(childView, view.getChildAdapterPosition(childView));

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
        public RecyclerItemClickListener (Context context, final RecyclerView recyclerView, OnItemClickListener listener)  {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                public boolean onSingleTapUp (MotionEvent e) {
                    if (mListener != null ) { //if (mChildView != null && mListener != null ) {
                        final int pos=0; //pos= mRecyclerView.getChildAdapterPosition(mChildView);
                        if (pos != RecyclerView.NO_POSITION) {  //在layout佈局沒有完成的時候會返回NO_POSITION
                            mListener.onItemClick(recyclerView, pos); //mListener.onItemClick(mChildView, pos);
                            return true ;
                        }
                    }
                    return false ;
                }
                public void onLongPress (MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mListener != null ) {    //TODO:判斷長按秒數
                      // if(checkLongPress(e)) {
                            mListener.onLongItemClick(child, recyclerView.getChildAdapterPosition(child));
                        //}
                    }
                }
            });
        }
        public  interface  OnItemClickListener  {
            public void onItemClick (View view, int position) ;
            public  void  onLongItemClick (View view, int position) ;
        }
    }
    private View.OnTouchListener imgListener = new View.OnTouchListener() {
        private float x, y;    // 原本圖片存在的X,Y軸位置
        private int mx, my; // 圖片被拖曳的X ,Y軸距離長度

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Log.e("View", v.toString());
            long eventtime=0;
            long downtime=0;
            switch (event.getAction()) {          //判斷觸控的動作

                case MotionEvent.ACTION_DOWN:// 按下圖片時
                    x = event.getX();                  //觸控的X軸位置
                    y = event.getY();                  //觸控的Y軸位置
                /*case MotionEvent.ACTION_UP:
                    if((event.getEventTime()-event.getDownTime()) >=50){
                        openEveryday();
                    }
                    break;*/

                case MotionEvent.ACTION_MOVE:// 移動圖片
                    eventtime=event.getEventTime();
                    downtime=event.getDownTime();

                    //getX()：是獲取當前控件(View)的座標

                    //getRawX()：是獲取相對顯示螢幕左上角的座標
                    mx = (int) (event.getRawX() - x);
                    my = (int) (event.getRawY() - y);
                    v.layout(mx, my, mx + v.getWidth(), my + v.getHeight());
                    break;
            }
            long sum=downtime-eventtime;
            if(sum <=0.05){
                return false;
            }
            Log.e("address", String.valueOf(mx) + "~~" + String.valueOf(my)); // 記錄目前位置
            return true;
        }
    };




}
