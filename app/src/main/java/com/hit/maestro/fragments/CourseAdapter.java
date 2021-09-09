package com.hit.maestro.fragments;


import android.net.Uri;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hit.maestro.Course;
import com.hit.maestro.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    List<Course> course;
    private myCourseListener listener;


    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_cell_fragment,parent,false);
        CourseViewHolder courseViewHolder=new CourseViewHolder(view);
        return courseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course =this.course.get(position);
        holder.name.setText(course.getName());
        holder.lecturer.setText(course.getLecturer());
        Glide.with(holder.itemView).load(Uri.parse(course.getImage())).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return course.size();
    }

    interface myCourseListener{
        void onCourseClicked(int position, View view);
    }
    public void setListener(myCourseListener listener){
        this.listener=listener;
    }

    public CourseAdapter(List<Course> courses) {
        this.course = courses;
    }

  public static class CourseViewHolder extends RecyclerView.ViewHolder{
      ImageView image;
      TextView name;
      TextView lecturer;
      public CourseViewHolder(@NonNull View itemView) {
          super(itemView);
          image=itemView.findViewById(R.id.course_cell_image);
          name=itemView.findViewById(R.id.course_cell_name);
          lecturer=itemView.findViewById(R.id.course_cell_lecturer);
          itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  //listener.onCourseClicked(getAdapterPosition(),v);
              }
          });
      }
  }
}
