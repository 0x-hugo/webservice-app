package com.empanada.app.webservice.ui.controller;


import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empanada.app.webservice.exceptions.UserServiceException;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.AddressDto;
import com.empanada.app.webservice.shared.dto.UserDto;
import com.empanada.app.webservice.ui.model.request.UserDetailsRequestModel;
import com.empanada.app.webservice.ui.model.response.AddressRest;
import com.empanada.app.webservice.ui.model.response.OperationStatusModel;
import com.empanada.app.webservice.ui.model.response.OperationStatusName;
import com.empanada.app.webservice.ui.model.response.UserRest;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
	
	@Autowired
	UserService userService;
	
	
	@GetMapping (	path = "/{id}",
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUserInformation (@PathVariable String id) throws UserServiceException {
		UserRest userResponse = new UserRest();
		ModelMapper modelMapper = new ModelMapper();
		
		UserDto userDto = userService.getUserByUserId(id);
//		BeanUtils.copyProperties(userDto, userResponse); it returns stackoverflow otherwise
		userResponse = modelMapper.map(userDto, UserRest.class);
		
		return userResponse;
	}
	
	@GetMapping (	produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(	@RequestParam(value = "page", defaultValue = "0") 	int page,
									@RequestParam(value = "limit", defaultValue = "5") int limit){
		//for confusion (it took me long time to figure out page 0 was the problem) set page 1 as page 0 
		if (page > 0) page -= 1;
		
		List<UserRest> returnValue = new ArrayList<UserRest>();
		List<UserDto> userList = userService.getUsers(page, limit);
		
		for(final UserDto user : userList) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(user, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
	}
	
	@PostMapping ( 	consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } ) 
	public UserRest createUser (@RequestBody UserDetailsRequestModel userDetails) {
		//It needs to return an object with addresses
		UserRest userResponse = new UserRest();
		 
		//UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
		//BeanUtils.copyProperties(createdUser, userResponse);
		userResponse = modelMapper.map(createdUser, UserRest.class);
		
		return userResponse;
	}
	
	@PutMapping ( 	path = "/{id}",
					consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } ) 
	public UserRest updateUser (@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest userResponse = new UserRest();
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updateUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updateUser, userResponse);
		
		return userResponse;
	}
	
	@DeleteMapping(	path = "/{id}",
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
	public OperationStatusModel deleteUser (@PathVariable String id) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(OperationStatusName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(OperationStatusName.SUCCESS.name());
		
		return returnValue;
	}
	
	// http://localhost:8080/spring-ws-app/users/jonn3odkmw/addresses
	@GetMapping (	path = "/{id}/addresses",
				produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
		public List<AddressRest> getUserAddresses (@PathVariable String id) throws UserServiceException {
		
		List<AddressRest> returnValue = new ArrayList<>();
		List<AddressDto> AddressDto = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		AddressDto = userService.getUserByUserId(id).getAddresses();

		//this is for mapping lists. 
		java.lang.reflect.Type listType = new TypeToken<List<AddressRest>>() {}.getType();
		
		returnValue = modelMapper.map(AddressDto, listType);
		
		//BeanUtils.copyProperties(AddressDto, addressResponse);
		
		return returnValue;
}
	
}
