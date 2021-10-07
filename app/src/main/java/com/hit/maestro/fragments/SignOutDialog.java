package com.hit.maestro.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hit.maestro.R;

public class SignOutDialog extends DialogFragment {

    interface OnSignOutListener{
        void onSignOut();
    }

    OnSignOutListener callBack;

    public SignOutDialog(OnSignOutListener callBack) {
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sign_out,container,false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        ImageView close = view.findViewById(R.id.close_logout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOutDialog.this.dismiss();
            }
        });

        Button yesBtn = view.findViewById(R.id.yes_btn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignOut();
                SignOutDialog.this.dismiss();

            }
        });

        Button noBtn = view.findViewById(R.id.no_btn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOutDialog.this.dismiss();
            }
        });

        return view;
    }
}
