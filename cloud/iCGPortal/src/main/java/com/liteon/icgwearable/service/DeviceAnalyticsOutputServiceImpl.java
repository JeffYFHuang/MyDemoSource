package com.liteon.icgwearable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.liteon.icgwearable.dao.DeviceAnalyticsOutputDAO;
import com.liteon.icgwearable.model.DeviceConfigurationsModel;
import com.liteon.icgwearable.transform.DeviceAnalyticsOutputTransform;

@Service("deviceAnalyticsOutputService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DeviceAnalyticsOutputServiceImpl implements DeviceAnalyticsOutputService {

	@Autowired
	private DeviceAnalyticsOutputDAO deviceAnalyticsOutputDAO;

	@Override
	public Integer getDeviceConfigurationId(String uuid) {
		// TODO Auto-generated method stub
		return this.deviceAnalyticsOutputDAO.getDeviceConfigurationId(uuid);
	}

	@Override
	public String getDeviceModel(Integer device_configuration_id) {
		// TODO Auto-generated method stub
		return this.deviceAnalyticsOutputDAO.getDeviceModel(device_configuration_id);
	}

	@Override
	public DeviceConfigurationsModel getDeviceConfigurations(int device_configuration_id) {
		// TODO Auto-generated method stub
		return this.deviceAnalyticsOutputDAO.getDeviceConfigurations(device_configuration_id);
	}
	
	@Override
	public DeviceConfigurationsModel getWearableDeviceConfigSettings(int deviceConfigId) {
		return this.deviceAnalyticsOutputDAO.getWearableDeviceConfigSettings(deviceConfigId);
	}
}
