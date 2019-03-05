package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.util.Date;

public class GeoFenceEntyExitTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8102674698480543000L;
	
	private Integer receiverZoneId;
	private String zoneName;
	private String mapType;
	private String bulingName;
	private String floorNumber;
	private String mapFileName;
	private Integer deviceEventId;
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public String getOutTime() {
		return outTime;
	}
	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}
	private String inTime;

	private String outTime;
	private String stdentName;
	private Integer studentId;
	private String studentClass;

	private Date duration;
	
	public Date getDuration() {
		return duration;
	}
	public void setDuration(Date duration) {
		this.duration = duration;
	}
	public Integer getReceiverZoneId() {
		return receiverZoneId;
	}
	public void setReceiverZoneId(Integer receiverZoneId) {
		this.receiverZoneId = receiverZoneId;
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
	public String getBulingName() {
		return bulingName;
	}
	public void setBulingName(String bulingName) {
		this.bulingName = bulingName;
	}
	public String getFloorNumber() {
		return floorNumber;
	}
	public void setFloorNumber(String floorNumber) {
		this.floorNumber = floorNumber;
	}
	public String getMapFileName() {
		return mapFileName;
	}
	public void setMapFileName(String mapFileName) {
		this.mapFileName = mapFileName;
	}
	public Integer getDeviceEventId() {
		return deviceEventId;
	}
	public void setDeviceEventId(Integer deviceEventId) {
		this.deviceEventId = deviceEventId;
	}
	
	public String getStdentName() {
		return stdentName;
	}
	public void setStdentName(String stdentName) {
		this.stdentName = stdentName;
	}
	public Integer getStudentId() {
		return studentId;
	}
	public void setStudentId(Integer studentId) {
		this.studentId = studentId;
	}
	public String getStudentClass() {
		return studentClass;
	}
	public void setStudentClass(String studentClass) {
		this.studentClass = studentClass;
	}

	
	
}
