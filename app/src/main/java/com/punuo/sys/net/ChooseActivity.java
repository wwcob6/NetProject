package com.punuo.sys.net;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import top.androidman.SuperButton;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        SuperButton chooseMain = (SuperButton) findViewById(R.id.main_button);
        SuperButton chooseMap = (SuperButton) findViewById(R.id.map_button);
        chooseMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        chooseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChooseActivity.this,StationActivity.class);
                startActivity(intent);
            }
        });
    }
}