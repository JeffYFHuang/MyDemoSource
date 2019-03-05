package com.liteon.icgwearable.model;

public class BeaconDeviceEventsModel {

	private String accesskey;
	private Integer event_id;
	private String startDate;
	private String endDate;
	private Integer zone_id;
	private String ips_receiver_mac;
	private String ips_receiver_status;
	
	public BeaconDeviceEventsModel() {}


	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getZone_id() {
		return zone_id;
	}

	public void setZone_id(Integer zone_id) {
		this.zone_id = zone_id;
	}

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public String getIps_receiver_mac() {
		return ips_receiver_mac;
	}

	public void setIps_receiver_mac(String ips_receiver_mac) {
		this.ips_receiver_mac = ips_receiver_mac;
	}
	
	public String getIps_receiver_status() {
		return ips_receiver_status;
	}


	public void setIps_receiver_status(String ips_receiver_status) {
		this.ips_receiver_status = ips_receiver_status;
	}

	@Override
	public String toString() {
		return "BeaconDeviceEventsModel [accesskey=" + accesskey + ", event_id=" + event_id + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", zone_id=" + zone_id + ", ips_receiver_mac=" + ips_receiver_mac + "]";
	}
}
