package com.liteon.icgwearable.model;

public class IPSReceiverModel {
	
	 private Integer receiverId;
	 private String receiverMac;
	 private String receiverName;
	 private Integer schoolId;
	 private String receiverVersion;
	// private String sessionId;
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	public String getReceiverMac() {
		return receiverMac;
	}
	public void setReceiverMac(String receiverMac) {
		this.receiverMac = receiverMac;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public Integer getSchoolId() {
		return schoolId;
	}
	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}
	public String getReceiverVersion() {
		return receiverVersion;
	}
	public void setReceiverVersion(String receiverVersion) {
		this.receiverVersion = receiverVersion;
	}
	@Override
	public String toString() {
		return "IPSReceiverModel [receiverId=" + receiverId + ", receiverMac=" + receiverMac + ", receiverName="
				+ receiverName + ", schoolId=" + schoolId + ", receiverVersion=" + receiverVersion + "]";
	}

	 
}
