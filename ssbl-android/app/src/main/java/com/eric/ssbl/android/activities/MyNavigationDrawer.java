package com.eric.ssbl.android.activities;

import android.os.Bundle;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.ProfileFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MyNavigationDrawer extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        MaterialSection map = newSection("Map", new ChartFragment());
        MaterialSection profile = newSection("Profile", new ProfileFragment());

        this.addSection(map);
        this.addSection(profile);

        MaterialAccount account = new MaterialAccount(this.getResources(),"Sanic Weedhog","gotta.go.fast@sanic.com", R.drawable.honey, R.drawable.nav_bar_cover_photo);
        this.addAccount(account);
    }
}