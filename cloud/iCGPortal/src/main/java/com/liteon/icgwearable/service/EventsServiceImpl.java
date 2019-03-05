package com.liteon.icgwearable.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.EventsDAO;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.EventSubscriptions;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.transform.DeviceEventsTransform;
import com.liteon.icgwearable.transform.GeoFenceEntyExitTransform;
import com.liteon.icgwearable.transform.SubscribedEvents;

import jnr.ffi.types.off_t;

@Service("eventsService")
public class EventsServiceImpl implements EventsService {

	@Autowired
	private EventsDAO eventsDAO;

	@Override
	public EventSubscriptions isMembersubscribed(int userId, int eventId, int studentId) {
		return this.eventsDAO.isMembersubscribed(userId, eventId, studentId);
	}

	@Override
	public void subscribeEvent(EventSubscriptions es) {
		this.eventsDAO.subscribeEvent(es);
	}

	@Override
	public Devices getDeviceByUuid(String uuid) {
		return this.eventsDAO.getDeviceByUuid(uuid);
	}

	@Override
	public SupportedEvents getSupportedEventsByEventId(int eventid) {
		return this.eventsDAO.getSupportedEventsByEventId(eventid);
	}

	@Override
	public void unsubscribeEvent(int userId, int eventId, int deviceId) {
		this.eventsDAO.unsubscribeEvent(userId, eventId, deviceId);
	}

	@Override
	public List<SubscribedEvents> getUserSubscribedEvents(int userId, String uuid, String userRole) {
		return this.eventsDAO.getUserSubscribedEvents(userId, uuid, userRole);
	}

	@Override
	public List<DeviceEventsTransform> findGPSData(int studentId, int eventId, String startDt, String endDt) {
		return this.eventsDAO.findGPSData(studentId, eventId, startDt, endDt);
	}

	@Override
	public List<DeviceEventsTransform> findAbnormalVitalSign(int studentId, int eventId, String startDt, String endDt) {
		return this.eventsDAO.findAbnormalVitalSign(studentId, eventId, startDt, endDt);
	}

	@Override
	public List<DeviceEventsTransform> findSensorMalfunction(int studentId, int eventId, String startDt, String endDt) {
		return this.eventsDAO.findSensorMalfunction(studentId, eventId, startDt, endDt);
	}

	@Override
	public List<DeviceEventsTransform> findBatteryLevel(int studentId, int eventId, String startDt, String endDt) {
		return this.eventsDAO.findBatteryLevel(studentId, eventId, startDt, endDt);
	}

	@Override
	public boolean deviceExistForEvent(String uuid, int eventId) {
		return this.eventsDAO.deviceExistForEvent(uuid, eventId);
	}

	@Override
	public void updateSubscribedEvent(EventSubscriptions es) {
		this.eventsDAO.updateSubscribedEvent(es);
	}

	@Override
	public int getPairedStatusWithMemberAndDevice(String uuid, int member_id) {
		return this.eventsDAO.getPairedStatusWithMemberAndDevice(uuid, member_id);
	}

	@Override
	public int getvalidStudentUnderParentmember(int student_id, int parent_id) {
		return this.eventsDAO.getvalidStudentUnderParentmember(student_id, parent_id);
	}

	@Override
	public List<Integer> getDeviceEventsForLocatorId(int locators_Id) {
		return this.eventsDAO.getDeviceEventsForLocatorId(locators_Id);
	}

	@Override
	public int updateZoneIdForDeviceEventIds(List<Integer> eventIdList) {
		return this.eventsDAO.updateZoneIdForDeviceEventIds(eventIdList);
	}

	@Override
	public int deleteDeviceEventsQueueForEventIds(List<Integer> eventIdList) {
		return this.eventsDAO.deleteDeviceEventsQueueForEventIds(eventIdList);
	}

	@Override
	public boolean deleteDeviceEventsForReceiverDevId(int receiverDevId) {
		return this.eventsDAO.deleteDeviceEventsForReceiverDevId(receiverDevId);
	}

	@Override
	public List<Integer> getDeviceEventIdsForGivenIdList(List<Integer> receiverZoneIdList) {
		return this.eventsDAO.getDeviceEventIdsForGivenIdList(receiverZoneIdList);
	}

	@Override
	public List<DeviceEventsTransform> findBandBackAlertAndSOSRemovingData(int studentId, int eventId, String startDt,
			String endDt) {
		return this.eventsDAO.findBandBackAlertAndSOSRemovingData(studentId, eventId, startDt, endDt);
	}

	@Override
	public List<DeviceEventsTransform> findBeaconEventsData(String sessionId, int eventId, String startDt, String endDt,
			int zone_id, int ips_receiver_id) {
		return this.eventsDAO.findBeaconEventsData(sessionId, eventId, startDt, endDt, zone_id, ips_receiver_id);
	}

	@Override
	public List<Integer> getDefaultEventIdListForParent() {
		return this.eventsDAO.getDefaultEventIdListForParent();
	}

	@Override
	public List<GeoFenceEntyExitTransform> getGeoFenceEntryOrExitEvents(int school_id, String inputdate) {
		return this.eventsDAO.getGeoFenceEntryOrExitEvents(school_id, inputdate);
	}
	
	@Override
	public List<Integer> getZoneIdListForReceiver(int ips_receiver_id){
		return this.eventsDAO.getZoneIdListForReceiver(ips_receiver_id);
	}

	@Override
	public void subscribeEvent(int memberID, int studentID, int alertid) {
		this.eventsDAO.subscribeEvent(memberID, studentID, alertid);
	}

	@Override
	public boolean isMemberSubscribed(int memberID, int studentID, int alertid) {
		return this.eventsDAO.isMemberSubscribed(memberID, studentID, alertid);
	}

	@Override
	public boolean isSupportedEvent(Integer eventId) {
		return this.eventsDAO.isSupportedEvent(eventId);
	}
}
