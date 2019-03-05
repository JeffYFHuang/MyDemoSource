package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "geozones")
public class Geozones {
	

	@Id
	@GeneratedValue
	@Column(name = "geozone_id", unique = true, nullable = false)
	private Integer geozoneId;
	
	@Column(name = "uuid")
	private String uuid;

	@Column(name = "zone_details")
	private String zoneDetails;
	
	@Column(name = "zone_name")
	private String zonename;
	
	@Column(name = "zone_entry_alert", columnDefinition="enum('yes','no')")
	private String zoneEntryAlert;
	
	@Column(name = "zone_exit_alert", columnDefinition="enum('yes','no')")
	private String zoneExitAlert;
	
	@Column(name = "zone_description")
	private String zoneDescription;
	
	@Column(name = "frequency_minutes")
	private Integer frequencyMinutes;
	
	@Column(name = "valid_till", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
	private Date validTill;
	@Column(name = "create_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
	private Date createDate;
	
	@Column(name = "updated_date", columnDefinition="DATETIME")
	@DateTimeFormat(pattern="dd-MM-yyyy  HH:mm:ss")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss")
	private Date updatedDate;
	
	public Set<DeviceEvents> getDeviceEvents() {
		return deviceEvents;
	}

	public void setDeviceEvents(Set<DeviceEvents> deviceEvents) {
		this.deviceEvents = deviceEvents;
	}

	@OneToMany(mappedBy = "geozones")
	@JsonIgnore
	private Set<DeviceEvents> deviceEvents = new HashSet<DeviceEvents>(0);
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id")
	private Users users;
	
	@Column(name="zone_radius")
	private Float zone_radius;
	
	public Float getZone_radius() {
		return zone_radius;
	}

	public void setZone_radius(Float zone_radius) {
		this.zone_radius = zone_radius;
	}

	public Integer getGeozoneId() {
		return geozoneId;
	}

	public void setGeozoneId(Integer geozoneId) {
		this.geozoneId = geozoneId;
	}

	public String getZoneDetails() {
		return zoneDetails;
	}

	public void setZoneDetails(String zoneDetails) {
		this.zoneDetails = zoneDetails;
	}

	public String getZonename() {
		return zonename;
	}

	public void setZonename(String zonename) {
		this.zonename = zonename;
	}

	public String getZoneEntryAlert() {
		return zoneEntryAlert;
	}

	public void setZoneEntryAlert(String zoneEntryAlert) {
		this.zoneEntryAlert = zoneEntryAlert;
	}

	public String getZoneExitAlert() {
		return zoneExitAlert;
	}

	public void setZoneExitAlert(String zoneExitAlert) {
		this.zoneExitAlert = zoneExitAlert;
	}

	public String getZoneDescription() {
		return zoneDescription;
	}

	public void setZoneDescription(String zoneDescription) {
		this.zoneDescription = zoneDescription;
	}

	public Integer getFrequencyMinutes() {
		return frequencyMinutes;
	}

	public void setFrequencyMinutes(Integer frequencyMinutes) {
		this.frequencyMinutes = frequencyMinutes;
	}

	public Date getValidTill() {
		return validTill;
	}

	public void setValidTill(Date validTill) {
		this.validTill = validTill;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
