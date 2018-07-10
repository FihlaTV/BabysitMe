package com.greece.nasiakouts.babysitterfinder.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.ButterKnife;

public class SittersResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitters_result);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.sitters_results);
    }
}
