package com.liteon.icgwearable.transform;

import java.util.Date;

public class StudentsMapLocationTransform implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8993424934180164213L;
	
	private String deviceUuid;
	private String gpsLocationData;
	private Date eventOccuredDate;
	
	public StudentsMapLocationTransform() {}

	public Date getEventOccuredDate() {
		return eventOccuredDate;
	}

	public void setEventOccuredDate(Date eventOccuredDate) {
		this.eventOccuredDate = eventOccuredDate;
	}

	public String getDeviceUuid() {
		return deviceUuid;
	}

	public void setDeviceUuid(String deviceUuid) {
		this.deviceUuid = deviceUuid;
	}

	public String getGpsLocationData() {
		return gpsLocationData;
	}

	public void setGpsLocationData(String gpsLocationData) {
		this.gpsLocationData = gpsLocationData;
	}
}
