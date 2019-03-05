package com.liteon.icgwearable.service;

import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Students;

public interface DeviceStudentsService {

	public DeviceStudents createDeviceStudents(String uuid, Students students, String via) throws Exception;
}
