package com.example.schedify.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.schedify.R;
import com.example.schedify.Util.SessionManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                boolean isLoginPrev = sessionManager.checkSession();
                Intent intent;

                if (isLoginPrev)
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                else
                    intent = new Intent(SplashScreen.this, LoginActivity.class);

                startActivity(intent);

            }
        }, 1);
    }
}