package com.liteon.icgwearable.dao;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.hibernate.entity.ClosedDeviceEvents;
import com.liteon.icgwearable.hibernate.entity.Geozones;
import com.liteon.icgwearable.hibernate.entity.Reminders;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.DeviceTypeAlertLatestModel;
import com.liteon.icgwearable.model.GeoZoneModel;
import com.liteon.icgwearable.model.KidsSafeModel;
import com.liteon.icgwearable.model.LatestAnnoncementModel;
import com.liteon.icgwearable.model.LatestGPSLocationModel;
import com.liteon.icgwearable.model.LatestGeoFenceAlertModel;
import com.liteon.icgwearable.model.LatestReminderModel;
import com.liteon.icgwearable.model.LatestRewardsModel;
import com.liteon.icgwearable.model.RemindersModel;
import com.liteon.icgwearable.model.SchoolTimeModel;
import com.liteon.icgwearable.model.StudentsSleepDataModel;
import com.liteon.icgwearable.transform.RemindersTransform;
import com.liteon.icgwearable.transform.RewardsCategoryTransform;
import com.liteon.icgwearable.transform.SchoolWorkingHoursTransform;
import com.liteon.icgwearable.transform.StudentRewardsTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.ValidParentUsersWithTokenTransform;
import com.liteon.icgwearable.util.Constant;

@Repository("commonDAO")
@Transactional
public class CommonDAOImpl implements CommonDAO {

	private static Logger log = Logger.getLogger(CommonDAOImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;
	
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
	
	@Value("${FALL_DETECTION_ID}")
	private Integer FALL_ID;
	@Value("${SENSOR_MALFUNCTION_ID}")
	private Integer SENSOR_MAL_ID;
	@Value("${ABNORMAL_VITAL_SIGN_ID}")
	private Integer ABNORMAL_ID;
	@Value("${LOW_BATTERY_ID}")
	private Integer LOW_B_ID;
	@Value("${STUDENT_LOCATION_ID}")
	private Integer STUDENT_L_ID;
	@Value("${BAND_REMOVAL_ALERT_ID}")
	private Integer BAND_RA_ID;

	@Override
	public List<GeoZoneModel> getUserGeoZones(int user_id, String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<GeoZoneModel> list = null;
		String geoZoneQuery = "\r\n"
				+ "select gz.geozone_id as geozone_id ,gz.user_id as user_id,gz.uuid as uuid,gz.zone_radius as zone_radius, gz.zone_details as zone_details,gz.zone_name as zone_name,\r\n"
				+ "gz.zone_entry_alert as zone_entry_alert,gz.zone_exit_alert as zone_exit_alert,gz.zone_description as zone_description,gz.frequency_minutes as frequency_minutes,\r\n"
				+ "gz.valid_till as valid_till from geozones as gz where gz.user_id=:user_id and gz.uuid=:uuid";
		log.info("kidsQuery" + geoZoneQuery);

		Query query = session.createSQLQuery(geoZoneQuery).addScalar("geozone_id").addScalar("user_id")
				.addScalar("uuid").addScalar("zone_details").addScalar("zone_name").addScalar("zone_radius")
				.addScalar("zone_entry_alert").addScalar("zone_exit_alert").addScalar("zone_description")
				.addScalar("frequency_minutes").addScalar("valid_till")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(GeoZoneModel.class));
		query.setParameter("user_id", user_id);
		query.setParameter("uuid", uuid);
		log.info("kidsQuery" + geoZoneQuery);
		list = query.list();
		tx.commit();
		session.close();
		return list;
	}

	@Override
	public void createGeoZone(Geozones geoZones) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(geoZones);
		tx.commit();
		session.close();
	}

	@Override
	public void updateGeoZone(Geozones geoZones) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.update(geoZones);
		tx.commit();
		session.close();
	}

	@Override
	public void deleteGeozone(int geoZoneId) {
		Session session = sessionFactory.openSession();
		String DeleteQuery_GeoZones = "DELETE from Geozones as gz where gz.geozoneId=:geoZoneId";
		Transaction deleteTransactionGeoZones = session.beginTransaction();
		session.createQuery(DeleteQuery_GeoZones).setParameter("geoZoneId", geoZoneId).executeUpdate();
		log.info("DeleteQuery_GeoFence Query " + DeleteQuery_GeoZones);
		deleteTransactionGeoZones.commit();
		session.close();
	}

	@Override
	public void createReminder(Reminders reminder) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(reminder);
		tx.commit();
		session.close();
	}

	@Override
	public void deleteReminder(int reminderId) {
		log.info("Enter deleteReminder {");
		Session session = sessionFactory.openSession();
		String DeleteQuery_GeoZones = "DELETE from reminders where reminder_id= " + reminderId;
		Transaction deleteTransactionGeoZones = session.beginTransaction();
		int i =session.createSQLQuery(DeleteQuery_GeoZones).executeUpdate();
		log.info("Value of i is:"+"\t"+i);
		log.info("DeleteQuery_Reminder Query " + DeleteQuery_GeoZones);
		deleteTransactionGeoZones.commit();
		session.close();
		log.info("Exit deleteReminder }");
	}

	@Override
	public List<RemindersTransform> getReminders(Users user) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		String remindersQuery = null;
		List<RemindersTransform> remindersTransformList = null;
		
		if(user.getRoleType().equals(Constant.SchoolTeacher)) {
			remindersQuery = "select rm.reminder_id as reminderId, cg.school_id as schoolId, cg.class as stClass,rm.comments as comments, rm.image_name as image, " + 
							 "rm.created_date as createdDt from reminders rm " +
							 "left join class_grade cg on cg.class_grade_id = rm.class_grade_id " +
							 "where cg.teacher_id= ? " ;
		}
		
		Query query = session.createSQLQuery(remindersQuery).addScalar("reminderId").addScalar("schoolId")
				.addScalar("stClass").addScalar("comments").addScalar("image").addScalar("createdDt")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RemindersTransform.class));
		query.setParameter(0, user.getId());
		log.info("RemindersQuery" + remindersQuery);
		remindersTransformList = query.list();
		
		tx.commit();
		session.close();
		return remindersTransformList;
	}

	@Override
	public void updateReminder(Reminders reminder) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.update(reminder);
		tx.commit();
		session.close();
	}

	@Override
	public int getTotalNoOfZeofencesPerUser(int user_id, String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<Object> list = null;
		String geoZoneQuery = "\r\n"
				+ "select count(gz.geozone_id) from geozones as gz where gz.user_id=:user_id and gz.uuid=:uuid";
		log.info("geoZoneQuery" + geoZoneQuery);
		Query query = session.createSQLQuery(geoZoneQuery);
		query.setParameter("user_id", user_id);
		query.setParameter("uuid", uuid);
		log.info("kidsQuery" + geoZoneQuery);
		list = query.list();
		tx.commit();
		session.close();
		return Integer.parseInt(list.get(0).toString());
	}

	@Override
	public String getImageNameByReminderId(int reminder_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<Object> list = null;
		String imageQuery = "\r\n" + "select image_name from reminders as rm where reminder_id=:reminderId";
		log.info("geoZoneQuery" + imageQuery);
		Query query = session.createSQLQuery(imageQuery);
		query.setParameter("reminderId", reminder_id);
		log.info("kidsQuery" + imageQuery);
		list = query.list();
		tx.commit();
		session.close();
		if(list.size()>0 && null != list.get(0))
		return list.get(0).toString();
		else
		return null;
	}

	@Override
	public List<StudentRewardsTransform> getStudentsRewardsByDate(int userId, int studentId, String date) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<StudentRewardsTransform> rewardsList;

		String studentsRewardsByDate = "SELECT rewards.name AS rewardname, rewards.reward_id AS rewardId, " + 
										"rc.category_name AS categoryName, rc.category_icon_url AS categoryIconUrl, " + 
										"rs.received_count AS receivedCount, rewards.reward_icon_url as reward_icon_url, school_user.name as school_teacher, " + 
										"IF(rs.created_date > rs.updated_date, rs.created_date, rs.updated_date) AS reward_date " + 
										"FROM users AS parent_user " +  
										"LEFT JOIN  accounts AS parent_account ON parent_account.account_id = parent_user.account_id " + 
										"LEFT JOIN parent_kids pk on pk.user_id=parent_user.user_id " +
										"LEFT JOIN students st on st.student_id = pk.student_id " +
										"LEFT JOIN class_grade cg on cg.class_grade_id = st.class_grade_id " + 
										"LEFT JOIN users school_user on school_user.user_id = cg.teacher_id " +
										"LEFT JOIN  rewards_students AS rs ON rs.teacher_id = cg.teacher_id AND rs.student_id = st.student_id " + 
										"LEFT JOIN  rewards ON rewards.reward_id = rs.reward_id " + 
										"LEFT JOIN  rewards_category AS rc  ON rc.rewards_category_id = rewards.rewards_category_id " + 
										"WHERE  parent_user.user_id = ? AND st.student_id = ? " +  
										"AND rewards.reward_id IS NOT NULL " + 
										"AND (date(rs.created_date) ="+ "'"+date+"'"+" OR date(rs.updated_date) =" +" '"+date+"'"+")" ;
		

		Query query = session.createSQLQuery(studentsRewardsByDate).addScalar("rewardname").addScalar("rewardId")
				.addScalar("categoryName")
				.addScalar("categoryIconUrl")
				.addScalar("receivedCount")
				.addScalar("reward_icon_url")
				.addScalar("school_teacher")
				.addScalar("reward_date")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentRewardsTransform.class));

		query.setParameter(0, userId);
		query.setParameter(1, studentId);

		rewardsList = query.list();
		tx.commit();
		session.close();
		return rewardsList;
	}

	@Override
	public Reminders getReminderByID(int reminderid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Reminders.class, "rm");
		criteria.add(Restrictions.eq("rm.reminderId", reminderid));
		@SuppressWarnings("unchecked")
		List<Reminders> reminders = criteria.list();
		tx.commit();
		session.close();
		return reminders.get(0);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ValidParentUsersWithTokenTransform> getParentUsersListForReminders(Integer cgId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<ValidParentUsersWithTokenTransform> usersList = null;
				
		String usersQuery = "select u.user_id as userId, u.role_type as roleType, u.android_app_token as androidAppToken, "
				+ "u.iphone_app_token as iPhoneAppToken, s.name as studentName from class_grade cg " +
							"left join students s on s.class_grade_id = cg.class_grade_id " +
							"left join parent_kids pk on pk.student_id = s.student_id " +
							"left join users u on u.user_id = pk.user_id " +
							"where u.role_type='parent_admin'  and u.user_active='y' "
							+ "and ((u.android_app_token is not null and u.android_app_token != '') OR "
							+ "(u.iphone_app_token is not null and u.iphone_app_token != ''))" +
							"and cg.class_grade_id = ? " ;
		Query query = session.createSQLQuery(usersQuery).addScalar("userId").addScalar("roleType")
				.addScalar("androidAppToken").addScalar("iPhoneAppToken").addScalar("studentName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(ValidParentUsersWithTokenTransform.class));
		query.setParameter(0, cgId);
		usersList = query.list();
		tx.commit();
		session.close();
		return usersList;
	}

	@Override
	public List<RemindersModel> getRemindersForParent(int user_id, String timeline, String studentname) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RemindersModel> usersList;
		String usersQuery = "";
		boolean stdentSelected = false;
		
		usersQuery = "SELECT cg.school_id AS school_id, cg.teacher_id AS teacherId, cg.class AS studentClass, "
				+ "rm.comments AS comments, rm.image_name AS image, rm.created_date AS createdDate, "
				+ "rm.updated_date AS updatedDate "
				+ "FROM reminders AS rm, class_grade AS cg "
				+ "LEFT JOIN students AS s on s.class_grade_id = cg.class_grade_id "
				+ "LEFT JOIN parent_kids AS pk on pk.student_id = s.student_id "
				+ "LEFT JOIN users AS u on u.user_id=pk.user_id "
				+ "WHERE rm.class_grade_id = cg.class_grade_id "
				+ "AND u.user_id = ? "
				+ "AND (DATE(rm.created_date) = ? OR DATE(rm.updated_date) = ?)"
				+ "AND rm.created_date >= CURDATE()";

		Query query = session.createSQLQuery(usersQuery).addScalar("school_id").addScalar("teacherId")
				.addScalar("studentClass").addScalar("comments").addScalar("image").addScalar("createdDate")
				.addScalar("updatedDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RemindersModel.class));
		query.setParameter(0, user_id);
		query.setParameter(1, timeline);
		query.setParameter(2, timeline);

		if (stdentSelected)
			query.setParameter("studentname", studentname);
		log.info("Query to execute is :: " + query);
		usersList = query.list();
		tx.commit();
		session.close();
		return usersList;

	}

	@Override
	public List<String> getKidsForParent(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String kidsListForParentQuery = "select distinct s.nickname as name from students s " + 
										"left join parent_kids pk on pk.student_id = s.student_id " +
										"left join users u on u.user_id = pk.user_id " +
										"where u.user_id= ? " ;
		List<String> kidsListForParent = null;
		Query query = session.createSQLQuery(kidsListForParentQuery);
		query.setParameter(0, userId);
		kidsListForParent = (List<String>) query.list();
		tx.commit();
		session.close();
		return kidsListForParent;
	}

	@Override
	public String getStudentClassByParentid(String studentName, int parent_account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String classNameOfStudentForaParentQuery = "select class from students where name=:name and account_id=:account_id";
		List<String> classListOfaStudentForParent = null;

		Query query = session.createSQLQuery(classNameOfStudentForaParentQuery);

		query.setParameter("name", studentName);
		query.setParameter("account_id", parent_account_id);
		classListOfaStudentForParent = (List<String>) query.list();
		tx.commit();
		session.close();
		return classListOfaStudentForParent.get(0).toString();

	}

	@Override
	public List<RemindersModel> getRemindersForTeacher(int teacher_id, String timeline, String classname) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RemindersModel> usersList;
		String usersQuery = "";
		boolean classSelected = false;

		if (null == timeline && null == classname) {
			usersQuery = "\r\n"
					+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
					+ "where rm.teacher_id=:teacher_id and rm.created_date >= CURDATE()";
		}

		if (null != timeline && null != classname) {
			if (timeline.equals("CurrentDay") && classname.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date >= CURDATE()";
			}
			if (timeline.equals("YesterDay") && classname.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date <= CURDATE()-1";
			}

			if (timeline.equals("Last 1 Week") && classname.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date <= CURDATE()-7";
			}

			if (timeline.equals("Last 30 Days") && classname.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date <= CURDATE()-30";
			}

			if (timeline.equals("CurrentDay") && !classname.equals("NONE")) {
				classSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.class=:classname and rm.created_date >= CURDATE()";
			}
			if (timeline.equals("YesterDay") && !classname.equals("NONE")) {
				classSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.class=:classname and rm.created_date <= CURDATE()-1";
			}

			if (timeline.equals("Last 1 Week") && !classname.equals("NONE")) {
				classSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.class=:classname and rm.created_date <= CURDATE()-7";
			}

			if (timeline.equals("Last 30 Days") && !classname.equals("NONE")) {
				classSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.class=:classname and rm.created_date <= CURDATE()-30";
			}

		}
		Query query = session.createSQLQuery(usersQuery).addScalar("school_id").addScalar("teacherId")
				.addScalar("studentClass").addScalar("comments").addScalar("image").addScalar("createdDate")
				.addScalar("updatedDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RemindersModel.class));
		query.setParameter("teacher_id", teacher_id);

		if (classSelected)
			query.setParameter("classname", classname);
		log.info("Query to execute is :: " + query);
		usersList = query.list();
		tx.commit();
		session.close();
		return usersList;

	}

	@Override
	public List<String> getClassesForTeacher(int teacher_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String classListForTeacherQuery = "select class from reminders rm where rm.teacher_id=:teacher_id";
		List<String> classListForTeacher = null;
		Query query = session.createSQLQuery(classListForTeacherQuery);
		query.setParameter("teacher_id", teacher_id);
		classListForTeacher = (List<String>) query.list();
		tx.commit();
		session.close();
		return classListForTeacher;
	}

	@Override
	public List<RemindersModel> getRemindersForSchoolAdmin(List<Integer> teachers, String timeline, int teacherid,
			String teachername) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RemindersModel> usersList = new ArrayList<>();
		List<RemindersModel> tempUsersList = null;
		String usersQuery = "";
		boolean teacherSelected = false;
		List<Integer> teacherIdlistforloop = new ArrayList<>();

		if (null == timeline && null == teachername) {
			usersQuery = "\r\n"
					+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
					+ "where rm.teacher_id=:teacher_id and rm.created_date >= CURDATE()";
		}

		if (null != timeline && null != teachername) {
			if (timeline.equals("CurrentDay") && teachername.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date >= CURDATE()";
			}
			if (timeline.equals("YesterDay") && teachername.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date <= CURDATE()-1";
			}

			if (timeline.equals("Last 1 Week") && teachername.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date <= CURDATE()-7";
			}

			if (timeline.equals("Last 30 Days") && teachername.equals("NONE")) {
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date <= CURDATE()-30";
			}

			if (timeline.equals("CurrentDay") && !teachername.equals("NONE")) {
				teacherSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id and rm.created_date >= CURDATE()";
			}
			if (timeline.equals("YesterDay") && !teachername.equals("NONE")) {
				teacherSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id  and rm.created_date <= CURDATE()-1";
			}

			if (timeline.equals("Last 1 Week") && !teachername.equals("NONE")) {
				teacherSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id  and rm.created_date <= CURDATE()-7";
			}

			if (timeline.equals("Last 30 Days") && !teachername.equals("NONE")) {
				teacherSelected = true;
				usersQuery = "\r\n"
						+ "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image ,rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm\r\n"
						+ "where rm.teacher_id=:teacher_id  and rm.created_date <= CURDATE()-30";
			}

		}
		if (!teacherSelected)
			teacherIdlistforloop = teachers;
		else
			teacherIdlistforloop.add(teacherid);
		for (int tempteacher_id : teacherIdlistforloop) {
			Query query = session.createSQLQuery(usersQuery).addScalar("school_id").addScalar("teacherId")
					.addScalar("studentClass").addScalar("comments").addScalar("image").addScalar("createdDate")
					.addScalar("updatedDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(RemindersModel.class));
			query.setParameter("teacher_id", tempteacher_id);
			log.info("Query to execute is :: " + query);
			tempUsersList = query.list();
			usersList.addAll(tempUsersList);
		}
		tx.commit();
		session.close();
		return usersList;
	}

	@Override
	public List<String> getTeachersForSchoolAdmin(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String teacherListForAdminQuery = "select name from users where role_type='school_teacher' and account_id=:account_id";
		List<String> teacherListForAdmin = null;
		Query query = session.createSQLQuery(teacherListForAdminQuery);
		query.setParameter("account_id", account_id);
		teacherListForAdmin = (List<String>) query.list();
		tx.commit();
		session.close();
		return teacherListForAdmin;
	}

	@Override
	public List<Integer> getTeacherIdsForSchoolAdmin(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String teacheridListForAdminQuery = "select user_id from users where role_type='school_teacher' and account_id=:account_id";
		List<Integer> teacheridListForAdmin = null;
		Query query = session.createSQLQuery(teacheridListForAdminQuery);
		query.setParameter("account_id", account_id);
		teacheridListForAdmin = (List<Integer>) query.list();
		tx.commit();
		session.close();
		return teacheridListForAdmin;
	}

	@Override
	public int getTeacherIdbyNameandAccount(String name, int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String teacheridListForAdminQuery = "select user_id from users where role_type='school_teacher' and name=:name and account_id=:account_id";
		List<Integer> teacheridListForAdmin = null;
		Query query = session.createSQLQuery(teacheridListForAdminQuery);
		query.setParameter("name", name);
		query.setParameter("account_id", account_id);
		teacheridListForAdmin = (List<Integer>) query.list();
		tx.commit();
		session.close();
		return teacheridListForAdmin.get(0);
	}

	@Override
	public SchoolTimeModel getSchoolTimelatestEvent(int student_id) {
		String latestSchoolEntryQuery ="select de.event_occured_date as enterDate ,de.gps_location_data as entry_gps_location from device_events de \r\n" + 
				"LEFT JOIN devices d on de.uuid= d.uuid \r\n" + 
				"LEFT JOIN device_students ds on ds.device_uuid = d.uuid and ds.status='active'\r\n" + 
				"where ds.student_id=:student_id AND de.event_id="+SCHOOL_ENTRY_ID+" and DATE(de.event_occured_date)=DATE(NOW()) ORDER BY event_occured_date DESC LIMIT 1";
		String latestSchoolExitQuery = "select de.event_occured_date as exitDate,de.gps_location_data as exit_gps_location from device_events de \r\n" + 
				"LEFT JOIN devices d on de.uuid= d.uuid \r\n" + 
				"LEFT JOIN device_students ds on ds.device_uuid = d.uuid and ds.status='active'\r\n" + 
				"where ds.student_id=:student_id1 AND de.event_id="+SCHOOL_EXIT_ID+" and DATE(de.event_occured_date)=DATE(NOW()) ORDER BY event_occured_date DESC LIMIT 1";
		
		SchoolTimeModel schoolTimeModel = new SchoolTimeModel();

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<SchoolTimeModel> latestSchoolEntry = null;
		Query query = session.createSQLQuery(latestSchoolEntryQuery)
				.addScalar("entry_gps_location").addScalar("enterDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SchoolTimeModel.class));
		query.setParameter("student_id", student_id);
		latestSchoolEntry = (List<SchoolTimeModel>) query.list();
		tx.commit();

		Transaction tx1 = session.beginTransaction();
		List<SchoolTimeModel> latestSchoolExit = null;
		Query query1 = session.createSQLQuery(latestSchoolExitQuery)
				.addScalar("exit_gps_location").addScalar("exitDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SchoolTimeModel.class));;
		query1.setParameter("student_id1", student_id);
		latestSchoolExit = (List<SchoolTimeModel>) query1.list();
		tx1.commit();

		session.close();
		
		if (null != latestSchoolEntry && latestSchoolEntry.size()>0) {
			log.info("latestSchoolEntry.get(0).getEnterDate()"+latestSchoolEntry.get(0).getEnterDate());
			log.info("latestSchoolEntry.get(0).getEntry_gps_location()"+latestSchoolEntry.get(0).getEntry_gps_location());
			schoolTimeModel.setEnterDate(latestSchoolEntry.get(0).getEnterDate());
			schoolTimeModel.setEntry_gps_location(latestSchoolEntry.get(0).getEntry_gps_location());
		}
		if(null != latestSchoolExit && latestSchoolExit.size()>0 )
			{
			log.info("latestSchoolExit.get(0).getExitDate()"+latestSchoolExit.get(0).getExitDate());
			log.info("latestSchoolExit.get(0).getExit_gps_location()"+latestSchoolExit.get(0).getExit_gps_location());
			schoolTimeModel.setExitDate(latestSchoolExit.get(0).getExitDate());
			schoolTimeModel.setExit_gps_location(latestSchoolExit.get(0).getExit_gps_location());
		}
		
		log.info(schoolTimeModel.getEntry_gps_location());
		log.info(schoolTimeModel.getEnterDate());
		log.info(schoolTimeModel.getExit_gps_location());
		log.info(schoolTimeModel.getExitDate());
			
		return schoolTimeModel;
	}

	@Override
	public LatestGeoFenceAlertModel getGeofencelatestEvent(int student_id) {
		log.info("getGeofencelatestEvent ++");
		String geoZoneEntryExitQuery = "select de.event_occured_date as enterDate, de.gps_location_data as entry_gps_location, \r\n" +
				"de.event_occured_date as exitDate, de.out_time as outTime, \r\n" +
				"de.gps_location_data as exit_gps_location from device_events de \r\n" + 
				"LEFT JOIN devices d on de.uuid= d.uuid \r\n" + 
				"LEFT JOIN device_students ds on ds.device_uuid = d.uuid and ds.status='active' \r\n" + 
				"where ds.student_id=:student_id AND de.event_id=" + GEOFENCE_ENTRY_ID + " and DATE(de.event_occured_date)=DATE(NOW()) ORDER BY event_occured_date DESC LIMIT 1 ";

		LatestGeoFenceAlertModel geofenceModel = new LatestGeoFenceAlertModel();

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<LatestGeoFenceAlertModel> latestGeozoneEntryExit = null;
		Query query = session.createSQLQuery(geoZoneEntryExitQuery)
				.addScalar("entry_gps_location").addScalar("enterDate")
				.addScalar("exitDate").addScalar("outTime").addScalar("exit_gps_location")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(LatestGeoFenceAlertModel.class));
		query.setParameter("student_id", student_id);
		latestGeozoneEntryExit = (List<LatestGeoFenceAlertModel>) query.list();
		tx.commit();

		if (null != latestGeozoneEntryExit && latestGeozoneEntryExit.size() > 0) {
			geofenceModel.setEnterDate(latestGeozoneEntryExit.get(0).getEnterDate());
			geofenceModel.setEntry_gps_location(latestGeozoneEntryExit.get(0).getEntry_gps_location());
			geofenceModel.setExitDate(latestGeozoneEntryExit.get(0).getExitDate());
			geofenceModel.setOutTime(latestGeozoneEntryExit.get(0).getOutTime());
			geofenceModel.setExit_gps_location(latestGeozoneEntryExit.get(0).getExit_gps_location());
		}
		
		session.close();
		log.info("getGeofencelatestEvent --");
		return geofenceModel;
	}

	@Override
	public DeviceTypeAlertLatestModel getDeviceAlertslatestEvent(int student_id) {
		String deviceEventAlertQuery = " select de.event_occured_date as eventOccuredDate , de.event_id as eventid ,de.gps_location_data as gps_location from device_events de \r\n" + 
				"				LEFT JOIN device_students ds on ds.device_uuid = de.uuid and ds.status='active'\r\n" + 
				"				LEFT JOIN supported_events se on se.event_id= de.event_id \r\n" + 
				"				where  ds.student_id=:student_id  and se.generated_by='device' and se.notify_parent='yes' \r\n" +
				"				and se.event_id not in (1,2,3,4) and DATE(de.event_occured_date)=DATE(NOW()) \r\n"+
				"				ORDER BY event_occured_date DESC LIMIT 1";

		DeviceTypeAlertLatestModel deviceAlertModel = null;

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<DeviceTypeAlertLatestModel> deviceEventAlert = null;
		Query query = session.createSQLQuery(deviceEventAlertQuery).addScalar("eventOccuredDate").addScalar("eventid").addScalar("gps_location")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceTypeAlertLatestModel.class));
		query.setParameter("student_id", student_id);
		deviceEventAlert = (List<DeviceTypeAlertLatestModel>) query.list();
		tx.commit();
		session.close();
		log.info("deviceEventAlert" + deviceEventAlert);
		if (deviceEventAlert.size() > 0)
			return deviceEventAlert.get(0);
		else
			return deviceAlertModel;

	}

	@Override
	public LatestGPSLocationModel getLatestGPSLocations(int student_id) {
		String deviceEventAlertQuery = "select de.event_occured_date as eventOccuredDate , de.gps_location_data as gps_location from device_events de \r\n" + 
				"LEFT JOIN device_students ds on ds.device_uuid = de.uuid and ds.status='active'\r\n" + 
				"				LEFT JOIN supported_events se on se.event_id= de.event_id \r\n" + 
				"where ds.student_id=:student_id AND de.event_id="+STUDENT_L_ID+" and DATE(de.event_occured_date)=DATE(NOW()) ORDER BY event_occured_date DESC LIMIT 1 ";

		LatestGPSLocationModel latestLocatinModel = null;

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<LatestGPSLocationModel> deviceLocation = null;
		Query query = session.createSQLQuery(deviceEventAlertQuery).addScalar("gps_location").addScalar("eventOccuredDate")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(LatestGPSLocationModel.class));
		query.setParameter("student_id", student_id);
		deviceLocation = (List<LatestGPSLocationModel>) query.list();
		tx.commit();
		session.close();
		log.info("deviceLocation" + deviceLocation);
		if (null != deviceLocation && deviceLocation.size() > 0)
			return deviceLocation.get(0);
		else
			return latestLocatinModel;
	}
	
	@Override
	public LatestReminderModel getLatestReminders(int student_id) {
		String latestReminderCreateQuery = "select IF(r.updated_date > r.created_date, r.updated_date, r.created_date) as latestDate,comments as comments from reminders  r \r\n" + 
				"				LEFT JOIN class_grade cg on cg.class_grade_id = r.class_grade_id\r\n" + 
				"				LEFT JOIN students s on cg.class_grade_id = s.class_grade_id\r\n" + 
				"				where s.student_id=:student_id AND "+ 
				" (DATE(r.created_date) = DATE(NOW()) OR DATE(r.updated_date) = DATE(NOW())) "
				+ "order by r.updated_date DESC LIMIT 1";
		
		LatestReminderModel latestReminderCreateModel = null;

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<LatestReminderModel> latestReminderCreate = null;
		Query query = session.createSQLQuery(latestReminderCreateQuery).addScalar("latestDate").addScalar("comments")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(LatestReminderModel.class));
		query.setParameter("student_id", student_id);
		latestReminderCreate = (List<LatestReminderModel>) query.list();
		tx.commit();
		session.close();
		if (null != latestReminderCreate && latestReminderCreate.size() > 0){
			latestReminderCreateModel = (LatestReminderModel) latestReminderCreate.get(0);
			return latestReminderCreateModel;
		}
			
		return null;
	}

	@Override
	public LatestAnnoncementModel getLatestAnnouncements(int student_id) {
		String latestAnnouncementCreateQuery = "select a.updated_date as latestDate,a.name as name  from announcement a \r\n" + 
				"				LEFT JOIN class_grade cg on cg.school_id = a.school_id\r\n" + 
				"				LEFT JOIN students s on cg.class_grade_id = s.class_grade_id\r\n" + 
				"				where  s.student_id=:student_id AND "
				+ "DATE(a.updated_date) = DATE(NOW()) "
				+ "order by a.updated_date DESC LIMIT 1";

		LatestAnnoncementModel latestAnnouncementCreateModel = null;

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<LatestAnnoncementModel> latestAnnouncementCreate = null;
		Query query = session.createSQLQuery(latestAnnouncementCreateQuery).addScalar("latestDate").addScalar("name")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(LatestAnnoncementModel.class));
		query.setParameter("student_id", student_id);
		latestAnnouncementCreate = (List<LatestAnnoncementModel>) query.list();
		tx.commit();
		session.close();
		if (null != latestAnnouncementCreate && latestAnnouncementCreate.size() > 0){
			latestAnnouncementCreateModel = (LatestAnnoncementModel) latestAnnouncementCreate.get(0);
			return latestAnnouncementCreateModel;
		}
		
		return null;
	}

	@Override
	public LatestRewardsModel getLatestRewards(int student_id) {
		String latestReweardsCreateQuery = "select rs.updated_date as latestDate ,rs.received_count as rewardsCount , rc.category_name as rewardCategory ,r.name as rewardName from rewards r \r\n" + 
				"LEFT JOIN rewards_students rs on r.reward_id=rs.reward_id \r\n" + 
				"LEFT JOIN rewards_category rc on rc.rewards_category_id=r.rewards_category_id \r\n" + 
				"where rs.student_id=:student_id "
				+ "AND DATE(rs.updated_date) = DATE(NOW()) "
				+ "order by rs.updated_date DESC LIMIT 1";

		LatestRewardsModel latestRewardsCreateModel = null;

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<LatestRewardsModel> latestRewardstCreate = null;
		Query query = session.createSQLQuery(latestReweardsCreateQuery).addScalar("latestDate").addScalar("rewardsCount").addScalar("rewardCategory").addScalar("rewardName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(LatestRewardsModel.class));
		query.setParameter("student_id", student_id);
		latestRewardstCreate = (List<LatestRewardsModel>) query.list();
		tx.commit();

		session.close();
		if (null != latestRewardstCreate && latestRewardstCreate.size() > 0){
			latestRewardsCreateModel = (LatestRewardsModel) latestRewardstCreate.get(0);
			return latestRewardsCreateModel;
		}

		return null;
	}

	@Override
	public List<KidsSafeModel> getKidsSafeNotificationData(int student_id,String date) {
		String geoZoneEntryQuery = "SELECT de.event_id as event_id, de.gps_location_data as gps_location, \r\n" +
				"de.event_occured_date as eventOccuredDate, de.out_time as outTime FROM device_events de  \r\n" + 
				"LEFT JOIN devices d on de.uuid= d.uuid \r\n" + 
				"LEFT JOIN device_students ds on ds.device_uuid = d.uuid and ds.status='active'\r\n" + 
				"WHERE event_id IN ("+SCHOOL_ENTRY_ID+","+SCHOOL_EXIT_ID+", "+GEOFENCE_ENTRY_ID+", "+GEOFENCE_EXIT_ID+","+SOS_ID+" ,"+FALL_ID+","+SENSOR_MAL_ID+","+ABNORMAL_ID+" ,"+LOW_B_ID+" ,"+STUDENT_L_ID+","+BAND_RA_ID+") and ds.student_id=:student_id and DATE(de.event_occured_date)=:date  \r\n" + 
				"ORDER BY FIELD(event_id, "+SCHOOL_ENTRY_ID+","+SCHOOL_EXIT_ID+", "+GEOFENCE_ENTRY_ID+", "+STUDENT_L_ID+", "+SOS_ID+" ,"+FALL_ID+","+SENSOR_MAL_ID+","+ABNORMAL_ID+" ,"+LOW_B_ID+" ,"+BAND_RA_ID+"), event_occured_date ASC ";
		
				Session session = sessionFactory.openSession();
				Transaction tx = session.beginTransaction();
				List<KidsSafeModel> kidSafeModelList = new ArrayList<KidsSafeModel>();
				Query query = session.createSQLQuery(geoZoneEntryQuery)
						.addScalar("event_id").addScalar("gps_location")
						.addScalar("eventOccuredDate").addScalar("outTime")
						.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
						.setResultTransformer(Transformers.aliasToBean(KidsSafeModel.class));
				query.setParameter("student_id", student_id);
				query.setParameter("date", date);
				kidSafeModelList = (List<KidsSafeModel>) query.list();
				tx.commit();
				session.close();
			
				return kidSafeModelList;
	}

	@Override
	public List<RemindersModel> findRemindersListForParent(int user_id, String date, int studentId) {
		log.info("Entering findRemindersListForParent {");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RemindersModel> remindersist = null;
		String remindersQuery = "";
		
		remindersQuery = "SELECT rm.reminder_id AS reminderId, cg.school_id AS school_id, cg.teacher_id AS teacherId,cg.class AS studentClass, " + 
						 "rm.comments AS comments, rm.image_name AS image, rm.created_date AS createdDate,  rm.updated_date AS updatedDate FROM reminders rm " +
						 "LEFT JOIN class_grade cg ON cg.class_grade_id = rm.class_grade_id " +
						 "LEFT JOIN students st ON st.class_grade_id = cg.class_grade_id " +
						 "LEFT JOIN parent_kids pk ON pk.student_id = st.student_id " +
						 "LEFT JOIN users u ON u.user_id = pk.user_id " +
						 "WHERE u.user_id= ? AND st.student_id = ? " ;	
		if(date.equals("null") || date.trim().length() == 0) {
			remindersQuery = remindersQuery + " AND (DATE(rm.created_date) = CURDATE() OR DATE(rm.updated_date) = CURDATE())" ;  
		}else {
			remindersQuery = remindersQuery + " AND (DATE(rm.created_date) = '"+date+"'  OR DATE(rm.updated_date) = '"+date+"') " ;
		}
		
		Query query = session.createSQLQuery(remindersQuery).addScalar("reminderId").addScalar("school_id").addScalar("teacherId")
				.addScalar("studentClass").addScalar("comments").addScalar("image").addScalar("createdDate")
				.addScalar("updatedDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RemindersModel.class));
		query.setParameter(0, user_id);
		query.setParameter(1, studentId);

		log.info("Query In findRemindersListForParent() To Execute Is :: " + query);
		remindersist = query.list();
		tx.commit();
		session.close();
		log.info("Exiting findRemindersListForParent }");
		return remindersist;
	}

	@Override
	public List<RemindersModel> findRemindersListForTeacher(int teacher_id, String date) {
		log.info("Entering findRemindersListForTeacher {");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RemindersModel> remindersList;
		String remindersQuery = "";
		boolean classSelected = false;

		remindersQuery = "select rm.school_id as school_id, rm.teacher_id as teacherId,rm.class as studentClass, rm.comments as comments, rm.image_name as image, " +
						  "rm.created_date as createdDate, rm.updated_date as updatedDate from reminders rm " +
						  "where rm.teacher_id= ? " ;
		
		if(null == date) {
			remindersQuery = remindersQuery + "and date(rm.created_date) = CURDATE()" ;
		}else {
			remindersQuery = remindersQuery + "and date(rm.created_date) = "+" ' "+date+" ' " ;
		}
		
		Query query = session.createSQLQuery(remindersQuery).addScalar("school_id").addScalar("teacherId")
				.addScalar("studentClass").addScalar("comments").addScalar("image").addScalar("createdDate")
				.addScalar("updatedDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RemindersModel.class));
		query.setParameter("teacher_id", teacher_id);

		log.info("Query to execute in findRemindersListForTeacher() is :: " + query);
		remindersList = query.list();
		tx.commit();
		session.close();
		log.info("Exiting findRemindersListForTeacher }");
		return remindersList;
	}

	@Override
	public List<RewardsCategoryTransform> findStudentsRewards(int studentId) {
		log.info("Entering findStudentsRewards {");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RewardsCategoryTransform> rewardsCategoryTrnsList;
		String rewardsCategoryQuery = "";
		
		rewardsCategoryQuery = "SELECT rc.rewards_category_id as category_id, rc.category_name as category_name, rc.category_icon_url as category_icon_url, " + 
								"cc.reward_category_count as reward_category_count,r.reward_id as reward_id, r.name as name, r.reward_icon_url as reward_icon_url, " +
								"rs.received_count AS reward_count, rs.created_date as created_date FROM rewards_category AS rc " +
								"LEFT JOIN rewards AS r ON r.rewards_category_id = rc.rewards_category_id " +
								"LEFT JOIN rewards_students AS rs ON rs.reward_id = r.reward_id " +
								"LEFT JOIN (SELECT rc1.rewards_category_id, SUM(rs1.received_count) AS reward_category_count " + 
								"FROM rewards_category AS rc1 LEFT JOIN rewards AS r1 ON r1.rewards_category_id = rc1.rewards_category_id " +
								"LEFT JOIN rewards_students AS rs1 ON rs1.reward_id = r1.reward_id GROUP BY rc1.rewards_category_id) AS cc " + 
								"ON cc.rewards_category_id = rc.rewards_category_id WHERE rs.student_id = ? " +
								"ORDER BY rc.rewards_category_id, r.reward_id, rs.created_date " ;
		
		Query query = session.createSQLQuery(rewardsCategoryQuery).addScalar("category_id").addScalar("category_name")
				.addScalar("category_icon_url").addScalar("reward_category_count").addScalar("reward_id").addScalar("name")
				.addScalar("reward_icon_url").addScalar("reward_count").addScalar("created_date")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardsCategoryTransform.class));
		query.setParameter(0, studentId);

		rewardsCategoryTrnsList = query.list();
		tx.commit();
		session.close();
		log.info("Exiting findStudentsRewards }");
		return rewardsCategoryTrnsList;
		
	}

	@Override
	public ClassGrade findClassGrade(int teacherId) {
		log.info("{ Entering Into findClassGrade()");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(ClassGrade.class);
		criteria.add(Restrictions.eq("teacher_id", teacherId));
		ClassGrade cg = (ClassGrade) criteria.uniqueResult();
		
		tx.commit();
		session.close();
		log.info("Exiting findClassGrade() }");
		return cg;
	}

	@Override
	public List<Integer> getParentIdByUuid(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<Integer> parentList = null;
		String sessionQuery = "select u.user_id from users u \r\n" + 
				"LEFT JOIN parent_kids pk on pk.user_id = u.user_id\r\n" + 
				"LEFT JOIN students s ON  s.student_id = pk.student_id \r\n" + 
				"LEFT JOIN device_students ds on  ds.student_id = s.student_id and  ds.status ='active'\r\n" + 
				"where ds.device_uuid =:uuid and u.role_type='parent_admin';";
		
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("uuid", uuid);
		
		parentList = (List<Integer>)query.list();
		session.close();
		
		return parentList;
	}

	@Override
	public Geozones getGeozoneById(int geoZone_id){
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Geozones.class, "gz");
		criteria.add(Restrictions.eq("gz.geozoneId", geoZone_id));
		@SuppressWarnings("unchecked")
		Geozones geoZone = (Geozones) criteria.uniqueResult();
		tx.commit();
		session.close();
		return geoZone;
	}

	@Override
	public int getDeviceEventIdByGeozoneId(int geozone_id,String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT device_event_id FROM device_events where geozone_id=:geoZone_id and uuid=:uuid";
		Integer sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("geoZone_id", geozone_id);
		query.setParameter("uuid", uuid);
		
			sessions = (Integer)query.uniqueResult();
		
		tx.commit();
		session.close();
		if(null != sessions )
			return sessions;
		else
		return 0;
	}
	
	@Override
	public int getClassGradeId(int user_id, int student_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select cg.class_grade_id from parent_kids pk"
				+ " left join students sts on pk.student_id = sts.student_id"
				+ " left join class_grade cg on sts.class_grade_id = cg.class_grade_id"
				+ " where pk.user_id = ? and pk.student_id = ?";
		
		Integer classGradeId = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter(0, user_id);
		query.setParameter(1, student_id);
		classGradeId = (Integer)query.uniqueResult();
		tx.commit();
		session.close();
		
		if(classGradeId != null)
			return classGradeId;
		else
			return 0;
		
	}
	
	@Override
	public boolean checkStudentId(int user_id, int student_id){
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select student_id from parent_kids where user_id = ? and student_id = ?";
		
		boolean isStudentIdBelongToParent = false;
		Integer studentId = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter(0, user_id);
		query.setParameter(1, student_id);
		studentId = (Integer) query.uniqueResult();
		tx.commit();
		session.close();
		
		if(studentId != null && studentId.intValue() > 0)
			isStudentIdBelongToParent = true;
		
		return isStudentIdBelongToParent;
	}
	
	@Override
	public void updateClassGrade(StudentsSleepDataModel studentsSleepDataModel){
		
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_UPDATE = "update students set allow_sleep_data = ? where  student_id = ?";
		Query query = session.createSQLQuery(SQL_UPDATE);
		query.setParameter(0, studentsSleepDataModel.getAllow_sleep_data().toLowerCase());
		query.setParameter(1, studentsSleepDataModel.getStudentId());
		query.executeUpdate();
		tx.commit();
		session.close();
	}
	
	@Override
	public List<StudentsListTransform> findStudentsIdsAndUUIDS(int teacher_id) {
		
		log.info("Entering findStudentsIdsAndUUIDS >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<StudentsListTransform> studentsListTrnsList;
		String studentsListQuery = "";
		studentsListQuery = "select s.name as studentName,s.student_id as studentId, ds.device_uuid as deviceUuid from class_grade cg "
				+ " left join students s on s.class_grade_id = cg.class_grade_id"
				+ " left join device_students ds on ds.student_id = s.student_id"
				+ " where cg.teacher_id = ? and s.allow_sleep_data = 'yes' and ds.status = 'active' " ;
		
		Query query = session.createSQLQuery(studentsListQuery)
						.addScalar("studentName")
						.addScalar("studentId")
						.addScalar("deviceUuid")
						.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
						.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));
		
		query.setParameter(0, teacher_id);

		studentsListTrnsList = query.list();
		tx.commit();
		session.close();
		log.info("Exiting findStudentsRewards }");
		return studentsListTrnsList;
	}
	
	@Override
	public List<Integer> getKididsForParent(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String kidsListForParentQuery = "select distinct pk.student_id as id from parent_kids pk where  pk.user_id=:user_id" ;
		List<Integer> kidsListForParent = null;
		Query query = session.createSQLQuery(kidsListForParentQuery);
		query.setParameter("user_id", userId);
		kidsListForParent = (List<Integer>) query.list();
		tx.commit();
		session.close();
		return kidsListForParent;
	}
	
	@Override
	public Date getDeviceEventOccuredDate(int device_event_id){
		log.info("Entering getDeviceEventOccuredDate >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<StudentsListTransform> studentsListTrnsList;
		String eventOcccuredDateQuery = "select event_occured_date from device_events where device_event_id = ? ";
		Query query = session.createSQLQuery(eventOcccuredDateQuery);
		query.setParameter(0, device_event_id);
		Date date = (Date)query.uniqueResult();
		
		if(date != null){
			return date;
		}
		
		log.info("date >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+date.toString());
		
		tx.commit();
		session.close();
		
		return null;
		
	}
	
	@Override
	public boolean checkSchoolHoursWithinRange(String time, int school_id){
		log.info("Entering checkSchoolHoursWithinRange >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String checkDeviceEventExistQuery = "select distinct(school_id) from school_details where ? >= school_in_start and ? <= school_out_end and school_id = ? ";
		Query query = session.createSQLQuery(checkDeviceEventExistQuery);
		query.setParameter(0, time);
		query.setParameter(1, time);
		query.setParameter(2, school_id);
		Integer deviceEventId =  (Integer) query.uniqueResult();
		
		if(deviceEventId != null){
			if(deviceEventId > 0){
				return true;
			}
		}
		
		tx.commit();
		session.close();
		return false;
		
	}
	
	@Override
	public boolean checkSchoolHoliday(String date, int school_id) {
		log.info("Entering checkSchoolHoursWithinRange >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String checkDeviceEventExistQuery = "select distinct(school_id) from school_calendar where date_close != ? and school_id = ? ";
		Query query = session.createSQLQuery(checkDeviceEventExistQuery);
		query.setParameter(0, date);
		query.setParameter(1, school_id);
		Integer deviceEventId = (Integer) query.uniqueResult();

		if (deviceEventId != null) {
			if (deviceEventId > 0) {
				return true;
			}
		}

		tx.commit();
		session.close();
		return false;

	}
	
	@Override
	public SchoolWorkingHoursTransform checkDeviceEventOccuredWithinSchoolHours(int school_id, String date, String time ){
		log.info("Entering checkDeviceEventOccuredWithinSchoolHours :::::  ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String checkDeventOccdwshQuery = "select distinct(sd.school_id) as schoolId , sd.school_in_start as schoolStart, sd.school_out_end as schoolOut from school_details sd"
				+ " left join school_calendar sc on sd.school_id = sc.school_id"
				+ " where sd.school_id = ? and ? >= sd.school_in_start and ? <= sd.school_out_end and sc.date_close != ?";
		
		Query query = session.createSQLQuery(checkDeventOccdwshQuery)
		.addScalar("schoolId")
		.addScalar("schoolStart")
		.addScalar("schoolOut")
		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
		.setResultTransformer(Transformers.aliasToBean(SchoolWorkingHoursTransform.class));
		query.setParameter(0, school_id);
		query.setParameter(1, time);
		query.setParameter(2, time);
		query.setParameter(3, date);
		
		SchoolWorkingHoursTransform schoolWorkingHoursTransform = (com.liteon.icgwearable.transform.SchoolWorkingHoursTransform) query.uniqueResult();
		
		if(schoolWorkingHoursTransform != null && 
				schoolWorkingHoursTransform.getSchoolId() != null && 
				schoolWorkingHoursTransform.getSchoolStart() != null && 
				schoolWorkingHoursTransform.getSchoolOut() != null){
			log.info("school_id :::::::::: "+schoolWorkingHoursTransform.getSchoolId());
			log.info("school_start :::::::::: "+schoolWorkingHoursTransform.getSchoolStart());
			log.info("school out :::::::::: "+schoolWorkingHoursTransform.getSchoolOut() );
		}
		
		tx.commit();
		session.close();
		return schoolWorkingHoursTransform;
		
	}
	
	
	@Override
	public boolean checkDeviceEventExists(int device_event_id){
		log.info("Entering checkClosedDeviceEventExists >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<StudentsListTransform> studentsListTrnsList;
		String checkDeviceEventExistQuery = "select device_event_id from device_events where device_event_id  = ?";
		Query query = session.createSQLQuery(checkDeviceEventExistQuery);
		query.setParameter(0, device_event_id);
		Integer deviceEventId =  (Integer) query.uniqueResult();
		
		if(deviceEventId != null){
			if(deviceEventId > 0){
				return true;
			}
		}
		
		tx.commit();
		session.close();
		return false;
		
	}
	
	
	@Override
	public boolean checkClosedDeviceEventExists(int device_event_id){
		log.info("Entering checkClosedDeviceEventExists >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String checkCloseDeviceEventExistQuery = "select device_event_id from closed_device_events where device_event_id  = ?";
		Query query = session.createSQLQuery(checkCloseDeviceEventExistQuery);
		query.setParameter(0, device_event_id);
		int deviceEventId = 0;
		if(query.uniqueResult() != null){
			deviceEventId = (int) query.uniqueResult();
			if(deviceEventId > 0){
				return true;
			}
		}
		
		tx.commit();
		session.close();
		return false;
	}
	
	@Override
	public boolean updateClosedDeviceEvents(int device_event_id, int user_id, String user_roleType, int duration,
			boolean deviceEventExists, String duringSchoolHoursButCloseAfterSchoolHoursFlag) {

		boolean updateFlag = false;
		log.info("Entering updateClosedDeviceEvents >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		if (!deviceEventExists) {

			log.info("deviceEventExists doesnot exists. Inserting new record");
			ClosedDeviceEvents cde = new ClosedDeviceEvents();
			cde.setDeviceEventId(device_event_id);
			cde.setDuration(duration);
			if (user_roleType.equals("school_teacher") || user_roleType.equals("school_staff")) {
				cde.setStaffId(user_id);
				if(duringSchoolHoursButCloseAfterSchoolHoursFlag.equals("yes")){
					cde.setIs_sos_abnormal("yes");
				}else{
					cde.setIs_sos_abnormal("no");
				}
				cde.setStaffClosedDate(new Date());
			} else if (user_roleType.equals("school_admin")) {
				cde.setAdminId(user_id);
				if(duringSchoolHoursButCloseAfterSchoolHoursFlag.equals("yes")){
					cde.setIs_sos_abnormal("yes");
				}else{
					cde.setIs_sos_abnormal("no");
				}
				cde.setAdminClosedDate(new Date());
			} else {
				cde.setParentId(user_id);
				cde.setParentClosedDate(new Date());
			}

			cde.setUpdatedDate(new Date());
			session.save(cde);

		} else {

			log.info("closed deviceEventExists exists. Updating existing records");
			String updateCloseDeviceEvent = "";
			if (user_roleType.equals("parent_admin")) {

				Criteria criteria = session.createCriteria(ClosedDeviceEvents.class);
				criteria.add(Restrictions.eq("deviceEventId", device_event_id));
				criteria.add(Restrictions.eq("parentId", user_id));
				ClosedDeviceEvents closedDeviceEvents = (ClosedDeviceEvents) criteria.uniqueResult();
				if (closedDeviceEvents != null) {
					log.info("record already updated for with parent_id. Ignoring upate.....");
					updateFlag = true;
				} else {
					log.info("record exists updating with parent_id >>>>>>>>>>>>>>>");
					updateCloseDeviceEvent = "update closed_device_events set parent_id = ?, parent_closed_date = ? where device_event_id = ?";
					Query query = session.createSQLQuery(updateCloseDeviceEvent);
					query.setParameter(0, user_id);
					query.setParameter(1, new Date());
					query.setParameter(2, device_event_id);
					query.executeUpdate();
				}

			} else {

				if (user_roleType.equals("school_teacher") || user_roleType.equals("school_staff")) {
					Criteria criteria = session.createCriteria(ClosedDeviceEvents.class);
					criteria.add(Restrictions.eq("deviceEventId", device_event_id));
					ClosedDeviceEvents closedDeviceEvents = (ClosedDeviceEvents) criteria.uniqueResult();
					if (closedDeviceEvents != null && closedDeviceEvents.getStaffId() == user_id) {
						log.info("record already updated for with staff_id/school_teacher. Ignoring upate.....");
						updateFlag = true;
					} else {
						log.info("record exists updating with school_teacher or school_staff ::::::::::::::: ");
						updateCloseDeviceEvent = "update closed_device_events set staff_id = ?, staff_closed_date = ?, is_sos_abnormal = ? where device_event_id = ?";
						Query query = session.createSQLQuery(updateCloseDeviceEvent);
						query.setParameter(0, user_id);
						query.setParameter(1, new Date());
						if(closedDeviceEvents.getIs_sos_abnormal().equals("no")){
							log.info("record already updated as abnormal SOS.....");
							query.setParameter(2, closedDeviceEvents.getIs_sos_abnormal());
						}else{
							if(duringSchoolHoursButCloseAfterSchoolHoursFlag.equals("yes")){
								query.setParameter(2, "yes");
							}else{
								query.setParameter(2, "no");
							}
						}
						query.setParameter(3, device_event_id);
						query.executeUpdate();
					}
				} else if (user_roleType.equals("school_admin")) {
					Criteria criteria = session.createCriteria(ClosedDeviceEvents.class);
					criteria.add(Restrictions.eq("deviceEventId", device_event_id));
					ClosedDeviceEvents closedDeviceEvents = (ClosedDeviceEvents) criteria.uniqueResult();
					if (closedDeviceEvents != null && closedDeviceEvents.getAdminId() == user_id) {
						log.info("record already updated for with adminId. Ignoring upate.....");
						updateFlag = true;
					} else {
						log.info("record exists updating with adminId <<<<<<<<<>>>>>>>>>>>>>>> ");
						updateCloseDeviceEvent = "update closed_device_events set admin_id = ?, admin_closed_date = ?, is_sos_abnormal = ? where device_event_id = ?";
						Query query = session.createSQLQuery(updateCloseDeviceEvent);
						query.setParameter(0, user_id);
						query.setParameter(1, new Date());
						if(closedDeviceEvents.getIs_sos_abnormal().equals("no")){
							log.info("record already updated as abnormal SOS.....");
							query.setParameter(2, closedDeviceEvents.getIs_sos_abnormal());
						}else{
							if(duringSchoolHoursButCloseAfterSchoolHoursFlag.equals("yes")){
								query.setParameter(2, "yes");
							}else{
								query.setParameter(2, "no");
							}
						}
						
						query.setParameter(3, device_event_id);
						query.executeUpdate();
					}
				}
			}
		}

		tx.commit();
		session.close();
		return updateFlag;
	}
	
	
	@Override
	public boolean updateDeviceEventQueueForCloseSOSAlert(int user_id) {
		Session session = sessionFactory.openSession();
		Transaction tx =session.beginTransaction();
		Query query = session.createSQLQuery("update device_events_queue set isEliminated = ? where user_id = ?");
		query.setParameter(0, "y");
		query.setParameter(1, user_id);
		int result = query.executeUpdate();
		log.info("Result "+result);
		tx.commit();
		session.close();
		return true;
	}
	
	
	@Override
	public boolean checkDeviceEventBelongsToParentUser(int user_id, int device_event_id){
		
		log.info("Entering checkDeviceEventBelongsToParentUser >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String checkDeviceEventBelongsToParentUserQuery = "select de.device_event_id as device_event_id, de.uuid as uuid, ds.student_id , pk.user_id from device_events de"
				+ " left join device_students ds on ds.device_uuid = de.uuid AND status = 'active'"
				+ " left join parent_kids pk on ds.student_id = pk.student_id"
				+ " where pk.user_id = ? and de.device_event_id = ? ";
		
		Query query = session.createSQLQuery(checkDeviceEventBelongsToParentUserQuery);
		query.setParameter(0, user_id);
		query.setParameter(1, device_event_id);
		if(query.uniqueResult() != null){
				return true;
		}
		
		tx.commit();
		session.close();
		return false;
		
	}
	
	
	@Override
	public boolean checkDeviceEventBelongsToSchoolUsers(int device_event_id, int school_id){
		log.info("Entering checkDeviceEventBelongsToSchoolUsers >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String checkDeviceEventBelongsToParentUserQuery = "select de.device_event_id as device_event_id, de.uuid as uuid, st.student_id from device_events de"
				+ " left join device_students ds on ds.device_uuid = de.uuid AND status = 'active'"
				+ " left join students st on st.student_id = ds.student_id"
				+ " left join class_grade cg on st.class_grade_id = cg.class_grade_id"
				+ " where de.device_event_id = ? and cg.school_id = ?";
		
		Query query = session.createSQLQuery(checkDeviceEventBelongsToParentUserQuery);
		query.setParameter(0, device_event_id);
		query.setParameter(1, school_id);
		if(query.uniqueResult() != null){
				return true;
		}
		
		tx.commit();
		session.close();
		return false;
		
	}
	
	@Override
	public String getSchoolClosingHours(int school_id){
		log.info("Entering checkDeviceEventBelongsToSchoolUsers >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String checkDeviceEventBelongsToParentUserQuery = "select school_out_end from school_details where school_id = ?";
		
		Query query = session.createSQLQuery(checkDeviceEventBelongsToParentUserQuery);
		query.setParameter(0, school_id);
		Time schoolOutHours = (Time)query.uniqueResult();
		
		tx.commit();
		session.close();
		
		return schoolOutHours.toString();
		
	}

	@Override
	public ClassGrade getClassGrade(String studentClass, String grade,int school_id) {
		log.info("entered : ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(ClassGrade.class, "cg");
		criteria.add(Restrictions.eq("cg.grade", grade));
		criteria.add(Restrictions.eq("cg.studentClass", studentClass));
		criteria.add(Restrictions.eq("cg.schoolId", school_id));
		log.info("before getting cg is : ");
		@SuppressWarnings("unchecked")
		ClassGrade classGrade = (ClassGrade) criteria.uniqueResult();
		log.info("classGrade is : "+classGrade);
		tx.commit();
		session.close();
		return classGrade;
	}
	
	
	@Override
	public boolean checkTokenIsValidOrNot(String tokenId){
		boolean tokenIsValid = false;
		log.info("Entering checkTokenIsValidOrNot >>>> ");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String tokenIsValidQuery = "select session_id from users where session_id = ?";
		
		Query query = session.createSQLQuery(tokenIsValidQuery);
		query.setParameter(0, tokenId);
		String gotTokenId = (String)query.uniqueResult();
		if(gotTokenId != null){
			tokenIsValid = true;
		}
		
		tx.commit();
		session.close();
		return tokenIsValid;
		
	}

	@Override
	public Geozones checkIfGeozoneIdExist(int geozoneId) {
		Session session = null;
		Transaction tx = null;
		Geozones geozones=null;
		try{
		 session=	sessionFactory.openSession();
		 tx =session.beginTransaction();
		 Criteria criteria = session.createCriteria(Geozones.class);
	     criteria.add(Restrictions.eq("geozoneId", geozoneId));
	     geozones = (Geozones)criteria.uniqueResult();
	     tx.commit();
		}catch(Exception e){
			log.info("Exception in checkIfGeozoneIdExist"+e);
			if(null != geozones)
				session.evict(geozones);
			tx.rollback();
		}finally{
			session.close();
		}
		
		
		return geozones;
	}

	@Override
	public HashMap<String, Object> getCountyList() {
		HashMap<String, Object> lCountyMap = new HashMap<String, Object>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT DISTINCT county_en FROM county_areas ORDER BY county_en";
		List<String> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		sessions = (List<String>)query.list();
		session.close();
		
		lCountyMap.put("counties", sessions);
		
		return lCountyMap;
	}
}
