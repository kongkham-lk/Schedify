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
import com.example.schedify.Models.TaskModel;
import com.example.schedify.Adaptors.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreateFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskModel> taskModelList;

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

        List<TaskModel> taskModelList = loadTaskList();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TaskAdapter taskAdapter = new TaskAdapter(taskModelList, requireActivity());
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
                taskModelList.add(new TaskModel(title, description, time, date, location));
                saveTaskList(taskModelList);
            } else if (((title != null && description != null && date != null) && index > -1)) {
                taskModelList.remove(index);
                taskModelList.add(index, new TaskModel(title, description, time, date, location ));
                saveTaskList(taskModelList);
            }
            if (delete > -1) {
                Log.println(Log.ASSERT, "yes", "Testing: " + delete);
                taskModelList.remove(delete);
                saveTaskList(taskModelList);
            }
        }

        return view;
    }


    private void saveTaskList(List<TaskModel> taskModelList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder sb = new StringBuilder();
        for (TaskModel taskModel : taskModelList) {
            sb.append(taskModel.getTitle()).append(",").append(taskModel.getDescription()).append(",").append(taskModel.getTime()).append(",").append(taskModel.getDate()).append(",").append(taskModel.getLocation()).append(",;");
        }

        editor.putString("taskList", sb.toString());
        editor.apply();
    }

    private List<TaskModel> loadTaskList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        String taskData = sharedPreferences.getString("taskList", "");

        List<TaskModel> taskModelList = new ArrayList<>();
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
                    taskModelList.add(new TaskModel(title, description, time, date, location));
                } else if (taskDetails.length == 4) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
                    String date = taskDetails[3];
                    taskModelList.add(new TaskModel(title, description, time, date, ""));
                }
            }
        }
        return taskModelList;
    }

}