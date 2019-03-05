package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class DeviceConfigTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7678923727625136906L;
	
	public Integer getConfig_id() {
		return config_id;
	}
	public void setConfig_id(Integer config_id) {
		this.config_id = config_id;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	private Integer config_id;
	private String model;
	public String getFirmware_version() {
		return firmware_version;
	}
	public void setFirmware_version(String firmware_version) {
		this.firmware_version = firmware_version;
	}
	public Integer getLowBattery() {
		return lowBattery;
	}
	public void setLowBattery(Integer lowBattery) {
		this.lowBattery = lowBattery;
	}
	public Integer getWearableSyncFrequency() {
		return wearableSyncFrequency;
	}
	public void setWearableSyncFrequency(Integer wearableSyncFrequency) {
		this.wearableSyncFrequency = wearableSyncFrequency;
	}
	public Integer getDeviceSelfTestingVersion() {
		return deviceSelfTestingVersion;
	}
	public void setDeviceSelfTestingVersion(Integer deviceSelfTestingVersion) {
		this.deviceSelfTestingVersion = deviceSelfTestingVersion;
	}
	public Integer getGpsReportFrequency() {
		return gpsReportFrequency;
	}
	public void setGpsReportFrequency(Integer gpsReportFrequency) {
		this.gpsReportFrequency = gpsReportFrequency;
	}
	private String firmware_version;

	private Integer lowBattery;
	
	private Integer wearableSyncFrequency;
		
	private Integer deviceSelfTestingVersion;
		
	private Integer gpsReportFrequency;
	
	

}
