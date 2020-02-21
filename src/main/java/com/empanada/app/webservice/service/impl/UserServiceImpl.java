package com.empanada.app.webservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.empanada.app.webservice.exceptions.UserServiceException;
import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.io.repository.impl.UserRepositoryPagination;
import com.empanada.app.webservice.pagination.Page;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.Utils;
import com.empanada.app.webservice.shared.dto.UserAdressDTO;
import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;
import com.empanada.app.webservice.ui.model.response.ErrorMessages;
import com.empanada.app.webservice.ui.utils.PageRequestWrapper;

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
	public UserBasicInformationDTO createUser(UserBasicInformationDTO user) {
		
		//check if email address already exist
		if (userRepository.findByEmail(user.getEmail()) != null) throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage());

		for (int i = 0; i < user.getAddresses().size(); i++) {
			UserAdressDTO address = user.getAddresses().get(i);
			
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);
		}
		ModelMapper modelMapper = new ModelMapper();
		//BeanUtils.copyProperties(user, userEntity);
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		String publicUserId = utils.generateUserId(30);
		userEntity.setPublicUserId(publicUserId); 
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(Utils.generateVerificationToken(publicUserId));
				
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserBasicInformationDTO userDtoCreationDetails = modelMapper.map(storedUserDetails, UserBasicInformationDTO.class);

//		new AmazonSES().verifyEmail(userDtoCreationDetails);
		
		
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
	public UserBasicInformationDTO getUserByEmail(String email) throws UserServiceException{
		UserEntity userDetails = userRepository.findByEmail(email);
		if (userDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		UserBasicInformationDTO userDtoInformation = new UserBasicInformationDTO();
		BeanUtils.copyProperties(userDetails, userDtoInformation);
		
		return userDtoInformation;
	}

	@Override
	public UserBasicInformationDTO getUserByPublicUserId(String userId) throws UserServiceException{
		 UserEntity userDetails = userRepository.findByPublicUserId(userId);
		 if (userDetails == null) 
			 throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		 
		 UserBasicInformationDTO userDtoInformation = new UserBasicInformationDTO();
		 BeanUtils.copyProperties(userDetails, userDtoInformation);
		 
		 return userDtoInformation;
	}
	
	@Override
	public UserBasicInformationDTO updateUser(String userId, UserBasicInformationDTO user) throws UserServiceException{
		UserEntity userDetails = userRepository.findByPublicUserId(userId);
		//I don't know if this exception is clear enough. Maybe change this in a near future
		if(userDetails == null) throw new UserServiceException(ErrorMessages.COULD_NOT_UPDATE_RECORD.getErrorMessage());
		
		//BeanUtils.copyProperties(user, userDetails); This caused issues on identifier instance altered. I decided to use SET as a better alternative
		userDetails.setFirstName(user.getFirstName());
		userDetails.setLastName(user.getLastName());
		userRepository.save(userDetails);
		
		UserBasicInformationDTO userDto = new UserBasicInformationDTO();
		BeanUtils.copyProperties(userDetails, userDto);
		
		return userDto;
	}
	
	@Override
	public void deleteUser(String userId) throws UserServiceException {
		UserEntity userDetails = userRepository.findByPublicUserId(userId);
		if (userDetails == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		userRepository.delete(userDetails);
		
	}

	@Override
	public List<UserBasicInformationDTO> getUsersIndexedByPage(Page page) {
		UserRepositoryPagination userResultPagination = new UserRepositoryPagination(userRepository);
		List<UserEntity> userListDetails = userResultPagination.getUsers(PageRequestWrapper.of(page));
		return copyModelToResponse(userListDetails);
	}
	
	private List<UserBasicInformationDTO> copyModelToResponse(List<UserEntity> userListDetails) {
		List<UserBasicInformationDTO> returnValue = new ArrayList<>();
		for(final UserEntity user : userListDetails) {
			UserBasicInformationDTO userModel = new UserBasicInformationDTO();
			BeanUtils.copyProperties(user, userModel);
			returnValue.add(userModel);
		}
		
		if (returnValue.isEmpty())
			return Collections.emptyList();
		
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
