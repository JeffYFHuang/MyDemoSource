package com.liteon.icgwearable.model;

public class FCMModel {
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	private String title;
	private String body;
	private Integer deviceEventId;
	private Integer eventId;
	
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public Integer getDeviceEventId() {
		return deviceEventId;
	}
	public void setDeviceEventId(Integer deviceEventId) {
		this.deviceEventId = deviceEventId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getQueueid() {
		return queueid;
	}
	public void setQueueid(int queueid) {
		this.queueid = queueid;
	}
	private String uuid="Empty";
	private Integer queueid;

	private String studentName;
	
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

}
