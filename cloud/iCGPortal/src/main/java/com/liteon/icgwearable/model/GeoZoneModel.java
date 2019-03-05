package com.liteon.icgwearable.model;

import java.util.Date;

public class GeoZoneModel {

	private Integer geozone_id = 0;
	private Integer user_id = 0;
	private String uuid;
	private String zone_details = "";
	private String zone_name = "";
	private String zone_entry_alert = "";
	private String zone_exit_alert = "";
	private String zone_description = "";
	private Integer frequency_minutes = 0;
	private Float zone_radius = 0f;
	
	public Float getZone_radius() {
		return zone_radius;
	}

	public void setZone_radius(Float zone_radius) {
		this.zone_radius = zone_radius;
	}

	private Date valid_till = null;

	public Integer getGeozone_id() {
		return geozone_id;
	}

	public void setGeozone_id(Integer geozone_id) {
		this.geozone_id = geozone_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public String getZone_details() {
		return zone_details;
	}

	public void setZone_details(String zone_details) {
		this.zone_details = zone_details;
	}

	public String getZone_name() {
		return zone_name;
	}

	public void setZone_name(String zone_name) {
		this.zone_name = zone_name;
	}

	public String getZone_entry_alert() {
		return zone_entry_alert;
	}

	public void setZone_entry_alert(String zone_entry_alert) {
		this.zone_entry_alert = zone_entry_alert;
	}

	public String getZone_exit_alert() {
		return zone_exit_alert;
	}

	public void setZone_exit_alert(String zone_exit_alert) {
		this.zone_exit_alert = zone_exit_alert;
	}

	public String getZone_description() {
		return zone_description;
	}

	public void setZone_description(String zone_description) {
		this.zone_description = zone_description;
	}

	public Integer getFrequency_minutes() {
		return frequency_minutes;
	}

	public void setFrequency_minutes(Integer frequency_minutes) {
		this.frequency_minutes = frequency_minutes;
	}

	public Date getValid_till() {
		return valid_till;
	}

	public void setValid_till(Date valid_till) {
		this.valid_till = valid_till;
	}
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
