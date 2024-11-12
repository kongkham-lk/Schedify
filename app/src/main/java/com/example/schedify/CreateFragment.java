
package com.example.schedify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
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

        taskList = loadTaskList();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new taskAdapter(taskList, requireActivity());
        recyclerView.setAdapter(taskAdapter);

        if (getActivity() != null && getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");

            if (title != null && description != null && date != null) {
                taskList.add(new Task(title, description, date + "\n" + time));
                saveTaskList(taskList);
            }
        }

        return view;
    }


    private void saveTaskList(List<Task> taskList) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TaskData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder sb = new StringBuilder();
        for (Task task : taskList) {
            sb.append(task.getTitle()).append(",").append(task.getDescription()).append(task.getTime()).append(";");
        }

        editor.putString("taskList", sb.toString());
        editor.apply();
    }

    private List<Task> loadTaskList() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TaskData", Context.MODE_PRIVATE);
        String taskData = sharedPreferences.getString("taskList", "");  // Empty string if no data

        List<Task> taskList = new ArrayList<>();
        if (!taskData.isEmpty()) {
            String[] tasks = taskData.split(";");
            for (String task : tasks) {
                String[] taskDetails = task.split(",");
                if (taskDetails.length == 3) {
                        String title = taskDetails[0];
                        String description = taskDetails[1];
                        String time = taskDetails[2];
                        taskList.add(new Task(title, description, time));

                }
            }
        }
        return taskList;
    }

}