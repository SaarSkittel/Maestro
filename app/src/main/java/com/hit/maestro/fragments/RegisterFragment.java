package com.hit.maestro.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RegisterFragment extends DialogFragment {

    interface OnCompletedFragmentListener{
        void onCompleted(String fullName);
        void onSignInFromRegisterFragment();
    }
    final int LOCATION_PERMISSION_REQUEST=1;
    User user;
    final String PICTURE_TAG="6";
    OnCompletedFragmentListener callBack;
    ShapeableImageView picture;
    RelativeLayout relativeLayout;
    FusedLocationProviderClient client;
    EditText location;
    Uri pic=Uri.parse("android.resource://com.hit.maestro/drawable/default_profile_picture");
    View view;
    Geocoder geocoder;
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
        location=view.findViewById(R.id.country_input);
        Button gps=view.findViewById(R.id.gps_btn);
        user=User.getInstance();
        Button registerBtn = view.findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullnameET.getText().toString().isEmpty()||emailET.getText().toString().isEmpty()||passwordET.getText().toString().isEmpty()||passwordConfET.getText().toString().isEmpty()||location.getText().toString().isEmpty()){
                    note.setText(getResources().getString(R.string.fields));
                }
                else if(!passwordET.getText().toString().equals(passwordConfET.getText().toString())){
                    note.setText(getResources().getString(R.string.pass_valid));
                }

                else{

                    note.setText(getResources().getString(R.string.success));
                    String email = emailET.getText().toString();
                    String password = passwordET.getText().toString();
                    user.getFirebaseAuth().createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                User.getInstance().setConnected(true);
                                User.getInstance().setFirebaseUser(User.getInstance().getFirebaseAuth().getCurrentUser());
                                User.getInstance().setFullName(fullnameET.getText().toString());
                                User.getInstance().setUserName(email);
                                User.getInstance().setPassword(password);
                                User.getInstance().setUID(User.getInstance().getFirebaseUser().getUid());
                                User.getInstance().setChats(new HashMap<String, List<ChatMessage>>(0));
                                User.getInstance().setNotifications(new ArrayList<String>());
                                User.getInstance().setCourses(new ArrayList<String>());
                                User.getInstance().getMessaging().unsubscribeFromTopic(User.getInstance().getUID());
                                User.getInstance().getMessaging().subscribeToTopic(User.getInstance().getUID());
                                User.getInstance().setOrderMessages(new ArrayList<String>());
                                User.getInstance().setLocation(location.getText().toString());
                                user.getFirebaseUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullnameET.getText().toString()).build());
                                DatabaseProxy.getInstance().setUserImageUri(pic,User.getInstance().getUID());
                                DatabaseProxy.getInstance().setUserName(fullnameET.getText().toString());
                                DatabaseProxy.getInstance().setLocation(location.getText().toString());

                                RegisterFragment.this.dismiss();
                                callBack.onCompleted(fullnameET.getText().toString());
                            }
                            else {
                                User.getInstance().setConnected(false);
                                User.getInstance().setUserName("");
                                User.getInstance().setPassword("");
                                note.setText(getResources().getString(R.string.wrong));
                            }
                        }
                    });
                }
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=23){
                    int hasLocationPermission= getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if(hasLocationPermission!=PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST);
                    }
                    else getCountryName();
                }
               getCountryName();
            }
        });
        TextView signInBtn = view.findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onSignInFromRegisterFragment();
            }
        });

        relativeLayout=view.findViewById(R.id.buttons_layout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureFragment pictureFragment=new PictureFragment(RegisterFragment.this,pic);
                pictureFragment.show(getChildFragmentManager(),PICTURE_TAG);
            }
        });

        picture = view.findViewById(R.id.user_image);
        Glide.with(this)
                .load(pic)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(picture);

        ImageView close = view.findViewById(R.id.close_register);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterFragment.this.dismiss();
            }
        });

        return view;
    }
    private void getCountryName(){
        Handler handler=new Handler(Looper.getMainLooper());
        client= LocationServices.getFusedLocationProviderClient(getActivity());
        geocoder=new Geocoder(getContext());
        LocationCallback callback=new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location1=locationResult.getLastLocation();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            List<Address>addresses=geocoder.getFromLocation(location1.getLatitude(),location1.getLongitude(),1);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    location.setText(addresses.get(0).getCountryName());
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        };
        LocationRequest request=LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(Build.VERSION.SDK_INT>=23&&getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            client.requestLocationUpdates(request,callback,null);
        else if(Build.VERSION.SDK_INT<=22)
            client.requestLocationUpdates(request,callback,null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]!=PackageManager.PERMISSION_GRANTED){
            Snackbar.make(view,R.string.permissions, BaseTransientBottomBar.LENGTH_LONG).show();
        }
        else getCountryName();
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
}
