package com.empanada.app.webservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empanada.app.webservice.io.entity.AddressEntity;
import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.AddressRepository;
import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.shared.dto.UserAddressDTO;

@Service
public class AddressServiceImpl implements AddressService {

  UserRepository userRepository;
  AddressRepository addressRepository;

  private static final Logger logger = LogManager.getLogger(AddressServiceImpl.class);

  @Autowired
  public AddressServiceImpl(UserRepository userRepositoryImpl, AddressRepository addressRepositoryImpl) {
    userRepository = userRepositoryImpl;
    addressRepository = addressRepositoryImpl;
  }

  @Override
  public List<UserAddressDTO> getAddresses(String userId) {
    final List<UserAddressDTO> userAddresses = new ArrayList<>();
    final ModelMapper mapper = new ModelMapper();

    final UserEntity user = userRepository.findByPublicUserId(userId);
    if (user == null) {
      logger.debug("No addresses found with id [{}]", userId);
      return Collections.emptyList();
    }

    final Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(user);
    for (final AddressEntity addressEntity : addresses) {
      userAddresses.add(mapper.map(addressEntity, UserAddressDTO.class));
    }

    return userAddresses;
  }

  @Override
  public UserAddressDTO getAddressById(String addressId) {
    UserAddressDTO returnValue = null;
    final AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

    if (addressEntity != null)
      returnValue = new ModelMapper().map(addressEntity, UserAddressDTO.class);

    return returnValue;
  }

}
