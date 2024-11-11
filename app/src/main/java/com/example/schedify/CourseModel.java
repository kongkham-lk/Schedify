package com.example.schedify;

public class CourseModel {

    String course_code;
    String course_name;


    public CourseModel(String course_code, String course_name)
    {
        this.course_code = course_code;
        this.course_name = course_name;
    }



    public String getCourse_code(){
        return course_code;
    }

    public String getCourse_name(){
        return course_name;
    }





}
