package com.hit.maestro;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class User {
    private FirebaseUser firebaseUser;
    private static User user=null;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String email;
    private String fullName;
    private String password;
    private boolean isConnected;
    final String TAG = "Connected";

    public boolean isConnected() {
        return isConnected;
    }

    public String getEmail() {
        return email;
    }

    public void setUserName(String email) {
        this.email = email;
    }

    public String getFullName() {
        return firebaseUser.getDisplayName();
    }

    public void setFullName(String fullName) {
        firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullName).build());;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String i_password) {
        password = i_password;
    }

    public static User getInstance(){
        if(user==null){
            synchronized (User.class){
                if(user==null){
                    user=new User();
                }
            }
        }
        return user;
    }
    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public FirebaseAuth.AuthStateListener getAuthStateListener() {
        return authStateListener;
    }

    public void setAuthStateListener(FirebaseAuth.AuthStateListener authStateListener) {
        this.authStateListener = authStateListener;
    }

    private User(){

        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser!=null)
                    isConnected=true;
                else
                    isConnected=false;
            }
        };
    }

    public FirebaseUser getFirebaseUser() {
        firebaseUser=firebaseAuth.getCurrentUser();
        return firebaseUser;
    }

    public void AddListener(){
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public void RemoveListener(){
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public void CreateUser(String i_fullName,String i_email,String i_password){
        /*firebaseAuth.createUserWithEmailAndPassword(i_email,i_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    isConnected=true;
                    fullName = i_fullName;
                    email = i_email;
                    password = i_password;
                    firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(i_fullName).build()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });

                }
                else{
                    isConnected=false;
                    fullName = "";
                    email = "";
                    password = "";
                }
            }
        });*/
        user.getFirebaseAuth().createUserWithEmailAndPassword(i_email,i_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    isConnected=true;
                    user.getFirebaseUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(i_fullName).build());
                    Log.d(TAG,"Sign up successful");
                }
                else {
                    isConnected=false;
                    Log.d(TAG,task.getException().getMessage());
                }
            }
        });
    }

    public void SignIn(String i_email,String i_password){
        firebaseAuth.signInWithEmailAndPassword(i_email,i_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    isConnected=true;
                    Log.d(TAG,"Sign in successful");
                    fullName = firebaseUser.getDisplayName();
                    email = i_email;
                    password = i_password;
                }
                else{
                    isConnected=false;
                    Log.d(TAG,task.getException().getMessage());
                }
            }
        });
    }

    public void SignOut(){
        firebaseAuth.signOut();
        isConnected=false;
    }
}
