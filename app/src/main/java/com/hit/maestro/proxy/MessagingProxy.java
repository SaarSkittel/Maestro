package com.hit.maestro.proxy;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MessagingProxy {
    final String APP_KEY="AAAAiElpjxU:APA91bG3BKh_XjVfcxfGQS8BNHPsH00gveXrxPrrBSn23AuRyw0l-dNqL0wkuW8gU3N-A5ro-9Reanf2pED2JQYlnWl9fXjfC6yT6GJKeXKFa-ZiQYFYd34uan6do614a6yUf-nGJ2mi";
    public void SendMessage(String to, String message, Context context){
        JSONObject jsonObject= new JSONObject();

        try {
            jsonObject.put("to","/topics/" + to);
            jsonObject.put("data",new JSONObject().put("message",message));
            String url="https://fcm.googleapis.com/fcm/send";
            RequestQueue queue= Volley.newRequestQueue(context);
            StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


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

