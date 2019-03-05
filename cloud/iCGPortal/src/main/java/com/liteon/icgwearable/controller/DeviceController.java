package com.liteon.icgwearable.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.apache.wink.json4j.JSONException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.datastax.driver.core.Row;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.DeviceConfigurations;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Geozones;
import com.liteon.icgwearable.hibernate.entity.IPSReceiver;
import com.liteon.icgwearable.hibernate.entity.PetDetails;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.BeaconDeviceEventCreateModel;
import com.liteon.icgwearable.model.BeaconDeviceEventsModel;
import com.liteon.icgwearable.model.DeviceConfigModel;
import com.liteon.icgwearable.model.DeviceConfigurationsModel;
import com.liteon.icgwearable.model.DeviceModel;
import com.liteon.icgwearable.model.FCMModel;
import com.liteon.icgwearable.model.GeoZoneModel;
import com.liteon.icgwearable.model.ParentActivityForDashboardModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.CassandraService;
import com.liteon.icgwearable.service.CommonService;
import com.liteon.icgwearable.service.DeviceAnalyticsOutputService;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.service.EventsService;
import com.liteon.icgwearable.service.IPSReceiverService;
import com.liteon.icgwearable.service.SystemConfigurationService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.DeviceAnalyticsOutputTransform;
import com.liteon.icgwearable.transform.DeviceEventsTransform;
import com.liteon.icgwearable.transform.DeviceStudentsTransform;
import com.liteon.icgwearable.transform.NotifyToTeacherAndStaffTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.SchoolInOutTransform;
import com.liteon.icgwearable.transform.ValidParentUsersWithTokenTransform;
import com.liteon.icgwearable.transform.ValidTeacherUsersWithTokenTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.DateUtil;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.FCMUtility;
import com.liteon.icgwearable.util.IPSConstants;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.StringUtility;

@RestController
public class DeviceController {

	private static Logger log = Logger.getLogger(DeviceController.class);

	@Autowired
	private DeviceService deviceService;
	@Resource(name = "configProperties")
	private Properties configProperties;
	@Autowired
	private UserService userService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private EventsService eventsService;
	@Autowired
	private CassandraService cassandraService;
	@Autowired
	private IPSReceiverService iPSReceiverService;
	@Value("${PARENT_ANDROID_SERVER_KEY}")
	private String PARENT_ANDROID_SERVER_KEY;
	@Value("${PARENT_APPLE_SERVER_KEY}")
	private String PARENT_APPLE_SERVER_KEY;
	@Value("${SCHOOL_ANDROID_SERVER_KEY}")
	private String SCHOOL_ANDROID_SERVER_KEY;
	@Value("${SCHOOL_APPLE_SERVER_KEY}")
	private String SCHOOL_APPLE_SERVER_KEY;
	@Autowired
	HttpServletResponse response;
	@Autowired
	private ActivityLogService activityLogs;
	@Autowired
	private HttpSession httpSession;

	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;
	@Autowired
	private DeviceAnalyticsOutputService deviceAnalyticsOutputService;
	@Value("${uuid.token.mapping}")
	private String uuidTokenMapping;
	@Value("${token.unavailable}")
	private String tokenUnavailable;
	@Value("${mac.token.mapping}")
	private String macTokenMapping;
	@Value("${start.date}")
	private String startDateFormat;
	@Value("${end.date}")
	private String endDateFormat;
	@Value("${Timezone}")
	private String timeZone;
	@Value("${application.url}")
	private String baseUrl;
	@Value("${downloads.url}")
	private String downloadsUrl;
	@Value("${firmware.download.path}")
	private String firmwareDownloadPath;
	@Value("${rewards.download.path}")
	private String rewardsDownloadPath;
	@Value("${SCHOOL_ENTRY_ALERT_ID}")
	private Integer SCHOOL_ENTRY_ID;
	@Value("${SCHOOL_EXIT_ALERT_ID}")
	private Integer SCHOOL_EXIT_ID;
	@Value("${GEOFENCE_ENTRY_ID}")
	private Integer GEOFENCE_ENTRY_ID;
	@Value("${GEOFENCE_EXIT_ID}")
	private Integer GEOFENCE_EXIT_ID;
	@Value("${GEOZONE_ENTRY_ID}")
	private Integer GEOZONE_ENTRY_ID;
	@Value("${GEOZONE_EXIT_ID}")
	private Integer GEOZONE_EIXT_ID;
	@Value("${SOS_ALERT_ID}")
	private Integer SOS_ID;
	@Value("${SOS_REMOVING_ID}")
	private Integer SOS_REMOVING_ID;
	@Value("${FALL_DETECTION_ID}")
	private Integer FALL_ID;
	@Value("${SENSOR_MALFUNCTION_ID}")
	private Integer SENSOR_MAL_ID;
	@Value("${ABNORMAL_VITAL_SIGN_ID}")
	private Integer ABNORMAL_ID;
	@Value("${LOW_BATTERY_ID}")
	private Integer LOW_B_ID;
	@Value("${STUDENT_LOCATION_ID}")
	private Integer STUDENT_L_ID;
	@Value("${BAND_REMOVAL_ALERT_ID}")
	private Integer BAND_RA_ID;
	@Value("${BAND_BACK_ALERT_ID}")
	private Integer BAND_RA_BACK_ID;
	@Value("${FLUME_URL}")
	private String FLUME_URL;
	@Value("${display.dateTime}")
	private String sourceDateFormat;
	@Value("${db.dateTime}")
	private String dbDateTime;
	@Value("${display.dateFormat}")
	private String sourceDisplayDateFormat;
	@Value("${display.dateTime}")
	private String sourceDateTime;
	@Value("${date.Timezone}")
	private String dateTimezone;
	@Autowired
	private SystemConfigurationService systemConfigurationService;

	@RequestMapping(value = "/wearable/WearableLogin/{uuid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String WearableLogin(@PathVariable("uuid") String uuid) {
		String sessionId = null;
		String type = null;

		log.info("uuid" + "\t" + uuid);
		String responseJSON = null;
		Devices devices = this.deviceService.checkDeviceIdExist(uuid);
		Integer[] configParams = this.systemConfigurationService.findSystemConfigurationParameters(1);
		log.info("ConfigParams" + "\n" + configParams[0] + "\t" + configParams[1] + "\t" + configParams[2] + "\t"
				+ configParams[3]);
		if (devices == null) {
			type = "device.WearableLogin";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_DEVICE_UUID_STATUS_MSG,
					Constant.WEARABLE_DEVICE_UUID_STATUS_CODE, type);
		}
		if (devices != null && (devices.getStatus().equals("broken") || devices.getStatus().equals("returned")
				|| devices.getStatus().equals(""))) {
			type = "device.WearableLogin";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_DEVICE_UUID_STATUS_MSG,
					Constant.WEARABLE_DEVICE_UUID_STATUS_CODE, type);
		}
		if (!this.deviceService.findDeviceInActiveModeBasedOnUUID(uuid)) {
			type = "device.WearableLogin";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_DEVICE_UUID_STATUS_MSG,
					Constant.WEARABLE_DEVICE_UUID_STATUS_CODE, type);
		} else {
			int sessionValidity = configParams[2];
			String oldSession = this.deviceService.getSessionIdbyuuid(uuid);
			if (oldSession != null && oldSession.trim().length() > 0
					&& CommonUtil.checkSessionValidity(devices).equals("VALID")) {
				sessionId = oldSession;
				sessionValidity = this.deviceService.getSessionValidityInMinutes(sessionId);
				if (sessionValidity == 0) {
					this.httpSession.invalidate();
					sessionId = this.httpSession.getId();
					sessionValidity = configParams[2];
					this.deviceService.updateDeviceBySessionId(devices.getDeviceId(), sessionId);
					log.info("sessionValidity" + "\t" + sessionValidity);
				}
			} else {
				this.httpSession.invalidate();
				sessionId = this.httpSession.getId();
				this.deviceService.updateDeviceBySessionId(devices.getDeviceId(), sessionId);
			}
			int schoolId = this.deviceService.findSchoolIdByUUID(uuid);
			type = "device.WearableLogin";
			responseJSON = ErrorCodesUtil.displayWearableLoginJSON(Constant.SucessMsgActivity,
					Constant.WEARABLE_FOTAUpdateSuccess_Code, type, sessionId, sessionValidity, schoolId);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/wearable/DeviceEventCreate/{sessionId}/{uuid}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String DeviceEventCreate(@PathVariable("uuid") String uuid, @PathVariable("sessionId") String sessionId,
			@RequestBody DeviceModel deviceModel) throws ParseException {

		log.info("Inside DeviceController.deviceCreateAlert()");
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String supportedFields = null;
		String type = "device.createalert";
		int createdEventId = 0;
		Devices devices = deviceService.checkDeviceIdExist(uuid);
		action = "Create";
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		String json = JSONUtility.convertObjectToJson(deviceModel);
		log.info("Device Model Json" + "\n" + json);
		JSONUtility jsonUtility = new JSONUtility();

		if (!JSONUtility.isValidJson(json) || !jsonUtility.checkJsonInput(json, sourceDateFormat)
				|| null == deviceModel.getSchoolId()) {
			ErrorMessage = "Input Is Invalid";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, Constant.WEARABLE_INPUT_INVALID_STATUS_CODE,
					type);
			return responseJSON;
		}

		String devicesbyUUIDAndSessionValidity = this.deviceService.findDevicesByUUIDAndCheckSessionValidity(uuid,
				sessionId);
		log.info("devicesbyUUIDAndSessionValidity" + "\t" + devicesbyUUIDAndSessionValidity);

		if (devices == null || devices.equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_DEVICE_UUID_STATUS_MSG,
					Constant.WEARABLE_DEVICE_UUID_STATUS_CODE, type);
			responseJSON = "{\"Return\": { \"Type\": \"device.DeviceEventCreate\",\"ResponseSummary\": { \"StatusCode\": \""
					+ StatusCode + "\",\"StatusMessage\": \"" + ErrorMessage + "\"}}}";
			return responseJSON;
		} else if (devicesbyUUIDAndSessionValidity.equals("success")) {

			int schoolID = this.deviceService.getSchoolIdbyDeviceUUID(uuid);
			if (null != deviceModel.getSchoolId()) {
				if (schoolID != deviceModel.getSchoolId()) {
					ErrorMessage = "Invaid school id and uuid combinaiton ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage,
							Constant.WEARABLE_INPUT_INVALID_STATUS_CODE, type);
					return responseJSON;
				}
			}

			SupportedEvents suppportedEvents = this.eventsService.getSupportedEventsByEventId(deviceModel.getEventid());

			if (suppportedEvents == null
					|| deviceModel.getEventid() == SCHOOL_ENTRY_ID
					|| deviceModel.getEventid() == SCHOOL_EXIT_ID
					|| deviceModel.getEventid() == GEOFENCE_ENTRY_ID
					|| deviceModel.getEventid() == GEOFENCE_EXIT_ID) {
				type = "device.DeviceEventCreate";
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_UNSUPPORTED_EVENT_STATUS_MSG,
						Constant.WEARABLE_UNSUPPORTED_EVENT_STATUS_CODE, type);
				return responseJSON;
			} else if ((supportedFields = this.deviceService
					.supportedFieldsUsingEventIdAndDeviceGenerated(deviceModel.getEventid())) != null) {

				log.info("supportedFields : " + supportedFields.toString());
				ErrorMessage = "Input is invalid";
				responseJSON = "{\"Return\": { \"Type\": \"device.DeviceEventCreate\",\"ResponseSummary\": { \"StatusCode\": \""
						+ Constant.WEARABLE_INPUT_INVALID_STATUS_CODE + "\",\"StatusMessage\": \"" + ErrorMessage
						+ "\"}}}";
				String[] supportedFieldsArray = supportedFields.split(",");

				for (String sf : supportedFieldsArray) {
					log.info("token" + sf);
					switch (sf) {

					case "gps_data_code":
						log.info("deviceModel.getGpsdatacode()" + (deviceModel.getGpsdatacode()));
						if (deviceModel.getGpsdatacode() == null || deviceModel.getGpsdatacode().trim().equals(""))
							return responseJSON;
						continue;
					case "gps_location_data":
						log.info("deviceModel.gps_location_data()" + (deviceModel.getGpslocationdata()));
						if (deviceModel.getGpslocationdata() == null)
							return responseJSON;
						continue;
					case "event_occured_date":
						log.info("deviceModel.event_occured_date()" + (deviceModel.getEventoccureddate()));
						if (deviceModel.getEventoccureddate() == null
								|| deviceModel.getEventoccureddate().trim().equals(""))
							return responseJSON;
						SimpleDateFormat eventsdf = new SimpleDateFormat(sourceDateFormat);
						try {
							eventsdf.setLenient(false);
							Date date = eventsdf.parse(deviceModel.getEventoccureddate());
							deviceModel.setEventOccurredDate(date);
							log.info("date is in correct format :  " + date);
						} catch (ParseException e) {
							log.info("date format or date is not valid");
							return responseJSON;
						}
						continue;
					case "vital_sign_type":
						if (deviceModel.getVitalsigntype() == null || deviceModel.getVitalsigntype().trim().equals(""))
							return responseJSON;
						continue;
					case "vital_sign_value":
						if (deviceModel.getVitalsignvalue() == null
								|| deviceModel.getVitalsignvalue().trim().equals(""))
							return responseJSON;
						continue;
					case "abnormal_code":
						if (deviceModel.getAbnormalcode() == null || deviceModel.getAbnormalcode().trim().equals(""))
							return responseJSON;
						continue;
					case "sensor_type_code":
						if (deviceModel.getSensortypecode() == null
								|| deviceModel.getSensortypecode().trim().equals(""))
							return responseJSON;
						continue;
					case "sensor_error_code":
						if (deviceModel.getSensorerrorcode() == null
								|| deviceModel.getSensorerrorcode().trim().equals(""))
							return responseJSON;
						continue;
					case "battery_level_value":
						if (deviceModel.getBatterylevelvalue() == null
								|| deviceModel.getBatterylevelvalue().trim().equals(""))
							return responseJSON;
						continue;
					}
				}

				log.info("after check  : ");

				SupportedEvents supportedEvents = this.deviceService.getEventsObjUsingId(deviceModel.getEventid());
				SchoolInOutTransform schoolInOutTimeModel = this.deviceService
						.getschoolInOuttimings(deviceModel.getSchoolId());
				if (null == schoolInOutTimeModel || null == schoolInOutTimeModel.getSchool_in_end()
						|| null == schoolInOutTimeModel.getSchool_in_start()
						|| null == schoolInOutTimeModel.getSchool_out_end()
						|| null == schoolInOutTimeModel.getSchool_out_start()) {
					responseJSON = "{\"Return\": { \"Type\": \"device.DeviceEventCreate\",\"ResponseSummary\": { \"StatusCode\": \""
							+ Constant.WEARABLE_MISSING_SCHOOL_TIMES_STATUS_CODE + "\",\"StatusMessage\": \""
							+ Constant.WEARABLE_MISSING_SCHOOL_TIMES_STATUS_MSG + "\"}}}";
					return responseJSON;

				}
				Geozones geoZone = null;
				boolean createEvent = this.deviceService.createDeviceEvent(deviceModel, devices.getDeviceId(),
						supportedEvents, uuid, geoZone);
				if (createEvent) {
					createdEventId = this.deviceService.getlatestDeviceEventId();
					log.info("latest created event id is : " + createdEventId);
					log.info("deviceModel.getEventid() is: " + deviceModel.getEventid());
					if (deviceModel.getEventid() == SOS_ID) {

						String[] data = deviceModel.getEventoccureddate().split(" ");
						String time = data[1];
						String isAbnormal = "no";
						Calendar calendar = Calendar.getInstance();
						int day = calendar.get(Calendar.DAY_OF_WEEK);
						if (day != 1 || day != 7) {
							isAbnormal = CommonUtil.isStudentSoSAbnormal(time,
									schoolInOutTimeModel.getSchool_in_end().toString(),
									schoolInOutTimeModel.getSchool_out_start().toString());
						}
						this.deviceService.updateSchoolEntryOrExitforSchoolEvent(createdEventId, time, isAbnormal,
								deviceModel.getEventid());

					}

					String parentUnsubscribeEvents = this.deviceService
							.getParentUnsunsribeEvent(deviceModel.getEventid());

					FCMModel fcmModel = new FCMModel();
					String eventName = deviceService.getEventNamebyId(deviceModel.getEventid());
					fcmModel.setBody(eventName + " Occured at " + deviceModel.getEventoccureddate().toString());
					log.info(eventName + " Occured at " + deviceModel.getEventoccureddate().toString());
					fcmModel.setUuid(uuid);
					log.info("uuid" + uuid);

					log.info("Inside CreateEvent Boolean true");
					StatusCode = Constant.WEARABLE_FOTAUpdateSuccess_Code;
					ErrorMessage = Constant.SucessMsgActivity;
					if (null != parentUnsubscribeEvents) {
						log.info("createdEventId is :" + createdEventId);
						log.info("parent can unsubscribe events ?: " + parentUnsubscribeEvents);
						List<ValidParentUsersWithTokenTransform> parentUsers = userService
								.getParentUserTokenDetails(deviceModel.getEventid(), uuid, parentUnsubscribeEvents);
						log.info("Parent users size : " + parentUsers.size());
						if (parentUsers.size() == 0) {
							log.info("NO Parent users subscribed for this event");
						} else {
							for (ValidParentUsersWithTokenTransform item : parentUsers) {
								log.info("sendign message to Parent User" + item.getUserId());

								try {
									int latestQueueId = this.deviceService.getlatestId();
									latestQueueId++;
									this.deviceService.addEntryToDeviceEventQueue(latestQueueId, item.getUserId(),
											createdEventId, devices.getDeviceId(), supportedEvents,
											item.getAndroidAppToken(), item.getiPhoneAppToken());
									int queueid = this.deviceService.getlatestId();
									String fcmTitle = eventName + ": " + item.getStudentName();
									fcmModel.setTitle(fcmTitle);
									fcmModel.setQueueid(queueid);
									fcmModel.setStudentName(item.getStudentName());
									fcmModel.setDeviceEventId(createdEventId);
									fcmModel.setEventId(deviceModel.getEventid());
									log.info("Sending notification to parent user on IPHONE");
									log.info("QueueID:" + queueid);
									log.info("UserID:" + item.getUserId());
									log.info("StudentName:" + item.getStudentName());
									log.info("Message Body:" + fcmModel.getBody());

									if (null != item.getAndroidAppToken() && !item.getAndroidAppToken().equals("")) {
										log.info("Sending notification to parent user on Android");
										log.info("PARENT_ANDROID_SERVER_KEY:" + PARENT_ANDROID_SERVER_KEY);
										log.info("App Token:" + item.getAndroidAppToken());
										FCMUtility.PushNotfication(PARENT_ANDROID_SERVER_KEY, item.getAndroidAppToken(),
												fcmModel);
									}
									if (null != item.getiPhoneAppToken() && !item.getiPhoneAppToken().equals("")) {
										log.info("Sending notification to parent user on IPHONE");
										log.info("PARENT_APPLE_SERVER_KEY:" + PARENT_APPLE_SERVER_KEY);
										log.info("App Token:" + item.getiPhoneAppToken());
										FCMUtility.PushNotfication(PARENT_APPLE_SERVER_KEY, item.getiPhoneAppToken(),
												fcmModel);
									}
									log.info("After sending notification to parent user");
								} catch (Exception e) {
									log.error("Error inside ValidParentUsersWithTokenTransform");
								}
							}
						}
					}
					log.info("deviceModel.getEventid()" + "\t" + deviceModel.getEventid());
					Calendar calendar = Calendar.getInstance();
					int day = calendar.get(Calendar.DAY_OF_WEEK);
					if (day != 1 || day != 7) {
						NotifyToTeacherAndStaffTransform notifyList = deviceService
								.getEventToNotifyToTeacherAndStaff(deviceModel.getEventid());
						int schoolId = 0;

						if (notifyList.getNotify_teacher().toUpperCase().equals("YES")) {
							String eventOccuredadte = deviceModel.getEventoccureddate();
							String[] data = eventOccuredadte.split(" ");
							String date = data[0];
							String time = data[1];
							log.info("date" + date);
							log.info("time" + time);
							log.info("deviceModel.getSchoolId()" + deviceModel.getSchoolId());
							schoolId = deviceService.getSchoolIdtoSendNotificationtoStaff(time, date,
									deviceModel.getSchoolId());

							if (schoolId == 0){
								log.info("Either non-school hours or holiday, no Push Notification to be sent");
							} else {
								log.info("Event occurred during school hours and non-holiday, need to push notification");
								List<ValidTeacherUsersWithTokenTransform> teacherUsers = userService
										.getTeacherUserTokenDetails(deviceModel.getEventid(), uuid);

								for (ValidTeacherUsersWithTokenTransform item : teacherUsers) {
									log.info("sendign message to Teacher User" + item.getUserId());
									try {
										int latestQueueId = this.deviceService.getlatestId();
										latestQueueId++;
										this.deviceService.addEntryToDeviceEventQueue(latestQueueId, item.getUserId(),
												createdEventId, devices.getDeviceId(), supportedEvents,
												item.getAndroidAppToken(), item.getiPhoneAppToken());
										int queueid = this.deviceService.getlatestId();
										String fcmTitle = eventName + ": " + item.getStudentName();
										fcmModel.setTitle(fcmTitle);
										fcmModel.setQueueid(queueid);
										fcmModel.setDeviceEventId(createdEventId);
										fcmModel.setStudentName(item.getStudentName());
										fcmModel.setEventId(deviceModel.getEventid());
										log.info("QueueID:" + queueid);
										log.info("UserID:" + item.getUserId());
										log.info("StudentName:" + item.getStudentName());
										log.info("Message Body:" + fcmModel.getBody());

										if (null != item.getAndroidAppToken()
												&& !item.getAndroidAppToken().equals("")) {
											log.info("Sending notification to parent user on Android");
											log.info("SCHOOL_ANDROID_SERVER_KEY:" + SCHOOL_ANDROID_SERVER_KEY);
											log.info("App Token:" + item.getAndroidAppToken());
											FCMUtility.PushNotfication(SCHOOL_ANDROID_SERVER_KEY,
													item.getAndroidAppToken(), fcmModel);
										}
										if (null != item.getiPhoneAppToken() && !item.getiPhoneAppToken().equals("")) {
											log.info("Sending notification to parent user on IPHONE");
											log.info("SCHOOL_APPLE_SERVER_KEY:" + SCHOOL_APPLE_SERVER_KEY);
											log.info("App Token:" + item.getiPhoneAppToken());
											FCMUtility.PushNotfication(SCHOOL_APPLE_SERVER_KEY,
													item.getiPhoneAppToken(), fcmModel);
										}
										log.info("After sending notification to teacher user");
									} catch (Exception e) {
										log.error("Error inside ValidTeacherUsersWithTokenTransform");
									}
								}

								if (notifyList.getNotify_staff().toUpperCase().equals("YES")) {
									List<ValidTeacherUsersWithTokenTransform> staffUsers = userService
											.getStaffUserTokenDetails(deviceModel.getEventid(), uuid);

									for (ValidTeacherUsersWithTokenTransform item : staffUsers) {
										log.info("sendign message to Staff User" + item.getUserId());
										try {
											int latestQueueId = this.deviceService.getlatestId();
											latestQueueId++;
											this.deviceService.addEntryToDeviceEventQueue(latestQueueId,
													item.getUserId(), createdEventId, devices.getDeviceId(),
													supportedEvents, item.getAndroidAppToken(),
													item.getiPhoneAppToken());
											int queueid = this.deviceService.getlatestId();
											String fcmTitle = eventName + ": " + item.getStudentName();
											fcmModel.setTitle(fcmTitle);
											fcmModel.setQueueid(queueid);
											fcmModel.setDeviceEventId(createdEventId);
											fcmModel.setStudentName(item.getStudentName());
											fcmModel.setEventId(deviceModel.getEventid());
											log.info("QueueID:" + queueid);
											log.info("UserID:" + item.getUserId());
											log.info("StudentName:" + item.getStudentName());
											log.info("Message Body:" + fcmModel.getBody());

											if (null != item.getAndroidAppToken()
													&& !item.getAndroidAppToken().equals("")) {
												log.info("Sending notification to parent user on Android");
												log.info("SCHOOL_ANDROID_SERVER_KEY:" + SCHOOL_ANDROID_SERVER_KEY);
												log.info("App Token:" + item.getAndroidAppToken());
												FCMUtility.PushNotfication(SCHOOL_ANDROID_SERVER_KEY,
														item.getAndroidAppToken(), fcmModel);
											}
											if (null != item.getiPhoneAppToken()
													&& !item.getiPhoneAppToken().equals("")) {
												log.info("Sending notification to parent user on IPHONE");
												log.info("SCHOOL_APPLE_SERVER_KEY:" + SCHOOL_APPLE_SERVER_KEY);
												log.info("App Token:" + item.getiPhoneAppToken());
												FCMUtility.PushNotfication(SCHOOL_APPLE_SERVER_KEY,
														item.getiPhoneAppToken(), fcmModel);
											}

											log.info("After sending notification to Staff user");
										} catch (Exception e) {
											log.error("Error inside ValidTeacherUsersWithTokenTransform");
										}
									}
								}
							}
						}
					}
					if (null != deviceModel.getGpslocationdata()) {
						log.error("inside getGpslocationdata non null check");
						List<Integer> parentList = this.commonService.getParentIdByUuid(uuid);
						for (Integer parent_id : parentList) {
							log.error("Befor calling: checkParentgeoZonesExists");
							checkParentgeoZonesExists(parent_id.intValue(), uuid, deviceModel, devices);
						}
					} else {
						log.error("inside getGpslocationdata is null check, not proceeding for Geozones");
					}

				} else {
					StatusCode = Constant.WEARABLE_CREATE_EVENT_STATUS_CODE;
					ErrorMessage = Constant.WEARABLE_CREATE_EVENT_STATUS_MSG;
				}
				responseJSON = "{\"Return\": { \"Type\": \"device.DeviceEventCreate\",\"ResponseSummary\": { \"StatusCode\": \""
						+ StatusCode + "\",\"StatusMessage\": \"" + ErrorMessage + "\"}}}";
				return responseJSON;

			} else {
				type = "device.DeviceEventCreate";
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_UNSUPPORTED_EVENT_STATUS_MSG,
						Constant.WEARABLE_UNSUPPORTED_EVENT_STATUS_CODE, type);
				return responseJSON;
			}
		} else if (devicesbyUUIDAndSessionValidity.equals(tokenUnavailable)) {
			type = "device.DeviceEventCreate";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INVALID_USER_STATUS_MSG,
					Constant.WEARABLE_INVALID_USER_STATUS_CODE, type);
			return responseJSON;
		} else if (devicesbyUUIDAndSessionValidity.equals(uuidTokenMapping)) {
			type = "device.DeviceEventCreate";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_UNAUTHORIZED_LOGIN_STATUS_MSG,
					Constant.SubscriptionsStatusCode, type);
			return responseJSON;
		} else if (devicesbyUUIDAndSessionValidity.equals("NOTVALID")) {
			type = "device.DeviceEventCreate";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
					Constant.WEARABLE_SESSION_EXPIRED_STATUS_CODE, type);
			return responseJSON;
		}

		return null;
	}

	public void checkParentgeoZonesExists(int parent_id, String uuid, DeviceModel deviceModel, Devices devices) {
		action = "Create";
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		List<GeoZoneModel> geozoneList = this.commonService.getUserGeoZones(parent_id, uuid);
		log.info("User ID  :" + parent_id);
		for (GeoZoneModel geoZone : geozoneList) {
			// cannot check if already reached last item
			int geoZoneFreequencyMinutes = geoZone.getFrequency_minutes();
			Date geoZoneValidTill = geoZone.getValid_till();
			int position = CommonUtil.getGeocodesPosition(geoZone.getZone_details(), deviceModel.getGpslocationdata(),
					geoZone.getZone_radius());
			long originalDiffinMinutes = 0;
			String existedEventperDay = null;
			if (position == -1 && geoZone.getZone_entry_alert().equals("yes")) {
				log.info("Geo zone entry detected");
				boolean isValid = false;
				deviceModel.setEventid(GEOZONE_ENTRY_ID);
				existedEventperDay = this.deviceService
						.getEventOcuuredDatebyzoneIdforCurrentDay(geoZone.getGeozone_id());
				log.info("existedEventperDay :" + existedEventperDay);
				log.info("Geo zone ID  :" + geoZone.getGeozone_id());
				String currentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				try {
					isValid = DateUtil.isStartDateAfterEndDate(geoZoneValidTill.toString(), currentdate,
							"yyyy-MM-dd HH:mm:ss");
					log.info("isValid" + isValid);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				log.info("event id is " + deviceModel.getEventid());
				if (isValid && deviceModel.getEventid() == GEOZONE_ENTRY_ID) {
					log.info("existedEventperDay :" + existedEventperDay);
					if (null != existedEventperDay) {
						originalDiffinMinutes = DateUtil.getDateDiffinMinutes(DateUtil.getTodaysDate(),
								existedEventperDay);
						log.info("originalDiffinMinutes : " + originalDiffinMinutes);
					}
				}

				if ((null == existedEventperDay && isValid) || (existedEventperDay != null && isValid
						&& originalDiffinMinutes > geoZoneFreequencyMinutes)) {
					log.info("isValid to create event : " + isValid);
					log.info("entered into geoZone");
					deviceModel.setEventid(GEOZONE_ENTRY_ID);
					SupportedEvents supportedEvents = this.deviceService.getEventsObjUsingId(deviceModel.getEventid());

					Geozones geoZone1 = commonService.getGeozoneById(geoZone.getGeozone_id());

					// Inactivate current zone entry/exit event queue
					int queue_id = this.deviceService.getLatestGeozoneEventId(geoZone.getGeozone_id(), GEOZONE_EIXT_ID);
					if (queue_id > 0) {
						this.deviceService.updateDeviceEventQueue(queue_id);
					}

					boolean createEvent = this.deviceService.createDeviceEvent(deviceModel, devices.getDeviceId(),
							supportedEvents, uuid, geoZone1);
					int createdEventId = this.deviceService.getlatestDeviceEventId();
					String parentUnsubscribeEvents = this.deviceService
							.getParentUnsunsribeEvent(deviceModel.getEventid());
					log.info("parent can unsubscribe events ?: " + parentUnsubscribeEvents);

					FCMModel fcmModel = new FCMModel();
					String eventName = deviceService.getEventNamebyId(deviceModel.getEventid());
					fcmModel.setDeviceEventId(createdEventId);
					fcmModel.setEventId(deviceModel.getEventid());

					log.info("eventName :" + eventName);
					fcmModel.setBody(eventName + " Occured at " + deviceModel.getEventoccureddate().toString());
					log.info(eventName + " Occured at " + deviceModel.getEventoccureddate().toString());
					fcmModel.setUuid(uuid);
					log.info("uuid" + uuid);

					log.info("Inside CreateEvent Boolean true");
					if (null != parentUnsubscribeEvents) {
						List<ValidParentUsersWithTokenTransform> parentUsers = userService
								.getParentUserTokenDetails(deviceModel.getEventid(), uuid, parentUnsubscribeEvents);
						log.info("Parent users size : " + parentUsers.size());

						if (parentUsers.size() == 0)
							log.info("NO Parent users subscribed for this event");

						for (ValidParentUsersWithTokenTransform item : parentUsers) {
							log.info("sending message to Parent User" + item.getUserId());

							try {
								int latestQueueId = this.deviceService.getlatestId();
								latestQueueId++;
								this.deviceService.addEntryToDeviceEventQueue(latestQueueId, item.getUserId(),
										createdEventId, devices.getDeviceId(), supportedEvents,
										item.getAndroidAppToken(), item.getiPhoneAppToken());
								int queueid = this.deviceService.getlatestId();
								String fcmTitle = eventName + ": " + item.getStudentName();
								fcmModel.setTitle(fcmTitle);
								fcmModel.setQueueid(queueid);
								fcmModel.setDeviceEventId(createdEventId);
								fcmModel.setStudentName(item.getStudentName());
								fcmModel.setEventId(deviceModel.getEventid());
								log.info("QueueID:" + queueid);
								log.info("UserID:" + item.getUserId());
								log.info("StudentName:" + item.getStudentName());
								log.info("Message Body:" + fcmModel.getBody());

								if (null != item.getAndroidAppToken() && !item.getAndroidAppToken().equals("")) {
									log.info("Sending notification to parent user on Android");
									log.info("PARENT_ANDROID_SERVER_KEY:" + PARENT_ANDROID_SERVER_KEY);
									log.info("App Token:" + item.getAndroidAppToken());
									FCMUtility.PushNotfication(PARENT_ANDROID_SERVER_KEY, item.getAndroidAppToken(),
											fcmModel);
								}
								if (null != item.getiPhoneAppToken() && !item.getiPhoneAppToken().equals("")) {
									log.info("Sending notification to parent user on IPHONE");
									log.info("PARENT_APPLE_SERVER_KEY:" + PARENT_APPLE_SERVER_KEY);
									log.info("App Token:" + item.getiPhoneAppToken());
									FCMUtility.PushNotfication(PARENT_APPLE_SERVER_KEY, item.getiPhoneAppToken(),
											fcmModel);
								}
								log.info("After sending notification to parent user");
							} catch (Exception e) {
								log.error("Error inside ValidParentUsersWithTokenTransform 2");
							}
						}
					}
				}
			} else if (position == 1 && geoZone.getZone_exit_alert().equals("yes")) {
				log.info("Geo zone exit detected");
				deviceModel.setEventid(GEOZONE_EIXT_ID);
				existedEventperDay = this.deviceService
						.getEventOcuuredDatebyzoneIdforCurrentDay(geoZone.getGeozone_id());
				if (null != existedEventperDay) {

					SupportedEvents supportedEvents = this.deviceService.getEventsObjUsingId(deviceModel.getEventid());
					Geozones geoZone1 = commonService.getGeozoneById(geoZone.getGeozone_id());

					int queue_id = this.deviceService.getLatestGeozoneEventId(geoZone.getGeozone_id(),
							GEOZONE_ENTRY_ID);
					if (queue_id > 0) {
						this.deviceService.updateDeviceEventQueue(queue_id);
					}

					boolean isValid = false;
					String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
					try {
						isValid = DateUtil.isStartDateAfterEndDate(geoZoneValidTill.toString(), currentDate,
								"yyyy-MM-dd HH:mm:ss");
						log.info("Geo zone exit, isValid: " + isValid);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					int latestGeozoneEventOccurred = 
							this.deviceService.getLatestGeozoneEventOccurred(geoZone.getGeozone_id());
					log.info("Latest Geozone Event Occurred => " + latestGeozoneEventOccurred);
					if (isValid && latestGeozoneEventOccurred == GEOZONE_ENTRY_ID) {
						log.info("Last geo zone event previous to this was geo zone entry");
						log.info("Create Geo zone exit event and send push notification");
						// the latest event that occurred for this geo zone ID
						// was Geozone Entry and we can proceed with creating
						// Geozone Exit event and triggering push notification
						boolean createEvent = this.deviceService.createDeviceEvent(deviceModel, devices.getDeviceId(),
								supportedEvents, uuid, geoZone1);
						int createdEventId = this.deviceService.getlatestDeviceEventId();
						String parentUnsubscribeEvents = this.deviceService
								.getParentUnsunsribeEvent(deviceModel.getEventid());
						log.info("parent can unsubscribe events ?: " + parentUnsubscribeEvents);

						FCMModel fcmModel = new FCMModel();
						String eventName = deviceService.getEventNamebyId(deviceModel.getEventid());
						log.info("eventName :" + eventName);
						fcmModel.setBody(eventName + " Occured at " + deviceModel.getEventoccureddate().toString());
						log.info(eventName + " Occured at " + deviceModel.getEventoccureddate().toString());
						fcmModel.setUuid(uuid);
						log.info("uuid" + uuid);

						log.info("Inside CreateEvent Boolean true");
						if (null != parentUnsubscribeEvents) {
							List<ValidParentUsersWithTokenTransform> parentUsers = userService
									.getParentUserTokenDetails(deviceModel.getEventid(), uuid, parentUnsubscribeEvents);
							log.info("Parent users size : " + parentUsers.size());

							if (parentUsers.size() == 0)
								log.info("NO Parent users subscribed for this event");

							for (ValidParentUsersWithTokenTransform item : parentUsers) {
								log.info("sending message to Parent User" + item.getUserId());

								try {
									int latestQueueId = this.deviceService.getlatestId();
									latestQueueId++;
									this.deviceService.addEntryToDeviceEventQueue(latestQueueId, item.getUserId(),
											createdEventId, devices.getDeviceId(), supportedEvents,
											item.getAndroidAppToken(), item.getiPhoneAppToken());
									int queueid = this.deviceService.getlatestId();
									String fcmTitle = eventName + ": " + item.getStudentName();
									fcmModel.setTitle(fcmTitle);
									fcmModel.setQueueid(queueid);
									fcmModel.setDeviceEventId(createdEventId);
									fcmModel.setStudentName(item.getStudentName());
									fcmModel.setEventId(deviceModel.getEventid());
									log.info("QueueID:" + queueid);
									log.info("UserID:" + item.getUserId());
									log.info("StudentName:" + item.getStudentName());
									log.info("Message Body:" + fcmModel.getBody());

									if (null != item.getAndroidAppToken() && !item.getAndroidAppToken().equals("")) {
										log.info("Sending notification to parent user on Android");
										log.info("PARENT_ANDROID_SERVER_KEY:" + PARENT_ANDROID_SERVER_KEY);
										log.info("App Token:" + item.getAndroidAppToken());
										FCMUtility.PushNotfication(PARENT_ANDROID_SERVER_KEY, item.getAndroidAppToken(),
												fcmModel);
									}
									if (null != item.getiPhoneAppToken() && !item.getiPhoneAppToken().equals("")) {
										log.info("Sending notification to parent user on IPHONE");
										log.info("PARENT_APPLE_SERVER_KEY:" + PARENT_APPLE_SERVER_KEY);
										log.info("App Token:" + item.getiPhoneAppToken());
										FCMUtility.PushNotfication(PARENT_APPLE_SERVER_KEY, item.getiPhoneAppToken(),
												fcmModel);
									}
									log.info("After sending notification to parent user");
								} catch (Exception e) {
									log.error("Error inside ValidParentUsersWithTokenTransform 3");
								}
							}
						}
					} else {
						// do nothing
						log.info("Last geo zone event previous to this was geo zone exit or no entry event at all");
						log.info("Skip Geo zone exit push notification and create event");
					}
				}
			}
		}
	}

	@RequestMapping(value = "/wearable/DeviceDataSync/{sessionId}/{uuid}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String DeviceDataSync(@RequestBody String inputJson, @PathVariable("uuid") String uuid,
			@PathVariable("sessionId") String sessionId) {
		log.info("Inside DeviceController.deviceDataSync()");
		String updateAvail = "";
		String responseJSON = null;
		String type = null;
		HashMap<Object, Object> hm = null;
		HashMap<Object, Object> firmwareUpdateMap = null;
		HashMap<Object, Object> deviceConfigMap = null;
		HashMap<Object, Object> vitalsMap = null;

		String rewardsJson = null;
		String firmwareUpdateJson = null;
		String deviceConfigJson = null;
		String vitalsJson = null;
		Date date = new Date();
		dbDateTime = dbDateTime.replace(":", "-");
		SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
		String datasyncFolderPath = this.configProperties.getProperty("datasync.upload.path") + "/"
				+ dateFormat.format(date);
		log.info("datasyncFolderPath" + "\t" + datasyncFolderPath);

		if ((sessionId.isEmpty() || sessionId.equals(null)) || (uuid.isEmpty() || uuid.equals(null))) {
			type = "device.DeviceDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_EMPTY_STATUS_MSG,
					Constant.WEARABLE_INPUT_EMPTY_STATUS_CODE, type);
			return responseJSON;
		}

		Devices devices = this.deviceService.checkDeviceIdExist(uuid);

		if (devices == null) {
			type = "device.DeviceDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_DEVICE_UUID_STATUS_MSG,
					Constant.WEARABLE_DEVICE_UUID_STATUS_CODE, type);
			return responseJSON;
		}

		boolean isValidJson = JSONUtility.isValidJson(inputJson);

		if (!isValidJson) {
			type = "device.DeviceDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_INVALID_STATUS_MSG,
					Constant.WEARABLE_INPUT_INVALID_STATUS_CODE, type);
			return responseJSON;
		}

		log.info("Is JSON Valid" + "\t" + isValidJson);
		Users user = new Users();
		user.setName("Wearable");
		user.setUsername("Wearable");
		user.setRoleType("Wearable");
		action = "Update";
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);

		String devicesbyUUIDAndSessionValidity = this.deviceService.findDevicesByUUIDAndCheckSessionValidity(uuid,
				sessionId);
		log.info("devicesbyUUIDAndSessionValidity" + "\t" + devicesbyUUIDAndSessionValidity);

		if (devicesbyUUIDAndSessionValidity.equals("success")) {
			boolean isDataSyncSuccess = true;
			log.info("inputJson" + "\n" + inputJson);
			if (null != FLUME_URL && !FLUME_URL.equals("")) {
				isDataSyncSuccess = JSONUtility.createJSONContentFile(inputJson, datasyncFolderPath, uuid, dbDateTime);
				log.info("Before Posting");
				log.info("FLUME_URL Found");
				RestTemplate restTemplate = new RestTemplate();

				List<HttpMessageConverter<?>> c = restTemplate.getMessageConverters();
				for (HttpMessageConverter<?> mc : c) {
					if (mc instanceof StringHttpMessageConverter) {
						StringHttpMessageConverter mcc = (StringHttpMessageConverter) mc;
						mcc.setWriteAcceptCharset(false);
					}
				}

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
				headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));

				URI uri = null;
				try {
					uri = new URI(FLUME_URL);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				HttpEntity<String> request = new HttpEntity<>(inputJson, headers);

				restTemplate.exchange(uri, HttpMethod.POST, request, Object.class);
				log.info("After Posting");
			}

			activityLogs.info(activityLog);
			if (isDataSyncSuccess) {
				log.info("Data Sync success");

				Integer config_id = this.deviceAnalyticsOutputService.getDeviceConfigurationId(uuid);
				log.debug("config_id is ::::: " + config_id);
				
				// Get wearable device config settings
				DeviceConfigurationsModel deviceConfigSettings = this.deviceAnalyticsOutputService
						.getWearableDeviceConfigSettings(config_id.intValue());
				if (null != deviceConfigSettings) {
					
					deviceConfigMap = new LinkedHashMap<>();
					if (deviceConfigSettings.getLow_battery_percent() != null
							&& deviceConfigSettings.getLow_battery_percent().intValue() > 0) {
						deviceConfigMap.put("low_battery_percent", deviceConfigSettings.getLow_battery_percent());
					}
					if (deviceConfigSettings.getGps_report_min() != null
							&& deviceConfigSettings.getGps_report_min().intValue() > 0) {
						deviceConfigMap.put("gps_report_min", deviceConfigSettings.getGps_report_min());
					}
					if (deviceConfigSettings.getDevice_self_test_hrs() != null
							&& deviceConfigSettings.getDevice_self_test_hrs() > 0) {
						deviceConfigMap.put("device_self_test_hrs",
								deviceConfigSettings.getDevice_self_test_hrs());
					}
					if (deviceConfigSettings.getWearable_sync_hrs() != null
							&& deviceConfigSettings.getWearable_sync_hrs() > 0) {
						deviceConfigMap.put("wearable_sync_hrs", deviceConfigSettings.getWearable_sync_hrs());
					}

					boolean isDeviceConfigMapEmtpy = deviceConfigMap.isEmpty();
					if (!isDeviceConfigMapEmtpy) {
						deviceConfigJson = JSONObject.toJSONString(deviceConfigMap);
						log.info("deviceConfigJson :::::::::::: " + deviceConfigJson);
					}
				} else {
					log.info("Wearable deviceConfigSettings is null");
				}
				
				// Get the PFI and vitals value from Cassandra DB
				// and construct the vitals JSON object
				try {
					int schoolId = this.deviceService.findSchoolIdByUUID(uuid);
					String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
					List<Row> rowsListForPFI = this.cassandraService.findPFIDataForDataSync(uuid,
							DateUtil.convertDateToUnixTime(DateUtil.getTodaysDate(), timeZone, dateTimezone),
							keyspace);
					List<Row> rowsListForHeartRate = this.cassandraService.findminMaxHeartRateForDataSync(uuid,
							DateUtil.convertDateToUnixTime(DateUtil.getTodaysDate(), timeZone, dateTimezone),
							keyspace);

					int pfi = 0;
					int situation = 0;
					int minStaticHeartRate = 0;
					int maxStaticHeartRate = 0;
					int minDynamicHeartRate = 0;
					int maxDynamicHeartRate = 0;

					log.debug("rowsListForPFI.size() :::::::::::::>   " + rowsListForPFI.size());
					log.debug("rowsListForHeartRate.size() :::::::::::::>   " + rowsListForHeartRate.size());

					vitalsMap = new LinkedHashMap<>();
					for (Row row : rowsListForPFI) {
						row.getInt(pfi);
						log.debug("pfi value >>>>>>>>>>>>>>>> entering for loop ::::::::::: ");
						log.debug("pfi value :::::::::::::::::::::> " + row.getInt("activeindex"));
						pfi = row.getInt("activeindex");
						log.info("pfi :::::::::::::::::" + pfi);
						vitalsMap.put("pfi", pfi);
					}

					for (Row row : rowsListForHeartRate) {
						log.debug("entering for loop heartrate ::::::::::: ");
						situation = row.getInt("situation");
						if (situation == 1) {
							minStaticHeartRate = row.getInt("min");
							maxStaticHeartRate = row.getInt("max");
							log.debug("minStaticHeartRate :::" + minStaticHeartRate);
							log.debug("maxStaticHeartRate :::" + maxStaticHeartRate);
							vitalsMap.put("static_heart_rate_min", minStaticHeartRate);
							vitalsMap.put("static_heart_rate_max", maxStaticHeartRate);

						} else if (situation == 2) {
							minDynamicHeartRate = row.getInt("min");
							maxDynamicHeartRate = row.getInt("max");
							log.debug("else if minStaticHeartRate :::" + minStaticHeartRate);
							log.debug("else if :::maxStaticHeartRate :::" + maxStaticHeartRate);
							vitalsMap.put("dynamic_heart_rate_min", minDynamicHeartRate);
							vitalsMap.put("dynamic_heart_rate_max", maxDynamicHeartRate);
						}
					}

					boolean isVitalsMapEmpty = vitalsMap.isEmpty();
					if (!isVitalsMapEmpty) {
						vitalsJson = JSONObject.toJSONString(vitalsMap);
						log.info("vitalsJson ::::::::: " + vitalsJson);
					}

				} catch (Exception e) {
					log.debug("Exception Generated ::::> ", e);
				}
				
				// Get the details of new FW update if available
				DeviceConfigurationsModel deviceConfigurationsModel = this.deviceAnalyticsOutputService
						.getDeviceConfigurations(config_id.intValue());

				if (deviceConfigurationsModel == null) {
					updateAvail = "no";
				} else if (deviceConfigurationsModel != null) {

					updateAvail = "yes";
					firmwareUpdateMap = new LinkedHashMap<>();
					firmwareUpdateMap.put("available", updateAvail);
					if (deviceConfigurationsModel.getName() != null && !deviceConfigurationsModel.getName().isEmpty()) {
						firmwareUpdateMap.put("name", deviceConfigurationsModel.getName());
					}
					if (deviceConfigurationsModel.getVersion() != null
							&& !deviceConfigurationsModel.getVersion().isEmpty()) {
						firmwareUpdateMap.put("version", deviceConfigurationsModel.getVersion());
					}
					if (deviceConfigurationsModel.getSize_mb() != null) {
						firmwareUpdateMap.put("size_mb", deviceConfigurationsModel.getSize_mb());
					}

					log.debug("file_url after concatenation ::::::::::: " + downloadsUrl + firmwareDownloadPath + "/"
							+ deviceConfigurationsModel.getFile_name());

					if (deviceConfigurationsModel.getFile_name() != null
							&& !deviceConfigurationsModel.getFile_name().isEmpty()
							&& ((deviceConfigurationsModel.getFile_name().contains(".zip"))
									|| (deviceConfigurationsModel.getFile_name().contains(".ZIP")))) {

						firmwareUpdateMap.put("file_url",
								downloadsUrl + firmwareDownloadPath + "/" + deviceConfigurationsModel.getFile_name());
					} else {
						firmwareUpdateMap.clear();
					}
					ObjectMapper firmwaremapperObj = new ObjectMapper();
					try {
						boolean isFirmwareUpdateMapEmpty = firmwareUpdateMap.isEmpty();
						if (!isFirmwareUpdateMapEmpty)
							firmwareUpdateJson = firmwaremapperObj.writeValueAsString(firmwareUpdateMap);
					} catch (JsonGenerationException e1) {
						log.debug("JsonGenerationException :::::> ", e1);
					} catch (JsonMappingException e1) {
						log.debug("JsonMappingException :::::> ", e1);
					} catch (IOException e1) {
						log.debug("JsonMappingException :::::> ", e1);
					}

					log.info("firmwareUpdateJson :::::::::::: " + firmwareUpdateJson);
				}

				// Get reward details based on the device UUID
				RewardsListTransform rtl = this.deviceService.findRewardsByDeviceUuid(uuid);

				if (null != rtl) {
					hm = new LinkedHashMap<>();
					if (rtl.getRewardCount() == null) {
						rewardsJson = null;
					} else {
						if (null != rtl.getRewardCount())
							hm.put("received_count", rtl.getRewardCount());
						if (null != rtl.getRank())
							hm.put("rank", rtl.getRank());

						ObjectMapper rewardsmapperObj = new ObjectMapper();
						try {
							rewardsJson = rewardsmapperObj.writeValueAsString(hm);
						} catch (JsonGenerationException e1) {
							log.debug("JsonGenerationException :::::> ", e1);
						} catch (JsonMappingException e1) {
							log.debug("JsonMappingException :::::> ", e1);
						} catch (IOException e1) {
							log.debug("JsonMappingException :::::> ", e1);
						}
					}
					log.info("rewardsJson" + "\t" + rewardsJson);
				}

				responseJSON = ErrorCodesUtil.displaySuccessJSONForDataSyncTest(Constant.SucessMsgActivity,
						Constant.WEARABLE_FOTAUpdateSuccess_Code, updateAvail, firmwareUpdateJson, deviceConfigJson,
						vitalsJson, rewardsJson);

				log.info("**responseJSON**" + "\t" + responseJSON);

				boolean validJson = JSONUtility.isValidJson(responseJSON);
				if (!validJson) {
					type = "device.DeviceDataSync";
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_INVALID_STATUS_MSG,
							Constant.WEARABLE_INPUT_INVALID_STATUS_CODE, type);
				}
			} else {
				activityLogs.error(activityLog);
				type = "device.DeviceDataSync";
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_FOLDER_CREATE_STATUS_MSG,
						Constant.WEARABLE_FOLDER_CREATE_STATUS_CODE, type);
			}

		} else if (devicesbyUUIDAndSessionValidity.equals(tokenUnavailable)) {
			type = "device.DeviceDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INVALID_USER_STATUS_MSG,
					Constant.WEARABLE_INVALID_USER_STATUS_CODE, type);
		} else if (devicesbyUUIDAndSessionValidity.equals(uuidTokenMapping)) {
			type = "device.DeviceDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_UNAUTHORIZED_LOGIN_STATUS_MSG,
					Constant.WEARABLE_UNAUTHORIZED_LOGIN_STATUS_CODE, type);
		} else if (devicesbyUUIDAndSessionValidity.equals("NOTVALID")) {
			type = "device.DeviceDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
					Constant.WEARABLE_SESSION_EXPIRED_STATUS_CODE, type);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/DeviceEventReport/{sessionID}/{studentId}/{eventId}/{startDate}/{endDate}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String DeviceEventReport(@PathVariable String sessionID, @PathVariable int studentId,
			@PathVariable int eventId, @PathVariable String startDate, @PathVariable String endDate)
			throws JSONException {

		String type = "device.DeviceEventReport";
		Map<String, List<DeviceEventsTransform>> deviceMap = new HashMap<String, List<DeviceEventsTransform>>();

		String respondJson = null;
		List<LinkedHashMap<String, Object>> eventsList1 = new ArrayList<>();

		Users user = userService.validateUserBySession(sessionID);
		if (user != null) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);
			if (sessionValidityResult.equals("NOTVALID")) {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
						Constant.StatusCodeValidity, Constant.DeviceEventReportType);
				return respondJson;
			}
			String roleType = user.getRoleType();
			if (roleType.equals(Constant.ParentAdmin)) {
				List<String> uuidList = this.deviceService.findDevicesByStudentId(studentId);
				SupportedEvents suppportedEvents = this.eventsService.getSupportedEventsByEventId(eventId);
				if (String.valueOf(studentId) == null && suppportedEvents == null && uuidList == null) {
					respondJson = ErrorCodesUtil.displayErrorJSON(Constant.ErrorDataMessage, Constant.StatusCode4,
							Constant.DeviceEventReportType);
					// JSONUtility.respondAsJSON(response, respondJson);
					return respondJson;
				}

				/*
				 * Commented below as we don't need to check if device exists
				 * for the requested event boolean deviceExistForEvent = false;
				 * 
				 * for (String uuid : uuidList) { deviceExistForEvent =
				 * this.eventsService.deviceExistForEvent(uuid, eventId);
				 * log.info("deviceExistForEvent" + "\t" + deviceExistForEvent);
				 * if (!deviceExistForEvent) { respondJson =
				 * ErrorCodesUtil.displayErrorJSON(
				 * Constant.StatusMessageForDeviceDoesNotExistForAGivenEvent,
				 * Constant.StatusCodeForDeviceDoesNotExistForAGivenEvent,
				 * Constant.DeviceEventReportType); //
				 * JSONUtility.respondAsJSON(response, respondJson); return
				 * respondJson; } }
				 */

				Map<String, Object> jsonMap = new LinkedHashMap<>();
				List<DeviceEventsTransform> detList = null;
				String jsonFinal = null;

				switch (eventId) {
				case Constant.ENTRY_ALERT_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());

					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***sosAlertJson***" + "\t" + respondJson);
					break;
				case Constant.EXIT_ALERT_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());

					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***sosAlertJson***" + "\t" + respondJson);
					break;
				case Constant.GEO_ENTRY_ALERT_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());

					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***sosAlertJson***" + "\t" + respondJson);
					break;
				case Constant.GEO_EXIT_ALERT_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());

					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***sosAlertJson***" + "\t" + respondJson);
					break;
				case Constant.SOS_ALERT_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());

					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}
					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***sosAlertJson***" + "\t" + respondJson);
					break;
				case Constant.SOS_REMOVE_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findBandBackAlertAndSOSRemovingData(studentId, eventId, startDate,
							endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***bandBackAlertJson***" + "\t" + respondJson);
					break;
				case Constant.FALL_DETECTION_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***fallAlertJson***" + "\t" + respondJson);
					break;
				case Constant.ABNORMAL_VITAL_SIGN_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findAbnormalVitalSign(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}
					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getVital_sign_type() != null)
								map2.put("vital_sign_type", det.getVital_sign_type());
							if (det.getVital_sign_value() != null)
								map2.put("vital_sign_value", det.getVital_sign_value());
							if (det.getAbnormal_code() != null)
								map2.put("abnormal_code", det.getAbnormal_code());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***abnormalVitalSignJson***" + "\t" + respondJson);
					break;
				case Constant.SENSOR_MALFUNCTION_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findSensorMalfunction(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getSensor_type_code() != null)
								map2.put("sensor_type_code", det.getSensor_type_code());
							if (det.getSensor_error_code() != null)
								map2.put("sensor_error_code", det.getSensor_error_code());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***sensorMalfunctionAlertJson***" + "\t" + respondJson);
					break;
				case Constant.LOW_BATTERY_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findBatteryLevel(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getBattery_level_value() != null)
								map2.put("battery_level_value", det.getBattery_level_value());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***sensorMalfunctionAlertJson***" + "\t" + respondJson);
					break;
				case Constant.STUDENT_LOCATION_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}
					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***studentLocationJson***" + "\t" + respondJson);
					break;
				case Constant.BAND_REMOVAL_ALERT_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findGPSData(studentId, eventId, startDate, endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getGps_data_code() != null)
								map2.put("gps_data_code", det.getGps_data_code());
							if (det.getGps_location_data() != null)
								map2.put("gps_location_data", det.getGps_location_data());
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***bandRemovalAlertJson***" + "\t" + respondJson);
					break;
				case Constant.BAND_BACK_ALERT_ID:
					if (String.valueOf(eventId) != null)
						jsonMap.put("event_id", eventId);
					if (suppportedEvents.getEventName() != null)
						jsonMap.put("event_name", suppportedEvents.getEventName());
					detList = this.eventsService.findBandBackAlertAndSOSRemovingData(studentId, eventId, startDate,
							endDate);

					for (DeviceEventsTransform eventsTransform : detList) {
						if (!deviceMap.containsKey(eventsTransform.getUuid())) {
							List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
							list.add(eventsTransform);
							deviceMap.put(eventsTransform.getUuid(), list);
						} else {
							deviceMap.get(eventsTransform.getUuid()).add(eventsTransform);
						}

					}

					log.debug("****deviceMap*****" + "\n" + deviceMap);
					log.info("deviceMap.size() ::" + deviceMap.size());

					deviceMap.forEach((k, v) -> {
						List<Object> eventsList = new ArrayList<>();
						LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
						String status = null;
						for (DeviceEventsTransform det : v) {
							LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();
							if (det.getEvent_occured_date() != null)
								map2.put("event_occured_date", det.getEvent_occured_date().toString());
							status = det.getStatus();
							eventsList.add(map2);
						}
						map1.put("uuid", k);
						if (status != null)
							map1.put("device_status", status);
						if (eventsList != null)
							map1.put("device_events", eventsList);
						eventsList1.add(map1);
					});
					jsonMap.put("devices", eventsList1);
					jsonFinal = JSONObject.toJSONString(jsonMap);
					respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, jsonFinal, type);
					log.info("***bandBackAlertJson***" + "\t" + respondJson);
					break;
				default:
					respondJson = ErrorCodesUtil.displayErrorJSON(
							Constant.StatusMessageForDeviceDoesNotExistForAGivenEvent,
							Constant.StatusCodeForDeviceDoesNotExistForAGivenEvent, Constant.DeviceEventReportType);
					break;
				}
			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsMessage,
						Constant.SubscriptionsStatusCode, Constant.DeviceEventReportType);
			}
		} else {
			respondJson = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.DeviceEventReportType);
		}
		return respondJson;
	}

	@RequestMapping(value = "/web/StudentActivity/{token}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String StudentActivity(@PathVariable("token") String token,
			@RequestBody ParentActivityForDashboardModel pADashboardModel) throws ParseException {
		log.info("Into StudentActivity() {");
		String statusMsg;
		String type = "device.StudentActivity";
		String responseJSON = "";
		List<Object> pfiList, stepsList, activityList, hrList, calList, sleepList, stressList, emotionList = null;
		boolean startEndDatesAreEqual = false;

		String json = JSONUtility.convertObjectToJson(pADashboardModel);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_INVALID_STATUS_MSG,
					Constant.WEARABLE_INPUT_INVALID_STATUS_CODE, type);
			return responseJSON;
		}

		if (pADashboardModel.getMeasure_type() == null || pADashboardModel.getMeasure_type().trim().length() == 0) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_EMPTY_STATUS_MSG,
					Constant.WEARABLE_INPUT_EMPTY_STATUS_CODE, type);
			return responseJSON;
		}

		String measureType = pADashboardModel.getMeasure_type();
		String splitBy = ",";
		String[] measureTypeList = measureType.split(splitBy);
		ArrayList<String> mTList = null;

		List<String> measureArrayToList = Arrays.asList(measureTypeList);
		log.info("measureArrayToList.size()" + "\t" + measureArrayToList.size());
		for (int i = 0; i < measureTypeList.length; i++) {
			if (i == 0) {
				mTList = new ArrayList<>();
			}
			if (measureTypeList[i].trim().equals("fitness")) {
				mTList.add("fitness");
			} else if (measureTypeList[i].trim().equals("steps")) {
				mTList.add("steps");
			} else if (measureTypeList[i].trim().equals("calories")) {
				mTList.add("calories");
			} else if (measureTypeList[i].trim().equals("activity")) {
				mTList.add("activity");
			} else if (measureTypeList[i].trim().equals("heartrate")) {
				mTList.add("heartrate");
			} else if (measureTypeList[i].trim().equals("sleep")) {
				mTList.add("sleep");
			} else if (measureTypeList[i].trim().equals("stress")) {
				mTList.add("stress");
			} else if (measureTypeList[i].trim().equals("emotion")) {
				mTList.add("emotion");
			}
		}

		Users user = userService.validateUserBySession(token);

		if (user != null) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);
			if (sessionValidityResult.equals("NOTVALID")) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
						Constant.StatusCodeValidity, type);
				return responseJSON;
			}
			if (user.getRoleType().equals(Constant.ParentAdmin)) {
				String startDt = null;
				String endDt = null;

				if ((pADashboardModel.getStart_date() == null || pADashboardModel.getStart_date().trim().length() == 0)
						&& (pADashboardModel.getEnd_date() == null
								|| pADashboardModel.getEnd_date().trim().length() == 0)) {
					startDt = DateUtil.getYesterdaysDate(sourceDisplayDateFormat) + " " + startDateFormat;
					endDt = DateUtil.getYesterdaysDate(sourceDisplayDateFormat) + " " + endDateFormat;
				} else if (pADashboardModel.getStart_date() != null && (pADashboardModel.getEnd_date() == null
						|| pADashboardModel.getEnd_date().trim().length() == 0)) {
					startDt = pADashboardModel.getStart_date() + " " + startDateFormat;
					endDt = pADashboardModel.getStart_date() + " " + endDateFormat;
				} else if ((pADashboardModel.getStart_date() == null
						|| pADashboardModel.getStart_date().trim().length() == 0)
						&& (pADashboardModel.getEnd_date() != null)) {
					startDt = pADashboardModel.getEnd_date() + " " + startDateFormat;
					endDt = pADashboardModel.getEnd_date() + " " + endDateFormat;
				} else if (DateUtil.isStartDateAfterEndDate(pADashboardModel.getStart_date(),
						pADashboardModel.getEnd_date(), sourceDisplayDateFormat)) {
					startDt = pADashboardModel.getEnd_date() + " " + startDateFormat;
					endDt = pADashboardModel.getStart_date() + " " + endDateFormat;
				} else {
					if (pADashboardModel.getStart_date().equals(pADashboardModel.getEnd_date())) {
						startEndDatesAreEqual = true;
					}
					startDt = pADashboardModel.getStart_date() + " " + startDateFormat;
					endDt = pADashboardModel.getEnd_date() + " " + endDateFormat;
				}

				log.debug("endDateFormat >>>>>>>>>>>>>>>>>>>>>>>>>>>" + endDateFormat);
				log.debug("startDt :::::::::::::::::::::::::::::::::  " + startDt);
				log.debug("endDt :::::::::::::::::::::::::::::::::  " + endDt);

				List<DeviceStudentsTransform> deviceStudentsTransformList = this.deviceService
						.findDeviceStudentsForStudentActivity(pADashboardModel.getStudent_id(), startDt, endDt);
				log.info("deviceStudentsTransformList.size()" + "\t" + deviceStudentsTransformList.size());

				String json1 = "";
				StringBuilder sb = null;

				if (null != mTList) {
					for (int i = 0; i < mTList.size(); i++) {
						if (mTList.get(i).trim().equals("fitness")) {
							pfiList = new ArrayList<>();
							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								log.debug("fitness start_date :::::::::: " + dts.getNew_start_date());
								log.debug("fitness end_date :::::::::: " + dts.getNew_end_date());
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								List<Row> rowsList = this.cassandraService.findPhyscialFitnessIndex(dts.getUuid(),
										DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
										DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone),
										keyspace);
								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									pfiList.add(jsonString);
							}

							json1 = StringUtility.getActivityOutput("fitness", pfiList);
							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}

						} else if (mTList.get(i).trim().equals("steps")) {
							stepsList = new ArrayList<>();
							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								List<Row> rowsList = this.cassandraService.findStepsCount(dts.getUuid(),
										DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
										DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone),
										keyspace);
								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									stepsList.add(jsonString);
							}
							json1 = StringUtility.getActivityOutput(mTList.get(i).trim(), stepsList);

							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}
						} else if (mTList.get(i).trim().equals("activity")) {
							activityList = new ArrayList<>();
							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								List<Row> rowsList = this.cassandraService.findActivity(dts.getUuid(),
										DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
										DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone),
										keyspace);
								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									activityList.add(jsonString);
							}

							json1 = StringUtility.getActivityOutput(mTList.get(i).trim(), activityList);

							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}

						} else if (mTList.get(i).trim().equals("heartrate")) {
							hrList = new ArrayList<>();
							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								log.debug("start_date :::::::::: " + dts.getNew_start_date());
								log.debug("start_date :::::::::: " + dts.getNew_end_date());
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								List<Row> rowsList = this.cassandraService.findHeartRate(dts.getUuid(),
										DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
										DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone),
										keyspace);

								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									hrList.add(jsonString);
							}

							json1 = StringUtility.getActivityOutput(mTList.get(i).trim(), hrList);

							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}
						} else if (mTList.get(i).trim().equals("calories")) {
							calList = new ArrayList<>();

							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								List<Row> rowsList = this.cassandraService.findCaloriesBurnt(dts.getUuid(),
										DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
										DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone),
										keyspace);
								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									calList.add(jsonString);
							}
							json1 = StringUtility.getActivityOutput(mTList.get(i).trim(), calList);

							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}
						} else if (mTList.get(i).trim().equals("sleep")) {
							sleepList = new ArrayList<>();
							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								List<Row> rowsList = this.cassandraService.findSleepRate(dts.getUuid(),
										DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
										DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone),
										keyspace);
								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									sleepList.add(jsonString);
							}
							json1 = StringUtility.getActivityOutput(mTList.get(i).trim(), sleepList);
							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}
						} else if (mTList.get(i).trim().equals("stress")) {
							stressList = new ArrayList<>();
							List<Row> rowsList = null;
							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								if (startEndDatesAreEqual && pADashboardModel.getSource() != null) {
									rowsList = this.cassandraService.findStressLevels(dts.getUuid(),
											DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone,
													dateTimezone),
											DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone,
													dateTimezone),
											keyspace);
								} else {
									rowsList = this.cassandraService.findStressRangeLevels(dts.getUuid(),
											DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone,
													dateTimezone),
											DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone,
													dateTimezone),
											keyspace);
								}
								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									stressList.add(jsonString);
							}
							json1 = StringUtility.getActivityOutput(mTList.get(i).trim(), stressList);
							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}
						} else if (mTList.get(i).trim().equals("emotion")) {
							emotionList = new ArrayList<>();
							List<Row> rowsList = null;
							for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
								log.debug("dts.getStart_date() ::::::: " + dts.getNew_start_date());
								log.debug("dts.getEnd_date() ::::::: " + dts.getNew_end_date());
								int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
								String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
								if (startEndDatesAreEqual && pADashboardModel.getSource() != null) {
									rowsList = this.cassandraService.findEmotionLevels(dts.getUuid(),
											DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone,
													dateTimezone),
											DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone,
													dateTimezone),
											keyspace);
								} else {
									rowsList = this.cassandraService.findEmotionRangeLevels(dts.getUuid(),
											DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone,
													dateTimezone),
											DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone,
													dateTimezone),
											keyspace);

								}
								String jsonString = StringUtility.convertRowListToString(rowsList);
								if (!jsonString.isEmpty())
									emotionList.add(jsonString);
							}

							json1 = StringUtility.getActivityOutput(mTList.get(i).trim(), emotionList);

							if (sb != null)
								sb.append("," + json1);
							else {
								sb = new StringBuilder();
								sb.append(json1);
							}
						}
					}
					responseJSON = ErrorCodesUtil.displayParentActivitiyForDashboardJSON(Constant.StatusCodeActivity,
							Constant.SucessMsgActivity, type, sb.toString());
				} else {
					responseJSON = ErrorCodesUtil.displayParentActivitiyForDashboardJSON(Constant.StatusCodeActivity,
							Constant.SucessMsgActivity, type, "");
				}
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsMessage,
						Constant.SubscriptionsStatusCode, type);
				return responseJSON;
			}
		} else {
			statusMsg = "Invalid User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, Constant.DisplayStatusCode, type);
			return responseJSON;
		}
		log.info("Exiting StudentActivity() }");
		return responseJSON;
	}

	@RequestMapping(value = "/WearableJsonData", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String WearableJsonData() {
		String jsonData = StringUtility.dummyWearableJsonData();
		return jsonData;
	}

	@RequestMapping(value = "/wearable/PetDataSync/{sessionId}/{uuid}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String PetDataSync(@PathVariable("uuid") String uuid, @PathVariable("sessionId") String sessionId,
			@RequestBody PetDetails petDetails) throws ParseException {

		log.info("Inside DeviceController.PetDataSync()");
		String responseJSON = null;
		String type = null;

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(sourceDisplayDateFormat);
		log.info("current date" + "\t" + dateFormat.format(date));

		if ((sessionId.isEmpty() || sessionId.equals(null)) || (uuid.isEmpty() || uuid.equals(null))) {
			type = "device.PetDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_EMPTY_STATUS_MSG,
					Constant.WEARABLE_INPUT_EMPTY_STATUS_CODE, type);
			return responseJSON;
		}

		Devices devices = this.deviceService.checkDeviceIdExist(uuid);

		if (devices == null) {
			type = "device.PetDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_DEVICE_UUID_STATUS_MSG,
					Constant.WEARABLE_DEVICE_UUID_STATUS_CODE, type);
			return responseJSON;
		}

		String inputJson = JSONUtility.convertObjectToJson(petDetails);
		log.info("Device Model Json" + "\n" + inputJson);
		boolean isValidJson = JSONUtility.isValidJson(inputJson);

		if (!isValidJson) {
			type = "device.PetDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_INVALID_STATUS_MSG,
					Constant.WEARABLE_INPUT_INVALID_STATUS_CODE, type);
			return responseJSON;
		}

		log.info("Is JSON Valid" + "\t" + isValidJson);
		Users user = new Users();
		user.setName("Wearable");
		user.setUsername("Wearable");
		user.setRoleType("Wearable");
		action = "Update";
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		String devicesbyUUIDAndSessionValidity = this.deviceService.findDevicesByUUIDAndCheckSessionValidity(uuid,
				sessionId);
		log.info("devicesbyUUIDAndSessionValidity" + "\t" + devicesbyUUIDAndSessionValidity);

		if (devicesbyUUIDAndSessionValidity.equals("success")) {
			log.info("Before Posting");
			boolean dataError = false;

			if (!(petDetails.getPettype().equals("dog"))) {
				dataError = true;
			}
			if (!(petDetails.getSizelevel().equals("sizelevel_1") || petDetails.getSizelevel().equals("sizelevel_2")
					|| petDetails.getSizelevel().equals("sizelevel_3")
					|| petDetails.getSizelevel().equals("sizelevel_4"))) {
				dataError = true;
			}
			if (!(petDetails.getShapelevel().equals("small") || petDetails.getShapelevel().equals("medium")
					|| petDetails.getShapelevel().equals("large"))) {
				dataError = true;
			}
			if (!(petDetails.getOrnamentlevel().equals("ornamentlevel_1")
					|| petDetails.getOrnamentlevel().equals("ornamentlevel_2")
					|| petDetails.getOrnamentlevel().equals("ornamentlevel_3")
					|| petDetails.getOrnamentlevel().equals("ornamentlevel_4"))) {
				dataError = true;
			}
			if (!(petDetails.getGladnesslevel().equals("veryhappy") || petDetails.getGladnesslevel().equals("happy")
					|| petDetails.getGladnesslevel().equals("unhappy"))) {
				dataError = true;
			}
			if (!(petDetails.getVigorlevel().equals("morevigorous") || petDetails.getVigorlevel().equals("vigorous")
					|| petDetails.getVigorlevel().equals("listless"))) {
				dataError = true;
			}
			if (dataError) {
				type = "device.PetDataSync";
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_INVALID_STATUS_MSG,
						Constant.WEARABLE_INPUT_INVALID_STATUS_CODE, type);
			} else {
				petDetails.setUuid(uuid);
				petDetails.setCreated_date(date);
				log.info("data sent for saving " + petDetails);
				this.deviceService.addPetData(petDetails);
				type = "device.PetDataSync";
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.SucessMsgActivity,
						Constant.WEARABLE_FOTAUpdateSuccess_Code, type);
			}

		} else if (devicesbyUUIDAndSessionValidity.equals(tokenUnavailable)) {
			type = "device.PetDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INVALID_USER_STATUS_MSG,
					Constant.WEARABLE_INVALID_USER_STATUS_CODE, type);
		} else if (devicesbyUUIDAndSessionValidity.equals(uuidTokenMapping)) {
			type = "device.PetDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_UNAUTHORIZED_LOGIN_STATUS_MSG,
					Constant.SubscriptionsStatusCode, type);
		} else if (devicesbyUUIDAndSessionValidity.equals("NOTVALID")) {
			type = "device.PetDataSync";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
					Constant.WEARABLE_SESSION_EXPIRED_STATUS_CODE, type);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/PetDetails/{sessionId}/{uuid}/{Date}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String PetDetails(@PathVariable("uuid") String uuid, @PathVariable("sessionId") String sessionId,
			@PathVariable String Date) {

		log.info("Inside DeviceController.PetDetails() " + "uuid " + uuid + "sessionId " + sessionId + "Date " + Date);
		String responseJSON = null;
		String statusCode = null;
		String errorMessage = null;
		String type = null;
		String jsonString = null;
		Map<Object, Object> map = null;

		if ((sessionId.isEmpty() || sessionId.equals(null)) || (uuid.isEmpty() || uuid.equals(null))) {
			errorMessage = "Input is Empty";
			statusCode = "ERR04";
			type = "device.PetDetails";
			responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
			return responseJSON;
		}

		Devices devices = this.deviceService.checkDeviceIdExist(uuid);

		if (devices == null) {
			errorMessage = "Invalid Device UUID";
			statusCode = "ERR06";
			type = "device.PetDetails";
			responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
			return responseJSON;
		}

		Users user = userService.getUserByMobileSessionId(sessionId);

		if (null == user) {
			statusCode = "ERR02";
			type = "device.PetDetails";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity, statusCode, type);
			return responseJSON;
		}
		if (!user.getRoleType().equals("parent_admin")) {
			type = "device.PetDetails";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_UNAUTHORIZED_LOGIN_STATUS_MSG,
					Constant.SubscriptionsStatusCode, type);
			return responseJSON;
		}
		List<Integer> userIdList = deviceService.getUseridListbyuuid(uuid);

		if (userIdList.size() > 0 && userIdList.contains(user.getId())) {
			try {
				log.info("Read Input Date" + "\t" + new SimpleDateFormat(sourceDisplayDateFormat).parse(Date));
				PetDetails tst = this.deviceService.getPetDetails(uuid,
						new SimpleDateFormat(sourceDisplayDateFormat).parse(Date));

				map = new LinkedHashMap<>();
				if (tst != null) {
					log.info("Pet Data " + tst.toString());
					if (tst.getPettype() != null)
						map.put("pettype", tst.getPettype().toString());
					if (tst.getPetname() != null)
						map.put("petname", tst.getPetname().toString());
					if (tst.getSizelevel() != null)
						map.put("sizelevel", tst.getSizelevel().toString());
					if (tst.getShapelevel() != null)
						map.put("shapelevel", tst.getShapelevel().toString());
					if (tst.getOrnamentlevel() != null)
						map.put("ornamentlevel", tst.getOrnamentlevel().toString());
					if (tst.getGladnesslevel() != null)
						map.put("gladnesslevel", tst.getGladnesslevel().toString());
					if (tst.getVigorlevel() != null)
						map.put("vigorlevel", tst.getVigorlevel().toString());
					jsonString = JSONObject.toJSONString(map);
				} else {
					jsonString = "";
				}

				responseJSON = ErrorCodesUtil.displayJSONForPetDetails("", "", jsonString);
				log.info("***petDetailsRespJson***" + "\t" + responseJSON);
			} catch (Exception e) {
				statusCode = "ERR05";
				errorMessage = "Input is Invaid";
				type = "device.PetDetails";
				responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
			}
		} else {
			type = "device.PetDetails";
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INVALID_USER_STATUS_MSG,
					Constant.DisplayStatusCode, type);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/wearable/FOTAUpdateSuccess/{sessionId}", method = RequestMethod.PUT, produces = {
			"application/json" })
	public String FOTAUpdateSuccess(@PathVariable("sessionId") String sessionId,
			@RequestBody DeviceConfigModel deviceConfigModel) throws ParseException {

		log.info("Inside DeviceController.POTAUpdateSuccess()");
		String responseJSON = null;

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(sourceDisplayDateFormat);
		log.info("current date" + "\t" + dateFormat.format(date));

		if ((sessionId.isEmpty() || sessionId.equals(null))
				|| (deviceConfigModel.getUuid().isEmpty() || deviceConfigModel.getUuid().equals(null))
				|| (deviceConfigModel.getFirmware_file().isEmpty()
						|| deviceConfigModel.getFirmware_file().equals(null))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_EMPTY_STATUS_MSG,
					Constant.WEARABLE_INPUT_EMPTY_STATUS_CODE, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
			return responseJSON;
		}

		Devices devices = this.deviceService.checkDeviceIdExist(deviceConfigModel.getUuid());

		if (devices == null) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_DEVICE_UUID_STATUS_MSG,
					Constant.WEARABLE_DEVICE_UUID_STATUS_CODE, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
			return responseJSON;
		}

		DeviceConfigurations deviceConfigurationsForFile = this.deviceService
				.isFirmwareFileExists(deviceConfigModel.getFirmware_file());

		if (deviceConfigurationsForFile == null) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INVALID_FIRMWARE_FILE_MSG,
					Constant.WEARABLE_INVALID_FIRMWARE_FILE_STATUS_CODE, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
			return responseJSON;
		}

		String inputJson = JSONUtility.convertObjectToJson(deviceConfigModel);
		log.info("Device Config Model Json" + "\n" + inputJson);
		boolean isValidJson = JSONUtility.isValidJson(inputJson);

		if (!isValidJson) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_INVALID_STATUS_MSG,
					Constant.WEARABLE_INPUT_INVALID_STATUS_CODE, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
			return responseJSON;
		}

		log.info("Is JSON Valid" + "\t" + isValidJson);
		Users user = new Users();
		user.setName("Wearable");
		user.setUsername("Wearable");
		user.setRoleType("Wearable");
		action = "Update";
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String devicesbyUUIDAndSessionValidity = this.deviceService
				.findDevicesByUUIDAndCheckSessionValidity(deviceConfigModel.getUuid(), sessionId);
		log.info("devicesbyUUIDAndSessionValidity" + "\t" + devicesbyUUIDAndSessionValidity);

		if (devicesbyUUIDAndSessionValidity.equals("success")) {

			DeviceConfigurations deviceConfig = this.deviceService
					.findDeviceConfigByFirmwareFile(deviceConfigModel.getFirmware_file());
			devices.setDeviceConfigurations(deviceConfig);
			this.deviceService.updateDevice(devices);

			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.SucessMsgActivity,
					Constant.WEARABLE_FOTAUpdateSuccess_Code, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
		} else if (devicesbyUUIDAndSessionValidity.equals(tokenUnavailable)) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INVALID_USER_STATUS_MSG,
					Constant.WEARABLE_INVALID_USER_STATUS_CODE, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
		} else if (devicesbyUUIDAndSessionValidity.equals(uuidTokenMapping)) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_UNAUTHORIZED_LOGIN_STATUS_MSG,
					Constant.WEARABLE_UNAUTHORIZED_LOGIN_STATUS_CODE, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
		} else if (devicesbyUUIDAndSessionValidity.equals("NOTVALID")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_SESSION_EXPIRED_STATUS_MSG,
					Constant.WEARABLE_SESSION_EXPIRED_STATUS_CODE, Constant.WEARABLE_FOTAUpdateSuccess_TYPE);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/IPS/BeaconDevicesEventReport/{sessionId}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String BeaconDevicesEventReport(@PathVariable("sessionId") String sessionId,
			@RequestBody BeaconDeviceEventsModel beaconDeviceEventsModel) throws ParseException {
		int ENTRY_ALERT_ID = Integer.valueOf(SCHOOL_ENTRY_ID).intValue();
		int EXIT_ALERT_ID = Integer.valueOf(SCHOOL_EXIT_ID).intValue();
		int GEO_ENTRY_ALERT_ID = Integer.valueOf(GEOFENCE_ENTRY_ID).intValue();
		int GEO_EXIT_ALERT_ID = Integer.valueOf(GEOFENCE_EXIT_ID).intValue();
		int SOS_ALERT_ID = Integer.valueOf(SOS_ID).intValue();
		IPSReceiver ipsReceiver = null;
		int eventId = beaconDeviceEventsModel.getEvent_id().intValue();
		String startDt = beaconDeviceEventsModel.getStartDate();
		String endDt = beaconDeviceEventsModel.getEndDate();
		String type = "device.BeaconDevicesEventReport";
		int zoneId = beaconDeviceEventsModel.getZone_id().intValue();

		Map<Integer, List<DeviceEventsTransform>> deviceMap = null;

		String respondJson = null;

		if ((sessionId.isEmpty() || sessionId.equals(null))
				|| (beaconDeviceEventsModel.getIps_receiver_mac().isEmpty()
						|| beaconDeviceEventsModel.getIps_receiver_mac().equals(null))
				|| (beaconDeviceEventsModel.getAccesskey().isEmpty()
						|| beaconDeviceEventsModel.getAccesskey().equals(null))
				|| (beaconDeviceEventsModel.getEvent_id().intValue() == 0
						|| beaconDeviceEventsModel.getEvent_id().equals(null))
				|| (beaconDeviceEventsModel.getStartDate().isEmpty()
						|| beaconDeviceEventsModel.getStartDate().equals(null))
				|| (beaconDeviceEventsModel.getEndDate().isEmpty()
						|| beaconDeviceEventsModel.getEndDate().equals(null))) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return respondJson;
		}

		ipsReceiver = this.iPSReceiverService.getIPSReceiverEntryForMac(beaconDeviceEventsModel.getIps_receiver_mac());

		if (ipsReceiver == null) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR08_MSG, IPSConstants.STATUSCODE_08, type);
			return respondJson;
		}

		String mobileNo = this.iPSReceiverService.getMobileNumberBySchoolId(ipsReceiver.getSchoolId());

		String iPSReceiverByMacAndSessionIdStatus = this.iPSReceiverService.findIPSReceiverByMacAndSessionId(sessionId,
				beaconDeviceEventsModel.getIps_receiver_mac());
		boolean accessKeyValidation = false;
		try {
			accessKeyValidation = AESEncryption.validatePassword(mobileNo, beaconDeviceEventsModel.getAccesskey());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.info("Exception Occured in BeaconDevicesEventReport()" + "\t" + e);
		}

		if (!accessKeyValidation) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
			return respondJson;
		}

		SupportedEvents suppportedEvents = this.eventsService
				.getSupportedEventsByEventId(beaconDeviceEventsModel.getEvent_id());
		if (iPSReceiverByMacAndSessionIdStatus.equals("Success")) {
			if (eventId == ENTRY_ALERT_ID || eventId == EXIT_ALERT_ID || eventId == GEO_ENTRY_ALERT_ID
					|| eventId == GEO_EXIT_ALERT_ID || eventId == SOS_ALERT_ID) {
				Map<String, Object> jsonMap = new LinkedHashMap<>();
				if (String.valueOf(ipsReceiver.getSchoolId()) != null)
					jsonMap.put("school_id", ipsReceiver.getSchoolId());
				if (String.valueOf(eventId) != null)
					jsonMap.put("event_id", eventId);
				if (suppportedEvents.getEventName() != null)
					jsonMap.put("event_name", suppportedEvents.getEventName());

				// Loop Start
				List<Integer> zoneIdsList = new ArrayList<Integer>();
				int ips_receiver_id = ipsReceiver.getReceiverId();
				if (eventId == ENTRY_ALERT_ID || eventId == EXIT_ALERT_ID || eventId == SOS_ALERT_ID) {
					zoneIdsList.add(0);
				}else{
					if (zoneId == 0) {
						zoneIdsList = this.eventsService.getZoneIdListForReceiver(ips_receiver_id);
					} else {
						zoneIdsList.add(zoneId);
					}
				}

				if (null != zoneIdsList) {
					int eventCount = 0;
					List<LinkedHashMap<String, Object>> eventsList1 = new ArrayList<>();

					for (int zone : zoneIdsList) {
						deviceMap = new HashMap<>();
						List<DeviceEventsTransform> detList = this.eventsService.findBeaconEventsData(sessionId,
								eventId, startDt, endDt, zone, ips_receiver_id);

						for (DeviceEventsTransform eventsTransform : detList) {
							if (!deviceMap.containsKey(eventsTransform.getZone_id())) {
								List<DeviceEventsTransform> list = new ArrayList<DeviceEventsTransform>();
								list.add(eventsTransform);
								deviceMap.put(eventsTransform.getZone_id(), list);
							} else {
								deviceMap.get(eventsTransform.getZone_id()).add(eventsTransform);
							}
						}
						log.debug("****For zone*****" + "\n" + zone);
						log.debug("****deviceMap*****" + "\n" + deviceMap);
						log.info("deviceMap.size() ::" + deviceMap.size());
						LinkedHashMap<String, Object> map1 = null;
						LinkedHashMap<String, Object> map2 = null;
						LinkedHashMap<String, List<String>> uuidDatemap = new LinkedHashMap<>();
						if (deviceMap.size() > 0) {
							Iterator<Map.Entry<Integer, List<DeviceEventsTransform>>> mapIterator = deviceMap.entrySet()
									.iterator();
							while (mapIterator.hasNext()) {
								log.debug("entered into while loop");

								Map.Entry<Integer, List<DeviceEventsTransform>> pair = mapIterator.next();
								List<DeviceEventsTransform> tempList = pair.getValue();
								log.info("tempList.size()" + "\t" + tempList.size());
								Integer zone_id = pair.getKey();
								// map1.put("zone_id", zone_id);
								log.info("**zone_id In Beacon Report**" + "\t" + zone_id);

								List<DeviceEventsTransform> tempList1 = tempList;
								for (DeviceEventsTransform det1 : tempList1) {
									if (!uuidDatemap.containsKey(det1.getUuid())) {
										List<String> list = new ArrayList<String>();
										list.add(det1.getEvent_occured_date().toString());
										uuidDatemap.put(det1.getUuid(), list);
									} else {
										uuidDatemap.get(det1.getUuid()).add(det1.getEvent_occured_date().toString());
									}
								}

								log.info("***uuidDatemap.size()***" + "\t" + uuidDatemap.size());
								List<String> uuidList = new ArrayList();
								for (DeviceEventsTransform det : tempList) {
									List<Object> datesList = new ArrayList<>();
									map1 = new LinkedHashMap<>();
									String status = det.getStatus();
									String uuid = det.getUuid();
									log.info("k value is" + "\t" + uuid);

									log.info("**Status In Beacon Report**" + "\t" + status);
									if(null == zone_id){
										zone_id = 0;
									}
									map1.put("zone_id", zone_id);

									if (!uuidList.contains(uuid)) {
										map1.put("uuid", uuid);
										if (status != null)
											map1.put("device_status", status);

										if (det.getEvent_occured_date() != null) {
											List<String> list1 = uuidDatemap.get(uuid);
											log.info("UUID" + "\t" + uuid);
											log.info("List Size" + "\t" + list1.size());
											for (String str : list1) {
												map2 = new LinkedHashMap<>();
												map2.put("event_occured_date", str);
												datesList.add(map2);
											}
										}
										log.info("***datesList***" + "\t" + datesList);
										map1.put("device_events", datesList);
										log.info("det.getEvent_occured_date().toString()" + "\t"
												+ det.getEvent_occured_date().toString());

										eventsList1.add(map1);
										log.info("***eventsList1***" + "\t" + eventsList1);
										log.info("**eventsList1.size()**" + "\t" + eventsList1.size());
										uuidList.add(uuid);
									}
									eventCount++;
									log.info("uuidList" + "\t" + uuidList);
									log.info("uuidList-Size" + "\t" + uuidList.size());
								}

								log.info("eventCount" + "\t" + eventCount);
							}
						}
					}
					// Loop End

					jsonMap.put("totalEvents", eventCount);
					jsonMap.put("devices", eventsList1);
				}

				String jsonFinal = JSONObject.toJSONString(jsonMap);
				respondJson = ErrorCodesUtil.displayJSONForEventsNotificationAPI(IPSConstants.BSUC01_MSG,
						IPSConstants.STATUSCODE_01, jsonFinal, type);
				log.info("***beaconDevicesJson***" + "\t" + respondJson);
			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR29_MSG, IPSConstants.STATUSCODE_29,
						type);
			}
		} else if (iPSReceiverByMacAndSessionIdStatus.equals(tokenUnavailable)) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
		} else if (iPSReceiverByMacAndSessionIdStatus.equals(macTokenMapping)) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
		} else if (iPSReceiverByMacAndSessionIdStatus.equals("NOTVALID")) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
		}
		return respondJson;
	}

	@RequestMapping(value = "/IPS/BeaconDeviceEventCreate/{sessionId}", method = RequestMethod.POST, consumes = {
			"application/json" }, produces = { "application/json" })
	public String BeaconDeviceEventCreate(@PathVariable("sessionId") String sessionId,
			@RequestBody List<BeaconDeviceEventCreateModel> beaconDeviceEventCreateModelList) throws ParseException {
		log.info("Inside DeviceController.BeaconDeviceEventCreate()");
		List<Integer> eventIDList = new ArrayList<Integer>();
		eventIDList.add(SCHOOL_ENTRY_ID);
		eventIDList.add(SCHOOL_EXIT_ID);
		eventIDList.add(GEOFENCE_ENTRY_ID);
		eventIDList.add(GEOFENCE_EXIT_ID);
		eventIDList.add(SOS_ID);
		String ErrorMessage = null;
		String StatusCode = Constant.BEACON_Success_Code;
		String responseJSON = null;
		IPSReceiver ipsReceiver = null;
		String type = "device.BeaconDeviceEventCreate";

		log.info("beaconDeviceEventCreateModelList.size()" + "\t" + beaconDeviceEventCreateModelList.size());
		List<String> resultList = new ArrayList();
		int i = 0;

		for (BeaconDeviceEventCreateModel beaconDeviceEventCreateModel : beaconDeviceEventCreateModelList) {
			i++;
			Integer eventId = beaconDeviceEventCreateModel.getEvent_id();
			String eventOccuredDate = beaconDeviceEventCreateModel.getEventoccureddate();
			String wearableUuid = beaconDeviceEventCreateModel.getWearable_uuid();
			Integer school_id = beaconDeviceEventCreateModel.getSchool_id();
			String accessKey = beaconDeviceEventCreateModel.getAccesskey();
			String ips_receiver_mac = beaconDeviceEventCreateModel.getIps_receiver_mac();
			Integer ips_receiver_zone_id = beaconDeviceEventCreateModel.getIps_receiver_zone_id();
			int ips_receiver_id = 0;

			if ((sessionId.isEmpty() || sessionId.equals(null)) || (accessKey.isEmpty() || accessKey.equals(null))
					|| !eventIDList.contains(eventId) || (null != ipsReceiver && school_id != ipsReceiver.getSchoolId())
					|| (null == ips_receiver_mac) || (ips_receiver_mac.isEmpty()) || (null == school_id)
					|| (school_id.intValue() == 0) || (null == accessKey) || (accessKey.isEmpty())) {
				resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR05_MSG + "::"
						+ IPSConstants.STATUSCODE_05 + "\"}");
				continue;
			}

			if (eventId == SCHOOL_ENTRY_ID || eventId == SCHOOL_EXIT_ID || eventId == SOS_ID) {
				ips_receiver_zone_id = 0;
				log.info("For School In/Out or SOS, ips_receiver_zone_id is not required");
			} else {
				if ((null == ips_receiver_zone_id) || (ips_receiver_zone_id.intValue() == 0)) {
					resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR05_MSG + "::"
							+ IPSConstants.STATUSCODE_05 + "\"}");
					continue;
				}
			}

			ipsReceiver = this.iPSReceiverService.getIPSReceiverEntryForMac(ips_receiver_mac);
			if (ipsReceiver == null) {
				resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR08_MSG + "::"
						+ IPSConstants.STATUSCODE_08 + "\"}");
				continue;
			}

			String iPSReceiverByMacAndSessionIdStatus = this.iPSReceiverService
					.findIPSReceiverByMacAndSessionId(sessionId, ips_receiver_mac);

			SupportedEvents supportedEvents = this.eventsService.getSupportedEventsByEventId(eventId);

			if (supportedEvents == null) {
				resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR30_MSG + "::"
						+ IPSConstants.STATUSCODE_30 + "\"}");
				continue;
			}

			if (null != ipsReceiver) {
				String mobileNo = this.iPSReceiverService.getMobileNumberBySchoolId(ipsReceiver.getSchoolId());
				ips_receiver_id = ipsReceiver.getReceiverId();
				boolean accessKeyValidation = false;
				if (null != mobileNo) {

					try {
						accessKeyValidation = AESEncryption.validatePassword(mobileNo, accessKey);
					} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
						log.info("Exception Occured in BeaconDevicesEventReport()" + "\t" + e);
					}
				} else {
					resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR04_MSG + "::"
							+ IPSConstants.STATUSCODE_04 + "\"}");
					continue;
				}

				if (!accessKeyValidation) {
					resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR22_MSG + "::"
							+ IPSConstants.STATUSCODE_22 + "\"}");
					continue;
				}
			}
			if (iPSReceiverByMacAndSessionIdStatus.equals("Success")) {
				Devices devices = deviceService.checkDeviceIdExist(wearableUuid);
				int schoolID = deviceService.getSchoolIdbyDeviceUUID(wearableUuid);
				if (devices == null || devices.equals("") || schoolID != school_id) {
					resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR15_MSG + "::"
							+ IPSConstants.STATUSCODE_15 + "\"}");
					continue;
				}

				Geozones geoZone = null;
				SchoolInOutTransform schoolInOutTimeModel = this.deviceService.getschoolInOuttimings(school_id);
				if (null == schoolInOutTimeModel || null == schoolInOutTimeModel.getSchool_in_end()
						|| null == schoolInOutTimeModel.getSchool_in_start()
						|| null == schoolInOutTimeModel.getSchool_out_end()
						|| null == schoolInOutTimeModel.getSchool_out_start()) {
					StatusCode = "BERR33";
					ErrorMessage = "Missing School Timings data, School In/Out & SOS Events will be ignored";
					resultList.add("{\"Record-" + i + "\":\"Error=" + ErrorMessage + "::" + StatusCode + "\"}");
					continue;
				}

				boolean createEvent = false;
				int createdEventId = 0;
				if (beaconDeviceEventCreateModel.getEvent_id() == GEOFENCE_EXIT_ID) {
					createdEventId = deviceService.getLatestGeoFenceEntryEventId(beaconDeviceEventCreateModel);
					if (createdEventId > 0) {
						createEvent = true;
					} else {
						createEvent = false;
					}
				} else {
					createEvent = this.deviceService.createBeaconDeviceEvent(beaconDeviceEventCreateModel,
							devices.getDeviceId(), supportedEvents, wearableUuid, geoZone, ips_receiver_zone_id,
							ips_receiver_id);
					createdEventId = this.deviceService.getlatestDeviceEventId();
				}

				if (createEvent) {
					log.info("ENTRY_ID is : " + SCHOOL_ENTRY_ID);
					log.info("deviceModel.getEventid() i s: " + eventId);
					if (eventId == SOS_ID) {
						String[] data = eventOccuredDate.split(" ");
						String date = data[0];
						String time = data[1];
						String isAbnormal = "no";
						Calendar calendar = Calendar.getInstance();
						int day = calendar.get(Calendar.DAY_OF_WEEK);
						if (day != 1 || day != 7) {
							isAbnormal = CommonUtil.isStudentSoSAbnormal(time,
									schoolInOutTimeModel.getSchool_in_end().toString(),
									schoolInOutTimeModel.getSchool_out_start().toString());
						}
						this.deviceService.updateSchoolEntryOrExitforSchoolEvent(createdEventId, time, isAbnormal,
								eventId);

					}
					if (eventId == SCHOOL_ENTRY_ID) {
						String[] data = eventOccuredDate.split(" ");
						String time = data[1];
						String isAbnormal = "no";
						Calendar calendar = Calendar.getInstance();
						int day = calendar.get(Calendar.DAY_OF_WEEK);

						if (day != 1 || day != 7) {
							isAbnormal = CommonUtil.isStudentAbnormal(time,
									schoolInOutTimeModel.getSchool_in_start().toString(),
									schoolInOutTimeModel.getSchool_in_end().toString());
						} else
							isAbnormal = "yes";
						this.deviceService.updateSchoolEntryOrExitforSchoolEvent(createdEventId, time, isAbnormal,
								eventId);
					} else if (eventId == SCHOOL_EXIT_ID) {
						String[] data = eventOccuredDate.split(" ");
						String time = data[1];
						String isAbnormal = "no";
						Calendar calendar = Calendar.getInstance();
						int day = calendar.get(Calendar.DAY_OF_WEEK);
						if (day != 1 || day != 7) {
							isAbnormal = CommonUtil.isStudentAbnormal(time,
									schoolInOutTimeModel.getSchool_out_start().toString(),
									schoolInOutTimeModel.getSchool_out_end().toString());
						} else
							isAbnormal = "yes";
						this.deviceService.updateSchoolEntryOrExitforSchoolEvent(createdEventId, time, isAbnormal,
								eventId);

					}
					if (eventId == GEOFENCE_ENTRY_ID) {
						String[] data = eventOccuredDate.split(" ");
						String time = data[1];

						this.deviceService.updateGeoFenceEntryExitforSchoolEvent(createdEventId, time, eventId);

					} else if (eventId == GEOFENCE_EXIT_ID) {
						String[] data = eventOccuredDate.split(" ");
						String time = data[1];

						this.deviceService.updateGeoFenceEntryExitforSchoolEvent(createdEventId, time, eventId);
					}

					String parentUnsubscribeEvents = this.deviceService.getParentUnsunsribeEvent(eventId);
					if (null == parentUnsubscribeEvents) {
						resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BSUC01_MSG + "::"
								+ IPSConstants.STATUSCODE_01 + "\"}");
						continue;
					}
					log.info("createdEventId is :" + createdEventId);
					log.info("parent can unsubscribe events ?: " + parentUnsubscribeEvents);

					FCMModel fcmModel = new FCMModel();
					String eventName = deviceService.getEventNamebyId(eventId);
					log.info("eventName :" + eventName);
					fcmModel.setBody(eventName + " Occured at " + eventOccuredDate.toString());
					log.info(eventName + " Occured at " + eventOccuredDate.toString());
					fcmModel.setUuid(wearableUuid);
					fcmModel.setEventId(eventId);
					log.info("uuid" + wearableUuid);

					log.info("Inside CreateEvent Boolean true");

					StatusCode = IPSConstants.STATUSCODE_01;
					ErrorMessage = IPSConstants.BSUC01_MSG;
					if (null != parentUnsubscribeEvents) {
						List<ValidParentUsersWithTokenTransform> parentUsers = userService
								.getParentUserTokenDetails(eventId, wearableUuid, parentUnsubscribeEvents);
						log.info("Parent users size : " + parentUsers.size());
						if (parentUsers.size() == 0)
							log.info("NO Parent users subscribed for this event");

						for (ValidParentUsersWithTokenTransform item : parentUsers) {
							log.info("sendign message to Parent User" + item.getUserId());

							try {
								int latestQueueId = this.deviceService.getlatestId();
								latestQueueId++;
								this.deviceService.addEntryToDeviceEventQueue(latestQueueId, item.getUserId(),
										createdEventId, devices.getDeviceId(), supportedEvents,
										item.getAndroidAppToken(), item.getiPhoneAppToken());
								int queueid = this.deviceService.getlatestId();
								String fcmTitle = eventName + ": " + item.getStudentName();
								fcmModel.setTitle(fcmTitle);
								fcmModel.setQueueid(queueid);
								fcmModel.setStudentName(item.getStudentName());
								fcmModel.setDeviceEventId(createdEventId);
								fcmModel.setEventId(eventId);
								log.info("QueueID:" + queueid);
								log.info("UserID:" + item.getUserId());
								log.info("StudentName:" + item.getStudentName());
								log.info("Message Body:" + fcmModel.getBody());

								if (null != item.getAndroidAppToken() && !item.getAndroidAppToken().equals("")) {
									log.info("Sending notification to parent user on Android");
									log.info("PARENT_ANDROID_SERVER_KEY:" + PARENT_ANDROID_SERVER_KEY);
									log.info("App Token:" + item.getAndroidAppToken());
									FCMUtility.PushNotfication(PARENT_ANDROID_SERVER_KEY, item.getAndroidAppToken(),
											fcmModel);
								}
								if (null != item.getiPhoneAppToken() && !item.getiPhoneAppToken().equals("")) {
									log.info("Sending notification to parent user on IPHONE");
									log.info("PARENT_APPLE_SERVER_KEY:" + PARENT_APPLE_SERVER_KEY);
									log.info("App Token:" + item.getiPhoneAppToken());
									FCMUtility.PushNotfication(PARENT_APPLE_SERVER_KEY, item.getiPhoneAppToken(),
											fcmModel);
								}
								log.info("After sending notification to parent user");
							} catch (Exception e) {
								log.error("Exception occured while sending PushNotfication > " + e);
							}
						}
					}
					log.info("deviceModel.getEventid()" + "\t" + eventId);
					Calendar calendar = Calendar.getInstance();
					int day = calendar.get(Calendar.DAY_OF_WEEK);
					if (day != 1 || day != 7) {

						NotifyToTeacherAndStaffTransform notifyList = deviceService
								.getEventToNotifyToTeacherAndStaff(eventId);
						int schoolId = 0;

						if (notifyList.getNotify_teacher().toUpperCase().equals("YES")) {
							String eventOccuredadte = eventOccuredDate;

							String[] data = eventOccuredadte.split(" ");
							String date = data[0];
							String time = data[1];
							log.info("date" + date);
							log.info("time" + time);
							log.info("deviceModel.getSchoolId()" + school_id);
							schoolId = deviceService.getSchoolIdtoSendNotificationtoStaff(time, date, school_id);
							log.info("schoolId" + schoolId);

							if (schoolId == 0)
								log.info("School closed ");
							if (schoolId > 0) {
								List<ValidTeacherUsersWithTokenTransform> teacherUsers = userService
										.getTeacherUserTokenDetails(eventId, wearableUuid);

								for (ValidTeacherUsersWithTokenTransform item : teacherUsers) {
									log.info("sendign message to Teacher User" + item.getUserId());
									try {
										int latestQueueId = this.deviceService.getlatestId();
										latestQueueId++;
										this.deviceService.addEntryToDeviceEventQueue(latestQueueId, item.getUserId(),
												createdEventId, devices.getDeviceId(), supportedEvents,
												item.getAndroidAppToken(), item.getiPhoneAppToken());
										int queueid = this.deviceService.getlatestId();
										String fcmTitle = eventName + ": " + item.getStudentName();
										fcmModel.setTitle(fcmTitle);
										fcmModel.setQueueid(queueid);
										fcmModel.setDeviceEventId(createdEventId);
										fcmModel.setStudentName(item.getStudentName());
										fcmModel.setEventId(eventId);
										log.info("QueueID:" + queueid);
										log.info("UserID:" + item.getUserId());
										log.info("StudentName:" + item.getStudentName());
										log.info("Message Body:" + fcmModel.getBody());

										if (null != item.getAndroidAppToken()
												&& !item.getAndroidAppToken().equals("")) {
											log.info("Sending notification to parent user on Android");
											log.info("SCHOOL_ANDROID_SERVER_KEY:" + SCHOOL_ANDROID_SERVER_KEY);
											log.info("App Token:" + item.getAndroidAppToken());
											FCMUtility.PushNotfication(SCHOOL_ANDROID_SERVER_KEY,
													item.getAndroidAppToken(), fcmModel);
										}
										if (null != item.getiPhoneAppToken() && !item.getiPhoneAppToken().equals("")) {
											log.info("Sending notification to parent user on IPHONE");
											log.info("SCHOOL_APPLE_SERVER_KEY:" + SCHOOL_APPLE_SERVER_KEY);
											log.info("App Token:" + item.getiPhoneAppToken());
											FCMUtility.PushNotfication(SCHOOL_APPLE_SERVER_KEY,
													item.getiPhoneAppToken(), fcmModel);
										}
										log.info("After sending notification to teacher user");
									} catch (Exception e) {
										log.error("Exception occured while sending PushNotfication > " + e);
									}
								}

								if (notifyList.getNotify_staff().toUpperCase().equals("YES")) {

									List<ValidTeacherUsersWithTokenTransform> staffUsers = userService
											.getStaffUserTokenDetails(eventId, wearableUuid);

									for (ValidTeacherUsersWithTokenTransform item : staffUsers) {
										log.info("sendign message to Staff User" + item.getUserId());
										try {
											int latestQueueId = this.deviceService.getlatestId();
											latestQueueId++;
											this.deviceService.addEntryToDeviceEventQueue(latestQueueId,
													item.getUserId(), createdEventId, devices.getDeviceId(),
													supportedEvents, item.getAndroidAppToken(),
													item.getiPhoneAppToken());
											int queueid = this.deviceService.getlatestId();
											String fcmTitle = eventName + ": " + item.getStudentName();
											fcmModel.setTitle(fcmTitle);
											fcmModel.setQueueid(queueid);
											fcmModel.setDeviceEventId(createdEventId);
											fcmModel.setStudentName(item.getStudentName());
											fcmModel.setEventId(eventId);
											log.info("QueueID:" + queueid);
											log.info("UserID:" + item.getUserId());
											log.info("StudentName:" + item.getStudentName());
											log.info("Message Body:" + fcmModel.getBody());

											if (null != item.getAndroidAppToken()
													&& !item.getAndroidAppToken().equals("")) {
												log.info("Sending notification to parent user on Android");
												log.info("SCHOOL_ANDROID_SERVER_KEY:" + SCHOOL_ANDROID_SERVER_KEY);
												log.info("App Token:" + item.getAndroidAppToken());
												FCMUtility.PushNotfication(SCHOOL_ANDROID_SERVER_KEY,
														item.getAndroidAppToken(), fcmModel);
											}
											if (null != item.getiPhoneAppToken()
													&& !item.getiPhoneAppToken().equals("")) {
												log.info("Sending notification to parent user on IPHONE");
												log.info("SCHOOL_APPLE_SERVER_KEY:" + SCHOOL_APPLE_SERVER_KEY);
												log.info("App Token:" + item.getiPhoneAppToken());
												FCMUtility.PushNotfication(SCHOOL_APPLE_SERVER_KEY,
														item.getiPhoneAppToken(), fcmModel);
											}
											log.info("After sending notification to Staff user");
										} catch (Exception e) {
											log.error("Exception occured while sending PushNotfication > " + e);
										}
									}
								}
							}
						}
					}
				} else {
					StatusCode = IPSConstants.STATUSCODE_31;
					ErrorMessage = IPSConstants.BERR31_MSG;
					resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR31_MSG + "::"
							+ IPSConstants.STATUSCODE_31 + "\"}");
					continue;
				}
			} else if (iPSReceiverByMacAndSessionIdStatus.equals(tokenUnavailable)) {
				resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR10_MSG + "::"
						+ IPSConstants.STATUSCODE_10 + "\"}");
				continue;
			} else if (iPSReceiverByMacAndSessionIdStatus.equals(macTokenMapping)) {
				resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR22_MSG + "::"
						+ IPSConstants.STATUSCODE_22 + "\"}");
				continue;
			} else if (iPSReceiverByMacAndSessionIdStatus.equals("NOTVALID")) {
				resultList.add("{\"Record-" + i + "\":\"Error=" + IPSConstants.BERR02_MSG + "::"
						+ IPSConstants.STATUSCODE_02 + "\"}");
				continue;
			}
		}
		String msg = "IPSDeviceCreateEvent Request Processed Successfully";
		responseJSON = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ StatusCode + "\",\"StatusMessage\": \"" + msg + "\"} ";
		if (resultList.size() != 0) {
			responseJSON = responseJSON + ", \"Result\":  { \"EachRecordStatus\": " + resultList + " }";
		}
		responseJSON = responseJSON + " } }";

		return responseJSON;
	}

	@RequestMapping(value = "/web/GradewiseFitness/{token}", method = RequestMethod.POST, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String GradewiseFitness(@PathVariable("token") String token,
			@RequestBody ParentActivityForDashboardModel pADashboardModel) throws ParseException {
		log.info("Into GradewiseFitness() {");
		String statusMsg;
		String statusCode;
		String responseJSON = null;
		Map<Object, Object> map = null;
		String jsonString = null;
		List myList = new ArrayList<>();
		String json = JSONUtility.convertObjectToJson(pADashboardModel);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			statusMsg = "Input Is Invalid";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, Constant.StatusCode,
					Constant.GRADEWISE_FITNESS_TYPE);
			return responseJSON;
		}

		if (pADashboardModel.getMeasure_type() == null || pADashboardModel.getMeasure_type().trim().length() == 0) {
			statusMsg = "Input Is Empty";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, Constant.StatusCode4,
					Constant.GRADEWISE_FITNESS_TYPE);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(token);
		log.info("user is " + user);

		if (user != null) {
			int school_id = user.getAccountId();
			if (user.getRoleType().equals(Constant.SchoolAdmin)) {

				String measureType = pADashboardModel.getMeasure_type();
				String startDt = null;
				String endDt = null;
				if ((pADashboardModel.getStart_date() == null || pADashboardModel.getStart_date().trim().length() == 0)
						&& (pADashboardModel.getEnd_date() == null
								|| pADashboardModel.getEnd_date().trim().length() == 0)) {
					startDt = DateUtil.getYesterdaysDate(sourceDisplayDateFormat) + " " + startDateFormat;
					endDt = DateUtil.getYesterdaysDate(sourceDisplayDateFormat) + " " + endDateFormat;
				} else if (pADashboardModel.getStart_date() != null && (pADashboardModel.getEnd_date() == null
						|| pADashboardModel.getEnd_date().trim().length() == 0)) {
					startDt = pADashboardModel.getStart_date() + " " + startDateFormat;
					endDt = pADashboardModel.getStart_date() + " " + endDateFormat;
				} else if ((pADashboardModel.getStart_date() == null
						|| pADashboardModel.getStart_date().trim().length() == 0)
						&& (pADashboardModel.getEnd_date() != null)) {
					startDt = pADashboardModel.getEnd_date() + " " + startDateFormat;
					endDt = pADashboardModel.getEnd_date() + " " + endDateFormat;
				} else if (DateUtil.isStartDateAfterEndDate(pADashboardModel.getStart_date(),
						pADashboardModel.getEnd_date(), sourceDisplayDateFormat)) {
					startDt = pADashboardModel.getEnd_date() + " " + startDateFormat;
					endDt = pADashboardModel.getStart_date() + " " + endDateFormat;
				} else {
					if (pADashboardModel.getStart_date().equals(pADashboardModel.getEnd_date())) {
					}
					startDt = pADashboardModel.getStart_date() + " " + startDateFormat;
					endDt = pADashboardModel.getEnd_date() + " " + endDateFormat;
				}

				log.debug("endDateFormat In GradewiseFitness >>>" + endDateFormat);
				log.debug("startDt  In GradewiseFitness >>>  " + startDt);
				log.debug("endDt In GradewiseFitness >>> " + endDt);

				List<DeviceStudentsTransform> deviceStudentsTransformList = this.deviceService
						.findStudentsInfoForFitnessActivity(school_id, pADashboardModel.getGrade(), startDt, endDt);
				List<DeviceStudentsTransform> finalList = null;
				Map<Integer, List<DeviceStudentsTransform>> tempMap = new HashMap<>();
				List<Row> rowsList = null;

				if (deviceStudentsTransformList.size() == 0) {
					String kidsRespJson = ErrorCodesUtil.displayJSONForGradewiseFitness(Constant.SucessMsgActivity,
							Constant.StatusCodeActivity, myList.toString(), Constant.GRADEWISE_FITNESS_TYPE);
					log.info("***kidsRespJson***" + "\t" + kidsRespJson);
					return kidsRespJson;
				}
				for (DeviceStudentsTransform dts : deviceStudentsTransformList) {
					int schoolId = this.deviceService.findSchoolIdByUUID(dts.getUuid());
					String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
					if (measureType.equals("pfi")) {
						rowsList = this.cassandraService.findFitnessActivity(dts.getUuid(),
								DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
								DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone), "pfi",
								keyspace);
					} else if (measureType.equals("steps")) {
						rowsList = this.cassandraService.findFitnessActivity(dts.getUuid(),
								DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
								DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone), "steps",
								keyspace);
					} else if (measureType.equals("calories")) {
						rowsList = this.cassandraService.findFitnessActivity(dts.getUuid(),
								DateUtil.convertDateToUnixTime(dts.getNew_start_date(), timeZone, dateTimezone),
								DateUtil.convertDateToUnixTime(dts.getNew_end_date(), timeZone, dateTimezone),
								"calories", keyspace);
					}
					
					if(null != rowsList){
						int initialPfi = 0;
						for (Row row : rowsList) {
							if (measureType.equals("pfi")) {
								initialPfi = row.getInt("pfi");
							} else if (measureType.equals("steps")) {
								initialPfi = row.getInt("steps");
							} else if (measureType.equals("calories")) {
								initialPfi = row.getInt("calories");
							}
						}
						if (!tempMap.containsKey(dts.getStudent_id())) {

							List<DeviceStudentsTransform> list = new ArrayList<DeviceStudentsTransform>();
							dts.setPfi(initialPfi);
							list.add(dts);
							tempMap.put(dts.getStudent_id(), list);
						} else {
							List tempList = tempMap.get(dts.getStudent_id());
							DeviceStudentsTransform old = (DeviceStudentsTransform) tempList.get(tempList.size() - 1);
							initialPfi += old.getPfi();
							dts.setPfi(initialPfi);
							tempMap.get(dts.getStudent_id()).remove(tempList.size() - 1);
							log.info("initialPfi" + initialPfi);
							log.info("dts.getStudent_id()" + dts.getStudent_id());
							tempMap.get(dts.getStudent_id()).add(dts);

						}
					}
				}
				log.info("tempMap is " + tempMap);

				log.info("deviceStudentsTransformList.size()" + "\t" + deviceStudentsTransformList.size());

				switch (measureType) {

				case "pfi": {
					if (tempMap.size() > 0) {
						Iterator<Map.Entry<Integer, List<DeviceStudentsTransform>>> mapIterator = tempMap.entrySet()
								.iterator();
						while (mapIterator.hasNext()) {
							Map.Entry<Integer, List<DeviceStudentsTransform>> pair = mapIterator.next();
							int student_id = pair.getKey();
							finalList = pair.getValue();
							int count = 1;
							int pfi = 0;
							for (DeviceStudentsTransform dts : finalList) {

								log.debug("fitness start_date :::::::::: " + dts.getNew_start_date());
								log.debug("fitness end_date :::::::::: " + dts.getNew_end_date());
								log.info("finalList size  is " + finalList.size());

								map = new HashMap();
								map.put("class", dts.getStudent_class());
								map.put("sname", dts.getStudent_name());
								map.put("studentid", dts.getStudent_id());
								map.put("value", dts.getPfi());

								count++;
								log.info("count is " + count);

								jsonString = JSONObject.toJSONString(map);
								myList.add(jsonString);
								log.info("List of GradewiseFitness" + "\n" + myList.toString());
							}
						}
						log.info("***myList***" + "\n" + myList);
						responseJSON = ErrorCodesUtil.displayJSONForGradewiseFitness(Constant.SucessMsgActivity,
								Constant.StatusCodeActivity, myList.toString(), Constant.GRADEWISE_FITNESS_TYPE);
						log.info("***kidsRespJson***" + "\t" + responseJSON);
						return responseJSON;
					}
					break;
				}

				case "steps": {
					if (tempMap.size() > 0) {
						Iterator<Map.Entry<Integer, List<DeviceStudentsTransform>>> mapIterator = tempMap.entrySet()
								.iterator();
						while (mapIterator.hasNext()) {
							Map.Entry<Integer, List<DeviceStudentsTransform>> pair = mapIterator.next();
							int student_id = pair.getKey();
							finalList = pair.getValue();
							int count = 1;
							int pfi = 0;
							for (DeviceStudentsTransform dts : finalList) {

								log.debug("fitness start_date :::::::::: " + dts.getNew_start_date());
								log.debug("fitness end_date :::::::::: " + dts.getNew_end_date());
								log.info("finalList size  is " + finalList.size());

								map = new HashMap();
								map.put("class", dts.getStudent_class());
								map.put("sname", dts.getStudent_name());
								map.put("studentid", dts.getStudent_id());
								map.put("value", dts.getPfi());

								count++;
								log.info("count is " + count);

								jsonString = JSONObject.toJSONString(map);
								myList.add(jsonString);
								log.info("List of GradewiseFitness" + "\n" + myList.toString());
							}
						}
						log.info("***myList***" + "\n" + myList);
						responseJSON = ErrorCodesUtil.displayJSONForGradewiseFitness(Constant.SucessMsgActivity,
								Constant.StatusCodeActivity, myList.toString(), Constant.GRADEWISE_FITNESS_TYPE);
						log.info("***kidsRespJson***" + "\t" + responseJSON);
						return responseJSON;

					}
					break;
				}
				case "calories": {
					if (tempMap.size() > 0) {
						Iterator<Map.Entry<Integer, List<DeviceStudentsTransform>>> mapIterator = tempMap.entrySet()
								.iterator();
						while (mapIterator.hasNext()) {
							Map.Entry<Integer, List<DeviceStudentsTransform>> pair = mapIterator.next();
							int student_id = pair.getKey();
							finalList = pair.getValue();
							int count = 1;
							int pfi = 0;
							for (DeviceStudentsTransform dts : finalList) {
								log.debug("fitness start_date :::::::::: " + dts.getNew_start_date());
								log.debug("fitness end_date :::::::::: " + dts.getNew_end_date());
								log.info("finalList size  is " + finalList.size());

								map = new HashMap();
								map.put("class", dts.getStudent_class());
								map.put("sname", dts.getStudent_name());
								map.put("studentid", dts.getStudent_id());
								map.put("value", dts.getPfi());

								count++;
								log.info("count is " + count);

								jsonString = JSONObject.toJSONString(map);
								myList.add(jsonString);
								log.info("List of GradewiseFitness" + "\n" + myList.toString());
							}
						}
						log.info("***myList***" + "\n" + myList);
						responseJSON = ErrorCodesUtil.displayJSONForGradewiseFitness(Constant.SucessMsgActivity,
								Constant.StatusCodeActivity, myList.toString(), Constant.GRADEWISE_FITNESS_TYPE);
						log.info("***kidsRespJson***" + "\t" + responseJSON);
						return responseJSON;
					}
					break;
				}
				default: {
					statusCode = "ERR06";
					statusMsg = "Not a Valid Activity Type";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode,
							Constant.GRADEWISE_FITNESS_TYPE);
				}
				}
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.GRADEWISE_FITNESS_TYPE);
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.StatusCodeJon, Constant.ErrorMessageJon,
					Constant.GRADEWISE_FITNESS_TYPE);
		}
		log.info("Exiting GradewiseFitness() }");
		return responseJSON;
	}
}
