package com.liteon.icgwearable.transform;

public class KidsListForParentMemberTransform {
	
	public KidsListForParentMemberTransform(){}
	
	private Integer deviceId;
	private String uuId;
	private Integer studentId;
//	private String studentName;
	private String studentnickName;
	private Integer memberuserId;
	private String memberName;
	//private int subscriptionId;
	private Integer eventId;
	public Integer getEventId() {
		return eventId;
	}
	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	private String  eventName;
	
	
	public Integer getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}
	public Integer getStudentId() {
		return studentId;
	}
	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}
	/*public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}*/
	public String getStudentnickName() {
		return studentnickName;
	}
	public void setStudentnickName(String studentnickName) {
		this.studentnickName = studentnickName;
	}

	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	
	public Integer getMemberuserId() {
		return memberuserId;
	}
	public void setMemberuserId(Integer memberuserId) {
		this.memberuserId = memberuserId;
	}
	public String getUuId() {
		return uuId;
	}
	public void setUuId(String uuId) {
		this.uuId = uuId;
	}
	

}
