package com.empanada.app.webservice.io.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.empanada.app.webservice.io.entity.AddressEntity;
import com.empanada.app.webservice.io.entity.UserEntity;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long>{

	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	
	AddressEntity findByAddressId(String addressId);
}
