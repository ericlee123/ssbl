package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.EditProfileActivity;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.User;

public class ProfileFragment extends Fragment {

    private View _view;
    private static boolean _refreshed = false;
    private User _user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        System.out.println("onCreateView called");

        _view = inflater.inflate(R.layout.fragment_eu, container, false);

        if (!_refreshed) {
            _user = DataManager.getCurUser();
            fillDetails();
        }

        return _view;
    }

    private void fillDetails() {

        if (_user == null) {
            _refreshed = false;
            return;
        }

        ((ImageView) _view.findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_gray);
        ((ImageView) _view.findViewById(R.id.eu_icon)).setImageResource(R.drawable.honey);
        ((TextView) _view.findViewById(R.id.eu_title)).setText(_user.getUsername());
        ((TextView) _view.findViewById(R.id.eu_subtitle)).setText("You");

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
        if (games.length() != 0)
            games.delete(games.length() - 1, games.length());
        ((TextView) _view.findViewById(R.id.eu_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        bio.append(_user.getBlurb());
        ((TextView) _view.findViewById(R.id.eu_description)).setText(bio.toString());

        StringBuilder attendingEvents = new StringBuilder();
        attendingEvents.append("Attending events\n");
        if (_user.getEvents() == null)
            attendingEvents.append("\t\t\t\tNot attending anything.");
        else {
            for (Event e : _user.getEvents())
                attendingEvents.append("\t\t\t\t" + e.getTitle() + "\n");
            attendingEvents.delete(attendingEvents.length() - 1, attendingEvents.length());
        }
        ((TextView) _view.findViewById(R.id.event_attending_list)).setText(attendingEvents);

        ImageButton lb = (ImageButton) _view.findViewById(R.id.eu_button_left);
        lb.setImageResource(R.drawable.blue_pencil);
        lb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });
        ((TextView) _view.findViewById(R.id.eu_button_left_caption)).setText(getString(R.string.edit_profile));

        ImageButton mb = (ImageButton) _view.findViewById(R.id.eu_button_middle);
        mb.setImageResource(R.drawable.green_face);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new message
            }
        });
        ((TextView) _view.findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.set_mood));

        ImageButton rb = (ImageButton) _view.findViewById(R.id.eu_button_right);
        rb.setImageResource(R.drawable.orange_search);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new message
            }
        });
        ((TextView) _view.findViewById(R.id.eu_button_right_caption)).setText(getString(R.string.view_friends));
    }

    public static void makeRefresh() {
        _refreshed = false;
    }
}
