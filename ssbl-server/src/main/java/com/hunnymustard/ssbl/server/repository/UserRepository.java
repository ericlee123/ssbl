package com.hunnymustard.ssbl.server.repository;

import com.hunnymustard.ssbl.model.User;

public class UserRepository extends GenericRepository<User, Integer> {

	@Override
	public User find(Integer key) {
		return (User) _factory.getCurrentSession().get(User.class, key);
	}

}
