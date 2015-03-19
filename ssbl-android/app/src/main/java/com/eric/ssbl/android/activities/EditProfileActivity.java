package com.eric.ssbl.android.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends Activity {

    private Context _context = this;
    private ProgressDialog _loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        View abv = getLayoutInflater().inflate(R.layout.action_bar_back, null);
        ab.setCustomView(abv);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((TextView) findViewById(R.id.action_bar_title)).setText("Edit Profile");

        setContentView(R.layout.activity_edit_profile);

        User u = DataManager.getCurUser();
        List<Game> lg = u.getGames();
        for (Game g: lg) {
            if (g.equals(Game.SSB64))
                ((CheckBox) findViewById(R.id.edit_profile_games_ssb64)).setChecked(true);
            else if (g.equals(Game.MELEE))
                ((CheckBox) findViewById(R.id.edit_profile_games_melee)).setChecked(true);
            else if (g.equals(Game.BRAWL))
                ((CheckBox) findViewById(R.id.edit_profile_games_brawl)).setChecked(true);
            else if (g.equals(Game.PM))
                ((CheckBox) findViewById(R.id.edit_profile_games_pm)).setChecked(true);
            else if (g.equals(Game.SMASH4))
                ((CheckBox) findViewById(R.id.edit_profile_games_smash4)).setChecked(true);
        }

        ((EditText) findViewById(R.id.edit_profile_bio)).setText(u.getBlurb());
    }

    public void saveProfile(View view) {

        _loading = ProgressDialog.show(_context, "Updating profile...", getString(R.string.chill_out), true);

        User u = DataManager.getCurUser();
        List<Game> games = new ArrayList<>();
        if (((CheckBox) findViewById(R.id.edit_profile_games_ssb64)).isChecked())
            games.add(Game.SSB64);
        if (((CheckBox) findViewById(R.id.edit_profile_games_melee)).isChecked())
            games.add(Game.MELEE);
        if (((CheckBox) findViewById(R.id.edit_profile_games_brawl)).isChecked())
            games.add(Game.BRAWL);
        if (((CheckBox) findViewById(R.id.edit_profile_games_pm)).isChecked())
            games.add(Game.PM);
        if (((CheckBox) findViewById(R.id.edit_profile_games_smash4)).isChecked())
            games.add(Game.SMASH4);
        u.setGames(games);

        String desc = ((EditText) findViewById(R.id.edit_profile_bio)).getText().toString();
        u.setBlurb(desc);

        new HttpProfileUpdater().execute(u);
    }

    public void goBack(View view) {
        finish();
    }

    private class HttpProfileUpdater extends AsyncTask<User, Void, Void> {

        private User updated;

        private void updateProfile(User edited) {

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

            updateProfile(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            _loading.dismiss();

            if (updated != null) {
                Toast.makeText(_context, "Profile saved!", Toast.LENGTH_SHORT).show();
                DataManager.setCurUser(updated);
                finish();
            } else {
                Toast.makeText(_context, "Error updating profile :(", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
