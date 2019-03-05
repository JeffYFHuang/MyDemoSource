package com.liteon.icgwearable.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.ActivityLogDAO;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.transform.ActivityLogTransform;
import com.liteon.icgwearable.transform.ExternalSystemStatusTransform;

@Service("activityLogService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AcitvityLogServiceImpl implements ActivityLogService {

	@Autowired
	private ActivityLogDAO activityLogDAO;

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
		this.activityLogDAO.info(activityLog);
	}

	@Override
	public void error(ActivityLog activityLog) {
		this.activityLogDAO.error(activityLog);
	}

	@Override
	public void critical(ActivityLog activityLog) {
		this.activityLogDAO.critical(activityLog);
	}

	@Override
	public List<ActivityLogTransform> findActivityNotificationBeaconLogs(String logType, int page_id, int total) {
		return this.activityLogDAO.findActivityNotificationBeaconLogs(logType, page_id, total);
	}

	@Override
	public List<ExternalSystemStatusTransform> findExternalSystemStatus(int pageid, int total) {
		return this.activityLogDAO.findExternalSystemStatus(pageid, total);
	}

}
