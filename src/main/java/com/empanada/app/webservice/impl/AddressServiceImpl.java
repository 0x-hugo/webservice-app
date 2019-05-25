package com.empanada.app.webservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.shared.dto.AddressDto;

public class AddressServiceImpl implements AddressService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public List<AddressDto> getAddresses(String userId) {
		List<AddressDto> returnValue = new ArrayList<AddressDto>();
		ModelMapper modelMapper = new ModelMapper();
		
		UserEntity userEntity = userRepository.findByUserId(userId);
		if (userEntity == null) return returnValue;
		
		//this is for mapping lists. 
		java.lang.reflect.Type listType = new TypeToken<List<AddressDto>>() {}.getType();
		returnValue = modelMapper.map(userEntity, listType);
		
		return returnValue;
	}

}
