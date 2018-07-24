package com.greece.nasiakouts.babysitterfinder.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.Models.User;
import com.greece.nasiakouts.babysitterfinder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class PersonalInfoFragment extends RegisterComponentFragment
        implements View.OnFocusChangeListener, View.OnTouchListener{


    @BindViews({R.id.name_input,
                R.id.phone_input,
                R.id.year_input})
    List<TextInputEditText> mPersonalInfList;

    @BindView(R.id.radio_group_sex)
    RadioGroup mRadioGroupSex;

    @BindView(R.id.radio_female)
    RadioButton mFemale_radio_button;

    @BindView(R.id.radio_male)
    RadioButton mMale_radio_button;

    private Date mDateRepresentation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personal_info, container, false);

        ButterKnife.bind(this, root);

        mPersonalInfList.get(Constants.INDEX_DATE_BORN_INPUT).setOnFocusChangeListener(this);
        mFemale_radio_button.setOnTouchListener(this);
        mMale_radio_button.setOnTouchListener(this);

        return root;
    }

    @Override
    public User getUser(User user) {
        if(user == null) return null;

        String fullName = mPersonalInfList.get(Constants.INDEX_NAME_INPUT)
                .getText().toString();
        String phoneNumber = mPersonalInfList.get(Constants.INDEX_PHONE_INPUT)
                .getText().toString();
        String dateBorn = mPersonalInfList.get(Constants.INDEX_DATE_BORN_INPUT)
                .getText().toString();

        if(TextUtils.isEmpty(fullName)) {
            mPersonalInfList.get(Constants.INDEX_NAME_INPUT)
                    .setError(getString(R.string.not_filled_name));
            return null;
        }

        if(TextUtils.isEmpty(phoneNumber)) {
            mPersonalInfList.get(Constants.INDEX_PHONE_INPUT)
                    .setError(getString(R.string.not_filled_phone));
            return null;
        }

        if(phoneNumber.length() < 4
                || phoneNumber.length() > 13
                || !android.util.Patterns.PHONE.matcher(phoneNumber).matches()) {
            mPersonalInfList.get(Constants.INDEX_PHONE_INPUT)
                    .setError(getString(R.string.no_valid_phone));
            return null;
        }

        if (TextUtils.isEmpty(mDateRepresentation.toString())) {
            mPersonalInfList.get(Constants.INDEX_DATE_BORN_INPUT)
                    .setError(getString(R.string.not_filled_born_date));
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(mDateRepresentation);
        if(Calendar.getInstance().get(Calendar.YEAR)
                - calendar.get(Calendar.YEAR) < 16) {
            mPersonalInfList.get(Constants.INDEX_DATE_BORN_INPUT)
                    .setError(getString(R.string.no_valid_born_date));
            return null;
        }

        int sex = mRadioGroupSex.getCheckedRadioButtonId();
        if(sex == -1) {
            if(getContext() != null)
                Toast.makeText(getContext(),
                        R.string.no_sex_selected, Toast.LENGTH_LONG).show();

            mFemale_radio_button.requestFocus();
            return null;
        }

        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setDateBornTimestamp(mDateRepresentation.getTime());
        user.setSexCode(sex);

        return user;
    }

    @Override
    public int getPosition() {
        return Constants.PERSONAL_INFO_FRAGMENT_SEQ;
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus) {
            Calendar mCurrentDate = Calendar.getInstance();

            if(getContext() == null) return;
            DatePickerDialog dialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {

                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.YEAR, year);
                            cal.set(Calendar.MONTH, monthOfYear);
                            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mDateRepresentation = cal.getTime();

                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.US);

                            mPersonalInfList.get(Constants.INDEX_DATE_BORN_INPUT)
                                    .setText(sdf.format(mDateRepresentation));

                            mFemale_radio_button.requestFocus();
                        }
                    }, mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                    mCurrentDate.get(Calendar.DAY_OF_MONTH));
            dialog.setCancelable(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
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
            dialog.getDatePicker().setMaxDate(cal.getTime().getTime());

            dialog.show();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.performClick();
        return false;
    }
}
