package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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
import com.fasterxml.jackson.databind.ObjectMapper;

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

        Integer id;
        try {
            id = getIntent().getExtras().getInt("event_id");
        } catch (NullPointerException e) {
            Toast.makeText(this, "Error displaying event", Toast.LENGTH_SHORT).show();
            return;
        }
        Event temp = new Event();
        temp.setEventId(id);
        new HttpEventGetter().execute(temp);
    }

    private void fillDetails() {
        if (_event == null) {
            Toast.makeText(this, "Error displaying event", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fill in the deets
        ((ImageView) findViewById(R.id.eu_cover_photo)).setImageResource(R.drawable.md_blue_black_x);
        ((ImageView) findViewById(R.id.eu_icon)).setImageResource(R.drawable.red_explosion);
        ((TextView) findViewById(R.id.eu_title)).setText(_event.getTitle());
        ((TextView) findViewById(R.id.eu_subtitle)).setText(getString(R.string.hosted_by) + " " + _event.getHost().getUsername());

        StringBuilder games = new StringBuilder();
        games.append(getString(R.string.games) + "\n");
        for (Game g: _event.getGames()) {
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

        // set time description
        TextView time = ((TextView) findViewById(R.id.event_time));
        time.setVisibility(View.VISIBLE);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date start = new Date(_event.getStartTime());
        Date end = new Date(_event.getEndTime());
        String startFormatted = formatter.format(start);
        String endFormatted = formatter.format(end);
        StringBuilder timeString = new StringBuilder();
        timeString.append(getString(R.string.start_time) + " - " + startFormatted + "\n");
        timeString.append(getString(R.string.end_time) + " - " + endFormatted);

        time.setText(timeString.toString());

        // set description
        StringBuilder description = new StringBuilder();
        description.append(getString(R.string.description) + "\n\t\t\t\t");
        description.append(_event.getDescription());
        ((TextView) findViewById(R.id.eu_description)).setText(description.toString());

        // set attendance list
        StringBuilder attendance = new StringBuilder();
        attendance.append(getString(R.string.whos_going) + "\n");
        if (_event.getUsers() == null)
            attendance.append("\t\t\t\tNo one is going to this event");
        else {
            for (User u : _event.getUsers())
                attendance.append("\t\t\t\t" + u.getUsername() + "\n");
            attendance.delete(attendance.length() - 1, attendance.length());
        }
        ((TextView) findViewById(R.id.event_attending_list)).setText(attendance.toString());

        // Manage buttons
        if (!_event.getHost().equals(DataManager.getCurUser())) {

            ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(R.drawable.green_up_arrow);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(_context, "leftButton", Toast.LENGTH_SHORT).show();
                }
            });
            ((TextView) findViewById(R.id.eu_button_left_caption)).setText(getString(R.string.attend_event));

            ImageButton mb = (ImageButton) findViewById(R.id.eu_button_middle);
            mb.setImageResource(R.drawable.blue_chat);
            mb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new message
                }
            });
            ((TextView) findViewById(R.id.eu_button_middle_caption)).setText(getString(R.string.message_host));

            ImageButton rb = (ImageButton) findViewById(R.id.eu_button_right);
            rb.setImageResource(R.drawable.orange_gps);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open up map
                }
            });
            ((TextView) findViewById(R.id.eu_button_right_caption)).setText(R.string.show_on_map);
        }
        else {
            ImageButton lb = (ImageButton) findViewById(R.id.eu_button_left);
            lb.setImageResource(R.drawable.blue_pencil);
            lb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(_context, "leftButton", Toast.LENGTH_SHORT).show();
                }
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

                // encode the user template into the request
                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(template));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

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
