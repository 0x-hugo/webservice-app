package com.empanada.app.webservice.exceptions;

public class UserNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 3645470195328258128L;

  public UserNotFoundException(String message) {
    super(message);
  }

}
