package com.hit.maestro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.hit.maestro.R;

public class RegisterFragment extends DialogFragment {

    interface OnRegisterFragmentListener{
        void onRegister(String fullname,String username,String email, String password);
        void onSignInFromRegisterFragment();
    }

    OnRegisterFragmentListener callBack;

    public RegisterFragment(OnRegisterFragmentListener callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_fragment,container,false);
        EditText fullnameET = rootView.findViewById(R.id.fullname_input);
        String fullname = fullnameET.getText().toString();
        EditText usernameET = rootView.findViewById(R.id.username_input);
        String username = usernameET.getText().toString();
        EditText emailET = rootView.findViewById(R.id.email_input);
        String email = emailET.getText().toString();
        EditText passwordET = rootView.findViewById(R.id.password_input);
        String password = passwordET.getText().toString();
        EditText passwordConfET = rootView.findViewById(R.id.confirmPassword_input);
        String passwordConf = passwordConfET.getText().toString();
        TextView note = rootView.findViewById(R.id.note);

        Button registerBtn = rootView.findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullname.isEmpty()||username.isEmpty()||email.isEmpty()||password.isEmpty()||passwordConf.isEmpty()){
                    note.setText("Please fill all fields");
                }
                else if(!password.equals(passwordConf)){
                    note.setText("Make sure the password is correct");
                }
                else{
                    callBack.onRegister(fullname,username,email,password);
                }
            }
        });

        TextView signInBtn = rootView.findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignInFromRegisterFragment();
            }
        });

        return rootView;
    }
}

/*
לשים במיין אקטיביטי!!!!!

      public void onSignInFromRegisterFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(REGISTER_TAG);//ליצור תג במיין
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        //להעלות את הפרגמנט של ההתחברות
    }

    public void onRegister(String fullname,String username,String email, String password){
        //save to DB
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(REGISTER_TAG);//ליצור תג במיין
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

* */
