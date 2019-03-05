package com.liteon.icgwearable.dao;

import java.util.HashMap;
import java.util.List;

import com.liteon.icgwearable.hibernate.entity.IPSReceiverZone;
import com.liteon.icgwearable.model.IPSReceiverZoneModel;

public interface IPSReceiverZoneDAO {

	public List<Integer> getAllZoneIDsForReceiverID(int receiver_Id);

	public boolean deleteAllZonesForReceiverId(int receiver_Id);
	
	public boolean createIPSReceiverZoneEntry(IPSReceiverZoneModel zm);
	
	 public boolean createIPSReceiverZoneEntry( IPSReceiverZone zm );
	
	public IPSReceiverZone getZoneDetailsForZoneId(int zone_Id);
	
	public List<IPSReceiverZone> getAllZoneDetailsForZoneIds(List<Integer>zonelist, int receiver_Id); 
	
	public boolean updateIPSReceiverZoneEntry(IPSReceiverZone zm);
	
	public List<IPSReceiverZone> getAllZoneDetailsForReceiverID(int receiver_Id);
	
	public IPSReceiverZone getZoneDetailsForZoneName(String zonename,int receiverId);
	
	 public int deleteAllZonesInZoneIdList(List<Integer>ids, int receiver_Id);
	 
	 public HashMap<String, Object> getStudentBeaconAlerts(int school_id, String inputdate,int receiver_Id);

}
