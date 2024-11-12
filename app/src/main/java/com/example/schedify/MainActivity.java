package com.example.schedify;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.bottom_navigation_bar);
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
                case 2:
                    tab.setText("Profile");
                    tab.setIcon(R.drawable.user);
                    break;
                case 3:
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

                CreateFragment fragment = new CreateFragment();
                Bundle args = new Bundle();
                args.putString("title", title);
                args.putString("description", description);
                args.putString("date", date);
                fragment.setArguments(args);

            }
        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
