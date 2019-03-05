package com.liteon.icgwearable.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.StudentsDAO;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.StudentCSVModel;
import com.liteon.icgwearable.model.StudentsModel;
import com.liteon.icgwearable.model.StudentsWebModel;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.StudentsMapLocationTransform;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;

@Service("studentsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class StudentsServiceImpl implements StudentsService {

	@Autowired
	private StudentsDAO studentsDAO;

	@Override
	public List<Students> listAllStudents() {
		return this.studentsDAO.listAllStudents();
	}

	@Override
	public void addStudent(Students students, String uuid, String firmwareVersion) {
		this.studentsDAO.addStudent(students, uuid, firmwareVersion);
	}

	@Override
	public void updateStudent(Students students) {
		this.studentsDAO.updateStudent(students);
	}

	@Override
	public Students getStudent(int id) {
		return this.studentsDAO.getStudent(id);
	}

	@Override
	public List<StudentsListTransform> findStudentsByAdminOrTeacher(int userId, String classId) {
		return this.studentsDAO.findStudentsByAdminOrTeacher(userId, classId);
	}

	@Override
	public List<StudentsTransform> listStudentUniqueClass(int userId) {
		return this.studentsDAO.listStudentUniqueClass(userId);
	}

	@Override
	public List<Students> findStudentsByClass(int schoolId, String classId, int pageNo) {
		return this.studentsDAO.findStudentsByClass(schoolId, classId, pageNo);
	}

	@Override
	public List<TeachersStudentsTransform> findStudentsClassByTransformer(int schoolId, String classId, int pageNo) {
		return this.studentsDAO.findStudentsClassByTransformer(schoolId, classId, pageNo);
	}

	@Override
	public StudentsMapLocationTransform viewStudentsLocation(String deviceUuid, int userId, String role) {
		return this.studentsDAO.viewStudentsLocation(deviceUuid, userId, role);
	}

	@Override
	public boolean createStudents(StudentCSVModel scsvm) {
		return this.studentsDAO.createStudents(scsvm);
	}

	@Override
	public int findSchoolId(int studentId) {
		return this.studentsDAO.findSchoolId(studentId);
	}

	@Override
	public Students getStudentForStudentsUpdate(int id) {
		return this.studentsDAO.getStudentForStudentsUpdate(id);
	}

	@Override
	public int findStudentsBySessionId(int userId, String token) {
		return this.studentsDAO.findStudentsBySessionId(userId, token);
	}

	@Override
	public HashMap<String, Object> getStudentAttendance(int school_id, String grade, String student_class,
			String inputdate) {
		return this.studentsDAO.getStudentAttendanceStats(school_id, grade, student_class, inputdate);
	}

	@Override
	public HashMap<String, Object> getAbsentAbnormalStudents(int school_id, String grade, String student_class,
			String inputdate) {
		return this.studentsDAO.getAbsentAbnormalStudentsList(school_id, grade, student_class, inputdate);
	}

	@Override
	public HashMap<String, Object> getStudentSOSAlertStats(int school_id, String inputdate) {
		return this.studentsDAO.getStudentSOSAlertStats(school_id, inputdate);
	}

	@Override
	public HashMap<String, Object> getStudentSOSAlerts(int school_id, String inputdate) {
		return this.studentsDAO.getStudentSOSAlerts(school_id, inputdate);
	}

	@Override
	public Map<String, Object> searchStudents(int school_id, String studentName, String registrationNo,
			String deviceUUID, String roleType, int start, int total) {
		return this.studentsDAO.searchStudents(school_id, studentName, registrationNo, deviceUUID, roleType, start,
				total);
	}

	@Override
	public Map<String, Object> uploadStudentsAndLinkDevice(List<StudentCSVModel> studentCsvList, Users loginUser) {
		return this.studentsDAO.uploadStudentsAndLinkDevice(studentCsvList, loginUser);
	}

	@Override
	public String createWebStudentsAndLinkDevice(StudentsWebModel studentsWebModel, Users loginUser) throws Exception {
		return this.studentsDAO.createWebStudentsAndLinkDevice(studentsWebModel, loginUser);
	}

	@Override
	public List<StudentsListTransform> findStudentsByAdmin(int userId, String studentGrade, String stClass, int page_id,
			int total) {
		return this.studentsDAO.findStudentsByAdmin(userId, studentGrade, stClass, page_id, total);
	}

	@Override
	public Students isRegistrationNumberExists(String regNo, Integer schoolId) {
		return this.studentsDAO.isRegistrationNumberExists(regNo, schoolId);
	}

	@Override
	public boolean updateStudentsApi(Users loginUser, StudentsModel studentsModel, Students st) {
		return this.studentsDAO.updateStudentsApi(loginUser, studentsModel, st);
	}

	@Override
	public int deleteStudentApi(int studentId) {
		return this.studentsDAO.deleteStudentApi(studentId);
	}

	@Override
	public HashMap<String, Object> getStudentRewardRankings(int school_id, String startdate, String enddate,
			String grade) {
		return this.studentsDAO.getStudentRewardRankings(school_id, startdate, enddate, grade);
	}

	@Override
	public Map<String, Object> getStudentRewards(int student_id) {
		return this.studentsDAO.getStudentRewards(student_id);
	}

	@Override
	public List<Integer> getGuardianIdsByStudentId(int student_id) {
		return this.studentsDAO.getGuardianIdsByStudentId(student_id);
	}

	@Override
	public int totalNooFStudents(int school_id, String studentName, String registrationNo, String deviceUUID,
			String roleType) {
		return this.studentsDAO.totalNooFStudents(school_id, studentName, registrationNo, deviceUUID, roleType);
	}

}
