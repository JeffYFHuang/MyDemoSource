package com.liteon.icgwearable.hibernate.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "devices")
public class Devices implements java.io.Serializable {
    /**
	 * 
	 */
	public enum DeviceActive{
		y,n;
	}	
	private static final long serialVersionUID = -8444408809154161303L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "device_id", unique = true, nullable = false)
	private Integer deviceId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_configuration_id", nullable = false)
	private DeviceConfigurations deviceConfigurations;
	
	@Column(name="school_id")
	private Integer schoolId;
	
	@Column(name = "status", columnDefinition = "enum('broken','returned','assigned','')")
	private String status;
		   
	@Column(name = "uuid")
	private String uuid;
	@Column(name = "session_id")
	private String session_id;
	
	@Column(name = "session_expiry")
	private Timestamp session_expiry;

	@Column(name = "created_date", columnDefinition="DATETIME")
	private Timestamp createdDate;
	
	
	@Column(name = "updated_date", columnDefinition="DATETIME")
	private Timestamp updatedDate;
	
	
	public Devices() {}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public Timestamp getSession_expiry() {
		return session_expiry;
	}

	public void setSession_expiry(Timestamp session_expiry) {
		this.session_expiry = session_expiry;
	}
	
	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public DeviceConfigurations getDeviceConfigurations() {
		return deviceConfigurations;
	}

	public void setDeviceConfigurations(DeviceConfigurations deviceConfigurations) {
		this.deviceConfigurations = deviceConfigurations;
	}
}