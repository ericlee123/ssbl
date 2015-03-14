package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eric.ssbl.R;

public class EditEventActivity extends Activity {

    private TextView _locationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        Bundle extras = getIntent().getExtras();
        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(getString(extras.getBoolean("new_event") ? R.string.create_event : R.string.edit_event));

        setContentView(R.layout.activity_edit_event);

        _locationStatus = (TextView) findViewById(R.id.edit_event_location_status);
        _locationStatus.setText(getString(R.string.location) + ": (not set)");
    }

    public void setLocationMap(View view) {
        // Open the interactive map
    }

    public void saveEvent(View view) {
        // save the event details
    }

    public void goBack(View view) {
        finish();
    }

}
