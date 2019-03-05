package com.liteon.icgwearable.transform;

import java.math.BigDecimal;
import java.util.Date;

public class RewardsCategoryTransform {
	
	private Integer category_id;
	private String category_name;
	private String category_icon_url;
	private BigDecimal reward_category_count;
	private Integer reward_id;
	private String name;
	private String reward_icon_url;
	private Integer reward_count;
	private String comments;
	private Date created_date;
	
	public RewardsCategoryTransform() {}
	
	public BigDecimal getReward_category_count() {
		return reward_category_count;
	}

	public void setReward_category_count(BigDecimal reward_category_count) {
		this.reward_category_count = reward_category_count;
	}

	public Integer getCategory_id() {
		return category_id;
	}
	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public String getCategory_icon_url() {
		return category_icon_url;
	}
	public void setCategory_icon_url(String category_icon_url) {
		this.category_icon_url = category_icon_url;
	}
	public Integer getReward_id() {
		return reward_id;
	}
	public void setReward_id(Integer reward_id) {
		this.reward_id = reward_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReward_icon_url() {
		return reward_icon_url;
	}
	public void setReward_icon_url(String reward_icon_url) {
		this.reward_icon_url = reward_icon_url;
	}
	public Integer getReward_count() {
		return reward_count;
	}
	public void setReward_count(Integer reward_count) {
		this.reward_count = reward_count;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
}
