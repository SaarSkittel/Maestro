package com.hit.maestro.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.hit.maestro.ChatMessage;
import com.hit.maestro.R;
import com.hit.maestro.proxy.DatabaseProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder>{
    List<HashMap<String,String>> userList;

    private UserListAdapter.myUserListener listener;

    public interface myUserListener{
        void onChatClicked(int position, View view);
    }

    public void setListener(UserListAdapter.myUserListener listener){
        this.listener=listener;
    }


    public UserListAdapter(List<HashMap<String, String>> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_cell,parent,false);
        UserListViewHolder viewHolder=new UserListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        HashMap<String,String>user=userList.get(position);
        holder.userName.setText(user.get("name"));
        Glide.with(holder.itemView).load(Uri.parse(user.get("image"))).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView imageView;
        TextView userName;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.user_image);
            userName=itemView.findViewById(R.id.username_tv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        listener.onChatClicked(getAdapterPosition(),v);
                    }
                }
            });
        }
    }
}
