package com.eric.ssbl.android.pojos;

import android.app.Notification;
import android.location.Location;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * This entity represents a User. Users are account holders in the application.
 * A variety of information about users is stored in the database including a history
 * of attended events, a short blurb or bio, login credentials, etc. Users are one
 * of the central entities in the smash locator.
 * 
 * @author ashwin
 */
public class User {

	private Integer _id;
	private String _username, _password, _email, _blurb;
	private Location _location;
	private Long _lastLoginTime;
	private Long _lastLocationTime;
	private Long _lastMessageTime;
	private Boolean _isPrivate;
	private List<Game> _games;
	private List<Event> _events;
	private List<Notification> _notifs;
	private List<User> _friends;
	private List<Conversation> _conversations;
	
	public User() {}
	
	public User(Integer id, String username, String password, String email, String blurb,
			Location location, Long lastLoginTime, Long lastLocationTime, Long lastMessageTime, 
			Boolean isPrivate, List<Game> games, List<Event> events, List<Notification> notifs, 
			List<User> friends, List<Conversation> conversations) {
		
		_id = id;
		_username = username;
		_password = password;
		_email = email;
		_blurb = blurb;
		_location = location;
		_lastLoginTime = lastLoginTime;
		_lastLocationTime = lastLocationTime;
		_lastMessageTime = lastMessageTime;
		_games = games;
		_events = events;
		_notifs = notifs;
		_friends = friends;
		_conversations = conversations;
		_isPrivate = isPrivate;
	}

	public Integer getUserId() {
		return _id;
	}
	
	public void setUserId(Integer id) {
		_id = id;
	}

	public String getUsername() {
		return _username;
	}
	
	public void setUsername(String username) {
		_username = username;
	}

	public String getPassword() {
		return _password;
	}
	
	public void setPassword(String password) {
		_password = password;
	}

	public String getEmail() {
		return _email;
	}
	
	public void setEmail(String email) {
		_email = email;
	}

	public Location getLocation() {
		return _location;
	}
	
	public void setLocation(Location location) {
		_location = location;
	}

	public Long getLastLoginTime() {
		return _lastLoginTime;
	}
	
	public void setLastLoginTime(Long lastLoginTime) {
		_lastLoginTime = lastLoginTime;
	}

	public Long getLastLocationTime() {
		return _lastLocationTime;
	}
	
	public void setLastLocationTime(Long lastLocationTime) {
		_lastLocationTime = lastLocationTime;
	}

	public Long getLastMessageTime() {
		return _lastMessageTime;
	}
	
	public void setLastMessageTime(Long lastMessageTime) {
		_lastMessageTime = lastMessageTime;
	}

	public String getBlurb() {
		return _blurb;
	}
	
	public void setBlurb(String blurb) {
		_blurb = blurb;
	}

	public Boolean isPrivate() {
		return _isPrivate;
	}
	
	public void setPrivate(Boolean isPrivate) {
		_isPrivate = isPrivate;
	}

	public List<Game> getGames() {
		return _games;
	}
	
	public void setGames(List<Game> games) {
		_games = games;
	}
	
	public void addGame(Game game) {
		if(_games == null) _games = new ArrayList<Game>();
		_games.add(game);
	}

	public List<Event> getEvents() {
		return _events;
	}
	
	public void setEvents(List<Event> events) {
		_events = events;
	}
	
	public void addEvent(Event event) {
		if(_events == null) _events = new ArrayList<Event>();
		_events.add(event);
	}

	public List<Notification> getNotifications() {
		return _notifs;
	}
	
	public void setNotifications(List<Notification> notifs) {
		_notifs = notifs;
	}
	
	public void addNotification(Notification notif) {
		if(_notifs == null) _notifs = new ArrayList<Notification>();
		_notifs.add(notif);
	}

	public List<User> getFriends() {
		return _friends;
	}
	
	public void setFriends(List<User> friends) {
		_friends = friends;
	}
	
	public void addFriend(User friend) {
		if(_friends == null) _friends = new ArrayList<User>();
		_friends.add(friend);
	}

	public List<Conversation> getConversations() {
		return _conversations;
	}
	
	public void setConversations(List<Conversation> conversations) {
		_conversations = conversations;
	}
	
	public void addConversation(Conversation conversation) {
		if(_conversations == null) _conversations = new ArrayList<Conversation>();
		_conversations.add(conversation);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_id)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User)
			return new EqualsBuilder()
				.append(_id, ((User) obj).getUserId())
				.build();
		return false;
	}
}
