package com.empanada.app.webservice.controller;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.empanada.app.webservice.pagination.Page;

public class TestResultPaginationTest {

	@BeforeEach
	public void setup() {
		System.out.println("Setting up " + this.getClass().getName() + "environment");
	}
	
	@AfterEach
	public void teardown() {
		System.out.println("tearing down " + this.getClass().getName() + "environment");
	}
	
	@Test
	private void initializePagesBiggerThan0Test() {
		Page resultPagination = Page.build(5, 1);
		assertEquals(5, resultPagination.getNumber());
		fail();
	}
	
	@Test
	private void initializePagesWith0() {
		Page resultPagination = Page.build(0, 1);
		assertEquals(0, resultPagination.getNumber());
	}

}
