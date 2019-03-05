package com.liteon.icgwearable.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.liteon.icgwearable.hibernate.entity.Announcement;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.AnnouncementModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.modelentity.AnnouncementModelEntity;
import com.liteon.icgwearable.service.AnnouncementService;
import com.liteon.icgwearable.service.ClassGradeService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.AnnouncementTransform;
import com.liteon.icgwearable.transform.ClassGradeTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.validator.AnnouncementValidator;

@RestController
public class AnnouncementController {
	private static Logger log = Logger.getLogger(AnnouncementController.class);

	@Autowired
	private AnnouncementService announcementService;
	@Resource(name = "configProperties")
	private Properties configProperties;
	@Autowired
	private AnnouncementValidator announcementValidator;
	@Autowired
	private UserService userService;
	@Autowired
	private AnnouncementModelEntity announcementModelEntity;
	@Autowired
	private HttpSession httpSession;
	@Autowired
	HttpServletResponse response;
	@Value("${db.dateTime}")
	private String dbDateTime;
	@Value("${display.dateTime}")
	private String sourceDateFormat;

	@Autowired
	private ClassGradeService classGradeService;

	@RequestMapping(value = "/editDeleteannouncement")
	public ModelAndView listOfAnnouncement(ModelAndView model, @RequestParam("token") String sessionID) {
		log.debug("List Of Announcement.");

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

		List<Announcement> listOfAnnouncement = this.announcementService
				.listOfAnnouncement(user.getAccountId());
		model.addObject("listOfAnnouncement", listOfAnnouncement);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.setViewName("editdeleteannouncement");
		model.addObject("dateTime", this.configProperties.getProperty("display.dateFormat"));
		return model;
	}

	@RequestMapping(value = "/editAnnouncement")
	public ModelAndView editAnnouncement(HttpServletRequest request, @RequestParam("token") String sessionID) {
		int announcementId = Integer.parseInt(request.getParameter("announcementId"));
		Announcement announcement = this.announcementService.getAnnouncement(announcementId);
		AnnouncementModel announcementModel = new AnnouncementModel();
		announcementModel.setAnnouncementId(announcementId);
		announcementModel.setName(announcement.getName());

		Users user = this.userService.validateUserBySession(sessionID);
		List<ClassGradeTransform> cgt = this.classGradeService.getStudentsClass(user.getId());
		List<String> classList = new ArrayList<>();

		for (ClassGradeTransform cgTransform : cgt) {
			classList.add(String.valueOf(cgTransform.getStudentClass()));
		}

		ModelAndView model = new ModelAndView("addannouncement");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("cgList", classList);
		model.addObject("announcement", announcementModel);
		return model;
	}

	@RequestMapping(value = "/deleteannouncement")
	public ModelAndView deleteAnnouncement(HttpServletRequest request, RedirectAttributes redirectAttributes,
			@RequestParam("token") String sessionID) {
		ModelAndView model = new ModelAndView();
		int announcementId = Integer.parseInt(request.getParameter("announcementId"));
		this.announcementService.deleteAnnouncement(announcementId);
		redirectAttributes.addFlashAttribute("delete_msg", "Announcement Deleted Successfully");

		model.addObject("sessionID", sessionID);
		model.setViewName("redirect:/editDeleteannouncement?token=" + sessionID);
		return model;
	}

	@RequestMapping(value = "/saveAnnouncement", method = RequestMethod.POST)
	public ModelAndView saveAnnouncement(@ModelAttribute("announcementModel") AnnouncementModel announcement,
			HttpServletRequest request, BindingResult result, RedirectAttributes redirectAttributes,
			@RequestParam("token") String sessionID) {

		ModelAndView model = new ModelAndView();

		announcementValidator.validate(announcement, result);

		log.info("announcement getting saved :::::::::::::::::::::::::::::::::::::::::::::::: ");

		String action = "Added";

		Users users = this.userService.validateUserBySession(sessionID);
		int userId = users.getId();
		String annId = request.getParameter("announcementId");
		log.info("announcementId" + "\t" + annId);
		UsersModel userModel = (UsersModel) this.httpSession.getAttribute("userModel");
		userModel.setId(userId);
		announcement.setUserId(userModel.getId());
		announcement.setSchoolId(users.getAccountId());

		log.info("schoolAnnouncementTitle from request :::::::::::::::::::::  "
				+ request.getParameter("schoolAnnouncementTitle"));
		log.info("schoolAnnouncementDescription from request :::::::::::::::::::::  "
				+ request.getParameter("schoolAnnouncementDescription"));

		if (request.getParameter("schoolAnnouncementTitle").trim().equals("")
				|| request.getParameter("schoolAnnouncementDescription").trim().equals("")) {
			log.info("titile is empty :::::::::: returning >>>>>>>>>>>>>>>>>>>>>>>>");
			List<Announcement> listOfAnnouncement = this.announcementService
					.listOfAnnouncement(users.getAccountId());
			model.addObject("listOfAnnouncement", listOfAnnouncement);
			model.addObject("sessionID", sessionID);
			model.setViewName("editdeleteannouncement");
			model.addObject("dateTime", this.configProperties.getProperty("display.dateFormat"));
			model.addObject("errorMessage", "Title or description cannot be empty");
			return model;
		}
		announcement.setName(request.getParameter("schoolAnnouncementTitle"));
		announcement.setDescription(request.getParameter("schoolAnnouncementDescription"));

		Announcement announcementEntity = this.announcementModelEntity.addAnnouncement(announcement, action,
				sourceDateFormat);
		this.announcementService.addAnnouncement(announcementEntity);
		model.addObject("add_announcement_msg",
				"Announcement " + announcement.getName() + " " + action + " Successfully");

		List<Announcement> listOfAnnouncement = this.announcementService
				.listOfAnnouncement(users.getAccountId());
		model.addObject("listOfAnnouncement", listOfAnnouncement);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, users.getName());
		model.setViewName("editdeleteannouncement");
		log.info("redirecting the url to edit delete announcement ");
		model.addObject("dateTime", this.configProperties.getProperty("display.dateFormat"));
		model.addObject("successMessage", "Added Announcement Successfully");
		return model;
	}

	@RequestMapping(value = "/addAnnouncement")
	public ModelAndView addAnnouncement(ModelAndView model, HttpServletRequest request,
			@RequestParam("token") String sessionID) {
		AnnouncementModel announcement = new AnnouncementModel();
		Users user = this.userService.validateUserBySession(sessionID);
		List<ClassGradeTransform> cgt = this.classGradeService.getStudentsClass(user.getId());
		List<String> classList = new ArrayList<>();

		for (ClassGradeTransform cgTransform : cgt) {
			classList.add(String.valueOf(cgTransform.getStudentClass()));
		}

		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("announcement", announcement);
		model.addObject("cgList", classList);
		model.setViewName("addannouncement");
		return model;
	}

	private ModelAndView saveOrUpdate(HttpServletRequest request, AnnouncementModel announcement, ModelAndView model,
			String action, @RequestParam("token") String sessionID) {
		// int userId = (int) request.getSession(false).getAttribute("userId");
		Users users = this.userService.validateUserBySession(sessionID);
		int userId = users.getId();
		String annId = request.getParameter("announcementId");
		log.info("announcementId" + "\t" + annId);
		UsersModel userModel = (UsersModel) this.httpSession.getAttribute("userModel");
		userModel.setId(userId);
		announcement.setUserId(userModel.getId());
		announcement.setSchoolId(users.getAccountId());

		log.info("schoolAnnouncementTitle from request :::::::::::::::::::::  "
				+ request.getParameter("schoolAnnouncementTitle"));
		log.info("schoolAnnouncementDescription from request :::::::::::::::::::::  "
				+ request.getParameter("schoolAnnouncementDescription"));

		if (request.getParameter("schoolAnnouncementTitle").trim().equals("")
				|| request.getParameter("schoolAnnouncementDescription").trim().equals("")) {
			log.info("titile is empty :::::::::: returning >>>>>>>>>>>>>>>>>>>>>>>>");
			;
			List<Announcement> listOfAnnouncement = this.announcementService
					.listOfAnnouncement(users.getAccountId());
			model.addObject("listOfAnnouncement", listOfAnnouncement);
			model.addObject("sessionID", sessionID);
			model.setViewName("editdeleteannouncement");
			model.addObject("dateTime", this.configProperties.getProperty("display.dateFormat"));
			model.addObject("errorMessage", "Title or description cannot be empty");
			return model;
		}
		announcement.setName(request.getParameter("schoolAnnouncementTitle"));
		announcement.setDescription(request.getParameter("schoolAnnouncementDescription"));

		Announcement announcementEntity = this.announcementModelEntity.addAnnouncement(announcement, action,
				sourceDateFormat);
		this.announcementService.addAnnouncement(announcementEntity);
		model.addObject("add_announcement_msg",
				"Announcement " + announcement.getName() + " " + action + " Successfully");

		List<Announcement> listOfAnnouncement = this.announcementService
				.listOfAnnouncement(users.getAccountId());
		model.addObject("listOfAnnouncement", listOfAnnouncement);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, users.getName());
		model.setViewName("redirect:/editDeleteannouncement?token=" + sessionID);
		model.addObject("dateTime", this.configProperties.getProperty("display.dateFormat"));
		model.addObject("successMessage", "Added Announcement Successfully");
		return model;
	}

	@RequestMapping(value = "/mobile/AnnouncementsList/{session_id}/{student_id}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String AnnouncementsList(@PathVariable("session_id") String session_id,
			@PathVariable("student_id") int student_id) {

		String statusCode = null;
		String errorMessage = null;
		String responseJSON = null;
		String type = "announcement.AnnouncementsList";
		Users user = userService.validateUserBySession(session_id);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(Constant.ErrorMessageValidity,
						Constant.StatusCodeValidity, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			if (!((user.getRoleType().equals("parent_admin")))) {
				statusCode = "ERR03";
				errorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
				return responseJSON;
			}

			if (!this.announcementService.checkStudentIdExist(student_id, user.getId())) {
				statusCode = "ERR05";
				errorMessage = "StudentId is invalid";
				responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
				return responseJSON;
			}

			String name = null;
			String desc = null;
			Timestamp createdDate = null;
			String updatedDate = null;
			String jsonString = null;
			List myList = new ArrayList<>();
			JSONArray jsonArray = new JSONArray();
			String jsonArrayString = null;

			log.info("token id from rest call " + this.httpSession.getId());
			List<AnnouncementTransform> announcementList = this.announcementService
					.getAnnouncementsForParent(student_id, user.getId());

			for (AnnouncementTransform annTransform : announcementList) {

				name = annTransform.getAnnouncement_title();
				Map<Object, Object> map = new LinkedHashMap<>();
				map.put("name", name);

				if (annTransform.getAnnouncement_description() != null) {
					desc = annTransform.getAnnouncement_description();
					map.put("description", desc);
				}
				if (annTransform.getDisplayDate() != null) {
					updatedDate = annTransform.getDisplayDate();
					map.put("updatedDate", updatedDate);
				} else {
					if (annTransform.getCreatedDate() != null) {
						createdDate = annTransform.getCreatedDate();
						map.put("createdDate", createdDate.toString());
					}
				}

				jsonString = JSONObject.toJSONString(map);
				myList.add(jsonString);
				log.info("My List Contents::::::::" + myList);

			}
			jsonArrayString = jsonArray.toString();
			log.info("jsonArrayString : " + "\n" + jsonArrayString);
			statusCode = "SUC01";
			errorMessage = "API Request Success";
			responseJSON = ErrorCodesUtil.displaySuccessJSONForAnnouncement(errorMessage, statusCode,
					myList.toString());

			return responseJSON;

		} else {
			statusCode = "ERR01";
			errorMessage = "Invalid user, failed to authenticate";
			responseJSON = ErrorCodesUtil.displayErroJSONForAnnouncement(errorMessage, statusCode);
			return responseJSON;
		}
	}

	@RequestMapping(value = "/updateAnnouncement/{session_id}", method = RequestMethod.POST)
	public String updateAnnouncement(@PathVariable("session_id") String sessionID,
			@RequestBody AnnouncementModel announcement) {

		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "student.updateStudentApi";
		String respondJson = null;
		String errorMessage = null;
		String msg = null;
		String responseJSON = null;

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
		}

		this.announcementService.updateAnnouncement(announcement.getAnnouncementId(), announcement.getName(),
				announcement.getDescription());

		respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		this.httpSession.setAttribute("updateMsg", "icglabel_announcement_updsuccess");
		return respondJson;

	}

	@RequestMapping(value = "/deleteAnnouncement", method = RequestMethod.POST)
	public String deleteAnnouncement(@RequestBody AnnouncementModel announcement) {
		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "student.deleteAnnouncement";
		String respondJson = null;

		this.announcementService.deleteAnnouncement(announcement.getAnnouncementId());

		respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		this.httpSession.setAttribute("deleteMsg", "icglabel_announcement_delsuccess");
		return respondJson;

	}

	@RequestMapping(value = "/addAnnouncement/{session_id}", method = RequestMethod.POST)
	public String addAnnouncement(@PathVariable("session_id") String sessionID,
			@RequestBody AnnouncementModel announcement) {
		String statusCode = "SUC01";
		String statusMsg = "API Request Success";
		String type = "student.addAnnouncement";
		String respondJson = null;
		String action = "Added";
		String errorMessage = null;
		String msg = null;
		String responseJSON = null;

		Users user = this.userService.validateUserBySession(sessionID);

		if (null != user) {

			String sessionValidityResult = CommonUtil.checkSessionValidity(user);
			if (sessionValidityResult.equals("NOTVALID")) {
				statusCode = "ERR02";
				errorMessage = "icglabel_invalid_token";
				msg = "FAILURE";
				responseJSON = ErrorCodesUtil.displayTokenValidOrInvalid(statusCode, errorMessage, msg);
				return responseJSON;
			}
		}
		log.debug("new Announcement ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");

		/*announcement.setUserId(user.getId());
		announcement.setSchoolId(user.getAccountId());

		Announcement announcementEntity = this.announcementModelEntity.addAnnouncement(announcement, action,
				sourceDateFormat);
		this.announcementService.addAnnouncement(announcementEntity);*/
		this.announcementService.addAnnouncement(user.getAccountId(), announcement.getName(), announcement.getDescription());

		respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
		this.httpSession.setAttribute("addedAnnMsg", "icglabel_announcement_addsuccess");
		return respondJson;
	}
}