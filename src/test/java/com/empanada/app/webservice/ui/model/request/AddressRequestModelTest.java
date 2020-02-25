package com.empanada.app.webservice.ui.model.request;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AddressRequestModelTest {

  @Test
  void getCity() {
    AddressRequestModel addressRequestModel = new AddressRequestModel();
    addressRequestModel.setCity("a");
    assertEquals("a", addressRequestModel.getCity());
  }

}
