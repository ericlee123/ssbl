package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditEventActivity extends Activity {

    private Context _context = this;
    private TextView _locationStatus;
    private ProgressDialog _loading;
    private Event _event;
    private Calendar _startCalendar;
    private Calendar _endCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        if (getIntent().hasExtra("event_json")) {
            try {
                _event = new ObjectMapper().readValue(getIntent().getStringExtra("event_json"), Event.class);
            } catch (Exception e) {
                Toast.makeText(_context, "Error loading old event details", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(_event == null ? R.string.create_event : R.string.edit_event);
        setContentView(R.layout.activity_edit_event);

        if (_event != null) {

            ((EditText) findViewById(R.id.edit_event_event_title)).setText(_event.getTitle());

            // set location

            for (Game g: _event.getGames()) {
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

            ((EditText) findViewById(R.id.edit_event_description)).setText(_event.getDescription());
            ((CheckBox) findViewById(R.id.edit_event_private)).setChecked(!_event.isPublic());
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

    public void setEventStartTime(View view) {

        if (_startCalendar == null)
            _startCalendar = Calendar.getInstance();

        final Calendar newDate = Calendar.getInstance();
        final TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                _startCalendar = newDate;
            }
        }, _startCalendar.get(Calendar.HOUR_OF_DAY), _startCalendar.get(Calendar.MINUTE), false);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.set(year, monthOfYear, dayOfMonth);
                tpd.show();
            }

        }, _startCalendar.get(Calendar.YEAR), _startCalendar.get(Calendar.MONTH), _startCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setEventEndTime(View view) {

        if (_endCalendar == null)
            _endCalendar = Calendar.getInstance();

        final Calendar newDate = Calendar.getInstance();
        final TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                _endCalendar = newDate;
            }
        }, _startCalendar.get(Calendar.HOUR_OF_DAY), _startCalendar.get(Calendar.MINUTE), false);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.set(year, monthOfYear, dayOfMonth);
                tpd.show();
            }

        }, _endCalendar.get(Calendar.YEAR), _endCalendar.get(Calendar.MONTH), _endCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void saveEvent(View view) {

        if (_event == null)
            _event = new Event();

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
        _event.setGames(games);

        // set time

        _event.setDescription(((EditText) findViewById(R.id.edit_event_description)).getText().toString());
        _event.setPublic(!((CheckBox) findViewById(R.id.edit_event_private)).isChecked());

        _loading = ProgressDialog.show(this, "Updating event...", getString(R.string.chill_out), true);
        DataManager.updateEvent(_event);
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
}
