package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greece.nasiakouts.babysitterfinder.Adapters.SittersResultRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Adapters.TimeSlotRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Appointment;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.greece.nasiakouts.babysitterfinder.Constants.ADD_TIMESLOT_REQUEST_CODE;

public class FindSitterActivity extends AppCompatActivity {

    @BindViews({R.id.total_kids,
            R.id.min_age,
            R.id.street_address})
    List<EditText> mNeedsInfoList;

    @BindView(R.id.radio_group_sitter_sex)
    RadioGroup mRadioGroupSitterSex;

    @BindView(R.id.needed_timeslots_rv)
    RecyclerView mNeededSitterSlotsRv;

    private TimeSlotRvAdapter mAdapter;
    private boolean fromRegistration = false;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_sitter);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.sitter_search);

        mAdapter = new TimeSlotRvAdapter(null);
        mNeededSitterSlotsRv.setAdapter(mAdapter);
        mNeededSitterSlotsRv.addItemDecoration(new DividerItemDecoration(mNeededSitterSlotsRv.getContext(),
                DividerItemDecoration.VERTICAL));
        mNeededSitterSlotsRv.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent == null) return;
        if (intent.hasExtra(Intent.EXTRA_TEXT) &&
                intent.getStringExtra(Intent.EXTRA_TEXT).equals(RegisterActivity.class.getName())) {
            fromRegistration = true;
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_SITTERS);
    }

    @Override
    public void onBackPressed() {
        if (fromRegistration) {
            Intent intent = new Intent(com.greece.nasiakouts.babysitterfinder.Activities.FindSitterActivity.this, LoggedInActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.cancel_button)
    public void cancelPressed() {
        onBackPressed();
    }

    @OnClick(R.id.ok_button)
    public void okPressed() {
        String totalKids = mNeedsInfoList.get(Constants.INDEX_TOTAL_KIDS_INPUT)
                .getText().toString();
        String youngestKidAge = mNeedsInfoList.get(Constants.INDEX_YOUNGEST_KID_INPUT)
                .getText().toString();
        String streetAddress = mNeedsInfoList.get(Constants.INDEX_STREET_INPUT)
                .getText().toString();

        if (TextUtils.isEmpty(totalKids)) {
            mNeedsInfoList.get(Constants.INDEX_TOTAL_KIDS_INPUT)
                    .setError(getString(R.string.total_kids));
            return;
        }

        if (TextUtils.isEmpty(youngestKidAge)) {
            mNeedsInfoList.get(Constants.INDEX_YOUNGEST_KID_INPUT)
                    .setError(getString(R.string.youngest_kid));
            return;
        }

        if (TextUtils.isEmpty(streetAddress)) {
            mNeedsInfoList.get(Constants.INDEX_STREET_INPUT)
                    .setError(getString(R.string.sitting_address));
            return;
        }

        if (mAdapter.getItemCount() == 0) {
            Toast.makeText(this, R.string.no_time_slot_added, Toast.LENGTH_LONG).show();
            return;
        }

        int selectedSex = mRadioGroupSitterSex.getCheckedRadioButtonId();

        if (selectedSex == -1) {
            Toast.makeText(this, R.string.not_filled_sitter_sex, Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedSex == R.id.radio_female) {
            selectedSex = Constants.INDEX_RADIO_FEMALE;
        } else if (selectedSex == R.id.radio_male) {
            selectedSex = Constants.INDEX_RADIO_MALE;
        } else {
            selectedSex = Constants.INDEX_RADIO_ANY;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // todo open login
            return;
        }
        String userId = firebaseUser.getUid();

        ArrayList<Appointment> temp = new ArrayList<>();
        for (TimeSlot timeSlot : mAdapter.getData()) {
            temp.add(new Appointment(Integer.parseInt(totalKids), Double.parseDouble(youngestKidAge),
                    streetAddress, timeSlot, selectedSex, userId));
        }

        final ArrayList<Appointment> appointments = temp;

        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_searching)
                .setCancelable(false)
                .show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FindSitterActivity.this, SittersResultActivity.class);
                intent.putExtra(Appointment.class.getName(), appointments);
                startActivity(intent);
            }
        }, 2000);
    }

    @OnClick(R.id.addNeededSlot)
    public void addNeededSlot() {
        Intent intent = new Intent(this, AddTimeSlotActivity.class);
        intent.putExtra(getString(R.string.interest_key), getString(R.string.user_value));
        startActivityForResult(intent, ADD_TIMESLOT_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TIMESLOT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mAdapter.insertTimeSlot((TimeSlot) data
                        .getSerializableExtra(TimeSlot.class.getName()));
            }
        }
    }

}
