package com.eric.ssbl.android.fragments;

import android.graphics.drawable.Drawable;
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

        View v = inflater.inflate(R.layout.fragment_ep, container, false);

        // Fill in the details
        Drawable icon = getResources().getDrawable(getResources().getIdentifier("@drawable/honey", null, getActivity().getPackageName()));
        ((ImageView) v.findViewById(R.id.ep_icon)).setImageDrawable(icon);
        ((TextView) v.findViewById(R.id.ep_title)).setText("timeline62x");
        ((TextView) v.findViewById(R.id.ep_subtitle)).setText("Competitive");

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        games.append("\t\t\t\tMelee\n");
        games.append("\t\t\t\tProject M.\n");
        games.append("\t\t\t\tSmash 64\n");
        games.append("\t\t\t\tSmash 4");
        ((TextView) v.findViewById(R.id.ep_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        bio.append("I'm down to smash whenever and whichever corner of a dark alleyway. ;)");
        ((TextView) v.findViewById(R.id.ep_description)).setText(bio.toString());

        // Check to see if it's the current user's profile
        if (true) {

            Drawable lbdraw = getResources().getDrawable(getResources().getIdentifier("@drawable/green_plus_button", null, getActivity().getPackageName()));
            ImageButton lb = (ImageButton) v.findViewById(R.id.ep_button_left);
            lb.setImageDrawable(lbdraw);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "leftButton", Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView) v.findViewById(R.id.ep_button_left_caption)).setText(getString(R.string.add_friend));

            Drawable mbdraw = getResources().getDrawable(getResources().getIdentifier("@drawable/blue_chat_button", null, getActivity().getPackageName()));
            ImageButton mb = (ImageButton) v.findViewById(R.id.ep_button_middle);
            mb.setImageDrawable(mbdraw);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) v.findViewById(R.id.ep_button_middle_caption)).setText(getString(R.string.message));

            Drawable rbdraw = getResources().getDrawable(getResources().getIdentifier("@drawable/orange_search_button", null, getActivity().getPackageName()));
            ImageButton rb = (ImageButton) v.findViewById(R.id.ep_button_right);
            rb.setImageDrawable(rbdraw);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) v.findViewById(R.id.ep_button_right_caption)).setText(getString(R.string.view_friends));
        }
        else {

//            Drawable lb = getResources().getDrawable(getResources().getIdentifier("@drawable/blue_chat_button", null, getPackageName()));
//            ((ImageButton) findViewById(R.id.ep_personal_button_left)).setImageDrawable(lb);
//
//            Drawable rb = getResources().getDrawable(getResources().getIdentifier("@drawable/orange_search_button", null, getPackageName()));
//            ((ImageButton) findViewById(R.id.ep_personal_button_right)).setImageDrawable(rb);
//            ((TextView) findViewById(R.id.ep_personal_button_right_caption)).setText(getString(R.string.view_friends));
        }

        return v;
    }


}
