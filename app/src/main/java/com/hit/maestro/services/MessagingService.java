package com.hit.maestro.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;

public class MessagingService extends FirebaseMessagingService {
    final String TAG="MessagingService";
    private FirebaseDatabase database;
    private DatabaseReference user;

    NotificationManager manager;
    final int NOTIFICATION_ID=1;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e(TAG,"service is up");
       /* database= FirebaseDatabase.getInstance();
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
*/
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        //sender==>sender.chat.setmessage==>messageproxy==>sendmessage(uid)==>fcm==>Receiver.onmessagereceived==>setMessage==>pushnotif
        //sender==>UID==>client==>sender?
        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String channelID=null;
            manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT>=26){
                channelID="1";
                CharSequence channelName="notification channel";
                int importance=NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel notificationChannel=new NotificationChannel(channelID,channelName,importance);
                manager.createNotificationChannel(notificationChannel);

            }

            Notification.Builder builder=new Notification.Builder(getBaseContext(),channelID);
            builder.setSmallIcon(android.R.drawable.ic_dialog_email).setContentTitle("Maestro").setContentText("You Have New Messages");

            Notification notification= builder.build();
            
            manager.notify(NOTIFICATION_ID,notification);

            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
           /* User user=User.getInstance();
            ChatMessage chatMessage=new ChatMessage(remoteMessage.getData().get("message"),remoteMessage.getData().get("UID"),remoteMessage.getData().get("UID"),"android.resource://com.hit.maestro/drawable/acoustic_guitar");
            DatabaseReference reference=DatabaseProxy.getInstance().getDatabase().getReference().child("/users/"+user.getUID()+"/chats/").child(remoteMessage.getData().get("UID"));
            if (remoteMessage.getFrom().matches("/topics/"+user.getUID())){
                reference.push().setValue(chatMessage);
            }*/
            /*Intent intent= new Intent("message_received");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

        }
    }
}
