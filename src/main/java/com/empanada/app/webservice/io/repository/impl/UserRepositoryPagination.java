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
import com.empanada.app.webservice.pagination.PaginationIndex;
import com.empanada.app.webservice.ui.utils.PageRequestWrapper;

@Service
public class UserRepositoryPagination {
	
	private static final Logger logger = LogManager.getLogger(UserRepositoryPagination.class);
	private PageRequest pageRequest;

	private UserRepository userRepository;
	
	@Autowired	
	public UserRepositoryPagination(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public UserRepositoryPagination (PaginationIndex indexPagination) {
		pageRequest = PageRequestWrapper.of(indexPagination);
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

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
