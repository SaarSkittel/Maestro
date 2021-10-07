package com.hit.maestro.fragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import androidx.navigation.Navigation;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hit.maestro.R;
import com.hit.maestro.ReadAndWriteStorage;
import com.hit.maestro.adapter.ViewPagerAdapter;

public class ChatFragment extends androidx.fragment.app.Fragment {
    View view;
    ViewPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;
    BroadcastReceiver newMessageReceived;
    private String[] titles = new String[]{"Chats", "Contacts"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_fragment, container, false);
        NotificationManager notifManager= (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        Toolbar toolbar = view.findViewById(R.id.chat_toolbar);
        Intent intent = new Intent("notification_cancel");
        intent.putExtra("UID","");
        intent.putExtra("isInChatFragment",true);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.white_back);
        viewPager = view.findViewById(R.id.chat_vp);
        tabLayout = view.findViewById(R.id.chat_tl);
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new ChatListFragment(), getResources().getString(R.string.chats));
        viewPagerAdapter.addFragment(new UserListFragment(), getResources().getString(R.string.contacts));
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ReadAndWriteStorage.loadFromNotification(getActivity())){
            ReadAndWriteStorage.setFromNotificationStorage(getActivity(),false);
        }
        Intent intent = new Intent("notification_cancel");
        intent.putExtra("UID","");
        intent.putExtra("isInChatFragment",true);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(view.getContext()).unregisterReceiver(newMessageReceived);
        Intent intent = new Intent("notification_cancel");
        intent.putExtra("UID","");
        intent.putExtra("isInChatFragment",false);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
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


}
