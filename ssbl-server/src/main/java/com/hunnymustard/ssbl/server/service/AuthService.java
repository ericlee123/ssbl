package com.hunnymustard.ssbl.server.service;

import com.hunnymustard.ssbl.model.User;

public interface AuthService {
	
	User getByCredentials(String username, String password);
	User getByParameters(String username, Integer id);
	
	User register(User user);
	User update(User user);
}
