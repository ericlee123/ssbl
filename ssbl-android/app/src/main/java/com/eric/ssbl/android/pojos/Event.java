package com.eric.ssbl.android.pojos;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * This entity represents a smash bros. event. Events are created by a host user at
 * a specified location over a specified duration of time. Other users can choose to
 * join the event if it is public, or can be invited to the event otherwise.
 * 
 * @author ashwin
 * 
 * @see com.hunnymustard.ssbm.model.User
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class Event {

	private Integer _id;
	private String _title, _description;
	private Location _location;
	private Long _startTime, _endTime;
	private User _host;
	private Boolean _public;
	private List<Game> _games;
	private List<User> _users;
	
	public Event() {}
	
	public Event(Integer id, String title, String description, Location location, Long startTime,
			Long endTime, User host, Boolean publc, List<Game> games, List<User> users) {	
		
		_id = id;
		_title = title;
		_description = description;
		_location = location;
		_startTime = startTime;
		_endTime = endTime;
		_host = host;
		_public = publc;
		_games = games;
		_users = users;
	}

	public Integer getEventId() {
		return _id;
	}
	
	public void setEventId(Integer id) {
		_id = id;
	}

	public User getHost() {
		return _host;
	}
	
	public void setHost(User host) {
		_host = host;
	}

	public String getTitle() {
		return _title;
	}
	
	public void setTitle(String title) {
		_title = title;
	}

	public Location getLocation() {
		return _location;
	}
	
	public void setLocation(Location location) {
		_location = location;
	}

	public Long getStartTime() {
		return _startTime;
	}
	
	public void setStartTime(Long startTime) {
		_startTime = startTime;
	}

	public Long getEndTime() {
		return _endTime;
	}
	
	public void setEndTime(Long endTime) {
		_endTime = endTime;
	}

	public String getDescription() {
		return _description;
	}
	
	public void setDescription(String description) {
		_description = description;
	}

	public Boolean isPublic() {
		return _public;
	}
	
	public void setPublic(Boolean publc) {
		_public = publc;
	}

	public List<User> getUsers() {
		return _users;
	}
	
	public void setUsers(List<User> users) {
		_users = users;
	}
	
	public void addUser(User user) {
		if(_users == null) _users = new ArrayList<User>();
		_users.add(user);
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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(_id)
			.build();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Event)
			return new EqualsBuilder()
				.append(_id, ((Event) obj).getEventId())
				.build();
		return false;
	}
}
