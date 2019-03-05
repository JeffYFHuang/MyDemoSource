package com.liteon.icgwearable.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.StudentCSVModel;
import com.liteon.icgwearable.model.StudentsModel;
import com.liteon.icgwearable.model.StudentsWebModel;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.StudentsMapLocationTransform;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;

public interface StudentsService {

	public List<Students> listAllStudents();

	public List<StudentsListTransform> findStudentsByAdminOrTeacher(int userId, String classId);

	public void addStudent(Students students, String uuid, String firmwareVersion);

	public void updateStudent(Students students);

	public Students getStudent(int id);
	
	public List<Integer> getGuardianIdsByStudentId(int student_id);

	// public boolean createStudents(List<StudentCSVModel> stList);

	public boolean createStudents(StudentCSVModel scsvm);

	public List<StudentsTransform> listStudentUniqueClass(int userId);

	public List<Students> findStudentsByClass(int schoolId, String classId, int pageNo);

	public List<TeachersStudentsTransform> findStudentsClassByTransformer(int schoolId, String classId, int pageNo);

	public StudentsMapLocationTransform viewStudentsLocation(String deviceUuid, int userId, String role);

	public int findSchoolId(int studentId);

	public Students getStudentForStudentsUpdate(int id);

	public int findStudentsBySessionId(int userId, String token);

	public HashMap<String, Object> getStudentAttendance(int school_id, String grade, String student_class,
			String inputdate);

	public HashMap<String, Object> getAbsentAbnormalStudents(int school_id, String grade, String student_class,
			String inputdate);

	public Map<String, Object> getStudentSOSAlertStats(int school_id, String inputdate);

	public Map<String, Object> getStudentSOSAlerts(int school_id, String inputdate);

	public Map<String, Object> searchStudents(int school_id, String studentName, String registrationNo,
			String deviceUUID, String roleType,int start,int total);
	
	public int  totalNooFStudents(int school_id, String studentName, String registrationNo,
			String deviceUUID, String roleType);

	public Map<String,Object> uploadStudentsAndLinkDevice(List<StudentCSVModel> studentCsvList, Users loginUser);

	public String createWebStudentsAndLinkDevice(StudentsWebModel studentsWebModel, Users loginUser) throws Exception;
	
	public List<StudentsListTransform> findStudentsByAdmin(int userId,String studentGrade, String stClass,int page_id, int total);
	
	public Students isRegistrationNumberExists(String regNo, Integer schoolId);
	
	public boolean updateStudentsApi(Users loginUser, StudentsModel studentsModel, Students st);
	
	public int deleteStudentApi(int studentId);

	public Map<String, Object> getStudentRewardRankings(int school_id, String startdate,  String enddate, String grade);

	public Map<String, Object> getStudentRewards(int student_id);
}
