package com.liteon.icgwearable.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.AnnouncementDAO;
import com.liteon.icgwearable.hibernate.entity.Announcement;
import com.liteon.icgwearable.transform.AnnouncementTransform;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {
	@Autowired
	private AnnouncementDAO announcementDAO;

	@Override
	public List<Announcement> listOfAnnouncement(int school_id) {
		return this.announcementDAO.listOfAnnouncement(school_id);
	}

	@Override
	public Announcement getAnnouncement(int announcementId) {
		return this.announcementDAO.getAnnouncement(announcementId);
	}

	@Override
	public void deleteAnnouncement(Integer announcementId) {
		this.announcementDAO.deleteAnnouncement(announcementId);
	}

	@Override
	public void addAnnouncement(Announcement announcement) {
		this.announcementDAO.addAnnouncement(announcement);
	}

	@Override
	public List<AnnouncementTransform> getAnnouncements(int userId, String uuid) {
		return this.announcementDAO.getAnnouncements(userId, uuid);
	}

	@Override
	public boolean isClassExists(String stClass) {
		return this.announcementDAO.isClassExists(stClass);
	}

	@Override
	public List<AnnouncementTransform> getAnnouncementsForParent(int student_id, int user_id) {
		return this.announcementDAO.getAnnouncementsForParent(student_id, user_id);
	}

	@Override
	public boolean checkStudentIdExist(int student_id, int user_id) {
		return this.announcementDAO.checkStudentIdExist(student_id, user_id);
	}

	@Override
	public void updateAnnouncement(int announcement_id, String name, String description) {
		this.announcementDAO.updateAnnouncement(announcement_id, name, description);		
	}

	@Override
	public void deleteAnnouncement(int announcement_id) {
		this.announcementDAO.deleteAnnouncement(announcement_id);
	}

	@Override
	public void addAnnouncement(int accountId, String name, String description) {
		this.announcementDAO.addAnnouncement(accountId, name, description);		
	}

}
