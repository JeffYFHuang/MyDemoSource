package com.liteon.icgwearable.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.EventSubscriptions;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.UserSubscriptions;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.service.EventsService;
import com.liteon.icgwearable.service.StudentsService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.GeoFenceEntyExitTransform;
import com.liteon.icgwearable.transform.SubscribedEvents;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
public class EventsController {

	private static Logger log = Logger.getLogger(EventsController.class);

	@Autowired
	private EventsService eventsService;
	@Autowired
	private UserService userService;
	@Autowired
	private DeviceService deviceService;

	@Autowired
	private StudentsService studentService;
	@Autowired
	HttpServletResponse response;
	@Value("${SCHOOL_ENTRY_ALERT_ID}")
	private Integer SCHOOL_ENTRY_ID;

	@Value("${SCHOOL_EXIT_ALERT_ID}")
	private Integer SCHOOL_EXIT_ID;

	@Value("${GEOFENCE_ENTRY_ID}")
	private Integer GEOFENCE_ENTRY_ID;

	@Value("${GEOFENCE_EXIT_ID}")
	private Integer GEOFENCE_EXIT_ID;
	@Autowired
	private ActivityLogService activityLogs;

	@Resource(name = "configProperties")
	private Properties configProperties;

	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;

	@RequestMapping(value = "/mobile/SupportedEventsList/{sessionID}/{uuid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SupportedEventsList(ModelAndView model, @PathVariable("sessionID") String sessionID,
			@PathVariable("uuid") String uuid) {

		JSONArray jsonArray = new JSONArray();
		List myList = new ArrayList<>();
		String jsonArrayString = null;
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;

		String jsonString = null;
		String type = "event.SupportedEventsList";
		log.info("logged our session Id" + sessionID);
		List<String> supportedEventtyeps = new ArrayList<String>();
		supportedEventtyeps.add("all");
		supportedEventtyeps.add("system");
		supportedEventtyeps.add("device");
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

			log.info("before");

			Integer deviceId = deviceService.getDeviceIdByUuidandUser(user, uuid);
			log.info("deviceId" + deviceId);

			if (deviceId != 0) {
				List<SubscribedEvents> evnetsList = eventsService.getUserSubscribedEvents(user.getId(), uuid,
						user.getRoleType());

				log.info("after");
				for (SubscribedEvents event : evnetsList) {

					Map<Object, Object> map = new LinkedHashMap<>();

					map.put("event_id", event.getSupportedEventId());
					map.put("event_name", event.getEventName());

					if (null != event.getSubscribedEventId())
						map.put("subscribed", "yes");
					else
						map.put("subscribed", "no");

					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);

					log.info("My List Contents::::::::" + myList);

				}

				jsonArrayString = jsonArray.toString();
				log.info("jsonArrayString : " + "\n" + jsonArrayString);

				StatusCode = "SUC01";
				ErrorMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displaySuccessJSONForMemeberList(type, ErrorMessage, StatusCode,
						myList.toString());
				// JSONUtility.respondAsJSON(response, responseJSON);
			} else {
				StatusCode = "ERR26";
				ErrorMessage = "Device is not associated with  user";
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

	@RequestMapping(value = "/mobile/SupportedEventsUpdate/{sessionID}/{student_id}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String SupportedEventsUpdate(@RequestBody List<UserSubscriptions> userSubscriptions, ModelAndView model,
			@PathVariable("sessionID") String sessionID, @PathVariable("student_id") int student_id) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
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

		List<String> subscribedList = new ArrayList<String>();
		subscribedList.add("yes");
		subscribedList.add("no");

		String type = "event.SupportedEventsUpdate";

		log.info("logged our session Id" + sessionID);

		if (userSubscriptions.size() == 0) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessage5, Constant.StatusCode,
					Constant.SubscriptionsUpdateType);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}
			if (!(user.getRoleType().equals("parent_admin"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}

			Students student = studentService.getStudent(student_id);
			if (null != student) {
				for (UserSubscriptions subscriptions : userSubscriptions) {

					if (subscriptions.getEventValue().isEmpty()) {
						StatusCode = "ERR04";
						ErrorMessage = "Empty input , please provide preferences to update ";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						return responseJSON;
					}
					if (!subscribedList.contains(subscriptions.getEventValue())) {
						StatusCode = "ERR05";
						ErrorMessage = "Invalid Event Value , please provide valid Event Value as 'yes' or 'no' ";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						return responseJSON;
					}
					boolean isSupportedEvent = false;
					if (null != subscriptions.getEventId())
						isSupportedEvent = eventsService.isSupportedEvent(subscriptions.getEventId());
					if (isSupportedEvent == true) {
						int userId = user.getId();
						if (subscriptions.getEventValue().equals("yes")) {
							boolean isMemberSubscribed = eventsService.isMemberSubscribed(user.getId(), student_id,
									subscriptions.getEventId());

							if (isMemberSubscribed != true) {
								ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary,
										ipaddress);
								eventsService.subscribeEvent(user.getId(), student_id, subscriptions.getEventId());
								activityLogs.info(activityLog);
								if (subscriptions.getEventId() == SCHOOL_ENTRY_ID) {
									isSupportedEvent = eventsService.isSupportedEvent(SCHOOL_EXIT_ID);
									if (isSupportedEvent == true) {
										eventsService.subscribeEvent(user.getId(), student_id, SCHOOL_EXIT_ID);
										activityLogs.info(activityLog);
									}
								}
								if (subscriptions.getEventId() == GEOFENCE_ENTRY_ID) {
									isSupportedEvent = eventsService.isSupportedEvent(GEOFENCE_EXIT_ID);
									if (isSupportedEvent == true) {
										eventsService.subscribeEvent(user.getId(), student_id, GEOFENCE_EXIT_ID);
										activityLogs.info(activityLog);
									}
								}
							}
						} else {
							action = "Update";
							ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary,
									ipaddress);
							eventsService.unsubscribeEvent(userId, student_id, subscriptions.getEventId());
							activityLogs.info(activityLog);
							if (subscriptions.getEventId() == SCHOOL_ENTRY_ID) {
								eventsService.unsubscribeEvent(userId, student_id, SCHOOL_EXIT_ID);
								activityLogs.info(activityLog);
							}
							if (subscriptions.getEventId() == GEOFENCE_ENTRY_ID) {
								eventsService.unsubscribeEvent(userId, student_id, GEOFENCE_EXIT_ID);
								activityLogs.info(activityLog);
							}
						}
					} else {
						StatusCode = "ERR05";
						ErrorMessage = "Invalid Input details ";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						return responseJSON;
					}
				}
			} else {
				StatusCode = "ERR05";
				ErrorMessage = "Invalid Input details ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
		}
		StatusCode = "SUC01";
		ErrorMessage = "API Request Success";
		responseJSON = ErrorCodesUtil.displaySuccessJSON(ErrorMessage, StatusCode, type);
		return responseJSON;
	}

	@RequestMapping(value = "/web/BeaconGeofenceEventsList/{sessionID}/{inputdate}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String BeaconGeofenceEventsList(@PathVariable("sessionID") String sessionID,
			@PathVariable("inputdate") String inputdate) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String jsonString = null;
		Map<Object, Object> outerZoneMap = new HashMap<Object, Object>();
		List<Object> outerEventList = new ArrayList<Object>();
		String type = "event.BeaconGeofenceEventsList";

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
			if (!user.getRoleType().equals("school_admin")) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			String download_url = this.configProperties.getProperty("downloads.url");
			String ips_path = this.configProperties.getProperty("ipsimages.download.path");
			List<GeoFenceEntyExitTransform> evnetsList = eventsService.getGeoFenceEntryOrExitEvents(user.getAccountId(),
					inputdate);

			Map<Integer, List<GeoFenceEntyExitTransform>> zoneMap = new HashMap<Integer, List<GeoFenceEntyExitTransform>>();

			for (GeoFenceEntyExitTransform event : evnetsList) {
				if (!zoneMap.containsKey(event.getReceiverZoneId())) {

					List<GeoFenceEntyExitTransform> list = new ArrayList<GeoFenceEntyExitTransform>();
					list.add(event);
					zoneMap.put(event.getReceiverZoneId(), list);
				} else {
					zoneMap.get(event.getReceiverZoneId()).add(event);
				}

			}
			if (zoneMap.size() > 0) {
				Iterator<Map.Entry<Integer, List<GeoFenceEntyExitTransform>>> mapIterator = zoneMap.entrySet()
						.iterator();

				while (mapIterator.hasNext()) {

					Map.Entry<Integer, List<GeoFenceEntyExitTransform>> pair = mapIterator.next();
					int zoneId = pair.getKey();
					List<GeoFenceEntyExitTransform> tempList = pair.getValue();

					if (tempList.size() > 0) {
						Map<Object, Object> innerZoneMap = new HashMap<Object, Object>();
						List<Object> innerEventList = new ArrayList<Object>();

						for (GeoFenceEntyExitTransform geoFenceTrans : tempList) {
							Map<Object, Object> innereventeMap = new HashMap<Object, Object>();
							String map_url = download_url + ips_path + "/" + geoFenceTrans.getMapFileName();
							innerZoneMap.put("zone_name", geoFenceTrans.getZoneName());
							innerZoneMap.put("map_type", geoFenceTrans.getMapType());
							innerZoneMap.put("total_entry", tempList.size());
							innerZoneMap.put("building_name", geoFenceTrans.getBulingName());
							innerZoneMap.put("floor_number", geoFenceTrans.getFloorNumber());
							innerZoneMap.put("zone_view", map_url);
							if (null != geoFenceTrans.getStudentId()) {
								innereventeMap.put("student_name", geoFenceTrans.getStdentName());
								innereventeMap.put("student_class", geoFenceTrans.getStudentClass());
								innereventeMap.put("student_id", geoFenceTrans.getStudentId());
								if (null != geoFenceTrans.getInTime()) {
									innereventeMap.put("in_time", geoFenceTrans.getInTime().toString());
								} else {
									innereventeMap.put("in_time", "");
								}
								if (null != geoFenceTrans.getOutTime()) {
									innereventeMap.put("out_time", geoFenceTrans.getOutTime().toString());
								} else {
									innereventeMap.put("out_time", "");
								}
								if (null != geoFenceTrans.getDuration()) {
									innereventeMap.put("duration", geoFenceTrans.getDuration().toString());
								} else {
									innereventeMap.put("duration", "");
								}
								innerEventList.add(innereventeMap);
							}
						}
						if (!innerEventList.isEmpty()) {
							innerZoneMap.put("students", innerEventList);
						}
						outerEventList.add(innerZoneMap);
					}

				}
				outerZoneMap.put("school_geofencing", outerEventList);
			}

			jsonString = JSONObject.toJSONString(outerZoneMap);
			log.info("jsonString : " + "\n" + jsonString);

			StatusCode = "SUC01";
			ErrorMessage = "API Request Success";
			responseJSON = ErrorCodesUtil.displaySuccessJSONForGeofenceList(type, ErrorMessage, StatusCode, jsonString);
			// JSONUtility.respondAsJSON(response, responseJSON);

		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}
}