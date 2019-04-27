package com.empanada.app.webservice.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.empanada.app.webservice.io.entity.UserEntity;

/**
 * If not used CrudRepository, DAO must be created and write down business logic for
 * each CRUD operation (such as SQL operations or Hibernate)
 * 
 * */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>{
	
	/**
	 * It already has create, read, update, delete methods but you can add some more. Beautiful
	 **/
	
	UserEntity findByEmail (String email); //custom method to implement
}
