package com.empanada.app.webservice.ui.utils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.Link;

import com.empanada.app.webservice.ui.controller.UserController;

/**
 * Helps on maintaining Hateoas in its own context
 */
public class LinkProvider {

  private LinkProvider() {
  }

  public static Link userInformation(String userId, String linkName) {
    return linkTo(methodOn(UserController.class).getUserInformation(userId)).withRel(linkName);
  }

  public static Link addressInformation(String userId, String addressId, String linkName) {
    return linkTo(methodOn(UserController.class).getAddressInformation(userId, addressId)).withRel(linkName);
  }

  public static Link userAddresses(String userId, String linkName) {
    return linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel(linkName);
  }

  public static Link selfUser(String userId) {
    return linkTo(UserController.class).slash(userId).withRel("user");
  }

  public static Link selfAddress(String addressId, String userId) {
    return linkTo(methodOn(UserController.class).getAddressInformation(userId, addressId)).withSelfRel();
  }

}
