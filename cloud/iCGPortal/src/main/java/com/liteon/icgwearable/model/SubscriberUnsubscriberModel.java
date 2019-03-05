package com.liteon.icgwearable.model;

import java.util.List;

public class SubscriberUnsubscriberModel implements Model{
	
	private Integer userId;
	private Integer deviceId;
	private List<Integer> subscriberIds;
	private List<Integer> unsubscriberIds;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public List<Integer> getSubscriberIds() {
		return subscriberIds;
	}
	public void setSubscriberIds(List<Integer> subscriberIds) {
		this.subscriberIds = subscriberIds;
	}
	public List<Integer> getUnsubscriberIds() {
		return unsubscriberIds;
	}
	public void setUnsubscriberIds(List<Integer> unsubscriberIds) {
		this.unsubscriberIds = unsubscriberIds;
	}
}
