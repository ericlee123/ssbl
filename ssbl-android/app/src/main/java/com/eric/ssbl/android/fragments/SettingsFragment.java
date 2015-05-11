package com.eric.ssbl.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eric.ssbl.R;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View _view = inflater.inflate(R.layout.activity_settings, container, false);

        return _view;
    }

    @Override
    public void onStop() {
        System.out.println("Settings Fragment stopped");
    }

    @Override
    public void onDestroyView() {
        System.out.println("Settings Fragment view destroyed");
    }

    @Override
    public void onDestroy() {
        System.out.println("Settings Fragment destroyed");
    }
}
