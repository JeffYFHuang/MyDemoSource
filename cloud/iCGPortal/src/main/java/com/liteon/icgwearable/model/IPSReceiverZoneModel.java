package com.liteon.icgwearable.model;

public class IPSReceiverZoneModel {

	private Integer zoneId;
	private Integer receiverId;
	private String zoneName;
	 private String mapType;	
	 private String buildingName;	
	 private String floorNum; 
	 private String mapFilename;	
	
	
	
	public Integer getZoneId() {
		return zoneId;
	}
	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}
	public Integer getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	public String getMapFilename() {
		return mapFilename;
	}
	public void setMapFilename(String mapFilename) {
		this.mapFilename = mapFilename;
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
	@Override
	public String toString() {
		return "IPSReceiverZoneModel [zoneId=" + zoneId + ", receiverId=" + receiverId + ", zoneName=" + zoneName
				+ ", mapType=" + mapType + ", buildingName=" + buildingName + ", floorNum=" + floorNum
				+ ", mapFilename=" + mapFilename + "]";
	}
	
	
	
}
