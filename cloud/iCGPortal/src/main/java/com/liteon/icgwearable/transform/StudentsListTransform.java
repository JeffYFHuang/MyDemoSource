package com.liteon.icgwearable.transform;

import java.util.Date;

public class StudentsListTransform implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3205348356901916069L;
	private int slNo;
	private Integer studentId;
	private int accountId;
	private int schoolId;
	private String studentName; 
	private String nickName;
	private String studentClass;
	private String rollNo;
	private Date dob;
	private Float height;
	private Float weight;
	private String gender;
	private String deviceUuid;
	private String grade;
	private String emergency_contact;
	private String registartionNumber;
	
	public StudentsListTransform() {}
	
	
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getRegistartionNumber() {
		return registartionNumber;
	}

	public void setRegistartionNumber(String registartionNumber) {
		this.registartionNumber = registartionNumber;
	}

	
	public String getEmergency_contact() {
		return emergency_contact;
	}

	public void setEmergency_contact(String emergency_contact) {
		this.emergency_contact = emergency_contact;
	}

	public int getSlNo() {
		return slNo;
	}
	
	public void setSlNo(int slNo) {
		this.slNo = slNo;
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

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
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

	public String getDeviceUuid() {
		return deviceUuid;
	}

	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}
	
	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	public Integer getStudentId() {
		return studentId;
	}

	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}
}
