package com.hit.maestro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hit.maestro.R;
import com.hit.maestro.User;

public class ResetPasswordFragment extends DialogFragment {

    EditText emailET;
    TextView note;
    Button resetBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_password_fragment,container,false);
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        emailET=view.findViewById(R.id.reset_email);
        note=view.findViewById(R.id.reset_note);
        resetBtn=view.findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString();
                if (email.equals("")){
                    note.setText(getResources().getString(R.string.enter_email));
                } else {
                    User.getInstance().getFirebaseAuth().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                emailET.setText("");
                                note.setText(getResources().getString(R.string.check_email));
                            } else {
                                String error = task.getException().getMessage();
                                note.setText(error);
                            }
                        }
                    });
                }
            }
        });

        ImageView close = view.findViewById(R.id.close_reset);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPasswordFragment.this.dismiss();
            }
        });

        return view;
    }
}
