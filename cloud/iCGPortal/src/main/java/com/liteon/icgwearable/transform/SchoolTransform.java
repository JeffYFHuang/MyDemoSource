package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class SchoolTransform implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7135382122727173281L;
	
	private String city;
	
	private String address;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	private String zipCode;
	
	private String state;

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
	private String country;
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	private String mobileNumber;
	private String county;

	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}

}
