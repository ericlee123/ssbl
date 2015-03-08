package com.hunnymustard.ssbl.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.service.AuthService;
import com.hunnymustard.ssbl.server.service.MessagingService;

// ssbl-server/ssbl/URL

@Controller
@RequestMapping("/ssbl")
public class MessagingController {

	@Autowired
	private MessagingService _messagingService;
	
	@Autowired
	private AuthService _authService;
	
	@RequestMapping(method=RequestMethod.GET, value="{username}/{id}/messaging/new")
	public @ResponseBody List<Message> getNewMessages(@PathVariable String username, @PathVariable Integer id) {
		User user = _authService.getByParameters(username, id);
		if(user == null) return null;
		
		return _messagingService.getNewMessages(user);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="{username}/{id}/messaging")
	public @ResponseBody List<Message> getAdditionalMessages(@PathVariable String username, @PathVariable Integer id,
			@RequestParam("conversation") Integer conversationId, @RequestParam("size") Integer size, 
			@RequestParam("additional") Integer additional) {
	
		User user = _authService.getByParameters(username, id);
		if(user == null) return null;
		
		return _messagingService.getAdditionalMessages(conversationId, size, additional);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/{username}/{id}/messaging/send")
	public @ResponseBody Message sendMessage(@PathVariable String username, 
			@PathVariable Integer id, @RequestBody Message message) {
		
		User user = _authService.getByParameters(username, id);
		if(user == null) return null;
		
		return _messagingService.sendMessage(message);
	}
}
