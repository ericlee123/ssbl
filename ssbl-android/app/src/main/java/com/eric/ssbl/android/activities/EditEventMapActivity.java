package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import com.eric.ssbl.R;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class EditEventMapActivity extends Activity {

    private static LatLng _setLocation;
    private static GoogleMap _map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Select location on map");
        setContentView(R.layout.activity_edit_event_map);

        _map = ((MapFragment) getFragmentManager().findFragmentById(R.id.edit_event_map)).getMap();
        _map.setMyLocationEnabled(true);
        if (_setLocation == null)
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(ChartFragment.getCurLoc(), 15));
        else
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_setLocation, 15));
    }

    public void setLocation(View view) {
        _setLocation = _map.getCameraPosition().target;

        try {
            Address a = new Geocoder(this).getFromLocation(_setLocation.latitude, _setLocation.longitude, 1).get(0);
            EditEventActivity.setLocation(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
