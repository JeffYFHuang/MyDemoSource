package com.liteon.icgwearable.model;

public class TeacherStaffModel {

	private String acntTeacherStaffRole;
	private String accMgtTeacherGrade;
	private String accTeacherStaffName;
	private String accMgtTeacherClass;
	private String accTeacherStaffEmail;
	private String accTeacherStaffContact;
	
	private TeacherStaffModel() {}

	public String getAcntTeacherStaffRole() {
		return acntTeacherStaffRole;
	}

	public void setAcntTeacherStaffRole(String acntTeacherStaffRole) {
		this.acntTeacherStaffRole = acntTeacherStaffRole;
	}

	public String getAccMgtTeacherGrade() {
		return accMgtTeacherGrade;
	}

	public void setAccMgtTeacherGrade(String accMgtTeacherGrade) {
		this.accMgtTeacherGrade = accMgtTeacherGrade;
	}

	public String getAccTeacherStaffName() {
		return accTeacherStaffName;
	}

	public void setAccTeacherStaffName(String accTeacherStaffName) {
		this.accTeacherStaffName = accTeacherStaffName;
	}

	public String getAccMgtTeacherClass() {
		return accMgtTeacherClass;
	}

	public void setAccMgtTeacherClass(String accMgtTeacherClass) {
		this.accMgtTeacherClass = accMgtTeacherClass;
	}

	public String getAccTeacherStaffEmail() {
		return accTeacherStaffEmail;
	}

	public void setAccTeacherStaffEmail(String accTeacherStaffEmail) {
		this.accTeacherStaffEmail = accTeacherStaffEmail;
	}

	public String getAccTeacherStaffContact() {
		return accTeacherStaffContact;
	}

	public void setAccTeacherStaffContact(String accTeacherStaffContact) {
		this.accTeacherStaffContact = accTeacherStaffContact;
	}

	@Override
	public String toString() {
		return "TeacherStaffModel [acntTeacherStaffRole=" + acntTeacherStaffRole + ", accMgtTeacherGrade="
				+ accMgtTeacherGrade + ", accTeacherStaffName=" + accTeacherStaffName + ", accMgtTeacherClass="
				+ accMgtTeacherClass + ", accTeacherStaffEmail=" + accTeacherStaffEmail + ", accTeacherStaffContact="
				+ accTeacherStaffContact + "]";
	}
}
