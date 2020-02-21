package com.empanada.app.webservice.pagination;

public class Page{

	private Integer number;
	private Integer size;
	
	public static Page buildPage(int number, int size) {
		return new Page(number, size);
	}
	
	private Page(int number, int size) {
		this.number = number;
		this.size = size;
	}
	
	public static Page buildPage() {
		return new Page();
	}
	
	private Page() {
		setDefaultValues();
	}

	private void setDefaultValues() {
		this.number = 0;
		this.size = 5;
		
	}

	public int getNumber() {
		return number;
	}
	
	public int getSize() {
		return size;
	}

}
