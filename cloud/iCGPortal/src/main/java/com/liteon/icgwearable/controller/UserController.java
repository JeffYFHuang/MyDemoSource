package com.liteon.icgwearable.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.EventSubscriptions;
import com.liteon.icgwearable.hibernate.entity.SchoolCalendar;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.AccountRegisters;
import com.liteon.icgwearable.model.GuardianModel;
import com.liteon.icgwearable.model.MemberPreferences;
import com.liteon.icgwearable.model.NewDevicePairingModel;
import com.liteon.icgwearable.model.ParentAppRegisterModel;
import com.liteon.icgwearable.model.ParentModel;
import com.liteon.icgwearable.model.TokenUpdateModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.service.AccountService;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.service.EventsService;
import com.liteon.icgwearable.service.ParentService;
import com.liteon.icgwearable.service.SchoolService;
import com.liteon.icgwearable.service.StudentsService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.GuardiansDetailsListTransform;
import com.liteon.icgwearable.transform.KidsListForParentMemberTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.DateUtil;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.HolidayCSVToJavaUtil;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.ProcessMailUtility;
import com.liteon.icgwearable.util.StringUtility;
import com.liteon.icgwearable.util.WebUtility;
import com.liteon.icgwearable.validator.*;

/**
 * @author Vikas,RK,Sarat, Rattaiah
 * @author Saroj Das Sonar issue was fixed
 * 
 */

@RestController
public class UserController {

	private static Logger log = Logger.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private EventsService eventsService;

	@Autowired
	private SchoolService SchoolService;

	@Autowired
	private StudentsService studentService;

	@Autowired
	private ActivityLogService activityLogs;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	HttpServletResponse response;
	@Resource(name = "configProperties")
	private Properties configProperties;
	@Value("${SCHOOL_ENTRY_ALERT_ID}")
	private int entryAlertId;
	@Value("${SCHOOL_EXIT_ALERT_ID}")
	private int exitAlertId;
	@Value("${SOS_ALERT_ID}")
	private int sosAlertId;
	@Value("${MAX_PARENTS}")
	private int max_parents;
	@Value("${BAND_REMOVAL_ALERT_ID}")
	private int bandRemovalAlertId;
	@Value("${display.dateTime}")
	private String sourceDateFormat;
	@Value("${display.dateFormat}")
	private static String sourceDisplayDateFormat;
	@Value("${Map_Url}")
	private String Map_Url;

	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;
	String validationResult;
	@Autowired
	private ProcessMailUtility processMailUtility;
	@Value("${display.dateTime}")
	private String sourceDateTime;
	@Value("${db.dateFormat}")
	private String dbDateFormat;
	DateUtil dateUtil = new DateUtil();

	@RequestMapping(value = "/home", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView home(ModelAndView model) {
		model.setViewName("home");
		return model;
	}

	@RequestMapping(value = "/adminlogin", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView adminlogin(ModelAndView model) {
		model.setViewName("adminlogin");
		return model;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView login(ModelAndView model) {
		model.addObject("googleId", this.configProperties.getProperty("GOOGLE.CLIENT.ID"));
		model.addObject("facebookId", this.configProperties.getProperty("FACEBOOK.APP.ID"));
		model.addObject("facebookAPIVersion", this.configProperties.getProperty("FACEBOOK.API.VERSION"));
		model.addObject(Constant.MapUrl, this.configProperties.getProperty("Map_Url"));

		model.setViewName("login");
		return model;
	}

	@RequestMapping(value = "/mobile/MobileAppTokenUpdate/{sessionId}", method = RequestMethod.PUT, produces = {
			"application/json" })
	public String MobileAppTokenUpdate(@RequestBody TokenUpdateModel tokenUpdateModel,
			@PathVariable("sessionId") String sessionId, ModelAndView model) throws UnknownHostException {

		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		action = "Update";
		summary = className + "." + methodName;
		InetAddress ipAddr = InetAddress.getLocalHost();
		ipaddress = ipAddr.getHostAddress();
		String responseJSON = null;
		String type = "user.MobileAppTokenUpdate";
		String appToken = tokenUpdateModel.getAppToken();
		String appType = tokenUpdateModel.getAppType();
		log.info("appToken" + appToken + "appType" + appType);
		List<String> appTypeList = new ArrayList<String>();
		appTypeList.add("android");
		appTypeList.add("iphone");

		String json = JSONUtility.convertObjectToJson(tokenUpdateModel);
		JSONUtility jsonUtility = new JSONUtility();
		boolean flag = jsonUtility.checkJsonInputForActivityLog(json);
		if (!flag) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessage5, Constant.StatusCode, type);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionId);
		if (user != null) {
			String mobileSession = user.getMobileSessionId();
			if (null == mobileSession) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
							Constant.StatusCodeValidity, type);
					return responseJSON;
				}
			}
			if (tokenUpdateModel.getAppToken().isEmpty() || tokenUpdateModel.getAppType().isEmpty()) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessage4, Constant.StatusCode4, type);
				return responseJSON;
			}
			if (!appTypeList.contains(tokenUpdateModel.getAppType())) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageType, Constant.StatusCode, type);
				return responseJSON;
			}
			String roleType = user.getRoleType();
			if (roleType.equals(Constant.ParentAdmin) || roleType.equals(Constant.ParentMember)
					|| roleType.equals(Constant.SchoolTeacher) || roleType.equals(Constant.SchoolStaff)) {
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				userService.updateAppToken(user.getId(), appToken, appType);
				activityLogs.info(activityLog);
				responseJSON = ErrorCodesUtil.displaySuccessJSON(Constant.SucessMsgActivity,
						Constant.StatusCodeActivity, type);
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
						Constant.UnauthorisedUserErrorCodeDescription, type);
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon, type);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/adminlogin", method = RequestMethod.POST, params = "login")
	public ModelAndView adminlogin(ModelAndView model, @ModelAttribute("userModel") UsersModel userModel,
			HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException, UnknownHostException {
		log.info("Admin Login Page - Step 1");
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		action = "UserLogin";
		summary = className + "." + methodName;
		InetAddress ipAddr = InetAddress.getLocalHost();
		ipaddress = ipAddr.getHostAddress();
		String sessionID = null;
		log.info("Admin Login Page - Step 2");
		if ((userModel.getUsername() == null || userModel.getUsername().trim().equals(""))
				|| ((userModel.getPassword() == null) || userModel.getPassword().trim().equals(""))) {
			summary += ", statusCode:" + Constant.LoginError + ", statusMsg:" + Constant.LoginInvalid;
			ActivityLog activityLog = CommonUtil.formulateActivityLogs("Null", action, summary, ipaddress, "Web User",
					"super_admin");
			activityLogs.error(activityLog);

			model.addObject(Constant.LoginError, Constant.LoginInvalid);
			model.setViewName("adminlogin");
			return model;
		}
		log.info("Admin Login Page - Step 3");
		Users user = this.userService.validateUserByUsername(userModel.getUsername());
		if (user == null) {
			summary += ", statusCode:" + Constant.LoginError + ", statusMsg:" + Constant.UsernameInvalid;
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(userModel.getUsername(), action, summary,
					ipaddress, "Web User", "super_admin");
			activityLogs.error(activityLog);

			model.addObject(Constant.LoginError, Constant.UsernameInvalid);
			model.setViewName("adminlogin");
			return model;
		} else {
			if (null != user.getPassword()) {
				boolean isValidPassword = AESEncryption.validatePassword(userModel.getPassword(), user.getPassword());
				if (!isValidPassword) {
					model.addObject(Constant.LoginError, Constant.InvalidPassword);
					model.setViewName("adminlogin");
					return model;
				}
			} else {
				model.addObject(Constant.LoginError, Constant.InvalidPassword);
				model.setViewName("adminlogin");
				return model;
			}

			log.info("Admin Login Page - Step 6");
			if (!(user.getRoleType().equals(Constant.SuperAdmin) || user.getRoleType().equals(Constant.SchoolAdmin)
					|| user.getRoleType().equals(Constant.SystemAdmin)
					|| user.getRoleType().equals(Constant.SupprotStaff))) {
				log.info("Admin Login Page - Step 7");
				summary += ", statusCode:" + Constant.LoginError + ", statusMsg:" + Constant.UnauthoriseUserMessage;
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(userModel.getUsername(), action, summary,
						ipaddress, "Web User", "super_admin");
				activityLogs.error(activityLog);
				log.info("Admin Login Page - Step 8");
				model.addObject(Constant.LoginError, Constant.UnauthoriseUserMessage);
				model.setViewName("adminlogin");
				return model;
			} else {
				String roleType = user.getRoleType();
				int userId = user.getId();
				request.getSession().setAttribute("userId", userId);

				this.httpSession.setAttribute("userId", userId);
				this.httpSession.setAttribute("name", user.getName());
				if (null != user && null != user.getSessionId()) {
					sessionID = user.getSessionId();
				}
				if (null == sessionID) {
					sessionID = this.httpSession.getId();
				}
				model.addObject(Constant.SessionID, sessionID);
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				userService.adduserSessionByID(userId, sessionID, new Date());
				activityLogs.info(activityLog);
				this.httpSession.setAttribute(Constant.UserModel, userModel);
				// this.httpSession.setAttribute(Constant.User_Name,
				// user.getUsername());
				if (roleType.equals(Constant.SuperAdmin)) {
					this.httpSession.setAttribute(Constant.CurrentUser, Constant.SuperAdmin);
					getUserDetails(model, user, Constant.SuperAdminViewname);
				} else if (roleType.equals(Constant.SchoolAdmin)) {
					this.httpSession.setAttribute(Constant.CurrentUser, Constant.SchoolAdmin);
					getUserDetails(model, user, Constant.SchoolAdminViewname);
				} else if (roleType.equals(Constant.SupprotStaff)) {
					log.info("Welcome To Support Staff Page");
					this.httpSession.setAttribute(Constant.CurrentUser, Constant.SupprotStaff);
					List<String> countyList = accountService.getCountys();
					getUserDetails(model, user, Constant.SupportStaffDashboardView);
					model.addObject("countyList", countyList);
					log.info("Size of Countys List" + "\t" + countyList.size());
				} else if (roleType.equals(Constant.SystemAdmin)) {
					this.httpSession.setAttribute(Constant.CurrentUser, Constant.SystemAdmin);
					getUserDetails(model, user, Constant.SystemAdminDashboardView);
				}
			}
		}
		log.info("Admin Login Page - Step Last");
		return model;

	}

	@RequestMapping(value = "/adminProfileManagement", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView adminProfileManagement(ModelAndView model, @RequestParam("token") String sessionID) {
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !(currentUser.equals(Constant.SuperAdmin)
				|| currentUser.equals(Constant.SupprotStaff) || currentUser.equals(Constant.SystemAdmin))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		String roleType = user.getRoleType();
		model.addObject("roleType", roleType);
		if (roleType.equals(Constant.SuperAdmin)) {
			getProfileView(model, sessionID, Constant.SuperAdminProfileManagement);
		} else if (roleType.equals(Constant.SupprotStaff)) {
			getProfileView(model, sessionID, Constant.SupprotStaffProfileManagement);
		} else if (roleType.equals(Constant.SystemAdmin)) {
			getProfileView(model, sessionID, Constant.SystemAdminProfileManagement);
		}
		return model;
	}

	private ModelAndView getProfileView(ModelAndView model, String sessionID, String View) {
		model.addObject(Constant.SessionID, sessionID);
		UsersModel userModel = (UsersModel) this.httpSession.getAttribute(Constant.UserModel);
		if (null == userModel) {
			model.addObject(Constant.LoginError, Constant.LoginInvalid);
			model.setViewName("login");
			return model;
		}
		Users user = this.userService.getUserDetails(userModel.getUsername());
		model.addObject(Constant.FirstName, user.getName());
		model.addObject(Constant.UserDetails, user);
		model.setViewName(View);
		return model;

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, params = "login")
	public ModelAndView login(ModelAndView model, @ModelAttribute("userModel") UsersModel userModel,
			HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeySpecException, UnknownHostException {
		log.info("Parent Login Page - Step 1");
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		action = "UserLogin";
		summary = className + "." + methodName;
		InetAddress ipAddr = InetAddress.getLocalHost();
		ipaddress = ipAddr.getHostAddress();
		String sessionID = null;
		log.info("userForm.getUsername()" + "\t" + userModel.getUsername());
		if ((userModel.getUsername() == null || userModel.getUsername().trim().equals(""))
				|| ((userModel.getPassword() == null) || userModel.getPassword().trim().equals(""))) {
			model.addObject(Constant.LoginError, Constant.LoginInvalid);
			model.setViewName("login");
			return model;
		}
		log.info("Parent Login Page - Step 2");
		Users user = this.userService.validateUserByUsername(userModel.getUsername());
		log.info("Parent Login Page - Step 3");
		if (user == null) {
			model.addObject(Constant.LoginError, Constant.UsernameInvalid);
			model.setViewName("login");
			return model;
		} else {
			log.debug("preUser.getPassword()" + user.getPassword());
			if (null != user.getPassword()) {
				boolean isValidPassword = AESEncryption.validatePassword(userModel.getPassword(), user.getPassword());
				log.debug("isValidPassword" + isValidPassword);
				if (!isValidPassword) {
					model.addObject(Constant.LoginError, Constant.InvalidPassword);
					model.setViewName("login");
					return model;
				}
			} else {
				model.addObject(Constant.LoginError, Constant.InvalidPassword);
				model.setViewName("adminlogin");
				return model;
			}
			String roleType = user.getRoleType();
			if (roleType.equals(Constant.ParentAdmin)) {
				int userId = user.getId();
				request.getSession().setAttribute("userId", userId);

				log.debug("userId" + "\t" + userId);
				log.debug("sessionID from logging " + this.httpSession.getId());

				log.debug("used from session" + this.httpSession.getAttribute("userId"));
				log.debug("RoleType" + "\t" + roleType);
				if (null != user && null != user.getSessionId())
					sessionID = user.getSessionId();
				if (null == sessionID)
					sessionID = this.httpSession.getId();
				model.addObject(Constant.SessionID, sessionID);

				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);

				log.info("Parent Login Page - Step 4");
				userService.adduserSessionByID(userId, sessionID, new Date());
				activityLogs.info(activityLog);

				this.httpSession.setAttribute(Constant.CurrentUser, Constant.ParentAdmin);
				List<TeachersStudentsTransform> teacherStudentsList = this.parentService.viewKidsList(userId, null);
				model.addObject(Constant.TeacherStudentList, teacherStudentsList);
				if (teacherStudentsList.isEmpty()) {
					getUserDetails(model, user, Constant.NewParentProfile);
				} else {
					getUserDetails(model, user, Constant.ParentAdminView);
				}
			} else {
				model.addObject(Constant.LoginError, Constant.UnauthoriseUserMessage);
				model.setViewName("login");
				return model;
			}
		}
		log.info("Parent Login Page - Step End");
		return model;
	}

	public ModelAndView getUserDetails(ModelAndView model, Users user, String viewName) {
		log.info("Inside New getUserDetails");

		this.httpSession.setAttribute(Constant.AccountID, user.getAccountId());
		this.httpSession.setAttribute(Constant.UserID, user.getId());

		if (null != user.getOpenidUsername()) {
			model.addObject(Constant.User_Name, user.getOpenidUsername());
			this.httpSession.setAttribute(Constant.User_Name, user.getOpenidUsername());
			if (null != user.getName()) {
				this.httpSession.setAttribute(Constant.FirstName, user.getName());
				model.addObject(Constant.FirstName, user.getName());
			}
		} else {
			model.addObject(Constant.FirstName, user.getName());
			model.addObject(Constant.User_Name, user.getUsername());
			this.httpSession.setAttribute(Constant.FirstName, user.getName());
			this.httpSession.setAttribute(Constant.User_Name, user.getUsername());
		}

		model.setViewName(viewName);
		return model;
	}

	/**
	 * This is User login RestAPIService. with the URL
	 * http://localhost:8080/icgwearable/login/ Initial Request should be :
	 * 
	 * @param
	 * 
	 * 			{
	 *            "username":"superadmin", "password":"superadmin" }
	 * 
	 * 
	 *            id user is already logged in from other devide and current
	 *            user want to force login Request should be
	 * 
	 *            { "username":"superadmin", "password":"superadmin",
	 *            "forcelogin":"Yes" }
	 * 
	 * 
	 * @return
	 * 
	 * 		Failure response has StatusCode "LUA01" and contains error
	 *         message.
	 * 
	 *         <pre>
	 *         { "Return": { "Type": "login.UserLogin", "ResponseSummary": {
	 *         "StatusCode": "LUA01", "ErrorMessage": "Invalid User Details" } }
	 *         }
	 *
	 *
	 *         Success response has empty StatusCode and empty error message
	 *         with session id returned
	 *
	 *         { "Return": { "Type": "login.UserLogin", "ResponseSummary": {
	 *         "StatusCode": "", "ErrorMessage": "", "SessionId":
	 *         "2987787FE66B0F7913574282676B91A7", "Forcelogin": "null" } } }
	 *
	 *
	 *
	 *
	 * @throws ScriptException
	 */
	@RequestMapping(value = "/mobile/UserLogin", method = RequestMethod.POST, produces = { "application/json" })
	public String UserLogin(@RequestBody UsersModel usersModel) throws ScriptException {
		Users user = null;
		boolean isValidPassword = false;
		String Json = JSONUtility.convertObjectToJson(usersModel);
		org.json.JSONObject jObject = new org.json.JSONObject(Json);
		Iterator keysToCopyIterator = jObject.keys();
		List<String> keysList = new ArrayList<String>();
		while (keysToCopyIterator.hasNext()) {
			String key = (String) keysToCopyIterator.next();
			keysList.add(key);
		}
		log.info("no of parameters : " + keysList.size());
		String userName = usersModel.getUsername();
		String passwWord = usersModel.getPassword();
		String forceLogin = usersModel.getForcelogin();
		// String isConfirmed = usersModel.getForcelogin();
		String sessionID = null;
		log.debug("userName :" + userName);
		log.debug("passwWord :" + passwWord);
		// log.debug("isConfirmed :" + isConfirmed);
		String responseJSON = null;
		log.info("userName" + userName);

		action = Constant.UserLoginAction;
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

		String json = JSONUtility.convertObjectToJson(usersModel);
		log.debug("json" + "\n" + json);
		if (!JSONUtility.isValidJson(json) || null != forceLogin) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.IsValidJsonErrorMessage,
					Constant.IsValidJsonStatusCode, Constant.UserUserLoginType);
			return responseJSON;
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		if (null == userName || null == passwWord) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.IsNullJsonErrorMessage,
					Constant.IsValidJsonStatusCode, Constant.UserUserLoginType);
			return responseJSON;
		}

		if (userName.isEmpty() || passwWord.isEmpty()) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.IsEmptyJsonErrorMessage,
					Constant.IsValidJsonStatusCode, Constant.UserUserLoginType);
			return responseJSON;
		}

		try {
			user = this.userService.validateUserByUsername(usersModel.getUsername());
			log.debug("userModel.getPassword()" + usersModel.getPassword());
			if (null != user) {
				log.info("preUser.getPassword()" + user.getPassword());
				isValidPassword = AESEncryption.validatePassword(usersModel.getPassword(), user.getPassword());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != user && isValidPassword) {
			if (user.getRoleType().equals("parent_admin") || user.getRoleType().equals("parent_member")
					|| user.getRoleType().equals("school_teacher") || user.getRoleType().equals("school_staff")) {
				log.info("if loop entered :");
				if (null != user.getMobileSessionId()) {
					sessionID = user.getMobileSessionId();
				} else {
					log.info("else");
					sessionID = this.httpSession.getId();
					Users oldUser = userService.getUserByMobileSessionId(sessionID);
					if (null != oldUser) {
						ActivityLog activityLog = CommonUtil.formulateActivityLogs(oldUser, action, summary, ipaddress);
						this.userService.resetSessionDetails(sessionID);
						if (null != activityLog.getName())
							activityLogs.info(activityLog);
						this.httpSession.invalidate();
						sessionID = this.httpSession.getId();
					}
				}

				log.info("sessionID" + "\t" + sessionID);

				ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
				userService.addMobileUserSessionByID(user.getId(), sessionID, new Date());
				activityLogs.info(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSONForuserLogin(Constant.ValidErrorMessage,
						Constant.ValidStatusCode, Constant.UserUserLoginType, sessionID, user.getId());
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.UserUserLoginType);
				return responseJSON;
			}
		}

		else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.DisplayErrorMessage, Constant.DisplayStatusCode,
					Constant.UserUserLoginType);
		}
		return responseJSON;

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView logout(ModelAndView model, @RequestParam("token") String sessionID) {
		model.addObject("googleId", this.configProperties.getProperty("GOOGLE.CLIENT.ID"));
		model.addObject("facebookId", this.configProperties.getProperty("FACEBOOK.APP.ID"));
		model.addObject("facebookAPIVersion", this.configProperties.getProperty("FACEBOOK.API.VERSION"));
		model.addObject(Constant.MapUrl, this.configProperties.getProperty("Map_Url"));

		action = "UserLogout";
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		// HttpSession httpSession=new HttpSession("false");
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("logged our session Id" + sessionID);
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		WebUtility webUtility = WebUtility.getWebUtility();
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			return webUtility.getModelAndViewObject();
		}

		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
		this.userService.resetSessionDetails(sessionID);
		if (null != activityLog.getName()) {
			log.info("logged our session Id[" + activityLog.getName() + "]");
			activityLogs.info(activityLog);
		}
		model.addObject(Constant.UserMessage, Constant.UserLoggedout);
		model.setViewName("home");
		this.httpSession.invalidate();
		return model;
	}

	/**
	 * This is User logout RestAPIService with the URL
	 * http://localhost:8080/icgwearable/logout/
	 * 
	 * @param sessionID
	 * 
	 * 
	 * @return
	 * 
	 * 		Failure response has StatusCode "LUA01" and contains error
	 *         message.
	 * 
	 *         <pre>
	 *         { "Return": { "Type": "login.UserLogout", "ResponseSummary": {
	 *         "StatusCode": "LUA01", "ErrorMessage": "Invalid Session Details"
	 *         } } }
	 *
	 *
	 *         Success response has empty StatusCode and empty error message
	 *         with session id returned
	 *
	 *         { "Return": { "Type": "login.UserLogout", "ResponseSummary": {
	 *         "StatusCode": "", "ErrorMessage": "" } } }
	 *
	 *
	 * @throws ScriptException
	 */

	@RequestMapping(value = "/mobile/UserLogout/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String UserLogout(@PathVariable("sessionID") String sessionID) {
		String responseJSON = null;
		String type = "user.UserLogout";
		log.debug("logged our session Id" + sessionID);

		action = "UserLogout";
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
		if (null != user) {
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			this.userService.resetMobileSessionDetails(sessionID);
			activityLogs.info(activityLog);
			this.httpSession.invalidate();
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ActivitySucessmessage, Constant.ActivityStatusCode,
					type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		} else {
			// ActivityLog activityLog = CommonUtil.formulateActivityLogs(user,
			// action, summary, ipaddress);
			// activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ActivityElsSucessmessage,
					Constant.ActivityElsStatusCode, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	/**
	 * This is ParentMember list details from Parent Admin User RestAPIService
	 * with the URL
	 * http://localhost:8080/icgwearable/GuardianSubscriptionsList/<<Current
	 * Session Id>>/
	 * 
	 * @param session
	 *            ID
	 * 
	 * 
	 * @return
	 * 
	 * 		{ "Return": { "Type": "ParentAdmin : list Parent Members",
	 *         "ResponseSummary": { "StatusCode": "", "ErrorMessage": "" },
	 *         "Results": { "Members List": [ { "SubscrptionID": 1, "KidName":
	 *         "T", "MemberName": "Abhinav", "UUID":
	 *         "f81d4fae-7dec-11d0-a765-02342591e6bf6", "EntryExit": { "id": 1,
	 *         "value": "yes" }, "SOS": { "id": 6, "value": "no" },
	 *         "BandRemoval": { "id": 19, "value": "yes" } }, { "SubscrptionID":
	 *         2, "KidName": "T", "MemberName": "Abhinav", "UUID":
	 *         "f81d4fae-7dec-11d0-a765-02342591e6bf6", "EntryExit": { "id": 1,
	 *         "value": "yes" }, "SOS": { "id": 6, "value": "yes" },
	 *         "BandRemoval": { "id": 19, "value": "no" } } ] } } }
	 *
	 * @throws ScriptException
	 */

	@RequestMapping(value = "/mobile/GuardianSubscriptionsList/{sessionID}/{member_id}/{student_id}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String GuardianSubscriptionsList(ModelAndView model, @PathVariable("sessionID") String sessionID,
			@PathVariable int member_id, @PathVariable int student_id) {

		JSONArray jsonArray = new JSONArray();
		List myList = new ArrayList<>();
		String jsonArrayString = null;
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String kidName = null;

		String uuId = null;
		Map<Integer, List<KidsListForParentMemberTransform>> deviceMap = new HashMap<Integer, List<KidsListForParentMemberTransform>>();
		String jsonString = null;
		log.debug("logged our session Id" + sessionID);
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, Constant.SubscriptionsType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (!(user.getRoleType().equals("parent_admin"))) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.SubscriptionsType);
				return responseJSON;
			}

			int getValidStudentid = eventsService.getvalidStudentUnderParentmember(student_id, member_id);
			if (getValidStudentid > 0) {
				List<KidsListForParentMemberTransform> kidsList = userService.getKidsForParentMember(user.getId(),
						member_id, student_id);
				log.debug("size is :" + kidsList.size());

				for (KidsListForParentMemberTransform kid : kidsList) {
					if (!deviceMap.containsKey(kid.getStudentId())) {
						List<KidsListForParentMemberTransform> list = new ArrayList<KidsListForParentMemberTransform>();
						list.add(kid);
						deviceMap.put(kid.getStudentId(), list);
					} else {
						deviceMap.get(kid.getStudentId()).add(kid);
					}

				}
				log.debug(deviceMap);
				log.info("deviceMap.size() ::" + deviceMap.size());
				Map<Integer, String> alertMap = new HashMap<Integer, String>();
				alertMap.put(entryAlertId, "School_Entry");
				alertMap.put(exitAlertId, "School_Exit");
				alertMap.put(sosAlertId, "SOS");
				alertMap.put(bandRemovalAlertId, "BandRemoval");
				List outermap = new ArrayList();
				Map outmostmap = new HashMap();
				Map<Object, Object> map = new LinkedHashMap<>();

				if (deviceMap.size() > 0) {
					Iterator<Map.Entry<Integer, List<KidsListForParentMemberTransform>>> mapIterator = deviceMap
							.entrySet().iterator();

					while (mapIterator.hasNext()) {
						log.debug("entered into while loop");
						Map.Entry<Integer, List<KidsListForParentMemberTransform>> pair = mapIterator.next();
						List<KidsListForParentMemberTransform> tempList = pair.getValue();

						List<Integer> eventIds = new ArrayList<>();
						JSONObject SOS = new JSONObject();
						JSONObject Entry = new JSONObject();
						JSONObject Exit = new JSONObject();
						JSONObject BandRemoval = new JSONObject();

						for (KidsListForParentMemberTransform kid : tempList) {

							// map.put("subscription_id",
							// kid.getSubscriptionId());

							if (kid.getStudentnickName() != null) {
								kidName = kid.getStudentnickName();
								map.put(Constant.NickName, kidName);
							}

							if (kid.getUuId() != null) {
								uuId = kid.getUuId();
								map.put(Constant.UUID, uuId);
							}
							log.debug("event id is :" + kid.getEventId());
							if (kid.getEventId() != null)
								eventIds.add(kid.getEventId());
						}
						log.debug("eventIdssize is :" + eventIds.size());

						for (int i = 0; i < eventIds.size(); i++) {
							int id = eventIds.get(i);
							alertMap.remove(id);

							if (id == entryAlertId) {
								Entry.put("id", id);
								Entry.put("value", "yes");
								map.put("School_Entry", Entry);
							}

							else if (id == exitAlertId) {
								Exit.put("id", id);
								Exit.put("value", "yes");
								map.put("School_Exit", Exit);
							} else if (id == sosAlertId) {
								SOS.put("id", id);
								SOS.put("value", "yes");
								map.put("SOS", SOS);
							} else if (id == bandRemovalAlertId) {
								BandRemoval.put("id", id);
								BandRemoval.put("value", "yes");
								map.put("BandRemoval", BandRemoval);
							}
							log.info("alert map before size for " + i + "st" + alertMap.size());
						}

						eventIds.clear();
						if (alertMap.size() > 0) {
							Set<Integer> keySet = alertMap.keySet();

							Iterator<Integer> iterator = keySet.iterator();

							while (iterator.hasNext()) {
								int keyId = iterator.next();
								log.info("keyid for NOt subscribed" + keyId);
								if (keyId == entryAlertId) {
									Entry.put("id", keyId);
									Entry.put("value", "no");
									map.put("School_Entry", Entry);
								}

								else if (keyId == exitAlertId) {
									Exit.put("id", keyId);
									Exit.put("value", "no");
									map.put("School_Exit", Exit);
								} else if (keyId == sosAlertId) {
									// log.info("entered into 11");
									SOS.put("id", keyId);
									SOS.put("value", "no");
									map.put("SOS", SOS);
								} else if (keyId == bandRemovalAlertId) {
									// log.info("entered into 18");
									BandRemoval.put("id", keyId);
									BandRemoval.put("value", "no");
									map.put("BandRemoval", BandRemoval);
								}
							}
						}
						alertMap.clear();

						outermap.add(map);

					}

					outmostmap.put(Constant.Subscription, outermap);
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
					log.debug("My List Contents::::::::" + myList);
					jsonArrayString = jsonArray.toString();
					log.info("jsonArrayString : " + "\n" + jsonArrayString);

					responseJSON = ErrorCodesUtil.displaySuccessJSONForMemeberList(Constant.SubscriptionsType,
							Constant.SucessMsgActivity, Constant.StatusCodeActivity, jsonString);
				} else {
					JSONObject SOS = new JSONObject();
					JSONObject Entry = new JSONObject();
					JSONObject Exit = new JSONObject();
					JSONObject BandRemoval = new JSONObject();
					if (alertMap.size() > 0) {
						Set<Integer> keySet = alertMap.keySet();

						Iterator<Integer> iterator = keySet.iterator();

						while (iterator.hasNext()) {
							int keyId = iterator.next();
							log.info("keyid for NOt subscribed" + keyId);
							if (keyId == entryAlertId) {
								Entry.put("id", keyId);
								Entry.put("value", "no");
								map.put("School_Entry", Entry);
							}

							else if (keyId == exitAlertId) {
								Exit.put("id", keyId);
								Exit.put("value", "no");
								map.put("School_Exit", Exit);
							} else if (keyId == sosAlertId) {
								// log.info("entered into 11");
								SOS.put("id", keyId);
								SOS.put("value", "no");
								map.put("SOS", SOS);
							} else if (keyId == bandRemovalAlertId) {
								// log.info("entered into 18");
								BandRemoval.put("id", keyId);
								BandRemoval.put("value", "no");
								map.put("BandRemoval", BandRemoval);
							}
						}
					}
					alertMap.clear();

					outermap.add(map);

					outmostmap.put(Constant.Subscription, outermap);
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
					log.debug("My List Contents::::::::" + myList);
					jsonArrayString = jsonArray.toString();
					log.info("jsonArrayString : " + "\n" + jsonArrayString);

					responseJSON = ErrorCodesUtil.displaySuccessJSONForMemeberList(Constant.SubscriptionsType,
							Constant.SucessMsgActivity, Constant.StatusCodeActivity, jsonString);

				}
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.JSONUtilityErrorMessage,
						Constant.JSONUtilityStatusCode, Constant.SubscriptionsType);
				// JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.SubscriptionsType);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	/**
	 * This is ParentMember preferences update api from Parent Admin User
	 * RestAPIService with the URL
	 * http://localhost:8080/icgwearable/GuardianSubscriptionsUpdate/0D074DEDCA07EF483E6E36ECBCDABDC1/2
	 * 
	 * @param sessionID
	 *            , subscrtionID with the body { "alertId" :"1", "alertValue":
	 *            "no" }
	 * 
	 * @return
	 * 
	 *
	 * @throws ScriptException
	 */
	@RequestMapping(value = "/mobile/GuardianSubscriptionsUpdate/{sessionID}/{uuID}/{memberID}/{studentID}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String GuardianSubscriptionsUpdate(@RequestBody List<MemberPreferences> memberpreferenceList,
			ModelAndView model, @PathVariable("sessionID") String sessionID, @PathVariable("uuID") String uuID,
			@PathVariable("memberID") int memberID, @PathVariable("studentID") int studentID) {

		String responseJSON = null;
		List<Integer> AlertIdList = new ArrayList<Integer>();
		List<Integer> successList = new ArrayList<Integer>();
		List<Integer> ignoredList = new ArrayList<Integer>();
		AlertIdList.add(entryAlertId);
		AlertIdList.add(exitAlertId);
		AlertIdList.add(sosAlertId);
		AlertIdList.add(bandRemovalAlertId);
		List<String> AlertValueList = new ArrayList<String>();
		AlertValueList.add("yes");
		AlertValueList.add("no");
		action = Constant.SubscriptionsUpdateAction;
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

		log.debug("logged our session Id" + sessionID);

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
						Constant.StatusCodeValidity, Constant.SubscriptionsUpdateType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (!(user.getRoleType().equals(Constant.ParentAdmin))) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsMessage,
						Constant.SubscriptionsStatusCode, Constant.SubscriptionsUpdateType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			if (memberpreferenceList.size() == 0) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessage5, Constant.StatusCode,
						Constant.SubscriptionsUpdateType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			int pairedStudentid = eventsService.getPairedStatusWithMemberAndDevice(uuID, memberID);
			if (pairedStudentid > 0) {

				for (MemberPreferences memberpreferences : memberpreferenceList) {

					log.debug("memberpreferenceList size  " + memberpreferenceList.size());
					log.debug("memberpreferences alert id  " + memberpreferences.getAlertId());
					log.debug("memberpreferences alert value   " + memberpreferences.getAlertValue());

					List<Integer> subscriberList = userService.getsubscriberListForParentAdmin(user.getAccountId());
					log.debug("memberID given" + memberID);
					// Devices device = eventsService.getDeviceByUuid(uuID);
					// Users subUser = userService.getUser(memberID);
					// Students student = studentService.getStudent(studentID);
					/*
					 * for (Integer integer : subscriberList) {
					 * log.debug("memberID from list" + integer); }
					 */
					if (subscriberList != null && subscriberList.size() > 0 && !(subscriberList.contains(memberID))) {
						log.debug("memberID Not Found");
						responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessage5, Constant.StatusCode,
								Constant.SubscriptionsUpdateType);
						// JSONUtility.respondAsJSON(response, responseJSON);
						return responseJSON;
					} else {
						log.debug("memberID Found");
						String alertValue = memberpreferences.getAlertValue();
						int Alertid = Integer.parseInt(memberpreferences.getAlertId());
						if (memberpreferences.getAlertId().isEmpty() || memberpreferences.getAlertValue().isEmpty()) {
							responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.IsNullJsonErrorMessage,
									Constant.StatusCode4, Constant.SubscriptionsUpdateType);
							// JSONUtility.respondAsJSON(response,
							// responseJSON);
							log.debug("Failed memberpreferences Check" + responseJSON);
							return responseJSON;
						}
						if (!AlertValueList.contains(memberpreferences.getAlertValue())) {
							responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.AlertValueErrorMessage,
									Constant.StatusCode, Constant.SubscriptionsUpdateType);
							// JSONUtility.respondAsJSON(response,
							// responseJSON);
							log.debug("Failed AlertValueList Check" + responseJSON);
							return responseJSON;
						}

						if (AlertIdList.contains(Alertid)) {
							log.debug("AlertIdList with Alertid Check Passed");
							successList.add(Alertid);
							SupportedEvents supportedEvent = eventsService.getSupportedEventsByEventId(Alertid);
							// int deviceId = device.getDeviceId();
							// int userId = subUser.getId();

							/*
							 * EventSubscriptions es = new EventSubscriptions();
							 * es.setStudents(student); es.setUsers(subUser);
							 * es.setEvents(supportedEvent);
							 * es.setCreatedDate(new Date());
							 */
							if (alertValue.equals("yes")) {
								log.debug("Subscribed for: " + Alertid);
								boolean isMemberSubscribed = eventsService.isMemberSubscribed(memberID, studentID,
										Alertid);

								if (isMemberSubscribed != true) {
									log.debug("isMemberSubscribed false");
									ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary,
											ipaddress);
									// eventsService.subscribeEvent(es);
									eventsService.subscribeEvent(memberID, studentID, Alertid);
									activityLogs.info(activityLog);
								}
							} else {
								log.debug("Un-Subscribed for: " + Alertid);
								action = Constant.SubscriptionsDeleteAction;
								ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary,
										ipaddress);
								eventsService.unsubscribeEvent(memberID, studentID, Alertid);
								activityLogs.info(activityLog);
							}
						} else {
							ignoredList.add(Alertid);
						}
					}
				}

				if (successList.size() < ignoredList.size()) {
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.AlertValueErrorMessage, Constant.StatusCode,
							Constant.SubscriptionsUpdateType);
					// JSONUtility.respondAsJSON(response, responseJSON);
					log.debug(responseJSON);
					return responseJSON;
				}
				log.info("ignoredList size: " + ignoredList.size());

				List<Integer> finalList = new ArrayList<Integer>(AlertIdList);
				finalList.removeAll(ignoredList);
				log.info("finalList size: " + finalList.size());
				String successMesssge = "API Request Success";
				String successIdString = "";
				if (ignoredList.size() > 0) {
					int i = 0;
					for (Object object : successList) {

						if (i == 0)
							successIdString = object.toString();
						else
							successIdString += "," + object.toString();
						i++;
					}

					successMesssge = successMesssge + " for the events :" + successIdString;
				}
				finalList.clear();
				responseJSON = ErrorCodesUtil.displaySuccessJSON(successMesssge, Constant.StatusCodeActivity,
						Constant.SubscriptionsUpdateType);
				// JSONUtility.respondAsJSON(response, responseJSON);
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ParentErrorMessage, Constant.ParentStatusCode,
						Constant.SubscriptionsUpdateType);
				// JSONUtility.respondAsJSON(response, responseJSON);
			}

		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.SubscriptionsUpdateType);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/adminResetPassword", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView adminResetPassword(@ModelAttribute("usersModel") UsersModel usersModel) {
		ModelAndView model = new ModelAndView();
		model.setViewName("adminResetPasswordRequest");
		return model;
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView resetPassword(@ModelAttribute("usersModel") UsersModel usersModel) {
		ModelAndView model = new ModelAndView();
		model.addObject("googleId", this.configProperties.getProperty("GOOGLE.CLIENT.ID"));
		model.addObject("facebookId", this.configProperties.getProperty("FACEBOOK.APP.ID"));
		model.addObject("facebookAPIVersion", this.configProperties.getProperty("FACEBOOK.API.VERSION"));
		model.setViewName("resetPassword");
		return model;
	}

	@RequestMapping(value = "/signUp", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView signUp(@ModelAttribute("usersModel") UsersModel usersModel) {
		ModelAndView model = new ModelAndView();
		model.addObject("googleId", this.configProperties.getProperty("GOOGLE.CLIENT.ID"));
		model.addObject("facebookId", this.configProperties.getProperty("FACEBOOK.APP.ID"));
		model.addObject("facebookAPIVersion", this.configProperties.getProperty("FACEBOOK.API.VERSION"));
		model.setViewName("signUp");
		return model;
	}

	@RequestMapping(value = "/guardianSubscription", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView getGuardianSubscription(@RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute(Constant.CurrentUser);
		ModelAndView model = new ModelAndView();

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.UserError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		} else if ((null == this.httpSession.getAttribute(Constant.CurrentUser))) {
			model.addObject(Constant.UserError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		} else if (!currentUser.equals(Constant.ParentAdmin)) {

			model.addObject(Constant.UserError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		} else {
			model.addObject(Constant.SessionID, sessionID);
			model.setViewName("guardianSubscription");
			model.addObject(Constant.FirstName, user.getName());
			return model;
		}

	}

	@RequestMapping(value = "/mobile/GuardianList/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String GuardianList(@PathVariable String sessionID) {

		JSONArray jsonArray = new JSONArray();
		List myList = new ArrayList<>();
		String jsonArrayString = null;

		String responseJSON = null;
		String guardianName = null;

		String jsonString = null;
		// String type = "user.GuardianList";
		log.debug("logged our session Id" + sessionID);

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
						Constant.StatusCodeValidity, Constant.GuardianNameType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (!(user.getRoleType().equals("parent_admin"))) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsMessage,
						Constant.SubscriptionsStatusCode, Constant.GuardianNameType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			List<GuardiansDetailsListTransform> guardiansDetailsList = this.userService
					.getGuardiansDetailsList(user.getAccountId());

			for (GuardiansDetailsListTransform guardianDetails : guardiansDetailsList) {
				Map<Object, Object> map = new LinkedHashMap<>();
				map.put("user_id", guardianDetails.getUser_id());

				if (guardianDetails.getGuadianName() != null) {
					guardianName = guardianDetails.getGuadianName();
					map.put("name", guardianName);
				}

				if (guardianDetails.getUserName() != null) {
					// userName = guardianDetails.getUserName();
					map.put("username", guardianDetails.getUserName());
				}
				if (guardianDetails.getMobile_number() != null) {
					map.put("mobile_number", guardianDetails.getMobile_number());
				}
				if (guardianDetails.getStatus() == 'y') {
					map.put("status", "Active");
				} else if (guardianDetails.getStatus() == 'n') {
					map.put("status", "Inactive");
				}

				jsonString = JSONObject.toJSONString(map);
				myList.add(jsonString);
				log.debug("My List Contents::::::::" + myList);
			}

			jsonArrayString = jsonArray.toString();
			log.debug("jsonArrayString : " + "\n" + jsonArrayString);
			responseJSON = ErrorCodesUtil.displaySuccessJSONForGuardianDetails(Constant.GuardianNameType,
					Constant.SucessMsgActivity, Constant.StatusCodeActivity, myList.toString());
			return responseJSON;
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.GuardianNameType);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/schoolAnnouncements", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView getSchoolAnnouncements(@RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute(Constant.CurrentUser);
		ModelAndView model = new ModelAndView();

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		if ((null == this.httpSession.getAttribute("currentUser"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		if (!currentUser.equals(Constant.ParentAdmin)) {

			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		} else {
			model.addObject(Constant.SessionID, sessionID);
			model.setViewName("schoolAnnouncements");
			model.addObject(Constant.FirstName, user.getName());
			return model;
		}

	}

	@RequestMapping(value = "/adminResetPassword", method = RequestMethod.POST)
	public ModelAndView adminResetPassword(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel) {
		Users user = this.userService.getUserByUserName(usersModel.getUsername());
		if (null != user) {
			this.userService.sendEmail(user);
			model.addObject(Constant.PasswordActivation, Constant.PasswordActivationMessage);
		} else {
			model.addObject(Constant.MailNotSent, Constant.MailNotSentMessage);
		}

		model.setViewName("adminlogin");
		return model;
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public ModelAndView resetPassword(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel) {
		Users user = this.userService.getUserByUserName(usersModel.getUsername());
		if (null != user) {
			this.userService.sendEmail(user);
			model.addObject(Constant.PasswordActivation, Constant.PasswordActivationMessage);
		} else {
			model.addObject(Constant.MailNotSent, Constant.MailNotSentMessage);
		}

		model.setViewName("login");
		return model;
	}

	/**
	 * This is resetPassword Rest API Service Request should be {
	 * "email":"ap@xyz.com" } Email should be valid ones. and in RestClient the
	 * content-type should be taken as application/json
	 * 
	 * @param users
	 * @return
	 * 
	 */
	@RequestMapping(value = "/mobile/PasswordResetRequest", method = RequestMethod.POST, produces = {
			"application/json" })
	public String PasswordResetRequest(@RequestBody UsersModel usersModel) {
		String response = null;
		Users user = this.userService.getUserByUserName(usersModel.getUsername());
		if (user == null) {
			response = ErrorCodesUtil.displayErrorJSON(Constant.DisplayErrorMessage, Constant.DisplayStatusCode,
					Constant.PasswordResetRequestType);
			return response;
		}

		if (user.getRoleType().equals(Constant.SchoolStaff) || user.getRoleType().equals(Constant.ParentAdmin)
				|| user.getRoleType().equals(Constant.SchoolTeacher)
				|| user.getRoleType().equals(Constant.ParentMember)) {
			this.userService.sendEmail(user);
			response = ErrorCodesUtil.displaySuccessJSON(Constant.EMAIL_SENT_MSG, Constant.EMAIL_SENT_STATUS_CODE,
					Constant.EMAIL_SENT_TYPE);
			return response;

		} else {
			response = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsMessage, Constant.SubscriptionsStatusCode,
					Constant.PasswordResetRequestType);
			return response;
		}

	}

	@RequestMapping(value = "/UserActivation", method = RequestMethod.POST, produces = { "application/json" })
	public String UserActivation(@RequestBody UsersModel usersModel) {
		log.info("Into UserActivation() {");
		Users user = this.userService.getUserDetails(usersModel.getUsername());
		log.info("1");
		if (user == null) {
			String respondJson = ErrorCodesUtil.displayErrorJSON(Constant.DisplayErrorMessage,
					Constant.DisplayStatusCode, Constant.PasswordResetRequestType);
			// JSONUtility.respondAsJSON(response, respondJson);
		}

		String userActivationCode = user.getActivationCode();
		log.info("***userActivationCode In UserActivation()***" + "\t" + userActivationCode);
		if (userActivationCode == null) {
			String respondJson = ErrorCodesUtil.displayErrorJSON("Active User, Please change password by logging in",
					"ERR111", Constant.ActivationRequestType);
			// JSONUtility.respondAsJSON(response, respondJson);
			return respondJson;
		}

		boolean isUserExists = this.userService.isUserExists(user.getId());
		log.debug("isUserExists***" + "\t" + isUserExists);

		if (isUserExists
				&& (user.getRoleType().equals(Constant.SchoolStaff) || user.getRoleType().equals(Constant.SchoolTeacher)
						|| user.getRoleType().equals(Constant.ParentMember))) {
			try {
				String status = this.userService.sendUserActivationEmail(user.getName(), user.getActivationCode(),
						user.getUsername());
				if (status.equals("Success")) {
					String respondJson = ErrorCodesUtil.displayErrorJSON(Constant.EmailActivationMessage,
							Constant.EmailActivationCode, Constant.ActivationRequestType);
					// JSONUtility.respondAsJSON(response, respondJson);
					return respondJson;
				}
			} catch (Exception e) {
				log.info("Into catch sendEmail()");
				String respondJson = ErrorCodesUtil.displayErrorJSON(Constant.EmailActivationFailureMessage,
						Constant.EmailActivationFailureCode, Constant.ActivationRequestType);
				// JSONUtility.respondAsJSON(response, respondJson);
				return respondJson;
				// throw new MailNotSendException("Issue with SMTP connection,
				// Mail Cannot Be Sent");
			}
		} else {
			String respondJson = ErrorCodesUtil.displayErrorJSON(Constant.SubscriptionsMessage,
					Constant.SubscriptionsStatusCode, Constant.PasswordResetRequestType);
			// JSONUtility.respondAsJSON(response, respondJson);
			return respondJson;
		}
		log.info("Exiting UserActivation() }");
		return Constant.PasswordResetMessage;
	}

	@RequestMapping(value = "/UserActivationRequest", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView UserActivationRequest(@RequestParam("key") String key,
			@ModelAttribute("usersModel") UsersModel usersModel) {
		log.debug("***key***" + "\t" + key);
		ModelAndView model = new ModelAndView();
		Users user = this.userService.findUserByActivationCode(key);
		if (null == user) {
			model.addObject(Constant.LoginError, Constant.ActivationCodeExpiredMessage);
			model.setViewName("resetPassword");
			return model;
		}
		/*
		 * String passwordActivationCode = user.getActivationCode(); if (null ==
		 * passwordActivationCode) { model.addObject(Constant.LoginError,
		 * Constant.ActivationCodeExpiredMessage);
		 * model.setViewName("resetPassword"); return model; }
		 */
		model.addObject(Constant.ActivationLinkKey, key);
		model.setViewName(Constant.AdminPasswordResetLink);
		return model;
	}

	@RequestMapping(value = "/SignupActivationRequest", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView SignupActivationRequest(ModelAndView model, @RequestParam("key") String key,
			@ModelAttribute("usersModel") UsersModel usersModel) {
		log.debug("***key***" + "\t" + key);
		Users user = this.userService.findBySignUpActivationCode(key);
		if (user == null) {
			model.addObject(Constant.ErrorMsg, Constant.ErrorMsgActivation);
			model.setViewName("signUp");
			return model;
		} else {
			userService.updateSignupKey(user);
			model.addObject(Constant.SuccessMsg, Constant.UserAccountMessage);
			model.setViewName("login");
			return model;
		}

	}

	@RequestMapping(value = "/AdminPasswordResetLink", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView AdminPasswordResetLink(@RequestParam("key") String key,
			@ModelAttribute("usersModel") UsersModel usersModel) {
		log.debug("***key***" + "\t" + key);
		ModelAndView model = new ModelAndView();
		Users user = this.userService.findUserByActivationCode(key);
		String activationCodeValidity = CommonUtil.checkActivationCodeValidity(user);
		if (user == null
				|| (activationCodeValidity != null && activationCodeValidity.equals(Constant.sessionValidity))) {
			model.addObject(Constant.LoginError, Constant.ActivationCodeExpiredMessage);
			model.setViewName("resetPassword");
			return model;
		}
		model.addObject(Constant.ActivationLinkKey, key);
		model.setViewName(Constant.AdminPasswordResetLink);
		return model;
	}

	@RequestMapping(value = "/AdminPasswordReset")
	public ModelAndView AdminPasswordReset(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			BindingResult result, @RequestParam("key") String key) {
		log.debug("***key in PasswordReset***" + "\t" + key);
		log.debug("key" + "\t" + key);

		Users user = this.userService.findUserByActivationCode(key);
		try {
			/*
			 * Date date = null; log.debug("password::--" + "\t" +
			 * usersModel.getPassword());
			 * user.setPassword(AESEncryption.generatePasswordHash(usersModel.
			 * getPassword())); user.setActivationCode(null);
			 * user.setActivationCodeExpiry(date); user.setUserActive("y");
			 * Users user1 = this.userService.updateUser(user);
			 * log.debug("user1.getPassword()" + "\t" + user1.getPassword());
			 */
			this.userService.updateUserActivation(user.getId(),
					AESEncryption.generatePasswordHash(usersModel.getPassword()), null, null, "y");
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addObject(Constant.PasswordChangeSuccess, Constant.PasswordChangeSuccessMessage);
		model.setViewName("adminlogin");
		return model;
	}

	@RequestMapping(value = "/PasswordResetLink", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView PasswordResetLink(@RequestParam("key") String key,
			@ModelAttribute("usersModel") UsersModel usersModel) {
		log.debug("***key***" + "\t" + key);
		ModelAndView model = new ModelAndView();
		Users user = this.userService.findUserByActivationCode(key);
		String activationCodeValidity = CommonUtil.checkActivationCodeValidity(user);
		if (user == null
				|| (activationCodeValidity != null && activationCodeValidity.equals(Constant.sessionValidity))) {
			model.addObject(Constant.LoginError, Constant.ActivationCodeExpiredMessage);
			model.setViewName("resetPassword");
			return model;
		}
		model.addObject(Constant.ActivationLinkKey, key);
		model.setViewName(Constant.PasswordResetLink);
		return model;
	}

	@RequestMapping(value = "/PasswordReset")
	public ModelAndView PasswordReset(ModelAndView model, @ModelAttribute("usersModel") UsersModel usersModel,
			BindingResult result, @RequestParam("key") String key) {
		log.debug("***key in PasswordReset***" + "\t" + key);
		log.debug("key" + "\t" + key);

		Users user = this.userService.findUserByActivationCode(key);
		try {
			/*
			 * Date date = null; log.debug("password::--" + "\t" +
			 * usersModel.getPassword());
			 * user.setPassword(AESEncryption.generatePasswordHash(usersModel.
			 * getPassword())); user.setActivationCode(null);
			 * user.setActivationCodeExpiry(date); user.setUserActive("y");
			 * Users user1 = this.userService.updateUser(user);
			 * log.debug("user1.getPassword()" + "\t" + user1.getPassword());
			 */
			this.userService.updateUserActivation(user.getId(),
					AESEncryption.generatePasswordHash(usersModel.getPassword()), null, null, "y");
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addObject(Constant.PasswordChangeSuccess, Constant.PasswordChangeSuccessMessage);
		model.setViewName("login");
		return model;
	}

	@RequestMapping(value = "/userDetails")
	public ModelAndView listEmployee(ModelAndView model) throws IOException {
		model.setViewName("userDetails");
		return model;
	}

	@RequestMapping(value = "/manageKids")
	public ModelAndView manageKids(ModelAndView model, @RequestParam("token") String sessionID,
			@RequestParam("member_id") int member_id) throws IOException {

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		if ((null == this.httpSession.getAttribute(Constant.CurrentUser))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		if ((this.httpSession.getAttribute(Constant.CurrentUser) != null)
				&& !(this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.ParentAdmin))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		List<KidsListForParentMemberTransform> kidsList = userService.getKidsForParentMember(user.getId(), member_id,
				0);

		model.addObject("kidsList", kidsList);
		model.addObject(Constant.SessionID, sessionID);
		model.setViewName("manageKids");

		return model;
	}

	@RequestMapping(value = "/superAdminProfile", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editAdminProfile(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.getUserBySessionId(sessionID);
		user.setPassword("");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.Accounts, user.getAccounts());
		model.setViewName(Constant.SuperAdminProfile);
		model.addObject(Constant.User, user);

		return model;
	}

	@RequestMapping(value = "/schoolAdminProfile", produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editSchoolAdminProfile(HttpServletRequest request) {
		log.debug("schoolAdminProfile");
		String contentType = request.getContentType();
		log.info("contentType in MobileConfigFileUpload" + "\t" + contentType);
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		log.info("sessionID" + "\t" + sessionID);
		Users user = userService.getUserBySessionId(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute(Constant.CurrentUser);

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

		if (((this.httpSession.getAttribute(Constant.CurrentUser) != null))
				&& (this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.ParentAdmin))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (currentUser.equals(Constant.SchoolAdmin) || currentUser.equals(Constant.SchoolStaff)) {
			Integer schoolId = user.getAccounts().getAccountId();
			List<SchoolCalendar> schoolCalendarList = null;
			schoolCalendarList = this.SchoolService.getSchoolCalendarListBySchoolId(schoolId);
			log.info("schoolCalendarList " + schoolCalendarList);
			model.addObject(Constant.Accounts, user.getAccounts());
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.addObject("schoolCalendarList", schoolCalendarList);
			model.setViewName(Constant.SchoolAdminProfile);
		} else if (currentUser.equals(Constant.ParentAdmin)) {
			model.setViewName(Constant.ParentAdminProfile);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
		}

		model.addObject(Constant.User, user);

		if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
			MultipartHttpServletRequest multiHttpReq = (MultipartHttpServletRequest) request;
			MultipartFile file = null;
			if (multiHttpReq.getFileMap().entrySet() != null) {
				Set set = multiHttpReq.getFileMap().entrySet();
				java.util.Iterator i = set.iterator();

				while (i.hasNext()) {
					Map.Entry me = (Map.Entry) i.next();
					String fileName = (String) me.getKey();
					file = (MultipartFile) me.getValue();
					log.info("Original fileName - " + file.getOriginalFilename());
					log.info("fileName - " + fileName);
				}
			}

			if (null != file && !(file.isEmpty())) {

				log.debug("holidaysUploadFile");

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
					model.addObject("csvMsg", "Please Choose a Valid CSV File");
					model.addObject(Constant.SessionID, sessionID);
					model.addObject(Constant.FirstName, user.getName());
					model.setViewName(Constant.SchoolAdminProfile);
					return model;
				} else {
					Integer schoolId = user.getAccounts().getAccountId();
					Date date = new Date();
					String os = System.getProperty("os.name");
					if (os.contains("Windows"))
						sourceDateTime = sourceDateTime.replace(":", "-");
					SimpleDateFormat dateFormat = new SimpleDateFormat(sourceDateTime);
					log.info("folder Path student check" + "\t"
							+ this.configProperties.getProperty("students.upload.path"));
					log.info("folder Path" + "\t" + this.configProperties.getProperty("holidays.upload.path"));
					try {
						File f = new File(
								WebUtility.createFolder(this.configProperties.getProperty("holidays.upload.path") + "/"
										+ schoolId + '_' + dateFormat.format(date) + ".csv"));
						boolean flag;

						flag = f.createNewFile();
						log.info("fileCreated>>holidays" + "\t" + flag);
						log.info("f.getPath() Account Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
						file.transferTo(f);
						List<SchoolCalendar> list = HolidayCSVToJavaUtil.convertHolidaysCsvToJava(f, schoolId,
								dbDateFormat);

						if (list == null) {
							model.addObject(Constant.INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
							model.addObject("csvMsg", "");
							model.addObject("holidayUploadCountMsg", "");
							model.addObject("rowDetails", "");
							model.addObject(Constant.SessionID, sessionID);
							model.addObject(Constant.FirstName, user.getName());
							model.setViewName(Constant.SchoolAdminProfile);
							return model;
						}
						log.info("School Calender List Size() " + "\t" + list.size());
						log.info("fileCreated>>holidays List" + "\t" + list.toString());

						List<SchoolCalendar> listValid = new ArrayList<SchoolCalendar>();

						List<SchoolCalendar> listInvalid = new ArrayList<SchoolCalendar>();
						StringBuilder rowDetails = new StringBuilder();
						for (SchoolCalendar schoolCalendar : list) {

							if (schoolCalendar.isValidEntry()) {
								listValid.add(schoolCalendar);

							} else {
								rowDetails.append(schoolCalendar.getSuccessMessage());
								rowDetails.append("\n");
								listInvalid.add(schoolCalendar);
							}

						}
						log.info("listValid list size " + listValid.size());
						log.info("listInvalid list size " + listInvalid.size());
						if (this.SchoolService.updateSchoolCalendarList(schoolId, listValid)) {

							List<SchoolCalendar> schoolCalendarList = null;
							schoolCalendarList = this.SchoolService.getSchoolCalendarListBySchoolId(schoolId);
							log.info("schoolCalendarList " + schoolCalendarList);
							StringBuilder successmessage = new StringBuilder();
							for (SchoolCalendar schoolCalendar : list) {
								successmessage.append(schoolCalendar.getSuccessMessage()).append(", ");
							}
							log.info("message data" + successmessage);
							model.addObject(Constant.INVALID_CSV_ERROR, "");
							model.addObject(Constant.CSV_SUCCESS, Constant.CSV_SUCCESS_MSG);
							int total_record = listValid.size() + listInvalid.size();
							model.addObject("holidayUploadCountMsg", "Total Records(" + total_record + "), Created("
									+ listValid.size() + "), Ignored(" + listInvalid.size() + ")");
							model.addObject("rowDetails", rowDetails);
							model.addObject("schoolCalendarList", schoolCalendarList);
							model.setViewName(Constant.SchoolAdminProfile);
						} else {
							log.info("Into Else of Upload");
							model.addObject(Constant.INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
							model.addObject(Constant.CSV_SUCCESS, "");
							model.addObject("holidayUploadCountMsg", "");
							model.addObject("rowDetails", "");
							model.setViewName(Constant.SchoolAdminProfile);
						}
					} catch (Exception e) {
						e.printStackTrace();
						model.addObject(Constant.INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
						model.addObject(Constant.CSV_SUCCESS, "");
						model.addObject("holidayUploadCountMsg", "");
						model.addObject("rowDetails", "");
						model.setViewName(Constant.SchoolAdminProfile);
						return model;
					}
				}
			} else {
				model.addObject(Constant.INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
				model.addObject(Constant.CSV_SUCCESS, "");
				model.addObject("holidayUploadCountMsg", "");
				model.addObject("rowDetails", "");
				model.setViewName(Constant.SchoolAdminProfile);
				return model;
			}
		}

		return model;
	}

	@RequestMapping(value = "/schoolTeacherProfile", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editSchoolTeacherProfile(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.getUserBySessionId(sessionID);
		user.setPassword("");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute(Constant.CurrentUser);
		log.debug("schoolTeacherProfile : sessionValidityResult : " + sessionValidityResult);
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		log.debug("schoolTeacherProfile : currentUser ::: " + currentUser);
		if (((this.httpSession.getAttribute(Constant.CurrentUser) != null))
				&& ((this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.SchoolAdmin))
						|| (this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.ParentAdmin)))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		model.addObject(Constant.Accounts, user.getAccounts());
		model.addObject(Constant.SessionID, sessionID);
		log.debug("currentUser : " + currentUser);
		if (currentUser.equals(Constant.SchoolTeacher)) {
			model.setViewName(Constant.SchoolTeacherProfile);
		}

		model.addObject(Constant.User, user);

		return model;
	}

	@RequestMapping(value = "/parentAdminProfile", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editParentOrParentMemberProfile(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.getUserBySessionId(sessionID);
		if (null == user) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}
		user.setPassword("");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute(Constant.CurrentUser);
		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		log.debug("parentAdminProfile : sessionValidityResult : " + sessionValidityResult);
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		log.debug("parentAdminProfile : currentUser ::: " + currentUser);
		if (((this.httpSession.getAttribute(Constant.CurrentUser) != null)) && ((this.httpSession
				.getAttribute(Constant.CurrentUser).equals(Constant.SchoolAdmin))
				|| (this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.SuperAdmin)
						|| this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.SchoolTeacher)))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		log.info("mobile Number: " + user.getMobileNumber());
		model.addObject(Constant.Accounts, user.getAccounts());
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		log.debug("currentUser : " + currentUser);
		if (currentUser.equals(Constant.ParentMember) || currentUser.equals(Constant.ParentAdmin)) {
			model.setViewName(Constant.ParentAdminProfile);
		}

		model.addObject(Constant.User, user);

		return model;
	}

	@RequestMapping(value = "/newParentProfile", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editParentProfile(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.getUserBySessionId(sessionID);
		if (null == user) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}
		user.setPassword("");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute(Constant.CurrentUser);
		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		log.debug("parentAdminProfile : sessionValidityResult : " + sessionValidityResult);
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		log.debug("parentAdminProfile : currentUser ::: " + currentUser);
		if (((this.httpSession.getAttribute(Constant.CurrentUser) != null)) && ((this.httpSession
				.getAttribute(Constant.CurrentUser).equals(Constant.SchoolAdmin))
				|| (this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.SuperAdmin)
						|| this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.SchoolTeacher)))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		log.info("mobile Number: " + user.getMobileNumber());
		model.addObject(Constant.Accounts, user.getAccounts());
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("user_name", user.getUsername());
		log.debug("currentUser : " + currentUser);
		if (currentUser.equals(Constant.ParentMember) || currentUser.equals(Constant.ParentAdmin)) {
			model.setViewName(Constant.NewParentProfile);
		}

		model.addObject(Constant.User, user);

		return model;
	}

	@RequestMapping(value = "/saveMember/{id}", method = RequestMethod.POST)
	public ModelAndView saveMemberDetails(@PathVariable("id") int id, @RequestParam("token") String sessionID,
			HttpServletRequest req) {
		ModelAndView model = new ModelAndView();
		log.debug("Entry :" + req.getParameter("entry" + id));
		log.debug("sos :" + req.getParameter("sos" + id));
		log.debug("band :" + req.getParameter("band" + id));
		action = Constant.SaveMemberDetailsAction;
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
		String SosAlert = req.getParameter("sos" + id);
		String entryexitalert = req.getParameter("entry" + id);
		String bandRemovealert = req.getParameter("band" + id);

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		if ((null == this.httpSession.getAttribute(Constant.CurrentUser))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		if ((this.httpSession.getAttribute(Constant.CurrentUser) != null)
				&& !(this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.ParentAdmin))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
		userService.updateMemberInfo(id, SosAlert, entryexitalert, bandRemovealert);
		activityLogs.info(activityLog);

		return new ModelAndView(Constant.RedirectUrl + sessionID + "/member_id=" + id);

	}

	@Autowired
	UserValidator userValidator;

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveUser(@ModelAttribute("user") Users webuser, BindingResult result,
			@RequestParam("accountId") Integer accntId, @RequestParam("token") String sessionID) {

		ParentModelValidation parentModelValidation = new ParentModelValidation();
		log.info("password " + webuser.getPassword());
		log.info("confirm password " + webuser.getcPassword());
		action = Constant.SaveMemberDetailsAction;
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		boolean mobileValidation = true;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		Users user = userService.getUserBySessionId(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		ModelAndView model = new ModelAndView();

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		userValidator.validate(user, result);
		String roleType = user.getRoleType();
		log.debug("roleType" + "\t" + roleType);
		if (roleType.equals(Constant.SuperAdmin)) {

			if (result.hasErrors()) {
				userValidator.validateFields(user);
				return new ModelAndView(Constant.RedirectSuperUrl + sessionID);
			}
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			if (null != webuser.getPassword()) {
				validationResult = parentModelValidation.validateUserInput(webuser.getName(), webuser.getPassword(),
						webuser.getcPassword(), webuser.getMobileNumber());
				log.debug("validationResult" + validationResult);
			}

			if (parentModelValidation.checkErrorFlag()) {
				this.httpSession.setAttribute(Constant.FailureRes, Constant.TRUE);
				this.httpSession.setAttribute(Constant.Result, validationResult);
				return new ModelAndView(Constant.RedirectSuperUrl + sessionID);
			}
			userService.updateUsersWithAccountId(user, webuser, null);
			activityLogs.info(activityLog);

			this.httpSession.setAttribute(Constant.SuccessRes, Constant.TRUE);
			return new ModelAndView(Constant.RedirectSuperUrl + sessionID);
		}

		if (roleType.equals(Constant.SchoolAdmin) || roleType.equals(Constant.SchoolStaff)) {

			if (result.hasErrors()) {
				userValidator.validateFields(user);
				return new ModelAndView(Constant.RedirectSchoolAdmin + sessionID);
			}

			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);

			if (null != webuser.getPassword()) {
				validationResult = parentModelValidation.validateUserInput(webuser.getName(), webuser.getPassword(),
						webuser.getcPassword(), webuser.getMobileNumber());
				log.debug("validationResult" + validationResult);
			}
			if (parentModelValidation.checkErrorFlag()) {
				this.httpSession.setAttribute(Constant.FailureRes, Constant.TRUE);
				this.httpSession.setAttribute(Constant.Result, validationResult);
				return new ModelAndView(Constant.RedirectSchoolAdmin + sessionID);
			}

			userService.updateUsersWithAccountId(user, webuser, null);
			activityLogs.info(activityLog);
			this.httpSession.setAttribute(Constant.SuccessRes, Constant.TRUE);
			model.setViewName(Constant.SchoolAdminViewname);
			return new ModelAndView(Constant.RedirectSchoolAdmin + sessionID);

		}
		if (roleType.equals(Constant.SchoolTeacher)) {

			if (result.hasErrors()) {
				userValidator.validateFields(user);
				return new ModelAndView(Constant.RedirectSchoolAdmin + sessionID);
			}
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			if (null != webuser.getMobileNumber() && webuser.getMobileNumber().length() > 0)
				mobileValidation = parentModelValidation.validateMobileNumber(webuser.getMobileNumber());
			if (!mobileValidation) {

			}
			if (null != webuser.getPassword()) {
				validationResult = parentModelValidation.validateUserInput(webuser.getName(), webuser.getPassword(),
						webuser.getcPassword(), webuser.getMobileNumber());
				log.debug("validationResult" + validationResult);
			}
			if (parentModelValidation.checkErrorFlag()) {
				this.httpSession.setAttribute(Constant.FailureRes, Constant.TRUE);
				this.httpSession.setAttribute(Constant.Result, validationResult);
				return new ModelAndView(Constant.RedirectSchoolAdmin + sessionID);
			}
			userService.updateUsersWithAccountId(user, webuser, null);
			activityLogs.info(activityLog);
			this.httpSession.setAttribute(Constant.SuccessRes, Constant.TRUE);
			model.setViewName(Constant.SchoolTeacherView);
			return new ModelAndView(Constant.RedirectSchoolAdmin + sessionID);

		}

		if (roleType.equals(Constant.ParentAdmin) || roleType.equals(Constant.ParentMember)) {

			if (result.hasErrors()) {
				userValidator.validateFields(user);
				return new ModelAndView(Constant.RedirectParentAdmin + sessionID);
			}
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			log.debug("password from web:" + webuser.getPassword());
			log.debug("vealidation result before :" + parentModelValidation.checkErrorFlag());
			if (null != webuser.getPassword()) {
				log.debug("password length : " + webuser.getPassword().length());
				log.debug("mobile number length : " + webuser.getMobileNumber().length());
				validationResult = parentModelValidation.validateUserInput(webuser.getName(), webuser.getPassword(),
						webuser.getcPassword(), webuser.getMobileNumber());
				log.debug("validationResult" + validationResult);
			}
			log.debug("vealidation result aferr :" + parentModelValidation.checkErrorFlag());
			if (parentModelValidation.checkErrorFlag()) {
				this.httpSession.setAttribute(Constant.FailureRes, Constant.TRUE);
				this.httpSession.setAttribute(Constant.Result, validationResult);
				return new ModelAndView(Constant.RedirectParentAdmin + sessionID);
			}
			userService.updateUsersWithAccountId(user, webuser, null);
			activityLogs.info(activityLog);
			this.httpSession.setAttribute(Constant.SuccessRes, Constant.TRUE);
			return new ModelAndView(Constant.RedirectParentAdmin + sessionID);
		}

		return null;
	}

	@RequestMapping(value = "/web/user/getUserDetails/{session_id}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getUserUsingId(@PathVariable("session_id") String session_id) {
		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;

		String statusCode = null;
		String statusMsg = null;
		String type = "user.getUserDetails";
		String respondJson = null;

		Users user = userService.validateUserBySession(session_id);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals("NOTVALID")) {
			statusCode = "ERR02";
			statusMsg = "Session Expired ,Please Relogin ";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			// JSONUtility.respondAsJSON(response, respondJson);
			return respondJson;
		}

		if (user != null) {

			// if (!sessionValidityResult.equals("NOTVALID")) {

			String roleType = user.getRoleType();
			int SchoolId = user.getAccountId();
			log.info("***School Id***" + "\n" + SchoolId);

			if (roleType.equals(Constant.SchoolAdmin)) {

				map = new LinkedHashMap<>();

				if (user.getUsername() != null)
					map.put("username", user.getUsername());
				if (user.getName() != null)
					map.put("name", user.getName());
				if (user.getMobileNumber() != null)
					map.put("mobileNumber", user.getMobileNumber());

				jsonString = JSONObject.toJSONString(map);

				log.info("***myList***" + "\n" + myList);
				String userRespJson = ErrorCodesUtil.displayJSONForUserDetailsList("", "", jsonString);
				log.info("***getUserDetails***" + "\t" + userRespJson);
				return userRespJson;

			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, type);
				return respondJson;
			}

		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			return respondJson;
		}
	}

	@RequestMapping(value = "/mobile/UserUpdate/{session_id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public String UserUpdate(@PathVariable("session_id") String session_id, @RequestBody ParentModel parentModel) {
		String responseJSON = null;
		String oldUserName = null;
		action = Constant.SaveMemberDetailsAction;
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

		Users user = userService.getUserByMobileSessionId(session_id);
		log.info("user is " + user);
		if (null == user) {
			user = userService.getUserBySessionId(session_id);
			if (null != user) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
							Constant.StatusCodeValidity, Constant.UserUpdateType);
					// JSONUtility.respondAsJSON(response, responseJSON);
					return responseJSON;
				}
			}
		}

		if ((null != user) && (user.getRoleType().equals(Constant.ParentAdmin)
				|| user.getRoleType().equals(Constant.ParentMember) || user.getRoleType().equals(Constant.SchoolTeacher)
				|| user.getRoleType().equals(Constant.SchoolStaff) || user.getRoleType().equals(Constant.SchoolAdmin)
				|| user.getRoleType().equals(Constant.SystemAdmin) || user.getRoleType().equals(Constant.SupprotStaff)
				|| user.getRoleType().equals(Constant.SuperAdmin))) {
			log.info("logged in user " + user);
			if (null != parentModel.getName()) {
				user.setName(parentModel.getName());
			}
			if ((user.getRoleType().equals(Constant.SuperAdmin)) && null != parentModel.getUsername()) {
				if (!user.getUsername().equals(parentModel.getUsername())) {
					if (!userService.isUsersExist(parentModel.getUsername())) {
						oldUserName = user.getUsername();
						user.setUsername(parentModel.getUsername());
						user.setActivationCode(StringUtility.randomStringOfLength(40));
						user.setUserActive("n");
					} else {
						log.info("Username already exists" + parentModel.getUsername());
						responseJSON = Constant.ResponseJSON + Constant.IsUserExists + Constant.StatusMessage
								+ Constant.IsUserExistsMessage + Constant.Bracket;
						// JSONUtility.respondAsJSON(response, responseJSON);
						return responseJSON;
					}

				}
			}
			user.setAccounts(user.getAccounts());
			if (null != parentModel.getPassword()) {
				try {
					user.setPassword(AESEncryption.generatePasswordHash(parentModel.getPassword()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (null != parentModel.getMobile_number()) {

				user.setMobileNumber(parentModel.getMobile_number());

			}
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			userService.updateUsersWithAccountId(user, null, oldUserName);
			activityLogs.info(activityLog);
			responseJSON = Constant.ResponseJSON + Constant.ActivityStatusCode + Constant.StatusMessage
					+ Constant.ActivitySucessmessage + Constant.Bracket;
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		} else {
			log.info("logged invalid user " + user);
			responseJSON = Constant.ResponseJSON + Constant.StatusCodeJon + Constant.StatusMessage
					+ Constant.ErrorMessageJon + Constant.Bracket;
			// JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

	}

	@RequestMapping(value = "/useractivation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> createAcount(@RequestBody AccountRegisters accountRegisters) {
		log.debug("CreateAcount method started.");
		boolean flag = true;
		String errorMessage = null;
		String statusCode = null;

		Devices devices = new Devices();
		devices.setUuid(accountRegisters.getUuid());

		Users users = new Users();

		users.setUsername(accountRegisters.getUsername());
		users.setPassword(accountRegisters.getPassword());
		users.setRoleType(accountRegisters.getRoleType());

		if (users.getUsername() != null && users.getUsername().trim().equals("")) {
			flag = false;
			statusCode = Constant.ErrorCode;
			errorMessage = Constant.UserFieldEmpty;
		} else if (users.getUsername().length() > 25) {
			flag = false;
			statusCode = Constant.ErrorCode;
			errorMessage = Constant.UserNameNotMore;
		}
		if (users.getPassword() != null && users.getPassword().trim().equals("")) {
			flag = false;
			statusCode = Constant.ErrorCode;
			errorMessage = Constant.PasswordCannotEmpty;

		} else if (users.getPassword().length() > 45) {
			flag = false;
			statusCode = Constant.ErrorCode;
			errorMessage = Constant.PasswordnotMore;
		}
		if (users.getRoleType() != null && users.getRoleType().trim().equals("")) {
			flag = false;
			statusCode = Constant.ErrorCode;
			errorMessage = Constant.RoleTypeNotEmpty;
		}
		if (users.getUsername() != null && users.getUsername().trim().equals("")) {
			flag = false;
			statusCode = Constant.ErrorCode;
			errorMessage = Constant.MailFieldNotEmpty;
		} else if (users.getUsername().length() > 255) {
			flag = false;
			statusCode = Constant.ErrorCode;
			errorMessage = Constant.MailNotMore;
		}

		if (flag) {
			errorMessage = userService.accountRegister(devices, users);
		}

		if (Constant.UserRegistered.equalsIgnoreCase(errorMessage)) {
			statusCode = Constant.SatatusCode200;

		} else if (Constant.UserAlreadyexist.equalsIgnoreCase(errorMessage)) {
			statusCode = Constant.SatatusCode400;
		} else {
			statusCode = Constant.ErrorCode;
		}
		String responseJSON = Constant.ReturnJSON + statusCode + "\",\"Message\": \"" + errorMessage + Constant.Bracket;

		// JSONUtility.respondAsJSON(response, responseJSON);
		return null;
	}

	@RequestMapping(value = "/mobile/ParentUserRegistration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String ParentUserRegistration(@RequestBody ParentAppRegisterModel parentAppRegisterModel) {

		log.debug("Into ParentUserRegistration() {");
		String statusCode = null;
		String msg = null;
		String responseJSON = null;
		action = Constant.SubscriptionsUpdateAction;
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

		/**
		 * We don't do any validation such as username(Email), password or not
		 * null etc at server level ie in Controllers. It is assumed that the
		 * data we get is a valid data with all the validations done at front
		 * end.(JSP level) So removed all the validation checks.
		 */
		Users user = this.userService.isFederatedUserExist(parentAppRegisterModel.getUsername());
		// log.info("Username from isFederatedUserExist call:" + user);
		if (null != user && null != user.getUsername()
				&& user.getUsername().equals(parentAppRegisterModel.getUsername())) {
			log.info("No OpenID UserExists, registered UserExists");
			responseJSON = ErrorCodesUtil.displayJSONForUserRegistrationFailure(Constant.IsUserExists,
					Constant.IsUserExistsMessage);
			return responseJSON;
		} else if (null != user && null != user.getOpenidUsername()
				&& user.getOpenidUsername().equals(parentAppRegisterModel.getUsername())) {
			log.info("OpenID UserExists");
			this.userService.updateOpenId(null, parentAppRegisterModel.getProfile_name(),
					parentAppRegisterModel.getUsername(), "WEB");
			statusCode = "0";
			responseJSON = ErrorCodesUtil.displayJSONForUserRegistration(statusCode, " API Request Success");
			return responseJSON;
		} else {
			log.info("Neither OpenID UserExists nor registered UserExists");
			/*
			 * }
			 * 
			 * boolean isUserExists =
			 * userService.isUsersExist(parentAppRegisterModel.getUsername());
			 * boolean openIdExist =
			 * this.userService.openIdExist(parentAppRegisterModel.getUsername()
			 * ); log.info("openIdExist" + "\t" + openIdExist);
			 * 
			 * if (isUserExists) {
			 * 
			 * } else if (openIdExist) {
			 * 
			 * } else {
			 */

			if (parentAppRegisterModel.getProfile_name() != null && parentAppRegisterModel.getUsername() != null
					&& parentAppRegisterModel.getPassword() != null) {

				Users users = new Users();
				Accounts account = accountService.createAccounts();

				log.info("account object" + account);
				if (account != null)
					log.debug("Account ID :" + account.getAccountId());
				users.setAccounts(account);
				users.setUsername(parentAppRegisterModel.getUsername());
				try {
					users.setPassword(AESEncryption.generatePasswordHash(parentAppRegisterModel.getPassword()));
				} catch (Exception e) {
					e.printStackTrace();
				}

				users.setName(parentAppRegisterModel.getProfile_name());
				users.setRoleType("parent_admin");
				if (null != parentAppRegisterModel.getMobile_number())
					users.setMobileNumber(parentAppRegisterModel.getMobile_number());
				users.setCreatedDate(new Date());

				msg = this.userService.userRegister(users);

				if (msg.equals(Constant.ValidErrorMessage)) {
					processMailUtility.signupSentMail(users.getName(), users.getSignupActivation(),
							users.getUsername());
					ActivityLog activityLog = CommonUtil.formulateActivityLogs(users, action, summary, ipaddress);
					activityLogs.info(activityLog);

					statusCode = "0";
					responseJSON = ErrorCodesUtil.displayJSONForUserRegistration(statusCode, msg);
					return responseJSON;
				} else {
					statusCode = "ERR32";
					responseJSON = ErrorCodesUtil.displayJSONForUserRegistrationInvalid(statusCode,
							Constant.UserRegistrationErr);
					return responseJSON;
				}
			} else {
				statusCode = "ERR05";
				responseJSON = ErrorCodesUtil.displayJSONForUserRegistrationInvalid(statusCode,
						Constant.IsValidJsonErrorMessage);
				return responseJSON;
			}
		}
	}

	@RequestMapping(value = "/mobile/ParentUserDevicePair/{session_id}/{uuid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String ParentUserDevicePair(@PathVariable("session_id") String session_id,
			@PathVariable("uuid") String uuid) {

		String errorMessage = null;
		String responseJSON = null;
		action = Constant.SaveMemberDetailsAction;
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
		Users users = userService.validateUserBySession(session_id);

		if (users != null) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(users);

			if (sessionValidityResult.equals("NOTVALID")) {
				responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.SubscriptionsErrorStatusCode,
						Constant.ErrorTokenMessage, Constant.FailureMessage);
				return responseJSON;
			}
			Devices device = this.deviceService.checkDeviceIdExist(uuid);
			if (device != null) {
				log.debug("device is not null ::::: status is >>> " + device.getStatus());
				if (device.getStatus().equals("broken")) {
					log.debug("if :::: device is broken");
					responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.NewDeviceErrorCode,
							Constant.NewDeviceErrorCodeMessage, Constant.FailureMessage);
					return responseJSON;
				} else if (device.getStatus().equals("returned")) {
					log.debug("else if :::: device is retuned");
					responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.NewDeviceErrorCode,
							Constant.NewDeviceErrorCodeMessage, Constant.FailureMessage);
					return responseJSON;
				} else if (device.getStatus().equals("")) {
					log.debug("else if :::: device is not assigned");
					responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.NewDeviceErrorCode,
							Constant.NewDeviceErrorCodeMessage, Constant.FailureMessage);
					return responseJSON;
				}

			} else {
				log.debug(
						"Else ::::: this.userService.linkNewDevicePairing(uuid) is null >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.NewDeviceErrorCode,
						Constant.NewDeviceErrorCodeMessage, Constant.FailureMessage);
				return responseJSON;
			}

			Students students = this.userService.linkNewDevicePairing(uuid);
			if (null == students) {
				responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.NewDeviceErrorCode,
						Constant.NewDeviceErrorCodeMessage, Constant.FailureMessage);
				return responseJSON;
			}
			boolean deviceStatus = this.deviceService.findDeviceActive(students.getStudentId(), uuid);
			log.info("deviceStatus in UserController" + "\t" + deviceStatus);
			boolean deviceAlreadyPair = this.deviceService.checkDeviceAlreadyPairedWithUser(uuid, users.getId());
			log.info("deviceAlreadyPair" + "\t" + deviceAlreadyPair);

			if (deviceStatus) {
				if (deviceAlreadyPair) {
					log.debug("if  :: device already paired :::: ");
					errorMessage = "Device Already paired for the user " + users.getRoleType();
					responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.DeviceErrorCode,
							Constant.DeviceErrorCodeMessage, Constant.FailureMessage);
					return responseJSON;

				} else {
					log.info("Good to go");
				}
			} else {
				responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.NewDeviceErrorCode,
						Constant.NewDeviceErrorCodeMessage, Constant.FailureMessage);
				return responseJSON;
			}
			int pairedCount = 0;

			if (users.getRoleType().equals(Constant.ParentAdmin)) {
				pairedCount = this.userService.checkTotalPairedSetForParentAdmin(students.getStudentId());
				log.debug("pairedCount ::::: " + pairedCount);
			}

			if (pairedCount < max_parents) {
				log.debug("if >>> this.userService.linkNewDevicePairing(uuid) >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				log.debug("inside students != null ");
				return getParentUserDevicePairResponseJson(students, uuid);

			} else {
				log.debug("22222  else :::::::");
				errorMessage = Constant.NoDeviceErrorCodeMessage + users.getRoleType();
				responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.NoDeviceErrorCode, errorMessage,
						Constant.FailureMessage);
				return responseJSON;
			}
		} else {
			log.debug("333333  else :::::::");
			responseJSON = ErrorCodesUtil.displayJSONForNewDevicePairing(Constant.DisplayStatusCode,
					Constant.ErrorMessageJon, Constant.FailureMessage);
			return responseJSON;
		}
		// return null;
	}

	public String getParentUserDevicePairResponseJson(Students students, String uuid) {

		String responseJSON = null;
		String json = null;

		Map<Object, Object> map = null;
		map = new LinkedHashMap<>();
		map.put("uuid", uuid);
		map.put("student_id", students.getStudentId());
		if (students.getName() != null) {
			map.put("student_name", students.getName());
		}

		if (students.getRollNo() != null) {
			map.put("roll_no", students.getRollNo());
		}

		if (students.getRegistartion_no() != null) {
			map.put("registration_no", students.getRegistartion_no());
		}

		if (students.getNickName() != null) {
			map.put("nickname", students.getNickName());
		}

		SimpleDateFormat dt = new SimpleDateFormat(sourceDateTime);
		SimpleDateFormat dt1 = new SimpleDateFormat(dbDateFormat);
		Date date = null;
		try {
			date = dt.parse(students.getDob().toString());
			if (dt1.format(date).toString() != null) {
				map.put("dob", dt1.format(date).toString());
			}
		} catch (ParseException e) {
			log.debug("DOB >>>>> parse Exception ::::: ");
		} catch (Exception e) {
			log.debug("DOB >>>> Exception arised");
		}
		if (students.getGender() != null) {
			map.put("gender", students.getGender());
		}

		if (students.getWeight() != null) {
			map.put("weight", students.getWeight());
		}

		if (students.getHeight() != null) {
			map.put("height", students.getHeight());
		}

		if (students.getEmergencyContactNo() != null) {
			map.put("emergency_contact", students.getEmergencyContactNo());
		}

		if (students.getAllergies() != null) {
			map.put("allergies", students.getAllergies());
		}

		json = JSONObject.toJSONString(map);
		responseJSON = ErrorCodesUtil.displayStudentDetails(Constant.ValidErrorMessage, Constant.ActivityStatusCode,
				json);
		return responseJSON;
	}

	@RequestMapping(value = "/mobile/ParentUserDeviceUnPair/{session_id}/{uuid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String ParentUserDeviceUnPair(@PathVariable("session_id") String session_id,
			@PathVariable("uuid") String uuid) {

		NewDevicePairingModel unpairModel = new NewDevicePairingModel();
		String errorMessage = null;
		String statusCode = null;
		String msg = null;
		String responseJSON = null;
		action = Constant.SaveMemberDetailsAction;
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

		Users users = userService.validateUserBySession(session_id);
		if (null != users) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(users);

			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				errorMessage = "Token provided is expired, need to re-login";
				msg = "FAILURE";
				responseJSON = ErrorCodesUtil.displayJSONForUnPairingDevice(statusCode, errorMessage, msg);
				return responseJSON;
			}
			Devices device = this.deviceService.checkDeviceIdExist(uuid);
			boolean unpairStatus = false;
			if (device == null) {
				responseJSON = ErrorCodesUtil.displayJSONForUnPairingDevice(Constant.NewDeviceErrorCode,
						Constant.NewDeviceErrorCodeMessage, Constant.FailureMessage);
				return responseJSON;

			} else {
				unpairStatus = this.userService.unpairParentDevices(uuid, users.getId());
				log.info("unpairStatus" + "\t" + unpairStatus);
				if (unpairStatus) {
					responseJSON = ErrorCodesUtil.displaySuccessOrFailureJSONForDeviceUnpair(Constant.DeviceUnpairType,
							Constant.SucessMsgActivity, Constant.StatusCodeActivity);
				} else {
					responseJSON = ErrorCodesUtil.displayErrorJSON("Device is not Paired", "ERR16",
							Constant.DeviceUnpairType);
				}
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.FailureMessage, Constant.StatusCodeJon,
					Constant.DeviceUnpairType);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/useractivation", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView userAccountActivation(HttpServletRequest request) {
		log.debug("User Validation Method.");

		String username = request.getParameter(Constant.UserName);
		String activationCode = request.getParameter(Constant.ActivationCode);

		String msg = userService.userValidation(username, activationCode);

		ModelAndView model = new ModelAndView();
		model.addObject(Constant.UserMessage, username + " " + msg);
		model.setViewName(Constant.RegisterSuccess);

		return model;
	}

	@RequestMapping(value = "/admindashboard", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView adminDashBoard(ModelAndView model, @RequestParam("token") String sessionID) {

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
				&& !(user.getRoleType().equals(Constant.SuperAdmin) || user.getRoleType().equals(Constant.SystemAdmin)
						|| user.getRoleType().equals(Constant.SupprotStaff))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		String roleType = user.getRoleType();
		getDisplayView(model, sessionID, roleType, user.getName());
		return model;
	}

	private ModelAndView getDisplayView(ModelAndView model, String sessionID, String roleType, String profileName) {
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, profileName);
		if (roleType.equals(Constant.SuperAdmin)) {
			model.setViewName(Constant.SuperAdminViewname);
		} else if (roleType.equals(Constant.SupprotStaff)) {
			model.setViewName(Constant.SupportStaffDashboardView);
		} else if (roleType.equals(Constant.SystemAdmin)) {
			model.setViewName(Constant.SystemAdminDashboardView);
		} else if (roleType.equals(Constant.SchoolAdmin)) {
			model.setViewName(Constant.SchoolAdminViewname);
		}

		return model;
	}

	@Autowired
	private ParentService parentService;

	@RequestMapping(value = "/parentdashboard", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView parentDashBoard(ModelAndView model, @RequestParam("token") String sessionID) {

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		if (null == user.getOpenidUsername() && (this.httpSession.getAttribute(Constant.CurrentUser) == null)) {

			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		if ((null == user.getOpenidUsername() && ((this.httpSession.getAttribute(Constant.CurrentUser) != null)))
				&& !((this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.ParentAdmin))
						|| (this.httpSession.getAttribute(Constant.CurrentUser).equals(Constant.ParentMember)))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		log.info("user.getOpenidUsername()" + "\t" + user.getOpenidUsername());
		if (user.getOpenidUsername() != null) {
			model.addObject("user_name", user.getOpenidUsername());
			this.httpSession.setAttribute(Constant.User_Name, user.getOpenidUsername());
		}

		UsersModel userModel = (UsersModel) this.httpSession.getAttribute(Constant.UserModel);
		if (user.getRoleType().equals(Constant.ParentAdmin)) {
			List<TeachersStudentsTransform> teacherStudentsList = this.parentService.viewKidsList(user.getId(), null);
			model.addObject(Constant.TeacherStudentList, teacherStudentsList);
			if (teacherStudentsList.isEmpty()) {
				getUserDetails(model, user, Constant.NewParentProfile);
			} else {
				getUserDetails(model, user, Constant.ParentAdminView);
			}
		}
		if (user.getRoleType().equals(Constant.ParentMember)) {
			model.setViewName(Constant.ParentMemberView);
			getUserDetails(model, user, Constant.ParentMemberView);
		}
		model.addObject(Constant.SessionID, sessionID);
		model.addObject("firstName", user.getName());
		return model;
	}

	@RequestMapping(value = "/schooldashboard", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView backtoDashboard(ModelAndView model, @RequestParam("token") String sessionID) {
		Users user = userService.getUserBySessionId(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		model.addObject(Constant.SessionID, sessionID);
		// UsersModel userModel = (UsersModel)
		// this.httpSession.getAttribute(Constant.UserModel);
		try {
			// Users userDetails =
			// this.userService.getUserDetails(userModel.getUsername());
			model.addObject(Constant.UserDetails, user);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName(Constant.SchoolAdminViewname);
		} catch (Exception e) {
			log.error("Error in backtoDashboard:" + e);
			model.setViewName("adminlogin");
		}
		return model;
	}

	@RequestMapping(value = "/mobile/UserDetails/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String UserDetails(@PathVariable String sessionID) {

		String respondJson = null;
		Map<Object, Object> map = null;
		String json = null;

		String statusCode = null;
		String statusMsg = null;

		Users user = userService.validateUserBySession(sessionID);

		log.info("user is " + user);
		if (user != null) {
			String mobileSession = user.getMobileSessionId();
			if (null == mobileSession) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					respondJson = ErrorCodesUtil.displayErrorJSON(Constant.DisplayErrorMessage,
							Constant.DisplayStatusCode, Constant.UserDetailsType);
					// JSONUtility.respondAsJSON(response, respondJson);
					return respondJson;
				}
			}
			String roleType = user.getRoleType();
			if (roleType.equals(Constant.ParentAdmin) || roleType.equals(Constant.ParentMember)
					|| roleType.equals(Constant.SchoolTeacher) || roleType.equals(Constant.SchoolStaff)
					|| roleType.equals(Constant.SupprotStaff) || roleType.equals(Constant.SystemAdmin)
					|| roleType.equals(Constant.SuperAdmin)) {
				map = new LinkedHashMap<>();
				map.put("name", user.getName());
				map.put("mobile_number", user.getMobileNumber());
				String username = (null != user.getUsername()) ? user.getUsername() : user.getOpenidUsername();
				map.put("username", username);

				if (user.getRoleType().equals(Constant.SchoolTeacher)
						|| user.getRoleType().equals(Constant.SchoolStaff)) {
					Users userDetails = this.userService.getUserDetails(user.getUsername());
					String accountName = userDetails.getAccounts().getAccountName();
					map.put(Constant.AccountName, accountName);
				}
				json = JSONObject.toJSONString(map);

				respondJson = ErrorCodesUtil.displayUserDetails(Constant.SucessMsgActivity, Constant.StatusCodeActivity,
						json);
				log.debug("***userDetailsJson***" + "\t" + respondJson);
			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.UserDetailsType);
			}
		} else {
			respondJson = ErrorCodesUtil.displayErrorJSON(Constant.DisplayErrorMessage, Constant.DisplayStatusCode,
					Constant.UserDetailsType);
		}

		return respondJson;
	}

	@RequestMapping(value = "/mobile/ContactDetails/{sessionID}/{uuid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getContactList(@PathVariable String sessionID, 
			@PathVariable("uuid") String uuid) {

		Map<Object, Object> map = null;
		String responseJSON = null;

		String statusCode = null;
		String statusMsg = null;
		Map<String, Object> contactsMap = null;
		String type = "ContactDetails";
		
		Users user = userService.validateUserBySession(sessionID);

		log.info("user is " + user);
		if (user != null) {
			String mobileSession = user.getMobileSessionId();
			if (null == mobileSession) {
				String sessionValidityResult = CommonUtil.checkSessionValidity(user);

				if (sessionValidityResult.equals("NOTVALID")) {
					statusCode = "ERR02";
					statusMsg = "Session Expired ,Please Relogin";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					return responseJSON;
				}
			}
			String roleType = user.getRoleType();

			if (null != roleType) {
				contactsMap = this.userService.getContactListForParentAdmin(uuid, roleType);
				
				responseJSON = ErrorCodesUtil.displaySchoolAdminData("SUC01", "API Request Success", type,
						JSONObject.toJSONString(contactsMap));
			}
			else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			}
		}
		else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/FederatedLogin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String FederatedLogin(@RequestBody String FederatedLoginResponse, HttpServletRequest request) {
		log.info("Into FederatedLogin() {");
		String responseJSON = null;
		boolean login = false;

		String profileName = "";
		String email = "";
		String token = "";
		String userAgent = "";

		log.info("FederatedLogin Response" + "\t" + FederatedLoginResponse);
		org.json.JSONObject jsonObj = new org.json.JSONObject(FederatedLoginResponse);

		try {
			profileName = jsonObj.getString("name");
			email = jsonObj.getString("email");
			token = jsonObj.getString("token");
			userAgent = jsonObj.getString("useragent");

			if (profileName == null || email == null || token == null) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.GoogleIsEmpty, Constant.StatusCode4,
						Constant.FederatedType);
				return responseJSON;
			}

			Users user = this.userService.isFederatedUserExist(email);
			if (null == user) {
				log.info("Neither OpenID UserExists nor registered UserExists");
				login = this.userService.insertFederatedLogin(token, profileName, email, userAgent);
			} else {
				if (null != user.getRoleType() && user.getRoleType().equals(Constant.ParentAdmin)) {
					log.info("Federated Login attempted by a Parent Admin User");
					if (null != user.getOpenidUsername() && user.getOpenidUsername().equals(email)) {
						log.info("OpenID UserExists");
						login = this.userService.updateOpenId(token, profileName, email, userAgent);
					} else if (null != user.getUsername() && user.getUsername().equals(email)) {
						log.info("No OpenID UserExists, registered UserExists");
						login = this.userService.updateFederatedLogin(token, profileName, email, userAgent);
					}
				} else {
					log.error("Federated Login attempted by a Non Parent Admin User");
					responseJSON = ErrorCodesUtil.displayFederatedLoginJSON(Constant.UnauthorisedUserErrorCodeDescription,
							Constant.UnauthorisedUserErrorCode, Constant.FederatedType, token);
					return responseJSON;
				}
			}

			if (login) {
				log.info("login status : " + "\t" + login);
				UsersModel userModel = new UsersModel();
				userModel.setOpenid_username(email);
				this.httpSession.setAttribute(Constant.UserModel, userModel);
				this.httpSession.setAttribute("currentUser", "parent_admin");
				responseJSON = ErrorCodesUtil.displayFederatedLoginJSON(Constant.SucessMsgActivity,
						Constant.StatusCodeActivity, Constant.FederatedType, token);
				return responseJSON;
			} else {
				responseJSON = ErrorCodesUtil.displayFederatedLoginJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.FederatedType, token);
				return responseJSON;
			}
		} catch (JSONException jsone) {
			log.error("Exception in FederatedLogin, not all required data is received: " + jsone.getMessage());
			responseJSON = ErrorCodesUtil.displayFederatedLoginJSON(Constant.UnauthorisedUserErrorCodeDescription,
					Constant.UnauthorisedUserErrorCode, Constant.FederatedType, token);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/createGuradian/{sessionId}", method = RequestMethod.POST)
	public String createGuradian(@PathVariable("sessionId") String sessionId,
			@RequestBody GuardianModel guardianModel) {
		log.info("<<<<<<<<<<<Inside saveGrantGuardianAccessToKids method>>>>>>>>>>");
		log.info("guardianModel.getGuardianUserName()" + "\t" + guardianModel.getGuardianUserName());
		log.info("guardianModel.getKidsname()" + "\t" + guardianModel.getKidsname());

		String responseJSON = null;
		Users userBySessionId = userService.getUserBySessionId(sessionId);

		if (null != userBySessionId) {

			if (userBySessionId.getRoleType().equals(Constant.ParentAdmin)) {
				Users guardianUser = userService.findUsers(guardianModel.getGuardianUserName());
				if (null == guardianUser) {
					this.parentService.createGuradian(userBySessionId, guardianModel);
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ActivitySucessmessage,
							Constant.ActivityStatusCode, Constant.PARENT_KIDS_CREATE_API_TYPE);
					return responseJSON;
				}
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.IsUserExistsMessage, Constant.IsUserExists,
						Constant.PARENT_KIDS_CREATE_API_TYPE);
				return responseJSON;
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.PARENT_KIDS_CREATE_API_TYPE);
				return responseJSON;
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.PARENT_KIDS_CREATE_API_TYPE);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/editGuardian/{sessionId}/{id}", method = RequestMethod.PUT)
	public String editGuardian(@PathVariable("sessionId") String sessionId, @PathVariable("id") String id,
			@RequestBody GuardianModel guardianModel) {
		log.info("Inside guardian edit function>>>>>>>>>>>> guardian Id: " + "\t" + id);
		String responseJSON = null;
		Users userBySessionId = userService.validateUserBySession(sessionId);
		if (null != userBySessionId) {
			if (userBySessionId.getRoleType().equals(Constant.ParentAdmin)) {
				Users checkUser = userService.findUsers(guardianModel.getGuardianUserName().toString());
				if (null != checkUser && checkUser.getId() == Integer.parseInt(id)) {
					Users guardianUser = userService.findUserById(Integer.parseInt(id));
					if (null != guardianUser) {
						this.parentService.editGuradian(guardianUser, guardianModel);
						responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ActivitySucessmessage,
								Constant.ActivityStatusCode, Constant.GUARDIAN_EDIT_API_TYPE);
						return responseJSON;
					} else {
						responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorCodeMessage, Constant.ErrorCode,
								Constant.GUARDIAN_EDIT_API_TYPE);
						return responseJSON;
					}
				}
				if (null != checkUser && checkUser.getId() != Integer.parseInt(id)) {
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.IsUserExistsMessage, Constant.IsUserExists,
							Constant.GUARDIAN_EDIT_API_TYPE);
					return responseJSON;
				} else {
					Users guardianUser = userService.findUserById(Integer.parseInt(id));
					if (null != guardianUser) {
						this.parentService.editGuradian(guardianUser, guardianModel);
						responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ActivitySucessmessage,
								Constant.ActivityStatusCode, Constant.GUARDIAN_EDIT_API_TYPE);
						return responseJSON;
					} else {
						responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorCodeMessage, Constant.ErrorCode,
								Constant.GUARDIAN_EDIT_API_TYPE);
						return responseJSON;
					}
				}
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.GUARDIAN_EDIT_API_TYPE);
				return responseJSON;
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.GUARDIAN_EDIT_API_TYPE);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/deleteGuardian/{sessionId}/{id}", method = RequestMethod.DELETE)
	public String deleteGuardian(@PathVariable("sessionId") String sessionId, @PathVariable("id") String id) {
		log.info("Inside Guardian Delete method:>>>>>>>>>>>>>>>" + "\t" + id);
		String responseJSON = null;
		Users userBySessionId = userService.validateUserBySession(sessionId);
		if (null != userBySessionId) {
			if (userBySessionId.getRoleType().equals(Constant.ParentAdmin)) {
				Users guardianUser = userService.findUserById(Integer.parseInt(id));
				if (null != guardianUser) {
					this.parentService.deleteGuardian(guardianUser);
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ActivitySucessmessage,
							Constant.ActivityStatusCode, Constant.GUARDIAN_DELETE_API_TYPE);
					return responseJSON;
				} else {
					responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorCodeMessage, Constant.ErrorCode,
							Constant.GUARDIAN_DELETE_API_TYPE);
					return responseJSON;
				}
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.GUARDIAN_DELETE_API_TYPE);
				return responseJSON;
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.GUARDIAN_DELETE_API_TYPE);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/activityLog", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView activityLog(ModelAndView model, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !currentUser.equals(Constant.SystemAdmin)) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName(Constant.ActivityLog);

		return model;
	}

	@RequestMapping(value = "/notificationLog", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView notificationLog(ModelAndView model, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !currentUser.equals(Constant.SystemAdmin)) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName(Constant.NotificationLog);
		return model;
	}

	@RequestMapping(value = "/backupLog", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView backupLog(ModelAndView model, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !currentUser.equals(Constant.SystemAdmin)) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName(Constant.BackupLog);
		return model;
	}

	@RequestMapping(value = "/externalSystemStatus", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView externalSystemStatus(ModelAndView model, @RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");

		if (sessionValidityResult.equals(Constant.sessionValidity)) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		if (null != currentUser && !currentUser.equals(Constant.SystemAdmin)) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName(Constant.ExternalSystemStatus);
		return model;
	}
}
