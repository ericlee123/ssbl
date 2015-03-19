package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Settings;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * turn notifications on/off
 * set location public/private
 * radius 1 5 10 20 50 100 give me the whole damn map
 */
public class SettingsActivity extends Activity {

    private Context _context = this;
    private Settings _settings;
    private File _settingsFile;

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
        _settingsFile = new File(getFilesDir(), "settings");
        if (_settingsFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(_settingsFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                _settings = (Settings) ois.readObject();
                ois.close();
                fis.close();
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.error_loading_settings), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            ((CheckBox) findViewById(R.id.settings_notifications)).setChecked(_settings.getAlert());
            ((CheckBox) findViewById(R.id.settings_location_private)).setChecked(_settings.getLocationPrivate());
            mapRadius.setSelection(_settings.getMapRadiusIndex());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // save the login info and send it to the server
        boolean alertMe = ((CheckBox) findViewById(R.id.settings_notifications)).isChecked();
        boolean lp = ((CheckBox) findViewById(R.id.settings_location_private)).isChecked();
        int temp = ((Spinner) findViewById(R.id.settings_map_radius)).getSelectedItemPosition();
        _settings = new Settings(alertMe, lp, temp);

        try {
            _settingsFile.delete();
            _settingsFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(_settingsFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(_settings);
            oos.close();
            fos.close();
            Toast.makeText(this, getString(R.string.saved_settings), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_saving_settings), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        User u = DataManager.getCurUser();
        u.setPrivate(lp);
        new HttpPrivacyUpdater().execute(u);

    }

    public static double getRadius() {
        return 5.0;
    }

    public void goBack(View view) {
        finish();
    }

    private class HttpPrivacyUpdater extends AsyncTask<User, Void, Void> {

        private User updated;

        private void updatePrivacy(User edited) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/edit/user");

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

                updated = om.readValue(jsonString, User.class);

            } catch (Exception e) {
                updated = null;
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(User... params) {

            updatePrivacy(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {

            if (updated != null)
                DataManager.setCurUser(updated);
        }
    }
}
