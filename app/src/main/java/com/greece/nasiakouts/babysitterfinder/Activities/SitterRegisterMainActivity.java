package com.greece.nasiakouts.babysitterfinder.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.Adapters.WorkingHoursRvAdapter;
import com.greece.nasiakouts.babysitterfinder.Models.TimeSlot;
import com.greece.nasiakouts.babysitterfinder.R;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.greece.nasiakouts.babysitterfinder.Constants.ADD_TIMESLOT_REQUEST_CODE;

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
    RecyclerView workingSlotsRv;

    @BindView(R.id.sitter_main_screen_next)
    Button goToNext;

    WorkingHoursRvAdapter adapter;
    TimeSlot timeSlot;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitter_register_main);

        ButterKnife.bind(this);

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.registration);

        currentActiviy = this;

        adapter = new WorkingHoursRvAdapter(null);
        workingSlotsRv.setAdapter(adapter);
        workingSlotsRv.addItemDecoration(new DividerItemDecoration(workingSlotsRv.getContext(),
                DividerItemDecoration.VERTICAL));
        workingSlotsRv.setLayoutManager(new LinearLayoutManager(this));

        currencyEditText.setOnFocusChangeListener(this);
        goToNext.setOnTouchListener(this);
    }

    @OnClick(R.id.addWorkingSlot)
    public void addWorkingSlot(View view){
        Intent intent = new Intent(this, AddTimeSlotActivity.class);
        intent.putExtra(getString(R.string.interest_key), getString(R.string.sitter_value));
        startActivityForResult(intent, ADD_TIMESLOT_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TIMESLOT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                timeSlot = (TimeSlot) data.getSerializableExtra(TimeSlot.class.getName());
                adapter.insertTimeslot(timeSlot);
            }
        }
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
            final CurrencyPicker picker = CurrencyPicker.newInstance(getString(R.string.select_currency_prompt));
            picker.show(getSupportFragmentManager(), getString(R.string.currency_picker));
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
