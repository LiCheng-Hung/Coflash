package com.example.coflash;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.app.SearchManager;
import android.net.Uri;
import android.widget.Toast;
import android.util.Log;
import android.util.DisplayMetrics;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.coflash.ui.main.Upload;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class SearchResultsActivity extends AppCompatActivity{

    private RecyclerView RV,RV_search;
    private FirebaseDatabase db;
    private Button tagsGroup;
    Context context = this,ncontext;
    int x_last = 0;
    private Object List;
    TextView id,tv1,tv2,tv3; //?????????????????????layout????????????????????????????????????
    ImageView imageView,good_view,bad_view;
    //????????????
    Uri uri;
    String data_list,uid;
    StorageReference storageReference,pic_storage;
    int PICK_CONTACT_REQUEST=1;
    DatabaseReference myRef, myRef2, myRef3;
    public static final float ALPHA_FULL = 1.0f;
    String plusid;
    List<String> classify_string=new ArrayList<>();
    public static String pos_classify;//???????????????(classify?????????)
    public static int status_classify;//????????????(classify?????????)

    ImageAdapter imageAdapter;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //??????action bar??????
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#F3BA55"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //???????????????
        FirebaseDatabase database = FirebaseDatabase.getInstance();//???????????????
        myRef = database.getReference("DB");
        myRef2 = myRef.child("push");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//?????????????????????
        if (user != null) {
            uid = user.getUid();//??????uid
        }

        //??????tag
        RV_search=(RecyclerView)findViewById(R.id.RV_classify);
        //RV_classify.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RV_search.setLayoutManager(linearLayoutManager);
        myRef.child("tag").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String s=String.valueOf(ds.getKey());
                    String data_text=s.toLowerCase();//????????????title
                    String[] data_text_split=data_text.split("");
                    String query_text=Tag.query_text.toLowerCase();//query???title
                    String[] query_text_split=query_text.split("");
                    for(int i=0;i<data_text_split.length;i++){//abc
                        if(data_text_split[i].equals(query_text_split[0])){//b
                            int k=i;//1
                            int j=0;
                            boolean status=true;
                            while((k<data_text_split.length) && (j<query_text_split.length)) {
                                if(data_text_split[k].equals(query_text_split[j])){
                                    k++;
                                    j++;
                                }else{
                                    status=false;
                                    break;
                                }
                                if(j>=query_text_split.length){//5
                                    break;
                                }
                                if(k>=data_text_split.length){//5
                                    status=false;
                                    break;
                                }
                            }
                            if(status==true){
                                classify_string.add(s);
                                break;
                            }
                        }
                    }
                }
                setup_classify();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //????????????RV
        RV=(RecyclerView)findViewById(R.id.RV);
        RV.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        //??????Adapter??????????????????
        imageAdapter = new ImageAdapter(new ImageAdapter.OnItemClick() {
            @Override
            public void onItemClick(Myinfo.PlusString data, Myinfo.PlusString myData) {
                /*Toast.makeText(Home.this
                        , "?????????"+myData.getTitle()+"??? "+data.getNesTitle()
                        , Toast.LENGTH_SHORT).show();*/
                Toast.makeText(SearchResultsActivity.this
                        , "????????????YA"
                        , Toast.LENGTH_SHORT).show();
            }
        });

        id=(TextView)findViewById(R.id.id);
        tv1=(TextView)findViewById(R.id.tv1);
        tv3=(TextView)findViewById(R.id.tv3);
        imageView=(ImageView)findViewById(R.id.imageView);
        good_view=(ImageView)findViewById(R.id.good);
        bad_view=(ImageView)findViewById(R.id.bad);

        //heart_view=(ImageView)findViewById(R.id.heart);

        firebase_select(myRef2);

        /*back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });*/
        setup_classify();
    }
    public void setup_classify(){
        SearchAdapter searchadapter=new SearchAdapter(context,classify_string,R.layout.search_cardview);
        RV_search.setAdapter(searchadapter);

        searchadapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                record(position);
            }
        });
    }
    public void record(int position){
        pos_classify=classify_string.get(position);
        openClassifyTag();
        Home.status_classify=7;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        menuItem.expandActionView();
        searchView.setQuery(Tag.query_text, false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Tag.query_text=query;
                openSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /**??????RecyclerView??????Filter??????*/
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
        //return true;
    }
    List list;
    public List<Map<String, Object>> getItem() {
        return list;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        //?????????????????????
        RV.requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
    //??????????????????
    private void firebase_select(DatabaseReference db) {
        final List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
        final List<Myinfo.PlusString> plus=new ArrayList<>();
        final List<Upload> uploadString=new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
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
                    //???????????????url???????????????upload???
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
                    String data_text=push_data.getTitle().toLowerCase();//????????????title
                    String[] data_text_split=data_text.split("");
                    String query_text=Tag.query_text.toLowerCase();//query???title
                    String[] query_text_split=query_text.split("");
                    for(int i=0;i<data_text_split.length;i++){//abc
                        if(data_text_split[i].equals(query_text_split[0])){//b
                            int k=i;//1
                            int j=0;
                            boolean status=true;//?????????????????????????????????true?????????
                            while(true) {
                                if(data_text_split[k].equals(query_text_split[j])){
                                    k++;//0.1.2.3.4
                                    j++;
                                }else{
                                    status=false;
                                    break;
                                }
                                if(j>=query_text_split.length){//5
                                    break;
                                }
                                if(k>=data_text_split.length){//5
                                    status=false;
                                    break;
                                }
                            }
                            if(status==true){
                                plus.add(push_data);
                                uploadString.add(upload[0]);
                                x_last = Integer.parseInt(ds.getKey());   //??????????????????key???
                                break;
                            }
                        }
                    }
                }
                if(plus.size()>0) {
                    ImageAdapter imageadapter = new ImageAdapter(context, uploadString, plus);
                    RV.setAdapter(imageadapter);
                    recyclerViewAction(RV, items, imageadapter);//??????????????????
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    private void recyclerViewAction(RecyclerView RV, final List<Map<String,Object>> items, final ImageAdapter imageadapter) {//??????????????????
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {//?????????????????????????????????
                return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            }
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView//??????item???????????????????????????????????????????????????????????????????????????????????????view??????????????????????????????????????????
                    , @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder targe) {
                int position = viewHolder.getAdapterPosition();

                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {////??????????????????
                int position = viewHolder.getAdapterPosition();
                plusid=String.valueOf(items.get(position).get("id"));//?????????id
                switch (direction) {
                    case ItemTouchHelper.LEFT://?????????
                        myRef.child("push").child(plusid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    String numbertext = String.valueOf(task.getResult().child("badnumber").getValue());//???????????????????????????
                                    if(task.getResult().child("gooduid").child(uid).getValue() != null){//??????uid?????????????????????
                                        //??????good??????-1?????????uid
                                        myRef.child("push").child(plusid).child("gooduid").child(uid).removeValue();
                                        String goodnumbertext = String.valueOf(task.getResult().child("goodnumber").getValue());//????????????????????????
                                        int numm = Integer.parseInt(goodnumbertext);//???????????????
                                        numm--;//???1
                                        String aftergoodnumbertext = Integer.toString(numm);//???????????????
                                        myRef.child("push").child(plusid).child("goodnumber").setValue(aftergoodnumbertext);//???????????????
                                    }
                                    myRef.child("push").child(plusid).child("baduid").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {//?????????????????????baduid??????
                                        @Override
                                        public void onComplete(Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            } else {
                                                if (task.getResult().child(uid).getValue() != null) {//??????????????????uid?????????????????????-1
                                                    myRef.child("push").child(plusid).child("baduid").child(uid).removeValue();
                                                    int number = Integer.parseInt(numbertext);//???????????????
                                                    number--;//???1
                                                    String afternumbertext = Integer.toString(number);//???????????????
                                                    myRef.child("push").child(plusid).child("badnumber").setValue(afternumbertext);//???????????????
                                                } else {//???????????????????????????+1
                                                    myRef.child("push").child(plusid).child("baduid").child(uid).setValue(0);
                                                    myRef.child("user").child(uid).child("mission").child("thumb").child("isFinish").setValue(true);
                                                    if (numbertext != "null") {//?????????????????????uid????????????
                                                        int number = Integer.parseInt(numbertext);//???????????????
                                                        number++;//???1
                                                        String afternumbertext = Integer.toString(number);//???????????????
                                                        myRef.child("push").child(plusid).child("badnumber").setValue(afternumbertext);//???????????????
                                                    } else {
                                                        int number = 1;
                                                        String afternumbertext = Integer.toString(number);//???????????????
                                                        myRef.child("push").child(plusid).child("badnumber").setValue(afternumbertext);//???????????????
                                                    }
                                                }
                                            }
                                            imageadapter.notifyItemChanged(viewHolder.getAdapterPosition());//?????????????????????????????????item//????????????????????????
                                        }
                                    });
                                }
                            }
                        });
                        break;
                    case ItemTouchHelper.RIGHT://??????
                        myRef.child("push").child(plusid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    String numbertext = String.valueOf(task.getResult().child("goodnumber").getValue());//????????????????????????
                                    if(task.getResult().child("baduid").child(uid).getValue() != null){//??????uid????????????????????????
                                        //??????bad??????-1?????????uid
                                        myRef.child("push").child(plusid).child("baduid").child(uid).removeValue();
                                        String badnumbertext = String.valueOf(task.getResult().child("badnumber").getValue());//???????????????????????????
                                        int num = Integer.parseInt(badnumbertext);//???????????????
                                        num--;//???1
                                        String afterbadnumbertext = Integer.toString(num);//???????????????
                                        myRef.child("push").child(plusid).child("badnumber").setValue(afterbadnumbertext);//???????????????
                                    }
                                    myRef.child("push").child(plusid).child("gooduid").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            } else {
                                                if (task.getResult().child(uid).getValue() != null) {//???????????????????????????-1
                                                    myRef.child("push").child(plusid).child("gooduid").child(uid).removeValue();
                                                    int number = Integer.parseInt(numbertext);//???????????????
                                                    number--;//???1
                                                    String afternumbertext = Integer.toString(number);//???????????????
                                                    myRef.child("push").child(plusid).child("goodnumber").setValue(afternumbertext);//???????????????
                                                } else {//?????????????????????+1
                                                    myRef.child("push").child(plusid).child("gooduid").child(uid).setValue(0);
                                                    myRef.child("user").child(uid).child("mission").child("thumb").setValue(true);
                                                    if (numbertext != "null") {
                                                        int number = Integer.parseInt(numbertext);//???????????????
                                                        number++;//???1
                                                        String afternumbertext = Integer.toString(number);//???????????????
                                                        myRef.child("push").child(plusid).child("goodnumber").setValue(afternumbertext);//???????????????
                                                    } else {
                                                        int number = 1;
                                                        String afternumbertext = Integer.toString(number);//???????????????
                                                        myRef.child("push").child(plusid).child("goodnumber").setValue(afternumbertext);//???????????????
                                                    }
                                                }
                                            }
                                            imageadapter.notifyItemChanged(viewHolder.getAdapterPosition());//?????????????????????????????????item//????????????????????????
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
                //?????????????????????????????????????????????????????????
                viewHolder.itemView.setScrollX(0);
            }
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {//???????????????
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
    private void openClassify() {
        Intent intent = new Intent(this, Classify.class);
        startActivity(intent);
    }
    private void openClassifyTag() {
        Intent intent = new Intent(this, ClassifyTag.class);
        startActivity(intent);
    }
    private void openSearch() {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        startActivity(intent);
    }
}
