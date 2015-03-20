package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.databind.ObjectMapper;

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

        if (!getIntent().hasExtra("user_json")) {
            Toast.makeText(_context, "Error loading user :(", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            String eventJson = getIntent().getStringExtra("user_json");
            _user = new ObjectMapper().readValue(eventJson, User.class);
            fillDetails();
        } catch (Exception e) {
            Toast.makeText(_context, "Error loading user :(", Toast.LENGTH_LONG).show();
        }
    }

    private void fillDetails() {

        if (_user == null)
            return;

        System.out.println("user id: " + _user.getUserId());

        ((ImageView) findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_tangents);
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
        if (_user.getGames().size() == 0)
            games.append("\t\t\t\t(none)\n");
        games.delete(games.length() - 1, games.length());
        ((TextView) findViewById(R.id.eu_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        bio.append(_user.getBlurb());
        ((TextView) findViewById(R.id.eu_description)).setText(bio.toString());

        StringBuilder attendingEvents = new StringBuilder();
        attendingEvents.append("Attending events\n");
        if (_user.getEvents() == null)
            attendingEvents.append("\t\t\t\tNot attending anything.");
        else {
            for (Event e : _user.getEvents())
                attendingEvents.append("\t\t\t\t" + e.getTitle() + "\n");
            attendingEvents.delete(attendingEvents.length() - 1, attendingEvents.length());
        }
        ((TextView) findViewById(R.id.event_attending_list)).setText(attendingEvents);

        // Check to see if it's the current user's profile
        if (!_user.equals(DataManager.getCurUser())) {

            ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(R.drawable.green_plus);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(_context, "leftButton", Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView) findViewById(R.id.eu_button_left_caption)).setText(getString(R.string.add_friend));

            ImageButton mb = (ImageButton) findViewById(R.id.eu_button_middle);
            mb.setImageResource(R.drawable.blue_chat);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.message));

            ImageButton rb = (ImageButton) findViewById(R.id.eu_button_right);
            rb.setImageResource(R.drawable.orange_search);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.eu_button_right_caption)).setText(getString(R.string.view_friends));

        }
        else {

            ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(R.drawable.blue_pencil);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(_context, EditProfileActivity.class));
                }
            });
            ((TextView) findViewById(R.id.eu_button_left_caption)).setText(getString(R.string.edit_profile));

            ImageButton mb = (ImageButton) findViewById(R.id.eu_button_middle);
            mb.setImageResource(R.drawable.green_face);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.set_mood));

            ImageButton rb = (ImageButton) findViewById(R.id.eu_button_right);
            rb.setImageResource(R.drawable.orange_search);
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

//    private class HttpUserGetter extends AsyncTask<User, Void, Void> {
//
//        private User u;
//
//        private void getUser(User template) {
//
//            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
//            url.append("/search/user");
//
//            try {
//                HttpClient client = new DefaultHttpClient();
//                HttpPost request = new HttpPost(url.toString());
//
//                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
//
//                // encode the user template into the request
//                ObjectMapper om = new ObjectMapper();
//                StringEntity body = new StringEntity(om.writeValueAsString(template));
//                request.setEntity(body);
//
//                HttpResponse response = client.execute(request);
//                String jsonString = EntityUtils.toString(response.getEntity());
//
//                if (jsonString.length() == 0)
//                    return;
//
//                List<User> lu = om.readValue(jsonString, new TypeReference<List<User>>(){});
//                u = lu.get(0);
//            } catch (Exception e) {
//                u = null;
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        protected Void doInBackground(User... params) {
//
//            getUser(params[0]);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void what) {
//            if (u != null) {
//                _user = u;
//                fillDetails();
//            }
//            else
//                Toast.makeText(_context, "Error retrieving profile", Toast.LENGTH_SHORT).show();
//        }
//    }
}
