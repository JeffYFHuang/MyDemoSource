package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class SchoolScheduleTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date dateclose;
	private Date datereopen;
	private Time schoolinstart;
	private Time schoolinend;
	private Time schooloutstart;
	private Time schooloutend;
	private String schoolId;


	public Date getDateclose() {
		return dateclose;
	}

	public void setDateclose(Date dateclose) {
		this.dateclose = dateclose;
	}

	public Date getDatereopen() {
		return datereopen;
	}

	public void setDatereopen(Date datereopen) {
		this.datereopen = datereopen;
	}

	public Time getSchoolinstart() {
		return schoolinstart;
	}

	public void setSchoolinstart(Time schoolinstart) {
		this.schoolinstart = schoolinstart;
	}

	public Time getSchoolinend() {
		return schoolinend;
	}

	public void setSchoolinend(Time schoolinend) {
		this.schoolinend = schoolinend;
	}

	public Time getSchooloutstart() {
		return schooloutstart;
	}

	public void setSchooloutstart(Time schooloutstart) {
		this.schooloutstart = schooloutstart;
	}

	public Time getSchooloutend() {
		return schooloutend;
	}

	public void setSchooloutend(Time schooloutend) {
		this.schooloutend = schooloutend;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	
}
