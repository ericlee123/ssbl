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

import org.json.JSONObject;

import java.io.File;
import java.util.Scanner;

/**
 * set location public/private
 * radius 1 5 10 20 50 100 give me the whole damn map
 */
public class SettingsActivity extends Activity {

    private static File _settingsFile;
    private static JSONObject _settings;

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
        if (_settingsFile == null || _settings == null)
            initFiles();
        try {
            ((CheckBox) findViewById(R.id.settings_location_private)).setChecked(_settings.getBoolean("location_private"));
            mapRadius.setSelection(_settings.getInt("map_radius_index"));
        } catch (Exception e) {
            _settingsFile.delete();
            Toast.makeText(this, "Error loading settings", Toast.LENGTH_LONG).show();
        }
    }

    private static void initFiles() {
        _settingsFile = new File("settings");
        if (_settingsFile.exists()) {
            try {
                Scanner scan = new Scanner(_settingsFile);
                _settings = new JSONObject(scan.nextLine());
                scan.close();
            } catch (Exception e) {
                _settingsFile.delete();
//                Toast.makeText(_context, "Error loading settings", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
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
        DataManager.saveSettings(_settings);
    }

    public void goBack(View view) {
        finish();
    }
}
