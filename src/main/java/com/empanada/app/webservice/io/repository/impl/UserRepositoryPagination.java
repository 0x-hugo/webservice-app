package com.empanada.app.webservice.io.repository.impl;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.empanada.app.webservice.io.entity.UserEntity;
import com.empanada.app.webservice.io.repository.UserRepository;

@Service
public class UserRepositoryPagination {
	
	private static final Logger logger = LogManager.getLogger(UserRepositoryPagination.class);
	private PageRequest pageRequest;

	UserRepository userRepository;
	
	public UserRepositoryPagination(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public List<UserEntity> getUsers(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
		try {
			return findUsersByPage();
		} catch(NullPointerException noItemsFoundException) {
			logger.info("no users found ");
			return Collections.emptyList();
		}
	}

	public List<UserEntity> findUsersByPage() throws NullPointerException {
		Page<UserEntity> userPage = userRepository.findAll(pageRequest);
		return userPage.getContent();
	}
	
	public UserRepository getUserRepository() {
		return userRepository;
	}

}
