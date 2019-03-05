package com.liteon.icgwearable.transform;

import java.sql.Timestamp;

public class ActivityLogTransform {

	private String profileName;
	private String username;
	private String role;
	private String action;
	private String ipaddress;
	private String created_date;
	
	public ActivityLogTransform() {}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	@Override
	public String toString() {
		return "ActivityLogTransform [profileName=" + profileName + ", username=" + username + ", role=" + role
				+ ", action=" + action + ", ipaddress=" + ipaddress + ", created_date=" + created_date + "]";
	}

}
