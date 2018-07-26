package com.greece.nasiakouts.babysitterfinder.Activities;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;
import com.greece.nasiakouts.babysitterfinder.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTimeSlotActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    @BindView(R.id.specific_date)
    EditText specificDateEditText;

    @BindView(R.id.day_spinner)
    Spinner daySpinner;

    @BindView(R.id.from_hour_input)
    EditText fromHourEditText;

    @BindView(R.id.to_hour_input)
    EditText toHourEditText;

    @BindView(R.id.stub_edittext)
    EditText stubEditText;

    @BindView(R.id.all_day_checkbox)
    CheckBox allDayCheckBox;

    @BindView(R.id.weekly_checkbox)
    CheckBox weeklyCheckbox;

    @BindView(R.id.weekly_section)
    LinearLayout weeklySection;

    private int fromHour;
    private int fromMin;
    private int toHour;
    private int toMin;
    private Date mDateSelected = null;
    private int mMode = -1;

    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private int mTimeViewFocused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time_slot);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.add_time_slot_title);

        Intent openingIntent = getIntent();

        if (openingIntent == null) return;

        if (openingIntent.getExtras() != null
                && openingIntent.getExtras().containsKey(getString(R.string.interest_key))) {
            String whoOpenedMe = (String) openingIntent.getExtras().get(getString(R.string.interest_key));
            if (whoOpenedMe != null && whoOpenedMe.equals(getString(R.string.user_value))) {
                weeklySection.setVisibility(View.VISIBLE);
                specificDateEditText.setVisibility(View.VISIBLE);
                daySpinner.setVisibility(View.GONE);

                mMode = Constants.USER_MODE;

                specificDateEditText.setOnFocusChangeListener(this);
            } else {
                weeklySection.setVisibility(View.GONE);
                specificDateEditText.setVisibility(View.GONE);
                daySpinner.setVisibility(View.VISIBLE);

                mMode = Constants.SITTER_MODE;

                ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
                        R.array.days, R.layout.spinner_layout_override);
                daySpinner.setAdapter(adapter);
            }
        }

        fromHourEditText.setOnFocusChangeListener(this);
        toHourEditText.setOnFocusChangeListener(this);

        allDayCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    fromHourEditText.setText("");
                    toHourEditText.setText("");
                }
            }
        });

        weeklyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (mDateSelected == null) return;
                    SimpleDateFormat onlyDay = new SimpleDateFormat("EEEE", Locale.US);
                    specificDateEditText.setText(onlyDay.format(mDateSelected));
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDatePickerDialog != null && mDatePickerDialog.isShowing()) {
            mDatePickerDialog.dismiss();
            outState.putBoolean(DatePickerDialog.class.getName(), true);
        }
        if (mTimePickerDialog != null && mTimePickerDialog.isShowing()) {
            mTimePickerDialog.dismiss();
            outState.putBoolean(TimePickerDialog.class.getName(), true);
            outState.putInt(AddTimeSlotActivity.class.getName(), mTimeViewFocused);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState == null) return;
        if (savedInstanceState.containsKey(DatePickerDialog.class.getName())) {
            if (savedInstanceState.getBoolean(DatePickerDialog.class.getName())) {
                openDatePicker();
            }
        }
        if (savedInstanceState.containsKey(TimePickerDialog.class.getName())
                && savedInstanceState.containsKey(AddTimeSlotActivity.class.getName())) {
            if (savedInstanceState.getBoolean(TimePickerDialog.class.getName())) {
                openTimePicker(savedInstanceState.getInt(AddTimeSlotActivity.class.getName()));
            }
        }
    }

    // region EditTexts with Pickers Handling
    @Override
    public void onFocusChange(final View view, boolean hasFocus) {
        if(hasFocus) {
            final int viewHavingFocusId = view.getId();

            if (viewHavingFocusId == R.id.specific_date) {
                openDatePicker();
            } else {
                openTimePicker(viewHavingFocusId);
            }
        }
    }

    private void openDatePicker() {
        if (mDatePickerDialog == null) {
            Calendar cal = Calendar.getInstance();

            mDatePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {

                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, monthOfYear);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mDateSelected = cal.getTime();

                            SimpleDateFormat onlyDate =
                                    new SimpleDateFormat(Constants.PATTERN_FULL_DATE_FORMAT,
                                            Locale.US);

                            specificDateEditText
                                    .setText(onlyDate.format(mDateSelected));

                            stubEditText.requestFocus();
                            Utils.hideKeyboard(AddTimeSlotActivity.this);
                            weeklyCheckbox.setChecked(false);
                        }
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            mDatePickerDialog.setCancelable(false);
            mDatePickerDialog.setTitle(R.string.sitting_date);
            mDatePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    stubEditText.requestFocus();
                    Utils.hideKeyboard(AddTimeSlotActivity.this);
                }
            });

            // Can not set time slot in the past
            mDatePickerDialog.getDatePicker().setMinDate(cal.getTime().getTime());
        }

        mDatePickerDialog.show();
    }

    private void openTimePicker(final int viewHavingFocusId) {
        mTimeViewFocused = viewHavingFocusId;
        if (mTimePickerDialog == null) {
            Calendar time = Calendar.getInstance();

            mTimePickerDialog = new TimePickerDialog(AddTimeSlotActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker,
                                              int selectedHour, int selectedMinute) {

                            if (viewHavingFocusId == R.id.to_hour_input) {
                                selectedHour = selectedHour == 0 ? 24 : selectedHour;
                            }

                            EditText viewHavingFocus;

                            if (viewHavingFocusId == R.id.from_hour_input) {
                                viewHavingFocus = fromHourEditText;
                                fromHour = selectedHour;
                                fromMin = selectedMinute;

                            } else {
                                viewHavingFocus = toHourEditText;
                                toHour = selectedHour;
                                toMin = selectedMinute;
                            }

                            SimpleDateFormat onlyTime =
                                    new SimpleDateFormat(Constants.PATTERN_HOUR_FORMAT, Locale.US);

                            Calendar time = Calendar.getInstance();
                            time.set(Calendar.HOUR_OF_DAY, selectedHour);
                            time.set(Calendar.MINUTE, selectedMinute);

                            viewHavingFocus.setText(onlyTime.format(time.getTime()));

                            allDayCheckBox.setChecked(false);
                            Utils.hideKeyboard(AddTimeSlotActivity.this);
                            stubEditText.requestFocus();
                        }
                    }, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), true);
            mTimePickerDialog.setTitle(viewHavingFocusId == R.id.from_hour_input ? getString(R.string.from_hour_hint) : getString(R.string.to_hour_hint));
            mTimePickerDialog.setCancelable(false);
            mTimePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Utils.hideKeyboard(AddTimeSlotActivity.this);
                    stubEditText.requestFocus();
                }
            });
        }

        mTimePickerDialog.show();
    }
    // endregion

    @OnClick(R.id.cancel_button)
    public void cancelPressed(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @OnClick(R.id.ok_button)
    public void okPressed(View view) {
        if (areAllFilledAndValid()) {

            SimpleDateFormat onlyDay =
                    new SimpleDateFormat(Constants.PATTERN_ALL_DAY_FORMAT, Locale.US);

            TimeSlot timeSlot = new TimeSlot(
                    mMode == Constants.USER_MODE ?
                            onlyDay.format(mDateSelected) : daySpinner.getSelectedItem().toString(),
                    allDayCheckBox.isChecked(),
                    mMode != Constants.USER_MODE || weeklyCheckbox.isChecked(),
                    allDayCheckBox.isChecked() ?
                            -1 : Double.parseDouble(fromHourEditText.getText()
                            .toString().replace(Constants.ANOKATOTELEIA, Constants.DOT)),
                    allDayCheckBox.isChecked() ?
                            -1 : Double.parseDouble(toHourEditText.getText()
                            .toString().replace(Constants.ANOKATOTELEIA, Constants.DOT)),
                    mMode == Constants.USER_MODE ?
                            specificDateEditText.getText().toString() : Constants.DASH
            );

            // getIsForever parameter exmplained
            // if we are user mode thus the new timeslot is being added while searching
            // for babysitter we give the value the user provided. if we are on sitter mode,
            // thus the new timeslot is being added while setting the sitter's working hours,
            // the getIsForever checkbox is invisible and we have the isforeever true by default
            // because it is ok to assume that a babysitter will provide their working hours in general
            // and not exceptions. In any case that's how the app works. if the user for some reason,
            // can't babysit one monday for example even though she or he states it as her or his working
            // hours, is ok since whenever a user tries to arrange a babysitting the sitter will
            // have to confirm it.

            Toast.makeText(getApplicationContext(),
                    R.string.timeslot_added,
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.putExtra(TimeSlot.class.getName(), timeSlot);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean areAllFilledAndValid() {
        switch (mMode) {
            case Constants.USER_MODE:
                if (TextUtils.isEmpty(specificDateEditText.getText().toString())) {
                    Toast.makeText(this,
                            getString(R.string.day_toast),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                break;
            default:
                if (daySpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(this,
                            getString(R.string.day_toast),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                break;
        }

        if (TextUtils.isEmpty(fromHourEditText.getText().toString())
                && !allDayCheckBox.isChecked()) {
            fromHourEditText.setError(getString(R.string.not_filled_from));
            return false;
        }

        if (TextUtils.isEmpty(toHourEditText.getText().toString())
                && !allDayCheckBox.isChecked()) {
            toHourEditText.setError(getString(R.string.not_filled_to));
            return false;
        }

        if (!allDayCheckBox.isChecked() &&
                (fromHour > toHour || (fromHour == toHour && fromMin > toMin))) {
            Toast.makeText(AddTimeSlotActivity.this,
                    getString(R.string.to_hour_less_than),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
