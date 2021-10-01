package com.hit.maestro.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hit.maestro.Course;
import com.hit.maestro.R;
import com.hit.maestro.Reaction;
import com.hit.maestro.adapter.ReactionAdapter;

import java.util.ArrayList;
import java.util.List;

public class AboutCourseFragment extends Fragment {

    View view;
    TextView CourseNameTv,LecturerNameTv,DescriptionTv;
    ImageView CoursePhoto;
    Button Register;
    Button AddComment;
    Course receivedCourse;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.main_fragment,container,false);

        Toolbar toolbar = view.findViewById(R.id.about_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = view.findViewById(R.id.recycleView_aboutCourse);
        recyclerView.setHasFixedSize(true);

        List<Reaction> reactions = new ArrayList<>();
        reactions.add(new Reaction("Saaar ","https://www.washingtoninstitute.org/sites/default/files/2021-02/SaarGideon_0.jpg","asscasasad",5));
        reactions.add(new Reaction("Saaar ","https://www.washingtoninstitute.org/sites/default/files/2021-02/SaarGideon_0.jpg","asscasasad",5));
        reactions.add(new Reaction("Saaar ","https://www.washingtoninstitute.org/sites/default/files/2021-02/SaarGideon_0.jpg","asscasasad",5));
        reactions.add(new Reaction("Saaar ","https://www.washingtoninstitute.org/sites/default/files/2021-02/SaarGideon_0.jpg","asscasasad",5));

        ReactionAdapter reactionAdapter = new ReactionAdapter(reactions);
        recyclerView.setAdapter(reactionAdapter);

        
        CourseNameTv =   view.findViewById(R.id.course_name_tv);
        LecturerNameTv = view.findViewById(R.id.lecturer_name_tv);
        DescriptionTv =  view.findViewById(R.id.description_tv);
        CoursePhoto =    view.findViewById(R.id.course_iv);
        Register =       view.findViewById(R.id.registerBtn);
        AddComment=      view.findViewById(R.id.add_comment_btn);




        setText(CourseNameTv, receivedCourse.getName());
        setText(LecturerNameTv, receivedCourse.getLecturer());
        setText(DescriptionTv, receivedCourse.getDescription());
        Bitmap bitmap=ReactionAdapter.StringToBitMap(receivedCourse.getImage());
        CoursePhoto.setImageBitmap(bitmap);



        AddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open dialog
                //take a comment
                //save in firebase
            }
        });
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //getParentFragmentManager().popBackStack();
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}