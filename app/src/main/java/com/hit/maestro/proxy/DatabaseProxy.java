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
import java.util.Locale;

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
        if(Locale.getDefault().getDisplayLanguage().equals("עברית"))
            courses=database.getReference().child("courses_heb");
        else
            courses=database.getReference().child("courses");
        courseList=new ArrayList<Course>();
        userList= new ArrayList<HashMap<String,String>>();
        lessonChats=new HashMap<String, List<ChatMessage>>();
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
        ref.setValue(user.getCourses());
    }

    public void setOrderMessages(List<String>messages, String UID){
        DatabaseReference reference = database.getReference("users").child(UID).child("order");
        reference.setValue(messages);
    }

    public List<String> getOrderMessages(String UID){
        List<String>orderMessages=new ArrayList<>();
        database.getReference("users").child(UID).child("order").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot=task.getResult();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    orderMessages.add(dataSnapshot.getValue(String.class));
                }
            }
        });
        return orderMessages;
    }
    public void  setLocation(String location){
        DatabaseReference reference = database.getReference("/users/"+User.getInstance().getUID()).child("location");
        reference.setValue(location);
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

    public List<String> getUserNotificationsByUID(String UID){
        List<String> notifications=new ArrayList<>();
        database.getReference().child("users/"+UID+"/notifications").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                DataSnapshot snapshot=task.getResult();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    notifications.add(dataSnapshot.getValue(String.class));
                }
            }
        });
        return notifications;
    }

    public void setUserNotifications(List<String>notifications){
        DatabaseReference reference = database.getReference().child("users/"+User.getInstance().getUID()+"/notifications");
        reference.setValue(notifications);
    }

    public void setUserImageUri(Uri image, String UID){
        StorageReference fileReference = storageReference.child(UID);
        fileReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("setImage","Uploaded");
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference reference = database.getReference("/users/"+User.getInstance().getUID()).child("image");
                        reference.setValue(uri.toString());
                    }
                });
            }
        });
    }

    public void setUserImageInStorage(Uri image, String UID) {
        StorageReference fileReference = storageReference.child(UID);
        fileReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("setImage","Uploaded");
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

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return courses[0];
    }

    public List<ChatMessage> getLessonChatById(String to){
        if(lessonChats.containsKey(to))
            return lessonChats.get(to);
        else
            return new ArrayList<ChatMessage>();
    }

    public void setCourses(List<String> courses){
       DatabaseReference reference = database.getReference().child("users/"+User.getInstance().getUID()+"/courses");
        reference.setValue(courses);
    }
}
