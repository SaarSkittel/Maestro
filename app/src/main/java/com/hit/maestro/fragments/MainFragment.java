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
import com.hit.maestro.Lesson;
import com.hit.maestro.R;
import com.hit.maestro.Subject;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements RegisterFragment.OnRegisterFragmentListener, RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener {
    View view;
    final String REGISTER_TAG="1";
    final String REGISTER_OR_LOGIN_TAG="2";
    final String LOGIN_TAG = "3";
    RecyclerView recyclerView;
    CourseAdapter adapter;
    Button registerBtn;
    RegisterFragment registerFragment;
    RegisterOrLoginFragment registerOrLoginFragment;
    LoginFragment loginFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_fragment,container,false);

        List<Lesson> lessons =new ArrayList<Lesson>();
        lessons.add(new Lesson("lesson1","123",""));
        lessons.add(new Lesson("lesson2","123",""));
        lessons.add(new Lesson("lesson3","123",""));
        lessons.add(new Lesson("lesson4","123",""));

        List<Subject>subjects= new ArrayList<Subject>();
        subjects.add(new Subject("subject1",lessons));
        subjects.add(new Subject("subject2",lessons));
        subjects.add(new Subject("subject3",lessons));
        subjects.add(new Subject("subject4",lessons));

        List<Course> courseList= new ArrayList<Course>();
        courseList.add(new Course("Electric Guitar","marty","android.resource://com.hit.maestro/drawable/electric_guitar","123",subjects));
        courseList.add(new Course("Acoustic Guitar","marty","android.resource://com.hit.maestro/drawable/acoustic_guitar","123",subjects));
        courseList.add(new Course("Bass Guitar","marty","android.resource://com.hit.maestro/drawable/bass","123",subjects));
        courseList.add(new Course("Piano","marty","android.resource://com.hit.maestro/drawable/piano","123",subjects));
        courseList.add(new Course("Drum","marty","android.resource://com.hit.maestro/drawable/drums","123",subjects));

        adapter=new CourseAdapter(courseList);
        adapter.setListener(new CourseAdapter.myCourseListener() {
            @Override
            public void onCourseClicked(int position, View view) {
                Bundle bundle=new Bundle();
                bundle.putSerializable("Course",courseList.get(position));
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_courseFragment,bundle);
            }
        });
        recyclerView =view.findViewById(R.id.course_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.setAdapter(adapter);

        registerBtn = view.findViewById(R.id.login_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerOrLoginFragment=new RegisterOrLoginFragment((RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener)MainFragment.this);
                registerOrLoginFragment.show(getChildFragmentManager(),REGISTER_OR_LOGIN_TAG);
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
        registerFragment.dismiss();
    }

    @Override
    public void onSignInFromRegisterFragment() {
        registerFragment.dismiss();
        loginFragment = new LoginFragment();
        loginFragment.show(getChildFragmentManager(),LOGIN_TAG);
    }

    @Override
    public void onSignIn() {
        //Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(REGISTER_OR_LOGIN_TAG);
        //getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        registerOrLoginFragment.dismiss();
        loginFragment = new LoginFragment();
        loginFragment.show(getChildFragmentManager(),LOGIN_TAG);
    }

    @Override
    public void onSignUp() {
        //Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(REGISTER_OR_LOGIN_TAG);
        //getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        registerOrLoginFragment.dismiss();
        registerFragment=new RegisterFragment((RegisterFragment.OnRegisterFragmentListener)this);
        registerFragment.show(getChildFragmentManager(),REGISTER_TAG);
    }
}
