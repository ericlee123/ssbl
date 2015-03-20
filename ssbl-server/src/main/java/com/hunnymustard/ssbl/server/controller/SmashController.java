package com.hunnymustard.ssbl.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.model.Message;
import com.hunnymustard.ssbl.model.Notification;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.exceptions.AuthException;
import com.hunnymustard.ssbl.server.service.AuthService;
import com.hunnymustard.ssbl.server.service.EditService;
import com.hunnymustard.ssbl.server.service.MessagingService;
import com.hunnymustard.ssbl.server.service.NotificationService;
import com.hunnymustard.ssbl.server.service.SearchService;

@Controller
@RequestMapping("/smash")
public class SmashController {

	@Autowired
	private SearchService _searchService;
	
	@Autowired
	private AuthService _authService;
	
	@Autowired
	private NotificationService _notificationService;
	
	@Autowired
	private MessagingService _messagingService;
	
	@Autowired
	private EditService _editService;
	
	/* Searching Mappings */
	@RequestMapping(method=RequestMethod.GET, value="/search/user")
	public @ResponseBody List<User> getUsersByProximity(@RequestParam("lat") Double lat, 
			@RequestParam("lon") Double lon, @RequestParam("radius") Double radius) {
		return _searchService.getUsersByProximity(new Location(null, lat, lon), radius);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/search/user")
	public @ResponseBody List<User> getUsersByExample(@RequestBody User example) {
		return _searchService.getUsersByExample(example);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/search/event")
	public @ResponseBody List<Event> getEventsByProximity(@RequestParam("lat") Double lat, 
			@RequestParam("lon") Double lon, @RequestParam("radius") Double radius) {
		return _searchService.getEventsByProximity(new Location(null, lat, lon), radius);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/search/event")
	public @ResponseBody List<Event> getUsersByExample(@RequestBody Event example) {
		return _searchService.getEventsByExample(example);
	}
	
	/* Auth Mappings */
	@RequestMapping(method=RequestMethod.GET, value="/auth/login")
	public @ResponseBody User login(@RequestHeader("username") String username, @RequestHeader("password") String password) {
		return _authService.getByCredentials(username, password);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/auth/register")
	public @ResponseBody User register(@RequestBody User user) {
		return _authService.register(user);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/messaging/{username}/{id}/new")
	public @ResponseBody List<Message> getNewMessages(@PathVariable String username, @PathVariable Integer id) {
		return _messagingService.getByNew(_authService.getByParameters(username, id));
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/messaging/{username}/{id}/{conversation}")
	public @ResponseBody List<Message> getAdditionalMessages(@PathVariable String username, @PathVariable Integer id,
			@PathVariable Integer conversation, @RequestParam("size") Integer size, 
			@RequestParam("additional") Integer additional) {
		_authService.getByParameters(username, id);
		return _messagingService.getByConversation(conversation, size, additional);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/messaging/{username}/{id}")
	public @ResponseBody Message sendMessage(@PathVariable String username, 
			@PathVariable Integer id, @RequestBody Message message) {
		if(!_authService.getByParameters(username, id).equals(message.getSender())) throw new AuthException();
		return _messagingService.send(message);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/messaging/delete")
	public void delete(@RequestBody Message message) {
		_messagingService.delete(message);
	}

	/* Notification Mappings */
	@RequestMapping(method=RequestMethod.GET, value="/notif/{username}/{id}")
	public @ResponseBody List<Notification> getNewNotifications(@PathVariable String username, @PathVariable Integer id) {
		return _notificationService.getByNew(_authService.getByParameters(username, id));
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/notif/{username}/{id}")
	public @ResponseBody Notification sendNotification(@PathVariable String username, @PathVariable Integer id, @RequestBody Notification notification) {
		if(!_authService.getByParameters(username, id).equals(notification.getSender())) throw new AuthException();
		return _notificationService.create(notification);
	}

	/* Edit Mappings */
	@RequestMapping(method=RequestMethod.POST, value="/edit/user/update")
	public @ResponseBody User updateUser(@RequestBody User user) {
		return _editService.update(user);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/edit/user/delete")
	public void deleteUser(@RequestBody User user) {
		_editService.delete(user);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/edit/event/update")
	public @ResponseBody Event updateEvent(@RequestBody Event event) {
		return _editService.update(event);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/edit/event/delete")
	public void deleteEvent(@RequestBody Event event) {
		_editService.delete(event);
	}
}
