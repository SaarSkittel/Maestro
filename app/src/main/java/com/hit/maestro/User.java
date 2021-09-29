package com.hit.maestro;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hit.maestro.proxy.DatabaseProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private FirebaseUser firebaseUser;
    private static User user=null;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String email;
    private String fullName;
    private String password;
    private String UID;
    private FirebaseMessaging messaging=FirebaseMessaging.getInstance();

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public HashMap<String, List<ChatMessage>> getChats() {
        return chats;
    }

    public void setChats(HashMap<String, List<ChatMessage>> chats) {
        this.chats = chats;
    }


    public List<ChatMessage> getChatById(String UID){

        return chats.get(UID);
    }

   /* public void setChats(HashMap<String, HashMap<String, HashMap<String, Object>>> chats) {
        this.chats = chats;
    }*/

    private List<String> courses;
    private HashMap<String,List<ChatMessage>> chats;

    public String getUID() {
        return UID;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

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

    public void getUserData(){
        //User user = DatabaseProxy.getInstance().getUser(UID);
       //this.chats= DatabaseProxy.getInstance().getChats(getUID());
       this.courses= DatabaseProxy.getInstance().getCourses(getUID());
    }
    public void setUserData(){
        DatabaseProxy.getInstance().setUser(this);
    }
    private User(){

        firebaseAuth=FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser!=null) {
                    isConnected = true;
                    courses=new ArrayList<>();

                    courses.add("electric_guitar");
                    courses.add("acoustic_guitar");
                    courses.add("bass_guitar");
                    courses.add("eran_homo");
                    chats=new HashMap<String,List<ChatMessage>>(0);
                    //chats= new HashMap<String, HashMap<String, HashMap<String, Object>>>();
                    List<ChatMessage>messages=new ArrayList<ChatMessage>();
                    UID=firebaseUser.getUid();
                    //chats.put(UID,messages);
                    messaging.unsubscribeFromTopic(UID);
                    messaging.subscribeToTopic(UID);

                }
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

    public void CreateUser(String i_fullName, String i_email, String i_password, Uri image){
        user.getFirebaseAuth().createUserWithEmailAndPassword(i_email,i_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    isConnected=true;
                    user.getFirebaseUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(i_fullName).build());
                    fullName = i_fullName;
                    email = i_email;
                    password = i_password;
                    DatabaseProxy.getInstance().setUserImageUri(image,UID);
                    DatabaseProxy.getInstance().setUserName(i_fullName);
                  //  UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(image)).build();
                    //user.getFirebaseAuth().getCurrentUser().updateProfile(request);
                    Log.d(TAG,"Sign up successful");
                }
                else {
                    isConnected=false;
                    fullName = "";
                    email = "";
                    password = "";
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
        messaging.subscribeToTopic(UID);
    }

    public boolean CheckStatus(){
        boolean status;
        if(firebaseUser!=null && !firebaseUser.isAnonymous())
            status=true;
        else
            status=false;
        return status;
    }
}
