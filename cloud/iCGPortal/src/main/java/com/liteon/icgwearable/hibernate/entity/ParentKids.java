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


@Entity
@Table(name = "parent_kids")
public class ParentKids implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "parent_kid_id", unique = true, nullable = false)
	private Integer parentKidId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id")
	private Users users;
	
	@ManyToOne
	@JoinColumn(name="student_id")
	private Students students;
	
	@Column(name = "created_date", columnDefinition="DATETIME")
	private Date createdDate;
	
	public Integer getParentKidId() {
		return parentKidId;
	}

	public void setParentKidId(Integer parentKidId) {
		this.parentKidId = parentKidId;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Students getStudent() {
		return students;
	}

	public void setStudent(Students student) {
		this.students = student;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
