package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventActivity extends Activity {

    private final Context _context = this;
    private Event _event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(getString(R.string.event));

        setContentView(R.layout.fragment_eu);

        if (!getIntent().hasExtra("event_json")) {
            Toast.makeText(_context, "Error loading event :(", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            System.out.println("before json");
            String eventJson = getIntent().getStringExtra("event_json");
            Event e = new ObjectMapper().readValue(eventJson, Event.class);
            System.out.println("here");
            new HttpEventGetter().execute(e);
        } catch (Exception e) {
            Toast.makeText(_context, "Error loading event :(", Toast.LENGTH_LONG).show();
        }
    }

    private void fillDetails() {
        if (_event == null) {
            Toast.makeText(this, "Error displaying event", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fill in the deets
        ((ImageView) findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_blue_black_x);
        ((ImageView) findViewById(R.id.eu_icon)).setImageResource(R.drawable.red_explosion);
        if (_event.getTitle() != null)
            ((TextView) findViewById(R.id.eu_title)).setText(_event.getTitle());

        if (_event.getHost() != null)
            ((TextView) findViewById(R.id.eu_subtitle)).setText(getString(R.string.hosted_by) + " " + _event.getHost().getUsername());
        else {
            Toast.makeText(_context, "This event does not have a host....weird", Toast.LENGTH_LONG).show();
            return;
        }

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        if (_event.getGames() == null || _event.getGames().size() == 0)
            games.append("\t\t\t\t(" + getString(R.string.none) + ")");
        else {
            for (Game g : _event.getGames()) {
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

        // set time description
        TextView time = ((TextView) findViewById(R.id.event_time));
        time.setVisibility(View.VISIBLE);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        StringBuilder timeString = new StringBuilder();
        timeString.append(getString(R.string.start_time) + " - ");
        if (_event.getStartTime() != null) {
            Date start = new Date(_event.getStartTime());
            String startFormatted = formatter.format(start);
            timeString.append(startFormatted);
        }
        else
            timeString.append("(N/A)");
        timeString.append("\n");

        timeString.append(getString(R.string.end_time) + " - ");
        if (_event.getEndTime() != null) {
            Date end = new Date(_event.getEndTime());
            String endFormatted = formatter.format(end);
            timeString.append(endFormatted);
        }
        else
            timeString.append("(N/A)");
        time.setText(timeString.toString());

        // set description
        StringBuilder description = new StringBuilder();
        description.append(getString(R.string.description) + "\n\t\t\t\t");
        if (_event.getDescription() != null)
            description.append(_event.getDescription());
        else
            description.append("(N/A)");
        ((TextView) findViewById(R.id.eu_description)).setText(description.toString());

        // set attendance list
        StringBuilder attendance = new StringBuilder();
        attendance.append(getString(R.string.whos_going) + "\n");
        if (_event.getUsers() == null || _event.getUsers().size() == 0)
            attendance.append("\t\t\t\tNo one is going to this event");
        else {
            for (User u : _event.getUsers())
                attendance.append("\t\t\t\t" + u.getUsername() + "\n");
            attendance.delete(attendance.length() - 1, attendance.length());
        }
        ((TextView) findViewById(R.id.event_attending_list)).setText(attendance.toString());

        // Manage buttons
        if (!_event.getHost().equals(DataManager.getCurUser())) {

            final boolean isAttending = _event.getUsers().contains(DataManager.getCurUser());

            final TextView leftCaption = (TextView) findViewById(R.id.eu_button_left_caption);
            leftCaption.setText(getString(isAttending ? R.string.unattend_event : R.string.attend_event));
            final ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(isAttending ? R.drawable.red_down_arrow : R.drawable.green_up_arrow);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<User> attending = _event.getUsers();

                    if (isAttending) {
                        attending.remove(DataManager.getCurUser());
                        lb.setImageResource(R.drawable.green_up_arrow);
                        leftCaption.setText(getString(R.string.attend_event));
                    }
                    else {
                        attending.add(DataManager.getCurUser());
                        lb.setImageResource(R.drawable.red_down_arrow);
                        leftCaption.setText(getString(R.string.unattend_event));
                    }

                    _event.setUsers(attending);
                    DataManager.updateEvent(_event);
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
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.message_host));
        }
        else {
            ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(R.drawable.blue_pencil);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String eventJson = new ObjectMapper().writeValueAsString(_event);
                        Intent i = new Intent(_context, EditEventActivity.class);
                        Bundle b = new Bundle();
                        b.putString("event_json", eventJson);
                        i.putExtras(b);
                        startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(_context, "Error going to edit event", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }                }
            });
            ((TextView) findViewById(R.id.eu_button_left_caption)).setText(getString(R.string.edit_event));

            ImageButton mb = (ImageButton) findViewById(R.id.eu_button_middle);
            mb.setImageResource(R.drawable.red_x);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // end the event?
                }
            });
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText("End event");
        }

        ImageButton rb = (ImageButton) findViewById(R.id.eu_button_right);
        rb.setImageResource(R.drawable.gray_fedora);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(_context, getString(R.string.mlady), Toast.LENGTH_SHORT).show();
            }
        });
        ((TextView) findViewById(R.id.eu_button_right_caption)).setText(getString(R.string.tip_fedora));
    }

    public void goBack(View view) {
        finish();
    }

    private class HttpEventGetter extends AsyncTask<Event, Void, Void> {

        private Event e;

        private void getEvent(Event template) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/search/event");

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

                System.out.println("getEvent url: " + url.toString());
                System.out.println("status code: " + response.getStatusLine().getStatusCode());
                System.out.println(jsonString);

                if (jsonString.length() == 0)
                    return;

                List<Event> le = om.readValue(jsonString, new TypeReference<List<Event>>(){});
                e = le.get(0);
            } catch (Exception exc) {
                e = null;
                exc.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Event... params) {
            getEvent(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (e != null) {
                _event = e;
                fillDetails();
            }
            else
                Toast.makeText(_context, "Error retrieving event", Toast.LENGTH_SHORT).show();
        }
    }
}
