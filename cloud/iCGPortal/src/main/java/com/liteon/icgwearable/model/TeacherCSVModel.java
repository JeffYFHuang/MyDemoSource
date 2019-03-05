package com.liteon.icgwearable.model;

public class TeacherCSVModel {


	private String name;
	private String username;
	private String password;
	private String uuid;
	private String contactNo;
	private String studentClass;
	private String grade;

	public TeacherCSVModel() {}
	
	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
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

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	

	@Override
	public String toString() {
		return "TeacherCSVModel [name=" + name + ", username=" + username + ", password=" + password + ", uuid=" + uuid
				+ ", contactNo=" + contactNo + ", studentClass=" + studentClass + ", grade=" + grade + "]";
	}
}
