package com.hit.maestro.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hit.maestro.Course;
import com.hit.maestro.MainActivity;
import com.hit.maestro.adapter.CourseAdapter;
import com.hit.maestro.proxy.DatabaseProxy;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.services.ChatService;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements RegisterFragment.OnCompletedFragmentListener, RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener {
    View view;
    final String REGISTER_TAG="1";
    final String REGISTER_OR_LOGIN_TAG="2";
    final String LOGIN_TAG = "3";
    RecyclerView recyclerView;
    CourseAdapter adapter;
    Button registerBtn;
    TextView helloTv;
    RegisterFragment registerFragment;
    RegisterOrLoginFragment registerOrLoginFragment;
    LoginFragment loginFragment;
    List<Course> courseList= new ArrayList<Course>();
    DatabaseProxy proxy;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Button test;
    User user;
    SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_fragment,container,false);

        sp = this.getActivity().getSharedPreferences("login_status", MODE_PRIVATE);
        user = User.getInstance();
        test=view.findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_chatFragment);
            }
        });
        helloTv = view.findViewById(R.id.hello_tv);
        drawerLayout=view.findViewById(R.id.drawer_Layout);
        navigationView=view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case R.id.item_sign_in:
                        loginFragment = new LoginFragment((RegisterFragment.OnCompletedFragmentListener)MainFragment.this);
                        loginFragment.show(getChildFragmentManager(),LOGIN_TAG);
                        break;
                    case R.id.item_sign_up:
                        registerFragment=new RegisterFragment((RegisterFragment.OnCompletedFragmentListener)MainFragment.this);
                        registerFragment.show(getChildFragmentManager(),REGISTER_TAG);
                        break;
                    case R.id.item_sign_out:
                        user.setUserData();
                        user.SignOut();
                        setNavigationViewSituation(false);
                        break;
                }

                return false;
            }
        });
        //boolean connected = sp.getBoolean("status",false);
        setNavigationViewSituation(user.isConnected()?true:false);
        proxy= DatabaseProxy.getInstance();
/*
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

        List<Course> courseList=new ArrayList<Course>();
        courseList.add(new Course("Electric Guitar","marty","android.resource://com.hit.maestro/drawable/electric_guitar","123",subjects));
        courseList.add(new Course("Acoustic Guitar","marty","android.resource://com.hit.maestro/drawable/acoustic_guitar","123",subjects));
        courseList.add(new Course("Bass Guitar","marty","android.resource://com.hit.maestro/drawable/bass","123",subjects));
        courseList.add(new Course("Piano","marty","android.resource://com.hit.maestro/drawable/piano","123",subjects));
        courseList.add(new Course("Drum","marty","android.resource://com.hit.maestro/drawable/drums","123",subjects));

        //proxy.setCourses(courseList, getContext());


*/

        adapter=new CourseAdapter(courseList);
        proxy.getCourses().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    courseList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Course course = dataSnapshot.getValue(Course.class);
                        courseList.add(course);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        adapter.setListener(new CourseAdapter.myCourseListener() {
            @Override
            public void onCourseClicked(int position, View view) {
                /*SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("status", user.isConnected());
                editor.commit();*/
                Bundle bundle=new Bundle();
                bundle.putSerializable("Course", courseList.get(position));
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
    }
    /*
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
    public void onCompleted() {
        setNavigationViewSituation(user.isConnected());
    }

    @Override
    public void onSignInFromRegisterFragment() {
        registerFragment.dismiss();
        loginFragment = new LoginFragment((RegisterFragment.OnCompletedFragmentListener)this);
        loginFragment.show(getChildFragmentManager(),LOGIN_TAG);
    }

    @Override
    public void onSignIn() {
        //Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(REGISTER_OR_LOGIN_TAG);
        //getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        registerOrLoginFragment.dismiss();
        loginFragment = new LoginFragment((RegisterFragment.OnCompletedFragmentListener)this);
        loginFragment.show(getChildFragmentManager(),LOGIN_TAG);
    }

    @Override
    public void onSignUp() {
        //Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(REGISTER_OR_LOGIN_TAG);
        //getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        registerOrLoginFragment.dismiss();
        registerFragment=new RegisterFragment((RegisterFragment.OnCompletedFragmentListener)this);
        registerFragment.show(getChildFragmentManager(),REGISTER_TAG);
        //setNavigationViewSituation(user.isConnected());
    }

    @Override
    public void onStart() {
        super.onStart();
        user.AddListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        user.RemoveListener();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setNavigationViewSituation(boolean signOutStatus){
        navigationView.getMenu().findItem(R.id.item_sign_in).setVisible(!signOutStatus);
        navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(!signOutStatus);
        navigationView.getMenu().findItem(R.id.item_sign_out).setVisible(signOutStatus);
        if(signOutStatus){
            Intent intent=new Intent(getContext() , ChatService.class);
            helloTv.setText("hello " + user.getEmail());
            getActivity().startService(intent);
        }
        else{
            helloTv.setText("Guest mode");
        }
    }
}
