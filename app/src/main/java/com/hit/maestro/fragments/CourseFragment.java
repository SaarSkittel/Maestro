package com.hit.maestro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hit.maestro.Course;
import com.hit.maestro.R;
import com.hit.maestro.adapter.SubjectAdapter;

public class CourseFragment extends Fragment {
    View view;
    ExpandableListView expandableListView;
    SubjectAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.course_fragment,container,false);
        Course course =(Course) getArguments().getSerializable("Course");
        adapter=new SubjectAdapter(getContext(),course.getSubjects());
        adapter.setListener(new SubjectAdapter.MyLessonListener() {
            @Override
            public void onLessonClicked(int childPosition, int groupPosition, View view) {
                Bundle bundle= new Bundle();
                bundle.putSerializable("Lesson",course.getSubjects().get(groupPosition).getLessons().get(childPosition));
                Navigation.findNavController(view).navigate(R.id.action_courseFragment_to_lessonFragment,bundle);
            }
        });
        expandableListView=view.findViewById(R.id.subject_elv);
        expandableListView.setAdapter(adapter);
        return view;
    }
}
