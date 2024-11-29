package com.example.schedify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Login extends AppCompatActivity {

    SessionManager sessionManager;

    TextView pageTitle;
    EditText input_username;
    EditText input_password;
    Button btn_login;
    Button btn_signup;
    boolean isSignupClicked = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        sessionManager = new SessionManager(getApplicationContext());

        pageTitle = findViewById(R.id.login_page_title);
        input_username = (EditText) findViewById(R.id.edt_username);
        input_password = (EditText) findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signup = findViewById(R.id.btn_signup);
    }

    public void login(View view)
    {
        String[] savedPrefs = sessionManager.retrieveSaveCredential();
        String savedUsername = savedPrefs[0];
        String savedPassword = savedPrefs[1];
        String username = input_username.getText().toString();
        String password = input_password.getText().toString();

        if(username.equals(savedUsername)&& password.equals(savedPassword))
        {
            Intent intent = new Intent(getApplicationContext(), HomeFragment.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "Username or Password Did Not Matched!", Toast.LENGTH_SHORT).show();
    }

    public void openSignup(View view)
    {
        if (!isSignupClicked) {
            isSignupClicked = true;
            updatePageComponent("Register", isSignupClicked);
        } else {
            isSignupClicked = false;
            updatePageComponent("Login", isSignupClicked);

            String username = input_username.getText().toString();
            String password = input_password.getText().toString();
            sessionManager.createSession(username, password);

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    private void updatePageComponent(String newTitle, boolean isSignupClicked) {
        pageTitle.setText(newTitle);
        input_username.setText("");
        input_password.setText("");
        btn_login.setVisibility(isSignupClicked ? View.GONE : View.VISIBLE);
    }
}