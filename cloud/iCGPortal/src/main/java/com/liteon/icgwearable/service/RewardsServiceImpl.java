package com.liteon.icgwearable.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.liteon.icgwearable.dao.RewardsDAO;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Rewards;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.hibernate.entity.RewardsCategory;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.RewardsCategoryModel;
import com.liteon.icgwearable.transform.RewardStatisticsTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.SchoolRewardsListTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;

@Service("rewardsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RewardsServiceImpl implements RewardsService {

	private static Logger log = Logger.getLogger(RewardsServiceImpl.class);
	@Autowired
	private RewardsDAO rewardsDAO;

	@Override
	public List<SchoolRewardsListTransform> getSchoolRewardsBySchoolId(int schoolId) {
		return this.rewardsDAO.getSchoolRewardsBySchoolId(schoolId);

	}

	@Override
	public void saveRewardsCategory(RewardsCategoryModel rewardsCategoryModel, Accounts accounts) {
		this.rewardsDAO.saveRewardsCategory(rewardsCategoryModel, accounts);
	}

	@Override
	public RewardsCategory getRewardsCategoryByRId(int rewards_category_id) {
		return this.rewardsDAO.getRewardsCategoryByRId(rewards_category_id);
	}

	@Override
	public RewardsCategory getCategoryByName(String categoryName, Accounts accounts,String rewards_category_id) {
		return this.rewardsDAO.getCategoryByName(categoryName, accounts,rewards_category_id);
	}

	@Override
	public Rewards getRewardsById(Integer rewardId) {
		return this.rewardsDAO.getRewardsById(rewardId);
	}
	
	@Override
	public Rewards getRewardsByName(String rewardName,  Integer rewardsCategoryId,  Integer rewardId) {
		return this.rewardsDAO.getRewardsByName(rewardName, rewardsCategoryId, rewardId);
	}

	@Override
	public boolean updateRewardsCategory(int rewards_category_id, String categoryName, String category_icon_url) {
		return this.rewardsDAO.updateRewardsCategory(rewards_category_id, categoryName, category_icon_url);
	}

	@Override
	public void deleteRewardsFromRewardsStudent(int rewardId) {
		this.rewardsDAO.deleteRewardsFromRewardsStudent(rewardId);
	}

	@Override
	public void deleteRewardsFromRewards(int rewards_category_id) {
		this.rewardsDAO.deleteRewardsFromRewards(rewards_category_id);
	}

	@Override
	public void deleteRewardsFromRewardsCategory(int rewards_category_id) {
		this.rewardsDAO.deleteRewardsFromRewardsCategory(rewards_category_id);
	}
	
	@Override
	public List<RewardsCategory> getRewardsCategoryList(int school_id){
		return this.rewardsDAO.getRewardsCategoryList(school_id);
	}

	@Override
	public List<Integer> getRewardIdFromRewardCategoryId(int rewardCategoryId) {
		return this.rewardsDAO.getRewardIdFromRewardCategoryId(rewardCategoryId);
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryId(int user_id, int school_id) {
		return this.rewardsDAO.getAllRewardsBasedOnRewardCategoryId(user_id, school_id);
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryName(String rewardCategoryName, int school_id) {
		return this.rewardsDAO.getAllRewardsBasedOnRewardCategoryName(rewardCategoryName, school_id);
	}

	@Override
	public RewardsListTransform getRewardsInfoBasedOnRewardId(Integer rewardId) {
		return this.rewardsDAO.getRewardsInfoBasedOnRewardId(rewardId);
	}

	@Override
	public boolean updateRewardsAndRewardsCategoryByIds(RewardsListTransform rewardsListTransform,
			String reward_icon_url) {
		return this.rewardsDAO.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, reward_icon_url);
	}

	@Override
	public boolean deleteRewardsFromRewardsAndRewardsStudents(int rewardId, int rewardCId) {
		return this.rewardsDAO.deleteRewardsFromRewardsAndRewardsStudents(rewardId, rewardCId);
	}

	@Override
	public boolean addNewRewards(RewardsListTransform rewardsListTransform, Users users) {
		return this.rewardsDAO.addNewRewards(rewardsListTransform, users);
	}

	@Override
	public List<RewardStatisticsTransform> getRewardStatistics(int account_id) {
		return this.rewardsDAO.getRewardStatistics(account_id);
	}

	@Override
	public List<RewardStatisticsTransform> getStudentClass(int account_id) {
		return this.rewardsDAO.getStudentClass(account_id);
	}

	@Override
	public List<RewardStatisticsTransform> getStudentName(int account_id) {
		return this.rewardsDAO.getStudentName(account_id);
	}

	@Override
	public String getRewardsCategoryNameBasedonRCId(Integer rewardCategoryId) {
		return this.rewardsDAO.getRewardsCategoryNameBasedonRCId(rewardCategoryId);
	}

	@Override
	public List<RewardStatisticsTransform> getUniqueRewardStatistics(int account_id, String name, String day) {
		return this.rewardsDAO.getUniqueRewardStatistics(account_id, name, day);
	}

	@Override
	public List<RewardStatisticsTransform> getRewardsStatisticsForSchoolAdmin(int account_id) {
		return this.rewardsDAO.getRewardsStatisticsForSchoolAdmin(account_id);
	}

	@Override
	public List<RewardStatisticsTransform> getStudentForAdmin(int account_id) {
		return this.rewardsDAO.getStudentForAdmin(account_id);
	}

	@Override
	public List<RewardStatisticsTransform> getClassForAdmin(int account_id) {
		return this.rewardsDAO.getClassForAdmin(account_id);
	}

	@Override
	public List<RewardStatisticsTransform> getTeacherForAdmin(int account_id) {
		return this.rewardsDAO.getTeacherForAdmin(account_id);
	}

	@Override
	public List<RewardStatisticsTransform> getClass(String user_name) {
		return this.rewardsDAO.getClass(user_name);
	}

	@Override
	public List<RewardStatisticsTransform> getStudentNames(String student_name) {
		return this.rewardsDAO.getStudentNames(student_name);
	}

	@Override
	public List<RewardStatisticsTransform> getUniqueRewards(int account_id, String day, String teacherName,
			String studentClass, String studentName) {
		return this.rewardsDAO.getUniqueRewards(account_id, day, teacherName, studentClass, studentName);
	}

	@Override
	public boolean assignAndReAssignRewards(StudentsListTransform studentsListTransform, Users users, int rewardId,
			String comments, int count) {
		return this.rewardsDAO.assignAndReAssignRewards(studentsListTransform, users, rewardId, comments, count);
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedBasedOnSchoolId(int accountId) {
		return this.rewardsDAO.getAllRewardsBasedBasedOnSchoolId(accountId);
	}

	@Override
	public boolean assignAndReAssignRewards(StudentsListTransform studentsListTransform, Users users, int rewardId,
			String comments) {
		return false;
	}

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int accountId, String studentClass) {
		return this.rewardsDAO.getStudentDetailsFromStudent(accountId, studentClass);
	}

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int userId) {
		return this.rewardsDAO.getStudentDetailsFromStudent(userId);
	}

	@Override
	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryNameForSchoolAdmin(String rewardCategoryName,
			int school_id) {
		return this.rewardsDAO.getAllRewardsBasedOnRewardCategoryNameForSchoolAdmin(rewardCategoryName, school_id);
	}

	@Override
	public int getReceivedCount(Integer rewardId) {
		return this.rewardsDAO.getReceivedCount(rewardId);
	}

	@Override
	public boolean updateRewardsAlreadyAssigned(StudentsListTransform studentsListTransform, Users users, int rewardId,
			int count) {
		return this.rewardsDAO.updateRewardsAlreadyAssigned(studentsListTransform, users, rewardId, count);
	}

}
