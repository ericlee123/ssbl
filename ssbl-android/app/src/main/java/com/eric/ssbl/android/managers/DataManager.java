package com.eric.ssbl.android.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.eric.ssbl.android.activities.ConversationActivity;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.EventListFragment;
import com.eric.ssbl.android.fragments.InboxFragment;
import com.eric.ssbl.android.fragments.ProfileFragment;
import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Location;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class DataManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/ssbl-server-2.0/smash";
//    private static String _90serverURL = "http://10.148.130.20:8080/ssbl-server-2.0/smash";
    //    private static String _serverURL = "http://192.168.1.9:8080/ssbl-server/smash";
    private static User _curUser;
    private static List<User> _nearbyUsers = new ArrayList<>();
    private static List<Event> _nearbyEvents = new ArrayList<>();
    private static List<Event> _hostingEvents = new ArrayList<>();
    private static HashMap<Integer, Event> _eventIdMap = new HashMap<>();

    ///////////////////////////////////////////////////
    // General stuff
    ///////////////////////////////////////////////////
    private static ChartFragment _chartFragment;
    private static ProfileFragment _profileFragment;
    private static InboxFragment _inboxFragment;
//    private static NotificationsFragment _notificationsFragment;
    private static EventListFragment _eventListFragment;

    public static void setChartFragment(ChartFragment cf) {
        _chartFragment = cf;
    }

    public static void setProfileFragment(ProfileFragment pf) {
        _profileFragment = pf;
    }

    public static void setInboxFragment(InboxFragment inboxFragment) {
        _inboxFragment = inboxFragment;
    }

//    public static void setNotificationsFragment(NotificationsFragment nf) {
//        _notificationsFragment = nf;
//    }

    public static void setEventListFragment(EventListFragment elf) {
        _eventListFragment = elf;
    }

    public static ChartFragment getChartFragment() {
        return _chartFragment;
    }

    public static ProfileFragment getProfileFragment() {
        return _profileFragment;
    }

    public static InboxFragment getInboxFragment() {
        return _inboxFragment;
    }

//    public static ChartFragment getNotificationsFragment() {
//        return _notificationsFragment;
//    }

    public static EventListFragment getEventListFragment() {
        return _eventListFragment;
    }

    public static void refreshFragments() {
        if (_chartFragment != null)
            _chartFragment.refresh();
        if (_profileFragment != null)
            _profileFragment.refresh();
        if (_inboxFragment != null)
            _inboxFragment.refresh();
//        if (_notificationsFragment != null)
//            _notificationsFragment.refresh();
        if (_eventListFragment != null)
            _eventListFragment.refresh();
    }

    public static void clearData() {
        _curUser = null;

        _chartFragment = null;
        _profileFragment = null;
//        _notificationsFragment = null;
        _inboxFragment = null;
        _eventListFragment = null;


        _nearbyUsers.clear();
        _eventIdMap.clear();
        _nearbyEvents.clear();
        _hostingEvents.clear();
    }

    public static String getServerUrl() {
        return _serverURL;
    }

    /////////////////////////////////////////////////
    // curUser section
    /////////////////////////////////////////////////
    public static User getCurUser() {
        return _curUser;
    }

    public static void setCurUser(User curUser) {
        _curUser = curUser;
    }

    /**
     * First updates the current user locally, and then sends an HTTP post
     * to the server. If the post is unsuccessful, the current user is reverted
     * back to normal. Returns the user object response from the server, otherwise
     * null if the request failed.
     *
     * @param updated the updated current user
     * @return the result
     */
    public static User httpUpdateCurUser(User updated) {
        if (updated == null)
            return null;

        User backup = _curUser;
        _curUser = updated;

        User result;
        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/edit/user/update");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");
            request.setHeader("Accept", "application/json");

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            System.out.println(om.writeValueAsString(updated));

            StringEntity body = new StringEntity(om.writeValueAsString(updated), "UTF-8");
            body.setContentType("application/json");
            request.setEntity(body);

            HttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());

//            System.out.println("update_cur_user");
//            System.out.println(url.toString());
//            System.out.println(response.getStatusLine().getStatusCode());
//            System.out.println(jsonString);

            if (jsonString.length() == 0)
                result = null;
            else
                result = om.readValue(jsonString, User.class);

            System.out.println("updated loc: " + (updated.getLocation() == null));
            System.out.println("result loc: " + (result.getLocation() == null));
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }

        _curUser = (result != null) ? result : backup;
        return result;
    }


    ////////////////////////////////////////////////////////
    // Relevant users and events
    ///////////////////////////////////////////////////////
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

    public static Event httpUpdateEvent(Event updated, boolean fresh) {

        if (updated == null)
            return null;

        Event result;
        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/edit/event");
        if (fresh)
            url.append("/create");
        else
            url.append("/update");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");
            request.setHeader("Accept", "application/json");

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            System.out.println("eventJson: " + om.writeValueAsString(updated));

            StringEntity body = new StringEntity(om.writeValueAsString(updated), "UTF-8");
            body.setContentType("application/json");
            request.setEntity(body);

            HttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());

//            System.out.println("update_event");
//            System.out.println(url.toString());
//            System.out.println(response.getStatusLine().getStatusCode());
//            System.out.println(jsonString);

            if (jsonString.length() == 0)
                result = null;
            else
                result = om.readValue(jsonString, Event.class);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }

        // update if successful
        if (result != null) {
            // Update locally
            Event backup = _eventIdMap.get(updated.getEventId());
            List<Event> oldList = _curUser.getEvents();

            if (backup != null) {
                _nearbyEvents.remove(backup);
                _hostingEvents.remove(backup);
                oldList.remove(backup);
            }

            _hostingEvents.add(result);
            oldList.add(result);
            _eventIdMap.put(result.getEventId(), result);
        }
        return result;
    }

    public static void httpDeleteEvent(Event deleted) {
        if (deleted == null)
            return;

        Event result;
        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/edit/event/delete");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");
            request.setHeader("Accept", "application/json");

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            StringEntity body = new StringEntity(om.writeValueAsString(deleted), "UTF-8");
            body.setContentType("application/json");
            request.setEntity(body);

            HttpResponse response = client.execute(request);

            System.out.println("delete_event");
            System.out.println(url.toString());
            System.out.println(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Update locally
        _eventIdMap.remove(deleted);
        List<Event> oldList = _curUser.getEvents();

        _nearbyEvents.remove(deleted);
        _hostingEvents.remove(deleted);
        oldList.remove(deleted);
    }


    ///////////////////////////////////////////
    // Conversations
    /////////////////////////////////////////////
    private static HashMap<Conversation, List<Message>> _conversationMap;
    private static ConversationActivity _openConversation;

    public static void refreshConversations() {
        List<Conversation> empties = _curUser.getConversations();
        _conversationMap = new HashMap<>();
        for (int i = 0; i < empties.size(); i++)
            _conversationMap.put(empties.get(i), new LinkedList<Message>());
//        fetchConversationPreviews();
    }

    public static void setOpenConversationActivity(ConversationActivity open) {
        _openConversation = open;
    }

    public static ConversationActivity getOpenConversationActivity() {
        return _openConversation;
    }

    public static void addNewMessages(List<Message> messageList) {
        for (Message m : messageList) {
            if (!_curUser.getConversations().contains(m.getConversation())) {
                m.getConversation().addRecipient(m.getSender());
                _curUser.addConversation(m.getConversation());
            }

            if (_conversationMap.get(m.getConversation()) == null) {
                List<Message> lm = new LinkedList<>();
                lm.add(m);
                _conversationMap.put(m.getConversation(), lm);
            } else
                _conversationMap.get(m.getConversation()).add(0, m);
        }

        new StuffDoer().execute();

        if (_inboxFragment != null)
            _inboxFragment.refresh();
        if (_openConversation != null) {
            _openConversation.showMessages();
        }
    }

    private static class StuffDoer extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            httpUpdateCurUser(_curUser);
            return null;
        }
    }

    public static HashMap<Conversation, List<Message>> getConversationMap() {
        return _conversationMap;
    }

    public static List<Message> httpFetchConversation(Conversation c) {

        // get the users
        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/messaging");
        url.append("/" + DataManager.getCurUser().getUsername());
        url.append("/" + DataManager.getCurUser().getUserId());
        url.append("/" + c.getConversationId());
        url.append("?size=" + ((_conversationMap.get(c) == null) ? 0 : _conversationMap.get(c).size()));
        url.append("&additional=" + 40);

        System.out.println("c size: " + _conversationMap.get(c).size());

        List<Message> lm;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");

            HttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());

            if (jsonString.length() == 0)
                return null;

            System.out.println("fetch_conversation");
            System.out.println(url.toString());
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(jsonString);

            ObjectMapper om = new ObjectMapper();
            lm = om.readValue(jsonString, new TypeReference<List<Message>>() {});

            for (int i = lm.size() - 1; i > 0; i -= 2)
                lm.remove(i);
        } catch (Exception e) {
            lm = null;
            e.printStackTrace();
        }

        if (lm != null)
            _conversationMap.get(c).addAll(lm);
        return lm;
    }

    private static void fetchConversationPreviews() {

        for (Conversation c : _conversationMap.keySet()) {

            // Skip getting a preview if the conversation already has messages
            if (_conversationMap.get(c) != null && _conversationMap.get(c).size() > 0)
                continue;

            // get the users
            StringBuilder url = new StringBuilder(DataManager.getServerUrl());
            url.append("/messaging");
            url.append("/" + getCurUser().getUsername());
            url.append("/" + getCurUser().getUserId());
            url.append("/" + c.getConversationId());
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
                _conversationMap.put(c, lm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    ////////////////////////////////////////////////
    // Settings
    //////////////////////////////////////////

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
            httpUpdateCurUser(u);
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


    ////////////////////////////////////////////////////////
    // Nearby users and events
    ////////////////////////////////////////////////////////
    private static Context _appContext;
    private static GoogleApiClient _googleApiClient;
    private static LatLng _curLoc;

    public void initLocationData(Context appContext) {
        _appContext = appContext;
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
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(_appContext, "Failed to connect to Google client", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        System.out.println("onConnectionSuspended");
    }

    /**
     * This should always be called from an aysnc task.
     */
    public void refreshCurLoc() {

        if (_googleApiClient == null)
            buildGoogleApiClient();

        android.location.Location here = LocationServices.FusedLocationApi.getLastLocation(_googleApiClient);
        if (here != null) {
            _curLoc = new LatLng(here.getLatitude(), here.getLongitude());

            if (_curUser != null) {
                _curUser.setLastLocationTime(System.currentTimeMillis());
                Location l = new Location();
                l.setLatitude(here.getLatitude());
                l.setLongitude(here.getLongitude());
                _curUser.setLocation(l);
            }

            new HttpNearbyHostingFetcher().execute(_curLoc);
        }
    }

    public static LatLng getCurLoc() {
        return _curLoc;
    }

    private class HttpNearbyHostingFetcher extends AsyncTask<LatLng, Void, Void> {
        @Override
        protected Void doInBackground(LatLng... params) {
            httpUpdateCurUser(_curUser);
            fetchNearbyUsers(params[0]);
            fetchNearbyEvents(params[0]);
            fetchHostingEvents();
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
            if (_chartFragment != null)
                _chartFragment.refresh();
        }
    }

    private void fetchNearbyUsers(LatLng loc) {
        List<User> nearbyUsers;

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

//            System.out.println("get_nearby_users");
//            System.out.println(url.toString());
//            System.out.println(response.getStatusLine().getStatusCode());
//            System.out.println(jsonString);
            if (jsonString.length() == 0)
                return;

            ObjectMapper om = new ObjectMapper();
            nearbyUsers = om.readValue(jsonString, new TypeReference<List<User>>() {});
        } catch (Exception e) {
            nearbyUsers = null;
            e.printStackTrace();
        }

        if (nearbyUsers != null)
            setNearbyUsers(nearbyUsers);
        else
            Toast.makeText(_appContext, "Error fetching nearby users", Toast.LENGTH_LONG).show();
    }

    private void fetchNearbyEvents(LatLng loc) {

        List<Event> nearbyEvents;

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

//            System.out.println("get_nearby_events");
//            System.out.println(url2.toString());
//            System.out.println(response.getStatusLine().getStatusCode());
//            System.out.println(jsonString);

            if (jsonString.length() == 0)
                return;

            ObjectMapper om = new ObjectMapper();
            nearbyEvents = om.readValue(jsonString, new TypeReference<List<Event>>() {});
        } catch (Exception e) {
            nearbyEvents = null;
            e.printStackTrace();
        }

        if (nearbyEvents != null)
            setNearbyEvents(nearbyEvents);
        else
            Toast.makeText(_appContext, "Error fetching nearby events", Toast.LENGTH_LONG).show();
    }

    private void fetchHostingEvents() {

        if (_curUser == null)
            return;

        Event e = new Event();
        e.setHost(_curUser);
        List<Event> hostingEvents;

        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/search/event");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");
            request.setHeader("Accept", "application/json");

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            StringEntity body = new StringEntity(om.writeValueAsString(e));
            body.setContentType("application/json");
            request.setEntity(body);

            HttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());

//            System.out.println("get_hosting_events");
//            System.out.println(url.toString());
//            System.out.println(response.getStatusLine().getStatusCode());
//            System.out.println(jsonString);

            if (jsonString.length() == 0)
                return;

            hostingEvents = om.readValue(jsonString, new TypeReference<List<Event>>() {});
        } catch (Exception exc) {
            hostingEvents = null;
            exc.printStackTrace();
        }

        if (hostingEvents != null)
            setHostingEvents(hostingEvents);
        else
            Toast.makeText(_appContext, "Error fetching hosting events", Toast.LENGTH_LONG).show();
    }
}
