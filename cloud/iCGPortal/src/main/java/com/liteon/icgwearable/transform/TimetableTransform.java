package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class TimetableTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4475905235103571670L;
	private String studentClass;
	
	private String grade;
	
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public TimetableTransform(){}

	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}
}
