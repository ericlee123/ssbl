package com.hunnymustard.ssbl.server.service.impl;

import java.util.List;

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
		return _userRepository.findByProximity(current, radius);
	}

	@Override
	public List<User> getUsersByExample(User example) {
		return _userRepository.findByExample(example);
	}
	
	@Override
	public List<Event> getEventsByProximity(Location current, Double radius) {
		return _eventRepository.findByProximity(current, radius);
	}

	@Override
	public List<Event> getEventsByExample(Event example) {
		return _eventRepository.findByExample(example);
	}
}
