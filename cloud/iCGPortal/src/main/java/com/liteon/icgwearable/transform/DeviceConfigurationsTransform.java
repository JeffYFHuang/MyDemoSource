package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class DeviceConfigurationsTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6608846117212347622L;

	private String configName;
	
	private String configValue;
	
	private String deviceModel;
	
	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getFrimWareVersion() {
		return frimWareVersion;
	}

	public void setFrimWareVersion(String frimWareVersion) {
		this.frimWareVersion = frimWareVersion;
	}

	private String frimWareVersion;
	

	public DeviceConfigurationsTransform(){}
	
	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
}
