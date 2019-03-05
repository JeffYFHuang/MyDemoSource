package com.liteon.icgwearable.model;

import java.util.Date;

public class SchoolModel {
	
	private int accountID;
	private String accountName;
	private String accountType;
	private String streetAddress;
	private String city;
	private String state;
	private String zipcode;
	private String county;
	private String country;
	private String accountActive;
	private String mobileNumber;
	private String address;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	private Date lastLoginDate;
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	public String getSchoolAdmin() {
		return schoolAdmin;
	}
	public void setSchoolAdmin(String schoolAdmin) {
		this.schoolAdmin = schoolAdmin;
	}
	public int getAllocatedDevices() {
		return allocatedDevices;
	}
	public void setAllocatedDevices(int allocatedDevices) {
		this.allocatedDevices = allocatedDevices;
	}
	private String schoolAdmin;
	private int allocatedDevices;
	
	
	public String getMobileNumber() {
		return mobileNumber;
	}
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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
	public String getAccountActive() {
		return accountActive;
	}
	public void setAccountActive(String accountActive) {
		this.accountActive = accountActive;
	}
	
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the accountID
	 */
	public int getAccountID() {
		return accountID;
	}
	/**
	 * @param accountID the accountID to set
	 */
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	public String getCounty() {
		return county;
	}
	public void setCounty(String county) {
		this.county = county;
	}

}
