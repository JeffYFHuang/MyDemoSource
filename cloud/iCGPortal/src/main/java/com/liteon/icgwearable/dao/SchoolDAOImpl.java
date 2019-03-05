package com.liteon.icgwearable.dao;

import java.math.BigInteger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.SchoolCalendar;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.hibernate.entity.SystemConfiguration;
import com.liteon.icgwearable.transform.SchoolScheduleTransform;
import com.liteon.icgwearable.transform.SearchSchoolTransform;
import com.liteon.icgwearable.transform.SysConfigurationTransform;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.StringUtility;

@Repository("schoolDAO")
public class SchoolDAOImpl implements SchoolDAO {

	private static Logger log = Logger.getLogger(SchoolDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public List<Accounts> listSchools(int start, int total) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Accounts.class);
		criteria.add(Restrictions.eq("accountType", "school"));
		criteria.setFirstResult(start);
		criteria.setMaxResults(total);
		@SuppressWarnings("unchecked")
		List<Accounts> schoolsList = criteria.list();
		tx.commit();
		session.close();
		return schoolsList;
	}

	@Override
	public void addSchool(Accounts accounts) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		if (accounts.getAccountId() == 0)
			session.save(accounts);
		else
			session.update(accounts);
		tx.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	public boolean deleteSchool(int id) {
		log.info("entered into deleteSchool ");
		Session session = sessionFactory.openSession();
		boolean deleteSuccess = false;
		Transaction transaction = null;
		try {
			/* for the table device_events_queue */
			transaction = session.beginTransaction();
			String devicceEventsQueue_DeleteQuery = "DELETE FROM device_events_queue WHERE device_event_id IN("
					+ "SELECT device_event_id FROM device_events WHERE uuid IN("
					+ "SELECT uuid FROM devices WHERE school_id =:school_id))";
			Query deleteQuerydeviceEventsQueue = session.createSQLQuery(devicceEventsQueue_DeleteQuery);
			deleteQuerydeviceEventsQueue.setParameter("school_id", id);
			log.info("devicceEventsQueue_DeleteQuery " + devicceEventsQueue_DeleteQuery);
			deleteQuerydeviceEventsQueue.executeUpdate();
			transaction.commit();

			/* for the table closed_device_events */
			transaction = session.beginTransaction();
			String devicceEventsClosed_DeleteQuery = "DELETE FROM closed_device_events WHERE device_event_id IN("
					+ "SELECT device_event_id FROM device_events WHERE uuid IN("
					+ "SELECT uuid FROM devices WHERE school_id =:school_id))";
			Query deleteQueryclosedDeviceEventsQueue = session.createSQLQuery(devicceEventsClosed_DeleteQuery);
			deleteQueryclosedDeviceEventsQueue.setParameter("school_id", id);
			log.info("devicceEventsQueue_DeleteQuery " + devicceEventsClosed_DeleteQuery);
			deleteQueryclosedDeviceEventsQueue.executeUpdate();
			transaction.commit();
			
			// for the table device_events
			transaction = session.beginTransaction();
			String devicceEvents_DeleteQuery = "DELETE FROM device_events WHERE uuid IN("
					+ "SELECT uuid FROM devices WHERE school_id =:school_id)";
			Query deleteQuerydeviceEvents = session.createSQLQuery(devicceEvents_DeleteQuery);
			deleteQuerydeviceEvents.setParameter("school_id", id);
			log.info("devicceEvents_DeleteQuery " + devicceEvents_DeleteQuery);
			deleteQuerydeviceEvents.executeUpdate();
			transaction.commit();

			// for the table geozones
			transaction = session.beginTransaction();
			String devicceGeoZones_DeleteQuery = "DELETE FROM geozones WHERE uuid IN("
					+ "SELECT uuid FROM devices WHERE school_id =:school_id)";
			Query deleteQueryForGeoZones = session.createSQLQuery(devicceGeoZones_DeleteQuery);
			deleteQueryForGeoZones.setParameter("school_id", id);
			log.info("devicceGeoZones_DeleteQuery " + devicceGeoZones_DeleteQuery);
			deleteQueryForGeoZones.executeUpdate();
			transaction.commit();
			
			/* for the table device_students */
			transaction = session.beginTransaction();
			String devicceStudents_DeleteQuery = "DELETE FROM device_students WHERE student_id IN("
					+ "SELECT student_id FROM students WHERE class_grade_id IN("
					+ "SELECT class_grade_id FROM class_grade WHERE school_id =:school_id))";
			Query deleteQuerydeviceStudents = session.createSQLQuery(devicceStudents_DeleteQuery);
			deleteQuerydeviceStudents.setParameter("school_id", id);
			log.info("devicceStudents_DeleteQuery " + devicceStudents_DeleteQuery);
			deleteQuerydeviceStudents.executeUpdate();
			transaction.commit();
			
			// // for the table event_subscriptions
			transaction = session.beginTransaction();
			String eventSubscription_DeleteQuery = "DELETE FROM event_subscriptions WHERE student_id IN("
					+ "SELECT student_id FROM students WHERE class_grade_id IN("
					+ "SELECT class_grade_id FROM class_grade WHERE school_id =:school_id))";
			Query deleteQueryForEventSubscription = session.createSQLQuery(eventSubscription_DeleteQuery);
			deleteQueryForEventSubscription.setParameter("school_id", id);
			log.info("eventSubscription_DeleteQuery " + eventSubscription_DeleteQuery);
			deleteQueryForEventSubscription.executeUpdate();
			transaction.commit();

			// for the table parent_kids
			transaction = session.beginTransaction();
			String parentKids_DeleteQuery = "DELETE FROM parent_kids WHERE student_id IN("
					+ "SELECT student_id FROM students WHERE class_grade_id IN("
					+ "SELECT class_grade_id FROM class_grade WHERE school_id =:school_id))";
			Query deleteQueryForParentKids = session.createSQLQuery(parentKids_DeleteQuery);
			deleteQueryForParentKids.setParameter("school_id", id);
			log.info("parentKids_DeleteQuery " + parentKids_DeleteQuery);
			deleteQueryForParentKids.executeUpdate();
			transaction.commit();
			
			
			// for the table rewards_students
			transaction = session.beginTransaction();
			String rewardStudents1_DeleteQuery = "DELETE FROM rewards_students WHERE reward_id IN("
					+ "SELECT reward_id FROM rewards WHERE rewards_category_id IN("
					+ "SELECT rewards_category_id FROM rewards_category WHERE school_id =:school_id))";
			Query deleteQueryForRewardStudents1 = session.createSQLQuery(rewardStudents1_DeleteQuery);
			deleteQueryForRewardStudents1.setParameter("school_id", id);
			log.info("rewardStudents1_DeleteQuery " + rewardStudents1_DeleteQuery);
			deleteQueryForRewardStudents1.executeUpdate();
			transaction.commit();
			
			// for the table rewards
			transaction = session.beginTransaction();
			String rewards_DeleteQuery = "DELETE FROM rewards WHERE rewards_category_id IN("
					+ "SELECT rewards_category_id FROM rewards_category WHERE school_id =:school_id)";
			Query deleteQueryForRewards = session.createSQLQuery(rewards_DeleteQuery);
			deleteQueryForRewards.setParameter("school_id", id);
			log.info("rewards_DeleteQuery " + rewards_DeleteQuery);
			deleteQueryForRewards.executeUpdate();
			transaction.commit();
			
			// for the table ips_receiver_device
			transaction = session.beginTransaction();
			String devicceIpsReceiver1_DeleteQuery = "DELETE FROM ips_receiver_device WHERE ips_receiver_id IN("
					+ "SELECT ips_receiver_id FROM ips_receiver WHERE school_id =:school_id)";
			Query deleteQueryForIPSRECEIVER1 = session.createSQLQuery(devicceIpsReceiver1_DeleteQuery);
			deleteQueryForIPSRECEIVER1.setParameter("school_id", id);
			log.info("devicceIpsReceiver1_DeleteQuery " + devicceIpsReceiver1_DeleteQuery);
			deleteQueryForIPSRECEIVER1.executeUpdate();
			transaction.commit();

			// for the table ips_receiver_zone
			transaction = session.beginTransaction();
			String ipszone_DeleteQuery = "DELETE FROM ips_receiver_zone WHERE ips_receiver_id IN("
					+ "SELECT ips_receiver_id FROM ips_receiver WHERE school_id =:school_id)";
			Query deleteQueryForIPSZONE = session.createSQLQuery(ipszone_DeleteQuery);
			deleteQueryForIPSZONE.setParameter("school_id", id);
			log.info("ipszone_DeleteQuery " + ipszone_DeleteQuery);
			deleteQueryForIPSZONE.executeUpdate();
			transaction.commit();
			
			// for the table reminders
			transaction = session.beginTransaction();
			String remonder_DeleteQuery = "DELETE FROM reminders WHERE class_grade_id IN("
					+ "SELECT class_grade_id FROM class_grade WHERE school_id =:school_id)";
			Query deleteQueryForReminders = session.createSQLQuery(remonder_DeleteQuery);
			deleteQueryForReminders.setParameter("school_id", id);
			log.info("remonder_DeleteQuery " + remonder_DeleteQuery);
			deleteQueryForReminders.executeUpdate();
			transaction.commit();

			// for the table timetable
			transaction = session.beginTransaction();
			String timetable_DeleteQuery = "DELETE FROM timetable WHERE class_grade_id IN ("
					+ "SELECT class_grade_id FROM class_grade WHERE school_id =:school_id)";
			Query deleteQueryForTimetable = session.createSQLQuery(timetable_DeleteQuery);
			deleteQueryForTimetable.setParameter("school_id", id);
			log.info("timetable_DeleteQuery " + timetable_DeleteQuery);
			deleteQueryForTimetable.executeUpdate();
			transaction.commit();

			// from students table
			transaction = session.beginTransaction();
			String students_DeleteQuery1 = "DELETE FROM students WHERE class_grade_id IN("
					+ "SELECT class_grade_id FROM class_grade WHERE school_id =:school_id)";
			Query deleteQueryForStudents1 = session.createSQLQuery(students_DeleteQuery1);
			deleteQueryForStudents1.setParameter("school_id", id);
			log.info("students_DeleteQuery1 " + students_DeleteQuery1);
			deleteQueryForStudents1.executeUpdate();
			transaction.commit();
			
			// for the table Reward category
			transaction = session.beginTransaction();
			String rewardsCategory_DeleteQuery = "DELETE FROM rewards_category WHERE school_id =:school_id";
			Query deleteQueryForRewardsCategory = session.createSQLQuery(rewardsCategory_DeleteQuery);
			deleteQueryForRewardsCategory.setParameter("school_id", id);
			log.info("rewardsCategory_DeleteQuery " + rewardsCategory_DeleteQuery);
			deleteQueryForRewardsCategory.executeUpdate();
			transaction.commit();
			
			// for the table class grade
			transaction = session.beginTransaction();
			String classgrade_DeleteQuery = "DELETE FROM class_grade WHERE school_id =:school_id";
			Query deleteQueryForClassGrade = session.createSQLQuery(classgrade_DeleteQuery);
			deleteQueryForClassGrade.setParameter("school_id", id);
			log.info("classgrade_DeleteQuery " + classgrade_DeleteQuery);
			deleteQueryForClassGrade.executeUpdate();
			transaction.commit();
			
			// for the table IPS RECEIVER
			transaction = session.beginTransaction();
			String ipsreceiver_DeleteQuery = "DELETE FROM ips_receiver WHERE school_id =:school_id";
			Query deleteQueryForIPSReceiver = session.createSQLQuery(ipsreceiver_DeleteQuery);
			deleteQueryForIPSReceiver.setParameter("school_id", id);
			log.info("ipsreceiver_DeleteQuery " + ipsreceiver_DeleteQuery);
			deleteQueryForIPSReceiver.executeUpdate();
			transaction.commit();

			// for the table Devices
			transaction = session.beginTransaction();
			String deviceaccount_DeleteQuery = "DELETE FROM devices WHERE school_id =:school_id";
			Query deleteQueryForDeviceAccounts = session.createSQLQuery(deviceaccount_DeleteQuery);
			deleteQueryForDeviceAccounts.setParameter("school_id", id);
			log.info("deleteQueryForDeviceAccounts " + deleteQueryForDeviceAccounts);
			deleteQueryForDeviceAccounts.executeUpdate();
			transaction.commit();

			// for the table school Calender
			transaction = session.beginTransaction();
			String schoolCalender_DeleteQuery = "DELETE FROM school_calendar WHERE school_id = :school_id";
			Query deleteQueryForSchoolCalnder = session.createSQLQuery(schoolCalender_DeleteQuery);
			deleteQueryForSchoolCalnder.setParameter("school_id", id);
			log.info("schoolCalender_DeleteQuery " + schoolCalender_DeleteQuery);
			deleteQueryForSchoolCalnder.executeUpdate();
			transaction.commit();

			// for the table SchoolDetails
			transaction = session.beginTransaction();
			String schoolDetails_DeleteQuery = "DELETE FROM school_details WHERE school_id = :school_id";
			Query deleteQueryForSchoolDetails = session.createSQLQuery(schoolDetails_DeleteQuery);
			deleteQueryForSchoolDetails.setParameter("school_id", id);
			log.info("deleteQueryForSchoolDetails " + deleteQueryForSchoolDetails);
			deleteQueryForSchoolDetails.executeUpdate();
			transaction.commit();

			// for the table Announcements
			transaction = session.beginTransaction();
			String announcement_DeleteQuery = "DELETE FROM announcement WHERE school_id = :school_id";
			Query deleteQueryForAnnouncement = session.createSQLQuery(announcement_DeleteQuery);
			deleteQueryForAnnouncement.setParameter("school_id", id);
			log.info("deleteQueryForAnnouncement " + deleteQueryForAnnouncement);
			deleteQueryForAnnouncement.executeUpdate();
			transaction.commit();

			// for the table Users
			transaction = session.beginTransaction();
			String users_DeleteQuery = "DELETE FROM users WHERE account_id =:school_id";
			Query deleteQueryForUsers = session.createSQLQuery(users_DeleteQuery);
			deleteQueryForUsers.setParameter("school_id", id);
			log.info("deleteQueryForUsers " + deleteQueryForUsers);
			deleteQueryForUsers.executeUpdate();
			transaction.commit();

			// for the table Accounts
			transaction = session.beginTransaction();
			String account_DeleteQuery = "DELETE FROM accounts WHERE account_id =:school_id";
			Query deleteQueryForAccounts = session.createSQLQuery(account_DeleteQuery);
			deleteQueryForAccounts.setParameter("school_id", id);
			log.info("deleteQueryForAccounts " + deleteQueryForAccounts);
			deleteQueryForAccounts.executeUpdate();
			transaction.commit();
			deleteSuccess = true;
		} catch (Exception e) {
			log.error("Exception Occured in deleteSchool ()" + "\t" + e);
			if(null != transaction) transaction.rollback();
		} finally {
			session.close();
		}
		log.info("Leaving deleteSchool");
		return deleteSuccess;
	}

	@Override
	public Accounts getSchool(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		return (Accounts) session.get(Accounts.class, id);
	}

	@Override
	public SchoolScheduleTransform viewScheduleList(int schoolId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String viewScheduleListQuery = null;

		viewScheduleListQuery = "SELECT sd.school_in_start as schoolinstart, sd.school_in_end as schoolinend, sd.school_out_start as schooloutstart, sd.school_out_end as schooloutend FROM school_details sd where  sd.school_id =:schoolId";

		List<SchoolScheduleTransform> viewScheduleList = null;

		Query query = session.createSQLQuery(viewScheduleListQuery).addScalar("schoolinstart").addScalar("schoolinend")
				.addScalar("schooloutstart").addScalar("schooloutend")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SchoolScheduleTransform.class));

		query.setParameter("schoolId", schoolId);

		viewScheduleList = (List<SchoolScheduleTransform>) query.list();
		tx.commit();
		session.close();
		if (viewScheduleList.size() > 0)
			return viewScheduleList.get(0);
		else
			return null;
	}

	@Override
	public SysConfigurationTransform viewSysConfigurationList() {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String viewScheduleListQuery = null;

		viewScheduleListQuery = "SELECT sd.iwps_sync_hours as adminScheduleDataSync, sd.wearable_session_validity_minutes as adminScheduleSessionValidity, sd.web_session_validity_minutes as adminScheduleUserSessionValidity, sd.password_reset_validity_minutes as adminSchedulePwdLinkValidity FROM system_configuration sd ";

		List<SysConfigurationTransform> viewScheduleList = null;

		Query query = session.createSQLQuery(viewScheduleListQuery).addScalar("adminScheduleDataSync")
				.addScalar("adminScheduleSessionValidity").addScalar("adminScheduleUserSessionValidity")
				.addScalar("adminSchedulePwdLinkValidity").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SysConfigurationTransform.class));

		viewScheduleList = (List<SysConfigurationTransform>) query.list();
		tx.commit();
		session.close();
		if (viewScheduleList.size() > 0)
			return viewScheduleList.get(0);
		else
			return null;
	}

	@Override
	public boolean isSchoolDetailsIdExist(int schoolId) {
		// TODO Auto-generated method stub
		boolean isSchoolDetailsIdExist = false;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(SchoolDetails.class);
		criteria.add(Restrictions.eq("schoolId", schoolId));
		SchoolDetails schoolDetails = (SchoolDetails) criteria.uniqueResult();
		if (schoolDetails != null) {
			isSchoolDetailsIdExist = true;
		}
		tx.commit();
		session.close();
		return isSchoolDetailsIdExist;
	}

	@Override
	public boolean isSchoolCalendarIdExist(int schoolId) {
		// TODO Auto-generated method stub
		boolean isSchoolCalendarIdExist = false;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(SchoolCalendar.class);
		criteria.add(Restrictions.eq("schoolId", schoolId));
		SchoolCalendar schoolCalendar = (SchoolCalendar) criteria.uniqueResult();
		if (schoolCalendar != null) {
			isSchoolCalendarIdExist = true;
		}
		tx.commit();
		session.close();
		return isSchoolCalendarIdExist;
	}

	@Override
	public void updateSchedule(SchoolDetails schoolDetails, boolean isUpdateSd) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		if (!isUpdateSd) {

			session.save(schoolDetails);
		} else {

			session.update(schoolDetails);
		}
		tx.commit();
		session.close();
	}

	@Override
	public void updateSystemConfiguration(SystemConfiguration systemConfiguration) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.update(systemConfiguration);
		tx.commit();
		session.close();
	}

	@Override
	public void addCalendar(SchoolCalendar schoolCalendar) {
		// TODO Auto-generated method stub
		log.info("In DAO IMPL");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(schoolCalendar);
		log.info("Name" + "\t" + schoolCalendar.getDateclose());
		tx.commit();
		session.close();
	}

	@Override
	public List<SchoolCalendar> getSchoolCalendarListBySchoolId(int schoolId) {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String schoolCalendarListQuery = null;

		schoolCalendarListQuery = "SELECT sc.name as name, sc.date_close as dateclose, sc.date_reopen as datereopen FROM school_calendar sc where  sc.school_id =:schoolId";

		List<SchoolCalendar> viewSchoolCalendarList = null;

		Query query = session.createSQLQuery(schoolCalendarListQuery).addScalar("name").addScalar("dateclose")
				.addScalar("datereopen").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SchoolCalendar.class));

		query.setParameter("schoolId", schoolId);

		viewSchoolCalendarList = (List<SchoolCalendar>) query.list();
		tx.commit();
		session.close();

		return viewSchoolCalendarList;

	}

	@Override
	public boolean updateSchoolCalendarList(int schoolId, List<SchoolCalendar> list) {
		// TODO Auto-generated method stub
		log.info("Inside school calendarbydateclose" + "\t");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("delete from school_calendar where school_id = ?");
		query.setParameter(0, schoolId);
		query.executeUpdate();
		tx.commit();
		tx = session.beginTransaction();
		try {
			log.info("deleted calendar data for schoolId" + "\t" + schoolId);
			List<SchoolCalendar> sc = list;
			if (null != sc && sc.size() > 0) {
				for (SchoolCalendar schoolCalendar : sc) {
					log.info("inserting schoolCalendar" + "\t" + schoolCalendar);
					session.save(schoolCalendar);
				}
			}
			tx.commit();

			return true;
		} catch (Exception exception) {
			tx.rollback();
		}

		finally {
			session.close();
		}
		return false;
	}

	@Override
	public int getTotalNoofParents(String profileName, String contactNo, String emailId, String deviceUUID) {
		Session session = sessionFactory.openSession();

		StringBuffer searchCriteria = new StringBuffer("");

		if (null != profileName && !profileName.equals("0")) {
			profileName = profileName.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
			searchCriteria.append("AND `users`.`name` LIKE ? ESCAPE '!'");
		}
		if (null != contactNo && !contactNo.equals("0")) {
			searchCriteria.append(" AND `users`.`mobile_number` = '" + contactNo + "'");
		}
		if (null != emailId && !emailId.equals("0")) {
			searchCriteria.append(
					" AND (`users`.`username` = '" + emailId + "' OR `users`.`openid_username` = '" + emailId + "')");
		}
		if (null != deviceUUID && !deviceUUID.equals("0")) {
			searchCriteria.append(" AND `device_students`.`device_uuid` = '" + deviceUUID + "'");
		}

		// Query to get total abnormal students
		String qStudents = "";
		if (null != deviceUUID && !deviceUUID.equals("0")) {
			qStudents = "        SELECT COUNT(*)\r\n" + "    FROM\r\n" + "        `users`,\r\n"
					+ "        `parent_kids`,\r\n" + "        `device_students`,\r\n" + "        `students`   \r\n"
					+ "    WHERE\r\n" + "        `users`.`user_id` = `parent_kids`.`user_id` \r\n"
					+ "        AND `parent_kids`.`student_id` = `students`.`student_id` \r\n"
					+ "        AND `students`.`student_id` = `device_students`.`student_id`   \r\n"
					+ "        AND `users`.`role_type` IN(\r\n" + "            'parent_admin', 'parent_member'\r\n"
					+ "        )" + searchCriteria + "ORDER BY `users`.`name`, `users`.`user_active` ";
		} else {
			qStudents = "SELECT COUNT(*) \r\n" + "    FROM\r\n" + "        `users`        \r\n" + "    WHERE\r\n"
					+ "        `users`.`role_type` IN(\r\n" + "            'parent_admin', 'parent_member'\r\n"
					+ "        )" + searchCriteria + "ORDER BY `users`.`name`, `users`.`user_active`  ";

		}
		try {
			Query query = session.createSQLQuery(qStudents);
			if (null != profileName && !profileName.equals("0")) {
				query.setParameter(0, "%" + profileName + "%");
			}

			BigInteger noOfRecords = (BigInteger) query.uniqueResult();
			return noOfRecords.intValue();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public Map<String, Object> searchParents(String profileName, String contactNo, String emailId, String deviceUUID,
			int start, int total) {
		HashMap<String, Object> lStudentsMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> lStudentsList = new ArrayList<HashMap<String, Object>>();
		Session session = sessionFactory.openSession();
		Query q = null;
		List<Object[]> rows = null;
		StringBuffer searchCriteria = new StringBuffer("");

		if (null != profileName && !profileName.equals("0")) {
			profileName = profileName.replace("!", "!!").replace("%", "!%").replace("_", "!_").replace("[", "![");
			searchCriteria.append("AND `users`.`name` LIKE ? ESCAPE '!'");
		}
		if (null != contactNo && !contactNo.equals("0")) {
			searchCriteria.append(" AND `users`.`mobile_number` = '" + contactNo + "'");
		}
		if (null != emailId && !emailId.equals("0")) {
			searchCriteria.append(
					" AND (`users`.`username` = '" + emailId + "' OR `users`.`openid_username` = '" + emailId + "')");
		}
		if (null != deviceUUID && !deviceUUID.equals("0")) {
			searchCriteria.append(" AND `device_students`.`device_uuid` = '" + deviceUUID + "'");
		}

		// Query to get total abnormal students
		String qStudents = "";
		if (null != deviceUUID && !deviceUUID.equals("0")) {
			qStudents = "        SELECT\r\n" + "        `users`.`user_id`,\r\n" + "        `users`.`name`,\r\n"
					+ "        IF(`users`.`username` = '', NULL, `users`.`username`),\r\n"
					+ "        `users`.`mobile_number`,\r\n" + "        `users`.`role_type`,\r\n"
					+ "        IF(`users`.`openid_username` = '', NULL, `users`.`openid_username`),\r\n"
					+ "        `users`.`lastlogin_date`,\r\n" + "        `users`.`user_active`,\r\n"
					+ "        `device_students`.`device_uuid`\r\n" + "    FROM\r\n" + "        `users`,\r\n"
					+ "        `parent_kids`,\r\n" + "        `device_students`,\r\n" + "        `students`   \r\n"
					+ "    WHERE\r\n" + "        `users`.`user_id` = `parent_kids`.`user_id` \r\n"
					+ "        AND `parent_kids`.`student_id` = `students`.`student_id` \r\n"
					+ "        AND `students`.`student_id` = `device_students`.`student_id`   \r\n"
					+ "        AND `users`.`role_type` IN(\r\n" + "            'parent_admin', 'parent_member'\r\n"
					+ "        )" + searchCriteria + "ORDER BY `users`.`name`, `users`.`user_active` LIMIT " + start
					+ "," + total;
		} else {
			qStudents = "SELECT\r\n" + "        `users`.`user_id`,\r\n" + "        `users`.`name`,\r\n"
					+ "        IF(`users`.`username` = '', NULL, `users`.`username`),\r\n"
					+ "        `users`.`mobile_number`,\r\n" + "        `users`.`role_type`,\r\n"
					+ "        IF(`users`.`openid_username` = '', NULL, `users`.`openid_username`),\r\n"
					+ "        `users`.`lastlogin_date`,\r\n" + "        `users`.`user_active`,\r\n"
					+ "        (SELECT COUNT(*) AS total_linked FROM `device_students`, `parent_kids`, `students`\r\n"
					+ "                   WHERE `students`.`student_id` = `device_students`.`student_id` \r\n"
					+ "         AND `parent_kids`.`student_id` = `students`.`student_id`\r\n"
					+ "         AND `users`.`user_id` = `parent_kids`.`user_id` ) AS devices_count\r\n" + "    FROM\r\n"
					+ "        `users`        \r\n" + "    WHERE\r\n" + "        `users`.`role_type` IN(\r\n"
					+ "            'parent_admin', 'parent_member'\r\n" + "        )" + searchCriteria
					+ "ORDER BY `users`.`name`, `users`.`user_active` LIMIT " + start + "," + total;

		}

		try {
			q = session.createSQLQuery(qStudents);
			if (null != profileName && !profileName.equals("0")) {
				q.setParameter(0, "%" + profileName + "%");
			}
			rows = q.list();
			String lprofileName = "";
			String lemailId = "";
			String lcontactNo = "";
			String luserType = "";
			String luserSource = "";
			String device_uuid = "";
			String lastlogin = "";
			String device_status = "";

			for (Object[] row : rows) {
				HashMap<String, Object> lSOSStudentsMap = new HashMap<String, Object>();
				Integer luser_id = (Integer) row[0];
				lprofileName = row[1].toString();
				lcontactNo = (null != row[3]) ? row[3].toString() : "";
				luserType = (row[4].toString().equalsIgnoreCase("parent_admin")) ? "Parent" : "Guardian";
				if (null != row[2] && null != row[5]) {
					luserSource = "Both";
					lemailId = row[2].toString();
				} else if (null != row[2]) {
					luserSource = "Internal";
					lemailId = row[2].toString();
				} else if (null != row[5]) {
					luserSource = "External";
					lemailId = row[5].toString();
				}
				log.info("the luserSource " + "\t" + luserSource.toString());

				lastlogin = (null != row[6]) ? row[6].toString() : "";
				device_status = (row[7].toString().equalsIgnoreCase("y")) ? "active" : "inactive";
				device_uuid = row[8].toString();

				log.info("all data got initialized " + "\t" + luser_id + " " + lprofileName + " " + lemailId + " "
						+ lcontactNo + " " + luserType + " " + luserSource + " " + device_uuid + " " + lastlogin + " "
						+ device_status);
				log.info("rows.size()" + "\t" + rows.size());

				lSOSStudentsMap.put("user_id", luser_id);
				lSOSStudentsMap.put("profileName", lprofileName);
				lSOSStudentsMap.put("emailId", lemailId);
				lSOSStudentsMap.put("contactNo", lcontactNo);
				lSOSStudentsMap.put("userType", luserType);
				lSOSStudentsMap.put("userSource", luserSource);
				lSOSStudentsMap.put("device_uuid", device_uuid);
				lSOSStudentsMap.put("lastlogin", lastlogin);
				lSOSStudentsMap.put("device_status", device_status);

				lStudentsList.add(lSOSStudentsMap);

			}
			lStudentsMap.put("search_result", lStudentsList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lStudentsMap;
	}

	@Override
	public SchoolDetails getSchoolDetailsBySchoolId(int schoolId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(SchoolDetails.class);
		criteria.add(Restrictions.eq("schoolId", schoolId));
		SchoolDetails sd = (SchoolDetails) criteria.uniqueResult();
		tx.commit();
		log.info("<<<< sd.getMobile_number() >>>>" + sd.getMobile_number());
		session.close();
		return sd;
	}

	@Override
	public SystemConfiguration getSystemConfigurationById(int sysId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(SystemConfiguration.class);
		criteria.add(Restrictions.eq("systemConfigurationId", sysId));
		SystemConfiguration sd = (SystemConfiguration) criteria.uniqueResult();
		tx.commit();
		session.close();
		return sd;
	}

	@Override
	public List<SearchSchoolTransform> searchSchool(int school_id) {
		Session session = null;
		Transaction tx = null;
		StringBuilder strB = null;
		String searchSchoolListQuery = null;
		List<SearchSchoolTransform> searchSchoolTransformList = null;
		Query query = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			strB = new StringBuilder();
			strB.append(
					"select acc.account_id as id, sd.mobile_number as contact, u.username as username, u.lastlogin_date as lastlogin,")
					.append("(select count(*) from devices as d where d.school_id = " + school_id
							+ " ) as alloteddevice, ")
					.append("sd.address as address, sd.county as county, sd.city as city, sd.state as state, sd.zipcode as zipcode"
							+ " , sd.country as country from accounts acc ")
					.append("left join school_details sd on sd.school_id = acc.account_id ")
					.append("left join users u on u.account_id = acc.account_id and u.role_type= " + "'"
							+ Constant.SchoolAdmin + "'")
					.append("where acc.account_id = ? ");

			searchSchoolListQuery = strB.toString();

			query = session.createSQLQuery(searchSchoolListQuery).addScalar("id").addScalar("contact")
					.addScalar("username").addScalar("lastlogin").addScalar("alloteddevice").addScalar("address")
					.addScalar("county").addScalar("city").addScalar("state").addScalar("zipcode").addScalar("country")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(SearchSchoolTransform.class));
			query.setParameter(0, school_id);
			searchSchoolTransformList = query.list();
		} catch (Exception e) {
			log.info("Exception Occured in searchSchool ()" + "\t" + e);
			tx.rollback();
		} finally {
			session.close();
		}

		return searchSchoolTransformList;
	}
}
