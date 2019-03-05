package com.liteon.icgwearable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.SystemConfigurationDAO;

@Service("systemConfigurationService")
public class SystemConfigurationServiceImpl implements SystemConfigurationService {
	
	@Autowired
    private SystemConfigurationDAO systemConfigurationDAO;

	@Override
	public Integer[] findSystemConfigurationParameters(int id) {
		// TODO Auto-generated method stub
		return this.systemConfigurationDAO.findSystemConfigurationParameters(id);
	}

}
