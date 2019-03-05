package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "class_grade")
public class ClassGrade implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1043910018788108273L;
		
	@Id
	@GeneratedValue
	@Column(name = "class_grade_id", unique = true, nullable = false)
	private Integer classGradeId;
	@Column(name="school_id")
	private Integer schoolId;
	@Column(name="grade")
	private String grade;
	@Column(name="class")
	private String studentClass;
	@Column(name="teacher_id")
	private Integer teacher_id;
	@Column(name = "created_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date created_date;
	@Column(name = "updated_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date updated_date;
	@OneToMany(mappedBy = "classGrade")
	@JsonIgnore
	private Set<Reminders> reminders = new HashSet<Reminders>(0);
	@OneToMany(mappedBy = "classGrade")
	@JsonIgnore
	private Set<Timetable> timeTable = new HashSet<Timetable>(0);
	@OneToMany(mappedBy = "classGrade")
	@JsonIgnore
	private Set<Students> students = new HashSet<Students>(0);
	
	public Set<Students> getStudents() {
		return students;
	}

	public void setStudents(Set<Students> students) {
		this.students = students;
	}

	public Set<Timetable> getTimeTable() {
		return timeTable;
	}

	public void setTimeTable(Set<Timetable> timeTable) {
		this.timeTable = timeTable;
	}

	public Set<Reminders> getReminders() {
		return reminders;
	}

	public void setReminders(Set<Reminders> reminders) {
		this.reminders = reminders;
	}

	public ClassGrade() {}
	
	public Integer getClassGradeId() {
		return classGradeId;
	}

	public void setClassGradeId(Integer classGradeId) {
		this.classGradeId = classGradeId;
	}

	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	public Integer getTeacher_id() {
		return teacher_id;
	}

	public void setTeacher_id(Integer teacher_id) {
		this.teacher_id = teacher_id;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}
}
