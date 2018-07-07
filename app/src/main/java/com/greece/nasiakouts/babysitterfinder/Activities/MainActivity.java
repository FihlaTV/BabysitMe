package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.greece.nasiakouts.babysitterfinder.Constants.INT_CODE;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.log_in_wrapper_layout)
    public void logIn(View view) {
        // TODO ...
    }

    @OnClick(R.id.register_wrapper_layout)
    public void register(View view) {
        final View dialogView = getLayoutInflater().inflate(R.layout.register_selecion_dialog, null);

        new AlertDialog.Builder(this)
            .setTitle(R.string.interest_prompt)
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    RadioGroup radioGroupInterest = dialogView.findViewById(R.id.radio_group_interest);
                    int selectedRadioInterest = radioGroupInterest.getCheckedRadioButtonId();

                    if(selectedRadioInterest == -1) return;

                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    intent.putExtra(INT_CODE, selectedRadioInterest);
                    startActivity(intent);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .show();
    }
}
