package com.greece.nasiakouts.babysitterfinder.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationCommonActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    @BindView(R.id.radio_group_interest)
    RadioGroup radioGroupInterest;

    @BindViews({ R.id.name_input, R.id.mail_input, R.id.phone_input, R.id.password_input, R.id.confirm_password_input, R.id.year_input })
    List<EditText> inputs;

    @BindView(R.id.radio_group_sex)
    RadioGroup radioGroupSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_common);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.registration);

        inputs.get(5).setOnFocusChangeListener(this);
    }

    @OnClick(R.id.common_screen_next)
    public void nextPressed(View view) {
        int selectedRadioInterest = radioGroupInterest.getCheckedRadioButtonId();
        int selectedRadioSex = radioGroupSex.getCheckedRadioButtonId();


        // Make sure all info has been provided and are valid before continue to next screen
        if(selectedRadioInterest == -1) {
            Toast.makeText(this, R.string.no_interest_selected, Toast.LENGTH_LONG).show();
            return;
        }

        for(EditText editText : inputs) {
            if(TextUtils.isEmpty(editText.getText().toString())) {
                Toast.makeText(this, R.string.no_all_info_filled, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String emailAddressedTyped = inputs.get(1).getText().toString();
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressedTyped).matches()) {
            Toast.makeText(this, R.string.no_valid_mail, Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNumberTyped = inputs.get(2).getText().toString();
        if(phoneNumberTyped.length() < 4
                || phoneNumberTyped.length() > 13
                || !android.util.Patterns.PHONE.matcher(phoneNumberTyped).matches()) {
            Toast.makeText(this, R.string.no_valid_number, Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordTyped = inputs.get(3).getText().toString();
        if(passwordTyped.length() < 4) {
            Toast.makeText(this, R.string.no_enough_chars_password, Toast.LENGTH_SHORT).show();
            return;
        }

        String confirmPasswordTyped = inputs.get(4).getText().toString();
        if(!passwordTyped.equals(confirmPasswordTyped)) {
            Toast.makeText(this, R.string.mismatch_passwords, Toast.LENGTH_SHORT).show();
            return;
        }

        String dateBorn = inputs.get(5).getText().toString();
        if(Calendar.getInstance().get(Calendar.YEAR)
                - Integer.parseInt(dateBorn.substring(dateBorn.lastIndexOf("/") + 1)) < 16) {
            Toast.makeText(this, R.string.born_date_not_accepted, Toast.LENGTH_SHORT).show();
            return;
        }

        if(selectedRadioSex == -1) {
            Toast.makeText(this, R.string.no_sex_selected, Toast.LENGTH_LONG).show();
            return;
        }
        // -------------------------------------------------------------------

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
            Calendar mCurrentDate = Calendar.getInstance();

            new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // "dd/mm/yy";
                            inputs.get(5).setText(new StringBuilder()
                                    .append(dayOfMonth)
                                    .append("/")
                                    .append(monthOfYear)
                                    .append("/")
                                    .append(year)
                                    .toString());
                        }
                    }, mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                    mCurrentDate.get(Calendar.DAY_OF_MONTH)).show();
        }
    }
}
