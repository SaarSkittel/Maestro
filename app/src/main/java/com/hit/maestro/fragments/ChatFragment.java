package com.hit.maestro.fragments;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hit.maestro.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    View view;
    ChatAdapter adapter;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.chat_fragment,container,false);
        List<ChatMessage>chatMessages=new ArrayList<ChatMessage>();
        chatMessages.add(new ChatMessage("hello","me",1,"android.resource://com.hit.maestro/drawable/electric_guitar"));
        chatMessages.add(new ChatMessage("hello2","me",2,"android.resource://com.hit.maestro/drawable/electric_guitar"));
        adapter=new ChatAdapter(chatMessages);
        recyclerView=view.findViewById(R.id.chat_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
        return view;

    }
}
