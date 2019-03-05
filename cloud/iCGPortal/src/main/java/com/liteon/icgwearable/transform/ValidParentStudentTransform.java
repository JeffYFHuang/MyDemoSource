package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class ValidParentStudentTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7496708542195574666L;
	
	private int studentId;
	private int accountId;
	private int maxParents;
	private int maxGuardians;
	//private int totalpairedset;
	
	public int getMaxParents() {
		return maxParents;
	}
	public void setMaxParents(int maxParents) {
		this.maxParents = maxParents;
	}
	public int getMaxGuardians() {
		return maxGuardians;
	}
	public void setMaxGuardians(int maxGuardians) {
		this.maxGuardians = maxGuardians;
	}
	private String deviceActive;
	/*public int getTotalpairedset() {
		return totalpairedset;
	}*/
	public String getDeviceActive() {
		return deviceActive;
	}
	public void setDeviceActive(String deviceActive) {
		this.deviceActive = deviceActive;
	}
	/*public void setTotalpairedset(int totalpairedset) {
		this.totalpairedset = totalpairedset;
	}*/
	public int getStudentId() {
		return studentId;
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	

}
