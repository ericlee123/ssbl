package com.eric.ssbl.android.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.eric.ssbl.android.activities.ConversationActivity;
import com.eric.ssbl.android.fragments.ChartFragment;
import com.eric.ssbl.android.fragments.EventListFragment;
import com.eric.ssbl.android.fragments.InboxFragment;
import com.eric.ssbl.android.fragments.UserFragment;
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

    public static String getServerUrl() {
        return _serverURL;
    }

    /**
     * To be called on log out.
     */
    public static void clearData() {
        _currentUser = null;

        _conversations = null;
        _conversationMap = null;
        _openConversation = null;

        _chartFragment = null;
        _userFragment = null;
//        _notificationsFragment = null;
        _inboxFragment = null;
        _eventListFragment = null;

        _nearbyUsers = null;
        _nearbyEvents = null;
        _hostingEvents = null;
    }

    /////////////////////////////////////////////////
    // curUser section
    /////////////////////////////////////////////////
    private static User _currentUser;
    private static User _backupUser;

    public static User getCurrentUser() {
        return _currentUser;
    }

    /**
     * Used for only updating the current user locally.
     * @param currentUser the current user
     */
    public static void setCurrentUser(User currentUser) {
        _currentUser = currentUser;
    }

    public static void setBackupUser(User backupUser) {
        _backupUser = backupUser;
    }

    /**
     * Code that calls this method should first update the current user's attributes.
     * This method will automatically revert to backup User object in the case of
     * failure.
     *
     * @return success
     */
    public static boolean httpUpdateCurrentUser() {

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

            StringEntity body = new StringEntity(om.writeValueAsString(_currentUser), "UTF-8");
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
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }

        if (result != null) {
            _currentUser = result;
            _backupUser = result; // If the update was successful, update the backup
            return true;
        }
        else {
            _currentUser = _backupUser;
            return false;
        }
    }





    ///////////////////////////////////////////////////
    // Conversations
    ///////////////////////////////////////////////////
    private static List<Conversation> _conversations;
    private static HashMap<Conversation, List<Message>> _conversationMap;
    private static ConversationActivity _openConversation;

    public static List<Conversation> getConversations() {
        return _conversations;
    }

    /**
     * Due to the database relationship between users and conversations, the
     * current user's conversations need to be initialized at login. After that,
     * conversations are managed seperately from the user.
     * @param conversations list of conversations
     */
    public static void setConversations(List<Conversation> conversations) {
        _conversations = conversations;
    }

    public static void reloadConversations() {
        _conversationMap = new HashMap<>();
        for (int i = 0; i < _conversations.size(); i++)
            _conversationMap.put(_conversations.get(i), new LinkedList<Message>());
//        fetchConversationPreviews();
    }

    public static HashMap<Conversation, List<Message>> getConversationMap() {
        return _conversationMap;
    }


    /**
     * This method helps other users figure out whether or not a ConversationActivity
     * is active.
     * @return active ConversationActivity or null
     */
    public static ConversationActivity getOpenConversationActivity() {
        return _openConversation;
    }

    public static void setOpenConversationActivity(ConversationActivity open) {
        _openConversation = open;
    }

    private static final int ADDITONAL_MESSAGES = 400;

    public static List<Message> httpFetchConversation(Conversation c) {

        // get the users
        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/messaging");
        url.append("/" + DataManager.getCurrentUser().getUsername());
        url.append("/" + DataManager.getCurrentUser().getUserId());
        url.append("/" + c.getConversationId());
//        url.append("?size=" + ((_conversationMap.get(c) == null) ? 0 : _conversationMap.get(c).size()));
        url.append("?size=0");
        url.append("&additional=" + ADDITONAL_MESSAGES);

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

        if (lm != null) {
            _conversationMap.put(c, lm);
        }
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
            url.append("/" + getCurrentUser().getUsername());
            url.append("/" + getCurrentUser().getUserId());
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

    /**
     * Called from async task in MessagingService
     * @param messageList new messages
     */
    public static void addNewMessages(List<Message> messageList) {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            Message m = messageList.get(i);
            if (!_conversations.contains(m.getConversation()))
                _conversations.add(m.getConversation());

            if (_conversationMap.get(m.getConversation()) == null) { // this shouldnt be null ever ??
                List<Message> lm = new ArrayList<>();
                lm.add(m);
                _conversationMap.put(m.getConversation(), lm);
            } else
                _conversationMap.get(m.getConversation()).add(0, m);
        }
    }






    ////////////////////////////////////////////////////////
    // Location and fetching nearby users and events
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

            if (_currentUser != null) {
                Location l = new Location();
                l.setLatitude(here.getLatitude());
                l.setLongitude(here.getLongitude());
                _currentUser.setLocation(l);
                _currentUser.setLastLocationTime(System.currentTimeMillis());
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
            httpUpdateCurrentUser();
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

        Event e = new Event();
        e.setHost(_currentUser);
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

            System.out.println("get_hosting_events");
            System.out.println(url.toString());
            System.out.println(response.getStatusLine().getStatusCode());
            System.out.println(jsonString);

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





    ////////////////////////////////////////////////////////
    // Relevant users and events
    ///////////////////////////////////////////////////////
    private static List<User> _nearbyUsers = new ArrayList<>();
    private static List<Event> _nearbyEvents = new ArrayList<>();
    private static List<Event> _hostingEvents = new ArrayList<>();
    private static HashMap<Integer, Event> _eventIdMap = new HashMap<>();

    public static void setNearbyUsers(List<User> nearbyUsers) {
        _nearbyUsers = nearbyUsers;
    }

    public static List<User> getNearbyUsers() {
        return _nearbyUsers;
    }

    public static void setNearbyEvents(List<Event> nearbyEvents) {
        _nearbyEvents = nearbyEvents;
    }

    public static List<Event> getNearbyEvents() {
        return _nearbyEvents;
    }

    public static void setHostingEvents(List<Event> hostingEvents) {
        _hostingEvents = hostingEvents;
    }

    public static List<Event> getHostingEvents() {
        return _hostingEvents;
    }





    //////////////////////////////////////////////////////
    // Updating/Fetching users and events
    //////////////////////////////////////////////////////
    public static List<User> httpFetchUsers(User example) {

        List<User> results;

        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/search/user");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");
            request.setHeader("Accept", "application/json");

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            StringEntity body = new StringEntity(om.writeValueAsString(example));
            body.setContentType("application/json");
            request.setEntity(body);

            HttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());

            if (jsonString.length() == 0)
                return null;

            results = om.readValue(jsonString, new TypeReference<List<User>>(){});
        } catch (Exception e) {
            results = null;
            e.printStackTrace();
        }

        return results;
    }

    public static List<Event> httpFetchEvents(Event example) {

        List<Event> results;

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

            StringEntity body = new StringEntity(om.writeValueAsString(example));
            body.setContentType("application/json");
            request.setEntity(body);

            HttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());

            if (jsonString.length() == 0)
                return null;

            results = om.readValue(jsonString, new TypeReference<List<Event>>(){});
        } catch (Exception e) {
            results = null;
            e.printStackTrace();
        }

        return results;
    }

    public static boolean httpUpdateEvent(Event updated) {

        Event result;
        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/edit/event/update");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");
            request.setHeader("Accept", "application/json");

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
            Event backup = _eventIdMap.get(updated.getEventId());
            List<Event> oldList = _currentUser.getEvents();

            if (backup != null) {
                _nearbyEvents.remove(backup);
                _hostingEvents.remove(backup);
                oldList.remove(backup);
            }
            _hostingEvents.add(result);
            oldList.add(result);
            _eventIdMap.put(result.getEventId(), result);

            return true;
        }
        return false;
    }

    public static boolean httpCreateEvent(Event newEvent) {
        Event result;
        StringBuilder url = new StringBuilder(DataManager.getServerUrl());
        url.append("/edit/event/create");

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url.toString());

            request.setHeader(HTTP.CONTENT_TYPE, "application/json");
            request.setHeader("Accept", "application/json");

            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            StringEntity body = new StringEntity(om.writeValueAsString(newEvent), "UTF-8");
            body.setContentType("application/json");
            request.setEntity(body);

            HttpResponse response = client.execute(request);
            String jsonString = EntityUtils.toString(response.getEntity());

//            System.out.println("create_event");
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
            _eventIdMap.put(result.getEventId(), result);
            _hostingEvents.add(result);
            _nearbyEvents.add(result);
            _currentUser.getEvents().add(result);

            return true;
        }
        return false;
    }

    public static void httpDeleteEvent(Event deleted) {
        if (deleted == null)
            return;

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

            client.execute(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update locally
        _eventIdMap.remove(deleted);
        List<Event> oldList = _currentUser.getEvents();

        _nearbyEvents.remove(deleted);
        _hostingEvents.remove(deleted);
        oldList.remove(deleted);
    }





    ///////////////////////////////////////////////////
    // Fragment management
    ///////////////////////////////////////////////////
    private static ChartFragment _chartFragment;
    private static UserFragment _userFragment;
    private static InboxFragment _inboxFragment;
    //    private static NotificationsFragment _notificationsFragment;
    private static EventListFragment _eventListFragment;

    public static void setChartFragment(ChartFragment chartFragment) {
        _chartFragment = chartFragment;
    }

    public static void setProfileFragment(UserFragment userFragment) {
        _userFragment = userFragment;
    }

    public static void setInboxFragment(InboxFragment inboxFragment) {
        _inboxFragment = inboxFragment;
    }

//    public static void setNotificationsFragment(NotificationsFragment notificationsFragment) {
//        _notificationsFragment = notificationsFragment;
//    }

    public static void setEventListFragment(EventListFragment eventListFragment) {
        _eventListFragment = eventListFragment;
    }

    public static ChartFragment getChartFragment() {
        return _chartFragment;
    }

    public static UserFragment getProfileFragment() {
        return _userFragment;
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

    /**
     * This method should only be called from onPostExecute in an AsyncTask.
     */
    public static void refreshAllFragments() {
        if (_chartFragment != null)
            _chartFragment.refresh();
        if (_userFragment != null)
            _userFragment.refresh();
        if (_inboxFragment != null)
            _inboxFragment.refresh();
//        if (_notificationsFragment != null)
//            _notificationsFragment.refresh();
        if (_eventListFragment != null)
            _eventListFragment.refresh();
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
                _settings.put("map_radius_index", 4);

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
            _currentUser.setPrivate(_settings.getBoolean("location_private"));
            httpUpdateCurrentUser();
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
}
