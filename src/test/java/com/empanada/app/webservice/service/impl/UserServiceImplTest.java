package com.empanada.app.webservice.service.impl;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.service.UserService;

class UserServiceImplTest {

  UserRepository userRepository;
  UserService userService;

  @BeforeEach
  private void setup() {
    userService = mock(UserService.class);
    MockitoAnnotations.initMocks(this);
//    mockPage();
  }
  
  @Test
  private void createUser() {
    
  }

}
