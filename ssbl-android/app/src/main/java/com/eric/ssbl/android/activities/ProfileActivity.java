package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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

        setContentView(R.layout.fragment_ep);

        // Fill in the details
        ((TextView) findViewById(R.id.ep_title)).setText("timeline62x");
        ((TextView) findViewById(R.id.ep_subtitle)).setText("Competitive");

        StringBuilder games = new StringBuilder();
        games.append("Games\n");
        games.append("\t\t\t\tMelee\n");
        games.append("\t\t\t\tProject M.\n");
        games.append("\t\t\t\tSmash 64\n");
        games.append("\t\t\t\tSmash 4");
        ((TextView) findViewById(R.id.ep_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append("Bio\n\t\t\t\t");
        bio.append("I'm down to smash whenever and whichever corner of a dark alleyway. ;)");
        ((TextView) findViewById(R.id.ep_description)).setText(bio.toString());


        findViewById(R.id.ep_other_buttons).setVisibility(View.VISIBLE);

        Drawable lb = getResources().getDrawable(getResources().getIdentifier("@drawable/green_plus_button", null, getPackageName()));
        ((ImageButton) findViewById(R.id.ep_button_left)).setImageDrawable(lb);
        ((TextView) findViewById(R.id.ep_button_left_caption)).setText(getString(R.string.add_friend));

        Drawable mb = getResources().getDrawable(getResources().getIdentifier("@drawable/blue_chat_button", null, getPackageName()));
        ((ImageButton) findViewById(R.id.ep_button_middle)).setImageDrawable(mb);
        ((TextView) findViewById(R.id.ep_button_middle_caption)).setText(getString(R.string.message));

        Drawable rb = getResources().getDrawable(getResources().getIdentifier("@drawable/orange_search_button", null, getPackageName()));
        ((ImageButton) findViewById(R.id.ep_button_right)).setImageDrawable(rb);
        ((TextView) findViewById(R.id.ep_button_right_caption)).setText(getString(R.string.view_friends));
    }

    public void goBack(View view) {
        finish();
    }

    public void leftButton(View view) {

    }

    public void middleButton(View view) {

    }

    public void rightButton(View view) {

    }
}
