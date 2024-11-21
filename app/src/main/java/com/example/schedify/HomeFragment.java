package com.example.schedify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView textView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private ListView list_view_home;
    private HomePageAdaptor homePageAdaptor;
    private ArrayList<CourseModel> courses;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        if (getActivity() != null && getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            String title = intent.getStringExtra("title");

            textView = view.findViewById(R.id.text_changer);
            textView.setText(title);
        }
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Initialize the ListView
        list_view_home = view.findViewById(R.id.list_view_home);

        // Initialize course data (Replace with your data source)
        courses = new ArrayList<>();
        courses.add(new CourseModel(R.drawable.gradient_color_1, "COMP 2160", "Mobile App Development", ""));
        courses.add(new CourseModel(R.drawable.gradient_color_2, "COMP 2210", "Programming Methods"));
        courses.add(new CourseModel(R.drawable.gradient_color_3, "COMP 2920", "Software Architecture"));
        courses.add(new CourseModel(R.drawable.gradient_color_4, "COMP 2230", "Data Structure"));




        // Set up the adapter
        HomePageAdaptor homePageAdaptor = new HomePageAdaptor(requireContext(), R.layout.home_items, courses);
        list_view_home.setAdapter(homePageAdaptor);

        return view;
    }
