package com.hit.maestro;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.hit.maestro.fragments.ReactionFragment;

import java.util.List;

public class ReactionAdapter extends FragmentStatePagerAdapter {
    List<Reaction> reactionlist;

    public ReactionAdapter(@NonNull FragmentManager fm, int behavior, Reaction reaction) {
        super(fm, behavior);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {


        return ReactionFragment.newInstance(reactionlist.get(position));
    }

    @Override
    public int getCount() {
        return reactionlist.size();

    }
}
