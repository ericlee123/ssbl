package com.hunnymustard.ssbl.server.repository;

import java.util.List;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.Location;

public interface EventRepository extends GenericRepository<Event, Integer> {

	List<Event> findByTitle(String title);
	List<Event> findByProximity(Location cur, Double radius);
	List<Event> findByExample(Event example);
	
}
