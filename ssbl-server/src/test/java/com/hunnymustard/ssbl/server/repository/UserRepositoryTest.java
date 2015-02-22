package com.hunnymustard.ssbl.server.repository;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hunnymustard.ssbl.server.config.ServerConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServerConfig.class})
@TransactionConfiguration(defaultRollback=true)
@WebAppConfiguration
public class UserRepositoryTest extends TestCase {

	@Autowired
	public UserRepository _userRepository;
	
	@Test
	public void testFindById() {
		assertNotNull(_userRepository.find(1));					// Pass on valid id
		assertNull(_userRepository.find(-1));					// Fail on invalid id
	}
	
	@Test
	public void testFindByCredentials() {
		assertNotNull(_userRepository.find("ashwin", "p0"));	// Pass on valid credentials
		assertNull(_userRepository.find("ashwin", "p1"));		// Fail on invalid password
		assertNull(_userRepository.find("ashwi ", "p0"));		// Fail on invalid username
		assertNull(_userRepository.find("ashwi ", "p1"));		// Fail on invalid credentials
	}
}
