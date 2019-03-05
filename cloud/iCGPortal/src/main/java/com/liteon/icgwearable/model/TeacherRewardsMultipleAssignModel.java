package com.liteon.icgwearable.model;

import java.util.List;

public class TeacherRewardsMultipleAssignModel {
	
	private List<Integer> reward_ids;
	private List<Integer> student_ids;
	private Integer received_count;
	private List<Integer> students_rewardids;
	
	public TeacherRewardsMultipleAssignModel() {}
	
	public Integer getReceived_count() {
		return received_count;
	}

	public void setReceived_count(Integer received_count) {
		this.received_count = received_count;
	}

	public List<Integer> getStudents_rewardids() {
		return students_rewardids;
	}

	public void setStudents_rewardids(List<Integer> students_rewardids) {
		this.students_rewardids = students_rewardids;
	}
	public List<Integer> getReward_ids() {
		return reward_ids;
	}
	public void setReward_ids(List<Integer> reward_ids) {
		this.reward_ids = reward_ids;
	}
	public List<Integer> getStudent_ids() {
		return student_ids;
	}
	public void setStudent_ids(List<Integer> student_ids) {
		this.student_ids = student_ids;
	}
}
