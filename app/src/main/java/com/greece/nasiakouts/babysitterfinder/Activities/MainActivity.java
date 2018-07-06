package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.log_in_wrapper_layout)
    public void logIn(View view) {
        // TODO ...
    }

    @OnClick(R.id.register_wrapper_layout)
    public void register(View view) {
        Intent intent = new Intent(this, RegistrationCommonActivity.class);
        startActivity(intent);
    }
}
