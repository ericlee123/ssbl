package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.User;

import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * turn notifications on/off
 * set location public/private
 * radius 1 5 10 20 50 100 give me the whole damn map
 */
public class SettingsActivity extends Activity {

    private File _settingsFile;
    private JSONObject _settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        ((TextView) abv.findViewById(R.id.action_bar_title)).setText(getString(R.string.settings));

        setContentView(R.layout.activity_settings);

        Spinner mapRadius = (Spinner) findViewById(R.id.settings_map_radius);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.map_radii_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapRadius.setAdapter(adapter);

        // Load previously saved settings
        if (_settingsFile == null || _settings == null) {
            _settingsFile = new File(getFilesDir(), "settings");
            if (_settingsFile.exists()) {
                try {
                    Scanner scan = new Scanner(_settingsFile);
                    _settings = new JSONObject(scan.nextLine());
                    scan.close();
                } catch (Exception e) {
                    _settingsFile.delete();
                    Toast.makeText(this, getString(R.string.error_loading_settings), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else {
                try {
                    _settingsFile.createNewFile();
                    _settings = new JSONObject();

                    _settings.put("location_private", false);
                    _settings.put("map_radius_index", 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            ((CheckBox) findViewById(R.id.settings_location_private)).setChecked(_settings.getBoolean("location_private"));
            mapRadius.setSelection(_settings.getInt("map_radius_index"));
        } catch (Exception e) {
            _settingsFile.delete();
            Toast.makeText(this, "Error loading settings", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // save the login info and send it to the server
        boolean lp = ((CheckBox) findViewById(R.id.settings_location_private)).isChecked();
        int index = ((Spinner) findViewById(R.id.settings_map_radius)).getSelectedItemPosition();
        try {
            _settings = new JSONObject();
            _settings.put("location_private", lp);
            _settings.put("map_radius_index", index);
        } catch (Exception e) {
            Toast.makeText(this, "Error saving settings", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            _settingsFile.delete();
            _settingsFile.createNewFile();

            PrintWriter pw = new PrintWriter(_settingsFile);
            pw.print(_settings.toString());
            pw.close();

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_saving_settings), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        User u = DataManager.getCurUser();
        u.setPrivate(lp);
        DataManager.updateCurUser(u);

    }

    public double getRadius() {

        if (_settingsFile == null || _settings == null) {
            _settingsFile = new File(getFilesDir(), "settings");
            if (_settingsFile.exists()) {
                try {
                    Scanner scan = new Scanner(_settingsFile);
                    _settings = new JSONObject(scan.nextLine());
                    scan.close();
                } catch (Exception e) {
                    _settingsFile.delete();
                    Toast.makeText(this, getString(R.string.error_loading_settings), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            else {
                try {
                    _settingsFile.createNewFile();
                    _settings = new JSONObject();

                    _settings.put("location_private", false);
                    _settings.put("map_radius_index", 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int index = 0;
        try {
            index = _settings.getInt("map_radius_index");
        } catch (Exception e) {
            _settingsFile.delete();
        }
        if (index == 0)
            return 1.0;
        if (index == 1)
            return 5.0;
        if (index == 2)
            return 10.0;
        if (index == 3)
            return 20.0;
        if (index == 4)
            return 50.0;
        if (index == 5)
            return 100.0;
        if (index == 6)
            return -1.0;

        return 10.0;
    }

    public void goBack(View view) {
        finish();
    }
}
