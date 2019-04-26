package com.empanada.app.webservice.impl;

import javax.management.RuntimeErrorException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empanada.app.webservice.UserRepository;
import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.UserDto;


@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDto createUser(UserDto user) {
		
		//check if email address already exist
		if (userRepository.findByEmail(user.getEmail()) != null) throw new RuntimeException("The user already exist");
		
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		//HardCode for test
		userEntity.setUserId("testUser");
		userEntity.setEncryptedPassword("tesst");
		
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserDto userDtoCreationDetails = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, userDtoCreationDetails);
		
		return userDtoCreationDetails;
	}

}
