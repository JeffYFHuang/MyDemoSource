package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.util.Date;

public class TeachersStudentsTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int studentId;
	private int accountId;
	private int schoolId;
	private String studentName;
	private String nickName;
	private String studentClass;
	private String rollNo;
	private Date dob;
	private Float height;
	private Float weight;
	private String registartionNumber;
	private String allergeis;
	private String gender;
	private Date createdDate;
	private Date updatedDate;
	private String deviceUuid;
	private Integer deviceId;
	private String emergency_contact;
	
	public TeachersStudentsTransform() {}
	
	public String getAllergeis() {
		return allergeis;
	}

	public void setAllergeis(String allergeis) {
		this.allergeis = allergeis;
	}

	public String getEmergency_contact() {
		return emergency_contact;
	}

	public void setEmergency_contact(String emergency_contact) {
		this.emergency_contact = emergency_contact;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getDeviceUuid() {
		return deviceUuid;
	}

	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public String getRegistartionNumber() {
		return registartionNumber;
	}

	public void setRegistartionNumber(String registartionNumber) {
		this.registartionNumber = registartionNumber;
	}
}
