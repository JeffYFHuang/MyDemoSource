package com.liteon.icgwearable.model;

import java.util.Date;

public class DeviceConfigModel {
	
	private String firmWareVersion;
	private String firmWareName;
	private String deviceModel;
	private String description;
	private Date createdDate;
	private double size;
	private int deviceConfigId;
	private int lowBattery;
	private int gepReportFreequency;
	private int dataSyncFreequency;
	private int deviceselftesting;

	private String uuid;
	private String firmware_file;
	
	public DeviceConfigModel() {}
	
	public int getDeviceselftesting() {
		return deviceselftesting;
	}

	public void setDeviceselftesting(int deviceselftesting) {
		this.deviceselftesting = deviceselftesting;
	}
	
	public int getDeviceConfigId() {
		return deviceConfigId;
	}
	public void setDeviceConfigId(int deviceConfigId) {
		this.deviceConfigId = deviceConfigId;
	}
	public int getLowBattery() {
		return lowBattery;
	}
	public void setLowBattery(int lowBattery) {
		this.lowBattery = lowBattery;
	}
	public int getGepReportFreequency() {
		return gepReportFreequency;
	}
	public void setGepReportFreequency(int gepReportFreequency) {
		this.gepReportFreequency = gepReportFreequency;
	}
	public int getDataSyncFreequency() {
		return dataSyncFreequency;
	}
	public void setDataSyncFreequency(int dataSyncFreequency) {
		this.dataSyncFreequency = dataSyncFreequency;
	}
	
	public String getFirmWareVersion() {
		return firmWareVersion;
	}
	public void setFirmWareVersion(String firmWareVersion) {
		this.firmWareVersion = firmWareVersion;
	}
	public String getFirmWareName() {
		return firmWareName;
	}
	public void setFirmWareName(String firmWareName) {
		this.firmWareName = firmWareName;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getFirmware_file() {
		return firmware_file;
	}

	public void setFirmware_file(String firmware_file) {
		this.firmware_file = firmware_file;
	}
	
}
