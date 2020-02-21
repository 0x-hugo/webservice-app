package com.empanada.app.webservice.controller;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.ui.controller.UserController;

public class UserControllerTest {

	UserService userService;
	
	AddressService addressService;
	
	@BeforeEach
	private void setup() {
		userService = mock(UserService.class);
		addressService = mock(AddressService.class);
	}
	
	@Test
	public void getAllUsersFromDB() {

	}
	
	@Test
	public void getUsersByPaginationTest() {
		UserController userController = new UserController(this.userService, this.addressService);
		
	}
}
