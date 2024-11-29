package com.example.sharedpreferencedemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schedify.HomeFragment;
import com.example.schedify.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                com.example.sharedpreferencedemo.SessionManager sessionManager = new com.example.sharedpreferencedemo.SessionManager(getApplicationContext());
                boolean b = sessionManager.checkSession();

                if (b==true) {
                    Intent intent = new Intent(SplashScreen.this, HomeFragment.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashScreen.this, Long.class);
                    startActivity(intent);
                }
            }
        }, 2000);
    }
}