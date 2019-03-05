package com.liteon.icgwearable.model;

import java.util.Date;

public class LatestGPSLocationModel {
	
	public Date getEventOccuredDate() {
		return eventOccuredDate;
	}
	public void setEventOccuredDate(Date eventOccuredDate) {
		this.eventOccuredDate = eventOccuredDate;
	}

	public String getGps_location() {
		return gps_location;
	}
	public void setGps_location(String gps_location) {
		this.gps_location = gps_location;
	}
	private Date eventOccuredDate;
	private String gps_location;

}
