package com.liteon.icgwearable.dao;

import java.util.Date;
import java.util.List;

import com.liteon.icgwearable.hibernate.entity.DeviceConfigurations;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Geozones;
import com.liteon.icgwearable.hibernate.entity.PetDetails;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.BeaconDeviceEventCreateModel;
import com.liteon.icgwearable.model.DeviceModel;
import com.liteon.icgwearable.transform.DeviceConfigurationsTransform;
import com.liteon.icgwearable.transform.DeviceStudentsTransform;
import com.liteon.icgwearable.transform.NotifyToTeacherAndStaffTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.SchoolInOutTransform;

public interface DeviceDAO {
	boolean createDeviceEvent();

	boolean createDeviceEvent(DeviceModel deviceModel, int device_id, SupportedEvents supportedEvents, String uuidm,
			Geozones geoZone);

	boolean createBeaconDeviceEvent(BeaconDeviceEventCreateModel beaconDeviceEventCreateModel, int device_id,
			SupportedEvents supportedEvents, String uuidm, Geozones geoZone, int ipsReceiverZoneId, int ips_receiver_id);

	boolean deviceExists(int deviceId);

	public List<DeviceConfigurationsTransform> getDeviceConfigurations(int deviceId);

	public Devices checkDeviceIdExist(int device_id);

	public List<Integer> getUseridListbyuuid(String uuid);

	public int getSchoolIdbyDeviceUUID(String uuid);

	public boolean checkEventIdAndDeviceUuid(int event_id, String deviceUuid, int device_id);

	public SupportedEvents getEventsObjUsingId(int event_id);

	public boolean checkDeviceAlreadyPairedWithUser(String uuid, int user_id);

	public Devices checkDeviceIdExist(String uuid);

	public void createDevice(Devices devices);

	public void updateDevice(Devices devices);

	public boolean addEntryToDeviceEventQueue(int queueId, int userId, int eventId, int device_id,
			SupportedEvents events, String androidAppToken, String iPhoneAppToken);

	public boolean updateDeviceEventQueue(int queue_id);

	public int getDeviceIdByUuidandUser(Users user, String uuid);

	public List<String> findDevicesByStudentId(int studentId);

	public Devices findDeviceByStudentId(int studentId);

	public int findDeviceIdByUUID(String uuid);

	public List<Integer> getDeviceEventQueueIds();

	public void updateDeviceBySessionId(int deviceId, String sessionId);

	public Devices findDevicesByUUIDAndSessionId(String uuid, String sessionId);

	public Devices getDevicesBySessionId(String sessionId);

	public String findDevicesByUUIDAndCheckSessionValidity(String uuid, String sessionId);

	public String getEventNamebyId(int event_id);

	public int findSchoolIdByUUID(String uuid);

	public String getSessionIdbyuuid(String uuid);

	public List<String> findUsersByDevice(String uuid);

	public String supportedFieldsUsingEventIdAndDeviceGenerated(int event_id);

	public int getlatestId();

	public int getlatestDeviceEventId();

	public int getSessionValidityInMinutes(String uuid);

	public RewardsListTransform findRewardsByDeviceUuid(String uuid);

	public String findDeviceUUIDByStudentId(int studentId);

	public NotifyToTeacherAndStaffTransform getEventToNotifyToTeacherAndStaff(int event_id);

	public int getSchoolIdtoSendNotificationtoStaff(String time, String date, int school_id);

	public List<DeviceStudentsTransform> findDeviceStudentsForStudentActivity(int studentId, String startDate,
			String endDate);

	public String getParentUnsunsribeEvent(int event_id);

	public int updateSchoolEntryOrExitforSchoolEvent(int device_event_id, String event_occured_time, String iSAbnormal,
			int event_id);

	public int updateGeoFenceEntryExitforSchoolEvent(int device_event_id, String event_occured_time, int event_id);

	public SchoolInOutTransform getschoolInOuttimings(int school_id);

	public void addPetData(PetDetails petDetails);

	public boolean findDeviceActiveOrInactive(String uuid, int userId);

	public PetDetails getPetDetails(String uuid, Date inputDate);

	public Devices findDeviceForSchoolId(String uuid, int schoolId);

	public DeviceConfigurations findDeviceConfigByFirmwareFile(String firmwareFile);

	public DeviceConfigurations isFirmwareFileExists(String firmwareFile);

	public boolean findDeviceActive(int student_id, String uuid);

	public boolean findDeviceInActiveModeBasedOnUUID(String uuid);

	public String getEventOcuuredDatebyzoneIdforCurrentDay(int geoZOneId);

	public List<DeviceStudentsTransform> findStudentsInfoForFitnessActivity(int schoolId, String grade,
			String startDate, String endDate);

	public int getLatestGeoFenceEntryEventId(BeaconDeviceEventCreateModel beaconDeviceEventCreateModelList);

	int getSchoolIdByUUID(String uuid);

	int getLatestGeozoneEventId(int geozone_id, int event_id);
	
	public int getLatestGeozoneEventOccurred(int geozoneId);
	
}
