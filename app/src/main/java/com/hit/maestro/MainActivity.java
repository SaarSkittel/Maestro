package com.hit.maestro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.hit.maestro.fragments.LoginFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivityTAG";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "create");

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Intent intent = new Intent("app_status");
        intent.putExtra("status",true);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "pause");

        /*Log.d(TAG,"pause");
        Intent intent = new Intent("app_status");
        intent.putExtra("status",false);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        SharedPreferences.Editor editor = sp.edit();
        if (sp.getBoolean("remember", false)) {
            editor.putString("email", User.getInstance().getEmail());
            editor.putString("password", User.getInstance().getPassword());
            User.getInstance().setConnected(true);
        } else {
            //editor.putBoolean("status", false);
            User.getInstance().setConnected(false);
        }
        editor.commit();*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        sp = getSharedPreferences("login_status", MODE_PRIVATE);
        if (sp.getBoolean("remember", false)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("email", User.getInstance().getEmail());
            editor.putString("password", User.getInstance().getPassword());
            editor.commit();
            User.getInstance().setConnected(true);
        } else {
            User.getInstance().getFirebaseAuth().signOut();

            //User.getInstance().SignOut();
        }
    }
}