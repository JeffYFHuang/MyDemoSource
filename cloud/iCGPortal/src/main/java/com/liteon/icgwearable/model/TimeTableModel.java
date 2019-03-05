package com.liteon.icgwearable.model;

public class TimeTableModel {

	private Integer slNo;
	private Integer timetableId;
	private Integer schoolId;
	private Integer userId;
	private String studentClass;
	private String weekDay;
	private String subjectOne;
	private String subjectTwo;
	private String subjectThree;
	private String subjectFour;
	private String subjectFive;
	private String subjectSix;
	private String subjectSeven;
	private String subjectEight;
	private String selectClass;
	private String grade;
	
	public TimeTableModel() {}
	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getSlNo() {
		return slNo;
	}

	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}

	public String getSelectClass() {
		return selectClass;
	}

	public void setSelectClass(String selectClass) {
		this.selectClass = selectClass;
	}

	public Integer getTimetableId() {
		return timetableId;
	}

	public void setTimetableId(Integer timetableId) {
		this.timetableId = timetableId;
	}

	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getSubjectOne() {
		return subjectOne;
	}

	public void setSubjectOne(String subjectOne) {
		this.subjectOne = subjectOne;
	}

	public String getSubjectTwo() {
		return subjectTwo;
	}

	public void setSubjectTwo(String subjectTwo) {
		this.subjectTwo = subjectTwo;
	}

	public String getSubjectThree() {
		return subjectThree;
	}

	public void setSubjectThree(String subjectThree) {
		this.subjectThree = subjectThree;
	}

	public String getSubjectFour() {
		return subjectFour;
	}

	public void setSubjectFour(String subjectFour) {
		this.subjectFour = subjectFour;
	}

	public String getSubjectFive() {
		return subjectFive;
	}

	public void setSubjectFive(String subjectFive) {
		this.subjectFive = subjectFive;
	}

	public String getSubjectSix() {
		return subjectSix;
	}

	public void setSubjectSix(String subjectSix) {
		this.subjectSix = subjectSix;
	}

	public String getSubjectSeven() {
		return subjectSeven;
	}

	public void setSubjectSeven(String subjectSeven) {
		this.subjectSeven = subjectSeven;
	}

	public String getSubjectEight() {
		return subjectEight;
	}

	public void setSubjectEight(String subjectEight) {
		this.subjectEight = subjectEight;
	}


	@Override
	public String toString() {
		return "TimeTableModel [studentClass=" + studentClass + ", subjectOne=" + subjectOne + ", subjectTwo="
				+ subjectTwo + ", subjectThree=" + subjectThree + ", subjectFour=" + subjectFour + ", subjectFive="
				+ subjectFive + ", subjectSix=" + subjectSix + ", subjectSeven=" + subjectSeven + ", subjectEight="
				+ subjectEight + ", grade=" + grade + "]";
	}
}
