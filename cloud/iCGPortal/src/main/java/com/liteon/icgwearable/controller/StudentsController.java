package com.liteon.icgwearable.controller;

import java.io.File;
import com.liteon.icgwearable.util.Constant;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.EventSubscriptions;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.SupportedEvents;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.StudentCSVModel;
import com.liteon.icgwearable.model.StudentsModel;
import com.liteon.icgwearable.model.StudentsWebModel;
import com.liteon.icgwearable.modelentity.StudentModelEntity;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.ClassGradeService;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.service.EventsService;
import com.liteon.icgwearable.service.ParentService;
import com.liteon.icgwearable.service.StudentsService;
import com.liteon.icgwearable.service.TeacherService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.ClassGradeTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.StudentsMapLocationTransform;
import com.liteon.icgwearable.transform.StudentsTransform;
import com.liteon.icgwearable.transform.TeachersStudentsTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.StudentCSVToJavaUtil;
import com.liteon.icgwearable.util.WebUtility;
import com.liteon.icgwearable.validator.KidsValidator;
import com.liteon.icgwearable.validator.StudentValidator;

@RestController
public class StudentsController {

	private static Logger log = Logger.getLogger(StudentsController.class);
	@Autowired
	private StudentsService studentsService;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	private StudentValidator validator;
	@Autowired
	private KidsValidator kidsValidator;
	@Autowired
	private StudentModelEntity studentModelEntity;
	@Autowired
	private TeacherService teacherService;
	@Autowired
	private ParentService parentService;
	@Autowired
	private UserService userService;
	@Autowired
	HttpServletRequest request;
	@Autowired
	HttpServletResponse response;
	@Autowired
	private DeviceService deviceService;
	@Autowired
	private EventsService eventService;
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
	@Autowired
	private ClassGradeService classGradeService;
	@Value("${display.dateTime}")
	private String sourceDateFormat;
	@Value("${display.dateFormat}")
	private String sourceDisplayDateFormat;
	@Value("${SCHOOL_ENTRY_ALERT_ID}")
	private Integer SCHOOL_ENTRY_ID;
	@Value("${SCHOOL_EXIT_ALERT_ID}")
	private Integer SCHOOL_EXIT_ID;
	@Value("${generic.allergies}")
	private String studentAllergies;
	@Value("${PAGINATION_NO_OF_RECORDS}")
	private int PAGINATION_NO_OF_RECORDS;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}

	@RequestMapping(value = "/uploadStudents", produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView uploadStudents(ModelAndView model, @RequestParam("studentFileUpload") MultipartFile file,
			RedirectAttributes redirectAttributes, @RequestParam("token") String sessionID) throws IOException {
		log.info("Into uploadStudents() {");
		List<StudentCSVModel> list = null;
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String redirectURL = "redirect:/schoolAccountManagement?token=" + sessionID;
		int studntCount = 0;
		int studentCsvError = 0;
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

		Date date = new Date();
		String os = System.getProperty("os.name");
		if (os.contains("Windows"))
			dbDateTime = dbDateTime.replace(":", "-");
		SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
		Integer schoolId = user.getAccountId();

		try {
			File f = new File(WebUtility.createFolder(this.configProperties.getProperty("students.upload.path") + "/"
					+ schoolId + '_' + dateFormat.format(date) + ".csv"));

			boolean flag = f.createNewFile();
			log.info("fileCreated>>Student" + "\t" + flag);
			file.transferTo(f);

			try {
				list = StudentCSVToJavaUtil.convertStudentsCsvToJava(f);
				studntCount = list.size();
			} catch (NullPointerException npe) {
				if (list == null || list.size() == 0) {
					model.addObject(Constant.STUDENT_INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
					model.addObject(Constant.SessionID, sessionID);
					model.addObject(Constant.FirstName, user.getName());
					model.setViewName("schoolAccountManagement");
					return model;
				}
			}
		} catch (Exception e) {
			log.info("Error On Console For Students" + "\t" + e);
			model.addObject(Constant.STUDENT_INVALID_CSV_ERROR, Constant.INVALID_CSV_ERROR_MSG);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("schoolAccountManagement");
			return model;
		}
		Integer stndCreated = 0;
		Integer stndUpdated = 0;
		Integer stndtFailed = 0;
		Integer stndtDevicesFailed = 0;

		Map<String, Object> studentsCreateOrUpdateMap = null;
		Map<Integer, Object> ignoredMap = null;
		List<Object> outerEventList = new ArrayList<Object>();
		String jsonString = null;

		if (list.size() > 0) {
			studentsCreateOrUpdateMap = this.studentsService.uploadStudentsAndLinkDevice(list, user);
			stndCreated = (Integer) studentsCreateOrUpdateMap.get("totalStudentsCreated");
			stndUpdated = (Integer) studentsCreateOrUpdateMap.get("totalStudentsUpdated");
			stndtFailed = (Integer) studentsCreateOrUpdateMap.get("totalStudentsFailed");
			stndtDevicesFailed = (Integer) studentsCreateOrUpdateMap.get("deviceStndsFailed");

			log.info("Students Created Count" + "\t" + stndCreated);
			log.info("Students Updated Count" + "\t" + stndUpdated);
			log.info("Students Failed Count" + "\t" + stndtFailed);
			log.info("Students Devices Failed Count" + "\t" + stndtDevicesFailed);

			Map<Object, Object> outerZoneMap = new HashMap<Object, Object>();

			ignoredMap = (HashMap<Integer, Object>) studentsCreateOrUpdateMap.get("failedStudentsMap");

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
		int totalIgonred = stndtFailed + studentCsvError;
		log.info("Students Ignored Count" + "\t" + totalIgonred);

		redirectAttributes.addFlashAttribute(Constant.STUDENT_CSV_SUCCESS, Constant.CSV_SUCCESS_MSG);
		redirectAttributes.addFlashAttribute("studentsRecordCount",
				"Total Records(" + studntCount + "), Updated(" + stndUpdated + "), Created(" + stndCreated
						+ "), Ignored(" + totalIgonred + "), StudentsToDeviceMappingFailed(" + stndtDevicesFailed
						+ ")");

		if (stndtFailed > 0 || stndtDevicesFailed > 0)
			redirectAttributes.addFlashAttribute("studentsIgnoredList", jsonString);

		log.info("Exiting uploadStudents() }");
		return new ModelAndView(redirectURL);
	}

	@RequestMapping(value = "/kidsSafety", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView getKidsSafety(@RequestParam("token") String sessionID) {

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		ModelAndView model = new ModelAndView();

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
		if (!currentUser.equals("parent_admin")) {

			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		} else {
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("kidsSafety");
			model.addObject("firstName", user.getName());
			return model;
		}

	}

	@RequestMapping(value = "/profileManagement", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView getProfileManagement(@RequestParam("token") String sessionID) {

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		ModelAndView model = new ModelAndView();

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
		if (!currentUser.equals("parent_admin")) {

			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("profileManagement");
		model.addObject("username", user.getUsername());
		return model;

	}

	@RequestMapping(value = "/kidsActivityReports", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView getKidsActivityReports(@RequestParam("token") String sessionID) {
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		ModelAndView model = new ModelAndView();

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
		if (!currentUser.equals("parent_admin")) {

			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		} else {
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("kidsActivityReports");
			model.addObject("firstName", user.getName());
			return model;
		}

	}

	@RequestMapping(value = "/studentslistview", produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView listStudents(ModelAndView model, @ModelAttribute("studentsModel") StudentsModel studentsModel,
			@RequestParam("token") String sessionID) {
		this.httpSession.setAttribute("pageNo", 1);

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		Integer userId = (Integer) this.httpSession.getAttribute("userId");
		log.info("currentUser in Students Controller" + "\t" + currentUser);
		this.httpSession.getAttribute("accountId");

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

		if (currentUser.equals("school_admin")) {
			List<StudentsTransform> stTransformList = this.studentsService.listStudentUniqueClass(userId);
			String studentClass = studentsModel.getUniqueClass();
			log.info("studentClass" + "\t" + studentClass);
			List<StudentsListTransform> studentsList = null;
			List<String> classList = new ArrayList<>();
			for (StudentsTransform stf : stTransformList) {
				log.info("stf.getStClass()" + stf.getStClass());
				classList.add("School: " + stf.getSchoolName() + " - " + "Class : " + stf.getStClass());
			}
			log.info("classList.size()" + classList.size());

			String classId = null;
			if (studentClass != null) {
				if (!studentClass.equals("NONE")) {
					String[] uniqueClassArray = studentClass.split("-");
					String classToken = uniqueClassArray[1];
					String[] classIdArray = classToken.split(":");
					classId = classIdArray[1];
				}
			}

			if ((userId > 0 && studentClass == null) || (studentClass.equals("NONE"))) {
				studentsList = this.studentsService.findStudentsByAdminOrTeacher(userId, null);
			} else {
				studentsList = this.studentsService.findStudentsByAdminOrTeacher(userId, classId.trim());
			}
			model.addObject("studentsList", studentsList);
			model.addObject("listUniqueClassForStudent", classList);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("studentsList");
		} else if (currentUser.equals("school_teacher")) {
			List<StudentsTransform> stTransformer = this.studentsService.listStudentUniqueClass(userId);
			String studentClass = studentsModel.getUniqueClass();
			log.info("studentClass" + "\t" + studentClass);
			List<StudentsListTransform> studentsList = null;

			List<String> stClassList = new ArrayList<>();

			for (StudentsTransform stf : stTransformer) {
				log.info("stf.getStClass()" + stf.getStClass());
				stClassList.add("School: " + stf.getSchoolName() + " - " + "Class : " + stf.getStClass());
			}
			log.info("classList.size()" + stClassList.size());
			String classId = null;
			if (studentClass != null) {
				if (!studentClass.equals("NONE")) {
					String[] uniqueClassArray = studentClass.split("-");
					String classToken = uniqueClassArray[1];
					String[] classIdArray = classToken.split(":");
					classId = classIdArray[1];
				}
			}

			if ((userId > 0 && studentClass == null) || (studentClass.equals("NONE"))) {
				studentsList = this.studentsService.findStudentsByAdminOrTeacher(userId, null);
			} else {
				studentsList = this.studentsService.findStudentsByAdminOrTeacher(userId, classId.trim());
			}
			model.addObject("studentsList", studentsList);
			model.addObject("listUniqueClassForStudent", stClassList);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("studentsList");
		} else if (currentUser.equals("parent_admin")) {
			log.info("entered as parent admin");
			List<TeachersStudentsTransform> teacherStudentsList = null;
			List<StudentsTransform> stTransform = this.parentService.findKidsClassAndSchool(userId);
			List<String> kidsClassAndSchoolList = new ArrayList<>();

			for (StudentsTransform stf : stTransform) {
				kidsClassAndSchoolList.add("School: " + stf.getSchoolName() + " - " + "Class : " + stf.getStClass());
			}

			String classId = null;
			String stUniqueClass = studentsModel.getUniqueClass();

			if (stUniqueClass != null) {
				log.info("stUniqueClass Value" + "\t" + stUniqueClass);
				if (!stUniqueClass.equals("NONE")) {
					String[] uniqueClassArray = stUniqueClass.split("-");
					String classToken = uniqueClassArray[1];
					String[] classIdArray = classToken.split(":");
					classId = classIdArray[1];
				}
			}

			if ((userId != null && studentsModel.getUniqueClass() == null)
					|| (studentsModel.getUniqueClass().equals("NONE")))
				teacherStudentsList = this.parentService.viewKidsList(userId, null);
			if (userId != null && classId != null)
				teacherStudentsList = this.parentService.viewKidsList(userId, classId.trim());

			model.addObject("teacherStudentsList", teacherStudentsList);
			model.addObject("sessionID", sessionID);
			model.addObject("listUniqueClassForStudent", kidsClassAndSchoolList);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("parentKids");
		}
		model.addObject("dateFormat", this.configProperties.getProperty("display.dateFormat"));
		return model;
	}

	@RequestMapping(value = "/addStudent", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView addStudent(ModelAndView model, @ModelAttribute("studentsModel") StudentsModel studentsModel,
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
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		List<ClassGradeTransform> cgt = this.classGradeService.getStudentsClass(user.getId());
		List<String> classList = new ArrayList<>();

		for (ClassGradeTransform cgTransform : cgt) {
			classList.add(String.valueOf(cgTransform.getStudentClass()));
		}

		this.httpSession.setAttribute("Add", "Add");
		this.httpSession.setAttribute("Added", "Student Added Successfully");
		model.addObject("cgList", classList);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("addStudent");
		return model;
	}

	@RequestMapping(value = "/saveStudent", method = RequestMethod.POST)
	public ModelAndView saveStudent(ModelAndView model,
			@ModelAttribute("studentsModel") @Validated StudentsModel studentsModel,
			RedirectAttributes redirectAttributes, BindingResult result, @RequestParam("token") String sessionID) {
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

		String currentUser = (String) this.httpSession.getAttribute("currentUser");

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
						|| (this.httpSession.getAttribute("currentUser").equals("parent_admin")))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		if (currentUser.equals("school_admin")) {
			validator.validate(studentsModel, result);
			if (result.hasErrors()) {
				log.info("Jsp Has Errors");
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("addStudent");
				return model;
			}
		} else if (currentUser.equals("parent_admin")) {
			log.info("currentUser1" + "\t" + currentUser);
			kidsValidator.validate(studentsModel, result);
			log.info("currentUser2" + "\t" + currentUser);
			if (result.hasErrors()) {
				log.info("Kids Edit View Has Errors");
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("editKid");
				return model;
			}
		}
		log.info("studentsModel.getStudentId()" + "\t" + studentsModel.getStudentId());
		if (studentsModel.getStudentId() == null || studentsModel.getStudentId().intValue() == 0) {
			log.info("New Adding Functionality");
			Students studentsEntity = this.studentModelEntity.prepareStudentsEntity(studentsModel,
					sourceDisplayDateFormat, sourceDateFormat);
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			this.studentsService.addStudent(studentsEntity, studentsModel.getUuid(), studentsModel.getFirmware());
			activityLogs.info(activityLog);
			redirectAttributes.addFlashAttribute("success",
					"Student " + studentsModel.getName() + " Added Successfully");

		} else {
			action = "Update";
			log.info("New Updating functionality");
			log.info("DOB From FE" + "\t" + studentsModel.getDob());
			this.httpSession.setAttribute("updateStudent", "updateStudent");
			ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary, ipaddress);
			log.info("studentsModel.getStudentId() " + studentsModel.getStudentId());

			Students studentsEntity = this.studentModelEntity.prepareStudentsEntity(studentsModel,
					sourceDisplayDateFormat, sourceDateFormat);
			Students oldStudentData = this.studentsService.getStudent(studentsEntity.getStudentId());
			activityLogs.info(activityLog);
			studentsEntity.setCreateDate(oldStudentData.getCreateDate());
			// studentsEntity.setDob(studentsModel.getDob());
			this.studentsService.updateStudent(studentsEntity);

			if (currentUser.equals("school_admin"))
				redirectAttributes.addFlashAttribute("updatesuccess",
						"Student " + studentsEntity.getName() + " Updated Successfully");
			else if (currentUser.equals("parent_admin")) {
				redirectAttributes.addFlashAttribute("updatesuccess", "Kids Profile Updated Successfully");
				return new ModelAndView(
						"redirect:/editStudent?token=" + sessionID + "&studentId=" + studentsEntity.getStudentId());
			}
		}
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return new ModelAndView("redirect:/studentslistview?token=" + sessionID);
	}

	@RequestMapping(value = "/saveWebStudents", method = RequestMethod.POST)
	public ModelAndView saveWebStudents(ModelAndView model,
			@ModelAttribute("studentsWebModel") StudentsWebModel studentsWebModel,
			RedirectAttributes redirectAttributes, @RequestParam("token") String sessionID) {
		log.info("Into saveWebStudents() {");
		String createOrUpdate = "";
		action = "Create";
		methodName = new Object() {
		}.getClass().getEnclosingMethod().getName();
		className = this.getClass().getName();
		summary = className + "." + methodName;
		InetAddress ipAddr;
		Devices devices = null;
		String stCreate = request.getParameter("studentCreate");
		log.info("*** stCreate ***" + "\t" + stCreate);

		String redirectURL = "redirect:/schoolAccountManagement?token=" + sessionID;

		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Users user = userService.validateUserBySession(sessionID);
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
		if (((this.httpSession.getAttribute("currentUser") != null))
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		Students students = this.studentsService.isRegistrationNumberExists(studentsWebModel.getAccMgtStudentAppno(),
				user.getAccountId());
		if (null != studentsWebModel.getAccMgtStudentUuid()) {
			devices = this.deviceService.findDeviceForSchoolId(studentsWebModel.getAccMgtStudentUuid(),
					user.getAccountId());
		}

		if (null != students) {
			redirectAttributes.addFlashAttribute(Constant.STUDENT_EXISTS, Constant.STUDENT_EXISTS_MSG);
			return new ModelAndView(redirectURL);
		} else if ((studentsWebModel.getAccMgtStudentUuid() == null
				|| studentsWebModel.getAccMgtStudentUuid().trim().length() == 0 || null != devices)
				&& students == null) {
			try {
				log.info("Checking createWebStudentsAndLinkDevice()");
				createOrUpdate = this.studentsService.createWebStudentsAndLinkDevice(studentsWebModel, user);
				log.info("createOrUpdate In StudentsController" + "\t" + createOrUpdate);
			} catch (Exception e) {
				log.info("Exception Occured While Device is Mapping");
				redirectAttributes.addFlashAttribute(Constant.DEVICE_MAP_ERROR, Constant.DEVICE_MAP_ERROR_MSG);
				return new ModelAndView(redirectURL);
			}
		} else if (null == devices) {
			log.info("Checking If devices are null");
			redirectAttributes.addFlashAttribute(Constant.UUID_NOT_EXISTS, Constant.UUID_NOT_EXISTS_MSG);
			return new ModelAndView(redirectURL);
		}
		try {
			if (createOrUpdate.equals("Create")) {
				redirectAttributes.addFlashAttribute(Constant.CREATE_ACCOUNT_SUCCESS,
						Constant.CREATE_ACCOUNT_SUCCESS_MSG);
			} else if (createOrUpdate.equals("Update")) {
				redirectAttributes.addFlashAttribute(Constant.CREATE_ACCOUNT_SUCCESS,
						Constant.UPDATE_ACCOUNT_SUCCESS_MSG);
			} else if (createOrUpdate.equals("deviceMappingError")) {
				log.info("Exception Occured While Device is Mapping --> In if Condition");
				redirectAttributes.addFlashAttribute(Constant.DEVICE_MAP_ERROR, Constant.DEVICE_MAP_ERROR_MSG);
				return new ModelAndView(redirectURL);
			}
		} catch (Exception e) {
			log.info("Exception Occured in saveWebStudents()" + e);
			redirectAttributes.addFlashAttribute(Constant.ACCOUNT_FAILED, Constant.ACCOUNT_FAILED_MSG);
		}
		return new ModelAndView(redirectURL);
	}

	@RequestMapping(value = "/editStudent")
	public ModelAndView editStudent(ModelAndView model, @ModelAttribute("studentsModel") StudentsModel studentsModel,
			@RequestParam("token") String sessionID) {

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		List<ClassGradeTransform> cgt = this.classGradeService.getStudentsClass(user.getId());
		List<String> classList = new ArrayList<>();

		for (ClassGradeTransform cgTransform : cgt) {
			classList.add(String.valueOf(cgTransform.getStudentClass()));
		}

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
						|| (this.httpSession.getAttribute("currentUser").equals("parent_admin")))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		Students student = this.studentsService.getStudent(studentsModel.getStudentId());
		this.httpSession.setAttribute("edit", "edit");
		this.httpSession.setAttribute("dob", student.getDob());
		log.info("dob from session" + "\t" + this.httpSession.getAttribute("dob"));
		StudentsModel sm = this.studentModelEntity.prepareStudentsModel(student, sourceDisplayDateFormat);
		model.addObject("studentsModel", sm);
		model.addObject("cgList", classList);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		if (user.getRoleType().equals("school_admin"))
			model.setViewName("addStudent");
		if (user.getRoleType().equals("parent_admin")) {
			List<TeachersStudentsTransform> teacherStudentsList = this.parentService.viewKidsList(user.getId(), null);
			model.addObject("teacherStudentsList", teacherStudentsList);
			model.setViewName("editKid");
		}
		return model;
	}

	@RequestMapping(value = "/editKids")
	public ModelAndView editKids(ModelAndView model, @RequestParam("token") String sessionID,
			@ModelAttribute("studentsModel") StudentsModel studentsModel) {

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
						|| (this.httpSession.getAttribute("currentUser").equals("parent_admin")))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		this.httpSession.setAttribute("edit", "edit");
		log.info("dob from session" + "\t" + this.httpSession.getAttribute("dob"));
		model.addObject("sessionID", sessionID);
		model.addObject("firstName", user.getName());
		if (user.getRoleType().equals("parent_admin")) {
			List<TeachersStudentsTransform> teacherStudentsList = this.parentService.viewKidsList(user.getId(), null);
			model.addObject("teacherStudentsList", teacherStudentsList);
			model.addObject("generic_allergies", studentAllergies);
			model.setViewName("editKid");
		}
		return model;
	}

	@RequestMapping(value = "/newParentKids")
	public ModelAndView newParentKids(ModelAndView model, @RequestParam("token") String sessionID,
			@ModelAttribute("studentsModel") StudentsModel studentsModel) {

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
						|| (this.httpSession.getAttribute("currentUser").equals("parent_admin")))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		this.httpSession.setAttribute("edit", "edit");

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("user_name", user.getUsername());
		if (user.getRoleType().equals("parent_admin")) {
			List<TeachersStudentsTransform> teacherStudentsList = this.parentService.viewKidsList(user.getId(), null);
			model.addObject("teacherStudentsList", teacherStudentsList);
			model.addObject("generic_allergies", studentAllergies);
			model.addObject("user_name", user.getUsername());
			model.setViewName("newParentKids");
			log.info("Inside newParentKids");
		}
		return model;
	}

	@RequestMapping(value = "/studentslistbyclass")
	public ModelAndView listStudentsByClass(ModelAndView model,
			@ModelAttribute("studentsModel") StudentsModel studentsModel, @RequestParam("token") String sessionID) {

		this.httpSession.setAttribute("pageNo", 1);

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

		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		Integer userId = (Integer) this.httpSession.getAttribute("userId");
		Integer schoolId = (Integer) this.httpSession.getAttribute("accountId");

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

		if (currentUser.equals("school_admin")) {
			List<StudentsTransform> stTransform = this.studentsService.listStudentUniqueClass(userId);
			List<String> classList = new ArrayList<>();

			for (StudentsTransform sf : stTransform) {
				classList.add(String.valueOf(sf.getStClass()));
			}
			List<StudentsModel> studentsList = this.studentModelEntity.prepareStudentsModelList(
					this.studentsService.findStudentsByClass(schoolId, studentsModel.getUniqueClass(), 1), 1,
					sourceDisplayDateFormat);
			model.addObject("studentsList", studentsList);
			model.addObject("listUniqueClassForStudent", classList);
			model.setViewName("studentsList");
		} else if (currentUser.equals("school_teacher")) {
			log.info("Entered into Else Part");
			List<StudentsTransform> stTransformer = this.teacherService.findStudentsClass(userId);
			List<String> stClassList = new ArrayList<>();

			for (StudentsTransform st : stTransformer) {
				stClassList.add(String.valueOf(st.getStClass()));
			}
			List<TeachersStudentsTransform> teacherStudentsList = this.studentsService
					.findStudentsClassByTransformer(schoolId, studentsModel.getUniqueClass(), 1);
			model.addObject("teacherStudentsList", teacherStudentsList);
			model.addObject("listUniqueClassForStudent", stClassList);
			model.setViewName("teachersStudentsList");
		} else if (currentUser.equals("parent_admin")) {
			log.info("Entered into Else Part");
			List<StudentsTransform> stTransform = this.parentService.findKidsClassAndSchool(userId);
			List<String> kidsClassAndSchoolList = new ArrayList<>();

			for (StudentsTransform stf : stTransform) {
				kidsClassAndSchoolList.add("School: " + stf.getSchoolName() + " - " + "Class : " + stf.getStClass());
			}

			String stUniqueClass = studentsModel.getUniqueClass();
			String[] uniqueClassArray = stUniqueClass.split("-");

			String classToken = uniqueClassArray[1];

			String[] classIdArray = classToken.split(":");
			String classId = classIdArray[1];

			List<TeachersStudentsTransform> teacherStudentsList = this.parentService.viewKidsListByClass(userId,
					Integer.parseInt(classId.trim()));
			model.addObject("teacherStudentsList", teacherStudentsList);
			model.addObject("listUniqueClassForStudent", kidsClassAndSchoolList);
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("parentKids");
			// return new ModelAndView("redirect:/studentslistview?token=" +
			// sessionID);
		}
		return model;
	}

	@RequestMapping(value = "/mobile/StudentLocation/{sessionID}/{uuid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentLocation(@PathVariable String sessionID, @PathVariable String uuid) {

		String statusCode = null;
		String statusMsg = null;
		String type = "students.StudentLocation";
		String respondJson = null;

		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin ";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return respondJson;
			}
			String roleType = user.getRoleType();
			int userId = user.getId();
			if (roleType.equals("school_teacher") || roleType.equals("parent_admin")) {
				String jsonString = null;
				Devices devices = this.deviceService.checkDeviceIdExist(uuid);
				if (null != devices) {
					StudentsMapLocationTransform stLocTrasnform = this.studentsService.viewStudentsLocation(uuid,
							userId, roleType);
					log.info("stLocTrasnform" + "\t" + stLocTrasnform);

					if (stLocTrasnform == null) {
						statusCode = "SUC01";
						statusMsg = "No Data";
						respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
						//JSONUtility.respondAsJSON(response, respondJson);
						return respondJson;
					}

					String loc = stLocTrasnform.getGpsLocationData();
					String[] locArray = null;
					Map<Object, Object> map = new LinkedHashMap<>();
					Date eventOccuredDate = stLocTrasnform.getEventOccuredDate();
					if (null != loc) {
						locArray = loc.split(",");
						String latitude = locArray[0];
						String longitude = locArray[1];

						map.put("latitude", latitude);
						map.put("longitude", longitude);
					}
					if (eventOccuredDate != null) {
						log.info("eventOccuredDate" + "\t" + eventOccuredDate);
						map.put("event_occured_date", eventOccuredDate.toString());
					}
					jsonString = JSONObject.toJSONString(map);
					respondJson = ErrorCodesUtil.displayJSONForStudentLocation("", "", jsonString);
					log.info("respJson" + "\t" + respondJson);
					//JSONUtility.respondAsJSON(response, respJson);
				} else {
					statusCode = "ERR06";
					statusMsg = "Device Does Not Exist";
					respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
				}
			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
		}

		return respondJson;
	}

	@RequestMapping(value = "/mobile/StudentList/{sessionID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentList(@PathVariable String sessionID) {

		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;

		String statusCode = null;
		String statusMsg = null;
		String type = null;
		String respondJson = null;
		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin ";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return respondJson;
			}
			String roleType = user.getRoleType();

			if (roleType.equals("parent_admin")) {
				List<TeachersStudentsTransform> tsTransformList = this.parentService.viewKidsList(user.getId(), null);
				for (TeachersStudentsTransform tst : tsTransformList) {
					map = new LinkedHashMap<>();
					List<Integer> guardianList = this.studentsService.getGuardianIdsByStudentId(tst.getStudentId());
					map.put("student_id", tst.getStudentId());
					if (tst.getStudentName() != null)
						map.put("name", tst.getStudentName());
					if (tst.getNickName() != null)
						map.put("nickname", tst.getNickName());
					if (tst.getHeight() != null)
						map.put("height", tst.getHeight());
					if (tst.getWeight() != null)
						map.put("weight", tst.getWeight());
					if (tst.getDob() != null)
						map.put("dob", tst.getDob().toString());
					if (tst.getGender() != null)
						map.put("gender", tst.getGender());
					if (tst.getAllergeis() != null)
						map.put("allergies", tst.getAllergeis());
					if (tst.getEmergency_contact() != null)
						map.put("emergency_contact", tst.getEmergency_contact());
					if (null != tst.getDeviceUuid())
						map.put("uuid", tst.getDeviceUuid());
					if (null != tst.getRollNo())
						map.put("roll_no", tst.getRollNo());
					if (null != tst.getRegistartionNumber())
						map.put("registartion_no", tst.getRegistartionNumber());
					map.put("guardian_count", guardianList.size());
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				log.info("***myList***" + "\n" + myList);
				respondJson = ErrorCodesUtil.displayJSONForStudentsList("", "", myList.toString());
				log.info("***kidsRespJson***" + "\t" + respondJson);
				//JSONUtility.respondAsJSON(response, kidsRespJson);
				//return kidsRespJson;
			} else if (roleType.equals("school_teacher")) {
				List<StudentsListTransform> stListTransform = this.studentsService
						.findStudentsByAdminOrTeacher(user.getId(), null);

				for (StudentsListTransform slt : stListTransform) {
					map = new LinkedHashMap<>();
					map.put("student_id", slt.getStudentId());
					if (slt.getStudentName() != null)
						map.put("name", slt.getStudentName());
					if (null != slt.getStudentClass())
						map.put("class", slt.getStudentClass());
					if (null != slt.getGrade())
						map.put("grade", slt.getGrade());
					if (null != slt.getRollNo())
						map.put("roll_no", slt.getRollNo());
					map.put("registartion_no", slt.getRegistartionNumber());
					if (slt.getEmergency_contact() != null)
						map.put("emergency_contact", slt.getEmergency_contact());
					if (null != slt.getDeviceUuid())
						map.put("uuid", slt.getDeviceUuid());
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				respondJson = ErrorCodesUtil.displayJSONForStudentsList("", "", myList.toString());
				log.info("***studentsRespJson***" + "\t" + respondJson);
				//JSONUtility.respondAsJSON(response, studentsRespJson);
				//return studentsRespJson;
			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "students.StudentList";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
				//return respondJson;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "students.StudentList";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			//return respondJson;
		}
		return respondJson;
	}

	@RequestMapping(value = "/web/StudentListByMenberId/{memberId}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentListByMenberId(@PathVariable int memberId) {

		String jsonString = null;
		List myList = new ArrayList<>();
		Map<Object, Object> map = null;

		List<TeachersStudentsTransform> tsTransformList = this.parentService.viewKidsList(memberId, null);
		for (TeachersStudentsTransform tst : tsTransformList) {
			map = new LinkedHashMap<>();
			map.put("student_id", tst.getStudentId());
			map.put("nickname", tst.getNickName());
			map.put("uuid", tst.getDeviceUuid());
			jsonString = JSONObject.toJSONString(map);
			myList.add(jsonString);
		}

		log.info("***myList***" + "\n" + myList);
		String kidsRespJson = ErrorCodesUtil.displayJSONForStudentsList("", "", myList.toString());
		log.info("***kidsRespJson***" + "\t" + kidsRespJson);
		//JSONUtility.respondAsJSON(response, kidsRespJson);
		return kidsRespJson;

	}

	@RequestMapping(value = "/mobile/StudentUpdate/{sessionID}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public String StudentUpdate(@PathVariable String sessionID, @RequestBody StudentsModel studentsModel) {
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
		String type = "students.StudentUpdate";
		String responseJSON = null;

		String json = JSONUtility.convertObjectToJson(studentsModel);
		log.info("json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			statusMsg = "Input Is Invalid";
			statusCode = "ERR05";
			type = "students.StudentUpdate";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}
		/**
		 * Check to see if the key names are not sent in the request, if not its
		 * a ERR04.
		 */
		org.json.JSONObject jsonObject = new org.json.JSONObject(studentsModel);
		if (jsonObject.isNull("student_id") || jsonObject.isNull("nickname") || jsonObject.isNull("gender")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_EMPTY_STATUS_MSG,
					Constant.StatusCode4, type);
			return responseJSON;
		}

		if ((sessionID.isEmpty() || sessionID.equals(null))
				|| (studentsModel.getStudent_id().intValue() == 0 || studentsModel.getStudent_id().equals(null))
				|| (studentsModel.getNickname().isEmpty() || studentsModel.getNickname() == null
						|| studentsModel.getNickname().trim().length() == 0)
				|| (studentsModel.getGender().isEmpty() || studentsModel.getGender().equals(null))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.WEARABLE_INPUT_EMPTY_STATUS_MSG,
					Constant.StatusCode4, type);
			return responseJSON;
		}

		/*Users user = userService.getUserByMobileSessionId(sessionID);

		log.info("user is " + user);
		if (null == user) {
			user = userService.getUserBySessionId(sessionID);
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
		}*/

		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
						Constant.StatusCodeValidity, Constant.SubscriptionsUpdateType);
				// JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			Students existingStudent = this.studentsService.getStudentForStudentsUpdate(studentsModel.getStudent_id());
			if(null != existingStudent){
				if (user.getRoleType().equals(Constant.ParentAdmin)) {
					boolean iskidAlreadyLinked = this.parentService.ParentKidLinked(user.getId(),
							studentsModel.getStudent_id());
					if (!iskidAlreadyLinked) {
						log.info("linkParentKid Not Found");
						this.parentService.linkParentKid(user.getId(), studentsModel.getStudent_id());
	
						ArrayList<Integer> eventidList = (ArrayList<Integer>) this.eventService
								.getDefaultEventIdListForParent();
	
						if (null != eventidList) {
							for (int eventid : eventidList) {
	
								/*EventSubscriptions oldEs = this.eventService.isMembersubscribed(user.getId(), eventid,
										existingStudent.getStudentId());
	
								if (null == oldEs) {*/
								boolean isMemberSubscribed = this.eventService.isMemberSubscribed(user.getId(), existingStudent.getStudentId(),
										eventid);
	
								if (isMemberSubscribed != true) {
									/*SupportedEvents supportedEvent = this.eventService.getSupportedEventsByEventId(eventid);
									EventSubscriptions es = new EventSubscriptions();
									es.setStudents(existingStudent);
									es.setUsers(user);
									es.setEvents(supportedEvent);
									es.setCreatedDate(new Date());*/
									ActivityLog activityLog = CommonUtil.formulateActivityLogs(user, action, summary,
											ipaddress);
									//eventService.subscribeEvent(es);
									eventService.subscribeEvent(user.getId(), existingStudent.getStudentId(), eventid);
									activityLogs.info(activityLog);
								}
							}
						}
	
					}
					Students students = this.studentModelEntity.studentsParentEntity(studentsModel, existingStudent,
							sourceDisplayDateFormat, sourceDateFormat);
					this.studentsService.updateStudent(students);
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					type = "students.StudentUpdate";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				} else if (user.getRoleType().equals(Constant.SchoolAdmin)) {
					Students students = this.studentModelEntity.studentsSchoolAdminEntity(studentsModel, existingStudent);
					this.studentsService.updateStudent(students);
					statusCode = "SUC01";
					statusMsg = "API Request Success";
					type = "students.StudentUpdate";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "students.StudentUpdate";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			}else {
				statusCode = "ERR31";
				statusMsg = "Invalid Student and parent member combination";
				type = "students.StudentUpdate";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "students.StudentUpdate";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/web/SchoolInOut/{sessionID}/{inputdate}/{grade}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SchoolInOut(@PathVariable String sessionID, @PathVariable String inputdate,
			@PathVariable String grade) throws Exception {

		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;

		String type = "students.SchoolInOut";
		Map<Object, Object> outerMap = null;
		Map<String, Object> studentAttendanceMap = null;
		Map<String, Object> studentAbsentAbnormalMap = null;

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("school_admin")) {
					int school_id = user.getAccountId();
					outerMap = new LinkedHashMap<>();

					if (grade.equals("0")) {
						studentAttendanceMap = studentsService.getStudentAttendance(school_id, null, null, inputdate);

						studentAbsentAbnormalMap = studentsService.getAbsentAbnormalStudents(school_id, null, null,
								inputdate);

						outerMap.put("school_statistics", studentAttendanceMap.get("statistics"));
						outerMap.put("absent_students", studentAbsentAbnormalMap.get("absent"));
						outerMap.put("abnormal_students", studentAbsentAbnormalMap.get("abnormal"));
					} else {
						studentAttendanceMap = studentsService.getStudentAttendance(school_id, grade, null, inputdate);
						outerMap.put("grade_statistics", studentAttendanceMap.get("statistics"));

						// Get list of classes in a grade and query statistics
						// for each class
						List<ClassGradeTransform> cgt = this.classGradeService.getGradeClass(school_id, "school_id",
								grade);
						ArrayList<Object> classwiseStudentsList = new ArrayList<Object>();
						for (ClassGradeTransform cgTransform : cgt) {
							Map<Object, Object> innerMap = new LinkedHashMap<>();
							String studentClass = cgTransform.getStudentClass().toString();

							studentAttendanceMap = studentsService.getStudentAttendance(school_id, grade, studentClass,
									inputdate);

							studentAbsentAbnormalMap = studentsService.getAbsentAbnormalStudents(school_id, grade,
									studentClass, inputdate);

							innerMap.put("student_class", studentClass);
							innerMap.put("class_statistics", studentAttendanceMap.get("statistics"));
							innerMap.put("absent_students", studentAbsentAbnormalMap.get("absent"));
							innerMap.put("abnormal_students", studentAbsentAbnormalMap.get("abnormal"));

							classwiseStudentsList.add(innerMap);
						}

						outerMap.put("classes", classwiseStudentsList);
					}
					responseJSON = ErrorCodesUtil.displaySchoolAdminData("API Request Success", "SUC01",
							"students.SchoolInOut", JSONObject.toJSONString(outerMap));
					log.info("***SchoolInOut JSON***" + "\t" + responseJSON);
					/*if (!StringUtility.isNull(request.getHeader("allowCacheContent"))
							&& request.getHeader("allowCacheContent").equals("1")) {
						JSONUtility.respondAsJSON(response, responseJSON, 720);
					} else {
						JSONUtility.respondAsJSON(response, responseJSON);
					}*/
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "students.SchoolInOut";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "students.SchoolInOut";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "students.SchoolInOut";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/web/SOSAlerts/{sessionID}/{inputdate}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SOSAlerts(@PathVariable String sessionID, @PathVariable String inputdate) throws Exception {

		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;

		String type = "students.SOSAlerts";
		Map<Object, Object> outerMap = null;
		Map<String, Object> studentSOSAlertStatsMap = null;
		Map<String, Object> studentStudentSOSAlertsMap = null;

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("school_admin")) {
					int school_id = user.getAccountId();
					outerMap = new LinkedHashMap<>();

					studentSOSAlertStatsMap = studentsService.getStudentSOSAlertStats(school_id, inputdate);
					outerMap.put("school_statistics", studentSOSAlertStatsMap.get("statistics"));

					studentStudentSOSAlertsMap = studentsService.getStudentSOSAlerts(school_id, inputdate);
					outerMap.put("sos_alerts", studentStudentSOSAlertsMap.get("alerts"));

					responseJSON = ErrorCodesUtil.displaySchoolAdminData("API Request Success", "SUC01",
							"students.SOSAlerts", JSONObject.toJSONString(outerMap));

					log.info("***SOSAlerts JSON***" + "\t" + responseJSON);
					/*if (!StringUtility.isNull(request.getHeader("allowCacheContent"))
							&& request.getHeader("allowCacheContent").equals("1")) {
						JSONUtility.respondAsJSON(response, responseJSON, 720);
					} else {
						JSONUtility.respondAsJSON(response, responseJSON);
					}*/

				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "students.SOSAlerts";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "students.SOSAlerts";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "students.SOSAlerts";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/web/SearchStudents/{sessionID}/{pageid}/{studentName}/{registrationNo}/{deviceUUID}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String SearchStudents(@PathVariable String sessionID, @PathVariable int pageid,
			@PathVariable String studentName, @PathVariable String registrationNo, @PathVariable String deviceUUID)
			throws Exception {

		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;
		int total = PAGINATION_NO_OF_RECORDS;
		int noofpages = 0;
		int currentPage = pageid;
		String type = "students.SearchStudents";
		Map<Object, Object> outerMap = null;
		Map<String, Object> studentsMap = null;

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("school_admin") || user.getRoleType().equals(Constant.SupprotStaff)) {
					int school_id = user.getAccountId();
					outerMap = new LinkedHashMap<>();

					if (pageid == 1) {
					} else {
						pageid = (pageid - 1) * total + 1;
					}

					int totalNoofstudents = studentsService.totalNooFStudents(school_id, studentName, registrationNo,
							deviceUUID, user.getRoleType());

					if (totalNoofstudents < total)
						noofpages = 1;
					else if (totalNoofstudents % total == 0)
						noofpages = totalNoofstudents / total;
					else
						noofpages = totalNoofstudents / total + 1;

					studentsMap = studentsService.searchStudents(school_id, studentName, registrationNo, deviceUUID,
							user.getRoleType(), pageid - 1, total);
					ArrayList<HashMap<String, Object>> lStudentsList = (ArrayList<HashMap<String, Object>>) studentsMap
							.get("search_result");
					// HashMap<String, Object> existingMap = (HashMap<String,
					// Object>)studentsMap.get("search_result");
					outerMap.put("noofPages", noofpages);
					outerMap.put("currentPage", currentPage);

					outerMap.put("students", lStudentsList);

					responseJSON = ErrorCodesUtil.displaySchoolAdminData("API Request Success", "SUC01",
							"students.SearchStudents", JSONObject.toJSONString(outerMap));

					log.info("***SOSAlerts JSON***" + "\t" + responseJSON);
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					type = "students.SearchStudents";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				type = "students.SearchStudents";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "students.SearchStudents";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/updateStudentApi/{sessionID}/{id}", method = RequestMethod.POST)
	public String updateStudentApi(@PathVariable("id") Integer id, @PathVariable("sessionID") String sessionID,
			@RequestBody StudentsModel studentsModel) {
		log.info("updateStudentApi Start");
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
		String respondJson = null;
		Users user = userService.validateUserBySession(sessionID);
		Students student = this.studentsService.getStudent(id);
		if (null != user) {
			if (user.getRoleType().equals(Constant.SchoolAdmin)) {
				boolean studentUpdated = this.studentsService.updateStudentsApi(user, studentsModel, student);
				log.info("studentUpdated" + "\t" + studentUpdated);
				if (studentUpdated) {
					respondJson = ErrorCodesUtil.displayErrorJSON(Constant.ValidErrorMessage, Constant.ValidStatusCode,
							Constant.STUDENT_UPDATE_API_TYPE);
				} else {
					respondJson = ErrorCodesUtil.displayErrorJSON(Constant.FailureMessage, Constant.FailureMessage,
							Constant.STUDENT_UPDATE_API_TYPE);
				}
			} else {
				respondJson = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCodeDescription,
						Constant.UnauthorisedUserErrorCode, Constant.STUDENT_UPDATE_API_TYPE);
			}
		} else {
			respondJson = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageJon, Constant.StatusCodeJon,
					Constant.STUDENT_UPDATE_API_TYPE);
		}
		log.info("updateStudentApi Before Return");
		return respondJson;
	}

	@RequestMapping(value = "/deleteStudentApi/{token}/{studentId}")
	public String deleteStudentApi(@PathVariable("studentId") String studentId, @PathVariable("token") String token) {

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

		Users sessionUser = userService.validateUserBySession(token);

		if (null != sessionUser) {

			if (sessionUser.getRoleType().equals(Constant.SchoolAdmin)) {
				ActivityLog activityLog = CommonUtil.formulateActivityLogs(sessionUser, action, summary, ipaddress);
				int noOfStndsDeleted = this.studentsService.deleteStudentApi(Integer.parseInt(studentId));
				log.info("No of Students Deleted" + "\t" + noOfStndsDeleted);
				activityLogs.info(activityLog);

				statusCode = "SUC01";
				statusMsg = "API Request Success";
				type = "student.deleteStudentApi";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
				//return respondJson;

			} else {
				statusCode = "ERR03";
				statusMsg = "Unauthorised User";
				type = "student.deleteStudentApi";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
				//return respondJson;
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			type = "student.deleteStudentApi";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			//return respondJson;
		}
		return respondJson;
	}

	@RequestMapping(value = "/studentsWebListAPI/{token}/{filterGrade}/{filterClass}/{pageid}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String studentsWebListAPI(@PathVariable("token") String sessionID,
			@PathVariable("filterGrade") String filterGrade, @PathVariable("filterClass") String filterClass,
			@PathVariable("pageid") int pageid) throws ParseException {
		log.info("Inside studentsWebListAPI");
		String type = "student.studentsWebListAPI";
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

				List<StudentsListTransform> studentsListTransform1 = this.studentsService
						.findStudentsByAdmin(user.getId(), filterGrade, filterClass, 0, 0);
				List<StudentsListTransform> studentsListTransform = this.studentsService
						.findStudentsByAdmin(user.getId(), filterGrade, filterClass, pageid, total);

				int listSize = studentsListTransform1.size();

				if (listSize > 0 && pageid == 1) {
					if (listSize < total)
						noofpages = 1;
					else if (listSize % total == 0)
						noofpages = listSize / total;
					else
						noofpages = listSize / total + 1;
					this.httpSession.setAttribute("studentspages", String.valueOf(noofpages));
				}

				for (StudentsListTransform slt : studentsListTransform) {
					map = new LinkedHashMap<>();
					if (slt.getStudentId() != null) {
						map.put("student_id", slt.getStudentId());
					}

					if (slt.getStudentName() != null) {
						map.put("student_name", slt.getStudentName());
					}

					if (slt.getRegistartionNumber() != null) {
						map.put("student_reg_no", slt.getRegistartionNumber());
					}

					if (slt.getGrade() != null) {
						map.put("student_grade", slt.getGrade());
					}

					if (slt.getStudentClass() != null) {
						map.put("student_class", slt.getStudentClass());
					}

					if (slt.getRollNo() != null) {
						map.put("student_rollno", slt.getRollNo());
					}

					if (slt.getDeviceUuid() != null) {
						map.put("student_uuid", slt.getDeviceUuid());
					}

					if (slt.getEmergency_contact() != null) {
						map.put("student_emergency_contact", slt.getEmergency_contact());
					}

					map.put("noofPages", (String) this.httpSession.getAttribute("studentspages"));
					map.put("currentPage", currentPage);

					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				responseJSON = ErrorCodesUtil.displayJSONForStudentsList("", "", myList.toString());
				log.info("***studentsRespJson In studentsWebListAPI***" + "\t" + responseJSON);
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
						Constant.UnauthorisedUserErrorCodeDescription, type);
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.StatusCodeJon, Constant.ErrorMessageJon, type);
		}
		return responseJSON;
	}

	@RequestMapping(value = "/filterWebStudents")
	public ModelAndView filterWebStudents(HttpServletRequest request, ModelAndView model,
			@RequestParam("token") String sessionID) {
		log.info("Into filterWebStudents() {");

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String redirectURL = "redirect:/schoolAccountManagement?token=" + sessionID;

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

		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		String studentGrade = request.getParameter("accMgtStudentGradeFilter");
		String studentClass = request.getParameter("accMgtStudentClassFilter");
		log.info("<<<studentGrade In filterWebStudents>>>" + "\t" + studentGrade);
		log.info("<<<studentClass In filterWebStudents>>>" + studentClass);

		if (null == studentGrade)
			studentGrade = "Grade";
		if (null == studentClass)
			studentClass = "Class";

		this.httpSession.setAttribute("studentGrade", studentGrade);
		this.httpSession.setAttribute("studentClass", studentClass);
		List<StudentsListTransform> studentsListTransform = this.studentsService.findStudentsByAdmin(user.getId(),
				studentGrade, studentClass, 0, 0);
		model.addObject("studentsList", studentsListTransform);

		return new ModelAndView(redirectURL);
	}

	@RequestMapping(value = "/web/filterWebStudentsAPI/{sessionID}/{filterGrade}/{filterClass}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String filterWebStudentsAPI(@PathVariable("sessionID") String sessionID,
			@PathVariable("filterGrade") String filterGrade, @PathVariable("filterClass") String filterClass)
			throws ParseException {

		String type = "student.filterWebStudentsAPI";
		Map<Object, Object> map = null;
		List myList = new ArrayList<>();
		String responseJSON = null;
		String jsonString = null;
		Users user = userService.validateUserBySession(sessionID);
		if (null != user) {
			if (user.getRoleType().equals(Constant.SchoolAdmin)) {

				List<StudentsListTransform> studentsListTransform = this.studentsService
						.findStudentsByAdmin(user.getId(), filterGrade, filterClass, 0, 0);
				for (StudentsListTransform slt : studentsListTransform) {
					map = new LinkedHashMap<>();
					if (slt.getStudentId() != null) {
						map.put("student_id", slt.getStudentId());
					}

					if (slt.getStudentName() != null) {
						map.put("student_name", slt.getStudentName());
					}

					if (slt.getRegistartionNumber() != null) {
						map.put("student_reg_no", slt.getRegistartionNumber());
					}

					if (slt.getGrade() != null) {
						map.put("student_grade", slt.getGrade());
					}

					if (slt.getStudentClass() != null) {
						map.put("student_class", slt.getStudentClass());
					}

					if (slt.getRollNo() != null) {
						map.put("student_rollno", slt.getRollNo());
					}

					if (slt.getDeviceUuid() != null) {
						map.put("student_uuid", slt.getDeviceUuid());
					}

					if (slt.getEmergency_contact() != null) {
						map.put("student_emergency_contact", slt.getEmergency_contact());
					}
					jsonString = JSONObject.toJSONString(map);
					myList.add(jsonString);
				}
				responseJSON = ErrorCodesUtil.displayJSONForStudentsList("", "", myList.toString());
				// JSONUtility.respondAsJSON(response, responseJSON);
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.UnauthorisedUserErrorCode,
						Constant.UnauthorisedUserErrorCodeDescription, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				//return responseJSON;
			}
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.StatusCodeJon, Constant.ErrorMessageJon, type);
			//return responseJSON;
		}
		return responseJSON;
	}

	@RequestMapping(value = "/web/RewardRanking/{sessionID}/{startdate}/{enddate}/{grade}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String RewardRanking(@PathVariable String sessionID, @PathVariable String startdate,
			@PathVariable String enddate, @PathVariable String grade) throws Exception {

		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;

		String type = "students.RewardRanking";
		Map<Object, Object> outerMap = null;
		Map<String, Object> studentRewardRankingMap = null;

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("school_admin")) {
					int school_id = user.getAccountId();
					outerMap = new LinkedHashMap<>();

					studentRewardRankingMap = studentsService.getStudentRewardRankings(school_id, startdate, enddate,
							grade);
					outerMap.put("rewards", studentRewardRankingMap.get("results"));

					responseJSON = ErrorCodesUtil.displaySchoolAdminData("API Request Success", "SUC01", type,
							JSONObject.toJSONString(outerMap));

					log.info(type + "*** JSON***" + "\t" + responseJSON);
					/*if (!StringUtility.isNull(request.getHeader("allowCacheContent"))
							&& request.getHeader("allowCacheContent").equals("1")) {
						JSONUtility.respondAsJSON(response, responseJSON, 720);
					} else {
						JSONUtility.respondAsJSON(response, responseJSON);
					}*/
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/web/StudentRewards/{sessionID}/{student_id}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String StudentRewards(@PathVariable String sessionID, @PathVariable int student_id) throws Exception {

		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;

		String type = "students.StudentRewards";
		Map<Object, Object> outerMap = null;
		Map<String, Object> studentRewardsMap = null;

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		boolean kidExistForParent = this.parentService.findKidForParent(user.getId(), student_id);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("parent_admin") && kidExistForParent) {
					outerMap = new LinkedHashMap<>();

					studentRewardsMap = studentsService.getStudentRewards(student_id);
					outerMap.put("rewards", studentRewardsMap.get("results"));

					responseJSON = ErrorCodesUtil.displaySchoolAdminData("SUC01", "API Request Success", type,
							JSONObject.toJSONString(outerMap));

					log.info(type + "*** JSON***" + "\t" + responseJSON);
					/*if (!StringUtility.isNull(request.getHeader("allowCacheContent"))
							&& request.getHeader("allowCacheContent").equals("1")) {
						JSONUtility.respondAsJSON(response, responseJSON, 720);
					} else {
						JSONUtility.respondAsJSON(response, responseJSON);
					}*/
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

}
