package com.hit.maestro.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.adapter.ChatListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ChatListFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    ChatListAdapter adapter;
    LinkedHashMap<String, List<ChatMessage>>map;
    BroadcastReceiver newMessageReceived;
    List<String> keys;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.chat_list_fragment,container,false);
        recyclerView=view.findViewById(R.id.list_chat_rv);
        map=new LinkedHashMap<>( User.getInstance().getChats());
        keys=new ArrayList<String>(map.keySet());
        adapter=new ChatListAdapter(map,keys);
        adapter.setListener(new ChatListAdapter.myChatListener() {
            @Override
            public void onChatClicked(String UID, View view) {
                Bundle bundle=new Bundle();
                bundle.putString("UID",UID);
                Navigation.findNavController(view).navigate(R.id.action_chatFragment_to_conversationFragment,bundle);
            }
        });
        IntentFilter filter=new IntentFilter("message_received");
        newMessageReceived=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                map.clear();
                map=new LinkedHashMap<>( User.getInstance().getChats());
                keys.clear();
                keys.addAll(map.keySet());
                adapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newMessageReceived,filter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        map.clear();
        map=new LinkedHashMap<>( User.getInstance().getChats());
        keys.clear();
        keys.addAll(map.keySet());
        adapter.notifyDataSetChanged();
    }
}
