package com.empanada.app.webservice.io.repository.impl;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.UserRepository;

@Repository
public class UserRepositoryPagination {
	
	private static final Logger logger = LogManager.getLogger(UserRepositoryPagination.class);
	
	private PageRequest pageRequest;
	private UserRepository userRepository;
	
	public UserRepositoryPagination() {}
	
	public UserRepositoryPagination(UserRepository userRepositoryImpl, com.empanada.app.webservice.pagination.Page page) {
		this.userRepository = userRepositoryImpl;
		this.pageRequest = PageRequest.of(page.getNumber(), page.getSize());
	}
	
	public List<UserEntity> getUsers() {
		try {
			return findUsersByPage();
		} catch(NullPointerException noItemsFoundException) {
			logger.info("no users found ");
			return Collections.emptyList();
		}
	}

	private List<UserEntity> findUsersByPage() throws NullPointerException {
		Page<UserEntity> userPage = userRepository.findAll(pageRequest);
		return userPage.getContent();
	}
	
	public UserRepository getUserRepository() {
		return userRepository;
	}

}
