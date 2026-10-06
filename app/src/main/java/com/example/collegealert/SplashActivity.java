package com.example.collegealert;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(
                    android.graphics.Color.parseColor(
                            "#1A73E8"));
        }


        new Handler().postDelayed(() -> {
            startActivity(new Intent(
                    SplashActivity.this,
                    MainActivity.class));
            finish();
        }, 2500);
    }
}