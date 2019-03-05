package com.liteon.icgwearable.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liteon.icgwearable.dao.IPSReceiverDeviceDAO;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;

@Service("iPSReceiverDeviceService")
public class IPSReceiverDeviceServiceImpl implements IPSReceiverDeviceService{

	 @Autowired
	  private IPSReceiverDeviceDAO rdeviceDao;
	  
	@Override
	public List<Integer> getReceiverDeviceIdsForZoneId(List<Integer> zoneIdList) {
		return this.rdeviceDao.getReceiverDeviceIdsForZoneId(zoneIdList);
	}

	@Override
	public List<Integer> getReceiverDeviceIds(int receiver_Id, List<Integer> zoneIdList ) {
		return this.rdeviceDao. getReceiverDeviceIds(receiver_Id,zoneIdList);
	}

	@Override
	public int deleteAllReceiverDevicesForIds(List<Integer> ids,int receiver_Id) {
		return this.rdeviceDao.deleteAllReceiverDevicesForIds(ids,receiver_Id);
	}

	@Override
	public List<IPSReceiverDevice> getReceiverDevices(Integer zoneId, int receiver_Id) {
		return this.rdeviceDao.getReceiverDevices(zoneId,receiver_Id);
	}

	@Override
	public IPSReceiverDevice getReceiverDeviceIdsForDeviceUUID(String devUUID) {
		return this.rdeviceDao.getReceiverDeviceIdsForDeviceUUID(devUUID);
	}

	@Override
	public boolean updateIPSReceiverDeviceEntry(IPSReceiverDevice ips) {
		return this.rdeviceDao.updateIPSReceiverDeviceEntry(ips);
	}

	@Override
	public boolean createIPSReceiverDeviceEntry(IPSReceiverDevice rd) {
		// TODO Auto-generated method stub
		return this.rdeviceDao.createIPSReceiverDeviceEntry(rd);
	}

}
