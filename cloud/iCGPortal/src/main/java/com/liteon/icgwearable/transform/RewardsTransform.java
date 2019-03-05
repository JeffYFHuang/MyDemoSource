package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class RewardsTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9178696858343584162L;
	
	public RewardsTransform(){}
	

	private String schoolname;
	private String kidname;
	private String nickname;
	private String kidclass;
	private String rewardname;
	private String categoryName;
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSchoolname() {
		return schoolname;
	}

	public void setSchoolname(String schoolname) {
		this.schoolname = schoolname;
	}

	public String getKidname() {
		return kidname;
	}

	public void setKidname(String kidname) {
		this.kidname = kidname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getKidclass() {
		return kidclass;
	}

	public void setKidclass(String kidclass) {
		this.kidclass = kidclass;
	}

	public String getRewardname() {
		return rewardname;
	}

	public void setRewardname(String rewardname) {
		this.rewardname = rewardname;
	}
}
