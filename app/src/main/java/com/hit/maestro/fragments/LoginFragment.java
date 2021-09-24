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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.hit.maestro.R;
import com.hit.maestro.User;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends DialogFragment {

    RegisterFragment.OnCompletedFragmentListener callback;
    SharedPreferences sp;

    public LoginFragment(RegisterFragment.OnCompletedFragmentListener callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment,container,false);
        EditText emailET = rootView.findViewById(R.id.email_login);
        EditText passwordET = rootView.findViewById(R.id.password_login);
        TextView note = rootView.findViewById(R.id.note_login);
        CheckBox rememberCheckBox = rootView.findViewById(R.id.remember_me);
        sp = this.getActivity().getSharedPreferences("login_status", MODE_PRIVATE);

        Button submitBtn = rootView.findViewById(R.id.submit_btn);
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

        return rootView;
    }
}
