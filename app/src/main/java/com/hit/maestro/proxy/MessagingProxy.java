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
import com.google.firebase.database.DatabaseReference;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Documented;
import java.sql.Array;
import java.util.ArrayList;
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
                    ChatMessage chatMessage=new ChatMessage(message,user.getFullName(),user.getUID(),"");
                    //ChatMessage chatMessage=new ChatMessage(user.getFirebaseUser().getPhotoUrl().toString(),user.getFullName(),user.g
                    DatabaseReference reference;

                    //temp.add(chatMessage);
                    if (isUser){
                        //DatabaseProxy.getInstance().getDatabase().getReference("/users/"+user.getUID()+"/chats/").child(to).setValue(I);
                        //reference=DatabaseProxy.getInstance().getDatabase().getReference("/users/"+user.getUID()+"/chats/").child(to);
                        reference=DatabaseProxy.getInstance().getDatabase().getReference("/users/"+user.getUID()+"/chats/").child(to);
                    }else {
                        reference=DatabaseProxy.getInstance().getDatabase().getReference("/chats/").child(to);
                    }
                    reference.push().setValue(chatMessage);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("VOLLEY_IS_SHIT",error.toString());
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
            queue.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

