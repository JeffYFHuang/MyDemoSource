package com.liteon.icgwearable.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.RewardsCategory;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.SchoolPrefModel;
import com.liteon.icgwearable.model.StudentsModel;
import com.liteon.icgwearable.model.TeacherCSVModel;
import com.liteon.icgwearable.model.TeacherRewardsMultipleAssignModel;
import com.liteon.icgwearable.model.TeacherRewardsToStudentModel;
import com.liteon.icgwearable.model.TeacherStaffModel;
import com.liteon.icgwearable.model.TeachersStaffCSVModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.model.UsersRestModel;
import com.liteon.icgwearable.modelentity.TeacherModelEntity;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.RewardsService;
import com.liteon.icgwearable.service.TeacherService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.RewardStatisticsTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.TeacherRewardsAssignedToStudentsTransform;
import com.liteon.icgwearable.transform.TeacherRewardsListTransform;
import com.liteon.icgwearable.transform.TeachersTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.TeacherCSVToJavaUtil;
import com.liteon.icgwearable.util.TeachersStaffCSVToJavaUtil;
import com.liteon.icgwearable.util.WebUtility;

@RestController
public class TeacherController {
	private static Logger log = Logger.getLogger(TeacherController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private TeacherModelEntity teacherModelEntity;
	@Autowired
	HttpServletRequest request;
	@Autowired
	private RewardsService rewardsService;
	@Resource(name = "configProperties")
	private Properties configProperties;
	@Autowired
	HttpServletResponse response;
	@Autowired
	private ActivityLogService activityLogs;
	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;
	@Value("${db.dateTime}")
	private String dbDateTime;
	@Value("${PAGINATION_NO_OF_RECORDS}")
	private int PAGINATION_NO_OF_RECORDS;

	WebUtility webUtility = WebUtility.getWebUtility();

	@RequestMapping(value = "/teachersUploadFile", method = RequestMethod.POST)
	public ModelAndView teachersuploadFileHandler(ModelAndView model,
			@ModelAttribute("usersModel") UsersModel usersModel, @RequestParam("fileUpload") MultipartFile file,
			RedirectAttributes redirectAttributes, BindingResult result, @RequestParam("token") String sessionID)
			throws IOException {

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Users user = userService.validateUserBySession(sessionID);
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

		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		String fileName = null;
		if (file != null) {
			fileName = file.getOriginalFilename();
			log.info("FileName" + "\t" + fileName);
		}

		String extn = fileName.substring(fileName.lastIndexOf(".") + 1);
		log.info("extn" + "\t" + extn);
		if (!extn.equals("csv")) {
			model.addObject("wrongExtn", "Please Choose a Valid CSV File");
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("schoolAccountManagement");
			return model;
		} else {
			log.info("f.getPath() Teachers Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
			// File f = new
			// File(System.getProperty("UPLOAD_LOCATION")+"/"+fileName);
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
			File f = new File(WebUtility.createFolder(this.configProperties.getProperty("teachers.upload.path") + "/"
					+ dateFormat.format(date) + ".csv"));
			boolean flag = f.createNewFile();
			log.info("fileCreated>>Teacher" + "\t" + flag);
			log.info("f.getPath() Teachers Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
			file.transferTo(f);
			log.info("FilePath" + "\t" + f.getAbsolutePath());

			Integer accountId = (Integer) this.httpSession.getAttribute("accountId");
			List<TeacherCSVModel> list = TeacherCSVToJavaUtil.convertTeachersCsvToJava(f);
			if (list == null) {
				model.addObject(Constant.TEACHER_INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("addTeacher");
				return model;
			}
			List<String> errorList = new ArrayList<>();
			for (int i = 1; i < list.size(); i++) {
				log.info("Into For Loop");
				boolean isUserExists = this.userService.isUsersExist(list.get(i).getUsername());

				if (!isUserExists) {
					if ((list.get(i).getName().trim().length() != 0) && (list.get(i).getUsername().trim().length() != 0)
							&& (list.get(i).getPassword().trim().length() != 0)
							&& (list.get(i).getUuid().trim().length() != 0)) {
						log.info(
								"list.get(i).getUuid().trim().length()" + "\t" + list.get(i).getUuid().trim().length());

						ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
						activityLogs.info(activityLog);
					} else {
						log.info("Into Else of Upload if if");
						errorList.add("Row No: " + i + "Failed To Upload");
						log.info("errorList::" + "\t" + errorList);
						redirectAttributes.addFlashAttribute("errorList", errorList);
					}
				} else {
					log.info("Into Else of Upload");
					errorList.add("Row No: " + i + "Failed To Upload");
					log.info("errorList::" + "\t" + errorList);
					redirectAttributes.addFlashAttribute("errorList", errorList);
				}
			}
		}
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return new ModelAndView("redirect:/teacherslistview?token=" + sessionID);
	}

	@RequestMapping(value = "/teacherStaffUpload", produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView teacherStaffUpload(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			@RequestParam("fileUpload") MultipartFile file, RedirectAttributes redirectAttributes, BindingResult result,
			@RequestParam("token") String sessionID) throws IOException {

		Map<Integer, Object> ignoredMap = null;
		List<Object> outerEventList = new ArrayList<Object>();
		String jsonString = null;
		List<TeachersStaffCSVModel> teacherStaffList = null;

		String redirectURL = "redirect:/schoolAccountManagement?token=" + sessionID;
		int totalTeacherStaffCount = 0;
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

		Users user = userService.getUserBySessionId(sessionID);
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

		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		String fileName = null;
		if (file != null) {
			fileName = file.getOriginalFilename();
			log.info("FileName" + "\t" + fileName);
		}

		String extn = fileName.substring(fileName.lastIndexOf(".") + 1);
		log.info("extn" + "\t" + extn);

		Date date = new Date();
		String os = System.getProperty("os.name");
		if (os.contains("Windows"))
			dbDateTime = dbDateTime.replace(":", "-");
		SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
		Integer schoolId = user.getAccounts().getAccountId();
		try {
			File f = new File(WebUtility.createFolder(this.configProperties.getProperty("teachers.upload.path") + "/"
					+ schoolId + '_' + dateFormat.format(date) + ".csv"));

			boolean flag = f.createNewFile();
			log.info("fileCreated>>Teacher" + "\t" + flag);
			log.info("f.getPath() Teachers Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
			file.transferTo(f);
			log.info("FilePath" + "\t" + f.getAbsolutePath());

			try {
				teacherStaffList = TeachersStaffCSVToJavaUtil.convertTeachersStaffCsvToJava(f);
				/*
				 * for (TeachersStaffCSVModel teachersStaffCSVModel :
				 * teacherStaffList) { log.info("TeachersStaffCSVModel" + "\n" +
				 * teachersStaffCSVModel.toString()); }
				 */
				totalTeacherStaffCount = teacherStaffList.size();

				/*
				 * log.info("teacherStaffList.size()" + "\t" +
				 * totalTeacherStaffCount); if (teacherStaffList.size() > 0)
				 * log.info("teacherStaffList size in Teacher Controller" + "\t"
				 * + teacherStaffList.size());
				 */
			} catch (NullPointerException e) {
				log.info("Exception while dealing with headers" + e);
				if (teacherStaffList == null || teacherStaffList.size() == 0) {
					model.addObject(Constant.TEACHER_INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
					model.addObject(Constant.SessionID, sessionID);
					model.addObject(Constant.FirstName, user.getName());
					model.setViewName("schoolAccountManagement");
					return model;
				}
			}
		} catch (Exception e) {
			log.info("Error On Console" + "\t" + e);
			model.addObject("invalidFile", "Errors in Uploaded File, CSV Upload Failed");
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("schoolAccountManagement");
			return model;
		}
		Map<String, Object> teacherStaffCreateOrUpdateMap = null;
		Integer totalTeacherStaffCreated = 0;
		Integer totalTeacherStaffUpdated = 0;
		Integer totalTeacherStaffFailed = 0;

		if (teacherStaffList.size() > 0) {
			teacherStaffCreateOrUpdateMap = this.teacherService.createOrUpdateTeachersStaff(teacherStaffList, user);

			totalTeacherStaffCreated = (Integer) teacherStaffCreateOrUpdateMap.get("teacherStaffCreated");
			totalTeacherStaffUpdated = (Integer) teacherStaffCreateOrUpdateMap.get("teacherStaffUpdated");
			totalTeacherStaffFailed = (Integer) teacherStaffCreateOrUpdateMap.get("teacherStaffFailed");

			log.info("totalTeacherStaffCreated" + "\t" + totalTeacherStaffCreated);
			log.info("totalTeacherStaffUpdated" + "\t" + totalTeacherStaffUpdated);
			log.info("totalTeacherStaffFailed" + "\t" + totalTeacherStaffFailed);

			Map<Object, Object> outerZoneMap = new HashMap<Object, Object>();

			ignoredMap = (HashMap<Integer, Object>) teacherStaffCreateOrUpdateMap.get("teacherStaffFailedMap");

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
			log.info("***ErroJsonString***" + jsonString);
		}

		redirectAttributes.addFlashAttribute(Constant.TEACHER_CSV_SUCCESS, Constant.CSV_SUCCESS_MSG);
		redirectAttributes.addFlashAttribute("teacherStaffRecordCount",
				"Total Records(" + totalTeacherStaffCount + "), Updated(" + totalTeacherStaffUpdated + "), Created("
						+ totalTeacherStaffCreated + "), Ignored(" + totalTeacherStaffFailed + ")");

		if (totalTeacherStaffFailed > 0) {
			redirectAttributes.addFlashAttribute("ignoredList", jsonString);
		}

		return new ModelAndView(redirectURL);
	}

	@RequestMapping(value = "/teacherslistview", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView listTeachers(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = userService.validateUserBySession(sessionID);
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
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		Integer userId = (Integer) this.httpSession.getAttribute("userId");
		this.httpSession.setAttribute("pageNo", 1);
		List<TeachersTransform> teachersList = this.teacherService.listAllTeachers(userId);
		model.addObject("teachersList", teachersList);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("teachersList");
		return model;
	}

	@RequestMapping(value = "/addTeacher", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView addTeacher(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			@RequestParam("token") String sessionID) {

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		/**
		 * session object setting is required if we dont want to show username
		 * and password in the form
		 */

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		this.httpSession.setAttribute("Add", "Add");
		this.httpSession.setAttribute("Added", "Teacher Added Successfully");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("addTeacher");
		return model;
	}

	@RequestMapping(value = "/saveTeacher", method = RequestMethod.POST)
	public ModelAndView saveTeacher(ModelAndView model,
			@ModelAttribute("usersModel") TeacherStaffModel teacherStaffModel, RedirectAttributes redirectAttributes,
			BindingResult result, @RequestParam("token") String sessionID) {

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

		log.info("In UserController>>saveTeacher()");
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		String redirectURL = "";
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		/**
		 * session object setting is required if we dont want to show username
		 * and password in the form
		 */

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		log.info("teacherStaffModel.getAccMgtTeacherGrade()" + "\t" + teacherStaffModel.getAccMgtTeacherGrade());
		log.info("teacherStaffModel.getAccTeacherStaffEmail()" + "\t" + teacherStaffModel.getAccTeacherStaffEmail());
		log.info("teacherStaffModel.getAccTeacherStaffName()" + "\t" + teacherStaffModel.getAccTeacherStaffName());
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return new ModelAndView(redirectURL);
	}

	@RequestMapping(value = "/saveTeacherOrStaff", method = RequestMethod.POST)
	public ModelAndView saveTeacherOrStaff(ModelAndView model,
			@ModelAttribute("teacherStaffModel") TeacherStaffModel teacherStaffModel,
			RedirectAttributes redirectAttributes, @RequestParam("token") String sessionID) {
		log.info("sessionID in saveTeacherOrStaff" + "\t" + sessionID);
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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		log.info("In UserController>>saveTeacher()");
		String redirectURL = "redirect:/schoolAccountManagement?token=" + sessionID;
		Users user = userService.getUserBySessionId(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		boolean isTeacherExist = this.userService.isUsersExist(teacherStaffModel.getAccTeacherStaffEmail());
		String currentUser = user.getRoleType();
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (isTeacherExist) {
			redirectAttributes.addFlashAttribute(Constant.ACCOUNT_EXISTS, Constant.ACCOUNT_EXISTS_MSG);
			return new ModelAndView(redirectURL);
		}
		log.info("Before Entering into createTeachersOrStaffForWeb()");
		String teacherCreatedorUpdated = "";
		try {
			teacherCreatedorUpdated = this.teacherService.createTeachersOrStaffForWeb(teacherStaffModel, user);
			if (teacherCreatedorUpdated.equals("Create"))
				redirectAttributes.addFlashAttribute(Constant.CREATE_ACCOUNT_SUCCESS,
						Constant.CREATE_ACCOUNT_SUCCESS_MSG);
			else if (teacherCreatedorUpdated.equals("Update"))
				redirectAttributes.addFlashAttribute(Constant.CREATE_ACCOUNT_SUCCESS,
						Constant.UPDATE_ACCOUNT_SUCCESS_MSG);
		} catch (Exception e) {
			log.info("Exception Occured While creating teachers" + "\n" + e);
			redirectAttributes.addFlashAttribute(Constant.ACCOUNT_FAILED, Constant.ACCOUNT_FAILED_MSG);
		}

		log.info("Account Created " + "\t" + teacherCreatedorUpdated);

		return new ModelAndView(redirectURL);
	}

	@RequestMapping(value = "/web/filterTeacherStaff/{sessionID}/{roleType}/{grade}/{pageid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String filterTeacherStaff(@PathVariable String sessionID, @PathVariable String roleType,
			@PathVariable String grade, @PathVariable("pageid") int pageid) throws ParseException {

		log.info("Into filterTeacherStaff() { ");

		Map<Object, Object> map = null;
		List myList = new ArrayList<>();
		String responseJSON = null;
		String jsonString = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int currentPage = pageid;
		int noofpages = 0;
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			if (user.getRoleType().equals(Constant.SchoolAdmin)) {
				if (pageid != 1) {
					pageid = (pageid - 1) * total + 1;
				}
				if (roleType == null || roleType.equals("") || roleType.trim().length() == 0)
					roleType = "ALL";
				if (grade == null || grade.equals("") || grade.trim().length() == 0)
					grade = "ALL";

				log.info("roleType in filterTeacherStaff" + "\t" + roleType);
				log.info("grade in filterTeacherStaff" + "\t" + grade);
				List<TeachersTransform> teacherTfrmList1 = this.teacherService.findTeachersStaffList(user.getId(),
						roleType, grade, 0, 0);
				List<TeachersTransform> teacherTfrmList = this.teacherService.findTeachersStaffList(user.getId(),
						roleType, grade, pageid, total);

				int listSize = teacherTfrmList1.size();

				if (listSize > 0 && pageid == 1) {
					if (listSize < total)
						noofpages = 1;
					else if (listSize % total == 0)
						noofpages = listSize / total;
					else
						noofpages = listSize / total + 1;
					this.httpSession.setAttribute("filterTeacherpages", String.valueOf(noofpages));
				}

				for (TeachersTransform tt : teacherTfrmList) {
					map = new LinkedHashMap<>();
					if (tt.getTeacherId() != null) {
						map.put("teacherstaff_id", tt.getTeacherId());
					}

					if (tt.getName() != null) {
						map.put("teacherstaff_name", tt.getName());
					}

					if (tt.getStatus() != null) {
						map.put("teacherstaff_status", String.valueOf(tt.getStatus()));
					}

					if (tt.getrType() != null && tt.getrType().equals("school_teacher")) {
						map.put("teacherstaff_role", "Teacher");
					} else if (tt.getrType() != null && tt.getrType().equals("school_staff")) {
						map.put("teacherstaff_role", "Staff");
					}

					if (tt.getStclass() != null) {
						map.put("teacherstaff_class", tt.getStclass());
					}

					if (tt.getGrade() != null) {
						map.put("teacherstaff_grade", tt.getGrade());
					}

					if (tt.getContact_no() != null) {
						map.put("teacherstaff_contactno", tt.getContact_no());
					}

					if (tt.getUsername().toLowerCase() != null) {
						map.put("teacherstaff_username", tt.getUsername().toLowerCase());
					}

					map.put("noofPages", (String) this.httpSession.getAttribute("filterTeacherpages"));
					map.put("currentPage", currentPage);

					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				responseJSON = ErrorCodesUtil.displayJSONForTeachersStaff(Constant.SucessMsgActivity,
						Constant.StatusCodeActivity, myList.toString(), Constant.TEACHER_CREATE_API_TYPE);
				log.info("***studentsRespJson In studentsWebListAPI***" + "\t" + responseJSON);
				log.info("Exiting teachersStaffWebListAPI() } ");
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
						Constant.UnauthorisedUserErrorCodeDescription, Constant.TEACHER_CREATE_API_TYPE);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.StatusCodeJon, Constant.ErrorMessageJon,
					Constant.TEACHER_CREATE_API_TYPE);
			return responseJSON;
		}
		log.info("Exiting filterTeacherStaff");
		return responseJSON;
	}

	@RequestMapping(value = "/editTeacher", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editTeacher(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			@RequestParam("token") String sessionID) {

		Users sessionUser = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(sessionUser);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		/**
		 * session setting is required if we dont want to show username and
		 * password values in the form
		 */

		if ((null == this.httpSession.getAttribute("currentUser"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		Users user = this.teacherService.getTeacher(usersModel.getId());

		this.httpSession.setAttribute("edit", "edit");
		this.httpSession.setAttribute("username", user.getUsername());
		this.httpSession.setAttribute("password", user.getPassword());

		UsersModel um = this.teacherModelEntity.prepareTecherModel(this.teacherService.getTeacher(usersModel.getId()));
		log.info("userModel :::::::::::::::::::::::::::::::::::::::: " + um.getUsername());
		model.addObject("usersModel", um);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("addTeacher");
		return model;
	}

	@RequestMapping(value = "/deleteTeacherStaff/{token}/{teacherId}")
	public String deleteTeacherStaff(@PathVariable("token") String sessionID,
			@PathVariable("teacherId") String teacherId) {

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

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String respondJson = null;
		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {

			if (user.getRoleType().equals(Constant.SchoolAdmin)) {
				this.teacherService.deleteTeacherStaff(Integer.parseInt(teacherId));

				statusCode = "SUC01";
				statusMsg = "API Request Success";
				type = "teacher.deleteTeacherStaff";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, respondJson);
				// return respondJson;

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "teacher.deleteTeacherStaff";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, respondJson);
				// return respondJson;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "teacher.deleteTeacherStaff";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, respondJson);
			// return respondJson;
		}
		return respondJson;
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView next(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = userService.validateUserBySession(sessionID);
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
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		List<UsersModel> teachersList = null;
		Integer pageNo = (Integer) this.httpSession.getAttribute("pageNo");
		pageNo++;
		this.httpSession.setAttribute("pageNo", pageNo);
		this.httpSession.setAttribute("next", "next");
		log.info("List Size()" + "\t" + this.teacherService.listTeachersByPage(pageNo).size());
		if (this.teacherService.listTeachersByPage(pageNo).size() > 0) {
			log.info("Into If");
			Integer slNo = (Integer) this.httpSession.getAttribute("slNo");
			teachersList = this.teacherModelEntity
					.prepareTeachersModelList(this.teacherService.listTeachersByPage(pageNo), slNo);
		} else {
			log.info("Entering Into teachersList.size()==0");
			this.httpSession.setAttribute("pageNo", 1);
			teachersList = this.teacherModelEntity.prepareTeachersModelList(this.teacherService.listTeachersByPage(1),
					1);
			log.info("Leaving teachersList.size()==0");
		}
		model.addObject("teachersList", teachersList);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("teachersList");
		return model;
	}

	@RequestMapping(value = "/previous", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView previous(ModelAndView model, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
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
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		List<UsersModel> teachersList = null;
		Integer pageNo = (Integer) this.httpSession.getAttribute("pageNo");
		this.httpSession.setAttribute("previous", "previous");

		pageNo--;
		if (pageNo <= 0)
			pageNo = 1;

		this.httpSession.setAttribute("pageNo", pageNo);
		log.info("List Size()" + "\t" + this.teacherService.listTeachersByPage(pageNo).size());
		if (this.teacherService.listTeachersByPage(pageNo).size() > 0) {
			log.info("Into If");
			Integer slNo = (Integer) this.httpSession.getAttribute("slNo");
			teachersList = this.teacherModelEntity
					.prepareTeachersModelList(this.teacherService.listTeachersByPage(pageNo), slNo);
		} else {
			log.info("Entering Into teachersList.size()==0");
			this.httpSession.setAttribute("pageNo", 1);
			teachersList = this.teacherModelEntity.prepareTeachersModelList(this.teacherService.listTeachersByPage(1),
					1);
			log.info("Leaving teachersList.size()==0");
		}
		model.addObject("teachersList", teachersList);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("teachersList");
		return model;
	}

	@RequestMapping(value = "/listteachersbyid/{id}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ResponseEntity<List<Users>> listTeachers(@PathVariable int id) {
		List<Users> users = this.teacherService.listTeachersById(id);
		return new ResponseEntity<List<Users>>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateTeacherStaff/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String updateTeacherStaff(@PathVariable("id") Integer id, @RequestBody UsersModel usersModel) {

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

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String respondJson = null;
		Users teacher = this.userService.findUserById(id.intValue());

		if (null != teacher) {
			if (teacher.getRoleType().equals(Constant.SchoolTeacher)
					|| teacher.getRoleType().equals(Constant.SchoolStaff)) {

				ActivityLog activityLog = CommonUtil.formulateActivityLogs(teacher, action, summary, ipaddress);
				this.teacherService.updateTeacherStaffApi(teacher, null, usersModel);
				activityLogs.info(activityLog);

				statusCode = "SUC01";
				statusMsg = "API Request Success";
				type = "teacher.updateTeacherStaff";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, respondJson);
				// return respondJson;

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "teacher.updateTeacherStaff";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, respondJson);
				// return respondJson;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "teacher.updateTeacherStaff";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, respondJson);
			// return respondJson;
		}
		return respondJson;
	}

	public Users prepareUsersEntityForRest(UsersRestModel usersRestModel, Users teacher) {
		Accounts accounts = new Accounts();
		accounts.setAccountId(usersRestModel.getAccounts().getAccountId());
		accounts.setAccountType(usersRestModel.getAccounts().getAccountType());

		if (usersRestModel.getId() > 0)
			teacher.setId(usersRestModel.getId());
		if (usersRestModel.getName() != null)
			teacher.setName(usersRestModel.getName());

		if (usersRestModel.getUsername() != null)
			teacher.setUsername(usersRestModel.getUsername());
		if (usersRestModel.getPassword() != null)
			teacher.setPassword(usersRestModel.getPassword());
		teacher.setAccounts(accounts);
		return teacher;
	}

	@RequestMapping(value = "/mobile/SchoolUserQuietHoursList/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SchoolUserQuietHoursList(@PathVariable("sessionID") String sessionID) {

		JSONArray jsonArray = new JSONArray();
		List myList = new ArrayList<>();
		String jsonArrayString = null;
		String ErrorMessage = null;
		String SuccessMessage = null;
		String StatusCode = null;
		String responseJSON = null;

		String type = "user.SchoolUserQuietHoursList";
		log.info("logged our session Id" + sessionID);

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			log.info("user rloe" + user.getRoleType());
			if (!(user.getRoleType().equals("school_teacher"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				// return responseJSON;
			}
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				// return responseJSON;
			}
			log.info("userId" + user.getId());

			jsonArray = null;

			jsonArrayString = jsonArray.toString();
			log.info("jsonArrayString : " + "\n" + jsonArrayString);

			StatusCode = "SUC01";
			SuccessMessage = "API Request Success";
			responseJSON = ErrorCodesUtil.displaySuccessJSONForSchoolAppPrefList(type, SuccessMessage, StatusCode,
					myList.toString());
			// JSONUtility.respondAsJSON(response, responseJSON);
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/SchoolUserQuietHoursDelete/{sessionID}", method = RequestMethod.DELETE, produces = {
			"application/json" })
	public String SchoolUserQuietHoursDelete(@RequestBody SchoolPrefModel schoolPref,
			@PathVariable("sessionID") String sessionID) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String SucessMessage = null;
		String type = "user.SchoolUserQuietHoursDelete";

		String json = JSONUtility.convertObjectToJson(schoolPref);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			ErrorMessage = "Input Is Invalid";
			StatusCode = "WERR05";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		List<String> prefDay = new ArrayList<String>();
		prefDay.add("weekday");
		prefDay.add("weekend");

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

		log.info("logged our session Id" + sessionID);

		if (schoolPref.getQuiteHoursDay().isEmpty()) {
			StatusCode = "ERR04";
			ErrorMessage = "Empty input , please provide preferences to update ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}
		if (!prefDay.contains(schoolPref.getQuiteHoursDay())) {
			StatusCode = "ERR05";
			ErrorMessage = "Invalid input  , please provide valid day as 'weekday' or 'weekend' ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {

			if (!(user.getRoleType().equals("school_teacher"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			teacherService.deletetSchoolAppPreferencesByUserId(user.getId(), schoolPref.getQuiteHoursDay());
			activityLogs.info(activityLog);

			StatusCode = "";
			ErrorMessage = "";
			responseJSON = ErrorCodesUtil.displaySuccessJSON(SucessMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;

		} else {
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			activityLogs.error(activityLog);
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/SchoolUserQuietHoursUpdate/{sessionID}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String SchoolUserQuietHoursUpdate(@RequestBody SchoolPrefModel schoolPref,
			@PathVariable("sessionID") String sessionID) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "user.SchoolUserQuietHoursUpdate";

		String json = JSONUtility.convertObjectToJson(schoolPref);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			ErrorMessage = "Input Is Invalid";
			StatusCode = "WERR05";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		List<String> prefDay = new ArrayList<String>();
		prefDay.add("weekday");
		prefDay.add("weekend");

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
		log.info("logged our session Id" + sessionID);

		if (schoolPref.getQuiteHoursDay().isEmpty() || schoolPref.getQuiteHoursFrom().toString().isEmpty()
				|| schoolPref.getQuiteHoursTo().toString().isEmpty()) {
			StatusCode = "ERR04";
			ErrorMessage = "Empty input , please provide preferences to update ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}
		if (!prefDay.contains(schoolPref.getQuiteHoursDay())) {
			StatusCode = "ERR05";
			ErrorMessage = "Invalid input  , please provide valid day as 'weekday' or 'weekend' ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {

			try {

				if (!(user.getRoleType().equals("school_teacher"))) {
					StatusCode = "ERR03";
					ErrorMessage = "Unauthorised User ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					StatusCode = "ERR02";
					ErrorMessage = "Session Expired ,Please Relogin ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}

				user = null;
			} catch (Exception e) {
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			activityLogs.error(activityLog);
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/StudentRewardsByTeacher/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentRewardsByTeacher(@PathVariable("sessionID") String sessionID) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "teacher.StudentRewardsByTeacher";
		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;

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
			if (!(user.getRoleType().equals(Constant.SchoolTeacher))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}
			List<TeacherRewardsAssignedToStudentsTransform> teacherRewardsAssignedToStudents = this.teacherService
					.StudentRewardsByTeacher(user.getId(), user.getAccountId());

			for (TeacherRewardsAssignedToStudentsTransform trst : teacherRewardsAssignedToStudents) {
				map = new LinkedHashMap<>();
				if (trst.getStudent_reward_id() != null) {
					map.put("student_reward_id", trst.getStudent_reward_id());
				}
				if (trst.getReward_id() != null) {
					map.put("reward_id", trst.getReward_id());
				}
				if (trst.getName() != null) {
					map.put("reward_name", trst.getName());
				}
				if (trst.getReward_icon_url() != null) {
					map.put("reward_icon_url",
							this.configProperties.getProperty("downloads.url")
									+ this.configProperties.getProperty("rewards.download.path") + "/"
									+ trst.getReward_icon_url());
				}
				if (trst.getCategory_name() != null) {
					map.put("category_name", trst.getCategory_name());
				}
				if (trst.getReceived_count() != null) {
					map.put("received_count", trst.getReceived_count());
				}
				if (trst.getCategory_icon_url() != null) {
					map.put("category_icon_url",
							this.configProperties.getProperty("downloads.url")
									+ this.configProperties.getProperty("rewards.download.path") + "/"
									+ trst.getCategory_icon_url());
				}
				if (trst.getReward_icon_url() != null) {
					map.put("reward_icon_url",
							this.configProperties.getProperty("downloads.url")
									+ this.configProperties.getProperty("rewards.download.path") + "/"
									+ trst.getReward_icon_url());
				}
				if (trst.getReward_date() != null) {
					map.put("reward_date", trst.getReward_date().toString());
				}
				if (trst.getStudent_id() != null) {
					map.put("student_id", trst.getStudent_id());
				}
				if (trst.getStudent_name() != null) {
					map.put("student_name", trst.getStudent_name());
				}
				jsonString = JSONObject.toJSONString(map);
				myList.add(jsonString);
			}
			log.info("***myList***" + "\n" + myList);
			StatusCode = "SUC01";
			responseJSON = ErrorCodesUtil.displayJSONForAssignedRewardsByTeacher(StatusCode, "", myList.toString());
			log.info("***teacherRewardsJson***" + "\t" + responseJSON);
			return responseJSON;
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/mobile/SchoolRewardsList/{sessionID}/{categoryId}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SchoolRewardsList(@PathVariable("sessionID") String sessionID, @PathVariable String categoryId) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "teacher.SchoolRewardsList";
		String jsonString = null;
		List myList = new ArrayList<>();
		List myList1 = new ArrayList<>();
		Map<Object, Object> map = null;
		Map<Object, Object> outerMap = null;
		Map<Object, Object> innerMap = null;

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
			if (!(user.getRoleType().equals("school_teacher") || user.getRoleType().equals("school_admin"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			int account_id = user.getAccountId();
			/*
			 * if (user.getAccounts().getAccountId() != account_id) { StatusCode
			 * = "ERR17"; ErrorMessage = "Invalid schoolId provided";
			 * responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage,
			 * StatusCode, type); //JSONUtility.respondAsJSON(response,
			 * responseJSON); return responseJSON; }
			 */

			if (categoryId != null && categoryId.equalsIgnoreCase("0")) {
				log.info("inside 0 only category list displayed");
				List<RewardsCategory> rewardsCategoryListTransform = this.rewardsService
						.getRewardsCategoryList(account_id);
				if (rewardsCategoryListTransform.size() > 0) {
					for (RewardsCategory trst : rewardsCategoryListTransform) {
						map = new LinkedHashMap<>();
						if (Integer.valueOf(trst.getRewards_category_id()) != null) {
							map.put("category_id", trst.getRewards_category_id());
						}
						if (trst.getCategory_name() != null) {
							map.put("category_name", trst.getCategory_name());
						}
						if (trst.getCategory_icon_url() != null) {
							log.info("url created " + this.configProperties.getProperty("downloads.url")
									+ this.configProperties.getProperty("rewards.download.path") + "/"
									+ trst.getCategory_icon_url());
							map.put("category_icon_url",
									this.configProperties.getProperty("downloads.url")
											+ this.configProperties.getProperty("rewards.download.path") + "/"
											+ trst.getCategory_icon_url());
						}
						jsonString = JSONObject.toJSONString(map);
						myList.add(jsonString);
					}

				}

			} else {
				List<TeacherRewardsListTransform> teacherRewardsListTransform = teacherService
						.getSchoolRewardsToTeacherBySchoolId(account_id, Integer.parseInt(categoryId));
				if (teacherRewardsListTransform.size() > 0) {
					TeacherRewardsListTransform trst1 = teacherRewardsListTransform.get(0);
					map = new LinkedHashMap<>();
					if (trst1.getCategory_name() != null) {
						map.put("category_name", trst1.getCategory_name());
					}
					if (trst1.getCategory_icon_url() != null) {
						map.put("category_icon_url",
								this.configProperties.getProperty("downloads.url")
										+ this.configProperties.getProperty("rewards.download.path") + "/"
										+ trst1.getCategory_icon_url());
					}
					if (teacherRewardsListTransform.size() > 0) {
						for (TeacherRewardsListTransform trst : teacherRewardsListTransform) {
							innerMap = new LinkedHashMap<>();
							if (trst.getReward_id() != null) {
								innerMap.put("reward_id", trst.getReward_id());
							}
							if (trst.getName() != null) {
								innerMap.put("reward_name", trst.getName());
							}
							if (trst.getReward_icon_url() != null) {
								innerMap.put("reward_icon_url",
										this.configProperties.getProperty("downloads.url")
												+ this.configProperties.getProperty("rewards.download.path") + "/"
												+ trst.getReward_icon_url());
							}
							log.info("inner map  " + innerMap.toString());

							myList1.add(innerMap);
							log.info("inner map  added to outerMap " + myList1.toString());

						}
					}
					map.put("rewards", myList1);
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
			}
			log.info("***myList***" + "\n" + myList);
			StatusCode = "SUC01";
			responseJSON = ErrorCodesUtil.displayJSONForRewardsList(StatusCode, "", myList.toString());
			log.info("***teacherRewardsJson***" + "\t" + responseJSON);
			// JSONUtility.respondAsJSON(response, teacherRewardsJson);
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			log.info("***teacherRewardsJson***" + "\t" + responseJSON);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/StudentRewardsAssign/{sessionID}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String StudentRewardsAssign(@PathVariable("sessionID") String sessionID,
			@RequestBody TeacherRewardsMultipleAssignModel teacherRewardsMultipleAssignModel) {

		String statusMsg = null;
		String statusCode = null;
		String responseJSON = null;
		String type = "teacher.StudentRewardsAssign";

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String json = JSONUtility.convertObjectToJson(teacherRewardsMultipleAssignModel);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			statusMsg = "Input Is Invalid";
			statusCode = "ERR05";
			type = "teacher.StudentRewardsAssign";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);

		if (user != null) {
			if (user.getRoleType().equals("school_teacher")) {

				List<Integer> schoolRewardIds = teacherService.getRewardsByAccountid(user.getAccountId());
				if (schoolRewardIds.size() == 0) {
					statusMsg = "No Reawards found";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, "ERR14", Constant.StudentRewardsAssign);
					return responseJSON;
				}
				List<Integer> studentIds = teacherRewardsMultipleAssignModel.getStudent_ids();
				List<Integer> rewardIds = teacherRewardsMultipleAssignModel.getReward_ids();

				/*
				 * for (int reward_id: rewardIds) {
				 * if(!schoolRewardIds.contains(reward_id))
				 * rewardIds.remove(reward_id); }
				 */

				if (rewardIds.size() == 0) {
					statusMsg = "No Valid Reawards found";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, "ERR33", Constant.StudentRewardsAssign);
					return responseJSON;
				}

				if (teacherRewardsMultipleAssignModel.getReceived_count() != null
						&& teacherRewardsMultipleAssignModel.getReceived_count() <= 0) {
					log.info("Received Count is invalid >>>>>>>>>>>>>>>>>>>>>");
					statusMsg = "Invalid Parameters specified. Received Count must be greater than 0";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, Constant.StatusCode,
							Constant.StudentRewardsAssign);
					return responseJSON;

				}
				if ((teacherRewardsMultipleAssignModel.getStudent_ids() != null
						&& teacherRewardsMultipleAssignModel.getStudent_ids().size() > 0)
						&& (teacherRewardsMultipleAssignModel.getReward_ids() != null
								&& teacherRewardsMultipleAssignModel.getReward_ids().size() > 0)
						&& (teacherRewardsMultipleAssignModel.getReceived_count() != null)) {

					Set<String> errorStudentIds = new HashSet<>();
					Set<String> errorRewardIds = new HashSet<>();
					Set<Integer> errorReceivedCounts = new HashSet<>();

					Integer count = 0;
					for (Integer studentId : studentIds) {
						for (int i = 0; i < rewardIds.size(); i++) {
							ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary,
									ipaddress);

							log.info(
									"teacherRewardsMultipleAssignModel.getReceived_count() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "
											+ teacherRewardsMultipleAssignModel.getReceived_count());
							if (teacherRewardsMultipleAssignModel.getReceived_count() != null) {
								count = teacherRewardsMultipleAssignModel.getReceived_count().intValue();
								log.info("inside if count is  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + count);
							}

							log.info("passed count as  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + count);
							String errorMsg = this.teacherService.assignRewardsToStudentsByTeacher(
									(Integer) rewardIds.get(i), studentId, count, user.getId());

							activityLogs.info(activityLog);
							if (errorMsg.equals("Success")) {
							} else if (errorMsg.contains("rewardIds")) {
								errorRewardIds.add(errorMsg.replace("rewardIds", "").trim());
							} else {
								errorStudentIds.add(errorMsg);
							}
						}
					}
					if ((errorStudentIds != null && errorStudentIds.size() > 0)
							|| (errorRewardIds != null && errorRewardIds.size() > 0)
							|| errorReceivedCounts != null && errorReceivedCounts.size() > 0) {
						statusCode = "ERR05";
						StringBuffer strResponse = new StringBuffer();
						if (errorStudentIds != null && errorStudentIds.size() > 0) {
							strResponse.append(errorStudentIds.toString()
									+ ": invalid student id(s) and rewards cannot be assigned \n");
						}
						if (errorRewardIds != null && errorRewardIds.size() > 0) {
							strResponse.append(
									errorRewardIds.toString() + ": rewardId(s) are invalid and doesn't exist. \n");
						}
						if (errorReceivedCounts != null && errorReceivedCounts.size() > 0) {
							strResponse.append(errorReceivedCounts.toString()
									+ ": received count is invalid. Received count must be greater than 0");
						}
						responseJSON = ErrorCodesUtil.displayErrorJSON(strResponse.toString(), statusCode, type);
						return responseJSON;
					}

					statusCode = "SUC01";
					statusMsg = "API Request Success";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				} else {
					statusCode = "ERR05";
					statusMsg = "Invalid Parameters specified";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				}
			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				return responseJSON;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/mobile/ReAssignRewards/{sessionID}/{student_rewardId}", method = RequestMethod.PUT, produces = {
			"application/json" })
	public String ReAssignRewards(@PathVariable("sessionID") String sessionID,
			@PathVariable("student_rewardId") int student_rewardId,
			@RequestBody TeacherRewardsToStudentModel teacherRewardsToStudentModel) {
		log.debug("Inside ReAssignMethod:::::::");

		String statusMsg = null;
		String statusCode = null;
		String responseJSON = null;
		String type = "teacher.ReAssignRewards";

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

		String json = JSONUtility.convertObjectToJson(teacherRewardsToStudentModel);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			statusMsg = "Input Is Invalid";
			statusCode = "ERR05";
			type = "teacher.ReAssignRewards";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (user.getRoleType().equals("school_teacher")) {
				log.debug("RewardsIDDDDDDDDDDD" + student_rewardId);
				Integer studentId = teacherRewardsToStudentModel.getStudent_id();
				log.debug("StudentIDDDDDD:::::::::::::::" + studentId);
				try {
					// validation for studentId
					int sId = studentId.intValue();
					if (sId <= 0) {
						statusMsg = "Input Is Invalid. StudentId must be greater than 0";
						statusCode = "ERR05";
						type = "teacher.ReAssignRewards";
						responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
						return responseJSON;
					}

				} catch (Exception nfe) {
					statusMsg = "Input Is Invalid. StudentId is invalid";
					statusCode = "ERR05";
					type = "teacher.ReAssignRewards";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				}
				Integer receivedCount = teacherRewardsToStudentModel.getReceived_count();
				log.debug("RECEIVED COUNT" + receivedCount);

				try {
					// validation for the received count
					int rc = receivedCount.intValue();
					if (rc <= 0) {
						statusMsg = "Input Is Invalid. Received Count must be greater than 0";
						statusCode = "ERR05";
						type = "teacher.ReAssignRewards";
						responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
						return responseJSON;
					}
				} catch (Exception nfe) {
					statusMsg = "Input Is Invalid. Received Count must be greater than 0";
					statusCode = "ERR05";
					type = "teacher.ReAssignRewards";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				}

				int schoolId = this.teacherService.getAccountId(studentId);
				if (schoolId == 0) {
					statusCode = "ERR06";
					statusMsg = "Student Id doesn't exist.";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				}

				if (schoolId != user.getAccountId()) {
					statusCode = "ERR06";
					statusMsg = "Student Id doesn't exist.";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				}

				StudentsListTransform studentsListTransform = new StudentsListTransform();
				studentsListTransform.setStudentId(studentId.intValue());

				if (!this.teacherService.checkStudentRewardId(student_rewardId)) {
					statusCode = "ERR05";
					statusMsg = "Student Reward Id doesn't exist.";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				}

				if (this.rewardsService.updateRewardsAlreadyAssigned(studentsListTransform, user, student_rewardId,
						receivedCount)) {
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					// return responseJSON;
				}

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				// JSONUtility.respondAsJSON(response, respondJson);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, respondJson);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/StudentRewardsDelete/{sessionID}/{student_rewardIds}", method = RequestMethod.DELETE, produces = {
			"application/json" })
	public String StudentRewardsDelete(@PathVariable("sessionID") String sessionID,
			@PathVariable Integer[] student_rewardIds) {

		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "teacher.StudentRewardsDelete";

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

		Users user = userService.getUserByMobileSessionId(sessionID);

		log.info("user is " + user);

		if (null != user) {

			if (!(user.getRoleType().equals("school_teacher"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}

			List<Integer> studentRewardIds = Arrays.asList(student_rewardIds);

			Set<Integer> errorStudentRewardIds = new HashSet<>();

			if (null != studentRewardIds) {
				try {
					for (Integer srIds : studentRewardIds) {
						int stId = srIds.intValue();
						boolean flag = this.teacherService.checkStudentRewardIdUnderToTeacher(stId, user.getId());
						log.debug("invalid rewards flag :" + flag);
						if (!flag) {
							errorStudentRewardIds.add(stId);
							continue;
						}
					}
				} catch (NumberFormatException nfe) {
					StatusCode = "ERR05";
					ErrorMessage = "Invalid Parameters specified";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					return responseJSON;
				} catch (Exception ex) {
					StatusCode = "ERR05";
					ErrorMessage = "Invalid Parameters specified";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					return responseJSON;
				}

				for (Integer srIds : studentRewardIds) {
					ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
					this.teacherService.deleteStudentsRewardsByTeacher(srIds, user.getId());
					activityLogs.info(activityLog);
				}

				if (errorStudentRewardIds.size() > 0) {
					ErrorMessage = "API Request Success. " + errorStudentRewardIds + " RewardIds are invalid";
				} else {
					ErrorMessage = "API Request Success";
				}

				StatusCode = "SUC01";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				return responseJSON;
			}

			StatusCode = "ERR05";
			ErrorMessage = "Invalid Parameters specified";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;

		} else {
			// ActivityLog activityLog= CommonUtil.formulateActivityLogs(user,
			// action, summary, ipaddress);
			// activityLogs.error(activityLog);
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			log.info("***teacherDeleteRewardsJson***" + "\t" + responseJSON);
			return responseJSON;

		}
	}

	@RequestMapping(value = "/uniqueRewardsStatisticsForTeacher")
	public ModelAndView uniqueRewardStatisticsForTeacher(HttpServletRequest request,
			@ModelAttribute("") StudentsModel studentsModel, ModelAndView model,
			@RequestParam("token") String sessionID) {
		Users users = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(users);
		log.info("sessionValidityResult :: " + sessionValidityResult);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		String day = request.getParameter("day");
		String studentClass = request.getParameter("uniqueClass");
		String studentName = request.getParameter("name");

		List<RewardStatisticsTransform> classList = this.teacherService
				.getClassNameForTeacher((int) users.getAccountId());
		List<String> classesList = new ArrayList<>();
		for (RewardStatisticsTransform list : classList) {
			classesList.add(String.valueOf(list.getStudentClass()));
		}

		List<RewardStatisticsTransform> rewardsList = this.teacherService.getStudentName((int) users.getAccountId(),
				day, studentClass, studentName);
		String imagePath = this.configProperties.getProperty("application.url")
				+ this.configProperties.getProperty("rewards.download.path");
		model.addObject("imagePath", imagePath);
		model.addObject("rewardsStatisticList", rewardsList);
		model.addObject("classList", classesList);
		model.setViewName("rewardsstatisticForTeacher");
		model.addObject("dateTime", this.configProperties.getProperty("display.dateTime"));
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, users.getName());
		return model;
	}

	@ResponseBody
	@RequestMapping(value = "/find-student-name", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getClassStudentForTeacher(HttpServletRequest request) {
		log.debug("START FROM AJAX");
		String getKey = null;
		List<RewardStatisticsTransform> classList = null;
		List<String> nameList = new ArrayList<>();
		StringBuilder classSb = null;
		try {
			getKey = request.getParameter("keyStudent");
			classList = new ArrayList<>();
			classList = (ArrayList<RewardStatisticsTransform>) this.rewardsService.getStudentNames(getKey);
			for (RewardStatisticsTransform studentName : classList) {
				nameList.add(studentName.getStudent_name());

			}

			classSb = new StringBuilder();
			classSb.append("<option value='NONE'>Show all Students</option>");
			for (String studentName : nameList) {
				classSb.append("<option >" + studentName + "</option>");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("END AJAX");
		return classSb.toString();
	}

	@RequestMapping(value = "/teachersStaffWebListAPI/{token}/{roleType}/{grade}/{pageid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String teachersStaffWebListAPI(@PathVariable("token") String sessionID, @PathVariable String roleType,
			@PathVariable String grade, @PathVariable("pageid") int pageid) throws ParseException {

		log.info("Into teachersStaffWebListAPI() { ");

		Map<Object, Object> map = null;
		List myList = new ArrayList<>();
		String responseJSON = null;
		String jsonString = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int currentPage = pageid;
		int noofpages = 0;
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			if (user.getRoleType().equals(Constant.SchoolAdmin)) {
				if (pageid != 1) {
					pageid = (pageid - 1) * total + 1;
				}

				log.info("roleType in teachersStaffWebListAPI" + "\t" + roleType);
				log.info("grade in teachersStaffWebListAPI" + "\t" + grade);

				List<TeachersTransform> teacherTfrmList1 = this.teacherService.findTeachersStaffList(user.getId(),
						roleType, grade, 0, 0);
				List<TeachersTransform> teacherTfrmList = this.teacherService.findTeachersStaffList(user.getId(),
						roleType, grade, pageid, total);

				int listSize = teacherTfrmList1.size();

				if (listSize > 0 && pageid == 1) {
					if (listSize < total)
						noofpages = 1;
					else if (listSize % total == 0)
						noofpages = listSize / total;
					else
						noofpages = listSize / total + 1;
					this.httpSession.setAttribute("teacherpages", String.valueOf(noofpages));
				}

				for (TeachersTransform tt : teacherTfrmList) {
					map = new LinkedHashMap<>();
					if (tt.getTeacherId() != null) {
						map.put("teacherstaff_id", tt.getTeacherId());
					}

					if (tt.getName() != null) {
						map.put("teacherstaff_name", tt.getName());
					}

					if (tt.getStatus() != null) {
						map.put("teacherstaff_status", String.valueOf(tt.getStatus()));
					}

					if (tt.getrType() != null && tt.getrType().equals("school_teacher")) {
						map.put("teacherstaff_role", "Teacher");
					} else if (tt.getrType() != null && tt.getrType().equals("school_staff")) {
						map.put("teacherstaff_role", "Staff");
					}

					if (tt.getStclass() != null) {
						map.put("teacherstaff_class", tt.getStclass());
					}

					if (tt.getGrade() != null) {
						map.put("teacherstaff_grade", tt.getGrade());
					}

					if (tt.getContact_no() != null) {
						map.put("teacherstaff_contactno", tt.getContact_no());
					}

					if (tt.getUsername().toLowerCase() != null) {
						map.put("teacherstaff_username", tt.getUsername().toLowerCase());
					}

					map.put("noofPages", (String) this.httpSession.getAttribute("teacherpages"));
					map.put("currentPage", currentPage);

					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				responseJSON = ErrorCodesUtil.displayJSONForTeachersStaff(Constant.SucessMsgActivity,
						Constant.StatusCodeActivity, myList.toString(), Constant.TEACHER_CREATE_API_TYPE);
				log.info("***studentsRespJson In studentsWebListAPI***" + "\t" + responseJSON);
				log.info("Exiting teachersStaffWebListAPI() } ");
				this.httpSession.setAttribute("role", "");
				this.httpSession.setAttribute("grade", "");
				return responseJSON;
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
						Constant.UnauthorisedUserErrorCodeDescription, Constant.TEACHER_CREATE_API_TYPE);
				return responseJSON;
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.StatusCodeJon, Constant.ErrorMessageJon,
					Constant.TEACHER_CREATE_API_TYPE);
			return responseJSON;
		}
	}
}
