package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "reminders")
public class Reminders implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	@Column(name = "reminder_id")
	private int reminderId;
	
	@ManyToOne
	@JoinColumn(name="class_grade_id")
	private ClassGrade classGrade;
	
	/*		@ManyToOne
	@JoinColumn(name="teacher_id")
	private Users users;
	
	@ManyToOne
	@JoinColumn(name="school_id")
	private Accounts accounts;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="class")
	private Students students;*/
	
	@Column(name = "comments")
	private String comments;
	@Column(name = "image_name")
	@Lob
	private String image;
	@Column(name = "created_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
	private Date createdDate;
	@Column(name = "updated_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
	private Date updatedDate;
//	@Column(name = "class")
//	private Integer studentClass;
//	
	public Reminders() {}
	
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	/*public Integer getStudentClass() {
		return studentClass;
	}


	public void setStudentClass(Integer studentClass) {
		this.studentClass = studentClass;
	}
*/

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
/*
	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public Accounts getAccounts() {
		return accounts;
	}

	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}*/

	public int getReminderId() {
		return reminderId;
	}

	public void setReminderId(int reminderId) {
		this.reminderId = reminderId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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
	
	public ClassGrade getClassGrade() {
		return classGrade;
	}

	public void setClassGrade(ClassGrade classGrade) {
		this.classGrade = classGrade;
	}
}
