package com.hit.maestro.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.proxy.DatabaseProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    //List<List<ChatMessage>> conversations;
    List<String> keys;
    private myChatListener listener;

    public interface myChatListener{
        void onChatClicked(String UID, View view);
    }

    public void setListener(myChatListener listener){
        this.listener=listener;
    }

    public ChatListAdapter(List<String> keys) {
        this.keys=keys;
        Collections.reverse(this.keys);
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_cell,parent,false);
        ChatListViewHolder viewHolder=new ChatListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        List<ChatMessage>chatMessages= User.getInstance().getChatById(keys.get(position));
        holder.userName.setText(DatabaseProxy.getInstance().getUserName(keys.get(position)));
        Glide.with(holder.itemView).load(Uri.parse(DatabaseProxy.getInstance().getUserImageUri(keys.get(position)))).into(holder.imageView);
        holder.latestMessage.setText(chatMessages.get(chatMessages.size()-1).getMessage());
        if(!User.getInstance().isInNotifications(keys.get(position))){
            holder.notification.setVisibility(View.INVISIBLE);
        }
        else {
            holder.notification.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return keys.size();
    }




    public class ChatListViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView imageView;
        TextView userName;
        TextView latestMessage;
        ImageView notification;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.conv_image);
            userName=itemView.findViewById(R.id.user_conv_tv);
            notification=itemView.findViewById(R.id.notification_icon_iv);
            latestMessage=itemView.findViewById(R.id.last_message_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        listener.onChatClicked(keys.get(getAdapterPosition()),v);
                    }
                }
            });
        }
    }
}
