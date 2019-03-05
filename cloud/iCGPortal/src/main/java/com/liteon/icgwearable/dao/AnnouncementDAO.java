package com.liteon.icgwearable.dao;

import java.util.List;

import com.liteon.icgwearable.hibernate.entity.Announcement;
import com.liteon.icgwearable.transform.AnnouncementTransform;

public interface AnnouncementDAO {
	public List<Announcement> listOfAnnouncement(int school_id);

	public Announcement getAnnouncement(int announcementId);

	public void deleteAnnouncement(Integer announcementId);

	public void addAnnouncement(Announcement announcement);

	public List<AnnouncementTransform> getAnnouncements(int userId, String uuid);

	//public boolean isNameExist(String name);
	
	public boolean isClassExists(String stClass);

	public List<AnnouncementTransform> getAnnouncementsForParent(int student_id, int user_id);

	public boolean checkStudentIdExist(int student_id, int user_id);

	public void updateAnnouncement(int announcement_id, String name, String description);

	public void deleteAnnouncement(int announcement_id);

	public void addAnnouncement(int school_id, String name, String description);
}
