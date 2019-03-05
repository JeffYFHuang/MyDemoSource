package com.liteon.icgwearable.model;

import java.util.Date;

public class DeviceModel {
	
	private Integer eventid;
	private String eventoccureddate;
	private String gpsdatacode;
	private String gpslocationdata;
	private String sensortypecode;
	private String sensorerrorcode;
	private String vitalsigntype;
	private String vitalsignvalue;
	private String abnormalcode;
	private String batterylevelvalue;
	private Date eventOccurredDate;
	private Integer schoolId;
	private Integer ipsLocatorId;
	
	
	public Integer getIpsLocatorId() {
		return ipsLocatorId;
	}
	public void setIpsLocatorId(Integer ipsLocatorId) {
		this.ipsLocatorId = ipsLocatorId;
	}
	public Integer getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}
	public Date getEventOccurredDate() {
		return eventOccurredDate;
	}
	public void setEventOccurredDate(Date eventOccurredDate) {
		this.eventOccurredDate = eventOccurredDate;
	}
	public String getGpsdatacode() {
		return gpsdatacode;
	}
	public String getEventoccureddate() {
		return eventoccureddate;
	}
	public void setEventoccureddate(String eventoccureddate) {
		this.eventoccureddate = eventoccureddate;
	}
	public void setGpsdatacode(String gpsdatacode) {
		this.gpsdatacode = gpsdatacode;
	}
	public String getGpslocationdata() {
		return gpslocationdata;
	}
	public void setGpslocationdata(String gpslocationdata) {
		this.gpslocationdata = gpslocationdata;
	}
	public String getSensortypecode() {
		return sensortypecode;
	}
	public void setSensortypecode(String sensortypecode) {
		this.sensortypecode = sensortypecode;
	}
	public String getSensorerrorcode() {
		return sensorerrorcode;
	}
	public void setSensorerrorcode(String sensorerrorcode) {
		this.sensorerrorcode = sensorerrorcode;
	}
	public String getVitalsigntype() {
		return vitalsigntype;
	}
	public void setVitalsigntype(String vitalsigntype) {
		this.vitalsigntype = vitalsigntype;
	}
	public String getVitalsignvalue() {
		return vitalsignvalue;
	}
	public void setVitalsignvalue(String vitalsignvalue) {
		this.vitalsignvalue = vitalsignvalue;
	}
	public String getAbnormalcode() {
		return abnormalcode;
	}
	public void setAbnormalcode(String abnormalcode) {
		this.abnormalcode = abnormalcode;
	}
	public String getBatterylevelvalue() {
		return batterylevelvalue;
	}
	public void setBatterylevelvalue(String batterylevelvalue) {
		this.batterylevelvalue = batterylevelvalue;
	}
	public Integer getEventid() {
		return eventid;
	}
	public void setEventid(Integer eventid) {
		this.eventid = eventid;
	}
	
	
}
