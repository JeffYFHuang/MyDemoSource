package com.liteon.icgwearable.model;

public class TeachersStaffCSVModel {

	private String grade;
	private String stClass;
	private String name;
	private String username;
	private String role;
	private String contactNo;
	
	public TeachersStaffCSVModel() {}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getStClass() {
		return stClass;
	}

	public void setStClass(String stClass) {
		this.stClass = stClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	@Override
	public String toString() {
		return "TeachersStaffCSVModel [grade=" + grade + ", stClass=" + stClass + ", name=" + name + ", username="
				+ username + ", role=" + role + ", contactNo=" + contactNo + "]";
	}
	
	
}
