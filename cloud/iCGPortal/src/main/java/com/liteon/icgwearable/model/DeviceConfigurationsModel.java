package com.liteon.icgwearable.model;

import java.sql.Date;

public class DeviceConfigurationsModel implements Model{
	private Integer device_configuration_id;
	private String name;
	private String version;
	private Double size_mb;
	private Integer low_battery_percent;
	private Integer gps_report_min;
	private Integer device_self_test_hrs;
	private Integer wearable_sync_hrs;
	private Date created_date;
	private String file_name;
	
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public Integer getDevice_configuration_id() {
		return device_configuration_id;
	}
	public void setDevice_configuration_id(Integer device_configuration_id) {
		this.device_configuration_id = device_configuration_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Double getSize_mb() {
		return size_mb;
	}
	public void setSize_mb(Double size_mb) {
		this.size_mb = size_mb;
	}
	public Integer getLow_battery_percent() {
		return low_battery_percent;
	}
	public void setLow_battery_percent(Integer low_battery_percent) {
		this.low_battery_percent = low_battery_percent;
	}
	public Integer getGps_report_min() {
		return gps_report_min;
	}
	public void setGps_report_min(Integer gps_report_min) {
		this.gps_report_min = gps_report_min;
	}
	public Integer getDevice_self_test_hrs() {
		return device_self_test_hrs;
	}
	public void setDevice_self_test_hrs(Integer device_self_test_hrs) {
		this.device_self_test_hrs = device_self_test_hrs;
	}
	public Integer getWearable_sync_hrs() {
		return wearable_sync_hrs;
	}
	public void setWearable_sync_hrs(Integer wearable_sync_hrs) {
		this.wearable_sync_hrs = wearable_sync_hrs;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	
	
}
