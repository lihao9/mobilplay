package com.lihao.mobilplay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.lihao.mobilplay.activity.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private boolean isEnterMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterMainActivity();
            }
        },2000);
    }

    public void enterMainActivity(){
        if (!isEnterMain){
            isEnterMain = true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        enterMainActivity();
        return super.onTouchEvent(event);
    }


}
