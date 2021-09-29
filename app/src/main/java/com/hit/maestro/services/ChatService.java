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

public class ChatService extends Service {
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
        /*
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String channelId="channel_id";
        String channelName="Music Channel";
/*
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,channelId);
        builder.setSmallIcon(android.R.drawable.star_on).setContentText("Maestro").setContentTitle("start");

        startForeground(1,builder.build());*/
        //final ChatRoom[] chats = new ChatRoom[1];
       // final HashMap<String, HashMap<String, HashMap<String, Object>>>[] chats = new HashMap[]{new HashMap<String,  HashMap<String,Object>>()};


        databaseProxy.getDatabase().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseProxy.getDatabase().getReference().child("users/"+user.getUID()+"/chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        List<HashMap<String, Object>> messageListHash = (List<HashMap<String, Object>>) dataSnapshot.getValue();
                        List<ChatMessage> messageList = new ArrayList<ChatMessage>();

                        for (int i = 0; i < messageListHash.size(); i++) {
                            ChatMessage message = new ChatMessage(messageListHash.get(i));
                            if (user.getChatById(dataSnapshot.getKey()) == null) {
                                user.getChats().put(dataSnapshot.getKey(), new ArrayList<ChatMessage>());
                            }
                            user.getChatById(dataSnapshot.getKey()).add(message);
                            //messageList.add(new ChatMessage(messageListHash.get(i)));
                        }
                    }
                    Intent intent = new Intent("message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseProxy.getDatabase().getReference().child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    HashMap<String,List<ChatMessage>> lessonChats=new HashMap<>();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                        List<HashMap<String, Object>> messageListHash = (List<HashMap<String, Object>>) dataSnapshot.getValue();
                        List<ChatMessage> messageList = new ArrayList<ChatMessage>();
                        for (int i = 0; i < messageListHash.size(); i++) {
                            ChatMessage message = new ChatMessage(messageListHash.get(i));
                            if (user.getChatById(dataSnapshot.getKey()) == null) {
                                user.getChats().put(dataSnapshot.getKey(), new ArrayList<ChatMessage>());
                            }
                            user.getChatById(dataSnapshot.getKey()).add(message);
                            //messageList.add(new ChatMessage(messageListHash.get(i)));
                        }
                        lessonChats.put(dataSnapshot.getKey(),messageList);
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

        databaseProxy.getDatabase().getReference().child("chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                List<HashMap<String, Object>> messageListHash=(List<HashMap<String, Object>>) snapshot.getValue();
                List<ChatMessage> messageList= new ArrayList<ChatMessage>();

                for(int i =0; i<messageListHash.size() ;i++){
                    ChatMessage message=new ChatMessage(messageListHash.get(i));
                    if(user.getChatById(snapshot.getKey())==null){
                        databaseProxy.getLessonChats().put(snapshot.getKey(),new ArrayList<ChatMessage>());
                    }
                    databaseProxy.getLessonChats().get(snapshot.getKey()).add(message);
                }

                Intent intent = new Intent("Lesson_message_received");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                List<HashMap<String, Object>> messageListHash=(List<HashMap<String, Object>>) snapshot.getValue();
                ChatMessage message=new ChatMessage(messageListHash.get(messageListHash.size()-1));
                databaseProxy.getLessonChats().get(snapshot.getKey()).add(message);
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
                        }
                        user.getChatById(snapshot.getKey()).add(message);
                        //messageList.add(new ChatMessage(messageListHash.get(i)));
                    }

                    Intent intent = new Intent("message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()) {
                    //HashMap<String, HashMap<String, Object>> item = (HashMap<String, HashMap<String, Object>>) snapshot.getValue();
                    //Collection<HashMap<String, Object>> values = item.values();
                    //List<HashMap<String, Object>> messageListHash=new ArrayList<HashMap<String, Object>>();
                    Log.d("KEY",snapshot.getKey());
                    //for(int i =0; i<values.size() ;i++){
                    List<HashMap<String, Object>> messageListHash=(List<HashMap<String, Object>>) snapshot.getValue();
                    ChatMessage message=new ChatMessage(messageListHash.get(messageListHash.size()-1));
                    user.getChatById(snapshot.getKey()).add(message);
                    //messageList.add(new ChatMessage(messageListHash.get(i)));
                   // }
                    
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

        /*final HashMap<String, List<ChatMessage>>[] lessonChats = new HashMap[]{new HashMap<String, List<ChatMessage>>()};
        databaseProxy.getDatabase().getReference().child("lesson_chat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    lessonChats[0] =snapshot.child(user.getUID()).getValue(ChatRoom.class).Chats;
                    user.setChats(lessonChats[0]);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SERVICE_MESSAGE","service is dead.");
    }
}
