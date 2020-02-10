package com.empanada.app.webservice.ui.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Wrapper for decoupling Spring Data external library
 * */
public class PageRequestWrapper extends PageRequest{

	private PageRequestWrapper(int page, int size) {
		super(page, size, Sort.unsorted());
	}

	public static PageRequest of(ResultPagination pagination) {
		return new PageRequestWrapper(pagination.getNumberOfPages(), pagination.getNumberOfPages());
	}
}
