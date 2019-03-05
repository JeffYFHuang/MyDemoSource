package com.liteon.icgwearable.dao;

import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Students;

public interface DeviceStudentsDAO {

	public DeviceStudents createDeviceStudents (String uuid, Students students,String via) throws Exception;
}
