package com.liteon.icgwearable.model;

import java.util.Date;

public class DeviceTypeAlertLatestModel {
	
	private Date eventOccuredDate;
	private Integer  eventid;
	private String gps_location;
	
	public String getGps_location() {
		return gps_location;
	}
	public void setGps_location(String gps_location) {
		this.gps_location = gps_location;
	}
	public Date getEventOccuredDate() {
		return eventOccuredDate;
	}
	public void setEventOccuredDate(Date eventOccuredDate) {
		this.eventOccuredDate = eventOccuredDate;
	}
	public int getEventid() {
		return eventid;
	}
	public void setEventid(int eventid) {
		this.eventid = eventid;
	}
}
