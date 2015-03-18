package com.eric.ssbl.android.managers;

import android.os.AsyncTask;

import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager {

//    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/SSBLServer";
    private static String _serverURL = "http://192.168.1.9:8080/ssbl-server/smash";
    private static User _curUser;
    private static List<User> _nearbyUsers;
    private static HashMap<Integer, User> _userIdMap = new HashMap<>();
    private static List<Event> _hostingEvents;
    private static List<Event> _nearbyEvents;
    private static HashMap<Integer, Event> _eventIdMap = new HashMap<>();

    private DataManager() {}

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
        _nearbyUsers = new ArrayList<>();
        _nearbyEvents = new ArrayList<>();
    }

    public static String getServerUrl() {
        return _serverURL;
    }

    public static void fetchAllData() {
        new HttpSynchronizer().execute();
    }

    private static class HttpSynchronizer extends AsyncTask<Void, Void, Void> {

        private void synchronize() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void what) {
        }
    }
}
