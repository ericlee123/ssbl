package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.eric.ssbl.R;
import com.eric.ssbl.android.activities.EventActivity;
import com.eric.ssbl.android.activities.ProfileActivity;
import com.eric.ssbl.android.managers.DataManager;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChartFragment extends Fragment {

    private static View _view;
    private static GoogleMap _map;
    private static LatLng _curLoc;
    private static int _defaultZoom = 13;
    private static List<User> _nearbyUsers = new ArrayList<>();
    public static List<Event> _nearbyEvents = new ArrayList<>();
    private static HashMap<Marker, String> _eu = new HashMap<>();
    private static boolean _refreshed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DataManager.setChartFragment(this);

        if (_view != null) {
            ViewGroup parent = (ViewGroup) _view.getParent();
            if (parent != null)
                parent.removeView(_view);
        }

        try {
            _view = inflater.inflate(R.layout.fragment_chart, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
            e.printStackTrace();
        }

        if (_map == null) {
            _map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_chart)).getMap();
            _map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    Intent i;
                    Bundle b = new Bundle();
                    if (marker.getAlpha() == 0.99F) {
                        i = new Intent(getActivity(), EventActivity.class);
                        b.putString("event_json", _eu.get(marker));
                    } else {
                        i = new Intent(getActivity(), ProfileActivity.class);
                        b.putString("user_json", _eu.get(marker));
                    }
                    i.putExtras(b);
                    startActivity(i);
                }
            });
        }

        if (!_refreshed)
            refresh();

        ImageButton center = (ImageButton) _view.findViewById(R.id.chart_center);
        center.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    centerMapOnSelf();
                }
            });

        return _view;
    }

    public void refresh() {
        _refreshed = true;
        // get fresh data from datamanager
        _curLoc = DataManager.getCurLoc();
        centerMapOnSelf();
        displayElements();
    }

    public void displayElements() {
        System.out.println("display elements");
        _map.clear();

        if (_curLoc != null)
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_curLoc, _defaultZoom));
        else
            Toast.makeText(getActivity(), "Error finding current location", Toast.LENGTH_SHORT).show();

        _eu.clear();

        ObjectMapper om = new ObjectMapper();
        long now = System.currentTimeMillis();

        List<User> relevantUsers = new ArrayList<>();
        relevantUsers.addAll(DataManager.getNearbyUsers());
//        relevantUsers.removeAll(DataManager.getCurUser().getFriends());
//        relevantUsers.addAll(DataManager.getCurUser().getFriends());
        for (User u: relevantUsers) {

            int elapsed = (int) ((now - u.getLastLocationTime()) / 60000);
            String updated = "Here ";
            if (elapsed < 60)
                updated += elapsed + " minutes ago";
            else if (elapsed < 1440)
                updated += (elapsed / 60) + " hours ago";
            else
                updated += (elapsed / 1440) + " days ago";

            Marker marker = _map.addMarker(new MarkerOptions()
                    .title(u.getUsername())
                    .snippet(updated)
                    .position(new LatLng(u.getLocation().getLatitude(), u.getLocation().getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gc_controller)));

            if (u.equals(DataManager.getCurUser()))
                marker.showInfoWindow();

            try {
                _eu.put(marker, om.writeValueAsString(u));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Event> relevantEvents = new ArrayList<>();
        relevantEvents.addAll(DataManager.getNearbyEvents());
//        relevantEvents.removeAll(DataManager.getCurUser().getEvents());
//        relevantEvents.addAll(DataManager.getCurUser().getEvents());
//        relevantEvents.removeAll(DataManager.getHostingEvents());
//        relevantEvents.addAll(DataManager.getHostingEvents());
        for (Event e: relevantEvents) {

            Marker marker = _map.addMarker(new MarkerOptions()
                            .title(e.getTitle())
                            .snippet("Hosted by " + e.getHost().getUsername())
                            .position(new LatLng(e.getLocation().getLatitude(), e.getLocation().getLongitude()))
                            .alpha(0.99F)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.event)));

            try {
                _eu.put(marker, om.writeValueAsString(e));
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

    }

    public void centerMapOnSelf() {
        if (_curLoc == null || _map == null)
            Toast.makeText(getActivity(), getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
        else
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_curLoc, _defaultZoom));
    }

    public static void clearData() {
        _refreshed = false;
        _map = null;

        if (_nearbyUsers != null)
            _nearbyUsers.clear();

        if (_nearbyEvents != null)
            _nearbyEvents.clear();

        if (_eu != null)
            _eu.clear();
    }
}