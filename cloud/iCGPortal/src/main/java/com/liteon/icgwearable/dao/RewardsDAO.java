package com.liteon.icgwearable.dao;

import java.util.List;

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Rewards;
import com.liteon.icgwearable.hibernate.entity.RewardsCategory;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.RewardsCategoryModel;
import com.liteon.icgwearable.transform.GuardiansDetailsListTransform;
import com.liteon.icgwearable.transform.RewardStatisticsTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.SchoolRewardsListTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;

public interface RewardsDAO {

	public List<SchoolRewardsListTransform> getSchoolRewardsBySchoolId(int school_id);

	public void saveRewardsCategory(RewardsCategoryModel rewardsCategoryModel, Accounts accounts);

	public RewardsCategory getRewardsCategoryByRId(int rewards_category_id);

	public Rewards getRewardsById(Integer rewardId);
	
	public RewardsCategory getCategoryByName(String categoryName, Accounts accounts,String rewards_category_id);

	public Rewards getRewardsByName(String rewardName,  Integer rewardsCategoryId, Integer rewardId);

	public boolean updateRewardsCategory(int rewards_category_id, String categoryName, String category_icon_url);

	public void deleteRewardsFromRewardsStudent(int rewardId);

	public void deleteRewardsFromRewards(int rewards_category_id);

	public void deleteRewardsFromRewardsCategory(int rewards_category_id);
	
	public List<RewardsCategory> getRewardsCategoryList(int school_id);

	public List<Integer> getRewardIdFromRewardCategoryId(int rewardCategoryId);

	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryId(int user_id, int school_id);

	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryName(String rewardCategoryName, int school_id);

	public RewardsListTransform getRewardsInfoBasedOnRewardId(Integer rewardId);

	public boolean updateRewardsAndRewardsCategoryByIds(RewardsListTransform rewardsListTransform,
			String reward_icon_url);

	public boolean deleteRewardsFromRewardsAndRewardsStudents(int rewardId, int rewardCId);

	public boolean addNewRewards(RewardsListTransform rewardsListTransform, Users users);

	// For Parent
	public List<RewardStatisticsTransform> getRewardStatistics(int account_id);

	public List<RewardStatisticsTransform> getStudentClass(int account_id);

	public List<RewardStatisticsTransform> getStudentName(int account_id);

	public List<RewardStatisticsTransform> getUniqueRewardStatistics(int account_id, String name, String day);

	// For SchoolAdmin
	public List<RewardStatisticsTransform> getRewardsStatisticsForSchoolAdmin(int account_id);

	public List<RewardStatisticsTransform> getStudentForAdmin(int account_id);

	public List<RewardStatisticsTransform> getClassForAdmin(int account_id);

	public List<RewardStatisticsTransform> getTeacherForAdmin(int account_id);

	public List<RewardStatisticsTransform> getUniqueRewards(int account_id, String day, String teacherName,
			String studentClass, String studentName);

	public List<RewardStatisticsTransform> getClass(String user_name);

	public List<RewardStatisticsTransform> getStudentNames(String student_name);

	public String getRewardsCategoryNameBasedonRCId(Integer rewardCategoryId);

	public int getReceivedCount(Integer rewardId);

	public List<StudentsListTransform> getStudentDetailsFromStudent(int userId);

	public boolean assignAndReAssignRewards(StudentsListTransform studentsListTransform, Users users, int rewardId,
			String comments, int count);

	public List<StudentsListTransform> getStudentDetailsFromStudent(int accountId, String studentClass);

	public List<GuardiansDetailsListTransform> getGuardiansDetailsList(int accountId);

	public boolean findUserActivationKey(String key);

	public List<RewardsListTransform> getAllRewardsBasedBasedOnSchoolId(int accountId);

	public List<RewardsListTransform> getAllRewardsBasedOnRewardCategoryNameForSchoolAdmin(String rewardCategoryName,
			int school_id);

	public boolean updateRewardsAlreadyAssigned(StudentsListTransform studentsListTransform, Users users, int rewardId, int count);

}
