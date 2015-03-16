package com.eric.ssbl.android.managers;

import android.os.AsyncTask;

import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.Notification;
import com.eric.ssbl.android.pojos.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DataManager {

//    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/SSBLServer";
    private static String _serverURL = "http://192.168.1.9:8080/ssbl-server/ssbl-server";
    private static User _curUser;
    private static HashMap<Integer, User> _users;
    private static HashMap<Integer, Event> _events;
    private static HashMap<Integer, Conversation> _conversations;
    private static HashMap<Integer, Notification> _notifications;

    private DataManager() {}

    public static User getCurUser() {
        return _curUser;
    }

    public static void setCurUser(User curUser) {
        _curUser = curUser;
    }

    public static Collection<User> getAllUsers() {
        return _users.values();
    }

    public static User getUser(int id) {
        return _users.get(id);
    }

    public static void setUser(User u) {
        _users.put(u.getUserId(), u);
        // set update to server
    }

    public static List<Event> getHostingEvents() {
        List<Event> temp = new LinkedList<>();
        for (Event e: _events.values()) {
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
        }
        return temp;
    }

    public static List<Event> getAttendingEvents() {
        List<Event> temp = new LinkedList<>();
        for (Event e: _events.values()) {
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);
            temp.add(e);

        }
        return temp;
    }

    public static List<Event> getNearbyEvents() {
        return new LinkedList<Event>();
    }

    public static Collection<Event> getAllEvents() {
        return _events.values();
    }

    public static Event getEvent(int id) {
        return _events.get(id);
    }

    public static void setEvent(Event e) {
        _events.put(e.getEventId(), e);
    }

    public static List<Conversation> getAllConversations() {
//        // Need to return in order of time
//        return (List) _conversations.values();

        List<Conversation> temp = new LinkedList<Conversation>();
        User u1 = new User();
        u1.setUsername("dank memer");
        User u2 = new User();
        u2.setUsername("current user");
        Conversation c = new Conversation();
        c.addRecipient(u1);
        c.addRecipient(u2);
        Message m1 = new Message();
        m1.setConversation(c);
        m1.setSender(u1);
        m1.setBody("hey wanna smash? ;)");
        Message m2 = new Message();
        m2.setConversation(c);
        m2.setSender(u2);
        m2.setBody("my mom told me not to talk to strangers");
        Message m3 = new Message();
        m3.setConversation(c);
        m3.setSender(u1);
        m3.setBody("ayy lmao");
        List<Message> m = new LinkedList<Message>();
        m.add(m1);
        m.add(m2);
        m.add(m3);
        c.setMessages(m);
        temp.add(c);
        return temp;
    }

    public static Conversation getConversation(int id) {
        return _conversations.get(id);
    }

    public static void setConversation(Conversation c) {
        _conversations.put(c.getConversationId(), c);
    }

    public static List<Notification> getNotifications() {
        return new ArrayList<Notification>();
    }

    public static void clearData() {
        if (_users != null)
            _users.clear();

        if (_events != null)
            _events.clear();

        if (_conversations != null)
            _conversations.clear();

        if (_notifications != null)
            _notifications.clear();
    }

    public static String getServerUrl() {
        return _serverURL;
    }

    public static void syncWithServer() {
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
