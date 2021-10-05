package com.hit.maestro.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
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
    final String PICTURE_TAG="6";
    OnCompletedFragmentListener callBack;
    ShapeableImageView picture;
    RelativeLayout relativeLayout;
    Uri pic=Uri.parse("android.resource://com.hit.maestro/drawable/default_profile_picture");
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
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
        EditText fullnameET = view.findViewById(R.id.fullname_input);
        EditText emailET = view.findViewById(R.id.email_input);
        EditText passwordET = view.findViewById(R.id.password_input);
        EditText passwordConfET = view.findViewById(R.id.confirmPassword_input);
        TextView note = view.findViewById(R.id.note);

        user=User.getInstance();
        Button registerBtn = view.findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullnameET.getText().toString().isEmpty()||emailET.getText().toString().isEmpty()||passwordET.getText().toString().isEmpty()||passwordConfET.getText().toString().isEmpty()){
                    note.setText(getResources().getString(R.string.fields));
                }
                else if(!passwordET.getText().toString().equals(passwordConfET.getText().toString())){
                    note.setText(getResources().getString(R.string.pass_valid));
                }
                /*else if(!exit){
                    note.setText("Please select a profile picture");
                }*/
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
                    note.setText(getResources().getString(R.string.success));
                    user.CreateUser(fullnameET.getText().toString(),emailET.getText().toString(),passwordET.getText().toString(),pic);
                    //user.setUserData();
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

        //Button selectPic = view.findViewById(R.id.add_pic);
        relativeLayout=view.findViewById(R.id.buttons_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureFragment pictureFragment=new PictureFragment(RegisterFragment.this,pic);
                pictureFragment.show(getChildFragmentManager(),PICTURE_TAG);
            }
        });

        //pic = Uri.parse(sp.getString("path_pic",null));
        picture = view.findViewById(R.id.user_image);
        Glide.with(this)
                .load(pic)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(picture);

        /*777file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg");
        takePictureActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    exit=true;
                    //Glide.with(RegisterFragment.this).load(pic).into(picture);
                }
            }
        });

        picFromAlbumActivityResultLauncher =registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //Glide.with(RegisterFragment.this).load(result.getData().getData()).into(picture);
                    pic=result.getData().getData();
                    view.getContext().getContentResolver().takePersistableUriPermission(pic
                            , result.getData().getFlags()
                                    & ( Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    + Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )
                    );
                    exit=true;
                }
            }
        });


        camera = view.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int hasWritePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission == PackageManager.PERMISSION_GRANTED) {
                        takePicture();
                    } else {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, write_permission_request_camera);
                    }
                } else {
                    takePicture();
                }

            }
        });

        album = view.findViewById(R.id.gallery);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int hasWritePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (hasWritePermission == PackageManager.PERMISSION_GRANTED) {
                        picFromMemory();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, write_permission_request_album);
                    }
                } else {
                    picFromMemory();
                }
            }
        });777*/

        ImageView close = view.findViewById(R.id.close_register);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment.this.dismiss();
            }
        });

        return view;
    }

    public void setPic(Uri pic) {
        this.pic = pic;
        Glide.with(this)
                .load(pic)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(picture);
    }

    public Uri getPic() {
        return pic;
    }

    /*private void picFromMemory(){
        exit=false;
        Intent intent= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //Intent intent= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        picFromAlbumActivityResultLauncher.launch(intent);
    }

    private void takePicture() {
        exit=false;
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        pic= FileProvider.getUriForFile(view.getContext(),getActivity().getPackageName()+".provider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,pic);
        takePictureActivityResultLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == write_permission_request_camera){
                takePicture();
            }
            else if(requestCode == write_permission_request_album){
                picFromMemory();
            }
            else {
                Toast.makeText(view.getContext(),"Can't work without permissions go to setting to grant access.",Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
