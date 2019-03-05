package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "ips_receiver_device")
public class IPSReceiverDevice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 256632504692023168L;
	
	
	 @Id
	 @GeneratedValue
	 @Column(name = "ips_receiver_device_id", unique = true, nullable = false)
	 private Integer receiverDeviceId;
	 
	 @Column(name = "ips_receiver_zone_id")
	 private Integer zoneId;
	 
	 @Column(name = "ips_receiver_id")
	 private Integer receiverId;
	 
	 @Column(name="device_uuid")
	 private String deviceUUID;
	 
	 @Column(name="firmware_name")
	 private String firmwareName;
	 
	 @Column(name="firmware_version")
	 private String firmwareVersion;

	 @Column(name="device_model")
	 private String deviceModel;
	 
	 @Column(name="status")
	 private String status;
	 
	 @Column(name="status_description")
	 private String statusDesc;
	 
	 @Column(name = "created_date", columnDefinition="DATETIME")
	 @DateTimeFormat(pattern="${display.dateTime}")
	 private Date created_date;
	 
	 @Column(name = "updated_date", columnDefinition="DATETIME")
	 @DateTimeFormat(pattern="${display.dateTime}")
	 private Date updated_date;

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

	
	
	public Integer getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
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

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "IPSReceiverDevice [receiverDeviceId=" + receiverDeviceId + ", zoneId=" + zoneId + ", deviceUUID="
				+ deviceUUID + ", firmwareName=" + firmwareName + ", firmwareVersion=" + firmwareVersion
				+ ", deviceModel=" + deviceModel + ", status=" + status + ", statusDesc=" + statusDesc
				+ ", created_date=" + created_date + ", updated_date=" + updated_date + "]";
	}
	 
	 
	 
}
