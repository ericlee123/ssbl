package com.hunnymustard.ssbl.server.service;

import java.util.List;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.model.User;

public interface SearchService {
	
	List<User> getUsersByProximity(Location current, Double radius);
	List<User> getUsersByExample(User example);

	List<Event> getEventsByProximity(Location current, Double radius);
	List<Event> getEventsByExample(Event example);
}
