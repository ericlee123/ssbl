package com.eric.ssbl.android.activities;

import android.os.Bundle;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.Fragment;
import com.eric.ssbl.android.fragments.InboxFragment;
import com.eric.ssbl.android.fragments.ProfileFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        this.disableLearningPattern();
        MaterialAccount account = new MaterialAccount(this.getResources(),"Sanic Weedhog","gotta.go.fast@sanic.com", R.drawable.honey, R.drawable.material_design);
        this.addAccount(account);

        MaterialSection map = newSection("Map", new ChartFragment());
        MaterialSection profile = newSection("Profile", new ProfileFragment());
        MaterialSection inbox = newSection("Inbox", new InboxFragment());
        // divisor
        MaterialSection settings = newSection("Settings", new Fragment());

        this.addSection(map);
        this.addSection(profile);
        this.addSection(inbox);

        this.addDivisor();

        this.addSection(settings);
    }
}