package com.example.coflash;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coflash.ui.main.Upload;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> implements TagAdapter.OnChildClick {
    private Context mContext;
    private List<Upload> mUploads;
    private List<Myinfo.PlusString> mPlusString;
    private List<Map<String,Object>> mitems;
    private LayoutInflater layoutInflater;
    private String uid;
    private DatabaseReference myRef2,myRef;
    public static String pos_classify;
    private Button tag;

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private OnItemClick onItemClick;
    private OnItemLongClickListener mOnItemLongClickListener = null; //0910

    private int count=0;
    private boolean isLongPress=true;   //0930

    String strClass;

    public ImageAdapter(OnItemLongClickListener onItemLongClickListener){

    }

    public ImageAdapter(OnItemClick onItemClick){
        this.onItemClick=onItemClick;
    }

    public ImageAdapter(Context context,List<Upload> uploads,List<Myinfo.PlusString> plusStrings){

        mContext=context;
        mUploads=uploads;
        mPlusString=plusStrings;
       // layoutInflater=LayoutInflater.from(mContext);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//取得使用者資料
        if (user != null) {
            uid = user.getUid();//取得uid
        }
        //連線資料庫
        FirebaseDatabase database = FirebaseDatabase.getInstance();//連線數據庫
        myRef = database.getReference("DB");
        myRef2 = myRef.child("push");

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.push_activity,parent,false));

    }
    long msCurrent ;
    long timeMove ;
    float lastY;
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Upload uploadCurrent=mUploads.get(position);
        Myinfo.PlusString pluscurrent=mPlusString.get(position);
        holder.id.setText(String.valueOf(pluscurrent.getId()));
        holder.tv1.setText(pluscurrent.getClassification());
        strClass = pluscurrent.getClassification();
        switch (strClass) {
            case "食":
                holder.imb_classify.setImageResource(R.drawable.img_food);
                break;
            case "衣":
                holder.imb_classify.setImageResource(R.drawable.img_cloth);
                break;
            case "住":
                holder.imb_classify.setImageResource(R.drawable.img_place);
                break;
            case "行":
                holder.imb_classify.setImageResource(R.drawable.img_move);
                break;
            case "育":
                holder.imb_classify.setImageResource(R.drawable.img_education);
                break;
            case "樂":
                holder.imb_classify.setImageResource(R.drawable.img_entertainment);
                break;
            case "其他":
                holder.imb_classify.setImageResource(R.drawable.img_other);
                break;
        }
        holder.tv3.setText(pluscurrent.getTitle());
        holder.name.setText(pluscurrent.getName());

        Glide.with(mContext).load(uploadCurrent.getImageUrl()).centerCrop().into(holder.imageView);  //CenterCrop()是一個裁剪技術，即縮放圖像讓它填充到 ImageView 界限內並且裁剪額外的部分。ImageView 可能會完全填充，但圖像可能不會完整顯示。


        //設置巢狀RecyclerView
        holder.RV_tag.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        TagAdapter tagadapter=new TagAdapter(mPlusString, position, this);
        holder.RV_tag.setAdapter(tagadapter);
        holder.RV_tag.setRecycledViewPool(viewPool);
        tagadapter.setOnItemClickListener(new TagAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, String tagtext) {
                record(tagtext);
            }
        });
        // 長按事件註冊及分發0910
        /*if(null != mOnItemLongClickListener) {
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mOnItemLongClickListener.onLongClick(holder.imageView, position);
                }
            });
        }*/

        //按讚顯示
        String plusid=String.valueOf(pluscurrent.getId());
        myRef.child("push").child(plusid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    if (task.getResult().child("gooduid").child(uid).getValue() != null) {//代表之前這個uid已按過讚
                        Glide.with(mContext).load(R.drawable.good_color).into(holder.good_button);
                    }else{//未按讚
                        Glide.with(mContext).load(R.drawable.good).into(holder.good_button);
                    }
                    if (task.getResult().child("baduid").child(uid).getValue() != null) {//代表之前這個uid已按過倒讚
                        Glide.with(mContext).load(R.drawable.bad_color).into(holder.bad_button);
                    }else{//未按讚
                        Glide.with(mContext).load(R.drawable.bad).into(holder.bad_button);
                    }
                }
            }
        });
        //收藏
        myRef.child("user").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    if (task.getResult().child("collect_id").child(plusid).getValue() != null) {//代表之前這個uid收藏了這則貼文
                        Glide.with(mContext).load(R.drawable.heart_color).into(holder.heart);
                    }else{//未收藏
                        Glide.with(mContext).load(R.drawable.heart).into(holder.heart);
                    }
                }
            }
        });
        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("user").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            if (task.getResult().child("collect_id").child(plusid).getValue() != null) {//代表之前這個uid收藏了這則貼文
                                Glide.with(mContext).load(R.drawable.heart).into(holder.heart);
                                myRef.child("user").child(uid).child("collect_id").child(plusid).removeValue();//移除紀錄
                            }else{//未收藏
                                Glide.with(mContext).load(R.drawable.heart_color).into(holder.heart);
                                myRef.child("user").child(uid).child("collect_id").child(plusid).setValue("0");//存進資料庫
                                myRef.child("user").child(uid).child("mission").child("heart").child("isFinish").setValue(true);
                            }
                        }
                    }
                });
            }
        });
    }



    public void record(String tagtext){//設置tag跳頁
        pos_classify=tagtext;
        Home.status_classify=4;
        openClassifyTag();
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    // 設置長按事件0910
    public void setOnItemLongClickListener(OnItemLongClickListener l) {
        this.mOnItemLongClickListener = (OnItemLongClickListener) l;
    }


    // 長按事件接口
    public interface OnItemLongClickListener {
        boolean onLongClick(View parent, int position);

        void onLongItemClick(int childAdapterPosition);
    }
    //0929
    public interface OnTouchListener{
        boolean onTouchEvent(View parent,int position);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView id,tv1,tv3,name;
        public ImageView imageView,good_button,bad_button,heart, imb_classify;
        public RecyclerView RV_tag;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            id=itemView.findViewById(R.id.id);
            tv1=itemView.findViewById(R.id.tv1);
            tv3=itemView.findViewById(R.id.tv3);
            name=itemView.findViewById(R.id.name);
            imageView=itemView.findViewById(R.id.imageView);
            RV_tag=itemView.findViewById(R.id.RV_tag);
            good_button=itemView.findViewById(R.id.good);
            bad_button=itemView.findViewById(R.id.bad);
            heart=itemView.findViewById(R.id.heart);
            imb_classify = itemView.findViewById(R.id.imb_classify);
        }
    }
    /**此處為點選Child-Item後從
     * @see TagAdapter 的回傳*/

    @Override
    public void onChildClick(Myinfo.PlusString data, int parentPosition) {
        onItemClick.onItemClick(data,mPlusString.get(parentPosition));
    }
    interface OnItemClick{
        void onItemClick(Myinfo.PlusString data, Myinfo.PlusString myData);
    }

    private void openClassifyTag() {
        Intent intent = new Intent(mContext, ClassifyTag.class);
        mContext.startActivity(intent);
    }
    public interface OnItemClickListener{
        boolean onClick(View parent, int position);
    }
    public boolean getLongPress(){  //0930
        return isLongPress;
    }

}
