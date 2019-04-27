package com.empanada.app.webservice.security;

public class SecurityConstants {
	
	public static final long EXPIRATION_DATE = 864000000; //10 days in milisec
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String TOKEN_SECRET = "jf9i4jgu83nfl0";

}
