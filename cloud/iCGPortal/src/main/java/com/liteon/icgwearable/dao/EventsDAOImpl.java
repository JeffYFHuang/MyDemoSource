package com.liteon.icgwearable.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.hibernate.entity.DeviceEvents;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.EventSubscriptions;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.transform.DeviceEventsTransform;
import com.liteon.icgwearable.transform.GeoFenceEntyExitTransform;
import com.liteon.icgwearable.transform.SubscribedEvents;

@Repository("eventsDAO")
public class EventsDAOImpl implements EventsDAO {

	private static Logger log = Logger.getLogger(EventsDAOImpl.class);
	@Autowired
	private SessionFactory sessionFactory;
	
	@Value("${SCHOOL_ENTRY_ALERT_ID}")
	private Integer SCHOOL_ENTRY_ID;
	
	@Value("${SCHOOL_EXIT_ALERT_ID}")
	private Integer SCHOOL_EXIT_ID;
	
	@Value("${GEOFENCE_ENTRY_ID}")
	private Integer GEOFENCE_ENTRY_ID;
	
	@Value("${GEOFENCE_EXIT_ID}")
	private Integer GEOFENCE_EXIT_ID;
	
	@Value("${SOS_ALERT_ID}")
	private Integer SOS_ID;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Bean
	public DateTimeFormatter dateTimeFormatter() {
		return ISODateTimeFormat.dateTimeParser();
	}

	@Override
	public EventSubscriptions isMembersubscribed(int userId, int eventId, int studentId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(EventSubscriptions.class, "es");
		criteria.createAlias("es.events", "ese");
		criteria.createAlias("es.users", "esu");
		criteria.createAlias("es.students", "esstu");
		criteria.add(Restrictions.eq("ese.eventId", eventId));
		criteria.add(Restrictions.eq("esu.id", userId));
		criteria.add(Restrictions.eq("esstu.studentId", studentId));
		EventSubscriptions es = (EventSubscriptions) criteria.uniqueResult();
		tx.commit();
		session.close();

		if (es != null)
			return es;
		else
			return null;
	}

	@Override
	public boolean isMemberSubscribed(int user_id, int student_id, int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean response = false;
		try {
			String SQL_QUERY = "SELECT `notification_id` FROM `event_subscriptions` "
					+ "WHERE `event_id` = :event_id AND `student_id` = :student_id AND `user_id` = :user_id";
			Query query = session.createSQLQuery(SQL_QUERY);
			query.setParameter("event_id", event_id);
			query.setParameter("student_id", student_id);
			query.setParameter("user_id", user_id);

			Integer notification_id = (Integer) query.uniqueResult();

			if (null != notification_id) {
				response = true;
			}
		} catch (Exception e) {
			log.debug("Exception inside isMemberSubscribed :  " + e.getStackTrace());
		} finally {
			tx.commit();
			session.close();
		}
		return response;
	}

	@Override
	public void subscribeEvent(EventSubscriptions es) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(es);
		tx.commit();
		session.close();
	}

	@Override
	public void subscribeEvent(int user_id, int student_id, int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("INSERT INTO event_subscriptions "
				+ "(event_id, student_id, user_id, created_date) " + "VALUES (?, ?, ?, ?)");
		query.setParameter(0, event_id);
		query.setParameter(1, student_id);
		query.setParameter(2, user_id);
		query.setParameter(3, new Date());
		int result = query.executeUpdate();
		log.info("subscribeEvent Result" + result);
		tx.commit();
		session.close();
	}

	@Override
	public Devices getDeviceByUuid(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Devices.class, "device");
		criteria.add(Restrictions.eq("device.uuid", uuid));
		Devices device = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();
		return device;
	}

	@Override
	public SupportedEvents getSupportedEventsByEventId(int eventid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(SupportedEvents.class, "se");
		criteria.add(Restrictions.eq("se.eventId", eventid));
		SupportedEvents supportedEvents = (SupportedEvents) criteria.uniqueResult();
		tx.commit();
		session.close();
		return supportedEvents;
	}

	@Override
	public boolean isSupportedEvent(int event_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "SELECT `event_id` FROM `supported_events` " + "WHERE `event_id` = :event_id";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("event_id", event_id);

		Integer eventId = (Integer) query.uniqueResult();

		tx.commit();
		session.close();
		if (null != eventId)
			return true;
		else
			return false;
	}

	@Override
	public List<SubscribedEvents> getUserSubscribedEvents(int userId, String uuid, String userRole) {
		List<SubscribedEvents> subscribedEventsList = new ArrayList<SubscribedEvents>();
		Session session = sessionFactory.openSession();
		log.info("enterd into impl");
		Transaction selectQueryForsubscribedEventsListTrxn = session.beginTransaction();
		String QueryForsubscribedEventsList = null;

		if (userRole.equals("parent_admin")) {
			QueryForsubscribedEventsList = "SELECT se.event_id as supportedEventId, se.event_name as eventName, "
					+ "es.event_id as subscribedEventId " + "FROM supported_events AS se "
					+ "LEFT JOIN ( SELECT event_id FROM device_students AS ds, event_subscriptions AS ess "
					+ "WHERE ess.student_id=ds.student_id AND ds.device_uuid = :uuid AND ess.user_id = :userId ) "
					+ "AS es ON es.event_id=se.event_id "
					+ "WHERE se.parent_unsubscribe='yes' and se.event_default='yes'";
		}

		log.info("before getUserSubscribedEvents Query");
		Type Type = null;
		Query selectQueryForsubscribedEventsList = session.createSQLQuery(QueryForsubscribedEventsList)
				.addScalar("supportedEventId").addScalar("eventName").addScalar("subscribedEventId")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SubscribedEvents.class));
		// Query selectQueryForsubscribedEventsList =
		// session.createQuery(QueryForsubscribedEventsList);
		log.info("after create Query");
		selectQueryForsubscribedEventsList.setParameter("userId", userId);
		selectQueryForsubscribedEventsList.setParameter("uuid", uuid);
		log.info("select Query " + selectQueryForsubscribedEventsList);
		subscribedEventsList = selectQueryForsubscribedEventsList.list();
		for (SubscribedEvents event : subscribedEventsList) {
			log.info("event.getSubscribedEventId()" + "\t" + event.getSubscribedEventId());
		}
		if (subscribedEventsList.size() > 0)
			log.info("list size " + subscribedEventsList.size());
		else
			log.info("empty list");
		selectQueryForsubscribedEventsListTrxn.commit();
		session.close();
		return subscribedEventsList;
	}

	@Override
	public void unsubscribeEvent(int userId, int student_id, int eventId) {
		Session session = sessionFactory.openSession();
		Transaction DeleteQuery_eventsubscriptionstxn = session.beginTransaction();
		String DeleteQuery_eventsubscriptions = "DELETE from EventSubscriptions as es  where es.users.id=:userId and es.events.eventId=:eventId and es.students.studentId=:studentId";
		Query DeleteQuery_eventsubscriptionsQry = session.createQuery(DeleteQuery_eventsubscriptions);
		DeleteQuery_eventsubscriptionsQry.setParameter("userId", userId);
		DeleteQuery_eventsubscriptionsQry.setParameter("eventId", eventId);
		DeleteQuery_eventsubscriptionsQry.setParameter("studentId", student_id);
		int deletedsubscriptions = DeleteQuery_eventsubscriptionsQry.executeUpdate();
		DeleteQuery_eventsubscriptionstxn.commit();
		session.close();
	}

	@Override
	public List<DeviceEventsTransform> findGPSData(int studentId, int eventId, String startDt, String endDt) {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String gpsQuery = null;
		List<DeviceEventsTransform> deviceEventsTransformList = null;

		gpsQuery = "SELECT de.gps_data_code as gps_data_code,de.gps_location_data as gps_location_data, de.event_id as event_id, ds.status as status,de.event_occured_date as event_occured_date, "
				+ "ds.device_uuid as uuid, ds.start_date as start_date, ds.end_date as end_date FROM device_students AS ds "
				+ "LEFT JOIN device_events AS de ON de.uuid = ds.device_uuid  AND de.event_id = ? "
				+ "WHERE ds.student_id = ? " + "AND (DATE(ds.start_date) BETWEEN " + "'" + startDt + "'" + "AND " + "'"
				+ endDt + "'" + " " + "OR DATE(ds.end_date) BETWEEN " + "'" + startDt + "'" + " AND " + "'" + endDt
				+ "'" + "" + " OR ds.end_date IS NULL)" + "AND (DATE(de.event_occured_date) BETWEEN " + "'" + startDt
				+ "'" + "AND " + "'" + endDt + "'" + ")";

		Query gpsTransformQuery = session.createSQLQuery(gpsQuery).addScalar("gps_data_code")
				.addScalar("gps_location_data").addScalar("event_id").addScalar("status")
				.addScalar("event_occured_date").addScalar("uuid").addScalar("start_date").addScalar("end_date")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceEventsTransform.class));
		gpsTransformQuery.setParameter(0, eventId);
		gpsTransformQuery.setParameter(1, studentId);
		deviceEventsTransformList = gpsTransformQuery.list();
		tx.commit();
		session.close();
		return deviceEventsTransformList;
	}

	@Override
	public List<DeviceEventsTransform> findBandBackAlertAndSOSRemovingData(int studentId, int eventId, String startDt,
			String endDt) {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String query = null;
		List<DeviceEventsTransform> deviceEventsTransformList = null;

		query = "SELECT ds.status as status,de.event_occured_date as event_occured_date, "
				+ "ds.device_uuid as uuid, ds.start_date as start_date, ds.end_date as end_date FROM device_students AS ds "
				+ "LEFT JOIN device_events AS de ON de.uuid = ds.device_uuid  AND de.event_id = ? "
				+ "WHERE ds.student_id = ? " + "AND (DATE(ds.start_date) BETWEEN " + "'" + startDt + "'" + "AND " + "'"
				+ endDt + "'" + " " + "OR DATE(ds.end_date) BETWEEN " + "'" + startDt + "'" + " AND " + "'" + endDt
				+ "'" + "" + " OR ds.end_date IS NULL)" + "AND (DATE(de.event_occured_date) BETWEEN " + "'" + startDt
				+ "'" + "AND " + "'" + endDt + "'" + ")";

		Query gpsTransformQuery = session.createSQLQuery(query).addScalar("status").addScalar("event_occured_date")
				.addScalar("uuid").addScalar("start_date").addScalar("end_date")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceEventsTransform.class));
		gpsTransformQuery.setParameter(0, eventId);
		gpsTransformQuery.setParameter(1, studentId);
		deviceEventsTransformList = gpsTransformQuery.list();
		tx.commit();
		session.close();
		return deviceEventsTransformList;
	}

	@Override
	public List<DeviceEventsTransform> findAbnormalVitalSign(int studentId, int eventId, String startDt, String endDt) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<DeviceEventsTransform> deviceEventsTransformList = null;
		String vitalSignQuery = null;

		vitalSignQuery = "SELECT de.vital_sign_type as vital_sign_type,de.vital_sign_value as vital_sign_value, de.abnormal_code as abnormal_code, de.event_id as event_id, ds.status as status,de.event_occured_date as event_occured_date, "
				+ "ds.device_uuid as uuid, ds.start_date as start_date, ds.end_date as end_date FROM device_students AS ds "
				+ "LEFT JOIN device_events AS de ON de.uuid = ds.device_uuid  AND de.event_id = ? "
				+ "WHERE ds.student_id = ? " + "AND (DATE(ds.start_date) BETWEEN " + "'" + startDt + "'" + "AND " + "'"
				+ endDt + "'" + " " + "OR DATE(ds.end_date) BETWEEN " + "'" + startDt + "'" + " AND " + "'" + endDt
				+ "'" + "" + " OR ds.end_date IS NULL)" + "AND (DATE(de.event_occured_date) BETWEEN " + "'" + startDt
				+ "'" + "AND " + "'" + endDt + "'" + ")";

		Query abnormalVitalSignQuery = session.createSQLQuery(vitalSignQuery).addScalar("vital_sign_type")
				.addScalar("vital_sign_value").addScalar("abnormal_code").addScalar("event_id").addScalar("status")
				.addScalar("event_occured_date").addScalar("uuid").addScalar("start_date").addScalar("end_date")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceEventsTransform.class));

		abnormalVitalSignQuery.setParameter(0, eventId);
		abnormalVitalSignQuery.setParameter(1, studentId);
		deviceEventsTransformList = abnormalVitalSignQuery.list();
		tx.commit();
		session.close();
		return deviceEventsTransformList;
	}

	@Override
	public List<DeviceEventsTransform> findSensorMalfunction(int studentId, int eventId, String startDt, String endDt) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		List<DeviceEventsTransform> deviceEventsTransformList = null;
		String sensorMalfunctionQuery = null;

		sensorMalfunctionQuery = "SELECT de.sensor_type_code as sensor_type_code,de.sensor_error_code as sensor_error_code, de.event_id as event_id, ds.status as status,de.event_occured_date as event_occured_date, "
				+ "ds.device_uuid as uuid, ds.start_date as start_date, ds.end_date as end_date FROM device_students AS ds "
				+ "LEFT JOIN device_events AS de ON de.uuid = ds.device_uuid  AND de.event_id = ? "
				+ "WHERE ds.student_id = ? " + "AND (DATE(ds.start_date) BETWEEN " + "'" + startDt + "'" + "AND " + "'"
				+ endDt + "'" + " " + "OR DATE(ds.end_date) BETWEEN " + "'" + startDt + "'" + " AND " + "'" + endDt
				+ "'" + "" + " OR ds.end_date IS NULL)" + "AND (DATE(de.event_occured_date) BETWEEN " + "'" + startDt
				+ "'" + "AND " + "'" + endDt + "'" + ")";

		Query sensorMalfunctionQ = session.createSQLQuery(sensorMalfunctionQuery).addScalar("sensor_type_code")
				.addScalar("sensor_error_code").addScalar("event_id").addScalar("status")
				.addScalar("event_occured_date").addScalar("uuid").addScalar("start_date").addScalar("end_date")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceEventsTransform.class));

		sensorMalfunctionQ.setParameter(0, eventId);
		sensorMalfunctionQ.setParameter(1, studentId);
		deviceEventsTransformList = sensorMalfunctionQ.list();
		tx.commit();
		session.close();
		return deviceEventsTransformList;
	}

	@Override
	public List<DeviceEventsTransform> findBatteryLevel(int studentId, int eventId, String startDt, String endDt) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		List<DeviceEventsTransform> deviceEventsTransformList = null;
		String batteryLevelQuery = null;

		batteryLevelQuery = "SELECT de.battery_level_value as battery_level_value, de.event_id as event_id, ds.status as status,de.event_occured_date as event_occured_date, "
				+ "ds.device_uuid as uuid, ds.start_date as start_date, ds.end_date as end_date FROM device_students AS ds "
				+ "LEFT JOIN device_events AS de ON de.uuid = ds.device_uuid  AND de.event_id = ? "
				+ "WHERE ds.student_id = ? " + "AND (DATE(ds.start_date) BETWEEN " + "'" + startDt + "'" + "AND " + "'"
				+ endDt + "'" + " " + "OR DATE(ds.end_date) BETWEEN " + "'" + startDt + "'" + " AND " + "'" + endDt
				+ "'" + " OR ds.end_date IS NULL)" + "AND (DATE(de.event_occured_date) BETWEEN " + "'" + startDt + "'"
				+ "AND " + "'" + endDt + "'" + ")";

		Query batteryLevelQ = session.createSQLQuery(batteryLevelQuery).addScalar("battery_level_value")
				.addScalar("event_id").addScalar("status").addScalar("event_occured_date").addScalar("uuid")
				.addScalar("start_date").addScalar("end_date").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceEventsTransform.class));

		batteryLevelQ.setParameter(0, eventId);
		batteryLevelQ.setParameter(1, studentId);
		deviceEventsTransformList = batteryLevelQ.list();
		tx.commit();
		session.close();
		return deviceEventsTransformList;
	}

	@Override
	public boolean deviceExistForEvent(String uuid, int eventId) {
		// TODO Auto-generated method stub
		boolean deviceExistForEvent = false;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceEventsQuery = "from DeviceEvents de where de.uuid =:uuid and de.events.eventId =:eventId";
		Query query = session.createQuery(deviceEventsQuery);
		query.setParameter("uuid", uuid);
		query.setParameter("eventId", eventId);
		List<DeviceEvents> deviceEventList = query.list();

		if (deviceEventList.size() > 0) {
			deviceExistForEvent = true;
		}
		tx.commit();
		session.close();
		return deviceExistForEvent;
	}

	@Override
	public void updateSubscribedEvent(EventSubscriptions es) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.update(es);
		tx.commit();
		session.close();
	}

	@Override
	public int getPairedStatusWithMemberAndDevice(String uuid, int member_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		log.info("enterd into impl");
		List<Integer> pairedStatusList = new ArrayList<Integer>();
		Transaction selectQueryForpairedStatusListTrxn = session.beginTransaction();

		/*
		 * String QueryForPairedStatus =
		 * "select ms.student_id from member_students ms \r\n" +
		 * "LEFT JOIN students s on s.student_id=ms.student_id\r\n" +
		 * "LEFT JOIN devices as d on s.student_id=d.student_id\r\n" +
		 * " where d.uuid=:uuid and  ms.user_id=:member_id";
		 */

		String QueryForPairedStatus = "select pk.student_id from parent_kids pk\r\n"
				+ " LEFT JOIN students s on s.student_id=pk.student_id \r\n"
				+ " LEFT JOIN device_students ds on ds.student_id=s.student_id \r\n"
				+ " LEFT JOIN devices as d on ds.device_uuid=d.uuid \r\n"
				+ " where d.uuid=:uuid and  pk.user_id=:member_id";

		log.info("QueryForPairedStatus" + QueryForPairedStatus);
		log.info("before create Query");

		Query selectQueryForPairedStatusList = session.createSQLQuery(QueryForPairedStatus);
		// Query selectQueryForsubscribedEventsList =
		// session.createQuery(QueryForsubscribedEventsList);
		log.info("after create Query");
		selectQueryForPairedStatusList.setParameter("uuid", uuid);
		selectQueryForPairedStatusList.setParameter("member_id", member_id);

		pairedStatusList = selectQueryForPairedStatusList.list();

		selectQueryForpairedStatusListTrxn.commit();
		session.close();
		if (pairedStatusList.size() > 0)
			return pairedStatusList.get(0);
		else
			return 0;
	}

	@Override
	public int getvalidStudentUnderParentmember(int student_id, int parent_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		log.info("enterd into impl");
		List<Integer> validStudentsList = new ArrayList<Integer>();
		Transaction selectQueryForvalidStudentsListTrxn = session.beginTransaction();

		String QueryForvalidStudent = "select s.student_id from students s \r\n"
				+ " LEFT JOIN parent_kids pk on pk.student_id = s.student_id\r\n"
				+ " LEFT JOIN users u on pk.user_id = u.user_id "
				+ " where s.student_id=:student_id and u.user_id=:parent_id";

		log.info("QueryForvalidStudent" + QueryForvalidStudent);
		log.info("before create Query");

		Query selectQueryForPairedStatusList = session.createSQLQuery(QueryForvalidStudent);
		// Query selectQueryForsubscribedEventsList =
		// session.createQuery(QueryForsubscribedEventsList);
		log.info("after create Query");
		selectQueryForPairedStatusList.setParameter("student_id", student_id);
		selectQueryForPairedStatusList.setParameter("parent_id", parent_id);

		validStudentsList = selectQueryForPairedStatusList.list();

		selectQueryForvalidStudentsListTrxn.commit();
		session.close();
		if (validStudentsList.size() > 0)
			return validStudentsList.get(0);
		else
			return 0;
	}

	@Override
	public List<Integer> getDeviceEventsForLocatorId(int locators_Id) {
		return null;
	}

	@Override
	public boolean deleteDeviceEventsForReceiverDevId(int receiverDevId) {

		log.info(" inside DAOImpl : deleteDeviceEventsForZoneId ");
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "DELETE FROM device_events WHERE ips_receiver_device_id  = ?";

			log.info("sessionQuery=" + sessionQuery);

			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter(0, receiverDevId);
			query.executeUpdate();

			tx.commit();
			return true;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e.getStackTrace());
			e.printStackTrace();
			return false;
		} finally {
			session.close();
		}

	}

	@Override
	public int updateZoneIdForDeviceEventIds(List<Integer> ids) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "UPDATE device_events SET ips_receiver_zone_id = 0 WHERE device_event_id in ( :ids)";

			Query query = session.createSQLQuery(sessionQuery);
			query.setParameterList("ids", ids);

			int count = query.executeUpdate();
			tx.commit();
			return count;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e.getStackTrace());

		} finally {
			session.close();
		}
		return 0;
	}

	@Override
	public int deleteDeviceEventsQueueForEventIds(List<Integer> ids) {
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "DELETE FROM device_events_queue WHERE device_event_id in ( :ids)";

			Query query = session.createSQLQuery(sessionQuery);
			query.setParameterList("ids", ids);

			int count = query.executeUpdate();
			tx.commit();
			return count;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e.getStackTrace());

		} finally {
			session.close();
		}
		return 0;

	}

	@Override
	public List<Integer> getDeviceEventIdsForGivenIdList(List<Integer> receiverZoneIdList) {

		log.info(" inside DAOImpl : getDeviceEventIdsForGivenIdList");
		Session session = null;

		try {
			session = sessionFactory.openSession();

			String sessionQuery = "SELECT device_event_id FROM device_events as det WHERE "
					+ "det.ips_receiver_zone_id in (:receiverZoneIdList)";

			log.info("query:" + sessionQuery);
			Query query = session.createSQLQuery(sessionQuery);
			query.setParameterList("receiverZoneIdList", receiverZoneIdList);

			List<Integer> idList = (List<Integer>) query.list();

			return idList;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl getDeviceEventIdsForGivenIdList:  " + e.getStackTrace());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}

	@Override
	public List<DeviceEventsTransform> findBeaconEventsData(String sessionId, int eventId, String startDt, String endDt,
			int zone_id, int ips_receiver_id) {
		Session session = null;
		Transaction tx = null;

		String beaconEventsQry = null;
		List<DeviceEventsTransform> deviceEventsTransformList = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			StringBuilder beaconStrBuilder = null;
			if (eventId == SCHOOL_ENTRY_ID || eventId == SCHOOL_EXIT_ID || eventId == SOS_ID) {
				log.info("For School In/Out or SOS, ips_receiver_zone_id is not required");
				beaconStrBuilder = new StringBuilder(
						"select de.event_id as event_id, ds.status as status, de.event_occured_date as event_occured_date, "
								+ "ds.device_uuid as uuid, de.ips_receiver_zone_id as zone_id from device_events de ")
										.append("join ips_receiver re on re.ips_receiver_id=de.ips_receiver_id ")
										.append("join device_students ds on ds.device_uuid=de.uuid ")
										.append("where re.session_id= ? and de.event_id= ? ")
										.append("AND DATE(de.event_occured_date) BETWEEN " + "'" + startDt + "'" + " AND "
												+ "'" + endDt + "'");
				beaconStrBuilder.append("AND de.ips_receiver_id= " + ips_receiver_id);
			}else{
				beaconStrBuilder = new StringBuilder(
						"select de.event_id as event_id, ds.status as status, de.event_occured_date as event_occured_date, "
								+ "ds.device_uuid as uuid, rz.ips_receiver_zone_id as zone_id from device_events de ")
										.append("join ips_receiver_zone rz on rz.ips_receiver_zone_id=de.ips_receiver_zone_id ")
										.append("join ips_receiver re on re.ips_receiver_id=rz.ips_receiver_id ")
										.append("join device_students ds on ds.device_uuid=de.uuid ")
										.append("where re.session_id= ? and de.event_id= ? ")
										.append("AND DATE(de.event_occured_date) BETWEEN " + "'" + startDt + "'" + " AND "
												+ "'" + endDt + "'");
				if (zone_id > 0){
					beaconStrBuilder.append("AND rz.ips_receiver_zone_id= " + zone_id);
				}
			}
			
			beaconEventsQry = beaconStrBuilder.toString();

			Query beaconEventsQuery = session.createSQLQuery(beaconEventsQry).addScalar("event_id").addScalar("status")
					.addScalar("event_occured_date").addScalar("uuid").addScalar("zone_id")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(DeviceEventsTransform.class));
			beaconEventsQuery.setParameter(0, sessionId);
			beaconEventsQuery.setParameter(1, eventId);
			deviceEventsTransformList = beaconEventsQuery.list();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in findBeaconEventsData()" + "\t" + e);
			tx.rollback();
		} finally {
			session.close();
		}
		return deviceEventsTransformList;
	}

	@Override
	public List<Integer> getDefaultEventIdListForParent() {
		log.info(" inside DAOImpl : getDefaultEventIdListForParent");
		Session session = null;

		try {
			session = sessionFactory.openSession();

			String sessionQuery = "select event_id from supported_events where event_default = 'yes' ";

			log.info("query:" + sessionQuery);
			Query query = session.createSQLQuery(sessionQuery);

			List<Integer> idList = (List<Integer>) query.list();

			return idList;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl getDefaultEventIdListForParent:  " + e.getStackTrace());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}

	@Override
	public List<GeoFenceEntyExitTransform> getGeoFenceEntryOrExitEvents(int school_id, String inputdate) {
		Session session = null;
		Transaction tx = null;

		String beaconEventsQry = null;
		List<GeoFenceEntyExitTransform> deviceEventsTransformList = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			beaconEventsQry = "SELECT irz.ips_receiver_zone_id AS receiverZoneId, irz.zone_name AS zoneName,"
					+ "irz.map_type AS mapType, irz.building_name AS bulingName, irz.floor_number AS floorNumber, "
					+ "irz.map_filename AS mapFileName, de.device_event_id AS deviceEventId, "
					+ "DATE_FORMAT(de.in_time, '%H:%i') AS inTime, DATE_FORMAT(de.out_time, '%H:%i') AS outTime, "
					+ "st.name as stdentName,st.student_id AS studentId, cg.class AS studentClass, "
					+ "TIMEDIFF(de.out_time ,de.in_time) AS duration " + "FROM ips_receiver_zone irz "
					+ "LEFT JOIN device_events de ON de.ips_receiver_zone_id = irz.ips_receiver_zone_id "
					+ "AND de.event_id = 3 AND DATE(de.event_occured_date) =:occured_date "
					+ "LEFT JOIN ips_receiver ir ON ir.ips_receiver_id = irz.ips_receiver_id "
					+ "LEFT JOIN accounts acc ON acc.account_id = ir.school_id "
					+ "LEFT JOIN device_students ds ON ds.device_uuid = de.uuid "
					+ "AND :occured_date BETWEEN DATE(ds.start_date) "
					+ "AND (IF(ds.end_date IS NULL, :occured_date, DATE(ds.end_date)))"
					+ "LEFT JOIN students st ON st.student_id = ds.student_id "
					+ "LEFT JOIN class_grade cg ON cg.class_grade_id = st.class_grade_id "
					+ "WHERE ir.school_id =:school_id";

			Query beaconEventsQuery = session.createSQLQuery(beaconEventsQry).addScalar("receiverZoneId")
					.addScalar("zoneName").addScalar("mapType").addScalar("bulingName").addScalar("floorNumber")
					.addScalar("mapFileName").addScalar("deviceEventId").addScalar("inTime").addScalar("outTime")
					.addScalar("stdentName").addScalar("studentId").addScalar("studentClass").addScalar("duration")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(GeoFenceEntyExitTransform.class));
			beaconEventsQuery.setParameter("school_id", school_id);
			beaconEventsQuery.setParameter("occured_date", inputdate);
			deviceEventsTransformList = beaconEventsQuery.list();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in findBeaconEventsData()" + "\t" + e);
			tx.rollback();
		} finally {
			session.close();
		}
		return deviceEventsTransformList;
	}

	@Override
	public List<Integer> getZoneIdListForReceiver(int ips_receiver_id) {
		log.info(" inside EventsDAOImpl : getZoneIdListForReceiver");
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "SELECT `ips_receiver_zone_id` FROM `ips_receiver_zone` WHERE `ips_receiver_id` = :ips_receiver_id AND `map_type` = 'partial'";

			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter("ips_receiver_id", ips_receiver_id);

			List<Integer> idList = (List<Integer>) query.list();
			tx.commit();
			return idList;
		} catch (Exception e) {
			log.debug("Exception inside EventsDAOImpl getZoneIdListForReceiver:  " + e.getStackTrace());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}
}
