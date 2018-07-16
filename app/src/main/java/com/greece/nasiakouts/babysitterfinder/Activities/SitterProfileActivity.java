package com.greece.nasiakouts.babysitterfinder.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.greece.nasiakouts.babysitterfinder.R;

public class SitterProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitter_profile);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.profile);
    }
}
