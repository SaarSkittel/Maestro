package com.hit.maestro.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hit.maestro.ChatMessage;
import com.hit.maestro.Course;
import com.hit.maestro.Lesson;
import com.hit.maestro.R;
import com.hit.maestro.ReadAndWriteStorage;
import com.hit.maestro.User;
import com.hit.maestro.adapter.ChatAdapter;
import com.hit.maestro.proxy.DatabaseProxy;
import com.hit.maestro.proxy.MessagingProxy;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LessonFragment extends Fragment {
    View view;
    YouTubePlayerView youTubePlayer;
    RecyclerView recyclerView;
    ChatAdapter adapter;
    BroadcastReceiver newMessageReceived;
    List<ChatMessage> chatMessages;
    ImageButton button;
    EditText text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.lesson_fragment,container,false);
        Toolbar toolbar = view.findViewById(R.id.lesson_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
        Lesson lesson =(Lesson) getArguments().getSerializable("Lesson");
        text=view.findViewById(R.id.text_send);
        button=view.findViewById(R.id.btn_send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!text.getText().toString().isEmpty()) {
                    MessagingProxy.SendMessageTo(lesson.getChatTitle(), text.getText().toString(), false, getContext());
                    text.setText("");
                }
            }
        });
        youTubePlayer = view.findViewById(R.id.youtube_player_view);
        if(DatabaseProxy.getInstance().getLessonChats().containsKey(lesson.getChatTitle())) {
            chatMessages = new ArrayList<>(DatabaseProxy.getInstance().getLessonChatById(lesson.getChatTitle()));
        }
        else {
            chatMessages = new ArrayList<>();
        }

        adapter=new ChatAdapter(chatMessages);

        recyclerView=view.findViewById(R.id.lesson_chat_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
        recyclerView.setAdapter(adapter);

        IntentFilter filter=new IntentFilter("lesson_message_received");
        newMessageReceived=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMessages.clear();
                chatMessages.addAll(DatabaseProxy.getInstance().getLessonChats().get(lesson.getChatTitle()));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
            }
        };
        LocalBroadcastManager.getInstance(view.getContext()).registerReceiver(newMessageReceived,filter);


        String videoId = lesson.getUrl();
        youTubePlayer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
                youTubePlayer.pause();
            }
        });

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
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(view.getContext()).unregisterReceiver(newMessageReceived);
        youTubePlayer.getYouTubePlayerWhenReady(YouTubePlayer::pause);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(ReadAndWriteStorage.loadFromNotification(getActivity())){
            getActivity().onBackPressed();
        }
        youTubePlayer.getYouTubePlayerWhenReady(YouTubePlayer::play);
    }
}
