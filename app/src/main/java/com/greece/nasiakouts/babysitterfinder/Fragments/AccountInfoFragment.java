package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.ButterKnife;

public class AccountInfoFragment extends RegisterComponentFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.account_info_fragment, container, false);

        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public User getUser() {
        return null;
    }
}
