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

import com.hunnymustard.ssbl.model.Event;
import com.hunnymustard.ssbl.model.Location;
import com.hunnymustard.ssbl.model.User;
import com.hunnymustard.ssbl.server.service.AuthService;
import com.hunnymustard.ssbl.server.service.SearchService;

@Controller
@RequestMapping("/search/{username}/{id}")
public class SearchController {

	@Autowired
	private SearchService _searchService;
	
	@Autowired
	private AuthService _authService;
	
	@RequestMapping(method=RequestMethod.GET, value="/user")
	public @ResponseBody List<User> getUsersByProximity(@PathVariable String username, 
			@PathVariable Integer id, @RequestParam("lat") Double lat, 
			@RequestParam("lon") Double lon, @RequestParam("radius") Double radius) {
		
		User user = _authService.getByParameters(username, id);
		if(user == null) return null;
		
		Location current = new Location(null, lat, lon);
		return _searchService.getUsersByProximity(current, radius);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/user")
	public @ResponseBody List<User> getUsersByExample(@PathVariable String username, 
			@PathVariable Integer id, @RequestBody User example) {
		
		User user = _authService.getByParameters(username, id);
		if(user == null) return null;
		
		return _searchService.getUsersByExample(example);
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value="/event")
	public @ResponseBody List<Event> getEventsByProximity(@PathVariable String username, 
			@PathVariable Integer id, @RequestParam("lat") Double lat, 
			@RequestParam("lon") Double lon, @RequestParam("radius") Double radius) {
		
		User user = _authService.getByParameters(username, id);
		if(user == null) return null;
		
		Location current = new Location(null, lat, lon);
		return _searchService.getEventsByProximity(current, radius);
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/event")
	public @ResponseBody List<Event> getUsersByExample(@PathVariable String username, 
			@PathVariable Integer id, @RequestBody Event example) {
		
		User user = _authService.getByParameters(username, id);
		if(user == null) return null;
		
		return _searchService.getEventsByExample(example);
	}
}
