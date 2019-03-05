package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.util.Date;

public class AdminTransform implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6762228682236784224L;
	
  public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getLastLoginDate() {
		return lastLoginDate;
	}
	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}
	private String userName;
	private Date lastLoginDate;
  
  
  

}
