package com.hit.maestro.proxy;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.Course;
import com.hit.maestro.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseProxy {
    private static DatabaseProxy databaseProxy=null;

    public FirebaseDatabase getDatabase() {
        return database;
    }

    private FirebaseDatabase database;
    private DatabaseReference courses;
    private List<Course> courseList;
    final String TAG= "USER_SETTINGS_IMPORT";
    public DatabaseReference getCourses() {
        return courses;
    }

    public void setCourses(DatabaseReference courses) {
        this.courses = courses;
    }


    private DatabaseProxy() {
        database=FirebaseDatabase.getInstance();
        courses=database.getReference().child("courses");
        courseList=new ArrayList<Course>();

    }
    public static DatabaseProxy getInstance(){
        if(databaseProxy==null){
            synchronized (DatabaseProxy.class){
                databaseProxy=new DatabaseProxy();
            }
        }
        return databaseProxy;
    }

    public void setUser(User user){ //Hash<String,Hash<String,List<ChatMessage>>,List<String>>
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUID()).child("courses");
        DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUID()).child("chats");
        ref.setValue(user.getCourses()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Log.e(TAG,"success export courses!");
                }
                else {
                    Log.e(TAG,task.getException().getMessage());
                }
            }
        });

        ref1.setValue(user.getChats()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Log.e(TAG,"success export chats!");
                }
                else {
                    Log.e(TAG,task.getException().getMessage());
                }
            }
        });
    }

   public List<String> getCourses(String UID){
        final List<String>[] courses = new List[]{new ArrayList<>()};
        database.getReference().child("users/"+UID+"/courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    courses[0] =(List<String>)snapshot.child(UID).getValue();
                   /* for (DataSnapshot dataSnapshot:snapshot.getChildren(UID)){
                        Course course= dataSnapshot.getValue(Course.class);
                        courseList.add(course);
                    }*/
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return courses[0];
    }
    /*
    public HashMap<String,List<ChatMessage>> getChats(String UID){
        final HashMap<String, List<ChatMessage>>[] chats = new HashMap[]{new HashMap<String, List<ChatMessage>>()};
        database.getReference().child("users/"+UID+"/chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                     chats[0] =(HashMap<String,List<ChatMessage>>)snapshot.getValue();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren(UID)){
                        Course course= dataSnapshot.getValue(Course.class);
                        courseList.add(course);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return chats[0];
    }*/

   /* public List<Course> getCourses(){

        courses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    courseList.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Course course= dataSnapshot.getValue(Course.class);
                        courseList.add(course);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return courseList;
    }



    public void setCourses(List<Course> courses, Context context){
        this.courses.setValue(courses).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context,"success!",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }*/
}
