package com.greece.nasiakouts.babysitterfinder.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.R;
import com.greece.nasiakouts.babysitterfinder.Utils;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationCommonActivity extends AppCompatActivity
        implements View.OnFocusChangeListener, View.OnTouchListener {

    Activity currentActivity;

    @BindView(R.id.radio_group_interest)
    RadioGroup radioGroupInterest;

    @BindViews(
            {R.id.name_input,
                    R.id.mail_input,
                    R.id.phone_input,
                    R.id.password_input,
                    R.id.confirm_password_input,
                    R.id.year_input})
    List<EditText> inputs;

    @BindView(R.id.radio_group_sex)
    RadioGroup radioGroupSex;

    @BindViews(
            {R.id.radio_babysitter,
                    R.id.radio_user,
                    R.id.radio_female,
                    R.id.radio_male})
    List<RadioButton> radios;

    @BindView(R.id.common_screen_next)
    Button goToNext;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_common);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.registration);

        currentActivity = this;

        inputs.get(Constants.INDEX_DATE_BORN_INPUT).setOnFocusChangeListener(this);

        for (RadioButton radio : radios) {
            radio.setOnTouchListener(this);
            goToNext.setOnTouchListener(this);
        }
    }

    private void nextPressed(View view) {
        int selectedRadioInterest = radioGroupInterest.getCheckedRadioButtonId();
        int selectedRadioSex = radioGroupSex.getCheckedRadioButtonId();
/*

        // Make sure all info has been provided and are valid before continue to next screen
        if(selectedRadioInterest == -1) {
            Toast.makeText(this,
                    R.string.no_interest_selected,
                    Toast.LENGTH_LONG).show();
            return;
        }

        for(EditText editText : inputs) {
            if(TextUtils.isEmpty(editText.getText().toString())) {
                Toast.makeText(this,
                        R.string.no_all_info_filled,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String emailAddressedTyped = inputs.get(Constants.INDEX_MAIL_INPUT).getText().toString();
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressedTyped).matches()) {
            Toast.makeText(this,
                    R.string.no_valid_mail,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumberTyped = inputs.get(Constants.INDEX_PHONE_INPUT).getText().toString();
        if(phoneNumberTyped.length() < 4
                || phoneNumberTyped.length() > 13
                || !android.util.Patterns.PHONE.matcher(phoneNumberTyped).matches()) {
            Toast.makeText(this,
                    R.string.no_valid_number,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordTyped = inputs.get(Constants.INDEX_PASSWORD_INPUT).getText().toString();
        if(passwordTyped.length() < 4) {
            Toast.makeText(this,
                    R.string.no_enough_chars_password,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String confirmPasswordTyped = inputs.get(Constants.INDEX_CONFIRM_PASSWORD_INPUT).getText().toString();
        if(!passwordTyped.equals(confirmPasswordTyped)) {
            Toast.makeText(this,
                    R.string.mismatch_passwords,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String dateBorn = inputs.get(Constants.INDEX_DATE_BORN_INPUT).getText().toString();
        if(Calendar.getInstance().get(Calendar.YEAR)
                - Integer.parseInt(dateBorn.substring(dateBorn.lastIndexOf("/") + 1)) < 16) {
            Toast.makeText(this,
                    R.string.born_date_not_accepted,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedRadioSex == -1) {
            Toast.makeText(this,
                    R.string.no_sex_selected,
                    Toast.LENGTH_LONG).show();
            return;
        }
        // -------------------------------------------------------------------
*/
        if(selectedRadioInterest == R.id.radio_babysitter) {
            Intent intent = new Intent(this, SitterRegisterMainActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, RegistrationCommonActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus) {
            Utils.hideKeyboard(currentActivity);

            Calendar mCurrentDate = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // "dd/mm/yy";
                            inputs.get(Constants.INDEX_DATE_BORN_INPUT).setText(new StringBuilder()
                                    .append(dayOfMonth)
                                    .append("/")
                                    .append(monthOfYear)
                                    .append("/")
                                    .append(year)
                                    .toString());

                            Utils.hideKeyboard(currentActivity);
                            radios.get(Constants.INDEX_RADIO_FEMALE).requestFocus();
                        }
                    }, mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                    mCurrentDate.get(Calendar.DAY_OF_MONTH));
            dialog.setCancelable(false);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Utils.hideKeyboard(currentActivity);
                    radios.get(Constants.INDEX_RADIO_FEMALE).requestFocus();
                }
            });
            dialog.show();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!(view instanceof RadioButton) && view instanceof Button) {
            nextPressed(view);
            return false;
        }

        view.performClick();
        view.clearFocus();

        int radioTouched = view.getId();
        if (radioTouched == R.id.radio_babysitter || radioTouched == R.id.radio_user) {
            inputs.get(Constants.INDEX_NAME_INPUT).post(new Runnable() {
                public void run() {
                    inputs.get(Constants.INDEX_NAME_INPUT).requestFocusFromTouch();
                    InputMethodManager lManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (lManager != null)
                        lManager.showSoftInput(inputs.get(Constants.INDEX_NAME_INPUT), 0);
                }
            });
        } else {
            goToNext.requestFocus();
        }

        return false;
    }
}
