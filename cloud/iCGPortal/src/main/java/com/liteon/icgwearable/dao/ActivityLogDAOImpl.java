package com.liteon.icgwearable.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.transform.ActivityLogTransform;
import com.liteon.icgwearable.transform.ExternalSystemStatusTransform;

@Repository("activityLogDAO")
public class ActivityLogDAOImpl implements ActivityLogDAO {

	private static Logger log = Logger.getLogger(ActivityLogDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	public void info(String name, String username, String userrole, String level, String action, String summary,
			String ipaddress, Date createddate) {
		// TODO Auto-generated method stub
	}

	@Override
	public void error(String name, String username, String userrole, String level, String action, String summary,
			String ipaddress, Date createddate) {
		// TODO Auto-generated method stub
	}

	@Override
	public void critical(String name, String username, String userrole, String level, String action, String summary,
			String ipaddress, Date createddate) {
		// TODO Auto-generated method stub
	}

	@Override
	public void info(ActivityLog activityLog) {
		if (null != activityLog.getUserRole() && activityLog.getUserRole() != "") {
			activityLog.setLevel("info");
			activityLog.setCreateDate(new Date());
			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			session.save(activityLog);
			tx.commit();
			session.close();
		}
	}

	@Override
	public void error(ActivityLog activityLog) {
		if (null != activityLog.getUserRole() && activityLog.getUserRole() != "") {
			activityLog.setLevel("error");
			activityLog.setCreateDate(new Date());
			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			log.info("activityLog in error=" + activityLog.getUserRole());
			session.save(activityLog);
			tx.commit();
			session.close();
		}
	}

	@Override
	public void critical(ActivityLog activityLog) {
		if (null != activityLog.getUserRole() && activityLog.getUserRole() != "") {
			activityLog.setLevel("critical");
			activityLog.setCreateDate(new Date());
			Session session = sessionFactory.openSession();
			Transaction tx = session.beginTransaction();
			log.info("activityLog in error=" + activityLog.toString());
			session.save(activityLog);
			tx.commit();
			session.close();
		}
	}

	@Override
	public List<ActivityLogTransform> findActivityNotificationBeaconLogs(String logType, int page_id, int total) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		StringBuilder activityBuilder = null;
		String alString = null;

		List<ActivityLogTransform> activityLogTransformList = null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			activityBuilder = new StringBuilder();
			log.info("page_id in findActivityNotificationBeaconLogs" + "\t" + page_id);
			log.info("total in findActivityNotificationBeaconLogs" + "\t" + total);
			activityBuilder
					.append("SELECT name as profileName, username as username, user_role as role,action as action,ipaddress as ipaddress, DATE_FORMAT(created_date,'%Y-%m-%d %H:%i:%s') as created_date ")
					.append("FROM activity_log ").append("where level in ('info','error','critical') ");

			if (logType.equals("activity")) {
				if (page_id > 0) {
					activityBuilder
							.append(" AND action in ('UserLogin','UserLogout','Create','Update','Delete','Submit','BeaconLogin') ")
							.append("AND user_role in ('parent_admin','parent_member','school_admin','school_teacher','school_staff','system_admin','support_staff','super_admin','Wearable', 'Beacon') ")
							.append("AND created_date > (NOW() - INTERVAL 31 DAY) ")
							.append("ORDER BY created_date DESC ").append("limit " + (page_id - 1) + "," + total);
				} else {
					activityBuilder
							.append("AND action in ('UserLogin','UserLogout','Create','Update','Delete','Submit','BeaconLogin') ")
							.append("AND user_role in ('parent_admin','parent_member','school_admin','school_teacher','school_staff','system_admin','support_staff','super_admin','Wearable','Beacon') ")
							.append("AND created_date > (NOW() - INTERVAL 31 DAY) ")
							.append("ORDER BY created_date DESC");
				}
			} else if (logType.equals("notification")) {
				if (page_id > 0) {
					activityBuilder.append("AND action in ('PushNotification','EmailNotification') ")
							.append("AND user_role in ('IWPS','event_queue') ")
							.append("AND created_date > (NOW() - INTERVAL 31 DAY) ")
							.append("ORDER BY created_date DESC ").append("limit " + (page_id - 1) + "," + total);
				} else {
					activityBuilder.append("AND action in ('PushNotification','EmailNotification') ")
							.append("AND user_role in ('IWPS','event_queue') ")
							.append("AND created_date > (NOW() - INTERVAL 31 DAY) ")
							.append("ORDER BY created_date DESC");
				}
			} else if (logType.equals("backup")) {
				if (page_id > 0) {
					activityBuilder.append("AND action in ('DatabaseBackup') ")
							.append("AND created_date > (NOW() - INTERVAL 31 DAY) ")
							.append("ORDER BY created_date DESC ").append("limit " + (page_id - 1) + "," + total);
				} else {
					activityBuilder.append("AND action in ('DatabaseBackup') ")
							.append("AND created_date > (NOW() - INTERVAL 31 DAY) ")
							.append("ORDER BY created_date DESC");
				}
			}
			alString = activityBuilder.toString();

			Query query = session.createSQLQuery(alString).addScalar("profileName").addScalar("username")
					.addScalar("role").addScalar("action").addScalar("ipaddress").addScalar("created_date")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(ActivityLogTransform.class));
			activityLogTransformList = query.list();

		} catch (Exception e) {
			log.info("Exception Occured in finaActivityLog() " + "\t" + e);
		} finally {
			session.close();
		}
		return activityLogTransformList;
	}

	@Override
	public List<ExternalSystemStatusTransform> findExternalSystemStatus(int page_id, int total) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		StringBuilder externalSystemBuilder = null;
		String essString = null;

		List<ExternalSystemStatusTransform> externalSystemList = null;

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();

			externalSystemBuilder = new StringBuilder();

			if (page_id > 0) {
				externalSystemBuilder
						.append("SELECT ipsr.ips_receiver_mac as ips_receiver_mac,ipsr.receiver_name as ips_receiver_name, ipsr.receiver_version as ips_receiver_version, ")
						.append("ipsr.receiver_status as ips_receiver_status, ipsrdevice.noofdevices as devicesCount, acc.account_name as school_name ")
						.append("FROM ips_receiver as ipsr ")
						.append("left join (select count(device_uuid) as noofdevices, ips_receiver_id as id from ips_receiver_device as ipsrd group by id) ipsrdevice ")
						.append("on ipsrdevice.id = ipsr.ips_receiver_id ")
						.append("left join accounts acc on acc.account_id = ipsr.school_id ")
						.append("limit " + (page_id - 1) + "," + total);
			} else {
				externalSystemBuilder
						.append("SELECT ipsr.ips_receiver_mac as ips_receiver_mac,ipsr.receiver_name as ips_receiver_name, ipsr.receiver_version as ips_receiver_version, ")
						.append("ipsr.receiver_status as ips_receiver_status, ipsrdevice.noofdevices as devicesCount, acc.account_name as school_name ")
						.append("FROM ips_receiver as ipsr ")
						.append("left join (select count(device_uuid) as noofdevices, ips_receiver_id as id from ips_receiver_device as ipsrd group by id) ipsrdevice ")
						.append("on ipsrdevice.id = ipsr.ips_receiver_id ")
						.append("left join accounts acc on acc.account_id = ipsr.school_id ");
			}
			essString = externalSystemBuilder.toString();

			Query query = session.createSQLQuery(essString).addScalar("ips_receiver_mac").addScalar("ips_receiver_name")
					.addScalar("ips_receiver_version").addScalar("ips_receiver_status").addScalar("devicesCount")
					.addScalar("school_name").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(ExternalSystemStatusTransform.class));
			externalSystemList = query.list();

		} catch (Exception e) {

		} finally {
			session.close();
		}

		return externalSystemList;
	}

}
