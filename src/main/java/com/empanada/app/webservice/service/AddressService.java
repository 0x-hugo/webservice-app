package com.empanada.app.webservice.service;

import java.util.List;

import com.empanada.app.webservice.shared.dto.AddressDto;


public interface AddressService {
	
	List<AddressDto> getAddresses(String userId);
	
	AddressDto getAddressByAddressId(String addressId);
}
