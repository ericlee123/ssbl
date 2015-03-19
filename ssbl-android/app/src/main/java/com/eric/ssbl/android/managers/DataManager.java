package com.eric.ssbl.android.managers;

import android.os.AsyncTask;
import android.os.Bundle;

import com.eric.ssbl.android.activities.LoginActivity;
import com.eric.ssbl.android.activities.SettingsActivity;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

//    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/SSBLServer";
    private static String _serverURL = "http://192.168.1.9:8080/ssbl-server/smash";
    private static User _curUser;
    private static List<User> _nearbyUsers = new ArrayList<>();
    private static HashMap<Integer, User> _userIdMap = new HashMap<>();
    private static List<Event> _nearbyEvents = new ArrayList<>();
    private static HashMap<Integer, Event> _eventIdMap = new HashMap<>();

    private static GoogleApiClient _googleApiClient;

    public static User getCurUser() {
        return _curUser;
    }

    public static void setCurUser(User curUser) {
        _curUser = curUser;
    }

    public static void setNearbyUsers(List<User> nu) {
        _nearbyUsers = nu;
        for (User u: nu)
            _userIdMap.put(u.getUserId(), u);
    }

    public static List<User> getNearbyUsers() {
        return _nearbyUsers;
    }

    public static User getUserById(int id) {
        return _userIdMap.get(id);
    }

    public static void setNearbyEvents(List<Event> ne) {
        _nearbyEvents = ne;
        for (Event e: ne)
            _eventIdMap.put(e.getEventId(), e);
    }

    public static List<Event> getHostingEvents() {
        return new ArrayList<Event>();
    }

    public static List<Event> getNearbyEvents() {
        return _nearbyEvents;
    }

    public static Event getEventById(int id) {
        return _eventIdMap.get(id);
    }

    public static void clearData() {
        _nearbyUsers.clear();
        _nearbyEvents.clear();

        _userIdMap.clear();
        _eventIdMap.clear();
    }

    public static String getServerUrl() {
        return _serverURL;
    }



    // Stupid stuff

    private LoginActivity _la;

    public DataManager() {}

    public void initializeData(LoginActivity la) {

        _la = la;
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        _googleApiClient = new GoogleApiClient.Builder(_la)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (_googleApiClient != null)
            _googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        getCurLoc();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) { }

    @Override
    public void onConnectionSuspended(int arg0) {
        System.out.println("onConnectionSuspended");
    }

    private void getCurLoc() {

        android.location.Location here = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        if (here != null) {
            LatLng loc = new LatLng(here.getLatitude(), here.getLongitude());
            new HttpEUGetter().execute(loc);
        }
    }

    private void done() {
        _la.goToMain();
    }

    private class HttpEUGetter extends AsyncTask<LatLng, Void, Void> {

        private List<User> uList;
        private List<Event> eList;

        private void search(LatLng loc) {

            // get the users
            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/search/user");
            url.append("?lat=" + loc.latitude + "&lon=" + loc.longitude + "&radius=" + SettingsActivity.getRadius());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                System.out.println("jsonString: " + jsonString);
                if (jsonString.length() == 0)
                    return;

                ObjectMapper om = new ObjectMapper();
                uList = om.readValue(jsonString, new TypeReference<List<User>>(){});
            } catch (Exception e) {
                uList = null;
                e.printStackTrace();
            }

            // get the events
            StringBuilder url2 = new StringBuilder(DataManager.getServerUrl());
            url2.append("/search/event");
            url2.append("?lat=" + loc.latitude + "&lon=" + loc.longitude + "&radius=" + SettingsActivity.getRadius());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url2.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                System.out.println("jsonString: " + jsonString);
                if (jsonString.length() == 0)
                    return;

                ObjectMapper om = new ObjectMapper();
                eList = om.readValue(jsonString, new TypeReference<List<Event>>(){});
            } catch (Exception e) {
                eList = null;
                e.printStackTrace();
            }
        }


        @Override
        protected Void doInBackground(LatLng... params) {
            search(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            setNearbyUsers(uList);
            setNearbyEvents(eList);
            done();
        }
    }
}
