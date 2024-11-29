package com.example.schedify;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

public class MainActivity extends AppCompatActivity implements WebViewLoginDialog.LoginCallback, HomeFragment.OnSyncButtonClickListener {

    private List<CourseModel> courseList;
    WebViewLoginDialog webViewLoginDialog;
    String cookie;

    public static TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabAdapter adapter;
    Button syncBtn;
    String targetURL; // flag for determine if show toast msg when fail to syn or just click back after view course page

    public void onSyncButtonClicked() {
        // Functionality to handle sync button click
        loadCourse();
        // Your function logic here
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        tabLayout = findViewById(R.id.tabLayout_bar);
        viewPager2 = findViewById(R.id.viewPager);

        adapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());  // Your TabAdapter for managing fragments
        adapter.setContext(this);
        viewPager2.setAdapter(adapter);

        // Set up TabLayout with ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Home");
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:
                    tab.setText("Create");
                    tab.setIcon(R.drawable.add);
                    break;
                /*case 2:
                    tab.setText("Profile");
                    tab.setIcon(R.drawable.user);
                    break;*/
                case 2:
                    tab.setText("Settings");
                    tab.setIcon(R.drawable.setting);
                    break;
            }
        }).attach();

        String frag = getIntent().getStringExtra("Fragment");
        if ("CreateFragment".equals(frag)) {
            viewPager2.setCurrentItem(1, false);
            String title = getIntent().getStringExtra("title");
            if (title != null) {
                String description = getIntent().getStringExtra("description");
                String date = getIntent().getStringExtra("date");
                String time = getIntent().getStringExtra("time");
                String location = getIntent().getStringExtra("location");

                Bundle args = new Bundle();
                args.putString("title", title);
                args.putString("description", description);
                args.putString("date", date);
                args.putString("time", time);
                args.putString("location", location);

                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(args);

                CreateFragment fragment = new CreateFragment();
                fragment.setArguments(args);

            }
        } else if ("HomeFragment".equals(frag)) {
            viewPager2.setCurrentItem(0, false);
            String title = getIntent().getStringExtra("title");
            if (title != null) {
                String description = getIntent().getStringExtra("description");
                String date = getIntent().getStringExtra("date");
                String time = getIntent().getStringExtra("time");
                String location = getIntent().getStringExtra("location");

                Bundle args = new Bundle();
                args.putString("title", title);
                args.putString("description", description);
                args.putString("date", date);
                args.putString("time", time);
                args.putString("location", location);

                CreateFragment fragment = new CreateFragment();
                fragment.setArguments(args);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(args);

            }
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

       //loadCourse();
    }

    public void loadCourse() {
        String initialCourseRegURL = "https://reg-prod.ec.tru.ca/StudentRegistrationSsb/ssb/registrationHistory/registrationHistory";
        String finalCourseRegURL = "https://reg-prod.ec.tru.ca/StudentRegistrationSsb/ssb/registrationHistory/reset?term=202510";
        String moodleURL = "https://moodle.tru.ca/my/";
        fetchcourseRegistrationAPI(initialCourseRegURL, finalCourseRegURL);
//        fetchcourseRegistrationAPI(moodleURL);
    }

    public void fetchcourseRegistrationAPI(String targetURL) {
        fetchcourseRegistrationAPI(targetURL, targetURL);
    }

    private void fetchcourseRegistrationAPI(String initialURL, String targetURL) {
        // Create and show the WebView login dialog
        this.targetURL = targetURL;
        webViewLoginDialog = new WebViewLoginDialog(MainActivity.this, initialURL, targetURL, this);
        webViewLoginDialog.show();
    }

    @Override
    public void onLoginResult(boolean isSuccess) {
        if (!targetURL.contains("course/view")) {
            if (isSuccess) {
                Toast.makeText(this, "Successfully Sync!", Toast.LENGTH_SHORT).show();
                String targetURL = webViewLoginDialog.getInitialURL();

                if (targetURL.contains("registration")) {
                    retrievedCourseRegistrationAPI(); // Proceed fetching course schedule
                    passCourseListToHomeFragment();
                } else if (targetURL.contains("moodle")) {
                    retrievedMoodleCourseAPI();
                }
            } else {
                // Handle login failure
                Toast.makeText(this, "Cancel Sync!", Toast.LENGTH_SHORT).show();
            }
        }
        WebViewLoginDialog.isOpen = false;
    }

    private void retrievedCourseRegistrationAPI() {
        CourseRegistrationResponse CourseAPIFetcher = new CourseRegistrationResponse(this);
        courseList = CourseAPIFetcher.retrievedCourseSchedule();
    }

    private void retrievedMoodleCourseAPI() {
        String moodleURL = "https://moodle.tru.ca/my/";

        MoodleApiResponse moodleApiResponse = new MoodleApiResponse(this);
        courseList = moodleApiResponse.retrievedCourseDataFromMoodle();
//        Log.d("Courses", courseList.get(1).getTitle());
    }

    private void passCourseListToHomeFragment() {
        if (courseList != null && !courseList.isEmpty()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag("f" + 0); // ViewPager2 uses "f" + position for fragment tags

            if (homeFragment != null) {
                homeFragment.filterOutCourseList(courseList);
                Log.e("MainActivity", "COURSE LIST FOUND");
            } else {
                Log.e("MainActivity", "HomeFragment is not found");
            }
        }
    }

}
