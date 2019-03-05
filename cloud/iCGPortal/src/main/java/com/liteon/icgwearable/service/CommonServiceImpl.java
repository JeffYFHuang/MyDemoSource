package com.liteon.icgwearable.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.CommonDAO;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.hibernate.entity.Geozones;
import com.liteon.icgwearable.hibernate.entity.Reminders;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.DeviceTypeAlertLatestModel;
import com.liteon.icgwearable.model.GeoZoneModel;
import com.liteon.icgwearable.model.KidsSafeModel;
import com.liteon.icgwearable.model.LatestAnnoncementModel;
import com.liteon.icgwearable.model.LatestGPSLocationModel;
import com.liteon.icgwearable.model.LatestGeoFenceAlertModel;
import com.liteon.icgwearable.model.LatestReminderModel;
import com.liteon.icgwearable.model.LatestRewardsModel;
import com.liteon.icgwearable.model.RemindersModel;
import com.liteon.icgwearable.model.SchoolTimeModel;
import com.liteon.icgwearable.model.StudentsSleepDataModel;
import com.liteon.icgwearable.transform.RemindersTransform;
import com.liteon.icgwearable.transform.RewardsCategoryTransform;
import com.liteon.icgwearable.transform.SchoolWorkingHoursTransform;
import com.liteon.icgwearable.transform.StudentRewardsTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.ValidParentUsersWithTokenTransform;

@Service("commonService")
public class CommonServiceImpl implements CommonService {

	@Autowired
	private CommonDAO commonDAO;

	@Override
	public List<GeoZoneModel> getUserGeoZones(int user_id, String uuid) {
		return commonDAO.getUserGeoZones(user_id, uuid);
	}

	@Override
	public void createGeoZone(Geozones geoZones) {
		commonDAO.createGeoZone(geoZones);
	}

	@Override
	public void updateGeoZone(Geozones geoZones) {
		commonDAO.updateGeoZone(geoZones);
	}

	@Override
	public void deleteGeozone(int geoZoneId) {
		commonDAO.deleteGeozone(geoZoneId);
	}

	@Override
	public void createReminder(Reminders reminder) {
		commonDAO.createReminder(reminder);
	}

	@Override
	public void deleteReminder(int reminderId) {
		commonDAO.deleteReminder(reminderId);
	}

	@Override
	public List<RemindersTransform> getReminders(Users user) {
		return this.commonDAO.getReminders(user);
	}

	@Override
	public void updateReminder(Reminders reminder) {
		commonDAO.updateReminder(reminder);
	}

	@Override
	public int getTotalNoOfZeofencesPerUser(int user_id, String uuid) {
		return commonDAO.getTotalNoOfZeofencesPerUser(user_id, uuid);
	}

	@Override
	public String getImageNameByReminderId(int reminder_id) {
		return commonDAO.getImageNameByReminderId(reminder_id);
	}

	public List<StudentRewardsTransform> getStudentsRewardsByDate(int userId, int studentId, String date) {
		return this.commonDAO.getStudentsRewardsByDate(userId, studentId, date);
	}

	@Override
	public Reminders getReminderByID(int reminderid) {
		return commonDAO.getReminderByID(reminderid);
	}

	@Override
	public List<ValidParentUsersWithTokenTransform> getParentUsersListForReminders(Integer cgId) {
		return commonDAO.getParentUsersListForReminders(cgId);
	}

	@Override
	public List<RemindersModel> getRemindersForParent(int user_id,String timeline, String studentname) {
		return commonDAO.getRemindersForParent(user_id,timeline,studentname);
	}

	@Override
	public List<String> getKidsForParent(int userId) {
		return commonDAO.getKidsForParent(userId);
	}

	@Override
	public String getStudentClassByParentid(String studentName, int parent_account_id) {
		return commonDAO.getStudentClassByParentid(studentName, parent_account_id);
	}

	@Override
	public List<RemindersModel> getRemindersForTeacher(int teacher_id, String timeline, String classname) {
		return commonDAO.getRemindersForTeacher(teacher_id, timeline, classname);
	}

	@Override
	public List<String> getClassesForTeacher(int teacher_id) {
		return commonDAO.getClassesForTeacher(teacher_id);
	}

	@Override
	public List<String> getTeachersForSchoolAdmin(int account_id) {
		return commonDAO.getTeachersForSchoolAdmin(account_id);
	}

	@Override
	public List<Integer> getTeacherIdsForSchoolAdmin(int account_id) {
		return commonDAO.getTeacherIdsForSchoolAdmin(account_id);
	}

	@Override
	public int getTeacherIdbyNameandAccount(String name, int account_id) {
		return commonDAO.getTeacherIdbyNameandAccount(name, account_id);
	}

	@Override
	public List<RemindersModel> getRemindersForSchoolAdmin(List<Integer> teachers, String timeline, int teacherid,
			String teachername) {
		return commonDAO.getRemindersForSchoolAdmin(teachers, timeline, teacherid, teachername);
	}

	@Override
	public SchoolTimeModel getSchoolTimelatestEvent(int student_id) {
		return commonDAO.getSchoolTimelatestEvent(student_id);
	}

	@Override
	public LatestGeoFenceAlertModel getGeofencelatestEvent(int student_id) {
		return commonDAO.getGeofencelatestEvent(student_id);
	}

	@Override
	public DeviceTypeAlertLatestModel getDeviceAlertslatestEvent(int student_id) {
		return commonDAO.getDeviceAlertslatestEvent(student_id);
	}

	@Override
	public LatestReminderModel getLatestReminders(int student_id) {
		return commonDAO.getLatestReminders(student_id);
	}

	@Override
	public LatestAnnoncementModel getLatestAnnouncements(int student_id) {
		return commonDAO.getLatestAnnouncements(student_id);
	}

	@Override
	public LatestRewardsModel getLatestRewards(int student_id) {
		return commonDAO.getLatestRewards(student_id);
	}

	@Override
	public LatestGPSLocationModel getLatestGPSLocations(int student_id) {
		return commonDAO.getLatestGPSLocations(student_id);
	}

	@Override
	public List<KidsSafeModel> getKidsSafeNotificationData(int student_id,String date) {
		return commonDAO.getKidsSafeNotificationData(student_id,date);
	}

	@Override
	public List<RemindersModel> findRemindersListForParent(int user_id, String date, int studentId) {
		return this.commonDAO.findRemindersListForParent(user_id, date, studentId);
	}

	@Override
	public List<RemindersModel> findRemindersListForTeacher(int teacher_id, String date) {
		return this.commonDAO.findRemindersListForTeacher(teacher_id, date);
	}

	@Override
	public List<RewardsCategoryTransform> findStudentsRewards(int studentId) {
		return this.commonDAO.findStudentsRewards(studentId);
	}

	@Override
	public ClassGrade findClassGrade(int teacherId) {
		return this.commonDAO.findClassGrade(teacherId);
	}

	@Override
	public List<Integer> getParentIdByUuid(String uuid) {
		return this.commonDAO.getParentIdByUuid(uuid);
	}

	@Override
	public Geozones getGeozoneById(int geoZone_id) {
		return this.commonDAO.getGeozoneById(geoZone_id);
	}

	@Override
	public int getDeviceEventIdByGeozoneId(int geozone_id, String uuid) {
		return this.commonDAO.getDeviceEventIdByGeozoneId(geozone_id, uuid);
	}

	@Override
	public int getClassGradeId(int user_id, int student_id) {
		return this.commonDAO.getClassGradeId(user_id, student_id);
	}

	@Override
	public void updateClassGrade(StudentsSleepDataModel studentsSleepDataModel) {
		this.commonDAO.updateClassGrade(studentsSleepDataModel);
	}

	@Override
	public List<StudentsListTransform> findStudentsIdsAndUUIDS(int teacher_id) {
		return this.commonDAO.findStudentsIdsAndUUIDS(teacher_id);
	}

	@Override
	public Date getDeviceEventOccuredDate(int device_event_id) {
		return this.commonDAO.getDeviceEventOccuredDate(device_event_id);
	}

	@Override
	public SchoolWorkingHoursTransform checkDeviceEventOccuredWithinSchoolHours(int school_id, String date, String time) {
		return this.commonDAO.checkDeviceEventOccuredWithinSchoolHours(school_id, date, time);
	}

	@Override
	public List<Integer> getKididsForParent(int userId) {
		return this.commonDAO.getKididsForParent(userId);
	}

	@Override
	public boolean checkClosedDeviceEventExists(int device_event_id) {
		return this.commonDAO.checkClosedDeviceEventExists(device_event_id);
	}

	@Override
	public boolean updateClosedDeviceEvents(int device_event_id, int user_id, String user_roleType, int duration,
			boolean deviceEventExists, String isSosAbnormal) {
		return this.commonDAO.updateClosedDeviceEvents(device_event_id, user_id, user_roleType, duration, deviceEventExists, isSosAbnormal);
		
	}

	@Override
	public boolean updateDeviceEventQueueForCloseSOSAlert(int user_id) {
		return this.commonDAO.updateDeviceEventQueueForCloseSOSAlert(user_id);
	}

	@Override
	public boolean checkDeviceEventExists(int device_event_id) {
		return this.commonDAO.checkDeviceEventExists(device_event_id);
	}

	@Override
	public boolean checkDeviceEventBelongsToParentUser(int user_id, int device_event_id) {
		return this.commonDAO.checkDeviceEventBelongsToParentUser(user_id, device_event_id);
	}

	@Override
	public boolean checkDeviceEventBelongsToSchoolUsers(int device_event_id, int school_id) {
		return this.commonDAO.checkDeviceEventBelongsToSchoolUsers(device_event_id, school_id);
	}

	@Override
	public boolean checkSchoolHoursWithinRange(String time, int school_id) {
		return this.commonDAO.checkSchoolHoursWithinRange(time, school_id);
	}

	@Override
	public boolean checkSchoolHoliday(String date, int school_id) {
		return this.commonDAO.checkSchoolHoliday(date, school_id);
	}

	@Override
	public String getSchoolClosingHours(int school_id) {
		return this.commonDAO.getSchoolClosingHours(school_id);
	}

	@Override
	public ClassGrade getClassGrade(String studentClass, String grade,int school_id) {
		return this.commonDAO.getClassGrade(studentClass, grade,school_id);
	}

	@Override
	public boolean checkTokenIsValidOrNot(String tokenId) {
		return this.commonDAO.checkTokenIsValidOrNot(tokenId);
	}

	@Override
	public Geozones checkIfGeozoneIdExist(int geozoneId) {
		return this.commonDAO.checkIfGeozoneIdExist(geozoneId);
	}

	@Override
	public boolean checkStudentId(int user_id, int student_id) {
		return this.commonDAO.checkStudentId(user_id, student_id);
	}

	@Override
	public HashMap<String, Object> getCountyList() {
		return this.commonDAO.getCountyList();
	}

}
