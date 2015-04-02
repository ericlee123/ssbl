package com.hunnymustard.ssbl.server.service.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.EventRepository;
import com.hunnymustard.ssbl.server.repository.UserRepository;
import com.hunnymustard.ssbl.server.service.SearchService;

@Service("searchService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class SearchServiceHibernate implements SearchService {

	@Autowired
	private UserRepository _userRepository;
	
	@Autowired
	private EventRepository _eventRepository;
	
	@Override
	public List<User> getUsersByProximity(Location current, Double radius) {
		List<User> users = _userRepository.findByProximity(current, radius);
		for(User user : users) {
			Hibernate.initialize(user.getLastLocationTime());
			Hibernate.initialize(user.getLocation());
			Hibernate.initialize(user.getGames());
			Hibernate.initialize(user.getEvents());
		}
		return users;
	}

	@Override
	public List<User> getUsersByExample(User example) {
		List<User> users = _userRepository.findByExample(example);
		for(User user : users) {
			Hibernate.initialize(user.getLocation());
			Hibernate.initialize(user.getGames());
			Hibernate.initialize(user.getEvents());
			Hibernate.initialize(user.getFriends());
		}
		return users;
	}
	
	@Override
	public User getUserById(int userId) {
		User user = _userRepository.find(userId);
		Hibernate.initialize(user.getLocation());
		Hibernate.initialize(user.getGames());
		Hibernate.initialize(user.getEvents());
		Hibernate.initialize(user.getFriends());
		return user;
	}
	
	@Override
	public List<Event> getEventsByProximity(Location current, Double radius) {
		List<Event> events = _eventRepository.findByProximity(current, radius);
		for(Event event : events) {
			Hibernate.initialize(event.getGames());
			Hibernate.initialize(event.getUsers());
			Hibernate.initialize(event.getHost());
			Hibernate.initialize(event.getLocation());
		}
		return events;
	}

	@Override
	public List<Event> getEventsByExample(Event example) {
		List<Event> events = _eventRepository.findByExample(example);
		for(Event event : events) {
			Hibernate.initialize(event.getGames());
			Hibernate.initialize(event.getUsers());
			Hibernate.initialize(event.getHost());
			Hibernate.initialize(event.getLocation());
		}
		return events;
	}
	
	@Override
	public Event getEventById(int eventId) {
		Event event = _eventRepository.find(eventId);
		Hibernate.initialize(event.getGames());
		Hibernate.initialize(event.getUsers());
		Hibernate.initialize(event.getHost());
		Hibernate.initialize(event.getLocation());
		return event;
	}
}
