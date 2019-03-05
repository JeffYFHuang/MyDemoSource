package com.liteon.icgwearable.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Timetable;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.TimeTableModel;
import com.liteon.icgwearable.model.TimeTablesModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.modelentity.TimeTableModelEntity;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.service.TimetableService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.StudentTimeTableTransform;
import com.liteon.icgwearable.transform.TeacherTimeTableTransform;
import com.liteon.icgwearable.transform.TimetableTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.TimeTableCSVToJavaUtil;
import com.liteon.icgwearable.util.WebUtility;

@RestController
public class TimetableController {
	private static Logger log = Logger.getLogger(TimetableController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private TimeTableModelEntity timetableModelEntity;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private TimetableService timetableService;

	@Autowired
	HttpServletResponse response;

	@Resource(name = "configProperties")
	private Properties configProperties;

	@Autowired
	private ActivityLogService activityLogs;

	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;
	@Value("${db.dateTime}")
	private String dbDateTime;
	private Integer userId;
	private String ErrorMessage = null;
	private String StatusCode = null;
	private String responseJSON = null;

	@RequestMapping(value = "/listOfTimeTable")
	public ModelAndView listOfTimeTable(ModelAndView model,
			@ModelAttribute("timeTablesModel") TimeTablesModel timeTablesModel, HttpServletRequest request,
			@RequestParam("token") String sessionID) {
		log.info("listOfTimeTable()");
		String className = timeTablesModel.getSelectClass();
		userId = (Integer) this.httpSession.getAttribute("userId");
		List<TimetableTransform> ttTransform = this.timetableService.listTimetableUniqueClass(userId);
		List<String> classList = new ArrayList<>();
		String classValue = null;

		for (TimetableTransform transform : ttTransform) {
			classList.add(String.valueOf(transform.getStudentClass()));
		}

		if (null != timeTablesModel && null != className) {
			classValue = className;
		}

		List<TimeTablesModel> listOfTimetables = timetableModelEntity
				.prepareTimeTablesModelList(timetableService.findtimeTableByClass(classValue), 1);

		if (null != listOfTimetables) {
			model.addObject(Constant.SessionID, sessionID);
			model.addObject("upload_msg", "Upload TimeTableFile");
			model.setViewName("timetables");
			return model;
		}
		model.addObject(Constant.SessionID, sessionID);
		request.setAttribute("listOfTimetables", listOfTimetables);
		request.setAttribute("listUniqueClassForTimetables", classList);
		model.addObject("view_class", "Class " + className + " Timetable");
		model.setViewName("listOfTimetables");
		return model;

	}

	@RequestMapping(value = "/listofclass")
	public ModelAndView listOfClass(ModelAndView model,
			@ModelAttribute("timeTablesModel") TimeTablesModel timeTablesModel, HttpServletRequest request,
			RedirectAttributes redirectAttributes, @RequestParam("token") String sessionID) {
		log.info("listOfClass()");
		userId = (Integer) this.httpSession.getAttribute("userId");

		Users user = this.userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if ((null == this.httpSession.getAttribute("currentUser"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		log.info("11 checked sessionValidity :::::::::::::::::::::::::::::");

		List<TimetableTransform> ttTransform = this.timetableService.listTimetableUniqueClass(userId);
		List<String> classList = new ArrayList<>();
		List<String> gradeList = new ArrayList<>();
		for (TimetableTransform transform : ttTransform) {
			classList.add(String.valueOf(transform.getStudentClass()));
			gradeList.add(String.valueOf(transform.getGrade()));
		}
		List<TimeTablesModel> listOfTimetables = null;
		request.setAttribute("listOfTimetables", listOfTimetables);
		request.setAttribute("listUniqueClassForTimetables", classList);
		model.addObject("listUniqueClasses", classList);
		model.addObject("listUniqueGrades", gradeList);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("add_msg", "Select Class to display TimeTable.");
		model.setViewName("listOfTimetables");
		return model;

	}

	@RequestMapping(value = "/timeTablesUploadFile", method = RequestMethod.POST, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView timeTableuploadFileHandler(ModelAndView model, @RequestParam("file") MultipartFile file,
			HttpServletRequest request, TimeTableModel timeTableModel, RedirectAttributes redirectAttributes,
			@RequestParam("token") String sessionID) throws IOException {
		String fileName = null;
		Map<Integer, String> ignoredMap = null;
		List<Object> outerEventList = new ArrayList<Object>();
		action = "Create";
		methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		String jsonString = null;
		int totalRecordCount = 0;
		int insertedRecordCount = 0;
		int ignoredRecordCount = 0;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {

			log.error("timeTableuploadFileHandler() ", e1);
		}

		boolean timetableInsertionSuccessful = false;

		int userId = (int) request.getSession(false).getAttribute("userId");
		Users user = userService.validateUserBySession(sessionID);

		UsersModel userModel = (UsersModel) this.httpSession.getAttribute("userModel");
		if (null == userModel) {
			model.setViewName("adminlogin");
			return model;
		}
		userModel.setId(userId);
		timeTableModel.setUserId(userModel.getId());
		timeTableModel.setSchoolId(userService.getAccountIdbyUserId(userModel.getId()).getAccountId());
		Integer schoolId = user.getAccountId();
		timeTableModel.setGrade("1");
		if (null != file) {
			fileName = file.getOriginalFilename();
			log.info("FileName" + "\t" + fileName);
		}
		Date date = new Date();
		String os = System.getProperty("os.name");
		log.info("Operating System" + "\t" + os);
		if (os.contains("Windows"))
			dbDateTime = dbDateTime.replace(":", "-");

		SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
		try {

			File f = new File(WebUtility.createFolder(this.configProperties.getProperty("timetable.upload.path") + "/"
					+ schoolId + '_' + dateFormat.format(date).trim() + ".csv"));

			boolean flag = f.createNewFile();
			log.info("fileCreated>>Teacher" + "\t" + flag);
			log.info("f.getPath() Teachers Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));

			file.transferTo(f);
			log.info("FilePath" + "\t" + f.getAbsolutePath());
			List<TimeTableModel> list = null;
			try {
				list = TimeTableCSVToJavaUtil.convertTimeTablesCsvToJava(f);
				log.info("list" + "\n" + list);
				for (TimeTableModel ttmodel : list) {
					log.info("TimeTableModel" + "\n" + ttmodel.toString());
				}

			} catch (NullPointerException e) {
				log.info("Exception while dealing with headers" + e);
				if (list == null || list.size() == 0) {
					model.addObject(Constant.INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
					model.addObject(Constant.SessionID, sessionID);
					model.setViewName("listOfTimetables");
					return model;
				}
			}
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			Map<String, Object> resultmap = timetableService.createTimeTable(list, timeTableModel);
			totalRecordCount = (Integer) resultmap.get("totalCount");
			insertedRecordCount = (Integer) resultmap.get("insertCount");
			ignoredRecordCount = (Integer) resultmap.get("ignnoredCount");
			ignoredMap = (HashMap<Integer, String>) resultmap.get("ignoredList");
			Map<Object, Object> outerZoneMap = new HashMap<Object, Object>();

			Iterator it = ignoredMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Map<Object, Object> innerZoneMap = new HashMap<Object, Object>();
				innerZoneMap.put("row", pair.getKey());
				innerZoneMap.put("error", pair.getValue());
				outerEventList.add(innerZoneMap);
			}
			outerZoneMap.put("error_log", outerEventList);
			jsonString = JSONObject.toJSONString(outerZoneMap);
			log.info("jsonString" + jsonString);

			log.info("totalRecordCount" + totalRecordCount);
			log.info("insertedRecordCount" + insertedRecordCount);
			log.info("ignoredRecordCount" + ignoredRecordCount);
			activityLogs.info(activityLog);
			log.info("timetableInsertionSuccessful" + "\t" + timetableInsertionSuccessful);
		} catch (Exception e) {
			log.info("Error On Console" + "\t" + e);
			model.addObject(Constant.INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("listOfTimetables");
			return model;
		}
		redirectAttributes.addFlashAttribute(Constant.CSV_SUCCESS, Constant.CSV_SUCCESS_MSG);
		redirectAttributes.addFlashAttribute("timetableRecordCount", "Total Records(" + totalRecordCount
				+ "),  Created(" + insertedRecordCount + "), Ignored(" + ignoredRecordCount + ")");
		if (ignoredRecordCount > 0)
			redirectAttributes.addFlashAttribute("ignoredList", jsonString);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return new ModelAndView("redirect:/listofclass?token=" + sessionID);
	}

	@RequestMapping(value = "/selectClassFromList")
	public ModelAndView listTimeTables(ModelAndView model,
			@ModelAttribute("timeTablesModel") TimeTablesModel timeTablesModel,
			@RequestParam("token") String sessionID) {
		userId = (Integer) this.httpSession.getAttribute("userId");
		model.addObject("listUniqueClass", timetableService.listTimetableUniqueClass(userId));
		model.addObject(Constant.SessionID, sessionID);
		model.setViewName("timetable");
		return model;
	}

	@RequestMapping(value = "/timeTable")
	public ModelAndView timeTable(@RequestParam("token") String sessionID) {
		ModelAndView model = new ModelAndView();
		model.addObject(Constant.SessionID, sessionID);
		model.addObject("upload_msg", "Select file for Upload Timetable.");
		model.setViewName("timetables");
		return model;
	}

	@RequestMapping(value = "/editTimeTable")
	public ModelAndView editTimeTable(ModelAndView model,
			@ModelAttribute("timeTableModel") TimeTableModel timeTableModel, HttpServletRequest request,
			@RequestParam("token") String sessionID) {
		log.info("editTimeTable()");
		String className = timeTableModel.getSelectClass();
		userId = (Integer) this.httpSession.getAttribute("userId");
		List<TimetableTransform> ttTransform = this.timetableService.listTimetableUniqueClass(userId);
		List<String> classList = new ArrayList<>();
		String classValue = null;

		for (TimetableTransform transform : ttTransform) {
			classList.add(String.valueOf(transform.getStudentClass()));
		}
		if (null != timeTableModel && null != className) {
			classValue = className;
		}

		List<TimeTableModel> listOfEditTimetables = timetableModelEntity
				.prepareTimeTableModelList(timetableService.findTimeTableByClass(classValue), 1);

		request.setAttribute("listOfEditTimetables", listOfEditTimetables);
		request.setAttribute("EditUniqueClassForTimetable", classList);
		model.addObject("select_class", "Edit Class " + className + " Timetable");
		model.addObject(Constant.SessionID, sessionID);
		model.setViewName("listOfEditTimetables");
		return model;
	}

	@RequestMapping(value = "/updateTimetable", method = RequestMethod.POST)
	public ModelAndView updateTimetable(ModelAndView model,
			@ModelAttribute("timeTableModel") TimeTableModel timeTableModel, HttpServletRequest request,
			RedirectAttributes redirectAttributes, @RequestParam("token") String sessionID) {

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

			log.error("updateTimetable() ", e1);
		}

		Users user = userService.validateUserBySession(sessionID);
		int userId = (int) request.getSession(false).getAttribute("userId");

		UsersModel userModel = (UsersModel) this.httpSession.getAttribute("userModel");
		userModel.setId(userId);

		timeTableModel.setUserId(userModel.getId());
		timeTableModel.setSchoolId(userService.getAccountIdbyUserId(userModel.getId()).getAccountId());

		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
		timetableService.updateTimetable(timeTableModel, request);
		activityLogs.info(activityLog);

		redirectAttributes.addFlashAttribute("update_msg",
				"Class " + timeTableModel.getSelectClass() + " TimeTable Updated Successfully.");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		redirectAttributes.addFlashAttribute("selectClass", timeTableModel.getSelectClass());
		ModelAndView modelAndView = new ModelAndView("redirect:/editTimeTable?token=" + sessionID);
		modelAndView.addObject("selectClass", timeTableModel.getSelectClass());
		return modelAndView;
	}

	@RequestMapping(value = "/deleteTimeTable")
	public ModelAndView deleteTimeTable(ModelAndView model, HttpServletRequest request,
			RedirectAttributes redirectAttributes, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String studentClass = request.getParameter("selectClass");
		action = "Delete";
		methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {

			log.error("deleteTimeTable() ", e1);
		}

		int userId = (int) request.getSession(false).getAttribute("userId");
		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
		timetableService.deleteTimeTable(studentClass, userId);
		activityLogs.info(activityLog);

		redirectAttributes.addFlashAttribute("delete_msg", "Class " + studentClass + " TimeTable Delete Successfully.");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return new ModelAndView("redirect:/listofclass?token=" + sessionID);
	}

	@RequestMapping(value = "/mobile/StudentClassTimetableView/{sessionID}/{uuid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentClassTimetableView(@PathVariable String sessionID, @PathVariable String uuid) {

		new JSONArray();
		new ArrayList<>();
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String jsonString = null;
		HashMap<String, HashMap<String, String>> outerMap = new LinkedHashMap<>();
		HashMap<String, String> innerMap = null;
		LinkedHashMap<String, Object> topMap = new LinkedHashMap<>();
		String type = "timetable.StudentClassTimetableView";

		log.debug("logged our session Id" + sessionID);
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode,
						Constant.SubscriptionsType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (!(user.getRoleType().equals("parent_admin"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			Devices device = deviceService.checkDeviceIdExist(uuid);
			log.info("device" + device);
			if (null == device) {
				StatusCode = "ERR06";
				ErrorMessage = "Invalid Device UUID";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			List<StudentTimeTableTransform> studentTimeTableList = timetableService
					.getStudentClassTimetable(user.getId(), uuid);
			for (int i = 0; i < studentTimeTableList.size(); i++) {
				StudentTimeTableTransform stt = (StudentTimeTableTransform) studentTimeTableList.get(i);
				topMap.put("class", stt.getStudentClass());
				topMap.put("grade", stt.getGrade());
				innerMap = new LinkedHashMap<>();
				if (null != stt.getSubjectOne())
					innerMap.put("sub_1", stt.getSubjectOne());
				if (null != stt.getSubjectTwo())
					innerMap.put("sub_2", stt.getSubjectTwo());
				if (null != stt.getSubjectThree())
					innerMap.put("sub_3", stt.getSubjectThree());
				if (null != stt.getSubjectFour())
					innerMap.put("sub_4", stt.getSubjectFour());
				if (null != stt.getSubjectFive())
					innerMap.put("sub_5", stt.getSubjectFive());
				if (null != stt.getSubjectSix())
					innerMap.put("sub_6", stt.getSubjectSix());
				if (null != stt.getSubjectSeven())
					innerMap.put("sub_7", stt.getSubjectSeven());
				if (null != stt.getSubjectEight())
					innerMap.put("sub_8", stt.getSubjectEight());
				if (null != stt.getWeekDay())
					outerMap.put(stt.getWeekDay(), innerMap);
			}
			topMap.put("schedule", outerMap);
			jsonString = JSONObject.toJSONString(topMap);
			// myList.add(jsonString);
			log.info("***classContentTableList***" + "\n" + jsonString);
			responseJSON = ErrorCodesUtil.displayJSONForParenttimeTable("", "", jsonString);
			// JSONUtility.respondAsJSON(response, classRespJson);
		} else {
			String statusCode = "ERR01";
			String errorMessage = "User Does Not Exist";
			type = "";
			responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
			// JSONUtility.respondAsJSON(response, respondJson);
		}

		return responseJSON;
	}

	private Map<String, Object> studentList(List<Timetable> listOfTimetables) {
		HashMap<String, HashMap<String, String>> outerMap = new LinkedHashMap<>();
		HashMap<String, String> innerMap = null;
		LinkedHashMap<String, Object> topMap = null;
		topMap = new LinkedHashMap<>();
		for (int i = 0; i < listOfTimetables.size(); i++) {
			innerMap = new LinkedHashMap<>();
			innerMap.put("sub_1", listOfTimetables.get(i).getSubjectOne());
			innerMap.put("sub_2", listOfTimetables.get(i).getSubjectTwo());
			innerMap.put("sub_3", listOfTimetables.get(i).getSubjectThree());
			innerMap.put("sub_4", listOfTimetables.get(i).getSubjectFour());
			innerMap.put("sub_5", listOfTimetables.get(i).getSubjectFive());
			innerMap.put("sub_6", listOfTimetables.get(i).getSubjectSix());
			innerMap.put("sub_7", listOfTimetables.get(i).getSubjectSeven());
			innerMap.put("sub_8", listOfTimetables.get(i).getSubjectEight());
			if (i == 0)
				outerMap.put("MON", innerMap);
			else if (i == 1)
				outerMap.put("TUE", innerMap);
			else if (i == 2)
				outerMap.put("WED", innerMap);
			else if (i == 3)
				outerMap.put("THU", innerMap);
			else if (i == 4) {
				outerMap.put("FRI", innerMap);
				// topMap.put("class",
				// listOfTimetables.get(i).getStudentClass());
				topMap.put("schedule", outerMap);
			}

		}
		return topMap;
	}

	@RequestMapping(value = "/mobile/TeacherClassTimetableView/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String TeacherClassTimetableView(@PathVariable String sessionID) {

		new JSONArray();
		new ArrayList<>();
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String jsonString = null;
		HashMap<String, HashMap<String, String>> outerMap = new LinkedHashMap<>();
		HashMap<String, String> innerMap = null;
		LinkedHashMap<String, Object> topMap = new LinkedHashMap<>();
		String type = "timetable.TeacherClassTimetableView";
		log.debug("logged our session Id" + sessionID);
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode,
						Constant.SubscriptionsType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (!(user.getRoleType().equals("school_teacher"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			List<TeacherTimeTableTransform> teacherTimeTableList = timetableService
					.getTeacherClassTimetable(user.getId());
			for (int i = 0; i < teacherTimeTableList.size(); i++) {
				TeacherTimeTableTransform ttt = (TeacherTimeTableTransform) teacherTimeTableList.get(i);
				topMap.put("class", ttt.getStudentClass());
				topMap.put("grade", ttt.getGrade());
				innerMap = new LinkedHashMap<>();
				if (null != ttt.getSubjectOne())
					innerMap.put("sub_1", ttt.getSubjectOne());
				if (null != ttt.getSubjectTwo())
					innerMap.put("sub_2", ttt.getSubjectTwo());
				if (null != ttt.getSubjectThree())
					innerMap.put("sub_3", ttt.getSubjectThree());
				if (null != ttt.getSubjectFour())
					innerMap.put("sub_4", ttt.getSubjectFour());
				if (null != ttt.getSubjectFive())
					innerMap.put("sub_5", ttt.getSubjectFive());
				if (null != ttt.getSubjectSix())
					innerMap.put("sub_6", ttt.getSubjectSix());
				if (null != ttt.getSubjectSeven())
					innerMap.put("sub_7", ttt.getSubjectSeven());
				if (null != ttt.getSubjectEight())
					innerMap.put("sub_8", ttt.getSubjectEight());
				if (null != ttt.getWeekDay())
					outerMap.put(ttt.getWeekDay(), innerMap);
			}
			topMap.put("schedule", outerMap);
			jsonString = JSONObject.toJSONString(topMap);
			// myList.add(jsonString);
			log.info("***classContentTableList***" + "\n" + jsonString);
			responseJSON = ErrorCodesUtil.displayJSONFortimeTable("", "", jsonString);

			// JSONUtility.respondAsJSON(response, classRespJson);
		} else {
			String statusCode = "ERR01";
			String errorMessage = "User Does Not Exist";
			type = "";
			responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
			// JSONUtility.respondAsJSON(response, respondJson);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/getTimeTable", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getTimeTable(@RequestParam("token") String sessionID,
			@RequestParam("studentClass") String studentClass, @RequestParam("grade") String grade,
			HttpServletRequest request, HttpServletResponse response) {

		Map<Object, Object> map = null;
		String type = "timetable.getTimeTable";

		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();
		new ArrayList<>();

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			Integer schoolId = user.getAccountId();
			List<StudentTimeTableTransform> timetableList = timetableService.getTimeTable(grade, studentClass,
					schoolId);

			Map<String, List<String>> sub_one = new HashMap<String, List<String>>();
			Map<String, List<String>> sub_two = new HashMap<String, List<String>>();
			Map<String, List<String>> sub_three = new HashMap<String, List<String>>();
			Map<String, List<String>> sub_four = new HashMap<String, List<String>>();
			Map<String, List<String>> sub_five = new HashMap<String, List<String>>();
			Map<String, List<String>> sub_six = new HashMap<String, List<String>>();
			Map<String, List<String>> sub_seven = new HashMap<String, List<String>>();
			Map<String, List<String>> sub_eight = new HashMap<String, List<String>>();

			for (StudentTimeTableTransform studentTimeTableTransform : timetableList) {

				if (!sub_one.containsKey("sub_one")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectOne());
					sub_one.put("sub_one", days);
				} else
					sub_one.get("sub_one").add(studentTimeTableTransform.getSubjectOne());

				if (!sub_two.containsKey("sub_two")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectTwo());
					sub_two.put("sub_two", days);
				} else
					sub_two.get("sub_two").add(studentTimeTableTransform.getSubjectTwo());

				if (!sub_three.containsKey("sub_three")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectThree());
					sub_three.put("sub_three", days);
				} else
					sub_three.get("sub_three").add(studentTimeTableTransform.getSubjectThree());

				if (!sub_four.containsKey("sub_four")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectFour());
					sub_four.put("sub_four", days);
				} else
					sub_four.get("sub_four").add(studentTimeTableTransform.getSubjectFour());

				if (!sub_five.containsKey("sub_five")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectFive());
					sub_five.put("sub_five", days);
				} else
					sub_five.get("sub_five").add(studentTimeTableTransform.getSubjectFive());

				if (!sub_six.containsKey("sub_six")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectSix());
					sub_six.put("sub_six", days);
				} else
					sub_six.get("sub_six").add(studentTimeTableTransform.getSubjectSix());

				if (!sub_seven.containsKey("sub_seven")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectSeven());
					sub_seven.put("sub_seven", days);
				} else
					sub_seven.get("sub_seven").add(studentTimeTableTransform.getSubjectSeven());

				if (!sub_eight.containsKey("sub_eight")) {
					List<String> days = new ArrayList<>();
					days.add(studentTimeTableTransform.getSubjectEight());
					sub_eight.put("sub_eight", days);
				} else
					sub_eight.get("sub_eight").add(studentTimeTableTransform.getSubjectEight());

			}
			List<String> subone_List = sub_one.get("sub_one");
			List<String> subtwo_List = sub_two.get("sub_two");
			List<String> subthree_List = sub_three.get("sub_three");
			List<String> subfour_List = sub_four.get("sub_four");
			List<String> subfive_List = sub_five.get("sub_five");
			List<String> subsix_List = sub_six.get("sub_six");
			List<String> subseven_List = sub_seven.get("sub_seven");
			List<String> subeight_List = sub_eight.get("sub_eight");
			String key = "";
			if (null != subone_List && subone_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subone_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subone_List.get(i));
				}
				finalList.add(map);
			}

			if (null != subtwo_List && subtwo_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subtwo_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subtwo_List.get(i));
				}
				finalList.add(map);
			}

			if (null != subthree_List && subthree_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subthree_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subthree_List.get(i));
				}
				finalList.add(map);
			}

			if (null != subfour_List && subfour_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subfour_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subfour_List.get(i));
				}
				finalList.add(map);
			}

			if (null != subfive_List && subfive_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subfive_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subfive_List.get(i));
				}
				finalList.add(map);
			}

			if (null != subsix_List && subsix_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subsix_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subsix_List.get(i));
				}
				finalList.add(map);
			}

			if (null != subseven_List && subseven_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subseven_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subseven_List.get(i));
				}
				finalList.add(map);
			}
			if (null != subeight_List && subeight_List.size() > 0) {
				map = new LinkedHashMap<>();
				key = "MON";
				for (int i = 0; i < subeight_List.size(); i++) {
					if (i == 1)
						key = "TUE";
					else if (i == 2)
						key = "WED";
					else if (i == 3)
						key = "THR";
					else if (i == 4)
						key = "FRI";
					map.put(key, subeight_List.get(i));
				}
				finalList.add(map);
			}
			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			// gradesList.add(deviceJSON);
			responseJSON = ErrorCodesUtil.displayTimetablecList("API Request Success", "SUC01", deviceJSON);
			// JSONUtility.respondAsJSON(response, gradesJson);
		}

		else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// return responseJSON;
		}
		return responseJSON;
	}
}
