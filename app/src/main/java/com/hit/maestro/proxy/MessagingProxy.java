package com.hit.maestro.proxy;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Documented;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagingProxy {

    public static void SendMessageTo(String to, String message,Boolean isUser, Context context){
        JSONObject jsonObject= new JSONObject();
        final String APP_KEY="AAAAiElpjxU:APA91bG3BKh_XjVfcxfGQS8BNHPsH00gveXrxPrrBSn23AuRyw0l-dNqL0wkuW8gU3N-A5ro-9Reanf2pED2JQYlnWl9fXjfC6yT6GJKeXKFa-ZiQYFYd34uan6do614a6yUf-nGJ2mi";
        try {
            User user=User.getInstance();
            jsonObject.put("to","/topics/" + to);
            jsonObject.put("data",new JSONObject().put("message",message).put("UID",user.getUID()));
            String url="https://fcm.googleapis.com/fcm/send";
            RequestQueue queue= Volley.newRequestQueue(context);
            StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    ChatMessage chatMessage=new ChatMessage(message,user.getFullName(),user.getUID(),DatabaseProxy.getInstance().getUserImageUri(user.getUID()));
                    DatabaseReference reference;

                    //temp.add(chatMessage);
                    if (isUser){
                        List<ChatMessage>chatMessages=new ArrayList<>(user.getChatById(to));
                        chatMessages.add(chatMessage);
                        reference=DatabaseProxy.getInstance().getDatabase().getReference("/users/"+user.getUID()+"/chats/").child(to);
                        reference.setValue(chatMessages);
                        reference=DatabaseProxy.getInstance().getDatabase().getReference("/users/"+to+"/chats/").child(user.getUID());
                        reference.setValue(chatMessages);
                        reference=  reference=DatabaseProxy.getInstance().getDatabase().getReference("/users/"+to).child("/notifications");
                        List<String>notifications= DatabaseProxy.getInstance().getUserNotificationsByUID(to);
                        for(int i=0;i<notifications.size();++i){
                            if(user.getUID().matches(notifications.get(i))){
                                notifications.remove(i);
                            }
                        }
                        notifications.add(user.getUID());
                        reference.setValue(notifications);

                    }
                    else {
                        List<ChatMessage>chatMessages=new ArrayList<>(DatabaseProxy.getInstance().getLessonChatById(to));
                        chatMessages.add(chatMessage);
                        reference=DatabaseProxy.getInstance().getDatabase().getReference("chats").child(to);
                        reference.setValue(chatMessages);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("VOLLEY",error.toString());
                    //Send message when something goes wrong

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> headers=new HashMap<>();
                    headers.put("Content-Type","application/json");
                    headers.put("Authorization","key="+APP_KEY);
                    return headers;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return jsonObject.toString().getBytes();
                }
            };
            queue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

