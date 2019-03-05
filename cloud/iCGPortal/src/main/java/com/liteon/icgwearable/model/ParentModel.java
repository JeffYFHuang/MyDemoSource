package com.liteon.icgwearable.model;

import javax.persistence.Column;

public class ParentModel implements Model{
	
	private String username;
	private String name;
	private String mobile_number;
	private String password;
	private String cPassword="";
	private String streetAddress;
	@Column(name = "role_type", columnDefinition = "enum('parent_admin','parent_member','school_admin','school_teacher','school_staff','super_admin','system_admin','support_staff')")
	private String roleType;
	
	public ParentModel() {}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMobile_number() {
		return mobile_number;
	}
	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getcPassword() {
		return cPassword;
	}
	public void setcPassword(String cPassword) {
		this.cPassword = cPassword;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	
	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
}
