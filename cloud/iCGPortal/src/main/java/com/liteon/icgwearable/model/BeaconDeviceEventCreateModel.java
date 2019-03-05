package com.liteon.icgwearable.model;

public class BeaconDeviceEventCreateModel {
	
	private Integer event_id;
	private String eventoccureddate;
	private String wearable_uuid;
	private String beacon_uuid;
	private String accesskey;
	private Integer school_id;
	private String ips_receiver_mac;
	private Integer ips_receiver_zone_id;
	
	public Integer getIps_receiver_zone_id() {
		return ips_receiver_zone_id;
	}

	public void setIps_receiver_zone_id(Integer ips_receiver_zone_id) {
		this.ips_receiver_zone_id = ips_receiver_zone_id;
	}

	public BeaconDeviceEventCreateModel() {}

	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}

	public String getEventoccureddate() {
		return eventoccureddate;
	}

	public void setEventoccureddate(String eventoccureddate) {
		this.eventoccureddate = eventoccureddate;
	}

	public String getWearable_uuid() {
		return wearable_uuid;
	}

	public void setWearable_uuid(String wearable_uuid) {
		this.wearable_uuid = wearable_uuid;
	}

	public String getBeacon_uuid() {
		return beacon_uuid;
	}

	public void setBeacon_uuid(String beacon_uuid) {
		this.beacon_uuid = beacon_uuid;
	}

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public Integer getSchool_id() {
		return school_id;
	}

	public void setSchool_id(Integer school_id) {
		this.school_id = school_id;
	}

	public String getIps_receiver_mac() {
		return ips_receiver_mac;
	}

	public void setIps_receiver_mac(String ips_receiver_mac) {
		this.ips_receiver_mac = ips_receiver_mac;
	}

	@Override
	public String toString() {
		return "BeaconDeviceEventCreateModel [event_id=" + event_id + ", eventoccureddate=" + eventoccureddate
				+ ", wearable_uuid=" + wearable_uuid + ", beacon_uuid=" + beacon_uuid + ", accesskey=" + accesskey
				+ ", school_id=" + school_id + ", ips_receiver_mac=" + ips_receiver_mac + "]";
	}
}
