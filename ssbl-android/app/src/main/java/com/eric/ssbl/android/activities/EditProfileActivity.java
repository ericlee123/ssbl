package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eric.ssbl.R;

public class EditProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((TextView) findViewById(R.id.action_bar_title)).setText("Edit Profile");

        setContentView(R.layout.activity_edit_profile);


    }

    public void saveProfile(View view) {
        // send the new user to the server
    }

    public void goBack(View view) {
        finish();
    }
}
