package com.empanada.app.webservice.ui.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.UserDto;
import com.empanada.app.webservice.ui.model.request.UserDetailsRequestModel;
import com.empanada.app.webservice.ui.model.response.UserRest;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
	
	@Autowired
	UserService userService;
	
	
	@GetMapping(path = "/{id}")
	public UserRest getUserInformation (@PathVariable String id) {
		UserRest userResponse = new UserRest();
		
		UserDto userDto = userService.getUserByUserId(id);
		if (userDto == null) throw new UsernameNotFoundException(id);
		
		BeanUtils.copyProperties(userDto, userResponse);
		
		return userResponse;
	}
	
	@PostMapping
	public UserRest createUser (@RequestBody UserDetailsRequestModel userDetails) {
		UserRest userResponse = new UserRest();
		 
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto createdUser = userService.createUser(userDto);
		BeanUtils.copyProperties(createdUser, userResponse);
		
		return userResponse;
	}
	
	@PutMapping
	public String updateUser () {
		return "update";
	}
	
	@DeleteMapping
	public String deleteUser () {
		return "delete";
	}
}
