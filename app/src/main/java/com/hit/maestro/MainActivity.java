package com.hit.maestro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.hit.maestro.fragments.LoginFragment;
import com.hit.maestro.fragments.NotificationFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivityTAG";
    SharedPreferences sp;
    SharedPreferences spn;

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        setIntent(newIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spn=getSharedPreferences("notif",MODE_PRIVATE);
        SharedPreferences.Editor editor=spn.edit();
        boolean a=getIntent().getBooleanExtra("notification",false);
        editor.putBoolean("notif",getIntent().getBooleanExtra("notification",false));
        editor.commit();
        setContentView(R.layout.activity_main);
        Log.d(TAG, "create");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "resume");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final boolean fromNotification = extras.getBoolean("notification");
            if (fromNotification) {
                spn=getSharedPreferences("notif",MODE_PRIVATE);
                SharedPreferences.Editor editor=spn.edit();
                editor.putBoolean("notif",true);
                editor.commit();
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "pause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroy");

        if (loadRememberMe()) {
            setEmailAtStorage(User.getInstance().getEmail());
            setPasswordAtStorage(User.getInstance().getPassword());
            User.getInstance().setConnected(true);
        } else {

            setEmailAtStorage("");
            setPasswordAtStorage("");
            if(User.getInstance().isConnected())
                User.getInstance().SignOut(getBaseContext());
        }
    }

    private boolean loadRememberMe(){
        try {
            FileInputStream fileInputStream= openFileInput("remember");
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            boolean isRemember = (boolean)objectInputStream.readObject();
            objectInputStream.close();
            return isRemember;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setEmailAtStorage(String email){
        try {
            FileOutputStream fileOutputStream = openFileOutput("email", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(email);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPasswordAtStorage(String password){
        try {
            FileOutputStream fileOutputStream = openFileOutput("password", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(password);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFromNotificationStorage(boolean fromNotification){
        try {
            FileOutputStream fileOutputStream = openFileOutput("notification", MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(fromNotification);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean loadFromNotification(){
        try {
            FileInputStream fileInputStream= openFileInput("notification");
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            boolean fromNotification = (boolean)objectInputStream.readObject();
            objectInputStream.close();
            return fromNotification;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}