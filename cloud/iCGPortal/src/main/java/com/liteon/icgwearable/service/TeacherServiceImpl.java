package com.liteon.icgwearable.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.liteon.icgwearable.dao.TeacherDAO;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
//import com.liteon.icgwearable.hibernate.entity.SchoolAppPreferences;
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

@Service("teacherService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class TeacherServiceImpl implements TeacherService {
	@Autowired
    private TeacherDAO teacherDAO;
	
	@Override
	public List<Users> listTeachersByPage(int i) {
		// TODO Auto-generated method stub
		return this.teacherDAO.listTeachersByPage(i);
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void addTeacher(Users users) {
		// TODO Auto-generated method stub
		this.teacherDAO.addTeacher(users);
	}

	@Override
	public Users getTeacher(int id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getTeacher(id);
	}
	@Override
	public void addUITeacher(Users users, String uuid) {
		// TODO Auto-generated method stub
		this.teacherDAO.addUITeacher(users, uuid);
	}
	@Override
	public void updateTeacherStaffApi(Users users, ClassGrade cg, UsersModel usersModel) {
		// TODO Auto-generated method stub
		this.teacherDAO.updateTeacherStaffApi(users, cg, usersModel);
	}
	@Override
	public List<TeachersTransform> listAllTeachers(int userId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.listAllTeachers(userId);
	}
	@Override
	public List<Users> listTeachersById(int id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.listTeachersById(id);
	}
	@Override
	public List<StudentsTransform> findStudentsClass(int userId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.findStudentsClass(userId);
	}
	@Override
	public List<TeachersStudentsTransform> findStudentsByTeacher(int userId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.findStudentsByTeacher(userId);
	}


	@Override
	public boolean createTeachers(TeacherCSVModel tcsvm, int accountId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.createTeachers(tcsvm, accountId);
	}


	/*@Override
	public List<SchoolAppPreferences> getSchoolAppPreferencesByUserId(int userId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getSchoolAppPreferencesByUserId(userId);
	}*/


	@Override
	public void deletetSchoolAppPreferencesByUserId(int userId, String QuiteHoursday) {
		this.teacherDAO.deletetSchoolAppPreferencesByUserId(userId, QuiteHoursday);
		
	}


	@Override
	public void addSchoolAppPreferencesByUserId(Users user, String QuiteHoursday, int QuiteHourFrom,
			int QuiteHourTo) {
		this.teacherDAO.addSchoolAppPreferencesByUserId(user, QuiteHoursday, QuiteHourFrom, QuiteHourTo);
		
	}


	/*@Override
	public void UpdateSchoolAppPreferencesByUserId(SchoolAppPreferences sap) {
		// TODO Auto-generated method stub
		this.teacherDAO.UpdateSchoolAppPreferencesByUserId(sap);
		
	}*/


	@Override
	public List<TeacherRewardsListTransform> getSchoolRewardsToTeacherBySchoolId(int school_id,  int category_id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getSchoolRewardsToTeacherBySchoolId(school_id,category_id);
	}


	@Override
	public String assignRewardsToStudentsByTeacher(Integer rewardId, Integer studentId,Integer count, Users user) {
		// TODO Auto-generated method stub
		
		return this.teacherDAO.assignRewardsToStudentsByTeacher(rewardId, studentId,count, user);
		
	}


	@Override
	public String deleteStudentsRewardsByTeacher(Integer students_rewardids, Integer user_id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.deleteStudentsRewardsByTeacher(students_rewardids, user_id);
		
	}
	
	@Override
	public int getAccountId(Integer studentId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getAccountId(studentId);
	}
	
	@Override
	public boolean checkRewardsId(int rewardId) {
		return this.teacherDAO.checkRewardsId(rewardId);
	}

	@Override
	public List<RewardStatisticsTransform> getRewardsStatisticsForTeacher(int account_id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getRewardsStatisticsForTeacher(account_id);
	}


	@Override
	public List<RewardStatisticsTransform> getStudentNameForTeacher(int account_id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getStudentNameForTeacher(account_id);
	}


	@Override
	public List<RewardStatisticsTransform> getClassNameForTeacher(int account_id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getClassNameForTeacher(account_id);
	}


	@Override
	public List<RewardStatisticsTransform> getStudentName(int account_id, String day, String studentClass,
			String studentName) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getStudentName(account_id, day, studentClass, studentName);
	}


	@Override
	public boolean checkStudentRewardId(int studentRewardId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.checkStudentRewardId(studentRewardId);
	}


	@Override
	public List<TeacherRewardsAssignedToStudentsTransform> StudentRewardsByTeacher(int teacher_id, int school_id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.StudentRewardsByTeacher(teacher_id, school_id);
	}
	@Override
	public boolean checkStudentRewardIdUnderToTeacher(Integer students_rewardids, Integer user_id){
		return this.teacherDAO.checkStudentRewardIdUnderToTeacher(students_rewardids, user_id);
	}


	@Override
	public List<Integer> getRewardsByAccountid(int account_id) {
		// TODO Auto-generated method stub
		return this.teacherDAO.getRewardsByAccountid(account_id);
	}


	@Override
	public Map<String,Object> createTeachersOrStaff(List<TeachersStaffCSVModel> teachersInsertList, Users loginUser) {
		// TODO Auto-generated method stub
		return this.teacherDAO.createTeachersOrStaff(teachersInsertList, loginUser);
	}


	@Override
	public Map<String,Object> updateTeachersOrStaff(Users loginUser, List<TeachersStaffCSVModel> teachersUpdateList) {
		// TODO Auto-generated method stub
		return this.teacherDAO.updateTeachersOrStaff(loginUser, teachersUpdateList);
	}


	@Override
	public List<TeachersTransform> findTeachersStaffList(int userId, String roleType, String grade, int page_id, int total) {
		// TODO Auto-generated method stub
		return this.teacherDAO.findTeachersStaffList(userId, roleType, grade, page_id, total);
	}


	@Override
	public int deleteTeacherStaff(int userId) {
		// TODO Auto-generated method stub
		return this.teacherDAO.deleteTeacherStaff(userId);
	}


	@Override
	public String createTeachersOrStaffForWeb(TeacherStaffModel teacherStaffModel, Users loginUser) {
		// TODO Auto-generated method stub
		return this.teacherDAO.createTeachersOrStaffForWeb(teacherStaffModel, loginUser);
	}


	@Override
	public Map<String, Object> createOrUpdateTeachersStaff(List<TeachersStaffCSVModel> teachersStaffList,
			Users loginUser) {
		// TODO Auto-generated method stub
		return this.teacherDAO.createOrUpdateTeachersStaff(teachersStaffList, loginUser);
	}


	@Override
	public String assignRewardsToStudentsByTeacher(Integer rewardId, Integer studentId, Integer count, int teacherId) {
		return this.teacherDAO.assignRewardsToStudentsByTeacher(rewardId, studentId, count, teacherId);
	}
}
