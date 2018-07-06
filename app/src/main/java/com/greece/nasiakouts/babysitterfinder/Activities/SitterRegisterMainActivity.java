package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.greece.nasiakouts.babysitterfinder.Adapters.WorkingHoursRvAdapter;
import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SitterRegisterMainActivity extends AppCompatActivity {

    @BindView(R.id.working_slots_rv)
    RecyclerView workingSlotsRvl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitter_register_main);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.registration);

        WorkingHoursRvAdapter adapter = new WorkingHoursRvAdapter(null);
        workingSlotsRvl.setAdapter(adapter);

/*
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses =
                    geoCoder.getFromLocationName(travel.getAddress() + "," +
                            travel.getCity() + "," + travel.getState() + "," + travel.getCountry(), 3);

            if (addresses.size() > 0) {
                point = new GeoPoint(
                        (int) (addresses.get(0).getLatitude() * 1E6),
                        (int) (addresses.get(0).getLongitude() * 1E6));
                travel.setLatitude(String.valueOf(point.getLatitudeE6()));
                travel.setLatitude(String.valueOf(point.getLongitudeE6()));
                long res = travel.update(context, null);
                if (res < 1){
                    result = false;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }*/
    }

    @OnClick(R.id.addWorkingSlot)
    public void addWorkingSlot(View view){
        Intent intent = new Intent(this, AddTimeSlotActivity.class);
        intent.putExtra(getString(R.string.interest_key), getString(R.string.sitter_value));
        startActivity(intent);

        /*
        boolean wrapInScrollView = true;
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.add_new_timeslot_title)
                .customView(R.layout.custom_view, wrapInScrollView)
                .positiveText(R.string.set_possitive_dialog)
                .show()
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        View view = dialog.getCustomView();
                    }

        });*/




    }
}
