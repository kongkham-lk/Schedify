package com.example.schedify.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedify.Activities.CreateTaskActivity;
import com.example.schedify.R;
import com.example.schedify.Models.Task;
import com.example.schedify.Adaptors.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create, container, false);

        Button buttonToFragmentB = view.findViewById(R.id.btn_create);
        buttonToFragmentB.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateTaskActivity.class);
            intent.putExtra("Fragment", "CreateTaskFragment");
            startActivity(intent);
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_create);

        List<Task> taskList = loadTaskList();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TaskAdapter taskAdapter = new TaskAdapter(taskList, requireActivity());
        recyclerView.setAdapter(taskAdapter);

        if (getActivity() != null && getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String location = intent.getStringExtra("location");
            int index = intent.getIntExtra("index", -1);
            int delete = intent.getIntExtra("delete", -1);

            if ((title != null && description != null && date != null) && index == -1) {
                taskList.add(new Task(title, description, time, date, location));
                saveTaskList(taskList);
            } else if (((title != null && description != null && date != null) && index > -1)) {
                taskList.remove(index);
                taskList.add(index, new Task(title, description, time, date, location ));
                saveTaskList(taskList);
            }
            if (delete > -1) {
                Log.println(Log.ASSERT, "yes", "Testing: " + delete);
                taskList.remove(delete);
                saveTaskList(taskList);
            }
        }

        return view;
    }


    private void saveTaskList(List<Task> taskList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder sb = new StringBuilder();
        for (Task task : taskList) {
            sb.append(task.getTitle()).append(",").append(task.getDescription()).append(",").append(task.getTime()).append(",").append(task.getDate()).append(",").append(task.getLocation()).append(",;");
        }

        editor.putString("taskList", sb.toString());
        editor.apply();
    }

    private List<Task> loadTaskList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        String taskData = sharedPreferences.getString("taskList", "");

        List<Task> taskList = new ArrayList<>();
        if (!taskData.isEmpty()) {
            String[] tasks = taskData.split(";");
            for (String task : tasks) {
                String[] taskDetails = task.split(",");
//                Log.println(taskDetails.length, "uu", taskDetails.length + "");
//                for (int i = 0; i < taskDetails.length; i++) {
//                    Log.println(taskDetails.length, "Counting number of tasks", taskDetails[i] + "");
//                }
                if (taskDetails.length == 5) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
                    String date = taskDetails[3];
                    String location = taskDetails[4];
                    taskList.add(new Task(title, description, time, date, location));
                } else if (taskDetails.length == 4) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
                    String date = taskDetails[3];
                    taskList.add(new Task(title, description, time, date, ""));
                }
            }
        }
        return taskList;
    }

}