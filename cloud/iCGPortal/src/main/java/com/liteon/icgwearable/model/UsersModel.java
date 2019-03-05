package com.liteon.icgwearable.model;

public class UsersModel {
	
	private Integer slNo=0;
	private Integer id=0;
	private String username;
	private String password;
	private String mobileNumber;
	private String roletypes;
	private String forcelogin;
	private String uniqueClass;
	private String name;
	private String uuid;
	private String cpassword;
	private String user_role;
	private String grade;
	private String studentClass;
	private String openid_username;
	
	public UsersModel() {}
	
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	
	public String getOpenid_username() {
		return openid_username;
	}

	public void setOpenid_username(String openid_username) {
		this.openid_username = openid_username;
	}

	public String getCpassword() {
		return cpassword;
	}

	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}

	public String getUser_role() {
		return user_role;
	}

	public void setUser_role(String user_role) {
		this.user_role = user_role;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUniqueClass() {
		return uniqueClass;
	}

	public void setUniqueClass(String uniqueClass) {
		this.uniqueClass = uniqueClass;
	}

	public String getForcelogin() {
		return forcelogin;
	}

	public void setForcelogin(String forcelogin) {
		this.forcelogin = forcelogin;
	}

	public Integer getSlNo() {
		return slNo;
	}

	public void setSlNo(Integer slNo) {
		this.slNo = slNo;
	}
	
	public String getRoletypes() {
		return roletypes;
	}

	public void setRoletypes(String roletypes) {
		this.roletypes = roletypes;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getUsername() {
		if(null != username)
		return username.toLowerCase();
		else
		return "";
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

	
}
