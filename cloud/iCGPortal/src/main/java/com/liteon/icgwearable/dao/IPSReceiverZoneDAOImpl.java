package com.liteon.icgwearable.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.hibernate.entity.IPSReceiverZone;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.IPSReceiverZoneModel;

@Repository("iPSReceiverZoneDAO")
@Transactional
public class IPSReceiverZoneDAOImpl implements IPSReceiverZoneDAO {

	private static Logger log = Logger.getLogger(IPSReceiverZoneDAOImpl.class);

	@Resource(name = "configProperties")
	private Properties configProperties;

	@Autowired
	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * get zone detail List for given receiver ID
	 */
	@Override
	public List<IPSReceiverZone> getAllZoneDetailsForReceiverID(int receiver_Id) {

		log.info(" inside DAOImpl : getAllZoneDetailsForReceiverID ::" + receiver_Id);
		Session session = null;
		try {
			session = sessionFactory.openSession();

			log.info(" inside DAOImpl : getAllZoneDetailsForReceiverID-----");
			/*
			 * String sessionQuery =
			 * "SELECT ips_receiver_zone_id as zoneId, zt.ips_receiver_id as receiverId,zone_name as zoneName,map_filename as mapFilename "
			 * +
			 * " FROM ips_receiver_zone as zt, ips_receiver as lt WHERE zt.ips_receiver_id = lt.ips_receiver_id AND lt.ips_receiver_id = :receiver_Id"
			 * ;
			 */

			String sessionQuery = "SELECT * FROM ips_receiver_zone WHERE ips_receiver_id = :receiver_Id";

			Query query = session.createSQLQuery(sessionQuery).addEntity(IPSReceiverZone.class);
			query.setParameter("receiver_Id", receiver_Id);

			List<IPSReceiverZone> list = (List<IPSReceiverZone>) query.list();
			if (list != null)
				log.info("list: " + list.size());

			return list;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}// end of get method

	/**
	 * get zone id list for given receiver ID
	 */
	@Override
	public List<Integer> getAllZoneIDsForReceiverID(int receiver_Id) {

		log.info(" inside DAOImpl : getAllZoneIDsForReceiverID ::" + receiver_Id);
		Session session = null;
		try {
			session = sessionFactory.openSession();

			log.info(" inside DAOImpl : getAllZoneIDsForReceiverID-----");
			String sessionQuery = "SELECT ips_receiver_zone_id FROM ips_receiver_zone as zt, ips_receiver as lt WHERE zt.ips_receiver_id = lt.ips_receiver_id AND lt.ips_receiver_id = :receiver_Id";
			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter("receiver_Id", receiver_Id);

			List<Integer> idList = (List<Integer>) query.list();
			log.info("idlist: " + idList.toArray());

			return idList;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);
			e.printStackTrace();
		} finally {
			session.close();
		}
		return null;
	}// end of get method

	@Override
	public boolean deleteAllZonesForReceiverId(int receiver_Id) {
		log.info(" inside DAOImpl : deleteAllZonesForReceiverId ");
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "DELETE FROM ips_receiver_zone WHERE ips_receiver_id  = :receiver_Id";

			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter("receiver_Id", receiver_Id);
			query.executeUpdate();
			tx.commit();
			return true;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e.getStackTrace());
			return false;
		} finally {
			session.close();
		}
	}

	@Override
	public boolean createIPSReceiverZoneEntry(IPSReceiverZoneModel zm) {
		boolean insertflag = false;
		Session session = null;
		Transaction tx = null;

		IPSReceiverZone ipsZone = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			ipsZone = new IPSReceiverZone();

			ipsZone.setIpsReceiverId(zm.getReceiverId());

			if (zm.getZoneName() != null)
				ipsZone.setZoneName(zm.getZoneName());

			if (zm.getMapFilename() != null)
				ipsZone.setMapFilename(zm.getMapFilename());

			ipsZone.setCreated_date(new Date());
			ipsZone.setUpdated_date(new Date());

			session.save(ipsZone);
			tx.commit();

			log.info("IPSReceiverZone DoaImpl.createIPSReceiverZoneEntry() ");
			insertflag = true;
		} catch (Exception he) {
			log.debug("HibernateException : Insertion Failed - " + he);
			if (null != ipsZone)
				session.evict(ipsZone);
			tx.rollback();
		} finally {
			session.close();
		}

		return insertflag;
	}

	@Override
	public IPSReceiverZone getZoneDetailsForZoneId(int zone_Id) {

		Session session = null;
		IPSReceiverZone ips = null;
		try {
			session = sessionFactory.openSession();

			ips = (IPSReceiverZone) session.get(IPSReceiverZone.class, zone_Id);
			log.info("inside DAOImpl, IPSReceiverZone details::" + ips.toString());

		} catch (Exception e) {
			log.info("Exception Occured in getZoneDetailsForZoneId() " + e);
			e.printStackTrace();

		} finally {
			session.close();
		}

		return ips;
	}

	@Override
	public boolean updateIPSReceiverZoneEntry(IPSReceiverZone ips) {
		// IPSReceiverZone ips=null;
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			// ips.setCreated_date(new Date());
			ips.setUpdated_date(new Date());

			log.info("inside DAO update function IPSReceiverZone details::" + ips.toString());

			session.update(ips);
			tx.commit();

			return true;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);
			return false;
		} finally {
			session.close();
		}
	}

	@Override
	public IPSReceiverZone getZoneDetailsForZoneName(String zonename, int receiverId) {

		Session session = null;
		IPSReceiverZone ips = null;

		try {
			session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();

			Criteria criteria = session.createCriteria(IPSReceiverZone.class, "ipsz");
			criteria.add(Restrictions.eq("ipsz.zoneName", zonename));
			criteria.add(Restrictions.eq("ipsz.receiverId", receiverId));
			ips = (IPSReceiverZone) criteria.uniqueResult();

			tx.commit();
		} catch (Exception e) {
			log.info("Exception Occured in getIPSReceiverEntryForMac() " + e);

		} finally {
			session.close();
		}

		return ips;

	}

	@Override
	public boolean createIPSReceiverZoneEntry(IPSReceiverZone ipsZone) {

		boolean insertflag = false;
		Session session = null;
		Transaction tx = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			ipsZone.setCreated_date(new Date());
			ipsZone.setUpdated_date(new Date());

			session.save(ipsZone);
			tx.commit();

			log.info("IPSReceiverZone DoaImpl.createIPSReceiverZoneEntry()......... ");
			insertflag = true;
		} catch (Exception he) {
			log.debug("HibernateException : Insertion Failed - " + he);
			if (null != ipsZone)
				session.evict(ipsZone);
			tx.rollback();
		} finally {
			session.close();
		}

		return insertflag;

	}

	@Override
	public List<IPSReceiverZone> getAllZoneDetailsForZoneIds(List<Integer> zonelist, int receiver_Id) {

		log.info(" inside DAOImpl : getAllZoneDetailsForZoneIds ::" + zonelist + "  recID=" + receiver_Id);
		Session session = null;
		String sessionQuery = null;
		Query query = null;
		try {
			session = sessionFactory.openSession();

			log.info(" inside DAOImpl : getAllZoneDetailsForZoneIds-----");
			/*
			 * String sessionQuery =
			 * "SELECT ips_receiver_zone_id as zoneId, zt.ips_receiver_id as receiverId,zone_name as zoneName,map_filename as mapFilename "
			 * +
			 * " FROM ips_receiver_zone as zt, ips_receiver as lt WHERE zt.ips_receiver_id = lt.ips_receiver_id AND lt.ips_receiver_id = :receiver_Id"
			 * ;
			 */

			if (zonelist == null) {
				sessionQuery = "SELECT * FROM ips_receiver_zone WHERE ips_receiver_id = :receiver_Id";
				query = session.createSQLQuery(sessionQuery).addEntity(IPSReceiverZone.class);
				query.setParameter("receiver_Id", receiver_Id);
			} else {
				sessionQuery = "SELECT * FROM ips_receiver_zone WHERE ips_receiver_id = :receiver_Id and ips_receiver_zone_id in ( :ids)";

				query = session.createSQLQuery(sessionQuery).addEntity(IPSReceiverZone.class);
				query.setParameter("receiver_Id", receiver_Id);
				query.setParameterList("ids", zonelist);
			}

			List<IPSReceiverZone> list = (List<IPSReceiverZone>) query.list();
			if (list != null)
				log.info("list: " + list.size());

			return list;

		} catch (Exception e) {
			log.debug("Exception inside  DAOImpl getAllZoneDetailsForZoneIds :  " + e);
		} finally {
			session.close();
		}
		return null;

	}

	@Override
	public int deleteAllZonesInZoneIdList(List<Integer> ids, int receiver_Id) {

		log.info("deleteAllZonesInZoneIdList==" + ids + "  receiver_Id" + receiver_Id);
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "DELETE FROM ips_receiver_zone "
					+ "WHERE ips_receiver_id =:receiverId and ips_receiver_zone_id in ( :ids)";

			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter("receiverId", receiver_Id);
			query.setParameterList("ids", ids);

			int count = query.executeUpdate();
			tx.commit();
			return count;

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);

		} finally {
			session.close();
		}

		return 0;
	}

	@Override
	public HashMap<String, Object> getStudentBeaconAlerts(int school_id, String inputdate, int receiver_Id) {
		log.info("zone dao, getStudentBeaconAlerts----");

		HashMap<String, Object> outermap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> studentBecaonAlertsList = new ArrayList<HashMap<String, Object>>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query q = null;
		List<Object[]> rows = null;
		Users userSOSClosed = null;

		// Query to get all needed data for geofence alert
		String query = "  SELECT students.student_id, students.name, class_grade.class, "
				+ "device_events.device_event_id, ips_receiver_device.ips_receiver_device_id,  "
				+ "device_events.event_occured_date, device_events.in_time, "
				+ "device_events.out_time, class_grade.school_id, zt1.ips_receiver_zone_id, "
				+ "zt1.zone_name, zt1.building_name, zt1.floor_number, zt1.map_filename "
				+ "FROM students, class_grade, device_students, device_events "
				+ "LEFT JOIN ips_receiver_device ON ips_receiver_device.ips_receiver_device_id = device_events.ips_receiver_device_id "
				+ "LEFT JOIN ips_receiver_zone zt1 ON zt1.ips_receiver_zone_id = ips_receiver_device.ips_receiver_zone_id  "
				+ "WHERE ips_receiver_device.ips_receiver_device_id in (SELECT ips_receiver_device_id FROM ips_receiver_device as st "
				+ "WHERE st.ips_receiver_zone_id in (SELECT ips_receiver_zone_id FROM ips_receiver_zone as zt, ips_receiver as lt  "
				+ "WHERE zt.ips_receiver_id = lt.ips_receiver_id AND lt.ips_receiver_id = ?)) AND students.class_grade_id = class_grade.class_grade_id  "
				+ " AND students.student_id = device_students.student_id "
				+ " AND device_students.device_uuid = device_events.uuid "
				+ " AND device_events.event_id in (3,4) AND DATE(device_events.event_occured_date) = ? "
				+ " AND class_grade.school_id = ? " + " AND ? BETWEEN DATE(device_students.start_date) "
				+ " AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) ";

		log.info("zone dao, getStudentBeaconAlerts----query" + query);

		q = session.createSQLQuery(query);
		q.setParameter(0, receiver_Id);
		q.setParameter(1, inputdate);
		q.setParameter(2, school_id);
		q.setParameter(3, inputdate);

		rows = q.list();
		for (Object[] row : rows) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			Integer studentId = (Integer) row[0];
			String studentName = row[1].toString();
			String studentClass = row[2].toString();
			Integer deviceeventId = (Integer) row[3];
			Integer receiverDeviceId = (Integer) row[4];
			String eventTimeStamp = row[5].toString();
			String eventInTime = row[6].toString();
			String eventOutTime = row[7].toString();
			Integer schoolId = (Integer) row[8];
			Integer zoneId = (Integer) row[9];
			String zoneName = (null != row[10]) ? row[10].toString() : "";
			String buildingName = (null != row[11]) ? row[11].toString() : "";
			String floorNumber = (null != row[12]) ? row[12].toString() : "";
			String mapFilename = "";
			if (null != row[13]) {
				mapFilename = this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("ipsimages.download.path") + "/" + row[13].toString();
			}

			log.info("studentId=" + studentId);
			log.info("studentName=" + studentName);
			log.info("studentClass=" + studentClass);
			log.info("deviceeventId=" + deviceeventId);
			log.info("receiverDeviceId=" + receiverDeviceId);
			log.info("eventTimeStamp=" + eventTimeStamp);
			log.info("eventInTime=" + eventInTime);
			log.info("eventOutTime=" + eventOutTime);
			log.info("schoolId=" + schoolId);
			log.info("zoneId=" + zoneId);
			log.info("zoneName=" + zoneName);
			log.info("buildingName=" + buildingName);
			log.info("floorNumber=" + floorNumber);
			log.info("mapFilename=" + mapFilename);

			map.put("student_id", studentId);
			map.put("student_name", studentName);
			map.put("student_class", studentClass);
			map.put("device_event_id", deviceeventId);
			map.put("receiver_device_Id", receiverDeviceId);
			map.put("event_timestamp", eventTimeStamp);
			map.put("in_time", eventInTime);
			map.put("out_time", eventOutTime);
			map.put("school_id", schoolId);
			map.put("zone_id", zoneId);
			map.put("zone_name", zoneName);
			map.put("building_name", buildingName);
			map.put("floor_number", floorNumber);
			map.put("mapFilename", mapFilename);

			studentBecaonAlertsList.add(map);
		}

		outermap.put("alerts", studentBecaonAlertsList);

		return outermap;
	}// end of method

}
