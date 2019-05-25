package com.empanada.app.webservice.impl;

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
import com.empanada.app.webservice.shared.dto.AddressDto;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> returnValue = new ArrayList<AddressDto>();
		ModelMapper modelMapper = new ModelMapper();
		
		//Because of public id, I cannot get the userDatabaseId and request db. I need, first, the object. 
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) return returnValue;
		
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
		for (AddressEntity addressEntity : addresses) {
			returnValue.add( modelMapper.map(addressEntity,AddressDto.class));
		}
		

		return returnValue;
	}

}
