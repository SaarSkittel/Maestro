package com.hit.maestro.fragments;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hit.maestro.User;
import com.hit.maestro.adapter.ChatAdapter;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.proxy.MessagingProxy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    View view;
    ChatAdapter adapter;
    TextView message;
    FloatingActionButton sendButton;
    RecyclerView recyclerView;
    BroadcastReceiver newMessageReceived;
    List<ChatMessage>chatMessages;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.chat_fragment,container,false);
        message=view.findViewById(R.id.chat_et);
        sendButton=view.findViewById(R.id.chat_btn);
        String UID;
        if(User.getInstance().getUID().matches("eJgWfwkxGUZws5wwQujsy5lAXzX2")){
            UID="AxF0O2nYNcfq8Fr8LHvqajWmmOV2";
        }
        else UID="eJgWfwkxGUZws5wwQujsy5lAXzX2";
        //if( User.getInstance().getChats()==null) User.getInstance().getChats().put(UID,new ArrayList<ChatMessage>());
        if(User.getInstance().getChats().containsKey(UID)==false)
            User.getInstance().getChats().put(UID,new  HashMap<String, HashMap<String, Object>>());

        chatMessages=User.getInstance().getChatById(UID);
        adapter=new ChatAdapter(User.getInstance().getChatById(UID));

        recyclerView=view.findViewById(R.id.chat_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        IntentFilter filter=new IntentFilter("message_received");
        newMessageReceived=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMessages=User.getInstance().getChatById(UID);
                adapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newMessageReceived,filter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagingProxy.SendMessageTo(UID,message.getText().toString(),true,getContext());
            }
        });
        return view;
        }
    }
