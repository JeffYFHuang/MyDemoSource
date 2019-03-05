package com.liteon.icgwearable.service;

import com.liteon.icgwearable.hibernate.entity.IPSReceiver;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;

public interface IPSReceiverService {
	
	public boolean isIPSReceiverForSchooIDExist(int schoolId);
	
	public boolean createIPSReceiverEntry(IPSReceiver ipsReceiver);
	
	public int[] getIPSReceiverDetailsForMac(String ipsReceiverMac);	
	
	public int updateIPSReceiverBySessionId(int receiver_Id, String sessionId);
	
	public IPSReceiver getIPSReceiverDetailsForID(int receiver_Id);
	
	public boolean updateIPSReceiverEntry(IPSReceiver ipsReceiver);
	
	public boolean deleteIPSReceiverEntry(int receiver_Id);
	
	public int checkMacExistWithAnyReceiver(String ipsReceiverMac);
	
	public IPSReceiver getIPSReceiverEntryForMac(String ipsReceiverMac);

	public int checkSchoolIdExistWithAnyReceiver(int schoolId);
	
	public IPSReceiver getIPSReceiverDetailsForSchoolID(int schoolId);	
	
	public int getSessionValidityInMinutes(String sessionId, int receiver_id);

	public String findIPSReceiverByMacAndSessionId(String sessionId, String macAddress);
	
	public String getMobileNumberBySchoolId(int schoolId);
	
	public IPSReceiverDevice findIPSReceiverDevice(int ipsReceiverId, int ips_receiver_zone_id);

	public IPSReceiver updateIPSReceiverStatus(String ips_receiver_mac, String ips_receiver_status);
}
