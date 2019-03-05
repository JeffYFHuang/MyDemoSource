package com.liteon.icgwearable.transform;

import java.sql.Timestamp;

public class AnnouncementTransform {

	private String school_name;
	private String kid_name;
	private String kid_nickname;
	private String announcement_title;
	private String announcement_description;
	private Timestamp createdDate;
	private Timestamp updatedDate;
	private String displayDate;
	
	public String getDisplayDate() {
		return displayDate;
	}

	public void setDisplayDate(String displayDate) {
		this.displayDate = displayDate;
	}

	public Timestamp getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Timestamp updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public AnnouncementTransform() {}
	
	public String getSchool_name() {
		return school_name;
	}
	public void setSchool_name(String school_name) {
		this.school_name = school_name;
	}
	public String getKid_name() {
		return kid_name;
	}
	public void setKid_name(String kid_name) {
		this.kid_name = kid_name;
	}
	public String getKid_nickname() {
		return kid_nickname;
	}
	public void setKid_nickname(String kid_nickname) {
		this.kid_nickname = kid_nickname;
	}
	public String getAnnouncement_title() {
		return announcement_title;
	}
	public void setAnnouncement_title(String announcement_title) {
		this.announcement_title = announcement_title;
	}
	public String getAnnouncement_description() {
		return announcement_description;
	}
	public void setAnnouncement_description(String announcement_description) {
		this.announcement_description = announcement_description;
	}
}
