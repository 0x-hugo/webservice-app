package com.empanada.app.webservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.empanada.app.webservice.exceptions.UserServiceException;
import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.Utils;
import com.empanada.app.webservice.shared.dto.AddressDto;
import com.empanada.app.webservice.shared.dto.UserDto;
import com.empanada.app.webservice.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	AddressService addressService;
	
	@Override
	public UserDto createUser(UserDto user) {
		
		//check if email address already exist
		if (userRepository.findByEmail(user.getEmail()) != null) throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

		for (int i = 0; i < user.getAddresses().size(); i++) {
			AddressDto address = user.getAddresses().get(i);
			
			//Implementing UserDetails inside address 
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}
		ModelMapper modelMapper = new ModelMapper();
		//BeanUtils.copyProperties(user, userEntity);
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId); 
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(Utils.generateVerificationToken(publicUserId));
		
		

		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserDto userDtoCreationDetails = new UserDto();
		//BeanUtils.copyProperties(storedUserDetails, userDtoCreationDetails);
		modelMapper.map(storedUserDetails, userDtoCreationDetails);
		
		return userDtoCreationDetails;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UserServiceException {
		UserEntity userLoginDetails = userRepository.findByEmail(email);
		
		if (userLoginDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		//User is a Spring Security BEAN
		return new User(userLoginDetails.getEmail(), userLoginDetails.getEncryptedPassword(),
						userLoginDetails.getEmailVerficationStatus(),
						true, true, true, new ArrayList<>());
		
//		return new User(userLoginDetails.getEmail(), userLoginDetails.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUserByEmail(String email) throws UserServiceException{
		
		UserEntity userDetails = userRepository.findByEmail(email);
		if (userDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		UserDto userDtoInformation = new UserDto();
		BeanUtils.copyProperties(userDetails, userDtoInformation);
		
		return userDtoInformation;
	}

	@Override
	public UserDto getUserByUserId(String userId) throws UserServiceException{
		 UserEntity userDetails = userRepository.findByUserId(userId);
		 if (userDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		 
		 UserDto userDtoInformation = new UserDto();
		 BeanUtils.copyProperties(userDetails, userDtoInformation);
		 
		 return userDtoInformation;
	}
	
	@Override
	public UserDto updateUser(String userId, UserDto user) throws UserServiceException{
		UserEntity userDetails = userRepository.findByUserId(userId);
		//I don't know if this exception is clear enough. Maybe change this in a near future
		if(userDetails == null) throw new UserServiceException(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage());
		
		//BeanUtils.copyProperties(user, userDetails); This caused issues on identifier instance altered. I decided to use SET as a better alternative
		userDetails.setFirstName(user.getFirstName());
		userDetails.setLastName(user.getLastName());
		userRepository.save(userDetails);
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		return userDto;
	}
	
	@Override
	public void deleteUser(String userId) throws UserServiceException {
		UserEntity userDetails = userRepository.findByUserId(userId);
		if (userDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.delete(userDetails);
		
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		
		List<UserDto> returnValue = new ArrayList<>();
		
		PageRequest pageableRequest = PageRequest.of(page, limit);
		
		Page<UserEntity> userPage = userRepository.findAll(pageableRequest);

		List<UserEntity> userListDetails = userPage.getContent();
		
		for(final UserEntity user : userListDetails) {
			UserDto userModel = new UserDto();
			BeanUtils.copyProperties(user, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
	}

	// I will add a new token on the user so it can match with the one in db. 
	// after that, null the field so you can't verify it twice
	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue = false; 
		
		UserEntity userEntity = userRepository.findByEmailVerificationToken(token);
		
		if(userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if(!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerficationStatus(Boolean.TRUE);
				userRepository.save(userEntity);
				returnValue = true;
			}
		}
		
		return returnValue;
	}

}
