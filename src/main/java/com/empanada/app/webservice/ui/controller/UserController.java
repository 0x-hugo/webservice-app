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

  @GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
  public CollectionModel<UserRest> getUsersByPagination(
      @RequestParam(value = "page", defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", defaultValue = "5") int resultsLimit) {
    final List<UserRest> userLinkedList = getLinkedUserListByPagination(pageNumber, resultsLimit);
    return new CollectionModel<>(userLinkedList);
  }

  private List<UserRest> getLinkedUserListByPagination(int pageNumber, int resultsLimit) {
    final Page paginationIndex = Page.build(pageNumber, resultsLimit);
    final List<UserBasicInformationDTO> basicUsersInformation = userService.getUsersIndexedByPage(paginationIndex);
    return addLinkToEachUsersWithDetails(basicUsersInformation);
  }

  private List<UserRest> addLinkToEachUsersWithDetails(List<UserBasicInformationDTO> basicUsersInformation) {
    final List<UserRest> users = new ArrayList<>();

    for (final UserBasicInformationDTO basicUserInformation : basicUsersInformation) {
      final Link userDetailsLink = linkTo(
          methodOn(UserController.class).getUserInformation(basicUserInformation.getPublicUserId())).withRel("user");

      final UserRest userInformation = new ModelMapper().map(basicUserInformation, UserRest.class);
      userInformation.add(userDetailsLink);
      users.add(userInformation);
    }

    return users;
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

  @PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
      MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
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

  // http://localhost:8080/spring-ws-app/users/jonn3odkmw/addresses
  @GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE,
      "application/hal+json" })
  public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) throws UserServiceException {
    final List<UserAdressDTO> addressesInfo = addressService.getAddresses(id);
    List<AddressRest> addresses = mapNewAddresses(addressesInfo);
    addresses = addLinksToAddresses(id, addresses);

    return new CollectionModel<>(addresses);
  }

  private List<AddressRest> mapNewAddresses(List<UserAdressDTO> addressesInfo) {
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
    final UserAdressDTO addressDto = addressService.getAddressById(addressId);
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
    final OperationStatus returnValue = new OperationStatus();
    returnValue.setName(OperationStatusName.VERIFY_EMAIL.name());

    final boolean isVerified = userService.verifyEmailToken(token);
    if (isVerified) {
      returnValue.setResult(OperationStatusResult.SUCCESS.name());
    } else {
      returnValue.setResult(OperationStatusResult.ERROR.name());
    }

    return returnValue;
  }

}
