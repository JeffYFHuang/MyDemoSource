package com.liteon.icgwearable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.IPSReceiverDAO;
import com.liteon.icgwearable.hibernate.entity.IPSReceiver;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;

@Service("iPSReceiverService")
public class IPSReceiverServiceImpl implements IPSReceiverService{
	
	 @Autowired
	 private IPSReceiverDAO receiverDao;

	@Override
	public boolean isIPSReceiverForSchooIDExist(int schoolId) {		
		return this.receiverDao.isIPSReceiverForSchooIDExist(schoolId);
	}

	@Override
	public boolean createIPSReceiverEntry(IPSReceiver ipsReceiver) {		
		return this.receiverDao.createIPSReceiverEntry(ipsReceiver);
	}

	@Override
	public int[] getIPSReceiverDetailsForMac(String ipsReceiverMac) {		
		return this.receiverDao.getIPSReceiverDetailsForMac(ipsReceiverMac);
	}

	@Override
	public int updateIPSReceiverBySessionId(int receiver_Id, String sessionId) {
		
		return this.receiverDao.updateIPSReceiverBySessionId(receiver_Id, sessionId);
	}

	@Override
	public IPSReceiver getIPSReceiverDetailsForID(int receiver_Id) {
		// TODO Auto-generated method stub
		return this.receiverDao.getIPSReceiverDetailsForID(receiver_Id);
	}

	@Override
	public boolean updateIPSReceiverEntry(IPSReceiver ipsReceiver) {
		// TODO Auto-generated method stub
		return this.receiverDao.updateIPSReceiverEntry(ipsReceiver);
	}

	@Override
	public boolean deleteIPSReceiverEntry(int receiver_Id) {
		// TODO Auto-generated method stub
		return this.receiverDao.deleteIPSReceiverEntry(receiver_Id);
	}

	@Override
	public int checkMacExistWithAnyReceiver(String ipsReceiverMac) {
		// TODO Auto-generated method stub
		return this.receiverDao.checkMacExistWithAnyReceiver(ipsReceiverMac);
	}

	@Override
	public IPSReceiver getIPSReceiverEntryForMac(String ipsReceiverMac) {
		// TODO Auto-generated method stub
		return this.receiverDao.getIPSReceiverEntryForMac(ipsReceiverMac);
	}


	@Override
	public int checkSchoolIdExistWithAnyReceiver(int schoolId) {
		// TODO Auto-generated method stub
		return this.receiverDao.checkSchoolIdExistWithAnyReceiver(schoolId);
	}

	@Override
	public IPSReceiver getIPSReceiverDetailsForSchoolID(int schoolId) {
		// TODO Auto-generated method stub
		return this.receiverDao.getIPSReceiverDetailsForSchoolID(schoolId);
	}

	@Override
	public int getSessionValidityInMinutes(String sessionId, int receiver_id) {
		// TODO Auto-generated method stub
		return this.receiverDao.getSessionValidityInMinutes(sessionId, receiver_id);
	}


	@Override
	public String findIPSReceiverByMacAndSessionId(String sessionId, String macAddress) {
		// TODO Auto-generated method stub
		return this.receiverDao.findIPSReceiverByMacAndSessionId(sessionId, macAddress);
	}

	@Override
	public String getMobileNumberBySchoolId(int schoolId) {
		// TODO Auto-generated method stub
		return this.receiverDao.getMobileNumberBySchoolId(schoolId);
	}

	@Override
	public IPSReceiverDevice findIPSReceiverDevice(int ipsReceiverId, int ips_receiver_zone_id) {
		// TODO Auto-generated method stub
		return this.receiverDao.findIPSReceiverDevice(ipsReceiverId, ips_receiver_zone_id);
	}

	@Override
	public IPSReceiver updateIPSReceiverStatus(String ips_receiver_mac, String ips_receiver_status) {
		// TODO Auto-generated method stub
		return this.receiverDao.updateIPSReceiverStatus(ips_receiver_mac, ips_receiver_status);
	}
}
