package com.liteon.icgwearable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liteon.icgwearable.dao.DeviceStudentsDAO;
import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Students;

@Service("deviceStudentsService")
public class DeviceStudentsServiceImpl implements DeviceStudentsService {

	@Autowired
	private DeviceStudentsDAO deviceStudentsDAO;
	@Override
	public DeviceStudents createDeviceStudents(String uuid, Students students, String via) throws Exception{
		// TODO Auto-generated method stub
		return this.deviceStudentsDAO.createDeviceStudents(uuid, students, via);
	}

}
