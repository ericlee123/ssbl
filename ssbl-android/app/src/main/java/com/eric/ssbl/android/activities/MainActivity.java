package com.eric.ssbl.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.EventListFragment;
import com.eric.ssbl.android.fragments.InboxFragment;
import com.eric.ssbl.android.fragments.NotificationsFragment;
import com.eric.ssbl.android.fragments.ProfileFragment;
import com.eric.ssbl.android.managers.DataManager;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        this.disableLearningPattern();
        MaterialAccount account = new MaterialAccount(this.getResources(), DataManager.getCurUser().getUsername(), DataManager.getCurUser().getEmail(), R.drawable.honey, R.drawable.md_tangents);
        this.addAccount(account);

        MaterialSection map = newSection(getString(R.string.map), new ChartFragment());
        MaterialSection profile = newSection(getString(R.string.profile), new ProfileFragment());
        MaterialSection inbox = newSection(getString(R.string.inbox), new InboxFragment());
        MaterialSection notifs = newSection(getString(R.string.notifications), new NotificationsFragment());
        MaterialSection events = newSection(getString(R.string.events), new EventListFragment());

        // divisor

        this.addSection(map);
        this.addSection(profile);
        this.addSection(notifs);
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
                // refresh all fragments
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_log_out:
                // first ask to verify?
                ChartFragment.reset();
                DataManager.clearData();
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
}