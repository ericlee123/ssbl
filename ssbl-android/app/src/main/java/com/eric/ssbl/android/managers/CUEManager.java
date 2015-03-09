package com.eric.ssbl.android.managers;

import com.eric.ssbl.android.pojos.Conversation;
import com.eric.ssbl.android.pojos.Event;
import com.eric.ssbl.android.pojos.Message;
import com.eric.ssbl.android.pojos.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CUEManager {

    private static String _serverURL = "http://ec2-54-69-43-179.us-west-2.compute.amazonaws.com:8080/SSBLServer";
    private static String _curUser;
    private static HashMap<String, User> _users;
    private static HashMap<String, Event> _events;
    private static HashMap<String, Conversation> _conversations;

    private CUEManager() {}

    public static String getCurUser() {
        return "current user";
    }

    public static void setCurUser(String curUser) {
        _curUser = curUser;
    }



    public static List<Conversation> getConversations() {
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


    public String getServerUrl() {
        return _serverURL;
    }
}
