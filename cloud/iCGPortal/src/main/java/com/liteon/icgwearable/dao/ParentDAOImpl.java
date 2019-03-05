package com.liteon.icgwearable.dao;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.hibernate.entity.ParentKids;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.GuardianModel;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.StringUtility;

@Repository("parentDAO")
public class ParentDAOImpl implements ParentDAO {

	private static Logger log = Logger.getLogger(ParentDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired
	private StudentsDAO studentsDAO;

	@Autowired
	private UserDAO userDAO;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public List<TeachersStudentsTransform> viewKidsList(int userId, String classId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String parentKidsQuery = null;

		parentKidsQuery = "	select s.student_id as studentId, s.name as studentName,  \r\n"
				+ " s.nickname as nickName, s.roll_no as rollNo,s.dob as dob, \r\n"
				+ " s.height as height, s.weight as weight,s.emergency_contact as emergency_contact, s.allergies as allergeis ,s.registartion_no as registartionNumber, s.gender as gender, \r\n"
				+ " s.created_date as createdDate, s.updated_date as updatedDate, ds.device_uuid as deviceUuid from students as s \r\n"
				+ " left join device_students ds on  ds.student_id = s.student_id and ds.status ='active' \r\n"
				+ " left join parent_kids pk on pk.student_id =  ds.student_id\r\n" + " where pk.user_id=:userid ";

		List<TeachersStudentsTransform> parentsKidsList = null;
		Query query = session.createSQLQuery(parentKidsQuery).addScalar("studentId").addScalar("studentName")
				.addScalar("nickName").addScalar("allergeis").addScalar("rollNo").addScalar("registartionNumber")
				.addScalar("dob").addScalar("height").addScalar("weight").addScalar("emergency_contact")
				.addScalar("gender").addScalar("createdDate").addScalar("updatedDate").addScalar("deviceUuid")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TeachersStudentsTransform.class));

		query.setParameter("userid", userId);

		parentsKidsList = (List<TeachersStudentsTransform>) query.list();
		tx.commit();
		session.close();
		return parentsKidsList;
	}

	@Override
	public void updateKids(Students kids) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.update(kids);
		tx.commit();
		session.close();
	}

	@Override
	public List<StudentsTransform> findKidsClassAndSchool(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String kidsClassSchoolQuery = "select distinct s.class as stClass, acc.account_name as schoolName from students s "
				+ "left join accounts a on a.account_id = s.account_id "
				+ "left join accounts acc on acc.account_id = s.school_id "
				+ "left join users u on u.account_id = a.account_id " + "where user_id= ? ";
		List<StudentsTransform> kidsClassSchoolList = null;

		Query query = session.createSQLQuery(kidsClassSchoolQuery).addScalar("stClass").addScalar("schoolName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsTransform.class));
		query.setParameter(0, userId);
		kidsClassSchoolList = (List<StudentsTransform>) query.list();
		tx.commit();
		session.close();
		return kidsClassSchoolList;
	}

	@Override
	public List<TeachersStudentsTransform> viewKidsListByClass(int userId, int classId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String parentKidsQuery = "select s.student_id as studentId, s.account_id as accountId, s.school_id as schoolId ,s.name as studentName, "
				+ "s.nickname as nickName, s.class as studentClass,s.roll_no as rollNo,s.dob as dob, "
				+ "s.height as height, s.weight as weight,  s.gender as gender, "
				+ "s.create_date, s.updated_date from students as s "
				+ "left join accounts a on a.account_id = s.account_id "
				+ "left join users u on u.account_id = a.account_id " + "where u.user_id= ? and s.class = ?";

		List<TeachersStudentsTransform> parentsKidsList = null;

		Query query = session.createSQLQuery(parentKidsQuery).addScalar("studentId").addScalar("accountId")
				.addScalar("schoolId").addScalar("studentName").addScalar("nickName").addScalar("studentClass")
				.addScalar("rollNo").addScalar("dob").addScalar("height").addScalar("weight").addScalar("gender")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(TeachersStudentsTransform.class));
		query.setParameter(0, userId);
		query.setParameter(1, classId);
		parentsKidsList = (List<TeachersStudentsTransform>) query.list();

		tx.commit();
		session.close();
		return parentsKidsList;
	}

	@Override
	public boolean ParentKidLinked(int userId, int studentId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_QUERY = "SELECT `parent_kid_id` FROM `parent_kids` "
				+ "WHERE `user_id` = :userId "
				+ "AND `student_id` = :studentId";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("userId", userId);
		query.setParameter("studentId", studentId);
		List<Object[]> row = query.list();
		tx.commit();
		session.close();

		if (row.size() > 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean findKidForParent(int userId, int studentId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "SELECT `parent_kid_id` FROM `parent_kids` "
				+ "WHERE `user_id` = :userId "
				+ "AND `student_id` = :studentId";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("userId", userId);
		query.setParameter("studentId", studentId);
		List<Object[]> row = query.list();
		tx.commit();
		session.close();

		if (row.size() > 0)
			return true;
		else
			return false;
	}

	/*
	 * Method : createGuradian--> To create the new guardian and its mapping
	 * with kids Param : Users(logged in user),GuardianModel(RequestBody)
	 */
	@Override
	public void createGuradian(Users userBySessionId, GuardianModel guardianModel) {
		Session session = null;
		Transaction tx = null;
		Users users = null;
		ParentKids parentKids = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			if (null != guardianModel) {
				users = new Users();
				users.setUsername(guardianModel.getGuardianUserName());
				users.setUserActive("n");
				users.setAccounts(userBySessionId.getAccounts());
				users.setRoleType(Constant.ParentMember);
				users.setActivationCode(StringUtility.randomStringOfLength(40));
				session.save(users);
				parentKids = createParentKidMapping(guardianModel, session, users, parentKids);
				tx.commit();
				String status = this.userDAO.sendUserActivationEmail(users.getUsername(), users.getActivationCode(),
						users.getUsername());
				if (status.equalsIgnoreCase("Success"))
					log.info("maill sent>>>>>>>>>>>>>>>>>>" + "\t" + status);
			} else {
				log.info("Users or student is empty");
			}
		} catch (Exception e) {
			log.info("Exception found in Guardian kid Mapping: " + "\t" + e);
			if (null != users)
				session.evict(users);
			if (null != parentKids)
				session.evict(parentKids);
			tx.rollback();
		} finally {
			session.close();
		}
	}

	/*
	 * Method : createParentKidMapping--> To create the guardian kid mapping in
	 * ParentKids table Param : GuardianModel,Session,Users,ParentKids
	 */
	private ParentKids createParentKidMapping(GuardianModel guardianModel, Session session, Users users,
			ParentKids parentKids) {
		List<String> kidsNames = guardianModel.getKidsname();
		for (String kid : kidsNames) {
			log.info(kid);
			Students student = this.studentsDAO.getStudent(Integer.parseInt(kid));
			parentKids = new ParentKids();
			parentKids.setUsers(users);
			parentKids.setStudent(student);
			parentKids.setCreatedDate(new Date());
			session.save(parentKids);
		}
		return parentKids;
	}

	/*
	 * Method : editGuradian--> To create the edit the existing guardian and its
	 * mapping with kids Param : Users(existing guardian
	 * user),GuardianModel(RequestBody)
	 */
	@Override
	public void editGuradian(Users guardianUser, GuardianModel guardianModel) {
		Session session = null;
		Transaction transaction = null;
		ParentKids parentKids = null;
		Boolean usernameChanged = true;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();

			/*
			 * if(null != guardianUser.getName()){ log.debug(
			 * "GuardianUser is not null ::"); log.debug(
			 * "guardianUser.getName() ::"+guardianUser.getName()); log.debug(
			 * "guardianModel.getName() :: "+guardianModel.getName());
			 * if(guardianUser.getName().equals(guardianModel.getName())){
			 * 
			 * } }
			 */
			if (null != guardianModel.getName()) {
				guardianUser.setName(guardianModel.getName());
			}
			// Need to send email verification link in case of email/user name
			// changed
			if (null != guardianModel.getGuardianUserName()) {
				if (guardianUser.getUsername().equals(guardianModel.getGuardianUserName())) {
					usernameChanged = false;
				}
				guardianUser.setUsername(guardianModel.getGuardianUserName());
			}
			if (null != guardianModel.getMobileNumber()) {
				guardianUser.setMobileNumber(guardianModel.getMobileNumber());
			}
			log.info("usernameChanged>>>>>>>>>>>>>>>>>>" + "\t" + usernameChanged);
			if (usernameChanged) {
				guardianUser.setActivationCode(StringUtility.randomStringOfLength(40));
				log.debug("setting the active status to n in users table");
				guardianUser.setUserActive("n");
				session.update(guardianUser);
			} else {
				log.debug("updating the user table");
				session.update(guardianUser);
			}
			// Delete Mapping
			try {
				String delete_PK = "delete from parent_kids where user_id=:userId";
				log.info("delete query>>>>>>" + delete_PK);
				int deletedPKStatus = session.createSQLQuery(delete_PK).setParameter("userId", guardianUser.getId())
						.executeUpdate();
				log.info("updated????" + deletedPKStatus);
			} catch (Exception e) {
				log.info("Exception Ocuured when deleting the parentKid Mapping when editing the guardian\t" + e);
			}

			// Create Mapping
			parentKids = createParentKidMapping(guardianModel, session, guardianUser, parentKids);
			transaction.commit();
			if (usernameChanged) {
				String status = this.userDAO.sendUserActivationEmail(guardianUser.getName(),
						guardianUser.getActivationCode(), guardianUser.getUsername());
				log.info("maill Status>>>>>>>>>>>>>>>>>>" + "\t" + status);
				if (status.equalsIgnoreCase("Success"))
					log.info("maill sent>>>>>>>>>>>>>>>>>>" + "\t" + status);
			}
		} catch (Exception e) {
			log.info("Exception in edit guardian" + "\t" + e);
			if (null != guardianUser)
				session.evict(guardianUser);
			if (null != guardianModel)
				session.evict(guardianModel);
			transaction.rollback();
		} finally {
			session.close();
		}
	}

	/*
	 * Method : deleteGuardian--> To delete the existing guardian and all its
	 * associated tables Param : Users(existing guardian user)
	 */
	@Override
	public void deleteGuardian(Users guardianUser) {
		Session session = null;
		Transaction transactionDEQ = null;
		try {
			session = sessionFactory.openSession();
			transactionDEQ = session.beginTransaction();

			String DeleteQuery_deviceEventsQueue = "delete from device_events_queue where user_id=:deq_user_id";
			int deleteddeviceEventsQueue = session.createSQLQuery(DeleteQuery_deviceEventsQueue)
					.setParameter("deq_user_id", guardianUser.getId()).executeUpdate();
			log.info("DeleteQuery_deviceEventsQueue Query " + DeleteQuery_deviceEventsQueue);
			log.info("updated " + deleteddeviceEventsQueue);
			transactionDEQ.commit();
		} catch (Exception e) {
			log.info("Exception in device_events_queue deletions when deleting guardian" + e);
		}
		try {
			Transaction transactionES = session.beginTransaction();

			String DeleteQuery_eventSubscriptions = "delete from event_subscriptions where user_id=:es_user_id";
			int deletedeventSubscriptions = session.createSQLQuery(DeleteQuery_eventSubscriptions)
					.setParameter("es_user_id", guardianUser.getId()).executeUpdate();
			log.info("DeleteQuery_deviceEventsQueue Query " + DeleteQuery_eventSubscriptions);
			log.info("updated " + deletedeventSubscriptions);
			transactionES.commit();
		} catch (Exception e) {
			log.info("Exception in device_events_queue deletions when deleting guardian" + e);
		}

		try {
			Transaction transactionPK = session.beginTransaction();

			String DeleteQuery_parentKid = "delete from parent_kids where user_id=:pk_user_id";
			int deletedparentKid = session.createSQLQuery(DeleteQuery_parentKid)
					.setParameter("pk_user_id", guardianUser.getId()).executeUpdate();
			log.info("DeleteQuery_parentKid Query " + DeleteQuery_parentKid);
			log.info("updated " + deletedparentKid);
			transactionPK.commit();
		} catch (Exception e) {
			log.info("Exception in parentkid Mapping deletions" + e);
		}
		try {
			Transaction transactionU = session.beginTransaction();

			String DeleteQuery_User = "delete from users where user_id=:user_id";
			int deletedUser = session.createSQLQuery(DeleteQuery_User).setParameter("user_id", guardianUser.getId())
					.executeUpdate();
			log.info("DeleteQuery_User Query " + DeleteQuery_User);
			log.info("updated " + deletedUser);
			transactionU.commit();

		} catch (Exception e) {
			log.info("Exception in guardian deletions" + e);
		} finally {
			session.close();
		}
	}

	@Override
	public void linkParentKid(int userId, int studentId) {
		log.info("Inside linkParentKid");
		/*Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		ParentKids pk = new ParentKids();
		pk.setUsers(users);
		pk.setStudent(students);
		pk.setCreatedDate(new java.util.Date());
		session.save(pk);
		tx.commit();
		session.close();*/
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("INSERT INTO parent_kids "
				+ "(created_date, student_id, user_id) "
				+ "VALUES (?, ?, ?)");
		query.setParameter(1, studentId);
		query.setParameter(2, userId);
		query.setParameter(0, new Date());
		int result = query.executeUpdate();
		log.info("linkParentKid Result" + result);
		tx.commit();
		session.close();
	}
}
