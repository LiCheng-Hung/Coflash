package com.example.coflash;

//import static com.google.firebase.database.snapshot.LeafNode.LeafType.String;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coflash.ui.main.Upload;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.lang.String;

public class Plus extends Activity implements NumberPicker.OnValueChangeListener{
    Context context=this;
    int x_last=0;
    String x_select="0";
    EditText pushLabel;
    EditText pushTitle;
    Button push;
    ImageButton plusBack;
    int Preset=0;

    //????????????+????????????
    ImageButton uploadbtn,imageButton3;  //0821
    ImageView photo;
    Intent intent;
    int PICK_CONTACT_REQUEST=1;
    Uri uri;
    private StorageReference mStorageRef;
    private DatabaseReference myRef,myRef2,myRefUserAddr, myRef3;
    ImageView imageView;

    Spinner spinner;
    String classification;
    int count=0;
    String text="";
    String addr;
    String uid;
    String name;

    TextView PushTime,PushDate,ActivityStartDate,ActivityStartTime,ActivityEndDate,ActivityEndTime;

    private RecyclerView tagsRecycler,addrRecycler;
    tagsGroupAdapter groupAdapter;
    AddrGroupAdapter addrGroupAdapter;

    TextView pushPlace;
    String iname="",url="";
    boolean uploadPhoto=false;
    TextView pushTime2;
    private Activity activity;

    String pushDay ;
    String pushTime ;   //??????????????????
    String activityStartDate ;    //??????????????????
    String activityStartTime;    //??????????????????
    String activityEndDate;    //??????????????????
    String activityEndTime;    //??????????????????
    String pushplace;
    Upload upload;

    //??????????????????
    private static final int ACTIVITY_REPORT = 1000;
    LatLng latLng;
    String str1;
    Geocoder geocoder;

    int yearToday,monthToday,dayToday;
    String weekToday;
    EditText word;    //??????????????????

    //tag??????
    List<Map<String,Object>> Tag = new ArrayList<Map<String,Object>>();
    int notion_last = 0;

    //????????????
    Date push_Time;
    Long push_Length = Long.valueOf(259199000);
    String push_Date, push_Timepoint;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        spinner = (Spinner) findViewById(R.id.chooseClassification);

        pushLabel = (EditText) findViewById(R.id.pushLabel);
        pushTitle = (EditText) findViewById(R.id.pushTitle);
        push = (Button) findViewById(R.id.pushCheck);
        plusBack = (ImageButton) findViewById(R.id.plusBack);

        uploadbtn = (ImageButton) findViewById(R.id.uploadbtn);    //??????+????????????
        photo = (ImageView) findViewById(R.id.photo);
        imageView = (ImageView) findViewById(R.id.imageView);

        ActivityStartDate = (TextView) findViewById(R.id.ActivityStartDate);
        ActivityStartTime = (TextView) findViewById(R.id.ActivityStartTime);
        ActivityEndDate = (TextView) findViewById(R.id.ActivityEndDate);
        ActivityEndTime = (TextView) findViewById(R.id.ActivityEndTime);
        PushDate = (TextView) findViewById(R.id.PushDate);
        PushTime = (TextView) findViewById(R.id.PushTime);

        tagsRecycler = (RecyclerView) findViewById(R.id.tagsRecycler);
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        addrRecycler = (RecyclerView) findViewById(R.id.addrRecycler);
        addrRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        pushPlace = (TextView) findViewById(R.id.pushPlace);

        imageButton3 = (ImageButton) findViewById(R.id.imageButton3);  //0821
        pushTime2=(TextView) findViewById(R.id.pushTime2);
        word=(EditText) findViewById(R.id.word);

        //textview?????????????????????????????????????????????
        Calendar calendar = Calendar.getInstance();
        yearToday = calendar.get(Calendar.YEAR);      //??????"????????????????????????
        monthToday = calendar.get(Calendar.MONTH);
        dayToday = calendar.get(Calendar.DAY_OF_MONTH);
        weekToday=ZellerForWeek(yearToday,monthToday,dayToday);
        String today=yearToday+"/"+(monthToday+1)+"/"+dayToday+" ("+ weekToday +")";
        PushDate.setText(today);
        ActivityStartDate.setText(today);
        ActivityEndDate.setText(today);
        //String nowDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());    //??????????????????
        Date date = new Date();
        push_Time = date;
        String nowTime = new SimpleDateFormat("HH:mm").format(date);
        PushTime.setText(nowTime);
        ActivityStartTime.setText(nowTime);
        ActivityEndTime.setText(nowTime);
        classification="???";

        pushTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//?????????????????????
        if (user != null) {
            uid = user.getUid();//??????uid
        }

        try{
            Intent i=new Intent();
            startActivityForResult(i, 1000);
        }catch(Exception e){
            Toast.makeText(this,"YET",Toast.LENGTH_SHORT);
        }

        //???DB
        FirebaseDatabase database = FirebaseDatabase.getInstance();//???????????????
        myRef = database.getReference("DB");
        //child():??????????????????
        myRef2 = myRef.child("push");
        myRefUserAddr = myRef.child("user").child(uid).child("address");    //0904
        myRef3 = myRef.child("tag");

        myRef.child("user").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    name = String.valueOf(task.getResult().child("name").getValue());//????????????user_name
                }
            }
        });

        firebase_select(myRef2);
        firebase_selectAddr(myRefUserAddr);
        firebase_selectTag(myRef3);

        List<String> tagList = new ArrayList<>();
        //????????????
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((pushLabel.getText().toString().indexOf("#")) > -1) {   //?????????"#"
                    //???"#"???????????????list???
                    for (String s : pushLabel.getText().toString().trim().split("#")) {
                        if (!(s.equals("")) && !(s.equals(" "))) {
                            tagList.add(s);
                        }
                    }

                } else {    //?????????"#"
                    if (!(pushLabel.getText().toString().trim().equals("")) && !(pushLabel.getText().toString().trim().equals(" "))) {
                        tagList.add(pushLabel.getText().toString().trim());
                    }
                }
                //?????????5???????????????????????????????????????????????????????????????
                if (tagList.size() <= 5) {
                    //0829 ??????????????????????????????????????????????????????????????????
//                    pushDay = PushDate.getText().toString();   //??????????????????
//                    pushTime = PushTime.getText().toString();   //??????????????????
                    activityStartDate = ActivityStartDate.getText().toString();    //??????????????????
                    activityStartTime = ActivityStartTime.getText().toString();    //??????????????????
                    activityEndDate = ActivityEndDate.getText().toString();    //??????????????????
                    activityEndTime = ActivityEndTime.getText().toString();    //??????????????????
                    pushplace=pushPlace.getText().toString();   //????????????


                    if(pushTitle.getText().toString().equals("")){
                        Toast.makeText(Plus.this, "?????????????????????", Toast.LENGTH_LONG).show();
                    }else if(pushplace.equals("??????")) {    //?????????????????????
                        Toast.makeText(Plus.this, "?????????????????????", Toast.LENGTH_LONG).show();
                    }else if(iname=="" && uri==null) {  //???????????????
                        Toast.makeText(Plus.this, "???????????????", Toast.LENGTH_LONG).show();
                    }else{  //????????????
                        x_last+=1;

                        /*??????????????????????????????????????????ID?????????(??????)
                        PlusString plusString = new PlusString(classification, pushLabel.getText().toString(), pushTitle.getText().toString(), uid, name);
                        myRef2.child(String.valueOf(x_last)).setValue(plusString); */
                        //0911 ??????????????????????????????????????????ID???????????????????????????
                        Myinfo.PlusString plusString = new Myinfo.PlusString(classification, pushLabel.getText().toString(), pushTitle.getText().toString(), uid, name,
                                pushplace,word.getText().toString());

                        //??????tag
                        for (int i = 0; i < tagList.size(); i++) {
                            boolean same = false;
                            long tagNumber = 0;
                            ArrayList<String> follower = new ArrayList<String>();
                            for(Map<String,Object> tag : Tag){
                                if(tagList.get(i).equals((String) tag.get("tagName"))){
                                    same = true;
                                    System.out.println(tagList.get(i));
                                    tagNumber = (long) tag.get("tagNumber");
                                    follower = (ArrayList<String>) tag.get("follower");
                                }
                            }
                            if (same){
                                myRef.child("tag").child(tagList.get(i)).child("number of post").setValue(tagNumber+1);
                                for(int j=0; j<follower.size(); j++){
                                    String uid_current = follower.get(j);
                                    String tag_current = tagList.get(i);
                                    myRef.child("user").child(uid_current).child("Notice").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            notion_last = (int) snapshot.getChildrenCount();
                                            notion_last += 1;
                                            myRef.child("user").child(uid_current).child("Notice").child(String.valueOf(notion_last)).child("content").setValue(tag_current);
                                            myRef.child("user").child(uid_current).child("Notice").child(String.valueOf(notion_last)).child("title").setValue(pushTitle.getText().toString());
                                            myRef.child("user").child(uid_current).child("Notice").child(String.valueOf(notion_last)).child("isRead").setValue(false);
                                            myRef.child("user").child(uid_current).child("Notice").child(String.valueOf(notion_last)).child("type").setValue("tag");
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                            else {
                                myRef.child("tag").child(tagList.get(i)).child("number of post").setValue(1);
                            }
                        }

                        //????????????????????????
                        String str_pushTime = push_Date+push_Timepoint;
                        SimpleDateFormat dateParser = new SimpleDateFormat("yyyyMMddHHmmss");
                        try {
                            push_Time = dateParser.parse(str_pushTime);
                        } catch (Exception e) {
                        }

                        //0911 ??????:????????????????????????????????????ID?????????
                        myRef2.child(String.valueOf(x_last)).child("classification").setValue(classification);
                        myRef2.child(String.valueOf(x_last)).child("tag").setValue(pushLabel.getText().toString());
                        myRef2.child(String.valueOf(x_last)).child("title").setValue(pushTitle.getText().toString());
                        myRef2.child(String.valueOf(x_last)).child("author").setValue(uid);
                        myRef2.child(String.valueOf(x_last)).child("name").setValue(name);
                        //????????????????????????????????????
                        myRef2.child(String.valueOf(x_last)).child("pushdate").child("push_Length").setValue(push_Length);
                        myRef2.child(String.valueOf(x_last)).child("pushdate").child("push_Timepoint").setValue(push_Time.getTime());
                        myRef2.child(String.valueOf(x_last)).child("activitydate").child("activityStartDate").setValue(activityStartDate);
                        myRef2.child(String.valueOf(x_last)).child("activitydate").child("activityStartTime").setValue(activityStartTime);
                        myRef2.child(String.valueOf(x_last)).child("activitydate").child("activityEndDate").setValue(activityEndDate);
                        myRef2.child(String.valueOf(x_last)).child("activitydate").child("activityEndTime").setValue(activityEndTime);
                        myRef2.child(String.valueOf(x_last)).child("word").setValue(word.getText().toString());
                        //myRef2.child(String.valueOf(x_last)).child("pushplace").setValue(pushplace);
                        myRef2.child(String.valueOf(x_last)).child("pushplace").child("address").setValue(pushplace);
                        myRef2.child(String.valueOf(x_last)).child("pushplace").child("latitude").setValue(latLng.latitude);
                        myRef2.child(String.valueOf(x_last)).child("pushplace").child("longitude").setValue(latLng.longitude);

                        if (iname != "" && url!="") {  //????????????????????????
                            uploadPhoto=true;
                            Upload upload = new Upload(iname, url);//???????????????????????????new Upload
                            myRef2.child(String.valueOf(x_last)).child("imageName").setValue(iname);
                            myRef2.child(String.valueOf(x_last)).child("imageUrl").setValue(url);
                            Toast.makeText(Plus.this, "Got url Successfully", Toast.LENGTH_SHORT).show();
                            //??????????????????
                            openHome();
                        }else {
                            uploadFile();
                        }

                        text = "";  //????????????????????????
                        count = 0;  //count:????????????????????????????????????
                    }
                } else{  //??????5??????????????????????????????num??????????????????????????????
                    Toast.makeText(context, "??????????????????5???", Toast.LENGTH_SHORT).show();
                    tagList.clear();
                }
            }
        });


        //?????????
        plusBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //????????????
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setType("image/*");      //????????????????????????????????????--????????????????????? setType("image/*")
                intent.setAction(Intent.ACTION_GET_CONTENT);
                iname="";//0823
                url="";
                startActivityForResult(intent, 1);
            }
        });

        //??????
        pushPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddress();
            }
        });

        //??????????????????"??????"
        //??????ArrayAdapter?????????????????????string array?????????spinner???layout
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,    //?????????Context
                        R.array.class_array,                             //??????????????????
                        android.R.layout.simple_spinner_item);  //??????Spinner???????????????View(????????????????????????)

        //??????Spinner?????????????????????????????????????????????????????????????????????????????????
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        //????????????ArrayAdapter?????????????????????spinner
        spinner.setAdapter(adapter);
        //?????????????????????0???
        spinner.setSelection(0, true);  //0823
        spinner.setOnItemSelectedListener(spnOnItemSelected);

        //0823
        //int[] bgcolor = new int[]{R.drawable.green, R.drawable.purple, R.drawable.orange};
        String[] bgcolor={"?????????","?????????","?????????"};
        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Plus.this);
                builder.setTitle("????????????");
                builder.setSingleChoiceItems(bgcolor, Preset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Preset = which;//???????????????????????????
                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(Preset){
                            case 0:
                                photo.setImageResource(R.drawable.greenbig);
                                iname="greenBig.png";
                                url="https://firebasestorage.googleapis.com/v0/b/coflash-48a90.appspot.com/o/uploads%2FgreenBig.png?alt=media&token=69d31ba8-51e0-40e1-b109-f481ce94acfc";
                                break;
                            case 1:
                                photo.setImageResource(R.drawable.purplebig);
                                iname="purpleBig.png";
                                url="https://firebasestorage.googleapis.com/v0/b/coflash-48a90.appspot.com/o/uploads%2FpurpleBig.png?alt=media&token=00fc2f6f-0ba5-47a1-b8ad-1f6949fbe131";
                                break;
                            case 2:
                                photo.setImageResource(R.drawable.orangebig);
                                iname="orangebig.png";
                                url="https://firebasestorage.googleapis.com/v0/b/coflash-48a90.appspot.com/o/uploads%2Forangebig.png?alt=media&token=f285d9df-7317-4344-b2cf-43d06a7badb6";
                                break;
                        }
                        dialog.dismiss();//???????????????
                    }
                });
                builder.show();
            }
        });
    }

    //??????????????????
    public void selectTime() {
        final Dialog dialog = new Dialog(Plus.this);
        dialog.setContentView(R.layout.number_picker_dialog);
        Button setBtn = dialog.findViewById(R.id.setBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
        final NumberPicker dayPicker = dialog.findViewById(R.id.numberPicker_D);
        dayPicker.setMaxValue(2);
        dayPicker.setMinValue(0);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setOnValueChangedListener(this);

        final NumberPicker hourPicker = dialog.findViewById(R.id.numberPicker_H);
        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);
        hourPicker.setWrapSelectorWheel(false);
        hourPicker.setOnValueChangedListener(this);

        final NumberPicker minPicker = dialog.findViewById(R.id.numberPicker_M);
        minPicker.setMaxValue(59);
        minPicker.setMinValue(10);
        minPicker.setWrapSelectorWheel(false);
        minPicker.setOnValueChangedListener(this);

        final NumberPicker secPicker = dialog.findViewById(R.id.numberPicker_S);
        secPicker.setMaxValue(59);
        secPicker.setMinValue(0);
        secPicker.setWrapSelectorWheel(false);
        secPicker.setOnValueChangedListener(this);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                pushTime2.setText(dayPicker.getValue()+"??? " +hourPicker.getValue() + "???:" + minPicker.getValue() + "???:" + secPicker.getValue()+"???");
                push_Length = Long.valueOf(dayPicker.getValue()*86400 + hourPicker.getValue()*3600 + minPicker.getValue()*60 + secPicker.getValue())*1000;
                System.out.println(push_Length);
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
    }

    //??????Spinner??????????????????
    private AdapterView.OnItemSelectedListener spnOnItemSelected
            = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // ???????????????????????????
            String sPos=String.valueOf(pos);
            classification=parent.getItemAtPosition(pos).toString();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            //?????????????????????
        }
    };



    List list;
    public List<Map<String,Object>> getItem(){
        return list;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void datePicker0(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);      //??????"??????"??????????????????????????????"??????"???
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // int week = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);//???????????????

        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String finalMyWeek = ZellerForWeek(year, month, day);
                push_Date = String.valueOf(year) + String.valueOf(month+1) + String.valueOf(day);
                System.out.println("??????");
                System.out.println(push_Date);
                //????????????
                String datetime = String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(day)+" ("+ finalMyWeek +")";
                PushDate.setText(datetime);   //?????????????????????????????????????????????
            }
        }, year, month, day).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void timePicker0(View v) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                push_Timepoint = String.valueOf(hourOfDay) + String.valueOf(minute);
                PushTime.setText(time);  //?????????????????????????????????????????????
            }
        }, hourOfDay, minute,true).show();

    }
    //???????????????????????????
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void datePicker(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);      //??????"????????????????????????
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String finalMyWeek = ZellerForWeek(year, month, day);

                //????????????
                String datetime = String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(day)+" ("+ finalMyWeek +")";
                ActivityStartDate.setText(datetime);   //?????????????????????????????????????????????
            }
        }, year, month, day).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void timePicker(View v) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                ActivityStartTime.setText(time);  //?????????????????????????????????????????????
            }
        }, hourOfDay, minute,true).show();

    }
    //???????????????????????????
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void datePicker2(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);      //??????"????????????????????????
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String finalMyWeek = ZellerForWeek(year, month, day);
                //????????????
                String datetime = String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(day)+" ("+ finalMyWeek +")";
                ActivityEndDate.setText(datetime);   //?????????????????????????????????????????????
            }
        }, year, month, day).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void timePicker2(View v) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                ActivityEndTime.setText(time);  //?????????????????????????????????????????????
            }
        }, hourOfDay, minute,true).show();

    }
    //??????(Zeller)?????????????????????
    public String ZellerForWeek(int year, int month, int day){
        int y=year-1;
        int m=month-1;
        int c=20;
        int d=day+12;
        int w;
        if((y+1)%4==0){  //???year?????????
            w=(y+(y/4)+(c/4)-2*c+(26*(m+1)/10)+d-1+1)%7;    //???+1
        }else{
            w=(y+(y/4)+(c/4)-2*c+(26*(m+1)/10)+d-1)%7;
        }
        String myWeek = null;
        switch(w)
        {
            case 6:
                myWeek="???";
                break;
            case 0:
                myWeek="???";
                break;
            case 1:
                myWeek="???";
                break;
            case 2:
                myWeek="???";
                break;
            case 3:
                myWeek="???";
                break;
            case 4:
                myWeek="???";
                break;
            case 5:
                myWeek="???";
                break;
            default:
                break;
        }
        return myWeek;
    }

    List<AddressString> address = new ArrayList<>();
    private void firebase_selectAddr(DatabaseReference db) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int x_sum=(int)snapshot.getChildrenCount();    //??????????????????

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //???????????????url???????????????upload???
                    LatLng reg = new LatLng((double) ds.child("LatLng").child("latitude").getValue(), (double) ds.child("LatLng").child("longitude").getValue());
                    AddressString address_data = ds.getValue(AddressString.class);
                    address_data = new AddressString(ds.getKey(), address_data.getAddressLine(), reg);  //?????????key???????????????????????????
                    address.add(address_data);
                }
                addrGroupAdapter=new AddrGroupAdapter(context,address);
                addrGroupAdapter.setOnItemClickListener(new AddrGroupAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        pushPlace.setText(address.get(position).getAddressLine());
                        latLng=new LatLng(address.get(position).getLatLng().latitude,address.get(position).getLatLng().longitude);
                    }
                });
                addrRecycler.setAdapter(addrGroupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    final List<String> tags=new ArrayList<>();
    //??????????????????
    private void firebase_select(DatabaseReference db) {
        final List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
        final List<Myinfo.PlusString> plus=new ArrayList<>();
        final List<Upload> uploadString=new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Myinfo.PlusString push_data=ds.getValue(Myinfo.PlusString.class);
                    Upload[] upload = {ds.getValue(Upload.class)};
                    Map<String,Object> item=new HashMap<String,Object>();
                    item.put("id",ds.getKey());
                    item.put("classification", push_data.getClassification());
                    item.put("tag",push_data.getTag());
                    item.put("title",push_data.getTitle());
                    item.put("author",push_data.getAuthor());
                    item.put("name",push_data.getName());
                    item.put("url", upload[0].getImageUrl());
                    //0911
                    item.put("user_name",push_data.getName());
                    item.put("pushplace",push_data.getAddr());
                    item.put("push_context",push_data.getPushContext());
                    items.add(item);
                    //0911
                    push_data=new Myinfo.PlusString(Integer.parseInt(ds.getKey()),push_data.getClassification(),push_data.getTag(),push_data.getTitle(),push_data.getAuthor(),push_data.getName(),
                            push_data.getAddr(),push_data.getPushContext());

                    if( push_data.getTag()!=null) {
                        if ((push_data.getTag().indexOf("#")) > -1) {
                            for (String s : push_data.getTag().trim().split("#")) { //trim()??????????????????
                                if (!(s.equals("")) && !(s.equals(" "))) {
                                    tags.add(s);
                                }
                            }
                        } else {
                            if (!(push_data.getTag().equals("")) && !(push_data.getTag().equals(" "))) {
                                tags.add(push_data.getTag().trim());
                            }
                        }
                    }


                    //???????????????url???????????????upload???
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
                    x_last = Integer.parseInt(ds.getKey());   //??????????????????key???

                }
                list = items;
                groupAdapter=new tagsGroupAdapter(context,tags);
                groupAdapter.setOnItemClickListener(new tagsGroupAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        count++;
                        System.out.println(groupAdapter.getStr()[position]);
                        String clickStr="#"+groupAdapter.getStr()[position];
                        if(count<=5){
                            text+=clickStr;
                            clickStr="";
                        }
                        pushLabel.setText(text);
                    }
                });
                tagsRecycler.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void firebase_selectTag(DatabaseReference db) {
        ArrayList<String> follower = new ArrayList<String>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot DS : snapshot.getChildren()) {
                    Map<String,Object> item=new HashMap<String,Object>();
                    String tagName = DS.getKey();
                    System.out.println(tagName);
                    long tagNumber = (long) DS.child("number of post").getValue();
                    follower.clear();
                    for (DataSnapshot ds : DS.child("follower").getChildren()){
                        follower.add(ds.getKey());
                    }
                    item.put("tagName", tagName);
                    item.put("tagNumber", tagNumber);
                    item.put("follower", follower);
                    Tag.add(item);
                }
                System.out.println("??????");
//                System.out.println(Tag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void openHome(){
        Intent intent=new Intent(this, Home.class);
        finish();
        startActivity(intent);
    }

    private void openAddress(){
        Intent intent=new Intent(this, PushAddress.class);
        startActivityForResult(intent, ACTIVITY_REPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ACTIVITY_REPORT) {
                latLng = data.getParcelableExtra("address_user_selected");
                geocoder = new Geocoder(this, Locale.TRADITIONAL_CHINESE);
                try {
                    List<Address> address;
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    pushPlace.setText(address.get(0).getAddressLine(0));
                    latLng=new LatLng(latLng.latitude,latLng.longitude);
                } catch (Exception e) {
                }
            }
        }
        switch (requestCode) {
            case 1000:  //ACTIVITY_RESULT
                System.out.println("TRUE0");
                //if(resultCode==RESULT_OK) {
                try {
                    System.out.println("TRUE");
                    data=this.getIntent();
                    Bundle bundle = data.getExtras();
                    String addr = bundle.getString("addr");
                    pushPlace.setText(addr);
                }catch(Exception e){
                    System.out.println("ERROR!!!!!!!!!!!!!!!!!!!!!!");

                }
                //}
                break;
            case 1:  //PICK_CONTACT_REQUEST
                try {
                    uri = data.getData();
                    photo.setImageURI(uri);
                }catch(Exception e){

                }
                break;

        }
    }

    //????????????????????????
    private String getFileExtension(Uri uri){
        ContentResolver contentResoler=getContentResolver();//??????????????????????????????:ContentResolver??????????????????MimeTypeMap???????????????
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(uri));
    }

    //????????????+?????????????????????????????????????????????  //0823
    private void uploadFile() {
        String imagename;
        StorageReference fileReference;

        if (uri != null) {
            imagename = System.currentTimeMillis() + "." + getFileExtension(uri);
            fileReference = mStorageRef.child(imagename);
            fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadPhoto=true;
                            String urll = uri.toString();
                            Upload upload = new Upload(fileReference.toString(), urll);
                            myRef2.child(String.valueOf(x_last)).child("imageName").setValue(upload.getName());
                            myRef2.child(String.valueOf(x_last)).child("imageUrl").setValue(upload.getImageUrl());
                            //??????????????????
                            Toast.makeText(Plus.this, "Got url Successfully", Toast.LENGTH_SHORT).show();
                            openHome();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            uploadPhoto=false;
                            Toast.makeText(Plus.this, "Got url unsuccessfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Plus.this, "Fail", Toast.LENGTH_SHORT).show();
                    uploadPhoto=false;
                }
            });
        } else {
            uploadPhoto=false;
            Toast.makeText(Plus.this, "???????????????", Toast.LENGTH_SHORT).show();
        }
    }
}
