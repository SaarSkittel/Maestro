package com.hit.maestro;

import android.app.Activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ReadAndWriteStorage {

    public static boolean loadRememberMe(Activity activity){
        try {
            FileInputStream fileInputStream= activity.openFileInput("remember");
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            boolean isRemember = (boolean)objectInputStream.readObject();
            objectInputStream.close();
            return isRemember;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setEmailAtStorage(Activity activity,String email){
        try {
            FileOutputStream fileOutputStream = activity.openFileOutput("email", activity.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(email);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPasswordAtStorage(Activity activity,String password){
        try {
            FileOutputStream fileOutputStream = activity.openFileOutput("password", activity.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(password);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setFromNotificationStorage(Activity activity,boolean fromNotification){
        try {
            FileOutputStream fileOutputStream = activity.openFileOutput("notification", activity.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(fromNotification);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean loadFromNotification(Activity activity){
        try {
            FileInputStream fileInputStream= activity.openFileInput("notification");
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            boolean fromNotification = (boolean)objectInputStream.readObject();
            objectInputStream.close();
            return fromNotification;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String loadEmail(Activity activity){
        try {
            FileInputStream fileInputStream= activity.openFileInput("email");
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            String email = (String)objectInputStream.readObject();
            objectInputStream.close();
            return email;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String loadPassword(Activity activity){
        try {
            FileInputStream fileInputStream= activity.openFileInput("password");
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            String password = (String)objectInputStream.readObject();
            objectInputStream.close();
            return password;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setRememberMeAtStorage(Activity activity,boolean isRemember){
        try {
            FileOutputStream fileOutputStream = activity.openFileOutput("remember", activity.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(isRemember);
            objectOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
