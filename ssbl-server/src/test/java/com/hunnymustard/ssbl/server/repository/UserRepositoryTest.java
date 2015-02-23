package com.hunnymustard.ssbl.server.repository;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.server.config.ServerConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServerConfig.class})
@TransactionConfiguration(defaultRollback=true)
@WebAppConfiguration
public class UserRepositoryTest extends TestCase {

	@Autowired
	private UserRepository _userRepository;
	
	@Test
	public void testGetById() {
		// Pass on valid id
		assertNotNull(_userRepository.find(1));
		
		// Fail on invalid id
		assertNull(_userRepository.find(-1));
	}
	
	@Test
	public void testGetByCredentials() {
		// Pass on valid credentials
		assertNotNull(_userRepository.findByCredentials("ashwin", "p0"));
		
		// Fail on invalid password
		assertNull(_userRepository.findByCredentials("ashwin", "p1"));

		// Fail on invalid username
		assertNull(_userRepository.findByCredentials("ashwi", "p0"));
	}
	
	@Test
	public void testListByLocation() {
		Location cur = _userRepository.findByCredentials("ashwin", "p0").getLocation();		
		assertEquals(1, _userRepository.findByProximity(cur, 1500.00).size());
		assertEquals(0, _userRepository.findByProximity(cur, 1300.00).size());
	}
}
