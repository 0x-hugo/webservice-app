package com.empanada.app.webservice.io;

import com.empanada.app.webservice.ui.model.response.UserRest;
import com.empanada.app.webservice.shared.dto.*;

public class UserMock {

  private UserRest userRest;

  public static UserRest buildDefaultUserRest() {
    return new UserRest();
  }

  public static UserBasicInformationDTO buildDefaultDTO() {
    return new UserBasicInformationDTO();
  }

   public static UserRest mappedUserRest() {
     return new UserRest();
   }

}
