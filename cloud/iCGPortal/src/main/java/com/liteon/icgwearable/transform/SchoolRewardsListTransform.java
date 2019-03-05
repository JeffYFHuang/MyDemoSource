package com.liteon.icgwearable.transform;

import java.math.BigInteger;

public class SchoolRewardsListTransform {
	
	private int rewards_category_id;
	private String category_name;
	private BigInteger noOfRewards;
	private java.sql.Timestamp date_created;
	private String icon;
	
	public int getRewards_category_id() {
		return rewards_category_id;
	}
	public void setRewards_category_id(int rewards_category_id) {
		this.rewards_category_id = rewards_category_id;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getCategory_name() {
		return category_name;
	}
	public java.sql.Timestamp getDate_created() {
		return date_created;
	}
	public void setDate_created(java.sql.Timestamp date_created) {
		this.date_created = date_created;
	}
	
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public BigInteger getNoOfRewards() {
		return noOfRewards;
	}
	public void setNoOfRewards(BigInteger noOfRewards) {
		this.noOfRewards = noOfRewards;
	}
	
}
