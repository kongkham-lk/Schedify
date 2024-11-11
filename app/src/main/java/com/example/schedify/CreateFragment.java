package com.example.schedify;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CreateFragment extends Fragment {

    private RecyclerView recyclerView;
    private taskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        Button buttonToFragmentB = view.findViewById(R.id.buttonChangePage);
        buttonToFragmentB.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateTaskActivity.class);
            intent.putExtra("Fragment", "CreateTaskFragment");
            startActivity(intent);
        });

        recyclerView = view.findViewById(R.id.recycler_view);

        taskList = new ArrayList<>();


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new taskAdapter(taskList);
        recyclerView.setAdapter(taskAdapter);

        if (getActivity() != null && getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String date = intent.getStringExtra("date");

            if (title != null && description != null && date != null) {
                taskList.add(new Task(title, date));
            }
        }

        return view;
    }
}
