package com.liteon.icgwearable.model;

public class StudentCSVModel {

	private String uuid;
	private String studentName;
	private String studentRollNo;
	private String studentClass;
	private String grade;
	private String admissionNo;
	private String emergency_contact;

	public StudentCSVModel() {}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentRollNo() {
		return studentRollNo;
	}

	public void setStudentRollNo(String studentRollNo) {
		this.studentRollNo = studentRollNo;
	}

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

	public String getAdmissionNo() {
		return admissionNo;
	}

	public void setAdmissionNo(String admissionNo) {
		this.admissionNo = admissionNo;
	}

	public String getEmergency_contact() {
		return emergency_contact;
	}

	public void setEmergency_contact(String emergency_contact) {
		this.emergency_contact = emergency_contact;
	}

	@Override
	public String toString() {
		return "StudentCSVModel [uuid=" + uuid + ", studentName=" + studentName + ", studentRollNo=" + studentRollNo
				+ ", studentClass=" + studentClass + ", grade=" + grade + ", admissionNo=" + admissionNo
				+ ", emergency_contact=" + emergency_contact + "]";
	}
	
	
}
