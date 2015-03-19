package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Game;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditEventActivity extends Activity {

    private Context _context = this;
    private TextView _locationStatus;
    private int _eventId;
    private ProgressDialog _loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        Bundle extras = getIntent().getExtras();
        _eventId = extras.getInt("event_id");
        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(getString(_eventId == -1 ? R.string.create_event : R.string.edit_event));
        setContentView(R.layout.activity_edit_event);

        if (_eventId != -1) {
            Event e = DataManager.getEventById(_eventId);
            if (e == null) {
                Toast.makeText(_context, "Error loading event details", Toast.LENGTH_SHORT).show();
                return;
            }

            ((EditText) findViewById(R.id.edit_event_event_title)).setText(e.getTitle());

            // set location

            for (Game g: e.getGames()) {
                if (g.equals(Game.SSB64))
                    ((CheckBox) findViewById(R.id.edit_event_games_ssb64)).setChecked(true);
                else if (g.equals(Game.MELEE))
                    ((CheckBox) findViewById(R.id.edit_event_games_melee)).setChecked(true);
                else if (g.equals(Game.BRAWL))
                    ((CheckBox) findViewById(R.id.edit_event_games_brawl)).setChecked(true);
                else if (g.equals(Game.PM))
                    ((CheckBox) findViewById(R.id.edit_event_games_pm)).setChecked(true);
                else if (g.equals(Game.SMASH4))
                    ((CheckBox) findViewById(R.id.edit_event_games_smash4)).setChecked(true);
            }

            // set time

            ((EditText) findViewById(R.id.edit_event_description)).setText(e.getDescription());
            ((CheckBox) findViewById(R.id.edit_event_public)).setChecked(!e.isPublic());
        }
        else {

            _locationStatus = (TextView) findViewById(R.id.edit_event_location_status);
            _locationStatus.setText(getString(R.string.location) + ": (not set)");
        }
    }

    public void setLocationMap(View view) {
        // Open the interactive map
    }

    public void setLocationAddress(View view) {
        // open address dialog
    }

    public void setDateTime(View view) {
        // open date time picker
    }

    public void saveEvent(View view) {
        // save the event details
        Event e = new Event();

        if (_eventId != -1)
            e.setEventId(_eventId);

        // set locaiton

        List<Game> games = new ArrayList<>();
        if (((CheckBox) findViewById(R.id.edit_event_games_ssb64)).isChecked())
            games.add(Game.SSB64);
        if (((CheckBox) findViewById(R.id.edit_event_games_melee)).isChecked())
            games.add(Game.MELEE);
        if (((CheckBox) findViewById(R.id.edit_event_games_brawl)).isChecked())
            games.add(Game.BRAWL);
        if (((CheckBox) findViewById(R.id.edit_event_games_pm)).isChecked())
            games.add(Game.PM);
        if (((CheckBox) findViewById(R.id.edit_event_games_smash4)).isChecked())
            games.add(Game.SMASH4);
        e.setGames(games);

        // set time

        e.setDescription(((EditText) findViewById(R.id.edit_event_description)).getText().toString());
        e.setPublic(!((CheckBox) findViewById(R.id.edit_event_private)).isChecked());

        _loading = ProgressDialog.show(this, "Updating event...", getString(R.string.chill_out), true);
        new HttpEventUpdater().execute(e);
    }

    public void goBack(View view) {
        finish();
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
        }
    }

    private class HttpEventUpdater extends AsyncTask<Event, Void, Void> {

        private Event updated;

        private void updateEvent(Event edited) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/edit/event");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(edited));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                updated = om.readValue(jsonString, Event.class);

            } catch (Exception e) {
                updated = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Event... params) {

            updateEvent(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();

            if (updated != null) {
                Toast.makeText(_context, "Event saved!", Toast.LENGTH_SHORT).show();
                DataManager.updateEvent(updated);
                finish();
            } else {
                Toast.makeText(_context, "Error updating event :(", Toast.LENGTH_LONG).show();
            }
        }
    }
}
