package com.empanada.app.webservice.ui.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
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

import com.empanada.app.webservice.exceptions.UserNotFoundException;
import com.empanada.app.webservice.exceptions.UserServiceException;
import com.empanada.app.webservice.pagination.Page;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.UserAdressDTO;
import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;
import com.empanada.app.webservice.ui.model.request.UserDetailsRequestModel;
import com.empanada.app.webservice.ui.model.response.AddressRest;
import com.empanada.app.webservice.ui.model.response.OperationStatus;
import com.empanada.app.webservice.ui.model.response.OperationStatusName;
import com.empanada.app.webservice.ui.model.response.OperationStatusResult;
import com.empanada.app.webservice.ui.model.response.UserRest;



@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {
	
	UserService userService;
	AddressService addressService;
	
	@Autowired
	public UserController(UserService userService, AddressService addressService) {
		this.userService = userService;
		this.addressService = addressService;
	}
	
	//TODO: extract "defaultValue" knowledge from controller params to its object
	/**
	 * returns a linked list in hal+json format
	 * */
	@GetMapping (	
			produces = { 
				MediaType.APPLICATION_XML_VALUE, 
				MediaType.APPLICATION_JSON_VALUE, 
				"application/hal+json" })
	public CollectionModel<UserRest> getUsersByPagination(	
											@RequestParam(value = "page", defaultValue = "0") 	int pageNumber,
											@RequestParam(value = "limit", defaultValue = "5") int resultsLimit) {
		List<UserRest> userLinkedList = getLinkedUserListByPagination(pageNumber, resultsLimit);
		return new CollectionModel<>(userLinkedList);
	}

	private List<UserRest> getLinkedUserListByPagination(int pageNumber, int resultsLimit) {
		Page paginationIndex = Page.build(pageNumber, resultsLimit);
		List<UserBasicInformationDTO> basicUsersInformation = userService.getUsersIndexedByPage(paginationIndex);
		return addLinkToEachUsersWithDetails(basicUsersInformation);
	}

	private List<UserRest> addLinkToEachUsersWithDetails(List<UserBasicInformationDTO> basicUsersInformation) {
		List<UserRest> users = new ArrayList<>();
		
		for(final UserBasicInformationDTO basicUserInformation : basicUsersInformation) {
			Link userDetailsLink = linkTo(methodOn(UserController.class)
										.getUserInformation(basicUserInformation.getPublicUserId()))
									.withRel("user");
			
			UserRest userInformation = new ModelMapper().map(basicUserInformation, UserRest.class);
			userInformation.add(userDetailsLink);
			users.add(userInformation);
		}
		
		return users;
	}
	
	@GetMapping (	path = "/{id}",
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public EntityModel<UserRest> getUserInformation (@PathVariable String id) throws UserServiceException {	
		UserBasicInformationDTO userInfo = userService.getUserByPublicUserId(id);
		return new EntityModel<>(addAddressLinkToUser(userInfo));
	}

	private UserRest addAddressLinkToUser(UserBasicInformationDTO userDto) {
		UserRest userInfo = new ModelMapper().map(userDto, UserRest.class);
		linkAddressesToUser(userInfo);
		return userInfo;
	}

	private void linkAddressesToUser(UserRest userInfo) {
		for (AddressRest address : userInfo.getAddresses()) {
			Link addressLink = linkTo(methodOn(UserController.class)
									.getAddressInformation(userInfo.getUserId(), address.getAddressId()))
								.withRel("address");
			address.add(addressLink);
		}
	}
	
	@PostMapping ( 	consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } ) 
	public UserRest createUser (@RequestBody UserDetailsRequestModel userDetails) {
		ModelMapper modelMapper = new ModelMapper();
		UserBasicInformationDTO userDto = modelMapper.map(userDetails, UserBasicInformationDTO.class);
		UserBasicInformationDTO createdUser = userService.createUser(userDto);
		
		return modelMapper.map(createdUser, UserRest.class);
	}
	
	@PutMapping ( 	path = "/{id}",
					consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } ) 
	public UserRest updateUser (@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
		UserBasicInformationDTO userDto = new ModelMapper().map(userDetails, UserBasicInformationDTO.class);
		UserBasicInformationDTO updateUser = userService.updateUser(id, userDto);
		return new ModelMapper().map(updateUser, UserRest.class);
	}
	
	@DeleteMapping(	path = "/{id}",
					produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE } )
	public OperationStatus deleteUser (@PathVariable String id) {
		OperationStatus operationStatus = new OperationStatus();
		operationStatus.setName(OperationStatusName.DELETE.name());
		
		try { 
			userService.deleteUserByPublicUserId(id);
			operationStatus.setResult(OperationStatusResult.SUCCESS.name());
		} catch (UserNotFoundException e){
			operationStatus.setResult(OperationStatusResult.ERROR.name());
		}
		
		return operationStatus;
	}
	
	// http://localhost:8080/spring-ws-app/users/jonn3odkmw/addresses
	@GetMapping (	path = "/{id}/addresses",
					produces = { 
							MediaType.APPLICATION_XML_VALUE, 
							MediaType.APPLICATION_JSON_VALUE, 
							"application/hal+json" })
	public CollectionModel<AddressRest> getUserAddresses (@PathVariable String id) throws UserServiceException {
		List<AddressRest> addresses = new ArrayList<>();
		
		List<UserAdressDTO> addressInfo = addressService.getAddresses(id);
		
		if(addressInfo != null && !addressInfo.isEmpty()) {
			//this is for mapping lists. 
			java.lang.reflect.Type listType = new TypeToken<List<AddressRest>>() {}.getType();
			addresses = new ModelMapper().map(addressInfo, listType);
			
			for (AddressRest address: addresses) {
				Link addressLink = linkTo(methodOn(UserController.class)
										.getAddressInformation(id, address.getAddressId()))
									.withRel("address");
				address.add(addressLink);
				
				Link userLink = linkTo(methodOn(UserController.class)
									.getUserInformation(id))
								.withRel("user");
				address.add(userLink);
			}
		}
		
		return new CollectionModel<>(addresses);
	}
	
	@GetMapping (	path = "/{userId}/addresses/{addressId}",
					produces = { 
							MediaType.APPLICATION_XML_VALUE, 
							MediaType.APPLICATION_JSON_VALUE, 
							"application/hal+json" })
	public EntityModel<AddressRest>getAddressInformation ( @PathVariable String userId, @PathVariable String addressId) {
		UserAdressDTO addressDto = addressService.getAddressById(addressId);
		AddressRest addressResponse = new ModelMapper().map(addressDto, AddressRest.class); 
		addressResponse = addDetailsToAddress(addressResponse, userId, addressId);
		
		return new EntityModel<>(addressResponse);
	}
	
	private AddressRest addDetailsToAddress (final AddressRest address, String userId, String addressId) {
		AddressRest addressResponse = address.clone();
		
		Link linkSelf = linkTo(methodOn(UserController.class).getAddressInformation(userId, addressId)).withSelfRel();
		Link linkUser = linkTo(UserController.class).slash(userId).withRel("user");
		Link linkAddresses = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		addressResponse.add(linkSelf);
		addressResponse.add(linkUser);
		addressResponse.add(linkAddresses);

		return addressResponse;
	}
	
	/*
	 * http://localhost:8080/spring-ws-app/users/email-verification?token=jkld1kl3
	 * */
	@GetMapping (path = "/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatus verifyEmailToken (@RequestParam(value = "token") String token) {
		OperationStatus returnValue = new OperationStatus();
		returnValue.setName(OperationStatusName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		if (isVerified) {
			returnValue.setResult(OperationStatusResult.SUCCESS.name());
		}else {
			returnValue.setResult(OperationStatusResult.ERROR.name());
		}
		
		return returnValue;
	}

	
	
	
	
}
