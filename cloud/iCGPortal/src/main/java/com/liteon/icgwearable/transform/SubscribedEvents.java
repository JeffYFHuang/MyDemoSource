package com.liteon.icgwearable.transform;

public class SubscribedEvents {
	
	public SubscribedEvents() {}
	
	private int supportedEventId;
	private String  eventName;
	private Integer subscribedEventId;
	
	public Integer getSubscribedEventId() {
		return subscribedEventId;
	}
	public void setSubscribedEventId(Integer subscribedEventId) {
			this.subscribedEventId = subscribedEventId;
	}
	
	public int getSupportedEventId() {
		return supportedEventId;
	}
	public void setSupportedEventId(int supportedEventId) {
		this.supportedEventId = supportedEventId;
	}
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
}
