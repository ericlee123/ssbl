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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

//    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/ssbl-server/smash";
    private static String _serverURL = "http://192.168.1.9:8080/ssbl-server/smash";
    private static User _curUser;
    private static List<User> _nearbyUsers = new ArrayList<>();
    private static List<Event> _nearbyEvents = new ArrayList<>();
    private static List<Event> _hostingEvents = new ArrayList<>();
    private static HashMap<Integer, User> _userIdMap = new HashMap<>();
    private static HashMap<Integer, Event> _eventIdMap = new HashMap<>();

    public static User getCurUser() {
        return _curUser;
    }

    public static void setCurUser(User curUser) {
        if (curUser == null)
            return;

        _curUser = curUser;
    }

    public static void setNearbyUsers(List<User> nearbyUsers) {
        if (nearbyUsers == null)
            return;

        _nearbyUsers = nearbyUsers;
        for (User u: nearbyUsers)
            _userIdMap.put(u.getUserId(), u);
    }

    public static List<User> getNearbyUsers() {
        return _nearbyUsers;
    }

    public static User getUserById(int id) {
        return _userIdMap.get(id);
    }

    public static void setNearbyEvents(List<Event> nearbyEvents) {
        if (nearbyEvents == null)
            return;

        _nearbyEvents = nearbyEvents;
        for (Event e: nearbyEvents)
            _eventIdMap.put(e.getEventId(), e);
    }

    public static void setHostingEvents(List<Event> hostingEvents) {
        if (hostingEvents == null)
            return;

        _hostingEvents = hostingEvents;
        for (Event e: hostingEvents)
            _eventIdMap.put(e.getEventId(), e);
    }

    public static List<Event> getHostingEvents() {
        return _hostingEvents;
    }

    public static List<Event> getNearbyEvents() {
        return _nearbyEvents;
    }

    public static void updateEvent(Event e) {
        _eventIdMap.put(e.getEventId(), e);
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
    private static GoogleApiClient _googleApiClient;

    public DataManager() {}

    public void init(LoginActivity la) {
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
            new HttpSynchronizer().execute(loc);
        }
    }

    private void done() {
        _la.goToMain();
    }

    private class HttpSynchronizer extends AsyncTask<LatLng, Void, Void> {

        private List<User> nearbyUsers;
        private List<Event> nearbyEvents;

        private List<Event> hostingEvents;

        private void getNearbyEU(LatLng loc) {

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
                nearbyUsers = om.readValue(jsonString, new TypeReference<List<User>>(){});
            } catch (Exception e) {
                nearbyUsers = null;
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
                nearbyEvents = om.readValue(jsonString, new TypeReference<List<Event>>(){});
            } catch (Exception e) {
                nearbyEvents = null;
                e.printStackTrace();
            }
        }

        private void getHostingEvents() {

            Event e = new Event();
            e.setHost(getCurUser());

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/search/event");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                // encode the user template into the request
                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(e));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                hostingEvents = om.readValue(jsonString, new TypeReference<List<Event>>() {
                });
            } catch (Exception exc) {
                hostingEvents = null;
                exc.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(LatLng... params) {
            getNearbyEU(params[0]);
            getHostingEvents();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            setNearbyUsers(nearbyUsers);
            setNearbyEvents(nearbyEvents);
            setHostingEvents(hostingEvents);
            done();
        }
    }

}
