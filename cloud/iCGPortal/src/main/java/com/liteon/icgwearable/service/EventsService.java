package com.liteon.icgwearable.service;

import java.util.List;

import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.EventSubscriptions;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.transform.DeviceEventsTransform;
import com.liteon.icgwearable.transform.GeoFenceEntyExitTransform;
import com.liteon.icgwearable.transform.SubscribedEvents;

public interface EventsService {
	public List<SubscribedEvents> getUserSubscribedEvents(int userId, String uuid, String userRole);

	public EventSubscriptions isMembersubscribed(int userId, int eventId, int studentId);

	public void subscribeEvent(EventSubscriptions es);

	public void updateSubscribedEvent(EventSubscriptions es);

	public int getPairedStatusWithMemberAndDevice(String uuid, int member_id);

	public int getvalidStudentUnderParentmember(int student_id, int parent_id);

	public Devices getDeviceByUuid(String uuid);

	public SupportedEvents getSupportedEventsByEventId(int eventid);

	public void unsubscribeEvent(int memberID, int studentID, int alertid);

	public List<DeviceEventsTransform> findGPSData(int studentId, int eventId, String startDt, String endDt);

	public List<DeviceEventsTransform> findAbnormalVitalSign(int studentId, int eventId, String startDt, String endDt);

	public List<DeviceEventsTransform> findSensorMalfunction(int studentId, int eventId, String startDt, String endDt);

	public List<DeviceEventsTransform> findBatteryLevel(int studentId, int eventId, String startDt, String endDt);

	public boolean deviceExistForEvent(String uuid, int eventId);

	public boolean deleteDeviceEventsForReceiverDevId(int receiverDevId);

	public List<Integer> getDeviceEventsForLocatorId(int locators_Id);

	public int updateZoneIdForDeviceEventIds(List<Integer> eventIdList);

	public int deleteDeviceEventsQueueForEventIds(List<Integer> eventIdList);

	public List<Integer> getDeviceEventIdsForGivenIdList(List<Integer> receiverZoneIdList);

	public List<Integer> getDefaultEventIdListForParent();

	public List<DeviceEventsTransform> findBandBackAlertAndSOSRemovingData(int studentId, int eventId, String startDt,
			String endDt);

	public List<DeviceEventsTransform> findBeaconEventsData(String sessionId, int eventId, String startDt, String endDt,
			int zone_id, int ips_receiver_id);

	public List<GeoFenceEntyExitTransform> getGeoFenceEntryOrExitEvents(int school_id, String inputdate);

	public List<Integer> getZoneIdListForReceiver(int ips_receiver_id);

	public void subscribeEvent(int memberID, int studentID, int alertid);

	public boolean isMemberSubscribed(int memberID, int studentID, int alertid);

	public boolean isSupportedEvent(Integer eventId);
}
