package com.hit.maestro.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.ChatRoom;
import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;

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

        final HashMap<String, HashMap<String, HashMap<String, Object>>>[] chats = new HashMap[]{new HashMap<String,  HashMap<String,Object>>()};
        databaseProxy.getDatabase().getReference().child("users/"+user.getUID()+"/chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    chats[0] =(HashMap<String, HashMap<String, HashMap<String, Object>>>)snapshot.getValue();
                    user.setChats(chats[0]);
                    Intent intent= new Intent("message_received");
                    LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
                }

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
}
