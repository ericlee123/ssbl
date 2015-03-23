package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

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
            String userJson = getIntent().getStringExtra("user_json");
            User u = new ObjectMapper().readValue(userJson, User.class);
            new HttpUserGetter().execute(u);
        } catch (Exception e) {
            Toast.makeText(_context, "Error loading user :(", Toast.LENGTH_LONG).show();
        }
    }

    private void fillDetails() {

        if (_user == null)
            return;

        ((ImageView) findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_tangents);
        ((ImageView) findViewById(R.id.eu_icon)).setImageResource(R.drawable.honey);
        if (_user.getUsername() != null)
            ((TextView) findViewById(R.id.eu_title)).setText(_user.getUsername());

        String lastLogin;
        if (_user.getLastLoginTime() != null) {
            lastLogin = "Logged in ";
            long now = System.currentTimeMillis();
            int elapsed = (int) ((now - _user.getLastLoginTime()) / 60000);
            if (elapsed < 60)
                lastLogin += elapsed + " minutes ago";
            else if (elapsed < 1440)
                lastLogin += (elapsed / 60) + " hours ago";
            else
                lastLogin += (elapsed / 1440) + " days ago";
        }
        else
            lastLogin = "Last login unavailable";
        ((TextView) findViewById(R.id.eu_subtitle)).setText(lastLogin);


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
        ((TextView) findViewById(R.id.eu_games)).setText(games.toString());

        StringBuilder bio = new StringBuilder();
        bio.append(getString(R.string.bio) + "\n\t\t\t\t");
        if (_user.getBlurb() != null)
            bio.append(_user.getBlurb());
        else
            bio.append("(N/A)");
        ((TextView) findViewById(R.id.eu_description)).setText(bio.toString());

        StringBuilder attendingEvents = new StringBuilder();
        attendingEvents.append("Attending events\n");
        if (_user.getEvents() == null || _user.getEvents().size() == 0)
            attendingEvents.append("\t\t\t\tNot attending anything.");
        else {
            for (Event e : _user.getEvents())
                attendingEvents.append("\t\t\t\t" + e.getTitle() + "\n");
            attendingEvents.delete(attendingEvents.length() - 1, attendingEvents.length());
        }
        ((TextView) findViewById(R.id.event_attending_list)).setText(attendingEvents);

        // Check to see if it's the current user's profile
        if (!_user.equals(DataManager.getCurUser())) {

            final boolean inCircle = DataManager.getCurUser().getFriends().contains(_user);

            final TextView leftCaption = (TextView) findViewById(R.id.eu_button_left_caption);
            leftCaption.setText(getString(inCircle ? R.string.uncircle : R.string.add_to_circle));
            final ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(inCircle ? R.drawable.red_x : R.drawable.green_plus);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User cur = DataManager.getCurUser();
                    List<User> circle = cur.getFriends();

                    if (inCircle) {
                        circle.remove(_user);
                        lb.setImageResource(R.drawable.green_plus);
                        leftCaption.setText(getString(R.string.add_to_circle));
                    } else {
                        circle.add(_user);
                        lb.setImageResource(R.drawable.red_x);
                        leftCaption.setText(getString(R.string.uncircle));
                    }

                    cur.setFriends(circle);
                    DataManager.updateCurUser(cur);
                }
            });

            ImageButton mb = (ImageButton) findViewById(R.id.eu_button_middle);
            mb.setImageResource(R.drawable.blue_chat);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.message));
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
            mb.setImageResource(R.drawable.gray_fedora);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(_context, getString(R.string.mlady), Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.tip_fedora));
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(_context, android.R.layout.simple_list_item_1);
        List<User> circle = _user.getFriends();
        List<String> circleNames = new ArrayList<>();
        if (circle != null)
            for (int i = 0; i < circle.size(); i++)
                circleNames.add(circle.get(i).getUsername());
        adapter.addAll(circleNames);

        ImageButton rb = (ImageButton) findViewById(R.id.eu_button_right);
        rb.setImageResource(R.drawable.orange_search);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(_context);
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
        ((TextView) findViewById(R.id.eu_button_right_caption)).setText(getString(R.string.view_friends));
    }

    public void goBack(View view) {
        finish();
    }

    private class HttpUserGetter extends AsyncTask<User, Void, Void> {

        private User u;

        private void getUser(User template) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/search/user");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
                request.setHeader("Accept", "application/json");

                ObjectMapper om = new ObjectMapper();
                om.enable(SerializationFeature.INDENT_OUTPUT);
                om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                StringEntity body = new StringEntity(om.writeValueAsString(template));
                body.setContentType("application/json");
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                System.out.println("getUser url: " + url.toString());
                System.out.println("status code: " + response.getStatusLine().getStatusCode());
                System.out.println(jsonString);

                if (jsonString.length() == 0)
                    return;

                List<User> lu = om.readValue(jsonString, new TypeReference<List<User>>(){});
                u = lu.get(0);
            } catch (Exception e) {
                u = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(User... params) {
            getUser(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (u != null) {
                _user = u;
                fillDetails();
            }
            else
                Toast.makeText(_context, "Error retrieving profile", Toast.LENGTH_SHORT).show();
        }
    }
}
