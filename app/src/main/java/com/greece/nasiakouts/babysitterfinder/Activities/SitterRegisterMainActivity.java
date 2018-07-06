package com.greece.nasiakouts.babysitterfinder.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.greece.nasiakouts.babysitterfinder.Adapters.WorkingHoursRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Constants;
import com.greece.nasiakouts.babysitterfinder.R;
import com.greece.nasiakouts.babysitterfinder.Utils;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SitterRegisterMainActivity extends AppCompatActivity
        implements View.OnTouchListener, View.OnFocusChangeListener {

    Activity currentActiviy;

    @BindView(R.id.sitter_address)
    EditText addressEditText;

    @BindView(R.id.sitter_charges)
    EditText chargesEditText;

    @BindView(R.id.currency_input)
    EditText currencyEditText;

    @BindView(R.id.working_slots_rv)
    RecyclerView workingSlotsRvl;

    @BindView(R.id.sitter_main_screen_next)
    Button goToNext;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitter_register_main);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.registration);

        currentActiviy = this;

        WorkingHoursRvAdapter adapter = new WorkingHoursRvAdapter(null);
        workingSlotsRvl.setAdapter(adapter);

        currencyEditText.setOnFocusChangeListener(this);
        goToNext.setOnTouchListener(this);
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!(view instanceof Button)) return false;
        nextPressed(view);
        return false;
    }

    private void nextPressed(View view) {
        String addressInput = addressEditText.getText().toString();
        String chargesInput = chargesEditText.getText().toString();

        if (TextUtils.isEmpty(addressInput) ||
                TextUtils.isEmpty(chargesInput)) {
            Toast.makeText(this,
                    R.string.no_all_info_filled,
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");
            picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
            picker.setListener(new CurrencyPickerListener() {
                @Override
                public void onSelectCurrency(String name, String code, String dialCode, int flagDrawableResID) {
                    currencyEditText.setText(code);
                    picker.dismiss();
                    goToNext.requestFocus();
                }
            });

        }
    }
}
