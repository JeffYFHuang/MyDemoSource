package com.liteon.icgwearable.transform;

import java.util.Date;

public class DeviceEventsTransform {
	
	private String gps_data_code;
	private String gps_location_data;
	private Integer event_id;
	private String status;
	private Date event_occured_date;
	private String uuid;
	private Date start_date;
	private Date end_date;
	
	private String vital_sign_type;
	private String vital_sign_value;
	private String abnormal_code;

	private String sensor_type_code;
	private String sensor_error_code;
	
	private String battery_level_value;
	private Integer zone_id;
	
	public DeviceEventsTransform(){}
	
	public String getSensor_type_code() {
		return sensor_type_code;
	}

	public void setSensor_type_code(String sensor_type_code) {
		this.sensor_type_code = sensor_type_code;
	}

	public String getSensor_error_code() {
		return sensor_error_code;
	}

	public void setSensor_error_code(String sensor_error_code) {
		this.sensor_error_code = sensor_error_code;
	}

	public String getBattery_level_value() {
		return battery_level_value;
	}

	public void setBattery_level_value(String battery_level_value) {
		this.battery_level_value = battery_level_value;
	}

	public String getVital_sign_type() {
		return vital_sign_type;
	}

	public void setVital_sign_type(String vital_sign_type) {
		this.vital_sign_type = vital_sign_type;
	}

	public String getVital_sign_value() {
		return vital_sign_value;
	}

	public void setVital_sign_value(String vital_sign_value) {
		this.vital_sign_value = vital_sign_value;
	}
	
	public String getGps_data_code() {
		return gps_data_code;
	}

	public void setGps_data_code(String gps_data_code) {
		this.gps_data_code = gps_data_code;
	}

	public String getGps_location_data() {
		return gps_location_data;
	}

	public void setGps_location_data(String gps_location_data) {
		this.gps_location_data = gps_location_data;
	}

	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEvent_occured_date() {
		return event_occured_date;
	}

	public void setEvent_occured_date(Date event_occured_date) {
		this.event_occured_date = event_occured_date;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getStart_date() {
		return start_date;
	}

	public void setStart_date(Date start_date) {
		this.start_date = start_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}
	
	
	public String getAbnormal_code() {
		return abnormal_code;
	}

	public void setAbnormal_code(String abnormal_code) {
		this.abnormal_code = abnormal_code;
	}

	public Integer getZone_id() {
		return zone_id;
	}

	public void setZone_id(Integer zone_id) {
		this.zone_id = zone_id;
	}

	@Override
	public String toString() {
		return "DeviceEventsTransform [gps_data_code=" + gps_data_code + ", gps_location_data=" + gps_location_data
				+ ", event_id=" + event_id + ", status=" + status + ", event_occured_date=" + event_occured_date
				+ ", uuid=" + uuid + ", start_date=" + start_date + ", end_date=" + end_date + ", vital_sign_type="
				+ vital_sign_type + ", vital_sign_value=" + vital_sign_value + ", abnormal_code=" + abnormal_code
				+ ", sensor_type_code=" + sensor_type_code + ", sensor_error_code=" + sensor_error_code
				+ ", battery_level_value=" + battery_level_value + ", zone_id=" + zone_id + "]";
	}
}
