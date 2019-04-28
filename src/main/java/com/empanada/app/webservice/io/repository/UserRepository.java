package com.empanada.app.webservice.io.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.empanada.app.webservice.io.entity.UserEntity;

/**
 * If not used CrudRepository, DAO must be created and write down business logic for
 * each CRUD operation (such as SQL operations or Hibernate)
 * 
 * */
@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long>{
	
	/**
	 * It already has create, read, update, delete methods but you can add some more. Beautiful
	 **/
	
	UserEntity findByEmail (String email); //Spring creates the query by using "findBy" and the column

	UserEntity findByUserId(String userId);
	
}
