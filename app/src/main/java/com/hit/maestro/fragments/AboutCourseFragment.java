package com.hit.maestro.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hit.maestro.Course;
import com.hit.maestro.R;
import com.hit.maestro.ReadAndWriteStorage;
import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;
import com.hit.maestro.services.DatabaseService;

public class AboutCourseFragment extends Fragment implements RegisterFragment.OnCompletedFragmentListener, RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener {

    View view;
    TextView courseNameTv,lecturerNameTv,descriptionTv;
    ImageView CoursePhoto;
    Button register;
    Button addComment;
    Course receivedCourse;
    final String REGISTER_TAG="1";
    final String REGISTER_OR_LOGIN_TAG="2";
    final String LOGIN_TAG = "3";
    RegisterFragment registerFragment;
    RegisterOrLoginFragment registerOrLoginFragment;
    LoginFragment loginFragment;
    String courseTitle;
    boolean isGuest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_about_course,container,false);

        Toolbar toolbar = view.findViewById(R.id.about_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);


        
        courseNameTv =   view.findViewById(R.id.course_name_tv);
        lecturerNameTv = view.findViewById(R.id.lecturer_name_tv);
        descriptionTv =  view.findViewById(R.id.description_tv);
        CoursePhoto =    view.findViewById(R.id.course_iv);
        register =       view.findViewById(R.id.registerBtn);


        courseTitle=getArguments().getString("Title");
        receivedCourse = (Course) getArguments().getSerializable("Course");
        isGuest = getArguments().getBoolean("guest",true);

        setText(courseNameTv, receivedCourse.getName());
        setText(lecturerNameTv, receivedCourse.getLecturer());
        setText(descriptionTv, receivedCourse.getDescription());
        Glide.with(this).load(receivedCourse.getImage()).centerCrop().into(CoursePhoto);


        if(isGuest){
            register.setText(getResources().getString(R.string.login_or_register));
        }
        else{
            register.setText(getResources().getString(R.string.register_to_course));
        }
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add to my courses list
                //get full Permissions
                if(isGuest){
                    registerOrLoginFragment=new RegisterOrLoginFragment((RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener)AboutCourseFragment.this);
                    registerOrLoginFragment.show(getChildFragmentManager(),REGISTER_OR_LOGIN_TAG);
                }
                else{
                    User.getInstance().addCourseToUser(courseTitle);
                    continueToCoursePage();
                }
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

            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted(String name) {
        Intent intent=new Intent(getActivity() , DatabaseService.class);
        getActivity().startService(intent);
        isGuest = !User.getInstance().isConnected();

        if(!isGuest && User.getInstance().isUserRegisteredToCourse(courseTitle)){
            continueToCoursePage();
        }
        else {
            register.setText(getResources().getString(R.string.register_to_course));
        }
    }

    @Override
    public void onSignInFromRegisterFragment() {
        registerFragment.dismiss();
        loginFragment = new LoginFragment((RegisterFragment.OnCompletedFragmentListener)this);
        loginFragment.show(getChildFragmentManager(),LOGIN_TAG);
    }

    @Override
    public void onSignIn() {
        registerOrLoginFragment.dismiss();
        loginFragment = new LoginFragment((RegisterFragment.OnCompletedFragmentListener)this);
        loginFragment.show(getChildFragmentManager(),LOGIN_TAG);
    }

    @Override
    public void onSignUp() {
        registerOrLoginFragment.dismiss();
        registerFragment=new RegisterFragment((RegisterFragment.OnCompletedFragmentListener)this);
        registerFragment.show(getChildFragmentManager(),REGISTER_TAG);
    }

    private void continueToCoursePage(){
        Bundle bundle=new Bundle();
        bundle.putSerializable("Course", receivedCourse);
        Navigation.findNavController(view).navigate(R.id.action_aboutCourseFragment_to_courseFragment,bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isGuest) {
            if (User.getInstance().isUserRegisteredToCourse(courseTitle)) {
                getActivity().onBackPressed();
            }
        }
        if(ReadAndWriteStorage.loadFromNotification(getActivity())){
            getActivity().onBackPressed();
        }
    }
}