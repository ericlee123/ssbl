package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eric.ssbl.R;

public class EditEventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ((TextView) abv.findViewById(R.id.lower_level_title))
                .setText(getString(savedInstanceState.getBoolean("new_event") ? R.string.create_event : R.string.edit_event));

        setContentView(R.layout.activity_edit_event);
    }

    public void saveEvent(View view) {
        // save the event details
    }

    public void goBack(View view) {
        finish();
    }

}
