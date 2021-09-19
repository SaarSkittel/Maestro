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

import androidx.fragment.app.DialogFragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.hit.maestro.R;
import com.hit.maestro.User;

public class RegisterFragment extends DialogFragment {

    interface OnCompletedFragmentListener{
        void onCompleted();
        void onSignInFromRegisterFragment();
    }

    User user;
    OnCompletedFragmentListener callBack;

    public RegisterFragment(OnCompletedFragmentListener callBack) {
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
        EditText emailET = rootView.findViewById(R.id.email_input);
        String email = emailET.getText().toString();
        EditText passwordET = rootView.findViewById(R.id.password_input);
        String password = passwordET.getText().toString();
        EditText passwordConfET = rootView.findViewById(R.id.confirmPassword_input);
        //String passwordConf = passwordConfET.getText().toString();
        TextView note = rootView.findViewById(R.id.note);
        user=User.getInstance();
        Button registerBtn = rootView.findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullnameET.getText().toString().isEmpty()||emailET.getText().toString().isEmpty()||passwordET.getText().toString().isEmpty()||passwordConfET.getText().toString().isEmpty()){
                    note.setText("Please fill all fields");
                }
                else if(!passwordET.getText().toString().equals(passwordConfET.getText().toString())){
                    note.setText("Make sure the password is correct");
                }
                else{
                   /* user.getFirebaseAuth().createUserWithEmailAndPassword(emailET.getText().toString(),passwordET.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                user.getFirebaseUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullname).build());
                                note.setText("Sign up successful");
                            }
                            else {
                                note.setText(task.getException().getMessage());
                            }
                        }
                    });*/
                    note.setText("Sign up successful, please wait");
                    user.CreateUser(fullnameET.getText().toString(),emailET.getText().toString(),passwordET.getText().toString());
                    user.setUserData();
                    callBack.onCompleted();
                    RegisterFragment.this.dismiss();
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
