package com.liteon.icgwearable.transform;


public class ValidNewDevicePairingTransform {
	
	/*private int studentId;
	private int accountId;
	private int totalpairedset;*/
	
	private String deviceActive;
	private String roleType;
	private int maxParents;
	private int maxGuardians;
	
	
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
	
	public String getDeviceActive() {
		return deviceActive;
	}
	public void setDeviceActive(String deviceActive) {
		this.deviceActive = deviceActive;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	

}
