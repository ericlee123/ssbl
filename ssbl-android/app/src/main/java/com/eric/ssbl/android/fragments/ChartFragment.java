package com.eric.ssbl.android.fragments;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
    private static HashMap<Marker, Integer> _id = new HashMap<>();
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

        _id.clear();
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
                    b.putInt("event_id", _id.get(marker));
                }
                else {
                    i = new Intent(getActivity(), ProfileActivity.class);
                    b.putInt("user_id", _id.get(marker));
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
            new HttpLocationUpdater().execute(_curLoc);
            displayElements();
        }
    }

    public void displayElements() {
        _map.clear();

        if (_curLoc != null)
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_curLoc, _defaultZoom));
        else
            Toast.makeText(getActivity(), "Error finding current location", Toast.LENGTH_SHORT).show();

        _id.clear();

        long now = System.currentTimeMillis();

        for (User u: DataManager.getNearbyUsers()) {

            int elapsed = (int) ((now - u.getLastLocationTime()) / 60000);
            String updated = "Updated ";
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

            _id.put(marker, u.getUserId());
        }

        for (Event e: DataManager.getNearbyEvents()) {

            Marker marker = _map.addMarker(new MarkerOptions()
                            .title(e.getTitle())
                            .snippet("Hosted by " + e.getHost().getUsername())
                            .position(new LatLng(e.getLocation().getLatitude(), e.getLocation().getLongitude()))
                            .alpha(0.99F)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.event)));

            _id.put(marker, e.getEventId());
        }

    }

    public void centerMapOnSelf() {
        if (_curLoc == null || _map == null)
            Toast.makeText(getActivity(), getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
        else
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_curLoc, _defaultZoom));
    }

    public static List<User> getNearbyUsers() {
        return _nearbyUsers;
    }

    public static List<Event> getNearbyEvents() {
        return _nearbyEvents;
    }

    private class HttpLocationUpdater extends AsyncTask<LatLng, Void, Void> {

        private User u;

        private void updateLocation(LatLng loc) {

            u = DataManager.getCurUser();
            Location newLoc;
            if (u.getLocation() == null)
                newLoc = new Location();
            else
                newLoc = u.getLocation();
            newLoc.setLatitude(loc.latitude);
            newLoc.setLongitude(loc.longitude);
            u.setLocation(newLoc);

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/edit/user");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(u));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                u = om.readValue(jsonString, User.class);
            } catch (Exception e) {
                u = null;
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(LatLng... params) {
            updateLocation(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (u != null) {
                DataManager.setCurUser(u);
                centerMapOnSelf();
            }
            else
                Toast.makeText(getActivity(), "Error updating current location", Toast.LENGTH_SHORT).show();
        }

    }

//    private class HttpEUGetter extends AsyncTask<LatLng, Void, Void> {
//
//        private List<User> uList;
//        private List<Event> eList;
//
//        private void search(LatLng loc) {
//
//            // get the users
//            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
//            url.append("/search/user");
//            url.append("?lat=" + loc.latitude + "&lon=" + loc.longitude + "&radius=" + SettingsActivity.getRadius());
//
//            try {
//                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet(url.toString());
//
//                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
//
//                HttpResponse response = client.execute(request);
//                String jsonString = EntityUtils.toString(response.getEntity());
//
//                System.out.println("jsonString: " + jsonString);
//                if (jsonString.length() == 0)
//                    return;
//
//                ObjectMapper om = new ObjectMapper();
//                uList = om.readValue(jsonString, new TypeReference<List<User>>(){});
//            } catch (Exception e) {
//                uList = null;
//                e.printStackTrace();
//            }
//
//            // get the events
//            StringBuilder url2 = new StringBuilder(DataManager.getServerUrl());
//            url2.append("/search/event");
//            url2.append("?lat=" + loc.latitude + "&lon=" + loc.longitude + "&radius=" + SettingsActivity.getRadius());
//
//            try {
//                HttpClient client = new DefaultHttpClient();
//                HttpGet request = new HttpGet(url2.toString());
//
//                request.setHeader(HTTP.CONTENT_TYPE, "application/json");
//
//                HttpResponse response = client.execute(request);
//                String jsonString = EntityUtils.toString(response.getEntity());
//
//                System.out.println("jsonString: " + jsonString);
//                if (jsonString.length() == 0)
//                    return;
//
//                ObjectMapper om = new ObjectMapper();
//                eList = om.readValue(jsonString, new TypeReference<List<Event>>(){});
//            } catch (Exception e) {
//                eList = null;
//                e.printStackTrace();
//            }
//        }
//
//
//        @Override
//        protected Void doInBackground(LatLng... params) {
//            search(params[0]);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void what) {
//            _nearbyUsers = uList;
//            _nearbyEvents = eList;
//            displayElements();
//        }
//    }
}