package com.hit.maestro.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.MainActivity;
import com.hit.maestro.R;
import com.hit.maestro.ReadAndWriteStorage;
import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class MessagingService extends FirebaseMessagingService {
    final String TAG="MessagingService";
    private FirebaseDatabase database;
    private DatabaseReference user;
    private static final String NOTIFICATION="notification";
    NotificationManager manager;
    final int NOTIFICATION_ID=1;
    BroadcastReceiver newMessageReceived;
    static String UID ="";
    static boolean status= false;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter("notification_cancel");
        newMessageReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               UID=new String(intent.getStringExtra("UID"));
               status=intent.getBooleanExtra("isInChatFragment",false);
            }
        };
        LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(newMessageReceived, filter);
    }

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

        if((UID==null||!UID.matches(remoteMessage.getData().get("UID")))&&!status) {
            if (remoteMessage.getData().size() > 0) {
                //Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                String channelID = null;
                manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= 26) {
                    channelID = "1";
                    CharSequence channelName = "notification channel";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, importance);
                    manager.createNotificationChannel(notificationChannel);

                }

                Notification.Builder builder=new Notification.Builder(getBaseContext(),channelID);
                builder.setSmallIcon(android.R.drawable.ic_dialog_email).setContentTitle("Maestro").setContentText("You Have New Messages");
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                setFromNotificationStorage();


                intent.putExtra(NOTIFICATION, true);
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
                //NavDeepLinkBuilder navDeepLinkBuilder= new NavDeepLinkBuilder(getBaseContext());
                //PendingIntent pendingIntent=navDeepLinkBuilder.setComponentName(MainActivity.class).setGraph(R.navigation.nav).setDestination(R.id.action_mainFragment_to_notificationFragment).createPendingIntent();
                builder.setContentIntent(pendingIntent);
                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                manager.notify(NOTIFICATION_ID, notification);
            }
            //Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
           /* User user=User.getInstance();
            ChatMessage chatMessage=new ChatMessage(remoteMessage.getData().get("message"),remoteMessage.getData().get("UID"),remoteMessage.getData().get("UID"),"android.resource://com.hit.maestro/drawable/acoustic_guitar");
            DatabaseReference reference=DatabaseProxy.getInstance().getDatabase().getReference().child("/users/"+user.getUID()+"/chats/").child(remoteMessage.getData().get("UID"));
            if (remoteMessage.getFrom().matches("/topics/"+user.getUID())){
                reference.push().setValue(chatMessage);
            }*/
            /*Intent intent= new Intent("message_received");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
        }else{
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getBaseContext(), notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

        }
    }

    private void setFromNotificationStorage(){
        try {
            FileOutputStream fileOutputStream = openFileOutput("notification", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(true);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
