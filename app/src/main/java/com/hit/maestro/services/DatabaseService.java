package com.hit.maestro.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.hit.maestro.ChatMessage;

import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService extends Service {

    private DatabaseProxy databaseProxy= DatabaseProxy.getInstance();
    private User user=User.getInstance();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        databaseProxy.getDatabase().getReference().child("users/"+user.getUID()).child("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    List<String>userCourseList=new ArrayList<>();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        userCourseList.add(dataSnapshot.getValue(String.class));
                    }
                    user.setCourses(userCourseList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseProxy.getDatabase().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                  List<HashMap<String,String>>userList=new ArrayList<>();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        HashMap<String,String> temp=new HashMap<>();
                        temp.put("name",dataSnapshot.child("name").getValue(String.class));
                        temp.put("image",dataSnapshot.child("image").getValue(String.class));
                        temp.put("UID",dataSnapshot.getKey());
                        userList.add(temp);
                    }
                    DatabaseProxy.getInstance().setAllUsers(userList);
                }
                Intent intent = new Intent("user_update");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        databaseProxy.getDatabase().getReference().child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    HashMap<String,List<ChatMessage>> lessonChats=new HashMap<>();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        List<HashMap<String, Object>> messageListHash = (List<HashMap<String, Object>>) dataSnapshot.getValue();
                        List<ChatMessage> messageList = new ArrayList<ChatMessage>();
                        for (int i = 0; i < messageListHash.size(); i++) {
                            ChatMessage message = new ChatMessage(messageListHash.get(i));
                            if (!lessonChats.containsKey(dataSnapshot.getKey())) {
                                lessonChats.put(dataSnapshot.getKey(), new ArrayList<ChatMessage>());
                            }
                            lessonChats.get(dataSnapshot.getKey()).add(message);
                        }
                    }
                    databaseProxy.setLessonChats(lessonChats);
                    Intent intent = new Intent("lesson_message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        databaseProxy.getDatabase().getReference().child("users/"+user.getUID()+"/chats").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()) {

                    List<HashMap<String, Object>> messageListHash=(List<HashMap<String, Object>>) snapshot.getValue();
                    List<ChatMessage> messageList= new ArrayList<ChatMessage>();
                    Log.d("KEY",snapshot.getKey());
                    for(int i =0; i<messageListHash.size() ;i++){
                        ChatMessage message=new ChatMessage(messageListHash.get(i));
                        if(user.getChatById(snapshot.getKey())==null){
                            user.getChats().put(snapshot.getKey(),new ArrayList<ChatMessage>());
                            user.getOrderMessages().add(snapshot.getKey());
                        }
                        user.getChatById(snapshot.getKey()).add(message);
                    }

                    Intent intent = new Intent("message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()) {
                    Log.d("KEY",snapshot.getKey());
                    List<HashMap<String, Object>> messageListHash=(List<HashMap<String, Object>>) snapshot.getValue();
                    ChatMessage message=new ChatMessage(messageListHash.get(messageListHash.size()-1));
                    user.getChatById(snapshot.getKey()).add(message);

                    for(int i=0;i<user.getOrderMessages().size();++i){
                        if(user.getOrderMessages().get(i).matches(snapshot.getKey())){
                            user.getOrderMessages().remove(i);
                            user.getOrderMessages().add(snapshot.getKey());
                        }
                    }
                    Intent intent = new Intent("message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseProxy.getDatabase().getReference().child("users/"+user.getUID()+"/notifications").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                for (int i=0;i<user.getNotifications().size();++i){
                    if(snapshot.getValue(String.class).matches(user.getNotifications().get(i))){
                        user.getNotifications().remove(i);
                    }
                }
                user.getNotifications().add(snapshot.getValue(String.class));
                databaseProxy.setUserNotifications(new ArrayList<String>());
                Intent intent = new Intent("notification_received");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SERVICE_MESSAGE","service is dead.");
    }
}
