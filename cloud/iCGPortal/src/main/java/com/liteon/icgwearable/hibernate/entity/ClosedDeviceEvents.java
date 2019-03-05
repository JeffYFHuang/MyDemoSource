package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;


@Entity(name = "ClosedDeviceEvents")
@Table(name = "closed_device_events")
public class ClosedDeviceEvents implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false)
	private Integer id;
	

	@Column(name = "device_event_id", nullable = false)
	private Integer deviceEventId;
	
	@Column(name="parent_id")
	private Integer parentId;
	
	@Column(name="admin_id")
	private Integer adminId;
	
	@Column(name="staff_id")
	private Integer staffId;
	
	@Column(name = "is_sos_abnormal", columnDefinition="enum('yes','no','')")
	private String is_sos_abnormal;
	
	public String getIs_sos_abnormal() {
		return is_sos_abnormal;
	}

	public void setIs_sos_abnormal(String is_sos_abnormal) {
		this.is_sos_abnormal = is_sos_abnormal;
	}

	public Date getParentClosedDate() {
		return parentClosedDate;
	}

	public void setParentClosedDate(Date parentClosedDate) {
		this.parentClosedDate = parentClosedDate;
	}

	public Date getAdminClosedDate() {
		return adminClosedDate;
	}

	public void setAdminClosedDate(Date adminClosedDate) {
		this.adminClosedDate = adminClosedDate;
	}

	public Date getStaffClosedDate() {
		return staffClosedDate;
	}

	public void setStaffClosedDate(Date staffClosedDate) {
		this.staffClosedDate = staffClosedDate;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@Column(name = "parent_closed_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date parentClosedDate;
	
	@Column(name = "admin_closed_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date adminClosedDate;
	
	@Column(name = "staff_closed_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date staffClosedDate;
	
	@Column(name = "duration")

	private Integer duration;
	
	
	@Column(name = "created_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date createdDate;
	
	@Column(name = "updated_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	private Date updatedDate;
	
	public Integer getDeviceEventId() {
		return deviceEventId;
	}

	public void setDeviceEventId(Integer deviceEventId) {
		this.deviceEventId = deviceEventId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}



	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	
	
}
