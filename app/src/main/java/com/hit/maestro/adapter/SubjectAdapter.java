package com.hit.maestro.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.hit.maestro.Lesson;
import com.hit.maestro.R;
import com.hit.maestro.Subject;

import java.util.List;


public class SubjectAdapter extends BaseExpandableListAdapter {
    List<Subject> subjects;
    private LayoutInflater inflater;
    private Context context;
    private MyLessonListener listener;


    public interface MyLessonListener{
        void onLessonClicked( int childPosition,int groupPosition, View view);
    }

    public void setListener(MyLessonListener listener) {
        this.listener = listener;
    }


    public SubjectAdapter(Context context, List<Subject> subjects) {
        this.context=context;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.subjects = subjects;
    }

    @Override
    public int getGroupCount() {
        return subjects.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return subjects.get(groupPosition).getLessons().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return subjects.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return subjects.get(groupPosition).getLessons().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.subject_item,null);
        }
        TextView subjectTV= (TextView)convertView.findViewById(R.id.subject_tv);
        Subject subject=(Subject)getGroup(groupPosition);
        subjectTV.setText(subject.getNameSubject());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Lesson lesson= (Lesson)getChild(groupPosition,childPosition);

        if(convertView==null){
           convertView=inflater.inflate(R.layout.lesson_item,null);
            View finalConvertView = convertView;
            TextView lessonTV =(TextView)convertView.findViewById(R.id.lesson_tv);
            lessonTV.setText(lesson.getNameLesson());
            lessonTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        listener.onLessonClicked(childPosition,groupPosition, finalConvertView);
                    }
                }
            });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
