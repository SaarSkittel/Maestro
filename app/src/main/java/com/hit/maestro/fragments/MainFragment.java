package com.hit.maestro.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hit.maestro.Course;
import com.hit.maestro.R;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements RegisterFragment.OnRegisterFragmentListener {
    View view;
    final String REGISTER_TAG="1";
    RecyclerView recyclerView;
    CourseAdapter adapter;
    Button registerBtn;
    RegisterFragment registerFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_fragment,container,false);

        List<Course> courseList= new ArrayList<Course>();
        courseList.add(new Course("Electric Guitar","marty","android.resource://com.hit.maestro/drawable/electric_guitar","123"));
        courseList.add(new Course("Acoustic Guitar","marty","android.resource://com.hit.maestro/drawable/acoustic_guitar","123"));
        courseList.add(new Course("Bass Guitar","marty","android.resource://com.hit.maestro/drawable/bass","123"));
        courseList.add(new Course("Piano","marty","android.resource://com.hit.maestro/drawable/piano","123"));
        courseList.add(new Course("Drum","marty","android.resource://com.hit.maestro/drawable/drums","123"));
        registerFragment=new RegisterFragment((RegisterFragment.OnRegisterFragmentListener)this);

        adapter=new CourseAdapter(courseList);
        recyclerView =view.findViewById(R.id.course_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.setAdapter(adapter);

        registerBtn = view.findViewById(R.id.login_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerFragment.show(getChildFragmentManager(),REGISTER_TAG);


            }
        });

        return view;
    }/*
    @Override
    public void onSignInFromRegisterFragment(){
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(REGISTER_TAG);//ליצור תג במיין
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        //להעלות את הפרגמנט של ההתחברות
    }

    @Override
    public void onRegister(String fullname,String username,String email, String password){
        //save to DB
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(REGISTER_TAG);//ליצור תג במיין
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }
*/

    @Override
    public void onRegister(String fullname, String username, String email, String password) {

    }

    @Override
    public void onSignInFromRegisterFragment() {

    }
}