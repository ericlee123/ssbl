package com.eric.ssbl.android.fragments;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.eric.ssbl.R;

/**
 * Created by eric on 3/1/15.
 */
public class Fragment extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
