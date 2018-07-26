package com.greece.nasiakouts.babysitterfinder.Widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.greece.nasiakouts.babysitterfinder.R;
import com.greece.nasiakouts.babysitterfinder.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The configuration screen for the {@link BabisitterFinderWidget BabisitterFinderWidget} AppWidget.
 */
public class BabisitterFinderWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.greece.nasiakouts.babysitterfinder.Widget.BabisitterFinderWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.radio_appointments_group)
    RadioGroup appointmentsGroup;

    public BabisitterFinderWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveGroupPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadGroupPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String groupValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (groupValue != null) {
            return groupValue;
        } else {
            return Constants.DASH;
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.babisitter_finder_widget_configure);
        ButterKnife.bind(this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @OnClick(R.id.ready_button)
    public void readyPressed() {
        int selected = appointmentsGroup.getCheckedRadioButtonId();
        if (selected == -1) {
            Toast.makeText(this,
                    R.string.select_group_toast,
                    Toast.LENGTH_LONG).show();
            return;
        }
        switch (selected) {
            case R.id.radio_weekly:
                saveGroupPref(this, mAppWidgetId, getString(R.string.weekly_appointments));
                break;
            case R.id.radio_simple:
                saveGroupPref(this, mAppWidgetId, getString(R.string.simple_appointments));
                break;
            default:
                saveGroupPref(this, mAppWidgetId, getString(R.string.both));
                break;
        }

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        BabisitterFinderWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

