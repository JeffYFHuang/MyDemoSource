package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class ValidTeacherUsersWithTokenTransform implements Serializable{

	private static final long serialVersionUID = 5214560932751124157L;
	
	public ValidTeacherUsersWithTokenTransform(){}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getAndroidAppToken() {
		return androidAppToken;
	}

	public void setAndroidAppToken(String androidAppToken) {
		this.androidAppToken = androidAppToken;
	}

	public String getiPhoneAppToken() {
		return iPhoneAppToken;
	}

	public void setiPhoneAppToken(String iPhoneAppToken) {
		this.iPhoneAppToken = iPhoneAppToken;
	}
	
	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	private String studentName;

	private int userId;
	private String roleType;
	private String androidAppToken;
	private String iPhoneAppToken;
}
