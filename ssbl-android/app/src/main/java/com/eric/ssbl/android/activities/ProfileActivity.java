package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        ((TextView) findViewById(R.id.profile_username)).setText("timeline62x");
        ((TextView) findViewById(R.id.profile_subtitle)).setText("Competitive");

        StringBuilder games = new StringBuilder();
        games.append("Games:\n");
        games.append("\t\t\t\tMelee\n");
        games.append("\t\t\t\tProject M.\n");
        games.append("\t\t\t\tSmash 64\n");
        games.append("\t\t\t\tSmash 4");
        ((TextView) findViewById(R.id.profile_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append("Bio:\n\t\t\t\t");
        bio.append("I'm down to smash whenever and whichever corner of a dark alleyway. ;)");
        ((TextView) findViewById(R.id.profile_bio_title)).setText(bio.toString());


        findViewById(R.id.profile_stranger_buttons).setVisibility(View.VISIBLE);

        int friendButton = getResources().getIdentifier("@drawable/green_plus_button", null, getPackageName());
        Drawable fb = getResources().getDrawable(friendButton);
        ((ImageButton) findViewById(R.id.profile_stranger_friend_button)).setImageDrawable(fb);
        ((TextView) findViewById(R.id.profile_stranger_friend_button_caption)).setText(getString(R.string.add_friend));
    }

    public void goBack(View view) {
        finish();
    }

    public void friendButton(View view) {
        // conditional based on friend status
        Toast.makeText(this, "Friend button pressed", Toast.LENGTH_SHORT).show();
    }

    public void startConversation(View view) {
        // go to message activity or something idk
        Toast.makeText(this, "Initiate conversation", Toast.LENGTH_SHORT).show();
    }

    public void viewFriends(View view) {
        Toast.makeText(this, "View friends", Toast.LENGTH_SHORT).show();
    }
}
