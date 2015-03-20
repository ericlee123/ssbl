package com.hunnymustard.ssbl.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid Credentials")
public class AuthException extends RuntimeException {

	private static final long serialVersionUID = 5800571958302227712L;
	
}
