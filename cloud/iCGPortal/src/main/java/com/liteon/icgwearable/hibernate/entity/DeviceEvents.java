package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "device_events")
public class DeviceEvents implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7951308721596497270L;
	@Id
	@GeneratedValue
	@Column(name = "device_event_id")
	private int deviceeventid;
	@Column(name = "uuid")
	private String uuid;
	@Column(name = "gps_data_code")
	private String gpsdatacode;
	@Column(name = "gps_location_data")
	private String gpslocationdata;
	@Column(name = "sensor_type_code")
	private String sensortypecode;
	@Column(name = "sensor_error_code")
	private String sensorerrorcode;
	@Column(name = "vital_sign_type")
	private String vitalsigntype;
	@Column(name = "battery_level_value")
	private String batterylevelvalue;
	@Column(name = "event_occured_date", columnDefinition="DATETIME")
	private Date eventoccureddate;
	@Column(name = "out_time", columnDefinition="DATETIME")
	private Date outTime;
	@Column(name = "is_entry_exit_abnormal", columnDefinition="enum('yes','no','')")
	private String isEntryExitAbnormal;
	@Column(name = "vital_sign_value")
	private String vitalsignvalue;
	@Column(name = "abnormal_code")
	private String abnormal_code;
	@Column(name = "in_time", columnDefinition="DATETIME")
	private Date inTime;
	@Column(name = "abnormal_reason")
	private String abormalReason;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "deviceEvents")
	private Set<DeviceEventsQueue> deviceEventsQueues = new HashSet<DeviceEventsQueue>(0);
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "event_id", nullable = false)
	private SupportedEvents events;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "geozone_id", nullable = false)
	private Geozones geozones;
	
	@Column(name = "ips_receiver_zone_id")
	private Integer ips_receiver_zone_id;
	
	@Column(name = "ips_receiver_id")
	private Integer ips_receiver_id;
	
	public Integer getIps_receiver_id() {
		return ips_receiver_id;
	}
	public void setIps_receiver_id(Integer ips_receiver_id) {
		this.ips_receiver_id = ips_receiver_id;
	}
	public Integer getIps_receiver_zone_id() {
		return ips_receiver_zone_id;
	}
	public void setIps_receiver_zone_id(Integer ips_receiver_zone_id) {
		this.ips_receiver_zone_id = ips_receiver_zone_id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getIsEntryExitAbnormal() {
		return isEntryExitAbnormal;
	}
	public void setIsEntryExitAbnormal(String isEntryExitAbnormal) {
		this.isEntryExitAbnormal = isEntryExitAbnormal;
	}
	
	public String getAbormalReason() {
		return abormalReason;
	}
	public void setAbormalReason(String abormalReason) {
		this.abormalReason = abormalReason;
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getAbnormal_code() {
		return abnormal_code;
	}

	public void setAbnormal_code(String abnormal_code) {
		this.abnormal_code = abnormal_code;
	}

	public String getVitalsignvalue() {
		return vitalsignvalue;
	}

	public void setVitalsignvalue(String vitalsignvalue) {
		this.vitalsignvalue = vitalsignvalue;
	}

	public Set<DeviceEventsQueue> getDeviceEventsQueues() {
		return deviceEventsQueues;
	}

	public void setDeviceEventsQueues(Set<DeviceEventsQueue> deviceEventsQueues) {
		this.deviceEventsQueues = deviceEventsQueues;
	}
	
	public Geozones getGeozones() {
		return geozones;
	}
	public void setGeozones(Geozones geozones) {
		this.geozones = geozones;
	}

	public int getDeviceeventid() {
		return deviceeventid;
	}

	public void setDeviceeventid(int deviceeventid) {
		this.deviceeventid = deviceeventid;
	}

	public String getGpsdatacode() {
		return gpsdatacode;
	}

	public void setGpsdatacode(String gpsdatacode) {
		this.gpsdatacode = gpsdatacode;
	}

	public String getGpslocationdata() {
		return gpslocationdata;
	}

	public void setGpslocationdata(String gpslocationdata) {
		this.gpslocationdata = gpslocationdata;
	}

	public String getSensortypecode() {
		return sensortypecode;
	}

	public void setSensortypecode(String sensortypecode) {
		this.sensortypecode = sensortypecode;
	}

	public String getSensorerrorcode() {
		return sensorerrorcode;
	}

	public void setSensorerrorcode(String sensorerrorcode) {
		this.sensorerrorcode = sensorerrorcode;
	}

	public String getVitalsigntype() {
		return vitalsigntype;
	}

	public void setVitalsigntype(String vitalsigntype) {
		this.vitalsigntype = vitalsigntype;
	}

	public String getBatterylevelvalue() {
		return batterylevelvalue;
	}

	public void setBatterylevelvalue(String batterylevelvalue) {
		this.batterylevelvalue = batterylevelvalue;
	}

	public Date getEventoccureddate() {
		return eventoccureddate;
	}

	public void setEventoccureddate(Date eventoccureddate) {
		this.eventoccureddate = eventoccureddate;
	}

	public SupportedEvents getEvents() {
		return events;
	}

	public void setEvents(SupportedEvents events) {
		this.events = events;
	}
/*	public Integer getips_receiver_device_id() {
		return ips_receiver_device_id;
	}
	public void setips_receiver_device_id(Integer ips_receiver_device_id) {
		this.ips_receiver_device_id = ips_receiver_device_id;
	}*/
}
