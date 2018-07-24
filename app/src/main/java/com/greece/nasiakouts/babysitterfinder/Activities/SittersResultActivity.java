package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greece.nasiakouts.babysitterfinder.Adapters.SittersResultRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Appointment;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SittersResultActivity extends AppCompatActivity {

    @BindView(R.id.available_sitters_rv)
    RecyclerView mAvailableSittersRv;

    private SittersResultRvAdapter mAdapter;
    private ArrayList<Appointment> mAppointments;
    private ArrayList<String> mAvailableSittersIds;
    private ArrayList<Babysitter> mAvailableSitters;
    private ArrayList<String> mAvailableSittersUidsParallel;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitters_result);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.sitters_results);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mAdapter = new SittersResultRvAdapter(this, null, null);
        mAvailableSittersRv.setAdapter(mAdapter);
        mAvailableSittersRv.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent == null) return;
        if (intent.getExtras() == null) return;
        if (!intent.getExtras().containsKey(FindSitterActivity.class.getName())) return;
        if (!intent.getExtras().containsKey(Appointment.class.getName())) return;

        mAvailableSittersIds = intent
                .getExtras().getStringArrayList(FindSitterActivity.class.getName());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_SITTERS);


        mAvailableSitters = new ArrayList<>();
        mAvailableSittersUidsParallel = new ArrayList<>();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot sitter : snapshot.getChildren()) {
                        if (mAvailableSittersIds.contains(sitter.getKey())) {
                            mAvailableSitters.add(sitter.getValue(Babysitter.class));
                            mAvailableSittersUidsParallel.add(sitter.getKey());
                        }
                    }
                    mAdapter.swapData(mAvailableSitters, mAvailableSittersUidsParallel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAppointments = (ArrayList<Appointment>) intent.getSerializableExtra(Appointment.class.getName());
    }

    public void insertAppointmentsToDb(final String userId, String sitterId) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_please_wait)
                .setCancelable(false)
                .show();

        for (Appointment appointment : mAppointments) {
            appointment.setCustomerId(userId);
            appointment.setSitterId(sitterId);
            storeAppointment(appointment);
        }

        alertDialog.dismiss();

        Toast.makeText(getApplicationContext(),
                "Appointment Arranged! Please wait for sitters confirmation",
                Toast.LENGTH_LONG).show();

        Intent goBackToLoggedInActivity =
                new Intent(SittersResultActivity.this, LoggedInActivity.class);
        goBackToLoggedInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goBackToLoggedInActivity);
    }

    private void storeAppointment(Appointment appointment) {
        final DatabaseReference mAppointmentDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_APPOINTMENTS)
                .child(appointment.getSlot().getDay())
                .child(appointment.getSitterId());

        appointment.getSlot().setDay(null);
        appointment.setSitterId(null);

        mAppointmentDatabaseReference.setValue(mAppointments);
    }
}
