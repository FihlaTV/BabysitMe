package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.Babysitter;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

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
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private String mRegisteredUid;
    private User mUser;
    private int mMode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(LoggedInActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }

        mRegisteredUid = firebaseUser.getUid();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String type = dataSnapshot
                        .child(Constants.FIREBASE_USER_TYPE)
                        .child(mRegisteredUid)
                        .getValue(String.class);

                if (type.equals(Constants.FIREBASE_SITTERS)) {
                    mMode = Constants.SITTER_MODE;

                    Babysitter sitter = dataSnapshot
                            .child(type)
                            .child(mRegisteredUid)
                            .getValue(Babysitter.class);

                    mUser = sitter;
                } else {
                    mMode = Constants.USER_MODE;

                    mUser = dataSnapshot
                            .child(type)
                            .child(mRegisteredUid)
                            .getValue(User.class);
                }

                Snackbar.make(findViewById(android.R.id.content),
                        "Welcome " + mUser.getFullName() + "!",
                        Snackbar.LENGTH_LONG).show();

                setSupportActionBar(toolbar);
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void findSitter() {
        Intent intent = new Intent(LoggedInActivity.this,
                FindSitterActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,
                LoggedInActivity.class.getName());
    }
}
