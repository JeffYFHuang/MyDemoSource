package com.liteon.icgwearable.transform;

import java.util.Date;

public class StudentRewardsTransform {

	private Integer rewardId;
	private String rewardname;
	private String categoryName;
	private String categoryIconUrl;
	private Integer receivedCount;
	private String comments;
	private String reward_icon_url;
	private String school_teacher;
	private Date reward_date;
	
	
	public String getSchool_teacher() {
		return school_teacher;
	}
	public void setSchool_teacher(String school_teacher) {
		this.school_teacher = school_teacher;
	}
	
	public Date getReward_date() {
		return reward_date;
	}
	public void setReward_date(Date reward_date) {
		this.reward_date = reward_date;
	}
	public String getComments() {
		return comments;
	}
	public String getReward_icon_url() {
		return reward_icon_url;
	}
	public void setReward_icon_url(String reward_icon_url) {
		this.reward_icon_url = reward_icon_url;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Integer getReceivedCount() {
		return receivedCount;
	}
	public void setReceivedCount(Integer receivedCount) {
		this.receivedCount = receivedCount;
	}
	public Integer getRewardId() {
		return rewardId;
	}
	public void setRewardId(Integer rewardId) {
		this.rewardId = rewardId;
	}
	public String getRewardname() {
		return rewardname;
	}
	public void setRewardname(String rewardname) {
		this.rewardname = rewardname;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryIconUrl() {
		return categoryIconUrl;
	}
	public void setCategoryIconUrl(String categoryIconUrl) {
		this.categoryIconUrl = categoryIconUrl;
	}
	
	
	
}
