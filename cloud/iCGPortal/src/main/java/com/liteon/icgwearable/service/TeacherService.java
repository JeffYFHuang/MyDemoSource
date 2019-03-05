package com.liteon.icgwearable.service;

import java.util.List;
import java.util.Map;

import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.TeacherCSVModel;
import com.liteon.icgwearable.model.TeacherStaffModel;
import com.liteon.icgwearable.model.TeachersStaffCSVModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.transform.RewardStatisticsTransform;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeacherRewardsAssignedToStudentsTransform;
import com.liteon.icgwearable.transform.TeacherRewardsListTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;
import com.liteon.icgwearable.transform.TeachersTransform;

public interface TeacherService {
	//public boolean createTeachers(List<TeacherCSVModel> list, int accountId);
	
	public boolean createTeachers(TeacherCSVModel tcsvm, int accountId);

	public List<Users> listTeachersByPage(int i);

	public List<TeachersTransform> listAllTeachers(int userId);
	
	public List<Users> listTeachersById(int id);
	
	public void addTeacher(Users users);

	public Users getTeacher(int id);

	public void addUITeacher(Users users, String uuid);

	public void updateTeacherStaffApi(Users users, ClassGrade cg, UsersModel usersModel);
	
	public List<StudentsTransform> findStudentsClass(int userId);
	
	public List<TeachersStudentsTransform> findStudentsByTeacher(int userId);
	
	//public List<com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences> getSchoolAppPreferencesByUserId(int userId);
	
	public void deletetSchoolAppPreferencesByUserId(int userId,String QuiteHoursday);
	
	public void addSchoolAppPreferencesByUserId(Users user,String QuiteHoursday,int QuiteHourFrom , int QuiteHourTo);
	
	//public void UpdateSchoolAppPreferencesByUserId(com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences sap);

	public List<TeacherRewardsListTransform> getSchoolRewardsToTeacherBySchoolId(int school_id, int category_id);
	
	
	public String assignRewardsToStudentsByTeacher(Integer rewardId, Integer studentId, Integer count, int teacherId);
	
	public String deleteStudentsRewardsByTeacher(Integer students_rewardids, Integer user_id);
	
	public boolean checkRewardsId(int rewardId);
	
	public int getAccountId(Integer studentId);
	//Rewards For Teacher
	public List<RewardStatisticsTransform> getRewardsStatisticsForTeacher(int account_id);
	
	public List<RewardStatisticsTransform> getStudentNameForTeacher(int account_id);
	
	public List<RewardStatisticsTransform> getClassNameForTeacher(int account_id);
    
	public List<RewardStatisticsTransform> getStudentName(int account_id, String day, String studentClass, String studentName);

	public boolean checkStudentRewardId(int studentRewardId);
	
	public List<TeacherRewardsAssignedToStudentsTransform> StudentRewardsByTeacher(int teacher_id, int school_id);

	public boolean checkStudentRewardIdUnderToTeacher(Integer students_rewardids, Integer user_id);
	
	public List<Integer> getRewardsByAccountid(int account_id);

	//public boolean createTeachersOrStaff(List<TeachersStaffCSVModel> teachersInsertList, Users user);
	
	public Map<String,Object> createTeachersOrStaff(List<TeachersStaffCSVModel> teachersInsertList, Users loginUser);
	
	//public boolean updateTeachersOrStaff(List<TeachersStaffCSVModel> teachersUpdateList);
	
	public Map<String,Object> updateTeachersOrStaff(Users loginUser, List<TeachersStaffCSVModel> teachersUpdateList);
	
	public List<TeachersTransform> findTeachersStaffList(int userId, String roleType, String grade, int page_id, int total);
	
	public int deleteTeacherStaff(int userId);
	
	public String createTeachersOrStaffForWeb(TeacherStaffModel teacherStaffModel, Users loginUser);
	
	public Map<String,Object> createOrUpdateTeachersStaff(List<TeachersStaffCSVModel> teachersStaffList, Users loginUser);

	String assignRewardsToStudentsByTeacher(Integer rewardId, Integer studentId, Integer count, Users user);
}
