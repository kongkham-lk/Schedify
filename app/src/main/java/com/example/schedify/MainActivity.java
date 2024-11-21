package com.example.schedify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONObject;

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
        String moodleURL = "https://moodle.tru.ca/my/";
        fetchcourseRegistrationAPI(courseRegURL);
        fetchcourseRegistrationAPI(moodleURL);
    }

    private void fetchcourseRegistrationAPI(String targetURL) {
        // Create and show the WebView login dialog
        webViewLoginDialog = new WebViewLoginDialog(MainActivity.this, targetURL, this);
        webViewLoginDialog.show();
    }

//    private void fetchMoodleAPI() {
//
//        try {
//            String moodleURL = "https://moodle.tru.ca/my/";
//
//            // Fetch the HTML content
//            String htmlContent = MoodleApiResponse.fetchHTML(moodleURL);
//
//            // Parse the HTML and extract data
//            JSONObject result = MoodleApiResponse.extractCourseDataFromMoodle(htmlContent);
//
//            // Print the JSON result
//            System.out.println(result.toString(4)); // Pretty-print JSON
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onLoginResult(boolean isSuccess) {
        if (isSuccess) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            cookie = webViewLoginDialog.getCookie();
            String targetURL = webViewLoginDialog.getTargetURL();

            if (targetURL.contains("registration"))
                retrievedCourseRegistrationAPI(); // Proceed fetching course schedule
            else if (targetURL.contains("moodle"))
                retrievedMoodleCourseAPI();
        } else {
            // Handle login failure
            Toast.makeText(this, "Syncing is Cancel!", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrievedCourseRegistrationAPI() {
        String courseRegURL = "https://reg-prod.ec.tru.ca/StudentRegistrationSsb/ssb/registrationHistory/reset?term=202510";

        CourseRegistrationResponse CourseAPIFetcher = new CourseRegistrationResponse();
        CourseAPIFetcher.setRequestMethod("GET");
        courseList = CourseAPIFetcher.retrievedCourseSchedule(courseRegURL, cookie);
    }

    private void retrievedMoodleCourseAPI() {
        String moodleURL = "https://moodle.tru.ca/my/";

        MoodleApiResponse moodleApiResponse = new MoodleApiResponse();
        moodleApiResponse.setRequestMethod("GET");
        courseList = moodleApiResponse.retrievedCourseDataFromMoodle(moodleURL, cookie);
    }
}