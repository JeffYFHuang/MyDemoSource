package com.liteon.icgwearable.model;

import java.sql.Time;
import java.util.Date;

public class KidsSafeModel {
	
	private Date eventOccuredDate;
	private String gps_location;
	private Integer event_id;
	private Time outTime;
	
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

	public Integer getEvent_id() {
		return event_id;
	}
	
	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}
	
	public Time getOutTime() {
		return outTime;
	}
	
	public void setOutTime(Time outTime) {
		this.outTime = outTime;
	} 

}
