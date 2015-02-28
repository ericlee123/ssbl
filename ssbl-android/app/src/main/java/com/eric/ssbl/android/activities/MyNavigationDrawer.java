package com.eric.ssbl.android.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;

import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MyNavigationDrawer extends MaterialNavigationDrawer {

    @Override
    public void init(Bundle bundle) {
        MaterialSection section = newSection("Section 1", new FragmentIndex());
        MaterialSection section1 = newSection("Map", new ChartFragment());

        this.addSection(section);
        this.addSection(section1);

        MaterialAccount account = new MaterialAccount(this.getResources(),"Milady","tips.fedora@reddit.com", R.drawable.jupiter, R.drawable.uranus);
        this.addAccount(account);
    }

    public static class FragmentIndex extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            TextView text = new TextView(this.getActivity());
            text.setText("Section");
            text.setGravity(Gravity.CENTER);
            return text;

        }
    }
}