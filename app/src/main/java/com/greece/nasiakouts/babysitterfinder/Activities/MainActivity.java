package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.greece.nasiakouts.babysitterfinder.Constants.INT_CODE;
import static com.greece.nasiakouts.babysitterfinder.PermissionUtils.checkAndAskForPermissions;
import static com.greece.nasiakouts.babysitterfinder.PermissionUtils.onPermissionResult;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.register_now_tv)
    TextView mRegister;

    @BindView(R.id.reset_tv)
    TextView mReset;

    @BindView(R.id.log_email_address)
    EditText mEmailAddress;

    @BindView(R.id.log_password)
    EditText mPassword;

    AlertDialog mSavingAlertDialog;
    FirebaseAuth mFirebaseAuth;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mSittersDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null
                && savedInstanceState
                .containsKey(getString(R.string.reset_visibility_key))) {
            mReset.setVisibility(savedInstanceState
                    .getInt(getString(R.string.reset_visibility_key)));
        }

        checkAndAskForPermissions(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mSittersDatabaseReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_SITTERS);
        mUsersDatabaseReference = mFirebaseDatabase
                .getReference()
                .child(Constants.FIREBASE_USERS);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // todo
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // todo
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mSittersDatabaseReference.addChildEventListener(mChildEventListener);
        mUsersDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull
            String[] permissions, @NonNull int[] grantResults) {
        onPermissionResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRegister.setTextColor(ContextCompat
                .getColor(MainActivity.this, R.color.secondary_text));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(getString(R.string.reset_visibility_key), mReset.getVisibility());
    }

    @OnClick(R.id.log_in_button)
    public void logIn(View view) {
        if (TextUtils.isEmpty(mEmailAddress.getText().toString())) {
            mEmailAddress.setError(getString(R.string.not_filled_email_address));
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(mEmailAddress.getText().toString()).matches()) {
            mEmailAddress.setError(getString(R.string.no_valid_email));
            return;
        }

        if (TextUtils.isEmpty(mPassword.getText().toString())) {
            mPassword.setError(getString(R.string.not_filled_password));
        }

        mSavingAlertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_saving)
                .setCancelable(false)
                .show();

        mFirebaseAuth.signInWithEmailAndPassword(mEmailAddress.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (mSavingAlertDialog != null) {
                            mSavingAlertDialog.cancel();
                        }

                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
                            if (currentUser == null) return;

                            String RegisteredUserID = currentUser.getUid();

                            // DatabaseReference userType = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_USER_TYPE).child(RegisteredUserID);

                            Toast.makeText(getApplicationContext(),
                                    mFirebaseAuth.getCurrentUser().getEmail(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            if (task.getException() == null) return;
                            Toast.makeText(getApplicationContext(),
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            mReset.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @OnClick(R.id.reset_tv)
    public void resetPassword() {
        mFirebaseAuth.sendPasswordResetEmail(mEmailAddress.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.reset_mail_toast,
                                    Toast.LENGTH_SHORT).show();
                            mReset.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @OnClick(R.id.register_now_tv)
    public void register(View view) {
        mRegister.setTextColor(ContextCompat.getColor(this, R.color.hyperlink_color));

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_selection, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.mode_prompt)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioGroup radioGroupInterest =
                                dialogView.findViewById(R.id.radio_group_interest);

                        int selectedRadioInterest =
                                radioGroupInterest.getCheckedRadioButtonId();

                        if(selectedRadioInterest == -1) {
                            Toast.makeText(MainActivity.this,
                                    R.string.no_register_mode_selected, Toast.LENGTH_LONG).show();
                            mRegister.setTextColor(ContextCompat
                                    .getColor(MainActivity.this, R.color.secondary_text));
                            return;
                        }

                        Intent intent =
                                new Intent(MainActivity.this, RegisterActivity.class);
                        intent.putExtra(INT_CODE, selectedRadioInterest);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mRegister.setTextColor(ContextCompat
                                .getColor(MainActivity.this, R.color.secondary_text));
                    }
                })
                .show();
    }
}
