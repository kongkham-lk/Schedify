package com.example.schedify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.List;

public class MainActivity extends AppCompatActivity implements WebViewLoginDialog.LoginCallback {

    private List<CourseModel> courseList;
    WebViewLoginDialog webViewLoginDialog;
    String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        String courseRegURL = "https://reg-prod.ec.tru.ca/StudentRegistrationSsb/ssb/registrationHistory/registrationHistory";

        // Create and show the WebView login dialog
        webViewLoginDialog = new WebViewLoginDialog(MainActivity.this, courseRegURL, this);
        webViewLoginDialog.show();
    }

    @Override
    public void onLoginResult(boolean isSuccess) {
        if (isSuccess) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            cookie = webViewLoginDialog.getCookie();
            String targetURL = webViewLoginDialog.getTargetURL();

            if (targetURL.contains("registration"))
                // Proceed fetching course schedule
                fetchCourseRegistrationAPI();
        } else {
            // Handle login failure
            Toast.makeText(this, "Syncing is Cancel!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCourseRegistrationAPI() {
        String url = "https://reg-prod.ec.tru.ca/StudentRegistrationSsb/ssb/registrationHistory/reset?term=202510";

        CourseRegistrationResponse CourseAPIFetcher = new CourseRegistrationResponse();
        CourseAPIFetcher.setRequestMethod("GET");
        courseList = CourseAPIFetcher.fetchCourseSchedule(url, cookie);
    }
}