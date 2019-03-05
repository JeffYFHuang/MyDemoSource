package com.liteon.icgwearable.dao;

import java.math.BigInteger;
import java.util.Calendar;
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

import com.google.api.client.util.Data;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.DeviceConfigurations;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.DeviceConfigModel;
import com.liteon.icgwearable.model.DeviceListModel;
import com.liteon.icgwearable.model.SchoolModel;
import com.liteon.icgwearable.transform.AccountFroSchoolTransform;
import com.liteon.icgwearable.transform.AdminTransform;
import com.liteon.icgwearable.transform.DeviceAccountTransform;
import com.liteon.icgwearable.transform.DeviceConfigTransform;
import com.liteon.icgwearable.transform.DeviceConfigurationsTransform;
import com.liteon.icgwearable.transform.DeviceStatsTransform;
import com.liteon.icgwearable.transform.SchoolTransform;

@Repository("accountDAO")
public class AccountDAOImpl implements AccountDAO {

	private static Logger log = Logger.getLogger(AccountDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public Accounts createAccounts() {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Accounts accounts = new Accounts();
		accounts.setAccountType("parent");
		accounts.setAccountActive("y");
		accounts.setCreatedDate(new java.util.Date());

		session.save(accounts);
		tx.commit();
		session.close();
		return accounts;
	}

	@Override
	public Accounts createAccountsForSchoolTeacherAndStaff() {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Accounts accounts = new Accounts();
		accounts.setAccountType("school");
		accounts.setAccountActive("y");
		accounts.setCreatedDate(new java.util.Date());

		session.save(accounts);
		tx.commit();
		session.close();
		return accounts;
	}

	@Override
	public List<DeviceAccountTransform> getassignedDeviceList(int admin_account_id, int page_id, int total,
			String grade_id, String classNo) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceListQuery = null;
		List<DeviceAccountTransform> deviceTransformList = null;

		deviceListQuery = "SELECT d.uuid AS uuid,s.name AS studentName, cg.grade AS grade, "
				+ "cg.class AS studentclass, ds.start_date AS dateAssigned FROM devices d\r\n"
				+ "LEFT JOIN device_students ds ON ds.device_uuid = d.uuid \r\n"
				+ "LEFT JOIN students s ON s.student_id = ds.student_id \r\n"
				+ "LEFT JOIN class_grade cg ON cg.class_grade_id = s.class_grade_id \r\n"
				+ " WHERE d.school_id =:account_id AND d.status='assigned' AND ds.status = 'active' AND cg.grade =:grade_id and cg.class =:classNo"
				+ " and d.uuid !='' limit " + (page_id - 1) + "," + total;

		Query query = session.createSQLQuery(deviceListQuery).addScalar("uuid").addScalar("studentName")
				.addScalar("grade").addScalar("studentclass").addScalar("dateAssigned")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceAccountTransform.class));
		query.setParameter("account_id", admin_account_id);
		query.setParameter("grade_id", grade_id);
		query.setParameter("classNo", classNo);
		deviceTransformList = query.list();
		tx.commit();
		session.close();
		return deviceTransformList;
	}

	@Override
	public List<DeviceAccountTransform> getunAssignedDeviceList(int admin_account_id, int page_id, int total) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceListQuery = null;
		List<DeviceAccountTransform> deviceTransformList = null;
		if (page_id == 0 || total == 0) {
			deviceListQuery = "select d.uuid as uuid from devices d\r\n"
					+ "where d.school_id =:account_id and d.status='' and d.uuid !='' ";
		} else {
			deviceListQuery = "select d.uuid as uuid from devices d\r\n"
					+ "where d.school_id =:account_id and d.status='' and d.uuid !='' limit " + (page_id - 1) + ","
					+ total;
		}
		Query query = session.createSQLQuery(deviceListQuery).addScalar("uuid")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceAccountTransform.class));
		query.setParameter("account_id", admin_account_id);

		deviceTransformList = query.list();

		tx.commit();
		session.close();
		return deviceTransformList;
	}

	@Override
	public List<DeviceAccountTransform> getunAssignedDeviceList(int admin_account_id, int page_id, int total,
			String deviceUUID) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceListQuery = null;
		List<DeviceAccountTransform> deviceTransformList = null;
		if (page_id == 0 || total == 0) {
			deviceListQuery = "select d.uuid as uuid from devices d\r\n"
					+ "where d.school_id =:account_id and d.status='' and d.uuid =:deviceUUID";
		} else {
			deviceListQuery = "select d.uuid as uuid from devices d\r\n"
					+ "where d.school_id =:account_id and d.status='' and d.uuid =:deviceUUID limit " + (page_id - 1)
					+ "," + total;
		}
		Query query = session.createSQLQuery(deviceListQuery).addScalar("uuid")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceAccountTransform.class));
		query.setParameter("account_id", admin_account_id);
		query.setParameter("deviceUUID", deviceUUID);

		deviceTransformList = query.list();

		tx.commit();
		session.close();
		return deviceTransformList;
	}

	@Override
	public Devices getUnassigendDeviceByUUID(String uuid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Devices.class, "d");
		criteria.add(Restrictions.eq("d.uuid", uuid));
		@SuppressWarnings("unchecked")
		Devices device = (Devices) criteria.uniqueResult();
		tx.commit();
		session.close();
		return device;
	}

	@Override
	public int getTotalNoofassignedDeviceList(int admin_account_id, String grade_id, String classNo) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceListQuery = null;

		deviceListQuery = "select count(*) from devices d \r\n"
				+ "					LEFT JOIN device_students ds on ds.device_uuid = d.uuid \r\n"
				+ "					LEFT JOIN students s on s.student_id = ds.student_id \r\n"
				+ "					LEFT JOIN class_grade cg on cg.class_grade_id = s.class_grade_id \r\n"
				+ "					where d.school_id =:account_id and d.status='assigned' and ds.status='active' "
				+ " AND cg.grade =:grade_id and cg.class =:classNo and d.uuid !='' ";

		Query query = session.createSQLQuery(deviceListQuery);
		query.setParameter("account_id", admin_account_id);
		query.setParameter("grade_id", grade_id);
		query.setParameter("classNo", classNo);

		BigInteger noOfRecords = (BigInteger) query.uniqueResult();

		tx.commit();
		session.close();
		return noOfRecords.intValue();
	}

	@Override
	public int getTotalNoofUnassignedDeviceList(int admin_account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceListQuery = null;
		deviceListQuery = "select count(*) from devices d where d.school_id =:account_id and d.status='' and d.uuid !='' ";

		Query query = session.createSQLQuery(deviceListQuery);
		query.setParameter("account_id", admin_account_id);

		BigInteger noOfRecords = (BigInteger) query.uniqueResult();

		tx.commit();
		session.close();
		return noOfRecords.intValue();
	}

	@Override
	public boolean checkAccountIDExist(int schoolId) {
		boolean flag = false;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "SELECT account_id FROM accounts WHERE account_id = :accId and account_type = 'school' ";
			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter("accId", schoolId);

			/*
			 * Criteria criteria = session.createCriteria(Accounts.class,"rec");
			 * criteria.add(Restrictions.eq("rec.accountId", schoolId));
			 * criteria.add(Restrictions.eq("rec.accountType", acc_type));
			 * Accounts acc = (Accounts) criteria.uniqueResult();
			 */

			Integer accID = (Integer) query.uniqueResult();

			tx.commit();
			if (accID != null && accID > 0) {
				log.debug("School with schoolId entry exist");
				flag = true;
			}

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);
		} finally {
			session.close();
		}

		return flag;
	}

	@Override
	public AdminTransform getSchoolAdmin(int account_id) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		AdminTransform adminTransform = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "SELECT u.username as userName,u.lastlogin_date as lastLoginDate FROM users u  WHERE u.account_id =:accId and u.role_type='school_admin' ";
			Query query = session.createSQLQuery(sessionQuery).addScalar("userName").addScalar("lastLoginDate")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(AdminTransform.class));
			query.setParameter("accId", account_id);
			adminTransform = (AdminTransform) query.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);
		} finally {
			session.close();
		}

		return adminTransform;
	}

	@Override
	public int getAllocatedDevices(int account_id) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		BigInteger allocateDevices = new BigInteger("0");
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "SELECT count(*) from devices where school_id =:accId  ";
			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter("accId", account_id);
			allocateDevices = (BigInteger) query.uniqueResult();
			tx.commit();
		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);
		} finally {
			session.close();
		}

		return allocateDevices.intValue();
	}

	@Override
	public SchoolTransform getSchoolDetails(int account_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String deviceListQuery = null;
		deviceListQuery = "select sd.mobile_number as mobileNumber, sd.city as city ,sd.country as country,"
				+ "sd.state as state,sd.zipcode as zipCode ,sd.county as county , sd.address as address "
				+ "from school_details sd "
				+ "where sd.school_id =:account_id ORDER BY `school_details_id` DESC LIMIT 1";
		Query query = session.createSQLQuery(deviceListQuery).addScalar("mobileNumber").addScalar("city")
				.addScalar("country").addScalar("state").addScalar("zipCode").addScalar("county").addScalar("address")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SchoolTransform.class));
		query.setParameter("account_id", account_id);
		SchoolTransform schoolTransform = (SchoolTransform) query.uniqueResult();
		tx.commit();
		session.close();
		return schoolTransform;
	}

	@Override
	public int updateSchoolDetails(SchoolModel schoolModel) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "UPDATE `school_details` SET `county`=:county, `city` =:city, `mobile_number`=:mobileNumber, "
				+ "`address`=:address, `state`=:state, `zipcode`=:zipcode WHERE `school_id` =:school_id ";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter("city", schoolModel.getCity());
		query.setParameter("state", schoolModel.getState());
		query.setParameter("zipcode", schoolModel.getZipcode());
		query.setParameter("school_id", schoolModel.getAccountID());
		query.setParameter("address", schoolModel.getStreetAddress());
		query.setParameter("mobileNumber", schoolModel.getMobileNumber());
		query.setParameter("county", schoolModel.getCounty());

		int result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();
		return result;
	}

	@Override
	public int updateSchoolAccountDetails(SchoolModel schoolModel) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "update accounts  set account_name=:account_name   where account_id=:school_id ";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter("account_name", schoolModel.getAccountName());

		query.setParameter("school_id", schoolModel.getAccountID());
		int result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();
		return result;
	}

	@Override
	public int updateSchoolAdminDetails(SchoolModel schoolModel, String OldUserName) {
		// TODO Auto-generated method stub
		int result = 0;
		// if((OldUserName == null) ||((null != OldUserName) &&
		// (!OldUserName.equals(schoolModel.getSchoolAdmin())))) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "update users  set username=:username,user_active='n'  where account_id=:school_id and role_type ='school_admin'";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter("username", schoolModel.getSchoolAdmin());

		query.setParameter("school_id", schoolModel.getAccountID());
		result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();
		// }
		return result;
	}

	@Override
	public Accounts createSchoolAccount(SchoolModel schoolModel) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Accounts accounts = new Accounts();
		accounts.setAccountType("school");
		accounts.setAccountActive("y");
		accounts.setAccountName(schoolModel.getAccountName());
		accounts.setCreatedDate(new java.util.Date());

		session.save(accounts);
		tx.commit();
		session.close();
		return accounts;
	}

	@Override
	public SchoolDetails createSchoolDetails(SchoolModel schoolModel, int account_id) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		SchoolDetails schoolDetails = new SchoolDetails();
		schoolDetails.setCity(schoolModel.getCity());
		schoolDetails.setCountry(schoolModel.getCountry());
		schoolDetails.setMobile_number(schoolModel.getMobileNumber());
		schoolDetails.setAddress(schoolModel.getAddress());
		schoolDetails.setZipcode(schoolModel.getZipcode());
		schoolDetails.setCounty(schoolModel.getCounty());
		schoolDetails.setState(schoolModel.getState());
		schoolDetails.setSchoolId(account_id);
		session.saveOrUpdate(schoolDetails);
		tx.commit();
		session.close();
		return schoolDetails;
	}

	@Override
	public Users createSchoolAdminAccount(SchoolModel schoolModel, Accounts account) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Users user = new Users();
		user.setAccounts(account);
		user.setUsername(schoolModel.getSchoolAdmin());
		user.setRoleType("school_admin");
		user.setCreatedDate(new java.util.Date());
		user.setUserActive("n");
		session.saveOrUpdate(user);
		tx.commit();
		session.close();
		return user;
	}

	@Override
	public int getLatestAccountId() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT MAX(account_id) FROM accounts";
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
	public List<Devices> listDevices(String uuid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Devices.class, "d");
		criteria.add(Restrictions.eq("d.uuid", uuid));
		List<Devices> deviceList = criteria.list();
		tx.commit();
		// session.close();
		return deviceList;
	}

	@Override
	public String getAccoutNameByAccoutnId(int accoutn_id) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		String accountName = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			String sessionQuery = "SELECT account_name FROM accounts as acc WHERE acc.account_id = :accId";
			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter("accId", accoutn_id);

			accountName = (String) query.uniqueResult();

			tx.commit();

		} catch (Exception e) {
			log.debug("Exception inside DAOImpl :  " + e);
		} finally {
			session.close();
		}

		return accountName;
	}

	@Override
	public DeviceConfigurationsTransform getDeviceConfigurations(int device_configuration_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String deviceListQuery = null;
		deviceListQuery = "select dc.device_model as deviceModel, dc.firmware_version as frimWareVersion  from device_configurations dc "
				+ "where dc.device_configuration_id =:device_configuration_id ";
		Query query = session.createSQLQuery(deviceListQuery).addScalar("deviceModel").addScalar("frimWareVersion")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceConfigurationsTransform.class));
		query.setParameter("device_configuration_id", device_configuration_id);
		DeviceConfigurationsTransform deviceConfigTransform = (DeviceConfigurationsTransform) query.uniqueResult();
		tx.commit();
		session.close();
		return deviceConfigTransform;
	}

	@Override
	public int updateDeviceDetails(DeviceListModel deviceModel) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "update devices  set uuid=:uuid   where device_id=:device_id ";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter("device_id", deviceModel.getDeviceid());

		query.setParameter("uuid", deviceModel.getUuid());
		int result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();
		return result;
	}

	@Override
	public int updateDeviceConfigurationDetails(DeviceListModel deviceModel) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "update device_configurations  set device_model=:device_model,firmware_version=:firmware_version   where device_configuration_id=:device_configuration_id ";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter("device_configuration_id", deviceModel.getDeviceConfigurationID());

		query.setParameter("device_model", deviceModel.getDeviceModel());
		query.setParameter("firmware_version", deviceModel.getFirmWareVersion());
		int result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();
		return result;
	}

	@Override
	public int getConfigurationId(DeviceListModel deviceListModel) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String deviceListQuery = null;
		deviceListQuery = "select device_configuration_id from device_configurations dc where dc.device_model=:deviceModel and  dc.firmware_version=:frimWareVersion";

		Query query = session.createSQLQuery(deviceListQuery);
		query.setParameter("deviceModel", deviceListModel.getDeviceModel());
		query.setParameter("frimWareVersion", deviceListModel.getFirmWareVersion());
		Integer configId = (Integer) query.uniqueResult();
		tx.commit();
		session.close();
		if (null != configId)
			return configId;
		else
			return 0;
	}

	@Override
	public DeviceConfigurations createDeviceConfigccount(DeviceListModel deviceModel) {
		log.info("createDeviceConfigccount{");
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		DeviceConfigurations deviceConfigurations = new DeviceConfigurations();
		deviceConfigurations.setDeviceModel(deviceModel.getDeviceModel());
		deviceConfigurations.setFirmwareVersion(deviceModel.getFirmWareVersion());
		deviceConfigurations.setCreatedDate(new java.util.Date());
		deviceConfigurations.setUpdatedDate(new java.util.Date());
		session.saveOrUpdate(deviceConfigurations);
		tx.commit();
		session.close();
		log.info("createDeviceConfigccount}");
		return deviceConfigurations;
	}

	@Override
	public Devices createDeviceAccount(DeviceListModel deviceModel, DeviceConfigurations deviceConfig) {
		log.info("createDeviceAccount{");

		// 1) create a java calendar instance
		Calendar calendar = Calendar.getInstance();

		// 2) get a java.util.Date from the calendar instance.
		// this date will represent the current instant, or "now".
		java.util.Date now = calendar.getTime();

		// 3) a java current time (now) instance
		java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Devices device = new Devices();
		device.setUuid(deviceModel.getUuid());
		device.setSchoolId(deviceModel.getSchoolId());
		device.setDeviceConfigurations(deviceConfig);
		device.setStatus(" ");
		device.setCreatedDate(currentTimestamp);
		device.setUpdatedDate(currentTimestamp);

		session.saveOrUpdate(device);
		tx.commit();
		session.close();
		log.info("createDeviceAccount}");
		return device;
	}

	@Override
	public int getLatestDeviceConfigId() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT MAX(device_configuration_id) FROM device_configurations";
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
	public DeviceConfigurations getDeviceConfigurationByID(int configId) {
		return (DeviceConfigurations) sessionFactory.getCurrentSession().get(DeviceConfigurations.class, configId);
	}

	@Override
	public List<String> getCountys() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT distinct(county_en) FROM county_areas ca \r\n"
				+ "LEFT JOIN school_details sd on ca.county_en = sd.county\r\n" + "where ca.county_en = sd.county";
		List<String> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);
		sessions = (List<String>) query.list();
		session.close();
		return sessions;
	}

	@Override
	public List<AccountFroSchoolTransform> getSchoolName(String county) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select a.account_name as account_name , a.account_id  as account_id from accounts a \r\n"
				+ "LEFT JOIN school_details sd on sd.school_id = a.account_id \r\n" + "where sd.county=:county";
		List<AccountFroSchoolTransform> sessions = null;
		Query query = session.createSQLQuery(sessionQuery).addScalar("account_name").addScalar("account_id")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(AccountFroSchoolTransform.class));
		;
		query.setParameter("county", county);
		sessions = (List<AccountFroSchoolTransform>) query.list();
		session.close();
		return sessions;
	}

	@Override
	public DeviceStatsTransform getDeviceStats(String county, String schoolName, String status) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String deviceListQuery = null;
		deviceListQuery = "select acc.account_name as schoolName, count(d.status) as count from accounts acc\r\n"
				+ "LEFT JOIN devices d on d.school_id = acc.account_id \r\n"
				+ "LEFT JOIN school_details sd on sd.school_id = d.school_id \r\n"
				+ "where sd.county =:county and acc.account_id=:accountid and d.status=:status";
		Query query = session.createSQLQuery(deviceListQuery).addScalar("schoolName").addScalar("count")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceStatsTransform.class));
		query.setParameter("county", county);
		query.setParameter("accountid", Integer.parseInt(schoolName));
		query.setParameter("status", status);
		DeviceStatsTransform deviceSatsTransform = (DeviceStatsTransform) query.uniqueResult();
		tx.commit();
		session.close();
		return deviceSatsTransform;
	}

	@Override
	public int deleteDevice(int deviceid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String DeleteQuery_Rewards = "delete from devices where device_id=:device_id";
		int deletedDevice = session.createSQLQuery(DeleteQuery_Rewards).setParameter("device_id", deviceid)
				.executeUpdate();
		log.info("DeleteQuery_Rewards Query " + DeleteQuery_Rewards);
		log.info("updated " + deletedDevice);
		tx.commit();
		return deletedDevice;
	}

	@Override
	public int getConfigIdByDeviceId(int deviceid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String deviceListQuery = null;
		deviceListQuery = "select device_configuration_id from devices d where d.device_id=:deviceid ";
		Query query = session.createSQLQuery(deviceListQuery);
		query.setParameter("deviceid", deviceid);
		Integer configId = (Integer) query.uniqueResult();
		tx.commit();
		session.close();
		if (null != configId)
			return configId;
		else
			return 0;
	}

	@Override
	public int updateDeviceConfigId(int deviceId, int configid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "update devices  set device_configuration_id=:config_id   where device_id=:device_id ";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter("device_id", deviceId);
		query.setParameter("config_id", configid);
		int result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();
		return result;
	}

	@Override
	public List<DeviceConfigTransform> getDeviceModels() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT a.device_configuration_id AS config_id, a.device_model AS model "
				+ "FROM device_configurations a "
				+ "INNER JOIN ( SELECT device_model, MAX(device_configuration_id) max_ID "
				+ "FROM device_configurations GROUP BY device_model ) b ON a.device_model = b.device_model "
				+ "AND a.device_configuration_id = b.max_ID ORDER BY model";
		List<DeviceConfigTransform> sessions = null;
		Query query = session.createSQLQuery(sessionQuery).addScalar("config_id").addScalar("model")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceConfigTransform.class));
		;

		sessions = (List<DeviceConfigTransform>) query.list();
		session.close();
		return sessions;
	}

	@Override
	public DeviceConfigurations getDeviceConfigDetails(int configId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(DeviceConfigurations.class, "dc");
		criteria.add(Restrictions.eq("dc.deviceConfigId", configId));
		DeviceConfigurations deviceConfig = (DeviceConfigurations) criteria.uniqueResult();
		tx.commit();
		// session.close();
		return deviceConfig;
	}

	@Override
	public int updateDeviceConfig(DeviceConfigModel deviceConfigModel) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String SQL_UPDATE = "update device_configurations set " + "gps_report_frequency=:gps_report_frequency, "
				+ "low_battery=:low_battery, " + "wearable_sync_frequency=:wearable_sync_frequency, "
				+ "device_self_testing_version=:device_self_testing_version  "
				+ "WHERE device_model IN( SELECT device_model " + "FROM (SELECT * FROM device_configurations) AS m2 "
				+ "WHERE device_configuration_id=:device_configuration_id) ";
		Query query = session.createSQLQuery(SQL_UPDATE);

		query.setParameter("device_configuration_id", deviceConfigModel.getDeviceConfigId());

		query.setParameter("low_battery", deviceConfigModel.getLowBattery());
		query.setParameter("gps_report_frequency", deviceConfigModel.getGepReportFreequency());
		query.setParameter("wearable_sync_frequency", deviceConfigModel.getDataSyncFreequency());
		query.setParameter("device_self_testing_version", deviceConfigModel.getDeviceselftesting());
		int result = query.executeUpdate();
		log.info("Result" + result);
		tx.commit();
		session.close();
		return result;
	}

	@Override
	public List<DeviceConfigurations> getDeviceConfigurationList(int start, int total, String modelSelectedValue) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(DeviceConfigurations.class, "dc");
		criteria.add(Restrictions.eq("dc.deviceModel", modelSelectedValue));
		criteria.setFirstResult(start);
		criteria.setMaxResults(total);
		List<DeviceConfigurations> deviceList = criteria.list();
		tx.commit();
		// session.close();
		return deviceList;
	}

	@Override
	public DeviceConfigurations createDeviceConfigccount(DeviceConfigurations deviceConfig) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		session.saveOrUpdate(deviceConfig);
		tx.commit();
		session.close();
		log.info("createDeviceConfigccount}");
		return deviceConfig;
	}

	@Override
	public int deleteDeviceConfig(int deviceConfigid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String DeleteQuery_Rewards = "delete from device_configurations where device_configuration_id=:deviceConfigid";
		int deletedDeviceConfig = session.createSQLQuery(DeleteQuery_Rewards)
				.setParameter("deviceConfigid", deviceConfigid).executeUpdate();
		log.info("DeleteQuery_Rewards Query " + DeleteQuery_Rewards);
		log.info("updated " + deletedDeviceConfig);
		tx.commit();
		return deletedDeviceConfig;
	}

	@Override
	public List<Integer> getDeviceIdByConfigId(int device_config_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "SELECT device_id  FROM devices  where device_configuration_id= :deviceConfigid ";
		List<Integer> sessions = null;
		Query query = session.createSQLQuery(sessionQuery);

		query.setParameter("deviceConfigid", device_config_id);
		sessions = (List<Integer>) query.list();
		session.close();
		return sessions;

	}

	@Override
	public List<DeviceConfigTransform> getDeviceConfiDetailsByModel(String model) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sessionQuery = "select device_configuration_id as config_id,firmware_version as firmware_version ,low_battery  as lowBattery, gps_report_frequency as gpsReportFrequency , device_self_testing_version as deviceSelfTestingVersion ,wearable_sync_frequency as wearableSyncFrequency from device_configurations where device_model=:model";
		List<DeviceConfigTransform> sessions = null;
		Query query = session.createSQLQuery(sessionQuery).addScalar("config_id").addScalar("firmware_version")
				.addScalar("lowBattery").addScalar("gpsReportFrequency").addScalar("deviceSelfTestingVersion")
				.addScalar("wearableSyncFrequency").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceConfigTransform.class));
		;
		query.setParameter("model", model);
		sessions = (List<DeviceConfigTransform>) query.list();
		session.close();
		return sessions;
	}

	@Override
	public Accounts findAccountBySchoolId(int school_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Accounts.class);
		criteria.add(Restrictions.eq("accountId", school_id));
		Accounts accounts = (Accounts) criteria.uniqueResult();
		tx.commit();
		// session.close();
		return accounts;
	}

	@Override
	public int deleteBrokenDevice(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String DeleteQuery_Rewards = "delete from devices where school_id=:school_id and status='broken' ";
		int deletedDevice = session.createSQLQuery(DeleteQuery_Rewards).setParameter("school_id", account_id)
				.executeUpdate();
		log.info("DeleteQuery_Rewards Query " + DeleteQuery_Rewards);
		log.info("updated " + deletedDevice);
		tx.commit();
		return deletedDevice;
	}

	@Override
	public int deleteReturndeDevice(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String DeleteQuery_Rewards = "delete from devices where school_id=:school_id and status='returned' ";
		int deletedDevice = session.createSQLQuery(DeleteQuery_Rewards).setParameter("school_id", account_id)
				.executeUpdate();
		log.info("DeleteQuery_Rewards Query " + DeleteQuery_Rewards);
		log.info("updated " + deletedDevice);
		tx.commit();
		return deletedDevice;
	}

	@Override
	public int getTotalNoofSchools() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String schoolListQuery = null;

		schoolListQuery = "select count(*) from accounts where account_type ='school' and account_active='y' ";

		Query query = session.createSQLQuery(schoolListQuery);

		BigInteger noOfRecords = (BigInteger) query.uniqueResult();

		tx.commit();
		session.close();
		return noOfRecords.intValue();
	}

	@Override
	public int getTotalNoofFirrmWare(String device_model) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String schoolListQuery = "SELECT COUNT(*) FROM device_configurations WHERE device_model=:device_model";
		Query query = session.createSQLQuery(schoolListQuery);
		query.setParameter("device_model", device_model);
		BigInteger noOfRecords = (BigInteger) query.uniqueResult();

		tx.commit();
		session.close();
		return noOfRecords.intValue();
	}

	@Override
	public List<DeviceConfigurations> getDeviceConfigurationList() {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(DeviceConfigurations.class, "dc");
		List<DeviceConfigurations> deviceList = criteria.list();
		tx.commit();
		return deviceList;
	}

	@Override
	public int getTotalnoOfSchoolwithConfigId(int device_config_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String schoolListQuery = null;

		schoolListQuery = "select count(*) from devices where device_configuration_id =:config_id";

		Query query = session.createSQLQuery(schoolListQuery);
		query.setParameter("config_id", device_config_id);

		BigInteger noOfRecords = (BigInteger) query.uniqueResult();

		tx.commit();
		session.close();
		return noOfRecords.intValue();
	}

	@Override
	public List<DeviceAccountTransform> getBrokenOrReturnedDeviceUUIDs(int admin_account_id, String device_status) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String deviceListQuery = null;
		List<DeviceAccountTransform> deviceTransformList = null;
		deviceListQuery = "SELECT uuid FROM devices WHERE school_id =:account_id and status=:device_status ";
		Query query = session.createSQLQuery(deviceListQuery).addScalar("uuid")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(DeviceAccountTransform.class));
		query.setParameter("account_id", admin_account_id);
		query.setParameter("device_status", device_status);
		deviceTransformList = query.list();

		tx.commit();
		session.close();
		return deviceTransformList;
	}

}
