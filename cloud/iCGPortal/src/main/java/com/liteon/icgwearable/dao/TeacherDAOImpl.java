package com.liteon.icgwearable.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Rewards;
import com.liteon.icgwearable.hibernate.entity.RewardsStudents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.TeacherCSVModel;
import com.liteon.icgwearable.model.TeacherStaffModel;
import com.liteon.icgwearable.model.TeachersStaffCSVModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.transform.RewardStatisticsTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeacherRewardsAssignedToStudentsTransform;
import com.liteon.icgwearable.transform.TeacherRewardsListTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;
import com.liteon.icgwearable.transform.TeachersTransform;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.EmailUtility;
import com.liteon.icgwearable.util.StringUtility;

@Repository("teacherDAO")
public class TeacherDAOImpl implements TeacherDAO {

	private static Logger log = Logger.getLogger(TeacherDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	private DeviceDAO deviceDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private ClassGradeDAO classGradeDAO;
	@Autowired
	private AccountDAO accountDAO;

	private Devices devices = null;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public boolean createTeachers(TeacherCSVModel tcsvm, int accountId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<Users> usersList = new ArrayList<>();
		Criteria criteriaAccounts = session.createCriteria(Accounts.class);
		criteriaAccounts.add(Restrictions.eq("accountId", accountId));
		Accounts accounts = (Accounts) criteriaAccounts.uniqueResult();
		accounts.setAccountId(accountId);
		accounts.setAccountType("school");
		Users users = null;
		boolean flag = false;

		tx = session.beginTransaction();
		users = new Users();

		users.setAccounts(accounts);
		users.setName(tcsvm.getName());
		users.setUsername(tcsvm.getUsername());
		try {
			users.setPassword(AESEncryption.generatePasswordHash(tcsvm.getPassword()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		users.setRoleType("school_teacher");
		users.setUserActive("y");
		users.setCreatedDate(new java.util.Date());
		session.save(users);
		session.flush();

		/**
		 * Inserting into class_grade table As per the rule there can be one
		 * Class Teacher to each class in a school. Which means School ID &
		 * Class are Composite PK in class_grad table. So, check if Class to
		 * School mapping record already exists in class_grade table. 1. Yes
		 * There is already a school teacher assigned for the class. Error Case,
		 * show appropriate error on UI. Rollback TXN. 2. No INSERT new record
		 * into class_grade table.
		 */

		if (!(classGradeDAO.classToSchoolMappingExist(Integer.parseInt(tcsvm.getStudentClass()), accountId))) {

			// ClassGradePK cgPK = new ClassGradePK(accountId,
			// Integer.parseInt(tcsvm.getStudentClass()));
			ClassGrade cg = new ClassGrade();
			// cg.setClassGradePK(cgPK);
			cg.setTeacher_id(users.getId());
			cg.setGrade(tcsvm.getGrade());
			cg.setCreated_date(new java.util.Date());

			this.classGradeDAO.createClassGrade(cg);
		} else {
			log.info("There is a school teacher already assigned to this class");
			tx.rollback();
		}

		String uuids = tcsvm.getUuid();
		log.info("uuids" + "\t" + uuids);
		String splitBy = ";";

		String[] uuidsArray = uuids.split(splitBy);
		if (uuids.trim().length() > 0) {
			for (int j = 0; j < uuidsArray.length; j++) {
				log.info("uuidsArray.length" + "\t" + uuidsArray.length);
				String uuid = uuidsArray[j];
				log.info("UUID" + "\t" + uuid);

				Devices devices1 = this.deviceDAO.checkDeviceIdExist(uuid);

				if (devices1 == null) {
					log.info("Getting into If loop");
					devices = new Devices();
					users = new Users();

					log.info("user_id" + "\t" + users.getId());
					String username = tcsvm.getUsername();
					int teacher_id = this.userDAO.getUserIdByUsername(username);
					log.info("teacher_id" + "\t" + teacher_id);
					log.info("list.get(i).getUuid()" + "\t" + tcsvm.getUuid());

					users.setId(teacher_id);
					users.setAccounts(accounts);
					users.setRoleType("school_teacher");
					users.setUserActive("y");

					devices.setUuid(uuid);
					// devices.setUsers(users);
					// devices.setMaxParents(1);
					// devices.setDeviceActive(devices.getDeviceActive().y);
					// devices.setPurchaseDate(new java.sql.Timestamp(new
					// java.util.Date().getTime()));
					devices.setCreatedDate(new java.sql.Timestamp(new java.util.Date().getTime()));
					this.deviceDAO.createDevice(devices);
					flag = true;
				} else {
					log.info("Getting into Else");
					String username = tcsvm.getUsername();
					int teacher_id = this.userDAO.getUserIdByUsername(username);
					log.info("teacher_id In Else" + "\t" + teacher_id);
					users = new Users();
					users.setAccounts(accounts);
					users.setId(teacher_id);
					users.setRoleType("school_teacher");
					users.setUserActive("y");
					// devices1.setUsers(users);
					this.deviceDAO.updateDevice(devices1);
					flag = true;
				}

			}
		}
		tx.commit();
		session.close();
		return flag;
	}

	@Override
	public List<Users> listTeachersByPage(int pageNo) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		int page = (pageNo - 1) * 5;
		Criteria criteria = session.createCriteria(Users.class);
		criteria.setFirstResult(page);
		criteria.setMaxResults(5);
		criteria.add(Restrictions.eq("roleType", "school_teacher"));
		List<Users> teachersList = criteria.list();
		tx.commit();
		session.close();
		return teachersList;
	}

	@Override
	public List<TeachersTransform> listAllTeachers(int userId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String teachersQuery = "select teachers.user_id as teacherId,teachers.account_id as teacherAccntId, teachers.role_type as roleType, "
				+ "teachers.name as name, teachers.username as username, teachers.password as password, "
				+ "teachers.create_date as createDate, teachers.updated_date as updateDate " + "from users as teachers "
				+ "left join users as admin on admin.account_id = teachers.account_id and admin.user_active='y' "
				+ "where teachers.role_type ='school_teacher' and admin.user_id = ? ";

		List<TeachersTransform> teachersTransformList = null;

		Query query = session.createSQLQuery(teachersQuery).addScalar("teacherId").addScalar("teacherAccntId")
				.addScalar("roleType").addScalar("name").addScalar("username").addScalar("password")
				.addScalar("createDate").addScalar("updateDate").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TeachersTransform.class));
		query.setParameter(0, userId);
		teachersTransformList = (List<TeachersTransform>) query.list();
		tx.commit();
		session.close();
		return teachersTransformList;
	}

	@Override
	public List<Users> listTeachersById(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("roleType", "school_teacher"));
		criteria.add(Restrictions.eq("id", id));
		List<Users> teachersList = criteria.list();
		tx.commit();
		session.close();
		return teachersList;
	}

	@Override
	public void addTeacher(Users users) {
		// TODO Auto-generated method stub
		log.info("In DAO IMPL");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(users);
		log.info("Name" + "\t" + users.getName());
		tx.commit();
		session.close();
	}

	@Override
	public Users getTeacher(int id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		return (Users) session.get(Users.class, id);
	}

	@Override
	public void addUITeacher(Users users, String uuidIn) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(users);
		tx.commit();

		log.info("uuidIn inside addUITeacher" + "\t" + uuidIn);
		String splitBy = ";";

		String[] uuidsArray = uuidIn.split(splitBy);
		log.info("uuidsArray.length" + "\t" + uuidsArray.length);
		if (uuidIn.trim().length() > 0) {
			for (int j = 0; j < uuidsArray.length; j++) {
				log.info("uuidsArray.length" + "\t" + uuidsArray.length);
				String uuid = uuidsArray[j];
				log.info("UUID" + "\t" + uuid);

				Devices devices1 = this.deviceDAO.checkDeviceIdExist(uuid);

				if (devices1 == null) {
					log.info("Getting into If loop");
					devices = new Devices();
					log.info("user_id" + "\t" + users.getId());

					devices.setUuid(uuid);
					// devices.setUsers(users);
					// devices.setMaxParents(1);
					// devices.setDeviceActive(devices.getDeviceActive().y);
					// devices.setPurchaseDate(new java.sql.Timestamp(new
					// java.util.Date().getTime()));
					devices.setCreatedDate(new java.sql.Timestamp(new java.util.Date().getTime()));
					this.deviceDAO.createDevice(devices);
				} else {
					log.info("Getting into Else");
					// devices1.setUsers(users);
					this.deviceDAO.updateDevice(devices1);
				}
			}
		}
		session.close();
	}

	@Override
	public void updateTeacherStaffApi(Users users, ClassGrade cg, UsersModel usersModel) {
		// TODO Auto-generated method stub
		log.info("Into updateTeacherStaffApi {");
		Session session = null;
		Transaction tx = null;
		ClassGrade existingCg = null, cgOfExistingUser = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			// existingUsers =
			// this.userDAO.findUsers(teacherStaffModel.getAccTeacherStaffEmail());
			if (usersModel.getUser_role().equals("Teacher")) {
				existingCg = this.classGradeDAO.checkIfClassGradeExists(users.getAccounts().getAccountId(),
						usersModel.getGrade(), usersModel.getStudentClass());
			}

			log.info("users.getRoleType()>>" + "\t" + users.getRoleType());
			users.setName(usersModel.getName());
			users.setMobileNumber(usersModel.getMobileNumber());
			if (usersModel.getUser_role().equals("Teacher"))
				users.setRoleType("school_teacher");
			else if (usersModel.getUser_role().equals("Staff"))
				users.setRoleType("school_staff");
			users.setUpdatedDate(new java.util.Date());
			session.update(users);

			cgOfExistingUser = this.classGradeDAO.findSchoolGrade(users.getId());

			log.info("Calling Before If");
			if (null != cgOfExistingUser) {
				log.info("Inside If");
				if (users.getRoleType().equals("school_teacher")) {
					if (existingCg == null) {
						/*
						 * cg = new ClassGrade();
						 * cg.setGrade(teacherStaffModel.getAccMgtTeacherGrade()
						 * ); cg.setStudentClass(teacherStaffModel.
						 * getAccMgtTeacherClass());
						 * cg.setSchoolId(String.valueOf(existingUsers.
						 * getAccounts().getAccountId()));
						 * cg.setTeacher_id(existingUsers.getId());
						 * cg.setCreated_date(new Date()); session.save(cg);
						 */
					} else {
						log.info("Into Else of Existing ClassGrade");
						cgOfExistingUser.setTeacher_id(null);
						this.classGradeDAO.updateClassGrade(cgOfExistingUser);

						existingCg.setTeacher_id(users.getId());
						existingCg.setUpdated_date(new Date());
						session.update(existingCg);
					}
				} else if (users.getRoleType().equals("school_staff")) {
					cgOfExistingUser.setTeacher_id(null);
					this.classGradeDAO.updateClassGrade(cgOfExistingUser);
				}
			} else {
				if (null != existingCg) {
					existingCg.setTeacher_id(users.getId());
					this.classGradeDAO.updateClassGrade(existingCg);
				}
			}
			tx.commit();
		} catch (Exception e) {
			log.info("Exception In updateTeacherStaffApi" + "\t" + e);
			if (null != users)
				session.evict(users);
			if (null != cg)
				session.evict(cg);
			tx.rollback();
		} finally {
			session.close();
		}
		log.info("Exiting updateTeacherStaffApi }");
	}

	@Override
	public List<StudentsTransform> findStudentsClass(int userId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String studentsClassQuery = "select distinct s.class as stClass, a.account_name as schoolName from students as s "
				+ "left join devices d on d.student_id=s.student_id " + "left join users u on u.user_id=d.teacher_id "
				+ "left join accounts a on a.account_id = u.account_id " + "where u.user_id = ? ";
		List<StudentsTransform> studentsClassList = null;

		Query query = session.createSQLQuery(studentsClassQuery).addScalar("stClass").addScalar("schoolName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsTransform.class));
		query.setParameter(0, userId);
		studentsClassList = (List<StudentsTransform>) query.list();
		tx.commit();
		session.close();
		return studentsClassList;
	}

	@Override
	public List<TeachersStudentsTransform> findStudentsByTeacher(int userId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String teachersStudentsQuery = "select s.account_id as accountId, s.school_id as schoolId ,s.name as studentName, s.nickname as nickName, s.class as studentClass, s.roll_no as rollNo, s.dob as dob,s.height as height, s.weight as weight, s.gender as gender, "
				+ "s.create_date, s.updated_date from Students as s "
				+ "left join Devices as d on d.student_id=s.student_id "
				+ "left join Users  as u on u.user_id = d.teacher_id " + "where u.user_id = ?";
		List<TeachersStudentsTransform> teachersStudentsList = null;

		Query query = session.createSQLQuery(teachersStudentsQuery).addScalar("accountId").addScalar("schoolId")
				.addScalar("studentName").addScalar("nickName").addScalar("studentClass").addScalar("rollNo")
				.addScalar("dob").addScalar("height").addScalar("weight").addScalar("gender")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TeachersStudentsTransform.class));
		query.setParameter(0, userId);
		teachersStudentsList = (List<TeachersStudentsTransform>) query.list();
		tx.commit();
		session.close();
		return teachersStudentsList;
	}

	/*
	 * @Override public
	 * List<com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences>
	 * getSchoolAppPreferencesByUserId(int userId) { // TODO Auto-generated
	 * method stub Session session = sessionFactory.openSession(); Transaction
	 * tx =session.beginTransaction(); Criteria criteria =
	 * session.createCriteria(com.liteon.icgwearable.hibernate.entity.
	 * SchoolAppPreferences.class,"sap"); criteria.createAlias("sap.users",
	 * "user"); criteria.add(Restrictions.eq("user.id", userId));
	 * List<com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences>
	 * schoolApppref =
	 * (List<com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences>)
	 * criteria.list(); tx.commit(); session.close(); return schoolApppref; }
	 */

	@Override
	public void deletetSchoolAppPreferencesByUserId(int userId, String quietHoursDay) {

		Session session = sessionFactory.openSession();
		String DeleteQuery_schollApppreferences = "DELETE from SchoolAppPreferences as sap  where sap.users.id=:userId and sap.quietHoursDay=:quietHoursDay";
		Transaction deleteschollApppreferencestxn = session.beginTransaction();
		Query deleteschollApppreferencesQry = session.createQuery(DeleteQuery_schollApppreferences);
		deleteschollApppreferencesQry.setParameter("userId", userId);
		deleteschollApppreferencesQry.setParameter("quietHoursDay", quietHoursDay);
		int deletedAnnouncemnets = deleteschollApppreferencesQry.executeUpdate();
		deleteschollApppreferencestxn.commit();
		session.close();
	}

	@Override
	public void addSchoolAppPreferencesByUserId(Users user, String QuiteHoursday, int QuiteHourFrom, int QuiteHourTo) {
		/*
		 * // com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences spp =
		 * new com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences();
		 * spp.setQuietHoursDay(QuiteHoursday);
		 * spp.setQuietHoursFrom(QuiteHourFrom);
		 * spp.setQuietHoursTo(QuiteHourTo); spp.setUsers(user); Session session
		 * = sessionFactory.openSession(); Transaction tx =
		 * session.beginTransaction(); session.save(spp); tx.commit();
		 * session.close(); }
		 */
	}

	/*
	 * @Override public void
	 * UpdateSchoolAppPreferencesByUserId(com.liteon.icgwearable.hibernate.
	 * entity.SchoolAppPreferences sap) { Session session =
	 * sessionFactory.openSession(); Transaction tx =
	 * session.beginTransaction(); session.update(sap); tx.commit();
	 * session.close(); }
	 */

	@Override
	public List<TeacherRewardsListTransform> getSchoolRewardsToTeacherBySchoolId(int school_id, int category_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String teachersStudentsQuery = "SELECT rds.reward_id as reward_id, rds.reward_icon_url as reward_icon_url, rds.name as name, rds.rewards_category_id category_id, \r\n"
				+ "rc.category_icon_url as category_icon_url, rc.category_name as category_name FROM rewards rds \r\n"
				+ "LEFT JOIN rewards_category rc ON  rc.rewards_category_id = rds.rewards_category_id where  rds.rewards_category_id = ?";

		List<TeacherRewardsListTransform> teacherRewardsListTransform = null;

		Query query = session.createSQLQuery(teachersStudentsQuery).addScalar("reward_id").addScalar("reward_icon_url")
				.addScalar("name").addScalar("category_name").addScalar("category_icon_url")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TeacherRewardsListTransform.class));
		// query.setParameter(0, school_id);
		query.setParameter(0, category_id);
		teacherRewardsListTransform = (List<TeacherRewardsListTransform>) query.list();
		log.debug("returned teacherRewardsListTransform list :::::: " + teacherRewardsListTransform.toString());
		tx.commit();
		session.close();
		return teacherRewardsListTransform;
	}

	@Override
	public String assignRewardsToStudentsByTeacher(Integer rewardId, Integer studentId, Integer count, Users user) {
		log.info("rewardId" + "\t" + rewardId);
		log.info("studentId" + "\t" + studentId);
		StringBuffer errorBuff = new StringBuffer();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Rewards rewards = new Rewards();
		rewards.setRewardId(rewardId);
		String erroMsg = "";
		RewardsStudents rewardsStudents = new RewardsStudents();

		Criteria deviceStudentsCriteria = session.createCriteria(DeviceStudents.class, "deviceStudents");
		deviceStudentsCriteria.createAlias("deviceStudents.students", "students");
		deviceStudentsCriteria.add(Restrictions.eq("students.studentId", studentId));
		deviceStudentsCriteria.add(Restrictions.eq("deviceStudents.status", "active"));
		DeviceStudents deviceStudents = (DeviceStudents) deviceStudentsCriteria.uniqueResult();
		if (deviceStudents != null) {
			rewardsStudents.setRewards(rewards);
			rewardsStudents.setStudents(deviceStudents.getStudents());
			rewardsStudents.setUsers(user);
			rewardsStudents.setCreatedDate(new Date());
			rewardsStudents.setReceivedCount(count);
			rewardsStudents.setUpdatedDate(new Date());

			try {
				log.debug("saving into Rewards Students table ::::::::::");
				session.save(rewardsStudents);
				log.debug("saved into Rewards Students table >>>>>>> ");
				tx.commit();
				session.close();
			} catch (HibernateException hbe) {
				log.debug("HibernateException occurred >>>>>>> ");
				errorBuff.append(rewardId + "");
				erroMsg = errorBuff.toString();
				errorBuff.delete(0, errorBuff.length());
				return erroMsg + " rewardIds";
			}
			return "Success";
		} else {
			errorBuff.append(studentId);
			erroMsg = errorBuff.toString();
			errorBuff.delete(0, errorBuff.length());
			return erroMsg;
			// return errorBuff.toString();
		}
	}

	@Override
	public String assignRewardsToStudentsByTeacher(Integer rewardId, Integer studentId, Integer count, Integer teacherId) {
		log.info("rewardId" + "\t" + rewardId);
		log.info("studentId" + "\t" + studentId);
		StringBuffer errorBuff = new StringBuffer();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Rewards rewards = new Rewards();
		rewards.setRewardId(rewardId);
		String erroMsg = "";
		String countQuery = "SELECT COUNT(ds.device_students_id) FROM device_students AS ds, students AS s "
				+ "WHERE ds.student_id = s.student_id AND ds.status = 'active' AND s.student_id = :student_id";

		Query query = session.createSQLQuery(countQuery);
		query.setParameter("student_id", studentId);

		BigInteger studentCount = (BigInteger) query.uniqueResult();
		if (null != studentCount && studentCount.intValue() == 1) {
			String rewardCreate = "INSERT INTO rewards_students "
					+ "(created_date, received_count, reward_id, student_id, updated_date, teacher_id) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			Query queryReward = session.createSQLQuery(rewardCreate);
			queryReward.setParameter(0, new Date());
			queryReward.setParameter(1, count);
			queryReward.setParameter(2, rewardId);
			queryReward.setParameter(3, studentId);
			queryReward.setParameter(4, new Date());
			queryReward.setParameter(5, teacherId);
			try {
				log.debug("saving into Rewards Students table ::::::::::");
				queryReward.executeUpdate();
				tx.commit();
				session.close();
			} catch (HibernateException hbe) {
				log.debug("HibernateException occurred >>>>>>> ");
				errorBuff.append(rewardId + "");
				erroMsg = errorBuff.toString();
				errorBuff.delete(0, errorBuff.length());
				return erroMsg + " rewardIds";
			}
			return "Success";
		} else {
			errorBuff.append(studentId);
			erroMsg = errorBuff.toString();
			errorBuff.delete(0, errorBuff.length());
			return erroMsg;
			// return errorBuff.toString();
		}
	}

	@Override
	public boolean checkStudentRewardIdUnderToTeacher(Integer students_rewardids, Integer user_id) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean flag = false;
		Integer rId = 0;

		String SQL_SELECT = "select id from rewards_students where id = ? and teacher_id = ?";
		Query query = session.createSQLQuery(SQL_SELECT);
		query.setParameter(0, students_rewardids);
		query.setParameter(1, user_id);
		rId = (Integer) query.uniqueResult();
		if (rId != null) {
			flag = true;
		}

		tx.commit();
		session.close();
		return flag;
	}

	@Override
	public String deleteStudentsRewardsByTeacher(Integer students_rewardids, Integer user_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		try {
			Query query = session.createSQLQuery("delete from rewards_students where id = ? and teacher_id = ?");
			query.setParameter(0, students_rewardids);
			query.setParameter(1, user_id);
			query.executeUpdate();
			tx.commit();
			session.close();
			log.info("record(s) successfully deleted");
			return "Success";

		} catch (HibernateException he) {
			log.debug("no record(s) to delete");
			return "no records(s) to delete";
		}

	}

	@Override
	public int getAccountId(Integer studentId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select cg.school_id from class_grade cg\r\n"
				+ "LEFT JOIN students s on s.class_grade_id= cg.class_grade_id where s.student_id=:student_id";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter("student_id", studentId);
		sessions = (List<Integer>) query.list();
		session.close();
		if (sessions.size() > 0)
			return sessions.get(0);
		else
			return 0;
	}

	@Override
	public boolean checkRewardsId(int rewardId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean flag = false;
		Integer rId = 0;

		String SQL_SELECT = "select reward_id from rewards where reward_id = ?";
		Query query = session.createSQLQuery(SQL_SELECT);
		query.setParameter(0, rewardId);
		rId = (Integer) query.uniqueResult();
		if (rId != null && rId.intValue() == rewardId) {
			flag = true;
		}

		tx.commit();
		session.close();
		return flag;
	}

	@Override
	public boolean checkStudentRewardId(int studentRewardId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean flag = false;
		Integer rId = 0;

		String SQL_SELECT = "select id from rewards_students where id = ?";
		Query query = session.createSQLQuery(SQL_SELECT);
		query.setParameter(0, studentRewardId);
		rId = (Integer) query.uniqueResult();
		if (rId != null && rId.intValue() == studentRewardId) {
			flag = true;
		}

		tx.commit();
		session.close();
		return flag;
	}

	@Override
	public boolean checkRewardsAlreadyAssingnedAndUpdate(Integer rewardId, Integer studentId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		// boolean rewardIds = false;
		// boolean studentIds = false;
		boolean flag = false;

		/*
		 * Criteria criteria = session.createCriteria(RewardsStudents.class,
		 * "rewardsStudents"); criteria.createAlias("rewardsStudents.students",
		 * "students"); criteria.add(Restrictions.eq("students.studentId",
		 * studentId));
		 * 
		 * criteria.createAlias("rewardsStudents.rewards", "rewards");
		 * criteria.add(Restrictions.eq("rewards.rewardId", rewardId));
		 * 
		 * RewardsStudents rewardsStudents = (RewardsStudents)
		 * criteria.uniqueResult();
		 * 
		 * if(rewardsStudents != null){
		 * if(rewardsStudents.getRewards().getRewardId().intValue() ==
		 * rewardId.intValue() &&
		 * rewardsStudents.getStudents().getStudentId().intValue() ==
		 * studentId.intValue()){
		 * 
		 * rewardIds = true; } }
		 */

		String SQL_SELECT = "select student_id as studentId from rewards_students where  reward_id = ? and student_id = ? ";
		Query query = session.createSQLQuery(SQL_SELECT).addScalar("studentId")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));
		query.setParameter(0, rewardId.intValue());
		query.setParameter(1, studentId.intValue());
		StudentsListTransform studentsListTransform2 = (StudentsListTransform) query.uniqueResult();
		if (studentsListTransform2 != null && studentsListTransform2.getStudentId() > 0) {
			flag = true;
		}

		tx.commit();
		session.close();
		return flag;
	}

	@Override
	public List<RewardStatisticsTransform> getRewardsStatisticsForTeacher(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String statisticsForSchoolAdmin = "SELECT rc.category_name, rc.created_date, r.name AS rewards_name, s.name AS student_name, s.class As studentClass, a.account_name, u.name AS user_name, r.reward_icon_url AS icon FROM rewards_category rc "
				+ "LEFT JOIN rewards r ON r.rewards_category_id = rc.rewards_category_id "
				+ "LEFT JOIN rewards_students rs ON rs.reward_id = r.reward_id "
				+ "LEFT JOIN students s ON s.student_id = rs.student_id "
				+ "LEFT JOIN accounts a ON a.account_id = s.school_id "
				+ "LEFT JOIN users u ON u.account_id = a.account_id "
				+ "WHERE u.account_id=? and u.role_type = 'school_teacher' ";
		List<RewardStatisticsTransform> rewardsData = null;
		Query query = session.createSQLQuery(statisticsForSchoolAdmin).addScalar("category_name")
				.addScalar("created_date").addScalar("rewards_name").addScalar("student_name").addScalar("studentClass")
				.addScalar("account_name").addScalar("user_name").addScalar("icon")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		rewardsData = query.list();
		tx.commit();
		session.close();
		return rewardsData;
	}

	@Override
	public List<RewardStatisticsTransform> getStudentNameForTeacher(int account_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String classQuery = "SELECT distinct t.name AS student_name " + "FROM students t "
				+ "LEFT JOIN accounts a ON a.account_id = t.school_id " + "WHERE a.account_id = ?";
		List<RewardStatisticsTransform> studentsList = null;
		Query query = session.createSQLQuery(classQuery).addScalar("student_name")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		studentsList = query.list();
		tx.commit();
		session.close();
		return studentsList;
	}

	@Override
	public List<RewardStatisticsTransform> getClassNameForTeacher(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String classQuery = "SELECT distinct t.class AS studentClass " + "FROM students t "
				+ "LEFT JOIN accounts a ON a.account_id = t.school_id " + "WHERE a.account_id = ?";
		List<RewardStatisticsTransform> classList = null;
		Query query = session.createSQLQuery(classQuery).addScalar("studentClass")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		classList = query.list();
		tx.commit();
		session.close();
		return classList;
	}

	@Override
	public List<RewardStatisticsTransform> getStudentName(int account_id, String day, String studentClass,
			String studentName) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String findUniqueQuery = "SELECT rc.category_name, rc.created_date, r.name AS rewards_name, s.name AS student_name, s.class As studentClass, a.account_name, u.name AS user_name, rc.category_icon_url AS icon "
				+ "FROM rewards_category rc " + "LEFT JOIN rewards r ON r.rewards_category_id = rc.rewards_category_id "
				+ "LEFT JOIN rewards_students rs ON rs.reward_id = r.reward_id "
				+ "LEFT JOIN students s ON s.student_id = rs.student_id "
				+ "LEFT JOIN accounts a ON a.account_id = s.school_id "
				+ "LEFT JOIN users u ON u.account_id = a.account_id " + "WHERE u.account_id=? ";
		if (!"NONE".equalsIgnoreCase(studentClass)) {
			findUniqueQuery = findUniqueQuery + " AND s.class = ?";
		}
		if (!"NONE".equalsIgnoreCase(studentName)) {
			findUniqueQuery = findUniqueQuery + " AND s.name = ?";
		}
		if (!"NONE".equalsIgnoreCase(day)) {
			findUniqueQuery = findUniqueQuery + " AND rc.created_date >= SUBDATE(CURDATE(), ?)";
		}
		log.info("DATE############################################## " + findUniqueQuery);
		List<RewardStatisticsTransform> uniqueData = null;
		Query query = session.createSQLQuery(findUniqueQuery).addScalar("category_name").addScalar("rewards_name")
				.addScalar("student_name").addScalar("studentClass").addScalar("created_date").addScalar("account_name")
				.addScalar("user_name").addScalar("icon").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		// boolean flag = false;
		int i = 1;
		if (!"NONE".equalsIgnoreCase(studentClass)) {
			query.setParameter(i++, studentClass);
			// flag = true;
		}
		if (!"NONE".equalsIgnoreCase(studentName)) {
			query.setParameter(i++, studentName);
			// flag = true;
		}
		if (!"NONE".equalsIgnoreCase(day)) {
			query.setParameter(i++, Integer.parseInt(day));

		}
		uniqueData = query.list();
		log.info("UniqueResultForTeacher::::::::::::::::" + uniqueData.toString());
		tx.commit();
		session.close();
		return uniqueData;
	}

	@Override
	public List<TeacherRewardsAssignedToStudentsTransform> StudentRewardsByTeacher(int teacher_id, int school_id) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String teachersStudentsQuery = "SELECT rs.id as student_reward_id, rs.updated_date as reward_date, rs.received_count as received_count, "
				+ " rds.reward_icon_url as reward_icon_url, rds.reward_id as reward_id, rds.name as name, "
				+ " rc.category_icon_url as category_icon_url,  rc.category_name as category_name, s.student_id, s.name AS student_name"
				+ " FROM rewards_students rs"
				+ " JOIN rewards rds ON rs.reward_id = rds.reward_id AND rs.teacher_id = ? "
				+ " JOIN rewards_category rc ON rc.rewards_category_id = rds.rewards_category_id "
				+ " JOIN students s ON s.student_id = rs.student_id " + " ORDER BY rs.id";

		List<TeacherRewardsAssignedToStudentsTransform> teacherRewardsListTransform = null;

		Query query = session.createSQLQuery(teachersStudentsQuery).addScalar("student_reward_id")
				.addScalar("reward_date").addScalar("received_count").addScalar("reward_icon_url")
				.addScalar("reward_id").addScalar("name").addScalar("category_icon_url").addScalar("category_name")
				.addScalar("student_id").addScalar("student_name").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TeacherRewardsAssignedToStudentsTransform.class));

		query.setParameter(0, teacher_id);
		teacherRewardsListTransform = (List<TeacherRewardsAssignedToStudentsTransform>) query.list();
		log.debug("returned teacherRewardsListTransform list :::::: " + teacherRewardsListTransform.toString());
		tx.commit();
		session.close();
		return teacherRewardsListTransform;
	}

	@Override
	public String reAssignRewardsByTeacher(Integer rewardId, Integer studentId, String comment, Integer count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> getRewardsByAccountid(int account_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String rewardsListQuery = "select reward_id from rewards, rewards_category where rewards.rewards_category_id = rewards_category.rewards_category_id"
				+ " and rewards_category.school_id = ? ";
		List<Integer> rewardsList = null;

		Query query = session.createSQLQuery(rewardsListQuery);
		query.setParameter(0, account_id);
		rewardsList = (List<Integer>) query.list();
		tx.commit();
		session.close();
		return rewardsList;
	}

	@Override
	public boolean createTeachers(TeachersStaffCSVModel teacherStaffModel) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<Users> usersList = new ArrayList<>();

		Accounts accounts = this.accountDAO.createAccountsForSchoolTeacherAndStaff();
		Users users = null;
		boolean flag = false;

		tx = session.beginTransaction();
		users = new Users();

		users.setAccounts(accounts);
		users.setName(teacherStaffModel.getName());
		users.setUsername(teacherStaffModel.getUsername());
		users.setMobileNumber(teacherStaffModel.getContactNo());
		users.setRoleType("school_teacher");
		users.setUserActive("n");
		users.setCreatedDate(new java.util.Date());

		/**
		 * Write an api to send email to the user created here.
		 */

		session.save(users);
		session.flush();
		return false;
	}

	@Override
	public boolean createStaff(TeachersStaffCSVModel teacherStaffModel) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<Users> usersList = new ArrayList<>();

		Accounts accounts = this.accountDAO.createAccountsForSchoolTeacherAndStaff();
		Users users = null;
		boolean flag = false;

		tx = session.beginTransaction();
		users = new Users();

		users.setAccounts(accounts);
		users.setName(teacherStaffModel.getName());
		users.setUsername(teacherStaffModel.getUsername());
		users.setMobileNumber(teacherStaffModel.getContactNo());
		users.setRoleType("school_staff");
		users.setUserActive("n");
		users.setCreatedDate(new java.util.Date());

		/**
		 * Write an api to send email to the user created here.
		 */

		session.save(users);
		session.flush();
		return false;
	}

	@Override
	public Map<String, Object> createTeachersOrStaff(List<TeachersStaffCSVModel> teachersInsertList, Users loginUser) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		int batchSize = 50;

		Users users = null;
		Integer teacherOrStaffCreatedCount = 0;
		Integer teacherOrStaffFailedCount = 0;

		Map<Integer, Object> failedTeachersMap = new HashMap<>();
		Map<String, Object> finalMap = new HashMap<String, Object>();
		try {
			session = sessionFactory.openSession();
			for (int i = 0; i < teachersInsertList.size(); i++) {
				log.info("Inside createTeachers() teachersInsertList.size()" + "\t" + teachersInsertList.size());
				log.info("Enter");

				tx = session.beginTransaction();

				users = new Users();
				users.setAccounts(loginUser.getAccounts());
				users.setName(teachersInsertList.get(i).getName());
				users.setUsername(teachersInsertList.get(i).getUsername().toLowerCase());
				users.setMobileNumber(teachersInsertList.get(i).getContactNo());
				if (teachersInsertList.get(i).getRole().equals("teacher"))
					users.setRoleType("school_teacher");
				else if (teachersInsertList.get(i).getRole().equals("staff"))
					users.setRoleType("school_staff");

				users.setUserActive("n");
				users.setActivationCode(StringUtility.randomStringOfLength(40));

				if (users.getRoleType().equals("school_teacher")) {
					ClassGrade existingCg = this.classGradeDAO.checkIfClassGradeExists(
							loginUser.getAccounts().getAccountId(), teachersInsertList.get(i).getGrade(),
							teachersInsertList.get(i).getStClass());
					log.info("existingCg ***" + "\t" + existingCg);

					if (existingCg == null) {
						log.info("Class Grade Creating Newely");
						session.save(users);
						ClassGrade cg = new ClassGrade();
						cg.setGrade(teachersInsertList.get(i).getGrade());
						cg.setStudentClass(teachersInsertList.get(i).getStClass());
						cg.setSchoolId(users.getAccounts().getAccountId());
						cg.setTeacher_id(users.getId());
						session.save(cg);
						teacherOrStaffCreatedCount++;
					} else if (null != existingCg && existingCg.getTeacher_id() == null) {
						log.info("Existing ClassGrade And Teacher Id is Null");
						session.save(users);
						existingCg.setTeacher_id(users.getId());
						session.update(existingCg);
						teacherOrStaffCreatedCount++;
					} else if (null != existingCg && existingCg.getTeacher_id() != null) {
						log.info("Ignore ClassGrade As Grade And Class is Already Assigned To Teacher");
						teacherOrStaffFailedCount++;
						failedTeachersMap.put(i,
								"Ignore Class and Grade As Grade And Class is Already Assigned To Teacher");
					}
				} else {
					session.save(users);
					teacherOrStaffCreatedCount++;
				}
				tx.commit();
				String status = this.userDAO.sendUserActivationEmail(users.getName(), users.getActivationCode(),
						users.getUsername());
				log.info("status in TeacherDaoImpl" + "\t" + status);
				if (batchSize % 50 == 0) {
					session.flush();
					session.clear();
				}
				log.info("Exit");
			}
		} catch (Exception e) {
			log.info("Exception Occured in createTeachersOrStaff()" + "\t" + e);
			teacherOrStaffFailedCount++;
			tx.rollback();
		} finally {
			session.close();
		}
		finalMap.put("teacherOrStaffCreatedCount", teacherOrStaffCreatedCount);
		finalMap.put("teacherOrStaffFailedCount", teacherOrStaffFailedCount);
		finalMap.put("failedTeachersMap", failedTeachersMap);

		return finalMap;
	}

	@Override
	public Map<String, Object> createOrUpdateTeachersStaff(List<TeachersStaffCSVModel> teachersStaffList,
			Users loginUser) {
		Session session = null;
		Transaction txForTeacherCreation, txForTeacherUpdation, txForStaffCreation, txForStaffUpdation,
				txForTeacherUpdation1, txForTeacherUpdation2, txForTeacherUpdation3, txForTeacherUpdation4 = null;

		Users users = null;
		Integer teacherCreatedCount = 0;
		Integer teacherUpdatedCount = 0;
		Integer teacherFailedCount = 0;
		Integer staffCreatedCount = 0;
		Integer staffUpdatedCount = 0;
		Integer staffFailedCount = 0;
		Integer teacherStaffCreateOrUploadFail = 0;

		Map<Integer, Object> failedTeachersMap = new HashMap<>();
		Map<String, Object> finalMap = new HashMap<String, Object>();
		try {
			session = sessionFactory.openSession();
			for (int i = 0; i < teachersStaffList.size(); i++) {
				log.info("Inside createOrUpdateTeachersStaff() teachersInsertList.size()" + "\t"
						+ teachersStaffList.size());
				log.info("Enter");
				log.info("Value of i" + i);
				log.info("teachersStaffList.get(i).getRole()" + "-" + i + teachersStaffList.get(i).getRole());
				log.info("teachersStaffList.get(i).getUsername()" + "\t" + teachersStaffList.get(i).getUsername());

				boolean isEmailValid = EmailUtility.verifyEmail(teachersStaffList.get(i).getUsername().toLowerCase());

				if (isEmailValid) {
					if (teachersStaffList.get(i).getRole().equals(" ") || teachersStaffList.get(i).getRole() == null
							|| teachersStaffList.get(i).getRole().trim().length() == 0
							|| teachersStaffList.get(i).getRole().isEmpty()) {
						log.info("Checking if role column exits or not");
						staffFailedCount++;
						failedTeachersMap.put(i, "Row without input the role type");
					} else if (!(teachersStaffList.get(i).getRole().equals("teacher")
							|| teachersStaffList.get(i).getRole().equals("staff"))) {
						log.info("Checking if role column is staff or teacher");
						staffFailedCount++;
						failedTeachersMap.put(i, "Row with unknown role type");
					}

					if (teachersStaffList.get(i).getRole().equals("teacher")) {
						if ((teachersStaffList.get(i).getGrade() != null
								&& (teachersStaffList.get(i).getGrade().trim().length() > 0
										&& teachersStaffList.get(i).getGrade().trim().length() <= 3)
								&& !teachersStaffList.get(i).getGrade().isEmpty())
								&& (teachersStaffList.get(i).getStClass() != null
										&& (teachersStaffList.get(i).getStClass().trim().length() > 0
												&& teachersStaffList.get(i).getStClass().trim().length() <= 3)
										&& !teachersStaffList.get(i).getStClass().isEmpty())
								&& (teachersStaffList.get(i).getName() != null
										&& teachersStaffList.get(i).getName().trim().length() > 0
										&& !teachersStaffList.get(i).getName().isEmpty())
								&& (teachersStaffList.get(i).getUsername().toLowerCase() != null
										&& teachersStaffList.get(i).getUsername().trim().toLowerCase().length() > 0)
								&& (teachersStaffList.get(i).getContactNo() != null
										&& (teachersStaffList.get(i).getContactNo().trim().length() > 0
												&& teachersStaffList.get(i).getContactNo().trim().length() <= 10))
								&& (teachersStaffList.get(i).getRole() != null
										&& teachersStaffList.get(i).getRole().trim().length() > 0
										&& !teachersStaffList.get(i).getRole().isEmpty())) {

							Users existingTeachers = this.userDAO
									.findUsers(teachersStaffList.get(i).getUsername().toLowerCase());

							if (null != existingTeachers
									&& existingTeachers.getRoleType().equals(Constant.SchoolTeacher)) {
								if (existingTeachers.getAccounts().getAccountId() == loginUser.getAccounts()
										.getAccountId()) {
									log.info("***School Teacher and Same AccountId ***Updating***");

									ClassGrade existingCg = this.classGradeDAO.checkIfClassGradeExists(
											loginUser.getAccounts().getAccountId(), teachersStaffList.get(i).getGrade(),
											teachersStaffList.get(i).getStClass());
									if (null != existingCg && null != existingCg.getTeacher_id()) {
										log.info("Chekcing if Both are in same teahcer id");
										if (existingTeachers.getId() == existingCg.getTeacher_id()) {
											txForTeacherUpdation = session.beginTransaction();
											// Update Teachers Account
											session.merge(existingTeachers);
											txForTeacherUpdation.commit();
											teacherUpdatedCount++;
										}
									} else if (null != existingCg && existingTeachers.getId() > 0) {
										log.info("Checking if class grade exists and teacher id is null");
										try {
											log.info("Into Try of NPE");
											if (existingCg.getTeacher_id() == null) {
												ClassGrade cg = this.classGradeDAO
														.findSchoolGrade(existingTeachers.getId());

												if (null != cg) {
													txForTeacherUpdation1 = session.beginTransaction();
													log.info("cg.getStudentClass() for teacher" + "\t"
															+ cg.getStudentClass());
													cg.setTeacher_id(null);
													session.merge(cg);
													txForTeacherUpdation1.commit();

													txForTeacherUpdation2 = session.beginTransaction();
													existingCg.setTeacher_id(existingTeachers.getId());
													session.merge(existingCg);
													log.info("existingCg.getStudentClass() for teacher-1" + "\t"
															+ existingCg.getStudentClass());
													txForTeacherUpdation2.commit();
													teacherUpdatedCount++;
												}
											}
										} catch (Exception e) {
											log.info("Into NPE");
											ClassGrade cg = this.classGradeDAO
													.findSchoolGrade(existingTeachers.getId());

											if (null != cg) {
												txForTeacherUpdation1 = session.beginTransaction();
												log.info("cg.getStudentClass() for teacher" + "\t"
														+ cg.getStudentClass());
												cg.setTeacher_id(null);
												session.merge(cg);
												txForTeacherUpdation1.commit();

												txForTeacherUpdation2 = session.beginTransaction();
												existingCg.setTeacher_id(existingTeachers.getId());
												session.merge(existingCg);
												log.info("existingCg.getStudentClass() for teacher-1" + "\t"
														+ existingCg.getStudentClass());
												txForTeacherUpdation2.commit();
												teacherUpdatedCount++;
											}
										}

									} else if (null != existingCg
											&& existingTeachers.getId() != existingCg.getTeacher_id()) {
										// Error, ClassGrade Mismatches.
										log.info("***ClassGrade Mismatches ***");
										teacherFailedCount++;
										failedTeachersMap.put(i, "ClassGrade Mismatches");
									} else if (null == existingCg) {
										log.info("Class Grade is New and does not exist in db");

										ClassGrade cg = this.classGradeDAO.findSchoolGrade(existingTeachers.getId());

										if (null != cg) {
											txForTeacherUpdation3 = session.beginTransaction();
											log.info("cg.getGrade() for teacher" + "\t" + cg.getGrade());
											log.info("cg.getTeacher_id() for teacher" + "\t" + cg.getTeacher_id());
											log.info("cg.getStudentClass() for teacher" + "\t" + cg.getStudentClass());
											cg.setTeacher_id(null);
											session.merge(cg);
											log.info("cg.getTeacher_id() for teacher--1" + "\t" + cg.getTeacher_id());
											log.info("cg.getGrade() for teacher-1" + "\t" + cg.getGrade());
											log.info(
													"cg.getStudentClass() for teacher-1" + "\t" + cg.getStudentClass());
											txForTeacherUpdation3.commit();

											txForTeacherUpdation4 = session.beginTransaction();
											ClassGrade newCg = new ClassGrade();
											newCg.setGrade(teachersStaffList.get(i).getGrade());
											newCg.setStudentClass(teachersStaffList.get(i).getStClass());
											newCg.setSchoolId(loginUser.getAccounts().getAccountId());
											newCg.setTeacher_id(existingTeachers.getId());
											session.save(newCg);
											log.info("Before Commit");
											txForTeacherUpdation4.commit();
											log.info("Aftr Commit");
										} else {
											log.info("***Creating Class Grade Now***");
											ClassGrade newCg = new ClassGrade();
											newCg.setGrade(teachersStaffList.get(i).getGrade());
											newCg.setStudentClass(teachersStaffList.get(i).getStClass());
											newCg.setSchoolId(loginUser.getAccounts().getAccountId());
											newCg.setTeacher_id(existingTeachers.getId());
											session.save(newCg);
										}
										teacherUpdatedCount++;
										log.info("teacherUpdatedCount--" + teacherUpdatedCount);
									}
									log.info("After If Loop");
								} else {
									// Error, User exists under diff account
									teacherFailedCount++;
									failedTeachersMap.put(i, "User already exists");
								}
							} else if (null != existingTeachers
									&& existingTeachers.getRoleType().equals(Constant.SchoolStaff)) {
								log.info("***Teacher Exists but alas he is a Staff***Updating***");

								ClassGrade existingCg = this.classGradeDAO.checkIfClassGradeExists(
										loginUser.getAccounts().getAccountId(), teachersStaffList.get(i).getGrade(),
										teachersStaffList.get(i).getStClass());

								if (null != existingCg && existingCg.getTeacher_id() != null
										&& existingCg.getTeacher_id() > 0) {
									// Error
									txForTeacherUpdation = session.beginTransaction();
									teacherFailedCount++;
									failedTeachersMap.put(i,
											"Teacher is already assigned to the class, grade combination");
									txForTeacherUpdation.commit();
								} else if (null != existingCg && existingCg.getTeacher_id() == null
										&& existingTeachers.getId() > 0) {
									txForTeacherUpdation = session.beginTransaction();
									log.info("Staff is getting converted into Teacher now");
									existingCg.setTeacher_id(existingTeachers.getId());
									existingTeachers.setRoleType(Constant.SchoolTeacher);
									session.merge(existingTeachers);
									session.merge(existingCg);
									teacherUpdatedCount++;
									txForTeacherUpdation.commit();
								} else if (null == existingCg) {
									log.info("Class Grade Not Already Existing");
									txForTeacherUpdation = session.beginTransaction();
									ClassGrade cg = new ClassGrade();
									cg.setGrade(teachersStaffList.get(i).getGrade());
									cg.setStudentClass(teachersStaffList.get(i).getStClass());
									cg.setSchoolId(loginUser.getAccounts().getAccountId());
									cg.setTeacher_id(existingTeachers.getId());

									existingTeachers.setRoleType(Constant.SchoolTeacher);
									session.merge(existingTeachers);
									session.merge(cg);
									txForTeacherUpdation.commit();
									teacherUpdatedCount++;
								}
							} else if (null != existingTeachers
									&& ((!existingTeachers.getRoleType().equals(Constant.SchoolStaff)
									&& !existingTeachers.getRoleType().equals(Constant.SchoolTeacher))
											|| (existingTeachers.getAccounts().getAccountId() != loginUser.getAccounts()
													.getAccountId()))) {
								log.info("User already exists");
								teacherFailedCount++;
								failedTeachersMap.put(i, "User already exists");
							} else if (null == existingTeachers) {
								// Create Teachers
								txForTeacherCreation = session.beginTransaction();
								Users users1 = new Users();
								users1.setAccounts(loginUser.getAccounts());
								users1.setName(teachersStaffList.get(i).getName());
								users1.setUsername(teachersStaffList.get(i).getUsername().toLowerCase());
								users1.setMobileNumber(teachersStaffList.get(i).getContactNo());
								users1.setRoleType(Constant.SchoolTeacher);
								users1.setUserActive("n");
								users1.setActivationCode(StringUtility.randomStringOfLength(40));

								// Check to see if class grade exists for a
								// school id, grade and class provided in csv.
								ClassGrade existingCg = this.classGradeDAO.checkIfClassGradeExists(
										loginUser.getAccounts().getAccountId(), teachersStaffList.get(i).getGrade(),
										teachersStaffList.get(i).getStClass());
								log.info("existingCg ***" + "\t" + existingCg);

								if (existingCg == null) {
									log.info("Class Grade Creating Newely");
									session.save(users1);
									ClassGrade cg = new ClassGrade();
									cg.setGrade(teachersStaffList.get(i).getGrade());
									cg.setStudentClass(teachersStaffList.get(i).getStClass());
									cg.setSchoolId(users1.getAccounts().getAccountId());
									cg.setTeacher_id(users1.getId());
									session.save(cg);
									teacherCreatedCount++;
								} else if (null != existingCg && existingCg.getTeacher_id() == null) {
									log.info("Existing ClassGrade And Teacher Id is Null 1");
									session.save(users1);
									log.info("Existing ClassGrade And Teacher Id is Null 2");
									existingCg.setTeacher_id(users1.getId());
									log.info("Existing ClassGrade And Teacher Id is Null 3");
									session.merge(existingCg);
									log.info("Existing ClassGrade And Teacher Id is Null 4");
									teacherCreatedCount++;
								} else if (null != existingCg && existingCg.getTeacher_id() != null) {
									log.info("Ignore ClassGrade As Grade And Class is Already Assigned To Teacher");
									teacherFailedCount++;
									failedTeachersMap.put(i,
											"Ignore Class and Grade As Grade And Class is Already Assigned To Teacher");
									users1 = null;
								}
								txForTeacherCreation.commit();

								try {
									String status = null;
									if (null != users1)
										status = this.userDAO.sendUserActivationEmail(users1.getName(),
												users1.getActivationCode(), users1.getUsername());
									log.info("Email Sent successfully or not" + "\t" + status);
								} catch (Exception e) {
									log.info("Exception Occured while sending Email");
									failedTeachersMap.put(i, "Activation email not sent");
								}
							}

						} else {
							teacherFailedCount++;
							failedTeachersMap.put(i, "One of the criteria for csv column fails for teacher");
						}
					} else if (teachersStaffList.get(i).getRole().equals("staff")) {

						if ((teachersStaffList.get(i).getName() != null
								&& teachersStaffList.get(i).getName().trim().length() > 0)
								&& (teachersStaffList.get(i).getUsername().toLowerCase() != null
										&& teachersStaffList.get(i).getUsername().trim().toLowerCase().length() > 0)
								&& (teachersStaffList.get(i).getContactNo() != null
										&& teachersStaffList.get(i).getContactNo().trim().length() > 0)
								&& (teachersStaffList.get(i).getRole() != null
										&& teachersStaffList.get(i).getRole().trim().length() > 0)) {

							Users existingStaff = this.userDAO
									.findUsers(teachersStaffList.get(i).getUsername().toLowerCase());

							if (null != existingStaff && existingStaff.getRoleType().equals(Constant.SchoolStaff)) {
								if (existingStaff.getAccounts().getAccountId() == loginUser.getAccounts()
										.getAccountId()) {
									log.info("*** Updating Staff with same account Id ****");
									txForStaffUpdation = session.beginTransaction();
									// Update Staff Information
									session.merge(existingStaff);
									// session.update(existingStaff);
									staffUpdatedCount++;
									txForStaffUpdation.commit();
									session.flush();
									session.clear();
								}else{
									log.info("User already exists");
									staffFailedCount++;
									failedTeachersMap.put(i, "User already exists");
								}
							} else if (null != existingStaff
									&& existingStaff.getRoleType().equals(Constant.SchoolTeacher)) {
								log.info("***Updating Staff with same account Id but he/she is a teacher***");
								txForStaffUpdation = session.beginTransaction();
								ClassGrade cg = this.classGradeDAO.findSchoolGrade(existingStaff.getId());
								log.info("cg.getTeacher_id()" + cg.getTeacher_id());
								log.info("cg.getStudentClass()" + cg.getStudentClass());
								log.info("cg.getGrade()" + cg.getGrade());

								if (null != cg)
									cg.setTeacher_id(null);
								session.merge(cg);
								existingStaff.setRoleType(Constant.SchoolStaff);
								session.merge(existingStaff);
								// What is the count here?
								staffUpdatedCount++;
								txForStaffUpdation.commit();
							} else if (null != existingStaff
									&& ((!existingStaff.getRoleType().equals(Constant.SchoolStaff)
									&& !existingStaff.getRoleType().equals(Constant.SchoolTeacher))
											|| (existingStaff.getAccounts().getAccountId() != loginUser.getAccounts()
													.getAccountId()))) {
								// What error needs to be displayed
								log.info("User already exists");
								staffFailedCount++;
								failedTeachersMap.put(i, "User already exists");
							} else 	if (existingStaff == null) {
								// Create a new staff
								txForStaffCreation = session.beginTransaction();
								users = new Users();
								users.setAccounts(loginUser.getAccounts());
								users.setName(teachersStaffList.get(i).getName());
								users.setUsername(teachersStaffList.get(i).getUsername().toLowerCase());
								users.setMobileNumber(teachersStaffList.get(i).getContactNo());
								users.setRoleType(Constant.SchoolStaff);
								users.setUserActive("n");
								users.setActivationCode(StringUtility.randomStringOfLength(40));
								session.save(users);

								txForStaffCreation.commit();
								staffCreatedCount++;
								try {
									String status = null;
									if (null != users)
										status = this.userDAO.sendUserActivationEmail(users.getName(),
												users.getActivationCode(), users.getUsername());
									log.info("Email Sent successfully or not" + "\t" + status);
								} catch (Exception e) {
									log.info("Exception Occured while sending Email");
									failedTeachersMap.put(i, "Activation email not sent");
								}
							}
						} else {
							staffFailedCount++;
							failedTeachersMap.put(i, "One of the criteria for csv column fails for staff");
						}
					}
				} else {
					log.info("Email Id is not valid");
					teacherStaffCreateOrUploadFail++;
					failedTeachersMap.put(i, "Username not correct");
				}
			}
		} catch (Exception e) {
			log.info("Exception Occured in createTeachersOrStaff()" + "\t" + e);
			List<Object> list = new ArrayList<>();
			list.add(e);
			log.info("Exception encountered" + "\t" + list.get(0));
		} finally {
			session.close();
		}
		Integer teacherStaffCreated = teacherCreatedCount + staffCreatedCount;
		Integer teacherStaffUpdated = teacherUpdatedCount + staffUpdatedCount;
		Integer teacherStaffFailed = teacherFailedCount + staffFailedCount + teacherStaffCreateOrUploadFail;

		finalMap.put("teacherStaffCreated", teacherStaffCreated);
		finalMap.put("teacherStaffUpdated", teacherStaffUpdated);
		finalMap.put("teacherStaffFailed", teacherStaffFailed);
		finalMap.put("teacherStaffFailedMap", failedTeachersMap);
		return finalMap;
	}

	@Override
	public String createTeachersOrStaffForWeb(TeacherStaffModel teacherStaffModel, Users loginUser) {
		// TODO Auto-generated method stub
		log.info("Into createTeachersOrStaffForWeb() {");
		Session session = null;
		Transaction tx = null;
		String createOrUpdate = null;
		Users users = null, existingUsers = null;
		ClassGrade cg = null, existingCg = null;
		try {
			log.info("Into Try");
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			existingUsers = this.userDAO.findUsers(teacherStaffModel.getAccTeacherStaffEmail());
			existingCg = this.classGradeDAO.checkIfClassGradeExists(loginUser.getAccounts().getAccountId(),
					teacherStaffModel.getAccMgtTeacherGrade(), teacherStaffModel.getAccMgtTeacherClass());
			if (existingUsers == null) {
				users = new Users();
				users.setAccounts(loginUser.getAccounts());
				users.setName(teacherStaffModel.getAccTeacherStaffName());
				users.setUsername(teacherStaffModel.getAccTeacherStaffEmail());
				users.setMobileNumber(teacherStaffModel.getAccTeacherStaffContact());
				users.setRoleType(teacherStaffModel.getAcntTeacherStaffRole());
				users.setUserActive("n");
				users.setCreatedDate(new java.util.Date());
				users.setActivationCode(StringUtility.randomStringOfLength(40));
				session.save(users);

				if (users.getRoleType().equals("school_teacher")) {
					if (existingCg == null) {
						cg = new ClassGrade();
						cg.setGrade(teacherStaffModel.getAccMgtTeacherGrade());
						cg.setStudentClass(teacherStaffModel.getAccMgtTeacherClass());
						cg.setSchoolId(users.getAccounts().getAccountId());
						cg.setTeacher_id(users.getId());
						cg.setCreated_date(new Date());
						session.save(cg);
					} else {
						log.info("Existing ClassGrade");
						existingCg.setTeacher_id(users.getId());
						existingCg.setUpdated_date(new Date());
						session.update(existingCg);
					}
				}
				tx.commit();
				createOrUpdate = "Create";
				String status = this.userDAO.sendUserActivationEmail(users.getName(), users.getActivationCode(),
						users.getUsername());
				log.info("status in TeacherDaoImpl" + "\t" + status);

			} else {
				existingUsers.setName(teacherStaffModel.getAccTeacherStaffName());
				existingUsers.setUsername(teacherStaffModel.getAccTeacherStaffEmail());
				existingUsers.setMobileNumber(teacherStaffModel.getAccTeacherStaffContact());
				existingUsers.setRoleType(teacherStaffModel.getAcntTeacherStaffRole());
				existingUsers.setUpdatedDate(new java.util.Date());
				session.update(existingUsers);

				ClassGrade cgOfExistingUser = this.classGradeDAO.findSchoolGrade(existingUsers.getId());
				if (null != cgOfExistingUser) {
					if (existingUsers.getRoleType().equals("school_teacher")) {
						if (existingCg == null) {
							cg = new ClassGrade();
							cg.setGrade(teacherStaffModel.getAccMgtTeacherGrade());
							cg.setStudentClass(teacherStaffModel.getAccMgtTeacherClass());
							cg.setSchoolId(existingUsers.getAccounts().getAccountId());
							cg.setTeacher_id(existingUsers.getId());
							cg.setCreated_date(new Date());
							session.save(cg);
						} else {
							log.info("Into Else of Existing ClassGrade");
							cgOfExistingUser.setTeacher_id(null);
							this.classGradeDAO.updateClassGrade(cgOfExistingUser);

							existingCg.setTeacher_id(existingUsers.getId());
							existingCg.setUpdated_date(new Date());
							session.update(existingCg);
						}
					} else if (existingUsers.getRoleType().equals("school_staff")) {
						cgOfExistingUser.setTeacher_id(null);
						this.classGradeDAO.updateClassGrade(cgOfExistingUser);
					}
				} else {
					existingCg.setTeacher_id(existingUsers.getId());
					this.classGradeDAO.updateClassGrade(existingCg);
				}
				tx.commit();
				createOrUpdate = "Update";
			}
			log.info("Exit");
		} catch (Exception e) {
			log.info("Exception Occured in createTeachersOrStaffForWeb()" + e);
			if (null != cg)
				session.evict(cg);
			if (null != users)
				session.evict(users);
			tx.rollback();
		} finally {
			session.close();
		}
		log.info("Exiting createTeachersOrStaffForWeb() }");
		return createOrUpdate;
	}

	@Override
	public Map<String, Object> updateTeachersOrStaff(Users loginUser, List<TeachersStaffCSVModel> teachersUpdateList) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		Users users = null;
		ClassGrade cg = null;
		int teacherOrStaffUpdateSuccess = 0;
		int teacherOrStaffUpdateFailure = 0;
		Map<String, Object> finalMap = new HashMap<String, Object>();

		try {
			session = sessionFactory.openSession();
			for (int i = 0; i < teachersUpdateList.size(); i++) {
				users = this.userDAO.findUsers(teachersUpdateList.get(i).getUsername().toLowerCase());
				tx = session.beginTransaction();
				if (teachersUpdateList.get(i).getRole().equals("teacher"))
					users.setRoleType("school_teacher");
				else if (teachersUpdateList.get(i).getRole().equals("staff"))
					users.setRoleType("school_staff");
				users.setMobileNumber(teachersUpdateList.get(i).getContactNo());
				users.setName(teachersUpdateList.get(i).getName());
				users.setUpdatedDate(new java.util.Date());
				session.merge(users);

				if (teachersUpdateList.get(i).getRole().equals("teacher")) {
					cg = this.classGradeDAO.checkIfClassGradeExists(loginUser.getAccounts().getAccountId(),
							teachersUpdateList.get(i).getGrade(), teachersUpdateList.get(i).getStClass());
					if (cg == null) {
						log.info("ClassGrade is creating Newely for updateTeachersOrStaff()");
						// session.merge(users);
						ClassGrade cgOfExistingUser = this.classGradeDAO.findSchoolGrade(users.getId());

						if (cgOfExistingUser == null) {
							log.info("Creating a new ClassGrade Object");
							cg = new ClassGrade();
							cg.setGrade(teachersUpdateList.get(i).getGrade());
							cg.setStudentClass(teachersUpdateList.get(i).getStClass());
							cg.setSchoolId(users.getAccounts().getAccountId());
							cg.setTeacher_id(users.getId());
							session.save(cg);
						} else {
							log.info("Updating a new ClassGrade Object");

							cgOfExistingUser.setTeacher_id(null);

							cgOfExistingUser.setGrade(teachersUpdateList.get(i).getGrade());
							cgOfExistingUser.setStudentClass(teachersUpdateList.get(i).getStClass());
							cgOfExistingUser.setSchoolId(users.getAccounts().getAccountId());
							cgOfExistingUser.setTeacher_id(users.getId());
							session.merge(cgOfExistingUser);
						}
						teacherOrStaffUpdateSuccess++;
					} else if (null != cg && cg.getTeacher_id() == null) {
						log.info("Existing ClassGrade And Teacher Id is Null for updateTeachersOrStaff()");
						// session.merge(users);
						cg.setTeacher_id(users.getId());
						// session.update(cg);
						session.merge(cg);
						teacherOrStaffUpdateSuccess++;
					} else if (null != cg && cg.getTeacher_id() != null) {
						log.info(
								"Existing ClassGrade And Teacher Id is Null for updateTeachersOrStaff() when teacher id is not null");
						cg.setTeacher_id(null);
						cg.setTeacher_id(users.getId());
						session.merge(cg);
						teacherOrStaffUpdateSuccess++;
					}
				} else {
					log.info("Role type is staff for updateTeachersOrStaff()");
					ClassGrade cgOfExistingUser = this.classGradeDAO.findSchoolGrade(users.getId());
					if (null != cgOfExistingUser) {
						cgOfExistingUser.setTeacher_id(null);
						session.merge(cgOfExistingUser);
					} else {
						log.info("No Class grade found for the user id");
					}
					teacherOrStaffUpdateSuccess++;
				}
				log.info("teacherOrStaffUpdateSuccessCount" + "\t" + teacherOrStaffUpdateSuccess);
				tx.commit();
			}
		} catch (Exception e) {
			log.info("Exception Caught in updateTeachersOrStaff()" + e);
			// teacherOrStaffUpdateFailure++;
			tx.rollback();
		} finally {
			session.close();
		}
		log.info("teacherOrStaffUpdateFailure" + "\t" + teacherOrStaffUpdateFailure);

		finalMap.put("teacherOrStaffUpdateSuccess", teacherOrStaffUpdateSuccess);
		finalMap.put("teacherOrStaffUpdateFailure", teacherOrStaffUpdateFailure);

		/*
		 * teacherStaffUpdateArray[0] = teacherOrStaffUpdateSuccess;
		 * teacherStaffUpdateArray[1] = teacherOrStaffUpdateFailure;
		 */

		return finalMap;
	}

	@Override
	public List<TeachersTransform> findTeachersStaffList(int userId, String roleType, String grade, int page_id,
			int total) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		List<TeachersTransform> teachersTransformList = null;
		StringBuilder teacherStaffSB = null;
		String teachersStaffBaseQry = "";
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			log.info("roleType in findTeachersStaffList()" + roleType);
			log.info("grade in findTeachersStaffList()" + grade);
			teacherStaffSB = new StringBuilder();

			teacherStaffSB
					.append("select teacherstaff.user_id as teacherId, teacherstaff.name as name,teacherstaff.user_active as status,teacherstaff.role_type as rType,cg.grade as grade, ")
					.append("cg.class as stclass, teacherstaff.mobile_number as contact_no,teacherstaff.username as username from users teacherstaff ")
					.append("left join class_grade cg on cg.teacher_id = teacherstaff.user_id ")
					.append("left join users admin on admin.account_id = teacherstaff.account_id ")
					.append("where admin.user_id = ? ");

			/*
			 * String teachersStaffBaseQry =
			 * "select teacherstaff.user_id as teacherId, teacherstaff.name as name,teacherstaff.user_active as status,teacherstaff.role_type as rType,cg.grade as grade,"
			 * +
			 * "cg.class as stclass, teacherstaff.mobile_number as contact_no,teacherstaff.username as username from users teacherstaff "
			 * +
			 * "left join class_grade cg on cg.teacher_id = teacherstaff.user_id "
			 * +
			 * "left join users admin on admin.account_id = teacherstaff.account_id "
			 * + "where admin.user_id = ? " ;
			 */

			if (roleType.equals(Constant.SchoolTeacher) && grade.equals("ALL")) {
				teacherStaffSB.append(" and (teacherstaff.role_type =" + "'" + Constant.SchoolTeacher + "'" + ")");
				// teachersStaffBaseQry = teachersStaffBaseQry + " and
				// (teacherstaff.role_type ="+ "'" +Constant.SchoolTeacher+ "'"
				// +")" ;
			} else if (roleType.equals(Constant.SchoolTeacher) && !grade.equals("ALL") && grade != null
					|| roleType.equals("ALL") && grade != null && !grade.equals("ALL")) {
				teacherStaffSB.append(" and (teacherstaff.role_type =" + "'" + Constant.SchoolTeacher + "'" + ")"
						+ " and grade= " + "'" + grade + "'");
				// teachersStaffBaseQry = teachersStaffBaseQry + " and
				// (teacherstaff.role_type ="+ "'" +Constant.SchoolTeacher+ "'"
				// +")" + " and grade= "+ "'"+ grade +"'" ;
			} else if (roleType.equals(Constant.SchoolStaff)) {
				teacherStaffSB.append(" and (teacherstaff.role_type = " + "'" + Constant.SchoolStaff + "'" + ")");
				// teachersStaffBaseQry = teachersStaffBaseQry + " and
				// (teacherstaff.role_type = " +"'" +Constant.SchoolStaff+ "'"
				// +")" ;
			} else {
				teacherStaffSB.append(" and (teacherstaff.role_type= " + "'" + Constant.SchoolTeacher + "'"
						+ " or teacherstaff.role_type= " + "'" + Constant.SchoolStaff + "'" + ")");
				// teachersStaffBaseQry = teachersStaffBaseQry + " and
				// (teacherstaff.role_type= " + "'" +Constant.SchoolTeacher+ "'"
				// +" or teacherstaff.role_type= " + "'" +Constant.SchoolStaff+
				// "'" +")" ;
			}

			if (page_id > 0) {
				teacherStaffSB = teacherStaffSB.append("limit " + (page_id - 1) + "," + total);
			}

			teachersStaffBaseQry = teacherStaffSB.toString();

			Query query = session.createSQLQuery(teachersStaffBaseQry).addScalar("teacherId").addScalar("name")
					.addScalar("status").addScalar("rType").addScalar("grade").addScalar("stclass")
					.addScalar("contact_no").addScalar("username").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(TeachersTransform.class));
			query.setParameter(0, userId);
			teachersTransformList = (List<TeachersTransform>) query.list();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception In findTeachersStaffList()" + e);
			tx.rollback();
		} finally {
			session.close();
		}
		return teachersTransformList;
	}

	/*
	 * @Override public int deleteTeacherStaff(int userId) { // TODO
	 * Auto-generated method stub
	 * 
	 * Session session = sessionFactory.openSession();
	 * 
	 *//**
		 * Update Teacher Id In Class Grade to null.
		 */

	/*
	 * Transaction updateClassGradeTransaction = session.beginTransaction();
	 * 
	 * Criteria criteria = session.createCriteria(ClassGrade.class);
	 * criteria.add(Restrictions.eq("teacher_id", userId)); ClassGrade cg =
	 * (ClassGrade)criteria.uniqueResult(); cg.setTeacher_id(null);
	 * session.update(cg);; updateClassGradeTransaction.commit();
	 * 
	 *//**
		 * Delete User related User Information from Users table
		 *//*
		 * 
		 * Transaction deleteUserTransaction = session.beginTransaction();
		 * String deleteQueryForUser =
		 * "DELETE from Users as u  where u.id=:userId ";
		 * 
		 * Query deleteUser = session.createQuery(deleteQueryForUser);
		 * deleteUser.setParameter("userId", userId); int deletedUsers =
		 * deleteUser.executeUpdate(); deleteUserTransaction.commit();
		 * 
		 * log.info("deletedUsers"+"\t"+deletedUsers); session.close();
		 * 
		 * return deletedUsers; }
		 */

	@Override
	public int deleteTeacherStaff(int userId) {
		// TODO Auto-generated method stub
		/**
		 * Run the below query in SQL to find out dependent tables of user data
		 * that needs to be removed before finally removing from users table
		 * SELECT DISTINCT TABLE_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE
		 * COLUMN_NAME in('user_id','teacher_id','staff_id') AND
		 * TABLE_SCHEMA='liteon'; Below tables needs to be taken care
		 * 1.class_grade 2.closed_device_events 3.device_events_queue
		 * 4.event_subscriptions 5.geozones 6.parent_kids 7.rewards_students
		 * 8.users
		 */

		Session session = null;
		Transaction updateClassGradeTransaction = null, deleteUserTransaction = null,
				deleteClosedDeviceEventsTransaction = null, deleteDeviceEventsQueueTransaction = null,
				deleteEventSubscriptionsTransaction = null, deleteGeozonesTransaction = null,
				deleteParentkidsTransaction = null, deleteRewardsStudentsTransaction = null,
				deleteRemindersTransaction = null, deleteTimeTableTransaction = null, deleteClassGradeBasedInClassGradeId = null;
		int deletedUsers = 0;
		try {

			session = sessionFactory.openSession();

			/**
			 * Update Teacher Id In Class Grade to null in case of user is
			 * School Teacher, In case of School Staff as there will not be any
			 * entry in Class_Grade we will bypass that validation.
			 */
			Users user = this.userDAO.findUserById(userId);

			if (user.getRoleType().equals(Constant.SchoolTeacher)) {
				
				Criteria criteria = session.createCriteria(ClassGrade.class);
				criteria.add(Restrictions.eq("teacher_id", userId));
				ClassGrade cg = (ClassGrade) criteria.uniqueResult();
				if(checkStudentAssociationWithTeacher(cg.getClassGradeId())){
					updateClassGradeTransaction = session.beginTransaction();
					log.debug("inside if ::: ");
					cg.setTeacher_id(null);
					session.update(cg);
					updateClassGradeTransaction.commit();
				}else{
					
					log.debug("inside else ::: ");
					deleteRemindersTransaction = session.beginTransaction();
					String deleteQueryForReminderDelete = "DELETE from Reminders where class_grade_id = :class_grade_id";
					Query deleteReminders = session.createQuery(deleteQueryForReminderDelete);
					deleteReminders.setParameter("class_grade_id", cg.getClassGradeId());
					deleteReminders.executeUpdate();
					deleteRemindersTransaction.commit();
					
					
					deleteTimeTableTransaction = session.beginTransaction();
					String deleteQueryForTimeTableDelete = "DELETE from Timetable where class_grade_id = :class_grade_id";
					Query deleteTimeTable = session.createQuery(deleteQueryForTimeTableDelete);
					deleteTimeTable.setParameter("class_grade_id", cg.getClassGradeId());
					deleteTimeTable.executeUpdate();
					deleteTimeTableTransaction.commit();
					
					deleteClassGradeBasedInClassGradeId = session.beginTransaction();
					String deleteQueryForClassGradeDelete = "DELETE from ClassGrade where class_grade_id = :class_grade_id";
					Query deleteClassGradeId = session.createQuery(deleteQueryForClassGradeDelete);
					deleteClassGradeId.setParameter("class_grade_id", cg.getClassGradeId());
					deleteClassGradeId.executeUpdate();
					deleteClassGradeBasedInClassGradeId.commit();
					
				}
				
			}

			// Delete staff id entry from ClosedDeviceEvents
			deleteClosedDeviceEventsTransaction = session.beginTransaction();
			String deleteQueryForClosedDeviceEvents = "DELETE from ClosedDeviceEvents as cde where cde.staffId=:userId ";

			Query deleteClosedDeviceEvents = session.createQuery(deleteQueryForClosedDeviceEvents);
			deleteClosedDeviceEvents.setParameter("userId", userId);
			deletedUsers = deleteClosedDeviceEvents.executeUpdate();
			deleteClosedDeviceEventsTransaction.commit();

			// Delete user id entry from ClosedDeviceQueue
			deleteDeviceEventsQueueTransaction = session.beginTransaction();
			String deleteQueryForDeviceEventsQueue = "DELETE from DeviceEventsQueue as deq where deq.users.id=:userId ";

			Query deleteDeviceEventsQueue = session.createQuery(deleteQueryForDeviceEventsQueue);
			deleteDeviceEventsQueue.setParameter("userId", userId);
			deletedUsers = deleteDeviceEventsQueue.executeUpdate();
			deleteDeviceEventsQueueTransaction.commit();

			// Delete user id entry from EventSubscriptions
			deleteEventSubscriptionsTransaction = session.beginTransaction();
			String deleteQueryForEventSubscriptions = "DELETE from EventSubscriptions as es where es.users.id=:userId ";

			Query deleteEventSubscriptions = session.createQuery(deleteQueryForEventSubscriptions);
			deleteEventSubscriptions.setParameter("userId", userId);
			deletedUsers = deleteEventSubscriptions.executeUpdate();
			deleteEventSubscriptionsTransaction.commit();

			// Delete user id entry from Geozones
			deleteGeozonesTransaction = session.beginTransaction();
			String deleteQueryForGeozones = "DELETE from Geozones as gz where gz.users.id=:userId ";

			Query deleteGeozones = session.createQuery(deleteQueryForGeozones);
			deleteGeozones.setParameter("userId", userId);
			deletedUsers = deleteGeozones.executeUpdate();
			deleteGeozonesTransaction.commit();

			// Delete user id entry from ParentKids
			deleteParentkidsTransaction = session.beginTransaction();
			String deleteQueryForParentkids = "DELETE from ParentKids as pk where pk.users.id=:userId ";

			Query deleteParentkids = session.createQuery(deleteQueryForParentkids);
			deleteParentkids.setParameter("userId", userId);
			deletedUsers = deleteParentkids.executeUpdate();
			deleteParentkidsTransaction.commit();

			// Delete user id entry from RewardsStudents
			deleteRewardsStudentsTransaction = session.beginTransaction();
			String deleteQueryForRewardsStudents = "DELETE from RewardsStudents as rs where rs.users.id=:userId ";

			Query deleteRewardsStudents = session.createQuery(deleteQueryForRewardsStudents);
			deleteRewardsStudents.setParameter("userId", userId);
			deletedUsers = deleteRewardsStudents.executeUpdate();
			deleteRewardsStudentsTransaction.commit();

			/**
			 * Delete User related User Information from Users table
			 */

			deleteUserTransaction = session.beginTransaction();
			String deleteQueryForUser = "DELETE from Users as u where u.id=:userId ";

			Query deleteUser = session.createQuery(deleteQueryForUser);
			deleteUser.setParameter("userId", userId);
			deletedUsers = deleteUser.executeUpdate();
			deleteUserTransaction.commit();

			log.info("deletedUsers" + "\t" + deletedUsers);

		} catch (Exception e) {
			log.info("Exception Occured in deleteTeacherStaff()" + e);
			if (null != updateClassGradeTransaction)
				updateClassGradeTransaction.rollback();
			if (null != deleteUserTransaction)
				deleteUserTransaction.rollback();
		} finally {
			session.close();
		}
		return deletedUsers;
	}
	
	
	public boolean checkStudentAssociationWithTeacher(Integer class_grade_id) {

		log.debug("class_grade_id :: "+class_grade_id);
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select IFNULL(st.student_id, 0) from class_grade cg"
				+ " LEFT JOIN students st on cg.class_grade_id = st.class_grade_id"
				+ " where cg.class_grade_id = ?";
		List<BigInteger> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		query.setParameter(0, class_grade_id);
		sessions = (List<BigInteger>) query.list();
		session.close();
		if (sessions != null && sessions.size() > 0){
			if(sessions.get(0).intValue() > 0){
				return true;
			}else{
				return false;
			}
		}
		return false;
		
	}
	

}
