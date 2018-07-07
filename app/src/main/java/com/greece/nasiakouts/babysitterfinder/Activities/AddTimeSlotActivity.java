package com.greece.nasiakouts.babysitterfinder.Activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;
import com.greece.nasiakouts.babysitterfinder.Utils;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.greece.nasiakouts.babysitterfinder.Constants.ANOKATOTELEIA;

public class AddTimeSlotActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    Activity currentActivity;

    @BindView(R.id.from_hour_input)
    EditText fromHourEditText;

    @BindView(R.id.to_hour_input)
    EditText toHourEditText;

    @BindView(R.id.stub_edittext)
    EditText stubEditText;

    @BindView(R.id.day_spinner)
    Spinner daySpinner;

    @BindView(R.id.all_day_checkbox)
    CheckBox allDayCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_slot);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.add_new_timeslot_title);

        currentActivity = this;

        fromHourEditText.setOnFocusChangeListener(this);
        toHourEditText.setOnFocusChangeListener(this);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                R.array.days, R.layout.spinner_layout_override);
        daySpinner.setAdapter(adapter);

        allDayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    fromHourEditText.setText("");
                    toHourEditText.setText("");
                }
            }
        });
    }

    @Override
    public void onFocusChange(final View view, boolean hasFocus) {
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

            TimePickerDialog mTimePicker = new TimePickerDialog(AddTimeSlotActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            viewHavingFocus.setText(new StringBuilder()
                                    .append(selectedHour)
                                    .append(ANOKATOTELEIA)
                                    .append(selectedMinute)
                                    .toString());

                            allDayCheckBox.setChecked(false);
                            Utils.hideKeyboard(currentActivity);
                            stubEditText.requestFocus();
                        }
                    }, hour, minute, true);
            mTimePicker.setTitle(prompt);
            mTimePicker.setCancelable(false);
            mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Utils.hideKeyboard(currentActivity);
                    stubEditText.requestFocus();
                }
            });
            mTimePicker.show();
        }
    }

    @OnClick(R.id.ok_button)
    public void okPressed(View view) {
        if (daySpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, getString(R.string.day_toast), Toast.LENGTH_LONG).show();
            return;
        }

        String daySelected = daySpinner.getSelectedItem().toString();
        String fromHour = fromHourEditText.getText().toString();
        String toHour = toHourEditText.getText().toString();

        if ((TextUtils.isEmpty(fromHour) || TextUtils.isEmpty(toHour))
                && !allDayCheckBox.isChecked()) {
            Toast.makeText(this, R.string.timeslot_toast, Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, R.string.timeslot_added, Toast.LENGTH_LONG).show();

        TimeSlot timeSlot;
        if (allDayCheckBox.isChecked()) {
            timeSlot = new TimeSlot(daySelected, fromHour, toHour, true);
        } else {
            timeSlot = new TimeSlot(daySelected, fromHour, toHour, false);
        }

        Intent intent = new Intent();
        intent.putExtra(TimeSlot.class.getName(), timeSlot);
        setResult(RESULT_OK, intent);
        finish();
    }
}
