package com.liteon.icgwearable.modelentity;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Announcement;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.AnnouncementModel;
import com.liteon.icgwearable.service.AnnouncementService;
import com.liteon.icgwearable.service.UserService;

public class AnnouncementModelEntity {
		@Autowired
		private UserService userService;
		@Autowired
		private AnnouncementService announcementService;
		public Announcement addAnnouncement(AnnouncementModel announcementmodel, String action, String sourceDateFormat) {
			Announcement announcement = null;
			
			
			Accounts accounts = this.userService.getAccountByUserId(announcementmodel.getUserId());
			Users users = new Users();
			users.setId(announcementmodel.getUserId());
			users.setAccounts(accounts);
			users.setUserActive("y");
			users.setRoleType("school_admin");

			announcement = new Announcement();
			announcement.setAnnouncementId(announcementmodel.getAnnouncementId());
			/*if(announcementmodel.getDescription() != null && action.equals("Added"))
				announcement.setDescription(announcementmodel.getDescription());
			else if(announcementmodel.getDescription() != null && action.equals("Updated")) {
				Announcement announcement1= this.announcementService.getAnnouncement(announcementmodel.getAnnouncementId());
				announcement.setDescription(announcement1.getDescription());
			}*/
			announcement.setName(announcementmodel.getName());
			announcement.setDescription(announcementmodel.getDescription());
			announcement.setSchoolId(announcementmodel.getSchoolId());
			//announcement.setSchool_id(accounts.getAccountId());
			//announcement.setUsers(users);
			
			if(action.equals("Added")){
				announcement.setCreatedDate(new Date());
				announcement.setUpdatedDate(new Date());
			}else if (action.equals("Updated")) {
				Announcement announcement1= this.announcementService.getAnnouncement(announcementmodel.getAnnouncementId());
				announcement.setCreatedDate(announcement1.getCreatedDate());
				announcement.setUpdatedDate(new Date());
			}
			return announcement;
		}
}
