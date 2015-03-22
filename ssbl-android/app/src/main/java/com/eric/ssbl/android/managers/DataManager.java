package com.eric.ssbl.android.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.eric.ssbl.android.activities.LoginActivity;
import com.eric.ssbl.android.activities.MainActivity;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Message;
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
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class DataManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    //    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/ssbl-server/smash";
    private static String _serverURL = "http://192.168.1.9:8080/ssbl-server/smash";
    private static User _curUser;
    private static List<User> _nearbyUsers = new ArrayList<>();
    private static List<Event> _nearbyEvents = new ArrayList<>();
    private static List<Event> _hostingEvents = new ArrayList<>();
    private static HashMap<Integer, Event> _eventIdMap = new HashMap<>();

    public static User getCurUser() {
        return _curUser;
    }

    public static void updateCurUser(User updated) {
        if (updated == null)
            return;
        _curUser = updated;
        new HttpUserUpdater().execute(updated);
    }

    public static void setCurUser(User u) {
        _curUser = u;
    }

    public static void setNearbyUsers(List<User> nearbyUsers) {
        if (nearbyUsers == null)
            return;
        _nearbyUsers = nearbyUsers;
    }

    public static List<User> getNearbyUsers() {
        return _nearbyUsers;
    }

    public static void setNearbyEvents(List<Event> nearbyEvents) {
        if (nearbyEvents == null)
            return;
        _nearbyEvents = nearbyEvents;
        for (Event e : nearbyEvents)
            _eventIdMap.put(e.getEventId(), e);
    }

    public static List<Event> getNearbyEvents() {
        return _nearbyEvents;
    }

    public static void setHostingEvents(List<Event> hostingEvents) {
        if (hostingEvents == null)
            return;
        _hostingEvents = hostingEvents;
        for (Event e : hostingEvents)
            _eventIdMap.put(e.getEventId(), e);
    }

    public static List<Event> getHostingEvents() {
        return _hostingEvents;
    }

    public static void updateEvent(Event updated) {

        Event old = _eventIdMap.get(updated.getEventId());
        List<Event> oldList = _curUser.getEvents();

        if (old != null) {
            _nearbyEvents.remove(old);
            _hostingEvents.remove(old);
            oldList.remove(old);
        }

        _hostingEvents.add(updated);
        oldList.add(updated);
        _eventIdMap.put(updated.getEventId(), updated);

        new HttpEventUpdater().execute(updated);
    }

    private static boolean _refreshing = false;
    public static void refreshEverything() {
        _refreshing = true;
        new DataManager().refreshCurLoc();
    }

    public static void clearData() {
        _curUser = null;

        _nearbyUsers.clear();

        _eventIdMap.clear();
        _nearbyEvents.clear();
        _hostingEvents.clear();
    }

    public static String getServerUrl() {
        return _serverURL;
    }

    private static class HttpUserUpdater extends AsyncTask<User, Void, Void> {

        private User updated;

        private void updateUser(User u) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/edit/user/update");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(u));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                System.out.println("updateUser url: " + url.toString());
                System.out.println(jsonString);
                if (jsonString.length() == 0)
                    return;

                updated = om.readValue(jsonString, User.class);
            } catch (Exception e) {
                updated = null;
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(User... params) {
            updateUser(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (updated == null)
                Toast.makeText(_appContext, "Error updating current user", Toast.LENGTH_LONG).show();
        }
    }

    private static class HttpEventUpdater extends AsyncTask<Event, Void, Void> {

        private Event updated;

        private void updateEvent(Event e) {

            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/edit/event/update");

            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                ObjectMapper om = new ObjectMapper();
                StringEntity body = new StringEntity(om.writeValueAsString(e));
                request.setEntity(body);

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

                if (jsonString.length() == 0)
                    return;

                updated = om.readValue(jsonString, Event.class);
            } catch (Exception exc) {
                updated = null;
                exc.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Event... params) {
            updateEvent(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (updated == null)
                Toast.makeText(_appContext, "Error updating event", Toast.LENGTH_LONG).show();
        }
    }

    // Managing conversations
    private static List<String> _conversationPreviews;

    public static void initConversationPreviews() {
        _conversationPreviews = new ArrayList<>();
        new HttpConversationPreviewer().execute();
    }

    public static List<String> getConversationPreviews() {
        return _conversationPreviews;
    }

    private static class HttpConversationPreviewer extends AsyncTask<Void, Void, Void> {

        private List<String> cp = new ArrayList<>();

        private void fetchPreviews() {

            for (int i = 0; i < getCurUser().getConversations().size(); i++) {
                // get the users
                StringBuilder url = new StringBuilder(DataManager.getServerUrl());
                url.append("/messaging");
                url.append("/" + getCurUser().getUsername());
                url.append("/" + getCurUser().getUserId());
                url.append("/" + i);
                url.append("?size=0");
                url.append("&additional=1");

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(url.toString());

                    request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                    HttpResponse response = client.execute(request);
                    String jsonString = EntityUtils.toString(response.getEntity());

                    if (jsonString.length() == 0)
                        return;

                    ObjectMapper om = new ObjectMapper();
                    List<Message> lm = om.readValue(jsonString, new TypeReference<List<Message>>() {});
                    cp.add(i, lm.get(0).getBody());
                } catch (Exception e) {
                    cp = null;
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            fetchPreviews();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (cp != null)
                _conversationPreviews = cp;
        }
    }

    /**
     * This section manages storage and access of settings.
     */
    private static File _settingsFile;
    private static JSONObject _settings;

    public static void initSettings(File fileDir) {
        _settingsFile = new File(fileDir, "settings");

        try {
            if (!_settingsFile.exists()) {
                _settingsFile.createNewFile();
                _settings = new JSONObject();

                _settings.put("location_private", false);
                _settings.put("map_radius_index", 2);

                PrintWriter pw = new PrintWriter(_settingsFile);
                pw.print(_settings.toString());
                pw.close();
            } else {
                Scanner scan = new Scanner(_settingsFile);
                _settings = new JSONObject(scan.nextLine());
            }
        } catch (Exception e) {
            _settingsFile.delete();
            e.printStackTrace();
        }
    }

    public static JSONObject getSettings() {
        return _settings;
    }

    public static void saveSettings(JSONObject settings) {

        _settings = settings;

        try {
            PrintWriter pw = new PrintWriter(_settingsFile);
            pw.print(_settings.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            User u = getCurUser();
            u.setPrivate(_settings.getBoolean("location_private"));
            updateCurUser(u);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is so poorly written i am sorry
    public static double getRadius() {

        if (_settingsFile == null || _settings == null)
            return 10.0;

        int index = 0;
        try {
            index = _settings.getInt("map_radius_index");
        } catch (Exception e) {
            _settingsFile.delete();
        }
        if (index == 0)
            return 1.0;
        if (index == 1)
            return 5.0;
        if (index == 2)
            return 10.0;
        if (index == 3)
            return 20.0;
        if (index == 4)
            return 50.0;
        if (index == 5)
            return 100.0;
        if (index == 6)
            return 500.0;
        if (index == 7)
            return 50000.0;

        return 10.0;
    }



    /**
     * This section initializes data of nearby users and events for the ChartFragment.
     */
    private static Context _appContext;
    private LoginActivity _la;
    private GoogleApiClient _googleApiClient;

    public void initNearby(LoginActivity la) {
        _la = la;
        _appContext = _la.getApplicationContext();
        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        _googleApiClient = new GoogleApiClient.Builder(_appContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (_googleApiClient != null)
            _googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        refreshCurLoc();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) { }

    @Override
    public void onConnectionSuspended(int arg0) {
        System.out.println("onConnectionSuspended");
    }

    private void refreshCurLoc() {

        if (_googleApiClient == null)
            buildGoogleApiClient();

        android.location.Location here = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        if (here != null) {
            System.out.println(here.getLatitude());
            LatLng loc = new LatLng(here.getLatitude(), here.getLongitude());
            new HttpEUGetter().execute(loc);
        }
    }

    private void initRefreshCallback() {
        if (_refreshing) {
            _refreshing = false;
            MainActivity.refreshFragments();
        }
        else
            _la.goToMain();
    }

    private class HttpEUGetter extends AsyncTask<LatLng, Void, Void> {

        private List<User> nearbyUsers;
        private List<Event> nearbyEvents;

        private List<Event> hostingEvents;

        private void getNearbyEU(LatLng loc) {

            // get the users
            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/search/user");
            url.append("?lat=" + loc.latitude + "&lon=" + loc.longitude + "&radius=" + DataManager.getRadius());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

//                System.out.println("getNearbyUsers url: " + url.toString());
//                System.out.println(jsonString);
                if (jsonString.length() == 0)
                    return;

                ObjectMapper om = new ObjectMapper();
                nearbyUsers = om.readValue(jsonString, new TypeReference<List<User>>() {});

            } catch (Exception e) {
                nearbyUsers = null;
                e.printStackTrace();
            }

            // get the events
            StringBuilder url2 = new StringBuilder(DataManager.getServerUrl());
            url2.append("/search/event");
            url2.append("?lat=" + loc.latitude + "&lon=" + loc.longitude + "&radius=" + DataManager.getRadius());

            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url2.toString());

                request.setHeader(HTTP.CONTENT_TYPE, "application/json");

                HttpResponse response = client.execute(request);
                String jsonString = EntityUtils.toString(response.getEntity());

//                System.out.println("getNearbyEvents url: " + url.toString());
//                System.out.println(jsonString);
                if (jsonString.length() == 0)
                    return;

                ObjectMapper om = new ObjectMapper();
                nearbyEvents = om.readValue(jsonString, new TypeReference<List<Event>>() {});
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

//                System.out.println("getHostingEvents url: " + url.toString());
//                System.out.println(jsonString);

                if (jsonString.length() == 0)
                    return;

                hostingEvents = om.readValue(jsonString, new TypeReference<List<Event>>() {});
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
            initRefreshCallback();
        }
    }


    /**
     * Methods for MessagingService
     */
    private static boolean _accountActive = false;
    private static boolean _appActive = false;

    public static void setAccountActive(boolean active) {
        _accountActive = active;
    }

    public static boolean isAccountActive() {
        return _accountActive;
    }

    public static void setAppActive(boolean active) {
        _appActive = active;
    }

    public static boolean isAppActive() {
        return _appActive;
    }
}
