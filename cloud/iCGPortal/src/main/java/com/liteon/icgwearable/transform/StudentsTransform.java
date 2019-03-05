package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class StudentsTransform implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7441501544385640828L;
	
	private String stClass;
	private String schoolName;
	
	public StudentsTransform() {}
	
	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getStClass() {
		return stClass;
	}

	public void setStClass(String stClass) {
		this.stClass = stClass;
	}

}
