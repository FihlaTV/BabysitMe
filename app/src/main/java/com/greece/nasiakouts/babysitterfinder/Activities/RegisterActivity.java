package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greece.nasiakouts.babysitterfinder.Adapters.TimeSlotRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Controls.NoScrollViewPager;
import com.greece.nasiakouts.babysitterfinder.Fragments.AccountInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.PersonalInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.RegisterComponentFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.SitterAdditionalInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.WorkingInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.OnAddTimeSlotListener;
import com.greece.nasiakouts.babysitterfinder.OnUploadPhotoEvent;
import com.greece.nasiakouts.babysitterfinder.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity
        implements OnAddTimeSlotListener, OnUploadPhotoEvent{

    @BindView(R.id.register_view_pager)
    public NoScrollViewPager mViewPager;

    @BindView(R.id.previous_button)
    public Button mPreviousButton;

    @BindView(R.id.next_button)
    public Button mNextButton;

    private User mUser;
    private int selectedMode;
    private RegisterAdapter mAdapter;
    private ArrayList<RegisterComponentFragment> mFragments;

    private AlertDialog mSavingAlertDialog;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserTypesDatabaseReference;
    private DatabaseReference mSittersDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mWorkingDaysReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.registration);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(Constants.INT_CODE)){
            selectedMode = bundle.getInt(Constants.INT_CODE);
        }

        mAdapter = new RegisterAdapter(getSupportFragmentManager());
        mFragments = new ArrayList<>();

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        updateButtonsVisibility(0);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserTypesDatabaseReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_USER_TYPE);
        mSittersDatabaseReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_SITTERS);
        mUsersDatabaseReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_USERS);
        mWorkingDaysReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_WORKING_DAYS);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(getString(R.string.orientation_bundle_key),
                getResources().getConfiguration().orientation);
    }

    private void updateButtonsVisibility(int position) {
        if(position == 0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
        }
        else {
            if(position ==  mAdapter.getCount() - 1) {
                mNextButton.setText(R.string.done);
            }
            else {
                mNextButton.setText(R.string.next);
            }

            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.next_button)
    public void buttonNextPressed(View view){
        RegisterComponentFragment fragment = mFragments.get(mViewPager.getCurrentItem());

        User temp = fragment.getUser(mUser);
        if(temp == null) return;
        mUser = temp;

        if(fragment.getPosition() == mAdapter.getCount() - 1) {
            mSavingAlertDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_saving)
                    .setCancelable(false)
                    .show();

            mFirebaseAuth.createUserWithEmailAndPassword(mUser.getEmailAddress(), mUser.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (mSavingAlertDialog != null) {
                                mSavingAlertDialog.cancel();
                            }

                            if (task.isSuccessful()) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user == null) return;
                                String userId = user.getUid();
                                Intent intent;

                                if (selectedMode == R.id.radio_babysitter) {
                                    mUserTypesDatabaseReference.child(userId).setValue("sitter");
                                    for (TimeSlot timeSlot : ((Babysitter) mUser).getTimeSlots()) {
                                        String day = timeSlot.getDay();
                                        timeSlot.setDay(null);
                                        mWorkingDaysReference
                                                .child(day)
                                                .child(userId).setValue(timeSlot);
                                    }
                                    ((Babysitter) mUser).setTimeSlots(null);
                                    mSittersDatabaseReference.child(userId).setValue(mUser);

                                    intent = new Intent(RegisterActivity.this,
                                            MainActivity.class);

                                } else {
                                    mUsersDatabaseReference.child(userId).setValue(mUser);
                                    mUserTypesDatabaseReference.child(userId).setValue("user");

                                    intent = new Intent(RegisterActivity.this,
                                            FindSitterActivity.class);
                                    intent.putExtra(Intent.EXTRA_TEXT,
                                            RegisterActivity.class.getName());
                                }
                                startActivity(intent);
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.already_user,
                                            Toast.LENGTH_SHORT).show();

                                } else {
                                    if (task.getException() == null) return;

                                    Toast.makeText(getApplicationContext(),
                                            task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
        }

        mViewPager.setCurrentItem(fragment.getPosition() + 1, true);
        updateButtonsVisibility(fragment.getPosition() + 1);
    }

    @OnClick(R.id.previous_button)
    public void buttonPreviousPressed(View view){

        RegisterComponentFragment fragment = mFragments.get(mViewPager.getCurrentItem());

        // if we are in the first fragment and user press the invisible previous button, ignore it
        if(fragment.getPosition() == 0) {
            return;
        }

        mViewPager.setCurrentItem(fragment.getPosition() - 1, true);
        updateButtonsVisibility(fragment.getPosition() - 1);
    }

    TimeSlotRvAdapter mWorkingHoursAdapter;
    @Override
    public void addTimeSlot(TimeSlotRvAdapter adapter) {
        if(adapter == null) return;
        mWorkingHoursAdapter = adapter;
        Intent intent = new Intent(this, AddTimeSlotActivity.class);
        intent.putExtra(getString(R.string.interest_key), getString(R.string.sitter_value));
        startActivityForResult(intent, Constants.ADD_TIMESLOT_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ADD_TIMESLOT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mWorkingHoursAdapter.insertTimeSlot((TimeSlot)data
                        .getSerializableExtra(TimeSlot.class.getName()));
            }
        }

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    // TODO setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    // imageview.setImageURI(selectedImage);
                }
                break;
        }
    }

    @Override
    public void uploadPhoto(TextView textView, Button button) {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_upload_photo, null);

        final AlertDialog alertDialog =
                new AlertDialog.Builder(this)
                .setView(dialogView)
                        .create();

        final RadioGroup radioGroupInterest =
                dialogView.findViewById(R.id.radio_group_upload);

        radioGroupInterest.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                alertDialog.dismiss();

                int selectedRadioInterest =
                        radioGroupInterest.getCheckedRadioButtonId();

                if (selectedRadioInterest == -1) {
                    Toast.makeText(RegisterActivity.this,
                            R.string.no_selected_upload, Toast.LENGTH_LONG).show();
                    return;
                }

                if (selectedRadioInterest == R.id.radio_capture) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
            }
        });

        alertDialog.show();
    }

    private class RegisterAdapter extends FragmentPagerAdapter{
        RegisterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            RegisterComponentFragment fragment = null;
            if(position < mFragments.size()) return mFragments.get(position);

            switch (selectedMode){
                case R.id.radio_babysitter:
                    switch (position){
                        case 0:
                            fragment = new AccountInfoFragment();
                            break;
                        case 1:
                            fragment = new PersonalInfoFragment();
                            break;
                        case 2:
                            fragment = new WorkingInfoFragment();
                            break;
                        case 3:
                            fragment = new SitterAdditionalInfoFragment();
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    switch (position) {
                        case 0:
                            fragment = new AccountInfoFragment();
                            break;
                        case 1:
                            fragment = new PersonalInfoFragment();
                            break;
                        default:
                            break;
                    }
            }

            mFragments.add(position, fragment);

            return fragment;
        }

        @Override
        public int getCount(){
            switch (selectedMode){
                case R.id.radio_babysitter:
                    return 4;
                default:
                    return 2;
            }
        }
    }
}
