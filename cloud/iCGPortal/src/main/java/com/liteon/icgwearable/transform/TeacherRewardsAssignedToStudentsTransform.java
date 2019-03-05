package com.liteon.icgwearable.transform;

import java.util.Date;

public class TeacherRewardsAssignedToStudentsTransform {
	
	private Integer student_reward_id;
	private Date reward_date;
	private Integer received_count;
	private Integer reward_id;
	private String name;
	private String description;
	private String category_name;
	private String category_icon_url;
	private String reward_icon_url;
	private Integer student_id;
	private String student_name;
	
	public Integer getStudent_id() {
		return student_id;
	}
	public void setStudent_id(Integer student_id) {
		this.student_id = student_id;
	}
	public String getStudent_name() {
		return student_name;
	}
	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
	public Date getReward_date() {
		return reward_date;
	}
	public void setReward_date(Date reward_date) {
		this.reward_date = reward_date;
	}
	
	public Integer getStudent_reward_id() {
		return student_reward_id;
	}
	public void setStudent_reward_id(Integer student_reward_id) {
		this.student_reward_id = student_reward_id;
	}
	public Integer getReceived_count() {
		return received_count;
	}
	public void setReceived_count(Integer received_count) {
		this.received_count = received_count;
	}
	public String getReward_icon_url() {
		return reward_icon_url;
	}
	public void setReward_icon_url(String reward_icon_url) {
		this.reward_icon_url = reward_icon_url;
	}
	public Integer getReward_id() {
		return reward_id;
	}
	public void setReward_id(Integer reward_id) {
		this.reward_id = reward_id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	
	public String getCategory_icon_url() {
		return category_icon_url;
	}
	public void setCategory_icon_url(String category_icon_url) {
		this.category_icon_url = category_icon_url;
	}
	public String toString(){
		
		
		return "{\n"+
			 "reward_id: "  + reward_id +","+
			 "name: "+  name +"," +
			 "reward_description:" + description +"," + 
			 "category_name : " + category_name +"," +
			 "reward_icon_url : "+ category_icon_url + "\n"
			 + "}";
		}
		
}
