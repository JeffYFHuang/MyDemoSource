package com.liteon.icgwearable.model;

import java.util.Date;

public class DeviceListModel {
	
	public Integer getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(Integer deviceid) {
		this.deviceid = deviceid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Integer getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getFirmWareVersion() {
		return firmWareVersion;
	}
	public void setFirmWareVersion(String firmWareVersion) {
		this.firmWareVersion = firmWareVersion;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getAddedDate() {
		return addedDate;
	}
	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}
	public Integer getDeviceConfigurationID() {
		return deviceConfigurationID;
	}
	public void setDeviceConfigurationID(Integer deviceConfigurationID) {
		this.deviceConfigurationID = deviceConfigurationID;
	}
	private Integer deviceid;
	private String uuid;
	private Integer schoolId;
	private String schoolName;
	private String firmWareVersion;
	private String deviceModel;
	private String status;
	private Date addedDate;
	private Integer deviceConfigurationID;
	
	
}
