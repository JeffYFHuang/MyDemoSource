package com.liteon.icgwearable.transform;

import java.math.BigInteger;

public class ExternalSystemStatusTransform {

	private String ips_receiver_mac;
	private String ips_receiver_name;
	private String ips_receiver_version;
	private String ips_receiver_status;
	private BigInteger devicesCount;
	private String school_name;
	public String getIps_receiver_mac() {
		return ips_receiver_mac;
	}
	public void setIps_receiver_mac(String ips_receiver_mac) {
		this.ips_receiver_mac = ips_receiver_mac;
	}
	public String getIps_receiver_name() {
		return ips_receiver_name;
	}
	public void setIps_receiver_name(String ips_receiver_name) {
		this.ips_receiver_name = ips_receiver_name;
	}
	public String getIps_receiver_version() {
		return ips_receiver_version;
	}
	public void setIps_receiver_version(String ips_receiver_version) {
		this.ips_receiver_version = ips_receiver_version;
	}
	public String getIps_receiver_status() {
		return ips_receiver_status;
	}
	public void setIps_receiver_status(String ips_receiver_status) {
		this.ips_receiver_status = ips_receiver_status;
	}
	
	public String getSchool_name() {
		return school_name;
	}
	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}
	public BigInteger getDevicesCount() {
		return devicesCount;
	}
	public void setDevicesCount(BigInteger devicesCount) {
		this.devicesCount = devicesCount;
	}
	@Override
	public String toString() {
		return "ExternalSystemStatusTransform [ips_receiver_mac=" + ips_receiver_mac + ", ips_receiver_name="
				+ ips_receiver_name + ", ips_receiver_version=" + ips_receiver_version + ", ips_receiver_status="
				+ ips_receiver_status + ", devicesCount=" + devicesCount + ", school_name=" + school_name + "]";
	}
}
