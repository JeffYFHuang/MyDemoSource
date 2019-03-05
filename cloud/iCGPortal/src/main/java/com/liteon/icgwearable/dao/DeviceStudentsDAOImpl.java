package com.liteon.icgwearable.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.exception.RecordAlreadyExistsException;
import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Students;

@Repository("deviceStudentsDAO")
public class DeviceStudentsDAOImpl implements DeviceStudentsDAO {

	private static Logger log = Logger.getLogger(DeviceStudentsDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public DeviceStudents createDeviceStudents(String uuid, Students students, String via) throws Exception {
		Session session = null;
		Transaction tx = null;
		DeviceStudents ds = null, deviceForStudent = null;
		Devices d = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			Criteria criteria = session.createCriteria(DeviceStudents.class);
			criteria.add(Restrictions.eq("deviceuuid", uuid));
			criteria.add(Restrictions.eq("status", "active"));
			ds = (DeviceStudents) criteria.uniqueResult();

			Criteria studentsCriteria = session.createCriteria(DeviceStudents.class, "ds");
			studentsCriteria.createAlias("ds.students", "students");
			studentsCriteria.add(Restrictions.eq("students.studentId", students.getStudentId()));
			studentsCriteria.add(Restrictions.eq("status", "active"));
			deviceForStudent = (DeviceStudents) studentsCriteria.uniqueResult();

			if (via.equals("Web")) {
				if (ds != null) {
					if (ds.getStatus().equals("active") && ds.getStudents().getStudentId() != students.getStudentId()) {
						ds = null;
						throw new RecordAlreadyExistsException("Device Already Mapped To Another Student");
					}
				} else {
					ds = new DeviceStudents();
					ds.setDeviceuuid(uuid);
					ds.setStatus("active");
					ds.setStudents(students);
					ds.setStartDate(new java.util.Date());
					session.save(ds);
				}

			} else if (via.equals("Upload")) {
				log.info("Device Stnds Upload -1");
				if (ds != null) {
					log.info("ds.getStudents().getStudentId()" + "\t" + ds.getStudents().getStudentId());
					log.info("students.getStudentId()" + "\t" + students.getStudentId());
					if (ds.getStatus().equals("active")
							&& !(ds.getStudents().getStudentId().equals(students.getStudentId()))) {
						log.info("Into createDeviceStudents() Upload Check with status as active");
						ds = null;
						return ds;
					}
				}
				log.info("Device Stnds Upload -2");
				if (ds != null && null != deviceForStudent && deviceForStudent.getDeviceuuid() == null) {
					log.info("***1*****");
					session.save(ds);
				} else if (ds != null && (null != deviceForStudent && deviceForStudent.getDeviceuuid() != null)
						&& (!ds.getDeviceuuid().equals(deviceForStudent.getDeviceuuid()))) {
					log.info("***2*****");
					deviceForStudent.setStatus("inactive");
					deviceForStudent.setEndDate(new java.util.Date());
					session.update(deviceForStudent);
					session.save(ds);
					log.info("***3*****");
				} else if (ds == null && (null != deviceForStudent && deviceForStudent.getDeviceuuid() != null
						&& deviceForStudent.getStatus().equals("active"))) {
					log.info("***4*****");
					deviceForStudent.setStatus("inactive");
					deviceForStudent.setEndDate(new java.util.Date());
					session.update(deviceForStudent);

					log.info("***Checking for Device*****");
					log.info("deviceForStudent.getDeviceuuid()" + "\t" + deviceForStudent.getDeviceuuid());
					Criteria criteriaDevice = session.createCriteria(Devices.class);
					criteriaDevice.add(Restrictions.eq("uuid", deviceForStudent.getDeviceuuid()));
					Devices d1 = (Devices) criteriaDevice.uniqueResult();
					d1.setStatus("");
					session.update(d1);
					log.info("End updating device");

					// create a new record
					ds = new DeviceStudents();
					ds.setDeviceuuid(uuid);
					ds.setStatus("active");
					ds.setStudents(students);
					ds.setStartDate(new java.util.Date());
					session.save(ds);
					log.info("Assigning new device to active ::::");
					log.info("***5*****");
				} else if (null != ds && null != deviceForStudent && (ds.getDeviceuuid()
						.equals(uuid) /* && (ds.getStatus().equals("active")) */)) {
					log.info("***6*****");
					session.update(ds);
					log.info("***7*****");
				} else {
					log.info("***8*****");
					ds = new DeviceStudents();
					ds.setDeviceuuid(uuid);
					ds.setStatus("active");
					ds.setStudents(students);
					ds.setStartDate(new java.util.Date());
					session.save(ds);
					log.info("***9*****");
				}
				log.info("Device Stnds Upload -3");
			}

			Criteria deviceCriteria = session.createCriteria(Devices.class);
			deviceCriteria.add(Restrictions.eq("uuid", uuid));
			d = (Devices) deviceCriteria.uniqueResult();
			d.setStatus("assigned");
			session.update(d);

			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in DeviceStudentsDAOImpl()" + e);
			if (null != ds)
				session.evict(ds);
			tx.rollback();
		}
		return ds;
	}
}
