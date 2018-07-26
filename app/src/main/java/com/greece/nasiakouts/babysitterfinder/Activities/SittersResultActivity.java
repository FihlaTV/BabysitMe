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
import com.greece.nasiakouts.babysitterfinder.Utils.Constants;
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
    private Appointment appointment;
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

        appointment = intent.getParcelableExtra(Appointment.class.getName());

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
                    final AlertDialog alertDialog = new AlertDialog.Builder(SittersResultActivity.this)
                            .setView(R.layout.dialog_please_wait)
                            .setCancelable(false)
                            .show();

                    for (DataSnapshot sitter : snapshot.getChildren()) {
                        if (mAvailableSittersIds.contains(sitter.getKey())) {

                            Babysitter babysitter = sitter.getValue(Babysitter.class);
                            String key = sitter.getKey();
                            if (babysitter == null || key == null) {
                                continue;
                            }

                            if (appointment.getTotalKids() > babysitter.getMaxKids()) {
                                continue;
                            }
                            if (appointment.getMinAge() < babysitter.getMinAge()) {
                                continue;
                            }
                            if (appointment.getSitterSex() != Constants.USER_ANY
                                    && appointment.getSitterSex() != babysitter.getSexCode()) {
                                continue;
                            }

                            appointment.setTotalCostUsingPerHour(babysitter.getCharges());

                            mAvailableSitters.add(babysitter);
                            mAvailableSittersUidsParallel.add(key);
                        }
                    }
                    mAdapter.swapData(mAvailableSitters, mAvailableSittersUidsParallel);

                    alertDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void insertAppointmentsToDb(final String userId, String sitterId) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_please_wait)
                .setCancelable(false)
                .show();

        appointment.setCustomerId(userId);
        appointment.setSitterId(sitterId);
        storeAppointment(appointment);

        alertDialog.dismiss();

        Toast.makeText(getApplicationContext(),
                R.string.pending_toast,
                Toast.LENGTH_LONG).show();

        Intent goBackToLoggedInActivity =
                new Intent(SittersResultActivity.this, LoggedInActivity.class);
        goBackToLoggedInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goBackToLoggedInActivity);
    }

    private void storeAppointment(Appointment appointment) {
        final DatabaseReference mAppointmentDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_APPOINTMENTS);

        mAppointmentDatabaseReference.push().setValue(this.appointment);
    }
}
