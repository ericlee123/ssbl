package com.hunnymustard.ssbl.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.repository.UserRepository;
import com.hunnymustard.ssbl.server.service.AuthService;

@Service("authService")
@Transactional(propagation = Propagation.REQUIRED, readOnly=false)
public class AuthServiceHibernate implements AuthService {

	@Autowired
	private UserRepository _userRepository;

	@Override
	public User getByCredentials(String username, String password) {
		return _userRepository.findByCredentials(username, password);
	}

	@Override
	public User getByParameters(String username, Integer id) {
		return _userRepository.findByParameters(username, id);
	}

	@Override
	public User register(User user) {
		return _userRepository.add(user);
	}

	@Override
	public User update(User user) {
		return _userRepository.update(user);
	}
	
}
