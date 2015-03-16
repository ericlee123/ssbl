package com.hunnymustard.ssbl.server.repository.impl;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.EventRepository;

@Repository("eventRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class EventRepositoryHibernate extends HibernateRepository<Event, Integer> implements EventRepository {

	@Override
	public Event find(Integer key) {
		return (Event) getSession().get(Event.class, key);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Event> findByTitle(String title) {
		List<Event> events = (List<Event>) getSession().createCriteria(Event.class)
				.add(Restrictions.ilike("title", title))
				.list();
		
		for(Event event : events) {
			Hibernate.initialize(event.getGames());
			Hibernate.initialize(event.getUsers());
			Hibernate.initialize(event.getHost());
			Hibernate.initialize(event.getLocation());
		}
		
		return events;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Event> findByProximity(Location cur, Double radius) {
		String hql = "from User user inner join fetch user.location as loc where loc.id != :id and acos("
				+ "sin(:lat1/57.2958) * sin(loc.latitude/57.2958) + cos(:lat1/57.2958) "
				+ "* cos(loc.latitude/57.2958) *  cos((loc.longitude - :lon1)/57.2958)) * 3956 <= :dist";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("id", cur.getLocationId());
		query.setDouble("lat1", cur.getLatitude());
		query.setDouble("lon1", cur.getLongitude());
		query.setDouble("dist", radius);
		
		List<Event> events = (List<Event>) query.list();
		for(Event event : events) {
			Hibernate.initialize(event.getGames());
			Hibernate.initialize(event.getUsers());
			Hibernate.initialize(event.getHost());
			Hibernate.initialize(event.getLocation());
		}
		
		return events;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Event> findByExample(Event example) {
		List<Event> events = (List<Event>) getSession()
				.createCriteria(Event.class)
				.add(Example.create(example).enableLike(MatchMode.ANYWHERE))
				.list();
		
		for(Event event : events) {
			Hibernate.initialize(event.getGames());
			Hibernate.initialize(event.getUsers());
			Hibernate.initialize(event.getHost());
			Hibernate.initialize(event.getLocation());
		}
		
		return events;	
	}
}
