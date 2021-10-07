package com.hit.maestro.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    BroadcastReceiver newNotificationReceived;
    List<ChatMessage> chatMessages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.conversation_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.conv_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        message = view.findViewById(R.id.chat_et);
        sendButton = view.findViewById(R.id.conv_btn);
        String UID = getArguments().getString("UID");

        Intent intent = new Intent("notification_cancel");
        intent.putExtra("UID",UID);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

        User.getInstance().updateNotifications(UID);
        //if( User.getInstance().getChats()==null) User.getInstance().getChats().put(UID,new ArrayList<ChatMessage>());
        if (User.getInstance().getChats().containsKey(UID) == false)

            User.getInstance().getChats().put(UID, new ArrayList<ChatMessage>());

        chatMessages = new ArrayList<ChatMessage>(User.getInstance().getChatById(UID));
        adapter = new ChatAdapter(User.getInstance().getChatById(UID));

        recyclerView = view.findViewById(R.id.conv_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        IntentFilter filter = new IntentFilter("message_received");
        newMessageReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMessages.clear();
                chatMessages.addAll(User.getInstance().getChatById(UID));
                User.getInstance().addMessageToOrderList(UID);
                adapter.notifyDataSetChanged();
                User.getInstance().updateNotifications(UID);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                if (!chatMessages.get(chatMessages.size() - 1).getUID().matches(User.getInstance().getUID())) {
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
                        r.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newMessageReceived, filter);
/*
        IntentFilter filter2 = new IntentFilter("notification_received");
        newNotificationReceived = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               User.getInstance().updateNotifications(UID);
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newNotificationReceived, filter);
*/
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().isEmpty()) {
                    User.getInstance().setUserNotification(UID);
                    User.getInstance().addMessageToOrderList(UID);
                    MessagingProxy.SendMessageTo(UID, message.getText().toString(), true, getContext());
                    message.setText("");
                }
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(view.getContext()).unregisterReceiver(newMessageReceived);
        LocalBroadcastManager.getInstance(view.getContext()).unregisterReceiver(newMessageReceived);
        Intent intent = new Intent("notification_cancel");
        intent.putExtra("UID","");
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //getParentFragmentManager().popBackStack();
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
