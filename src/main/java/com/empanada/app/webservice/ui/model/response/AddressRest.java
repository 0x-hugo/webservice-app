package com.empanada.app.webservice.ui.model.response;

import org.springframework.hateoas.RepresentationModel;

import com.empanada.app.webservice.ui.utils.LinkProvider;

public class AddressRest extends RepresentationModel<AddressRest> {

  private String addressId;
  private String city;
  private String country;
  private String streetName;
  private String postalCode;

  LinkProvider linkProvider;

  public AddressRest(LinkProvider linkProvider) {
    this.linkProvider = linkProvider;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getAddressId() {
    return addressId;
  }

  public void setAddressId(String addressId) {
    this.addressId = addressId;
  }

}
