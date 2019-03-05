package com.liteon.icgwearable.transform;

import java.util.Date;

public class DeviceAnalyticsOutputTransform {
	
	private String physicalFitnessIndex;

	private Date createdDate;
	
	private String firmwareVersion;
	
	public DeviceAnalyticsOutputTransform() {}
	
	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getPhysicalFitnessIndex() {
		return physicalFitnessIndex;
	}

	public void setPhysicalFitnessIndex(String physicalFitnessIndex) {
		this.physicalFitnessIndex = physicalFitnessIndex;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
