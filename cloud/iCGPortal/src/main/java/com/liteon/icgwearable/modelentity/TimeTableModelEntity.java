package com.liteon.icgwearable.modelentity;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.liteon.icgwearable.hibernate.entity.Timetable;
import com.liteon.icgwearable.model.TimeTableModel;
import com.liteon.icgwearable.model.TimeTablesModel;

public class TimeTableModelEntity {
	
	@Autowired
	private HttpSession httpSession;

	public List<TimeTablesModel> prepareTimeTablesModelList(List<Timetable> timetablesList , int slNo) {
		List<TimeTablesModel> timeTablesModelList = null;
		if (timetablesList != null && !timetablesList.isEmpty()) {
			timeTablesModelList = new ArrayList<>();
			TimeTablesModel timeTablesModel = null;

			for (Timetable timetables : timetablesList) {
				timeTablesModel = new TimeTablesModel();
				
				timeTablesModel.setSlNo(slNo);
				timeTablesModel.setTimetableId(timetables.getTimetableId());
				//timeTablesModel.setStudentClass(timetables.getStudentClass());
				timeTablesModel.setSubjectOne(timetables.getSubjectOne());
				timeTablesModel.setSubjectTwo(timetables.getSubjectTwo());
				timeTablesModel.setSubjectThree(timetables.getSubjectThree());
				timeTablesModel.setSubjectFour(timetables.getSubjectFour());
				timeTablesModel.setSubjectFive(timetables.getSubjectFive());
				timeTablesModel.setSubjectSix(timetables.getSubjectSix());
				timeTablesModel.setSubjectSeven(timetables.getSubjectSeven());
				timeTablesModel.setSubjectEight(timetables.getSubjectEight());
				timeTablesModelList.add(timeTablesModel);
				slNo++;
			}
			this.httpSession.setAttribute("slNo", slNo);
		}
		return timeTablesModelList;
	}
	public List<TimeTableModel> prepareTimeTableModelList(List<Timetable> timetablesList , int slNo) {
		List<TimeTableModel> timeTableModelList = null;
		if (timetablesList != null && !timetablesList.isEmpty()) {
			timeTableModelList = new ArrayList<>();
			TimeTableModel timeTableModel = null;

			for (Timetable timetable : timetablesList) {
				timeTableModel = new TimeTableModel();
				
				timeTableModel.setSlNo(slNo);
				timeTableModel.setWeekDay(timetable.getWeekDay());
				timeTableModel.setTimetableId(timetable.getTimetableId());
				//timeTableModel.setStudentClass(String.valueOf(timetable.getStudentClass()));
				timeTableModel.setSubjectOne(timetable.getSubjectOne());
				timeTableModel.setSubjectTwo(timetable.getSubjectTwo());
				timeTableModel.setSubjectThree(timetable.getSubjectThree());
				timeTableModel.setSubjectFour(timetable.getSubjectFour());
				timeTableModel.setSubjectFive(timetable.getSubjectFive());
				timeTableModel.setSubjectSix(timetable.getSubjectSix());
				timeTableModel.setSubjectSeven(timetable.getSubjectSeven());
				timeTableModel.setSubjectEight(timetable.getSubjectEight());
				timeTableModelList.add(timeTableModel);
				slNo++;
			}
			this.httpSession.setAttribute("slNo", slNo);
		}
		return timeTableModelList;
	}
	
}
