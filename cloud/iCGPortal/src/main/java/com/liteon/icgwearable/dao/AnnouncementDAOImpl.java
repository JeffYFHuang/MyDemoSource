package com.liteon.icgwearable.dao;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.liteon.icgwearable.hibernate.entity.Announcement;
import com.liteon.icgwearable.transform.AnnouncementTransform;

@Repository("announcementDAO")
public class AnnouncementDAOImpl implements AnnouncementDAO {
	private static Logger log = Logger.getLogger(AnnouncementDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}

	@Override
	public List<Announcement> listOfAnnouncement(int school_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Announcement.class);
		criteria.add(Restrictions.eq("schoolId", school_id));
		criteria.addOrder(Order.desc("updatedDate"));
		List<Announcement> list = criteria.list();

		if (list.isEmpty()) {
			return null;
		}
		tx.commit();
		session.close();
		return list;
	}

	@Override
	public Announcement getAnnouncement(int announcementId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Announcement announcement = (Announcement) session.get(Announcement.class, announcementId);
		tx.commit();
		session.close();
		return announcement;
	}

	@Override
	public void deleteAnnouncement(Integer announcementId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Announcement announcement = (Announcement) session.get(Announcement.class, announcementId);
		if (null != announcement) {
			session.delete(announcement);
		}
		tx.commit();
		session.close();
	}

	@Override
	public void addAnnouncement(Announcement announcement) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(announcement);
		tx.commit();
		session.close();
	}

	@Override
	public void addAnnouncement(int school_id , String name, String description) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("INSERT INTO announcement "
				+ "(created_date, description, name, school_id, updated_date) "
				+ "VALUES (?, ?, ?, ?, ?)");
		query.setParameter(0, new Date());
		query.setParameter(1, description);
		query.setParameter(2, name);
		query.setParameter(3, school_id);
		query.setParameter(4, new Date());
		int result = query.executeUpdate();
		log.info("addAnnouncement Result" + result);
		tx.commit();
		session.close();
	}

	@Override
	public List<AnnouncementTransform> getAnnouncements(int userId, String uuid) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		List<AnnouncementTransform> announcementList = null;
		String announcementQuery = null;
		Query query = null;
		StringBuilder announcementBuilder = null;
		
		if (userId > 0 && uuid == null) {
			log.info("Into uuserID and null -1");
			announcementBuilder = new StringBuilder();
			
			announcementBuilder.append("SELECT DISTINCT a.name AS announcement_title, a.description AS announcement_description FROM users AS parent_user ")
			.append("LEFT JOIN parent_kids pk on pk.user_id = parent_user.user_id ")
			.append("LEFT JOIN students s on s.student_id=pk.student_id ")
			.append("LEFT JOIN class_grade cg on cg.class_grade_id = s.class_grade_id ")
			.append("LEFT JOIN announcement AS a ON a.school_id = cg.school_id " )
			.append("WHERE parent_user.user_id = ? AND ")
			.append("(")
			.append("date(a.created_date) = date(now()) OR date(a.updated_date) = DATE(now())) ");
			
			announcementQuery = announcementBuilder.toString();
			
		} else if (userId > 0 && uuid != null) {

			announcementBuilder = new StringBuilder();
			
			announcementBuilder.append("SELECT a.name AS announcement_title, a.description AS announcement_description FROM users AS parent_user ")
			.append("LEFT JOIN accounts AS parent_account ON parent_account.account_id = parent_user.account_id ")
			.append("LEFT JOIN parent_kids pk on pk.user_id = parent_user.user_id ")
			.append("LEFT JOIN students s on s.student_id=pk.student_id ")
			.append("LEFT JOIN device_students ds on ds.student_id = s.student_id and ds.device_uuid= ? and ds.status='active' ")
			.append("LEFT JOIN class_grade cg on cg.class_grade_id = s.class_grade_id ")
			.append("LEFT JOIN accounts AS school_account ON school_account.account_id = cg.school_id ")
			.append("LEFT JOIN announcement AS a ON a.school_id = cg.school_id ")
			.append("WHERE parent_user.user_id = ? AND ")
			.append("( ")
			.append("date(a.created_date) = date(now()) OR date(a.updated_date) = DATE(now())) ") ;
			
			announcementQuery = announcementBuilder.toString();
			
		}

		if (userId > 0 && uuid == null) {
			log.info("Into uuserID and null -2");
			query = session.createSQLQuery(announcementQuery)
					.addScalar("announcement_title").addScalar("announcement_description")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(AnnouncementTransform.class));
		} else if (userId > 0 && uuid != null) {
			query = session.createSQLQuery(announcementQuery).addScalar("announcement_title")
					.addScalar("announcement_description")
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.setResultTransformer(Transformers.aliasToBean(AnnouncementTransform.class));
		}

		if (userId > 0 && uuid == null) {
			log.info("Into uuserID and null -3");
			query.setParameter(0, userId);
		} else if (userId > 0 && uuid != null) {
			query.setParameter(0, uuid);
			query.setParameter(1, userId);
		}
		announcementList = (List<AnnouncementTransform>) query.list();
		tx.commit();
		session.close();
		return announcementList;
	}

	@Override
	public boolean isClassExists(String stClass) {
		Session session = sessionFactory.openSession();
		Transaction tx= session.beginTransaction();
		List<Integer> announcementList = null;
		boolean classExists = false;
		StringBuilder classExistsBuilder = new StringBuilder();
		String classQuery = null;
		
		classExistsBuilder.append("select a.announcement_id from announcement as a ").append("left join class_grade as cg on cg.class = a.class and cg.school_id=a.school_id ")
						  .append("where a.class=? ");
		classQuery = classExistsBuilder.toString();
		
		Query query = session.createSQLQuery(classQuery);
		query.setParameter(0, stClass);
		announcementList = (List<Integer>) query.list();
		tx.commit();
		
		if(announcementList.size() > 0)
			classExists = true;
		
		return classExists;
	}
	
	@Override
	public boolean checkStudentIdExist(int student_id, int user_id){
		boolean checkStudentIdFlag = false;
		Session session = sessionFactory.openSession();
		Transaction tx= session.beginTransaction();
		String checkStudentIdQuery ="select student_id from parent_kids where student_id = ? and user_id = ? ";
		
		Query query = session.createSQLQuery(checkStudentIdQuery);
		query.setParameter(0, student_id);
		query.setParameter(1, user_id);
		
		Integer stId = (Integer) query.uniqueResult();
		
		if(stId != null && stId > 0){
			checkStudentIdFlag = true;
		}
		
		tx.commit();
		session.close();
		
		return checkStudentIdFlag;
	}
	
	@Override
	public List<AnnouncementTransform> getAnnouncementsForParent(int student_id, int user_id){
		
		List<AnnouncementTransform> announcementList = null;
		Session session = sessionFactory.openSession();
		Transaction tx= session.beginTransaction();
		StringBuilder annoucementForParentBuilder = new StringBuilder();
		String checkStudentIdQuery = null;
		
		annoucementForParentBuilder.append("select an.name as announcement_title , an.description as announcement_description, ")
		.append("an.created_date as createdDate, DATE_FORMAT(an.updated_date, '%Y-%m-%d %r') ")
		.append("as displayDate from parent_kids pk ")
		.append("join students st on st.student_id = pk.student_id ")
		.append("join class_grade cg on st.class_grade_id = cg.class_grade_id ")
		.append("join announcement an on an.school_id = cg.school_id ")
		.append("where pk.student_id = ? and pk.user_id = ? order by an.updated_date desc ");

		checkStudentIdQuery = annoucementForParentBuilder.toString();
		
		Query query = session.createSQLQuery(checkStudentIdQuery)
						.addScalar("announcement_title")
						.addScalar("announcement_description")
						.addScalar("createdDate")
						.addScalar("displayDate")
						.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
						.setResultTransformer(Transformers.aliasToBean(AnnouncementTransform.class));
		
		query.setParameter(0, student_id);
		query.setParameter(1, user_id);
		announcementList = (List<AnnouncementTransform>) query.list();
		
		tx.commit();
		session.close();
		return announcementList;
	}
	
	@Override
	public void updateAnnouncement(int announcement_id , String name, String description){
		Session session = sessionFactory.openSession();
		Transaction tx= session.beginTransaction();
		
		String updateAnnouncementBasedOnIdQuery ="update announcement set name = ?, description = ? where announcement_id = ?";
		Query query = session.createSQLQuery(updateAnnouncementBasedOnIdQuery);
		query.setParameter(0, name);
		query.setParameter(1, description);
		query.setParameter(2, announcement_id);
		
		query.executeUpdate();
		tx.commit();
		session.close();
	}
	
	@Override
	public void deleteAnnouncement(int announcement_id){
		Session session = sessionFactory.openSession();
		Transaction tx= session.beginTransaction();
		
		String deleteAnnouncementBasedOnIdQuery ="delete from announcement where announcement_id = ?";
		Query query = session.createSQLQuery(deleteAnnouncementBasedOnIdQuery);
		query.setParameter(0, announcement_id);
		query.executeUpdate();
		
		tx.commit();
		session.close();
	}
}
