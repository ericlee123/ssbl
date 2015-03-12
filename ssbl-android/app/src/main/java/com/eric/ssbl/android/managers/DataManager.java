package com.eric.ssbl.android.managers;

import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Game;
import com.eric.ssbl.android.pojos.Location;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.Notification;
import com.eric.ssbl.android.pojos.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DataManager {

    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/SSBLServer";
    private static User _curUser;
    private static HashMap<Integer, User> _users;
    private static HashMap<Integer, Event> _events;
    private static HashMap<Integer, Conversation> _conversations;
    private static HashMap<Integer, Notification> _notifications;

    private DataManager() {}

    public static void init() {
        _users = new HashMap<>();
        _events = new HashMap<>();

        // fill with test data
        User u1 = new User();
        u1.setUserId(1);
        u1.setUsername("test");
        u1.setBlurb("I am a test account square up");
        Location l1 = new Location(11, 30.287714, -97.739243);
        u1.setLocation(l1);
        u1.setLastLocationTime(1426110640403L);
        u1.setLastLoginTime(1426110640303L);
        List<Game> g1 = new ArrayList<Game>();
        g1.add(Game.MELEE);
        u1.setGames(g1);

        User u2 = new User();
        u2.setUserId(2);
        u2.setUsername("MioM | Mango");
        u2.setBlurb("ayy lmao");
        Location l3 = new Location(13, 30.286297, -97.736615);
        u2.setLocation(l3);
        u2.setLastLocationTime(1426100000000L);
        u2.setLastLoginTime(1426110640303L);
        u2.setGames(g1);

        Event e1 = new Event();
        e1.setEventId(21);
        e1.setTitle("Smashfest 2015");
        e1.setDescription("Smash. My place. Don't even knock. Just come right the fuck in. ;)");
        Location l2 = new Location(12, 30.285482, -97.742108);
        e1.setLocation(l2);
        e1.setStartTime(1426121640403L);
        e1.setEndTime(1426131640403L);
        e1.setHost(u1);
        e1.setPublic(true);
        e1.setGames(g1);
        List<User> fuck = new ArrayList<User>();
        fuck.add(u1);
        fuck.add(u2);

        setCurUser(u1);
        _users.put(u1.getUserId(), u1);
        _users.put(u2.getUserId(), u2);
        _events.put(e1.getEventId(), e1);
    }

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

    public static List<Notification> getAllNotifications() {
        return (List<Notification>) _notifications.values();
    }

    public static Notification getNotification(int id) {
        return _notifications.get(id);
    }

    public static void setNotification(Notification n) {
        _notifications.put(n.getNotificationId(), n);
    }

    public static String getServerUrl() {
        return _serverURL;
    }
}
