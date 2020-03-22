package com.empanada.app.webservice.ui.controller;

import java.util.ArrayList;
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
import com.empanada.app.webservice.shared.dto.UserAddressDTO;
import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;
import com.empanada.app.webservice.ui.model.request.UserDetailsRequestModel;
import com.empanada.app.webservice.ui.model.response.AddressRest;
import com.empanada.app.webservice.ui.model.response.OperationStatus;
import com.empanada.app.webservice.ui.model.response.OperationStatusName;
import com.empanada.app.webservice.ui.model.response.OperationStatusResult;
import com.empanada.app.webservice.ui.model.response.UserRest;
import com.empanada.app.webservice.ui.utils.LinkProvider;
import com.empanada.app.webservice.ui.utils.MapperBuilder;

@RestController
@RequestMapping(value = "/users", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
public class UserController {

  UserService userService;
  AddressService addressService;
  MapperBuilder mapperBuilder;

  ModelMapper mapper;

  @Autowired
  public UserController(UserService userService, AddressService addressService, MapperBuilder mapperBuilder) {
    this.userService = userService;
    this.addressService = addressService;
    this.mapperBuilder = mapperBuilder;
    this.mapper = mapperBuilder.getMapper();
  }

  @GetMapping(produces = "application/hal+json")
  public CollectionModel<UserRest> getUsersByPagination(
      @RequestParam(value = "page", defaultValue = "0") int pageNumber,
      @RequestParam(value = "limit", defaultValue = "5") int resultsLimit) {
    final List<UserRest> usersDetails = getUsersDetailsByPagination(pageNumber, resultsLimit);
    return new CollectionModel<>(usersDetails);
  }

  private List<UserRest> getUsersDetailsByPagination(int pageNumber, int resultsLimit) {
    final Page paginationIndex = Page.build(pageNumber, resultsLimit);
    final List<UserBasicInformationDTO> usersBasicInformation = userService.getUsersIndexedByPage(paginationIndex);
    return buildLinkWithDetails(usersBasicInformation);
  }

  private List<UserRest> buildLinkWithDetails(List<UserBasicInformationDTO> usersBasicInformation) {
    final List<UserRest> users = new ArrayList<>();
    usersBasicInformation.forEach(userBasicInfo -> {
      final UserRest user = mapper.map(userBasicInfo, UserRest.class);
      addLinkToUser(user);
      users.add(user);
    });

    return users;
  }

  private void addLinkToUser(final UserRest user) {
    final Link userDetails = LinkProvider.userInformation(user.getUserId(), "user");
    user.add(userDetails);
  }

  @GetMapping(path = "/{id}", produces = "application/hal+json")
  public EntityModel<UserRest> getUserInformation(@PathVariable String id) throws UserServiceException {
    final UserRest userInfo = getUserDetails(id);
    return new EntityModel<>(userInfo);
  }

  private UserRest getUserDetails(String id) {
    final UserBasicInformationDTO userInfo = userService.getUserByPublicUserId(id);
    return buildLinkWithDetails(userInfo);
  }

  private UserRest buildLinkWithDetails(UserBasicInformationDTO userDto) {
    final UserRest userInfo = mapper.map(userDto, UserRest.class);
    linkAddressesToUser(userInfo);
    return userInfo;
  }

  private void linkAddressesToUser(UserRest userInfo) {
    userInfo.getAddresses().forEach(address -> {
      final Link addressLink = LinkProvider.addressInformation(userInfo.getUserId(), address.getAddressId(), "address");
      address.add(addressLink);
    });
  }

  @PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
  public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
    final UserBasicInformationDTO userDto = mapper.map(userDetails, UserBasicInformationDTO.class);
    final UserBasicInformationDTO createdUser = userService.createUser(userDto);
    return mapper.map(createdUser, UserRest.class);
  }

  @PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
  public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {
    final UserBasicInformationDTO userDto = mapper.map(userDetails, UserBasicInformationDTO.class);
    final UserBasicInformationDTO updateUser = userService.updateUser(id, userDto);
    return mapper.map(updateUser, UserRest.class);
  }

  @DeleteMapping(path = "/{id}")
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

  @GetMapping(path = "/{id}/addresses", produces = "application/hal+json")
  public CollectionModel<AddressRest> getUserAddresses(@PathVariable String id) throws UserServiceException {
    final List<UserAddressDTO> addressesInfo = addressService.getAddresses(id);
    List<AddressRest> addresses = mapAddresses(addressesInfo);
    addresses = addLinksToAddresses(id, addresses);
    return new CollectionModel<>(addresses);
  }

  private List<AddressRest> mapAddresses(List<UserAddressDTO> addressesInfo) {
    // this is for mapping lists.
    final java.lang.reflect.Type addresses = new TypeToken<List<AddressRest>>() {
    }.getType();
    return mapper.map(addressesInfo, addresses);
  }

  private List<AddressRest> addLinksToAddresses(String id, List<AddressRest> addresses) {
    final List<AddressRest> addressesCopy = new ArrayList<>(addresses);
    for (final AddressRest address : addressesCopy) {
      final Link addressLink = LinkProvider.addressInformation(id, address.getAddressId(), "address");
      address.add(addressLink);

      final Link userLink = LinkProvider.userInformation(id, "user");
      address.add(userLink);
    }

    return addressesCopy;
  }

  @GetMapping(path = "/{userId}/addresses/{addressId}", produces = "application/hal+json")
  public EntityModel<AddressRest> getAddressInformation(@PathVariable String userId, @PathVariable String addressId) {
    final UserAddressDTO addressDto = addressService.getAddressById(addressId);
    AddressRest addressResponse = mapper.map(addressDto, AddressRest.class);
    addressResponse = addDetailsToAddress(addressResponse, userId, addressId);
    return new EntityModel<>(addressResponse);
  }

  private AddressRest addDetailsToAddress(final AddressRest address, String userId, String addressId) {
    final AddressRest addressResponse = clone(address);

    final Link linkSelf = LinkProvider.selfAddress(addressId, userId);
    final Link linkUser = LinkProvider.selfUser(userId);
    final Link linkAddresses = LinkProvider.userAddresses(userId, "addresses");

    addressResponse.add(linkSelf);
    addressResponse.add(linkUser);
    addressResponse.add(linkAddresses);

    return addressResponse;
  }

  private AddressRest clone(AddressRest addressToCopy) {
    return mapper.map(addressToCopy, AddressRest.class);
  }

  /*
   * http://localhost:8080/spring-ws-app/users/email-verification?token=jkld1kl3
   */
  @GetMapping(path = "/email-verification")
  public OperationStatus verifyEmailToken(@RequestParam(value = "token") String token) {
    final OperationStatus operationStatus = new OperationStatus();
    operationStatus.setName(OperationStatusName.VERIFY_EMAIL.name());

    try {
      userService.verifyEmailToken(token);
      operationStatus.setResult(OperationStatusResult.SUCCESS.name());
    } catch (final UserServiceException e) {
      operationStatus.setResult(OperationStatusResult.ERROR.name());
    }

    return operationStatus;
  }

}
