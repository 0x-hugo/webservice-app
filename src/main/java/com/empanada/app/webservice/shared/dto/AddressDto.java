package com.empanada.app.webservice.shared.dto;

public class AddressDto {
	
	private long id;
	private String city;
	private String country;
	private String streetName;
	private String postalCode;
	
	// It's supossed to get some information from the user who has that address (not sure about the impl) TODO
	private UserDto userDetails;
	
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
	public UserDto getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(UserDto userDetails) {
		this.userDetails = userDetails;
	}
}
