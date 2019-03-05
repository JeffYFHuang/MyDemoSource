package com.liteon.icgwearable.dao;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.hibernate.entity.DeviceConfigurations;
import com.liteon.icgwearable.hibernate.entity.DeviceEvents;
import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Geozones;
import com.liteon.icgwearable.hibernate.entity.PetDetails;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.BeaconDeviceEventCreateModel;
import com.liteon.icgwearable.model.DeviceModel;
import com.liteon.icgwearable.transform.DeviceConfigurationsTransform;
import com.liteon.icgwearable.transform.DeviceStudentsTransform;
import com.liteon.icgwearable.transform.NotifyToTeacherAndStaffTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.SchoolInOutTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.StringUtility;

@Repository("deviceDAO")
@Transactional
public class DeviceDAOImpl implements DeviceDAO {

	private static Logger log = Logger.getLogger(DeviceDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	protected SystemConfigurationDAO systemConfigurationDAO;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public boolean createDeviceEvent(DeviceModel deviceModel, int device_id, SupportedEvents events, String uuid,
			Geozones geoZone) {
		boolean insertSuccessful = false;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			DeviceEvents deviceEvents = new DeviceEvents();
			deviceEvents.setEvents(events);
			deviceEvents.setUuid(uuid);
			deviceEvents.setGpsdatacode(deviceModel.getGpsdatacode());
			if (deviceModel.getGpslocationdata() != null && !deviceModel.getGpslocationdata().trim().equals("")) {
				if (StringUtility.validateLatLong(deviceModel.getGpslocationdata()))
					deviceEvents.setGpslocationdata(deviceModel.getGpslocationdata());
			} else {
				deviceEvents.setGpslocationdata("");
			}
			deviceEvents.setSensortypecode(deviceModel.getSensortypecode());
			deviceEvents.setSensorerrorcode(deviceModel.getSensorerrorcode());
			deviceEvents.setVitalsigntype(deviceModel.getVitalsigntype());
			deviceEvents.setVitalsignvalue(deviceModel.getVitalsignvalue());
			deviceEvents.setAbnormal_code(deviceModel.getAbnormalcode());
			deviceEvents.setBatterylevelvalue(deviceModel.getBatterylevelvalue());
			deviceEvents.setEventoccureddate(
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(deviceModel.getEventoccureddate()));
			if (null != geoZone) {
				deviceEvents.setGeozones(geoZone);
			}
			session.save(deviceEvents);
			session.getTransaction().commit();

			session.close();
			log.info("DeviceDAOImpl.createDeviceEvent() ");
			insertSuccessful = true;
		} catch (HibernateException he) {
			log.debug("HibernateException : Insertion Failed - " + he);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return insertSuccessful;
	}

	@Override
	public boolean addEntryToDeviceEventQueue(int queueId, int userId, int eventId, int device_id,
			SupportedEvents events, String androidAppToken, String iPhoneAppToken) {
		boolean insertSuccessful = false;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			String SQLINSERT_QUERY = "INSERT INTO device_events_queue(queue_id,device_event_id, user_id, queue_sent, queue_sent_date, "
					+ "created_date, isEliminated, android_app_token, iphone_app_token) "
					+ "VALUES (?, ?, ?, ?, null, now(), ?," + "'" + androidAppToken + "'" + "," + "'" + iPhoneAppToken + "'" + ")";

			Query query2 = session.createSQLQuery(SQLINSERT_QUERY);
			query2.setParameter(0, queueId);
			query2.setParameter(1, eventId);
			query2.setParameter(2, userId);
			query2.setParameter(3, 0);
			query2.setParameter(4, "n");

			int insertSuccess = query2.executeUpdate();
			if (insertSuccess == 0 || insertSuccess == 1) {
				log.info("insertion sucessful ! ");
			}
			tx.commit();
			log.info("DeviceDAOImpl.addEntryToDeviceEventQueue() ");
			insertSuccessful = true;
		} catch (HibernateException he) {
			log.debug("HibernateException : Insertion Failed - " + he);
		} finally {
			session.close();
		}
		return insertSuccessful;
	}

	public Devices checkDeviceIdExist(int device_id) {
		return (Devices) sessionFactory.getCurrentSession().get(Devices.class, device_id);
	}

	@Override
	public boolean checkEventIdAndDeviceUuid(int event_id, String deviceUuid, int device_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean eIdAndDUidFound = false;
		String SQL_QUERY = "select e.eventId, d.uuid from SupportedEvents e, Devices d where e.eventId = ? and d.uuid = ? and d.deviceId = ?";
		Query query = session.createQuery(SQL_QUERY);
		query.setParameter(0, event_id);
		query.setParameter(1, deviceUuid);
		query.setParameter(2, device_id);

		List list = query.list();

		if ((list != null) && (list.size() > 0)) {
			eIdAndDUidFound = true;
		}
		tx.commit();
		session.close();
		return eIdAndDUidFound;
	}

	@Override
	public SupportedEvents getEventsObjUsingId(int event_id) {
		return (SupportedEvents) sessionFactory.getCurrentSession().get(SupportedEvents.class, event_id);
	}

	@Override
	public List<DeviceConfigurationsTransform> getDeviceConfigurations(int deviceId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Map<String, String> configMap = new LinkedHashMap<>();

		List<DeviceConfigurationsTransform> deviceConfigList;
		String deviceConfigQuery = "select dc.config_name as configName, dc.config_value as configValue "
				+ "from device_configurations as dc, devices d " + "where d.device_id= ? "
				+ "and dc.device_id = d.device_id ";

		Query query = session.createSQLQuery(deviceConfigQuery).addScalar("configName").addScalar("configValue")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceConfigurationsTransform.class));
		query.setParameter(0, deviceId);

		deviceConfigList = query.list();
		tx.commit();
		session.close();
		return deviceConfigList;
	}

	@Override
	public boolean createDeviceEvent() {
		return false;
	}

	@Override
	public boolean deviceExists(int uuid) {
		boolean flag = false;

		return flag;
	}

	@Override
	public Devices checkDeviceIdExist(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		Devices devices = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();
		return devices;
	}

	@Override
	public void createDevice(Devices devices) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(devices);
		tx.commit();
		session.close();
	}

	@Override
	public void updateDevice(Devices devices) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.update(devices);
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in updateDevice()" + "\t" + e);
			if (null != devices)
				session.evict(devices);
			tx.rollback();
		} finally {
			session.close();
		}
	}

	@Override
	public boolean updateDeviceEventQueue(int queue_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session
				.createSQLQuery("update device_events_queue set isEliminated =:isEliminated where queue_id=:queue_id ");
		query.setParameter("isEliminated", "y");
		query.setParameter("queue_id", queue_id);
		int result = query.executeUpdate();
		log.info("Result " + result);
		tx.commit();
		session.close();
		return true;
	}

	@Override
	public int getDeviceIdByUuidandUser(Users user, String uuid) {
		List<Integer> deviceList;
		String deviceUuidQuery = null;
		deviceUuidQuery = "SELECT d.device_id as deviceId " + "FROM devices d "
				+ "LEFT JOIN device_students ds on ds.device_uuid = d.uuid "
				+ "LEFT JOIN students s on s.student_id = ds.student_id "
				+ "LEFT JOIN parent_kids pk on pk.student_id = s.student_id "
				+ "LEFT JOIN class_grade cg on cg.class_grade_id = s.class_grade_id "
				+ "LEFT JOIN users u on u.account_id = cg.school_id and u.user_id = pk.user_id "
				+ "WHERE pk.user_id =:userId " + "and ds.device_uuid =:uuid  and ds.status='active' ";

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery(deviceUuidQuery);
		query.setParameter("userId", user.getId());
		query.setParameter("uuid", uuid);
		log.info("deviceUuidQuery" + query);
		deviceList = (List<Integer>) query.list();
		tx.commit();
		session.close();
		if (deviceList.size() > 0) {
			log.info("retruned value" + deviceList.get(0));
			return deviceList.get(0);
		} else
			return 0;
	}

	@Override
	public List<String> findDevicesByStudentId(int studentId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<String> uuidList = null;

		String devicesByStudentsQuery = "select d.uuid as uuid from devices d "
				+ "left join device_students ds on ds.device_uuid = d.uuid " + "where ds.student_id = ? ";
		Query query = session.createSQLQuery(devicesByStudentsQuery);
		query.setParameter(0, studentId);
		uuidList = query.list();
		tx.commit();
		session.close();
		return uuidList;
	}

	public Devices findDeviceByStudentId(int studentId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Devices.class, "d");
		criteria.createAlias("d.students", "ds");
		criteria.add(Restrictions.eq("ds.studentId", studentId));
		Devices devices = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();
		return devices;
	}

	@Override
	public int findDeviceIdByUUID(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Integer deviceId = null;
		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		Devices devices = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();
		if (null != devices) {
			deviceId = (Integer) devices.getDeviceId();
		}
		if (null != deviceId)
			return deviceId;
		else
			return 0;
	}

	@Override
	public void updateDeviceBySessionId(int deviceId, String sessionId) {
		Integer[] configParams = this.systemConfigurationDAO.findSystemConfigurationParameters(1);
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		Timestamp updatedAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
		updatedAt.setTime(updatedAt.getTime() + (((configParams[2] * 60) + configParams[2]) * 1000));
		Query query = session.createQuery(
				"update Devices as u set u.session_id=:sessionId ,u.session_expiry =:sessionExpiry where u.deviceId=:deviceId");
		query.setParameter("sessionId", sessionId);
		query.setParameter("sessionExpiry", updatedAt);
		query.setParameter("deviceId", deviceId);
		int result = query.executeUpdate();
		log.info("Result" + result);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public Devices findDevicesByUUIDAndSessionId(String uuid, String sessionId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		criteria.add(Restrictions.eq("session_id", sessionId));
		Devices devices = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();

		return devices;
	}

	@Override
	public Devices getDevicesBySessionId(String sessionId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("session_id", sessionId));
		Devices devices = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();
		return devices;
	}

	@Override
	public String findDevicesByUUIDAndCheckSessionValidity(String uuid, String sessionId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria sessionCriteria = session.createCriteria(Devices.class);
		sessionCriteria.add(Restrictions.eq("session_id", sessionId));
		Devices sessionDevices = (Devices) sessionCriteria.uniqueResult();

		if (sessionDevices == null) {
			return "Token Does Not Exist";
		}

		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		criteria.add(Restrictions.eq("session_id", sessionId));
		Devices devices = (Devices) criteria.uniqueResult();

		if (devices == null) {
			return "UUID,Token Mapping Does Not Exist";
		}

		String sessionValidityResult = CommonUtil.checkSessionValidity(devices);

		if (sessionValidityResult.equals("NOTVALID")) {
			return "NOTVALID";
		}

		tx.commit();
		session.close();
		return "success";
	}

	@Override
	public String getEventNamebyId(int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String eventNameQuery = "select event_name from supported_events where event_id=:event_id";
		List<String> eventName = null;
		Query query = session.createSQLQuery(eventNameQuery);
		query.setParameter("event_id", event_id);
		eventName = (List<String>) query.list();
		session.close();
		return eventName.get(0);
	}

	@Override
	public int findSchoolIdByUUID(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		StringBuilder sessionQuery = new StringBuilder();
		sessionQuery.append("select devices.school_id from device_students d "
				+ "left join students s on s.student_id=d.student_id "
				+ "left join devices on devices.uuid=d.device_uuid "
				+ "where d.device_uuid=:uuid AND d.status = 'active' AND devices.status='assigned' ");
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery.toString());
		query.setParameter("uuid", uuid);
		sessions = (List<Integer>) query.list();
		session.close();
		if (null != sessions && sessions.size() > 0 && null != sessions.get(0))
			return sessions.get(0);
		else
			return -1;
	}

	@Override
	public String getSessionIdbyuuid(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select session_id from devices where uuid=:uuid";
		List<String> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("uuid", uuid);
		sessions = (List<String>) query.list();
		session.close();
		if (null != sessions.get(0) && sessions.get(0).trim().length() > 0)
			return sessions.get(0);
		else
			return "";
	}

	@Override
	public List<String> findUsersByDevice(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String usersRoleQuery = "select u.role_type from devices d "
				+ "left join students s on s.student_id=d.student_id "
				+ "left join users u on u.account_id=s.account_id " + "where d.uuid=:uuid ";
		List<String> userRole = null;
		Query query = session.createSQLQuery(usersRoleQuery);
		query.setParameter("uuid", uuid);
		userRole = (List<String>) query.list();
		session.close();
		return userRole;
	}

	@Override
	public String supportedFieldsUsingEventIdAndDeviceGenerated(int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String usersRoleQuery = "select supported_fields from supported_events where event_id = ? ";
		Query query = session.createSQLQuery(usersRoleQuery);
		query.setParameter(0, event_id);
		String supportedFields = (String) query.uniqueResult();
		session.close();
		return supportedFields;
	}

	@Override
	public int getlatestId() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT MAX(queue_id) FROM device_events_queue";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		sessions = (List<Integer>) query.list();
		session.close();
		if (null != sessions.get(0))
			return sessions.get(0);
		else
			return 0;
	}

	@Override
	public int getSessionValidityInMinutes(String sessionId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("session_id", sessionId));
		Devices devices = (Devices) criteria.uniqueResult();

		Timestamp ts = devices.getSession_expiry();
		Time ts1 = null;
		String tsQuery = "select TIMEDIFF(" + "'" + ts + "'" + ",NOW()) ";
		Query query = session.createSQLQuery(tsQuery);
		ts1 = (Time) query.uniqueResult();
		log.info("ts1.getMinutes()" + "\t" + ts1.getMinutes());
		tx.commit();
		session.close();
		return ts1.getMinutes();
	}

	@Override
	public RewardsListTransform findRewardsByDeviceUuid(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		RewardsListTransform rLT = null;

		String rewardsByUUIDQuery = "SELECT rewardCount, rank FROM "
				+ "(SELECT student_id, rewardCount, CASE WHEN @l=rewardCount THEN @r ELSE @r\\:=@r+1 END AS rank, "
				+ "@l\\:=rewardCount FROM ( SELECT student_id, SUM(received_count) AS rewardCount FROM `rewards_students` "
				+ "WHERE teacher_id = ( SELECT class_grade.teacher_id FROM students, class_grade "
				+ "WHERE class_grade.class_grade_id = students.class_grade_id AND students.student_id = "
				+ "(SELECT student_id FROM device_students WHERE device_uuid = ? "
				+ "AND status='active') ) GROUP BY student_id ORDER BY rewardCount DESC ) totals, "
				+ "(SELECT @r\\:=0, @l\\:=NULL) rank) AS ranks WHERE student_id = "
				+ "(SELECT student_id FROM device_students WHERE device_uuid = ? " + "AND status='active' LIMIT 1)";

		Query query = session.createSQLQuery(rewardsByUUIDQuery);
		query.setParameter(0, uuid);
		query.setParameter(1, uuid);

		List<Object[]> row = query.list();
		for (Object[] list : row) {
			rLT = new RewardsListTransform();
			try {
				if (null != list[0])
					rLT.setRewardCount(Integer.parseInt(list[0].toString()));
				if (null != list[1])
					rLT.setRank(Integer.parseInt(list[1].toString()));
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			} catch (Exception pe) {
				pe.printStackTrace();
			}
		}
		tx.commit();
		session.close();
		return rLT;
	}

	@Override
	public List<Integer> getDeviceEventQueueIds() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select queue_id from device_events_queue";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		sessions = (List<Integer>) query.list();
		session.close();
		if (sessions.size() > 0)
			return sessions;
		else
			return null;
	}

	@Override
	public int getlatestDeviceEventId() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT MAX(device_event_id) FROM device_events";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		sessions = (List<Integer>) query.list();
		session.close();
		if (null != sessions && sessions.size() > 0)
			return sessions.get(0);
		else
			return 0;
	}

	@Override
	public String findDeviceUUIDByStudentId(int studentId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria sessionCriteria = session.createCriteria(DeviceStudents.class, "ds");
		sessionCriteria.createAlias("ds.students", "students");
		sessionCriteria.add(Restrictions.eq("students.studentId", studentId));
		DeviceStudents deviceSt = (DeviceStudents) sessionCriteria.uniqueResult();
		tx.commit();
		session.close();

		return deviceSt.getDeviceuuid();
	}

	@Override
	public NotifyToTeacherAndStaffTransform getEventToNotifyToTeacherAndStaff(int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<NotifyToTeacherAndStaffTransform> notifyList = null;
		String notifyQuery = "select se.notify_staff as notify_staff, se.notify_teacher as notify_teacher from supported_events se where se.event_id =:event_id ";

		Query query = session.createSQLQuery(notifyQuery).addScalar("notify_staff").addScalar("notify_teacher")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(NotifyToTeacherAndStaffTransform.class));
		query.setParameter("event_id", event_id);

		notifyList = query.list();
		tx.commit();
		session.close();
		if (notifyList.size() > 0 && null != notifyList.get(0))
			return notifyList.get(0);
		else
			return null;
	}

	@Override
	public int getSchoolIdtoSendNotificationtoStaff(String time, String date, int school_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = null;
		
		sessionQuery = "SELECT sc.school_id FROM `school_calendar` AS sc "
		                + "WHERE sc.school_id=? AND ? BETWEEN date_close "
		                + "AND IF(date_reopen IS NULL, date_close, date_reopen) ";

		List<Integer> queryList = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter(0, school_id);
        query.setParameter(1, date);
        
		queryList = (List<Integer>) query.list();
		
		if (queryList.size() > 0 && null != queryList.get(0)){
			//Event occurred during school holiday, no need to send notification
			tx.commit();
			session.close();
			return 0;
		} else {
			//Event occurred during non school holiday, need to check if school hours
			sessionQuery = "SELECT sd.school_id FROM school_details AS sd WHERE "
	                + "(CAST(? AS time) >= sd.`school_in_start` AND CAST(? AS time) <= sd.`school_out_end` "
	                + "AND sd.school_id=?) ";
			List<Integer> list = null;
			Query qry = session.createSQLQuery(sessionQuery);
			qry.setParameter(0, time);
			qry.setParameter(1, time);
			qry.setParameter(2, school_id);
			
			list = (List<Integer>) qry.list();
			
			if (list.size() > 0 && null != list.get(0)){
				//Event occurred during school hours, need to send notification
				tx.commit();
				session.close();
				return 1;
			}
			
			tx.commit();
			session.close();
			return 0; //Either non-school hours or holiday, no Push Notification to be sent
		}
	}

	@Override
	public boolean checkDeviceAlreadyPairedWithUser(String uuid, int user_id) {
		Session session = sessionFactory.openSession();
		boolean deviceAlreadyPair = false;

		String sessionQuery = "select parent_kid_id from parent_kids pk \r\n"
				+ "LEFT JOIN device_students ds ON ds.student_id = pk.student_id  AND ds.status = 'active' \r\n"
				+ "where pk.user_id =:user_id and ds.device_uuid =:uuid";
		List<Integer> devicePairList = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("user_id", user_id);
		query.setParameter("uuid", uuid);

		devicePairList = (List<Integer>) query.list();
		session.close();
		log.info("devicePairList.size()" + "\t" + devicePairList.size());
		if (devicePairList.size() > 0 && null != devicePairList.get(0))
			deviceAlreadyPair = true;

		return deviceAlreadyPair;
	}

	@Override
	public List<DeviceStudentsTransform> findDeviceStudentsForStudentActivity(int studentId, String startDate,
			String endDate) {
		log.debug("inside findDeviceStudentsForStudentActivity ::: studentId >>> " + studentId);
		Session session = null;
		Transaction tx = null;
		List<DeviceStudentsTransform> deviceStudentsTransformList = null;
		StringBuilder deviceStudentsBldr = null;
		String deviceStudentsQry = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			deviceStudentsBldr = new StringBuilder();
			deviceStudentsBldr.append("SELECT ds.status as status, ds.device_uuid as uuid, IF(ds.start_date < " + "'"
					+ startDate + "'" + "," + "'" + startDate + "'" + ", ds.start_date) as new_start_date, \r\n"
					+ "IF(ds.end_date IS NULL, IF(" + "'" + endDate + "'" + "<NOW()," + "'" + endDate + "'"
					+ ",NOW()), IF(ds.end_date > " + "'" + endDate + "'" + "," + "'" + endDate + "'"
					+ ", ds.end_date)) as new_end_date\r\n" + "FROM device_students AS ds WHERE ds.student_id = ?\r\n"
					+ "HAVING (new_start_date BETWEEN " + "'" + startDate + "'" + " AND " + "'" + endDate + "'"
					+ " OR new_end_date BETWEEN " + "'" + startDate + "'" + " AND " + "'" + endDate + "')"
					+ " AND new_start_date <= new_end_date");
			deviceStudentsQry = deviceStudentsBldr.toString();

			Query query = session.createSQLQuery(deviceStudentsQry).addScalar("status").addScalar("uuid")
					.addScalar("new_start_date").addScalar("new_end_date")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(DeviceStudentsTransform.class));
			query.setParameter(0, studentId);
			deviceStudentsTransformList = (List<DeviceStudentsTransform>) query.list();
			tx.commit();
		} catch (Exception e) {
			log.error("Exception Occured in findDeviceStudentsForStudentActivity ()" + "\t" + e);
			tx.rollback();
		} finally {
			session.close();
		}
		return deviceStudentsTransformList;
	}

	@Override
	public String getParentUnsunsribeEvent(int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT parent_unsubscribe FROM supported_events se "
				+ "WHERE notify_parent='yes' AND event_id =:event_id ";
		// Removed AND generated_by = 'device' check
		String sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("event_id", event_id);
		if (query.list().size() > 0 && null != query.list().get(0))
			sessions = (String) query.list().get(0);

		tx.commit();
		session.close();
		return sessions;
	}

	@Override
	public int updateSchoolEntryOrExitforSchoolEvent(int device_event_id, String event_occured_time, String iSAbnormal,
			int event_id) {
		int rowsUpdated = 0;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = null;
		if (event_id == 1 && iSAbnormal.toUpperCase().equals("YES"))
			sessionQuery = "update device_events de set de.in_time =:in_time , de.is_entry_exit_abnormal =:is_abnormal where device_event_id =:device_event_id ";
		else if (event_id == 1 && iSAbnormal.toUpperCase().equals("NO"))
			sessionQuery = "update device_events de set de.in_time =:in_time  where device_event_id =:device_event_id ";
		else if (event_id == 2 && iSAbnormal.toUpperCase().equals("YES"))
			sessionQuery = "update device_events de set de.out_time =:out_time , de.is_entry_exit_abnormal =:is_abnormal where device_event_id =:device_event_id ";
		else if (event_id == 2 && iSAbnormal.toUpperCase().equals("NO"))
			sessionQuery = "update device_events de set de.out_time =:out_time  where device_event_id =:device_event_id";
		else if (event_id == 13)
			sessionQuery = "update device_events de set  de.is_entry_exit_abnormal =:is_abnormal where device_event_id =:device_event_id ";

		if (null != sessionQuery) {

			Query query = session.createSQLQuery(sessionQuery);
			if (event_id == 1 && iSAbnormal.toUpperCase().equals("YES")) {
				query.setParameter("in_time", event_occured_time);
				query.setParameter("is_abnormal", iSAbnormal);
			} else if (event_id == 1 && iSAbnormal.toUpperCase().equals("NO")) {
				query.setParameter("in_time", event_occured_time);
			} else if (event_id == 2 && iSAbnormal.toUpperCase().equals("YES")) {
				query.setParameter("out_time", event_occured_time);
				query.setParameter("is_abnormal", iSAbnormal);
			} else if (event_id == 2 && iSAbnormal.toUpperCase().equals("NO")) {
				query.setParameter("out_time", event_occured_time);
			} else if (event_id == 13) {
				query.setParameter("is_abnormal", iSAbnormal);
			}

			query.setParameter("device_event_id", device_event_id);
			rowsUpdated = query.executeUpdate();
			tx.commit();
			session.close();
		}
		return rowsUpdated;
	}

	@Override
	public SchoolInOutTransform getschoolInOuttimings(int school_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		SchoolInOutTransform schoolTimeList = null;

		String deviceStudentsQry = "select sd.school_in_start as school_in_start ,sd.school_in_end as school_in_end,sd.school_out_start as school_out_start ,sd.school_out_end as school_out_end from school_details sd where sd.school_id =:school_id ";

		Query query = session.createSQLQuery(deviceStudentsQry).addScalar("school_in_start").addScalar("school_in_end")
				.addScalar("school_out_start").addScalar("school_out_end")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SchoolInOutTransform.class));
		query.setParameter("school_id", school_id);

		schoolTimeList = (SchoolInOutTransform) query.uniqueResult();
		tx.commit();
		return schoolTimeList;
	}

	@Override
	public int updateGeoFenceEntryExitforSchoolEvent(int device_event_id, String event_occured_time, int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = null;
		if (event_id == 3)
			sessionQuery = "update device_events de set de.in_time =:in_time , de.is_entry_exit_abnormal ='yes' where device_event_id =:device_event_id ";
		if (event_id == 4)
			sessionQuery = "update device_events de set de.out_time =:out_time , de.is_entry_exit_abnormal ='yes' where device_event_id =:device_event_id ";

		Query query = session.createSQLQuery(sessionQuery);
		if (event_id == 3) {
			query.setParameter("in_time", event_occured_time);
		} else if (event_id == 4) {
			query.setParameter("out_time", event_occured_time);
		}

		query.setParameter("device_event_id", device_event_id);
		int rowsUpdated = query.executeUpdate();
		tx.commit();
		session.close();
		return rowsUpdated;
	}

	@Override
	public boolean findDeviceActiveOrInactive(String uuid, int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean deviceStatus = false;
		String deviceActiveInactiveStatusQry = "select ds.status as status from device_students ds \r\n"
				+ "left join students s on s.student_id = ds.student_id \r\n"
				+ "left join parent_kids pk on pk.student_id = s.student_id \r\n"
				+ "where ds.device_uuid= ? and ds.status= ?";

		Query query = session.createSQLQuery(deviceActiveInactiveStatusQry);
		query.setParameter(0, uuid);
		query.setParameter(1, "active");

		String status = (String) query.uniqueResult();

		if (null != status && status.equals("active"))
			deviceStatus = true;
		log.info("***Status***" + "\t" + status);
		log.info("***deviceStatus***" + "\t" + deviceStatus);
		session.close();
		tx.commit();
		return deviceStatus;
	}

	@Override
	public boolean findDeviceActive(int student_id, String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean deviceStatus = false;
		String deviceActiveInactiveStatusQry = "select ds.status as status from device_students ds where ds.device_uuid =  ? "
				+ "and ds.status= ? and ds.student_id = ?";

		Query query = session.createSQLQuery(deviceActiveInactiveStatusQry);
		query.setParameter(0, uuid);
		query.setParameter(1, "active");
		query.setParameter(2, student_id);

		String status = (String) query.uniqueResult();

		if (null != status && status.equals("active"))
			deviceStatus = true;
		log.info("***Status***" + "\t" + status);
		log.info("***deviceStatus***" + "\t" + deviceStatus);
		tx.commit();
		session.close();
		return deviceStatus;
	}

	@Override
	public void addPetData(PetDetails petDetails) {
		log.info("In DAO IMPL");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(petDetails);
		log.info("pet data added" + "\t" + petDetails.toString());
		tx.commit();
		session.close();
	}

	@Override
	public PetDetails getPetDetails(String uuid, Date inputDate) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String viewPetListQuery = null;

		viewPetListQuery = "SELECT pd.pet_type as pettype, pd.pet_name as petname, pd.size_level as sizelevel, pd.shape_level as shapelevel, pd.ornament_level as ornamentlevel, pd.gladness_level as gladnesslevel, pd.vigor_level as vigorlevel FROM pet_details pd where  pd.uuid =:uuid && pd.created_date =:inputDate";

		List<PetDetails> viewPetList = null;

		Query query = session.createSQLQuery(viewPetListQuery).addScalar("pettype").addScalar("petname")
				.addScalar("sizelevel").addScalar("shapelevel").addScalar("ornamentlevel").addScalar("gladnesslevel")
				.addScalar("vigorlevel").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(PetDetails.class));

		query.setParameter("uuid", uuid);
		query.setParameter("inputDate", inputDate);

		viewPetList = (List<PetDetails>) query.list();
		tx.commit();
		session.close();
		if (viewPetList.size() > 0)
			return viewPetList.get(0);
		else
			return null;
	}

	@Override
	public Devices findDeviceForSchoolId(String uuid, int schoolId) {
		Session session = null;
		Transaction tx = null;
		Devices devices = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			Criterion cr1 = Restrictions.eq("status", "");
			Criterion cr2 = Restrictions.eq("status", "assigned");
			Criterion cr3 = Restrictions.or(cr1, cr2);

			Criteria criteria = session.createCriteria(Devices.class);
			criteria.add(Restrictions.eq("uuid", uuid));
			criteria.add(Restrictions.eq("schoolId", schoolId));
			criteria.add(cr3);
			devices = (Devices) criteria.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in findDeviceForSchoolId()" + e);
			if (null != devices)
				session.evict(devices);
			tx.rollback();
		} finally {
			session.close();
		}
		return devices;
	}

	@Override
	public DeviceConfigurations findDeviceConfigByFirmwareFile(String firmwareFile) {
		Session session = null;
		Transaction tx = null;
		DeviceConfigurations deviceConfig = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(DeviceConfigurations.class);
			criteria.add(Restrictions.eq("firmwareFile", firmwareFile));
			deviceConfig = (DeviceConfigurations) criteria.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in findDeviceConfigByFirmwareFile()" + e);
			if (null != deviceConfig)
				session.evict(deviceConfig);
			tx.rollback();
		} finally {
			session.close();
		}
		return deviceConfig;
	}

	@Override
	public DeviceConfigurations isFirmwareFileExists(String firmwareFile) {
		Session session = null;
		Transaction tx = null;
		DeviceConfigurations deviceConfig = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(DeviceConfigurations.class);
			criteria.add(Restrictions.eq("firmwareFile", firmwareFile));
			deviceConfig = (DeviceConfigurations) criteria.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in isFirmwareFileExists()" + e);
			if (null != deviceConfig)
				session.evict(deviceConfig);
			tx.rollback();
		} finally {
			session.close();
		}
		return deviceConfig;
	}

	@Override
	public boolean createBeaconDeviceEvent(BeaconDeviceEventCreateModel beaconDeviceEventCreateModel, int device_id,
			SupportedEvents supportedEvents, String uuid, Geozones geoZone, int ipsReceiverZoneId, int ips_receiver_id) {
		boolean insertSuccessful = false;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			DeviceEvents deviceEvents = new DeviceEvents();
			deviceEvents.setEvents(supportedEvents);
			deviceEvents.setUuid(uuid);
			if (null != geoZone)
				deviceEvents.setGeozones(geoZone);

			deviceEvents.setEventoccureddate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(beaconDeviceEventCreateModel.getEventoccureddate()));
			if (null != geoZone) {
				deviceEvents.setGeozones(geoZone);
			}
			if (ipsReceiverZoneId > 0) {
				deviceEvents.setIps_receiver_zone_id(ipsReceiverZoneId);
			}
			if (ips_receiver_id > 0) {
				deviceEvents.setIps_receiver_id(ips_receiver_id);
			}
			session.save(deviceEvents);
			session.getTransaction().commit();

			session.close();
			log.info("DeviceDAOImpl.createDeviceEvent() ");
			insertSuccessful = true;
		} catch (HibernateException he) {
			log.debug("HibernateException : Insertion Failed - " + he);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return insertSuccessful;
	}

	@Override
	public int getSchoolIdbyDeviceUUID(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select school_id from class_grade cg\r\n"
				+ "LEFT JOIN students s on s.class_grade_id = cg.class_grade_id \r\n"
				+ "LEFT JOIN device_students ds on ds.student_id = s.student_id \r\n"
				+ "where ds.device_uuid =:uuid and ds.status ='active'";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("uuid", uuid);
		sessions = query.list();
		tx.commit();
		session.close();
		if (sessions.size() > 0 && null != sessions.get(0))
			return sessions.get(0);
		else
			return 0;
	}

	@Override
	public List<Integer> getUseridListbyuuid(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "\r\n" + "select pk.user_id from parent_kids pk\r\n"
				+ "LEFT JOIN device_students ds on ds.student_id = pk.student_id\r\n" + "where ds.device_uuid=:uuid";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("uuid", uuid);
		sessions = query.list();
		tx.commit();
		session.close();
		return sessions;
	}

	@Override
	public boolean findDeviceInActiveModeBasedOnUUID(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean deviceStatus = false;

		String deviceActiveInactiveStatusQry = "select status from device_students where device_uuid = ? and status = ?";
		Query query = session.createSQLQuery(deviceActiveInactiveStatusQry);
		query.setParameter(0, uuid);
		query.setParameter(1, "active");

		String status = (String) query.uniqueResult();

		if (null != status && status.equals("active"))
			deviceStatus = true;
		log.info("***Status***" + "\t" + status);
		log.info("***deviceStatus***" + "\t" + deviceStatus);
		tx.commit();
		session.close();
		return deviceStatus;
	}

	@Override
	public String getEventOcuuredDatebyzoneIdforCurrentDay(int geoZOneId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT event_occured_date FROM device_events where event_id = 10 and geozone_id =:geoZoneId and DATE(event_occured_date) = DATE(NOW()); ";
		String sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("geoZoneId", geoZOneId);
		if (query.list().size() > 0 && null != query.list().get(0))
			sessions = query.list().get(0).toString();
		else
			sessions = null;
		tx.commit();
		session.close();

		return sessions;
	}

	@Override
	public int getLatestGeozoneEventId(int geozone_id, int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT device_event_id FROM device_events "
				+ "WHERE event_id =:event_id and geozone_id =:geozone_id and "
				+ "DATE(event_occured_date) = DATE(NOW()) " + "ORDER BY event_occured_date DESC LIMIT 1; ";
		Integer sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("geozone_id", geozone_id);
		query.setParameter("event_id", event_id);
		sessions = (Integer) query.uniqueResult();
		tx.commit();
		session.close();

		if (null != sessions)
			return sessions;
		else
			return 0;
	}
	
	@Override
	public int getLatestGeozoneEventOccurred(int geozoneId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT event_id FROM device_events WHERE event_id IN(10,11) "
				+ "AND geozone_id =:geozoneId AND DATE(event_occured_date) = DATE(NOW()) "
				+ "ORDER BY event_occured_date DESC LIMIT 1";
		Integer geozoneEvent = null;

		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("geozoneId", geozoneId);

		geozoneEvent = (Integer) query.uniqueResult();
		tx.commit();
		session.close();

		if(null != geozoneEvent) {
			return geozoneEvent.intValue();
		} else {
			return 0;
		}
	}

	@Override
	public int getLatestGeoFenceEntryEventId(BeaconDeviceEventCreateModel beaconDeviceEventCreateModel) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT de.device_event_id FROM device_events de where de.uuid =:uuid \r\n"
				+ "and de.event_id = 3 and de.ips_receiver_zone_id =:zone_id and de.event_occured_date <=:occureddate \r\n"
				+ "and de.out_time is null order by de.event_occured_date desc LIMIT 1; ";
		Integer sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("uuid", beaconDeviceEventCreateModel.getWearable_uuid());
		query.setParameter("zone_id", beaconDeviceEventCreateModel.getIps_receiver_zone_id());
		query.setParameter("occureddate", beaconDeviceEventCreateModel.getEventoccureddate());
		sessions = (Integer) query.uniqueResult();
		tx.commit();
		session.close();
		if (null != sessions)
			return sessions;
		else
			return 0;
	}

	@Override
	public List<DeviceStudentsTransform> findStudentsInfoForFitnessActivity(int schoolId, String grade,
			String startDate, String endDate) {
		log.debug("Inside findStudentsInfo { ");
		Session session = null;
		Transaction tx = null;
		List<DeviceStudentsTransform> deviceStudentsTransformList = null;
		StringBuilder studentListBuilder = null;
		String studentsListQry = "";
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			studentListBuilder = new StringBuilder();
			studentListBuilder.append("SELECT ds.status as status, ds.device_uuid as uuid, ")
					.append("IF(ds.start_date < " + "'" + startDate + "'" + "," + "'" + startDate + "'"
							+ ", ds.start_date) as new_start_date, ")
					.append("IF(ds.end_date IS NULL, IF(" + "'" + endDate + "'" + "<NOW()," + "'" + endDate + "'"
							+ ",NOW()), IF(ds.end_date > " + "'" + endDate + "'" + "," + "'" + endDate + "'"
							+ ", ds.end_date)) as new_end_date, ")
					.append("s.student_id as student_id, cg.class as student_class, s.name as student_name ")
					.append("FROM device_students AS ds, students s, class_grade cg ")
					.append("where ds.student_id = s.student_id and cg.class_grade_id = s.class_grade_id and school_id= ? and grade= ? ")
					.append("HAVING new_start_date BETWEEN " + "'" + startDate + "'" + " AND " + "'" + endDate + "'"
							+ " OR new_end_date BETWEEN " + "'" + startDate + "'" + " AND " + "'" + endDate + "'");

			studentsListQry = studentListBuilder.toString();

			Query query = session.createSQLQuery(studentsListQry).addScalar("status").addScalar("uuid")
					.addScalar("new_start_date").addScalar("new_end_date").addScalar("student_id")
					.addScalar("student_class").addScalar("student_name")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(DeviceStudentsTransform.class));
			query.setParameter(0, schoolId);
			query.setParameter(1, grade);

			deviceStudentsTransformList = (List<DeviceStudentsTransform>) query.list();
			tx.commit();
		} catch (Exception e) {
			log.info("ErrorOccured in findStudentsInfo ()" + "\t" + e);
			tx.rollback();
		} finally {
			session.close();
		}
		return deviceStudentsTransformList;
	}

	@Override
	public int getSchoolIdByUUID(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Integer schoolId = null;
		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		Devices devices = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();
		if (null != devices) {
			schoolId = (Integer) devices.getSchoolId();
		}
		if (null != schoolId)
			return schoolId;
		else
			return 0;
	}
}
