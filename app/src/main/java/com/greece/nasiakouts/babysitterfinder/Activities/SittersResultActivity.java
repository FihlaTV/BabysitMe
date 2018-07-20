package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.greece.nasiakouts.babysitterfinder.Adapters.SittersResultRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Models.Appointment;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SittersResultActivity extends AppCompatActivity {

    @BindView(R.id.available_sitters_rv)
    RecyclerView mAvailableSittersRv;

    private SittersResultRvAdapter mAdapter;
    private Appointment mAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitters_result);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.sitters_results);

        Intent intent = getIntent();
        if (intent == null) return;

        mAppointment = (Appointment) intent.getSerializableExtra(Appointment.class.getName());



        mAdapter = new SittersResultRvAdapter(this, null);
        mAvailableSittersRv.setAdapter(mAdapter);
        mAvailableSittersRv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void insertAppointmentToDb(String userId, String sitterEmail) {
        // todo
    }
}
