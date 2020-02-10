package com.empanada.app.webservice.ui.utils;

public class ResultPagination {

	private int numberOfPages;
	private int resultsPerPage;
	
	public static ResultPagination buildPagination(int numberOfPages, int resultsPerPage) {
		return new ResultPagination(numberOfPages, resultsPerPage);
	}
	
	private ResultPagination(int numberOfPages, int resultsPerPage) {
		this.numberOfPages = numberOfPages;
		this.resultsPerPage = resultsPerPage;
		initializePages();
	}
	
	public static ResultPagination buildPagination() {
		return new ResultPagination();
	}
	
	private ResultPagination() {
		setDefaultValues();
		initializePages();
	}
	
	//Index 0 
	private void initializePages() {
		if (numberOfPages > 0) numberOfPages --;
	}

	private void setDefaultValues() {
		this.numberOfPages = 0;
		this.resultsPerPage = 5;
		
	}

	public int getNumberOfPages() {
		return numberOfPages;
	}
	
	public int getResultsPerPage() {
		return resultsPerPage;
	}

}
