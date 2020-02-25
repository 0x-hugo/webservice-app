package com.empanada.app.webservice.io.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "address")
public class AddressEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -7468683765977775335L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false)
  private String addressId;

  @Column(nullable = false, length = 100)
  private String city;

  @Column(nullable = false, length = 50)
  private String country;

  @Column(nullable = false, length = 100)
  private String streetName;

  @Column(nullable = false, length = 10)
  private String postalCode;

  @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
  @JoinColumn(name = "addresses")
  private UserEntity userDetails;

  public String getAddressId() {
    return addressId;
  }

  public void setAddressId(String addressId) {
    this.addressId = addressId;
  }

  public UserEntity getUserDetails() {
    return userDetails;
  }

  public void setUserDetails(UserEntity userDetails) {
    this.userDetails = userDetails;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
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

}
