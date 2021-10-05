package com.hit.maestro.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.hit.maestro.R;

public class RegisterOrLoginFragment extends DialogFragment {

    interface OnRegisterOrLoginFragmentListener{
        void onSignIn();
        void onSignUp();
    }

    RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener callBack;

    public RegisterOrLoginFragment(RegisterOrLoginFragment.OnRegisterOrLoginFragmentListener callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_or_login_fragment,container,false);

        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        Button signUpBtn = view.findViewById(R.id.signup_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignUp();
            }
        });

        Button signInBtn = view.findViewById(R.id.signin_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignIn();
            }
        });

        ImageView close = view.findViewById(R.id.close_register_or_login);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterOrLoginFragment.this.dismiss();
            }
        });

        return view;
    }
}

