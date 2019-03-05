package com.liteon.icgwearable.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Rewards;
import com.liteon.icgwearable.hibernate.entity.RewardsCategory;
import com.liteon.icgwearable.hibernate.entity.RewardsStudents;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.RewardsCategoryModel;
import com.liteon.icgwearable.transform.GuardiansDetailsListTransform;
import com.liteon.icgwearable.transform.RewardStatisticsTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.SchoolRewardsListTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;

@Repository("rewardsDAO")
public class RewardsDAOImpl implements RewardsDAO {

	private static Logger log = Logger.getLogger(RewardsDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	public List<SchoolRewardsListTransform> getSchoolRewardsBySchoolId(int school_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		String schoolRewardsQuery = "SELECT rc.rewards_category_id as rewards_category_id, rc.category_name as category_name, IFNULL(rewards_count.total_rewards, 0) as "
				+ " noOfRewards,  rc.category_icon_url as icon, rc.created_date as date_created FROM rewards_category rc "
				+ " LEFT JOIN ( SELECT distinct(rewards_category_id) as rewards_category_id, count(reward_id) as total_rewards FROM rewards rs "
				+ " GROUP BY rs.reward_id) AS rewards_count "
				+ " ON rewards_count.rewards_category_id = rc.rewards_category_id "
				+ " WHERE rc.school_id= ? ORDER BY rc.category_name ";

		List<SchoolRewardsListTransform> schooolRewardsListTransform = null;

		Query query = session.createSQLQuery(schoolRewardsQuery).addScalar("rewards_category_id")
				.addScalar("category_name").addScalar("noOfRewards").addScalar("icon").addScalar("date_created")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(SchoolRewardsListTransform.class));

		query.setParameter(0, school_id);
		schooolRewardsListTransform = query.list();
		log.debug("returned schooolRewardsListTransform list :::::: " + schooolRewardsListTransform.toString());
		tx.commit();
		session.close();
		return schooolRewardsListTransform;
	}

	@Override
	public void saveRewardsCategory(RewardsCategoryModel rewardsCategoryModel, Accounts accounts) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		RewardsCategory rewardsCategory = new RewardsCategory();
		rewardsCategory.setAccounts(accounts);
		rewardsCategory.setCategory_name(rewardsCategoryModel.getCategory_name());
		rewardsCategory.setCategory_icon_url(rewardsCategoryModel.getCategory_icon_url());
		rewardsCategory.setCreated_date(new Date());
		session.save(rewardsCategory);
		tx.commit();
		session.close();
	}

	@Override
	public boolean updateRewardsCategory(int rewards_category_id, String categoryName, String category_icon_url) {

		log.debug("RewardsDAOImpl.java ::: category_icon_url :::::::::::::: " + category_icon_url);
		log.debug("RewardsDAOImpl.java ::: rewards_category_id :::::::::::::: " + rewards_category_id);
		log.debug("RewardsDAOImpl.java ::: categoryName :::::::::::::: " + categoryName);

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		Criteria criteria = session.createCriteria(RewardsCategory.class);
		criteria.add(Restrictions.eq("rewards_category_id", rewards_category_id));
		RewardsCategory rewardsCategory = (RewardsCategory) criteria.uniqueResult();
		rewardsCategory.setCategory_name(categoryName);
		log.debug("category_icon_url none :::::::::::::: " + category_icon_url.equalsIgnoreCase("none"));
		if(!category_icon_url.equalsIgnoreCase("none"))
		rewardsCategory.setCategory_icon_url(category_icon_url);
		
		session.update(rewardsCategory);

		log.debug("rewardsCategory.getCategory_icon_url() ::::: " + rewardsCategory.getCategory_icon_url());
		log.debug("rewardsCategory.getCategory_name() ::::: " + rewardsCategory.getCategory_name());
		log.debug("rewardsCategory.getRewards_category_id() ::::: " + rewardsCategory.getRewards_category_id());

		tx.commit();
		session.close();
		return true;
	}

	@Override
	public Rewards getRewardsById(Integer rewardId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Rewards.class);
		criteria.add(Restrictions.eq("rewardId", rewardId));
		Rewards rewards = (Rewards) criteria.uniqueResult();
		tx.commit();
		session.close();
		return rewards;
	}

	@Override
	public RewardsCategory getRewardsCategoryByRId(int rewards_category_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(RewardsCategory.class);
		criteria.add(Restrictions.eq("rewards_category_id", rewards_category_id));
		RewardsCategory rewardsCategory = (RewardsCategory) criteria.uniqueResult();
		tx.commit();
		session.close();
		return rewardsCategory;
	}
	
	@Override
	public RewardsCategory getCategoryByName(String categoryName,  Accounts accounts,String rewards_category_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(RewardsCategory.class);
		if(null != rewards_category_id && Integer.parseInt(rewards_category_id) > 0){
			criteria.add(Restrictions.ne("rewards_category_id", Integer.parseInt(rewards_category_id)));
		}
		criteria.add(Restrictions.eq("category_name", categoryName));
		criteria.add(Restrictions.eq("accounts", accounts));
		RewardsCategory rewardsCategory = (RewardsCategory) criteria.uniqueResult();
		tx.commit();
		session.close();
		return rewardsCategory;
	}
	
	@Override
	public Rewards getRewardsByName(String rewardName,  Integer rewardsCategoryId, Integer rewardId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(Rewards.class,"re");
		//log.info("accounts.getAccountId() >>>>>>>>>>>>>>>>> "+getRewardsCategoryByRId(rewardsCategoryId).toString());
		criteria.createAlias("re.rewardsCategory", "rec");
		if(null != rewardId && rewardId > 0){
			criteria.add(Restrictions.ne("rewardId", rewardId));
		}
		criteria.add(Restrictions.eq("rec.rewards_category_id", rewardsCategoryId));
		criteria.add(Restrictions.eq("name", rewardName));
		Rewards rewards = (Rewards) criteria.uniqueResult();
		tx.commit();
		session.close();
		return rewards;
	}
	
	
	
	@Override
	public List<RewardsCategory> getRewardsCategoryList(int school_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(RewardsCategory.class);
		Accounts accounts = new Accounts();
		accounts.setAccountId(school_id);
		criteria.add(Restrictions.eq("accounts", accounts));
		List<RewardsCategory> rewardsCategory = (List<RewardsCategory>) criteria.list();
		tx.commit();
		session.close();
		return rewardsCategory;
	}

	
	
	@Override
	public List<Integer> getRewardIdFromRewardCategoryId(int rewardCategoryId) {
		// TODO Auto-generated method stub
		List<Integer> rewardsIdList = new ArrayList<Integer>();
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String selectRewardIds = "select reward_id from rewards where rewards_category_id = ?";
		Query selectRewardIdsList = session.createSQLQuery(selectRewardIds);
		selectRewardIdsList.setParameter(0, rewardCategoryId);
		log.info("select Query " + selectRewardIds);
		rewardsIdList = selectRewardIdsList.list();
		if (rewardsIdList.size() > 0)
			log.info("list size " + rewardsIdList.size());
		else
			log.info("empty list");
		tx.commit();
		session.close();
		return rewardsIdList;
	}

	@Override
	public void deleteRewardsFromRewardsStudent(int rewardId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("delete from rewards_students where reward_id = ?");
		query.setParameter(0, rewardId);
		query.executeUpdate();
		tx.commit();
		session.close();
	}

	@Override
	public void deleteRewardsFromRewards(int rewards_category_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("delete from rewards where rewards_category_id = ?");
		query.setParameter(0, rewards_category_id);
		query.executeUpdate();
		tx.commit();
		session.close();
	}

	@Override
	public void deleteRewardsFromRewardsCategory(int rewards_category_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createSQLQuery("delete from rewards_category where rewards_category_id = ?");
		query.setParameter(0, rewards_category_id);
		query.executeUpdate();
		tx.commit();
		session.close();
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedBasedOnSchoolId(int accountId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String selectRewardsList = "select rc.category_name as categoryName, rds.rewards_category_id as rewardsCategoryId, rds.name as rewardName, rds.reward_icon_url as icon, "
				+ "rds.created_date as dateCreated, rds.reward_id as rewardId from rewards rds "
				+ "LEFT JOIN rewards_category rc ON rds.rewards_category_id = rc.rewards_category_id "
				+ "where rds.school_id = ?";

		List<RewardsListTransform> rewardsListTransform = null;
		Query query = session.createSQLQuery(selectRewardsList).addScalar("categoryName").addScalar("rewardsCategoryId")
				.addScalar("rewardName").addScalar("dateCreated").addScalar("rewardId").addScalar("icon")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardsListTransform.class));
		query.setParameter(0, accountId);
		rewardsListTransform = query.list();
		tx.commit();
		session.close();
		return rewardsListTransform;
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryId(int user_id, int school_id) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*
		 * String selectRewardsList =
		 * "select rc.category_name as categoryName, rds.rewards_category_id as rewardsCategoryId, rds.name as rewardName, rs.received_count as rewardCount, rds.reward_icon_url as icon, "
		 * + "rds.description as description, " +
		 * "rds.created_date as dateCreated, rds.reward_id as rewardId from rewards rds "
		 * +
		 * "LEFT JOIN rewards_category rc ON rds.rewards_category_id = rc.rewards_category_id "
		 * + "LEFT JOIN rewards_students rs ON rs.reward_id = rds.reward_id " +
		 * "and rds.school_id = rc.school_id";
		 */
		String selectRewardsList = "select rc.category_name as categoryName, rds.rewards_category_id as rewardsCategoryId, rds.name as rewardName,"
				+ " rds.reward_icon_url as icon, rds.created_date as dateCreated, rds.reward_id as rewardId"
				+ " from rewards rds" + " LEFT JOIN rewards_category rc"
				+ " ON rds.rewards_category_id = rc.rewards_category_id  and rds.school_id = rc.school_id"
				+ " where rds.school_id = ? ";

		List<RewardsListTransform> rewardsListTransform = null;
		Query query = session.createSQLQuery(selectRewardsList).addScalar("categoryName").addScalar("rewardsCategoryId")
				.addScalar("rewardName").addScalar("dateCreated").addScalar("rewardId").addScalar("icon")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardsListTransform.class));

		query.setParameter(0, school_id);
		rewardsListTransform = query.list();
		tx.commit();
		session.close();
		return rewardsListTransform;
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryName(String rewardCategoryName, int school_id) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		log.debug("CategoryName :::::::: " + rewardCategoryName);
		/*
		 * String selectRewardsList =
		 * "select rc.category_name as categoryName, rds.rewards_category_id as rewardsCategoryId, rds.name as rewardName, rds.reward_icon_url as icon, "
		 * +
		 * "rds.description as description, rds.created_date as dateCreated, rs.received_count as rewardCount, rds.reward_id as rewardId from rewards rds, rewards_category rc, rewards_students rs "
		 * +
		 * "where rc.category_name = ? and rds.rewards_category_id = rc.rewards_category_id and rds.reward_id = rs.reward_id and rds.school_id = ?"
		 * ;
		 */

		String selectRewardsList = "select rc.category_name as categoryName, rds.rewards_category_id as rewardsCategoryId, rds.name as rewardName,"
				+ " rds.reward_icon_url as icon,rds.created_date as dateCreated, rds.reward_id as rewardId from"
				+ " rewards rds  " + " LEFT JOIN " + " rewards_category rc "
				+ " ON rds.rewards_category_id = rc.rewards_category_id"
				+ " where rc.category_name = ? and rds.school_id = ?";

		List<RewardsListTransform> rewardsListTransform = null;
		Query query = session.createSQLQuery(selectRewardsList).addScalar("categoryName").addScalar("rewardsCategoryId")
				.addScalar("rewardName").addScalar("dateCreated").addScalar("rewardId").addScalar("icon")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardsListTransform.class));

		query.setParameter(0, rewardCategoryName);
		query.setParameter(1, school_id);
		rewardsListTransform = query.list();
		tx.commit();
		session.close();
		return rewardsListTransform;
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryNameForSchoolAdmin(String rewardCategoryName,
			int school_id) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		log.debug("CategoryName" + rewardCategoryName);
		String selectRewardsList = "select rc.category_name as categoryName, rds.rewards_category_id as rewardsCategoryId, rds.name as rewardName, rds.reward_icon_url as icon, "
				+ " rds.created_date as dateCreated, rds.reward_id as rewardId" + " from rewards rds "
				+ " LEFT JOIN rewards_category rc ON rds.rewards_category_id = rc.rewards_category_id"
				+ " where rc.category_name = ? and rds.school_id = ?";

		List<RewardsListTransform> rewardsListTransform = null;
		Query query = session.createSQLQuery(selectRewardsList).addScalar("categoryName").addScalar("rewardsCategoryId")
				.addScalar("rewardName").addScalar("dateCreated").addScalar("rewardId").addScalar("icon")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardsListTransform.class));

		query.setParameter(0, rewardCategoryName);
		query.setParameter(1, school_id);

		rewardsListTransform = query.list();
		tx.commit();
		session.close();
		return rewardsListTransform;
	}

	@Override
	public RewardsListTransform getRewardsInfoBasedOnRewardId(Integer rewardId) {

		RewardsListTransform rewardsListTransform = null;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String selectReward = "select rds.reward_id as rewardId, rds.name as rewardName,rds.reward_icon_url as icon, rds.rewards_category_id as rewardsCategoryId"
				+ " from rewards rds where reward_id = ?";
		Query query = session.createSQLQuery(selectReward).addScalar("rewardId").addScalar("rewardName")
				.addScalar("rewardsCategoryId").addScalar("icon").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardsListTransform.class));

		query.setParameter(0, rewardId);
		rewardsListTransform = (RewardsListTransform) query.uniqueResult();
		tx.commit();
		session.close();
		return rewardsListTransform;
	}

	@Override
	public boolean updateRewardsAndRewardsCategoryByIds(RewardsListTransform rewardsListTransform,
			String reward_icon_url) {

		Session session = sessionFactory.openSession();
		// Transaction tx = session.beginTransaction();
		boolean flag = true;

		/*
		 * Criteria criteria = session.createCriteria(RewardsCategory.class);
		 * criteria.add(Restrictions.eq("rewards_category_id",
		 * rewardsListTransform.getRewardsCategoryId())); RewardsCategory
		 * rewardsCategory = (RewardsCategory) criteria.uniqueResult();
		 * rewardsCategory.setCategory_name(rewardsListTransform.getCategoryName
		 * ()); session.update(rewardsCategory); tx.commit();
		 */

		Transaction tx2 = session.beginTransaction();

		if (reward_icon_url != null && reward_icon_url.equals("null")) {
			reward_icon_url = null;
		} else if (reward_icon_url != null && reward_icon_url.equals("")) {
			reward_icon_url = null;
		}
		
		log.debug("is file uploaded url none" + reward_icon_url.equalsIgnoreCase("none"));
		if(!reward_icon_url.equalsIgnoreCase("none")) {
			log.debug("the inside not none url" );
		String SQL_UPDATE = "update rewards set name = ?, reward_icon_url = ?, rewards_category_id = ? where reward_id = ?";
		Query query2 = session.createSQLQuery(SQL_UPDATE);
		query2.setParameter(0, rewardsListTransform.getRewardName());
		query2.setParameter(1, reward_icon_url);
		query2.setParameter(2, rewardsListTransform.getRewardsCategoryId());
		query2.setParameter(3, rewardsListTransform.getRewardId());
		int result = query2.executeUpdate();
		log.debug("Result : " + result);
		tx2.commit();

		session.close();
		return flag;
		}else {
			log.debug("the inside none url" );
			String SQL_UPDATE = "update rewards set name = ?, rewards_category_id = ? where reward_id = ?";
			Query query2 = session.createSQLQuery(SQL_UPDATE);
			query2.setParameter(0, rewardsListTransform.getRewardName());
			query2.setParameter(1, rewardsListTransform.getRewardsCategoryId());
			query2.setParameter(2, rewardsListTransform.getRewardId());
			int result = query2.executeUpdate();
			log.debug("Result : " + result);
			tx2.commit();

			session.close();
			return flag;
			
		}

		
	}

	@Override
	public boolean deleteRewardsFromRewardsAndRewardsStudents(int rewardId, int rewardCId) {

		boolean flag = true;
		Session session = sessionFactory.openSession();
		Transaction tx2 = session.beginTransaction();
		String SQL_DELETE_REWARDSTUDENT = "delete from rewards_students where reward_id = ?";
		Query query2 = session.createSQLQuery(SQL_DELETE_REWARDSTUDENT);
		query2.setParameter(0, rewardId);
		int result = query2.executeUpdate();
		log.debug("Result : " + result);
		tx2.commit();

		Transaction tx = session.beginTransaction();
		String SQL_DELETE_REWARDS = "delete from rewards where reward_id = ? and rewards_category_id = ?";
		Query query1 = session.createSQLQuery(SQL_DELETE_REWARDS);
		query1.setParameter(0, rewardId);
		query1.setParameter(1, rewardCId);
		query1.executeUpdate();
		tx.commit();

		session.close();
		return flag;
	}

	@Override
	public String getRewardsCategoryNameBasedonRCId(Integer rewardCategoryId) {

		String categoryName = null;
		RewardsListTransform rewardsListTransform = null;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String selectRewardCategory = "select category_name from rewards_category where rewards_category_id = ?";

		Query query = session.createSQLQuery(selectRewardCategory);

		query.setParameter(0, rewardCategoryId);
		if (query.uniqueResult() != null) {
			categoryName = (String) query.uniqueResult();
		}
		tx.commit();
		session.close();
		return categoryName;
	}

	@Override
	public int getReceivedCount(Integer rewardId) {
		Integer count = null;
		RewardsListTransform rewardsListTransform = null;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String selectReceivedCount = "select received_count from rewards_students where reward_id = ? ";

		Query query = session.createSQLQuery(selectReceivedCount);

		query.setParameter(0, rewardId);
		if (query.uniqueResult() != null) {
			count = (Integer) query.uniqueResult();
		}
		tx.commit();
		session.close();
		log.debug("Received Count" + count);
		return count;
	}

	@Override
	public boolean addNewRewards(RewardsListTransform rewardsListTransform, Users users) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean flag = true;
		Rewards rewards = new Rewards();
		//rewards.setAccounts(users.getAccounts());
		rewards.setCreatedDate(new Date());
		rewards.setName(rewardsListTransform.getRewardName());
		// rewards.setDescription(rewardsListTransform.getDescription());
		rewards.setReward_icon_url(rewardsListTransform.getIcon());
		rewards.setRewardsCategory(getRewardsCategoryByRId(Integer.parseInt(rewardsListTransform.getUniqueCategory())));
		session.save(rewards);
		tx.commit();
		session.close();
		return flag;
	}
	
	

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int userId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		log.info("userId :::::::::::::::::::::::::::::::::::::::::::::: " + userId);
		String selectStudentDetails = "select s.name as studentName, s.student_id as studentId, s.class as studentClass  from students s "
				+ "left join users u on u.account_id = s.school_id " + "where u.user_id= ? ";
		// String selectStudentDetails = "select s.name as studentName,
		// s.student_id as studentId, s.class as studentClass from students as s
		// where s.account_id = ? ";
		List<StudentsListTransform> studentsDetailsTransform = null;
		log.info("selectStudentDetails  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + selectStudentDetails);
		Query query = session.createSQLQuery(selectStudentDetails).addScalar("studentName").addScalar("studentId")
				.addScalar("studentClass").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));

		query.setParameter(0, userId);
		studentsDetailsTransform = (List<StudentsListTransform>) query.list();
		log.info("studentsDetailsTransform.size()" + "\t" + studentsDetailsTransform.size());
		log.info("UserDaoImpl >>>>>>>>>>>>>> studentsDetailsTransform :::::::::::::::: "
				+ studentsDetailsTransform.toString());
		tx.commit();
		session.close();
		return studentsDetailsTransform;
	}

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int accountId, String studentClass) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		log.info("accountId : " + accountId);
		String selectStudentDetails = "select name as studentName, student_id as studentId from students where school_id = ? and class = ?";
		Query query = session.createSQLQuery(selectStudentDetails).addScalar("studentName").addScalar("studentId")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(StudentsListTransform.class));

		query.setParameter(0, accountId);
		query.setParameter(1, studentClass);

		List<StudentsListTransform> studentsDetailsTransform = (List<StudentsListTransform>) query.list();
		tx.commit();
		session.close();
		return studentsDetailsTransform;
	}

	@Override
	public boolean assignAndReAssignRewards(StudentsListTransform studentsListTransform, Users users, int rewardId,
			String comments, int count) {

		boolean flag = true;
		Students students = null;
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		RewardsStudents rewardsStudents = new RewardsStudents();
		Criteria criteria = session.createCriteria(Rewards.class);
		criteria.add(Restrictions.eq("rewardId", rewardId));

		Rewards rewards = null;
		try {
			rewards = (Rewards) criteria.uniqueResult();
		} catch (HibernateException he) {
			log.info("rewards ::::::: reward already assigned ");
			rewards = new Rewards();
			students = new Students();
			students.setStudentId(studentsListTransform.getStudentId());
			rewards.setRewardId(rewardId);
			rewardsStudents.setUsers(users);
			rewardsStudents.setRewards(rewards);
			rewardsStudents.setStudents(students);
			rewardsStudents.setReceivedCount(count);
			rewardsStudents.setCreatedDate(new Date());
			rewardsStudents.setUpdatedDate(new Date());

			session.saveOrUpdate(rewardsStudents);
			tx.commit();
			return flag;
		}

		criteria = session.createCriteria(Students.class);
		criteria.add(Restrictions.eq("studentId", studentsListTransform.getStudentId()));
		// log.debug("HelloStudents"+students.toString());
		try {
			students = (Students) criteria.uniqueResult();
			rewardsStudents.setStudents(students);
		} catch (HibernateException he) {
			log.info("rewardsStudents :::::: reward already assigned");
			return flag;
		}

		rewardsStudents.setUsers(users);
		rewardsStudents.setReceivedCount(count);
		rewardsStudents.setStudents(students);
		rewardsStudents.setRewards(rewards);
		rewardsStudents.setCreatedDate(new Date());
		rewardsStudents.setUpdatedDate(new Date());

		session.save(rewardsStudents);
		tx.commit();
		session.close();
		return flag;
	}

	@Override
	public boolean updateRewardsAlreadyAssigned(StudentsListTransform studentsListTransform, Users users, int rewardId,
			int count) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		boolean flag = false;
		String SQL_UPDATE = "update rewards_students set received_count = ? , student_id = ?, updated_date = ? where id = ?";
		Query query2 = session.createSQLQuery(SQL_UPDATE);

		query2.setParameter(0, count);
		query2.setParameter(1, studentsListTransform.getStudentId());
		query2.setParameter(2, new Date());
		query2.setParameter(3, rewardId);

		int result = query2.executeUpdate();
		log.debug("Result : " + result);
		tx.commit();
		session.close();
		flag = true;
		return flag;

	}

	@Override
	public List<RewardStatisticsTransform> getStudentClass(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*
		 * String classQuery = "SELECT DISTINCT t.class AS studentClass " +
		 * "FROM students t " +
		 * "LEFT JOIN users u ON u.account_id = t.account_id " +
		 * "WHERE u.account_id = ?";
		 */
		String classQuery = "SELECT DISTINCT t.class AS studentClass " + "FROM class_grade t "
				+ "LEFT JOIN users u ON u.account_id = t.school_id " + "WHERE u.account_id = ? ";
		List<RewardStatisticsTransform> classList = null;
		Query query = session.createSQLQuery(classQuery).addScalar("studentClass")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		classList = query.list();
		tx.commit();
		session.close();
		return classList;
	}

	@Override
	public List<RewardStatisticsTransform> getStudentName(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*
		 * String classQuery = "SELECT DISTINCT t.name AS student_name " +
		 * "FROM students t " +
		 * "LEFT JOIN users u ON u.account_id = t.account_id " +
		 * "WHERE u.account_id = ?";
		 */
		String studntQuery = "SELECT DISTINCT t.name AS student_name " + "FROM students t "
				+ "LEFT JOIN parent_kids pk on pk.student_id = t.student_id "
				+ "LEFT JOIN users u ON u.user_id = pk.user_id " + "WHERE u.account_id = ? ";
		List<RewardStatisticsTransform> studentsList = null;
		Query query = session.createSQLQuery(studntQuery).addScalar("student_name")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		studentsList = query.list();
		log.info("StudentList#############" + studentsList.toString());
		tx.commit();
		session.close();
		return studentsList;
	}

	@Override
	public List<RewardStatisticsTransform> getRewardStatistics(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		/*
		 * String rewardStatisticsQuery =
		 * "SELECT DISTINCT rc.category_name,rc.created_date,r.name AS rewards_name, "
		 * +
		 * "s.name AS student_name, s.class As studentClass, a.account_name, u.name AS user_name, rc.category_icon_url AS icon FROM rewards_category rc "
		 * +
		 * "LEFT JOIN rewards r ON r.rewards_category_id = rc.rewards_category_id "
		 * + "LEFT JOIN rewards_students rs ON rs.reward_id = r.reward_id " +
		 * "LEFT JOIN students s ON s.student_id = rs.student_id " +
		 * "LEFT JOIN accounts a ON a.account_id = s.account_id " +
		 * "LEFT JOIN users u ON u.account_id = a.account_id " +
		 * "WHERE s.account_id=? ";
		 */
		String rewardStatisticsQuery = "SELECT DISTINCT rc.category_name as category_name ,rc.created_date as created_date,r.name AS rewards_name, "
				+ "s.name AS student_name, cg.class As studentClass, u.name AS user_name, rc.category_icon_url AS icon FROM rewards_category rc "
				+ "LEFT JOIN rewards r ON r.rewards_category_id = rc.rewards_category_id "
				+ "LEFT JOIN rewards_students rs ON rs.reward_id = r.reward_id "
				+ "LEFT JOIN students s ON s.student_id = rs.student_id "
				+ "LEFT JOIN class_grade cg on cg.teacher_id = rs.teacher_id "
				+ "LEFT JOIN parent_kids pk on pk.student_id = s.student_id "
				+ "LEFT JOIN users u ON u.user_id = pk.user_id " + "WHERE u.user_id= ? ";
		List<RewardStatisticsTransform> rewardStatisticsTransform = null;
		Query query = session.createSQLQuery(rewardStatisticsQuery).addScalar("category_name").addScalar("rewards_name")
				.addScalar("student_name").addScalar("studentClass").addScalar("created_date").addScalar("user_name")
				.addScalar("icon").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));

		query.setParameter(0, account_id);
		rewardStatisticsTransform = query.list();
		log.info("RewardStatistics::::::::" + rewardStatisticsTransform.toString());
		tx.commit();
		session.close();
		return rewardStatisticsTransform;
	}

	@Override
	public List<RewardStatisticsTransform> getUniqueRewardStatistics(int account_id, String name, String day) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String findUniqueQuery = "SELECT DISTINCT rc.category_name, rc.created_date, r.name AS rewards_name, s.name AS student_name, s.class As studentClass, a.account_name, u.name AS user_name, rc.category_icon_url AS icon "
				+ "FROM rewards_category rc " + "LEFT JOIN rewards r ON r.rewards_category_id = rc.rewards_category_id "
				+ "LEFT JOIN rewards_students rs ON rs.reward_id = r.reward_id "
				+ "LEFT JOIN students s ON s.student_id = rs.student_id "
				+ "LEFT JOIN accounts a ON a.account_id = s.account_id "
				+ "LEFT JOIN users u ON u.account_id = a.account_id " + "WHERE s.account_id=? ";
		if (!"NONE".equalsIgnoreCase(name)) {
			findUniqueQuery = findUniqueQuery + " AND s.name = ?";
		}
		if (!"NONE".equalsIgnoreCase(day)) {
			findUniqueQuery = findUniqueQuery + " AND rc.created_date >= SUBDATE(CURDATE(), ?)";
		}
		List<RewardStatisticsTransform> uniqueData = null;
		Query query = session.createSQLQuery(findUniqueQuery).addScalar("category_name").addScalar("rewards_name")
				.addScalar("student_name").addScalar("studentClass").addScalar("created_date").addScalar("account_name")
				.addScalar("user_name").addScalar("icon").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		boolean flag = false;
		if (!"NONE".equalsIgnoreCase(name)) {
			query.setParameter(1, name);
			flag = true;
		}
		if (!"NONE".equalsIgnoreCase(day)) {
			if (flag) {
				query.setParameter(2, Integer.parseInt(day));
			} else {
				query.setParameter(1, Integer.parseInt(day));
			}
		}
		uniqueData = query.list();
		log.info("UniqueResult::::::::::::::::" + uniqueData.toString());
		tx.commit();
		session.close();
		return uniqueData;
	}

	@Override
	public List<RewardStatisticsTransform> getRewardsStatisticsForSchoolAdmin(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		log.debug("ACCOUNT ID$$$$$$" + account_id);
		String statisticsForSchoolAdmin = "SELECT rc.category_name, rc.created_date, r.name AS rewards_name, s.name AS student_name, s.class As studentClass, a.account_name, u.name AS user_name, rc.category_icon_url AS icon FROM rewards_category rc "
				+ "LEFT JOIN rewards r ON r.rewards_category_id = rc.rewards_category_id "
				+ "LEFT JOIN rewards_students rs ON rs.reward_id = r.reward_id "
				+ "LEFT JOIN students s ON s.student_id = rs.student_id "
				+ "LEFT JOIN accounts a ON a.account_id = s.school_id "
				+ "LEFT JOIN users u ON u.account_id = a.account_id "
				+ "WHERE u.account_id=? and u.role_type = 'school_admin' ";
		List<RewardStatisticsTransform> rewardsData = null;
		Query query = session.createSQLQuery(statisticsForSchoolAdmin).addScalar("category_name")
				.addScalar("created_date").addScalar("rewards_name").addScalar("student_name").addScalar("studentClass")
				.addScalar("account_name").addScalar("user_name").addScalar("icon")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		rewardsData = query.list();
		tx.commit();
		session.close();
		return rewardsData;
	}

	@Override
	public List<RewardStatisticsTransform> getUniqueRewards(int account_id, String day, String teacherName,
			String studentClass, String studentName) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String findUniqueQuery = "SELECT DISTINCT rc.category_name, rc.created_date, r.name AS rewards_name, s.name AS student_name, s.class As studentClass, a.account_name, u.name AS user_name, rc.category_icon_url AS icon "
				+ "FROM rewards_category rc " + "LEFT JOIN rewards r ON r.rewards_category_id = rc.rewards_category_id "
				+ "LEFT JOIN rewards_students rs ON rs.reward_id = r.reward_id "
				+ "LEFT JOIN students s ON s.student_id = rs.student_id "
				+ "LEFT JOIN accounts a ON a.account_id = s.school_id "
				+ "LEFT JOIN users u ON u.account_id = a.account_id " + "WHERE u.account_id=? ";
		log.info("Teacher Name" + "\t" + teacherName);
		log.info("Student Name" + "\t" + studentName);
		if (!"NONE".equalsIgnoreCase(teacherName)) {
			findUniqueQuery = findUniqueQuery + " AND u.name = ?";
		}
		if (!"NONE".equalsIgnoreCase(studentClass)) {
			findUniqueQuery = findUniqueQuery + " AND s.class = ?";
		}
		if (!"NONE".equalsIgnoreCase(studentName)) {
			findUniqueQuery = findUniqueQuery + " AND s.name = ?";
		}
		if (!"NONE".equalsIgnoreCase(day)) {
			findUniqueQuery = findUniqueQuery + " AND rc.created_date >= SUBDATE(CURDATE(), ?)";
		}
		List<RewardStatisticsTransform> uniqueData = null;
		Query query = session.createSQLQuery(findUniqueQuery).addScalar("category_name").addScalar("rewards_name")
				.addScalar("student_name").addScalar("studentClass").addScalar("created_date").addScalar("account_name")
				.addScalar("user_name").addScalar("icon").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		// boolean flag = false;
		int i = 1;
		if (!"NONE".equalsIgnoreCase(teacherName)) {
			query.setParameter(i++, teacherName);
			// flag = true;
		}
		if (!"NONE".equalsIgnoreCase(studentClass)) {
			query.setParameter(i++, studentClass);
			// flag = true;
		}
		if (!"NONE".equalsIgnoreCase(studentName)) {
			query.setParameter(i++, studentName);
			// flag = true;
		}
		if (!"NONE".equalsIgnoreCase(day)) {
			query.setParameter(i++, Integer.parseInt(day));
		}
		uniqueData = query.list();
		log.info("UniqueResult::::::::::::::::" + uniqueData.toString());
		tx.commit();
		session.close();
		return uniqueData;
	}

	@Override
	public List<RewardStatisticsTransform> getStudentForAdmin(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String classQuery = "SELECT distinct t.name AS student_name " + "FROM students t "
				+ "LEFT JOIN users u ON u.account_id = t.school_id " + "WHERE u.account_id = ?";
		List<RewardStatisticsTransform> studentsList = null;
		Query query = session.createSQLQuery(classQuery).addScalar("student_name")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		studentsList = query.list();
		tx.commit();
		session.close();
		return studentsList;
	}

	@Override
	public List<RewardStatisticsTransform> getClassForAdmin(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String classQuery = "SELECT distinct t.class AS studentClass " + "FROM students t "
				+ "LEFT JOIN users u ON u.account_id = t.school_id " + "WHERE u.account_id = ? ";
		List<RewardStatisticsTransform> classList = null;
		Query query = session.createSQLQuery(classQuery).addScalar("studentClass")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);
		classList = query.list();
		tx.commit();
		session.close();
		return classList;
	}

	@Override
	public List<RewardStatisticsTransform> getTeacherForAdmin(int account_id) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String teacherQuery = "SELECT DISTINCT u.name AS user_name " + "FROM users u "
				+ "LEFT JOIN accounts a ON a.account_id = u.account_id " + "WHERE a.account_id = ? ";
		List<RewardStatisticsTransform> teacherList = null;
		Query query = session.createSQLQuery(teacherQuery).addScalar("user_name")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, account_id);

		teacherList = query.list();
		tx.commit();
		session.close();
		return teacherList;
	}

	@Override
	public List<RewardStatisticsTransform> getClass(String user_name) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String classQuery = "SELECT DISTINCT t.class AS studentClass " + "FROM students t "
				+ "LEFT JOIN users u ON u.account_id = t.school_id " + "WHERE u.name = ? ";
		List<RewardStatisticsTransform> classList = null;
		Query query = session.createSQLQuery(classQuery).addScalar("studentClass")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, user_name);
		classList = query.list();
		log.info("Classsssss" + classList.toString());
		tx.commit();
		session.close();
		return classList;
	}

	@Override
	public List<RewardStatisticsTransform> getStudentNames(String student_name) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String studentQuery = "SELECT name AS student_name " + "FROM students where class = ? ";
		List<RewardStatisticsTransform> studentList = null;
		Query query = session.createSQLQuery(studentQuery).addScalar("student_name")
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(RewardStatisticsTransform.class));
		query.setParameter(0, student_name);
		studentList = query.list();
		log.info("Classsssss" + studentList.toString());
		tx.commit();
		session.close();
		return studentList;
	}

	@Override
	public List<GuardiansDetailsListTransform> getGuardiansDetailsList(int accountId) {

		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String role_type = "parent_member";
		String guadiansListQuery = "select user_id as user_id, name AS guadianName, role_type as role_type from users where account_id = ? and role_type = ?";
		List<GuardiansDetailsListTransform> guardiansDetailsList = null;
		Query query = session.createSQLQuery(guadiansListQuery).addScalar("user_id").addScalar("guadianName")
				.addScalar("role_type").setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setResultTransformer(Transformers.aliasToBean(GuardiansDetailsListTransform.class));

		query.setParameter(0, accountId);
		query.setParameter(1, role_type);
		guardiansDetailsList = query.list();

		log.info("return guardiansDetailsList : " + guardiansDetailsList.toString());

		tx.commit();
		session.close();
		return guardiansDetailsList;

	}

	@Override
	public boolean findUserActivationKey(String key) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();

		boolean userExists = false;
		Criteria criteria = session.createCriteria(Users.class);
		criteria.add(Restrictions.eq("activationCode", key));

		Users user = (Users) criteria.uniqueResult();

		if (user != null)
			userExists = true;

		tx.commit();
		session.close();

		return userExists;
	}

}
