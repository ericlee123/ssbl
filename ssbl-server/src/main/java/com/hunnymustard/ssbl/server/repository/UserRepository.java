package com.hunnymustard.ssbl.server.repository;

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

}
