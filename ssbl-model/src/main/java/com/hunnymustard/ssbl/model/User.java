package com.hunnymustard.ssbl.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="users")
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class User {

	private Integer _id;
	private String _username, _password, _email, _blurb;
	private Location _location;
	private Long _refreshTime;
	private List<Game> _games;
	private List<Event> _events;
	private List<Notification> _notifs;
	
	public User() {}
	
	public User(Integer id, String username, String password, String email, String blurb,
			Location location, Long refreshTime, List<Game> games, List<Event> events,
			List<Notification> notifs) {
		
		_id = id;
		_username = username;
		_password = password;
		_email = email;
		_blurb = blurb;
		_location = location;
		_refreshTime = refreshTime;
		_games = games;
		_events = events;
		_notifs = notifs;
	}
	
	@Id
	@GenericGenerator(name="gen",strategy="increment")
	@GeneratedValue(generator="gen")
	@Column(name="user_id", unique=true, nullable=false)
	public Integer getUserId() {
		return _id;
	}
	
	public void setUserId(int id) {
		_id = id;
	}
	
	@Column(name="username", nullable=false)
	public String getUsername() {
		return _username;
	}
	
	public void setUsername(String username) {
		_username = username;
	}
	
	@Column(name="password", nullable=false)
	@Basic(fetch=FetchType.LAZY)
	public String getPassword() {
		return _password;
	}
	
	public void setPassword(String password) {
		_password = password;
	}
	
	@Column(name="email", nullable=false)
	@Basic(fetch=FetchType.LAZY)
	public String getEmail() {
		return _email;
	}
	
	public void setEmail(String email) {
		_email = email;
	}
	
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="location_id")
	public Location getLocation() {
		return _location;
	}
	
	public void setLocation(Location location) {
		_location = location;
	}
	
	@Column(name="refresh_time")
	@Basic(fetch=FetchType.LAZY)
	public Long getRefreshTime() {
		return _refreshTime;
	}
	
	public void setRefreshTime(long refreshTime) {
		_refreshTime = refreshTime;
	}
	
	@Column(name="blurb")
	public String getBlurb() {
		return _blurb;
	}
	
	public void setBlurb(String blurb) {
		_blurb = blurb;
	}
	
	@ElementCollection(targetClass=Game.class)
    @JoinTable(name="user_games",
            joinColumns = {@JoinColumn(name="user_id")})
    @Column(name="game_id")
	@Enumerated(EnumType.ORDINAL)
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
	
    @ManyToMany(mappedBy="users", fetch=FetchType.EAGER)
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
	
    @OneToMany(cascade=CascadeType.ALL, mappedBy="receiver")
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
