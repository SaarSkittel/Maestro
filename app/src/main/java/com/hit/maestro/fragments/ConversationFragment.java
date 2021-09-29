package com.hit.maestro.fragments;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hit.maestro.User;
import com.hit.maestro.adapter.ChatAdapter;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.proxy.MessagingProxy;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {

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
        view=inflater.inflate(R.layout.conversation_fragment,container,false);
        message=view.findViewById(R.id.chat_et);
        sendButton=view.findViewById(R.id.conv_btn);
        String UID=getArguments().getString("UID");

        //if( User.getInstance().getChats()==null) User.getInstance().getChats().put(UID,new ArrayList<ChatMessage>());
        if(User.getInstance().getChats().containsKey(UID)==false)
            //User.getInstance().getChats().put(UID,new  HashMap<String, HashMap<String, Object>>());
            User.getInstance().getChats().put(UID,new ArrayList<ChatMessage>());

        chatMessages=new ArrayList<ChatMessage>(User.getInstance().getChatById(UID));
        adapter=new ChatAdapter(User.getInstance().getChatById(UID));

        recyclerView=view.findViewById(R.id.conv_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        IntentFilter filter=new IntentFilter("message_received");
        newMessageReceived=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMessages.clear();
                chatMessages=new ArrayList<ChatMessage>(User.getInstance().getChatById(UID));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
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