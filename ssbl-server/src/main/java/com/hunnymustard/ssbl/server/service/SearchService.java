package com.hunnymustard.ssbl.server.service;

public interface SearchService {

	List<User> getByProximity();
	List<Event> getByProximity();
	List<User> getByExample();
	List<User> getByExample();
	
}
