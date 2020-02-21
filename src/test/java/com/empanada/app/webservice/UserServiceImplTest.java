package com.empanada.app.webservice;

import org.mockito.Mock;

import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.service.UserService;

import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;

public class UserServiceImplTest {
	
	@InjectMocks
	UserService userService;
	
	@Mock
	UserRepository userRepository;
	
	private void getAllUsers() {
	}

}
