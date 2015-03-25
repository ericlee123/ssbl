package com.hunnymustard.ssbl.server.service;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.User;

public interface EditService {

	User update(User user);
	void delete(User user);
	User create(User user);
	
	Event update(Event event);
	void delete(Event event);
	Event create(Event event);
}
