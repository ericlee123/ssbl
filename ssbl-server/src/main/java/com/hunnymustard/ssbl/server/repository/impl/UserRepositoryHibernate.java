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

import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.UserRepository;

@Repository("userRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class UserRepositoryHibernate extends HibernateRepository<User, Integer> implements UserRepository {

	@Override
	public User find(Integer key) {
		return (User) getSession().get(User.class, key);
	}

	@Override
	public User findByCredentials(String username, String password) {
		User user = (User) getSession().createCriteria(User.class)
				.add(Restrictions.eq("username", username))
				.add(Restrictions.eq("password", password))
				.uniqueResult();
		
		if(user != null) {
			Hibernate.initialize(user.getEmail());
			Hibernate.initialize(user.getLocation());
			Hibernate.initialize(user.getGames());
			Hibernate.initialize(user.getNotifications());
			Hibernate.initialize(user.getEvents());
		}
		
		return user;
	}
	
	@Override
	public User findByParameters(String username, Integer id) {
		User user = (User) getSession().createCriteria(User.class)
				.add(Restrictions.eq("username", username))
				.add(Restrictions.idEq(id))
				.uniqueResult();
		
		if(user != null) {
			Hibernate.initialize(user.getEmail());
			Hibernate.initialize(user.getLocation());
			Hibernate.initialize(user.getGames());
			Hibernate.initialize(user.getNotifications());
			Hibernate.initialize(user.getEvents());
		}
		
		return user;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findByProximity(Location cur, Double radius) {
		// https://docs.jboss.org/hibernate/search/4.2/reference/en-US/html/spatial.html way to improve
		// using spatial hibernate queries.
		String hql = "from User user inner join fetch user.location as loc where acos("
				+ "sin(:lat1/57.2958) * sin(loc.latitude/57.2958) + cos(:lat1/57.2958) "
				+ "* cos(loc.latitude/57.2958) *  cos((loc.longitude - :lon1)/57.2958)) * 3956 <= :dist";
		
		Query query = getSession().createQuery(hql);
		query.setDouble("lat1", cur.getLatitude());
		query.setDouble("lon1", cur.getLongitude());
		query.setDouble("dist", radius);
		
		List<User> users = (List<User>) query.list();
		for(User user : users) {
			Hibernate.initialize(user.getLocation());
			Hibernate.initialize(user.getGames());
			Hibernate.initialize(user.getEvents());
		}
		
		return users;
	}

	@SuppressWarnings("unchecked")
	public List<User> findByExample(User example) {
		List<User> users = (List<User>) getSession()
				.createCriteria(User.class)
				.add(Example.create(example).enableLike(MatchMode.ANYWHERE))
				.list();
		
		for(User user : users) {
			Hibernate.initialize(user.getLocation());
			Hibernate.initialize(user.getGames());
			Hibernate.initialize(user.getEvents());
		}
		
		return users;
	}
}
