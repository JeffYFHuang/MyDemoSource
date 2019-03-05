package com.liteon.icgwearable.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

public interface CommonService {

	public List<GeoZoneModel> getUserGeoZones(int user_id, String uuid);

	public void createGeoZone(Geozones geoZones);

	public void updateGeoZone(Geozones geoZones);

	public void deleteGeozone(int geoZoneId);

	public void createReminder(Reminders reminder);

	public void updateReminder(Reminders reminder);

	public void deleteReminder(int reminderId);

	public List<RemindersTransform> getReminders(Users user);

	public Reminders getReminderByID(int reminderid);

	public int getTotalNoOfZeofencesPerUser(int user_id, String uuid);

	public String getImageNameByReminderId(int reminder_id);

	public List<ValidParentUsersWithTokenTransform> getParentUsersListForReminders(Integer cgId);

	public List<StudentRewardsTransform> getStudentsRewardsByDate(int userId, int uuid, String date);

	public List<RemindersModel> getRemindersForParent(int user_id, String timeline, String studentname);

	public List<String> getKidsForParent(int userId);

	public String getStudentClassByParentid(String studentName, int parent_account_id);

	public List<RemindersModel> getRemindersForTeacher(int teacher_id, String timeline, String classname);

	public List<String> getClassesForTeacher(int teacher_id);

	public List<RemindersModel> getRemindersForSchoolAdmin(List<Integer> teachers, String timeline, int teacherid,
			String teachername);

	public List<String> getTeachersForSchoolAdmin(int account_id);

	public List<Integer> getTeacherIdsForSchoolAdmin(int account_id);

	public int getTeacherIdbyNameandAccount(String name, int account_id);

	public SchoolTimeModel getSchoolTimelatestEvent(int student_id);

	public LatestGeoFenceAlertModel getGeofencelatestEvent(int student_id);

	public DeviceTypeAlertLatestModel getDeviceAlertslatestEvent(int student_id);

	public LatestReminderModel getLatestReminders(int student_id);

	public LatestRewardsModel getLatestRewards(int student_id);

	public LatestAnnoncementModel getLatestAnnouncements(int student_id);

	public LatestGPSLocationModel getLatestGPSLocations(int student_id);

	public List<KidsSafeModel> getKidsSafeNotificationData(int student_id, String date);

	/*
	 * New Reminders List Methods
	 */

	public List<RemindersModel> findRemindersListForParent(int user_id, String date, int studentId);

	public List<RemindersModel> findRemindersListForTeacher(int teacher_id, String date);

	public List<RewardsCategoryTransform> findStudentsRewards(int studentId);
	
	public ClassGrade findClassGrade(int teacherId);
	
	public List<Integer> getParentIdByUuid(String uuid ) ;
	
	public  Geozones getGeozoneById(int geoZone_id);
	public int getDeviceEventIdByGeozoneId (int geozone_id,String uuid);
	
	public int getClassGradeId(int user_id, int student_id);
	
	public void updateClassGrade(StudentsSleepDataModel studentsSleepDataModel);
	
	public List<StudentsListTransform> findStudentsIdsAndUUIDS(int teacher_id);
	
	public Date getDeviceEventOccuredDate(int device_event_id);
	
	public SchoolWorkingHoursTransform checkDeviceEventOccuredWithinSchoolHours(int school_id, String date, String time);
	
	public List<Integer> getKididsForParent(int userId);
	
	public boolean checkClosedDeviceEventExists(int device_event_id);

	public boolean updateClosedDeviceEvents(int device_event_id, int user_id, String user_roleType, int duration,
			boolean deviceEventExists, String isSosAbnormal);
	public boolean updateDeviceEventQueueForCloseSOSAlert(int user_id);
	
	public boolean checkDeviceEventExists(int device_event_id);
	
	public boolean checkDeviceEventBelongsToParentUser(int user_id, int device_event_id);

	public boolean checkDeviceEventBelongsToSchoolUsers(int device_event_id, int school_id);
	
	public boolean checkSchoolHoursWithinRange(String time, int school_id);

	public boolean checkSchoolHoliday(String date, int school_id);
	
	public String getSchoolClosingHours(int school_id);
	
	public ClassGrade getClassGrade(String studentClass, String grade,int school_id);

	public boolean checkTokenIsValidOrNot(String tokenId);

	public Geozones checkIfGeozoneIdExist(int geozoneId);
	
	public boolean checkStudentId(int user_id, int student_id);

	public HashMap<String, Object> getCountyList();
}

