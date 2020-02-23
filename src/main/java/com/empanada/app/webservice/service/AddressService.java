package com.empanada.app.webservice.service;

import java.util.List;

import com.empanada.app.webservice.shared.dto.UserAdressDTO;


public interface AddressService {
	
	List<UserAdressDTO> getAddresses(String userId);
	
	UserAdressDTO getAddressById(String addressId);
}
