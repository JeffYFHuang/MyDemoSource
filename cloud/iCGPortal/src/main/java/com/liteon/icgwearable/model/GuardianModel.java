package com.liteon.icgwearable.model;

import java.util.List;

public class GuardianModel {

	private String name;
	private String guardianUserName;
	private List<String> kidsname;
	private String mobileNumber;
	
	public GuardianModel() {}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getGuardianUserName() {
		return guardianUserName;
	}

	public void setGuardianUserName(String guardianUserName) {
		this.guardianUserName = guardianUserName;
	}

	public List<String> getKidsname() {
		return kidsname;
	}

	public void setKidsname(List<String> kidsname) {
		this.kidsname = kidsname;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "GuardianUserName [name=" + name + "guardianUserName=" + guardianUserName + "mobileNumber=" +mobileNumber+"]";
	}
}
