package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.eric.ssbl.android.pojos.Location;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditEventActivity extends Activity {

    private Context _context = this;

    private EditText _title;
    private static Address _address;
    private static TextView _locationStatus;
    private CheckBox _ssb64;
    private CheckBox _melee;
    private CheckBox _brawl;
    private CheckBox _pm;
    private CheckBox _smash4;

    private Calendar _startCalendar;
    private Calendar _endCalendar;

    private EditText _description;
    private CheckBox _isPrivate;

    private ProgressDialog _loading;
    private Event _event;

    private boolean _new;

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

        _new = (_event == null);
        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(_event == null ? R.string.create_event : R.string.edit_event);
        setContentView(R.layout.activity_edit_event);

        _title = (EditText) findViewById(R.id.edit_event_event_title);
        _locationStatus = (TextView) findViewById(R.id.edit_event_location_status);
        _ssb64 = (CheckBox) findViewById(R.id.edit_event_games_ssb64);
        _melee = (CheckBox) findViewById(R.id.edit_event_games_melee);
        _brawl = (CheckBox) findViewById(R.id.edit_event_games_brawl);
        _pm = (CheckBox) findViewById(R.id.edit_event_games_pm);
        _smash4 = (CheckBox) findViewById(R.id.edit_event_games_smash4);
        _description = (EditText) findViewById(R.id.edit_event_description);
        _isPrivate = (CheckBox) findViewById(R.id.edit_event_private);

        _startCalendar = Calendar.getInstance();
        _endCalendar = Calendar.getInstance();

        if (_event != null) {

            if (_event.getTitle() != null)
                _title.setText(_event.getTitle());

            if (_event.getLocation() != null) {
                try {
                    _address = new Geocoder(this).getFromLocation(_event.getLocation().getLatitude(), _event.getLocation().getLongitude(), 1).get(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (_event.getGames() != null) {
                for (Game g : _event.getGames()) {
                    if (g.equals(Game.SSB64))
                        _ssb64.setChecked(true);
                    else if (g.equals(Game.MELEE))
                        _melee.setChecked(true);
                    else if (g.equals(Game.BRAWL))
                        _brawl.setChecked(true);
                    else if (g.equals(Game.PM))
                        _pm.setChecked(true);
                    else if (g.equals(Game.SMASH4))
                        _smash4.setChecked(true);
                }
            }

            if (_event.getStartTime() != null)
                _startCalendar.setTimeInMillis(_event.getStartTime());
            if (_event.getEndTime() != null)
                _endCalendar.setTimeInMillis(_event.getEndTime());

            if (_event.getDescription() != null)
                _description.setText(_event.getDescription());
            if (_event.isPublic() != null)
                _isPrivate.setChecked(!_event.isPublic());
        }

        updateLocationText();
        updateTimeButtonText();
    }

    public void setLocationMap(View view) {
        Intent i = new Intent(this, EditEventMapActivity.class);
        Bundle b = new Bundle();
        if (_address != null) {
            b.putDouble("latitude", _address.getLatitude());
            b.putDouble("longitude", _address.getLongitude());
        }
        i.putExtras(b);
        startActivity(i);
    }

    public static void setAddress(Address a) {
        _address = a;
        updateLocationText();
    }

    public void setLocationAddress(View view) {

        LayoutInflater li = LayoutInflater.from(this);
        final View _enterAddressPrompt = li.inflate(R.layout.prompt_enter_address, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(_context);
        adb
                .setView(_enterAddressPrompt)
                .setTitle("Enter address")
                .setCancelable(true)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String textAddress = ((EditText) _enterAddressPrompt.findViewById(R.id.prompt_enter_address_text)).getText().toString();

                        try {
                            Geocoder coder = new Geocoder(_context);
                            List<Address> addresses = coder.getFromLocationName(textAddress, 1);
                            if (addresses.size() >= 1) {
                                _address = addresses.get(0);
                                updateLocationText();
                            } else
                                Toast.makeText(_context, "Unable to determine location from input", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            Toast.makeText(_context, "Error finding exact location through address", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        adb.create().show();
    }

    private static void updateLocationText() {
        if (_address == null) {
            _locationStatus.setText("Location: (not set)");
            return;
        }

        StringBuilder temp = new StringBuilder("Location: ");
        for (int i = 0; i <= _address.getMaxAddressLineIndex(); i++)
            temp.append(_address.getAddressLine(i) + "\n");
        _locationStatus.setText(temp.toString());
    }

    public void setEventStartTime(View view) {

        if (_startCalendar == null)
            _startCalendar = Calendar.getInstance();

        final Calendar newDate = Calendar.getInstance();
        final TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                newDate.set(newDate.get(Calendar.YEAR), newDate.get(Calendar.MONTH), newDate.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);

                if (_endCalendar != null && newDate.getTimeInMillis() >= _endCalendar.getTimeInMillis()) {
                    Toast.makeText(_context, "Please choose a time before the end time", Toast.LENGTH_LONG).show();
                    return;
                }
                _startCalendar = newDate;
                updateTimeButtonText();
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

                if (_startCalendar != null && newDate.getTimeInMillis() <= _startCalendar.getTimeInMillis()) {
                    Toast.makeText(_context, "Please choose a time after the start time", Toast.LENGTH_LONG).show();
                    return;
                }
                _endCalendar = newDate;
                updateTimeButtonText();
            }
        }, _startCalendar.get(Calendar.HOUR_OF_DAY), _startCalendar.get(Calendar.MINUTE), false);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.set(year, monthOfYear, dayOfMonth);
                tpd.show();
            }

        }, _endCalendar.get(Calendar.YEAR), _endCalendar.get(Calendar.MONTH), _endCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTimeButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

        StringBuilder startTime = new StringBuilder("Start time: ");
        if (_startCalendar == null)
            startTime.append("(not set)");
        else
            startTime.append(sdf.format(_startCalendar.getTime()));
        ((Button) findViewById(R.id.edit_event_start_time_button)).setText(startTime);

        StringBuilder endTime = new StringBuilder("End time: ");
        if (_endCalendar == null)
            endTime.append("(not set)");
        else
            endTime.append(sdf.format(_endCalendar.getTime()));
        ((Button) findViewById(R.id.edit_event_end_time_button)).setText(endTime);
    }

    public void saveEvent(View view) {

        if (_title.getText().toString().length() == 0) {
            Toast.makeText(_context, "Please enter an event title", Toast.LENGTH_LONG).show();
            return;
        }

        if (_address == null) {
            Toast.makeText(_context, "Please enter a location", Toast.LENGTH_LONG).show();
            return;
        }

        if (_startCalendar == null) {
            Toast.makeText(_context, "Please enter a start time", Toast.LENGTH_LONG).show();
            return;
        }
        if (_endCalendar == null) {
            Toast.makeText(_context, "Please enter a end time", Toast.LENGTH_LONG).show();
            return;
        }

        if (_event == null)
            _event = new Event();

        _event.setTitle(_title.getText().toString());
        _event.setHost(DataManager.getCurUser());
        _event.setUsers(new ArrayList<User>());

        Location l = new Location();
        l.setLatitude(_address.getLatitude());
        l.setLongitude(_address.getLongitude());
        _event.setLocation(l);

        List <Game> games = new ArrayList<>();
        if (_ssb64.isChecked())
            games.add(Game.SSB64);
        if (_melee.isChecked())
            games.add(Game.MELEE);
        if (_brawl.isChecked())
            games.add(Game.BRAWL);
        if (_pm.isChecked())
            games.add(Game.PM);
        if (_smash4.isChecked())
            games.add(Game.SMASH4);
        _event.setGames(games);

        _event.setStartTime(_startCalendar.getTimeInMillis());
        _event.setEndTime(_endCalendar.getTimeInMillis());

        _event.setDescription(_description.getText().toString());
        _event.setPublic(!_isPrivate.isChecked());

        _loading = ProgressDialog.show(this, "Saving event...", getString(R.string.chill_out), true);
        new HttpEventUpdater().execute(_event);

        // Reset the static fields
        _address = null;
        _locationStatus = null;
    }

    public void goBack(View view) {
        finish();
    }

    private class HttpEventUpdater extends AsyncTask<Event, Void, Void> {

        private Event updated;

        @Override
        protected Void doInBackground(Event... params) {
            updated = DataManager.httpUpdateEvent(params[0], _new);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();
            if (updated != null) {
                Toast.makeText(_context, "Event saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(_context, "Error saving event :(", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
