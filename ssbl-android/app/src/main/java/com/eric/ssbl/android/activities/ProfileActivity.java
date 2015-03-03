package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eric.ssbl.R;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_lower_level, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ((TextView) abv.findViewById(R.id.lower_level_title)).setText("Profile");

        setContentView(R.layout.fragment_profile);

        // Fill in the details
        StringBuilder games = new StringBuilder();
        games.append("Games:\n");
        games.append("\t\t\t\tMelee\n");
        games.append("\t\t\t\tProject M.\n");
        games.append("\t\t\t\tSmash 64\n");
        games.append("\t\t\t\tSmash 4");
        ((TextView) findViewById(R.id.profile_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append("Bio:\n\t\t\t\t");
        bio.append("My name is smishy and i like to smash smash smash a a a a a a a a a a a a a a  a a a a a a a a a a a a a a a a a aw w w w  w w w w w w w w d d d  a aw w w w  w w w w w w w w d d d  a aw w w w  w w w w w w w w d d d  a aw w w w  w w w w w w w w d d d d  d d d d d  d d d  d d d d  d d d d d   d d d d d  d d d d  d d d d d d d d  d d d d d w w w w w w  w w w w w w w w  w!!!");
        ((TextView) findViewById(R.id.profile_bio_title)).setText(bio.toString());

        findViewById(R.id.profile_stranger_buttons).setVisibility(View.VISIBLE);
    }

    public void goBack(View view) {
        finish();
    }

    public void startConversation(View view) {
        // go to message activity or something idk
    }
}
