package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class ClassGradeTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5899756830337764025L;
	private Integer teacher_id;
	private String  studentClass;
	private String role_type;
	private String  studentGrade;

	public ClassGradeTransform() {}

	public String getStudentGrade() {
		return studentGrade;
	}

	public void setStudentGrade(String studentGrade) {
		this.studentGrade = studentGrade;
	}
	
	public String getStudentClass() {
		return studentClass;
	}
	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}
	public String getRole_type() {
		return role_type;
	}
	public void setRole_type(String role_type) {
		this.role_type = role_type;
	}

	public Integer getTeacher_id() {
		return teacher_id;
	}

	public void setTeacher_id(Integer teacher_id) {
		this.teacher_id = teacher_id;
	}

	@Override
	public String toString() {
		return "ClassGradeTransform [teacher_id=" + teacher_id + ", studentClass=" + studentClass + ", role_type="
				+ role_type + ", studentGrade=" + studentGrade + "]";
	}
}
