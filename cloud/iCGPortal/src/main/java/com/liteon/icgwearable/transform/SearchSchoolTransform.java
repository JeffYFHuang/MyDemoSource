package com.liteon.icgwearable.transform;

import java.math.BigInteger;
import java.util.Date;

public class SearchSchoolTransform {

	private Integer id;
	private String contact;
	private String username;
	private Date lastlogin;
	private BigInteger alloteddevice;
	private String address;
	private String county;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Date getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(Date lastlogin) {
		this.lastlogin = lastlogin;
	}
	public BigInteger getAlloteddevice() {
		return alloteddevice;
	}
	public void setAlloteddevice(BigInteger alloteddevice) {
		this.alloteddevice = alloteddevice;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	@Override
	public String toString() {
		return "SearchSchoolTransform [id=" + id + ", contact=" + contact + ", username=" + username + ", lastlogin="
				+ lastlogin + ", alloteddevice=" + alloteddevice + ", address=" + address + ", county=" + county
				+ ", city=" + city + ", state=" + state + ", zipcode=" + zipcode + ", country=" + country + "]";
	}
}
