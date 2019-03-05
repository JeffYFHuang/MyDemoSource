package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "system_configuration")
public class SystemConfiguration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4217199944863510818L;
	
	@Id
	@GeneratedValue
	@Column(name = "system_configuration_id", unique = true, nullable = false)
	private Integer systemConfigurationId;
	
	@Column(name="iwps_sync_hours")
	private Integer iwpsSyncHours;
	
	@Column(name="web_session_validity_minutes")
	private Integer webSessionValidityMinutes;

	@Column(name="wearable_session_validity_minutes")
	private Integer wearableSessionValidityMinutes;
	
	@Column(name="password_reset_validity_minutes")
	private Integer passwordResetValidityMinutes;
	
	@Column(name = "student_allergies")
	private String studentAllergies;
	
	@Column(name = "source_date_format")
	private String sourceDateFormat;
	
	@Column(name = "source_datetime_format")
	private String sourceDatetimeFormat;
	
	@Column(name = "db_date_format")
	private String dbDateFormat;
	
	@Column(name = "db_datetime_format")
	private String dbDatetimeFormat;
	
	public Integer getSystemConfigurationId() {
		return systemConfigurationId;
	}

	public void setSystemConfigurationId(Integer systemConfigurationId) {
		this.systemConfigurationId = systemConfigurationId;
	}

	public Integer getIwpsSyncHours() {
		return iwpsSyncHours;
	}

	public void setIwpsSyncHours(Integer iwpsSyncHours) {
		this.iwpsSyncHours = iwpsSyncHours;
	}

	public Integer getWebSessionValidityMinutes() {
		return webSessionValidityMinutes;
	}

	public void setWebSessionValidityMinutes(Integer webSessionValidityMinutes) {
		this.webSessionValidityMinutes = webSessionValidityMinutes;
	}

	public Integer getWearableSessionValidityMinutes() {
		return wearableSessionValidityMinutes;
	}

	public void setWearableSessionValidityMinutes(Integer wearableSessionValidityMinutes) {
		this.wearableSessionValidityMinutes = wearableSessionValidityMinutes;
	}

	public Integer getPasswordResetValidityMinutes() {
		return passwordResetValidityMinutes;
	}

	public void setPasswordResetValidityMinutes(Integer passwordResetValidityMinutes) {
		this.passwordResetValidityMinutes = passwordResetValidityMinutes;
	}

	public String getStudentAllergies() {
		return studentAllergies;
	}

	public void setStudentAllergies(String studentAllergies) {
		this.studentAllergies = studentAllergies;
	}

	public String getSourceDateFormat() {
		return sourceDateFormat;
	}

	public void setSourceDateFormat(String sourceDateFormat) {
		this.sourceDateFormat = sourceDateFormat;
	}

	public String getSourceDatetimeFormat() {
		return sourceDatetimeFormat;
	}

	public void setSourceDatetimeFormat(String sourceDatetimeFormat) {
		this.sourceDatetimeFormat = sourceDatetimeFormat;
	}

	public String getDbDateFormat() {
		return dbDateFormat;
	}

	public void setDbDateFormat(String dbDateFormat) {
		this.dbDateFormat = dbDateFormat;
	}

	public String getDbDatetimeFormat() {
		return dbDatetimeFormat;
	}

	public void setDbDatetimeFormat(String dbDatetimeFormat) {
		this.dbDatetimeFormat = dbDatetimeFormat;
	}

}
