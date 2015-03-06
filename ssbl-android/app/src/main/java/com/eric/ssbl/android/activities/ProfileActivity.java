package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;

public class ProfileActivity extends Activity {

    private final Context _context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ((TextView) abv.findViewById(R.id.lower_level_title)).setText(getString(R.string.profile));

        setContentView(R.layout.fragment_ep);

        // Fill in the details
        ((ImageView) findViewById(R.id.ep_cover_photo)).setImageResource(R.drawable.md_blue_black_x);
        ((ImageView) findViewById(R.id.ep_icon)).setImageResource(R.drawable.honey);
        ((TextView) findViewById(R.id.ep_title)).setText("timeline62x");
        ((TextView) findViewById(R.id.ep_subtitle)).setText("Competitive");

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        games.append("\t\t\t\tMelee\n");
        games.append("\t\t\t\tProject M.\n");
        games.append("\t\t\t\tSmash 64\n");
        games.append("\t\t\t\tSmash 4");
        ((TextView) findViewById(R.id.ep_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        bio.append("I'm down to smash whenever and whichever corner of a dark alleyway. ;)");
        ((TextView) findViewById(R.id.ep_description)).setText(bio.toString());

        // Check to see if it's the current user's profile
        if (true) {

            ImageButton lb = (ImageButton) findViewById(R.id.ep_button_left);
            lb.setImageResource(R.drawable.green_plus_button);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(_context, "leftButton", Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView) findViewById(R.id.ep_button_left_caption)).setText(getString(R.string.add_friend));

            ImageButton mb = (ImageButton) findViewById(R.id.ep_button_middle);
            mb.setImageResource(R.drawable.blue_chat_button);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.ep_button_middle_caption)).setText(getString(R.string.message));

            ImageButton rb = (ImageButton) findViewById(R.id.ep_button_right);
            rb.setImageResource(R.drawable.orange_search_button);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.ep_button_right_caption)).setText(getString(R.string.view_friends));

        }
        else {

        }
    }

    public void goBack(View view) {
        finish();
    }

}
