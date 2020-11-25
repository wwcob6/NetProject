package com.punuo.sys.net;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_LENGTH =2000 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this, ChooseActivity.class);
                startActivity(intent);
                //关闭splashActivity，避免按返回键返回此界面
                SplashActivity.this.finish();
            }
        }, SPLASH_LENGTH);
    }
}