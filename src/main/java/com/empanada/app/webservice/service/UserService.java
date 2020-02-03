package com.empanada.app.webservice.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;

public interface UserService extends UserDetailsService {

	UserBasicInformationDTO createUser (UserBasicInformationDTO user);
	
	UserBasicInformationDTO getUserByEmail (String email);
	
	UserBasicInformationDTO getUserByUserId (String UserId);
	
	UserBasicInformationDTO updateUser (String userId, UserBasicInformationDTO user);
	
	void deleteUser (String userId);

	List<UserBasicInformationDTO> getUsers(int page, int value);

	boolean verifyEmailToken(String token);
}
