package com.greece.nasiakouts.babysitterfinder.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.greece.nasiakouts.babysitterfinder.Constants.INT_CODE;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.register_now_tv)
    TextView mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRegister.setTextColor(ContextCompat
                .getColor(MainActivity.this, R.color.secondary_text));
    }

    @OnClick(R.id.log_in_button)
    public void logIn(View view) {
        // TODO ...
    }

    @OnClick(R.id.register_now_tv)
    public void register(View view) {
        mRegister.setTextColor(ContextCompat.getColor(this, R.color.hyperlink_color));

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_selection, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.mode_prompt)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioGroup radioGroupInterest =
                                dialogView.findViewById(R.id.radio_group_interest);

                        int selectedRadioInterest =
                                radioGroupInterest.getCheckedRadioButtonId();

                        if(selectedRadioInterest == -1) {
                            Toast.makeText(MainActivity.this,
                                    R.string.no_register_mode_selected, Toast.LENGTH_LONG).show();
                            mRegister.setTextColor(ContextCompat
                                    .getColor(MainActivity.this, R.color.secondary_text));
                            return;
                        }

                        Intent intent =
                                new Intent(MainActivity.this, RegisterActivity.class);
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
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mRegister.setTextColor(ContextCompat
                                .getColor(MainActivity.this, R.color.secondary_text));
                    }
                })
                .show();
    }
}
