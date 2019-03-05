package com.liteon.icgwearable.dao;

import java.util.List;
import java.util.Map;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Timetable;
import com.liteon.icgwearable.model.TimeTableModel;
import com.liteon.icgwearable.transform.StudentTimeTableTransform;
import com.liteon.icgwearable.transform.StudentsClassListTransform;
import com.liteon.icgwearable.transform.TeacherTimeTableTransform;
import com.liteon.icgwearable.transform.TimetableTransform;

public interface TimetableDAO {
	public List<TimetableTransform> listTimetableUniqueClass(int userId);

	public List<Timetable> findtimeTableByClass(String studentClass);

	public Map<String, Object> createTimeTable(List<TimeTableModel> timeList, TimeTableModel timeTableModel,
			Accounts accounts);

	public List<Timetable> findTimeTableByClass(String studentClass);

	public void updateTimetable(Timetable timetable);

	public void deleteTimeTable(String studentClass, int userId);

	public Students checkClass(Integer studentId);

	public List<Timetable> listOfSubject(Accounts accounts, Integer studentClass);

	public List<StudentsClassListTransform> listOfClass(int userId);

	public List<TeacherTimeTableTransform> getTeacherClassTimetable(int teacher_id);

	public List<StudentTimeTableTransform> getStudentClassTimetable(int user_id, String uuid);

	public List<StudentTimeTableTransform> getTimeTable(String grade, String studentClass, Integer schoolId);
}
