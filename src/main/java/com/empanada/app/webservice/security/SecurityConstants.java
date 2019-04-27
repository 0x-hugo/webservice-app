package com.empanada.app.webservice.security;

import com.empanada.app.webservice.SpringApplicationContext;

public class SecurityConstants {
	
	public static final long EXPIRATION_DATE = 864000000; 		//10 days in milisec
	public static final String TOKEN_PREFIX = "Empanada ";		//Prefix for token creator
	public static final String HEADER_STRING = "Authorization"; //Name of Header for authentication
	public static final String SIGN_UP_URL = "/users"; 			//URL for request 
//	public static final String TOKEN_SECRET = "jf9i4jgu83nfl0";	//Secret generator

	public static String getTokenSecret() {
		AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
		return appProperties.getSecretToken();
	}
}
