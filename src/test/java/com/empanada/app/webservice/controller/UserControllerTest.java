package com.empanada.app.webservice.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.CollectionModel;

import com.empanada.app.webservice.io.UserMock;
import com.empanada.app.webservice.pagination.Page;
import com.empanada.app.webservice.pagination.PageMock;
import com.empanada.app.webservice.service.AddressService;
import com.empanada.app.webservice.service.UserService;
import com.empanada.app.webservice.shared.dto.UserBasicInformationDTO;
import com.empanada.app.webservice.ui.controller.UserController;
import com.empanada.app.webservice.ui.model.response.UserRest;
import com.empanada.app.webservice.ui.utils.MapperBuilder;

public class UserControllerTest {

  UserService userService;
  AddressService addressService;
  MapperBuilder mapperBuilder;

  private Page defaultPage;

  @BeforeEach
  private void setup() {
    userService = mock(UserService.class);
    addressService = mock(AddressService.class);
    MockitoAnnotations.initMocks(this);
    mockPage();
  }

  private void mockPage() {
    defaultPage = PageMock.buildDefaultPage();
  }

  @Test
  public void getAllUsersFromDB() {

  }

  @SuppressWarnings("serial")
  @Test
  public void getUsersByPaginationTest() {
    UserController userController = new UserController(this.userService, this.addressService, this.mapperBuilder);

    when(userService.getUsersIndexedByPage(any())).thenReturn(new ArrayList<UserBasicInformationDTO>() {
      {
        add(UserMock.buildDefaultDTO());
      }
    });

    assertThat(userController.getUsersByPagination(PageMock.getDefaultPagenumber(), PageMock.getDefaultResults()),
        is(new CollectionModel<>(new ArrayList<UserRest>()
            {{
              add(UserMock.buildDefaultUserRest());
            }})));
  }
}
