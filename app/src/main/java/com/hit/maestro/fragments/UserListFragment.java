package com.hit.maestro.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.adapter.ChatListAdapter;
import com.hit.maestro.adapter.UserListAdapter;
import com.hit.maestro.proxy.DatabaseProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserListFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    UserListAdapter adapter;
    List<HashMap<String,String>>userList;
    BroadcastReceiver newMessageReceived;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.user_list_fragment,container,false);
        recyclerView=view.findViewById(R.id.user_list_rv);

        DatabaseReference reference=DatabaseProxy.getInstance().getDatabase().getReference();

        userList=new ArrayList<>(DatabaseProxy.getInstance().getAllUsers());
        adapter=new UserListAdapter(userList);

        adapter.setListener(new UserListAdapter.myUserListener() {
            @Override
            public void onChatClicked(int position, View view) {
                Bundle bundle=new Bundle();
                bundle.putString("UID",userList.get(position).get("UID"));
                Navigation.findNavController(view).navigate(R.id.action_chatFragment_to_conversationFragment,bundle);
            }
        });

        IntentFilter filter=new IntentFilter("user_update");
        newMessageReceived=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                userList.clear();
                userList.addAll(DatabaseProxy.getInstance().getAllUsers());
                adapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newMessageReceived,filter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
