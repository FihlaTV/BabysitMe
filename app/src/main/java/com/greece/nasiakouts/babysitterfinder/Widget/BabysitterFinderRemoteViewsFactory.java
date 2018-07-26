package com.greece.nasiakouts.babysitterfinder.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.greece.nasiakouts.babysitterfinder.Models.Appointment;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;
import com.greece.nasiakouts.babysitterfinder.Utils.Constants;

import java.util.ArrayList;

public class BabysitterFinderRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    boolean lock = true;
    private ArrayList<Appointment> dataList = new ArrayList<>();
    private String groupPref;
    private Context context = null;
    private int appWidgetId;

    public BabysitterFinderRemoteViewsFactory(Context context, Intent intent) {
        if (context == null || intent == null) return;

        this.context = context;

        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        groupPref = intent.getStringExtra(String.class.getName());

        loadAppointmentsFromFirebase(groupPref);

        while (lock) {

        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        loadAppointmentsFromFirebase(groupPref);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews
     *
     */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.babysitter_finder_widget_list_item);

        TimeSlot timeSlot = dataList.get(position).getSlot();
        remoteView.setTextViewText(R.id.day, timeSlot.getDay());
        remoteView.setTextViewText(R.id.specific_date_or_every_week,
                timeSlot.isForever() ?
                        context.getString(R.string.each_week) :
                        timeSlot.getSpecificDay());

        remoteView.setTextViewText(R.id.time_period,
                timeSlot.isAllDay() ?
                        context.getString(R.string.all_day) :
                        timeSlot.getTimeFrom() + Constants.DASH + timeSlot.getTimeTo());

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    private void loadAppointmentsFromFirebase(String groupPref) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) return;
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference typeDatabaseReference = firebaseDatabase.getReference()
                .child(Constants.FIREBASE_USER_ALL_INFO)
                .child(Constants.FIREBASE_USER_TYPE)
                .child(firebaseUser.getUid());
        typeDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue(String.class);
                if (userType == null) return;

                DatabaseReference reference = firebaseDatabase.getReference()
                        .child(Constants.FIREBASE_APPOINTMENTS);
                Query query = reference
                        .orderByChild(Constants.FIREBASE_SITTER_ID)
                        .equalTo(firebaseUser.getUid());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Appointment appointment = dataSnapshot.getValue(Appointment.class);
                                dataList.add(appointment);
                            }
                            lock = false;
                            Intent triggerUpdateIntent =
                                    new Intent(context, BabisitterFinderWidget.class);
                            triggerUpdateIntent.setAction(Constants.FETCHED_DATA_ACTION);
                            context.sendBroadcast(triggerUpdateIntent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
