package com.hit.maestro.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hit.maestro.Course;
import com.hit.maestro.R;

public class CourseCellFragment extends Fragment {
    View view;
    TextView name;
    ImageView image;

    public static CourseCellFragment newInstance(Course course){
        CourseCellFragment courseCellFragment =new CourseCellFragment();
        Bundle bundle= new Bundle();
        bundle.putString("name",course.getName());
        bundle.putString("image",course.getImage());
        courseCellFragment.setArguments(bundle);
        return courseCellFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.course_cell_fragment,container,false);
        name=view.findViewById(R.id.course_cell_name);
        name.setText(getArguments().getString("name"));
        image=view.findViewById(R.id.course_cell_image);
        Glide.with(this).load(Uri.parse(getArguments().getString("image"))).into(image);

        return view;
    }
}
