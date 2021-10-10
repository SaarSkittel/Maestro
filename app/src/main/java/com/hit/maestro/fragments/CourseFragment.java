package com.hit.maestro.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.hit.maestro.Course;
import com.hit.maestro.R;
import com.hit.maestro.ReadAndWriteStorage;
import com.hit.maestro.User;
import com.hit.maestro.adapter.SubjectAdapter;

import static android.content.Context.MODE_PRIVATE;

public class CourseFragment extends Fragment {
    View view;
    ExpandableListView expandableListView;
    SubjectAdapter adapter;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.course_fragment, container, false);

        Course course = (Course) getArguments().getSerializable("Course");
        Toolbar toolbar = view.findViewById(R.id.curse_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        TextView title=view.findViewById(R.id.course_title);
        title.setText(course.getName());
        imageView=view.findViewById(R.id.course_image_iv);
        Glide.with(view).load(course.getImage()).centerCrop().into(imageView);
        adapter = new SubjectAdapter(getContext(), course.getSubjects());
        adapter.setListener(new SubjectAdapter.MyLessonListener() {
            @Override
            public void onLessonClicked(int childPosition, int groupPosition, View view) {
                if (User.getInstance().isConnected()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Lesson", course.getSubjects().get(groupPosition).getLessons().get(childPosition));
                    Navigation.findNavController(view).navigate(R.id.action_courseFragment_to_lessonFragment, bundle);
                } else {

                }
            }
        });
        expandableListView = view.findViewById(R.id.subject_elv);
        expandableListView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ReadAndWriteStorage.loadFromNotification(getActivity())){
            getActivity().onBackPressed();
        }
    }
}
