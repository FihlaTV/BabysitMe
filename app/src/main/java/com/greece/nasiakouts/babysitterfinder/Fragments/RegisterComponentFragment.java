package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.support.v4.app.Fragment;

import com.greece.nasiakouts.babysitterfinder.Models.User;

public abstract class RegisterComponentFragment extends Fragment {
    public abstract User getUser();
    public abstract int getPosition();
}
