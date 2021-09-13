package com.hit.maestro;

import android.app.PendingIntent;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseProxy {
    private static DatabaseProxy databaseProxy=null;
    private FirebaseDatabase database;
    private DatabaseReference courses;
    private List<Course> courseList;
    private MutableLiveData<List<Course>> courseListLiveData=new MutableLiveData<>();

    interface DatabaseListener{
        void onFinishedCourse();
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
    public MutableLiveData<List<Course>> getCourses(){

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
        courseListLiveData.setValue(courseList);
        return courseListLiveData;
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
    }
}
