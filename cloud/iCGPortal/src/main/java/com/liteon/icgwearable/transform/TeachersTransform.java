package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.util.Date;

public class TeachersTransform implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5167111122420408496L;

	private Integer teacherId;
	private int teacherAccntId;
	private String email;
	private String password;
	private Date createDate;
	private Date updateDate;
	private String roleType;
	
	private String name;
	private Character status;
	private String rType;
	private String grade;
	private String stclass;
	private String contact_no;
	private String username;
	
	public TeachersTransform() {}
	

	public Integer getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Integer teacherId) {
		this.teacherId = teacherId;
	}

	public int getTeacherAccntId() {
		return teacherAccntId;
	}

	public void setTeacherAccntId(int teacherAccntId) {
		this.teacherAccntId = teacherAccntId;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}


	public String getContact_no() {
		return contact_no;
	}

	public void setContact_no(String contact_no) {
		this.contact_no = contact_no;
	}
	
	public Character getStatus() {
		return status;
	}

	public void setStatus(Character status) {
		this.status = status;
	}

	public String getrType() {
		return rType;
	}

	public void setrType(String rType) {
		this.rType = rType;
	}

	public String getStclass() {
		return stclass;
	}

	public void setStclass(String stclass) {
		this.stclass = stclass;
	}
}
