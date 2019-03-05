package com.liteon.icgwearable.model;

import java.util.Date;

public class LatestReminderModel {
	
	private Date latestDate;
	private String comments;
	
	public Date getLatestDate() {
		return latestDate;
	}
	public void setLatestDate(Date latestDate) {
		this.latestDate = latestDate;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}


}
