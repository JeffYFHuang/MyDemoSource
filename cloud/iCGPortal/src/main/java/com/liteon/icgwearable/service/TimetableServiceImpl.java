package com.liteon.icgwearable.service;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.TimetableDAO;
import com.liteon.icgwearable.dao.UserDAO;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Timetable;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.TimeTableModel;
import com.liteon.icgwearable.transform.StudentTimeTableTransform;
import com.liteon.icgwearable.transform.StudentsClassListTransform;
import com.liteon.icgwearable.transform.TeacherTimeTableTransform;
import com.liteon.icgwearable.transform.TimetableTransform;

@Service("timetableService")
@Transactional
public class TimetableServiceImpl implements TimetableService {

	@Autowired
	private TimetableDAO timetableDAO;

	@Autowired
	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setTimetableDAO(TimetableDAO timetableDAO) {
		this.timetableDAO = timetableDAO;
	}

	@Override
	public List<TimetableTransform> listTimetableUniqueClass(int userId) {
		return timetableDAO.listTimetableUniqueClass(userId);
	}

	@Override
	public List<Timetable> findtimeTableByClass(String studentClass) {
		return timetableDAO.findtimeTableByClass(studentClass);
	}

	@Override
	public Map<String, Object> createTimeTable(List<TimeTableModel> timeList, TimeTableModel timeTableModel) {
		Accounts accounts = userDAO.getAccountByUserId(timeTableModel.getUserId());
		return timetableDAO.createTimeTable(timeList, timeTableModel, accounts);
	}

	@Override
	public List<Timetable> findTimeTableByClass(String studentClass) {
		return timetableDAO.findTimeTableByClass(studentClass);
	}

	@Override
	public void updateTimetable(TimeTableModel timeTableModel, HttpServletRequest request) {
		Timetable timetable1 = null;
		Timetable timetable2 = null;
		Timetable timetable3 = null;
		Timetable timetable4 = null;
		Timetable timetable5 = null;

		Accounts accounts = userDAO.getAccountByUserId(timeTableModel.getUserId());
		Users users = new Users();
		users.setId(timeTableModel.getUserId());
		users.setAccounts(accounts);
		users.setUserActive("y");
		users.setRoleType("school_admin");

		timetable1 = new Timetable();

		timetable1.setTimetableId(Integer.parseInt(request.getParameter("timeTableOne")));
		timetable1.setWeekDay(request.getParameter("weekdayOne"));
		timetable1.setSubjectOne(request.getParameter("subjectMONOne"));
		timetable1.setSubjectTwo(request.getParameter("subjectMONTwo"));
		timetable1.setSubjectThree(request.getParameter("subjectMONThree"));
		timetable1.setSubjectFour(request.getParameter("subjectMONFour"));
		timetable1.setSubjectFive(request.getParameter("subjectMONFive"));
		timetable1.setSubjectSix(request.getParameter("subjectMONSix"));
		timetable1.setSubjectSeven(request.getParameter("subjectMONSeven"));
		timetable1.setSubjectEight(request.getParameter("subjectMONEight"));

		timetableDAO.updateTimetable(timetable1);

		timetable2 = new Timetable();

		timetable2.setTimetableId(Integer.parseInt(request.getParameter("timeTableTwo")));
		timetable2.setWeekDay(request.getParameter("weekdayTwo"));
		timetable2.setSubjectOne(request.getParameter("subjectTUEOne"));
		timetable2.setSubjectTwo(request.getParameter("subjectTUETwo"));
		timetable2.setSubjectThree(request.getParameter("subjectTUEThree"));
		timetable2.setSubjectFour(request.getParameter("subjectTUEFour"));
		timetable2.setSubjectFive(request.getParameter("subjectTUEFive"));
		timetable2.setSubjectSix(request.getParameter("subjectTUESix"));
		timetable2.setSubjectSeven(request.getParameter("subjectTUESeven"));
		timetable2.setSubjectEight(request.getParameter("subjectTUEEight"));

		timetableDAO.updateTimetable(timetable2);

		timetable3 = new Timetable();

		timetable3.setTimetableId(Integer.parseInt(request.getParameter("timeTableThree")));
		timetable3.setWeekDay(request.getParameter("weekdayThree"));
		timetable3.setSubjectOne(request.getParameter("subjectWEDOne"));
		timetable3.setSubjectTwo(request.getParameter("subjectWEDTwo"));
		timetable3.setSubjectThree(request.getParameter("subjectWEDThree"));
		timetable3.setSubjectFour(request.getParameter("subjectWEDFour"));
		timetable3.setSubjectFive(request.getParameter("subjectWEDFive"));
		timetable3.setSubjectSix(request.getParameter("subjectWEDSix"));
		timetable3.setSubjectSeven(request.getParameter("subjectWEDSeven"));
		timetable3.setSubjectEight(request.getParameter("subjectWEDEight"));

		timetableDAO.updateTimetable(timetable3);

		timetable4 = new Timetable();

		timetable4.setTimetableId(Integer.parseInt(request.getParameter("timeTableFour")));
		timetable4.setWeekDay(request.getParameter("weekdayFour"));
		timetable4.setSubjectOne(request.getParameter("subjectTHUOne"));
		timetable4.setSubjectTwo(request.getParameter("subjectTHUTwo"));
		timetable4.setSubjectThree(request.getParameter("subjectTHUThree"));
		timetable4.setSubjectFour(request.getParameter("subjectTHUFour"));
		timetable4.setSubjectFive(request.getParameter("subjectTHUFive"));
		timetable4.setSubjectSix(request.getParameter("subjectTHUSix"));
		timetable4.setSubjectSeven(request.getParameter("subjectTHUSeven"));
		timetable4.setSubjectEight(request.getParameter("subjectTHUEight"));

		timetableDAO.updateTimetable(timetable4);

		timetable5 = new Timetable();

		timetable5.setTimetableId(Integer.parseInt(request.getParameter("timeTableFive")));
		timetable5.setWeekDay(request.getParameter("weekdayFive"));
		timetable5.setSubjectOne(request.getParameter("subjectFRIOne"));
		timetable5.setSubjectTwo(request.getParameter("subjectFRITwo"));
		timetable5.setSubjectThree(request.getParameter("subjectFRIThree"));
		timetable5.setSubjectFour(request.getParameter("subjectFRIFour"));
		timetable5.setSubjectFive(request.getParameter("subjectFRIFive"));
		timetable5.setSubjectSix(request.getParameter("subjectFRISix"));
		timetable5.setSubjectSeven(request.getParameter("subjectFRISeven"));
		timetable5.setSubjectEight(request.getParameter("subjectFRIEight"));

		timetableDAO.updateTimetable(timetable5);
	}

	@Override
	public void deleteTimeTable(String studentClass, int userId) {
		timetableDAO.deleteTimeTable(studentClass, userId);
	}

	@Override
	public Students checkClass(int studentId) {
		return timetableDAO.checkClass(studentId);
	}

	@Override
	public List<Timetable> listOfSubject(Accounts accounts, Integer studentClass) {
		return timetableDAO.listOfSubject(accounts, studentClass);
	}

	@Override
	public List<StudentsClassListTransform> listOfClass(int userId) {
		return timetableDAO.listOfClass(userId);
	}

	@Override
	public List<TeacherTimeTableTransform> getTeacherClassTimetable(int teacher_id) {
		return timetableDAO.getTeacherClassTimetable(teacher_id);
	}

	@Override
	public List<StudentTimeTableTransform> getStudentClassTimetable(int user_id, String uuid) {
		return timetableDAO.getStudentClassTimetable(user_id, uuid);
	}

	@Override
	public List<StudentTimeTableTransform> getTimeTable(String grade, String studentClass, Integer schoolId) {
		return timetableDAO.getTimeTable(grade, studentClass, schoolId);
	}
}
