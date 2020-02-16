package com.empanada.app.webservice.pagination;

public class PaginationIndex {

	private int numberOfPages;
	private int resultsPerPage;
	
	public static PaginationIndex buildIndex(int numberOfPages, int resultsPerPage) {
		return new PaginationIndex(numberOfPages, resultsPerPage);
	}
	
	private PaginationIndex(int numberOfPages, int resultsPerPage) {
		this.numberOfPages = numberOfPages;
		this.resultsPerPage = resultsPerPage;
	}
	
	public static PaginationIndex buildPagination() {
		return new PaginationIndex();
	}
	
	private PaginationIndex() {
		setDefaultValues();
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
