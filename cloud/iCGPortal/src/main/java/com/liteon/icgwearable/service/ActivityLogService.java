package com.liteon.icgwearable.service;

import java.util.Date;
import java.util.List;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.transform.ActivityLogTransform;
import com.liteon.icgwearable.transform.ExternalSystemStatusTransform;

public interface ActivityLogService {

	public void info(String name, String username, String userrole, String level, String action, String summary,
			String ipaddress, Date createddate);

	public void error(String name, String username, String userrole, String level, String action, String summary,
			String ipaddress, Date createddate);

	public void critical(String name, String username, String userrole, String level, String action, String summary,
			String ipaddress, Date createddate);

	public void info(ActivityLog activityLog);

	public void error(ActivityLog activityLog);

	public void critical(ActivityLog activityLog);

	public List<ActivityLogTransform> findActivityNotificationBeaconLogs(String logType, int page_id, int total);

	public List<ExternalSystemStatusTransform> findExternalSystemStatus(int pageid, int total);
}
