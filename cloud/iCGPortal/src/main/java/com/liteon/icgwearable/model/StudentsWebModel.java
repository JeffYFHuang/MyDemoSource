package com.liteon.icgwearable.model;

public class StudentsWebModel {
	
	private String accMgtStudentName;
	private String accntMgtStudentGrade;
	private String accMgtStudentUuid;
	private String accMgtStudentClass;
	private String accMgtStudentAppno;
	private String accMgtStudentContact;
	private String accMgtStudentRollno;
	
	public StudentsWebModel() {}

	public String getAccMgtStudentName() {
		return accMgtStudentName;
	}

	public void setAccMgtStudentName(String accMgtStudentName) {
		this.accMgtStudentName = accMgtStudentName;
	}

	public String getAccntMgtStudentGrade() {
		return accntMgtStudentGrade;
	}

	public void setAccntMgtStudentGrade(String accntMgtStudentGrade) {
		this.accntMgtStudentGrade = accntMgtStudentGrade;
	}

	public String getAccMgtStudentUuid() {
		return accMgtStudentUuid;
	}

	public void setAccMgtStudentUuid(String accMgtStudentUuid) {
		this.accMgtStudentUuid = accMgtStudentUuid;
	}

	public String getAccMgtStudentClass() {
		return accMgtStudentClass;
	}

	public void setAccMgtStudentClass(String accMgtStudentClass) {
		this.accMgtStudentClass = accMgtStudentClass;
	}

	public String getAccMgtStudentAppno() {
		return accMgtStudentAppno;
	}

	public void setAccMgtStudentAppno(String accMgtStudentAppno) {
		this.accMgtStudentAppno = accMgtStudentAppno;
	}

	public String getAccMgtStudentContact() {
		return accMgtStudentContact;
	}

	public void setAccMgtStudentContact(String accMgtStudentContact) {
		this.accMgtStudentContact = accMgtStudentContact;
	}

	public String getAccMgtStudentRollno() {
		return accMgtStudentRollno;
	}

	public void setAccMgtStudentRollno(String accMgtStudentRollno) {
		this.accMgtStudentRollno = accMgtStudentRollno;
	}

	@Override
	public String toString() {
		return "StudentsWebModel [accMgtStudentName=" + accMgtStudentName + ", accntMgtStudentGrade="
				+ accntMgtStudentGrade + ", accMgtStudentUuid=" + accMgtStudentUuid + ", accMgtStudentClass="
				+ accMgtStudentClass + ", accMgtStudentAppno=" + accMgtStudentAppno + ", accMgtStudentContact="
				+ accMgtStudentContact + ", accMgtStudentRollno=" + accMgtStudentRollno + "]";
	}
}
