package com.liteon.icgwearable.hibernate.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "supported_events")
public class SupportedEvents implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3294146412351634064L;
	@Id
	@GeneratedValue
	@Column(name = "event_id", unique = true, nullable = false)
	private Integer eventId;
	@Column(name = "event_name")
	private String eventName;
	@Column(name = "event_description")
	private String eventDescription;
	@Column(name = "event_default",columnDefinition="enum('yes','no')")
	private String eventDefault;
	
	@Column(name = "notify_member",columnDefinition="enum('yes','no')")
	private String notifyMember;
	
	public String getNotifyMember() {
		return notifyMember;
	}

	public void setNotifyMember(String notifyMember) {
		this.notifyMember = notifyMember;
	}

	@Column(name = "parent_unsubscribe",columnDefinition="enum('yes','no')")
	private String eventUnsubscribeBparent;
	
	public String getEventUnsubscribeBparent() {
		return eventUnsubscribeBparent;
	}

	public void setEventUnsubscribeBparent(String eventUnsubscribeBparent) {
		this.eventUnsubscribeBparent = eventUnsubscribeBparent;
	}

	@Column(name = "generated_by", columnDefinition="enum('device','system')")
	private String generatedBy;
	
	public String getGeneratedBy() {
		return generatedBy;
	}

	public void setGeneratedBy(String generatedBy) {
		this.generatedBy = generatedBy;
	}

	public String getNotifyStaff() {
		return notifyStaff;
	}

	public void setNotifyStaff(String notifyStaff) {
		this.notifyStaff = notifyStaff;
	}

	public String getNotify() {
		return notify;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public String getNotifyTeacher() {
		return notifyTeacher;
	}

	public void setNotifyTeacher(String notifyTeacher) {
		this.notifyTeacher = notifyTeacher;
	}

	public Set<EventSubscriptions> getEventSubscriptions() {
		return eventSubscriptions;
	}

	public void setEventSubscriptions(Set<EventSubscriptions> eventSubscriptions) {
		this.eventSubscriptions = eventSubscriptions;
	}

	@Column(name = "notify_staff", columnDefinition="enum('yes','no')")
	private String notifyStaff;
	
	@Column(name = "notify_parent", columnDefinition="enum('yes','no')")
	private String notify;
	
	@Column(name = "notify_teacher", columnDefinition="enum('yes','no')")
	private String notifyTeacher;

	@Column(name = "supported_fields")
	private String supported_fields;
	
	public String getSupported_fields() {
		return supported_fields;
	}

	public void setSupported_fields(String supported_fields) {
		this.supported_fields = supported_fields;
	}

	@Transient
	private boolean eventChecked;
	
	public Set<DeviceEvents> getDeviceEvents() {
		return deviceEvents;
	}

	public Set<EventSubscriptions> getEventNotificationses() {
		return eventSubscriptions;
	}

	public void setDeviceEvents(Set<DeviceEvents> deviceEvents) {
		this.deviceEvents = deviceEvents;
	}

	public void setEventNotificationses(Set<EventSubscriptions> eventSubscriptions) {
		this.eventSubscriptions = eventSubscriptions;
	}

	@OneToMany(mappedBy = "events")
	private Set<DeviceEvents> deviceEvents = new HashSet<DeviceEvents>(0);
	@OneToMany(mappedBy = "events")
	private Set<EventSubscriptions> eventSubscriptions = new HashSet<EventSubscriptions>(0);

	public SupportedEvents() {
	}

	public SupportedEvents(String eventName, String eventDefault) {
		this.eventName = eventName;
		this.eventDefault = eventDefault;
	}

	public SupportedEvents(String eventName, String eventDescription, String eventTemplate, String eventDefault, Set deviceEventses/*,
			Set eventNotificationses*/) {
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.eventDefault = eventDefault;
		//this.deviceEvents = deviceEvents;
		this.deviceEvents = deviceEventses;
		//this.eventNotificationses = eventNotificationses;
	}

	public Integer getEventId() {
		return this.eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return this.eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDescription() {
		return this.eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	public String getEventDefault() {
		return this.eventDefault;
	}

	public void setEventDefault(String eventDefault) {
		this.eventDefault = eventDefault;
	}
	public Set getDeviceEventses() {
		return this.deviceEvents;
	}

	public void setDeviceEventses(Set deviceEventses) {
		this.deviceEvents = deviceEventses;
	}

	public boolean isEventChecked() {
		return eventChecked;
	}

	public void setEventChecked(boolean eventChecked) {
		this.eventChecked = eventChecked;
	}
	}
