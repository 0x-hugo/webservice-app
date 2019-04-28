package com.empanada.app.webservice.exceptions;

public class UserServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3926840436818823961L;

	public UserServiceException(String message) {
		super (message);
	}
}
