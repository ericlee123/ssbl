package com.hunnymustard.ssbl.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.EventRepository;
import com.hunnymustard.ssbl.server.repository.UserRepository;
import com.hunnymustard.ssbl.server.service.EditService;

public class EditServiceHibernate implements EditService {

	@Autowired
	private UserRepository _userRepository;
	
	@Autowired
	private EventRepository _eventRepository;
	
	@Override
	public User update(User user) {
		return _userRepository.update(user);
	}

	@Override
	public Event update(Event event) {
		return _eventRepository.update(event);
	}

}
