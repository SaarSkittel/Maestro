package com.hit.maestro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.hit.maestro.R;
import com.hit.maestro.User;

public class LoginFragment extends DialogFragment {

    User user;
    RegisterFragment.OnCompletedFragmentListener callback;

    public LoginFragment(RegisterFragment.OnCompletedFragmentListener callback) {
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment,container,false);
        EditText emailET = rootView.findViewById(R.id.email_login);
        EditText passwordET = rootView.findViewById(R.id.password_login);
        TextView note = rootView.findViewById(R.id.note_login);
        user = User.getInstance();

        Button submitBtn = rootView.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                if(email.isEmpty()||password.isEmpty()){
                    note.setText("Please fill all fields");
                }
                else{
                    user.SignIn(email,password);
                    if(user.isConnected()){
                        LoginFragment.this.dismiss();
                        callback.onCompleted();
                    }
                    else{
                        note.setText("The username or password is incorrect");
                    }
                }
                /*else if(checkLogin(email, password )){
                    login(email, password);
                    LoginFragment.this.dismiss();
                }
                else{
                    note.setText("The username or password is incorrect");
                }*/
            }
        });

        return rootView;
    }

    private boolean checkLogin(String username, String password ){
        return true;
    }

    private void login(String username, String password){

    }
}
