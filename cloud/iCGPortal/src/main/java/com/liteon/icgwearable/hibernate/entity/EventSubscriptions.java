package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "event_subscriptions")
public class EventSubscriptions implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6066035206451693396L;
	@Id
	@GeneratedValue
	@Column(name = "notification_id")
	private Integer notificationId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private Students students;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private SupportedEvents events;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;
	@Column(name = "created_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	private Date createdDate;

	public EventSubscriptions() {}

	public EventSubscriptions(Date createdDate) {
		this.createdDate = createdDate;
	}

	public EventSubscriptions(SupportedEvents events, Users users, Date createdDate, Set deviceEventsQueues) {
		this.events = events;
		this.users = users;
		this.createdDate = createdDate;
	}
	public Integer getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Integer notificationId) {
		this.notificationId = notificationId;
	}


	public SupportedEvents getEvents() {
		return events;
	}

	public void setEvents(SupportedEvents events) {
		this.events = events;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Students getStudents() {
		return students;
	}

	public void setStudents(Students students) {
		this.students = students;
	}
}
