package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
@Entity
@Table(name = "timetable")
public class Timetable implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -580681994427623291L;
	@Id
	@GeneratedValue
	@Column(name = "timetable_id", unique = true, nullable = false)
	private Integer timetableId;
	
	@ManyToOne
	@JoinColumn(name="class_grade_id")
	private ClassGrade classGrade;
	@Column(name = "subject_one")
	private String subjectOne;
	@Column(name = "subject_two")
	private String subjectTwo;
	@Column(name = "subject_three")
	private String subjectThree;
	@Column(name = "subject_four")
	private String subjectFour;
	@Column(name = "subject_five")
	private String subjectFive;
	@Column(name = "subject_six")
	private String subjectSix;
	@Column(name = "subject_seven")
	private String subjectSeven;
	@Column(name = "subject_eight")
	private String subjectEight;
	@Column(name = "week_day", columnDefinition = "enum('MON','TUE','WED','THU','FRI')")
	private String weekDay;
	@Column(name = "create_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	private Date createdDate;
	

	public Timetable() {}

	public ClassGrade getClassGrade() {
		return classGrade;
	}

	public void setClassGrade(ClassGrade classGrade) {
		this.classGrade = classGrade;
	}

	public Integer getTimetableId() {
		return this.timetableId;
	}

	public void setTimetableId(Integer timetableId) {
		this.timetableId = timetableId;
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

	public String getWeekDay() {
		return this.weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
