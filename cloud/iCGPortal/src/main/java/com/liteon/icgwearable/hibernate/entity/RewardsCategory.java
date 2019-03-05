package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rewards_category")
public class RewardsCategory implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7652894587059540764L;
	
	@Id
	@GeneratedValue
	@Column(name = "rewards_category_id", unique = true, nullable = false)
	private int rewards_category_id;
	
	@ManyToOne
	@JoinColumn(name = "school_id", nullable = false)
	private Accounts accounts;
	
	@Column(name = "category_name")
	private String category_name;
	
	@Column(name = "category_icon_url")
	private String category_icon_url;
	
	@Column(name = "created_date", columnDefinition="DATETIME")
	private Date created_date;
	
	
	@Override
	public String toString() {
		return "RewardsCategory [rewards_category_id=" + rewards_category_id + ", accounts=" + accounts
				+ ", category_name=" + category_name + ", category_icon_url=" + category_icon_url + ", created_date="
				+ created_date + "]";
	}

	public int getRewards_category_id() {
		return rewards_category_id;
	}

	public String getCategory_icon_url() {
		return category_icon_url;
	}



	public void setCategory_icon_url(String category_icon_url) {
		this.category_icon_url = category_icon_url;
	}



	public void setRewards_category_id(int rewards_category_id) {
		this.rewards_category_id = rewards_category_id;
	}

	public Accounts getAccounts() {
		return accounts;
	}

	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

}
