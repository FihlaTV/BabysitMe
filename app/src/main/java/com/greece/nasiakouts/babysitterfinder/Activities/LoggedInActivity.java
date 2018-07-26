package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.greece.nasiakouts.babysitterfinder.Utils.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Appointment;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoggedInActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.profile)
    ImageView sitterAction;
    @BindView(R.id.find_sitter)
    ImageView userAction;

    private User mUser;
    private int mMode = -1;
    private HashMap<String, ArrayList<Appointment>> appointments = new HashMap<>();

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAppointmentsDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        ButterKnife.bind(this);

        getWindow().setStatusBarColor(Color.parseColor("#00796B"));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAppointmentsDatabaseReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_APPOINTMENTS);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getApplicationContext(),
                    R.string.diconect, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoggedInActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }
        String userId = firebaseUser.getUid();

        Intent intent = getIntent();
        if(intent == null) return;
        mUser = (User)intent.getSerializableExtra(User.class.getName());
        if(mUser == null) return;

        Query query;
        if(mUser instanceof Babysitter) {
            mMode = Constants.SITTER_MODE;
            query = mAppointmentsDatabaseReference
                    .orderByChild(Constants.FIREBASE_SITTER_ID).equalTo(userId);
        }
        else {
            mMode = Constants.USER_MODE;
            query = mAppointmentsDatabaseReference
                    .orderByChild(Constants.FIREBASE_USER_ID).equalTo(userId);
        }

        if (savedInstanceState == null) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Welcome " + mUser.getFullName() + "!",
                    Snackbar.LENGTH_LONG).show();

            setSupportActionBar(toolbar);
        }

        appointments.put(Constants.HASHMAP_APPOINTMENTS_WEEKLY, new ArrayList<Appointment>());
        appointments.put(Constants.HASHMAP_APPOINTMENT_SIMPLE, new ArrayList<Appointment>());
        appointments.put(Constants.HASHMAP_APPOINTMENT_PENDING, new ArrayList<Appointment>());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Appointment appointment = snapshot.getValue(Appointment.class);
                        if (appointment == null) continue;

                        if (appointment.getSlot().isForever()) {
                            appointments
                                    .get(Constants.HASHMAP_APPOINTMENTS_WEEKLY)
                                    .add(appointment);
                            continue;
                        }

                        if (appointment.isAcceptedBySitter()) {
                            appointments
                                    .get(Constants.HASHMAP_APPOINTMENT_SIMPLE)
                                    .add(appointment);
                            continue;
                        }

                        appointments
                                .get(Constants.HASHMAP_APPOINTMENT_PENDING)
                                .add(appointment);
                    }

                    System.out.print("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ..
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) return;
        else if (savedInstanceState.containsKey(getString(R.string.mode_save_inst_key))) {
            mMode = savedInstanceState.getInt(getString(R.string.mode_save_inst_key));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMode == Constants.SITTER_MODE) {
            sitterAction.setVisibility(View.VISIBLE);
            userAction.setVisibility(View.GONE);
            getSupportActionBar().setTitle(R.string.sitter_mng);
            userAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    findSitter();
                }
            });

        } else if (mMode == Constants.USER_MODE) {
            sitterAction.setVisibility(View.GONE);
            userAction.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(R.string.user_mng);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(getString(R.string.mode_save_inst_key), mMode);
    }

    @OnClick(R.id.find_sitter)
    public void findSitter() {
        Intent intent = new Intent(LoggedInActivity.this,
                FindSitterActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,
                LoggedInActivity.class.getName());
        startActivity(intent);
    }
}