package com.hit.maestro.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.User;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    List<ChatMessage>chatMessages;

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==2){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_sender_cell,parent,false);
        }
        else{
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_receiver_cell,parent,false);
        }
        ChatViewHolder chatViewHolder= new ChatAdapter.ChatViewHolder(view,viewType);
        return chatViewHolder ;
    }

    @Override
    public int getItemViewType(int position) {
        Integer viewType;
        if(chatMessages.get(position).getUID().matches(User.getInstance().getUID())) {
            viewType = 1;
        }
        else viewType=2;
        return viewType;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = this.chatMessages.get(position);
        holder.text.setText(chatMessage.getMessage());
        Glide.with(holder.itemView).load(Uri.parse(chatMessage.getImage())).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    public ChatAdapter(List<ChatMessage>chatMessages){this.chatMessages=chatMessages;}

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        MaterialTextView text;
        ShapeableImageView image;
        public ChatViewHolder(@NonNull View itemView,int viewType) {
            super(itemView);
            if(viewType==2){
                text =(MaterialTextView) itemView.findViewById(R.id.sender_text);
                image= itemView.findViewById(R.id.sender_image);

            }
            else {
                text = itemView.findViewById(R.id.receiver_text);
                image=itemView.findViewById(R.id.receiver_image);
            }

        }
    }


}
