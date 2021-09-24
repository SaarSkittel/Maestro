package com.hit.maestro.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
import com.hit.maestro.ChatRoom;
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

        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String channelId="channel_id";
        String channelName="Music Channel";

        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,channelId);
        builder.setSmallIcon(android.R.drawable.star_on).setContentText("Maestro").setContentTitle("start");

        startForeground(1,builder.build());
        final ChatRoom[] chats = new ChatRoom[1];
       // final HashMap<String, HashMap<String, HashMap<String, Object>>>[] chats = new HashMap[]{new HashMap<String,  HashMap<String,Object>>()};
        databaseProxy.getDatabase().getReference().child("users/"+user.getUID()+"/chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                  /*  chats[0] =(ChatRoom) snapshot.getValue();
                    user.setChats(chats[0].getMessages());
                    Intent intent= new Intent("message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);*/
                    GenericTypeIndicator<HashMap<String, HashMap<String,HashMap<String,Object>>>> genericTypeIndicator;
                    genericTypeIndicator = new GenericTypeIndicator<HashMap<String,HashMap<String,HashMap<String,Object>>>>(){};

                    HashMap<String, HashMap<String,HashMap<String,Object>>> hashMap = snapshot.getValue(genericTypeIndicator);
                    HashMap<String,List<ChatMessage>> chats= new HashMap<>();

                    for (HashMap.Entry<String, HashMap<String, HashMap<String, Object>>> entry : hashMap.entrySet()) {
                        HashMap<String, HashMap<String, Object>>temp = entry.getValue();
                        Collection<HashMap<String, Object>> values = temp.values();
                        List<HashMap<String, Object>> messageListHash=new ArrayList<HashMap<String, Object>>(values);
                        List<ChatMessage> messageList= new ArrayList<ChatMessage>();
                        for(int i =0; i<values.size() ;i++){
                            messageList.add(new ChatMessage(messageListHash.get(i)));
                        }
                        chats.put(entry.getKey(),messageList);
                    }
                    user.setChats(chats);
                    Intent intent= new Intent("message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        databaseProxy.getDatabase().getReference().child("users/"+user.getUID()+"/chats").addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage chatMessage=snapshot.getValue(ChatMessage.class);
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
        });*/

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
