package com.hit.maestro.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.hit.maestro.R;
import com.hit.maestro.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterFragment extends DialogFragment {

    interface OnCompletedFragmentListener{
        void onCompleted();
        void onSignInFromRegisterFragment();
    }

    User user;
    OnCompletedFragmentListener callBack;
    final int WRITE_PERMISSION_REQUEST = 1;
    final int PICK_FROM_GALLERY = 2;
    final int CAMERA_REQUEST = 3;
    Button m_Take_Pic_Btn,m_Pick_Photo_From_Gallery_Btn;
    File m_File;
    String m_PhotoPath;
    Boolean isPhoto = false ;
    Bitmap bitmap;
    //File file;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> picFromAlbumLauncher;
    Uri pic;
    ImageView image;
    View view;

    public RegisterFragment(OnCompletedFragmentListener callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.register_fragment,container,false);
        EditText fullnameET = view.findViewById(R.id.fullname_input);
        EditText emailET = view.findViewById(R.id.email_input);
        EditText passwordET = view.findViewById(R.id.password_input);
        EditText passwordConfET = view.findViewById(R.id.confirmPassword_input);
        TextView note = view.findViewById(R.id.note);
        image=view.findViewById(R.id.register_iv);
        user=User.getInstance();
        Button registerBtn = view.findViewById(R.id.register_btn);
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

        TextView signInBtn = view.findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignInFromRegisterFragment();
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            int hasWritePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
            }
        }
        setChoosePicBtn();
        setPickSongBtn();
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            //Intent data = result.getData();
                            //bitmap = (Bitmap)data.getExtras().get("data");
                            //imageView.setImageBitmap(bitmap);

                            Glide.with(RegisterFragment.this).load(pic).into(image);
                            // bitmap = BitmapFactory.decodeFile(m_File.getAbsolutePath());
                            // imageView.setImageBitmap(bitmap);
                            isPhoto = true;
                        }
                    }
                });
        picFromAlbumLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == AppCompatActivity.RESULT_OK) {
                            //m_Song_Photo.setImageURI(selectedImageUri);
                            pic=result.getData().getData();
                            getActivity().getBaseContext().getContentResolver().takePersistableUriPermission(pic
                                    , result.getData().getFlags()
                                            & ( Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            + Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    )
                            );
                            //Glide.with(getActivity().getBaseContext()).load(pic).into(image);
                            Glide.with(RegisterFragment.this).load(pic).into(image);
                            isPhoto = true;
                        }
                    }
                });
        m_File = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg");

        return view;
    }

    private void setChoosePicBtn() {

        m_Take_Pic_Btn = view.findViewById(R.id.camera);
        m_Take_Pic_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                pic = FileProvider.getUriForFile(getActivity(),getActivity().getPackageName()+".provider",m_File);//Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,pic);
                Log.d("File",pic.toString());
                cameraLauncher.launch(intent);
            }
        });

    }



    private void setPickSongBtn() {
        m_Pick_Photo_From_Gallery_Btn = view.findViewById(R.id.gallery);
        m_Pick_Photo_From_Gallery_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {
                    int hasWritePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST);
                    }
                    else
                    {
                        choosePic();
                    }
                }
                else
                {
                    choosePic();
                }
            }
        });
    }
    private void choosePic() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        galleryIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        picFromAlbumLauncher.launch(galleryIntent);
    }

}
