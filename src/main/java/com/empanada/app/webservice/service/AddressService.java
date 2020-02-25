package com.empanada.app.webservice.service;

import java.util.List;

import com.empanada.app.webservice.shared.dto.UserAddressDTO;

public interface AddressService {

  List<UserAddressDTO> getAddresses(String userId);

  UserAddressDTO getAddressById(String addressId);
}
