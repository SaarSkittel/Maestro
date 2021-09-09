package com.hit.maestro;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class User {
    private FirebaseUser firebaseUser;
    private static User user=null;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userName;
    private String fullName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return firebaseUser.getDisplayName();
    }

    public void setFullName(String fullName) {
        firebaseUser.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(fullName).build());;
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
    }

    public FirebaseUser getFirebaseUser() {
        firebaseUser=firebaseAuth.getCurrentUser();
        return firebaseUser;
    }
}
