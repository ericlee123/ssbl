package com.hunnymustard.ssbl.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.EventRepository;
import com.hunnymustard.ssbl.server.repository.UserRepository;
import com.hunnymustard.ssbl.server.service.EditService;

@Service("editService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
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
	public void delete(User user) {
		_userRepository.remove(user);
	}

	@Override
	public Event update(Event event) {
		return _eventRepository.update(event);
	}

	@Override
	public void delete(Event event) {
		_eventRepository.remove(event);
	}
	
}
