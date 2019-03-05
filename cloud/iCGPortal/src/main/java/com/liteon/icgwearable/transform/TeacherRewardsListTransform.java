package com.liteon.icgwearable.transform;

public class TeacherRewardsListTransform {
	
	private Integer reward_id;
	private String name;
	//private String description;
	private String category_name;
	private String category_icon_url;
	private String reward_icon_url;
	
	
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
	/*public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}*/
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
			 //"reward_description:" + description +"," + 
			 "category_name : " + category_name +"," +
			 "reward_icon_url : "+ category_icon_url + "\n"
			 + "}";
		}
		
}
