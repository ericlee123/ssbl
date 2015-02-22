package com.hunnymustard.ssbl.server.repository;

import com.hunnymustard.ssbl.model.Event;

public class EventRepository extends GenericRepository<Event, Integer> {

	@Override
	public Event find(Integer key) {
		return (Event) _factory.getCurrentSession().get(Event.class, key);
	}
}
