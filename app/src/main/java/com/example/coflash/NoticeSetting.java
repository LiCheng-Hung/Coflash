package com.example.coflash;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class NoticeSetting extends Activity {

    private ImageButton back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticesetting);
        back = findViewById(R.id.back);


        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void openMyinfo() {
        Intent intent = new Intent(this, Myinfo.class);
        startActivity(intent);
    }

}