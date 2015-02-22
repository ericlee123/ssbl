package com.hunnymustard.ssbl.server.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Event;

@Repository("eventRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class EventRepository extends GenericRepository<Event, Integer> {

	@Override
	public Event find(Integer key) {
		return (Event) _factory.getCurrentSession().get(Event.class, key);
	}
}
