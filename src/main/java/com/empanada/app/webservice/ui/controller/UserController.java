package com.empanada.app.webservice.ui.controller;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
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
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.UserAdressDTO;
import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;
import com.empanada.app.webservice.ui.model.request.UserDetailsRequestModel;
import com.empanada.app.webservice.ui.model.response.AddressRest;
import com.empanada.app.webservice.ui.model.response.OperationStatusModel;
import com.empanada.app.webservice.ui.model.response.OperationStatusName;
import com.empanada.app.webservice.ui.model.response.OperationStatusResult;
import com.empanada.app.webservice.ui.model.response.UserRest;
import com.empanada.app.webservice.ui.utils.ResultPagination;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;
	
	@GetMapping (	path = "/{id}",
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resource<UserRest> getUserInformation (@PathVariable String id) throws UserServiceException {
		UserRest userResponse = new UserRest();
		ModelMapper modelMapper = new ModelMapper();
		
		UserBasicInformationDTO userDto = userService.getUserByUserId(id);
		//Link userLink = linkTo(methodOn(UserController.class).).withSelfRel();
//		BeanUtils.copyProperties(userDto, userResponse); it returns stackoverflow otherwise
		
		userResponse = modelMapper.map(userDto, UserRest.class);
		
		for (AddressRest address : userResponse.getAddresses()) {
			Link addressLink = linkTo(methodOn(UserController.class).getAddressInformation(id, address.getAddressId())).withRel("address");
			address.add(addressLink);
		}
		
		return new Resource<>(userResponse);
	}
	
	//TODO: extract "defaultValue" knowledge from controller to its object
	@GetMapping (	produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resources<UserRest> getUsers(	@RequestParam(value = "page", defaultValue = "0") 	int page,
											@RequestParam(value = "limit", defaultValue = "5") int limit){
		ResultPagination pagination = ResultPagination.buildPagination(page, limit);
		
		List<UserRest> returnValue = new ArrayList<UserRest>();
		List<UserBasicInformationDTO> userList = userService.getUsers(pagination);
		
		if(userList != null && !userList.isEmpty()) {
			for(final UserBasicInformationDTO user : userList) {
				UserRest userModel = new UserRest();
				
				Link userLink = linkTo(methodOn(UserController.class).getUserInformation(user.getUserId())).withRel("user");
				userModel = new ModelMapper().map(user, UserRest.class);
				userModel.add(userLink);

				returnValue.add(userModel);
			}
		}
			
		return new Resources<>(returnValue);
	}
	
	@PostMapping ( 	consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } ) 
	public UserRest createUser (@RequestBody UserDetailsRequestModel userDetails) {
		//It needs to return an object with addresses
		UserRest userResponse = new UserRest();
		 
		//UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		ModelMapper modelMapper = new ModelMapper();
		UserBasicInformationDTO userDto = modelMapper.map(userDetails, UserBasicInformationDTO.class);
		
		UserBasicInformationDTO createdUser = userService.createUser(userDto);
		//BeanUtils.copyProperties(createdUser, userResponse);
		userResponse = modelMapper.map(createdUser, UserRest.class);
		
		return userResponse;
	}
	
	@PutMapping ( 	path = "/{id}",
					consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } ) 
	public UserRest updateUser (@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserRest userResponse = new UserRest();
		
		UserBasicInformationDTO userDto = new UserBasicInformationDTO();
		userDto = new ModelMapper().map(userDetails, UserBasicInformationDTO.class);
		
		
		UserBasicInformationDTO updateUser = userService.updateUser(id, userDto);
		userResponse = new ModelMapper().map(updateUser, UserRest.class);
//		BeanUtils.copyProperties(updateUser, userResponse);
		
		return userResponse;
	}
	
	@DeleteMapping(	path = "/{id}",
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
	public OperationStatusModel deleteUser (@PathVariable String id) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(OperationStatusName.DELETE.name());
		
		userService.deleteUser(id);
		
		//TODO: to review
		returnValue.setOperationResult(OperationStatusResult.SUCCESS.name());
		
		return returnValue;
	}
	
	
	//if more functionalities added, I will create it's own controller
	// http://localhost:8080/spring-ws-app/users/jonn3odkmw/addresses
	@GetMapping (	path = "/{id}/addresses",
				produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
		public Resources<AddressRest> getUserAddresses (@PathVariable String id) throws UserServiceException {
		
		List<AddressRest> addressesResponse = new ArrayList<>();
		List<UserAdressDTO> addressDto = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		
		addressDto = addressService.getAddresses(id);
		
		if(addressDto != null && !addressDto.isEmpty()) {
			//this is for mapping lists. 
			java.lang.reflect.Type listType = new TypeToken<List<AddressRest>>() {}.getType();
			addressesResponse = modelMapper.map(addressDto, listType);
			
			for (AddressRest address: addressesResponse) {
				Link addressLink = linkTo(methodOn(UserController.class).getAddressInformation(id, address.getAddressId())).withRel("address");
				address.add(addressLink);
				
				Link userLink = linkTo(methodOn(UserController.class).getUserInformation(id)).withRel("user");
				address.add(userLink);
			}
		}
		
		
		
		//BeanUtils.copyProperties(AddressDto, addressResponse);
		
		return new Resources<>(addressesResponse);
	}
	
	@GetMapping (	path = "/{userId}/addresses/{addressId}",
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
		public Resource<AddressRest>getAddressInformation (	@PathVariable String userId,
													@PathVariable String addressId) {
		AddressRest addressResponse = new AddressRest();
		//link al mismo controller
		Link linkSelf = linkTo(methodOn(UserController.class).getAddressInformation(userId, addressId)).withSelfRel();
		Link linkUser = linkTo(UserController.class).slash(userId).withRel("user");
		Link linkAddresses = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		UserAdressDTO addressDto = addressService.getAddressByAddressId(addressId);
		addressResponse = new ModelMapper().map(addressDto, AddressRest.class); 
		
		addressResponse.add(linkSelf);
		addressResponse.add(linkAddresses);
		addressResponse.add(linkUser);
		
		return new Resource<>(addressResponse);
	}
	
	/*
	 * http://localhost:8080/spring-ws-app/users/email-verification?token=jkld1kl3
	 * */
	@GetMapping (path = "/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel verifyEmailToken (@RequestParam(value = "token") String token) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(OperationStatusName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		if (isVerified) {
			returnValue.setOperationResult(OperationStatusResult.SUCCESS.name());
		}else {
			returnValue.setOperationResult(OperationStatusResult.ERROR.name());
		}
		
		return returnValue;
	}

	
	
	
	
}
