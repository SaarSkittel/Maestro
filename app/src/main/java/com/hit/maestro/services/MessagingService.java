package com.hit.maestro.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    final String TAG="MessagingService";
    private FirebaseDatabase database;
    private DatabaseReference user;



    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e(TAG,"service is up");
        database= FirebaseDatabase.getInstance();
        user=database.getReference().child("users");
        user.child(s).setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    Log.e(TAG,"success!");
                }
                else {
                    Log.e(TAG,task.getException().getMessage());
                }
            }
        });

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        //sender==>UID==>client==>sender?
        if (remoteMessage.getData().size() > 0) {//from: /topics/A ==>/topics/UID, /topics/guitar
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Intent intent= new Intent("message_received");
            intent.putExtra("message",remoteMessage.getSenderId() + ":" + remoteMessage.getData().get("message"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
