package com.hunnymustard.ssbl.server.service.impl;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.exceptions.AuthException;
import com.hunnymustard.ssbl.server.repository.UserRepository;
import com.hunnymustard.ssbl.server.service.AuthService;

@Service("authService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class AuthServiceHibernate implements AuthService {

	@Autowired
	private UserRepository _userRepository;

	@Override
	public User getByCredentials(String username, String password) {
		// Find the user by their credentials and update their login
		// timestamp in the database.
		User user = _userRepository.findByCredentials(username, password);
		if(user == null) throw new AuthException();
		
		user.setLastLoginTime(System.currentTimeMillis());
		user.setPassword((String) _userRepository.load(User.class, "password", user.getUserId()));
		user.setEmail((String) _userRepository.load(User.class, "email", user.getUserId()));
		Hibernate.initialize(user.getLocation());
		Hibernate.initialize(user.getGames());
		Hibernate.initialize(user.getNotifications());
		Hibernate.initialize(user.getEvents());
		Hibernate.initialize(user.getFriends());
		
		Hibernate.initialize(user.getConversations());
		return _userRepository.update(user);
	}

	@Override
	public User getByParameters(String username, Integer id) {
		User user = _userRepository.findByParameters(username, id);
		if(user == null) throw new AuthException();
		return _userRepository.findByParameters(username, id);
	}
	
}
