package com.hit.maestro.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hit.maestro.Course;
import com.hit.maestro.R;

public class AboutCourse extends Fragment {

    View view;
    TextView CourseNameTv,LecturerNameTv,DescriptionTv;
    ImageView CoursePhoto;
    Button Register;
    ViewPager pager;
    Course receivedCourse;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.main_fragment,container,false);

        CourseNameTv =   view.findViewById(R.id.course_name_tv);
        LecturerNameTv = view.findViewById(R.id.lecturer_name_tv);
        DescriptionTv =  view.findViewById(R.id.description_tv);
        CoursePhoto =    view.findViewById(R.id.course_iv);
        Register =       view.findViewById(R.id.registerBtn);

        pager = view.findViewById(R.id.pager);
        //build a comment structure fragment
        //fill up pager


        setText(CourseNameTv, receivedCourse.getName());
        setText(LecturerNameTv, receivedCourse.getLecturer());
        setText(DescriptionTv, receivedCourse.getDescription());


        //set image
        //set comments


        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add to my courses list
                //get full Permissions
            }
        });


        return view;

    }

    private void setText(TextView textView,String name){
        textView.setText(name);
    }
}