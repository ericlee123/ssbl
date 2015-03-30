package com.eric.ssbl.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.EventListFragment;
import com.eric.ssbl.android.fragments.InboxFragment;
import com.eric.ssbl.android.fragments.NotificationsFragment;
import com.eric.ssbl.android.fragments.UserFragment;
import com.eric.ssbl.android.managers.DataManager;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer {

    private ProgressDialog _loading;

    @Override
    public void init(Bundle bundle) {

        this.disableLearningPattern();
        MaterialAccount account = new MaterialAccount(this.getResources(), DataManager.getCurrentUser().getUsername(), DataManager.getCurrentUser().getEmail(), R.drawable.smash_bros_icon, R.drawable.md_gray);
        this.addAccount(account);

        MaterialSection map = newSection(getString(R.string.map), new ChartFragment());
        MaterialSection profile = newSection(getString(R.string.profile), new UserFragment());
        MaterialSection inbox = newSection(getString(R.string.inbox), new InboxFragment());
//        MaterialSection notifs = newSection(getString(R.string.notifications), new NotificationsFragment());
        MaterialSection events = newSection(getString(R.string.events), new EventListFragment());

        // divisor

        this.addSection(map);
        this.addSection(profile);
//        this.addSection(notifs);
        this.addSection(inbox);
        this.addSection(events);

        this.addDivisor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.action_refresh:
                _loading = ProgressDialog.show(this, "Refreshing...", "Just sit back and relax ;)", true);
                new EverythingRefresher().execute();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_log_out:
                DataManager.clearData();
                ChartFragment.clearData();
                EventListFragment.clearData();
                InboxFragment.clearData();
                NotificationsFragment.clearData();
                UserFragment.clearData();

                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class EverythingRefresher extends AsyncTask<Void, Void, Void> {

        private void doStuff() {
            new DataManager().refreshCurLoc();
            DataManager.reloadConversations();
        }

        @Override
        protected Void doInBackground(Void... params) {
            doStuff();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            DataManager.refreshAllFragments();
            _loading.dismiss();
        }
    }
}