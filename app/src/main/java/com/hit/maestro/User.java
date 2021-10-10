package com.hit.maestro;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
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
    private List<String>notifications;
    private List<String> courses;
    private HashMap<String,List<ChatMessage>> chats;
    private FirebaseMessaging messaging=FirebaseMessaging.getInstance();
    private List<String>orderMessages;
    private  String location;


    public boolean isInNotifications(String UID){
        boolean answer=false;
        for(int i=0;i<notifications.size();++i){
            if(notifications.get(i).matches(UID)){
                answer=true;
                break;
            }
        }
        return answer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void updateNotifications(String UID){
        for(int i=0;i<notifications.size();++i){
            if(notifications.get(i).matches(UID)) {
                notifications.remove(i);
            }
        }
    }

    public void addMessageToOrderList(String UID){
        for(int i=0;i<orderMessages.size();++i){
            if(orderMessages.get(i).matches(UID)){
                orderMessages.remove(i);
            }
        }
        orderMessages.add(UID);
    }

    public FirebaseMessaging getMessaging() {
        return messaging;
    }

    public void setMessaging(FirebaseMessaging messaging) {
        this.messaging = messaging;
    }

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

    public void addCourseToUser(String courseName){
        courses.add(courseName);
        DatabaseProxy.getInstance().setCourses(courses);
    }

    public boolean isUserRegisteredToCourse(String courseName){
        boolean isRegistered=false;
        if(!courses.isEmpty()){
            for (int i=0;i<courses.size();++i){
                if(courseName.matches(courses.get(i))){
                    isRegistered=true;
                }
            }
        }
        return isRegistered;
    }

    public List<ChatMessage> getChatById(String UID){

        return chats.get(UID);
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }

    public List<String> getOrderMessages() {
        return orderMessages;
    }

    public void setOrderMessages(List<String> orderMessages) {
        this.orderMessages = orderMessages;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }


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

    public void setUserNotification(String UID){
        for (int i=0;i<notifications.size();++i){
            if(notifications.get(i).matches(UID)){
                notifications.remove(i);
            }
        }
        notifications.add(UID);
    }

    public void getUserData(){
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
                    location=new String();
                    courses=new ArrayList<>();
                    orderMessages=new ArrayList<>();
                    chats=new HashMap<String,List<ChatMessage>>(0);
                    notifications=new ArrayList<String>();
                    UID=firebaseUser.getUid();
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
                    messaging.subscribeToTopic(UID);
                    DatabaseProxy.getInstance().setUserImageUri(image,UID);
                    DatabaseProxy.getInstance().setUserName(i_fullName);
                    DatabaseProxy.getInstance().setLocation(location);
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
                    Log.d(TAG,"Sign in successful");
                    isConnected=true;
                    fullName = firebaseUser.getDisplayName();
                    email = i_email;
                    password = i_password;
                    firebaseUser = firebaseAuth.getCurrentUser();
                    UID=firebaseUser.getUid();
                    chats=new HashMap<String,List<ChatMessage>>(0);
                    notifications=new ArrayList<String>();
                    messaging.unsubscribeFromTopic(UID);
                    messaging.subscribeToTopic(UID);
                    orderMessages=new ArrayList<String>();
                    getUserData();
                }
                else{
                    isConnected=false;
                    Log.d(TAG,task.getException().getMessage());
                }
            }
        });
    }

    public void SignOut(Context context) {
        isConnected = false;
        messaging.unsubscribeFromTopic(UID);
        DatabaseProxy.getInstance().setUserNotifications(notifications);
        DatabaseProxy.getInstance().setOrderMessages(orderMessages,UID);

        firebaseAuth.signOut();
        NotificationManager notifManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        user=null;
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
