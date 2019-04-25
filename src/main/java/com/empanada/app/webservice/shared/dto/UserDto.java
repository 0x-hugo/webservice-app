package com.empanada.app.webservice.shared.dto;

import java.io.Serializable;

public class UserDto implements Serializable{

	//bc of dto
	private static final long serialVersionUID = 1L;
	//db id
	private long id;
	//public id
	private String userId;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String encryptedPassword;
	private String emailVerificationToken;
	private Boolean emailverificationStatus;
	
}
