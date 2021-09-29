package com.hit.maestro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hit.maestro.R;
import com.hit.maestro.adapter.ViewPagerAdapter;

public class ChatFragment extends androidx.fragment.app.Fragment {
    View view;
    ViewPagerAdapter adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    ViewPagerAdapter viewPagerAdapter;

    private String[] titles=new String[]{"Chats","Contacts"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.chat_fragment,container,false);
        viewPager=view.findViewById(R.id.chat_vp);
        tabLayout=view.findViewById(R.id.chat_tl);
        viewPagerAdapter=new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatListFragment(),"Chats");
        viewPagerAdapter.addFragment(new UserListFragment(),"Contacts");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}
