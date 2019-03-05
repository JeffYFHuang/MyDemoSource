package com.liteon.icgwearable.dao;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.exception.MailNotSendException;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.DeviceStudents;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Timetable;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.NewDevicePairingModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.transform.AccountsTransform;
import com.liteon.icgwearable.transform.DevicesTransform;
import com.liteon.icgwearable.transform.GuardiansDetailsListTransform;
import com.liteon.icgwearable.transform.HealthStudentTransform;
import com.liteon.icgwearable.transform.KidsListForParentMemberTransform;
import com.liteon.icgwearable.transform.RewardsTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.ValidNewDevicePairingTransform;
import com.liteon.icgwearable.transform.ValidParentStudentTransform;
import com.liteon.icgwearable.transform.ValidParentUsersWithTokenTransform;
import com.liteon.icgwearable.transform.ValidTeacherUsersWithTokenTransform;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ProcessMailUtility;
import com.liteon.icgwearable.util.StringUtility;

@Repository("userDAO")
public class UserDAOImpl implements UserDAO {

	private static Logger log = Logger.getLogger(UserDAOImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	private ProcessMailUtility processMailUtility;
	@Autowired
	protected SystemConfigurationDAO systemConfigurationDAO;

	@Resource(name = "configProperties")
	private Properties configProperties;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public Users checkLogin(String userName, String userPassword) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Users user = null;

		// Query using Hibernate Query Language
		String SQL_QUERY = " from Users as o where o.username=:username and o.password=:password and o.userActive = 'y' ";
		Query query = session.createQuery(SQL_QUERY);
		query.setParameter("username", userName);
		query.setParameter("password", userPassword);
		List list = query.list();

		if ((list != null) && (list.size() > 0)) {
			log.info("user retived");
			user = (Users) list.get(0);
		}
		session.close();
		return user;
	}

	@Override
	public Users getUserDetails(String userName) {
		Users users = null;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			String SQL_QUERY = " from Users where username = :username and userActive = 'y'";
			Query query = session.createQuery(SQL_QUERY);
			query.setParameter("username", userName);
			List list = query.list();

			if (list != null && (list.size() > 0)) {
				users = (Users) list.get(0);
			}
			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}
	
	@Override
	public HashMap<String, Object> getContactListForParentAdmin(String uuid, String role_type) {
		ArrayList<HashMap<String, Object>> contactsList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> outterMap = new HashMap<String, Object>();
		List<Object[]> rows = null;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String strQuery = null;
		
		if(role_type.equals("parent_admin")){
			strQuery = "SELECT u.`name`, u.`username`, u.`role_type` "
					+ "FROM `users` AS u, device_students ds, students s, class_grade cg "
					+ "WHERE s.student_id = ds.student_id AND ds.device_uuid = :uuid "
					+ "AND s.class_grade_id = cg.class_grade_id AND cg.school_id = u.account_id "
					+ "AND u.user_active = 'y' AND ds.status='active' AND u.role_type IN ('school_teacher', 'school_staff')";
		}else{
			strQuery = "SELECT u.`name`, u.`username`, u.`role_type` FROM `users` AS u, device_students ds, parent_kids pk WHERE "
					+ "pk.student_id = ds.student_id AND pk.user_id = u.user_id AND ds.device_uuid = :uuid AND "
					+ "u.user_active = 'y' AND ds.status='active' AND u.role_type='parent_admin'";
		}
		
		Query query = session.createSQLQuery(strQuery);
		query.setParameter("uuid", uuid);
		rows = query.list();
		
		for (Object[] row : rows) {
			HashMap<String, Object> contactsmap = new HashMap<String, Object>();
			String name = row[0].toString();
			String username = row[1].toString();
			String userrole = row[2].toString();

			contactsmap.put("name", name);
			contactsmap.put("username", username);
			contactsmap.put("userrole", userrole);
			

			contactsList.add(contactsmap);
		}

		outterMap.put("results", contactsList);

		return outterMap;
		
	}

	@Override
	public Users getUserByUserName(String userName) {
		log.info("entered getUserByUserName");
		log.info("userName: " + userName);
		Session session = null;
		Transaction tx = null;
		Users user = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			String SQL_QUERY = "SELECT user_id, name, username, role_type FROM users"
					+ " WHERE username = :userName ";
			Query query = session.createSQLQuery(SQL_QUERY);
			query.setParameter("userName", userName);
			List<Object[]> row = query.list();
			for (Object[] list : row) {
				user = new Users();
				if (null != list[1]) {
					user.setName(list[1].toString());
				}
				if (null != list[2]) {
					user.setUsername(list[2].toString());
				}
				if (null != list[3]) {
					user.setRoleType(list[3].toString());
				}

				try {
					if (null != list[0]) {
						user.setId(Integer.parseInt(list[0].toString()));
					}
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != tx) {
				tx.commit();
			}
			if(null != session) {
				session.close();
			}
		}
		return user;
	}

	@Override
	public Users updateUser(Users user) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(user);
		tx.commit();
		session.close();
		return user;
	}

	@Override
	public void deleteUser(Users user) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.delete(user);
		tx.commit();
		session.close();

	}

	@Override
	public int deleteUserById(int userId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String DeleteQuery_users = "delete from users where user_id=:userid";
		int deletedUser = session.createSQLQuery(DeleteQuery_users).setParameter("userid", userId).executeUpdate();
		log.info("DeleteQuery_user by Id " + DeleteQuery_users);
		log.info("updated " + deletedUser);
		tx.commit();
		return deletedUser;
	}

	@Override
	public void deleteGuardian(Users guardianUser) {
		// Same method is used to delete Parent & Guardian
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
			Transaction transactionPK = session.beginTransaction();

			String DeleteQuery_parentKid = "delete from geozones where user_id=:pk_user_id";
			int deletedparentKid = session.createSQLQuery(DeleteQuery_parentKid)
					.setParameter("pk_user_id", guardianUser.getId()).executeUpdate();
			log.info("DeleteQuery_geozones Query " + DeleteQuery_parentKid);
			log.info("updated " + deletedparentKid);
			transactionPK.commit();
		} catch (Exception e) {
			log.info("Exception in geozones Mapping deletions" + e);
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
	public List<Users> getUserListByRoleType(int school_id, int type, int page_id, int total) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String usersListQuery = null;
		StringBuilder userListBuilder = null;
		List<Users> viewUsersList = null;
		try {
			userListBuilder = new StringBuilder();
			if (type == 0) {
				if (page_id > 0) {
					userListBuilder
							.append("SELECT sc.user_id as id, sc.name as name, sc.username as username, sc.mobile_number as mobileNumber, sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM users sc where  sc.account_id =:schoolId AND (sc.role_type = 'system_admin' OR sc.role_type='support_staff') ")
							.append("limit " + (page_id - 1) + "," + total);
				} else {
					userListBuilder.append(
							"SELECT sc.user_id as id, sc.name as name, sc.username as username, sc.mobile_number as mobileNumber, sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM users sc where  sc.account_id =:schoolId AND (sc.role_type = 'system_admin' OR sc.role_type='support_staff') ");
				}

				// usersListQuery = ;
			} else if (type == 1) {
				if (page_id > 0) {
					userListBuilder
							.append("SELECT sc.user_id as id, sc.name as name, sc.username as username, sc.mobile_number as mobileNumber, sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM users sc where  sc.account_id =:schoolId AND sc.role_type = 'system_admin' ")
							.append("limit " + (page_id - 1) + "," + total);
				} else {
					userListBuilder.append(
							"SELECT sc.user_id as id, sc.name as name, sc.username as username, sc.mobile_number as mobileNumber, sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM users sc where  sc.account_id =:schoolId AND sc.role_type = 'system_admin'");
				}
				// usersListQuery = "SELECT sc.user_id as id, sc.name as name,
				// sc.username as username, sc.mobile_number as mobileNumber,
				// sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM
				// users sc where sc.account_id =:schoolId AND sc.role_type =
				// 'system_admin'" ;
			} else if (type == 2) {
				if (page_id > 0) {
					userListBuilder
							.append("SELECT sc.user_id as id, sc.name as name, sc.username as username, sc.mobile_number as mobileNumber, sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM users sc where  sc.account_id =:schoolId AND sc.role_type = 'support_staff' ")
							.append("limit " + (page_id - 1) + "," + total);

				} else {
					userListBuilder.append(
							"SELECT sc.user_id as id, sc.name as name, sc.username as username, sc.mobile_number as mobileNumber, sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM users sc where  sc.account_id =:schoolId AND sc.role_type = 'support_staff' ");
				}
				// usersListQuery = "SELECT sc.user_id as id, sc.name as name,
				// sc.username as username, sc.mobile_number as mobileNumber,
				// sc.role_type as roleType, sc.lastlogin_date as lastLogin FROM
				// users sc where sc.account_id =:schoolId AND sc.role_type =
				// 'support_staff'" ;
			}
			usersListQuery = userListBuilder.toString();

			Query query = session.createSQLQuery(usersListQuery).addScalar("id").addScalar("name").addScalar("username")
					.addScalar("mobileNumber").addScalar("roleType").addScalar("lastLogin")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(Users.class));
			query.setParameter("schoolId", school_id);
			viewUsersList = (List<Users>) query.list();
			tx.commit();
		} catch (Exception e) {
			log.error("Exception Occured in getUserListByRoleType ()" + "\t" + e);
			tx.rollback();
		} finally {
			session.close();
		}
		return viewUsersList;
	}

	@Override
	public Users getUser(int userId) {
		// TODO Auto-generated method stub
		return (Users) sessionFactory.getCurrentSession().get(Users.class, userId);
	}

	@Override
	public Users updateUsersWithAccountId(Users users, Users webuser, String oldUserNamer) {

		Session session = sessionFactory.openSession();

		Transaction tx = session.beginTransaction();
		log.info("users.getPassword()" + users.getPassword());
		log.info("users.getMobileNumber()" + "\t" + users.getMobileNumber());
		try {
			log.info("users.getPassword() afetr encryption " + AESEncryption.generatePasswordHash(users.getPassword()));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (null != webuser) {
			// Users users2 = (Users) criteriaUsers.uniqueResult();
			users.setName(webuser.getName());
			users.setMobileNumber(webuser.getMobileNumber());
			if (webuser.getPassword().length() > 0) {
				try {
					users.setPassword(AESEncryption.generatePasswordHash(webuser.getPassword()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.debug(e.getMessage());
				}
			}
			users.setUpdatedDate(new Date());
			log.info("users.getPassword() afetr stroing into DB  " + users.getPassword());
		}
		session.update(users);
		log.info("Updated Users table Successfully...");
		tx.commit();
		try {
			if (users.getRoleType().equals(Constant.SuperAdmin) && (null != oldUserNamer)) {
				String status = this.sendUserActivationEmail(users.getName(), users.getActivationCode(),
						users.getUsername());
				log.info("maill sent>>>>>>>>>>>>>>>>>>" + "\t" + status);
				Transaction transaction = session.beginTransaction();
				users.setSessionId(null);
				session.update(users);
				transaction.commit();
			}
		} catch (Exception e) {
			log.debug("Exception while sending email to superadmin for changing his username" + "\t" + e);
		}

		session.close();
		return users;
	}

	@Override
	public String getRoleType(String username, String password) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*
		 * Criteria criteria = session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("username", username));
		 * criteria.add(Restrictions.eq("password", password)); Users users =
		 * (Users) criteria.uniqueResult();
		 */

		String SQL_QUERY = " from Users where username = :username AND password = :password";
		Query query = session.createQuery(SQL_QUERY);
		query.setParameter("username", username);
		query.setParameter("password", password);
		List list = query.list();
		Users users = null;
		if (list != null && (list.size() > 0)) {
			users = (Users) list.get(0);
		}

		tx.commit();
		session.close();
		return users.getRoleType();
	}

	@Override
	public List<RewardsTransform> getRewards(String userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<RewardsTransform> rewardsList;

		String rewardsQuery = "SELECT school_account.account_name AS schoolname, s.name AS kidname, s.nickname AS nickname, cg.class AS kidclass, rewards.name AS rewardname, "
				+ "rc.category_name as categoryName FROM users AS parent_user "
				+ "LEFT JOIN accounts AS parent_account ON parent_account.account_id = parent_user.account_id "
				+ "LEFT JOIN parent_kids pk on pk.user_id = parent_user.user_id "
				+ "LEFT JOIN students s on s.student_id=pk.student_id "
				+ "LEFT JOIN device_students ds on ds.student_id = s.student_id "
				+ "LEFT JOIN class_grade cg on cg.class_grade_id = s.class_grade_id  "
				+ "LEFT JOIN rewards_students AS rs ON rs.teacher_id = cg.teacher_id AND rs.student_id = ds.student_id "
				+ "LEFT JOIN rewards ON rewards.reward_id = rs.reward_id "
				+ "LEFT JOIN rewards_category AS rc ON rc.rewards_category_id = rewards.rewards_category_id "
				+ "LEFT JOIN users AS school_user ON school_user.user_id = cg.teacher_id "
				+ "LEFT JOIN accounts AS school_account ON school_account.account_id = school_user.account_id "
				+ "WHERE parent_user.user_id = ? AND rewards.reward_id IS NOT NULL ";
		Query query = session.createSQLQuery(rewardsQuery).addScalar("schoolname").addScalar("kidname")
				.addScalar("nickname").addScalar("kidclass").addScalar("rewardname").addScalar("categoryName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardsTransform.class));

		query.setParameter(0, userId);
		rewardsList = query.list();
		tx.commit();
		session.close();
		return rewardsList;
	}

	@Override
	public List<DevicesTransform> getDeviceUuid(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceUuidQuery = "SELECT devices.device_id as deviceId, devices.uuid as uuid FROM users \r\n"
				+ "	LEFT JOIN parent_kids pk ON users.user_id = pk.user_id \r\n"
				+ "   LEFT JOIN students s ON s.student_id = pk.student_id\r\n"
				+ "   LEFT JOIN device_students ds ON s.student_id = ds.student_id and ds.status='active'\r\n"
				+ "   LEFT JOIN devices ON devices.uuid = ds.device_uuid\r\n" + "	WHERE users.user_id =:user_id ";

		log.info("userId" + "\t" + userId);
		List<DevicesTransform> deviceList;
		Query query = session.createSQLQuery(deviceUuidQuery).addScalar("deviceId").addScalar("uuid")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DevicesTransform.class));
		query.setParameter("user_id", userId);
		deviceList = query.list();
		tx.commit();
		session.close();
		return deviceList;
	}

	@Override
	public int findUserIdByUNAndPass(String username, String password) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_QUERY = " from Users where username = :username AND password = :password";
		Query query = session.createQuery(SQL_QUERY);
		query.setParameter("username", username);
		query.setParameter("password", password);
		List list = query.list();
		Users users = null;
		if (list != null && (list.size() > 0)) {
			users = (Users) list.get(0);
		}

		tx.commit();
		session.close();
		return users.getId();
	}

	@Override
	public List<HealthStudentTransform> getHealthInfo(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<HealthStudentTransform> list = null;
		String healthQuery = "SELECT h.name as name, h.description as description, hs.comments as parameters FROM health_students AS hs "
				+ "LEFT JOIN health AS h ON h.health_id = hs.health_id "
				+ "LEFT JOIN students AS s ON s.student_id = hs.student_id "
				+ "LEFT JOIN accounts AS a ON a.account_id = s.account_id "
				+ "LEFT JOIN users AS u ON u.account_id = a.account_id " + "WHERE u.user_id = ? ";
		Query query = session.createSQLQuery(healthQuery).addScalar("name").addScalar("description")
				.addScalar("parameters").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(HealthStudentTransform.class));
		query.setParameter(0, userId);
		list = query.list();
		tx.commit();
		session.close();
		return list;
	}

	@Override
	public String accountRegister(Devices devices, Users user) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		user.setActivationCode(StringUtility.randomStringOfLength(40));
		user.setUserActive("n");

		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("username", user.getUsername()));
		Users users = (Users) criteria.uniqueResult();

		if (users != null) {
			return "Username already exist.";
		}
		criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", devices.getUuid()));
		Devices device = (Devices) criteria.uniqueResult();

		if (device == null) {
			return "Device Id doesn't exist.";
		}
		Accounts accounts = null;
		user.setAccounts(accounts);
		session.saveOrUpdate(user);
		tx.commit();
		session.close();

		processMailUtility.sentMail(user.getUsername(), user.getActivationCode(), user.getUsername(),
				user.getRoleType());

		log.debug("Updated Users table Successfully...");

		return "User registration successfully.";

	}

	public String sendEmail(Users users) {
		Integer[] configParams = this.systemConfigurationDAO.findSystemConfigurationParameters(1);
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Timestamp updatedAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
		updatedAt.setTime(updatedAt.getTime() + (((configParams[3] * 60) + configParams[3]) * 1000));
		String password_activation_code = StringUtility.randomStringOfLength(40);

		/*
		 * Criteria criteria = session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("id", users.getId())); Users user =
		 * (Users) criteria.uniqueResult();
		 * user.setActivationCode(StringUtility.randomStringOfLength(40));
		 * user.setActivationCodeExpiry(updatedAt); session.saveOrUpdate(user);
		 */

		Query query = session
				.createSQLQuery("UPDATE users SET password_activation_code_expiry = :password_activation_code_expiry, "
						+ "password_activation_code = :password_activation_code WHERE user_id = :id");
		query.setParameter("id", users.getId());
		query.setParameter("password_activation_code_expiry", updatedAt);
		query.setParameter("password_activation_code", password_activation_code);
		int result = query.executeUpdate();
		log.info("Result" + result);

		try {
			processMailUtility.sentMail(users.getName(), password_activation_code, users.getUsername(),
					users.getRoleType(), configParams[3]);
		} catch (Exception e) {
			log.info("Into catch sendEmail()");
			throw new MailNotSendException("Issue with SMTP connection, Mail Cannot Be Sent");
		}
		log.debug("Updated Users table Successfully...");

		tx.commit();
		session.close();

		return "Success";
	}

	@Override
	public String userValidation(String username, String activationCode) {
		String result = null;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		/*
		 * Criteria criteria = session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("username", username));
		 * criteria.add(Restrictions.eq("activationCode", activationCode));
		 * Users users = (Users) criteria.uniqueResult(); if (users != null) {
		 * if (users.getUserActive().equalsIgnoreCase("n")) {
		 * users.setUserActive("y"); session.saveOrUpdate(users);
		 * session.flush();
		 * 
		 * session.close(); return "Successfully Activated."; } else { return
		 * "Account already activated."; } } else { return
		 * "Activatin code doesn't match."; }
		 */

		String SQL_QUERY = "SELECT `user_id`, `user_active` FROM `users` WHERE `username` = :username, "
				+ "AND password_activation_code = :activationCode ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("username", username);
		query.setParameter("activationCode", activationCode);
		List<Object[]> row = query.list();

		if (row.size() > 0) {
			String userActive = "n";
			for (Object[] list : row) {
				userActive = (null != list[1]) ? list[1].toString() : "n";
			}
			if (userActive.equalsIgnoreCase("n")) {
				Query queryUpdate = session
						.createSQLQuery("UPDATE users SET user_active = 'y' WHERE username = :username");
				queryUpdate.setParameter("username", username);
				int update_result = query.executeUpdate();
				log.info("Result to update user_active:" + update_result);

				result = "Successfully Activated.";
			} else {
				result = "Account already activated.";
			}
		} else {
			result = "Activatin code doesn't match.";
		}

		tx.commit();
		session.close();

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ValidTeacherUsersWithTokenTransform> getTeacherUserTokenDetails(int eventId, String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<ValidTeacherUsersWithTokenTransform> list = null;
		String healthQuery = "SELECT u.user_id as userId, u.role_type as roleType,u.android_app_token as androidAppToken, "
				+ "u.iphone_app_token as iPhoneAppToken, s.name as studentName\r\n" + "from  users u\r\n"
				+ "LEFT JOIN class_grade AS cg ON cg.teacher_id = u.user_id \r\n"
				+ "LEFT JOIN students AS s ON s.class_grade_id = cg.class_grade_id\r\n"
				+ "LEFT JOIN device_students AS ds ON ds.student_id = s.student_id\r\n"
				+ "where u.role_type='school_teacher' AND ds.device_uuid =:uuid and ds.end_date is null "
				+ "and ((u.android_app_token is not null and u.android_app_token != '') OR "
				+ "(u.iphone_app_token is not null and u.iphone_app_token != ''))";
		Query query = session.createSQLQuery(healthQuery).addScalar("userId").addScalar("roleType")
				.addScalar("studentName").addScalar("androidAppToken").addScalar("iPhoneAppToken")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(ValidTeacherUsersWithTokenTransform.class));
		query.setParameter("uuid", uuid);
		list = query.list();
		tx.commit();
		session.close();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ValidParentUsersWithTokenTransform> getParentUserTokenDetails(int eventId, String uuid,
			String isParentDefault) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<ValidParentUsersWithTokenTransform> list = null;
		String parentQuery = null;
		if (null != isParentDefault && isParentDefault.toUpperCase().equals("NO")) {
			parentQuery = " SELECT u.user_id as userId, u.role_type as roleType,u.android_app_token as androidAppToken, "
					+ "u.iphone_app_token as iPhoneAppToken, s.name as studentName FROM users  u \r\n"
					+ "LEFT JOIN parent_kids pk on pk.user_id = u.user_id\r\n"
					+ "LEFT JOIN students s ON s.student_id = pk.student_id\r\n"
					+ "LEFT JOIN device_students ds on ds.student_id = s.student_id\r\n"
					+ "where ds.device_uuid =:uuid and ds.status = 'active' and u.role_type='parent_admin'"
					+ "and ((u.android_app_token is not null and u.android_app_token != '') OR "
					+ "(u.iphone_app_token is not null and u.iphone_app_token != ''))";
		}
		if (null != isParentDefault && isParentDefault.toUpperCase().equals("YES")) {
			parentQuery = " SELECT u.user_id as userId, u.role_type as roleType,u.android_app_token as androidAppToken, "
					+ "u.iphone_app_token as iPhoneAppToken,  s.name as studentName \r\n" + "FROM users AS u \r\n"
					+ "LEFT JOIN parent_kids pk on pk.user_id = u.user_id \r\n"
					+ "LEFT JOIN students s ON s.student_id = pk.student_id \r\n"
					+ "LEFT JOIN device_students ds ON s.student_id = ds.student_id \r\n"
					+ "LEFT JOIN devices d ON d.uuid = ds.device_uuid \r\n"
					+ "LEFT JOIN  event_subscriptions AS es ON  es.user_id = u.user_id AND es.student_id = s.student_id \r\n"
					+ "where  ds.device_uuid =:uuid and ds.status = 'active' and es.event_id =:event_id "
					+ "and ((u.android_app_token is not null and u.android_app_token != '') OR "
					+ "(u.iphone_app_token is not null and u.iphone_app_token != ''))";
		}
		Query query = session.createSQLQuery(parentQuery).addScalar("userId").addScalar("roleType")
				.addScalar("androidAppToken").addScalar("iPhoneAppToken").addScalar("studentName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(ValidParentUsersWithTokenTransform.class));
		if (isParentDefault.toUpperCase().equals("NO")) {
			query.setParameter("uuid", uuid);
		}
		if (isParentDefault.toUpperCase().equals("YES")) {
			query.setParameter("uuid", uuid);
			query.setParameter("event_id", eventId);
		}
		list = query.list();
		tx.commit();
		session.close();
		return list;
	}

	@Override
	public String getRoleType(int accountId) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		List<String> list = null;
		String roleQuery = "select distinct(role_type) from users where account_id=? and role_type='school_admin'";
		Query query = session.createSQLQuery(roleQuery);
		query.setParameter(0, accountId);
		list = query.list();
		if (list.size() > 0)
			return list.get(0).toString();
		else
			return "NO";
	}

	@Override
	public Accounts getAccountsByAccId(int accId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria accountsCriteria = session.createCriteria(Accounts.class);
		accountsCriteria.add(Restrictions.eq("accountId", accId));
		Accounts accounts = (Accounts) accountsCriteria.uniqueResult();

		tx.commit();
		session.close();
		return accounts;
	}

	@Override
	public Users getUserByAccId(int accID) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Users.class, "user");
		criteria.createAlias("user.accounts", "accounts");
		criteria.add(Restrictions.eq("accounts.accountId", accID));
		criteria.add(Restrictions.eq("user.roleType", "school_admin"));
		Users users = (Users) criteria.uniqueResult();
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public int getAccountIdByUserId(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("id", userId));
		Users users = (Users) criteria.uniqueResult();
		tx.commit();
		session.close();
		return users.getAccounts().getAccountId();
	}

	public Accounts getAccounts(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria usersCriteria = session.createCriteria(Users.class);
		usersCriteria.add(Restrictions.eq("id", userId));
		Users users = (Users) usersCriteria.uniqueResult();
		int accountId = users.getAccounts().getAccountId();

		Criteria accountsCriteria = session.createCriteria(Accounts.class);
		accountsCriteria.add(Restrictions.eq("accountId", accountId));
		Accounts accounts = (Accounts) accountsCriteria.uniqueResult();

		tx.commit();
		session.close();
		return accounts;
	}

	@Override
	public boolean isUsersExist(String username) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean isUserExist = false;

		String SQL_QUERY = "SELECT `user_id` FROM `users` WHERE `username` = :username ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("username", username);
		List<Object[]> row = query.list();

		if (row.size() > 0) {
			isUserExist = true;
		}
		tx.commit();
		session.close();
		log.info("isUserExist" + "\t" + isUserExist);
		return isUserExist;
	}

	@Override
	public boolean isEmailExist(String email) {
		Session session = sessionFactory.openSession();
		boolean isEmailExist = false;

		String usernameQuery = "SELECT email from Users ";
		Query query = session.createQuery(usernameQuery);
		List<String> usernameList = (List<String>) query.list();

		for (String emailfromDB : usernameList) {
			if (email.equals(emailfromDB)) {
				isEmailExist = true;
				break;
			}
		}
		log.info("isEmailExist" + "\t" + isEmailExist);
		session.close();
		return isEmailExist;
	}

	@Override
	public ValidParentStudentTransform checkDeviceUuidAndGetstudent_idAndAccoundId(String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "select dev.student_id as studentId,  dev.max_parents as maxParents, dev.max_guardians as maxGuardians "
				+ "from devices dev where dev.uuid =:uuid ";
		Query query = session.createSQLQuery(SQL_QUERY).addScalar("studentId").addScalar("maxParents")
				.addScalar("maxGuardians").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(ValidParentStudentTransform.class));
		;
		query.setParameter("uuid", uuid);

		List<ValidParentStudentTransform> validParentStudentTransform = (List<ValidParentStudentTransform>) query
				.list();

		tx.commit();
		session.close();
		if (validParentStudentTransform.size() > 0)
			return validParentStudentTransform.get(0);
		else
			return null;
	}

	@Override
	public ValidNewDevicePairingTransform checkDeviceUuidAndGetDeviceActive(NewDevicePairingModel newDevicePairingModel,
			int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String newDevicePairQuery = "select u.role_type as roleType, ds.status as deviceActive, d.max_parents as maxParents, d.max_guardians as maxGuardians from users u "
				+ "left join parent_kids pk on pk.user_id = u.user_id "
				+ "left join device_students ds on ds.student_id = pk.student_id "
				+ "left join devices d on d.uuid = ds.device_uuid "
				+ "left join accounts ac on ac.account_id = u.account_id "
				+ "where d.uuid = ? and ac.account_type = ? and u.user_id = ? ";

		Query query = session.createSQLQuery(newDevicePairQuery).addScalar("roleType").addScalar("deviceActive")
				.addScalar("maxParents").addScalar("maxGuardians").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(ValidNewDevicePairingTransform.class));
		;
		query.setParameter(0, newDevicePairingModel.getUuid());
		query.setParameter(1, "parent");
		query.setParameter(2, userId);

		ValidNewDevicePairingTransform validNewDevicePairingTransform = (ValidNewDevicePairingTransform) query
				.uniqueResult();

		tx.commit();
		session.close();
		return validNewDevicePairingTransform;

	}

	@Override
	public Students linkNewDevicePairing(String uuid) {
		log.debug("inside updateNewDevicePairing method");
		Session session = sessionFactory.openSession();
		Criteria deviceStudentsCriteria = session.createCriteria(DeviceStudents.class);
		deviceStudentsCriteria.add(Restrictions.eq("deviceuuid", uuid));
		deviceStudentsCriteria.add(Restrictions.eq("status", "active"));
		DeviceStudents deviceStudents = (DeviceStudents) deviceStudentsCriteria.uniqueResult();

		Criteria studentsCriteria = session.createCriteria(Students.class);
		if (deviceStudents != null && deviceStudents.getStudents() != null) {
			log.debug("inside updateNewDevicePairing method, deviceStudents NOT NULL");
			studentsCriteria.add(Restrictions.eq("studentId", deviceStudents.getStudents().getStudentId()));
		} else {
			log.debug("inside updateNewDevicePairing method, deviceStudents NULL");
			return null;
		}
		Students students = (Students) studentsCriteria.uniqueResult();

		return students;
	}

	@Override
	public boolean checkIfDeviceAlreadyLinked(int student_id) {
		log.debug("inside checkIfDeviceAlreadyLinked method");
		boolean checkFlag = false;
		Session session = sessionFactory.openSession();
		String SQL_QUERY = "select student_id as student from students where student_id = ? ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter(0, student_id);

		Integer studentId = (Integer) query.uniqueResult();
		if (studentId != null) {
			checkFlag = true;
			log.debug("checkIfDeviceAlreadyLinked() studentId >>>>>>> " + studentId);
		}
		log.debug("checkIfDeviceAlreadyLinked() ::::: checkFlag >>>>>>> " + checkFlag);
		return checkFlag;
	}

	@Override
	public String updateNewDevicePairing(Devices devices, Users user, NewDevicePairingModel newDevicePairingModel) {

		log.debug("inside updateNewDevicePairing method");

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", devices.getUuid()));
		Devices device = (Devices) criteria.uniqueResult();
		Students students = null;
		log.info("updateNewDevicePairing ::::::::::::::::::: ");

		if (user.getRoleType().equalsIgnoreCase("parent_admin")
				|| user.getRoleType().equalsIgnoreCase("parent_member")) {

			log.info("role is  :::::::::::: " + user.getRoleType());
			device = null;
			if (device != null) {
				log.info("device is already paired. Please unpair it and then try again pairing.");
				return "device is already paired. Please unpair it and then try again pairing.";
			}
			log.info("setting device as active :::::::::: ");
			session.update(device);
			tx.commit();

		}

		Transaction tx2 = session.beginTransaction();
		criteria.add(Restrictions.eq("studentId", students.getStudentId()));
		log.info("Date of Birth from model : " + newDevicePairingModel.getDob());
		log.info("Date of Birth from students: " + students.getDob());
		students.setGender(newDevicePairingModel.getGender().toUpperCase());
		students.setHeight(Float.valueOf(newDevicePairingModel.getHeight()));
		students.setWeight(Float.valueOf(newDevicePairingModel.getWeight()));
		log.info("Emergency Contact number:" + newDevicePairingModel.getEmergency_contact());
		students.setEmergencyContactNo(newDevicePairingModel.getEmergency_contact());
		students.setCreateDate(new Date());
		log.info("Emergency Contact number from Students :" + students.getEmergencyContactNo());
		session.update(students);

		tx2.commit();
		session.close();

		log.debug("Updated Device table Successfully...");

		return "API Request Success";
	}

	@Override
	public String updateDeviceUnPairing(Devices devices, Users user, NewDevicePairingModel newDevicePairingModel) {

		log.debug("inside updateNewDevicePairing method");

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Devices.class);
		criteria.add(Restrictions.eq("uuid", devices.getUuid()));
		Devices device = (Devices) criteria.uniqueResult();

		if (user.getRoleType().equalsIgnoreCase("parent_admin")) {
			log.info("user role is parent_admin ::::::::::");
			device = null;
			// if (device.getDeviceActive() == DeviceActive.n) {
			if (device == null) {
				log.info("Device is already unpaired ::::::::::");
				return "device is already unpaired.";
			}
			log.info("setting device active status to n :::::::::::::::");
			device.setUpdatedDate(new java.sql.Timestamp(new java.util.Date().getTime()));
			session.update(device);
			tx.commit();

		} else if (user.getRoleType().equalsIgnoreCase("parent_member")) {
			log.info("user role is parent_member ::::::::::");
			String SQL_QUERY = "select paired_status from member_subscriptions where device_id = ?";
			Query query = session.createSQLQuery(SQL_QUERY);
			query.setParameter(0, device.getDeviceId());

			if (query.uniqueResult() != null) {
				String pairedStatus = (String) query.uniqueResult();
				log.info("paired status ::::::::" + pairedStatus);
				if (pairedStatus.equals("no")) {
					return "device already unpaired";
				} else {
					log.info("updating member subscriptions table ::::::::::::");
					String SQL_UPDATE = "update member_subscriptions set paired_status = ?, date_updated = ? where device_id = ?";
					Query query2 = session.createSQLQuery(SQL_UPDATE);
					query2.setParameter(0, "no");
					query2.setParameter(1, new java.sql.Timestamp(new java.util.Date().getTime()));
					query2.setParameter(2, device.getDeviceId());
					int result = query2.executeUpdate();
					log.debug("Result : " + result);
					tx.commit();

				}
			}
		}

		session.close();

		log.debug("Updated Device table Successfully...");

		return "API Request Success";
	}

	@Override
	public int checkTotalPairedSetWithUsers(String uuid, String role) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String parentCountQuery = "SELECT count(pk.user_id) AS current_parent_count FROM parent_kids pk\r\n"
				+ "left join device_students ds on ds.student_id = pk.student_id\r\n"
				+ "left join users u on u.user_id = pk.user_id\r\n"
				+ "where ds.device_uuid=:uuid and ds.status='active' \r\n" + "and u.role_type=:user_role";

		Query query = session.createSQLQuery(parentCountQuery);
		query.setParameter("uuid", uuid);
		query.setParameter("user_role", role);

		BigInteger count = (BigInteger) query.uniqueResult();
		tx.commit();
		session.close();
		return count.intValue();
	}

	@Override
	public int checkTotalPairedSetForParentAdmin(int student_id) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String parentCountQuery = "select count(*) from parent_kids as pk, users where pk.user_id = users.user_id and pk.student_id = ? and users.role_type= ?";

		Query query = session.createSQLQuery(parentCountQuery);
		query.setParameter(0, student_id);
		query.setParameter(1, "parent_admin");

		BigInteger count = (BigInteger) query.uniqueResult();
		tx.commit();
		session.close();
		return count.intValue();
	}

	@Override
	public void updateStudentsTableOnDeviceUnpair(int student_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "update students set account_id = ? where student_id = ?";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter(0, null);
		query.setParameter(1, student_id);
		int result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();

	}

	@Override
	public String userRegister(Users user) {
		log.debug("inside userRegister method");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		user.setSignupActivation(StringUtility.randomStringOfLength(40));
		user.setUserActive("n");
		session.save(user);
		tx.commit();
		session.close();
		log.debug("Updated Users table Successfully...");
		return "API Request Success";
	}

	@Override
	public boolean isUserExists(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean isUserExists = false;

		String SQL_QUERY = "SELECT `user_id` FROM `users` WHERE `user_id` = :userId ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("userId", userId);
		List<Object[]> row = query.list();
		if (row.size() > 0) {
			isUserExists = true;
		}

		tx.commit();
		session.close();
		return isUserExists;
	}

	@Override
	public boolean isUsernameExists(String username) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean isUserExists = false;

		String SQL_QUERY = "SELECT `user_id` FROM `users` WHERE `username` = :username ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("username", username);
		List<Object[]> row = query.list();
		if (row.size() > 0) {
			isUserExists = true;
		}

		tx.commit();
		session.close();
		return isUserExists;
	}

	@Override
	public int getUserIdByUsername(String username) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("username", username));
		Users users = (Users) criteria.uniqueResult();
		tx.commit();
		session.close();
		if (null != users)
			return users.getId();
		else
			return 0;
	}

	@Override
	public void updateAppToken(int id, String appToken, String appType) {
		log.info("Inside updateAppToken, appToken:" + appToken + ",appType: " + appType);
		Query query = null;
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();

		if (null != appType && appType.equals("iphone")) {
			query = session.createQuery(
					"update Users as u set u.iPhoneAppToken = NULL WHERE u.iPhoneAppToken=:iPhoneAppToken");
			query.setParameter("iPhoneAppToken", appToken);
			query.executeUpdate();

			session.getTransaction().commit();
			session.getTransaction().begin();

			query = session.createQuery("update Users as u set u.iPhoneAppToken=:iPhoneAppToken WHERE u.id=:userId");
			query.setParameter("iPhoneAppToken", appToken);
			query.setParameter("userId", id);
			int result = query.executeUpdate();
			log.info("updateAppToken Result for iPhoneAppToken:" + result);
		} else if (null != appType && appType.equals("android")) {
			query = session.createQuery(
					"update Users as u set u.androidAppToken = NULL WHERE u.androidAppToken=:androidAppToken");
			query.setParameter("androidAppToken", appToken);
			query.executeUpdate();

			session.getTransaction().commit();
			session.getTransaction().begin();

			query = session.createQuery("update Users as u set u.androidAppToken=:androidAppToken WHERE u.id=:userId");
			query.setParameter("androidAppToken", appToken);
			query.setParameter("userId", id);
			int result = query.executeUpdate();
			log.info("updateAppToken Result for androidAppToken:" + result);
		}
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void adduserSessionByID(int id, String sessionId, Date lastlogindate) {
		Integer[] configParams = this.systemConfigurationDAO.findSystemConfigurationParameters(1);
		Query query = null;
		log.info("id" + "\t" + id);
		log.info("configParams[1] Value" + "\t" + configParams[1]);
		log.info("sessionId" + sessionId);
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		Timestamp updatedAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
		updatedAt.setTime(updatedAt.getTime() + (((configParams[1] * 60) + configParams[1]) * 1000));

		// Additional checks added to remove already existing Session ID if
		// previous user not logged out and trying to login as new user.
		query = session.createSQLQuery("SELECT user_id FROM users WHERE session_id = :sessionId");
		query.setParameter("sessionId", sessionId);
		Integer user_id = (Integer) query.uniqueResult();
		if (null != user_id && user_id > 0) {
			query = session
					.createQuery("update Users as u set u.sessionId=NULL, u.sessionExpiry =NULL where u.id=:userId");
			query.setParameter("userId", user_id);
			int result = query.executeUpdate();
			log.info("adduserSessionByID, set previous session to empty:" + result);
		}

		query = session.createQuery("update Users as u set u.sessionId=:sessionId ,u.sessionExpiry =:sessionExpiry, "
				+ "u.lastLogin=:lastlogindate where u.id=:userId");
		query.setParameter("sessionId", sessionId);
		query.setParameter("sessionExpiry", updatedAt);
		query.setParameter("lastlogindate", lastlogindate);
		query.setParameter("userId", id);
		int result = query.executeUpdate();
		log.info("adduserSessionByID, Result" + result);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public Users getUserBySessionId(String sessionId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = " from Users where sessionId = :sessionId";
		Query query = session.createQuery(SQL_QUERY);
		query.setParameter("sessionId", sessionId);
		List list = query.list();
		Users users = null;
		if (list != null && (list.size() > 0)) {
			users = (Users) list.get(0);
		}
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public void resetSessionDetails(String sessionId) {
		log.info("sessionId" + "\t" + sessionId);
		String curSessionId = null;
		Date sessionExpiry = null;
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		Query query = session.createQuery(
				"update Users as u set u.sessionId=:curSessionId ,u.sessionExpiry =:sessionExpiry where u.sessionId=:sessionId");
		query.setParameter("curSessionId", curSessionId);
		query.setParameter("sessionExpiry", sessionExpiry);
		query.setParameter("sessionId", sessionId);
		int result = query.executeUpdate();
		log.info("Result" + result);
		session.getTransaction().commit();
		session.close();

	}

	@Override
	public Accounts getAccountByUserId(int userId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("id", userId));
		Users users = (Users) criteria.uniqueResult();
		tx.commit();
		session.close();
		return users.getAccounts();
	}

	@Override
	public List<KidsListForParentMemberTransform> getKidsForParentMember(int userId, int memberId, int student_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<KidsListForParentMemberTransform> list = null;
		String kidsQuery = null;
		Query query = null;
		String studentSearch = "";
		if (student_id > 0) {
			studentSearch = "AND pk.student_id = " + student_id;
		}

		kidsQuery = "SELECT ds.device_uuid as uuId,kid.student_id as studentId, kid.nickname as studentnickName, "
				+ "member.user_id as memberuserId, member.name as memberName, es.event_id as eventId , se.event_name as eventName "
				+ "FROM users AS admin, users AS member " + "LEFT JOIN parent_kids as pk ON pk.user_id=member.user_id "
				+ "LEFT JOIN students AS kid ON kid.student_id = pk.student_id "
				+ "LEFT JOIN device_students AS ds ON ds.student_id = kid.student_id AND ds.status = 'active' "
				+ "LEFT JOIN event_subscriptions AS es ON member.user_id = es.user_id AND es.student_id = kid.student_id "
				+ "LEFT JOIN supported_events se ON se.event_id= es.event_id "
				+ "WHERE admin.account_id = member.account_id " + "AND admin.user_id =:user_id "
				+ "AND member.role_type = 'parent_member' and es.student_id =:student_id and  es.user_id =:member_id "
				+ studentSearch;
		log.info("kidsQuery" + kidsQuery);

		query = session.createSQLQuery(kidsQuery).addScalar("studentId").addScalar("uuId").addScalar("studentnickName")
				.addScalar("memberuserId").addScalar("memberName").addScalar("eventId").addScalar("eventName")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(KidsListForParentMemberTransform.class));
		query.setParameter("user_id", userId);
		query.setParameter("student_id", student_id);
		query.setParameter("member_id", memberId);

		log.info("kidsQuery" + kidsQuery);
		list = (List<KidsListForParentMemberTransform>) query.list();
		tx.commit();
		session.close();
		return list;
	}

	@Override
	public List<Timetable> listOfTimeTable() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Timetable.class);
		List<Timetable> list = criteria.list();

		if (list.isEmpty()) {
			return null;
		}
		session.close();
		return list;
	}

	@Override
	public void updateMemberInfo(int subscriptionId, String SosAlert, String entryexitalert, String bandRemovealert) {
		log.info("Entry :" + entryexitalert);
		log.info("sos :" + SosAlert);
		log.info("band :" + bandRemovealert);
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		Query query = session.createQuery(
				"update MemberSubscriptions ms set ms.entryExitAlert=:entryexitalert,ms.bandRemoveAlert=:bandremovealert,ms.sosAlert=:SOS_alert where ms.subscriptionId=:subId");
		log.info("query" + query);
		query.setParameter("entryexitalert", entryexitalert);
		query.setParameter("bandremovealert", bandRemovealert);
		query.setParameter("SOS_alert", SosAlert);
		query.setParameter("subId", subscriptionId);
		int result = query.executeUpdate();
		log.info("Result" + result);
		session.getTransaction().commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getsubscriberListForParentAdmin(int account_id) {
		List<Integer> subscriberList = new ArrayList<Integer>();
		Session session = sessionFactory.openSession();
		Transaction selectQueryForSubscriberTrxn = session.beginTransaction();
		String selectQueryForSubscriber = "select u.id from Users as u  where u.accounts.accountId =:account_id  and u.roleType='parent_member'";
		Query selectQueryForSubscriberList = session.createQuery(selectQueryForSubscriber);
		selectQueryForSubscriberList.setParameter("account_id", account_id);
		log.info("select Query " + selectQueryForSubscriberList);
		subscriberList = selectQueryForSubscriberList.list();
		if (subscriberList.size() > 0)
			log.info("list size " + subscriberList.size());
		else
			log.info("empty list");
		selectQueryForSubscriberTrxn.commit();
		session.close();

		return subscriberList;
	}

	@Override
	public int updateMemberInfofromApi(int memberId, int Alertid, String alertValue) {
		log.info("memberId :" + memberId);
		log.info("Alertid :" + Alertid);
		log.info("alertValue :" + alertValue);
		String toUpdatealert = null;

		if (Alertid == 1)
			toUpdatealert = "entryExitAlert";
		if (Alertid == 6)
			toUpdatealert = "sosAlert";
		if (Alertid == 19)
			toUpdatealert = "bandRemoveAlert";

		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		Query query = session.createQuery("update MemberSubscriptions ms set ms." + toUpdatealert
				+ "=:alertValue where ms.subscriptionId=:subId");
		log.info("query" + query);
		query.setParameter("alertValue", alertValue);
		query.setParameter("subId", memberId);
		int result = query.executeUpdate();
		log.info("Result" + result);
		session.getTransaction().commit();
		session.close();
		return result;
	}

	public AccountsTransform checkAccountIdExist(Integer deviceId) {
		log.debug("inside updateNewDevicePairing method");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String accountIdQuery = "select s.account_id as accountId from students s "
				+ " left join devices d on d.student_id = s.student_id where d.device_id=?";
		AccountsTransform accountsTransform = null;
		Query query = session.createSQLQuery(accountIdQuery).addScalar("accountId")
				.setResultTransformer(Transformers.aliasToBean(AccountsTransform.class));
		query.setParameter(0, deviceId);
		accountsTransform = (AccountsTransform) query.uniqueResult();
		tx.commit();
		session.close();
		return accountsTransform;

	}

	@Override
	public AccountsTransform checkSessionAccountId(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String accountIdQuery = "select u.account_id as accountId from users u " + " where u.user_id=?";
		AccountsTransform accountsTransform = null;
		Query query = session.createSQLQuery(accountIdQuery).addScalar("accountId")
				.setResultTransformer(Transformers.aliasToBean(AccountsTransform.class));
		query.setParameter(0, userId);
		accountsTransform = (AccountsTransform) query.uniqueResult();
		tx.commit();
		session.close();
		return accountsTransform;
	}

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int userId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		log.info("userId :::::::::::::::::::::::::::::::::::::::::::::: " + userId);
		String selectStudentDetails = "select s.name as studentName, s.student_id as studentId, s.class as studentClass  from students s "
				+ "left join users u on u.account_id = s.school_id " + "where u.user_id= ? ";
		List<StudentsListTransform> studentsDetailsTransform = null;
		log.info("selectStudentDetails  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + selectStudentDetails);
		Query query = session.createSQLQuery(selectStudentDetails).addScalar("studentName").addScalar("studentId")
				.addScalar("studentClass").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));

		query.setParameter(0, userId);
		studentsDetailsTransform = (List<StudentsListTransform>) query.list();
		log.info("studentsDetailsTransform.size()" + "\t" + studentsDetailsTransform.size());
		log.info("UserDaoImpl >>>>>>>>>>>>>> studentsDetailsTransform :::::::::::::::: "
				+ studentsDetailsTransform.toString());
		tx.commit();
		session.close();
		return studentsDetailsTransform;
	}

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int accountId, String studentClass) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		log.info("accountId : " + accountId);
		log.info("studentClass : " + studentClass);
		String selectStudentDetails = null;
		Query query = null;
		if (studentClass == null) {
			log.debug("getStudentDetailsFromStudent() has null value ");
			selectStudentDetails = "select name as studentName, student_id as studentId from students where school_id = ?";
			query = session.createSQLQuery(selectStudentDetails).addScalar("studentName").addScalar("studentId")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));

			query.setParameter(0, accountId);

		} else {
			selectStudentDetails = "select name as studentName, student_id as studentId from students where school_id = ? and class = ?";
			query = session.createSQLQuery(selectStudentDetails).addScalar("studentName").addScalar("studentId")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));

			query.setParameter(0, accountId);
			query.setParameter(1, studentClass);
		}

		List<StudentsListTransform> studentsDetailsTransform = (List<StudentsListTransform>) query.list();
		tx.commit();
		session.close();
		return studentsDetailsTransform;
	}

	@Override
	public Users findUserByActivationCode(String key) {
		/*
		 * Session session = sessionFactory.openSession(); Transaction tx =
		 * session.beginTransaction(); Criteria criteria =
		 * session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("activationCode", key)); Users users =
		 * (Users) criteria.uniqueResult(); tx.commit(); session.close(); return
		 * users;
		 */
		log.info("entered findUserByActivationCode");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "SELECT `user_id`, `account_id`, `role_type`, `name`, `username`, `password`, "
				+ "`openid_username`, `user_active`, `mobile_number`, `android_app_token`, `iphone_app_token`, "
				+ "`mobile_session_id`, `session_id`, `session_expiry`, `lastlogin_date`, `created_date`, "
				+ "`updated_date`, `password_activation_code_expiry` FROM `users` WHERE password_activation_code = :key ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("key", key);
		List<Object[]> row = query.list();
		Users users = null;
		for (Object[] list : row) {
			users = new Users();
			if (null != list[2])
				users.setRoleType(list[2].toString());
			if (null != list[3])
				users.setName(list[3].toString());
			if (null != list[4])
				users.setUsername(list[4].toString());
			if (null != list[5])
				users.setPassword(list[5].toString());
			if (null != list[6])
				users.setOpenidUsername(list[6].toString());
			if (null != list[7])
				users.setUserActive(list[7].toString());
			if (null != list[8])
				users.setMobileNumber(list[8].toString());
			if (null != list[9])
				users.setAndroidAppToken(list[9].toString());
			if (null != list[10])
				users.setiPhoneAppToken(list[10].toString());
			if (null != list[11])
				users.setMobileSessionId(list[11].toString());
			if (null != list[12])
				users.setSessionId(list[12].toString());
			
			try {
				if (null != list[0])
					users.setId(Integer.parseInt(list[0].toString()));
				if (null != list[13])
					users.setSessionExpiry((Date) list[13]);
				if (null != list[17])
					users.setActivationCodeExpiry((Date)list[17]);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			} catch (Exception pe) {
				pe.printStackTrace();
			}
		}
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public List<GuardiansDetailsListTransform> getGuardiansDetailsList(int accountId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String role_type = "parent_member";
		String guadiansListQuery = "select user_id as user_id, name AS guadianName, username as userName,role_type as role_type,mobile_number as mobile_number,user_active as status  "
				+ "from users where account_id = ? and role_type = ?";
		List<GuardiansDetailsListTransform> guardiansDetailsList = null;

		Query query = session.createSQLQuery(guadiansListQuery).addScalar("user_id").addScalar("userName")
				.addScalar("guadianName").addScalar("role_type").addScalar("mobile_number").addScalar("status")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(GuardiansDetailsListTransform.class));

		query.setParameter(0, accountId);
		query.setParameter(1, role_type);
		guardiansDetailsList = query.list();

		log.info("return guardiansDetailsList : " + guardiansDetailsList);

		tx.commit();
		session.close();
		return guardiansDetailsList;

	}

	@Override
	public boolean findUserActivationKey(String password_activation_code) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		boolean userExists = false;
		/*
		 * Criteria criteria = session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("activationCode", key));
		 * 
		 * Users user = (Users) criteria.uniqueResult();
		 * 
		 * if (user != null) userExists = true;
		 */

		String SQL_QUERY = "SELECT `user_id` FROM `users` WHERE `password_activation_code` = :password_activation_code ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("password_activation_code", password_activation_code);
		List<Object[]> row = query.list();
		if (row.size() > 0) {
			userExists = true;
		}

		tx.commit();
		session.close();

		return userExists;
	}

	@Override
	public boolean checkRoleForUser(String username, String role) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		boolean userExists = false;
		/*
		 * Criteria criteria = session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("username", username));
		 * criteria.add(Restrictions.eq("roleType", role));
		 * 
		 * Users user = (Users) criteria.uniqueResult();
		 * 
		 * if (user != null) userExists = true;
		 */

		String SQL_QUERY = "SELECT `user_id` FROM `users` WHERE `username` = :username AND role_type = :role ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("username", username);
		query.setParameter("role", role);
		List<Object[]> row = query.list();
		if (row.size() > 0) {
			userExists = true;
		}

		tx.commit();
		session.close();

		return userExists;
	}

	@Override
	public void updateSignupKey(Users users) {
		log.info("updateSignupKey");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*
		 * users.setSignupActivation(""); users.setUserActive("y");
		 * session.update(users);
		 */

		String SQL_UPDATE = "update users set signup_activation_code=signup_activation_code, "
				+ "user_active = 'y' where user_id=:user_id";
		Query query = session.createSQLQuery(SQL_UPDATE);
		query.setParameter("user_id", users.getId());
		query.executeUpdate();

		tx.commit();
		session.close();
	}

	@Override
	public Users findBySignUpActivationCode(String key) {
		log.info("findBySignUpActivationCode");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_QUERY = "SELECT `user_id`, `account_id`, `role_type`, `name`, `username`, `password`, "
				+ "`openid_username`, `user_active`, `mobile_number`, `android_app_token`, `iphone_app_token`, "
				+ "`mobile_session_id`, `session_id`, `session_expiry`, `lastlogin_date`, `created_date`, "
				+ "`updated_date` FROM `users` WHERE signup_activation_code =:signup_activation_code "
				+ "AND user_active = 'n'";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("signup_activation_code", key);
		List<Object[]> row = query.list();
		Users users = null;
		for (Object[] list : row) {
			users = new Users();
			if (null != list[2])
				users.setRoleType(list[2].toString());
			if (null != list[3])
				users.setName(list[3].toString());
			if (null != list[4])
				users.setUsername(list[4].toString());
			if (null != list[5])
				users.setPassword(list[5].toString());
			if (null != list[6])
				users.setOpenidUsername(list[6].toString());
			if (null != list[7])
				users.setUserActive(list[7].toString());
			if (null != list[8])
				users.setMobileNumber(list[8].toString());
			if (null != list[9])
				users.setAndroidAppToken(list[9].toString());
			if (null != list[10])
				users.setiPhoneAppToken(list[10].toString());
			if (null != list[11])
				users.setMobileSessionId(list[11].toString());
			if (null != list[12])
				users.setSessionId(list[12].toString());
			try {
				if (null != list[0])
					users.setId(Integer.parseInt(list[0].toString()));
				if (null != list[1])
					users.setAccountId(Integer.parseInt(list[1].toString()));
				if (null != list[13])
					users.setSessionExpiry((Date) list[13]);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			} catch (Exception pe) {
				pe.printStackTrace();
			}
		}

		tx.commit();
		session.close();
		return users;
	}

	@Override
	public Users findByPasswordActivationCode(String key) {
		log.info("Into findByPasswordActivationCode(){");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("activationCode", key));
		Users users = (Users) criteria.uniqueResult();
		tx.commit();
		session.close();
		log.info("Exiting findByPasswordActivationCode()}");
		return users;
	}

	@Override
	public int getAccountIdFromStudentId(int student_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "select stu.account_id from students stu where stu.student_id=:student_id";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("student_id", student_id);

		Integer account_id = (Integer) query.uniqueResult();

		tx.commit();
		session.close();
		if (null != account_id)
			return account_id;
		else
			return -1;
	}

	@Override
	public int getCurrentParentCount(int account_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "select count(account_id) as count from users where account_id=:account_id and role_type='parent_admin'";
		Query query = session.createSQLQuery(SQL_QUERY).addScalar("count", IntegerType.INSTANCE);
		;
		query.setParameter("account_id", account_id);

		int currentCount = (Integer) query.uniqueResult();

		tx.commit();
		session.close();

		return currentCount;

	}

	@Override
	public void updateUserWithAccountId(String userName, int account_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_UPDATE = "update users set account_id=:account_id where username=:username";
		Query query = session.createSQLQuery(SQL_UPDATE);
		query.setParameter("account_id", account_id);
		query.setParameter("username", userName);
		int result = query.executeUpdate();
		log.debug("Result : " + result);
		tx.commit();
		session.close();
	}

	@Override
	public void createNewAccount(Accounts account) {
		// TODOAuto-generated method stub
		log.debug("inside accountRegister method");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.save(account);
		tx.commit();
		session.close();
		log.debug("Updated Account table Successfully...");

	}

	@Override
	public int getLatestAccountId() {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT MAX(account_id) FROM accounts";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		sessions = (List<Integer>) query.list();
		session.close();
		if (sessions.size() > 0)
			return sessions.get(0);
		else
			return 0;
	}

	@Override
	public void updateStudentWithAccountId(int student_id, int account_id) {
		// s
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_UPDATE = "update students set account_id=:account_id where student_id=:student_id";
		Query query = session.createSQLQuery(SQL_UPDATE);
		query.setParameter("account_id", account_id);
		query.setParameter("student_id", student_id);
		int result = query.executeUpdate();
		log.debug("Result : " + result);
		tx.commit();
		session.close();
	}

	@Override
	public void addMobileUserSessionByID(int id, String sessionId, Date lastlogindate) {

		// TODO Auto-generated method stub
		log.info("id" + "\t" + id);
		log.info("sessionId" + sessionId);
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		// Timestamp updatedAt = new
		// Timestamp(Calendar.getInstance().getTimeInMillis());
		// updatedAt.setTime(updatedAt.getTime() + (((59 * 60) + 59) * 1000));
		Query query = session.createQuery(
				"update Users as u set u.mobileSessionId=:sessionId , u.lastLogin=:lastlogindate where u.id=:userId");
		query.setParameter("sessionId", sessionId);
		// query.setParameter("sessionExpiry", updatedAt);
		query.setParameter("lastlogindate", lastlogindate);
		query.setParameter("userId", id);
		int result = query.executeUpdate();
		log.info("Result" + result);
		session.getTransaction().commit();
		session.close();

	}

	@Override
	public Users getUserByMobileSessionId(String sessionId) {
		// TODO Auto-generated method stub
		log.info("entered here 1");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*
		 * Criteria criteria = session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("mobileSessionId", sessionId)); Users
		 * users = (Users) criteria.uniqueResult();
		 */
		String SQL_QUERY = " from Users where mobileSessionId = :sessionId";
		Query query = session.createQuery(SQL_QUERY);
		query.setParameter("sessionId", sessionId);
		List list = query.list();
		Users users = null;
		if (list != null && (list.size() > 0)) {
			users = (Users) list.get(0);
		}
		tx.commit();
		session.close();
		log.info("entered here 2");
		return users;
	}

	@Override
	public void resetMobileSessionDetails(String sessionId) {
		// TODO Auto-generated method stub
		log.info("sessionId" + "\t" + sessionId);
		String curSessionId = null;
		Date sessionExpiry = null;
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		Query query = session.createQuery(
				"update Users as u set u.mobileSessionId=:curSessionId where u.mobileSessionId=:sessionId");
		query.setParameter("curSessionId", curSessionId);
		// query.setParameter("sessionExpiry", sessionExpiry);
		query.setParameter("sessionId", sessionId);
		int result = query.executeUpdate();
		log.info("Result" + result);
		session.getTransaction().commit();
		session.close();

	}

	@Override
	public boolean updateFederatedLogin(String sesionId, String profileName, String email, String userAgent) {
		// TODO Auto-generated method stub
		Integer[] configParams = this.systemConfigurationDAO.findSystemConfigurationParameters(1);
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Timestamp updatedAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
		updatedAt.setTime(updatedAt.getTime() + (((configParams[1] * 60) + configParams[1]) * 1000));
		boolean updateLogin = false;
		/*
		 * Criteria criteria = session.createCriteria(Users.class);
		 * criteria.add(Restrictions.eq("username", email)); Users user =
		 * (Users) criteria.uniqueResult();
		 */
		String SQL_UPDATE = null;
		Query query = null;
		if (userAgent.toUpperCase().equals("MOBILE")) {
			/*
			 * user.setName(profileName); user.setMobileSessionId(sesionId);
			 * user.setOpenidUsername(email);
			 */
			SQL_UPDATE = "update users set name = :name, mobile_session_id = :mobile_session_id, username = :username where username=:username";
			query = session.createSQLQuery(SQL_UPDATE);
			query.setParameter("name", profileName);
			query.setParameter("username", email);
			query.setParameter("mobile_session_id", sesionId);
		} else if (userAgent.toUpperCase().equals("WEB")) {
			/*
			 * user.setName(profileName); user.setSessionId(sesionId);
			 * user.setSessionExpiry(updatedAt); user.setOpenidUsername(email);
			 */
			SQL_UPDATE = "update users set name = :name, session_id = :session_id, session_expiry = :session_expiry, username = :username where username=:username";
			query = session.createSQLQuery(SQL_UPDATE);
			query.setParameter("name", profileName);
			query.setParameter("username", email);
			query.setParameter("session_id", sesionId);
			query.setParameter("session_expiry", updatedAt);
		}
		// session.update(user);
		int result = query.executeUpdate();
		log.debug("Result : " + result);
		if (result > 0) {
			updateLogin = true;
		}
		tx.commit();
		session.close();

		return updateLogin;
	}

	@Override
	public boolean updateOpenId(String sesionId, String profileName, String email, String userAgent) {
		Integer[] configParams = this.systemConfigurationDAO.findSystemConfigurationParameters(1);
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Timestamp updatedAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
		updatedAt.setTime(updatedAt.getTime() + (((configParams[1] * 60) + configParams[1]) * 1000));

		boolean updateLogin = false;
		String SQL_UPDATE = null;
		Query query = null;
		if (userAgent.toUpperCase().equals("MOBILE")) {
			SQL_UPDATE = "update users set mobile_session_id = :mobile_session_id where openid_username=:username";
			query = session.createSQLQuery(SQL_UPDATE);
			// query.setParameter("name", profileName); //name = :name,
			query.setParameter("username", email);
			query.setParameter("mobile_session_id", sesionId);
		} else if (userAgent.toUpperCase().equals("WEB")) {
			SQL_UPDATE = "update users set session_id = :session_id, session_expiry = :session_expiry where openid_username=:username";
			query = session.createSQLQuery(SQL_UPDATE);
			// query.setParameter("name", profileName); //name = :name,
			query.setParameter("username", email);
			query.setParameter("session_id", sesionId);
			query.setParameter("session_expiry", updatedAt);
		}
		int result = query.executeUpdate();
		log.debug("Result : " + result);
		if (result > 0) {
			updateLogin = true;
		}

		tx.commit();
		session.close();

		return updateLogin;
	}

	@Override
	public boolean insertFederatedLogin(String sessionId, String profileName, String email, String userAgent) {
		Integer[] configParams = this.systemConfigurationDAO.findSystemConfigurationParameters(1);
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean insertLogin = false;

		Timestamp updatedAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
		updatedAt.setTime(updatedAt.getTime() + (((configParams[1] * 60) + configParams[1]) * 1000));

		Accounts accounts = new Accounts();
		accounts.setAccountName("Parent Account");
		accounts.setAccountType("parent");
		accounts.setCreatedDate(new Date());
		accounts.setAccountActive("y");
		session.save(accounts);

		Users user = new Users();
		user.setAccounts(accounts);
		user.setUserActive("y");
		user.setRoleType("parent_admin");
		user.setUsername(null);
		user.setOpenidUsername(email);
		user.setName(profileName);

		if (userAgent.toUpperCase().equals("MOBILE")) {
			user.setMobileSessionId(sessionId);
		} else if (userAgent.toUpperCase().equals("WEB")) {
			user.setSessionId(sessionId);
			user.setSessionExpiry(updatedAt);
		}
		session.save(user);
		tx.commit();

		insertLogin = true;
		session.close();

		return insertLogin;
	}

	@Override
	public boolean openIdExist(String openId) {

		log.info("entered into  openIdExist" + "\t");
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		log.info("entered into  openIdExist1" + "\t");
		Transaction tx = session.beginTransaction();
		log.info("entered into  openIdExist2" + "\t");
		boolean isUserExist = false;

		String usernameQuery = "SELECT openidUsername from Users";
		log.info("entered into  openIdExist3" + "\t");
		Query query = session.createQuery(usernameQuery);
		log.info("before isUserExist" + "\t" + isUserExist);
		List<String> usernameList = (List<String>) query.list();

		log.info("after isUserExist" + "\t" + isUserExist);
		usernameList.removeAll(Collections.singleton(null));

		for (String uname : usernameList) {
			if ((uname != null || uname.trim().length() > 0) && (uname.equals(openId))) {
				isUserExist = true;
				break;
			}
		}
		log.info("isUserExist" + "\t" + isUserExist);
		session.close();
		return isUserExist;
	}

	public boolean unpairParentDevices(String uuid, int user_id) {
		log.info("Entered unpairParentDevices");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean unpairStatus = false;

		String usersQry = "select user_id from users where account_id = (select account_id from users where user_id = ?)";

		Query usersCreateQry = session.createSQLQuery(usersQry);
		usersCreateQry.setParameter(0, user_id);

		List<Integer> usersList = (List<Integer>) usersCreateQry.list();

		for (int userId : usersList) {
			String unpairQry = "delete from parent_kids where user_id = " + userId + "  AND student_id in "
					+ "(select student_id from device_students where device_uuid =:uuid AND status = 'active' )";
			Query unpairCreateQry = session.createSQLQuery(unpairQry);
			unpairCreateQry.setParameter("uuid", uuid);
			int i = unpairCreateQry.executeUpdate();
			if (i > 0) {
				unpairStatus = true;

				// Delete Event Subscriptions
				String sEventSubscriptions = "delete from event_subscriptions where user_id = " + userId
						+ "  AND student_id in "
						+ "(select student_id from device_students where device_uuid =:uuid AND status = 'active' )";
				Query qEventSubscriptions = session.createSQLQuery(sEventSubscriptions);
				qEventSubscriptions.setParameter("uuid", uuid);
				qEventSubscriptions.executeUpdate();
			}
		}
		log.info("Exiting unpairParentDevices");

		tx.commit();
		session.close();
		return unpairStatus;
	}

	@Override
	public List<ValidTeacherUsersWithTokenTransform> getStaffUserTokenDetails(int eventId, String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<ValidTeacherUsersWithTokenTransform> list = null;
		String healthQuery = "SELECT u.user_id as userId, u.role_type as roleType,u.android_app_token as androidAppToken, "
				+ "u.iphone_app_token as iPhoneAppToken, s.name as studentName "
				+ "from  users u LEFT JOIN class_grade AS cg ON  u.account_id = cg.school_id "
				+ "LEFT JOIN students AS s ON s.class_grade_id = cg.class_grade_id "
				+ "LEFT JOIN device_students AS ds ON ds.student_id = s.student_id "
				+ "where u.role_type='school_staff' AND ds.device_uuid =:uuid and ds.end_date is null "
				+ "and ((u.android_app_token is not null and u.android_app_token != '') OR "
				+ "(u.iphone_app_token is not null and u.iphone_app_token != '')) ";
		Query query = session.createSQLQuery(healthQuery).addScalar("userId").addScalar("roleType")
				.addScalar("studentName").addScalar("androidAppToken").addScalar("iPhoneAppToken")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(ValidTeacherUsersWithTokenTransform.class));
		query.setParameter("uuid", uuid);
		list = query.list();
		tx.commit();
		session.close();
		return list;
	}

	@Override
	public Users findUsers(String username) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("username", username));
		Users users = (Users) criteria.uniqueResult();
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public Users findUsersBySchoolId(String username, Integer schoolId) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Users.class, "u");
		criteria.createAlias("u.accounts", "acc");
		criteria.add(Restrictions.eq("username", username));
		criteria.add(Restrictions.eq("acc.accountId", schoolId));
		Users users = (Users) criteria.uniqueResult();
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public Users getOpenIdUserDetails(String userName) {
		// TODO Auto-generated method stub
		Users users = null;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			String SQL_QUERY = " from Users where openid_username = :username";
			Query query = session.createQuery(SQL_QUERY);
			query.setParameter("username", userName);
			List list = query.list();

			if (list != null && (list.size() > 0)) {
				users = (Users) list.get(0);
			}
			return users;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			session.close();
		}
	}

	@Override
	public String sendUserActivationEmail(String name, String passwordActivationCode, String username) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		try {
			processMailUtility.userActivationByEmail(name, passwordActivationCode, username);
		} catch (Exception e) {
			log.info("Into catch sendEmail()");
			throw new MailNotSendException("Issue with SMTP connection, Mail Cannot Be Sent");
		}
		log.debug("Updated Users table Successfully...");
		tx.commit();
		session.close();
		return "Success";
	}

	@Override
	public Users findUserById(int userId) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		Users users = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(Users.class);
			criteria.add(Restrictions.eq("id", userId));
			users = (Users) criteria.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			log.info("Exception in findUserById" + "\t" + e);
			session.evict(users);
			tx.rollback();
		} finally {
			session.close();
		}
		return users;
	}

	@Override
	public Users validateUserBySessionId(String sessionID) {
		log.info("entered validateUserBySessionId");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = " FROM Users WHERE (sessionId = :sessionId OR mobileSessionId = :sessionId) AND userActive = 'y'";
		Query query = session.createQuery(SQL_QUERY);
		query.setParameter("sessionId", sessionID);
		List list = query.list();
		Users users = null;
		if (list != null && (list.size() > 0)) {
			users = (Users) list.get(0);
		}
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public Users validateUserBySession(String sessionID) {
		log.info("entered validateUserBySession");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "SELECT `user_id`, `account_id`, `role_type`, `name`, `username`, `password`, "
				+ "`openid_username`, `user_active`, `mobile_number`, `android_app_token`, `iphone_app_token`, "
				+ "`mobile_session_id`, `session_id`, `session_expiry`, `lastlogin_date`, `created_date`, "
				+ "`updated_date` FROM `users` WHERE (session_id = :sessionId OR mobile_session_id = :sessionId) "
				+ "AND user_active = 'y'";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("sessionId", sessionID);
		List<Object[]> row = query.list();
		Users users = null;
		for (Object[] list : row) {
			users = new Users();
			if (null != list[2])
				users.setRoleType(list[2].toString());
			if (null != list[3])
				users.setName(list[3].toString());
			if (null != list[4])
				users.setUsername(list[4].toString());
			if (null != list[5])
				users.setPassword(list[5].toString());
			if (null != list[6])
				users.setOpenidUsername(list[6].toString());
			if (null != list[7])
				users.setUserActive(list[7].toString());
			if (null != list[8])
				users.setMobileNumber(list[8].toString());
			if (null != list[9])
				users.setAndroidAppToken(list[9].toString());
			if (null != list[10])
				users.setiPhoneAppToken(list[10].toString());
			if (null != list[11] && sessionID.equals(list[11]))
				users.setMobileSessionId(list[11].toString());
			if (null != list[12] && sessionID.equals(list[12]))
				users.setSessionId(list[12].toString());
			try {
				if (null != list[0])
					users.setId(Integer.parseInt(list[0].toString()));
				if (null != list[1])
					users.setAccountId(Integer.parseInt(list[1].toString()));
				if (null != list[13])
					users.setSessionExpiry((Date) list[13]);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			} catch (Exception pe) {
				pe.printStackTrace();
			}
		}
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public Users validateUserByUsername(String username) {
		log.info("entered validateUserBySession");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "SELECT `user_id`, `account_id`, `role_type`, `name`, `username`, `password`, "
				+ "`openid_username`, `user_active`, `mobile_number`, `android_app_token`, `iphone_app_token`, "
				+ "`mobile_session_id`, `session_id`, `session_expiry`, `lastlogin_date`, `created_date`, "
				+ "`updated_date` FROM `users` WHERE username = :username AND user_active = 'y'";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("username", username);
		List<Object[]> row = query.list();
		Users users = null;
		for (Object[] list : row) {
			users = new Users();
			if (null != list[2])
				users.setRoleType(list[2].toString());
			if (null != list[3])
				users.setName(list[3].toString());
			if (null != list[4])
				users.setUsername(list[4].toString());
			if (null != list[5])
				users.setPassword(list[5].toString());
			if (null != list[6])
				users.setOpenidUsername(list[6].toString());
			if (null != list[7])
				users.setUserActive(list[7].toString());
			if (null != list[8])
				users.setMobileNumber(list[8].toString());
			if (null != list[9])
				users.setAndroidAppToken(list[9].toString());
			if (null != list[10])
				users.setiPhoneAppToken(list[10].toString());
			if (null != list[11])
				users.setMobileSessionId(list[11].toString());
			if (null != list[12])
				users.setSessionId(list[12].toString());
			try {
				if (null != list[0])
					users.setId(Integer.parseInt(list[0].toString()));
				if (null != list[13])
					users.setSessionExpiry((Date) list[13]);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			} catch (Exception pe) {
				pe.printStackTrace();
			}
		}
		tx.commit();
		session.close();
		return users;
	}

	@Override
	public Users isFederatedUserExist(String username) {
		log.info("entered isUsersExist");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String SQL_QUERY = "SELECT `user_id`, `account_id`, `role_type`, `name`, `username`, `password`, "
				+ "`openid_username`, `user_active`, `mobile_number`, `android_app_token`, `iphone_app_token`, "
				+ "`mobile_session_id`, `session_id`, `session_expiry`, `lastlogin_date`, `created_date`, "
				+ "`updated_date` FROM `users` WHERE (username = :username OR openid_username = :username) ";
		Query query = session.createSQLQuery(SQL_QUERY);
		query.setParameter("username", username);
		List<Object[]> row = query.list();
		Users users = null;
		for (Object[] list : row) {
			users = new Users();
			if (null != list[2])
				users.setRoleType(list[2].toString());
			if (null != list[3])
				users.setName(list[3].toString());
			if (null != list[4])
				users.setUsername(list[4].toString());
			if (null != list[5])
				users.setPassword(list[5].toString());
			if (null != list[6])
				users.setOpenidUsername(list[6].toString());
			if (null != list[7])
				users.setUserActive(list[7].toString());
			if (null != list[8])
				users.setMobileNumber(list[8].toString());
			if (null != list[9])
				users.setAndroidAppToken(list[9].toString());
			if (null != list[10])
				users.setiPhoneAppToken(list[10].toString());
			if (null != list[11])
				users.setMobileSessionId(list[11].toString());
			if (null != list[12])
				users.setSessionId(list[12].toString());
			try {
				if (null != list[0])
					users.setId(Integer.parseInt(list[0].toString()));
				if (null != list[13])
					users.setSessionExpiry((Date) list[13]);
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
			} catch (Exception pe) {
				pe.printStackTrace();
			}
		}
		log.info("exiting isUsersExist");
		tx.commit();
		session.close();
		return users;
	}

	public void updateUserActivation(int user_id, String password, String password_activation_code,
			String password_activation_code_expiry, String user_active) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Query query = session
				.createSQLQuery("UPDATE users SET password_activation_code_expiry = :password_activation_code_expiry, "
						+ "password_activation_code = :password_activation_code, password = :password, "
						+ "user_active = :user_active, signup_activation_code = NULL WHERE user_id = :user_id");
		query.setParameter("user_id", user_id);
		query.setParameter("password_activation_code_expiry", password_activation_code_expiry);
		query.setParameter("password_activation_code", password_activation_code);
		query.setParameter("password", password);
		query.setParameter("user_active", user_active);
		query.executeUpdate();

		log.debug("Updated Users table Successfully...");

		tx.commit();
		session.close();
	}
}
