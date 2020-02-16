package com.empanada.app.webservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.empanada.app.webservice.pagination.PaginationIndex;

public class TestResultPagination {

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
		PaginationIndex resultPagination = PaginationIndex.buildIndex(5, 1);
		assertEquals(5, resultPagination.getNumberOfPages());
	}
	
	@Test
	private void initializePagesWith0() {
		PaginationIndex resultPagination = PaginationIndex.buildIndex(0, 1);
		assertEquals(0, resultPagination.getNumberOfPages());
	}

}
