package com.hunnymustard.ssbl.server.repository;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.User;

@Repository("userRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class UserRepository extends GenericRepository<User, Integer> {

	@Override
	public User find(Integer key) {
		return (User) _factory.getCurrentSession().get(User.class, key);
	}

	public User find(String username, String password) {
		User user = (User) _factory.getCurrentSession().createCriteria(User.class)
				.add(Restrictions.eq("username", username))
				.add(Restrictions.eq("password", password))
				.uniqueResult();
		
		if(user != null) {
			Hibernate.initialize(user.getEmail());
			Hibernate.initialize(user.getLocation());
			Hibernate.initialize(user.getRefreshTime());
			Hibernate.initialize(user.getNotifications());
			Hibernate.initialize(user.getGames());
		}
		
		return user;
	}
}
