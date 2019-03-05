package com.liteon.icgwearable.service;

import java.util.HashMap;
import java.util.List;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverZone;
import com.liteon.icgwearable.model.IPSReceiverZoneModel;

public interface IPSReceiverZoneService {

	public List<Integer> getAllZoneIDsForReceiverID(int receiver_Id);
	
	 public boolean deleteAllZonesForReceiverId(int receiver_Id);
	 
	 public boolean createIPSReceiverZoneEntry( IPSReceiverZoneModel zm );
	 
	 public boolean createIPSReceiverZoneEntry( IPSReceiverZone zm );
	 
	 public IPSReceiverZone getZoneDetailsForZoneId(int zone_Id);
	 
	 public boolean updateIPSReceiverZoneEntry(IPSReceiverZone zm);
	 
	 public List<IPSReceiverZone> getAllZoneDetailsForReceiverID(int receiver_Id); 
	 
	 public List<IPSReceiverZone> getAllZoneDetailsForZoneIds(List<Integer>zonelist, int receiver_Id); 
	 
	 public IPSReceiverZone getZoneDetailsForZoneName(String zonename,int receiverId);
	 
	 public void deleteImage(String fileName);
	 
	 public int deleteAllZonesInZoneIdList(List<Integer>ids, int receiver_Id);
	 
	 public HashMap<String, Object> getStudentBeaconAlerts(int school_id, String inputdate,int receiver_Id);
	 
}
