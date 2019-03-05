package com.liteon.icgwearable.model;

import java.util.Date;

public class LatestRewardsModel {
	
	public Date getLatestDate() {
		return latestDate;
	}
	public void setLatestDate(Date latestDate) {
		this.latestDate = latestDate;
	}
	public int getRewardsCount() {
		return rewardsCount;
	}
	public void setRewardsCount(int rewardsCount) {
		this.rewardsCount = rewardsCount;
	}
	public String getRewardCategory() {
		return rewardCategory;
	}
	public void setRewardCategory(String rewardCategory) {
		this.rewardCategory = rewardCategory;
	}
	public String getRewardName() {
		return rewardName;
	}
	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}
	private Date latestDate;
	private Integer rewardsCount;
	private String rewardCategory;
	private String rewardName;
	
	
	

	

}
