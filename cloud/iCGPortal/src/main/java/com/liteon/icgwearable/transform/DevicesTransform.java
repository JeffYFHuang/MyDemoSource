package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.util.Date;

public class DevicesTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4168919463549124024L;
	
	private Integer deviceId;
	private String uuid;
	private Character deviceActive;
	public Character getDeviceActive() {
		return deviceActive;
	}


	public void setDeviceActive(Character deviceActive) {
		this.deviceActive = deviceActive;
	}
	private Date activationDate;
	private Date deactivationDate;
	

	public DevicesTransform(){}
	

	public Date getActivationDate() {
		return activationDate;
	}


	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}


	public Date getDeactivationDate() {
		return deactivationDate;
	}


	public void setDeactivationDate(Date deactivationDate) {
		this.deactivationDate = deactivationDate;
	}
	
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
