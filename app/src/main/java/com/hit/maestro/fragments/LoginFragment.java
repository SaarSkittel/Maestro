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

public class LoginFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment,container,false);
        EditText usernameET = rootView.findViewById(R.id.username_login);
        EditText passwordET = rootView.findViewById(R.id.password_login);
        TextView note = rootView.findViewById(R.id.note_login);

        Button submitBtn = rootView.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameET.getText().toString();
                String password = passwordET.getText().toString();
                if(username.isEmpty()||password.isEmpty()){
                    note.setText("Please fill all fields");
                }
                else if(checkLogin(username, password )){
                    //התחבר
                    LoginFragment.this.dismiss();
                }
                else{
                    note.setText("The username or password is incorrect");
                }
            }
        });

        return rootView;
    }

    private boolean checkLogin(String username, String password ){
        return true;
    }
}
