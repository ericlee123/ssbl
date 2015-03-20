package com.hunnymustard.ssbl.server.controller;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {

	@ResponseStatus(value=HttpStatus.CONFLICT, reason="Data integrity violation")  // 409
	@ExceptionHandler(DataIntegrityViolationException.class)
	public void handleDataConflict() {
		// http://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
	}
	
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Database Error")
	@ExceptionHandler({SQLException.class, DataAccessException.class})
	public void handleDatabaseError() {
		// http://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
	}
}
