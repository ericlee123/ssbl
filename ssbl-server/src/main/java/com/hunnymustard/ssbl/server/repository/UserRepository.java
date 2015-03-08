package com.hunnymustard.ssbl.server.repository;

import java.util.List;

import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.model.User;

public interface UserRepository extends GenericRepository<User, Integer> {

	User findByCredentials(String username, String password);
	List<User> findByProximity(Location current, Double radius);
	List<User> findByExample(User example);
	
}
