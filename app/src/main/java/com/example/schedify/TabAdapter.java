package com.example.schedify;

import android.content.Context;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {
    Context mainContext;
    private final SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public TabAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    public void setContext(Context context) {
        mainContext = context;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new HomeFragment();
            case 1:
                return  new CreateFragment();
            /*case 2:
                return new ProfileFragment();*/
            case 2:
                return new SettingsFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public Fragment getFragmentAt(int position) {
        return registeredFragments.get(position);
    }
}
