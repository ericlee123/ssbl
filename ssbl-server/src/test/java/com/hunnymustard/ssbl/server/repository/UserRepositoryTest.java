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
	public void testFind() {
		
	}
}
