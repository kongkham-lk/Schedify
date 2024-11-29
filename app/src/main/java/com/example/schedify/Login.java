package com.example.sharedpreferencedemo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schedify.HomeFragment;
import com.example.schedify.R;

public class Login extends AppCompatActivity {

    com.example.sharedpreferencedemo.SessionManager sessionManager;

    EditText log_email;
    EditText log_password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        sessionManager = new com.example.sharedpreferencedemo.SessionManager(getApplicationContext());

        log_email = (EditText) findViewById(R.id.log_email);
        log_password = (EditText) findViewById(R.id.log_password);

    }

    public void login(View view)
    {
        String email1 = log_email.getText().toString();
        String pass1 = log_password.getText().toString();

        if(email1.equals("bihaansapkota01@gmail.com")&& pass1.equals("1234"))
        {
            sessionManager.createSession("bihaan", "bihaansapkota01@gmail.com", "1234");

            Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
            startActivity(intent);
        }

        else {
            Toast.makeText(this, "Email and password did not match", Toast.LENGTH_SHORT).show();
        }

    }



    public void openSignup(View view)
    {
        Intent intent = new Intent(getApplicationContext(), com.example.sharedpreferencedemo.Login.class);
        startActivity(intent);
    }
}