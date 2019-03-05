package com.liteon.icgwearable.model;

public class IPSReceiverDeviceModel {
	
	 private Integer receiverDeviceId;
	 private Integer zoneId;
	 private Integer receiverId;
	 private String deviceUUID;
	 private String firmwareName;
	 private String firmwareVersion;
	 private String deviceModel;
	 private String status;
	 private String statusDesc;
	public Integer getReceiverDeviceId() {
		return receiverDeviceId;
	}
	public void setReceiverDeviceId(Integer receiverDeviceId) {
		this.receiverDeviceId = receiverDeviceId;
	}
	public Integer getZoneId() {
		return zoneId;
	}
	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}
	public String getDeviceUUID() {
		return deviceUUID;
	}
	public void setDeviceUUID(String deviceUUID) {
		this.deviceUUID = deviceUUID;
	}
	public String getFirmwareName() {
		return firmwareName;
	}
	public void setFirmwareName(String firmwareName) {
		this.firmwareName = firmwareName;
	}
	public String getFirmwareVersion() {
		return firmwareVersion;
	}
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	@Override
	public String toString() {
		return "IPSReceiverDeviceModel [receiverDeviceId=" + receiverDeviceId + ", zoneId=" + zoneId + ", receiverId="
				+ receiverId + ", deviceUUID=" + deviceUUID + ", firmwareName=" + firmwareName + ", firmwareVersion="
				+ firmwareVersion + ", deviceModel=" + deviceModel + ", status=" + status + ", statusDesc=" + statusDesc
				+ "]";
	}
	
	 
	 

}
