package com.empanada.app.webservice.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.empanada.app.webservice.exceptions.UserNotFoundException;
import com.empanada.app.webservice.pagination.Page;
import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;

public interface UserService extends UserDetailsService {

	UserBasicInformationDTO createUser (UserBasicInformationDTO user);
	
	UserBasicInformationDTO getUserByEmail (String email);
	
	UserBasicInformationDTO getUserByPublicUserId (String UserId);
	
	UserBasicInformationDTO updateUser (String userId, UserBasicInformationDTO user);
	
	void deleteUserByPublicUserId (String userId) throws UserNotFoundException;

	List<UserBasicInformationDTO> getUsersIndexedByPage(Page pagination);

	boolean verifyEmailToken(String token);
}
