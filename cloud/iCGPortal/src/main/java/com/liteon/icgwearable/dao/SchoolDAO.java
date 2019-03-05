package com.liteon.icgwearable.dao;

import java.util.List;
import java.util.Map;

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.SchoolCalendar;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.hibernate.entity.SystemConfiguration;
import com.liteon.icgwearable.transform.SchoolScheduleTransform;
import com.liteon.icgwearable.transform.SearchSchoolTransform;
import com.liteon.icgwearable.transform.SysConfigurationTransform;

public interface SchoolDAO {
	
	public List<Accounts> listSchools( int start, int total);

	public void addSchool(Accounts account);

	public boolean deleteSchool(int  id);
	
	public Accounts getSchool(int id);
	
	public SchoolScheduleTransform viewScheduleList(int schoolId);
	
	public SysConfigurationTransform viewSysConfigurationList();
	
	public boolean isSchoolCalendarIdExist(int schoolId);
	
	public boolean isSchoolDetailsIdExist(int schoolId);
	
	public void updateSchedule(SchoolDetails schoolDetails, boolean isUpdateSd);
	
	public void updateSystemConfiguration(SystemConfiguration systemConfiguration);
	
	public List<SchoolCalendar> getSchoolCalendarListBySchoolId(int schoolId);
	
	public SchoolDetails getSchoolDetailsBySchoolId(int schoolId);
	
	public SystemConfiguration getSystemConfigurationById(int sysId);
	
	public void addCalendar(SchoolCalendar schoolCalendar);
	
	public boolean updateSchoolCalendarList(int schoolId, List<SchoolCalendar> list); 
	
	public Map<String, Object> searchParents(String profileName, String contactNo, String emailId,
			String deviceUUID, int start, int total);
	
	public int getTotalNoofParents(String profileName, String contactNo, String emailId,
			String deviceUUID);
	
	public List<SearchSchoolTransform> searchSchool(int school_id);
}
