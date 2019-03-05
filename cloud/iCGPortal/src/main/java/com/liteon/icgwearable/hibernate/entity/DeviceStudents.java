package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;



@Entity
@Table(name = "device_students")
public class DeviceStudents implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7552097561581041101L;
 
	@Id
	@GeneratedValue
	@Column(name = "device_students_id", unique = true, nullable = false)
	private Integer deviceStudentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private Students students;
	@Column(name = "device_uuid")
	private String deviceuuid;
	@Column(name = "status", columnDefinition = "enum('active', 'inactive')")
	private String status;
	@Column(name = "start_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private Date startDate;
	
	@Column(name = "end_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="yyyy-MM-dd  HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
	private Date endDate;
	
	public DeviceStudents(){
		
	}
	public Integer getDeviceStudentId() {
		return deviceStudentId;
	}
	public void setDeviceStudentId(Integer deviceStudentId) {
		this.deviceStudentId = deviceStudentId;
	}
	public Students getStudents() {
		return students;
	}
	public void setStudents(Students students) {
		this.students = students;
	}
	public String getDeviceuuid() {
		return deviceuuid;
	}
	public void setDeviceuuid(String deviceuuid) {
		this.deviceuuid = deviceuuid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
