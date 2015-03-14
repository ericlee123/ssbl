package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.ProfileActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChartFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{

    private static View _view;
    private static boolean _init;
    private static GoogleMap _map;
    private GoogleApiClient _googleApiClient;
    private LatLng _curLoc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (_view != null) {
            ViewGroup parent = (ViewGroup) _view.getParent();
            if (parent != null)
                parent.removeView(_view);
        }
        try {
            _view = inflater.inflate(R.layout.fragment_chart, container, false);
            if (!_init)
                init();
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        ImageButton refresh = (ImageButton) _view.findViewById(R.id.chart_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(null);
            }
        });

        return _view;
    }

    private void init() {
        _init = true;
        _map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_chart)).getMap();
        buildGoogleApiClient();

        _map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent i = new Intent(getActivity(), ProfileActivity.class);
                startActivity(i);
            }
        });
    }

    // put all of these map operations in a class?

    /**
     * This is the beginning of the sequence to create a GoogleApiClient, connect, and
     * then update the current location.
     */
    private void buildGoogleApiClient() {
        _googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (_googleApiClient != null)
            _googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        updateCurLoc();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {

    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    private void updateCurLoc() {
        // coarse location vs exact vs no location?
        Location here = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        if (here != null) {
            _curLoc = new LatLng(here.getLatitude(), here.getLongitude());
            // Update location to database
            displayElements();
        }
        else {
            Toast.makeText(getActivity(), "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
        }
    }

    public void displayElements() {
        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_curLoc, 15));

        _map.addMarker(new MarkerOptions()
                        .title("test marker")
                        .snippet("this is a test marker")
                        .position(new LatLng(30.288203, -97.739908)));
    }

    public void refresh(View view) {
        Toast.makeText(getActivity(), "Refreshing...", Toast.LENGTH_SHORT).show();
    }
}