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
import com.greece.nasiakouts.babysitterfinder.Utils.Constants;
import com.greece.nasiakouts.babysitterfinder.Controls.NoScrollViewPager;
import com.greece.nasiakouts.babysitterfinder.Fragments.AccountInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.PersonalInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.RegisterComponentFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.SitterAdditionalInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Fragments.WorkingInfoFragment;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.Interfaces.OnAddTimeSlotListener;
import com.greece.nasiakouts.babysitterfinder.Interfaces.OnUploadPhotoEvent;
import com.greece.nasiakouts.babysitterfinder.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
    private int mSelectedMode;
    private RegisterAdapter mAdapter;
    private RegisterComponentFragment[] mFragments;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserTypesDatabaseReference;
    private DatabaseReference mSittersDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mWorkingDaysReference;

    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;

    private AlertDialog mSavingAlertDialog;
    private AlertDialog mPhotoUploadAlertDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar()
                .setTitle(R.string.registration);

        // region Detect mode running
        Bundle bundle = getIntent().getExtras();
        if(bundle != null
                && bundle.containsKey(Constants.INT_CODE)){
            mSelectedMode = bundle.getInt(Constants.INT_CODE);
        }
        else if(savedInstanceState != null
                && savedInstanceState.containsKey(Constants.INT_CODE)) {
            mSelectedMode = savedInstanceState.getInt(Constants.INT_CODE);
        }
        // endregion

        // region Firebase References
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
        // endregion

        // region ViewPager things
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
        // endregion
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.INT_CODE, mSelectedMode);
        outState.putSerializable(User.class.getName(), mUser);
        outState.putInt(ViewPager.class.getName(), mViewPager.getCurrentItem());

        if (mPhotoUploadAlertDialog != null
                && mPhotoUploadAlertDialog.isShowing()) {
            mPhotoUploadAlertDialog.dismiss();
            outState.putBoolean(AlertDialog.class.getName(), true);
        } else {
            outState.putBoolean(AlertDialog.class.getName(), false);
        }

        // todo handle rotation on saving ?
        if (mSavingAlertDialog != null
                && mSavingAlertDialog.isShowing()) {
            mSavingAlertDialog.dismiss();
            outState.putBoolean(FirebaseDatabase.class.getName(), true);
        }
        else {
            outState.putBoolean(FirebaseDatabase.class.getName(), false);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) return;

        if (savedInstanceState.getBoolean(AlertDialog.class.getName(),
                false)) {
            showUploadPhotoDialog();
        }

        if (savedInstanceState.getBoolean(FirebaseDatabase.class.getName(),
                false)){
            showUploadPhotoDialog();
        }
    }

    // region ViewPager Navigation Stuff
    private void updateButtonsVisibility(int position) {
        if(position == 0) {
            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.INVISIBLE);
        } else {
            if(position ==  mAdapter.getCount() - 1) {
                mNextButton.setText(R.string.done);
            } else {
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
        // No all fields filled and valid, so do not go to next fragment
        if(temp == null) return;
        mUser = temp;

        // region Registration Completed
        if(fragment.getPosition() == mAdapter.getCount() - 1) {
            showSavingAlertDialog();

            mFirebaseAuth.createUserWithEmailAndPassword(mUser.getEmailAddress(), mUser.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // todo giati cancel??
                            if (mSavingAlertDialog != null) {
                                mSavingAlertDialog.cancel();
                            }

                            if (task.isSuccessful()) {

                                // region Store to Firebase RealTime Database
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user == null) return;
                                String userId = user.getUid();
                                Intent intent;

                                if (mSelectedMode == R.id.radio_babysitter) {

                                    uploadPhotoToCloudStorage(imageUri);

                                    mUserTypesDatabaseReference
                                            .child(userId)
                                            .setValue(Constants.FIREBASE_SITTERS);

                                    int count = 0;
                                    for (TimeSlot timeSlot : ((Babysitter) mUser).getTimeSlots()) {
                                        String day = timeSlot.getDay();

                                        String timeSlotUid = userId +
                                                Constants.UNDERSCORE + (++count);

                                        mWorkingDaysReference
                                                .child(day)
                                                .child(timeSlotUid)
                                                .setValue(timeSlot);
                                    }
                                    ((Babysitter) mUser).setTimeSlots(null);
                                    mSittersDatabaseReference.child(userId).setValue(mUser);

                                    // todo open profile loggin?
                                    intent = new Intent(RegisterActivity.this,
                                            MainActivity.class);

                                } else {
                                    mUserTypesDatabaseReference
                                            .child(userId)
                                            .setValue(Constants.FIREBASE_USERS);

                                    mUsersDatabaseReference
                                            .child(userId)
                                            .setValue(mUser);

                                    intent = new Intent(RegisterActivity.this,
                                            FindSitterActivity.class);
                                    intent.putExtra(Intent.EXTRA_TEXT,
                                            RegisterActivity.class.getName());
                                }
                                // endregion

                                startActivity(intent);

                            } else {
                                if (task.getException()
                                        instanceof FirebaseAuthUserCollisionException) {
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
        // endregion

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
    // endregion

    private void showSavingAlertDialog() {
        if (mSavingAlertDialog == null) {
            mSavingAlertDialog = new AlertDialog.Builder(this)
                    .setView(R.layout.dialog_saving)
                    .setCancelable(false)
                    .show();
        } else {
            mSavingAlertDialog.show();
        }
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
                TimeSlot timeSlot = data.getParcelableExtra(TimeSlot.class.getName());
                mWorkingHoursAdapter.insertTimeSlot(timeSlot);
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

    // region Photo Selection and Upload Stuff
    @Override
    public void showUploadPhotoDialog() {
        if (mPhotoUploadAlertDialog == null) {
            final View dialogView = getLayoutInflater().inflate(R.layout.dialog_upload_photo, null);

            mPhotoUploadAlertDialog =
                    new AlertDialog.Builder(this)
                            .setView(dialogView)
                            .create();

            final RadioGroup radioGroupInterest =
                    dialogView.findViewById(R.id.radio_group_upload);

            radioGroupInterest.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    mPhotoUploadAlertDialog.dismiss();

                    int selectedRadioInterest =
                            radioGroupInterest.getCheckedRadioButtonId();

                    if (selectedRadioInterest == -1) {
                        Toast.makeText(RegisterActivity.this,
                                R.string.no_selected_upload, Toast.LENGTH_LONG).show();
                        return;
                    }

                    // region Create Intent for Photo
                    if (selectedRadioInterest == R.id.radio_capture) {
                        File photoFile = createImageFile();
                        if (photoFile != null) {
                            imageUri = FileProvider.getUriForFile(RegisterActivity.this,
                                    Constants.AUTHORITY, photoFile);

                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(takePicture, Constants.TAKE_PHOTO_REQUEST_CODE);
                        }
                    } else {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, Constants.FROM_GALLERY_REQUEST_CODE);
                    }
                    // endregion
                }
            });

        }
        mPhotoUploadAlertDialog.show();
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File image = null;
        try {
            image = File.createTempFile(
                    Constants.IMAGE_FILE_PREFIX + timeStamp,
                    Constants.IMAGE_FILE_SUFFIX,
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    private void uploadPhotoToCloudStorage(Uri imagePath) {
        if (imagePath == null) return;

        // todo handle rotation on this ??
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.uploading_title));
        progressDialog.show();

        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user == null) return;

        StorageReference ref = mStorageReference
                .child(Constants.STORAGE_IMAGES_PATH + user.getUid());
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
                        progressDialog.setMessage(getString(R.string.uploaded) +
                                progress + Constants.PERCENTAGE);
                    }
                });
    }
    // endregion

    private RegisterComponentFragment getNewFragment(int position) {
        RegisterComponentFragment fragment = null;
        switch (mSelectedMode) {
            case R.id.radio_babysitter:
                switch (position){
                    case Constants.ACCOUNT_INFO_FRAGMENT_SEQ:
                        fragment = new AccountInfoFragment();
                        break;
                    case Constants.PERSONAL_INFO_FRAGMENT_SEQ:
                        fragment = new PersonalInfoFragment();
                        break;
                    case Constants.WORKING_INFO_FRAGMENT_SEQ:
                        fragment = new WorkingInfoFragment();
                        break;
                    case Constants.SITTER_ADDITIONAL_INFO_FRAGMENT_SEQ:
                        fragment = new SitterAdditionalInfoFragment();
                        break;
                    default:
                        break;
                }
                break;
            default:
                switch (position) {
                    case Constants.ACCOUNT_INFO_FRAGMENT_SEQ:
                        fragment = new AccountInfoFragment();
                        break;
                    case Constants.PERSONAL_INFO_FRAGMENT_SEQ:
                        fragment = new PersonalInfoFragment();
                        break;
                    default:
                        break;
                }
        }
        return fragment;
    }

    // region PagerAdapter Stuff
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
            switch (mSelectedMode) {
                case R.id.radio_babysitter:
                    return Constants.VIEW_PAGER_FRAGMENTS_FOR_SITTER;
                default:
                    return Constants.VIEW_PAGER_FRAGMENTS_FOR_USER;
            }
        }
    }
    // endregion
}