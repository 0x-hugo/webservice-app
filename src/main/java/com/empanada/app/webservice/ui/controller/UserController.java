package com.empanada.app.webservice.ui.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
	
	@GetMapping
	public String getUserInformation () {
		return "something";
	}
	
	
	@PostMapping
	public String createUser () {
		return "create userhas been used";
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
