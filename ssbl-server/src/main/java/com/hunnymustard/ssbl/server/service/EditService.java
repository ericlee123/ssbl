package com.hunnymustard.ssbl.server.service;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.User;

public interface EditService {

	User update(User user);
	Event update(Event event);
	
}
