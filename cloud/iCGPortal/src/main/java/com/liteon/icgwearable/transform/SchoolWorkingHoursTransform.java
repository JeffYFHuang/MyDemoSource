package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class SchoolWorkingHoursTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4999704095500919683L;
	
	private Time schoolOut;
	private Time schoolStart;
	private Date eventOccuredDate;
	private Integer device_event_id;
	
	public Integer getDevice_event_id() {
		return device_event_id;
	}
	public void setDevice_event_id(Integer device_event_id) {
		this.device_event_id = device_event_id;
	}
	private Integer schoolId;
	public Time getSchoolOut() {
		return schoolOut;
	}
	public Date getEventOccuredDate() {
		return eventOccuredDate;
	}
	public void setEventOccuredDate(Date eventOccuredDate) {
		this.eventOccuredDate = eventOccuredDate;
	}
	public void setSchoolOut(Time schoolOut) {
		this.schoolOut = schoolOut;
	}
	public Time getSchoolStart() {
		return schoolStart;
	}
	public void setSchoolStart(Time schoolStart) {
		this.schoolStart = schoolStart;
	}
	public Integer getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}
}
