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
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.User;

public class ProfileActivity extends Activity {

    private final Context _context = this;
    private User _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(getString(R.string.profile));

        setContentView(R.layout.fragment_eu);

        Integer id;
        try {
            id = getIntent().getExtras().getInt("user_id");
        } catch (NullPointerException e) {
            Toast.makeText(this, getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show();
            return;
        }
        _user = DataManager.getUser(id);

        // Fill in the details
        ((ImageView) findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_blue_black_x);
        ((ImageView) findViewById(R.id.eu_icon)).setImageResource(R.drawable.honey);
        ((TextView) findViewById(R.id.eu_title)).setText(_user.getUsername());

        String lastLogin = "Logged in ";
        long now = System.currentTimeMillis();
        int elapsed = (int) ((now - _user.getLastLoginTime()) / 60000);
        if (elapsed < 60)
            lastLogin += elapsed + " minutes ago";
        else if (elapsed < 1440)
            lastLogin += (elapsed / 60) + " hours ago";
        else
            lastLogin += (elapsed / 1440) + " days ago";

        ((TextView) findViewById(R.id.eu_subtitle)).setText(lastLogin);

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        for (Game g: _user.getGames()) {
            games.append("\t\t\t\t");
            if (g == Game.SSB64)
                games.append("Smash 64");
            else if (g == Game.MELEE)
                games.append("Melee");
            else if (g == Game.BRAWL)
                games.append("Brawl");
            else if (g == Game.PM)
                games.append("Project M.");
            else if (g == Game.SMASH4)
                games.append("Smash 4");
            games.append("\n");
        }
        if (games.length() != 0)
            games.delete(games.length() - 1, games.length());
        ((TextView) findViewById(R.id.eu_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        bio.append(_user.getBlurb());
        ((TextView) findViewById(R.id.eu_description)).setText(bio.toString());

        // Check to see if it's the current user's profile
        if (_user.equals(DataManager.getCurUser())) {



        }
        else {

            ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(R.drawable.green_plus_button);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(_context, "leftButton", Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView) findViewById(R.id.eu_button_left_caption)).setText(getString(R.string.add_friend));

            ImageButton mb = (ImageButton) findViewById(R.id.eu_button_middle);
            mb.setImageResource(R.drawable.blue_chat_button);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.message));

            ImageButton rb = (ImageButton) findViewById(R.id.eu_button_right);
            rb.setImageResource(R.drawable.orange_search_button);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.eu_button_right_caption)).setText(getString(R.string.view_friends));
        }
    }

    public void goBack(View view) {
        finish();
    }

}
