package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.util.Date;

public class RewardStatisticsTransform implements Serializable{
	
	private String category_name;
	private String rewards_name;
	private String student_name;
	private String studentClass;
	private Date created_date;
	//private String account_name;
	private String user_name;
	private String icon;
	
	public RewardStatisticsTransform(){}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getRewards_name() {
		return rewards_name;
	}

	public void setRewards_name(String rewards_name) {
		this.rewards_name = rewards_name;
	}

	public String getStudent_name() {
		return student_name;
	}

	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}

	public String getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

/*	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}
*/
	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
