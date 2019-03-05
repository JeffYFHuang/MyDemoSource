package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

	@Id
	@GeneratedValue
	@Column(name = "activity_log_id", unique = true, nullable = false)
	private Integer activityLogId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "username")
	private String userName;
	
	@Column(name = "user_role", columnDefinition="enum('parent_admin','parent_member','school_admin','school_teacher','school_staff','super_admin')")
	private String userRole;
	
	@Column(name = "level", columnDefinition="enum('Info','Error','Crtical')")
	private String level;
	
	@Column(name = "action", columnDefinition="enum('UserLogin','UserLogout','Update','Create','Delete','PushNotification','EmailNotification')")
	private String action;
	
	@Column(name = "ipaddress")
	private String ipaddress;
	
	@Column(name = "summary")
	private String summary;
	
	@Column(name = "created_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
	private Date createDate;
	
	public Integer getActivityLogId() {
		return activityLogId;
	}

	public void setActivityLogId(Integer activityLogId) {
		this.activityLogId = activityLogId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "ActivityLog [activityLogId=" + activityLogId + ", name=" + name + ", userName=" + userName
				+ ", userRole=" + userRole + ", level=" + level + ", action=" + action + ", ipaddress=" + ipaddress
				+ ", summary=" + summary + ", createDate=" + createDate + "]";
	}

	
}
