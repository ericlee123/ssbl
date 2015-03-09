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

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_eu, container, false);

        // Fill in the details
        ((ImageView) v.findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_blue_black_x);

        ((ImageView) v.findViewById(R.id.eu_icon)).setImageResource(R.drawable.honey);
        ((TextView) v.findViewById(R.id.eu_title)).setText("timeline62x");
        ((TextView) v.findViewById(R.id.eu_subtitle)).setText("Competitive");

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        games.append("\t\t\t\tMelee\n");
        games.append("\t\t\t\tProject M.\n");
        games.append("\t\t\t\tSmash 64\n");
        games.append("\t\t\t\tSmash 4");
        ((TextView) v.findViewById(R.id.eu_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        bio.append("I'm down to smash whenever and whichever corner of a dark alleyway. ;)");
        ((TextView) v.findViewById(R.id.eu_description)).setText(bio.toString());

        // Check to see if it's the current user's profile
        if (true) {

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
        }
        else {

        }

        return v;
    }


}
