package com.eric.ssbl.android.activities;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;

import com.eric.ssbl.R;
import com.eric.ssbl.android.managers.DataManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class EditEventMapActivity extends Activity {

    private LatLng _loc;
    private GoogleMap _map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Select location on map");
        setContentView(R.layout.activity_edit_event_map);

        if (getIntent().hasExtra("latitude") && getIntent().hasExtra("longitude")) {
            Bundle b = getIntent().getExtras();
            _loc = new LatLng(b.getDouble("latitude"), b.getDouble("longitude"));
        }

        _map = ((MapFragment) getFragmentManager().findFragmentById(R.id.edit_event_map)).getMap();
        _map.setMyLocationEnabled(true);
        if (_loc != null)
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_loc.latitude, _loc.longitude), 15));
        else
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(DataManager.getCurLoc(), 15));
    }

    public void setLocation(View view) {
        LatLng temp = _map.getCameraPosition().target;
        try {
            Address a = new Geocoder(this).getFromLocation(temp.latitude, temp.longitude, 1).get(0);
            EditEventActivity.setAddress(a);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
