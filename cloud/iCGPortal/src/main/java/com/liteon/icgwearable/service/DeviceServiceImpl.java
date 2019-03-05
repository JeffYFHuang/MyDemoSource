package com.liteon.icgwearable.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.liteon.icgwearable.dao.DeviceDAO;
import com.liteon.icgwearable.hibernate.entity.DeviceConfigurations;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Geozones;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;
import com.liteon.icgwearable.hibernate.entity.PetDetails;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.BeaconDeviceEventCreateModel;
import com.liteon.icgwearable.model.DeviceEliminateModel;
import com.liteon.icgwearable.model.DeviceModel;
import com.liteon.icgwearable.transform.DeviceConfigurationsTransform;
import com.liteon.icgwearable.transform.DeviceStudentsTransform;
import com.liteon.icgwearable.transform.NotifyToTeacherAndStaffTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.SchoolInOutTransform;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private DeviceDAO deviceDAO;

	public void setDeviceDAO(DeviceDAO deviceDAO) {
		this.deviceDAO = deviceDAO;
	}

	@Override
	public boolean createDeviceEvent(DeviceModel deviceModel, int device_id, SupportedEvents supportedEvents,
			String uuid, Geozones geoZone) {
		return this.deviceDAO.createDeviceEvent(deviceModel, device_id, supportedEvents, uuid, geoZone);
	}

	@Override
	public Devices checkDeviceIdExist(int device_id) {
		return this.deviceDAO.checkDeviceIdExist(device_id);
	}

	@Override
	public boolean deviceExists(int deviceId) {
		return this.deviceDAO.deviceExists(deviceId);
	}

	@Override
	public boolean checkEventIdAndDeviceUuid(int event_id, String deviceUuid, int device_id) {
		return this.deviceDAO.checkEventIdAndDeviceUuid(event_id, deviceUuid, device_id);
	}

	@Override
	public SupportedEvents getEventsObjUsingId(int event_id) {
		return this.deviceDAO.getEventsObjUsingId(event_id);
	}

	@Override
	public List<DeviceConfigurationsTransform> getDeviceConfigurations(int deviceId) {
		return this.deviceDAO.getDeviceConfigurations(deviceId);
	}

	@Override
	public boolean addEntryToDeviceEventQueue(int queueId, int userId, int eventId, int device_id,
			SupportedEvents events, String androidAppToken, String iPhoneAppToken) {
		return this.deviceDAO.addEntryToDeviceEventQueue(queueId, userId, eventId, device_id, events, androidAppToken,
				iPhoneAppToken);
	}

	@Override
	public boolean updateDeviceEventQueue(int queue_id) {
		return this.deviceDAO.updateDeviceEventQueue(queue_id);
	}

	@Override
	public Devices checkDeviceIdExist(String uuid) {
		return this.deviceDAO.checkDeviceIdExist(uuid);
	}

	@Override
	public int getDeviceIdByUuidandUser(Users user, String uuid) {
		return this.deviceDAO.getDeviceIdByUuidandUser(user, uuid);
	}

	@Override
	public List<String> findDevicesByStudentId(int studentId) {
		return this.deviceDAO.findDevicesByStudentId(studentId);
	}

	@Override
	public Devices findDeviceByStudentId(int studentId) {
		return this.deviceDAO.findDeviceByStudentId(studentId);
	}

	@Override
	public void updateDevice(Devices devices) {
		this.deviceDAO.updateDevice(devices);
	}

	@Override
	public int findDeviceIdByUUID(String uuid) {
		return this.deviceDAO.findDeviceIdByUUID(uuid);
	}

	@Override
	public void updateDeviceBySessionId(int deviceId, String sessionId) {
		this.deviceDAO.updateDeviceBySessionId(deviceId, sessionId);
	}

	@Override
	public Devices findDevicesByUUIDAndSessionId(String uuid, String sessionId) {
		return this.deviceDAO.findDevicesByUUIDAndSessionId(uuid, sessionId);
	}

	@Override
	public Devices getDevicesBySessionId(String sessionId) {
		return this.deviceDAO.getDevicesBySessionId(sessionId);
	}

	@Override
	public String findDevicesByUUIDAndCheckSessionValidity(String uuid, String sessionId) {
		return this.deviceDAO.findDevicesByUUIDAndCheckSessionValidity(uuid, sessionId);
	}

	@Override
	public String getEventNamebyId(int event_id) {
		return this.deviceDAO.getEventNamebyId(event_id);
	}

	@Override
	public int findSchoolIdByUUID(String uuid) {
		return this.deviceDAO.findSchoolIdByUUID(uuid);
	}

	@Override
	public String getSessionIdbyuuid(String uuid) {
		return this.deviceDAO.getSessionIdbyuuid(uuid);
	}

	@Override
	public List<String> findUsersByDevice(String uuid) {
		return this.deviceDAO.findUsersByDevice(uuid);
	}

	@Override
	public String supportedFieldsUsingEventIdAndDeviceGenerated(int event_id) {
		return this.deviceDAO.supportedFieldsUsingEventIdAndDeviceGenerated(event_id);
	}

	@Override
	public int getlatestId() {
		return this.deviceDAO.getlatestId();
	}

	@Override
	public int getSessionValidityInMinutes(String uuid) {
		return this.deviceDAO.getSessionValidityInMinutes(uuid);
	}

	@Override
	public RewardsListTransform findRewardsByDeviceUuid(String uuid) {
		return this.deviceDAO.findRewardsByDeviceUuid(uuid);
	}

	@Override
	public List<Integer> getDeviceEventQueueIds() {
		return this.deviceDAO.getDeviceEventQueueIds();
	}

	@Override
	public int getlatestDeviceEventId() {
		return this.deviceDAO.getlatestDeviceEventId();
	}

	@Override
	public String findDeviceUUIDByStudentId(int studentId) {
		return this.deviceDAO.findDeviceUUIDByStudentId(studentId);
	}

	@Override
	public NotifyToTeacherAndStaffTransform getEventToNotifyToTeacherAndStaff(int event_id) {
		return this.deviceDAO.getEventToNotifyToTeacherAndStaff(event_id);
	}

	@Override
	public int getSchoolIdtoSendNotificationtoStaff(String time, String date, int school_id) {
		return this.deviceDAO.getSchoolIdtoSendNotificationtoStaff(time, date, school_id);
	}

	@Override
	public boolean checkDeviceAlreadyPairedWithUser(String uuid, int user_id) {
		return this.deviceDAO.checkDeviceAlreadyPairedWithUser(uuid, user_id);
	}

	@Override
	public List<DeviceStudentsTransform> findDeviceStudentsForStudentActivity(int studentId, String startDate,
			String endDate) {
		return this.deviceDAO.findDeviceStudentsForStudentActivity(studentId, startDate, endDate);
	}

	@Override
	public String getParentUnsunsribeEvent(int event_id) {
		return this.deviceDAO.getParentUnsunsribeEvent(event_id);
	}

	@Override
	public SchoolInOutTransform getschoolInOuttimings(int school_id) {
		return this.deviceDAO.getschoolInOuttimings(school_id);
	}

	@Override
	public int updateSchoolEntryOrExitforSchoolEvent(int device_event_id, String event_occured_time, String iSAbnormal,
			int event_id) {
		return this.deviceDAO.updateSchoolEntryOrExitforSchoolEvent(device_event_id, event_occured_time, iSAbnormal,
				event_id);
	}

	@Override
	public int updateGeoFenceEntryExitforSchoolEvent(int device_event_id, String event_occured_time, int event_id) {
		return this.deviceDAO.updateGeoFenceEntryExitforSchoolEvent(device_event_id, event_occured_time, event_id);
	}

	@Override
	public boolean findDeviceActiveOrInactive(String uuid, int userId) {
		return this.deviceDAO.findDeviceActiveOrInactive(uuid, userId);
	}

	@Override
	public void addPetData(PetDetails petDetails) {
		this.deviceDAO.addPetData(petDetails);
	}

	@Override
	public PetDetails getPetDetails(String uuid, Date inputDate) {
		return this.deviceDAO.getPetDetails(uuid, inputDate);
	}

	@Override
	public Devices findDeviceForSchoolId(String uuid, int schoolId) {
		return this.deviceDAO.findDeviceForSchoolId(uuid, schoolId);
	}

	@Override
	public DeviceConfigurations findDeviceConfigByFirmwareFile(String firmwareFile) {
		return this.deviceDAO.findDeviceConfigByFirmwareFile(firmwareFile);
	}

	@Override
	public DeviceConfigurations isFirmwareFileExists(String firmwareFile) {
		return this.deviceDAO.isFirmwareFileExists(firmwareFile);
	}

	@Override
	public boolean createBeaconDeviceEvent(BeaconDeviceEventCreateModel beaconDeviceEventCreateModel, int device_id,
			SupportedEvents supportedEvents, String uuidm, Geozones geoZone, int ipsReceiverZoneId, int ips_receiver_id) {
		return this.deviceDAO.createBeaconDeviceEvent(beaconDeviceEventCreateModel, device_id, supportedEvents, uuidm,
				geoZone, ipsReceiverZoneId, ips_receiver_id);
	}

	@Override
	public boolean findDeviceActive(int student_id, String uuid) {
		return this.deviceDAO.findDeviceActive(student_id, uuid);
	}

	@Override
	public int getSchoolIdbyDeviceUUID(String uuid) {
		return this.deviceDAO.getSchoolIdbyDeviceUUID(uuid);
	}

	@Override
	public List<Integer> getUseridListbyuuid(String uuid) {
		return this.deviceDAO.getUseridListbyuuid(uuid);
	}

	@Override
	public boolean findDeviceInActiveModeBasedOnUUID(String uuid) {
		return this.deviceDAO.findDeviceInActiveModeBasedOnUUID(uuid);
	}

	@Override
	public String getEventOcuuredDatebyzoneIdforCurrentDay(int geoZOneId) {
		return this.deviceDAO.getEventOcuuredDatebyzoneIdforCurrentDay(geoZOneId);
	}

	@Override
	public List<DeviceStudentsTransform> findStudentsInfoForFitnessActivity(int schoolId, String grade,
			String startDate, String endDate) {
		return this.deviceDAO.findStudentsInfoForFitnessActivity(schoolId, grade, startDate, endDate);
	}

	@Override
	public int getLatestGeoFenceEntryEventId(BeaconDeviceEventCreateModel beaconDeviceEventCreateModelList) {
		return this.deviceDAO.getLatestGeoFenceEntryEventId(beaconDeviceEventCreateModelList);
	}

	@Override
	public int getSchoolIdByUUID(String uuid) {
		return this.deviceDAO.getSchoolIdByUUID(uuid);
	}

	@Override
	public int getLatestGeozoneEventId(int geozone_id, int event_id) {
		return this.deviceDAO.getLatestGeozoneEventId(geozone_id, event_id);
	}
	
	@Override
	public int getLatestGeozoneEventOccurred(int geozoneId) {
		return this.deviceDAO.getLatestGeozoneEventOccurred(geozoneId);
	}
}
