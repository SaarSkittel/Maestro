package com.hit.maestro.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hit.maestro.R;
import com.hit.maestro.Reaction;

public class ReactionFragment extends Fragment {
    public ReactionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        View root = inflater.inflate(R.layout.reaction_fragment,container,false);

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    public static ReactionFragment newInstance(Reaction reaction){

        ReactionFragment ReactionFragment = new ReactionFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name",reaction.getName());
        bundle.putString("image",reaction.getImage());
        bundle.putString("comment",reaction.getComment());
        bundle.putInt("rating",reaction.getRating());

        ReactionFragment.setArguments(bundle);
        return  ReactionFragment;
    }
}
