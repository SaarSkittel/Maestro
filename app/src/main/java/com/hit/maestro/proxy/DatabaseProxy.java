package com.hit.maestro.proxy;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.Course;
import com.hit.maestro.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseProxy {
    private static DatabaseProxy databaseProxy=null;


    public FirebaseDatabase getDatabase() {
        return database;
    }
    List<HashMap<String,String>> userList;
    private FirebaseDatabase database;
    private DatabaseReference courses;
    private List<Course> courseList;
    final String TAG= "USER_SETTINGS_IMPORT";
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("ImageFolder");
    public DatabaseReference getCourses() {
        return courses;
    }
    HashMap<String,List<ChatMessage>> lessonChats;

    public void setCourses(DatabaseReference courses) {
        this.courses = courses;
    }


    public HashMap<String, List<ChatMessage>> getLessonChats() {
        return lessonChats;
    }

    public void setLessonChats(HashMap<String, List<ChatMessage>> lessonChats) {
        this.lessonChats = lessonChats;
    }

    private DatabaseProxy() {
        database=FirebaseDatabase.getInstance();
        courses=database.getReference().child("courses");
        courseList=new ArrayList<Course>();
        userList= new ArrayList<HashMap<String,String>>();
        lessonChats=new HashMap<>();
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
    public void setUserName(String name){
        DatabaseReference reference = database.getReference("/users/"+User.getInstance().getUID()).child("name");
        reference.setValue(name);
    }
    public List<HashMap<String,String>> getAllUsers(){
        return userList;
    }
    public void setAllUsers(List<HashMap<String,String>> allUsers){
        this.userList=allUsers;
    }

    public String getUserName(String UID){
        String name=new String();
        for(int i=0;i<userList.size();++i){
            if(userList.get(i).get("UID").matches(UID)){
                name=userList.get(i).get("name");
                break;
            }
        }
        return name;
    }

    public void setUserImageUri(Uri image, String UID){
        //DatabaseReference reference = database.getReference("/users/"+User.getInstance().getUID()).child("images");
        //reference.setValue(image);

        StorageReference fileReference = storageReference.child(UID);
        fileReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("setImage","Uploaded");
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference reference = database.getReference("/users/"+User.getInstance().getUID()).child("images");
                        reference.setValue(uri.toString());
                    }
                });
            }
        });
    }

    public String getUserImageUri(String UID){
        String image=new String();
        for(int i=0;i<userList.size();++i){
            if(userList.get(i).get("UID").matches(UID)){
                image=userList.get(i).get("image");
                break;
            }
        }
        return image;
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
