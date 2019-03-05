package com.liteon.icgwearable.dao;

import com.liteon.icgwearable.model.DeviceConfigurationsModel;
import com.liteon.icgwearable.transform.DeviceAnalyticsOutputTransform;

public interface DeviceAnalyticsOutputDAO {
	public Integer getDeviceConfigurationId(String uuid);
	public String getDeviceModel(Integer device_configuration_id);
	public DeviceConfigurationsModel getDeviceConfigurations(int device_configuration_id);
	public DeviceConfigurationsModel getWearableDeviceConfigSettings(int deviceConfigId);
}
