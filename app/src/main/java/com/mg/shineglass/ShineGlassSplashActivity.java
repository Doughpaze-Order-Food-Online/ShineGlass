package com.mg.shineglass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class ShineGlassSplashActivity extends AppCompatActivity {

    private static int TIME_OUT = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(ShineGlassSplashActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);
    }
}