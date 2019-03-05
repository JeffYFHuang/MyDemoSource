package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class GuardiansDetailsListTransform implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5222285117775337809L;
	
	private int user_id;
	private String guadianName;
	private String role_type;
	private String mobile_number;
	private String userName;
	private char status;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMobile_number() {
		return mobile_number;
	}
	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getGuadianName() {
		return guadianName;
	}
	public void setGuadianName(String guadianName) {
		this.guadianName = guadianName;
	}
	public String getRole_type() {
		return role_type;
	}
	public void setRole_type(String role_type) {
		this.role_type = role_type;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	
}
