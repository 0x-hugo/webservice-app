package com.empanada.app.webservice.ui.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.empanada.app.webservice.exceptions.UserNotFoundException;
import com.empanada.app.webservice.exceptions.UserServiceException;
import com.empanada.app.webservice.pagination.Page;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.UserAddressDTO;
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

  @GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
  public CollectionModel<UserRest> getUsersByPagination(
      //TODO: extract this params default values to its own domain
      @RequestParam(value = "page", defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", defaultValue = "5") int resultsLimit) {
    final List<UserRest> userLinkedList = getLinkedUserListByPagination(pageNumber, resultsLimit);
    return new CollectionModel<>(userLinkedList);
  }

  private List<UserRest> getLinkedUserListByPagination(int pageNumber, int resultsLimit) {
    final Page paginationIndex = Page.build(pageNumber, resultsLimit);
    final List<UserBasicInformationDTO> usersBasicInformation = userService.getUsersIndexedByPage(paginationIndex);
    return addLinkDetails(usersBasicInformation);
  }

  private List<UserRest> addLinkDetails(List<UserBasicInformationDTO> usersBasicInformation) {
    final List<UserRest> users = new ArrayList<>();
    usersBasicInformation.forEach(userBasicInfo -> {
      UserRest userRest = new ModelMapper().map(userBasicInfo, UserRest.class);
      userRest = addLinkTo(userRest);
      users.add(userRest);
    });
    
    return users;
  }

  private UserRest addLinkTo(final UserRest userBasicInformation) {
    UserRest userCopy = clone(userBasicInformation);
    final Link userDetailsLink = buildUserLinkToDetails(userCopy);
    userCopy.add(userDetailsLink);
    
    return userCopy;
  }
  
  private Link buildUserLinkToDetails(UserRest user) {
    return linkTo(methodOn(UserController.class).getUserInformation(user.getUserId())).withRel("user");
  }

  private UserRest clone(UserRest userToCopy) {
    final ModelMapper mapper = new ModelMapper();
    final UserRest copiedUser = new UserRest();
    mapper.map(userToCopy, UserRest.class);

    return copiedUser;
  }

  @GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,
      "application/hal+json" })
  public EntityModel<UserRest> getUserInformation(@PathVariable String id) throws UserServiceException {
    final UserBasicInformationDTO userInfo = userService.getUserByPublicUserId(id);
    return new EntityModel<>(addAddressLinkToUser(userInfo));
  }

  private UserRest addAddressLinkToUser(UserBasicInformationDTO userDto) {
    final UserRest userInfo = new ModelMapper().map(userDto, UserRest.class);
    linkAddressesToUser(userInfo);
    return userInfo;
  }

  private void linkAddressesToUser(UserRest userInfo) {
    for (final AddressRest address : userInfo.getAddresses()) {
      final Link addressLink = linkTo(
          methodOn(UserController.class).getAddressInformation(userInfo.getUserId(), address.getAddressId()))
              .withRel("address");
      address.add(addressLink);
    }
  }

  @PostMapping( consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
                produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
  public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
    final ModelMapper modelMapper = new ModelMapper();
    final UserBasicInformationDTO userDto = modelMapper.map(userDetails, UserBasicInformationDTO.class);
    final UserBasicInformationDTO createdUser = userService.createUser(userDto);

    return modelMapper.map(createdUser, UserRest.class);
  }

  @PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
          MediaType.APPLICATION_JSON_VALUE })
  public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
    final UserBasicInformationDTO userDto = new ModelMapper().map(userDetails, UserBasicInformationDTO.class);
    final UserBasicInformationDTO updateUser = userService.updateUser(id, userDto);
    return new ModelMapper().map(updateUser, UserRest.class);
  }

  @DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
  public OperationStatus deleteUser(@PathVariable String id) {
    final OperationStatus operationStatus = new OperationStatus();
    operationStatus.setName(OperationStatusName.DELETE.name());

    try {
      userService.deleteUserByPublicUserId(id);
      operationStatus.setResult(OperationStatusResult.SUCCESS.name());
    } catch (final UserNotFoundException e) {
      operationStatus.setResult(OperationStatusResult.ERROR.name());
    }

    return operationStatus;
  }

  @GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,
      "application/hal+json" })
  public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) throws UserServiceException {
    final List<UserAddressDTO> addressesInfo = addressService.getAddresses(id);
    List<AddressRest> addresses = mapNewAddresses(addressesInfo);
    addresses = addLinksToAddresses(id, addresses);

    return new CollectionModel<>(addresses);
  }

  private List<AddressRest> mapNewAddresses(List<UserAddressDTO> addressesInfo) {
    // this is for mapping lists.
    final java.lang.reflect.Type addresses = new TypeToken<List<AddressRest>>() {
    }.getType();
    return new ModelMapper().map(addressesInfo, addresses);
  }

  private List<AddressRest> addLinksToAddresses(String id, List<AddressRest> addresses) {
    final List<AddressRest> addressesCopy = new ArrayList<>(addresses);
    for (final AddressRest address : addressesCopy) {
      final Link addressLink = linkTo(methodOn(UserController.class).getAddressInformation(id, address.getAddressId()))
          .withRel("address");
      address.add(addressLink);

      final Link userLink = linkTo(methodOn(UserController.class).getUserInformation(id)).withRel("user");
      address.add(userLink);
    }

    return addressesCopy;
  }

  @GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
      MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
  public EntityModel<AddressRest> getAddressInformation(@PathVariable String userId, @PathVariable String addressId) {
    final UserAddressDTO addressDto = addressService.getAddressById(addressId);
    AddressRest addressResponse = new ModelMapper().map(addressDto, AddressRest.class);
    addressResponse = addDetailsToAddress(addressResponse, userId, addressId);

    return new EntityModel<>(addressResponse);
  }

  private AddressRest addDetailsToAddress(final AddressRest address, String userId, String addressId) {
    final AddressRest addressResponse = clone(address);

    final Link linkSelf = linkTo(methodOn(UserController.class).getAddressInformation(userId, addressId)).withSelfRel();
    final Link linkUser = linkTo(UserController.class).slash(userId).withRel("user");
    final Link linkAddresses = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");

    addressResponse.add(linkSelf);
    addressResponse.add(linkUser);
    addressResponse.add(linkAddresses);

    return addressResponse;
  }

  private AddressRest clone(AddressRest addressToCopy) {
    final ModelMapper mapper = new ModelMapper();
    final AddressRest copiedAddress = new AddressRest();
    mapper.map(addressToCopy, copiedAddress);

    return copiedAddress;
  }

  /*
   * http://localhost:8080/spring-ws-app/users/email-verification?token=jkld1kl3
   */
  @GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE,
      MediaType.APPLICATION_XML_VALUE })
  public OperationStatus verifyEmailToken(@RequestParam(value = "token") String token) {
    final OperationStatus operationStatus = new OperationStatus();
    operationStatus.setName(OperationStatusName.VERIFY_EMAIL.name());

    try {
      userService.verifyEmailToken(token);
      operationStatus.setResult(OperationStatusResult.SUCCESS.name());
    } catch (UserServiceException e) {
      operationStatus.setResult(OperationStatusResult.ERROR.name());
    }

    return operationStatus;
  }

}
