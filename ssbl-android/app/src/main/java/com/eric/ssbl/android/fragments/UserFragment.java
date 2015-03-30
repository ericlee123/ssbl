package com.eric.ssbl.android.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.EditProfileActivity;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.User;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private static View _view;
    private static User _user;
    private static boolean _refreshed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DataManager.setProfileFragment(this);

        _view = inflater.inflate(R.layout.fragment_eu, container, false);

        if (!_refreshed)
            refresh();

        return _view;
    }

    public void refresh() {
        _refreshed = true;
        _user = DataManager.getCurrentUser();
        fillDetails();
    }

    private void fillDetails() {

        if (!isAdded()) {
            _refreshed = false;
            return;
        }

        if (_user == null || _view == null) {
            _refreshed = false;
            return;
        }

        ((ImageView) _view.findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_black_yellow);
        ((ImageView) _view.findViewById(R.id.eu_icon)).setImageResource(R.drawable.honey);
        if (_user.getUsername() != null)
            ((TextView) _view.findViewById(R.id.eu_title)).setText(_user.getUsername());
        ((TextView) _view.findViewById(R.id.eu_subtitle)).setText("You");

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        if (_user.getGames() == null || _user.getGames().size() == 0)
            games.append("\t\t\t\t(" + getString(R.string.none) + ")");
        else {
            for (Game g : _user.getGames()) {
                games.append("\t\t\t\t");
                if (g.equals(Game.SSB64))
                    games.append(getString(R.string.ssb64));
                else if (g.equals(Game.MELEE))
                    games.append(getString(R.string.melee));
                else if (g.equals(Game.BRAWL))
                    games.append(getString(R.string.brawl));
                else if (g.equals(Game.PM))
                    games.append(getString(R.string.pm));
                else if (g.equals(Game.SMASH4))
                    games.append(getString(R.string.smash4));
                games.append("\n");
            }
            games.delete(games.length() - 1, games.length());
        }
        ((TextView) _view.findViewById(R.id.eu_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        if (_user.getBlurb() != null)
            bio.append(_user.getBlurb());
        else
            bio.append("(N/A)");
        ((TextView) _view.findViewById(R.id.eu_description)).setText(bio.toString());

        StringBuilder attendingEvents = new StringBuilder();
        attendingEvents.append("Attending events\n");
        if (_user.getEvents() == null || _user.getEvents().size() == 0)
            attendingEvents.append("\t\t\t\tNot attending anything.");
        else {
            for (Event e : _user.getEvents())
                attendingEvents.append("\t\t\t\t" + e.getTitle() + "\n");
            attendingEvents.delete(attendingEvents.length() - 1, attendingEvents.length());
        }
        ((TextView) _view.findViewById(R.id.event_attending_list)).setText(attendingEvents);

        // 3 buttons
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
        mb.setImageResource(R.drawable.gray_fedora);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getString(R.string.mlady), Toast.LENGTH_SHORT).show();
            }
        });
        ((TextView) _view.findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.tip_fedora));

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        List<User> circle = DataManager.getCurrentUser().getFriends();
        List<String> circleNames = new ArrayList<>();
        if (circle != null)
            for (int i = 0; i < circle.size(); i++)
                circleNames.add(circle.get(i).getUsername());
        adapter.addAll(circleNames);

        ImageButton rb = (ImageButton) _view.findViewById(R.id.eu_button_right);
        rb.setImageResource(R.drawable.orange_search);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb
                        .setTitle("Circle")
                        .setCancelable(true)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNeutralButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                adb.create().show();
            }
        });
        ((TextView) _view.findViewById(R.id.eu_button_right_caption)).setText(getString(R.string.view_circle));
    }

    public static void clearData() {
        _refreshed = false;
        _user = null;
    }
}
