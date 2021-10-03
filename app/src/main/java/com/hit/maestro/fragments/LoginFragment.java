package com.hit.maestro.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends DialogFragment {

    RegisterFragment.OnCompletedFragmentListener callback;
    SharedPreferences sp;
    final String RESET_TAG = "4";

    public LoginFragment(RegisterFragment.OnCompletedFragmentListener callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment,container,false);
        EditText emailET = view.findViewById(R.id.email_login);
        EditText passwordET = view.findViewById(R.id.password_login);
        TextView note = view.findViewById(R.id.note_login);
        CheckBox rememberCheckBox = view.findViewById(R.id.remember_me);
        sp = this.getActivity().getSharedPreferences("login_status", MODE_PRIVATE);

        Button submitBtn = view.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                if(email.isEmpty()||password.isEmpty()){
                    note.setText("Please fill all fields");
                }
                else{/*
                    user.SignIn(email,password);
                    if(user.isConnected()){
                        LoginFragment.this.dismiss();
                        user.getUserData();
                        callback.onCompleted();
                    }
                    else{
                        note.setText("The username or password is incorrect");
                    }*/
                    note.setText("please wait");
                    User.getInstance().getFirebaseAuth().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //editor.putBoolean("status", true);
                                User.getInstance().setConnected(true);
                                User.getInstance().setFullName(User.getInstance().getFirebaseUser().getDisplayName());
                                User.getInstance().setUserName(email);
                                User.getInstance().setPassword(password);
                                User.getInstance().setFirebaseUser(User.getInstance().getFirebaseAuth().getCurrentUser());
                                User.getInstance().setUID(User.getInstance().getFirebaseUser().getUid());
                                User.getInstance().setCourses(new ArrayList<>());
                                User.getInstance().setChats(new HashMap<String, List<ChatMessage>>(0));
                                User.getInstance().setNotifications(new ArrayList<String>());
                                User.getInstance().getMessaging().unsubscribeFromTopic(User.getInstance().getUID());
                                User.getInstance().getMessaging().subscribeToTopic(User.getInstance().getUID());
                                LoginFragment.this.dismiss();
                                User.getInstance().getUserData();
                                callback.onCompleted();
                            }
                            else{
                                //editor.putBoolean("status", false);
                                User.getInstance().setConnected(false);
                                note.setText("The username or password is incorrect");
                            }
                        }
                    });
                }
                SharedPreferences.Editor editor = sp.edit();
                if(rememberCheckBox.isChecked())
                    editor.putBoolean("remember", true);
                else
                    editor.putBoolean("remember", false);
                editor.commit();
            }
        });

        TextView forgotBtn = view.findViewById(R.id.forgot_btn);
        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                resetPasswordFragment.show(getChildFragmentManager(),RESET_TAG);
            }
        });

        ImageView close = view.findViewById(R.id.close_login);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment.this.dismiss();
            }
        });

        return view;
    }
}
