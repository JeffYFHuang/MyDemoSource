package com.liteon.icgwearable.model;

import java.sql.Time;
import java.util.Date;

public class LatestGeoFenceAlertModel {
	
	private Date enterDate;
	private	Date exitDate;
	private String entry_gps_location;
	private String exit_gps_location;
	private Time outTime;

	public String getEntry_gps_location() {
		return entry_gps_location;
	}
	
	public void setEntry_gps_location(String entry_gps_location) {
		this.entry_gps_location = entry_gps_location;
	}
	
	public String getExit_gps_location() {
		return exit_gps_location;
	}
	
	public void setExit_gps_location(String exit_gps_location) {
		this.exit_gps_location = exit_gps_location;
	}
	
	public Date getEnterDate() {
		return enterDate;
	}
	
	public void setEnterDate(Date enterDate) {
		this.enterDate = enterDate;
	}
	
	public Date getExitDate() {
		return exitDate;
	}
	
	public void setExitDate(Date exitDate) {
		this.exitDate = exitDate;
	}

	public Time getOutTime() {
		return outTime;
	}

	public void setOutTime(Time outTime) {
		this.outTime = outTime;
	}

}
