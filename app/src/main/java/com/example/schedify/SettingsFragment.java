package com.example.schedify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private RecyclerView recycler_view_settings;
    private SettingsAdapter settingsAdapter;
    private ArrayList<Settings> settings;
    com.example.sharedpreferencedemo.SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sessionManager = new com.example.sharedpreferencedemo.SessionManager(requireContext());
        recycler_view_settings = view.findViewById(R.id.recycler_view_create);
        settings = new ArrayList<>();
        settings.add(new Settings("Log out"));

        settingsAdapter = new SettingsAdapter(settings);
        recycler_view_settings.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_view_settings.setAdapter(settingsAdapter);

        return view;
    }

    public void signOut(View view) {
        sessionManager.signOut();
    }
}
