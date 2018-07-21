package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greece.nasiakouts.babysitterfinder.Adapters.SittersResultRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Constants;
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
    ArrayList<String> availableSittersIds;
    private ArrayList<Babysitter> availableSitters;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitters_result);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.sitters_results);

        mAdapter = new SittersResultRvAdapter(this, null);
        mAvailableSittersRv.setAdapter(mAdapter);
        mAvailableSittersRv.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent == null) return;
        if (intent.getExtras() == null) return;
        if (!intent.getExtras().containsKey(FindSitterActivity.class.getName())) return;

        availableSittersIds = intent
                .getExtras().getStringArrayList(FindSitterActivity.class.getName());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_SITTERS);


        availableSitters = new ArrayList<>();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot sitter : snapshot.getChildren()) {
                    if (availableSittersIds.contains(sitter.getKey())) {
                        availableSitters.add(sitter.getValue(Babysitter.class));
                    }
                }
                mAdapter.swapData(availableSitters);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAppointment = (Appointment) intent.getSerializableExtra(Appointment.class.getName());

    }

    public void insertAppointmentToDb(final String userId, String sitterEmail) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_please_wait)
                .setCancelable(false)
                .show();

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mMapDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_SITTER_ADDRESS_ID_MAP)
                .child(sitterEmail);

        final DatabaseReference mAppointmentDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_APPOINTMENTS);

        mMapDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String sitterId = dataSnapshot.getChildren().iterator()
                            .next().getValue(String.class);

                    if (sitterId == null) return;

                    mAppointment.setCustomerId(userId);
                    mAppointment.setSitterId(sitterId);

                    mAppointmentDatabaseReference.child(mAppointment.getSlot().getDay()).child(sitterId);
                    mAppointment.getSlot().setDay(null);
                    mAppointment.setSitterId(null);
                    mAppointmentDatabaseReference.setValue(mAppointment);
                    alertDialog.dismiss();

                    Toast.makeText(getApplicationContext(),
                            "Appointment Arranged! Please wait for sitters confirmation",
                            Toast.LENGTH_LONG).show();

                    Intent goBackToLoggedInActivity =
                            new Intent(SittersResultActivity.this, LoggedInActivity.class);
                    goBackToLoggedInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(goBackToLoggedInActivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
