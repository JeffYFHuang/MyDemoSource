package com.liteon.icgwearable.model;

public class IPSLoginDataModel {
	
	private String accesskey;
	private String ips_receiver_mac;
	public String getAccesskey() {
		return accesskey;
	}
	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}
	/*public String getIpsReceiverMac() {
		return ips_receiver_mac;
	}
	public void setIpsReceiverMac(String ipsReceiverMac) {
		this.ips_receiver_mac = ipsReceiverMac;
	} */
	
	
	public String getIps_receiver_mac() {
		return ips_receiver_mac;
	}
	public void setIps_receiver_mac(String ips_receiver_mac) {
		this.ips_receiver_mac = ips_receiver_mac;
	}
	@Override
	public String toString() {
		return "IPSLoginDataModel [accesskey=" + accesskey + ", ips_receiver_mac=" + ips_receiver_mac + "]";
	}
	
	

}
