package com.empanada.app.webservice.impl;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.empanada.app.webservice.exceptions.UserServiceException;
import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.UserDto;
import com.empanada.app.webservice.shared.utils.Utils;
import com.empanada.app.webservice.ui.model.response.ErrorMessages;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public UserDto createUser(UserDto user) {
		
		//check if email address already exist
		if (userRepository.findByEmail(user.getEmail()) != null) throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());
		
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		//HardCode for test
		userEntity.setUserId(utils.generateUserId(30));
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserDto userDtoCreationDetails = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, userDtoCreationDetails);
		
		return userDtoCreationDetails;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UserServiceException {
		UserEntity userLoginDetails = userRepository.findByEmail(email);
		
		if (userLoginDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		//User is a Spring Security BEAN
		return new User(userLoginDetails.getEmail(), userLoginDetails.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUserByEmail(String email) {
		
		UserEntity userDetails = userRepository.findByEmail(email);
		if (userDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		UserDto userDtoInformation = new UserDto();
		BeanUtils.copyProperties(userDetails, userDtoInformation);
		
		return userDtoInformation;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		 UserEntity userDetails = userRepository.findByUserId(userId);
		 if (userDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		 
		 UserDto userDtoInformation = new UserDto();
		 BeanUtils.copyProperties(userDetails, userDtoInformation);
		 
		 return userDtoInformation;
	} 

}
