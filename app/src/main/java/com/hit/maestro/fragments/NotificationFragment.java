package com.hit.maestro.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.adapter.ChatListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    List<String> notifications;
    ChatListAdapter adapter;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.fragment_notification, container, false);
       recyclerView = view.findViewById(R.id.notification_rv);
       user= User.getInstance();
        notifications = new ArrayList<>(user.getNotifications());
       Collections.reverse(notifications);
       adapter = new ChatListAdapter(notifications);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setListener(new ChatListAdapter.myChatListener() {
            @Override
            public void onChatClicked(String UID, View view) {
                Bundle bundle=new Bundle();
                bundle.putString("UID",UID);

                Navigation.findNavController(view).navigate(R.id.conversationFragment,bundle);
            }
        });

        return  view;
    }
}