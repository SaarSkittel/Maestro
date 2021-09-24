package com.hit.maestro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"create");
        sp = getSharedPreferences("login_status", MODE_PRIVATE);
        if (sp.getBoolean("remember", false)) {
            User.getInstance().setConnected(true);
            User.getInstance().SignIn(sp.getString("email",""),sp.getString("password",""));
        }
        else {
            User.getInstance().setConnected(false);
            User.getInstance().SignOut();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"pause");
        SharedPreferences.Editor editor = sp.edit();
        if (sp.getBoolean("remember", false)) {
            editor.putString("email", User.getInstance().getEmail());
            editor.putString("password", User.getInstance().getPassword());
            User.getInstance().setConnected(true);
        } else {
            //editor.putBoolean("status", false);
            User.getInstance().setConnected(false);
        }
        editor.commit();
    }
}