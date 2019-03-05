package com.liteon.icgwearable.transform;

public class RewardsListTransform {
	
	@Override
	public String toString() {
		return "RewardsListTransform [categoryName=" + categoryName + ", rewardsCategoryId=" + rewardsCategoryId
				+ ", rewardName=" + rewardName + ", dateCreated=" + dateCreated + ", uniqueCategory=" + uniqueCategory
				+ ", rewardId=" + rewardId + ", icon=" + icon + ", rewardCount=" + rewardCount + ", rewardIconUrl="
				+ rewardIconUrl + "]";
	}
	private String categoryName;
	private Integer rewardsCategoryId;
	private String rewardName;
	private java.sql.Timestamp dateCreated;
	private String uniqueCategory;
	private Integer rewardId;
	private String icon;
	private Integer rewardCount;
	private String rewardIconUrl;
	private Integer rank;
	
	public String getIcon() {
		return icon;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public Integer getRewardCount() {
		return rewardCount;
	}
	public void setRewardCount(Integer rewardCount) {
		this.rewardCount = rewardCount;
	}
	public String getRewardIconUrl() {
		return rewardIconUrl;
	}
	public void setRewardIconUrl(String rewardIconUrl) {
		this.rewardIconUrl = rewardIconUrl;
	}
	public Integer getRewardId() {
		return rewardId;
	}
	public void setRewardId(Integer rewardId) {
		this.rewardId = rewardId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public String getUniqueCategory() {
		return uniqueCategory;
	}
	public void setUniqueCategory(String uniqueCategory) {
		this.uniqueCategory = uniqueCategory;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public Integer getRewardsCategoryId() {
		return rewardsCategoryId;
	}
	public void setRewardsCategoryId(Integer rewardsCategoryId) {
		this.rewardsCategoryId = rewardsCategoryId;
	}
	public String getRewardName() {
		return rewardName;
	}
	public void setRewardName(String rewardName) {
		this.rewardName = rewardName;
	}
	public java.sql.Timestamp getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(java.sql.Timestamp dateCreated) {
		this.dateCreated = dateCreated;
	} 
	
}
