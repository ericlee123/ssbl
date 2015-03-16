package com.hunnymustard.ssbl.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/status")
public class StatusController {

	@RequestMapping(method=RequestMethod.GET)
	public @ResponseBody String hello() {
		return "Hello, World!";
	}
	
}
