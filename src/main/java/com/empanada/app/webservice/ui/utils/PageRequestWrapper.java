package com.empanada.app.webservice.ui.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.empanada.app.webservice.pagination.PaginationIndex;

/**
 * Wrapper for decoupling Spring Data external library
 * */
public class PageRequestWrapper extends PageRequest{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PageRequestWrapper(int page, int size) {
		super(page, size, Sort.unsorted());
	}

	public static PageRequest of(PaginationIndex pagination) {
		return new PageRequestWrapper(pagination.getNumberOfPages(), pagination.getResultsPerPage());
	}
}
