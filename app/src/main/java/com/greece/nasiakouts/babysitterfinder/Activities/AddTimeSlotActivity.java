package com.greece.nasiakouts.babysitterfinder.Activities;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.greece.nasiakouts.babysitterfinder.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTimeSlotActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    @BindView(R.id.from_hour_input)
    EditText fromHourEditText;

    @BindView(R.id.to_hour_input)
    EditText toHourEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_slot);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.add_new_timeslot_title);

        fromHourEditText.setOnFocusChangeListener(this);
        toHourEditText.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus) {
            int viewHavingFocusId = view.getId();
            final EditText viewHavingFocus;
            String prompt;
            if(viewHavingFocusId == R.id.from_hour_input) {
                viewHavingFocus = fromHourEditText;
                prompt = getString(R.string.from_hour_hint);
            }
            else {
                viewHavingFocus = toHourEditText;
                prompt = getString(R.string.to_hour_hint);
            }

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);

            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(AddTimeSlotActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            viewHavingFocus.setText(new StringBuilder()
                                    .append(selectedHour)
                                    .append(":")
                                    .append(selectedMinute)
                                    .toString());
                        }
                    }, hour, minute, true);
            mTimePicker.setTitle(prompt);
            mTimePicker.show();
        }
    }
}
