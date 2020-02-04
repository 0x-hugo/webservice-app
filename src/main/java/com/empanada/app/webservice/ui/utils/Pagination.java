package com.empanada.app.webservice.ui.utils;

public class Pagination {

	private int numberOfPages;
	private int currentPage;
	
	private int defaultPage;
	
	private Pagination(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}
	
	public static Pagination buildPagination() {
		
	}
	
	
	public int getCurrentPage() {
		return currentPage;
	}
	
//	public int getFirstPage
	
	public void initilizePage() {
		decrementCurrentPageByOne();
	}

	private void decrementCurrentPageByOne() {
		if (currentPage > 0) currentPage -= 1;
	}
	
	
}
