package com.liteon.icgwearable.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.DeviceConfigurations;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.SchoolCalendar;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.hibernate.entity.SystemConfiguration;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.DeviceConfigModel;
import com.liteon.icgwearable.model.DeviceListModel;
import com.liteon.icgwearable.model.ParentModel;
import com.liteon.icgwearable.model.SchoolModel;
import com.liteon.icgwearable.model.TimeTableModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.service.AccountService;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.CassandraService;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.service.SchoolAdminService;
import com.liteon.icgwearable.service.SchoolService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.AccountFroSchoolTransform;
import com.liteon.icgwearable.transform.AdminTransform;
import com.liteon.icgwearable.transform.DeviceAccountTransform;
import com.liteon.icgwearable.transform.DeviceConfigTransform;
import com.liteon.icgwearable.transform.DeviceConfigurationsTransform;
import com.liteon.icgwearable.transform.DeviceStatsTransform;
import com.liteon.icgwearable.transform.SchoolScheduleTransform;
import com.liteon.icgwearable.transform.SchoolTransform;
import com.liteon.icgwearable.transform.SearchSchoolTransform;
import com.liteon.icgwearable.transform.SysConfigurationTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.DeviceCSVToJavaUtil;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.HolidayCSVToJavaUtil;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.StringUtility;
import com.liteon.icgwearable.util.WebUtility;
import com.liteon.icgwearable.validator.AccountValidator;
import com.liteon.icgwearable.validator.SchoolValidator;

/**
 * @author Rattaiah
 * 
 */

@RestController
public class AccountController {

	private static Logger log = Logger.getLogger(AccountController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private SchoolService SchoolService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private HttpSession httpSession;

	@Autowired(required = true)
	private SchoolAdminService schoolAdminService;

	@Autowired
	private SchoolValidator schoolValidator;

	@Autowired
	private AccountValidator accountValidator;

	@Autowired
	HttpServletResponse response;

	@Autowired
	private ActivityLogService activityLogs;
	
	@Autowired
	private CassandraService cassandraService;

	@Value("${PAGINATION_NO_OF_RECORDS}")
	private int PAGINATION_NO_OF_RECORDS;

	@Value("${db.dateFormat}")
	private String dbDateTime;

	@Resource(name = "configProperties")
	private Properties configProperties;

	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;

	private String ErrorMessage = null;
	private String StatusCode = null;
	private String responseJSON = null;
	String type = "device.viewStudentSleepData";

	@RequestMapping(value = "/onSuccess", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView adminDashBoard(ModelAndView model, @ModelAttribute("schoolModel") SchoolModel schoolModel) {
		model.setViewName("SchoolSuccess");
		return model;
	}

	@RequestMapping(value = "/schoolAccountManagement")
	public ModelAndView schoolAccountManagement(HttpServletRequest request, ModelAndView model,
			@RequestParam("token") String sessionID) {
		log.debug("schoolAccountManagement");
		log.info("Session iD from Add " + sessionID);

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

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("schoolAccountManagement");
		return model;
	}

	@RequestMapping(value = "/schoolFitness")
	public ModelAndView schoolFitness(ModelAndView model, @RequestParam("token") String sessionID) {

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

		log.debug("schoolFitness");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("schoolFitness");
		return model;
	}

	@RequestMapping(value = "/schoolSearchStudent")
	public ModelAndView schoolSearchStudent(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = this.userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
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

		if (null != currentUser
				&& !(currentUser.equals(Constant.SupprotStaff) || currentUser.equals(Constant.SchoolAdmin))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		String roleType = user.getRoleType();

		if (roleType.equals(Constant.SchoolAdmin)) {
			log.debug(Constant.SchoolSearchStudent);
			model.setViewName(Constant.SchoolSearchStudent);
		} else if (roleType.equals(Constant.SupprotStaff)) {
			model.setViewName(Constant.SupportStaffSearchStudent);
		}
		return model;
	}

	@RequestMapping(value = "/supportStaffParentSearch")
	public ModelAndView supportStaffParentSearch(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = this.userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		WebUtility webUtility = WebUtility.getWebUtility();

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			return webUtility.getAdminLoginPage();
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !currentUser.equals(Constant.SupprotStaff)) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("supportStaffParentSearch");
		return model;
	}

	@RequestMapping(value = "/supportStaffSchoolSearch")
	public ModelAndView supportStaffSchoolSearch(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = this.userService.validateUserBySession(sessionID);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		WebUtility webUtility = WebUtility.getWebUtility();

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			return webUtility.getAdminLoginPage();
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !currentUser.equals(Constant.SupprotStaff)) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		List<String> countyList = accountService.getCountys();
		model.addObject("countyList", countyList);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("supportStaffSchoolSearch");
		return model;
	}

	@RequestMapping(value = "/supportStaffDeviceSearch")
	public ModelAndView supportStaffDeviceSearch(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = this.userService.validateUserBySession(sessionID);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		WebUtility webUtility = WebUtility.getWebUtility();

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			return webUtility.getAdminLoginPage();
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !currentUser.equals(Constant.SupprotStaff)) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("supportStaffDeviceSearch");
		return model;
	}

	@RequestMapping(value = "/addSchool", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView addSchool(ModelAndView model, @ModelAttribute("schoolModel") SchoolModel schoolModel,
			@RequestParam("token") String sessionID) {

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
				&& !(this.httpSession.getAttribute("currentUser").equals("super_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		log.info("Session iD from Add " + sessionID);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("addSchool");
		return model;
	}

	@RequestMapping(value = "/editSchool", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editTeacher(ModelAndView viewmodel, @ModelAttribute("schoolModel") SchoolModel schoolModel,
			@RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		SchoolModel sm;
		if (sessionValidityResult.equals("NOTVALID")) {
			viewmodel.addObject(Constant.LoginError, Constant.SessionValidityResult);
			viewmodel.setViewName("adminlogin");
			return viewmodel;
		}

		if ((null == this.httpSession.getAttribute("currentUser"))) {
			viewmodel.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			viewmodel.setViewName("adminlogin");
			return viewmodel;
		}
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("super_admin"))) {
			viewmodel.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			viewmodel.setViewName("adminlogin");
			return viewmodel;
		}

		log.info("schoolModel.getAccountID()" + "\t" + schoolModel.getAccountID());
		if (this.httpSession.getAttribute("schoolModel") != null) {
			sm = (SchoolModel) this.httpSession.getAttribute("schoolModel");
			this.httpSession.removeAttribute("schoolModel");
		} else {
			sm = prepareSchoolModel(this.SchoolService.getSchool(schoolModel.getAccountID()));
		}
		this.httpSession.setAttribute("isFromEdit", "true");
		this.httpSession.setAttribute("isEdit", "true");
		viewmodel.addObject("schoolModel", sm);
		viewmodel.addObject(Constant.SessionID, sessionID);
		viewmodel.addObject(Constant.FirstName, user.getName());
		viewmodel.setViewName("addSchool");
		return viewmodel;
	}

	@RequestMapping(value = "/saveSchool", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView saveSchool(@ModelAttribute("schoolModel") SchoolModel schoolModel,
			@RequestParam("token") String sessionID, BindingResult result) {
		action = "Create";
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		ModelAndView model = new ModelAndView();
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		schoolValidator.validate(schoolModel, result);

		if (result.hasErrors()) {
			log.info("Jsp Has Errors");
			model.addObject("sessionID", sessionID);
			model.setViewName("addSchool");
			return model;
		}
		log.info("sav method session ID" + sessionID);
		this.httpSession.setAttribute("schoolModel", schoolModel);
		Accounts schoolEntity = prepareSchoolEntity(schoolModel);
		log.info("schoolEntity.getAccountId()" + "\t" + schoolEntity.getAccountId());
		log.info("schoolEntity.getAccountName()" + "\t" + schoolEntity.getAccountName());
		log.info("schoolEntity.getAccountType()" + "\t" + schoolEntity.getAccountType());
		this.SchoolService.addSchool(schoolEntity);
		log.info("edit from update school" + this.httpSession.getAttribute("isEdit"));
		if (this.httpSession.getAttribute("isEdit") != null) {
			action = "Update";
			this.httpSession.setAttribute("isEdited", "true");
			this.httpSession.setAttribute("isEdit", "true");
			return new ModelAndView("redirect:/editSchool?token=" + sessionID);
		} else
			this.httpSession.setAttribute("isAdded", "true");

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
		activityLogs.info(activityLog);
		return new ModelAndView("redirect:/schoolslistview?token=" + sessionID);
	}

	@RequestMapping(value = "/updateSchoolDetails/{session_id}/{account_id}", method = RequestMethod.POST)
	public String updateSchoolDetails(@PathVariable("session_id") String sessionID,
			@PathVariable("account_id") int accountId, @RequestBody SchoolModel schoolModel) {
		action = "update";
		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "Account.updateSchoolDetails";
		String respondJson = null;
		String errorMessage = null;
		String msg = null;
		String responseJSON = null;
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			errorMessage = "Token provided is expired, need to re-login";
			msg = "FAILURE";
			responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
			return responseJSON;
		}

		log.info("sav method session ID" + sessionID);
		int adminUpdate = 0;
		log.info("newSchoolAdminUser: getAccountID" + schoolModel.getAccountID());
		log.info("newSchoolAdminUser: getAccountName" + schoolModel.getAccountName());
		log.info("newSchoolAdminUser: getSchoolAdmin" + schoolModel.getSchoolAdmin());
		AdminTransform adminTransform = this.accountService.getSchoolAdmin(schoolModel.getAccountID());

		int entereduserId = userService.getUserIdByUsername(schoolModel.getSchoolAdmin());
		int userId = userService.getUserIdByUsername(adminTransform.getUserName());
		Users newSchoolAdminUser = userService.getUser(userId);
		log.info("newSchoolAdminUser: getAccountID" + newSchoolAdminUser.getAccounts().getAccountId());
		if (entereduserId == 0) {
			log.info("enetereed here");
			this.accountService.updateSchoolDetails(schoolModel);
			this.accountService.updateSchoolAccountDetails(schoolModel);
			newSchoolAdminUser.setUsername(schoolModel.getSchoolAdmin());
			newSchoolAdminUser.setActivationCode(StringUtility.randomStringOfLength(40));
			log.info("New Admin User" + "\t" + newSchoolAdminUser.getUsername());
			Users user1 = this.userService.updateUser(newSchoolAdminUser);

			adminUpdate = this.accountService.updateSchoolAdminDetails(schoolModel, adminTransform.getUserName());
			log.info("New Admin User1" + "\t" + user1.getUsername());

			this.userService.sendUserActivationEmail(user1.getUsername(), user1.getActivationCode(),
					user1.getUsername());
		} else if (null != adminTransform && null != adminTransform.getUserName()) {
			if (newSchoolAdminUser.getUsername().equals(schoolModel.getSchoolAdmin())) {
				log.info("enetereed here1");
				this.accountService.updateSchoolDetails(schoolModel);
				this.accountService.updateSchoolAccountDetails(schoolModel);
			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.IsUserExistsMessage, Constant.IsUserExists,
						type);
				this.httpSession.setAttribute("updateMsg", "School Details  Successfully Updated");
				return respondJson;
			}
		} else {
			log.info("enetereed her2e");
			adminUpdate = this.accountService.updateSchoolAdminDetails(schoolModel, adminTransform.getUserName());
			newSchoolAdminUser.setActivationCode(StringUtility.randomStringOfLength(40));
			log.info("New Admin User" + "\t" + newSchoolAdminUser.getUsername());
			Users user1 = this.userService.updateUser(newSchoolAdminUser);
			log.info("New Admin User Changed" + "\t" + user1.getUsername());
			this.userService.sendUserActivationEmail(user1.getUsername(), user1.getActivationCode(),
					user1.getUsername());
		}
		log.info("admin update count : " + adminUpdate);
		if (adminUpdate > 0) {
			try {
			} catch (Exception e) {

			}
		}

		respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		this.httpSession.setAttribute("updateMsg", "School Details  Successfully Updated");
		return respondJson;

	}

	@RequestMapping(value = "/updateDeviceDetails/{session_id}", method = RequestMethod.POST)
	public String updateDeviceDetails(@PathVariable("session_id") String sessionID,
			@RequestBody DeviceListModel deviceModel) {
		action = "update";
		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "Account.updateSchoolDetails";
		String respondJson = null;
		String errorMessage = null;
		String msg = null;
		String responseJSON = null;
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			errorMessage = "Token provided is expired, need to re-login";
			msg = "FAILURE";
			responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
			return responseJSON;
		}

		log.info("sav method session ID" + sessionID);

		log.info(deviceModel.getAddedDate());
		log.info(deviceModel.getDeviceConfigurationID());
		log.info(deviceModel.getDeviceid());

		this.accountService.updateDeviceDetails(deviceModel);
		int deviceConfigUpdate = this.accountService.updateDeviceConfigurationDetails(deviceModel);
		this.httpSession.setAttribute("updatedDeviceuuid", deviceModel.getUuid());

		if (deviceConfigUpdate > 0) {
			try {
				// this.userService.sendEmail(user);
			} catch (Exception e) {

			}
		}

		respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		this.httpSession.setAttribute("updateMsg", "School Details  Successfully Updated");
		return respondJson;

	}

	@RequestMapping(value = "/createDeviceDetails/{session_id}", method = RequestMethod.POST)
	public String createDeviceDetails(@PathVariable("session_id") String sessionID,
			@RequestBody DeviceListModel deviceModel) {
		action = "create";
		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "Account.createDeviceDetails";
		String respondJson = null;
		String errorMessage = null;
		String msg = null;
		String responseJSON = null;
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			errorMessage = "Token provided is expired, need to re-login";
			msg = "FAILURE";
			responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
			return responseJSON;
		}

		log.info("sav method session ID" + sessionID);

		log.info(deviceModel.getDeviceModel());
		log.info(deviceModel.getFirmWareVersion());
		log.info(deviceModel.getUuid());

		int configId = this.accountService.getConfigurationId(deviceModel);

		log.info("configId :" + configId);

		int deviceId = this.deviceService.findDeviceIdByUUID(deviceModel.getUuid());

		log.info("deviceId" + deviceId);

		boolean isSchoolExists = this.accountService.checkAccountIDExist(deviceModel.getSchoolId());

		log.info("isSchoolExists" + isSchoolExists);

		if (deviceId == 0 && isSchoolExists) {
			try {
				DeviceConfigurations deviceConfig = null;
				if (configId == 0) {
					try {
						this.accountService.createDeviceConfigccount(deviceModel);
						// this.userService.sendEmail(user);
					} catch (Exception e) {

					}
					deviceConfig = this.accountService
							.getDeviceConfigurationByID(this.accountService.getLatestDeviceConfigId());
				} else {
					deviceConfig = this.accountService.getDeviceConfigurationByID(configId);
				}
				this.accountService.createDeviceAccount(deviceModel, deviceConfig);
				// this.userService.sendEmail(user);
			} catch (Exception e) {

			}
		} else {
			statusCode = "ERR35";
			statusMsg = "Duplicate Device UUID";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			this.httpSession.setAttribute("updateMsg", statusMsg);
			return respondJson;
		}

		respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		this.httpSession.setAttribute("updateMsg", "Device Added Successfully");
		return respondJson;

	}

	@RequestMapping(value = "/addSchoolDetails/{session_id}", method = RequestMethod.POST)
	public String addSchoolDetails(@PathVariable("session_id") String sessionID, @RequestBody SchoolModel schoolModel) {

		action = "create";
		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "Account.updateSchoolDetails";
		String respondJson = null;
		String errorMessage = null;
		String msg = null;
		String responseJSON = null;
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			errorMessage = "Token provided is expired, need to re-login";
			msg = "FAILURE";
			responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
			return responseJSON;
		}
		log.info("UserName  is " + schoolModel.getSchoolAdmin());
		int existingUserId = userService.getUserIdByUsername(schoolModel.getSchoolAdmin());

		log.info("existingUserId is " + existingUserId);
		if (existingUserId == 0) {

			this.accountService.createSchoolAccount(schoolModel);
			int newSchoolAccountId = this.accountService.getLatestAccountId();
			this.accountService.createSchoolDetails(schoolModel, newSchoolAccountId);
			Users newSchoolAdminUser = this.accountService.createSchoolAdminAccount(schoolModel,
					userService.getAccountsByAccId(newSchoolAccountId));
			newSchoolAdminUser.setActivationCode(StringUtility.randomStringOfLength(40));
			Users user1 = this.userService.updateUser(newSchoolAdminUser);
			this.userService.sendUserActivationEmail(user1.getUsername(), user1.getActivationCode(),
					user1.getUsername());
			
			this.cassandraService.createKeyspaceBySchoolId(newSchoolAccountId);

			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			this.httpSession.setAttribute("updateMsg", "School Details  Successfully Created");
			return respondJson;
		} else {
			statusCode = "ERR13";
			statusMsg = "User account already exist.";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			this.httpSession.setAttribute("updateMsg", "School Details  Failed to  Create");
			return respondJson;
		}

	}

	@RequestMapping(value = "/deleteSchool", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView deleteSchool(@RequestParam("token") String sessionID,
			@RequestParam("account_id") int account_id) {
		log.info("entered into delete Action");
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
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		ModelAndView model = new ModelAndView();
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			model.addObject("delete_failure", "1");
			return model;
		}

		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
		this.SchoolService.deleteSchool(account_id);
		activityLogs.info(activityLog);
		ModelAndView model1 = new ModelAndView("redirect:/schoolslistview?token=" + sessionID);
		model1.addObject("delete_success", "1");
		return model1;

	}

	@RequestMapping(value = "/schoolslistview", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView listSchools(ModelAndView model, @RequestParam("token") String sessionID) {
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
				&& !(this.httpSession.getAttribute("currentUser").equals("super_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		this.httpSession.removeAttribute("isEdit");
		HashMap<Integer, String> schoolAdminSatus = new HashMap<Integer, String>();
		model.addObject("schoolAdminSatus", schoolAdminSatus);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("schoolsList");
		return model;
	}

	@RequestMapping(value = "/adminDeviceManagement", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView adminDeviceManagement(ModelAndView model, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		ArrayList<Integer> lowatteryList = new ArrayList<Integer>();
		for (int i = 1; i <= 100; i++) {
			lowatteryList.add(i);
		}

		ArrayList<Integer> deviceSelfList = new ArrayList<Integer>();
		for (int i = 1; i <= 24; i++) {
			deviceSelfList.add(i);
		}

		ArrayList<Integer> gpsReportList = new ArrayList<Integer>();
		for (int i = 1; i <= 60; i++) {
			gpsReportList.add(i);
		}

		ArrayList<Integer> dataSyncList = new ArrayList<Integer>();
		for (int i = 1; i <= 24; i++) {
			dataSyncList.add(i);
		}
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
				&& !(this.httpSession.getAttribute("currentUser").equals("super_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != this.httpSession.getAttribute("updatedDeviceuuid")) {
			String uuid = (String) this.httpSession.getAttribute("updatedDeviceuuid");
			List<Devices> devicesList = accountService.listDevices(uuid);
			List<DeviceListModel> deviceList = prepareDeviceModelList(devicesList);
			this.httpSession.removeAttribute("updatedDeviceuuid");
			model.addObject("deviceList", deviceList);
		}
		List<String> countyList = accountService.getCountys();

		List<DeviceConfigTransform> deviceModelList = accountService.getDeviceModels();

		List<DeviceConfigurations> deviceConfigList = accountService.getDeviceConfigurationList();
		List<DeviceConfigModel> deviceConfigModelsList = prepareDeviceConfigModelList(deviceConfigList);

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("countyList", countyList);
		model.addObject("deviceConfigModelsList", deviceConfigModelsList);
		model.addObject("lowatteryList", lowatteryList);
		model.addObject("deviceSelfList", deviceSelfList);
		model.addObject("gpsReportList", gpsReportList);
		model.addObject("dataSyncList", dataSyncList);
		model.addObject("deviceModelList", deviceModelList);
		model.setViewName("adminDeviceManagement");
		return model;
	}

	@RequestMapping(value = "/addSchoolAdmin", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView addTeacher(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			@RequestParam("token") String sessionID, @RequestParam("accountID") String accountID) {
		/**
		 * session object setting is required if we dont want to show username
		 * and password in the form
		 */

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		this.httpSession.setAttribute("accountID", accountID);
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
				&& !(this.httpSession.getAttribute("currentUser").equals("super_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		this.httpSession.setAttribute("Added", "Admin Added Successfully");
		model.setViewName("addSchoolAdmin");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;

	}

	@RequestMapping(value = "/saveSchoolAdmin", method = RequestMethod.POST)
	public ModelAndView saveTeacher(@ModelAttribute("usersModel") UsersModel usersModel, BindingResult result,
			RedirectAttributes redirectAttributes, @RequestParam("token") String sessionID) throws Exception {
		ModelAndView model = new ModelAndView();
		log.info("In UserController");

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
		String accID = this.httpSession.getAttribute("accountID").toString();
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		accountValidator.validate(usersModel, result);
		if (result.hasErrors()) {
			log.info("Jsp Has Errors");
			model.addObject("sessionID", sessionID);
			model.setViewName("saveSchoolAdmin");
			return model;
		}
		if (usersModel.getId() == 0) {
			if (this.userService.isUsersExist(usersModel.getUsername())) {
				log.info("Sorry Name already Exits");
				model.addObject("userNameError", "UserNameAlreadyExists");
				model.addObject("sessionID", sessionID);
				// model.addObject("accountID", accID);
				model.setViewName("addSchoolAdmin");
				return model;
			}

		} else {
			if (this.userService.isUsersExist(usersModel.getUsername())) {
				log.info("Sorry Name already Exits");

				model.addObject("userNameError", "UserNameAlreadyExists");
				model.addObject("sessionID", sessionID);
				model.setViewName("saveSchoolAdmin");

				return model;
			}

		}
		if (usersModel.getId() == 0) {
			log.info("New Adding Functionality");
			Users adminEntity = prepareAdminEntity(usersModel);
			this.httpSession.setAttribute("addSchoolAdmin", "true");
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			this.schoolAdminService.addUser(adminEntity);
			activityLogs.info(activityLog);
			redirectAttributes.addFlashAttribute("success",
					"Admin " + usersModel.getUsername() + " Added Successfully");
			return new ModelAndView("redirect:/schoolslistview?accountID=" + accID + "&token=" + sessionID);

		} else {
			action = "Update";
			log.info("New Updating functionality");
			this.httpSession.setAttribute("editSchoolAdmin", "true");
			log.info("USermodel ID" + usersModel.getId());
			log.info("USermodel Name" + usersModel.getName());
			Users teacherEntity = prepareAdminEntity(usersModel);
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			this.schoolAdminService.updateUser(teacherEntity);
			activityLogs.info(activityLog);
			model.addObject("accountID", accID);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("saveSchoolAdmin");

			return model;
		}
	}

	@RequestMapping(value = "/editSchoolAdmin", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editTeacher(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			@RequestParam("token") String sessionID) {
		model.setViewName("editSchoolAdmin");
		model.addObject("sessionID", sessionID);
		return model;
	}

	@RequestMapping(value = "/getSchoolAdmin", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView getTeacher(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			@RequestParam("token") String sessionID, @RequestParam("accountID") String accountID) {

		if ((null == this.httpSession.getAttribute("currentUser"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("super_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		this.httpSession.setAttribute("accountID", accountID);
		Users user = this.userService.getUserByAccId(Integer.parseInt(accountID));
		log.info("User ID" + user);
		UsersModel um = prepareAdminModel(user);
		model.addObject("usersModel", um);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("saveSchoolAdmin");

		return model;
	}

	public UsersModel prepareAdminModel(Users users) {
		UsersModel um = new UsersModel();
		um.setId(users.getId());
		um.setName(users.getName());
		um.setUsername(users.getUsername());
		um.setPassword("");

		return um;
	}

	private Users prepareAdminEntity(UsersModel usersModel) throws Exception {
		Users users = new Users();
		String accId = (String) this.httpSession.getAttribute("accountID");
		Accounts accounts = this.userService.getAccountsByAccId(Integer.parseInt(accId));
		users.setId(usersModel.getId());
		users.setName(usersModel.getName());
		users.setUsername(usersModel.getUsername());
		users.setPassword(AESEncryption.generatePasswordHash(usersModel.getPassword()));
		users.setMobileNumber(usersModel.getMobileNumber());
		users.setRoleType("school_admin");
		users.setUserActive("y");
		users.setCreatedDate(new java.util.Date());
		users.setAccounts(accounts);
		return users;
	}

	public List<SchoolModel> prepareSchoolsModelList(List<Accounts> accountsList) {
		List<SchoolModel> schoolModelList = null;
		if (accountsList != null && !accountsList.isEmpty()) {
			schoolModelList = new ArrayList<>();
			SchoolModel schoolModel = null;

			for (Accounts a : accountsList) {
				schoolModel = new SchoolModel();
				int allocatedDevices = accountService.getAllocatedDevices(a.getAccountId());
				log.info("account id" + a.getAccountId());
				AdminTransform adminTransform = accountService.getSchoolAdmin(a.getAccountId());
				log.info("adminTransform" + adminTransform);
				SchoolTransform schoolTransform = accountService.getSchoolDetails(a.getAccountId());
				schoolModel.setAccountID(a.getAccountId());
				schoolModel.setAccountName(a.getAccountName());
				schoolModel.setAccountType(a.getAccountType());
				schoolModel.setAccountActive(a.getAccountActive());
				schoolModel.setAllocatedDevices(allocatedDevices);
				if (null != adminTransform) {
					schoolModel.setSchoolAdmin(adminTransform.getUserName());
					schoolModel.setLastLoginDate(adminTransform.getLastLoginDate());
				}
				if (null != schoolTransform) {
					log.info("schoolTransform value :" + schoolTransform.getAddress());
					schoolModel.setMobileNumber(schoolTransform.getMobileNumber());
					schoolModel.setCity(schoolTransform.getCity());
					schoolModel.setCountry(schoolTransform.getCountry());
					schoolModel.setCounty(schoolTransform.getCounty());
					schoolModel.setState(schoolTransform.getState());
					schoolModel.setZipcode(schoolTransform.getZipCode());
					schoolModel.setAddress(schoolTransform.getAddress());
					log.info("in School Model ; " + schoolModel.getAddress());
				}

				schoolModelList.add(schoolModel);
			}
		}
		return schoolModelList;
	}

	public List<DeviceListModel> prepareDeviceModelList(List<Devices> deviceList) {
		List<DeviceListModel> deviceModelList = null;
		if (deviceList != null && !deviceList.isEmpty()) {
			deviceModelList = new ArrayList<>();
			DeviceListModel deviceListModel = null;

			for (Devices device : deviceList) {
				deviceListModel = new DeviceListModel();
				String schoolName = accountService.getAccoutNameByAccoutnId(device.getSchoolId());
				log.info(" schoolId " + device.getSchoolId());
				DeviceConfigurationsTransform deviceConfigTransFrom = accountService
						.getDeviceConfigurations(device.getDeviceConfigurations().getDeviceConfigId());
				log.info("DeviceConfigurationsTransform " + deviceConfigTransFrom);

				deviceListModel.setDeviceid(device.getDeviceId());
				deviceListModel.setAddedDate(device.getCreatedDate());
				deviceListModel.setDeviceModel(deviceConfigTransFrom.getDeviceModel());
				deviceListModel.setFirmWareVersion(deviceConfigTransFrom.getFrimWareVersion());
				deviceListModel.setSchoolId(device.getSchoolId());
				deviceListModel.setStatus(device.getStatus());
				deviceListModel.setUuid(device.getUuid());
				deviceListModel.setSchoolName(schoolName);
				deviceListModel.setDeviceConfigurationID(device.getDeviceConfigurations().getDeviceConfigId());
				deviceModelList.add(deviceListModel);
			}
		}
		return deviceModelList;
	}

	public List<DeviceConfigModel> prepareDeviceConfigModelList(List<DeviceConfigurations> deviceConfigList) {
		List<DeviceConfigModel> deviceConfigModelList = null;
		if (deviceConfigList != null && !deviceConfigList.isEmpty()) {
			deviceConfigModelList = new ArrayList<>();

			DeviceConfigModel deviceConfigModel = null;

			for (DeviceConfigurations deviceConfig : deviceConfigList) {
				log.info("Config ID " + deviceConfig.getDeviceConfigId());
				deviceConfigModel = new DeviceConfigModel();
				deviceConfigModel.setDeviceConfigId(deviceConfig.getDeviceConfigId());
				deviceConfigModel.setCreatedDate(deviceConfig.getCreatedDate());
				deviceConfigModel.setDescription(deviceConfig.getDescription());
				deviceConfigModel.setFirmWareName(deviceConfig.getFirmwareName());
				deviceConfigModel.setFirmWareVersion(deviceConfig.getFirmwareVersion());
				deviceConfigModel.setDeviceModel(deviceConfig.getDeviceModel());
				if (null != deviceConfig.getFirmwaresize())
					deviceConfigModel.setSize(deviceConfig.getFirmwaresize());
				deviceConfigModelList.add(deviceConfigModel);
			}
		}
		return deviceConfigModelList;
	}

	private Accounts prepareSchoolEntity(SchoolModel schoolModel) {
		Accounts account = new Accounts();
		log.info("schoolModel.getAccountName()" + "\t" + schoolModel.getAccountName());
		log.info("schoolModel.getAccountType()" + "\t" + schoolModel.getAccountType());
		log.info("schoolModel.getAccountActive()" + "\t" + schoolModel.getAccountActive());
		account.setAccountId(schoolModel.getAccountID());
		account.setAccountName(schoolModel.getAccountName());
		account.setAccountType("school");
		account.setAccountActive(schoolModel.getAccountActive());
		return account;
	}

	public SchoolModel prepareSchoolModel(Accounts a) {
		SchoolModel schoolModel = new SchoolModel();

		schoolModel.setAccountID(a.getAccountId());
		schoolModel.setAccountName(a.getAccountName());
		schoolModel.setAccountType(a.getAccountType());
		schoolModel.setAccountActive(a.getAccountActive());
		return schoolModel;
	}

	@RequestMapping(value = "/scheduleList/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String ScheduleList(@PathVariable String sessionID) {

		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;

		String statusCode = null;
		String statusMsg = null;
		String type = null;

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			statusMsg = "Session Expired ,Please Relogin ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			return responseJSON;
		}

		if (user != null) {
			// if (!sessionValidityResult.equals("NOTVALID")) {

			String roleType = user.getRoleType();
			int SchoolId = user.getAccountId();
			log.info("***School Id***" + "\n" + SchoolId);
			if (roleType.equals("school_admin")) {
				SchoolScheduleTransform tst = this.SchoolService.viewScheduleList(SchoolId);
				if (null == tst) {
					statusCode = "ERR17";
					statusMsg = "Invalid School Id";
					type = "scheduleList";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
					//return responseJSON;
				} else {
					map = new LinkedHashMap<>();

					if (tst.getSchoolinstart() != null)
						map.put("schoolinstart", tst.getSchoolinstart().toString());
					if (tst.getSchoolinend() != null)
						map.put("schoolinend", tst.getSchoolinend().toString());
					if (tst.getSchooloutstart() != null)
						map.put("schooloutstart", tst.getSchooloutstart().toString());
					if (tst.getSchooloutend() != null)
						map.put("schooloutend", tst.getSchooloutend().toString());
					jsonString = JSONObject.toJSONString(map);

					log.info("***myList***" + "\n" + myList);
					responseJSON = ErrorCodesUtil.displayJSONForScheduleList("", "", jsonString);
					log.info("***ScheduleRespJson***" + "\t" + responseJSON);
					//JSONUtility.respondAsJSON(response, scheduleRespJson);
					//return scheduleRespJson;
				}
			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "scheduleList";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
				//return responseJSON;
			}

		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "scheduleList";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/saveSchedule/{sessionID}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String SaveSchedule(@PathVariable String sessionID,
			@RequestBody SchoolScheduleTransform schoolScheduleTransform) {

		log.info("Into saveSchedule() {");
		String statusCode = null;
		String statusMsg = null;
		String type = "saveSchedule";

		String json = JSONUtility.convertObjectToJson(schoolScheduleTransform);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			statusMsg = "Input Is Invalid";
			statusCode = "ERR05";
			type = "saveSchedule";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);
			if (null != user) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					statusCode = "ERR02";
					statusMsg = "Session Expired ,Please Relogin ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
			}
		Integer schoolId = user.getAccountId();
		boolean isUpdateSd = this.SchoolService.isSchoolDetailsIdExist(schoolId);
		log.info("isUpdateSd " + "\t" + isUpdateSd);

		SchoolDetails schoolDetails = prepareSchoolDetailsEntity(schoolScheduleTransform, schoolId, isUpdateSd);
		log.info("schoolDetails " + "\t" + schoolDetails.toString());

		if (user.getRoleType().equals(Constant.SchoolAdmin)) {

			this.SchoolService.updateSchedule(schoolDetails, isUpdateSd);
			statusCode = "SUC01";
			statusMsg = "API Request Success";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		} else {
			statusCode = "ERR03";
			statusMsg = "Unauthorised User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}
	}

	private SchoolDetails prepareSchoolDetailsEntity(SchoolScheduleTransform model, Integer schoolId,
			boolean isUpdateSd) {

		SchoolDetails sd = new SchoolDetails();
		if (isUpdateSd) {
			sd = this.SchoolService.getSchoolDetailsBySchoolId(schoolId);
		}

		log.info("model.getSchoolinstart()" + "\t" + model.getSchoolinstart());
		log.info("model.getSchoolinend()" + "\t" + model.getSchoolinend());
		log.info("model.getSchooloutstart()" + "\t" + model.getSchooloutstart());
		log.info("model.getSchooloutend()" + "\t" + model.getSchooloutend());
		log.info("model.getSchoolId()" + "\t" + schoolId);
		sd.setSchoolInStart(model.getSchoolinstart());
		sd.setSchoolInEnd(model.getSchoolinend());
		sd.setSchoolOutStart(model.getSchooloutstart());
		sd.setSchoolOutEnd(model.getSchooloutend());
		sd.setSchoolId(schoolId);

		return sd;
	}

	private SystemConfiguration prepareSysConfigurationEntity(SysConfigurationTransform model) {

		SystemConfiguration sd = new SystemConfiguration();
		int sysconfIdDefault = 1;
		sd = this.SchoolService.getSystemConfigurationById(sysconfIdDefault);

		log.info("model.getAdminScheduleDataSync()" + "\t" + model.getAdminScheduleDataSync());
		log.info("model.getAdminScheduleSessionValidity()" + "\t" + model.getAdminScheduleSessionValidity());
		log.info("model.getAdminScheduleUserSessionValidity()" + "\t" + model.getAdminScheduleUserSessionValidity());
		log.info("model.getAdminSchedulePwdLinkValidity()" + "\t" + model.getAdminSchedulePwdLinkValidity());
		sd.setIwpsSyncHours(model.getAdminScheduleDataSync());
		sd.setWearableSessionValidityMinutes(model.getAdminScheduleSessionValidity());
		sd.setWebSessionValidityMinutes(model.getAdminScheduleUserSessionValidity());
		sd.setPasswordResetValidityMinutes(model.getAdminSchedulePwdLinkValidity());
		log.info("configuration object created " + sd.toString());
		return sd;
	}

	@RequestMapping(value = "/holidaysUploadFile", method = RequestMethod.POST)
	public ModelAndView holidaysuploadFileHandler(ModelAndView model, @RequestParam("file") MultipartFile file,
			@ModelAttribute("schoolCalendar") SchoolCalendar schoolCalendar, RedirectAttributes redirectAttributes,
			@RequestParam("token") String sessionID) throws IOException, ParseException {
		log.debug("holidaysUploadFile");
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

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

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if ((null == user.getRoleType())) {
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
			model.addObject(Constant.UPLOAD_ERROR, Constant.UPLOAD_ERROR_MSG);
			model.addObject("sessionID", sessionID);
			model.setViewName(Constant.SchoolAdminProfile);
			return model;
		} else {
			Integer schoolId = user.getAccountId();
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
			log.info("folder Path student check" + "\t" + this.configProperties.getProperty("students.upload.path"));
			log.info("folder Path" + "\t" + this.configProperties.getProperty("holidays.upload.path"));
			File f = new File(WebUtility.createFolder(this.configProperties.getProperty("holidays.upload.path") + "/"
					+ schoolId + '_' + dateFormat.format(date) + ".csv"));
			boolean flag = f.createNewFile();
			log.info("fileCreated>>holidays" + "\t" + flag);
			log.info("f.getPath() Account Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
			file.transferTo(f);

			List<SchoolCalendar> list = HolidayCSVToJavaUtil.convertHolidaysCsvToJava(f, schoolId, dbDateTime);

			if (list == null) {
				model.addObject(Constant.UPLOAD_ERROR, Constant.UPLOAD_ERROR_MSG);
				model.addObject("sessionID", sessionID);
				model.setViewName(Constant.SchoolAdminProfile);
				return model;
			}

			log.info("fileCreated>>holidays List" + "\t" + list.toString());
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			if (this.SchoolService.updateSchoolCalendarList(schoolId, list)) {
				activityLogs.info(activityLog);
			} else {
				log.info("Into Else of Upload");
				model.addObject(Constant.UPLOAD_ERROR, Constant.UPLOAD_ERROR_MSG);
			}
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return new ModelAndView("redirect:/schoolAdminProfile?token=" + sessionID);
	}

	@RequestMapping(value = "/deviceManagement")
	public ModelAndView deviceManagement(ModelAndView model, @RequestParam("token") String sessionID) {
		log.debug("deviceManagement");
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

		int pageid = 1;
		int total = PAGINATION_NO_OF_RECORDS;
		int noofassigendpages = 0;
		int noofunassigendpages = 0;
		if (pageid == 1) {
		} else {
			pageid = (pageid - 1) * total + 1;
		}

		List<DeviceAccountTransform> assigendDeviceList = null;
		List<DeviceAccountTransform> unassigendDeviceList = null;

		int totalNoofassigendDevices = 0;
		int totalNoofunassigendDevices = 0;

		if (totalNoofassigendDevices < total)
			noofassigendpages = 1;
		else if (totalNoofassigendDevices % total == 0)
			noofassigendpages = totalNoofassigendDevices / total;
		else
			noofassigendpages = totalNoofassigendDevices / total + 1;

		if (totalNoofunassigendDevices < total)
			noofunassigendpages = 1;
		else if (totalNoofunassigendDevices % total == 0)
			noofunassigendpages = totalNoofunassigendDevices / total;
		else
			noofunassigendpages = totalNoofunassigendDevices / total + 1;

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("assigendDeviceList", assigendDeviceList);
		model.addObject("totalNoofassigendDevicepages", noofassigendpages);
		model.addObject("totalNoofunassigendDevicepages", noofunassigendpages);
		model.addObject("unassigendDeviceList", unassigendDeviceList);
		model.setViewName("deviceManagement");
		return model;
	}

	@RequestMapping(value = "/deviceManagementAssigendDeviceList", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String deviceManagementAssigendDeviceList(ModelAndView model, @RequestParam("token") String sessionID,
			@RequestParam("studentClass") String studentClass, @RequestParam("grade") String grade,
			@RequestParam("pageid") int pageid) throws ParseException {

		Map<Object, Object> map = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int noofpages = 0;
		int currentPage = pageid;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();
		new ArrayList<>();
		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			if (pageid == 1) {
			} else {
				pageid = (pageid - 1) * total + 1;
			}
			List<DeviceAccountTransform> assigendDeviceList = accountService
					.getassignedDeviceList(user.getAccountId(), pageid, total, grade, studentClass);

			int totalNoofassigendDevices = accountService
					.getTotalNoofassignedDeviceList(user.getAccountId(), grade, studentClass);

			log.debug("Inside deviceManagementAssigendDeviceList: totalNoofassigendDevices>" + totalNoofassigendDevices
					+ ", total>" + total);

			if (totalNoofassigendDevices < total)
				noofpages = 1;
			else if (totalNoofassigendDevices % total == 0)
				noofpages = totalNoofassigendDevices / total;
			else
				noofpages = totalNoofassigendDevices / total + 1;

			for (DeviceAccountTransform device : assigendDeviceList) {

				log.info("student_grade" + device.getGrade());
				map = new LinkedHashMap<>();
				if (null != device.getUuid())
					map.put("uuid", device.getUuid());
				if (null != device.getStudentclass())
					map.put("student_class", device.getStudentclass());

				if (null != device.getStudentName())
					map.put("student_name", device.getStudentName());

				if (null != device.getGrade())
					map.put("student_grade", device.getGrade());
				if (null != device.getDateAssigned())
					map.put("sDate", device.getDateAssigned().toString());
				map.put("noofPages", noofpages);
				map.put("currentPage", currentPage);
				finalList.add(map);
			}
			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayAssigenedDeviecList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
			return responseJSON;
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/deviceManagementUnassigendDeviceList", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String deviceManagementUnassigendDeviceList(ModelAndView model, @RequestParam("token") String sessionID,
			@RequestParam("pageid") int pageid) throws ParseException {

		Map<Object, Object> map = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int currentPage = pageid;
		int noofpages = 0;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();
		new ArrayList<>();
		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			if (pageid == 1) {
			} else {
				pageid = (pageid - 1) * total + 1;
			}
			List<DeviceAccountTransform> assigendDeviceList = accountService
					.getunAssignedDeviceList(user.getAccountId(), pageid, total);

			int totalNoofassigendDevices = accountService
					.getTotalNoofUnassignedDeviceList(user.getAccountId());
			if (totalNoofassigendDevices < total)
				noofpages = 1;
			else if (totalNoofassigendDevices % total == 0)
				noofpages = totalNoofassigendDevices / total;
			else
				noofpages = totalNoofassigendDevices / total + 1;

			for (DeviceAccountTransform device : assigendDeviceList) {

				map = new LinkedHashMap<>();
				if (device.getUuid() != null)
					map.put("uuid", device.getUuid());

				map.put("noofPages", noofpages);
				map.put("currentPage", currentPage);
				finalList.add(map);
			}
			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayAssigenedDeviecList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
			return responseJSON;
		}

		else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/deviceManagementUnassigendDevice", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String deviceManagementUnassigendDevice(ModelAndView model, @RequestParam("token") String sessionID,
			@RequestParam("deviceUUID") String deviceUUID, @RequestParam("pageid") int pageid) throws ParseException {

		Map<Object, Object> map = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int currentPage = pageid;
		int noofpages = 0;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();
		new ArrayList<>();
		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			if (pageid == 1) {
			} else {
				pageid = (pageid - 1) * total + 1;
			}
			List<DeviceAccountTransform> assigendDeviceList = accountService
					.getunAssignedDeviceList(user.getAccountId(), pageid, total, deviceUUID);

			if (null != assigendDeviceList && assigendDeviceList.size() > 0) {
				noofpages = 1;
			}

			for (DeviceAccountTransform device : assigendDeviceList) {

				map = new LinkedHashMap<>();
				if (device.getUuid() != null)
					map.put("uuid", device.getUuid());
				map.put("noofPages", noofpages);
				map.put("currentPage", currentPage);
				finalList.add(map);
			}
			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayAssigenedDeviecList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
			return responseJSON;
		}

		else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/downloadCsv", method = RequestMethod.POST)
	public ModelAndView downLoadCSV(ModelAndView model, @RequestParam("token") String sessionID,
			HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/csv");
		Users user = userService.validateUserBySession(sessionID);
		String fileName = user.getAccountId() + "_" + "unAssigendDevicesList" + "_"
				+ new Date().toString();
		response.setHeader("content-disposition", "attachment;filename =" + fileName + ".csv");
		List<DeviceAccountTransform> unassigendDeviceList = accountService
				.getunAssignedDeviceList(user.getAccountId(), 0, 0);
		try {
			ServletOutputStream writer = response.getOutputStream();
			log.info("downloading contents to csv");
			writer.print("UUID");
			writer.println();
			for (DeviceAccountTransform device : unassigendDeviceList) {
				writer.print("" + device.getUuid());
				writer.println();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/downloadBrokenDeviceUUIDs", method = RequestMethod.GET)
	public ModelAndView downloadBrokenDeviceUUIDs(ModelAndView model, @RequestParam("token") String sessionID,
			@RequestParam("school_id") Integer school_id, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/csv");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user && null != school_id) {
			String roleType = user.getRoleType();
			if (roleType.equals("super_admin")) {
				String fileName = school_id + "_" + "BrokenDeviceUUIDs" + "_" + new Date().toString();
				response.setHeader("content-disposition", "attachment;filename =" + fileName + ".csv");
				List<DeviceAccountTransform> brokenDeviceList = accountService.getBrokenOrReturnedDeviceUUIDs(school_id,
						"broken");
				try {
					ServletOutputStream writer = response.getOutputStream();
					log.info("downloadBrokenDeviceUUIDs to csv");
					writer.print("UUID");
					writer.println();
					for (DeviceAccountTransform device : brokenDeviceList) {
						writer.print("" + device.getUuid());
						writer.println();
					}
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				log.info("downloadBrokenDeviceUUIDs User With [" + sessionID + "] Super Admin Role Not Found");
			}
		} else {
			log.info("downloadBrokenDeviceUUIDs User With [" + sessionID + "] Token Not Found or School ID is Null");
		}
		return null;
	}

	@RequestMapping(value = "/downloadReturnedDeviceUUIDs", method = RequestMethod.GET)
	public ModelAndView downloadReturnedDeviceUUIDs(ModelAndView model, @RequestParam("token") String sessionID,
			@RequestParam("school_id") Integer school_id, HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/csv");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user && null != school_id) {
			String roleType = user.getRoleType();
			if (roleType.equals("super_admin")) {
				String fileName = school_id + "_" + "ReturnedDeviceUUIDs" + "_" + new Date().toString();
				response.setHeader("content-disposition", "attachment;filename =" + fileName + ".csv");
				List<DeviceAccountTransform> returnedDeviceList = accountService
						.getBrokenOrReturnedDeviceUUIDs(school_id, "returned");
				try {
					ServletOutputStream writer = response.getOutputStream();
					log.info("downloadReturnedDeviceUUIDs to csv");
					writer.print("UUID");
					writer.println();
					for (DeviceAccountTransform device : returnedDeviceList) {
						writer.print("" + device.getUuid());
						writer.println();
					}
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				log.info("downloadReturnedDeviceUUIDs User With [" + sessionID + "] Super Admin Role Not Found");
			}
		} else {
			log.info("downloadReturnedDeviceUUIDs User With [" + sessionID + "] Token Not Found or School ID is Null");
		}
		return null;
	}

	@RequestMapping(value = "/updateUnassignedDeviceStatus", method = RequestMethod.POST)
	public ModelAndView updateUnassignedDeviceStatus(ModelAndView model, @RequestParam("token") String sessionID,
			HttpServletRequest request, HttpServletResponse response) {
		String[] uuids = request.getParameterValues("uuid");
		String[] statuses = request.getParameterValues("status");
		int page_id = 1;
		if (null != uuids & null != statuses) {
			for (int i = 0; i < uuids.length; i++) {
				log.info("uuid : " + i + uuids[i].toString());
				log.info("status : " + i + statuses[i].toString());
				Devices device = this.accountService.getUnassigendDeviceByUUID(uuids[i]);
				if (statuses[i].equals("unassigned"))
					device.setStatus("");
				else
					device.setStatus(statuses[i]);
				this.deviceService.updateDevice(device);
			}
		}
		return new ModelAndView(Constant.RedirectDeviceManagement + sessionID + "&pageid=" + page_id);
	}

	@RequestMapping(value = "/adminSystemConfiguration")
	public ModelAndView adminSystemConfiguration(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = this.userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		WebUtility webUtility = WebUtility.getWebUtility();
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			return webUtility.getAdminLoginPage(); 
		}

		log.debug("adminSystemConfiguration");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("adminSystemConfiguration");
		return model;
	}

	@RequestMapping(value = "/sysConfigurationList/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String sysConfigurationList(@PathVariable String sessionID) {

		String jsonString = null;
		Map<Object, Object> map = null;

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String respondJson = null;

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			statusMsg = "Session Expired ,Please Relogin ";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			return respondJson;
		}

		if (user != null) {
			String roleType = user.getRoleType();
			if (roleType.equals("super_admin")) {
				SysConfigurationTransform tst = this.SchoolService.viewSysConfigurationList();
				if (null == tst) {
					statusCode = "ERR17";
					statusMsg = "Invalid School Id";
					type = "sysConfigurationList";
					respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
					return respondJson;
				} else {
					map = new LinkedHashMap<>();

					if (tst.getAdminScheduleDataSync() != null)
						map.put("adminScheduleDataSync", tst.getAdminScheduleDataSync().toString());
					if (tst.getAdminScheduleSessionValidity() != null)
						map.put("adminScheduleSessionValidity", tst.getAdminScheduleSessionValidity().toString());
					if (tst.getAdminScheduleUserSessionValidity() != null)
						map.put("adminScheduleUserSessionValidity",
								tst.getAdminScheduleUserSessionValidity().toString());
					if (tst.getAdminSchedulePwdLinkValidity() != null)
						map.put("adminSchedulePwdLinkValidity", tst.getAdminSchedulePwdLinkValidity().toString());
					jsonString = JSONObject.toJSONString(map);

					String scheduleRespJson = ErrorCodesUtil.displayJSONForSysConfigurationList("", "", jsonString);
					log.info("***sysConfigurationList***" + "\t" + scheduleRespJson);
					//JSONUtility.respondAsJSON(response, scheduleRespJson);
					return scheduleRespJson;
				}
			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "sysConfigurationList";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
				return respondJson;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "sysConfigurationList";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			return respondJson;
		}
	}

	@RequestMapping(value = "/adminSysConfiguration/{sessionID}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String adminSysConfiguration(@PathVariable String sessionID,
			@RequestBody SysConfigurationTransform schoolScheduleTransform) {

		log.info("Into adminSysConfiguration() {");
		String statusCode = null;
		String statusMsg = null;
		String type = "SysConfiguration";

		String json = JSONUtility.convertObjectToJson(schoolScheduleTransform);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			statusMsg = "Input Is Invalid";
			statusCode = "ERR05";
			type = "saveSchedule";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			return responseJSON;
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

			Users user = userService.validateUserBySession(sessionID);
			if (null != user) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					statusCode = "ERR02";
					statusMsg = "Session Expired ,Please Relogin ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
				
				if (user.getRoleType().equals(Constant.SuperAdmin)) {
					SystemConfiguration systemConfiguration = prepareSysConfigurationEntity(schoolScheduleTransform);
					log.info("SystemConfiguration " + "\t" + systemConfiguration.toString());
					this.SchoolService.updateSystemConfiguration(systemConfiguration);
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
			}
		return null;
	}

	@RequestMapping(value = "/adminUserAccountManagement")
	public ModelAndView adminUserAccountManagement(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = this.userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		WebUtility webUtility = WebUtility.getWebUtility();
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			return webUtility.getAdminLoginPage();
		}

		log.debug("adminUserAccountManagement");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("adminUserAccountManagement");
		return model;
	}

	@RequestMapping(value = "/adminParentAccountManagement")
	public ModelAndView adminParentAccountManagement(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = this.userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		WebUtility webUtility = WebUtility.getWebUtility();
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			return webUtility.getAdminLoginPage();
		}

		log.debug("adminParentAccountManagement");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("adminParentAccountManagement");
		return model;
	}

	@RequestMapping(value = "/SearchParentAccountManagement/{sessionID}/{profileName}/{contactNo}/{emailId}/{deviceUUID}/{pageid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SearchParents(@PathVariable String sessionID, @PathVariable String profileName,
			@PathVariable String contactNo, @PathVariable String emailId, @PathVariable String deviceUUID,
			@PathVariable int pageid) throws Exception {

		String statusCode = null;
		String statusMsg = null;

		int total = PAGINATION_NO_OF_RECORDS;
		int noofpages = 0;
		int currentPage = pageid;

		String type = "parents.searchParents";
		Map<Object, Object> outerMap = null;
		Map<String, Object> parentsMap = null;

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (profileName.isEmpty() || profileName.equals(null))
				|| (contactNo.isEmpty() || contactNo.equals(null)) || (emailId.isEmpty() || emailId.equals(null))
				|| (deviceUUID.isEmpty() || deviceUUID.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals(Constant.SuperAdmin)
						|| user.getRoleType().equals(Constant.SupprotStaff)) {
					outerMap = new LinkedHashMap<>();

					if (pageid == 1) {
					} else {
						pageid = (pageid - 1) * total + 1;
					}
					String profileNameDecoded = URLDecoder.decode(profileName, "UTF-8");
					log.debug("profileNameDecoded ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: "
							+ profileNameDecoded);
					parentsMap = this.SchoolService.searchParents(profileName, contactNo, emailId,
							deviceUUID, pageid - 1, total);
					int totalNoofSchools = this.SchoolService.getTotalNoofParents(profileName,
							contactNo, emailId, deviceUUID);
					log.debug("totalNoofParents = " + totalNoofSchools);
					if (totalNoofSchools < total)
						noofpages = 1;
					else if (totalNoofSchools % total == 0)
						noofpages = totalNoofSchools / total;
					else
						noofpages = totalNoofSchools / total + 1;

					outerMap.put("parents", parentsMap.get("search_result"));

					outerMap.put("noofPages", noofpages);

					outerMap.put("currentPage", currentPage);

					responseJSON = ErrorCodesUtil.displaySchoolAdminData("API Request Success", "SUC01",
							"parents.searchParents", JSONObject.toJSONString(outerMap));

					log.info("***SOSAlerts JSON***" + "\t" + responseJSON);
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "parents.searchParents";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "parents.searchParents";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "parents.searchParents";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/SaveParentAccountManagement/{sessionID}/{userID}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String SaveParents(@PathVariable String sessionID, @PathVariable String userID,
			@RequestBody ParentModel parentsModel) throws Exception {

		String statusCode = null;
		String statusMsg = null;

		String type = "parents.saveParents";

		log.info("Inside SaveParentAccountManagement " + type);

		if ((sessionID.isEmpty() || sessionID.equals(null))
				|| (parentsModel.getName().isEmpty() || parentsModel.getName().equals(null))
				|| (parentsModel.getMobile_number().isEmpty() || parentsModel.getMobile_number().equals(null))
				|| (parentsModel.getUsername().isEmpty() || parentsModel.getUsername().equals(null))
				|| (userID.isEmpty() || userID.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		Users user = userService.validateUserBySession(sessionID);
		// log.info("Session User " + user.toString());
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals(Constant.SuperAdmin)) {
					Users parentUser = userService.getUser(Integer.parseInt(userID));
					log.info("Parent User " + parentUser.toString());
					parentUser.setName(parentsModel.getName());
					parentUser.setMobileNumber(parentsModel.getMobile_number());
					log.info("parentsModel.getUsername()" + parentsModel.getUsername());
					log.info("parentUser.getUsername()" + parentUser.getUsername());
					if (null == parentUser.getUsername()) {
						this.userService.updateUser(parentUser);
						log.info("parentUser.getUsername() is NULL, user is EXTERNAL(FB or GP) user.");
					} else {
						log.info("email id has changed "
								+ parentUser.getUsername().equalsIgnoreCase(parentsModel.getUsername()));
						if (!parentUser.getUsername().equalsIgnoreCase(parentsModel.getUsername())) {
							if (this.userService.getUserDetails(parentsModel.getUsername()) == null) {
								parentUser.setUsername(parentsModel.getUsername());
								parentUser.setUserActive("n");
								parentUser.setActivationCode(StringUtility.randomStringOfLength(40));
								Users user1 = this.userService.updateUser(parentUser);
								log.debug("user1.getPassword()" + "\t" + user1.toString());
								String status = this.userService.sendUserActivationEmail(user1.getUsername(),
										user1.getActivationCode(), user1.getUsername());
								log.info("maill sent successfully >>>>>>>>>>>>>>>>>>" + "\t" + status);
							} else {
								statusCode = "ERR19";
								statusMsg = "Username / Email id already exists";
								type = "parents.saveParents";
								responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
								//JSONUtility.respondAsJSON(response, responseJSON);
								return responseJSON;

							}
						} else {
							Users user1 = this.userService.updateUser(parentUser);
							log.debug("user1.getPassword()" + "\t" + user1.toString());
						}
					}
					StatusCode = "SUC01";
					ErrorMessage = "API Request Success";

					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;

				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "parents.saveParents";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "parents.saveParents";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "parents.saveParents";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/DeleteParentAccountManagement/{sessionID}/{userID}")
	public String DeleteParents(@PathVariable String sessionID, @PathVariable String userID) throws Exception {

		String statusCode = null;
		String statusMsg = null;

		String type = "parents.deleteParents";

		log.info("Inside DeleteParentAccountManagement " + type);

		if ((sessionID.isEmpty() || sessionID.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		Users user = userService.validateUserBySession(sessionID);
		// log.info("Session User " + user.toString());
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals(Constant.SuperAdmin)) {
					Users parentUser = userService.getUser(Integer.parseInt(userID));
					this.userService.deleteGuardian(parentUser);
					/*log.info(" User type " + parentUser.getRoleType());
					if (parentUser.getRoleType().equalsIgnoreCase(Constant.ParentAdmin)) {
						log.info("Parent User " + parentUser.toString());

						this.userService.deleteUser(parentUser);
						log.info("deleted parent user successfully ");
					} else {
						log.info("Guardian User " + parentUser.toString());

						this.userService.deleteGuardian(parentUser);
						log.info("deleted Guardian user successfully ");
					}*/

					StatusCode = "SUC01";
					ErrorMessage = "API Request Success";

					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);

				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "parents.deleteParents";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "parents.deleteParents";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "parents.deleteParents";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/SearchUserAccountLists/{sessionID}/{accountType}/{pageid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String UserAccountLists(@PathVariable String sessionID, @PathVariable String accountType,
			@PathVariable("pageid") int pageid) throws Exception {

		String statusCode = null;
		String statusMsg = null;

		String type = "user.UserAccountLists";

		List myList = new ArrayList<>();
		String jsonString = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int currentPage = pageid;
		int noofpages = 0;

		Map<Object, Object> map = null;

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (accountType.isEmpty() || accountType.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals(Constant.SuperAdmin)) {
					log.info("inside 0 only category list displayed");

					if (pageid == 1) {
						// pageid = pageid * total ;
					} else {
						pageid = (pageid - 1) * total + 1;
					}

					int school_id = user.getAccountId();
					List<Users> userAccountList1 = this.userService.getUserListByRoleType(school_id,
							Integer.parseInt(accountType), 0, 0);
					List<Users> userAccountList = this.userService.getUserListByRoleType(school_id,
							Integer.parseInt(accountType), pageid, total);

					int listSize = userAccountList1.size();

					if (listSize > 0 && pageid == 1) {
						if (listSize < total)
							noofpages = 1;
						else if (listSize % total == 0)
							noofpages = listSize / total;
						else
							noofpages = listSize / total + 1;
						this.httpSession.setAttribute("studentsListPages", String.valueOf(noofpages));
					}

					if (userAccountList.size() > 0) {
						for (Users trst : userAccountList) {
							map = new LinkedHashMap<>();

							map.put("user_id", trst.getId());
							if (trst.getName() != null) {
								map.put("profileName", trst.getName());
							}
							if (trst.getUsername() != null) {
								map.put("emailId", trst.getUsername());
							}
							if (trst.getMobileNumber() != null) {
								map.put("contactNo", trst.getMobileNumber());
							}
							if (trst.getRoleType() != null) {
								map.put("userType", trst.getRoleType());
							}
							if (trst.getLastLogin() != null) {
								map.put("lastlogin", trst.getLastLogin().toString());
							}

							map.put("noofPages", (String) this.httpSession.getAttribute("studentsListPages"));
							map.put("currentPage", currentPage);

							jsonString = JSONObject.toJSONString(map);
							myList.add(jsonString);
						}

					}
					log.info("***myList***" + "\n" + myList);
					StatusCode = "SUC01";
					responseJSON = ErrorCodesUtil.displayJSONForUserAccountLists(StatusCode, "",
							myList.toString());
					log.info("***teacherRewardsJson***" + "\t" + responseJSON);
					//JSONUtility.respondAsJSON(response, teacherRewardsJson);
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "user.UserAccountLists";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "user.UserAccountLists";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "user.UserAccountLists";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/SaveUserAccountManagement/{sessionID}/{userID}/{saveType}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String SaveUserAccount(@PathVariable String sessionID, @PathVariable String userID,
			@PathVariable String saveType, @RequestBody ParentModel parentsModel) throws Exception {

		String statusCode = null;
		String statusMsg = null;

		String type = "user.saveUserAccounts";

		log.info("Inside SaveParentAccountManagement " + type);

		if ((sessionID.isEmpty() || sessionID.equals(null))
				|| (parentsModel.getName().isEmpty() || parentsModel.getName().equals(null))
				|| (parentsModel.getMobile_number().isEmpty() || parentsModel.getMobile_number().equals(null))
				|| (parentsModel.getUsername().isEmpty() || parentsModel.getUsername().equals(null))
				|| (parentsModel.getRoleType().isEmpty() || parentsModel.getRoleType().equals(null))
				|| (userID.isEmpty() || userID.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		Users user = userService.getUserBySessionId(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals(Constant.SuperAdmin)) {

					if (saveType.equalsIgnoreCase("1")) {
						Users parentUser = userService.getUser(Integer.parseInt(userID));
						log.info("Parent User " + parentUser.toString());
						parentUser.setName(parentsModel.getName());
						parentUser.setMobileNumber(parentsModel.getMobile_number());
						parentUser.setRoleType(parentsModel.getRoleType());
						parentUser.setUpdatedDate(new java.util.Date());
						log.info("parentsModel.getUsername()" + parentsModel.getUsername());
						log.info("parentUser.getUsername()" + parentUser.getUsername());
						log.info("email id has changed "
								+ !parentUser.getUsername().equalsIgnoreCase(parentsModel.getUsername()));
						if (!parentUser.getUsername().equalsIgnoreCase(parentsModel.getUsername())) {
							if (this.userService.isUsernameExists(parentsModel.getUsername()) == false) {
								parentUser.setUsername(parentsModel.getUsername());
								parentUser.setUserActive("n");
								parentUser.setActivationCode(StringUtility.randomStringOfLength(40));
								Users user1 = this.userService.updateUser(parentUser);
								log.debug("user1.getPassword()" + "\t" + user1.toString());
								String status = this.userService.sendUserActivationEmail(user1.getUsername(),
										user1.getActivationCode(), user1.getUsername());
								log.info("maill sent successfully >>>>>>>>>>>>>>>>>>" + "\t" + status);
							} else {
								statusCode = "ERR19";
								statusMsg = "Username / Email id already exists";
								type = "user.saveUserAccounts";
								responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
								//JSONUtility.respondAsJSON(response, responseJSON);
								return responseJSON;

							}
						} else {
							Users user1 = this.userService.updateUser(parentUser);
							log.debug("user1.getPassword()" + "\t" + user1.toString());
						}
						StatusCode = "SUC01";
						ErrorMessage = "API Request Success";

						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						//JSONUtility.respondAsJSON(response, responseJSON);
						return responseJSON;
					} else if (saveType.equalsIgnoreCase("0")) {
						Users parentUser = new Users();
						log.info("New  User " + parentUser.toString());
						parentUser.setName(parentsModel.getName());
						parentUser.setMobileNumber(parentsModel.getMobile_number());
						parentUser.setRoleType(parentsModel.getRoleType());
						parentUser.setCreatedDate(new java.util.Date());
						parentUser.setAccounts(user.getAccounts());
						log.info("parentsModel.getUsername()" + parentsModel.getUsername());
						log.info("parentUser.getUsername()" + parentUser.getUsername());

						if (this.userService.isUsernameExists(parentsModel.getUsername()) == false) {
							parentUser.setUsername(parentsModel.getUsername());
							parentUser.setUserActive("n");
							parentUser.setActivationCode(StringUtility.randomStringOfLength(40));
							Users user1 = this.userService.updateUser(parentUser);
							log.debug("user1.getPassword()" + "\t" + user1.toString());
							String status = this.userService.sendUserActivationEmail(user1.getUsername(),
									user1.getActivationCode(), user1.getUsername());
							log.info("maill sent successfully >>>>>>>>>>>>>>>>>>" + "\t" + status);
							StatusCode = "SUC01";
							ErrorMessage = "API Request Success";

							responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						} else {
							statusCode = "ERR19";
							statusMsg = "Username / Email id already exists";
							type = "user.saveUserAccounts";
							responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
							//JSONUtility.respondAsJSON(response, responseJSON);
						}

						//JSONUtility.respondAsJSON(response, responseJSON);
					}
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "user.saveUserAccounts";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "user.saveUserAccounts";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "user.saveUserAccounts";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/DeleteUserAccountManagement/{sessionID}/{userID}")
	public String DeleteUserAccount(@PathVariable String sessionID, @PathVariable String userID) throws Exception {

		String statusCode = null;
		String statusMsg = null;

		String type = "users.deleteUserAccount";

		log.info("Inside SaveParentAccountManagement " + type);

		if ((sessionID.isEmpty() || sessionID.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			return responseJSON;
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		Users user = userService.validateUserBySession(sessionID);
		// log.info("Session User " + user.toString());
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals(Constant.SuperAdmin)) {
					Users parentUser = userService.getUser(Integer.parseInt(userID));
					log.info("UserAccount" + parentUser.toString());

					this.userService.deleteUserById(parentUser.getId());

					log.info("deleted UserAccount user successfully ");

					StatusCode = "SUC01";
					ErrorMessage = "API Request Success";

					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					//return responseJSON;

				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "users.deleteUserAccount";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					//return responseJSON;
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "users.deleteUserAccount";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				//return responseJSON;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "users.deleteUserAccount";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/getDeviceStatDetails", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getDeviceStatDetails(@RequestParam("token") String sessionID, @RequestParam("county") String county,
			@RequestParam("schoolName") String schoolName) throws ParseException {

		Map<Object, Object> map = null;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();
		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {

			DeviceStatsTransform assigendDeviceList = accountService.getDeviceStats(county, schoolName, "assigned");
			log.info("schoolName : " + assigendDeviceList.getSchoolName());
			DeviceStatsTransform unassigendDeviceList = accountService.getDeviceStats(county, schoolName, " ");
			DeviceStatsTransform retrunedDeviceList = accountService.getDeviceStats(county, schoolName, "returned");
			DeviceStatsTransform brokenDeviceList = accountService.getDeviceStats(county, schoolName, "broken");
			map = new LinkedHashMap<>();
			if (null != assigendDeviceList.getSchoolName()) {
				map.put("SchoolName", assigendDeviceList.getSchoolName());
				map.put("schoolId", Integer.parseInt(schoolName));
				map.put("assigend", assigendDeviceList.getCount().intValue());
				map.put("unassigend", unassigendDeviceList.getCount().intValue());
				map.put("broken", brokenDeviceList.getCount().intValue());
				map.put("returned", retrunedDeviceList.getCount().intValue());
			}
			finalList.add(map);
			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayDeviceStatsList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/getDeviceDetails", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getDeviceDetails(@RequestParam("token") String sessionID, @RequestParam("uuid") String uuid)
			throws ParseException {

		Map<Object, Object> map = null;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();

		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {

			List<Devices> devicesList = accountService.listDevices(uuid);
			List<DeviceListModel> deviceList = prepareDeviceModelList(devicesList);
			map = new LinkedHashMap<>();
			if (null != deviceList && null != deviceList.get(0).getUuid()) {
				map.put("uuid", deviceList.get(0).getUuid());
				map.put("schoolName", deviceList.get(0).getSchoolName());
				map.put("firmWareVersion", deviceList.get(0).getFirmWareVersion());
				map.put("deviceModel", deviceList.get(0).getDeviceModel());
				map.put("addedDate", deviceList.get(0).getAddedDate().toString());
				map.put("status", deviceList.get(0).getStatus());
				map.put("deviceid", deviceList.get(0).getDeviceid());
				map.put("deviceconfigid", deviceList.get(0).getDeviceConfigurationID());

			}
			finalList.add(map);
			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayDeviceList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/deviceUploadFile", method = RequestMethod.POST, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView deviceUploadFile(ModelAndView model, @RequestParam("file") MultipartFile file,
			HttpServletRequest request, TimeTableModel timeTableModel, RedirectAttributes redirectAttributes,
			@RequestParam("token") String sessionID) throws IOException {
		String fileName = null;
		Integer insertCount = 0;
		Integer ignoredCount = 0;
		Integer totalCount = 0;
		Integer updatedCount = 0;
		String jsonString = null;
		List<Object> outerEventList = new ArrayList<Object>();
		Map<Object, Object> outerZoneMap = new HashMap<Object, Object>();
		action = "Create";
		methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {

			log.error("deviceFileHandler() ", e1);
		}

		Users user = userService.validateUserBySession(sessionID);

		if (null != file) {
			fileName = file.getOriginalFilename();
			log.info("FileName" + "\t" + fileName);
		}
		// log.info("f.getPath() User Controller" + "\t" +
		// System.getProperty("UPLOAD_LOCATION"));
		Date date = new Date();
		String os = System.getProperty("os.name");
		log.info("Operating System" + "\t" + os);
		if (os.contains("Windows"))
			dbDateTime = dbDateTime.replace(":", "-");

		SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
		try {
			File f = new File(WebUtility.createFolder(
					this.configProperties.getProperty("device.upload.path") + "/" + dateFormat.format(date) + ".csv"));

			boolean flag = f.createNewFile();
			log.info("fileCreated>>School Admin" + "\t" + flag);
			log.info("f.getPath() " + "\t" + System.getProperty("UPLOAD_LOCATION"));

			file.transferTo(f);
			log.info("FilePath" + "\t" + f.getAbsolutePath());
			List<DeviceListModel> list = null;
			try {
				log.info("Before convertDevicesCsvToJava");
				list = DeviceCSVToJavaUtil.convertDevicesCsvToJava(f);
				log.info("After convertDevicesCsvToJava");
			} catch (NullPointerException e) {
				log.info("Exception while dealing with headers" + e);
				if (list == null || list.size() == 0) {
					model.addObject("csvInvalidMsg", "icglabel_invalidcsverrormsg");
					model.addObject("sessionID", sessionID);
					model.setViewName("listOfTimetables");
					return model;
				}
			}
			log.info("Outside Try Catch convertDevicesCsvToJava");
			totalCount = list.size();
			log.info("After list.size(): " + totalCount);
			int deviceCount = -1;
			for (DeviceListModel deviceModel : list) {
				deviceCount++;

				int configId = this.accountService.getConfigurationId(deviceModel);

				log.info("configId :" + configId);

				int deviceId = this.deviceService.findDeviceIdByUUID(deviceModel.getUuid());

				log.info("deviceId" + deviceId);

				int schoolId = this.deviceService.getSchoolIdByUUID(deviceModel.getUuid());
				log.info("Existing Device's schoolId:" + schoolId);

				boolean isSchoolExists = this.accountService.checkAccountIDExist(deviceModel.getSchoolId());
				log.info("isSchoolExists" + isSchoolExists);

				if (deviceModel.getUuid().length() > 36) {
					log.error("Error: Invalid device UUID, UUID length > 36");
					ignoredCount++;
					Map<Object, Object> innerZoneMap = new HashMap<Object, Object>();
					innerZoneMap.put("row", deviceCount);
					innerZoneMap.put("error", "Device UUID exceeds 36 characters length");
					outerEventList.add(innerZoneMap);
				} else {
					if (deviceId > 0 && schoolId > 0 && isSchoolExists && deviceModel.getSchoolId() != schoolId) {
						ignoredCount++;
						Map<Object, Object> innerZoneMap = new HashMap<Object, Object>();
						innerZoneMap.put("row", deviceCount);
						innerZoneMap.put("error", "Device is Mapped to different School");
						outerEventList.add(innerZoneMap);
					} else if (deviceId > 0 && isSchoolExists) {
						int existingConfigId = this.accountService.getConfigIdByDeviceId(deviceId);
						if (configId == 0) {
							try {
								this.accountService.createDeviceConfigccount(deviceModel);
								configId = this.accountService.getLatestDeviceConfigId();
								this.accountService.updateDeviceConfigId(deviceId, configId);
								updatedCount++;
							} catch (Exception e) {
								log.error("1. Error while creating new device configuration" + e);
							}
						} else if (existingConfigId != configId) {
							this.accountService.updateDeviceConfigId(deviceId, configId);
							updatedCount++;
						} else {
							ignoredCount++;
							Map<Object, Object> innerZoneMap = new HashMap<Object, Object>();
							innerZoneMap.put("row", deviceCount);
							innerZoneMap.put("error", "Device to Model mapping already exists");
							outerEventList.add(innerZoneMap);
						}
					} else if (deviceId == 0 && isSchoolExists) {
						try {
							DeviceConfigurations deviceConfig = null;
							if (configId == 0) {
								try {
									this.accountService.createDeviceConfigccount(deviceModel);
								} catch (Exception e) {
									log.error("2. Error while creating new device configuration" + e);
								}
								deviceConfig = this.accountService
										.getDeviceConfigurationByID(this.accountService.getLatestDeviceConfigId());
							} else {
								deviceConfig = this.accountService.getDeviceConfigurationByID(configId);
							}
							this.accountService.createDeviceAccount(deviceModel, deviceConfig);
							insertCount++;
						} catch (Exception e) {

						}
					} else {
						ignoredCount++;
						Map<Object, Object> innerZoneMap = new HashMap<Object, Object>();
						innerZoneMap.put("row", deviceCount);
						innerZoneMap.put("error", "School ID Not Found");
						outerEventList.add(innerZoneMap);
					}
				}

			}
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);

			activityLogs.info(activityLog);
			if (outerEventList.size() > 0) {
				outerZoneMap.put("error_log", outerEventList);
				jsonString = JSONObject.toJSONString(outerZoneMap);
				log.info("jsonString" + jsonString);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error On Console" + "\t" + e);
			model.addObject("csvInvalidMsg", "icglabel_invalidcsverrormsg");
			model.addObject("sessionID", sessionID);
			redirectAttributes.addFlashAttribute("csvInvalidMsg", "icglabel_invalidcsverrormsg");
			return new ModelAndView("redirect:/adminDeviceManagement?token=" + sessionID);
		}
		if (insertCount > 0 || updatedCount > 0 || ignoredCount > 0) {
			redirectAttributes.addFlashAttribute("csvuploadmsg", "create_device_success");
			redirectAttributes.addFlashAttribute("deviceRecordCount", "Total Records(" + totalCount + "),  Created("
					+ insertCount + "), Updated(" + updatedCount + "), Ignored(" + ignoredCount + ")");
			if (ignoredCount > 0)
				redirectAttributes.addFlashAttribute("ignoredList", jsonString);
		} else if (ignoredCount == totalCount) {
			redirectAttributes.addFlashAttribute("csvInvalidMsg", "icglabel_invalidcsverrormsg");
		}
		model.addObject("sessionID", sessionID);
		return new ModelAndView("redirect:/adminDeviceManagement?token=" + sessionID);
	}

	@RequestMapping(value = "/getSchoolDetails", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getSchoolDetails(@RequestParam("token") String sessionID, @RequestParam("county") String county)
			throws ParseException {

		Map<Object, Object> map = null;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();

		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {

			List<AccountFroSchoolTransform> schoolNames = this.accountService.getSchoolName(county);

			for (AccountFroSchoolTransform school : schoolNames) {

				map = new LinkedHashMap<>();

				map.put("school_id", school.getAccount_id());

				map.put("schoolName", school.getAccount_name());

				finalList.add(map);
			}

			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayDeviceList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/deleteDevice", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView deleteDevice(@RequestParam("token") String sessionID, @RequestParam("deviceid") int deviceid) {
		log.info("entered into delete Action");
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
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		ModelAndView model = new ModelAndView();
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
		this.accountService.deleteDevice(deviceid);
		activityLogs.info(activityLog);
		this.httpSession.setAttribute("isDeleted", "true");
		return new ModelAndView("redirect:/adminDeviceManagement?token=" + sessionID);

	}

	@RequestMapping(value = "/getDeviceConfigDetails", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getDeviceConfigDetails(@RequestParam("token") String sessionID,
			@RequestParam("deviceConfigId") int deviceConfigId) throws ParseException {

		Map<Object, Object> map = null;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();

		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {

			DeviceConfigurations deviceConfigurations = accountService.getDeviceConfigDetails(deviceConfigId);

			map = new LinkedHashMap<>();
			map.put("lowbaatery", deviceConfigurations.getLowBattery());
			map.put("gpsreportfreequency", deviceConfigurations.getGpsReportFrequency());
			map.put("wearablefreequency", deviceConfigurations.getWearableSyncFrequency());
			map.put("deviceselftesting", deviceConfigurations.getDeviceSelfTestingVersion());
			map.put("deviceConfigId", deviceConfigId);

			finalList.add(map);

			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayDeviceList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/updateDeviceConfigDetails/{session_id}", method = RequestMethod.POST)
	public String updateDeviceConfigDetails(@PathVariable("session_id") String sessionID,
			@RequestBody DeviceConfigModel deviceConfigModel) {
		action = "update";
		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "Account.updateSchoolDetails";
		String respondJson = null;
		String errorMessage = null;
		String msg = null;
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			errorMessage = "Token provided is expired, need to re-login";
			msg = "FAILURE";
			responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
			return responseJSON;
		}

		log.info("sav method session ID" + sessionID);

		log.info(deviceConfigModel.getDataSyncFreequency());
		log.info(deviceConfigModel.getDeviceConfigId());
		log.info(deviceConfigModel.getGepReportFreequency());

		int deviceUpdate = this.accountService.updateDeviceConfig(deviceConfigModel);

		if (deviceUpdate > 0) {
			try {
				// this.userService.sendEmail(user);
			} catch (Exception e) {

			}
		}

		respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		this.httpSession.setAttribute("updateMsg", "School Details  Successfully Updated");
		return respondJson;

	}

	@RequestMapping(value = "/DeviceConfigCreate/{sessionID}/{model}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String ClassRemindersCreate(HttpServletRequest httpReq, @PathVariable("sessionID") String sessionID,
			@PathVariable("model") String model) {
		String ErrorMessage = null;
		String StatusCode = null;
		String OperationType = "Create";
		new LinkedHashMap<>();
		String type = "Account.DeviceConfigCreate";
		double kilobytes = 0.0;
		WebUtility webUtility = WebUtility.getWebUtility();
		File f = null;
		// int school_id = Integer.parseInt(httpReq.getParameter("school_id"));
		if (httpReq.getParameterNames().hasMoreElements()) {
			String element = httpReq.getParameterNames().nextElement();
			log.info("Element" + "\t" + element);
		}

		log.info("httpReq.getRequestURI()" + "\t" + httpReq.getRequestURI());
		String adminDeviceFotaVersion = httpReq.getParameter("adminDeviceFotaVersion");
		log.info("comments" + "\t" + adminDeviceFotaVersion);
		// String studentClass = httpReq.getParameter("class_id").toString();
		String adminDeviceFirmwareDeviceFilter = httpReq.getParameter("adminDeviceFirmwareDeviceFilter");
		String adminDeviceFirmwareDeviceName = httpReq.getParameter("adminDeviceFotaName");
		String adminDeviceFirmwareDeviceFileName = httpReq.getParameter("adminDeviceFotaFile");
		log.info("adminDeviceFirmwareDeviceFilter" + "\t" + adminDeviceFirmwareDeviceFilter);
		log.info("adminDeviceFirmwareDeviceName" + "\t" + adminDeviceFirmwareDeviceName);
		log.info("adminDeviceFirmwareDeviceFileName" + "\t" + adminDeviceFirmwareDeviceFileName);
		String adminDeviceFotaDescription = httpReq.getParameter("adminDeviceFotaDescription");
		log.info("adminDeviceFotaDescription" + "\t" + adminDeviceFotaDescription);

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

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);
			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		}

		if (null != user) {

			try {

				String contentType = httpReq.getContentType();
				log.info("contentType in MobileConfigFileUpload" + "\t" + contentType);
				MultipartHttpServletRequest multiHttpReq = (MultipartHttpServletRequest) httpReq;
				log.info("1");
				MultipartFile multipartFile = null;
				if (multiHttpReq.getFileMap().entrySet() != null) {
					log.info("2");
					Set set = multiHttpReq.getFileMap().entrySet();
					java.util.Iterator i = set.iterator();
					log.info("3");
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
						String originalFileName = multipartFile.getOriginalFilename();
						// File f = new
						// File(servletContext.getRealPath("/resources\\uploads\\reminders\\"
						// + multipartFile.getOriginalFilename()));
						if (multiHttpReq.getFileMap().entrySet() != null) {
							SimpleDateFormat currenttimestamp = new SimpleDateFormat("yyyyMMddHHmmss");
							log.info("Timestamp added" + "\t" + currenttimestamp.format(new Date()));
							String modifiedFileName = model.toLowerCase() + "_" + currenttimestamp.format(new Date())
									+ ".ZIP";

							log.info("fileName Created" + "\t" + modifiedFileName);
							f = new File(
									webUtility.createNewFolder(this.configProperties.getProperty("firmware.upload.path")
											+ "/" + modifiedFileName));

							// f = new
							// File(this.configProperties.getProperty("deviceconfig.upload.path")
							// + "/"

							// System.getProperty("UPLOAD_LOCATION_FOR_REMINDERS")
							// + "/" + multipartFile.getOriginalFilename());
							double bytes = f.length();
							kilobytes = (bytes / (1024 * 1024));

							log.info("f.getName().toUpperCase(): " + f.getName().toUpperCase());
							if ((originalFileName.toUpperCase().endsWith(".ZIP") && (kilobytes <= 10))) {
								f.createNewFile();
								multipartFile.transferTo(f);
							} else {
								String statusCode = "ERR19";
								String errorMessage = "File not suported ";
								responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
								//JSONUtility.respondAsJSON(response, responseJSON);
								return responseJSON;
							}
						}
					} catch (Exception e) {
						return "You failed to upload " + e.getMessage();
					}
				}
				log.info("Before Reminders");
				List<DeviceConfigTransform> deviceConfigList = this.accountService.getDeviceConfiDetailsByModel(model);

				log.info("" + deviceConfigList.size());
				int latestid = 0;
				DeviceConfigTransform latestDeviceConfig = null;
				for (DeviceConfigTransform deviceConfig : deviceConfigList) {
					if (deviceConfig.getConfig_id() > latestid) {
						latestid = deviceConfig.getConfig_id();
						latestDeviceConfig = deviceConfig;
					}

				}
				log.info("get Config ID " + latestDeviceConfig.getConfig_id());
				DeviceConfigurations deviceConfigurations = new DeviceConfigurations();
				deviceConfigurations.setCreatedDate(new Date());
				deviceConfigurations.setDescription(adminDeviceFotaDescription);
				deviceConfigurations.setFirmwareVersion(adminDeviceFotaVersion);
				deviceConfigurations.setFirmwareName(adminDeviceFirmwareDeviceName);
				deviceConfigurations.setFirmwareFile(adminDeviceFirmwareDeviceFileName);
				deviceConfigurations.setDeviceModel(model);
				deviceConfigurations.setUpdatedDate(new Date());
				if (null != latestDeviceConfig) {
					deviceConfigurations.setLowBattery(latestDeviceConfig.getLowBattery());
					deviceConfigurations.setGpsReportFrequency(latestDeviceConfig.getGpsReportFrequency());
					deviceConfigurations.setWearableSyncFrequency(latestDeviceConfig.getWearableSyncFrequency());
					deviceConfigurations.setDeviceSelfTestingVersion(latestDeviceConfig.getDeviceSelfTestingVersion());
				}

				if (null != f)
					deviceConfigurations.setFirmwareFile(f.getName());
				deviceConfigurations.setFirmwaresize(kilobytes);
				log.info("Into type Create()");
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, OperationType, summary, ipaddress);
				accountService.createDeviceConfigccount(deviceConfigurations);
				activityLogs.info(activityLog);

				responseJSON = ErrorCodesUtil.displayErrorJSON("API Request Success", "SUC01", type);
				//JSONUtility.respondAsJSON(response, gradesJson);
				//return responseJSON;

			} catch (Exception e) {
				log.info(e.getMessage());
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, OperationType, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid User ";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, OperationType, summary, ipaddress);
			activityLogs.error(activityLog);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/getDeviceConfigListDetails", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getDeviceConfigListDetails(@RequestParam("selectedModelValue") String selectedModelValue, @RequestParam("token") String sessionID,
			@RequestParam("pageid") int pageid) throws ParseException {

		log.debug("selectedModelValue >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+selectedModelValue);
		Map<Object, Object> map = null;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();
		int total = PAGINATION_NO_OF_RECORDS;
		int noofpages = 0;
		int currentPage = pageid;

		log.debug("logged our session Id" + sessionID);
		log.debug("deviceManagement");
		Users user = userService.validateUserBySession(sessionID);
		String download_url = this.configProperties.getProperty("downloads.url");
		String firmWare_path = this.configProperties.getProperty("firmware.download.path");
		if (null != user) {
			String firmware_url = null;
			if (pageid == 1) {
			} else {
				pageid = (pageid - 1) * total + 1;
			}
			List<DeviceConfigurations> deviceconfigurations = this.accountService.getDeviceConfigurationList(pageid - 1,
					total, selectedModelValue);
			log.info("deviceconfigurations" + deviceconfigurations.size());
			int totalNooffirmwares = accountService.getTotalNoofFirrmWare(selectedModelValue);
			if (totalNooffirmwares < total)
				noofpages = 1;
			else if (totalNooffirmwares % total == 0)
				noofpages = totalNooffirmwares / total;
			else
				noofpages = totalNooffirmwares / total + 1;

			for (DeviceConfigurations devicdConfig : deviceconfigurations) {
				if (null != devicdConfig.getFirmwareFile())
					firmware_url = download_url + firmWare_path + "/" + devicdConfig.getFirmwareFile();
				log.info("devicdConfig.getFirmwareFile()" + devicdConfig.getFirmwareFile());
				map = new LinkedHashMap<>();
				int associatedDecviceCount = this.accountService
						.getTotalnoOfSchoolwithConfigId(devicdConfig.getDeviceConfigId());
				map.put("config_id", devicdConfig.getDeviceConfigId());
				map.put("firmwareVersion", devicdConfig.getFirmwareVersion());
				map.put("description", devicdConfig.getFirmwareName());
				map.put("relasedDate", devicdConfig.getCreatedDate().toString());
				map.put("Size", devicdConfig.getFirmwaresize());
				if (null != devicdConfig.getFirmwaresize())
					map.put("fileMame", firmware_url);
				map.put("noofPages", noofpages);
				map.put("currentPage", currentPage);
				if (associatedDecviceCount > 0)
					map.put("associatedDecviceCount", associatedDecviceCount);
				finalList.add(map);
			}

			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			responseJSON = ErrorCodesUtil.displayDeviceList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
		} else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/deleteDeviceConfig", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView deleteDeviceConfig(@RequestParam("token") String sessionID,
			@RequestParam("deviceid") int deviceConfigid) {
		log.info("entered into delete Action");
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
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		ModelAndView model = new ModelAndView();
		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		log.info("Invoked ");
		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
		List<Integer> deleteDeviceIDList = this.accountService.getDeviceIdByConfigId(deviceConfigid);
		int i = 1;
		for (int device_id : deleteDeviceIDList) {
			this.accountService.deleteDevice(device_id);
			i++;
		}
		log.info("valu of i " + i);
		log.info("valu of list  " + deleteDeviceIDList.size());

		this.accountService.deleteDeviceConfig(deviceConfigid);
		activityLogs.info(activityLog);
		this.httpSession.setAttribute("isDeleted", "true");
		return new ModelAndView("redirect:/adminDeviceManagement?token=" + sessionID);

	}

	@RequestMapping(value = "/getSchoolList", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getSchoolList(@RequestParam("token") String sessionID, @RequestParam("pageid") int pageid)
			throws ParseException {

		Map<Object, Object> map = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int noofpages = 0;
		int currentPage = pageid;
		ArrayList<Object> finalList = new ArrayList<>();
		String deviceJSON = null;
		Map<Object, Object> outermap = new LinkedHashMap<>();
		new ArrayList<>();
		log.debug("logged our session Id" + sessionID);
		log.debug("School Management");
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {

			if (pageid == 1) {
			} else {
				pageid = (pageid - 1) * total + 1;
			}

			List<SchoolModel> schoolsList = prepareSchoolsModelList(SchoolService.listSchools(pageid - 1, total));

			int totalNoofSchools = accountService.getTotalNoofSchools();

			if (totalNoofSchools < total)
				noofpages = 1;
			else if (totalNoofSchools % total == 0)
				noofpages = totalNoofSchools / total;
			else
				noofpages = totalNoofSchools / total + 1;
			if (null != schoolsList) {
				for (SchoolModel school : schoolsList) {

					map = new LinkedHashMap<>();
					if (school.getAccountName() != null)
						map.put("accountName", school.getAccountName());

					map.put("accountId", school.getAccountID());

					if (school.getMobileNumber() != null)
						map.put("contactNumber", school.getMobileNumber());

					if (school.getSchoolAdmin() != null)
						map.put("schoolAdmin", school.getSchoolAdmin());
					if (school.getLastLoginDate() != null)
						map.put("lastlogin", school.getLastLoginDate().toString());

					if (school.getCity() != null)
						map.put("city", school.getCity());

					if (school.getZipcode() != null)
						map.put("zipCode", school.getZipcode());

					if (school.getCounty() != null)
						map.put("county", school.getCounty());

					if (school.getState() != null)
						map.put("state", school.getState());

					if (school.getAddress() != null)
						map.put("address", school.getAddress());

					map.put("allocatedDevices", school.getAllocatedDevices());

					map.put("noofPages", noofpages);

					map.put("currentPage", currentPage);

					finalList.add(map);
				}
			}
			outermap.put("finalList", finalList);
			deviceJSON = JSONObject.toJSONString(outermap);
			log.info("gradesJSON" + "\t" + deviceJSON);
			// gradesList.add(deviceJSON);
			responseJSON = ErrorCodesUtil.displayAssigenedDeviecList("API Request Success", "SUC01", deviceJSON);
			//JSONUtility.respondAsJSON(response, gradesJson);
		}
		else {
			StatusCode = "ERR01";
			ErrorMessage = "Invalid user";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/deleteSchoolApi/{token}/{accountId}")
	public String deleteSchoolApi(@PathVariable("token") String sessionID, @PathVariable("accountId") int accountId) {
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
			log.error("deleteSchoolApi, Error while getting: InetAddress");
		}

		String statusCode = null;
		String statusMsg = null;
		String type = null;

		Users sessionUser = userService.validateUserBySession(sessionID);
		if (null != sessionUser) {
			if (sessionUser.getRoleType().equals(Constant.SuperAdmin)) {
				boolean deleteSchool = this.SchoolService.deleteSchool(accountId);
				if(deleteSchool == true) {
					//Call Cassandra Function To Delete Keyspace
					this.cassandraService.deleteKeyspaceBySchoolId(accountId);
					
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					type = "account.deleteSchoolApi";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					
					summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
					ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
					activityLogs.info(activityLog);
				} else {
					statusCode = "ERR00";
					statusMsg = "Delete School Failed";
					type = "account.deleteSchoolApi";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					
					summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
					ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
					activityLogs.error(activityLog);					
				}
			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "account.deleteSchoolApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "account.deleteSchoolApi";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);

			summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
			activityLogs.error(activityLog);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/deleteDeviceApi/{token}/{deviceID}")
	public String deleteDeviceApi(@PathVariable("deviceID") int device_id, @PathVariable("token") String token) {

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
			log.error("deleteSchoolApi, Error while getting: InetAddress");
		}

		String statusCode = null;
		String statusMsg = null;
		String type = null;

		Users sessionUser = userService.validateUserBySession(token);

		if (null != sessionUser) {
			if (sessionUser.getRoleType().equals(Constant.SuperAdmin)) {
				this.accountService.deleteDevice(device_id);

				statusCode = "SUC01";
				statusMsg = "API Request Success";
				type = "account.deleteDeviceApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.info(activityLog);

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "account.deleteDeviceApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "account.deleteDeviceApi";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);

			summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
			activityLogs.error(activityLog);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/deleteBrokenDeviceApi/{token}/{schoolID}")
	public String deleteBrokenDeviceApi(@PathVariable("schoolID") int school_id, @PathVariable("token") String token) {

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
			log.error("deleteSchoolApi, Error while getting: InetAddress");
		}

		String statusCode = null;
		String statusMsg = null;
		String type = null;

		Users sessionUser = userService.validateUserBySession(token);

		if (null != sessionUser) {
			if (sessionUser.getRoleType().equals(Constant.SuperAdmin)) {
				this.accountService.deleteBrokenDevice(school_id);

				statusCode = "SUC01";
				statusMsg = "API Request Success";
				type = "account.deleteDeviceApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.info(activityLog);

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "account.deleteDeviceApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "account.deleteDeviceApi";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);

			summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
			activityLogs.error(activityLog);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/deleteReturndeDeviceApi/{token}/{schoolID}")
	public String deleteReturndeDeviceApi(@PathVariable("schoolID") int school_id,
			@PathVariable("token") String token) {

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
			log.error("deleteSchoolApi, Error while getting: InetAddress");
		}

		String statusCode = null;
		String statusMsg = null;
		String type = null;

		Users sessionUser = userService.validateUserBySession(token);

		if (null != sessionUser) {
			if (sessionUser.getRoleType().equals(Constant.SuperAdmin)) {
				this.accountService.deleteReturndeDevice(school_id);

				statusCode = "SUC01";
				statusMsg = "API Request Success";
				type = "account.deleteDeviceApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.info(activityLog);

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "account.deleteDeviceApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "account.deleteDeviceApi";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);

			summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
			activityLogs.error(activityLog);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/deleteDeviceConfigApi/{token}/{config_id}")
	public String deleteDeviceConfigApi(@PathVariable("config_id") int config_id, @PathVariable("token") String token) {

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
			log.error("deleteSchoolApi, Error while getting: InetAddress");
		}

		String statusCode = null;
		String statusMsg = null;
		String type = null;

		Users sessionUser = userService.validateUserBySession(token);

		if (null != sessionUser) {
			if (sessionUser.getRoleType().equals(Constant.SuperAdmin)) {
				List<Integer> deleteDeviceIDList = this.accountService.getDeviceIdByConfigId(config_id);
				int i = 1;
				for (int device_id : deleteDeviceIDList) {
					this.accountService.deleteDevice(device_id);
					i++;
				}
				log.info("valu of i " + i);
				log.info("valu of list  " + deleteDeviceIDList.size());

				this.accountService.deleteDeviceConfig(config_id);

				statusCode = "SUC01";
				statusMsg = "API Request Success";
				type = "account.deleteDeviceConfigApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.info(activityLog);

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "account.deleteDeviceConfigApi";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);

				summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				activityLogs.error(activityLog);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "account.deleteDeviceConfigApi";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);

			summary += ", statusCode:" + statusCode + ", statusMsg:" + statusCode + ", type" + statusCode;
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
			activityLogs.error(activityLog);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/web/SearchSchool/{sessionID}/{schoolid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SearchSchool(@PathVariable String sessionID, @PathVariable("schoolid") int schoolid) {

		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;

		String logType = "searchstudent";
		List<SearchSchoolTransform> searchSchoolTrfrmList = null;
		String statusCode = null;
		String statusMsg = null;
		String type = null;
		StringBuilder addressBuilder = new StringBuilder();
		String address = null;

		Users user = userService.validateUserBySession(sessionID);
			if (null != user) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					statusCode = "ERR02";
					statusMsg = "Session Expired ,Please Relogin ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
					return responseJSON;
				}
			}

		if (null != user && user.getRoleType().equals(Constant.SupprotStaff)) {

			searchSchoolTrfrmList = this.SchoolService.searchSchool(schoolid);

			for (SearchSchoolTransform sst : searchSchoolTrfrmList) {
				map = new LinkedHashMap<>();

				if (null != sst.getAddress())
					addressBuilder.append(sst.getAddress() + ", ");
				if (null != sst.getCity())
					addressBuilder.append(sst.getCity() + ", ");
				if (null != sst.getCounty())
					addressBuilder.append(sst.getCounty() + ", ");
				if (null != sst.getState())
					addressBuilder.append(sst.getState() + ", ");
				if (null != sst.getZipcode())
					addressBuilder.append(sst.getZipcode() + ", ");
				if (null != sst.getCountry())
					addressBuilder.append(sst.getCountry());
				address = addressBuilder.toString();

				if (sst.getId() != null)
					map.put("id", sst.getId());
				if (sst.getContact() != null)
					map.put("contact", sst.getContact());
				if (sst.getUsername() != null)
					map.put("username", sst.getUsername());
				if (sst.getLastlogin() != null)
					map.put("lastlogin", sst.getLastlogin().toString());
				if (sst.getAlloteddevice() != null)
					map.put("allocateddevice", sst.getAlloteddevice().toString());
				if (sst.getAddress() != null)
					map.put("address", address);
				jsonString = JSONObject.toJSONString(map);
				myList.add(jsonString);
			}
			responseJSON = ErrorCodesUtil.displayJSONForActivityNotificationLog(Constant.SucessMsgActivity,
					Constant.StatusCodeActivity, myList.toString(), Constant.SEARCH_SCHOOL_TYPE, logType);
			// return respondJson;
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
					Constant.UnauthorisedUserErrorCodeDescription, Constant.SEARCH_SCHOOL_TYPE);
			// JSONUtility.respondAsJSON(response, responseJSON);
			//return respondJson;
		}
		return responseJSON;
	}
}
