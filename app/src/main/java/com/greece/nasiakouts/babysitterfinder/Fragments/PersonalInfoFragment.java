package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class PersonalInfoFragment extends RegisterComponentFragment
        implements View.OnFocusChangeListener, View.OnTouchListener{

    @BindViews({R.id.name_input,
                R.id.phone_input,
            R.id.date_born_input})
    List<TextInputEditText> mPersonalInfoList;

    @BindViews({R.id.name_input_wrapper,
            R.id.phone_input_wrapper,
            R.id.date_born_input_wrapper})
    List<TextInputLayout> mPersonalInfoWrapperList;

    @BindView(R.id.radio_group_sex)
    RadioGroup mRadioGroupSex;

    @BindView(R.id.radio_female)
    RadioButton mFemale_radio_button;

    @BindView(R.id.radio_male)
    RadioButton mMale_radio_button;

    private long mDateRepresentation = -1;
    private DatePickerDialog mDateDialog = null;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal_info, container, false);
        ButterKnife.bind(this, root);

        mPersonalInfoList.get(Constants.INDEX_DATE_BORN_INPUT).setOnFocusChangeListener(this);
        mFemale_radio_button.setOnTouchListener(this);
        mMale_radio_button.setOnTouchListener(this);

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDateRepresentation != -1) {
            outState.putLong(Date.class.getName(), mDateRepresentation);
        }
        if (mDateDialog != null && mDateDialog.isShowing()) {
            outState.putBoolean(DatePickerDialog.class.getName(), true);
        } else {
            outState.putBoolean(DatePickerDialog.class.getName(), false);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState == null) return;
        if (savedInstanceState.containsKey(Date.class.getName())) {
            mDateRepresentation = savedInstanceState.getLong(Date.class.getName());
        }
        if (savedInstanceState.containsKey(DatePickerDialog.class.getName())) {
            if (savedInstanceState.getBoolean(DatePickerDialog.class.getName())) {
                openDatePicker();
            }
        }
    }

    @Override
    public User getUser(User user) {
        if(user == null) return null;

        // region Get User Inputs
        String fullName = mPersonalInfoList.get(Constants.INDEX_NAME_INPUT)
                .getText().toString();
        String phoneNumber = mPersonalInfoList.get(Constants.INDEX_PHONE_INPUT)
                .getText().toString();
        String dateBorn = mPersonalInfoList.get(Constants.INDEX_DATE_BORN_INPUT)
                .getText().toString();
        int sex = mRadioGroupSex.getCheckedRadioButtonId();
        // endregion

        if (areValidAndFilled(fullName, phoneNumber, dateBorn, mDateRepresentation, sex)) {
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setDateBornTimestamp(mDateRepresentation);
            user.setSexCode(sex);
            return user;
        }

        return null;
    }

    private boolean areValidAndFilled(String fullName,
                                      String phoneNumber,
                                      String dateBorn,
                                      long dateBornLongRepresentation,
                                      int sex) {
        if(TextUtils.isEmpty(fullName)) {
            mPersonalInfoWrapperList.get(Constants.INDEX_NAME_INPUT)
                    .setError(getString(R.string.not_filled_name));
            return false;
        }

        if(TextUtils.isEmpty(phoneNumber)) {
            mPersonalInfoWrapperList.get(Constants.INDEX_PHONE_INPUT)
                    .setError(getString(R.string.not_filled_phone));
            return false;
        }

        if(phoneNumber.length() < 4
                || phoneNumber.length() > 13
                || !android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            mPersonalInfoWrapperList.get(Constants.INDEX_PHONE_INPUT)
                    .setError(getString(R.string.no_valid_phone));
            return false;
        }

        if (TextUtils.isEmpty(dateBorn)) {
            mPersonalInfoWrapperList.get(Constants.INDEX_DATE_BORN_INPUT)
                    .setError(getString(R.string.not_filled_born_date));
            return false;
        }

        if (dateBornLongRepresentation == -1) {
            mPersonalInfoWrapperList.get(Constants.INDEX_DATE_BORN_INPUT)
                    .setError(getString(R.string.not_filled_born_date));
            return false;
        }

        if(sex == -1) {
            if(getContext() != null)
                Toast.makeText(getContext(),
                        R.string.no_sex_selected, Toast.LENGTH_LONG).show();

            mFemale_radio_button.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public int getPosition() {
        return Constants.PERSONAL_INFO_FRAGMENT_SEQ;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus) {
            openDatePicker();
        }
    }

    private void openDatePicker() {
        Calendar mCurrentDate = Calendar.getInstance();

        if (getContext() == null) return;
        if (mDateDialog == null) {
            mDateDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {

                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, monthOfYear);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mDateRepresentation = cal.getTime().getTime();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.US);

                            mPersonalInfoList.get(Constants.INDEX_DATE_BORN_INPUT)
                                    .setText(sdf.format(cal.getTime()));

                            mFemale_radio_button.requestFocus();
                        }
                    }, mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                    mCurrentDate.get(Calendar.DAY_OF_MONTH));
            mDateDialog.setCancelable(false);
            mDateDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    mFemale_radio_button.requestFocus();
                }
            });

            // Any user either a regular one or sitter has to be at least 16 years old
            // so thus why we are setting the max date limit to the date picker
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, mCurrentDate.get(Calendar.YEAR) - Constants.MIN_AGE_OF_USER);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, Calendar.SUNDAY);
            cal.set(Calendar.DAY_OF_MONTH, Constants.DAYS_IN_DECEMBER);
            mDateDialog.getDatePicker().setMaxDate(cal.getTime().getTime());
        }
        mDateDialog.show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.performClick();
        return false;
    }
}
