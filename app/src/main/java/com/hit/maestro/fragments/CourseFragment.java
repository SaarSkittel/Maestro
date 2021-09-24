package com.hit.maestro.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.hit.maestro.Course;
import com.hit.maestro.R;
import com.hit.maestro.User;
import com.hit.maestro.adapter.SubjectAdapter;

import static android.content.Context.MODE_PRIVATE;

public class CourseFragment extends Fragment {
    View view;
    ExpandableListView expandableListView;
    SubjectAdapter adapter;
    TextView title;
    SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.course_fragment,container,false);
        sp = this.getActivity().getSharedPreferences("login_status", MODE_PRIVATE);
        title = view.findViewById(R.id.course_title);
        //Boolean connectedStatus = sp.getBoolean("status",false);
        title.setText(User.getInstance().isConnected()?"hello " + User.getInstance().getFullName():"Guest mode");
        Course course =(Course) getArguments().getSerializable("Course");
        adapter=new SubjectAdapter(getContext(),course.getSubjects());
        adapter.setListener(new SubjectAdapter.MyLessonListener() {
            @Override
            public void onLessonClicked(int childPosition, int groupPosition, View view) {
                if(User.getInstance().isConnected()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Lesson", course.getSubjects().get(groupPosition).getLessons().get(childPosition));
                    Navigation.findNavController(view).navigate(R.id.action_courseFragment_to_lessonFragment, bundle);
                }
                else{

                }
            }
        });
        expandableListView=view.findViewById(R.id.subject_elv);
        expandableListView.setAdapter(adapter);
        return view;
    }
}
