package com.liteon.icgwearable.controller;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.datastax.driver.core.Row;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.ClassGrade;
import com.liteon.icgwearable.hibernate.entity.Geozones;
import com.liteon.icgwearable.hibernate.entity.Reminders;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.DeviceTypeAlertLatestModel;
import com.liteon.icgwearable.model.FCMModel;
import com.liteon.icgwearable.model.GeoZoneModel;
import com.liteon.icgwearable.model.KidsSafeModel;
import com.liteon.icgwearable.model.LatestAnnoncementModel;
import com.liteon.icgwearable.model.LatestGPSLocationModel;
import com.liteon.icgwearable.model.LatestGeoFenceAlertModel;
import com.liteon.icgwearable.model.LatestReminderModel;
import com.liteon.icgwearable.model.LatestRewardsModel;
import com.liteon.icgwearable.model.RemindersModel;
import com.liteon.icgwearable.model.SchoolTimeModel;
import com.liteon.icgwearable.model.StudentsSleepDataModel;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.CassandraService;
import com.liteon.icgwearable.service.ClassGradeService;
import com.liteon.icgwearable.service.CommonService;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.service.ParentService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.ActivityLogTransform;
import com.liteon.icgwearable.transform.ClassGradeTransform;
import com.liteon.icgwearable.transform.ExternalSystemStatusTransform;
import com.liteon.icgwearable.transform.RemindersTransform;
import com.liteon.icgwearable.transform.RewardsCategoryTransform;
import com.liteon.icgwearable.transform.StudentRewardsTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;
import com.liteon.icgwearable.transform.ValidParentUsersWithTokenTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.DateUtil;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.FCMUtility;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.PropertiesUtil;
import com.liteon.icgwearable.util.WebUtility;

@RestController
public class CommonController {

	private static Logger log = Logger.getLogger(CommonController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private CommonService commonService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private ParentService parentService;

	@Autowired
	private ClassGradeService classGradeService;

	@Autowired
	HttpServletRequest request;

	@Autowired
	HttpServletResponse response;

	@Autowired
	ServletContext servletContext;

	@Value("${PARENT_ANDROID_SERVER_KEY}")
	private String ANDROID_SERVER_KEY;

	@Value("${PARENT_APPLE_SERVER_KEY}")
	private String APPLE_SERVER_KEY;

	@Value("${MAX_GEOZONES}")
	private int MAX_GEOZONES;

	@Value("${db.dateTime}")
	private String dbDateTime;

	@Value("${SCHOOL_ENTRY_ALERT_ID}")
	private Integer SCHOOL_ENTRY_ID;

	@Value("${SCHOOL_EXIT_ALERT_ID}")
	private Integer SCHOOL_EXIT_ID;

	@Value("${GEOFENCE_ENTRY_ID}")
	private Integer GEOFENCE_ENTRY_ID;

	@Value("${GEOFENCE_EXIT_ID}")
	private Integer GEOFENCE_EXIT_ID;

	@Value("${SOS_ALERT_ID}")
	private Integer SOS_ID;

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
	@Autowired
	private ActivityLogService activityLogs;

	private Map deviveMap = PropertiesUtil.getDeviceIDNameMapping();

	WebUtility webUtility = WebUtility.getWebUtility();

	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;
	@Resource(name = "configProperties")
	private Properties configProperties;
	@Value("${PAGINATION_NO_OF_RECORDS}")
	private int PAGINATION_NO_OF_RECORDS;

	@RequestMapping(value = "/mobile/GeozoneList/{sessionID}/{uuid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String GeozoneList(@PathVariable("sessionID") String sessionID, @PathVariable("uuid") String uuid) {

		JSONArray jsonArray = new JSONArray();
		List myList = new ArrayList<>();
		String jsonArrayString = null;
		String ErrorMessage = null;

		String StatusCode = null;
		String responseJSON = null;

		String jsonString = null;
		String type = "common.GeozoneList";
		log.info("logged our session Id" + sessionID);

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			log.info("user rloe" + user.getRoleType());
			if (!((user.getRoleType().equals("parent_admin")) || (user.getRoleType().equals("school_teacher")))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (uuid != null) {
				List<GeoZoneModel> zonessList = commonService.getUserGeoZones(user.getId(), uuid);
				log.info("after");
				for (GeoZoneModel zones : zonessList) {
					Map<Object, Object> map = new LinkedHashMap<>();
					if (zones.getGeozone_id() != null || zones.getGeozone_id().intValue() != 0)
						map.put("geozone_id", zones.getGeozone_id());
					if (zones.getUser_id() != null || zones.getUser_id().intValue() != 0)
						map.put("user_id", zones.getUser_id());
					if (uuid != null)
						map.put("uuid", uuid);
					if (zones.getZone_details() != null)
						map.put("zone_details", zones.getZone_details());
					if (zones.getZone_name() != null)
						map.put("zone_name", zones.getZone_name());
					if (zones.getZone_entry_alert() != null || zones.getZone_entry_alert().trim().length() != 0)
						map.put("zone_entry_alert", zones.getZone_entry_alert());
					if (zones.getZone_exit_alert() != null || zones.getZone_exit_alert().trim().length() != 0)
						map.put("zone_exit_alert", zones.getZone_exit_alert());
					if (zones.getZone_description() != null)
						map.put("zone_description", zones.getZone_description());
					if (zones.getZone_radius() != null)
						map.put("zone_radius", zones.getZone_radius());
					if (zones.getFrequency_minutes() != null || zones.getFrequency_minutes().intValue() != 0)
						map.put("frequency_minutes", zones.getFrequency_minutes());
					if (zones.getValid_till() != null)
						map.put("valid_till", zones.getValid_till().toString());
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
					log.info("My List Contents::::::::" + myList);
				}

				jsonArrayString = jsonArray.toString();
				log.info("jsonArrayString : " + "\n" + jsonArrayString);

				StatusCode = "SUC01";
				ErrorMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displaySuccessJSONForGeozones(type, ErrorMessage, StatusCode,
						myList.toString());
				// JSONUtility.respondAsJSON(response, responseJSON);
			} else {
				StatusCode = "ERR06";
				ErrorMessage = "Invalid Device";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);

			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/GeozoneCreate/{sessionID}/{uuID}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String GeozoneCreate(@RequestBody GeoZoneModel geoZoneModel, ModelAndView model,
			@PathVariable("sessionID") String sessionID, @PathVariable("uuID") String uuID) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String userOperation = "Create";
		String type = "common.GeozoneCreate";

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

		String json = JSONUtility.convertObjectToJson(geoZoneModel);
		log.info("Request Json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			ErrorMessage = "Input Is Invalid";
			StatusCode = "WERR05";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		List<String> zoneTypeList = new ArrayList<String>();
		zoneTypeList.add("circle");
		zoneTypeList.add("rectangle");

		List<Integer> freequencyLimitList = new ArrayList<Integer>();
		freequencyLimitList.add(5);
		freequencyLimitList.add(10);
		freequencyLimitList.add(30);

		List<String> commonCheckList = new ArrayList<String>();
		commonCheckList.add("yes");
		commonCheckList.add("no");

		Users user = null;
		log.info("logged our session Id" + sessionID);
		try {
			if (geoZoneModel.getGeozone_id() == 0) {
				userOperation = "Create";
			} else if (geoZoneModel.getGeozone_id() > 0) {
				userOperation = "Update";
			}
			action = userOperation;
			if (geoZoneModel.getZone_details().isEmpty() || geoZoneModel.getZone_name().isEmpty()
					|| geoZoneModel.getValid_till() == null || geoZoneModel.getFrequency_minutes() == null
					|| geoZoneModel.getFrequency_minutes() == 0) {
				StatusCode = "ERR04";
				ErrorMessage = "Empty input , please provide valid input ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (!commonCheckList.contains(geoZoneModel.getZone_entry_alert())
					|| !commonCheckList.contains(geoZoneModel.getZone_exit_alert())) {
				StatusCode = "ERR05";
				ErrorMessage = "Invalid alert Value input , please provide valid Alert  Value as 'yes' or 'no' ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			if (!freequencyLimitList.contains(geoZoneModel.getFrequency_minutes())) {
				StatusCode = "ERR05";
				ErrorMessage = "Invalid frequency input , please provide valid freequency limit in 5,10 and 30 minutes ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			user = userService.validateUserBySession(sessionID);

			if (null != user) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					StatusCode = "ERR02";
					ErrorMessage = "Session Expired ,Please Relogin ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
				if (!((user.getRoleType().equals("parent_admin")))) {
					StatusCode = "ERR03";
					ErrorMessage = "Unauthorised User ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}

				Integer deviceId = deviceService.getDeviceIdByUuidandUser(user, uuID);
				log.info("deviceId" + deviceId);
				if (deviceId == 0) {

					StatusCode = "ERR06";
					ErrorMessage = "Invalid Device";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
				if (userOperation.equals("Create")) {
					int existingGeofencesforUser = commonService.getTotalNoOfZeofencesPerUser(user.getId(), uuID);
					log.info("existingGeofencesforUser:" + deviceId);
					if (existingGeofencesforUser >= MAX_GEOZONES) {
						StatusCode = "ERR18";
						ErrorMessage = "Reached max no of Geofence per device that can be created";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						// JSONUtility.respondAsJSON(response, responseJSON);
						return responseJSON;
					}
				}
				// Devices device = eventsService.getDeviceByUuid(uuID);
				Users subUser = userService.getUserByMobileSessionId(sessionID);
				log.info("userID:::" + subUser.getId());
				Geozones gz = new Geozones();
				gz.setZoneDetails(geoZoneModel.getZone_details());
				gz.setZone_radius(geoZoneModel.getZone_radius());
				gz.setUsers(subUser);
				gz.setUuid(uuID);
				gz.setZonename(geoZoneModel.getZone_name());
				gz.setZoneEntryAlert(geoZoneModel.getZone_entry_alert());
				gz.setZoneExitAlert(geoZoneModel.getZone_exit_alert());
				if (geoZoneModel.getZone_description() != null)
					gz.setZoneDescription(geoZoneModel.getZone_description());
				gz.setValidTill(geoZoneModel.getValid_till());
				gz.setFrequencyMinutes(geoZoneModel.getFrequency_minutes());
				// gz.setUsersToNotify(geoZoneModel.getUsers_to_notify());
				gz.setCreateDate(new Date());
				gz.setUpdatedDate(new Date());

				if (userOperation.equals("Create")) {

					ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
					commonService.createGeoZone(gz);
					StatusCode = "SUC01";
					ErrorMessage = "API Request Success";
					responseJSON = ErrorCodesUtil.displaySuccessJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);

					activityLogs.info(activityLog);
					return responseJSON;

				} else {
					Geozones geozones = this.commonService
							.checkIfGeozoneIdExist(geoZoneModel.getGeozone_id().intValue());
					if (null == geozones) {
						StatusCode = "ERR05";
						ErrorMessage = "Input is invalid";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						// JSONUtility.respondAsJSON(response, responseJSON);
						ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
						activityLogs.error(activityLog);
						return responseJSON;
					}
					gz.setGeozoneId(geoZoneModel.getGeozone_id());

					ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
					commonService.updateGeoZone(gz);

					activityLogs.info(activityLog);
					StatusCode = "SUC01";
					ErrorMessage = "API Request Success";
					responseJSON = ErrorCodesUtil.displaySuccessJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}

			} else {
				StatusCode = "ERR01";
				ErrorMessage = "Invalid User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} catch (Exception e) {
			if (null != user) {
				StatusCode = "ERR03";
				ErrorMessage = "Failed to authorize.";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				activityLogs.error(activityLog);
				log.debug(e.getMessage());
			}
		}
		return responseJSON;

	}

	@RequestMapping(value = "/mobile/GeozoneDelete/{sessionID}/{uuID}", method = RequestMethod.DELETE, produces = {
			"application/json" })
	public String GeozoneDelete(@RequestBody GeoZoneModel geoZoneModel, ModelAndView model,
			@PathVariable("sessionID") String sessionID, @PathVariable("uuID") String uuID) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "common.GeozoneDelete";

		action = "Delete";
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

		log.info("logged our session Id" + sessionID);

		String json = JSONUtility.convertObjectToJson(geoZoneModel);
		log.info("Request Json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			ErrorMessage = "Input Is Invalid";
			StatusCode = "WERR05";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			try {

				if (!((user.getRoleType().equals("parent_admin")) || (user.getRoleType().equals("school_teacher")))) {
					StatusCode = "ERR03";
					ErrorMessage = "Unauthorised User ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}

				Integer deviceId = deviceService.getDeviceIdByUuidandUser(user, uuID);
				log.info("deviceId" + deviceId);
				if (deviceId == 0) {

					StatusCode = "ERR06";
					ErrorMessage = "Invalid Device";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
				List<Integer> zoneIdList = new ArrayList<Integer>();
				int geoZoneId = geoZoneModel.getGeozone_id();
				List<GeoZoneModel> zonessList = commonService.getUserGeoZones(user.getId(), uuID);
				log.info("after");
				for (GeoZoneModel zones : zonessList) {
					zoneIdList.add(zones.getGeozone_id());
				}
				if (!zoneIdList.contains(geoZoneId)) {
					StatusCode = "ERR";
					ErrorMessage = "Invalid Zone id ";
					responseJSON = ErrorCodesUtil.displaySuccessJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
				if (geoZoneModel.getGeozone_id() > 0) {

					ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);

					commonService.deleteGeozone(geoZoneId);
					activityLogs.info(activityLog);
					StatusCode = "SUC01";
					ErrorMessage = "API Request Success";
					responseJSON = ErrorCodesUtil.displaySuccessJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}

			} catch (Exception e) {
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			activityLogs.error(activityLog);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/ClassRemindersCreate/{sessionID}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String ClassRemindersCreate(HttpServletRequest httpReq, @PathVariable("sessionID") String sessionID) {
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String OperationType = "Create";
		boolean operationStatus = false;
		String type = "common.ClassRemindersCreate";
		File f = null;
		if (httpReq.getParameterNames().hasMoreElements()) {
			String element = httpReq.getParameterNames().nextElement();
			log.info("Element" + "\t" + element);
		}

		log.info("httpReq.getRequestURI()" + "\t" + httpReq.getRequestURI());
		String comments = httpReq.getParameter("comments");
		log.info("comments" + "\t" + comments);
		String reminder_id = httpReq.getParameter("reminder_id");
		String delete_icon = httpReq.getParameter("delete_icon");
		FCMModel fcmModel = new FCMModel();

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

		if (null != reminder_id)
			OperationType = "Update";

		log.info("logged our session Id" + sessionID);

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);
			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}
			try {
				if (!(user.getRoleType().equals("school_teacher"))) {
					StatusCode = "ERR03";
					ErrorMessage = "Unauthorised User ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					return responseJSON;
				} else {
					String contentType = httpReq.getContentType();
					if (null != contentType && null != comments) {
						log.info("contentType in ClassRemindersCreate: " + contentType);
						MultipartHttpServletRequest multiHttpReq = (MultipartHttpServletRequest) httpReq;
						MultipartFile multipartFile = null;
						if (multiHttpReq.getFileMap().entrySet() != null) {
							Set set = multiHttpReq.getFileMap().entrySet();
							java.util.Iterator i = set.iterator();

							while (i.hasNext()) {
								Map.Entry me = (Map.Entry) i.next();
								String fileName = (String) me.getKey();
								multipartFile = (MultipartFile) me.getValue();
								log.info("Original fileName - " + multipartFile.getOriginalFilename());
								log.info("fileName - " + fileName);
							}
						}

						if (null != multipartFile && !(multipartFile.isEmpty())) {
							try {
								if (multiHttpReq.getFileMap().entrySet() != null) {
									try {
										DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
										Date currDate = new Date();
										Date d = df.parse(df.format(currDate));
										DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
										DateFormat time = new SimpleDateFormat("HH:mm:ss");
										String replacehyfenInDate = date.format(d).replace("-", "");
										String replaceColonInTime = time.format(d).replace(":", "");
										String finalDateTime = replacehyfenInDate + replaceColonInTime;
										String orgFileNameWithExt = multipartFile.getOriginalFilename();
										log.debug("orgFileNameWithExt :::::::::::::::::::::::::::: "
												+ orgFileNameWithExt);
										String filenameAndExt[] = orgFileNameWithExt.split("\\.");

										f = new File(this.configProperties.getProperty("reminders.upload.path") + "/"
												+ user.getId() + "_" + user.getAccountId() + "_" + finalDateTime + "."
												+ filenameAndExt[1]);

									} catch (ParseException e) {
										log.debug("parserException ::: ", e);
									}
									double bytes = f.length();
									double kilobytes = (bytes / 1024);
									log.info("f.getName().toUpperCase(): " + f.getName().toUpperCase());
									if ((f.getName().toUpperCase().endsWith(".JPG")
											|| f.getName().toUpperCase().endsWith(".JPEG")
											|| f.getName().toUpperCase().endsWith(".PNG")) && (kilobytes < 150)) {
										f.createNewFile();
										multipartFile.transferTo(f);

									} else {
										String statusCode = "ERR19";
										String errorMessage = "File not suported ";
										responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
										return responseJSON;
									}
								}
							} catch (Exception e) {
								return "You failed to upload " + e.getMessage();
							}
						}
						log.info("Before Reminders");
						String eventName = null;
						Reminders reminder = new Reminders();
						if (null != comments)
							reminder.setComments(comments);

						ClassGrade cg = this.commonService.findClassGrade(user.getId());
						if (cg != null) {
							reminder.setClassGrade(cg);
						}
						if (f != null) {
							reminder.setImage(f.getName());
						}
						if (OperationType.equals("Create")) {
							log.info("Into type Create()");
							ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, OperationType, summary,
									ipaddress);
							reminder.setCreatedDate(new Date());
							reminder.setUpdatedDate(new Date());
							commonService.createReminder(reminder);
							activityLogs.info(activityLog);
							operationStatus = true;

							eventName = deviceService.getEventNamebyId(5);
							fcmModel.setBody(eventName + " Occured at " + new Date().toString());

						} else {
							Reminders Oldreminder = commonService.getReminderByID(Integer.parseInt(reminder_id));
							if (null != comments)
								Oldreminder.setComments(comments);
							if (f != null) {
								Oldreminder.setImage(f.getName());
							} else if (delete_icon != null && delete_icon.toUpperCase().equals("YES"))
								Oldreminder.setImage(null);
							reminder.setUpdatedDate(new Date());
							ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, OperationType, summary,
									ipaddress);

							commonService.updateReminder(Oldreminder);
							operationStatus = true;
							activityLogs.info(activityLog);
							eventName = deviceService.getEventNamebyId(5);
							fcmModel.setBody(eventName + " Occured at " + new Date().toString());
						}

						if (operationStatus) {
							Integer cgId = reminder.getClassGrade().getClassGradeId();
							log.info("cgId" + "\t" + cgId);
							List<ValidParentUsersWithTokenTransform> usersList = commonService
									.getParentUsersListForReminders(cgId);
							for (ValidParentUsersWithTokenTransform parentUser : usersList) {
								log.info("Parent Userid" + parentUser.getUserId());
								String fcmTitle = eventName + ": " + parentUser.getStudentName();
								fcmModel.setTitle(fcmTitle);
								if (null != parentUser.getAndroidAppToken() && !parentUser.getAndroidAppToken().equals("")){
									FCMUtility.PushNotfication(ANDROID_SERVER_KEY, parentUser.getAndroidAppToken(), fcmModel);
								}
								if (null != parentUser.getiPhoneAppToken() && !parentUser.getiPhoneAppToken().equals("")){
									FCMUtility.PushNotfication(APPLE_SERVER_KEY, parentUser.getiPhoneAppToken(), fcmModel);
								}
							}
							String statusCode = "SUC01";
							String errorMessage = "API Request Success";
							responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
						} else {
							responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorDataMessage,
									Constant.StatusCodeJon, type);
						}
					} else {
						responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorDataMessage,
								Constant.StatusCodeJon, type);
					}
				}
			} catch (Exception e) {
				log.debug("Exception inside ClassRemindersCreate: ", e);
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, OperationType, summary, ipaddress);
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorDataMessage, Constant.StatusCodeJon, type);
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, OperationType, summary, ipaddress);
			activityLogs.error(activityLog);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/ClassRemindersDelete/{sessionID}/{reminderId}", method = RequestMethod.DELETE, produces = {
			"application/json" })
	public String ClassRemindersDelete(@PathVariable("sessionID") String sessionID,
			@PathVariable("reminderId") int reminderId) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;

		String type = "common.ClassRemindersDelete";
		log.info("logged our session Id" + sessionID);

		action = "Delete";
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

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			try {
				if (!((user.getRoleType().equals("school_teacher")))) {
					StatusCode = "ERR03";
					ErrorMessage = "Unauthorised User ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
				String fileName = commonService.getImageNameByReminderId(reminderId);
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				commonService.deleteReminder(reminderId);
				activityLogs.info(activityLog);
				if (null != fileName) {
					try {

						File f = new File(this.configProperties.getProperty("reminders.upload.path") + "/" + fileName);

						if (f != null)
							f.delete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				StatusCode = "SUC01";
				ErrorMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displaySuccessJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;

			} catch (Exception e) {
				log.info("entered into catch block");
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			activityLogs.error(activityLog);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/ClassRemindersList/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String ClassRemindersList(@PathVariable("sessionID") String sessionID) {
		List myList = new ArrayList<>();
		Set<String> set = new HashSet<>();
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String jsonString = null;
		String type = "common.ClassRemindersList";
		log.info("logged our session Id" + sessionID);
		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			} else if (!user.getRoleType().equals("school_teacher")) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			} else {
				List<RemindersTransform> reminderTransformList = this.commonService.getReminders(user);

				for (RemindersTransform reminder : reminderTransformList) {
					log.info("inside for loop for reminders");
					log.info("reminder.getStClass()" + "\t" + reminder.getStClass());
					set.add(String.valueOf(reminder.getStClass()));
				}

				for (String s : set) {
					log.info("Into Set For Loop");
					log.info("String **" + "\t" + s);
					Map<Object, Object> map1 = new LinkedHashMap<>();
					List l = new ArrayList<>();
					String classId = "Class";
					for (RemindersTransform rt : reminderTransformList) {
						String stClass = String.valueOf(rt.getStClass());
						if (stClass.equals(s)) {
							Map<Object, Object> map = new LinkedHashMap<>();
							if (rt.getReminderId() != null)
								map.put("reminder_id", rt.getReminderId());
							if (rt.getSchoolId() != null)
								map.put("school_id", rt.getSchoolId());
							if (rt.getStClass() != null)
								map.put("class", rt.getStClass());
							if (rt.getComments() != null)
								map.put("comments", rt.getComments());
							if (rt.getImage() != null)
								map.put("filename",
										this.configProperties.getProperty("downloads.url")
												+ this.configProperties.getProperty("reminders.download.path") + "/"
												+ rt.getImage());

							if (rt.getCreatedDt() != null)
								map.put("created_date", rt.getCreatedDt().toString());
							l.add(map);
						}
					}
					map1.put(classId, l);
					jsonString = JSONObject.toJSONString(map1);
					myList.add(jsonString);
					log.info("myList" + "\n" + myList);
				}
				StatusCode = "SUC01";
				ErrorMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displaySuccessJSONForReminders(type, ErrorMessage, StatusCode,
						myList.toString());
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/StudentRewardsByDate/{sessionID}/{student_id}/{date}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentRewardsByDate(@PathVariable String sessionID, @PathVariable int student_id,
			@PathVariable("date") String date) throws Exception {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "common.StudentRewardsByDate";
		Map<Object, Object> map = null;
		String jsonString = null;
		List myList = new ArrayList<>();

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.DisplayErrorMessage, Constant.DisplayStatusCode,
						Constant.UserDetailsType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			List<Integer> validStudents = this.commonService.getKididsForParent(user.getId());

			if (!(validStudents.size() > 0) || !(validStudents.contains(student_id))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}

			if (user.getRoleType().equals(Constant.ParentAdmin) || user.getRoleType().equals(Constant.ParentMember)) {

				List<StudentRewardsTransform> studentRewardsList = this.commonService
						.getStudentsRewardsByDate(user.getId(), student_id, date);

				if (studentRewardsList != null) {
					for (StudentRewardsTransform srt : studentRewardsList) {
						map = new LinkedHashMap<>();
						if (srt.getRewardId() != null) {
							map.put("reward_id", srt.getRewardId());
						}
						if (srt.getRewardname() != null) {
							map.put("reward_name", srt.getRewardname());
						}
						if (srt.getCategoryName() != null) {
							map.put("category_name", srt.getCategoryName());
						}
						if (srt.getReceivedCount() != null) {
							map.put("received_count", srt.getReceivedCount());
						}
						if (srt.getCategoryIconUrl() != null) {
							map.put("category_icon_url",
									this.configProperties.getProperty("downloads.url")
											+ this.configProperties.getProperty("rewards.download.path") + "/"
											+ srt.getCategoryIconUrl());
						}
						if (srt.getReward_icon_url() != null) {
							map.put("reward_icon_url",
									this.configProperties.getProperty("downloads.url")
											+ this.configProperties.getProperty("rewards.download.path") + "/"
											+ srt.getReward_icon_url());
						}
						if (srt.getSchool_teacher() != null) {
							map.put("teacher", srt.getSchool_teacher());
						}

						if (srt.getReward_date() != null) {
							map.put("reward_date", srt.getReward_date().toString());
						}
						jsonString = JSONObject.toJSONString(map);
						myList.add(jsonString);
					}

					log.info("***myList***" + "\n" + myList);
					StatusCode = "SUC01";
					String studentRewardsJson = ErrorCodesUtil.displayJSONForStudentRewardsList("SUC01",
							"API Request Success", myList.toString());
					log.info("***studentRewardsJson***" + "\t" + studentRewardsJson);
					return studentRewardsJson;
				} else {
					log.info("***myList***" + "\n" + myList);
					StatusCode = "";
					String studentRewardsJson = ErrorCodesUtil.displayJSONForStudentRewardsList("SUC01",
							"API Request Success", myList.toString());
					log.info("***studentRewardsJson***" + "\t" + studentRewardsJson);
					return studentRewardsJson;
				}

			} else {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user, failed to authenticate";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;

		}
	}

	@RequestMapping(value = "/reminderslist")
	public ModelAndView listReminders(ModelAndView model,
			@ModelAttribute("RemindersModel") RemindersModel remindersModel, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		if ((null == this.httpSession.getAttribute("currentUser"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		if (((this.httpSession.getAttribute("currentUser") != null))
				&& !((this.httpSession.getAttribute("currentUser").equals("school_admin"))
						|| (this.httpSession.getAttribute("currentUser").equals("school_teacher"))
						|| (this.httpSession.getAttribute("currentUser").equals("parent_admin")))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		String timeLine = request.getParameter("timeLine");
		String students = request.getParameter("students");
		String classes = request.getParameter("classes");
		String teacher = request.getParameter("teacher");
		int teacher_id = 0;

		log.info("students after getting class : " + students);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");

		List<String> daystoList = new ArrayList<>();
		daystoList.add("CurrentDay");
		daystoList.add("YesterDay");
		daystoList.add("Last 1 Week");
		daystoList.add("Last 30 Days");
		model.addObject(Constant.FirstName, user.getName());
		if (currentUser.equals("parent_admin")) {
			if (students != null && !students.equals("NONE")) {
				students = commonService.getStudentClassByParentid(students, user.getAccountId());
			}
			List<String> studentsList = commonService.getKidsForParent(user.getId());
			List<RemindersModel> RemindersList = this.commonService.getRemindersForParent(user.getId(), timeLine,
					students);
			model.addObject("RemindersList", RemindersList);
			model.addObject("studentsList", studentsList);
			model.addObject("daystoList", daystoList);
			model.addObject("sessionID", sessionID);
			model.addObject("currentUser", "parent_admin");
			model.setViewName("RemindersViewForParent");
		} else if (currentUser.equals("school_teacher")) {
			List<String> classList = commonService.getClassesForTeacher(user.getId());
			List<RemindersModel> RemindersList = this.commonService.getRemindersForTeacher(user.getId(), timeLine,
					classes);
			model.addObject("RemindersList", RemindersList);
			model.addObject("classList", classList);
			model.addObject("daystoList", daystoList);
			model.addObject("sessionID", sessionID);
			model.addObject("currentUser", "school_teacher");
			model.setViewName("RemindersViewForTeacher");
		} else if (currentUser.equals("school_admin")) {
			List<String> teacherList = commonService.getTeachersForSchoolAdmin(user.getAccountId());
			List<Integer> teacheridList = commonService.getTeacherIdsForSchoolAdmin(user.getAccountId());

			if (teacher != null && !teacher.equals("NONE")) {
				teacher_id = commonService.getTeacherIdbyNameandAccount(teacher, user.getAccountId());
				log.info("teacher_id is : " + teacher_id);
			}
			List<RemindersModel> RemindersList = commonService.getRemindersForSchoolAdmin(teacheridList, timeLine,
					teacher_id, teacher);
			model.addObject("RemindersList", RemindersList);
			model.addObject("teacherList", teacherList);
			model.addObject("daystoList", daystoList);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.addObject("currentUser", "school_admin");
			model.setViewName("RemindersViewForAdmin");
		}

		return model;
	}

	@RequestMapping(value = "/web/ParentDashboardNotifications/{sessionID}/{student_id}/{notificationtype}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String ParentDashboardNotifications(@PathVariable String sessionID, @PathVariable int student_id,
			@PathVariable String notificationtype) throws Exception {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "common.ParentDashboardNotifications";
		Map<Object, Object> outerMap = null;
		String jsonString = null;
		List myList = new ArrayList<>();

		List<Integer> validStudentsList = new ArrayList<>();

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			if (user.getRoleType().equals("parent_admin")) {

				List<TeachersStudentsTransform> tsTransformList = this.parentService.viewKidsList(user.getId(), null);
				for (TeachersStudentsTransform kid : tsTransformList) {
					validStudentsList.add(kid.getStudentId());
				}

				if (!validStudentsList.contains(student_id)) {
					StatusCode = "ERR01";
					ErrorMessage = "Invalid user, failed to authenticate";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					return responseJSON;
				}
				SchoolTimeModel schoolTimeModel = null;
				LatestGeoFenceAlertModel latestgeofenceModel = null;
				DeviceTypeAlertLatestModel deviceAlertModel = null;
				LatestReminderModel latestReminderModel = null;
				LatestAnnoncementModel latestAnnouncementModel = null;
				LatestRewardsModel latestRewardModel = null;
				LatestGPSLocationModel latestGPSLocationModel = null;

				if (notificationtype.equals("dashboard")) {
					schoolTimeModel = commonService.getSchoolTimelatestEvent(student_id);
					latestgeofenceModel = commonService.getGeofencelatestEvent(student_id);
					deviceAlertModel = commonService.getDeviceAlertslatestEvent(student_id);
					latestReminderModel = commonService.getLatestReminders(student_id);
					latestAnnouncementModel = commonService.getLatestAnnouncements(student_id);
					latestRewardModel = commonService.getLatestRewards(student_id);
				} else {
					schoolTimeModel = commonService.getSchoolTimelatestEvent(student_id);
					latestgeofenceModel = commonService.getGeofencelatestEvent(student_id);
					deviceAlertModel = commonService.getDeviceAlertslatestEvent(student_id);
					latestGPSLocationModel = commonService.getLatestGPSLocations(student_id);
				}
				String AlertType = "";
				if (null != deviceAlertModel) {

					if (null != deviceAlertModel) {
						log.info("deviceAlertModel.getEventid()" + deviceAlertModel.getEventid());
						switch (deviceAlertModel.getEventid()) {
						case 13:
							AlertType = "SOS";
							break;
						case 14:
							AlertType = "SOS Removing";
							break;
						case 15:
							AlertType = "Fall Detection";
							break;
						case 16:
							AlertType = "Abnormal Vital Sign";
							break;
						case 17:
							AlertType = "Sensor Malfunction";
							break;
						case 18:
							AlertType = "Low Battery level Notification";
							break;
						case 19:
							AlertType = "GPS Location ";
							break;
						case 20:
							AlertType = "Band Removal Alert";
							break;
						case 21:
							AlertType = "Band Back Alert";
							break;
						}
					}
				}

				outerMap = new LinkedHashMap<>();

				Map<Object, Object> schooltimeMap = new LinkedHashMap<>();
				Map<Object, Object> geofenceMap = new LinkedHashMap<>();
				Map<Object, Object> alertMap = new LinkedHashMap<>();
				Map<Object, Object> remindersMap = new LinkedHashMap<>();
				Map<Object, Object> rewardsMap = new LinkedHashMap<>();
				Map<Object, Object> anouncementsMap = new LinkedHashMap<>();
				Map<Object, Object> gpsLocationMap = new LinkedHashMap<>();

				if (notificationtype.equals("dashboard")) {

					if (null != schoolTimeModel.getEnterDate())
						schooltimeMap.put("entry_time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(schoolTimeModel.getEnterDate()));
					else
						schooltimeMap.put("entery_time", "");

					if (null != schoolTimeModel.getEntry_gps_location())
						schooltimeMap.put("entry_gps", schoolTimeModel.getEntry_gps_location());
					else
						schooltimeMap.put("entery_gps", "");

					if (null != schoolTimeModel.getExitDate())
						schooltimeMap.put("exit_time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(schoolTimeModel.getExitDate()));
					else
						schooltimeMap.put("exit_time", "");

					if (null != schoolTimeModel.getExit_gps_location())
						schooltimeMap.put("exit_gps", schoolTimeModel.getExit_gps_location());
					else
						schooltimeMap.put("exit_gps", "");

					if (null != latestgeofenceModel.getEnterDate())
						geofenceMap.put("entry_time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(latestgeofenceModel.getEnterDate()));
					else
						geofenceMap.put("entery_time", "");

					if (null != latestgeofenceModel.getEntry_gps_location())
						geofenceMap.put("entry_gps", latestgeofenceModel.getEntry_gps_location());
					else
						geofenceMap.put("entry_gps", "");

					if (null != latestgeofenceModel.getExitDate() 
							&& null != latestgeofenceModel.getOutTime()) {
						String geofenceExitDate = "";
						geofenceExitDate = new SimpleDateFormat(this.configProperties.getProperty("display.dateFormat"))
								.format(latestgeofenceModel.getExitDate()) + " "
								+ latestgeofenceModel.getOutTime().toString();
						geofenceMap.put("exit_time",  geofenceExitDate);
					} else {
						geofenceMap.put("exit_time",  "");
					}

					if (null != latestgeofenceModel.getExit_gps_location())
						geofenceMap.put("exit_gps", latestgeofenceModel.getExit_gps_location());
					else
						geofenceMap.put("exit_gps", "");

					alertMap.put("type", AlertType);
					if (null != deviceAlertModel && null != deviceAlertModel.getEventOccuredDate())
						alertMap.put("time", new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
								.format(deviceAlertModel.getEventOccuredDate()));
					else
						alertMap.put("time", "");

					if (null != deviceAlertModel && null != deviceAlertModel.getGps_location())
						alertMap.put("alert_gps", deviceAlertModel.getGps_location());

					else
						alertMap.put("alert_gps", "");

					if (null != latestReminderModel && null != latestReminderModel.getComments())
						remindersMap.put("title", latestReminderModel.getComments());
					else
						remindersMap.put("title", "");

					if (null != latestReminderModel && null != latestReminderModel.getLatestDate())
						remindersMap.put("time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(latestReminderModel.getLatestDate()));
					else
						remindersMap.put("time", "");

					if (null != latestRewardModel && null != latestRewardModel.getRewardCategory())
						rewardsMap.put("category", latestRewardModel.getRewardCategory());
					else
						rewardsMap.put("category", "");

					if (null != latestRewardModel && null != latestRewardModel.getRewardName())
						rewardsMap.put("reward", latestRewardModel.getRewardName());
					else
						rewardsMap.put("reward", "");

					if (null != latestRewardModel)
						rewardsMap.put("Count", latestRewardModel.getRewardsCount());
					else
						rewardsMap.put("Count", "");

					if (null != latestAnnouncementModel && null != latestAnnouncementModel.getName())
						anouncementsMap.put("title", latestAnnouncementModel.getName());
					else
						anouncementsMap.put("title", "");

					if (null != latestAnnouncementModel && null != latestAnnouncementModel.getLatestDate())
						anouncementsMap.put("time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(latestAnnouncementModel.getLatestDate()));
					else
						anouncementsMap.put("time", "");
				} else {
					if (null != schoolTimeModel.getEnterDate())
						schooltimeMap.put("entry_time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(schoolTimeModel.getEnterDate()));
					else
						schooltimeMap.put("entry_time", "");

					if (null != schoolTimeModel.getEntry_gps_location())
						schooltimeMap.put("entry_gps", schoolTimeModel.getEntry_gps_location());
					else
						schooltimeMap.put("entry_gps", "");

					if (null != schoolTimeModel.getExitDate())
						schooltimeMap.put("exit_time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(schoolTimeModel.getExitDate()));
					else
						schooltimeMap.put("exit_time", "");

					if (null != schoolTimeModel.getExit_gps_location())
						schooltimeMap.put("exit_gps", schoolTimeModel.getExit_gps_location());
					else
						schooltimeMap.put("exit_gps", "");

					if (null != latestgeofenceModel.getEnterDate())
						geofenceMap.put("entry_time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(latestgeofenceModel.getEnterDate()));
					else
						geofenceMap.put("entry_time", "");

					if (null != latestgeofenceModel.getEntry_gps_location())
						geofenceMap.put("entry_gps", latestgeofenceModel.getEntry_gps_location());
					else
						geofenceMap.put("entry_gps", "");

					if (null != latestgeofenceModel.getExitDate())
						geofenceMap.put("exit_time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(latestgeofenceModel.getExitDate()));
					else
						geofenceMap.put("exit_time", "");

					if (null != latestgeofenceModel.getExit_gps_location())
						geofenceMap.put("exit_gps", latestgeofenceModel.getExit_gps_location());
					else
						geofenceMap.put("exit_gps", "");

					alertMap.put("type", AlertType);

					if (null != deviceAlertModel && null != deviceAlertModel.getEventOccuredDate())
						alertMap.put("time", new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
								.format(deviceAlertModel.getEventOccuredDate()));
					else
						alertMap.put("time", "");
					if (null != deviceAlertModel && null != deviceAlertModel.getGps_location())
						alertMap.put("alert_gps", deviceAlertModel.getGps_location());

					else
						alertMap.put("alert_gps", "");

					gpsLocationMap.put("type", "GPS Location");

					if (null != latestGPSLocationModel && null != latestGPSLocationModel.getEventOccuredDate())
						gpsLocationMap.put("time",
								new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
										.format(latestGPSLocationModel.getEventOccuredDate()));
					else
						gpsLocationMap.put("time", "");
					if (null != latestGPSLocationModel && null != latestGPSLocationModel.getGps_location())
						gpsLocationMap.put("location_gps", latestGPSLocationModel.getGps_location());

					else
						gpsLocationMap.put("location_gps", "");

				}

				if (notificationtype.equals("dashboard")) {
					outerMap.put("schooltime", schooltimeMap);
					outerMap.put("geozone", geofenceMap);
					outerMap.put("alert", alertMap);
					outerMap.put("reminder", remindersMap);
					outerMap.put("reward", rewardsMap);
					outerMap.put("announcement", anouncementsMap);

				} else {
					outerMap.put("schooltime", schooltimeMap);
					outerMap.put("geozone", geofenceMap);
					outerMap.put("alert", alertMap);
					outerMap.put("location", gpsLocationMap);
				}

				jsonString = JSONObject.toJSONString(outerMap);
				myList.add(jsonString);

				log.info("***myList***" + "\n" + myList);

				StatusCode = "";
				String studentRewardsJson = ErrorCodesUtil.ParentDashboardNotifications("SUC01", "API Request Success",
						myList.toString());
				log.info("***studentRewardsJson***" + "\t" + studentRewardsJson);
				return studentRewardsJson;

			} else {
				StatusCode = "ERR01";
				ErrorMessage = "Invalid user, failed to authenticate";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}
		} else {

			StatusCode = "ERR03";
			ErrorMessage = "Unauthorised User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;

		}
	}

	@RequestMapping(value = "/web/KidsSafetyNotifications/{sessionID}/{student_id}/{inputdate}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String KidsSaftyDashboardNotifications(@PathVariable String sessionID, @PathVariable int student_id,
			@PathVariable String inputdate) throws Exception {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "common.KidsSaftyDashboardNotifications";
		Map<Object, Object> outerMap = null;
		String jsonString = null;
		List myList = new ArrayList<>();

		List<Integer> validStudentsList = new ArrayList<>();
		Map<Integer, List<KidsSafeModel>> kidSafeMap = new HashMap<Integer, List<KidsSafeModel>>();

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			if (user.getRoleType().equals("parent_admin")) {
				List<TeachersStudentsTransform> tsTransformList = this.parentService.viewKidsList(user.getId(), null);

				for (TeachersStudentsTransform kid : tsTransformList) {
					validStudentsList.add(kid.getStudentId());
				}

				if (!validStudentsList.contains(student_id)) {
					StatusCode = "ERR03";
					ErrorMessage = "Unauthorised User";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					return responseJSON;
				}

				List<KidsSafeModel> kidSafeList = commonService.getKidsSafeNotificationData(student_id, inputdate);
				log.info("size is :" + kidSafeList.size());

				for (KidsSafeModel kid : kidSafeList) {
					if (!kidSafeMap.containsKey(kid.getEvent_id())) {
						List<KidsSafeModel> list = new ArrayList<KidsSafeModel>();
						list.add(kid);
						kidSafeMap.put(kid.getEvent_id(), list);
					} else {
						kidSafeMap.get(kid.getEvent_id()).add(kid);
					}

				}

				outerMap = new LinkedHashMap<>();

				Map<Object, Object> schooltimeMap = new LinkedHashMap<>();
				List<Object> schooltimeentryList = new ArrayList<Object>();
				List<Object> schooltimeexitList = new ArrayList<Object>();
				Map<Object, Object> geofenceMap = new LinkedHashMap<>();
				List<Object> geofenceentryList = new ArrayList<Object>();
				List<Object> geofenceexitList = new ArrayList<Object>();
				List<Object> alertList = new ArrayList<Object>();
				List<Object> gpslocationList = new ArrayList<Object>();

				Iterator<Map.Entry<Integer, List<KidsSafeModel>>> mapIterator = kidSafeMap.entrySet().iterator();

				while (mapIterator.hasNext()) {
					log.info("entered into while loop");
					Map.Entry<Integer, List<KidsSafeModel>> pair = mapIterator.next();
					List<KidsSafeModel> tempList = pair.getValue();

					for (KidsSafeModel tempModel : tempList) {
						Map<Object, Object> schooltimeentryMap = new LinkedHashMap<>();
						if (tempModel.getEvent_id() == SCHOOL_ENTRY_ID) {
							schooltimeentryMap.put("time",
									new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
											.format(tempModel.getEventOccuredDate()));
							schooltimeentryMap.put("gps", tempModel.getGps_location());
							schooltimeentryList.add(schooltimeentryMap);

						} else if (tempModel.getEvent_id() == SCHOOL_EXIT_ID) {
							Map<Object, Object> schooltimeexitMap = new LinkedHashMap<>();
							schooltimeexitMap.put("time",
									new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
											.format(tempModel.getEventOccuredDate()));
							schooltimeexitMap.put("gps", tempModel.getGps_location());
							schooltimeexitList.add(schooltimeexitMap);

						} else if (tempModel.getEvent_id() == GEOFENCE_ENTRY_ID) {
							Map<Object, Object> geofenceentryMap = new LinkedHashMap<>();
							geofenceentryMap.put("time",
									new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
											.format(tempModel.getEventOccuredDate()));
							geofenceentryMap.put("gps", tempModel.getGps_location());
							geofenceentryList.add(geofenceentryMap);
							
							// populate geofenceExitMap here as the data with event ID "GEOFENCE_EXIT_ID"
							// will not exist as a separate row, since only the out time value in 
							// "out_time" column is updated for geofence exit event 
							// in geofence entry record itself.
							Map<Object, Object> geofenceexitMap = new LinkedHashMap<>();
							String exitTime = new SimpleDateFormat(this.configProperties.getProperty("display.dateFormat"))
									.format(tempModel.getEventOccuredDate())
									+ " " + tempModel.getOutTime().toString();
							geofenceexitMap.put("time", exitTime);
							geofenceexitMap.put("gps", tempModel.getGps_location());
							geofenceexitList.add(geofenceexitMap);

						} else if (tempModel.getEvent_id() == GEOFENCE_EXIT_ID) {
							Map<Object, Object> geofenceexitMap = new LinkedHashMap<>();
							geofenceexitMap.put("time",
									new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
											.format(tempModel.getEventOccuredDate()));
							geofenceexitMap.put("gps", tempModel.getGps_location());
							geofenceexitList.add(geofenceexitMap);

						} else if (tempModel.getEvent_id() == STUDENT_L_ID) {
							Map<Object, Object> gpslocationMap = new LinkedHashMap<>();
							gpslocationMap.put("time",
									new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
											.format(tempModel.getEventOccuredDate()));
							gpslocationMap.put("gps", tempModel.getGps_location());
							gpslocationList.add(gpslocationMap);

						} else {
							log.info("deviecMap" + deviveMap);
							log.info("event is :" + tempModel.getEvent_id().toString());
							log.info("event is :" + tempModel.getEvent_id());
							Map<Object, Object> alertinnermap = new LinkedHashMap<>();
							alertinnermap.put("type", deviveMap.get(tempModel.getEvent_id().toString()));
							alertinnermap.put("time",
									new SimpleDateFormat(this.configProperties.getProperty("display.dateTime"))
											.format(tempModel.getEventOccuredDate()));
							alertinnermap.put("gps", tempModel.getGps_location());
							alertList.add(alertinnermap);
						}
					}

					schooltimeMap.put("entry", schooltimeentryList);
					schooltimeMap.put("exit", schooltimeexitList);
					geofenceMap.put("entry", geofenceentryList);
					geofenceMap.put("exit", geofenceexitList);
				}

				outerMap.put("schooltime", schooltimeMap);
				outerMap.put("geofence", geofenceMap);
				outerMap.put("alert", alertList);
				outerMap.put("gpslocation", gpslocationList);

				jsonString = JSONObject.toJSONString(outerMap);
				myList.add(jsonString);

				log.info("***myList***" + "\n" + myList);

				StatusCode = "";
				String studentRewardsJson = ErrorCodesUtil.KidsSaftyNotifications("SUC01", "API Request Success",
						jsonString);
				log.info("***studentRewardsJson***" + "\t" + studentRewardsJson);
				return studentRewardsJson;

			} else {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;

			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user, failed to authenticate";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;

		}
	}

	@RequestMapping(value = "/mobile/kidRemindersList/{token}/{studentId}/{date}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String kidRemindersList(@PathVariable("token") String token, @PathVariable("studentId") int studentId,
			@PathVariable("date") String date) {
		log.info("Entering Into kidRemindersList {");

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String responseJSON = null;
		String remindersJson = null;
		List remindersL = new ArrayList<>();
		Map<String, String> remindersMap = null;
		Users user = userService.validateUserBySession(token);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
			} else {
				if (user.getRoleType().equals("parent_admin")) {
					List<RemindersModel> remindersList = this.commonService.findRemindersListForParent(user.getId(),
							date, studentId);
					for (RemindersModel rm : remindersList) {
						remindersMap = new LinkedHashMap<>();
						if (String.valueOf(rm.getReminderId()) != null)
							remindersMap.put("reminder_id", String.valueOf(rm.getReminderId()));
						if (String.valueOf(rm.getSchool_id()) != null)
							remindersMap.put("school_id", String.valueOf(rm.getSchool_id()));
						if (rm.getStudentClass() != null)
							remindersMap.put("class", String.valueOf(rm.getStudentClass()));
						if (rm.getComments() != null)
							remindersMap.put("comments", rm.getComments());
						if (rm.getImage() != null)
							remindersMap.put("filename",
									this.configProperties.getProperty("downloads.url")
											+ this.configProperties.getProperty("reminders.download.path") + "/"
											+ rm.getImage());
						if (rm.getCreatedDate().toString() != null)
							remindersMap.put("created_date", rm.getCreatedDate().toString());
						remindersJson = JSONObject.toJSONString(remindersMap);
						remindersL.add(remindersJson);
					}
					log.info("remindersL" + "\t" + remindersL);
					responseJSON = ErrorCodesUtil.displayRemindersJSONForParent("API Request Success", "SUC01",
							remindersL.toString());
					// JSONUtility.respondAsJSON(response, kidsRespJson);
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "common.kidRemindersList";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
				}
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "common.kidRemindersList";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		log.info("Exiting From kidRemindersList }");
		return responseJSON;
	}

	@RequestMapping(value = "/web/StudentsRewards/{token}/{student_id}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentsRewards(@PathVariable("token") String token, @PathVariable("student_id") int student_id) {

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String responseJSON = null;
		Map<String, String> map = null;
		String rewardsJson = null;
		List<String> rewardsList = new ArrayList<>();

		Users user = userService.validateUserBySession(token);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
			} else {
				if (user.getRoleType().equals("parent_admin")) {
					List<RewardsCategoryTransform> rctList = this.commonService.findStudentsRewards(student_id);

					for (RewardsCategoryTransform rct : rctList) {
						map = new LinkedHashMap();

						if (rct.getCategory_id() != null)
							map.put("rewards_cateogory_id", String.valueOf(rct.getCategory_id()));
						if (rct.getName() != null)
							map.put("category_name", rct.getName());
						if (rct.getCategory_icon_url() != null) {
							map.put("category_icon_url",
									this.configProperties.getProperty("downloads.url")
											+ this.configProperties.getProperty("rewards.download.path") + "/"
											+ rct.getCategory_icon_url());
						}
						if (rct.getReward_count() != null)
							map.put("reward_category_count", String.valueOf(rct.getReward_category_count()));
						if (rct.getReward_id() != null)
							map.put("reward_id", String.valueOf(rct.getReward_count()));
						if (rct.getName() != null)
							map.put("name", rct.getName());

						if (rct.getReward_icon_url() != null) {
							map.put("reward_icon_url",
									this.configProperties.getProperty("downloads.url")
											+ this.configProperties.getProperty("rewards.download.path") + "/"
											+ rct.getReward_icon_url());
						}

						if (rct.getReward_count() != null)
							map.put("reward_count", String.valueOf(rct.getReward_count()));
						if (rct.getComments() != null)
							map.put("comments", rct.getComments());
						if (rct.getCreated_date() != null)
							map.put("created_date", rct.getCreated_date().toString());

						rewardsJson = JSONObject.toJSONString(map);
						rewardsList.add(rewardsJson);
					}
					log.info("rewardsList" + "\t" + rewardsList);
					responseJSON = ErrorCodesUtil.displayStudentsRewards("API Request Success", "SUC01",
							rewardsList.toString());
					// JSONUtility.respondAsJSON(response, rewardJson);
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "common.StudentsRewards";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
				}
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "common.StudentsRewards";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/grantTeacherAccessToSleepData/{token}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String GrantTeacherAccessToSleepData(@PathVariable("token") String sessionID,
			@RequestBody StudentsSleepDataModel studentsSleepDataModel) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String SuccessMessage = null;
		String type = "device.grantTeacherAccessToSleepData";
		log.debug("logged our session Id" + sessionID);

		Users user = this.userService.validateUserBySession(sessionID);
		if (null != user) {
			log.info("user role" + user.getRoleType());
			if (!(user.getRoleType().equals("parent_admin"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			} else {
				boolean isStudentIdBelongsToParent = this.commonService.checkStudentId(user.getId(),
						studentsSleepDataModel.getStudentId());

				if (studentsSleepDataModel.getAllow_sleep_data() != null
						&& (studentsSleepDataModel.getAllow_sleep_data().toLowerCase().equals("yes")
								|| studentsSleepDataModel.getAllow_sleep_data().toLowerCase().equals("no"))) {
					if (isStudentIdBelongsToParent) {
						this.commonService.updateClassGrade(studentsSleepDataModel);
						StatusCode = "SUC01";
						SuccessMessage = "API Request Success";
						responseJSON = ErrorCodesUtil.displayGrantTeacherAccessToSleepData(type, SuccessMessage,
								StatusCode, "");
					} else {
						StatusCode = "ERR03";
						ErrorMessage = "Unauthorised User ";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						// JSONUtility.respondAsJSON(response, responseJSON);
					}
				} else {
					StatusCode = "ERR05";
					SuccessMessage = "Input is invalid. Allow Sleep Data should be yes/no";
					responseJSON = ErrorCodesUtil.displayGrantTeacherAccessToSleepData(type, SuccessMessage, StatusCode,
							"");
				}
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/web/getGradeClass/{token}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getGradeClass(@PathVariable("token") String token) {

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String responseJSON = null;
		String gradesJSON = null;
		Map<Object, Object> map = null;
		Users user = userService.validateUserBySession(token);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		List<String> gradesList = new ArrayList<>();
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("school_admin")) {
					List<ClassGradeTransform> cgt = this.classGradeService.getGradeClass(user.getAccountId(),
							"school_id", null);
					for (ClassGradeTransform cgTransform : cgt) {
						log.info("1");
						map = new LinkedHashMap<>();
						log.info("2");
						if (cgTransform.getStudentGrade() != null)
							map.put("grade", cgTransform.getStudentGrade());
						log.info("3");
						if (cgTransform.getStudentClass() != null)
							map.put("class", cgTransform.getStudentClass());
						log.info("4");
						gradesJSON = JSONObject.toJSONString(map);
						log.info("gradesJSON" + "\t" + gradesJSON);
						gradesList.add(gradesJSON);
						log.info("gradesList" + "\t" + gradesList.size());
					}
					log.info("getGradeClass: " + "\t" + gradesList);
					responseJSON = ErrorCodesUtil.displayGrades("API Request Success", "SUC01", gradesList.toString());
					// JSONUtility.respondAsJSON(response, responseJSON);

				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "common.getGradeClass";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "common.getGradeClass";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "common.getGradeClass";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/web/displayUnassinedGradeClass/{token}/{teacherId}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String displayUnassinedGradeClass(@PathVariable("token") String token,
			@PathVariable("teacherId") Integer teacherId) {

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String responseJSON = null;
		String gradesJSON = null;
		Map<Object, Object> map = null;
		boolean status = false;
		Users user = userService.validateUserBySession(token);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		List<String> gradesList = new ArrayList<>();
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("school_admin")) {
					List<ClassGradeTransform> cgt = this.classGradeService.getUnassignedGradeClass(user.getAccountId(),
							teacherId);
					for (ClassGradeTransform cgTransform : cgt) {
						log.info("1");
						map = new LinkedHashMap<>();
						log.info("2");
						if (cgTransform.getStudentGrade() != null)
							map.put("grade", cgTransform.getStudentGrade());
						log.info("3");
						if (cgTransform.getStudentClass() != null)
							map.put("class", cgTransform.getStudentClass());
						log.info("4");
						if (cgTransform.getTeacher_id() == null || cgTransform.getTeacher_id().intValue() == 0)
							status = false;
						else if (cgTransform.getTeacher_id().equals(teacherId))
							status = true;

						if (status)
							map.put("status", "assgined");
						else
							map.put("status", "unassgined");

						gradesJSON = JSONObject.toJSONString(map);
						log.info("gradesJSON" + "\t" + gradesJSON);
						gradesList.add(gradesJSON);
						log.info("gradesList" + "\t" + gradesList.size());
					}
					log.info("getGradeClass: " + "\t" + gradesList);
					responseJSON = ErrorCodesUtil.displayGrades("API Request Success", "SUC01", gradesList.toString());
					// JSONUtility.respondAsJSON(response, responseJSON);

				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "common.getGradeClass";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "common.getGradeClass";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "common.getGradeClass";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	@Autowired
	private CassandraService cassandraService;
	@Value("${Timezone}")
	private String timeZone;
	@Value("${date.Timezone}")
	private String dateTimezone;

	@RequestMapping(value = "/mobile/viewStudentSleepData/{token}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String ViewStudentSleepData(@PathVariable("token") String sessionID) throws ParseException {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String SuccessMessage = null;
		String type = "device.viewStudentSleepData";
		log.debug("logged our session Id" + sessionID);

		Users user = this.userService.validateUserBySession(sessionID);
		if (null != user) {

			log.info("user role" + user.getRoleType());
			if (!(user.getRoleType().equals("school_teacher"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}
			log.info("userId" + user.getId());
			List<StudentsListTransform> studentsListTrnsList = this.commonService.findStudentsIdsAndUUIDS(user.getId());
			List<Row> rowsList = null;
			Map map1 = new LinkedHashMap<>();
			List myList = new ArrayList<>();
			if (studentsListTrnsList.size() > 0) {
				String startDate = DateUtil.getYesterdaysDate();
				log.info("startDate >>>>>>  " + startDate);
				String endDate = DateUtil.getTodaysDate();
				log.info("endDate ::::   " + endDate);
				log.info("timeZone >>>>   " + timeZone);
				log.info("dateTimezone ::::   " + dateTimezone);
				String jsonStr = null;
				for (StudentsListTransform studentsListTransform : studentsListTrnsList) {
					int schoolId = this.deviceService.findSchoolIdByUUID(studentsListTransform.getDeviceUuid());
					String keyspace = this.cassandraService.getKeyspaceBySchoolId(schoolId);
					log.info("studentsListTransform.getDeviceUuid() >>>>> " + studentsListTransform.getDeviceUuid());
					rowsList = this.cassandraService.findSleepData(studentsListTransform.getDeviceUuid(),
							DateUtil.convertDateToUnixTime(startDate, timeZone, dateTimezone),
							DateUtil.convertDateToUnixTime(endDate, timeZone, dateTimezone), keyspace);
					log.info("rowList ?????????????????????????????????????????????????????????????  "
							+ rowsList.toString());

					if (rowsList.size() > 0) {
						for (Row row : rowsList) {
							log.info("row uuid ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| "
									+ row.getString("uuid"));
							map1.put("studentId", studentsListTransform.getStudentId());
							map1.put("studentName", studentsListTransform.getStudentName());
							map1.put("uuid", row.getString("uuid"));
							map1.put("duration", row.getInt("duration"));

							jsonStr = JSONObject.toJSONString(map1);
							myList.add(jsonStr);
							log.info("My List Contents::::::::" + myList);
						}
					}
				}

				StatusCode = "SUC01";
				SuccessMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displayViewStudentSleepData(StatusCode, SuccessMessage, type,
						myList.toString());
				return responseJSON;

			} else {
				StatusCode = "SUC01";
				SuccessMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displayViewStudentSleepData(StatusCode, SuccessMessage, type,
						myList.toString());

				return responseJSON;
			}

		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
		}

	}

	@RequestMapping(value = "/mobile/CloseSOSAlert/{token}/{device_event_id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String closeSOSAlert(@PathVariable("token") String token,
			@PathVariable("device_event_id") int device_event_id) throws ParseException {

		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;
		String closeSOSAlertJson = null;
		String type = "device.closeSOSAlert";
		Users user = userService.validateUserBySession(token);

		log.info("user is from mobile sesion:" + user);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);
			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		} else {
			statusCode = Constant.DisplayStatusCode;
			statusMsg = Constant.DisplayErrorMessage;
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			return responseJSON;
		}
		log.info("user Role is : " + user.getRoleType());
		if (user.getRoleType().equals("school_admin")) {
			boolean checkFlagForSchoolUser = this.commonService.checkDeviceEventBelongsToSchoolUsers(device_event_id,
					user.getAccountId());
			log.info(" checkFlagForSchoolUser >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + checkFlagForSchoolUser);
			if (!checkFlagForSchoolUser) {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				return responseJSON;
			}
		}

		if (user.getRoleType().equals("parent_admin")) {
			boolean checkFlagForParentUser = this.commonService.checkDeviceEventBelongsToParentUser(user.getId(),
					device_event_id);
			if (!checkFlagForParentUser) {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				return responseJSON;
			}
		}
		if (user.getRoleType().equals("school_teacher") || user.getRoleType().equals("school_staff")) {
			boolean checkFlagForSchoolUser = this.commonService.checkDeviceEventBelongsToSchoolUsers(device_event_id,
					user.getAccountId());
			if (!checkFlagForSchoolUser) {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				return responseJSON;
			}
		}

		if (user.getRoleType().equals("school_admin") || user.getRoleType().equals("school_teacher")
				|| user.getRoleType().equals("school_staff") || user.getRoleType().equals("parent_admin")) {
			log.info("eneterd   this   ");
			boolean checkDeviceEventExists = this.commonService.checkDeviceEventExists(device_event_id);
			if (!checkDeviceEventExists) {
				statusCode = "ERR01";
				statusMsg = "Device Event doesn't exists";
				ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				return responseJSON;
			}

			Date eventOccuredDate = this.commonService.getDeviceEventOccuredDate(device_event_id);
			log.info("eventOccuredDate >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>    "
					+ eventOccuredDate.toString());
			// start split eventOccuredDateTime to date and time

			DateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = f.parse(eventOccuredDate.toString());
			DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat time = new SimpleDateFormat("HH:mm:ss");
			String eventOccuredextDate = date.format(d);
			String eventOccuredextTime = time.format(d);
			// end ::: split eventOccuredDateTime to date and time

			// start ::: current date - eventOccrentTime in minutes
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date2 = new Date();
			DateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
			String extCurrenTime = currentTime.format(date2);
			Date currentDate = dateFormat.parse(dateFormat.format(date2));
			long durationInMinutes = (currentDate.getTime() - d.getTime()) / 60000;
			// end ::::

			boolean updateFlag = false;

			// Current date is weekday or not
			Date dt = new Date();
			Calendar c = Calendar.getInstance();
			c.setTime(dt);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			System.out.println("dayOfWeek >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + dayOfWeek);
			String[] strDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thusday", "Friday",
					"Saturday" };

			if (strDays[dayOfWeek - 1].equals("Sunday") || strDays[dayOfWeek - 1].equals("Saturday")
					|| this.commonService.checkSchoolHoliday(eventOccuredextDate, user.getAccountId())
					|| this.commonService.checkSchoolHoursWithinRange(eventOccuredextTime, user.getAccountId())) {

				log.info("event occured outside school hours and user attended outside shool hours >>>>>>>");

				String[] currentEventOccuredHoursExt = eventOccuredextTime.split(":");
				String[] currentWorkingHoursExt = extCurrenTime.split(":");

				String duringSchoolHoursButCloseAfterSchoolHoursFlag = "no";

				log.info("currentEventOccuredHoursExt >>>>>>>>>>>>> "
						+ Integer.parseInt(currentEventOccuredHoursExt[0]));
				log.info("currentWorkingHoursExt :::::::::::::::::" + Integer.parseInt(currentWorkingHoursExt[0]));

				String schoolOutHours = this.commonService.getSchoolClosingHours(user.getAccountId());
				String[] schoolHoursEnd = schoolOutHours.split(":");
				log.info("schoolOutHours ::::::::::::::::::::::::::::::::: " + schoolOutHours);

				int schoolHoursMinutes = (Integer.parseInt(schoolHoursEnd[0]) * 60)
						+ (Integer.parseInt(schoolHoursEnd[1]));
				int currentWorkingHoursInMinutes = (Integer.parseInt(currentWorkingHoursExt[0]) * 60)
						+ Integer.parseInt(currentWorkingHoursExt[1]);

				log.info("schoolHoursMinutes :::::::::::::::::::::: " + schoolHoursMinutes);
				log.info("currentWorkingHoursInMinutes :::::::::::::::::::::: " + currentWorkingHoursInMinutes);

				if (schoolHoursMinutes <= currentWorkingHoursInMinutes) {
					log.info("if ::::::: schoolHoursMinutes <= currentWorkingHoursInMinutes  ::::::::::::::::::");
					duringSchoolHoursButCloseAfterSchoolHoursFlag = "no";
				}
				if (schoolHoursMinutes >= currentWorkingHoursInMinutes) {
					log.info("if ::::::: schoolHoursMinutes >= currentWorkingHoursInMinutes  ::::::::::::::::::");
					duringSchoolHoursButCloseAfterSchoolHoursFlag = "no";
				} else {
					log.info("else schoolHoursMinutes not <= currentWorkingHoursInMinutes  ::::::::::::::::::");
					duringSchoolHoursButCloseAfterSchoolHoursFlag = "yes";
				}

				if (this.commonService.checkClosedDeviceEventExists(device_event_id)) {
					updateFlag = this.commonService.updateClosedDeviceEvents(device_event_id, user.getId(),
							user.getRoleType(), (int) durationInMinutes, true,
							duringSchoolHoursButCloseAfterSchoolHoursFlag);
					this.commonService.updateDeviceEventQueueForCloseSOSAlert(user.getId());
				} else {
					updateFlag = this.commonService.updateClosedDeviceEvents(device_event_id, user.getId(),
							user.getRoleType(), (int) durationInMinutes, false,
							duringSchoolHoursButCloseAfterSchoolHoursFlag);
					this.commonService.updateDeviceEventQueueForCloseSOSAlert(user.getId());
				}

				if (updateFlag) {
					statusCode = "ERR01";
					statusMsg = "CloseDeviceEvent already updated";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				} else {
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					responseJSON = ErrorCodesUtil.displaycloseSOSAlert(type, statusMsg, statusCode);
					return responseJSON;
				}

			}
			if (!(this.commonService.checkSchoolHoursWithinRange(eventOccuredextTime, user.getAccountId()))) {
				String duringSchoolHoursButCloseAfterSchoolHoursFlag = "no";

				if (this.commonService.checkClosedDeviceEventExists(device_event_id)) {
					updateFlag = this.commonService.updateClosedDeviceEvents(device_event_id, user.getId(),
							user.getRoleType(), (int) durationInMinutes, true,
							duringSchoolHoursButCloseAfterSchoolHoursFlag);
					this.commonService.updateDeviceEventQueueForCloseSOSAlert(user.getId());

					if (updateFlag) {
						statusCode = "ERR01";
						statusMsg = "CloseDeviceEvent already updated";
						responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
						return responseJSON;
					}
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					responseJSON = ErrorCodesUtil.displaycloseSOSAlert(type, statusMsg, statusCode);
					return responseJSON;

				} else {
					updateFlag = this.commonService.updateClosedDeviceEvents(device_event_id, user.getId(),
							user.getRoleType(), (int) durationInMinutes, false,
							duringSchoolHoursButCloseAfterSchoolHoursFlag);
					this.commonService.updateDeviceEventQueueForCloseSOSAlert(user.getId());
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					responseJSON = ErrorCodesUtil.displaycloseSOSAlert(type, statusMsg, statusCode);
					return responseJSON;
				}
			}
		}
		return closeSOSAlertJson;
	}

	@RequestMapping(value = "/web/isTokenValid/{token}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String isTokenValid(@PathVariable("token") String sessionID) throws ParseException {
		String errorMessage = null;
		String statusCode = null;
		String responseJSON = null;
		String msg = null;
		log.debug("logged our session Id" + sessionID);
		Users user = this.userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				errorMessage = "Token provided is expired, need to re-login";
				msg = "FAILURE";
				responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
				return responseJSON;
			}

			boolean isTokenValid = this.commonService.checkTokenIsValidOrNot(sessionID);

			if (isTokenValid) {
				statusCode = "SUC01";
				errorMessage = "API Request Success";
				msg = "SUCCESS";
				responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
				return responseJSON;
			} else {
				statusCode = "ERR02";
				errorMessage = "Token is Invalid";
				msg = "FAILURE";
				responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
				return responseJSON;
			}

		} else {
			statusCode = "ERR01";
			errorMessage = "Invalid User";
			msg = "FAILURE";
			responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
			return responseJSON;
		}

	}

	@RequestMapping(value = "/web/getCountyList/{token}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getCountyList(@PathVariable("token") String token) {
		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;
		Users user = userService.validateUserBySession(token);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String type = "common.getCountyList";
		HashMap<String, Object> countyMap = null;

		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (null != user.getRoleType()) {
					countyMap = commonService.getCountyList();

					responseJSON = ErrorCodesUtil.displaySchoolAdminData("API Request Success", "SUC01", type,
							JSONObject.toJSONString(countyMap));

					/*
					 * log.info(type + "*** JSON***" + "\t" + responseJSON); if
					 * (!StringUtility.isNull(request.getHeader(
					 * "allowCacheContent")) &&
					 * request.getHeader("allowCacheContent").equals("1")) {
					 * JSONUtility.respondAsJSON(response, responseJSON, 720); }
					 * else { JSONUtility.respondAsJSON(response, responseJSON);
					 * }
					 */

				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/web/ActivityNotificationLogs/{logType}/{sessionID}/{pageid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String ActivityNotificationLogs(@PathVariable String logType, @PathVariable String sessionID,
			@PathVariable("pageid") int pageid) {

		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int currentPage = pageid;
		int noofpages = 0;
		List<ActivityLogTransform> activityLogTrsfrmList = null, activityLogTrsfrmList1 = null;
		String respondJson = null;
		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsErrorStatusCode,
						Constant.SubscriptionsErrorMessage, Constant.ACTIVITY_LOG_API_TYPE);
				return respondJson;
			}
			if (user.getRoleType().equals(Constant.SystemAdmin)) {
				if (pageid != 1) {
					pageid = (pageid - 1) * total + 1;
				}

				if (logType.equals("activity")) {
					activityLogTrsfrmList1 = this.activityLogs.findActivityNotificationBeaconLogs("activity", 0, 0);
					activityLogTrsfrmList = this.activityLogs.findActivityNotificationBeaconLogs("activity", pageid,
							total);
				}

				else if (logType.equals("notification")) {
					activityLogTrsfrmList1 = this.activityLogs.findActivityNotificationBeaconLogs("notification", 0, 0);
					activityLogTrsfrmList = this.activityLogs.findActivityNotificationBeaconLogs("notification", pageid,
							total);
				}

				else if (logType.equals("backup")) {
					activityLogTrsfrmList1 = this.activityLogs.findActivityNotificationBeaconLogs("backup", 0, 0);
					activityLogTrsfrmList = this.activityLogs.findActivityNotificationBeaconLogs("backup", pageid,
							total);
				}

				int listSize = activityLogTrsfrmList1.size();

				if (listSize > 0 && pageid == 1) {
					if (listSize < total)
						noofpages = 1;
					else if (listSize % total == 0)
						noofpages = listSize / total;
					else
						noofpages = listSize / total + 1;
					this.httpSession.setAttribute("pages", String.valueOf(noofpages));
				}

				log.info("noofpages in CommonController" + "\t" + noofpages);
				for (ActivityLogTransform alt : activityLogTrsfrmList) {
					map = new LinkedHashMap<>();

					if (alt.getProfileName() != null)
						map.put("profile_name", alt.getProfileName());
					if (alt.getUsername() != null)
						map.put("username", alt.getUsername());
					if (alt.getRole() != null)
						map.put("role", alt.getRole());
					if (alt.getAction() != null)
						map.put("action", alt.getAction());
					if (alt.getIpaddress() != null)
						map.put("ipaddress", alt.getIpaddress());
					if (alt.getCreated_date() != null)
						map.put("created_date", alt.getCreated_date());
					map.put("noofPages", (String) this.httpSession.getAttribute("pages"));
					map.put("currentPage", currentPage);
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				respondJson = ErrorCodesUtil.displayJSONForActivityNotificationLog(Constant.SucessMsgActivity,
						Constant.StatusCodeActivity, myList.toString(), Constant.ACTIVITY_LOG_API_TYPE, logType);
			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
						Constant.UnauthorisedUserErrorCodeDescription, Constant.ACTIVITY_LOG_API_TYPE);
			}
		} else {
			respondJson = ErrorCodesUtil.displayErrorJSON(Constant.DisplayStatusCode, Constant.DisplayErrorMessage,
					Constant.ACTIVITY_LOG_API_TYPE);
		}

		return respondJson;
	}

	@RequestMapping(value = "/web/ExternalSystemStatus/{sessionID}/{pageid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String ExternalSystemStatus(@PathVariable String sessionID, @PathVariable("pageid") int pageid) {

		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int currentPage = pageid;
		int noofpages = 0;
		List<ExternalSystemStatusTransform> externalSystemStatusTrfmList, externalSystemStatusTrfmList1 = null;
		String respondJson = null;
		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsErrorStatusCode,
						Constant.SubscriptionsErrorMessage, Constant.EXTERNAL_SYSTEM_STATUS_TYPE);
				return respondJson;
			}
			if (user.getRoleType().equals(Constant.SystemAdmin)) {
				if (pageid != 1) {
					pageid = (pageid - 1) * total + 1;
				}

				externalSystemStatusTrfmList1 = this.activityLogs.findExternalSystemStatus(0, 0);
				externalSystemStatusTrfmList = this.activityLogs.findExternalSystemStatus(pageid, total);

				int listSize = externalSystemStatusTrfmList1.size();

				if (listSize > 0 && pageid == 1) {
					if (listSize < total)
						noofpages = 1;
					else if (listSize % total == 0)
						noofpages = listSize / total;
					else
						noofpages = listSize / total + 1;
					this.httpSession.setAttribute("esspages", String.valueOf(noofpages));
				}
				for (ExternalSystemStatusTransform alt : externalSystemStatusTrfmList) {
					map = new LinkedHashMap<>();

					if (null != alt.getIps_receiver_mac())
						map.put("ips_receiver_mac", alt.getIps_receiver_mac());
					if (alt.getIps_receiver_name() != null)
						map.put("ips_receiver_name", alt.getIps_receiver_name());
					if (alt.getIps_receiver_version() != null)
						map.put("ips_receiver_version", alt.getIps_receiver_version());
					if (alt.getIps_receiver_status() != null)
						map.put("ips_receiver_status", alt.getIps_receiver_status());
					if (alt.getDevicesCount() != null)
						map.put("device_count", alt.getDevicesCount());
					if (alt.getSchool_name() != null)
						map.put("school_name", alt.getSchool_name().toString());
					map.put("noofPages", (String) this.httpSession.getAttribute("esspages"));
					map.put("currentPage", currentPage);
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				respondJson = ErrorCodesUtil.displayJSONForActivityNotificationLog(Constant.SucessMsgActivity,
						Constant.StatusCodeActivity, myList.toString(), Constant.EXTERNAL_SYSTEM_STATUS_TYPE,
						Constant.EXTERNAL_LOG_TYPE);
			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
						Constant.UnauthorisedUserErrorCodeDescription, Constant.EXTERNAL_SYSTEM_STATUS_TYPE);
			}
		} else {
			respondJson = ErrorCodesUtil.displayErrorJSON(Constant.DisplayStatusCode, Constant.DisplayErrorMessage,
					Constant.EXTERNAL_SYSTEM_STATUS_TYPE);
		}

		return respondJson;
	}

}