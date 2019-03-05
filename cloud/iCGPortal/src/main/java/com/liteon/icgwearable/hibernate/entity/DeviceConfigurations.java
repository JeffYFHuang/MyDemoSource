package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "device_configurations")
public class DeviceConfigurations implements java.io.Serializable{

	private static final long serialVersionUID = 386131647254161156L;
	
	@Id
	@GeneratedValue
	@Column(name = "device_configuration_id", unique = true, nullable = false)
	private int deviceConfigId;

	@Column(name = "device_model")
	private String deviceModel;
	
	@Column(name = "firmware_name")
	private String firmwareName;
	
	@Column(name = "description")
	private String description;
	@Column(name = "firmware_version")
	private String firmwareVersion;
	
	@Column(name = "firmware_size")
	private Double firmwaresize;
	
	@Column(name = "firmware_file")
	private String firmwareFile;
	
	@Column(name="low_battery")
	private Integer lowBattery;
	
	@Column(name="wearable_sync_frequency")
	private Integer wearableSyncFrequency;
	
	@Column(name="device_self_testing_version")
	private Integer deviceSelfTestingVersion;
	
	@Column(name="gps_report_frequency")
	private Integer gpsReportFrequency;
	
	@Column(name = "created_date", columnDefinition="DATETIME")
	private Date createdDate;
	
	@Column(name = "updated_date", columnDefinition="DATETIME")
	private Date updatedDate;
	

	public DeviceConfigurations() {}
	
	public int getDeviceConfigId() {
		return deviceConfigId;
	}

	public void setDeviceConfigId(int deviceConfigId) {
		this.deviceConfigId = deviceConfigId;
	}

	public String getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getFirmwareName() {
		return firmwareName;
	}

	public void setFirmwareName(String firmwareName) {
		this.firmwareName = firmwareName;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public Double getFirmwaresize() {
		return firmwaresize;
	}

	public void setFirmwaresize(Double firmwaresize) {
		this.firmwaresize = firmwaresize;
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

	public Integer getGpsReportFrequency() {
		return gpsReportFrequency;
	}

	public void setGpsReportFrequency(Integer gpsReportFrequency) {
		this.gpsReportFrequency = gpsReportFrequency;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public String getFirmwareFile() {
		return firmwareFile;
	}

	public void setFirmwareFile(String firmwareFile) {
		this.firmwareFile = firmwareFile;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getDeviceSelfTestingVersion() {
		return deviceSelfTestingVersion;
	}

	public void setDeviceSelfTestingVersion(Integer deviceSelfTestingVersion) {
		this.deviceSelfTestingVersion = deviceSelfTestingVersion;
	}
}
