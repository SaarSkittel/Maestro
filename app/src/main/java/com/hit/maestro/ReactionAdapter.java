package com.hit.maestro;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder> {
  private List<Reaction> Reactions;
  private myReactionListener listener;
     interface myReactionListener {
         void onReactionClicked(int position, View view);

     }
    public void setListener(myReactionListener listener){
        this.listener=listener;
    }
    public ReactionAdapter(List<Reaction> reactions) {
        Reactions = reactions;
    }

    @NonNull
    @Override
    public ReactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reaction_cell,parent,false);
        ReactionViewHolder ReactionViewHolder = new ReactionViewHolder(view);
        return ReactionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReactionViewHolder holder, int position) {
        Reaction reaction = Reactions.get(position);
        Bitmap bitmap=StringToBitMap(reaction.getImage());

        holder.imageIV.setImageBitmap(bitmap);
        holder.commentTV.setText(reaction.getComment());
        holder.nameTV.setText(reaction.getName());
        holder.ratingTV.setText(reaction.getRating());
    }
    public static Bitmap StringToBitMap(String image){
        try{
            byte [] encodeByte= Base64.decode(image,Base64.DEFAULT);

            InputStream inputStream  = new ByteArrayInputStream(encodeByte);
            Bitmap bitmap  = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    @Override
    public int getItemCount() {
        return Reactions.size();
    }

    public class ReactionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIV;
        TextView  nameTV;
        TextView  commentTV;
        TextView ratingTV;
        public ReactionViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIV= itemView.findViewById(R.id.cell_img);
            nameTV= itemView.findViewById(R.id.cell_writerName_TV);
            commentTV= itemView.findViewById(R.id.cell_comment_TV);
            ratingTV = itemView.findViewById(R.id.cell_rating_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        listener.onReactionClicked(getAdapterPosition(),v);
                    }
                }
            });

        }
    }
}