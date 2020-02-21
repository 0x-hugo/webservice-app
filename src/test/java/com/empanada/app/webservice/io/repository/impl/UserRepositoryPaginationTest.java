package com.empanada.app.webservice.io.repository.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.empanada.app.webservice.io.repository.UserRepository;
import com.empanada.app.webservice.pagination.Page;
import com.empanada.app.webservice.ui.utils.PageRequestWrapper;


class UserRepositoryPaginationTest {
	
	@Mock
	private UserRepository userRepository;
	
	private Page page;
	
	private UserRepositoryPagination userRepositoryPagination;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		page = Page.buildPage(0, 5);
		userRepositoryPagination = new UserRepositoryPagination(userRepository, page);	
	}
	
	@Test
	void nullObjectInsteadOfNull() {
		PageRequest pageRequest = PageRequestWrapper.of(page);
		when(userRepository.findAll(pageRequest)).thenReturn(null);
		assertThat(userRepositoryPagination.getUsers(), is(Collections.emptyList()));
	}
	
	@Test 
	private void findUsersWithNullUserRepository(){
//		when(userRepository.findAll( pageRequest )).thenReturn(null);
//		assertThrows(NullPointerException.class, () -> userRepository.findAll());
	}

}
