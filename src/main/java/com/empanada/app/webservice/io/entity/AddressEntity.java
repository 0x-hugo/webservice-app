package com.empanada.app.webservice.io.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.empanada.app.webservice.shared.dto.UserDto;

@Entity(name="address")
public class AddressEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7468683765977775335L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column(nullable = false, length = 30)
	private long addressId;

	@Column(nullable = false, length = 100)
	private String city;
	
	@Column(nullable = false, length = 50)
	private String country;
	
	@Column(nullable = false, length = 100)
	private String streetName;
	
	@Column(nullable = false, length = 10)
	private String postalCode;
	
	@JoinColumn(name="addresses")
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
	
	
}
