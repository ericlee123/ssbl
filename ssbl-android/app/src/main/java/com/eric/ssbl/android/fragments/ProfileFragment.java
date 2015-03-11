package com.eric.ssbl.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.Manager;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.User;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_eu, container, false);

        // Fill in the details
        User _user = Manager.getCurUser();
        if (_user == null) {
            Toast.makeText(getActivity(), getString(R.string.error_loading_profile), Toast.LENGTH_SHORT).show();
            return v;
        }
        ((ImageView) v.findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_blue_black_x);
        ((ImageView) v.findViewById(R.id.eu_icon)).setImageResource(R.drawable.honey);
        ((TextView) v.findViewById(R.id.eu_title)).setText(_user.getUsername());
        ((TextView) v.findViewById(R.id.eu_subtitle)).setText("You");

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        for (Game g : _user.getGames()) {
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
        ((TextView) v.findViewById(R.id.eu_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        bio.append(_user.getBlurb());
        ((TextView) v.findViewById(R.id.eu_description)).setText(bio.toString());


        ImageButton lb = (ImageButton) v.findViewById(R.id.eu_button_left);
        lb.setImageResource(R.drawable.green_plus_button);
        lb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "leftButton", Toast.LENGTH_SHORT).show();
            }
        });
        ((TextView) v.findViewById(R.id.eu_button_left_caption)).setText(getString(R.string.add_friend));

        ImageButton mb = (ImageButton) v.findViewById(R.id.eu_button_middle);
        mb.setImageResource(R.drawable.blue_chat_button);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new message
            }
        });
        ((TextView) v.findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.message));

        ImageButton rb = (ImageButton) v.findViewById(R.id.eu_button_right);
        rb.setImageResource(R.drawable.orange_search_button);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new message
            }
        });
        ((TextView) v.findViewById(R.id.eu_button_right_caption)).setText(getString(R.string.view_friends));

        return v;
    }
}
