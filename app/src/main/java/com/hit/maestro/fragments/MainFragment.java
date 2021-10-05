package com.hit.maestro.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.Course;
import com.hit.maestro.adapter.CourseAdapter;
import com.hit.maestro.proxy.DatabaseProxy;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.services.DatabaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements RegisterFragment.OnCompletedFragmentListener, RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener, SignOutDialog.OnSignOutListener {
    View view;
    final String REGISTER_TAG="1";
    final String REGISTER_OR_LOGIN_TAG="2";
    final String LOGIN_TAG = "3";
    final String PICTURE_TAG="6";
    final String SIGN_OUT_TAG="7";
    RecyclerView recyclerView;
    CourseAdapter adapter;
    Button registerBtn;
    BadgeDrawable badge;
    SignOutDialog signOutDialog;
    RegisterFragment registerFragment;
    RegisterOrLoginFragment registerOrLoginFragment;
    LoginFragment loginFragment;
    List<Course> courseList= new ArrayList<Course>();
    DatabaseProxy proxy;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    User user;
    SharedPreferences sp;
    ProgressBar progressBar;
    View headerLayout;
    TextView navTitle;
    LinearLayout editPic;
    ShapeableImageView navImage;
    AnimatorSet animatorSet;
    List<String>courseTitles;
    boolean isGuest;
    boolean createFirstTime = true;
    BroadcastReceiver newMessageReceived;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.main_fragment,container,false);
        sp = getActivity().getSharedPreferences("login_status", MODE_PRIVATE);
        progressBar=view.findViewById(R.id.progress_bar);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu);
        courseTitles=new ArrayList<>();
        //sp = this.getActivity().getSharedPreferences("login_status", MODE_PRIVATE);
        user = User.getInstance();
        registerBtn = view.findViewById(R.id.login_btn);

        drawerLayout=view.findViewById(R.id.drawer_Layout);
        navigationView=view.findViewById(R.id.navigation_view);
        headerLayout = navigationView.getHeaderView(0);
        navImage=headerLayout.findViewById(R.id.nav_user_image);
        navTitle=headerLayout.findViewById(R.id.nav_header_tv);
        editPic=headerLayout.findViewById(R.id.edit_pic);
        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureFragment pictureFragment=new PictureFragment(MainFragment.this, Uri.parse(DatabaseProxy.getInstance().getUserImageUri(User.getInstance().getUID())));
                pictureFragment.show(getChildFragmentManager(),PICTURE_TAG);
            }
        });
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
                        signOutDialog=new SignOutDialog((SignOutDialog.OnSignOutListener)MainFragment.this);
                        signOutDialog.show(getChildFragmentManager(),SIGN_OUT_TAG);
                        break;
                    case R.id.item_chat:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_chatFragment);
                        break;
                    case R.id.item_notif:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_notificationFragment);
                        break;
                }

                return false;
            }
        });
        //boolean connected = sp.getBoolean("status",false);
        setNavigationViewSituation(user.isConnected()?true:false);
        proxy= DatabaseProxy.getInstance();

        adapter=new CourseAdapter(courseList);
        proxy.getCourses().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    courseList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        courseTitles.add(dataSnapshot.getKey());
                        Course course = dataSnapshot.getValue(Course.class);
                        courseList.add(course);
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
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
                isGuest=!user.isConnected();
                String course =courseTitles.get(position);
                //String course = courseList.get(position).getName();
                if(!isGuest && User.getInstance().isUserRegisteredToCourse(course)){
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("Course", courseList.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_courseFragment,bundle);
                }
                else{
                    Bundle bundle=new Bundle();
                    if(isGuest){
                        bundle.putBoolean("guest",true);

                    }
                    else{
                        bundle.putBoolean("guest",false);
                    }
                    bundle.putString("Title",courseTitles.get(position));
                    bundle.putSerializable("Course", courseList.get(position));
                    Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_aboutCourseFragment,bundle);
                }
                //Bundle bundle=new Bundle();
                //bundle.putSerializable("Course", courseList.get(position));
                //Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_courseFragment,bundle);
            }
        });
        recyclerView =view.findViewById(R.id.course_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        recyclerView.setAdapter(adapter);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(registerBtn,"alpha",0.5f).setDuration(1000);
        anim1.setRepeatMode(ValueAnimator.REVERSE);
        anim1.setRepeatCount(Animation.INFINITE);
        animatorSet=new AnimatorSet();
        animatorSet.play(anim1);
        animatorSet.start();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerOrLoginFragment=new RegisterOrLoginFragment((RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener)MainFragment.this);
                registerOrLoginFragment.show(getChildFragmentManager(),REGISTER_OR_LOGIN_TAG);
            }
        });

        if(createFirstTime){
            RememberMe();
            createFirstTime = false;
        }

        IntentFilter filter=new IntentFilter("notification_received");
        newMessageReceived=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(User.getInstance().getNotifications().isEmpty()){
                    navigationView.getMenu().findItem(R.id.item_notif).setIcon(R.drawable.notifications);
                }
                else{
                    navigationView.getMenu().findItem(R.id.item_notif).setIcon(R.drawable.blue_notifications);
                }
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newMessageReceived,filter);

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
    public void onSignOut() {
        user.setUserData();
        user.SignOut();
        setNavigationViewSituation(false);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("remember", false);
        editor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        //user.AddListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        //user.RemoveListener();
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
        Uri pic;
        navigationView.getMenu().findItem(R.id.item_sign_in).setVisible(!signOutStatus);
        navigationView.getMenu().findItem(R.id.item_sign_up).setVisible(!signOutStatus);
        navigationView.getMenu().findItem(R.id.item_sign_out).setVisible(signOutStatus);
        navigationView.getMenu().findItem(R.id.item_chat).setVisible(signOutStatus);
        navigationView.getMenu().findItem(R.id.item_notif).setVisible(signOutStatus);
        if(signOutStatus){
            registerBtn.setVisibility(View.GONE);
            editPic.setVisibility(View.VISIBLE);
            Intent intent=new Intent(getContext() , DatabaseService.class);
            String title = getResources().getString(R.string.hello_coma) +" "+ user.getFullName();
            navTitle.setText(title);
            isGuest = false;
            pic=Uri.parse(DatabaseProxy.getInstance().getUserImageUri(User.getInstance().getUID()));
            getActivity().startService(intent);
            if(User.getInstance().getNotifications().isEmpty()){
                navigationView.getMenu().findItem(R.id.item_notif).setIcon(R.drawable.notifications);
            }
            else{
                navigationView.getMenu().findItem(R.id.item_notif).setIcon(R.drawable.blue_notifications);
            }
        }
        else{
            registerBtn.setVisibility(View.VISIBLE);
            editPic.setVisibility(View.INVISIBLE);

            navTitle.setText(getResources().getString(R.string.hello));
            isGuest=true;
            pic=Uri.parse("android.resource://com.hit.maestro/drawable/default_profile_picture");
        }
        Glide.with(this)
                .load(pic)
                .into(navImage);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAndLoadPic(Uri uri){
        Glide.with(this)
                .load(uri)
                .into(navImage);
    }

    private void RememberMe(){
        if (sp.getBoolean("remember", false)) {
            String email = sp.getString("email", "");
            String password = sp.getString("password", "");
            User.getInstance().getFirebaseAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //editor.putBoolean("status", true);
                        User.getInstance().setConnected(true);
                        User.getInstance().setFullName(User.getInstance().getFirebaseUser().getDisplayName());
                        User.getInstance().setUserName(email);
                        User.getInstance().setPassword(password);
                        User.getInstance().setFirebaseUser(User.getInstance().getFirebaseAuth().getCurrentUser());
                        User.getInstance().setUID(User.getInstance().getFirebaseUser().getUid());
                        //User.getInstance().setCourses(new ArrayList<>());
                        User.getInstance().setChats(new HashMap<String, List<ChatMessage>>(0));
                        User.getInstance().setNotifications(new ArrayList<String>());
                        User.getInstance().getMessaging().unsubscribeFromTopic(User.getInstance().getUID());
                        User.getInstance().getMessaging().subscribeToTopic(User.getInstance().getUID());
                        User.getInstance().setOrderMessages(new ArrayList<String>());
                        User.getInstance().getUserData();
                        setNavigationViewSituation(true);
                    } else {
                        User.getInstance().setConnected(false);
                    }
                }
            });
        }
    }
}
