package com.empanada.app.webservice.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.empanada.app.webservice.shared.dto.UserDto;

public interface UserService extends UserDetailsService {

	UserDto createUser (UserDto user);
	
	UserDto getUserByEmail (String email);
	
	UserDto getUserByUserId (String UserId);
	
	UserDto updateUser (String userId, UserDto user);
	
	void deleteUser (String userId);

	List<UserDto> getUsers(int page, int value);

	boolean verifyEmailToken(String token);
}
