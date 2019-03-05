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
@Table(name = "ips_receiver_zone")
public class IPSReceiverZone implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -990199385119284416L;
	
	
	 @Id
	 @GeneratedValue
	 @Column(name = "ips_receiver_zone_id", unique = true, nullable = false)
	 private Integer zoneId;
	 
	 @Column(name = "ips_receiver_id")
	 private Integer receiverId;
	 
	 @Column(name="zone_name")
	 private String zoneName;
	 
	 @Column(name="map_type")
	 private String mapType;
	 
	 @Column(name="building_name")
	 private String buildingName;
	 
	 @Column(name="floor_number")
	 private String floorNum;
	 
	 @Column(name="map_filename")
	 private String mapFilename;
	 
	 @Column(name = "created_date", columnDefinition="DATETIME")
	 @DateTimeFormat(pattern="${display.dateTime}")
	 private Date created_date;
	 
	 @Column(name = "updated_date", columnDefinition="DATETIME")
	 @DateTimeFormat(pattern="${display.dateTime}")
	 private Date updated_date;

	public Integer getZoneId() {
		return zoneId;
	}

	public void setZoneId(Integer zoneId) {
		this.zoneId = zoneId;
	}

	public Integer getReceiverId() {
		return receiverId;
	}

	public void setIpsReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
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

	public String getMapFilename() {
		return mapFilename;
	}

	public void setMapFilename(String mapFilename) {
		this.mapFilename = mapFilename;
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
		return "IPSReceiverZone [zoneId=" + zoneId + ", receiverId=" + receiverId + ", zoneName=" + zoneName
				+ ", mapType=" + mapType + ", buildingName=" + buildingName + ", floorNum=" + floorNum
				+ ", mapFilename=" + mapFilename + ", created_date=" + created_date + ", updated_date=" + updated_date
				+ "]";
	}

	
	 

}
