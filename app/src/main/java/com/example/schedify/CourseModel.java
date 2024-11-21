package com.example.schedify;

public class CourseModel {

    String course_code;
    String tv_location;
    int course_img;
    String start_time;
    String end_time;



    public CourseModel( int course_img, String course_code, String tv_location, String start_time, String end_time)
    {
        this.course_img = course_img;
        this.course_code = course_code;
        this.start_time = start_time;
        this.end_time = end_time;

    }



    public int getCourse_img(){
        return course_img;
    }



    public String getCourse_code(){
        return course_code;
    }

    public String getTv_location(){
        return tv_location;
    }


    public String getStart_time(){
        return start_time;
    }


    public String getEnd_time(){
        return end_time;
    }











}
