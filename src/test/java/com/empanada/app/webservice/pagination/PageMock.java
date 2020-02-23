package com.empanada.app.webservice.pagination;

/*
 * I know the name is ugly but I couldn't find a more descriptive name
 **/
public class PageMock {

	private static final int DEFAULT_RESULTS = 5;
	private static final int DEFAULT_PAGENUMBER = 0;

	public static Page buildDefaultPage() {
		return Page.build(DEFAULT_PAGENUMBER, DEFAULT_RESULTS);
	}

	public static int getDefaultResults() {
		return DEFAULT_RESULTS;
	}

	public static int getDefaultPagenumber() {
		return DEFAULT_PAGENUMBER;
	}
	
}
