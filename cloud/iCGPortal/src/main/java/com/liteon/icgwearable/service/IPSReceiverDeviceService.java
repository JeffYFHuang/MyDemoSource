package com.liteon.icgwearable.service;

import java.util.List;

import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;

public interface IPSReceiverDeviceService {
	
	 public List<Integer> getReceiverDeviceIdsForZoneId(List<Integer> zoneIdList);
	 
	 public List<Integer> getReceiverDeviceIds(int receiver_Id, List<Integer> zoneIdList );
	 
	 public int deleteAllReceiverDevicesForIds(List<Integer> ids,int receiver_Id);
	 
	 public List<IPSReceiverDevice> getReceiverDevices(Integer zoneId, int receiver_Id);
	 
	// public List<IPSReceiverDevice> getReceiverDevicesForZoneId(int receiver_Id);
	 
	 public IPSReceiverDevice getReceiverDeviceIdsForDeviceUUID(String devUUID);
	 
	 public boolean updateIPSReceiverDeviceEntry(IPSReceiverDevice ips);
	 
	 public boolean createIPSReceiverDeviceEntry( IPSReceiverDevice rd );
	 

}
