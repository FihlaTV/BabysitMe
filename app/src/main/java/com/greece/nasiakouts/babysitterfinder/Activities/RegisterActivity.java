package com.greece.nasiakouts.babysitterfinder.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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

    private Uri imageUri;

    private User mUser;
    private int selectedMode;
    private RegisterAdapter mAdapter;
    private RegisterComponentFragment[] mFragments;

    private AlertDialog mSavingAlertDialog;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserTypesDatabaseReference;
    private DatabaseReference mSittersDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mWorkingDaysReference;

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private AlertDialog photoUploadAlertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.registration);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null
                && bundle.containsKey(Constants.INT_CODE)){
            selectedMode = bundle.getInt(Constants.INT_CODE);
        }
        else if(savedInstanceState != null
                && savedInstanceState.containsKey(Constants.INT_CODE)) {
            selectedMode = savedInstanceState.getInt(Constants.INT_CODE);
        }

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

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();


        mAdapter = new RegisterAdapter(getSupportFragmentManager());
        mFragments = new RegisterComponentFragment[mAdapter.getCount()];

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());

        if(savedInstanceState == null){
            mViewPager.setCurrentItem(0);
            updateButtonsVisibility(0);
        }
        else {
            if(savedInstanceState.containsKey(User.class.getName())
                    && savedInstanceState.containsKey(ViewPager.class.getName())){
                mUser = (User) savedInstanceState.getSerializable(User.class.getName());
                int posRestored = savedInstanceState.getInt(ViewPager.class.getName());
                mViewPager.setCurrentItem(posRestored);
                updateButtonsVisibility(posRestored);
            }
            else {
                Toast.makeText(getApplicationContext(),
                        R.string.error_occurred,
                        Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.INT_CODE, selectedMode);
        outState.putSerializable(User.class.getName(), mUser);
        outState.putInt(ViewPager.class.getName(), mViewPager.getCurrentItem());

        if(photoUploadAlertDialog != null
                && photoUploadAlertDialog.isShowing()) {
            outState.putBoolean(getString(R.string.upload_photo_si_key), true);
        }
        else {
            outState.putBoolean(getString(R.string.upload_photo_si_key), false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) return;

        if(savedInstanceState.getBoolean(getString(R.string.upload_photo_si_key),
                false)){
            showUploadPhotoDialog();
        }
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
        if (mFragments.length < mViewPager.getCurrentItem()) return;

        RegisterComponentFragment fragment = mFragments[mViewPager.getCurrentItem()];
        if (fragment == null) return;

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
                                uploadPhotoToCloudStorage(imageUri);

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
                                                .child(userId)
                                                .push()
                                                .setValue(timeSlot);
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
        if (mFragments.length < mViewPager.getCurrentItem()) return;

        RegisterComponentFragment fragment = mFragments[mViewPager.getCurrentItem()];
        if (fragment == null) return;

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
            case Constants.TAKE_PHOTO_REQUEST_CODE:
            case Constants.FROM_GALLERY_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    if (data.getData() != null) {
                        imageUri = data.getData();
                        if (mFragments[mViewPager.getCurrentItem()]
                                instanceof SitterAdditionalInfoFragment) {
                            ((SitterAdditionalInfoFragment) mFragments[mViewPager.getCurrentItem()])
                                    .setHasSelectedPhoto(true);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void showUploadPhotoDialog() {
        if(photoUploadAlertDialog == null){
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_upload_photo, null);

            photoUploadAlertDialog =
                    new AlertDialog.Builder(this)
                            .setView(dialogView)
                            .create();

            final RadioGroup radioGroupInterest =
                    dialogView.findViewById(R.id.radio_group_upload);

            radioGroupInterest.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    photoUploadAlertDialog.dismiss();

                    int selectedRadioInterest =
                            radioGroupInterest.getCheckedRadioButtonId();

                    if (selectedRadioInterest == -1) {
                        Toast.makeText(RegisterActivity.this,
                                R.string.no_selected_upload, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (selectedRadioInterest == R.id.radio_capture) {
                        File photoFile = createImageFile();
                        if (photoFile != null) {
                            imageUri = FileProvider.getUriForFile(RegisterActivity.this,
                                    "com.greece.nasiakouts.babysitterfinder.provider", photoFile);

                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(takePicture, Constants.TAKE_PHOTO_REQUEST_CODE);
                        }
                    } else {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, Constants.FROM_GALLERY_REQUEST_CODE);
                    }
                }
            });

        }
        photoUploadAlertDialog.show();
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File image = null;
        try {
            image = File.createTempFile(
                    "image_" + timeStamp,
                    ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private void uploadPhotoToCloudStorage(Uri imagePath) {
        if (imagePath == null) return;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user == null) return;

        StorageReference ref = mStorageReference.child("images/" + user.getUid());
        ref.putFile(imagePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        int progress = (int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + progress + "%");
                    }
                });
    }

    private RegisterComponentFragment getNewFragment(int position) {
        RegisterComponentFragment fragment = null;
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
        return fragment;
    }

    private class RegisterAdapter extends FragmentPagerAdapter {
        RegisterAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            RegisterComponentFragment fragment = mFragments[position];
            if (fragment != null) return fragment;

            mFragments[position] = getNewFragment(position);
            return mFragments[position];
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RegisterComponentFragment fragment =
                    (RegisterComponentFragment) super.instantiateItem(container, position);

            mFragments[position] = fragment;

            return fragment;
        }

        @Override
        public int getCount() {
            switch (selectedMode) {
                case R.id.radio_babysitter:
                    return 4;
                default:
                    return 2;
            }
        }
    }
}