package com.liteon.icgwearable.model;

import java.util.Date;

public class DeviceEventModel {
	
	
	private Integer deviceId;
	private Integer deviceEventId;
	private Integer eventId;
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public Integer getDeviceEventId() {
		return deviceEventId;
	}
	public void setDeviceEventId(Integer deviceEventId) {
		this.deviceEventId = deviceEventId;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public Date getEventoccureddate() {
		return eventoccureddate;
	}
	public void setEventoccureddate(Date eventoccureddate) {
		this.eventoccureddate = eventoccureddate;
	}
	private Date eventoccureddate;

}
