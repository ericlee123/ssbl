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
import com.eric.ssbl.android.pojos.Location;
import com.eric.ssbl.android.pojos.User;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChartFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener {

    private View _view;
    private static GoogleMap _map;
    private GoogleApiClient _googleApiClient;
    private LatLng _curLoc;
    private static int _defaultZoom = 13;
    private static List<User> _nearbyUsers = new ArrayList<>();
    public static List<Event> _nearbyEvents = new ArrayList<>();
    private static HashMap<Marker, String> _eu = new HashMap<>();
    private static boolean _refreshed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

        if (_googleApiClient == null)
            buildGoogleApiClient();

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (_map != null)
            _map.clear();

        _refreshed = false;

        _eu.clear();
        _nearbyUsers.clear();
        _nearbyEvents.clear();
    }

    private void refresh() {
        _refreshed = true;
        _map = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.fragment_chart)).getMap();
        buildGoogleApiClient();

        _map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent i;
                Bundle b = new Bundle();
                if (marker.getAlpha() == 0.99F) {
                    i = new Intent(getActivity(), EventActivity.class);
                    b.putString("event_json", _eu.get(marker));
                }
                else {
                    i = new Intent(getActivity(), ProfileActivity.class);
                    b.putString("user_json", _eu.get(marker));
                }
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    public static void makeRefresh() {
        _refreshed = false;
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
        else
            Toast.makeText(getActivity(), "Error trying to connect to Google", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle arg0) {
        updateCurLoc();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(getActivity(), "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        System.out.println("onConnectionSuspended");
    }

    private void updateCurLoc() {

        android.location.Location here = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        if (here != null) {
            _curLoc = new LatLng(here.getLatitude(), here.getLongitude());

            User curUser = DataManager.getCurUser();
            Location loc = curUser.getLocation();
            if (loc == null)
                loc = new Location();
            loc.setLatitude(_curLoc.latitude);
            loc.setLongitude(_curLoc.longitude);
            curUser.setLocation(loc);
            curUser.setLastLocationTime(System.currentTimeMillis());
            DataManager.updateCurUser(curUser);

//            displayElements();
        }
    }

//    public void displayElements() {
//        _map.clear();
//
//        if (_curLoc != null)
//            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_curLoc, _defaultZoom));
//        else
//            Toast.makeText(getActivity(), "Error finding current location", Toast.LENGTH_SHORT).show();
//
//        _eu.clear();
//
//        ObjectMapper om = new ObjectMapper();
//        long now = System.currentTimeMillis();
//
//        List<User> relevantUsers = new ArrayList<>();
//        relevantUsers.addAll(DataManager.getNearbyUsers());
//        relevantUsers.removeAll(DataManager.getCurUser().getFriends());
//        relevantUsers.addAll(DataManager.getCurUser().getFriends());
//        for (User u: relevantUsers) {
//
//            // To enforce synchronization with the current user
//            if (u.equals(DataManager.getCurUser())) {
//                Location loc = u.getLocation();
//                if (loc == null)
//                    loc = new Location();
//                loc.setLatitude(_curLoc.latitude);
//                loc.setLongitude(_curLoc.longitude);
//                u.setLocation(loc);
//            }
//
//            int elapsed = (int) ((now - u.getLastLocationTime()) / 60000);
//            String updated = "Updated ";
//            if (elapsed < 60)
//                updated += elapsed + " minutes ago";
//            else if (elapsed < 1440)
//                updated += (elapsed / 60) + " hours ago";
//            else
//                updated += (elapsed / 1440) + " days ago";
//
//            System.out.println(u.getUsername() == null);
//            System.out.println(updated == null);
//            System.out.println(u.getLocation() == null);
//            System.out.println(BitmapDescriptorFactory.fromResource(R.drawable.gc_controller) == null);
//            System.out.println("new");
//            Marker marker = _map.addMarker(new MarkerOptions()
//                    .title(u.getUsername())
//                    .snippet(updated)
//                    .position(new LatLng(u.getLocation().getLatitude(), u.getLocation().getLongitude()))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gc_controller)));
//
//            if (u.equals(DataManager.getCurUser()))
//                marker.showInfoWindow();
//
//            try {
//                _eu.put(marker, om.writeValueAsString(u));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        List<Event> relevantEvents = new ArrayList<>();
//        relevantEvents.addAll(DataManager.getNearbyEvents());
//        relevantEvents.removeAll(DataManager.getCurUser().getEvents());
//        relevantEvents.addAll(DataManager.getCurUser().getEvents());
//        relevantEvents.removeAll(DataManager.getHostingEvents());
//        relevantEvents.addAll(DataManager.getHostingEvents());
//        for (Event e: relevantEvents) {
//
//            Marker marker = _map.addMarker(new MarkerOptions()
//                            .title(e.getTitle())
//                            .snippet("Hosted by " + e.getHost().getUsername())
//                            .position(new LatLng(e.getLocation().getLatitude(), e.getLocation().getLongitude()))
//                            .alpha(0.99F)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.event)));
//
//            try {
//                _eu.put(marker, om.writeValueAsString(e));
//            } catch (Exception exc) {
//                exc.printStackTrace();
//            }
//        }
//
//    }

    public void centerMapOnSelf() {
        if (_curLoc == null || _map == null)
            Toast.makeText(getActivity(), getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
        else
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_curLoc, _defaultZoom));
    }

}