package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.os.Bundle;

import com.eric.ssbl.R;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (savedInstanceState.getBoolean("from_map"))
                getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {

        }
        setContentView(R.layout.fragment_profile);
    }
}
