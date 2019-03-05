package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class AccountFroSchoolTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4907659726549175847L;
	
	private int account_id ;
	public int getAccount_id() {
		return account_id;
	}
	public void setAccount_id(int account_id) {
		this.account_id = account_id;
	}
	public String getAccount_name() {
		return account_name;
	}
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
	private String account_name;

}
