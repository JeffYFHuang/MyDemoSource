package com.liteon.icgwearable.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
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

import com.liteon.icgwearable.exception.RecordAlreadyExistsException;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.StudentCSVModel;
import com.liteon.icgwearable.model.StudentsModel;
import com.liteon.icgwearable.model.StudentsWebModel;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.StudentsMapLocationTransform;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.WebUtility;
import com.liteon.icgwearable.service.UserService;

@Repository("studentsDAO")
public class StudentsDAOImpl implements StudentsDAO {

	private static Logger log = Logger.getLogger(StudentsDAOImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	protected AccountDAO accountDAO;
	@Autowired
	protected DeviceDAO deviceDAO;

	@Resource(name = "configProperties")
	private Properties configProperties;
	@Autowired
	protected HttpSession httpSession;

	@Value("${SCHOOL_ENTRY_ALERT_ID}")
	private Integer SCHOOL_ENTRY_ID;

	@Value("${SCHOOL_EXIT_ALERT_ID}")
	private Integer SCHOOL_EXIT_ID;

	@Value("${SOS_ALERT_ID}")
	private String SOS_ALERT_ID;

	@Value("${FALL_DETECTION_ID}")
	private String FALL_DETECTION_ID;

	@Value("${STUDENT_LOCATION_ID}")
	private String STUDENT_LOCATION_ID;

	@Value("${BAND_REMOVAL_ALERT_ID}")
	private String BAND_REMOVAL_ALERT_ID;

	@Autowired
	private ClassGradeDAO classGradeDAO;

	@Autowired
	private UserService userService;

	@Autowired
	private DeviceStudentsDAO deviceStudentsDAO;

	WebUtility webUtility = WebUtility.getWebUtility();

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public List<Students> listAllStudents() {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Students.class);
		List<Students> studentsList = criteria.list();
		tx.commit();
		session.close();
		return studentsList;
	}

	@Override
	public void addStudent(Students students, String uuid, String firmwareVersion) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(students);
		tx.commit();

		Devices devices = this.deviceDAO.checkDeviceIdExist(uuid);
		// Firmware firmware = this.firmwareDAO.firmwareExists(firmwareVersion);
		/*
		 * String firmwareVersion1 = null; if (firmware == null) { firmware =
		 * new Firmware(); log.info("Into Firmware If");
		 * firmware.setFirmwareVersion(firmwareVersion);
		 * firmware.setFirmwareDescription(firmwareVersion);
		 * firmware.setFirmwareName(firmwareVersion);
		 * firmware.setCreatedDate(new java.util.Date()); firmware =
		 * this.firmwareDAO.createFirmware(firmware); firmwareVersion1 =
		 * firmware.getFirmwareVersion(); log.info("firmwareVersion1 into if" +
		 * "\t " + firmwareVersion1); } else if (firmware != null) { firmware =
		 * this.firmwareDAO.updateFirmware(firmware); firmwareVersion1 =
		 * firmware.getFirmwareVersion();
		 * log.info("firmwareVersion1 into else if" + "\t " + firmwareVersion1);
		 * }
		 */
		if (devices == null) {
			log.info("Getting into If loop");
			devices = new Devices();
			devices.setUuid(uuid);
			// devices.setStudents(students);
			// devices.setMaxParents(1);
			// devices.setDeviceActive(devices.getDeviceActive().y);
			// devices.setPurchaseDate(new java.sql.Timestamp(new
			// java.util.Date().getTime()));
			// devices.setCreatedDate(new java.sql.Timestamp(new
			// java.util.Date().getTime()));
			// devices.setFirmware(firmware);
			this.deviceDAO.createDevice(devices);
		} else if (devices != null) {
			log.info("Getting into Else");
			// devices.setStudents(students);
			// devices.setFirmware(firmware);
			this.deviceDAO.updateDevice(devices);
		}
		session.close();
	}

	@Override
	public void updateStudent(Students students) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(students);
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Caught in updateStudent()" + "\t" + e);
			if (null != students)
				session.evict(students);
			tx.rollback();
		} finally {
			session.close();
		}
	}

	@Override
	public Students getStudentForStudentsUpdate(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Students.class);
		criteria.add(Restrictions.eq("studentId", id));
		Students students = (Students) criteria.uniqueResult();
		tx.commit();
		session.close();
		return students;
	}

	@Override
	public Students getStudent(int id) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		Students st = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			st = (Students) session.get(Students.class, id);
		} catch (Exception e) {
			log.info("Exception Occured in getStudent() " + e);
			if (null != st)
				session.evict(st);
			tx.rollback();
		} finally {
			tx.commit();
			session.close();
		}

		return st;

	}

	@Override
	public boolean createStudents(StudentCSVModel scsvm) {
		// TODO Auto-generated method stub
		boolean isStudentExist = false;
		Integer accountId = (Integer) this.httpSession.getAttribute("accountId");
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<Students> usersList = new ArrayList<>();
		Students students = null;

		tx = session.beginTransaction();
		students = new Students();
		Accounts accounts = this.accountDAO.createAccounts();

		students.setName(scsvm.getStudentName().trim());
		// students.setStudentClass(Integer.parseInt(scsvm.getStudentClass().trim()));
		students.setRollNo(scsvm.getStudentRollNo().trim());
		// students.setAccounts(accounts);
		// students.setSchoolId(accountId);
		session.save(students);
		tx.commit();

		String uuid = scsvm.getUuid();
		// String firmwareVersion = scsvm.getDeviceFirmwareVersion();
		Devices devices = this.deviceDAO.checkDeviceIdExist(uuid);
		/*
		 * Firmware firmware = this.firmwareDAO.firmwareExists(firmwareVersion);
		 * String firmwareVersion1 = null; if (firmware == null) { firmware =
		 * new Firmware(); log.info("Into Firmware If");
		 * firmware.setFirmwareVersion(firmwareVersion);
		 * firmware.setFirmwareDescription(firmwareVersion);
		 * firmware.setFirmwareName(firmwareVersion);
		 * firmware.setCreatedDate(new java.util.Date()); firmware =
		 * this.firmwareDAO.createFirmware(firmware); firmwareVersion1 =
		 * firmware.getFirmwareVersion(); } else if (firmware != null) {
		 * firmware = this.firmwareDAO.updateFirmware(firmware);
		 * firmwareVersion1 = firmware.getFirmwareVersion(); }
		 */
		if (devices == null) {
			log.info("Getting into If loop");
			devices = new Devices();
			devices.setUuid(uuid);
			// devices.setStudents(students);
			// devices.setMaxParents(1);
			// devices.setDeviceActive(devices.getDeviceActive().y);
			// devices.setPurchaseDate(new java.sql.Timestamp(new
			// java.util.Date().getTime()));
			devices.setCreatedDate(new java.sql.Timestamp(new java.util.Date().getTime()));
			// devices.setFirmware(firmware);
			this.deviceDAO.createDevice(devices);
			isStudentExist = true;
		} else if (devices != null) {
			log.info("Getting into Else");
			// devices.setStudents(students);
			// devices.setFirmware(firmware);
			this.deviceDAO.updateDevice(devices);
			isStudentExist = true;
		}
		session.close();
		return isStudentExist;
	}

	@Override
	public List<StudentsListTransform> findStudentsByAdminOrTeacher(int userId, String classId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String studentQuery = null;

		// if (userId > 0 && classId == null) {
		studentQuery = "select s.student_id as studentId, s.name as studentName,s.nickname as nickName,\r\n"
				+ " s.roll_no as rollNo,s.registartion_no as registartionNumber,s.dob as dob,s.height as height,s.weight as weight,s.emergency_contact as emergency_contact, \r\n"
				+ "s.gender as gender,cg.grade as grade, cg.class as studentClass,ds.device_uuid as deviceUuid from students s \r\n"
				+ " left join device_students ds on  ds.student_id = s.student_id and ds.status='active' \r\n"
				+ " left join class_grade cg on cg.class_grade_id = s.class_grade_id \r\n"
				+ " left join users u on u.account_id = cg.school_id  and cg.teacher_id = u.user_id \r\n"
				+ " where u.user_id=:user_id";
		/*
		 * } else if (userId > 0 && classId != null) { studentQuery =
		 * "select s.student_id as studentId, s.account_id as accountId, s.school_id as schoolId,s.name as studentName,s.nickname as nickName,s.class as studentClass, "
		 * +
		 * "s.roll_no as rollNo,s.dob as dob,s.height as height,s.weight as weight,s.emergency_contact as emergency_contact,s.gender as gender,d.uuid as deviceUuid from students s "
		 * +
		 * "left join devices d on d.student_id = s.student_id and d.device_active='y' "
		 * +
		 * "left join users  as u on u.user_id = d.teacher_id and u.user_active='y' "
		 * +
		 * "left join users uu on uu.account_id = s.school_id and uu.user_active='y' "
		 * + "where uu.user_id= ? and s.class = ? "; }
		 */

		List<StudentsListTransform> studentsList = null;

		Query query = session.createSQLQuery(studentQuery).addScalar("studentId").addScalar("studentName")
				.addScalar("nickName").addScalar("rollNo").addScalar("registartionNumber").addScalar("height")
				.addScalar("weight").addScalar("emergency_contact").addScalar("gender").addScalar("deviceUuid")
				.addScalar("grade").addScalar("studentClass").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));

		query.setParameter("user_id", userId);

		studentsList = query.list();
		tx.commit();
		session.close();
		return studentsList;
	}

	@Override
	public List<StudentsListTransform> findStudentsByAdmin(int userId, String studentGrade, String stClass, int page_id,
			int total) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		List<StudentsListTransform> studentsList = null;
		StringBuilder studentsSB = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			String studentQuery = "", filterQuery = "";

			studentsSB = new StringBuilder();
			studentsSB.append("select s.student_id as studentId, s.name as studentName,s.nickname as nickName, ")
					.append("s.roll_no as rollNo,s.registartion_no as registartionNumber,s.dob as dob,s.height as height,s.weight as weight,s.emergency_contact as emergency_contact, ")
					.append("s.gender as gender,cg.grade as grade, cg.class as studentClass,ds.device_uuid as deviceUuid from students s ")
					.append("left join device_students ds on  ds.student_id = s.student_id and ds.status='active' ")
					.append("left join class_grade cg on cg.class_grade_id = s.class_grade_id ")
					.append("left join users u on u.account_id = cg.school_id ")
					.append("left join users admin on admin.user_id =cg.teacher_id ")
					.append("where u.user_id=:user_id ");

			String orderByQ = "order by studentId";

			if (studentGrade.equals("Grade") && stClass.equals("Class")) {
				studentsSB.append(" ");
			} else {
				studentsSB.append(
						" and cg.grade = " + "'" + studentGrade + "'" + "and cg.class = " + "'" + stClass + "'");
			}

			studentsSB.append(" order by studentId ");

			if (page_id > 0) {
				studentsSB.append("limit " + (page_id - 1) + "," + total);
			}

			studentQuery = studentsSB.toString();

			Query query = session.createSQLQuery(studentQuery).addScalar("studentId").addScalar("studentName")
					.addScalar("nickName").addScalar("rollNo").addScalar("registartionNumber").addScalar("dob")
					.addScalar("height").addScalar("weight").addScalar("emergency_contact").addScalar("gender")
					.addScalar("grade").addScalar("studentClass").addScalar("deviceUuid")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));

			query.setParameter("user_id", userId);

			studentsList = query.list();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception Caught In findStudentsByAdmin () " + e);
			if (null != studentsList)
				session.evict(studentsList);
			tx.rollback();
		} finally {
			session.close();
		}
		return studentsList;
	}

	@Override
	public List<StudentsTransform> listStudentUniqueClass(int userId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String uniqueClassQuery = "select distinct s.class as stClass, a.account_name as schoolName from students s "
				+ "left join accounts a on a.account_id = s.school_id "
				+ "left join users u on u.account_id=a.account_id " + "where user_id= ? ";

		List<StudentsTransform> studentsClassList = null;

		Query query = session.createSQLQuery(uniqueClassQuery).addScalar("stClass").addScalar("schoolName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsTransform.class));
		query.setParameter(0, userId);
		studentsClassList = query.list();
		tx.commit();
		session.close();
		return studentsClassList;
	}

	@Override
	public List<Students> findStudentsByClass(int schoolId, String classId, int pageNo) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		int page = (pageNo - 1) * 5;
		Criteria criteria = session.createCriteria(Students.class);
		criteria.setFirstResult(page);
		criteria.setMaxResults(5);
		criteria.add(Restrictions.eq("schoolId", schoolId));
		criteria.add(Restrictions.eq("studentClass", classId));
		List<Students> studentsList = criteria.list();
		tx.commit();
		session.close();
		return studentsList;
	}

	@Override
	public List<TeachersStudentsTransform> findStudentsClassByTransformer(int schoolId, String classId, int pageNo) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		int page = (pageNo - 1) * 5;
		Criteria criteria = session.createCriteria(Students.class);
		criteria.setFirstResult(page);
		criteria.setMaxResults(5);
		criteria.add(Restrictions.eq("schoolId", schoolId));
		criteria.add(Restrictions.eq("studentClass", classId));
		List<Students> studentsList = criteria.list();

		SimpleDateFormat dt1 = new SimpleDateFormat(this.configProperties.getProperty("display.dateFormat"));

		List<TeachersStudentsTransform> teaStuTransformList = new ArrayList<>();
		TeachersStudentsTransform teaStTransform = null;
		for (Students st : studentsList) {
			teaStTransform = new TeachersStudentsTransform();
			// teaStTransform.setAccountId(st.getAccounts().getAccountId());
			teaStTransform.setStudentName(st.getName());
			// teaStTransform.setStudentClass(st.getStudentClass());
			teaStTransform.setRollNo(st.getRollNo());
			Date formattedDate = st.getDob();
			teaStTransform.setHeight(st.getHeight());
			teaStTransform.setWeight(st.getWeight());
			try {
				teaStTransform.setDob(dt1.parse(formattedDate.toString()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			teaStTransform.setGender(st.getGender());
			teaStuTransformList.add(teaStTransform);
		}
		tx.commit();
		session.close();
		return teaStuTransformList;
	}

	@Override
	public StudentsMapLocationTransform viewStudentsLocation(String deviceUuid, int userId, String role) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String mapsLocationQuery = null;
		StudentsMapLocationTransform studentsMapLocationTransform = null;

		if (role.equals("parent_admin")) {
			mapsLocationQuery = "select ds.device_uuid as deviceUuid, de.gps_location_data as gpsLocationData, de.event_occured_date as eventOccuredDate from device_events as de "
					+ "left join device_students ds on ds.device_uuid = de.uuid and ds.status='active' "
					+ "left join parent_kids pk on pk.student_id = ds.student_id "
					+ "where ds.device_uuid= ? and pk.user_id = ? "
					+ "AND (de.gps_location_data IS NOT NULL AND de.gps_location_data <> '')"
					+ "ORDER BY de.event_occured_date DESC LIMIT 1 ";
		} else if (role.equals("school_admin") || role.equals("school_teacher")) {
			log.info("Into here1");
			mapsLocationQuery = "select ds.device_uuid as deviceUuid, de.gps_location_data as gpsLocationData, de.event_occured_date as eventOccuredDate from device_events as de "
					+ "left join device_students ds on ds.device_uuid = de.uuid and ds.status='active' "
					+ "left join students s on s.student_id = ds.student_id "
					+ "left join class_grade cg on cg.class_grade_id = s.class_grade_id "
					+ "left join users u on u.account_id = cg.school_id " + "where ds.device_uuid= ? and u.user_id = ? "
					+ "AND (de.gps_location_data IS NOT NULL AND de.gps_location_data <> '')"
					+ " ORDER BY de.event_occured_date DESC LIMIT 1 ";
		}

		Query query = session.createSQLQuery(mapsLocationQuery).addScalar("deviceUuid").addScalar("gpsLocationData")
				.addScalar("eventOccuredDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsMapLocationTransform.class));
		query.setParameter(0, deviceUuid);
		query.setParameter(1, userId);
		studentsMapLocationTransform = (StudentsMapLocationTransform) query.uniqueResult();
		tx.commit();
		session.close();
		return studentsMapLocationTransform;
	}

	@Override
	public int findSchoolId(int studentId) {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Students.class);
		criteria.add(Restrictions.eq("studentId", studentId));
		Students students = (Students) criteria.uniqueResult();

		tx.commit();
		session.close();
		// return students.getSchoolId();
		return -1;
	}

	@Override
	public int findStudentsBySessionId(int studentId, String token) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		/*
		 * String studentCountQuery= "SELECT COUNT(u.user_id) FROM users AS u "
		 * + "LEFT JOIN students AS s ON s.account_id = u.account_id " +
		 * "WHERE u.session_id = ? " + "AND s.student_id = ? " ;
		 */

		String studentCountQry = "SELECT COUNT(u.user_id) FROM users AS u \r\n"
				+ "LEFT JOIN parent_kids pk on pk.user_id = u.user_id\r\n"
				+ "LEFT JOIN students AS s ON s.student_id = pk.student_id\r\n" + "WHERE u.mobile_session_id = ? \r\n"
				+ "AND s.student_id = ?";
		Query q = session.createSQLQuery(studentCountQry);
		q.setParameter(0, token);
		q.setParameter(1, studentId);
		BigInteger studentCount = (BigInteger) q.uniqueResult();

		tx.commit();
		session.close();
		return studentCount.intValue();
	}

	@Override
	public HashMap<String, Object> getStudentAttendanceStats(int school_id, String grade, String student_class,
			String inputdate) {
		HashMap<String, Object> lStudentAttendanceStats = new HashMap<String, Object>();
		HashMap<String, Object> lStudentAttendanceStatsMap = new HashMap<String, Object>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		StringBuffer checkGradeClass = new StringBuffer("");

		if (null != grade) {
			checkGradeClass.append("AND class_grade.grade = '" + grade + "'");
		}
		if (null != student_class) {
			checkGradeClass.append(" AND class_grade.class = '" + student_class + "'");
		}

		// Query to get total students count in a school
		String totalStudentsCountQuery = "SELECT COUNT(DISTINCT students.student_id) AS total_count "
				+ "FROM students, class_grade, device_students "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id " + "AND class_grade.school_id = ? "
				+ "AND DATE(students.created_date) <= ? " + "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) "
				+ checkGradeClass;

		Query q = session.createSQLQuery(totalStudentsCountQuery);
		q.setParameter(0, school_id);
		q.setParameter(1, inputdate);
		q.setParameter(2, inputdate);
		BigInteger totalStudents = (BigInteger) q.uniqueResult();

		// Query to get total students school IN
		String totalStudentsInQuery = "SELECT COUNT(DISTINCT students.student_id) AS school_in_count "
				+ "FROM students, class_grade, device_students, device_events "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid " + "AND device_events.event_id = ? "
				+ "AND DATE(device_events.event_occured_date) = ? " + "AND device_events.in_time IS NOT NULL "
				+ "AND class_grade.school_id = ? " + "AND DATE(students.created_date) <= ? "
				+ "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) "
				+ checkGradeClass;

		q = session.createSQLQuery(totalStudentsInQuery);
		q.setParameter(0, SCHOOL_ENTRY_ID);
		q.setParameter(1, inputdate);
		q.setParameter(2, school_id);
		q.setParameter(3, inputdate);
		q.setParameter(4, inputdate);
		BigInteger totalStudentsIn = (BigInteger) q.uniqueResult();

		// Query to get total students school OUT
		String totalStudentsOutQuery = "SELECT COUNT(DISTINCT students.student_id) AS school_out_count "
				+ "FROM students, class_grade, device_students, device_events "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid " + "AND device_events.event_id = ? "
				+ "AND DATE(device_events.event_occured_date) = ? " + "AND device_events.out_time IS NOT NULL "
				+ "AND class_grade.school_id = ? " + "AND DATE(students.created_date) <= ? "
				+ "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) "
				+ checkGradeClass;

		q = session.createSQLQuery(totalStudentsOutQuery);
		q.setParameter(0, SCHOOL_EXIT_ID);
		q.setParameter(1, inputdate);
		q.setParameter(2, school_id);
		q.setParameter(3, inputdate);
		q.setParameter(4, inputdate);
		BigInteger totalStudentsOut = (BigInteger) q.uniqueResult();

		// Query to get total abnormal students
		String totalStudentsAbnormalQuery = "SELECT COUNT(DISTINCT students.student_id) AS abnormal_count "
				+ "FROM students, class_grade, device_students, device_events "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid "
				+ "AND (device_events.event_id = ? OR device_events.event_id = ?) "
				+ "AND DATE(device_events.event_occured_date) = ? "
				+ "AND device_events.is_entry_exit_abnormal = 'yes' " + "AND class_grade.school_id = ? "
				+ "AND DATE(students.created_date) <= ? " + "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) "
				+ checkGradeClass;

		q = session.createSQLQuery(totalStudentsAbnormalQuery);
		q.setParameter(0, SCHOOL_ENTRY_ID);
		q.setParameter(1, SCHOOL_EXIT_ID);
		q.setParameter(2, inputdate);
		q.setParameter(3, school_id);
		q.setParameter(4, inputdate);
		q.setParameter(5, inputdate);
		BigInteger totalStudentsAbnormal = (BigInteger) q.uniqueResult();

		// Query to get total present students
		String totalStudentsPresentQuery = "SELECT COUNT(DISTINCT students.student_id) AS present_count "
				+ "FROM students, class_grade, device_students, device_events "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid "
				+ "AND (device_events.event_id = ? OR device_events.event_id = ?) "
				+ "AND DATE(device_events.event_occured_date) = ? " + "AND class_grade.school_id = ? "
				+ "AND DATE(students.created_date) <= ? " + "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) "
				+ checkGradeClass;

		q = session.createSQLQuery(totalStudentsPresentQuery);
		q.setParameter(0, SCHOOL_ENTRY_ID);
		q.setParameter(1, SCHOOL_EXIT_ID);
		q.setParameter(2, inputdate);
		q.setParameter(3, school_id);
		q.setParameter(4, inputdate);
		q.setParameter(5, inputdate);
		BigInteger totalStudentsPresent = (BigInteger) q.uniqueResult();
		BigInteger totalStudentsAbsent = totalStudents.subtract(totalStudentsPresent);

		tx.commit();
		session.close();

		lStudentAttendanceStatsMap.put("total_count", totalStudents);
		lStudentAttendanceStatsMap.put("school_in_count", totalStudentsIn);
		lStudentAttendanceStatsMap.put("school_out_count", totalStudentsOut);
		lStudentAttendanceStatsMap.put("abnormal_count", totalStudentsAbnormal);
		lStudentAttendanceStatsMap.put("absent_count", totalStudentsAbsent);

		lStudentAttendanceStats.put("statistics", lStudentAttendanceStatsMap);

		return lStudentAttendanceStats;
	}

	@Override
	public HashMap<String, Object> getAbsentAbnormalStudentsList(int school_id, String grade, String student_class,
			String inputdate) {
		HashMap<String, Object> lAbsentAbnormalStudentsList = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> lAbsentStudentsList = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> lAbnormalStudentsList = new ArrayList<HashMap<String, Object>>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query q = null;
		List<Object[]> rows = null;
		StringBuffer checkGradeClass = new StringBuffer("");

		if (null != grade) {
			checkGradeClass.append("AND class_grade.grade = '" + grade + "'");
		}
		if (null != student_class) {
			checkGradeClass.append(" AND class_grade.class = '" + student_class + "'");
		}

		// Query to get absent students list
		String qStudentsAbsent = "SELECT students.student_id, students.name, class_grade.grade, class_grade.class "
				+ "FROM students, class_grade " + "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND class_grade.school_id = ? " + checkGradeClass + "AND DATE(students.created_date) <= ? "
				+ "AND students.student_id IN( SELECT DISTINCT device_students.student_id FROM device_students "
				+ "WHERE device_students.device_uuid NOT IN " + "(SELECT device_events.uuid FROM device_events "
				+ "WHERE (device_events.event_id = ? OR device_events.event_id = ?) "
				+ "AND DATE(device_events.event_occured_date) = ?) " + "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) )";

		q = session.createSQLQuery(qStudentsAbsent);
		q.setParameter(0, school_id);
		q.setParameter(1, inputdate);
		q.setParameter(2, SCHOOL_ENTRY_ID);
		q.setParameter(3, SCHOOL_EXIT_ID);
		q.setParameter(4, inputdate);
		q.setParameter(5, inputdate);

		rows = q.list();
		for (Object[] row : rows) {
			HashMap<String, Object> lAbsentStudentsMap = new HashMap<String, Object>();
			Integer studentId = (Integer) row[0];
			String studentName = row[1].toString();
			String studentGrade = row[2].toString();
			String studentClass = row[3].toString();

			lAbsentStudentsMap.put("student_id", studentId);
			lAbsentStudentsMap.put("student_name", studentName);
			lAbsentStudentsMap.put("student_grade", studentGrade);
			lAbsentStudentsMap.put("student_class", studentClass);

			lAbsentStudentsList.add(lAbsentStudentsMap);
		}

		// Query to get total abnormal students
		String qStudentsAbnormal = "SELECT students.student_id, students.name, class_grade.grade, class_grade.class, "
				+ "IF(device_events.in_time IS NULL, device_events.out_time, device_events.in_time) AS abnormal_time, device_events.abnormal_reason "
				+ "FROM students, class_grade, device_students, device_events "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid "
				+ "AND (device_events.event_id = ? OR device_events.event_id = ?) "
				+ "AND DATE(device_events.event_occured_date) = ? "
				+ "AND device_events.is_entry_exit_abnormal = 'yes' " + "AND class_grade.school_id = ? "
				+ "AND DATE(students.created_date) <= ? " + "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) "
				+ checkGradeClass;

		q = session.createSQLQuery(qStudentsAbnormal);
		q.setParameter(0, SCHOOL_ENTRY_ID);
		q.setParameter(1, SCHOOL_EXIT_ID);
		q.setParameter(2, inputdate);
		q.setParameter(3, school_id);
		q.setParameter(4, inputdate);
		q.setParameter(5, inputdate);

		rows = q.list();
		for (Object[] row : rows) {
			String abnormalReason = "";
			String abnormalTime = "";
			boolean isExists = false;
			HashMap<String, Object> lAbnormalStudentsMap = new HashMap<String, Object>();
			Integer studentId = (Integer) row[0];
			String studentName = row[1].toString();
			String studentGrade = row[2].toString();
			String studentClass = row[3].toString();
			if (null != row[4])
				abnormalTime = row[4].toString();
			if (null != row[5])
				abnormalReason = row[5].toString();

			if (null != lAbnormalStudentsList && lAbnormalStudentsList.size() > 0) {
				for (HashMap<String, Object> hashMap : lAbnormalStudentsList) {
					log.info("fresh studentId" + studentId);
					log.info("existing studentId" + hashMap.get("student_id"));
					if (hashMap.get("student_id").equals(studentId)) {
						isExists = true;
						log.info("isExists" + isExists);
					}
				}
			}

			// Object returnedValue =lAbnormalStudentsMap.put("student_id",
			// studentId);
			if (!isExists) {
				lAbnormalStudentsMap.put("student_id", studentId);

				lAbnormalStudentsMap.put("student_name", studentName);

				lAbnormalStudentsMap.put("student_grade", studentGrade);

				lAbnormalStudentsMap.put("student_class", studentClass);

				lAbnormalStudentsMap.put("student_abnormalTime", abnormalTime);

				lAbnormalStudentsMap.put("student_abnormalReason", abnormalReason);

				lAbnormalStudentsList.add(lAbnormalStudentsMap);
			}
		}

		lAbsentAbnormalStudentsList.put("absent", lAbsentStudentsList);
		lAbsentAbnormalStudentsList.put("abnormal", lAbnormalStudentsList);

		tx.commit();
		session.close();

		return lAbsentAbnormalStudentsList;
	}

	@Override
	public HashMap<String, Object> getStudentSOSAlertStats(int school_id, String inputdate) {
		HashMap<String, Object> lStudentSOSStats = new HashMap<String, Object>();
		HashMap<String, Object> lStudentSOSStatsMap = new HashMap<String, Object>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		// Query to get total SOS Alerts sent by students in a school
		String totalStudentsCountQuery = "SELECT COUNT(students.student_id) AS sos_count "
				+ "FROM students, class_grade, device_students, device_events "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid " + "AND device_events.event_id = ? "
				+ "AND DATE(device_events.event_occured_date) = ? " + "AND class_grade.school_id = ? "
				+ "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) ";

		Query q = session.createSQLQuery(totalStudentsCountQuery);
		q.setParameter(0, SOS_ALERT_ID);
		q.setParameter(1, inputdate);
		q.setParameter(2, school_id);
		q.setParameter(3, inputdate);
		BigInteger totalSOSAlerts = (BigInteger) q.uniqueResult();

		// Query to get total SOS Alerts sent by students in a school and Closed
		// by P/A/S
		String totalStudentsInQuery = "SELECT COUNT(students.student_id) AS sos_closed_count "
				+ "FROM students, class_grade, device_students, device_events, closed_device_events "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid "
				+ "AND closed_device_events.device_event_id = device_events.device_event_id "
				+ "AND device_events.event_id = ? " + "AND DATE(device_events.event_occured_date) = ? "
				+ "AND class_grade.school_id =  ? " + "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) ";

		q = session.createSQLQuery(totalStudentsInQuery);
		q.setParameter(0, SOS_ALERT_ID);
		q.setParameter(1, inputdate);
		q.setParameter(2, school_id);
		q.setParameter(3, inputdate);
		BigInteger totalSOSAlertsClosed = (BigInteger) q.uniqueResult();

		tx.commit();
		session.close();

		lStudentSOSStatsMap.put("total_count", totalSOSAlerts);
		lStudentSOSStatsMap.put("closed_count", totalSOSAlertsClosed);
		lStudentSOSStatsMap.put("open_count", totalSOSAlerts.subtract(totalSOSAlertsClosed));

		lStudentSOSStats.put("statistics", lStudentSOSStatsMap);

		return lStudentSOSStats;
	}

	@Override
	public HashMap<String, Object> getStudentSOSAlerts(int school_id, String inputdate) {
		HashMap<String, Object> lStudentSOSAlertsList = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> lSOSStudentsList = new ArrayList<HashMap<String, Object>>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query q = null;
		List<Object[]> rows = null;
		Users userSOSClosed = null;

		// Query to get total abnormal students
		String qStudentsAbnormal = "SELECT students.student_id, students.name, class_grade.class, "
				+ "DATE_FORMAT(device_events.event_occured_date,'%H:%i:%s') AS event_occured_time, closed_device_events.duration, "
				+ "closed_device_events.parent_id, closed_device_events.admin_id, closed_device_events.staff_id, "
				+ "device_events.gps_location_data, device_events.device_event_id, closed_device_events.is_sos_abnormal, "
				+ "device_events.event_occured_date, device_events.is_entry_exit_abnormal "
				+ "FROM students, class_grade, device_students, device_events "
				+ "LEFT JOIN closed_device_events ON closed_device_events.device_event_id = device_events.device_event_id "
				+ "WHERE students.class_grade_id = class_grade.class_grade_id "
				+ "AND students.student_id = device_students.student_id "
				+ "AND device_students.device_uuid = device_events.uuid " + "AND device_events.event_id = ? "
				+ "AND DATE(device_events.event_occured_date) = ? " + "AND class_grade.school_id = ? "
				+ "AND ? BETWEEN DATE(device_students.start_date) "
				+ "AND IF(device_students.end_date IS NULL, CURDATE(), DATE(device_students.end_date)) ";

		q = session.createSQLQuery(qStudentsAbnormal);
		q.setParameter(0, SOS_ALERT_ID);
		q.setParameter(1, inputdate);
		q.setParameter(2, school_id);
		q.setParameter(3, inputdate);

		rows = q.list();
		for (Object[] row : rows) {
			HashMap<String, Object> lSOSStudentsMap = new HashMap<String, Object>();
			String gpsLocation = null;
			Integer studentId = (Integer) row[0];
			String studentName = row[1].toString();
			String studentClass = row[2].toString();
			String eventTime = row[3].toString();
			Integer sosDuration = (Integer) row[4];
			Integer parentId = (Integer) row[5];
			Integer adminId = (Integer) row[6];
			Integer staffId = (Integer) row[7];
			if (null != row[8])
				gpsLocation = row[8].toString();
			Integer deviceEventId = (Integer) row[9];
			String eventTimeStamp = row[11].toString();
			String isCloseAbnormal = (null != row[10]) ? row[10].toString() : "";
			String isSOSEntryAbnormal = (null != row[12]) ? row[12].toString() : "";

			String isAbnormal = "";
			if (isSOSEntryAbnormal.equals("yes")) {
				if (!isCloseAbnormal.equals("no")) {
					isAbnormal = "yes";
				}
			}

			String parentName = null;
			if (null != parentId) {
				userSOSClosed = userService.getUser(parentId);
				parentName = userSOSClosed.getName();
			}
			String staffName = null;
			if (null != staffId) {
				userSOSClosed = userService.getUser(staffId);
				staffName = userSOSClosed.getName();
			}
			String adminName = null;
			if (null != adminId) {
				userSOSClosed = userService.getUser(adminId);
				adminName = userSOSClosed.getName();
			}

			lSOSStudentsMap.put("student_id", studentId);
			lSOSStudentsMap.put("student_name", studentName);
			lSOSStudentsMap.put("student_class", studentClass);
			lSOSStudentsMap.put("sos_start_time", eventTime);
			lSOSStudentsMap.put("sos_duration", sosDuration);
			lSOSStudentsMap.put("wearable_name", studentName);
			lSOSStudentsMap.put("parent_name", parentName);
			lSOSStudentsMap.put("staff_name", staffName);
			lSOSStudentsMap.put("admin_name", adminName);
			lSOSStudentsMap.put("gps_location_data", gpsLocation);
			lSOSStudentsMap.put("device_event_id", deviceEventId);
			lSOSStudentsMap.put("abnormal", isAbnormal);
			lSOSStudentsMap.put("sos_datetime", eventTimeStamp);

			lSOSStudentsList.add(lSOSStudentsMap);
		}

		lStudentSOSAlertsList.put("alerts", lSOSStudentsList);

		return lStudentSOSAlertsList;
	}

	@Override
	public Map<String, Object> searchStudents(int school_id, String studentName, String registrationNo,
			String deviceUUID, String roleType, int start, int total) {
		HashMap<String, Object> lStudentsMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> lStudentsList = new ArrayList<HashMap<String, Object>>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query q = null;
		List<Object[]> rows = null;
		StringBuffer searchCriteria = new StringBuffer("");

		if (null != studentName && !studentName.equals("0")) {
			searchCriteria.append("AND `students`.`name` LIKE '%" + studentName + "%'");
		}
		if (null != registrationNo && !registrationNo.equals("0")) {
			searchCriteria.append(" AND `students`.`registartion_no` = '" + registrationNo + "'");
		}
		if (null != deviceUUID && !deviceUUID.equals("0")) {
			searchCriteria.append(" AND `device_students`.`device_uuid` = '" + deviceUUID + "'");
		}

		if (roleType.equals(Constant.SchoolAdmin)) {
			searchCriteria.append(" AND class_grade.school_id = ?");
		}

		String qStudents = "SELECT `students`.`student_id`, `students`.`registartion_no`, `students`.`roll_no`, "
				+ "`students`.`name`, `students`.`emergency_contact`, `class_grade`.`grade`, `class_grade`.`class`, "
				+ "`device_students`.`device_uuid`, `device_students`.`status`, "
				+ "(SELECT CONCAT(`gps_location_data`, ',', DATE_FORMAT(`event_occured_date`, '%Y-%m-%d %r')) FROM `device_events` "
				+ "WHERE `device_events`.`uuid` = `device_students`.`device_uuid` "
				+ "AND (`device_events`.`gps_location_data` IS NOT NULL OR `device_events`.`gps_location_data` <> '') "
				+ "AND `device_events`.`event_id` IN "
				+ "(SELECT `event_id` FROM `supported_events` WHERE `supported_fields` LIKE '%gps_location_data%') "
				+ "ORDER BY `event_occured_date` DESC LIMIT 1) AS `last_know_location` "
				+ "FROM `students`, `class_grade`, `device_students` "
				+ "WHERE `students`.`class_grade_id` = `class_grade`.`class_grade_id` "
				+ "AND `students`.`student_id` = `device_students`.`student_id` " + searchCriteria
				+ " ORDER BY `students`.`name`, `device_students`.`status` LIMIT " + start + "," + total;

		q = session.createSQLQuery(qStudents);
		if (roleType.equals(Constant.SchoolAdmin)) {
			q.setParameter(0, school_id);
		}
		rows = q.list();
		for (Object[] row : rows) {
			HashMap<String, Object> lSOSStudentsMap = new HashMap<String, Object>();
			Integer student_id = (Integer) row[0];
			String registartion_no = (null != row[1]) ? row[1].toString() : "";
			String roll_no = (null != row[2]) ? row[2].toString() : "";
			String student_name = (null != row[3]) ? row[3].toString() : "";
			String emergency_contact = (null != row[4]) ? row[4].toString() : "";
			String student_grade = (null != row[5]) ? row[5].toString() : "";
			String student_class = (null != row[6]) ? row[6].toString() : "";
			String device_uuid = (null != row[7]) ? row[7].toString() : "";
			String device_status = (null != row[8]) ? row[8].toString() : "";
			String gps_location_data = (null != row[9]) ? row[9].toString() : "";

			lSOSStudentsMap.put("student_id", student_id);
			lSOSStudentsMap.put("registartion_no", registartion_no);
			lSOSStudentsMap.put("roll_no", roll_no);
			lSOSStudentsMap.put("student_name", student_name);
			lSOSStudentsMap.put("emergency_contact", emergency_contact);
			lSOSStudentsMap.put("student_grade", student_grade);
			lSOSStudentsMap.put("student_class", student_class);
			lSOSStudentsMap.put("device_uuid", device_uuid);
			lSOSStudentsMap.put("device_status", device_status);
			lSOSStudentsMap.put("gps_location_data", gps_location_data);

			lStudentsList.add(lSOSStudentsMap);
		}

		lStudentsMap.put("search_result", lStudentsList);

		return lStudentsMap;
	}

	public Map<String, Object> uploadStudentsAndLinkDevice(List<StudentCSVModel> studentCsvList, Users loginUser) {
		log.info("Into uploadStudentsAndLinkDevice {");
		Session session = null;
		Transaction tx = null;
		ClassGrade cg = null;
		Students students = null;
		DeviceStudents ds = null;
		int stndtsCreated = 0, stndsUpdate = 0, stundFaild = 0, deviceStndsFailed = 0;

		Map<Integer, Object> failedStudentsMap = new HashMap<>();
		Map<String, Object> finalMap = new HashMap<String, Object>();
		try {
			session = sessionFactory.openSession();
			log.info("Before studentCsvList Checks, Size:" + studentCsvList.size());
			for (int i = 0; i < studentCsvList.size(); i++) {
				log.info("Before studentCsvList Checks" + i);
				if (null != studentCsvList.get(i).getUuid() && null != studentCsvList.get(i).getAdmissionNo()
						&& null != studentCsvList.get(i).getGrade() && null != studentCsvList.get(i).getStudentClass()
						&& studentCsvList.get(i).getUuid().trim().length() > 0
						&& studentCsvList.get(i).getAdmissionNo().trim().length() > 0
						&& studentCsvList.get(i).getGrade().trim().length() > 0
						&& studentCsvList.get(i).getStudentClass().trim().length() > 0 
						&& studentCsvList.get(i).getGrade().trim().length() <=3
						&& studentCsvList.get(i).getStudentClass().trim().length() <=3
						&& studentCsvList.get(i).getAdmissionNo().trim().length() <=10) {
					log.info("Mandatory Checks Passed" + i + "\n" + studentCsvList.get(i).getUuid()
							+ ", getUuid:" + studentCsvList.get(i).getUuid()
							+ ", getAdmissionNo:" + studentCsvList.get(i).getAdmissionNo()
							+ ", getGrade:" + studentCsvList.get(i).getGrade()
							+ ", getStudentClass:" + studentCsvList.get(i).getStudentClass());
					
					Devices devicesExistForGivenSchoolId = this.deviceDAO.findDeviceForSchoolId(
							studentCsvList.get(i).getUuid(), loginUser.getAccountId());
					students = isRegistrationNumberExists(studentCsvList.get(i).getAdmissionNo(),
							loginUser.getAccountId());
					cg = this.classGradeDAO.checkIfClassGradeExists(loginUser.getAccountId(),
							studentCsvList.get(i).getGrade(), studentCsvList.get(i).getStudentClass());
					if (null != devicesExistForGivenSchoolId) {
						tx = session.beginTransaction();
						if (cg == null) {
							cg = new ClassGrade();
							cg.setGrade(studentCsvList.get(i).getGrade());
							cg.setStudentClass(studentCsvList.get(i).getStudentClass());
							cg.setSchoolId(loginUser.getAccountId());
							cg.setTeacher_id(null);
							cg.setCreated_date(new Date());
							session.save(cg);
						} else {
							cg.setUpdated_date(new Date());
							session.merge(cg);
						}

						if (students == null) {
							students = new Students();
							students.setClassGrade(cg);
							students.setCreateDate(new java.util.Date());
							students.setRegistartion_no(studentCsvList.get(i).getAdmissionNo());
							students.setRollNo(studentCsvList.get(i).getStudentRollNo());
							students.setName(studentCsvList.get(i).getStudentName());
							students.setEmergencyContactNo(studentCsvList.get(i).getEmergency_contact());
							session.save(students);
							stndtsCreated++;
						} else {
							students.setClassGrade(cg);
							students.setUpdatedDate(new java.util.Date());
							students.setRegistartion_no(studentCsvList.get(i).getAdmissionNo());
							students.setRollNo(studentCsvList.get(i).getStudentRollNo());
							students.setName(studentCsvList.get(i).getStudentName());
							students.setEmergencyContactNo(studentCsvList.get(i).getEmergency_contact());
							session.merge(students);
							stndsUpdate++;
						}
						tx.commit();

						if (studentCsvList.get(i).getUuid() != null) {
							ds = this.deviceStudentsDAO.createDeviceStudents(studentCsvList.get(i).getUuid(), students,
									"Upload");
							if (ds == null) {
								deviceStndsFailed++;
								failedStudentsMap.put(i,
										"Student Created/Updated but device is assigned to another student");
							}

							log.info("ds in StudentsDaoImpl" + ds);
						}
					} else {
						stundFaild++;
						failedStudentsMap.put(i, "Device not mapped to school");
						log.info("Student Failed Count in StudentsDaoImpl" + "\t" + stundFaild);
					}
				} else {
					stundFaild++;
					failedStudentsMap.put(i, "At least one of the mandatory field is missing or invalid");
					log.info("At least one of the mandatory field is missing for Row>" + "\t" + i);
				}
			}
		} catch (Exception e) {
			log.info("Exception Occured in uploadStudentsAndLinkDevice()" + e);
			if (cg != null)
				session.evict(cg);
			if (students != null)
				session.evict(students);
			if (ds != null)
				session.evict(ds);
			tx.rollback();
		} finally {
			session.close();
		}

		finalMap.put("totalStudentsCreated", stndtsCreated);
		finalMap.put("totalStudentsUpdated", stndsUpdate);
		finalMap.put("totalStudentsFailed", stundFaild);
		finalMap.put("deviceStndsFailed", deviceStndsFailed);
		finalMap.put("failedStudentsMap", failedStudentsMap);

		log.info("Exiting uploadStudentsAndLinkDevice() }");
		return finalMap;
	}

	@Override
	public String createWebStudentsAndLinkDevice(StudentsWebModel studentsWebModel, Users loginUser) {
		log.info("Into createStudentsAndLinkDevice() {");
		Session session = null;
		Transaction tx = null;
		ClassGrade cg = null;
		Students students = null;
		DeviceStudents ds = null;
		String createOrUpdate = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			cg = this.classGradeDAO.checkIfClassGradeExists(loginUser.getAccountId(),
					studentsWebModel.getAccntMgtStudentGrade(), studentsWebModel.getAccMgtStudentClass());

			/**
			 * Based on Class Grade Table Entry, prepare Students Object.
			 */
			students = new Students();
			students.setClassGrade(cg);
			students.setCreateDate(new java.util.Date());
			students.setRegistartion_no(studentsWebModel.getAccMgtStudentAppno());
			students.setRollNo(studentsWebModel.getAccMgtStudentRollno());
			students.setName(studentsWebModel.getAccMgtStudentName());
			students.setEmergencyContactNo(studentsWebModel.getAccMgtStudentContact());
			session.save(students);
			createOrUpdate = "Create";

			tx.commit();

			/**
			 * Based on Students Object created above , create an entry into
			 * Device_Students Table.
			 */
			try {
				if (studentsWebModel.getAccMgtStudentUuid() != null) {
					ds = this.deviceStudentsDAO.createDeviceStudents(studentsWebModel.getAccMgtStudentUuid(), students,
							"Web");
					if (null == ds)
						createOrUpdate = "deviceMappingError";
					log.info("ds in StudentsDaoImpl" + ds);
				}
			} catch (RecordAlreadyExistsException re) {
				log.info("Record Already Exists Exception While Mapping Devices to Student" + "\t" + re);
			}

		} catch (Exception e) {
			log.info("Exception Occured in createStudentsAndLinkDevice()" + e);
			if (cg != null)
				session.evict(cg);
			if (students != null)
				session.evict(students);
			if (ds != null)
				session.evict(ds);
			tx.rollback();
		} finally {
			session.close();
		}
		log.info("ExitingCreateStudentsAndLinkDevice() }");
		return createOrUpdate;
	}

	@Override
	public Students isRegistrationNumberExists(String regNo, Integer schoolId) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		Students students = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria studentsCriteria = session.createCriteria(Students.class, "st");
			studentsCriteria.createAlias("st.classGrade", "cg");
			studentsCriteria.add(Restrictions.eq("cg.schoolId", schoolId));
			studentsCriteria.add(Restrictions.eq("st.registartion_no", regNo));
			students = (Students) studentsCriteria.uniqueResult();
		} catch (Exception e) {

		} finally {
			session.close();
		}
		return students;
	}

	@Override
	public boolean updateStudentsApi(Users loginUser, StudentsModel studentsModel, Students st) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		ClassGrade cg = null;
		boolean studentUpdated = false;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			if (studentsModel.getGrade() != null && studentsModel.getStudentClass() != null) {
				cg = this.classGradeDAO.checkIfClassGradeExists(loginUser.getAccountId(),
						studentsModel.getGrade(), studentsModel.getStudentClass());

				st.setName(studentsModel.getName());
				st.setEmergencyContactNo(studentsModel.getEmergency_contact());
				st.setClassGrade(cg);
				session.update(st);
				tx.commit();
				studentUpdated = true;
			}
		} catch (Exception e) {
			log.info("Exception Occured in updateStudentsApi() " + e);
			if (null != st)
				session.evict(st);
			if (null != cg)
				session.evict(cg);
			tx.rollback();
		} finally {
			session.close();
		}
		return studentUpdated;
	}

	@Override
	public int deleteStudentApi(int studentId) {
		// TODO Auto-generated method stub
		/**
		 * Run below Query in SQL to find all dependent tables with student_id
		 * as column that needs to be deleted before removing entry from
		 * Students table SELECT DISTINCT TABLE_NAME FROM
		 * INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME in('student_id') AND
		 * TABLE_SCHEMA='liteon'; Below tables needs to be taken care 1.
		 * device_students 2. parent_kids 3. rewards_students 4. students
		 */
		Session session = null;
		int deletedStnds = 0;
		Transaction deleteDeviceStudentsTransaction = null, deleteEventSubscriptionsTransaction = null,
				deleteParentkidsTransaction = null, deleteRewardsStudentsTransaction = null,
				deleteStudentsTransaction = null;
		try {
			session = sessionFactory.openSession();

			/**
			 * Delete Device Students Related information from Device Students
			 * Table
			 */

			deleteDeviceStudentsTransaction = session.beginTransaction();

			String deviceUUIDQry = "from DeviceStudents as ds  where ds.students.studentId=:studentId AND ds.status = 'active' ";

			Query uuidQry = session.createQuery(deviceUUIDQry);
			uuidQry.setParameter("studentId", studentId);
			DeviceStudents deviceStudents = (DeviceStudents) uuidQry.uniqueResult();

			String deleteQueryForDeviceStudents = "DELETE from DeviceStudents as ds  where ds.students.studentId=:studentId ";

			Query deleteDsQry = session.createQuery(deleteQueryForDeviceStudents);
			deleteDsQry.setParameter("studentId", studentId);
			int deletedDS = deleteDsQry.executeUpdate();
			log.info("<<*deletedDS*>>" + "\t" + deletedDS);

			if (deletedDS > 0) {
				Criteria criteriaAccounts = session.createCriteria(Devices.class);
				criteriaAccounts.add(Restrictions.eq("uuid", deviceStudents.getDeviceuuid()));
				Devices devices = (Devices) criteriaAccounts.uniqueResult();
				devices.setStatus("");
				session.update(devices);
			}

			deleteDeviceStudentsTransaction.commit();

			/**
			 * Delete Entry from Event Subscriptions Table
			 */

			deleteEventSubscriptionsTransaction = session.beginTransaction();

			String deleteEventSubscriptionsQuery = "DELETE from EventSubscriptions as es  where es.students.studentId=:studentId ";

			Query deleteESQry = session.createQuery(deleteEventSubscriptionsQuery);
			deleteESQry.setParameter("studentId", studentId);
			int deletedES = deleteESQry.executeUpdate();
			log.info("<<*deletedES*>>" + "\t" + deletedES);

			deleteEventSubscriptionsTransaction.commit();

			// Delete student id entry from ParentKids
			deleteParentkidsTransaction = session.beginTransaction();
			String deleteQueryForParentkids = "DELETE from ParentKids as pk where pk.students.studentId=:studentId ";

			Query deleteParentkids = session.createQuery(deleteQueryForParentkids);
			deleteParentkids.setParameter("studentId", studentId);
			deletedStnds = deleteParentkids.executeUpdate();
			deleteParentkidsTransaction.commit();

			// Delete Student id entry from RewardsStudents
			deleteRewardsStudentsTransaction = session.beginTransaction();
			String deleteQueryForRewardsStudents = "DELETE from RewardsStudents as rs where rs.students.studentId=:studentId ";

			Query deleteRewardsStudents = session.createQuery(deleteQueryForRewardsStudents);
			deleteRewardsStudents.setParameter("studentId", studentId);
			deletedStnds = deleteRewardsStudents.executeUpdate();
			deleteRewardsStudentsTransaction.commit();

			/**
			 * Delete Students related Student Information from Students table
			 */

			deleteStudentsTransaction = session.beginTransaction();
			String deleteQueryForStudent = "DELETE from Students as st  where st.studentId=:studentId ";

			Query deleteStudent = session.createQuery(deleteQueryForStudent);
			deleteStudent.setParameter("studentId", studentId);
			deletedStnds = deleteStudent.executeUpdate();
			deleteStudentsTransaction.commit();
		} catch (Exception e) {
			log.info("Exception Caught in deleteStudentApi ()" + "\t" + e);
		} finally {
			session.close();
		}

		return deletedStnds;
	}

	@Override
	public HashMap<String, Object> getStudentRewardRankings(int school_id, String startdate, String enddate,
			String grade) {
		HashMap<String, Object> lStudentRewardRankings = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> lRewardsByCategoryList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> lRewardsByCategory = new HashMap<String, Object>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		StringBuffer checkGradeClass = new StringBuffer("");
		List<Object[]> rows = null;
		Query q = null;
		String download_url = this.configProperties.getProperty("downloads.url");
		String rewards_path = this.configProperties.getProperty("rewards.download.path");

		if (null != grade && !grade.equals("0")) {
			checkGradeClass.append(" AND class_grade.grade = '" + grade + "' ");
		}

		log.info("Query to get category wise total rewards received count within a Grade");
		String qRewardsByCategory = "SELECT `rewards_category`.`rewards_category_id`, `rewards_category`.`category_name`, "
				+ "`rewards_category`.`category_icon_url`, `category_group_rewards`.`reward_category_count` "
				+ "FROM `rewards_category` " + "LEFT JOIN (SELECT `rewards_category`.`rewards_category_id`, "
				+ "SUM(`rewards_students`.`received_count`) AS reward_category_count "
				+ "FROM `rewards_category`, `rewards`, `rewards_students`, `students`, `class_grade` "
				+ "WHERE `rewards`.`rewards_category_id` = `rewards_category`.`rewards_category_id` "
				+ "AND `rewards_students`.`reward_id` = `rewards`.`reward_id` "
				+ "AND `rewards_students`.`student_id` = `students`.`student_id` "
				+ "AND `students`.`class_grade_id` = `class_grade`.`class_grade_id` "
				+ "AND `class_grade`.`school_id` = `rewards_category`.`school_id` "
				+ "AND `rewards_category`.`school_id` = ? "
				+ "AND DATE(`rewards_students`.`updated_date`) BETWEEN ? AND ? " + checkGradeClass
				+ "GROUP BY `rewards_category`.`rewards_category_id` " + ") AS `category_group_rewards` "
				+ "ON `category_group_rewards`.`rewards_category_id` = `rewards_category`.`rewards_category_id` "
				+ "WHERE `rewards_category`.`school_id` = ?";

		q = session.createSQLQuery(qRewardsByCategory);
		q.setParameter(0, school_id);
		q.setParameter(1, startdate);
		q.setParameter(2, enddate);
		q.setParameter(3, school_id);

		rows = q.list();

		for (Object[] row : rows) {
			HashMap<String, Object> lRewardsByCategoryMap = new HashMap<String, Object>();
			Integer rewards_category_id = (Integer) row[0];
			String category_name = row[1].toString();
			String category_icon = row[2].toString();
			BigDecimal reward_category_count = (BigDecimal) row[3];
			String category_icon_url = download_url + rewards_path + "/" + category_icon;

			lRewardsByCategoryMap.put("rewards_category_id", rewards_category_id);
			lRewardsByCategoryMap.put("total_count", reward_category_count);
			lRewardsByCategoryMap.put("name", category_name);
			lRewardsByCategoryMap.put("category_icon_url", category_icon_url);

			lRewardsByCategoryList.add(lRewardsByCategoryMap);
		}

		lRewardsByCategory.put("reward_statistics", lRewardsByCategoryList);

		if (null != grade && !grade.equals("0")) {
			ArrayList<HashMap<String, Object>> lRewardsByClassCategoryList = new ArrayList<HashMap<String, Object>>();
			log.info("Query to get class wise total rewards received");
			String sRewardsByClass = "SELECT SUM(`rewards_students`.`received_count`) AS received_count, `class_grade`.`class` "
					+ "FROM `rewards_students`, `students`, `class_grade` "
					+ "WHERE `rewards_students`.`student_id` = `students`.`student_id` "
					+ "AND `class_grade`.`class_grade_id` = `students`.`class_grade_id` "
					+ "AND `class_grade`.`school_id` = ? "
					+ "AND DATE(`rewards_students`.`updated_date`) BETWEEN ? AND ? " + "AND class_grade.grade = ? "
					+ "GROUP BY `class_grade`.`class` "
					+ "ORDER BY received_count DESC";

			Query qRewardsByClass = session.createSQLQuery(sRewardsByClass);
			qRewardsByClass.setParameter(0, school_id);
			qRewardsByClass.setParameter(1, startdate);
			qRewardsByClass.setParameter(2, enddate);
			qRewardsByClass.setParameter(3, grade);

			rows = qRewardsByClass.list();
			int index = 0;
			for (Object[] row : rows) {
				HashMap<String, Object> lRewardsByClassMap = new HashMap<String, Object>();
				index++;
				BigDecimal reward_category_count = (BigDecimal) row[0];
				String studentClass = row[1].toString();

				lRewardsByClassMap.put("class", studentClass);
				lRewardsByClassMap.put("total_class_rewards", reward_category_count);
				lRewardsByClassMap.put("class_rank", index);

				log.info("Query to get class topper among total rewards received");
				String sClassTopper = "SELECT SUM(`rewards_students`.`received_count`) AS received_count, "
						+ "`students`.`name`, `students`.`student_id` "
						+ "FROM `rewards_students`, `students`, `class_grade` "
						+ "WHERE `rewards_students`.`student_id` = `students`.`student_id` "
						+ "AND `class_grade`.`class_grade_id` = `students`.`class_grade_id` "
						+ "AND `class_grade`.`school_id` = ? "
						+ "AND DATE(`rewards_students`.`updated_date`) BETWEEN ? AND ? " + "AND class_grade.grade = ? "
						+ "AND class_grade.class = ? " + "GROUP BY `students`.`name`, `students`.`student_id`"
						+ "ORDER BY `received_count` DESC LIMIT 1";

				Query qClassTopper = session.createSQLQuery(sClassTopper);
				qClassTopper.setParameter(0, school_id);
				qClassTopper.setParameter(1, startdate);
				qClassTopper.setParameter(2, enddate);
				qClassTopper.setParameter(3, grade);
				qClassTopper.setParameter(4, studentClass);

				List<Object[]> toppers = qClassTopper.list();
				for (Object[] topper : toppers) {
					BigDecimal topper_rewards_count = (BigDecimal) topper[0];
					String topper_student_name = topper[1].toString();
					Integer topper_student_id = (Integer) topper[2];

					lRewardsByClassMap.put("top_student_id", topper_student_id);
					lRewardsByClassMap.put("top_student_name", topper_student_name);
					lRewardsByClassMap.put("top_student_rewards", topper_rewards_count);
				}

				log.info("Query to get category wise total rewards received count within a Grade & Class");
				String sClassCategoriesList = "SELECT `rewards_category`.`rewards_category_id`, `rewards_category`.`category_name`, "
						+ "`category_group_rewards`.`reward_category_count` " + "FROM `rewards_category` "
						+ "LEFT JOIN (SELECT `rewards_category`.`rewards_category_id`, "
						+ "SUM(`rewards_students`.`received_count`) AS reward_category_count "
						+ "FROM `rewards_category`, `rewards`, `rewards_students`, `students`, `class_grade` "
						+ "WHERE `rewards`.`rewards_category_id` = `rewards_category`.`rewards_category_id` "
						+ "AND `rewards_students`.`reward_id` = `rewards`.`reward_id` "
						+ "AND `rewards_students`.`student_id` = `students`.`student_id` "
						+ "AND `students`.`class_grade_id` = `class_grade`.`class_grade_id` "
						+ "AND `class_grade`.`school_id` = `rewards_category`.`school_id` "
						+ "AND `rewards_category`.`school_id` = ? "
						+ "AND DATE(`rewards_students`.`updated_date`) BETWEEN ? AND ? " + "AND class_grade.grade = ? "
						+ "AND class_grade.class = ? " + "GROUP BY `rewards_category`.`rewards_category_id` "
						+ ") AS `category_group_rewards` "
						+ "ON `category_group_rewards`.`rewards_category_id` = `rewards_category`.`rewards_category_id` "
						+ "WHERE `rewards_category`.`school_id` = ?";

				Query qClassCategoriesList = session.createSQLQuery(sClassCategoriesList);
				qClassCategoriesList.setParameter(0, school_id);
				qClassCategoriesList.setParameter(1, startdate);
				qClassCategoriesList.setParameter(2, enddate);
				qClassCategoriesList.setParameter(3, grade);
				qClassCategoriesList.setParameter(4, studentClass);
				qClassCategoriesList.setParameter(5, school_id);

				List<Object[]> classCategories = qClassCategoriesList.list();
				HashMap<String, Object> lRewardsByCategoryClass = new HashMap<String, Object>();
				ArrayList<HashMap<String, Object>> lRewardsByCategoryClassList = new ArrayList<HashMap<String, Object>>();
				for (Object[] classCategory : classCategories) {
					HashMap<String, Object> lRewardsByCategoryClassMap = new HashMap<String, Object>();
					Integer class_category_id = (Integer) classCategory[0];
					String class_category_name = classCategory[1].toString();
					BigDecimal class_category_count = (BigDecimal) classCategory[2];

					lRewardsByCategoryClassMap.put("id", class_category_id);
					lRewardsByCategoryClassMap.put("total_count", class_category_count);
					lRewardsByCategoryClassMap.put("name", class_category_name);

					log.info("Query to get class topper among total rewards received within the Category above");
					String sCategoryClassTopper = "SELECT SUM(`rewards_students`.`received_count`) AS received_count, "
							+ "`students`.`name`, `students`.`student_id` "
							+ "FROM `rewards_category`, `rewards`, `rewards_students`, `students`, `class_grade` "
							+ "WHERE `rewards_category`.`school_id` = `class_grade`.`school_id` "
							+ "AND `rewards_category`.`rewards_category_id` = `rewards`.`rewards_category_id` "
							+ "AND `rewards_students`.`reward_id` = `rewards`.`reward_id` "
							+ "AND `rewards_students`.`student_id` = `students`.`student_id` "
							+ "AND `class_grade`.`class_grade_id` = `students`.`class_grade_id` "
							+ "AND `class_grade`.`school_id` = `rewards_category`.`school_id` "
							+ "AND `class_grade`.`school_id` = ? "
							+ "AND DATE(`rewards_students`.`updated_date`) BETWEEN ? AND ? "
							+ "AND `class_grade`.`grade` = ? " + "AND `class_grade`.`class` = ? "
							+ "AND `rewards_category`.`rewards_category_id` = ? "
							+ "GROUP BY `students`.`name`, `students`.`student_id`"
							+ "ORDER BY `received_count` DESC LIMIT 1";

					Query qCategoryClassTopper = session.createSQLQuery(sCategoryClassTopper);
					qCategoryClassTopper.setParameter(0, school_id);
					qCategoryClassTopper.setParameter(1, startdate);
					qCategoryClassTopper.setParameter(2, enddate);
					qCategoryClassTopper.setParameter(3, grade);
					qCategoryClassTopper.setParameter(4, studentClass);
					qCategoryClassTopper.setParameter(5, class_category_id);

					List<Object[]> catClasstoppers = qCategoryClassTopper.list();
					for (Object[] catClasstopper : catClasstoppers) {
						BigDecimal topper_rewards_count = (BigDecimal) catClasstopper[0];
						String topper_student_name = catClasstopper[1].toString();
						Integer topper_student_id = (Integer) catClasstopper[2];

						lRewardsByCategoryClassMap.put("top_student_id", topper_student_id);
						lRewardsByCategoryClassMap.put("top_student_name", topper_student_name);
						lRewardsByCategoryClassMap.put("top_student_rewards", topper_rewards_count);
					}

					lRewardsByCategoryClassList.add(lRewardsByCategoryClassMap);
				}
				lRewardsByClassMap.put("reward_categories", lRewardsByCategoryClassList);
				lRewardsByClassCategoryList.add(lRewardsByClassMap);
			}

			lRewardsByCategory.put("class_rankings", lRewardsByClassCategoryList);
		}

		lStudentRewardRankings.put("results", lRewardsByCategory);

		return lStudentRewardRankings;
	}

	@Override
	public HashMap<String, Object> getStudentRewards(int student_id) {
		ArrayList<HashMap<String, Object>> lRewardsByCategoryList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> lRewardsByCategory = new HashMap<String, Object>();
		Session session = sessionFactory.openSession();
		List<Object[]> rows = null;
		Query q = null;
		String download_url = this.configProperties.getProperty("downloads.url");
		String rewards_path = this.configProperties.getProperty("rewards.download.path");

		// Query to get category wise total rewards received count
		String qRewardsByCategory = " SELECT `rewards_category`.`rewards_category_id`, `rewards_category`.`category_name`, "
				+ "`rewards_category`.`category_icon_url`, `rewards`.`reward_id`, `rewards`.`name`, "
				+ "`rewards`.`reward_icon_url`, `rewards_group`.`reward_count` "
				+ "FROM `rewards_category`, `rewards`, "
				+ "(SELECT `rewards`.`reward_id`, SUM(`rewards_students`.`received_count`) AS reward_count "
				+ "FROM `rewards`, `rewards_students` "
				+ "WHERE `rewards_students`.`reward_id` = `rewards`.`reward_id` "
				+ "AND `rewards_students`.`student_id` = ? GROUP BY `rewards`.`reward_id`) AS `rewards_group` "
				+ "WHERE `rewards_category`.`rewards_category_id` = `rewards`.`rewards_category_id` "
				+ "AND `rewards_group`.`reward_id` = `rewards`.`reward_id` "
				+ "ORDER BY `rewards_category`.`rewards_category_id`, `rewards`.`reward_id` ";

		q = session.createSQLQuery(qRewardsByCategory);
		q.setParameter(0, student_id);

		rows = q.list();

		for (Object[] row : rows) {
			HashMap<String, Object> lRewardsByCategoryMap = new HashMap<String, Object>();
			Integer rewards_category_id = (Integer) row[0];
			String category_name = row[1].toString();
			String category_icon_name = row[2].toString();
			Integer reward_id = (Integer) row[3];
			String reward_name = row[4].toString();
			String reward_icon = row[5].toString();
			BigDecimal reward_count = (BigDecimal) row[6];
			String category_icon_url = download_url + rewards_path + "/" + category_icon_name;
			String reward_icon_url = download_url + rewards_path + "/" + reward_icon;

			lRewardsByCategoryMap.put("rewards_category_id", rewards_category_id);
			lRewardsByCategoryMap.put("category_name", category_name);
			lRewardsByCategoryMap.put("category_icon_url", category_icon_url);
			lRewardsByCategoryMap.put("reward_id", reward_id);
			lRewardsByCategoryMap.put("reward_name", reward_name);
			lRewardsByCategoryMap.put("reward_icon_url", reward_icon_url);
			lRewardsByCategoryMap.put("reward_count", reward_count);

			lRewardsByCategoryList.add(lRewardsByCategoryMap);
		}

		lRewardsByCategory.put("results", lRewardsByCategoryList);

		return lRewardsByCategory;
	}

	@Override
	public List<Integer> getGuardianIdsByStudentId(int student_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<Integer> list = null;
		String StdentListQuery = "select pk.user_id from parent_kids pk\r\n"
				+ "LEFT JOIN users u on u.user_id = pk.user_id \r\n"
				+ "where u.role_type='parent_member' and pk.student_id=:student_id";
		log.info("StdentListQuery" + StdentListQuery);

		Query query = session.createSQLQuery(StdentListQuery);
		query.setParameter("student_id", student_id);

		log.info("StdentListQuery" + StdentListQuery);
		// query.setParameter(1, eventId);
		list = query.list();
		tx.commit();
		session.close();
		return list;
	}

	@Override
	public int totalNooFStudents(int school_id, String studentName, String registrationNo, String deviceUUID,
			String roleType) {
		// TODO Auto-generated method stub
		HashMap<String, Object> lStudentsMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> lStudentsList = new ArrayList<HashMap<String, Object>>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query q = null;
		List<Object[]> rows = null;
		StringBuffer searchCriteria = new StringBuffer("");

		if (null != studentName && !studentName.equals("0")) {
			searchCriteria.append("AND `students`.`name` LIKE '%" + studentName + "%'");
		}
		if (null != registrationNo && !registrationNo.equals("0")) {
			searchCriteria.append(" AND `students`.`registartion_no` = '" + registrationNo + "'");
		}
		if (null != deviceUUID && !deviceUUID.equals("0")) {
			searchCriteria.append(" AND `device_students`.`device_uuid` = '" + deviceUUID + "'");
		}

		if (roleType.equals(Constant.SchoolAdmin)) {
			searchCriteria.append(" AND class_grade.school_id = ?");
		}

		// Query to get total no of students
		String qStudents = "SELECT COUNT(*)  " + "FROM `students`, `class_grade`, `device_students` "
				+ "WHERE `students`.`class_grade_id` = `class_grade`.`class_grade_id` "
				+ "AND `students`.`student_id` = `device_students`.`student_id` " + searchCriteria
				+ " ORDER BY `students`.`name`, `device_students`.`status`";

		q = session.createSQLQuery(qStudents);
		if (roleType.equals(Constant.SchoolAdmin)) {
			q.setParameter(0, school_id);
		}
		BigInteger noOfRecords = (BigInteger) q.uniqueResult();

		tx.commit();
		session.close();
		return noOfRecords.intValue();

	}

}
