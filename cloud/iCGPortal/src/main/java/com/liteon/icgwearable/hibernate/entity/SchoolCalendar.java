package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;


@Entity
@Table(name ="school_calendar")
public class SchoolCalendar implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2021177889314475345L;
	
	@Id
	@GeneratedValue
	@Column(name = "school_calendar_id", unique = true, nullable = false)
	private Integer schoolCalendarId;
	
	@Column(name="school_id")
	private Integer schoolId;
	
	@Column(name = "name")
	private String name;

	@Column(name = "date_close")
	private Date dateclose;
	
	@Column(name = "date_reopen")
	private Date datereopen;
	
	@Column(name = "created_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date created_date;
	
	@Column(name = "updated_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date updated_date;
	
	 @Transient 
	 String successMessage;
	 
	 @Transient 
	 boolean validEntry;
	
	public boolean isValidEntry() {
		return validEntry;
	}

	public void setValidEntry(boolean validEntry) {
		this.validEntry = validEntry;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public Integer getSchoolCalendarId() {
		return schoolCalendarId;
	}

	public void setSchoolCalendarId(Integer schoolCalendarId) {
		this.schoolCalendarId = schoolCalendarId;
	}

	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	@Override
	public String toString() {
		return "SchoolCalendar [schoolCalendarId=" + schoolCalendarId + ", schoolId=" + schoolId + ", name=" + name
				+ ", dateclose=" + dateclose + ", datereopen=" + datereopen + ", created_date=" + created_date
				+ ", updated_date=" + updated_date + "]";
	}

	

}
