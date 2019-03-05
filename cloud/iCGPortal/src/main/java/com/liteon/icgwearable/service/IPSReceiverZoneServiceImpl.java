package com.liteon.icgwearable.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.IPSReceiverZoneDAO;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverZone;
import com.liteon.icgwearable.model.IPSReceiverZoneModel;


@Service("iPSReceiverZoneService")
public class IPSReceiverZoneServiceImpl implements IPSReceiverZoneService{
	  
	 @Autowired
	  private IPSReceiverZoneDAO zoneDao;
	  
	  @Resource(name = "configProperties")
		 private Properties configProperties;
	  
	@Override
	public List<Integer> getAllZoneIDsForReceiverID(int receiver_Id) {
		return this.zoneDao.getAllZoneIDsForReceiverID(receiver_Id);
	}
	@Override
	public boolean deleteAllZonesForReceiverId(int receiver_Id) {
		return this.zoneDao.deleteAllZonesForReceiverId(receiver_Id);
	}
	@Override
	public boolean createIPSReceiverZoneEntry(IPSReceiverZoneModel zm) {
		return this.zoneDao.createIPSReceiverZoneEntry(zm) ;
	}
	@Override
	public IPSReceiverZone getZoneDetailsForZoneId(int zone_Id) {
		return this.zoneDao.getZoneDetailsForZoneId(zone_Id);
	}
	@Override
	public boolean updateIPSReceiverZoneEntry(IPSReceiverZone zm) {
		return this.zoneDao.updateIPSReceiverZoneEntry(zm);
	}
	@Override
	public List<IPSReceiverZone> getAllZoneDetailsForReceiverID(int receiver_Id) {
		return  this.zoneDao.getAllZoneDetailsForReceiverID(receiver_Id);
	}
	@Override
	public IPSReceiverZone getZoneDetailsForZoneName(String zonename,int receiverId) {
		
		return this.zoneDao.getZoneDetailsForZoneName(zonename,receiverId);
	}
	@Override
	public boolean createIPSReceiverZoneEntry(IPSReceiverZone zm) {
		return this.zoneDao.createIPSReceiverZoneEntry(zm);
	}
	
	public void deleteImage(String fileName){
		 if(null != fileName) {
			   try {

				   File f = new File(this.configProperties.getProperty("ipsimages.upload.path") + "/" +fileName);
				    if (f != null)
					     f.delete();
		        } catch (Exception e) {
			         e.printStackTrace();
		        }
		 }
		 
	 }//end of deletImage method
	@Override
	public List<IPSReceiverZone> getAllZoneDetailsForZoneIds(List<Integer> zonelist, int receiver_Id) {		
		return this.zoneDao.getAllZoneDetailsForZoneIds(zonelist,receiver_Id);
	}
	@Override
	public int deleteAllZonesInZoneIdList(List<Integer> ids, int receiver_Id) {
		
		return this.zoneDao.deleteAllZonesInZoneIdList(ids, receiver_Id);
	}
	@Override
	public HashMap<String, Object> getStudentBeaconAlerts(int school_id, String inputdate, int receiver_Id) {
		
		return this.zoneDao.getStudentBeaconAlerts( school_id, inputdate, receiver_Id);
	}

}
