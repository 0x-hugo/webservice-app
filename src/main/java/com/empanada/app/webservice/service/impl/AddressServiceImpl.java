package com.empanada.app.webservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empanada.app.webservice.io.entity.AddressEntity;
import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.AddressRepository;
import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.shared.dto.UserAdressDTO;

@Service
public class AddressServiceImpl implements AddressService {

	UserRepository userRepository;
	AddressRepository addressRepository;
	
	@Autowired
	public AddressServiceImpl(UserRepository userRepositoryImpl, AddressRepository addressRepositoryImpl) {
		userRepository = userRepositoryImpl;
		addressRepository = addressRepositoryImpl;
	}
	
	@Override
	public List<UserAdressDTO> getAddresses(String userId) {
		List<UserAdressDTO> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		//Because of public id, I cannot get the userDatabaseId and request db. I need, first, the object. 
		UserEntity userEntity = userRepository.findByPublicUserId(userId);
		if (userEntity == null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		for (AddressEntity addressEntity : addresses) {
			returnValue.add( modelMapper.map(addressEntity,UserAdressDTO.class));
		}
		

		return returnValue;
	}

	@Override
	public UserAdressDTO getAddressByAddressId(String addressId) {
		UserAdressDTO returnValue = null;
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		
		if (addressEntity != null)
			returnValue = new ModelMapper().map(addressEntity, UserAdressDTO.class);
		
		return returnValue;
	}

}
