package com.liteon.icgwearable.transform;

import java.util.Date;

public class SessionDetailsForIDTransform {
	
	Integer id;
	String sessionId;
	Date expiryduration;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Date getExpiryduration() {
		return expiryduration;
	}
	public void setExpiryduration(Date expiryduration) {
		this.expiryduration = expiryduration;
	}

	
}
