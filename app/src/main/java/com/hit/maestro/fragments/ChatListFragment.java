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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChatListFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    ChatListAdapter adapter;
    HashMap<String, List<ChatMessage>>map;
    BroadcastReceiver newMessageReceived;
    List<String> keys;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.chat_list_fragment,container,false);
        recyclerView=view.findViewById(R.id.list_chat_rv);
        map=new HashMap<>( User.getInstance().getChats());
        keys = new ArrayList<String>( map.keySet());
       /* if(!map.isEmpty()) {
            keys = sortHashMapByValues(User.getInstance().getChats());
        }*/
        adapter=new ChatListAdapter(keys);
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
                map=new HashMap<>(User.getInstance().getChats());
                //if(!map.isEmpty()){
                    //keys=new ArrayList<String>(map.keySet());
                    keys.clear();
                    keys = new ArrayList<String>( map.keySet());
                    //keys=sortHashMapByValues(User.getInstance().getChats());
                    adapter.notifyDataSetChanged();
                //}
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newMessageReceived,filter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }
   /* public List<String> sortHashMapByValues(HashMap<String, List<ChatMessage>> passedMap) {
        Map<LocalDateTime,String> hashMap=new HashMap<>();
        List<LocalDateTime>dateTimes=new ArrayList<>();
        for (Map.Entry entry: passedMap.entrySet()){
            List<ChatMessage> list=passedMap.get(entry.getKey());
            hashMap.put(LocalDateTime.parse(list.get(list.size()-1).getTime()),entry.getKey().toString());
            dateTimes.add(LocalDateTime.parse(list.get(list.size()-1).getTime()));
        }

        Collections.sort(dateTimes);
        List<String>sortedKeys=new ArrayList<>();
        for (int i= 0;i<dateTimes.size();++i){
            sortedKeys.add(hashMap.get(dateTimes.get(i)));
        }

        return sortedKeys;
    }
*/

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
