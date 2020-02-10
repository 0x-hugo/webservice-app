package com.empanada.app.webservice.ui.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestAdapter extends PageRequest{

	private PageRequestAdapter(int page, int size) {
		super(page, size, Sort.unsorted());
	}

	public static PageRequest of(ResultPagination pagination) {
		return new PageRequestAdapter(pagination.getNumberOfPages(), pagination.getNumberOfPages());
	}
}
