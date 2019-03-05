package com.liteon.icgwearable.model;

/**
 * @author BGH47285
 *
 */
public class IPSReceiverCSVModel {
	
	private String recordType;
	 //used in case of deletion, record id is anything but receiver, zone and device id
	private String recordId;
	
	//-----Receiver fields--------------
	
	 private String receiverMac;
	 private String receiverName;	 
	 private String receiverVersion;
	 
	 
	//-----Receiver zone fields--------------
	  // private Integer receiverId;
		private String zoneName;
		 private String mapType;	
		 private String buildingName;	
		 private String floorNum; 
		 private String deleteMapFile;	

	//-----Receiver device fields--------------
		// private Integer zoneId;
		// private Integer receiverId;
		 private String deviceUUID;
		 private String firmwareName;
		 private String firmwareVersion;
		 private String deviceModel;
		 private String status;
		 private String statusDesc;
		 
		 public String getRecordType() {
			return recordType;
		}
		public void setRecordType(String recordType) {
			this.recordType = recordType;
		}
		
		
		public String getRecordId() {
			return recordId;
		}
		public void setRecordId(String recordId) {
			this.recordId = recordId;
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
		
		public String getReceiverVersion() {
			return receiverVersion;
		}
		public void setReceiverVersion(String receiverVersion) {
			this.receiverVersion = receiverVersion;
		}
		public String getZoneName() {
			return zoneName;
		}
		public void setZoneName(String zoneName) {
			this.zoneName = zoneName;
		}
		public String getMapType() {
			return mapType;
		}
		public void setMapType(String mapType) {
			this.mapType = mapType;
		}
		public String getBuildingName() {
			return buildingName;
		}
		public void setBuildingName(String buildingName) {
			this.buildingName = buildingName;
		}
		public String getFloorNum() {
			return floorNum;
		}
		public void setFloorNum(String floorNum) {
			this.floorNum = floorNum;
		}
		public String getDeleteMapFile() {
			return deleteMapFile;
		}
		public void setDeleteMapFile(String deleteMapFile) {
			this.deleteMapFile = deleteMapFile;
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
		@Override
		public String toString() {
			return "IPSReceiverCSVModel [recordType=" + recordType +", recordId=" + recordId + ", receiverMac=" + receiverMac + ", receiverName="
					+ receiverName  + ", receiverVersion=" + receiverVersion + ", zoneName="
					+ zoneName + ", mapType=" + mapType + ", buildingName=" + buildingName + ", floorNum=" + floorNum
					+ ", deleteMapFile=" + deleteMapFile + ", deviceUUID=" + deviceUUID + ", firmwareName="
					+ firmwareName + ", firmwareVersion=" + firmwareVersion + ", deviceModel=" + deviceModel
					+ ", status=" + status + ", statusDesc=" + statusDesc + "]";
			
			//return "IPSReceiverCSVModel [recordType=" + recordType +", recordId=" + recordId+"]";
		}
		
		 
		 
		 
}
