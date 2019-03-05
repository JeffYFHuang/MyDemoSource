package com.liteon.icgwearable.service;

import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.SchoolDAO;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.SchoolCalendar;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.hibernate.entity.SystemConfiguration;
import com.liteon.icgwearable.transform.SchoolScheduleTransform;
import com.liteon.icgwearable.transform.SearchSchoolTransform;
import com.liteon.icgwearable.transform.SysConfigurationTransform;

@Service("schoolService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class SchoolServiceImpl implements SchoolService{

	@Autowired
    private SchoolDAO schoolDAO;
	@Override
	public List<Accounts> listSchools(int start, int total) {
		return this.schoolDAO.listSchools(start,total);
	}

	@Override
	public void addSchool(Accounts account) {
		 this.schoolDAO.addSchool(account);
	}

	@Override
	public boolean deleteSchool(int id ) {
		return this.schoolDAO.deleteSchool(id);
	}

	@Override
	public Accounts getSchool(int id) {
		return this.schoolDAO.getSchool(id);
	}
	
	@Override
	public SchoolScheduleTransform viewScheduleList(int schoolId){
		return this.schoolDAO.viewScheduleList(schoolId);
	}
	
	@Override
	public SysConfigurationTransform viewSysConfigurationList(){
		return this.schoolDAO.viewSysConfigurationList();
	}
	
	@Override
	public boolean isSchoolCalendarIdExist(int schoolId) {
		return this.schoolDAO.isSchoolCalendarIdExist(schoolId);
	}
	
	@Override
	public boolean isSchoolDetailsIdExist(int schoolId) {
		return this.schoolDAO.isSchoolDetailsIdExist(schoolId);
	}
	
	@Override
	public void updateSchedule( SchoolDetails schoolDetails, boolean isUpdateSd) {
		this.schoolDAO.updateSchedule( schoolDetails, isUpdateSd);
	};
	
	@Override
	public void updateSystemConfiguration(SystemConfiguration systemConfiguration) {
		this.schoolDAO.updateSystemConfiguration( systemConfiguration);
	};
	
	@Override
	public List<SchoolCalendar> getSchoolCalendarListBySchoolId(int schoolId) {
		return this.schoolDAO.getSchoolCalendarListBySchoolId(schoolId);
	}
	
	@Override
	public SchoolDetails getSchoolDetailsBySchoolId(int schoolId) {
		return this.schoolDAO.getSchoolDetailsBySchoolId(schoolId);
	}
	
	@Override
	public SystemConfiguration getSystemConfigurationById(int sysId) {
		return this.schoolDAO.getSystemConfigurationById(sysId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addCalendar(SchoolCalendar schoolCalendar) {
		this.schoolDAO.addCalendar(schoolCalendar);
	}
	
	public boolean updateSchoolCalendarList(int schoolId, List<SchoolCalendar> list) {
				return this.schoolDAO.updateSchoolCalendarList(schoolId, list);
	}
	
	@Override
	public Map<String, Object> searchParents(String profileName, String contactNo, String emailId,
			String deviceUUID,int start,int total) {
		return this.schoolDAO.searchParents(profileName, contactNo, emailId, deviceUUID, start, total);
	}
	
	@Override
	public int getTotalNoofParents(String profileName, String contactNo, String emailId,
			String deviceUUID) {
		return this.schoolDAO.getTotalNoofParents(profileName, contactNo, emailId, deviceUUID);
	}

	@Override
	public List<SearchSchoolTransform> searchSchool(int school_id) {
		// TODO Auto-generated method stub
		return this.schoolDAO.searchSchool(school_id);
	}


}
