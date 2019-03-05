package com.liteon.icgwearable.controller;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Rewards;
import com.liteon.icgwearable.hibernate.entity.RewardsCategory;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.RewardsCategoryModel;
import com.liteon.icgwearable.model.StudentsModel;
import com.liteon.icgwearable.service.RewardsService;
import com.liteon.icgwearable.service.TeacherService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.RewardStatisticsTransform;
import com.liteon.icgwearable.transform.RewardsListTransform;
import com.liteon.icgwearable.transform.RewardsTransform;
import com.liteon.icgwearable.transform.SchoolRewardsListTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.WebUtility;

@RestController
public class RewardsController {

	private static Logger log = Logger.getLogger(RewardsController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private RewardsService rewardsService;

	@Autowired
	private HttpSession httpSession;
	@Autowired
	HttpServletResponse response;
	@Resource(name = "configProperties")
	private Properties configProperties;
	@Autowired
	private TeacherService teacherService;

	WebUtility webUtility = WebUtility.getWebUtility();

	private String methodName;
	private String className;
	private String action;
	private String summary;
	private String ipaddress;

	@RequestMapping(value = "/manageRewardsCategory", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView manageRewardsCategory(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);

		log.info("sessionValidityResult :: " + sessionValidityResult);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		String currentUser = user.getRoleType();
		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		List<SchoolRewardsListTransform> schoolRewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());

		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");

		model.addObject("schoolRewardsList", schoolRewardsListTransform);
		model.addObject("imagePath", imagePath);
		model.setViewName("schoolRewardsList");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;

	}

	@RequestMapping(value = "/addOrEditRewardsCategory", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView addRewardsCategory(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String currentUser = user.getRoleType();
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		log.info("sessionValidityResult :: " + sessionValidityResult);

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

		model.setViewName("newOrEditRewardsCategory");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;

	}

	@RequestMapping(value = "/saveRewardsCategory", method = RequestMethod.POST)
	public ModelAndView saveRewardsCategory(@RequestParam("name") String name, @RequestParam("file") MultipartFile file,
			@RequestParam("token") String sessionID) throws IOException {

		ModelAndView model = new ModelAndView();
		Users user = userService.getUserBySessionId(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
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

		if (name == null || name.trim().isEmpty()) {
			model.addObject("cName", "CategoryName cannot be blank");
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("newOrEditRewardsCategory");
			log.debug("CategoryName cannot be blank");
			return model;
		}

		String fileName = null;
		String extn = null;
		if (file != null) {
			fileName = file.getOriginalFilename();
			extn = fileName.substring(fileName.lastIndexOf(".") + 1);
			log.info("FileName" + "\t" + fileName);
			log.info("extn" + "\t" + extn);
		}

		if (extn.equals("jpg") || extn.equals("jpeg") || extn.equals("png")) {

			long fileSize = (file.getSize() / 1000);
			File f = null;
			if (fileSize <= 150) {

				try {
					f = new File(webUtility.createNewFolder(
							this.configProperties.getProperty("rewards.upload.path") + "/" + fileName));
					boolean flag = f.createNewFile();
					log.info("fileCreated>>Student" + "\t" + flag);
				} catch (Exception ex) {
					log.info("file already exits");
				}

				log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
				RewardsCategoryModel rewardsCategoryModel = new RewardsCategoryModel();
				rewardsCategoryModel.setCategory_icon_url(fileName);
				rewardsCategoryModel.setCategory_name(name);
				Accounts accounts = user.getAccounts();
				log.info("accounts.getAccountId() >>>>>>>>>>>>>>>>> " + accounts.getAccountId());
				this.rewardsService.saveRewardsCategory(rewardsCategoryModel, accounts);

				try {
					file.transferTo(f);
				} catch (Exception ex) {
					log.info("file already exits");
				}
				log.info("redirect to addOrEditRewardsCategory view");
				model.addObject("sessionID", sessionID);
				model.addObject("success", "Rewards Category Successfully Saved.");
				model.setViewName("newOrEditRewardsCategory");
				return model;

			} else {
				model.addObject("wrongExtn",
						"Please Choose a Valid JPG or JPEG or PNG File that must be less than 150KB of size");
				model.addObject("sessionID", sessionID);
				model.setViewName("newOrEditRewardsCategory");
				log.debug("Invalid File :::::::::::");
				return model;
			}

		} else if (name != null && file.isEmpty()) {
			RewardsCategoryModel rewardsCategoryModel = new RewardsCategoryModel();
			rewardsCategoryModel.setCategory_name(name);
			Accounts accounts = user.getAccounts();
			this.rewardsService.saveRewardsCategory(rewardsCategoryModel, accounts);
			model.setViewName("newOrEditRewardsCategory");
			model.addObject("success", "Rewards Category Successfully Saved.");
			model.addObject("sessionID", sessionID);
			return model;
		}

		else {
			model.addObject("wrongExtn",
					"Please Choose a Valid JPG or JPEG or PNG File that must be less than 150KB of size");
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("newOrEditRewardsCategory");
			log.debug("Invalid File :::::::::::");
			return model;
		}
	}

	@RequestMapping(value = "/mobile/RewardsCategoryAdd/{sessionID}/{name}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String RewardsCategoryAdd(HttpServletRequest httpReq, @PathVariable("sessionID") String sessionID,
			@PathVariable String name) {
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "Rewards.RewardsCategoryAdd";
		File f = null;
		if (httpReq.getParameterNames().hasMoreElements()) {
			String element = httpReq.getParameterNames().nextElement();
			log.info("Element" + "\t" + element);
		}

		log.info("httpReq.getRequestURI()" + "\t" + httpReq.getRequestURI());

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

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (name.isEmpty() || name.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.getUserBySessionId(sessionID);
		log.info("logged our session Id" + sessionID);
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
			log.info("accounts.getAccountId() inside >>>>>>>>>>>>>>>>> ");
			Accounts accounts = user.getAccounts();
			log.info("accounts.getAccountId() >>>>>>>>>>>>>>>>> " + accounts.getAccountId());

			if (this.rewardsService.getCategoryByName(name, accounts, null) != null) {
				log.info("inside unique name error  ");
				StatusCode = "ERR03";
				ErrorMessage = "Reward Category already exists ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		}

		String contentType = httpReq.getContentType();
		log.info("contentType in MobileConfigFileUpload" + "\t" + contentType);
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

			String fileName = null;
			String extn = null;
			if (multipartFile != null) {
				fileName = multipartFile.getOriginalFilename();
				extn = fileName.substring(fileName.lastIndexOf(".") + 1);
				log.info("FileName" + "\t" + fileName);
				log.info("extn" + "\t" + extn);
			}

			if (extn.equalsIgnoreCase("png") || extn.equalsIgnoreCase("jpg") || extn.equalsIgnoreCase("jpeg")) {

				long fileSize = multipartFile.getSize();
				if (fileSize >= 1) {

					try {
						Accounts accounts = user.getAccounts();
						SimpleDateFormat currenttimestamp = new SimpleDateFormat("yyyyMMddHHmmss");
						log.info("Timestamp added" + "\t" + currenttimestamp.format(new Date()));
						String fileName1 = accounts.getAccountId() + "_" + currenttimestamp.format(new Date()) + ".png";
						fileName1 = fileName1.replaceAll(" ", "_").toLowerCase();
						log.info("fileName Created" + "\t" + fileName1);
						f = new File(webUtility.createNewFolder(
								this.configProperties.getProperty("rewards.upload.path") + "/" + fileName1));
						boolean flag = f.createNewFile();
						log.info("fileCreated>>Student" + "\t" + flag);

						log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
						RewardsCategoryModel rewardsCategoryModel = new RewardsCategoryModel();
						rewardsCategoryModel.setCategory_icon_url(fileName1);
						rewardsCategoryModel.setCategory_name(name);

						log.info("accounts.getAccountId() >>>>>>>>>>>>>>>>> " + accounts.getAccountId());
						this.rewardsService.saveRewardsCategory(rewardsCategoryModel, accounts);
						multipartFile.transferTo(f);
					} catch (Exception ex) {
						log.info("file already exists");
						ErrorMessage = "File Already Exists";
						StatusCode = "ERR04";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						//JSONUtility.respondAsJSON(response, responseJSON);
					}
					log.info("redirect to addOrEditRewardsCategory view");

					String statusCode = "SUC01";
					String errorMessage = "API Request Success";
					responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				} else {
					ErrorMessage = "Input is Empty";
					StatusCode = "ERR04";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				String statusCode = "ERR19";
				String errorMessage = "File not supported, Please upload png file type only ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				//return responseJSON;
			}
		} else {

			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/RewardsCategoryInAdd/{sessionID}/{rewardID}/{name}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String RewardsCategoryInAdd(HttpServletRequest httpReq, @PathVariable("sessionID") String sessionID,
			@PathVariable String rewardID, @PathVariable String name) {
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "Rewards.RewardsCategoryInAdd";
		File f = null;
		if (httpReq.getParameterNames().hasMoreElements()) {
			String element = httpReq.getParameterNames().nextElement();
			log.info("Element" + "\t" + element);
		}

		log.info("httpReq.getRequestURI()" + "\t" + httpReq.getRequestURI());

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

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (name.isEmpty() || name.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

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
			log.info("accounts.getAccountId() inside unique check >>>>>>>>>>>>>>>>> ");

			if (this.rewardsService.getRewardsByName(name, Integer.valueOf(rewardID), null) != null) {
				log.info("inside unique name error  ");
				StatusCode = "ERR03";
				ErrorMessage = "Reward already exists ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		}

		String contentType = httpReq.getContentType();
		log.info("contentType in MobileConfigFileUpload" + "\t" + contentType);
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

			String fileName = null;
			String extn = null;
			if (multipartFile != null) {
				fileName = multipartFile.getOriginalFilename();
				extn = fileName.substring(fileName.lastIndexOf(".") + 1);
				log.info("FileName" + "\t" + fileName);
				log.info("extn" + "\t" + extn);
			}

			if (extn.equalsIgnoreCase("png") || extn.equalsIgnoreCase("jpg") || extn.equalsIgnoreCase("jpeg")) {

				long fileSize = multipartFile.getSize();
				if (fileSize >= 1) {

					try {
						SimpleDateFormat currenttimestamp = new SimpleDateFormat("yyyyMMddHHmmss");
						log.info("Timestamp added" + "\t" + currenttimestamp.format(new Date()));
						String catName = this.rewardsService.getRewardsCategoryByRId(Integer.valueOf(rewardID))
								.getCategory_name();
						String fileName1 = user.getAccountId() + "_" + currenttimestamp.format(new Date()) + ".png";
						fileName1 = fileName1.replaceAll(" ", "_").toLowerCase();
						log.info("fileName Created" + "\t" + fileName1);
						f = new File(webUtility.createNewFolder(
								this.configProperties.getProperty("rewards.upload.path") + "/" + fileName1));
						boolean flag = f.createNewFile();
						log.info("fileCreated>>Student" + "\t" + flag);

						log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
						RewardsListTransform rewardsListTransform = new RewardsListTransform();
						rewardsListTransform.setRewardName(name);

						rewardsListTransform.setIcon(fileName1);
						rewardsListTransform.setUniqueCategory(rewardID);
						rewardsListTransform.setRewardsCategoryId(Integer.valueOf(rewardID));

						this.rewardsService.addNewRewards(rewardsListTransform, user);
						multipartFile.transferTo(f);
					} catch (Exception ex) {
						log.info(ex.toString());
						log.info("file already exists");
						ErrorMessage = "File Already Exists";
						StatusCode = "ERR04";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						//JSONUtility.respondAsJSON(response, responseJSON);
					}
					log.info("redirect to addOrEditRewardsCategory view");

					String statusCode = "SUC01";
					String errorMessage = "API Request Success";
					responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				} else {

					ErrorMessage = "Input is Empty";
					StatusCode = "ERR04";
					responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				String statusCode = "ERR19";
				String errorMessage = "File not supported, Please upload png file type only ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				//return responseJSON;
			}
		} else {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/RewardsCategoryEdit/{sessionID}/{rewardID}/{name}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String RewardsCategoryEdit(HttpServletRequest httpReq, @PathVariable("sessionID") String sessionID,
			@PathVariable String rewardID, @PathVariable String name) {
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "Rewards.RewardsCategoryEdit";
		File f = null;
		if (httpReq.getParameterNames().hasMoreElements()) {
			String element = httpReq.getParameterNames().nextElement();
			log.info("Element" + "\t" + element);
		}

		log.info("httpReq.getRequestURI()" + "\t" + httpReq.getRequestURI());

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

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (name.isEmpty() || name.equals(null))
				|| (rewardID.isEmpty() || rewardID.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.getUserBySessionId(sessionID);
		log.info("logged our session Id" + sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			if (this.rewardsService.getCategoryByName(name, user.getAccounts(), rewardID) != null) {
				log.info("inside unique name error  ");
				StatusCode = "ERR03";
				ErrorMessage = "Reward Category already exists ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		}

		String contentType = httpReq.getContentType();
		log.info("contentType in MobileConfigFileUpload" + "\t" + contentType);
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
		try {
			if (null != multipartFile && !(multipartFile.isEmpty())) {

				String fileName = null;
				String extn = null;
				if (multipartFile != null) {
					fileName = multipartFile.getOriginalFilename();
					extn = fileName.substring(fileName.lastIndexOf(".") + 1);
					log.info("FileName" + "\t" + fileName);
					log.info("extn" + "\t" + extn);
				}

				if (extn.equalsIgnoreCase("png") || extn.equalsIgnoreCase("jpg") || extn.equalsIgnoreCase("jpeg")) {

					long fileSize = multipartFile.getSize();
					if (fileSize >= 1) {
						RewardsCategory localRewardCategory = this.rewardsService
								.getRewardsCategoryByRId(Integer.parseInt(rewardID));
						if (localRewardCategory != null) {
							String filetodelete = localRewardCategory.getCategory_icon_url();
							String full_path = this.configProperties.getProperty("rewards.upload.path") + "/"
									+ filetodelete;
							log.info("fullpath for file delte  " + full_path);

							Files.deleteIfExists(Paths.get(full_path));
							log.info("Old file got successfully deleted");

						}

						Accounts accounts = user.getAccounts();
						SimpleDateFormat currenttimestamp = new SimpleDateFormat("yyyyMMddHHmmss");
						log.info("Timestamp added" + "\t" + currenttimestamp.format(new Date()));
						String fileName1 = accounts.getAccountId() + "_" + currenttimestamp.format(new Date()) + ".png";
						fileName1 = fileName1.replaceAll(" ", "_").toLowerCase();
						log.info("fileName Created" + "\t" + fileName1);
						f = new File(webUtility.createNewFolder(
								this.configProperties.getProperty("rewards.upload.path") + "/" + fileName1));
						boolean flag = f.createNewFile();
						log.info("fileCreated>>Student" + "\t" + flag);

						log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
						log.info("accounts.getAccountId() >>>>>>>>>>>>>>>>> " + accounts.getAccountId());
						multipartFile.transferTo(f);
						log.info("Integer.parseInt(rewardID) " + Integer.parseInt(rewardID));
						this.rewardsService.updateRewardsCategory(Integer.parseInt(rewardID), name, fileName1);

						log.info("redirect to addOrEditRewardsCategory view");

						String statusCode = "SUC01";
						String errorMessage = "API Request Success";
						responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
						//JSONUtility.respondAsJSON(response, responseJSON);
					} else {
						ErrorMessage = "Input is Empty";
						StatusCode = "ERR04";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						//JSONUtility.respondAsJSON(response, responseJSON);
					}
				} else {
					String statusCode = "ERR19";
					String errorMessage = "File not supported, Please upload png file type only ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					//return responseJSON;
				}
			} else {
				this.rewardsService.updateRewardsCategory(Integer.parseInt(rewardID), name, "none");
				log.info("edit without file  change");

				String statusCode = "SUC01";
				String errorMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} catch (NoSuchFileException e) {
			log.info("No such file/directory exists");
			ErrorMessage = "File Deletion Failed, No such file/directory exists";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		} catch (DirectoryNotEmptyException e) {
			log.info("Directory is not empty.");
			ErrorMessage = "File Deletion Failed, Directory is not empty.";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		} catch (IOException e) {
			log.info("Invalid permissions.");
			ErrorMessage = "File Deletion Failed, Invalid permissions.";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		} catch (Exception ex) {
			log.info(ex.toString());
			log.info("file Upload exists");
			ErrorMessage = "File Upload failed";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/RewardsCategoryInEdit/{sessionID}/{rewardCID}/{rewardID}/{name}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String RewardsCategoryInEdit(HttpServletRequest httpReq, @PathVariable("sessionID") String sessionID,
			@PathVariable String rewardCID, @PathVariable String rewardID, @PathVariable String name) {
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String type = "Rewards.RewardsCategoryInEdit";
		File f = null;
		if (httpReq.getParameterNames().hasMoreElements()) {
			String element = httpReq.getParameterNames().nextElement();
			log.info("Element" + "\t" + element);
		}

		log.info("httpReq.getRequestURI()" + "\t" + httpReq.getRequestURI());

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

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (name.isEmpty() || name.equals(null))
				|| (rewardCID.isEmpty() || rewardCID.equals(null)) || (rewardID.isEmpty() || rewardID.equals(null))) {
			ErrorMessage = "Input is Empty";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);
		log.info("logged our session Id" + sessionID);
		if (null != user) {
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			log.info("accounts.getAccountId() inside unique check >>>>>>>>>>>>>>>>> ");

			log.info("Inside RewardsCategoryInEdit :>>>>>>>");
			log.info("rewardCID ::: " + rewardCID);
			if (this.rewardsService.getRewardsByName(name, Integer.valueOf(rewardCID),
					Integer.valueOf(rewardID)) != null) {
				log.info("inside unique name error :::: ");
				StatusCode = "ERR03";
				ErrorMessage = "Reward already exists ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
		}

		String contentType = httpReq.getContentType();
		log.info("contentType in MobileConfigFileUpload" + "\t" + contentType);
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

		try {
			RewardsListTransform rewardsListTransform = this.rewardsService
					.getRewardsInfoBasedOnRewardId(Integer.parseInt(rewardID));
			if (null != multipartFile && !(multipartFile.isEmpty())) {

				String fileName = null;
				String extn = null;
				if (multipartFile != null) {
					fileName = multipartFile.getOriginalFilename();
					extn = fileName.substring(fileName.lastIndexOf(".") + 1);
					log.info("FileName" + "\t" + fileName);
					log.info("extn" + "\t" + extn);
				}

				if (rewardsListTransform != null && (extn.equalsIgnoreCase("png") || extn.equalsIgnoreCase("jpg")
						|| extn.equalsIgnoreCase("jpeg"))) {

					long fileSize = multipartFile.getSize();
					if (fileSize >= 1) {
						Rewards localReward = this.rewardsService.getRewardsById(Integer.parseInt(rewardID));
						if (localReward != null) {
							String filetodelete = localReward.getReward_icon_url();
							String full_path = this.configProperties.getProperty("rewards.upload.path") + "/"
									+ filetodelete;
							log.info("fullpath for file delte  " + full_path);

							Files.deleteIfExists(Paths.get(full_path));
							log.info("Old Reward icon file got successfully deleted");
						}

						SimpleDateFormat currenttimestamp = new SimpleDateFormat("yyyyMMddHHmmss");
						log.info("Timestamp added" + "\t" + currenttimestamp.format(new Date()));
						String fileName1 = user.getAccountId() + "_" + currenttimestamp.format(new Date()) + ".png";
						fileName1 = fileName1.replaceAll(" ", "_").toLowerCase();
						log.info("fileName Created" + "\t" + fileName1);
						f = new File(webUtility.createNewFolder(
								this.configProperties.getProperty("rewards.upload.path") + "/" + fileName1));
						boolean flag = f.createNewFile();
						log.info("fileCreated>>Student" + "\t" + flag);

						log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
						rewardsListTransform.setRewardsCategoryId(Integer.parseInt(rewardCID));
						rewardsListTransform.setRewardName(name);
						multipartFile.transferTo(f);
						log.info("Integer.parseInt(rewardID) " + Integer.parseInt(rewardCID));
						this.rewardsService.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, fileName1);

						log.info("redirect to addOrEditRewardsCategory view");

						String statusCode = "SUC01";
						String errorMessage = "API Request Success";
						responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
						//JSONUtility.respondAsJSON(response, responseJSON);
					} else {
						ErrorMessage = "Input is Empty";
						StatusCode = "ERR04";
						responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
						//JSONUtility.respondAsJSON(response, responseJSON);
					}
				} else {
					String statusCode = "ERR19";
					String errorMessage = "File not supported, Please upload png file type only ";
					responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
					//JSONUtility.respondAsJSON(response, responseJSON);
					//return responseJSON;
				}
			} else {
				rewardsListTransform.setRewardsCategoryId(Integer.parseInt(rewardCID));
				rewardsListTransform.setRewardName(name);
				log.info("accounts.getAccountId() >>>>>>>>>>>>>>>>> " + rewardsListTransform.toString());
				this.rewardsService.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, "none");
				log.info("edit without file  change");

				String statusCode = "SUC01";
				String errorMessage = "API Request Success";
				responseJSON = ErrorCodesUtil.displayErrorJSON(errorMessage, statusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
			}
		} catch (NoSuchFileException e) {
			log.info("No such file/directory exists");
			ErrorMessage = "File Deletion Failed, No such file/directory exists";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		} catch (DirectoryNotEmptyException e) {
			log.info("Directory is not empty.");
			ErrorMessage = "File Deletion Failed, Directory is not empty.";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		} catch (IOException e) {
			log.info("Invalid permissions.");
			ErrorMessage = "File Deletion Failed, Invalid permissions.";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		} catch (Exception ex) {
			log.info(ex.toString());
			log.info("File Upload failed");
			ErrorMessage = "File Upload failed";
			StatusCode = "ERR04";
			responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
			//JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/mobile/RewardsCategoryDelete/{token}/{rewardCId}")
	public String RewardsCategoryDelete(@PathVariable("token") String sessionID,
			@PathVariable("rewardCId") String rewardCId) {
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

		String statusCode = null;
		String statusMsg = null;
		String respondJson = null;
		String type = "Rewards.RewardsCategoryDelete";

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (rewardCId.isEmpty() || rewardCId.equals(null))) {
			statusMsg = "Input is Empty";
			statusCode = "ERR04";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			return respondJson;
		}
		Users user = userService.validateUserBySession(sessionID);
		try {
			if (null != user) {

				if (user.getRoleType().equals(Constant.SchoolAdmin)) {
					String sessionValidityResult = CommonUtil.checkSessionValidity(user);

					if (sessionValidityResult.equals("NOTVALID")) {
						statusMsg = "ERR02";
						statusCode = "Session Expired ,Please Relogin ";
						//respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
						return respondJson;
					}

					List<Integer> rewardIds = this.rewardsService
							.getRewardIdFromRewardCategoryId(Integer.parseInt(rewardCId));
					for (Integer rewardId : rewardIds) {
						Rewards localReward = this.rewardsService.getRewardsById(rewardId);
						if (localReward != null) {
							String filetodelete = localReward.getReward_icon_url();
							String full_path = this.configProperties.getProperty("rewards.upload.path") + "/"
									+ filetodelete;
							log.info("fullpath for file delte  " + full_path);

							Files.deleteIfExists(Paths.get(full_path));
							log.info("Old Reward icon file got successfully deleted");

						}
						this.rewardsService.deleteRewardsFromRewardsStudent(rewardId);
					}
					this.rewardsService.deleteRewardsFromRewards(Integer.parseInt(rewardCId));
					RewardsCategory localRewardCategory = this.rewardsService
							.getRewardsCategoryByRId(Integer.parseInt(rewardCId));
					if (localRewardCategory != null) {
						String filetodelete = localRewardCategory.getCategory_icon_url();
						String full_path = this.configProperties.getProperty("rewards.upload.path") + "/"
								+ filetodelete;
						log.info("fullpath for file delte  " + full_path);

						Files.deleteIfExists(Paths.get(full_path));
						log.info("Old file got successfully deleted");

					}
					this.rewardsService.deleteRewardsFromRewardsCategory(Integer.parseInt(rewardCId));

					statusCode = "SUC01";
					statusMsg = "API Request Success";
					respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
					//return respondJson;
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
					//return respondJson;
				}
			} else {
				statusCode = "ERR01";
				statusMsg = "Invalid User";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
				//return respondJson;
			}
		} catch (Exception ex) {
			log.info(ex.toString());
			log.info("file already exists/Existing File Delete Failed");
			statusMsg = "File Already Exists/Existing File Delete Failed";
			statusCode = "ERR04";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
		}
		
		return respondJson;
	}

	@RequestMapping(value = "/mobile/RewardsCategoryInDelete/{token}/{rewardCId}/{rewardID}")
	public String RewardsCategoryInDelete(@PathVariable("token") String sessionID,
			@PathVariable("rewardCId") String rewardCId, @PathVariable("rewardID") String rewardID) {
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

		String statusCode = null;
		String statusMsg = null;
		String respondJson = null;
		String type = "Rewards.RewardsCategoryInDelete";

		if ((sessionID.isEmpty() || sessionID.equals(null)) || (rewardCId.isEmpty() || rewardCId.equals(null))
				|| (rewardID.isEmpty() || rewardID.equals(null))) {
			statusMsg = "Input is Empty";
			statusCode = "ERR04";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
			return respondJson;
		}
		Users user = userService.validateUserBySession(sessionID);
		try {
			if (null != user) {

				if (user.getRoleType().equals(Constant.SchoolAdmin)) {
					String sessionValidityResult = CommonUtil.checkSessionValidity(user);
					if (sessionValidityResult.equals("NOTVALID")) {
						statusMsg = "ERR02";
						statusCode = "Session Expired ,Please Relogin ";
						respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
						return respondJson;
					}

					// ActivityLog activityLog =
					// CommonUtil.formulateActivityLogs(sessionUser, action,
					// summary, ipaddress);
					log.info("rewardID data " + rewardID);
					log.info("rewardCId data " + rewardCId);
					Rewards localReward = this.rewardsService.getRewardsById(Integer.parseInt(rewardID));
					if (localReward != null) {
						String filetodelete = localReward.getReward_icon_url();
						String full_path = this.configProperties.getProperty("rewards.upload.path") + "/"
								+ filetodelete;
						log.info("fullpath for file delte  " + full_path);

						Files.deleteIfExists(Paths.get(full_path));
						log.info("Old Reward icon file got successfully deleted");

					}
					this.rewardsService.deleteRewardsFromRewardsAndRewardsStudents(Integer.parseInt(rewardID),
							Integer.parseInt(rewardCId));

					statusCode = "SUC01";
					statusMsg = "API Request Success";
					respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
					//return respondJson;
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
					//JSONUtility.respondAsJSON(response, respondJson);
					//return respondJson;
				}
			} else {
				statusCode = "ERR01";
				statusMsg = "Invalid User";
				respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
				//JSONUtility.respondAsJSON(response, respondJson);
				//return respondJson;
			}
		} catch (Exception ex) {
			log.info(ex.toString());
			log.info("file already exists/Existing File Delete Failed");
			statusMsg = "File already exists/Existing File Delete Failed";
			statusCode = "ERR04";
			respondJson = ErrorCodesUtil.displayErrorJSON(statusMsg, statusCode, type);
			//JSONUtility.respondAsJSON(response, respondJson);
		}
		return respondJson;
	}

	@RequestMapping(value = "/editRewardsCategory", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView editRewardsCategory(@ModelAttribute("rewardsModel") RewardsCategoryModel rewardsCategoryModel,
			@RequestParam("rewardId") String rewardId, @RequestParam("token") String sessionID) throws IOException {

		ModelAndView model = new ModelAndView();
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		log.info("sessionValidityResult :: " + sessionValidityResult);
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
		RewardsCategory rewardsCategory = this.rewardsService.getRewardsCategoryByRId(Integer.parseInt(rewardId));

		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");

		String imageName = rewardsCategory.getCategory_icon_url();
		String cName = rewardsCategory.getCategory_name();
		model.addObject("imageName", imageName);
		model.addObject("imagePath", imagePath);
		model.addObject("cName", cName);
		model.addObject("rewardId", rewardId);
		model.setViewName("editRewardsCategory");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;
	}

	@RequestMapping(value = "/updateRewardsCategory", method = RequestMethod.POST)
	public ModelAndView RewardsCategory(@RequestParam("name") String categoryName,
			@RequestParam("file") MultipartFile newFile, @RequestParam("token") String sessionID,
			HttpServletRequest request) throws IOException {

		String oldImage = request.getParameter("oldImg");
		int rewardId = Integer.parseInt(request.getParameter("rwdId"));
		String deleteOldimageFlag = request.getParameter("deleteOldImage");

		ModelAndView model = new ModelAndView();
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
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

		String fileName = null;
		String extn = null;

		String newFileName = newFile.getOriginalFilename();
		log.info("categoryName >>>>>>>>>> " + categoryName);
		log.info("oldImageName >>>>>>>>>> " + oldImage);
		log.info("newFileName >>>>>>>>>> " + newFileName);

		if (categoryName != null && !categoryName.trim().equals("")) {

			if (categoryName != null && oldImage.equals("null") && !(newFileName.isEmpty())) {

				if (newFile != null) {
					fileName = newFile.getOriginalFilename();
					extn = fileName.substring(fileName.lastIndexOf(".") + 1);
					log.info("FileName" + "\t" + fileName);
					log.info("extn" + "\t" + extn);
				}

				if (extn.equals("jpg") || extn.equals("jpeg") || extn.equals("png")) {
					File f = null;
					long fileSize = (newFile.getSize() / 1000);
					if (fileSize <= 150) {
						log.info("file to be uploaded in >>>>>> "
								+ this.configProperties.getProperty("rewards.upload.path") + "/" + fileName);
						boolean flag = false;
						try {
							f = new File(webUtility.createNewFolder(
									this.configProperties.getProperty("rewards.upload.path") + "/" + fileName));
							flag = f.createNewFile();
							log.info("fileCreated>>Student" + "\t" + flag);
							log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
							newFile.transferTo(f);
						} catch (Exception e) {
							log.debug("Exception occurrred ::::::::::::::::: File already exists");
						}

						this.rewardsService.updateRewardsCategory(rewardId, categoryName, newFileName);
						model.addObject("imageName", fileName);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("cName", categoryName);
						model.addObject("rewardId", request.getParameter("rwdId"));
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						model.addObject("success", "successfully updated the record");
						model.setViewName("editRewardsCategory");
						return model;

					} else {

						model.addObject("imageName", null);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("cName", categoryName);
						model.addObject("rewardId", request.getParameter("rwdId"));
						model.addObject("wrongExtn",
								"Please Choose a Valid JPG or JPEG or PNG File which is less than or equal to 150 KB");
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						model.setViewName("editRewardsCategory");
						log.debug("File is > 150 KB");
						return model;

					}

				} else {
					model.addObject("imageName", null);
					model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
							+ this.configProperties.getProperty("rewards.download.path"));
					model.addObject("cName", categoryName);
					model.addObject("rewardId", request.getParameter("rwdId"));
					model.addObject("fileSize",
							"Please Choose a Valid JPG or JPEG or PNG File which is less than or equal to 150 KB");
					model.addObject(Constant.SessionID, sessionID);
					model.addObject(Constant.FirstName, user.getName());
					model.setViewName("editRewardsCategory");
					log.debug("Invalid File : File must be in JPEG or PNG or JPG format : 1");
					return model;
				}
			}

			if (categoryName != null && !oldImage.equals("null") && !(newFileName.isEmpty())
					&& deleteOldimageFlag != null && deleteOldimageFlag.equals("delete")) {

				if (newFile != null) {
					fileName = newFile.getOriginalFilename();
					extn = fileName.substring(fileName.lastIndexOf(".") + 1);
					log.info("FileName" + "\t" + fileName);
					log.info("extn" + "\t" + extn);
				}

				if (extn.equals("jpg") || extn.equals("jpeg") || extn.equals("png")) {

					log.debug("file size ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::"
							+ newFile.getSize());

					long fileSize = (newFile.getSize() / 1000);

					if (fileSize <= 150) {
						try {
							File f = new File(webUtility.createNewFolder(
									this.configProperties.getProperty("rewards.upload.path") + "/" + fileName));

							boolean flag = f.createNewFile();

							log.info("fileCreated>>Student" + "\t" + flag);
							log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
							newFile.transferTo(f);
						} catch (Exception e) {
							log.debug("Exception occurrred ::::::::::::::::: File already exists");
						}

						this.rewardsService.updateRewardsCategory(rewardId, categoryName,
								newFile.getOriginalFilename());
						model.addObject("imageName", fileName);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("cName", categoryName);
						model.addObject("rewardId", request.getParameter("rwdId"));
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						model.setViewName("editRewardsCategory");
						model.addObject("success", "successfully updated the record");
						return model;

					} else {
						model.addObject("imageName", oldImage);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("cName", categoryName);
						model.addObject("rewardId", request.getParameter("rwdId"));
						model.addObject("fileSize",
								"Please Choose a Valid JPG or JPEG or PNG File which is less than or equal to 150 KB");
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						model.setViewName("editRewardsCategory");
						log.debug("File is > 150 KB");
						return model;
					}

				} else {

					model.addObject("imageName", oldImage);
					model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
							+ this.configProperties.getProperty("rewards.download.path"));
					model.addObject("cName", categoryName);
					model.addObject("rewardId", request.getParameter("rwdId"));
					model.addObject(Constant.SessionID, sessionID);
					model.addObject(Constant.FirstName, user.getName());
					model.addObject("wrongExtn",
							"Please Choose a Valid JPG or JPEG or PNG File which is less than or equal to 150 KB");
					model.setViewName("editRewardsCategory");
					log.debug("Invalid File : File must be in JPEG or PNG or JPG format >>>>>>>>>>> ");
					return model;
				}

			}

			if (categoryName != null && !oldImage.equals("null") && !(newFileName.isEmpty())
					&& deleteOldimageFlag == null) {

				model.addObject("imageName", oldImage);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("cName", categoryName);
				model.addObject("rewardId", request.getParameter("rwdId"));
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.addObject("wrongExtn",
						"Please check on the check box to delete old file and then upload new file");
				model.setViewName("editRewardsCategory");
				log.debug("Old file not deleted >>>>>>>>>>> ");
				return model;
			}

			if (categoryName != null && !oldImage.equals("null") && newFileName.isEmpty() && deleteOldimageFlag != null
					&& deleteOldimageFlag.equals("delete")) {

				log.info("updating db , old is not null and new image are null");
				this.rewardsService.updateRewardsCategory(rewardId, categoryName, null);
				model.addObject("imageName", null);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("cName", categoryName);
				model.addObject("rewardId", request.getParameter("rwdId"));
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.addObject("success", "successfully updated the record");
				model.setViewName("editRewardsCategory");
				return model;

			}

			if (categoryName != null && !oldImage.equals("null") && newFileName.isEmpty()
					&& deleteOldimageFlag == null) {
				this.rewardsService.updateRewardsCategory(rewardId, categoryName, oldImage);
				model.addObject("imageName", oldImage);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("cName", categoryName);
				model.addObject("rewardId", request.getParameter("rwdId"));
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.addObject("success", "successfully updated the record");
				model.setViewName("editRewardsCategory");
				log.debug("Old file not deleted >>>>>>>>>>> ");
				return model;
			} else {

				log.info("updating db , old is null and new image are null and category name is not null");
				this.rewardsService.updateRewardsCategory(rewardId, categoryName, null);
				model.addObject("imageName", null);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("cName", categoryName);
				model.addObject("rewardId", request.getParameter("rwdId"));
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.addObject("success", "successfully updated the record");
				model.setViewName("editRewardsCategory");
				return model;

			}
		} else {

			if (oldImage.equals("null")) {
				model.addObject("imageName", null);
			} else {
				model.addObject("imageName", oldImage);
			}
			model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
					+ this.configProperties.getProperty("rewards.download.path"));
			model.addObject("cName", categoryName);
			model.addObject("rewardId", request.getParameter("rwdId"));
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.addObject("wrongExtn", "CategoryName Cannot be blank or empty");
			model.setViewName("editRewardsCategory");
			log.debug("Old file not deleted >>>>>>>>>>> ");
			return model;

		}

	}

	@RequestMapping(value = "/deleteRewardsCategory", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView deleteRewardsCategory(@RequestParam("rewardId") String rewardCId,
			@RequestParam("token") String sessionID) throws IOException {

		ModelAndView model = new ModelAndView();
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
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
		List<Integer> rewardIds = this.rewardsService.getRewardIdFromRewardCategoryId(Integer.parseInt(rewardCId));
		for (Integer rewardId : rewardIds) {
			this.rewardsService.deleteRewardsFromRewardsStudent(rewardId);
		}
		this.rewardsService.deleteRewardsFromRewards(Integer.parseInt(rewardCId));
		this.rewardsService.deleteRewardsFromRewardsCategory(Integer.parseInt(rewardCId));
		model.addObject("sessionID", sessionID);
		return new ModelAndView("redirect:/manageRewardsCategory?token=" + sessionID);
	}

	@RequestMapping(value = "/manageRewardsSchoolAdmin", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView manageRewardsSchoolAdmin(
			@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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
		Set<String> rcList = new HashSet<>();
		List<SchoolRewardsListTransform> schoolRewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());
		List<RewardsListTransform> rewardsListTransform = this.rewardsService
				.getAllRewardsBasedBasedOnSchoolId((int) user.getAccountId());

		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");
		model.addObject("rewardsList", rewardsListTransform);
		model.addObject("imagePath", imagePath);

		for (SchoolRewardsListTransform rlt : schoolRewardsListTransform) {
			rcList.add(rlt.getCategory_name());
		}
		model.addObject("listCategoryForRewards", rcList);
		model.setViewName("manageRewardsShcoolAdmin");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;
	}

	@RequestMapping(value = "/displaySelectedReward")
	public ModelAndView displaySelectedReward(
			@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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

		List<RewardsListTransform> rewardsListTransform = null;
		Set<String> rcList = new HashSet<>();
		List<SchoolRewardsListTransform> schoolRewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());
		for (SchoolRewardsListTransform rlt : schoolRewardsListTransform) {
			rcList.add(rlt.getCategory_name());
		}

		log.debug("selected val ::: " + rewardsListTransform2.getUniqueCategory());
		if (rewardsListTransform2.getUniqueCategory().equals("NONE")) {
			log.debug("selected val ::: NONE");
			rewardsListTransform = this.rewardsService
					.getAllRewardsBasedBasedOnSchoolId((int) user.getAccountId());
		} else {
			log.debug("selected val is NOT NONE ::: " + rewardsListTransform2.getUniqueCategory());
			rewardsListTransform = this.rewardsService.getAllRewardsBasedOnRewardCategoryNameForSchoolAdmin(
					rewardsListTransform2.getUniqueCategory(), (int) user.getAccountId());
			String imagePath = this.configProperties.getProperty("downloads.url")
					+ this.configProperties.getProperty("rewards.download.path");
			model.addObject("rewardsList", rewardsListTransform);
			model.addObject("imagePath", imagePath);
		}
		model.addObject("rewardsList", rewardsListTransform);
		model.addObject("listCategoryForRewards", rcList);
		model.setViewName("manageRewardsShcoolAdmin");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;
	}

	@RequestMapping(value = "/editSelectedReward")
	public ModelAndView editSelectedReward(
			@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		String selectedCategoryName = request.getParameter("cName");
		Integer selectedCategoryId = Integer.parseInt(request.getParameter("categoryId"));
		log.info("selected category name : " + selectedCategoryName);
		log.info("categoryId >>>>>>>>>>>>>>>>>> " + selectedCategoryId);

		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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

		RewardsListTransform rewardsTransform = this.rewardsService
				.getRewardsInfoBasedOnRewardId(Integer.parseInt(request.getParameter("rewardsId")));
		List<SchoolRewardsListTransform> rewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());

		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");

		String imageName = rewardsTransform.getIcon();

		Map<Integer, String> cNamesAndCIds = new LinkedHashMap<>();
		for (SchoolRewardsListTransform rlt : rewardsListTransform) {
			cNamesAndCIds.put(rlt.getRewards_category_id(), rlt.getCategory_name());

		}

		Map<Integer, String> copy = new LinkedHashMap<>(cNamesAndCIds);
		cNamesAndCIds.keySet().retainAll(Collections.singleton(selectedCategoryId));
		cNamesAndCIds.putAll(copy);

		model.addObject("listRewards", cNamesAndCIds);
		model.addObject("rName", rewardsTransform.getRewardName());
		model.addObject("imageName", imageName);
		model.addObject("imagePath", imagePath);
		model.setViewName("editSelectedReward");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("rewardId", request.getParameter("rewardsId"));
		return model;

	}

	@RequestMapping(value = "/updateSelectedReward", method = RequestMethod.POST)
	public ModelAndView updateSelectedReward(
			@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request, RedirectAttributes redirectAttributes,
			@RequestParam("file") MultipartFile newFile) throws IOException {
		String sessionID = request.getParameter("token");
		log.info("rewardId ::::" + request.getParameter("rewardId"));
		log.info(("selected reward category id :::::::::::::: " + rewardsListTransform2.getRewardsCategoryId()));

		ModelAndView model = new ModelAndView();

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
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

		String rewardId = request.getParameter("rewardId");
		String fileName = null;
		String extn = null;

		String newFileName = newFile.getOriginalFilename();
		String rewardName = request.getParameter("name");
		String oldImage = request.getParameter("oldImg");
		Integer rewardCategoryId = rewardsListTransform2.getRewardsCategoryId();
		String deleteOldimageFlag = request.getParameter("deleteOldImage");
		log.info("oldImage :::::: " + oldImage);
		log.info("newFileName >>>>>>>>>> " + newFileName);
		log.info("rewardName :::::::::::: " + rewardName);
		log.info("rewardCategoryId ::::::::::::" + rewardCategoryId);
		log.info("deleteOldimageFlag :::::::::: " + deleteOldimageFlag);

		RewardsListTransform rewardsListTransform = this.rewardsService
				.getRewardsInfoBasedOnRewardId(Integer.parseInt(request.getParameter("rewardId")));
		rewardsListTransform.setRewardsCategoryId(rewardsListTransform2.getRewardsCategoryId());
		rewardsListTransform.setRewardName(request.getParameter("name"));
		rewardsListTransform.setRewardsCategoryId(rewardCategoryId);

		if (rewardName != null && !rewardName.trim().equals("")) {
			if (rewardName != null && (oldImage.equals("null") || oldImage.equals("")) && !(newFileName.isEmpty())) {

				if (newFile != null) {
					fileName = newFile.getOriginalFilename();
					extn = fileName.substring(fileName.lastIndexOf(".") + 1);
					log.info("FileName" + "\t" + fileName);
					log.info("extn" + "\t" + extn);
				}

				if (extn.equals("jpg") || extn.equals("jpeg") || extn.equals("png")) {

					long fileSize = (newFile.getSize() / 1000);
					if (fileSize <= 150) {

						try {
							File f = new File(webUtility.createNewFolder(
									this.configProperties.getProperty("rewards.upload.path") + "/" + fileName));
							boolean flag = f.createNewFile();
							log.info("fileCreated>>Student" + "\t" + flag);
							log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
							newFile.transferTo(f);
						} catch (Exception e) {
							log.debug("Exception occurrred ::::::::::::::::: File already exists");
						}

						this.rewardsService.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, newFileName);
						model.addObject("imageName", newFileName);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("rName", request.getParameter("name"));
						model.addObject("rewardId", request.getParameter("rewardId"));
						model.addObject("categoryId", rewardCategoryId);
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						this.httpSession.setAttribute("success", "successfully updated the record");
						model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
								+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
						return model;

					} else {
						model.addObject("imageName", null);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("rName", request.getParameter("name"));
						model.addObject("rewardId", request.getParameter("rewardId"));
						model.addObject("categoryId", rewardCategoryId);
						this.httpSession.setAttribute("wrongExtn",
								"Please Choose a Valid JPG or JPEG or PNG File which is less than or equal to 150 KB");
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
								+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
						log.debug("File is > 150 KB");
						return model;
					}
				}
			}

			if (rewardName != null && !oldImage.equals("null") && !(newFileName.isEmpty()) && deleteOldimageFlag != null
					&& deleteOldimageFlag.equals("delete")) {

				fileName = newFile.getOriginalFilename();
				extn = fileName.substring(fileName.lastIndexOf(".") + 1);
				log.info("FileName" + "\t" + fileName);
				log.info("extn" + "\t" + extn);

				if (extn.equals("jpg") || extn.equals("jpeg") || extn.equals("png")) {

					long fileSize = (newFile.getSize() / 1000);
					if (fileSize <= 150) {
						log.info("file to delete  : " + this.configProperties.getProperty("rewards.upload.path") + "/"
								+ oldImage);
						try {
							File f = new File(webUtility.createNewFolder(
									this.configProperties.getProperty("rewards.upload.path") + "/" + fileName));
							boolean flag = f.createNewFile();
							log.info("fileCreated>>Student" + "\t" + flag);
							log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
							newFile.transferTo(f);
						} catch (Exception e) {
							log.debug("Exception occurrred ::::::::::::::::: File already exists");
						}

						this.rewardsService.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, newFileName);
						model.addObject("imageName", newFileName);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("rName", request.getParameter("name"));
						model.addObject("rewardId", request.getParameter("rewardId"));
						model.addObject("categoryId", rewardCategoryId);
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						this.httpSession.setAttribute("success", "successfully updated the record");
						model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
								+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
						return model;
					} else {
						model.addObject("imageName", oldImage);
						model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
								+ this.configProperties.getProperty("rewards.download.path"));
						model.addObject("rName", request.getParameter("name"));
						model.addObject("rewardId", request.getParameter("rewardId"));
						model.addObject("categoryId", rewardCategoryId);
						this.httpSession.setAttribute("wrongExtn",
								"Please Choose a Valid JPG or JPEG or PNG File which is less than or equal to 150 KB");
						model.addObject(Constant.SessionID, sessionID);
						model.addObject(Constant.FirstName, user.getName());
						model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
								+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
						log.debug("File is > 150 KB");
						return model;

					}
				}
			}

			if (rewardName != null && !oldImage.equals("null") && !oldImage.equals("") && !(newFileName.isEmpty())
					&& deleteOldimageFlag == null) {

				model.addObject("imageName", oldImage);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("rName", request.getParameter("name"));
				model.addObject("rewardId", request.getParameter("rewardId"));
				model.addObject("categoryId", rewardCategoryId);
				this.httpSession.setAttribute("wrongExtn",
						"Please Choose a Valid JPG or JPEG or PNG File which is less than or equal to 150 KB and check the checkbox to delete the existing file");
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
						+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
				log.debug("3333 File is > 150 KB");
				return model;

			}

			if (rewardName != null && !oldImage.equals("null") && newFileName.isEmpty() && deleteOldimageFlag != null
					&& deleteOldimageFlag.equals("delete")) {

				this.rewardsService.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, null);
				model.addObject("imageName", null);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("rName", request.getParameter("name"));
				model.addObject("rewardId", request.getParameter("rewardId"));
				model.addObject("categoryId", rewardCategoryId);
				this.httpSession.setAttribute("success", "successfully updated the record");
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
						+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
				log.info(" 111 update rewards table ");
				return model;

			}

			if (rewardName != null && !oldImage.equals("null") && newFileName.isEmpty() && deleteOldimageFlag == null) {
				this.rewardsService.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, oldImage);
				model.addObject("imageName", null);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("rName", request.getParameter("name"));
				model.addObject("rewardId", request.getParameter("rewardId"));
				model.addObject("categoryId", rewardCategoryId);
				this.httpSession.setAttribute("success", "successfully updated the record");
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
						+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
				log.debug(" 222 Old file not deleted >>>>>>>>>>> ");
				return model;
			} else {

				log.info("updating db , old is null and new image are null and category name is not null");
				this.rewardsService.updateRewardsAndRewardsCategoryByIds(rewardsListTransform, null);
				model.addObject("imageName", null);
				model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
						+ this.configProperties.getProperty("rewards.download.path"));
				model.addObject("rName", request.getParameter("name"));
				model.addObject("rewardId", request.getParameter("rewardId"));
				model.addObject("categoryId", rewardCategoryId);
				this.httpSession.setAttribute("success", "successfully updated the record");
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
						+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
				log.info("else ::: update rewards table ");
				return model;

			}
		} else {
			log.info("rewardname is empty or null");
			if (oldImage.equals("null")) {
				model.addObject("imageName", null);
			} else {
				model.addObject("imageName", oldImage);
			}
			model.addObject("imagePath", this.configProperties.getProperty("downloads.url")
					+ this.configProperties.getProperty("rewards.download.path"));
			model.addObject("rName", request.getParameter("name"));
			model.addObject("rewardId", request.getParameter("rewardId"));
			model.addObject("categoryId", rewardCategoryId);
			this.httpSession.setAttribute("wrongExtn", "name cannot be empty");
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			model.setViewName("redirect:/editSelectedReward?rewardsId=" + rewardId + "&token="
					+ request.getParameter("token") + "&categoryId=" + rewardCategoryId);
			log.info("outelse :::::: update rewards table ");
			return model;
		}

	}

	@RequestMapping(value = "/deleteSelectedReward")
	public ModelAndView deleteSelectedReward(
			@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request) {
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		ModelAndView model = new ModelAndView();
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

		int rewardId = Integer.parseInt(request.getParameter("rewardsId"));
		int rewardCId = 0;
		boolean flag = this.rewardsService.deleteRewardsFromRewardsAndRewardsStudents(rewardId, rewardCId);
		return new ModelAndView("redirect:/manageRewardsSchoolAdmin?token=" + request.getParameter("token"));
	}

	@RequestMapping(value = "/addNewRewards")
	public ModelAndView addNewReward(@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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

		List<SchoolRewardsListTransform> schoolRewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());
		LinkedHashMap<Integer, String> rcList = new LinkedHashMap<Integer, String>();

		for (SchoolRewardsListTransform rlt : schoolRewardsListTransform) {
			rcList.put(rlt.getRewards_category_id(), rlt.getCategory_name());

		}

		model.addObject("listRewards", rcList);
		model.setViewName("addNewRewards");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;
	}

	@RequestMapping(value = "/saveReward", method = RequestMethod.POST)
	public ModelAndView saveReward(@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request, @RequestParam("file") MultipartFile file) {
		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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

		RewardsListTransform rewardsListTransform = new RewardsListTransform();
		rewardsListTransform.setRewardName(request.getParameter("name"));

		String rcnandcid = rewardsListTransform2.getUniqueCategory();

		List<SchoolRewardsListTransform> schoolRewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());
		LinkedHashMap<Integer, String> rcList = new LinkedHashMap<Integer, String>();
		for (SchoolRewardsListTransform rlt : schoolRewardsListTransform) {
			rcList.put(rlt.getRewards_category_id(), rlt.getCategory_name());
		}

		if (rcnandcid.equals("NONE") || request.getParameter("name").trim().equals("")
				|| null == request.getParameter("name")) {
			model.addObject("listRewards", rcList);
			model.setViewName("addNewRewards");
			model.addObject("failure", "Mandatory fields category and name are required to create reward");
			model.addObject(Constant.SessionID, sessionID);
			model.addObject(Constant.FirstName, user.getName());
			return model;

		}
		String fileName = null;
		String extn = null;
		if (file != null) {
			fileName = file.getOriginalFilename();
			extn = fileName.substring(fileName.lastIndexOf(".") + 1);
			log.info("FileName" + "\t" + fileName);
			log.info("extn" + "\t" + extn);
		}

		if (extn.equals("jpg") || extn.equals("jpeg") || extn.equals("png")) {
			long fileSize = (file.getSize() / 1000);
			File f = null;
			if (fileSize <= 150) {

				try {
					f = new File(webUtility.createNewFolder(
							this.configProperties.getProperty("rewards.upload.path") + "/" + fileName));
					boolean flag = f.createNewFile();
					log.info("fileCreated>>Student" + "\t" + flag);
					log.info("f.getPath() User Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));

					file.transferTo(f);
				} catch (Exception ex) {
					log.info("file already exits");
				}

			} else {
				model.addObject("wrongExtn", "Please Choose a Valid JPG or JPEG or PNG File which is less than 150KB");
				model.addObject(Constant.SessionID, sessionID);
				model.addObject(Constant.FirstName, user.getName());
				model.setViewName("addNewRewards");
				log.debug("Invalid File :::::::::::");
				return model;
			}

		}

		rewardsListTransform.setIcon(file.getOriginalFilename());
		rewardsListTransform.setRewardsCategoryId(Integer.parseInt(rcnandcid));
		boolean flag = this.rewardsService.addNewRewards(rewardsListTransform, user);

		model.addObject("listRewards", rcList);
		model.setViewName("addNewRewards");
		model.addObject("success", "Added Reward Successfully");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;
	}

	@RequestMapping(value = "/rewardStatistics")
	public ModelAndView rewardsStatistics(HttpServletRequest request,
			@ModelAttribute("studentsModel") StudentsModel studentsModel, @RequestParam("token") String sessionID) {
		ModelAndView model = new ModelAndView();
		Users users = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(users);
		String currentUser = (String) this.httpSession.getAttribute("currentUser");
		log.info("sessionValidityResult :: " + sessionValidityResult);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}
		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
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

		List<RewardStatisticsTransform> classList = this.rewardsService
				.getStudentClass((int) users.getAccountId());
		List<String> classesList = new ArrayList<>();
		for (RewardStatisticsTransform list : classList) {
			classesList.add(String.valueOf(list.getStudentClass()));
		}
		List<RewardStatisticsTransform> studentList = this.rewardsService
				.getStudentName((int) users.getAccountId());
		List<String> studentsList = new ArrayList<>();
		for (RewardStatisticsTransform listData : studentList) {
			studentsList.add(listData.getStudent_name());
		}
		List<RewardStatisticsTransform> rewardStatisticsListTransforms = this.rewardsService
				.getRewardStatistics(users.getId());
		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");
		model.addObject("imagePath", imagePath);
		model.addObject("studentList", studentsList);
		model.addObject("classList", classesList);
		model.addObject("rewardsStatistic", rewardStatisticsListTransforms);
		model.setViewName("rewardsStatistics");
		model.addObject("dateTime", this.configProperties.getProperty("display.dateFormat"));
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, users.getName());
		return model;
	}

	@RequestMapping(value = "/rewardsStatistics")
	public ModelAndView rewardStatisticsForSchoolAdmin(HttpServletRequest request,
			@ModelAttribute("studentsModel") StudentsModel studentsModel, ModelAndView model,
			@RequestParam("token") String sessionID) {
		List<String> classesList = new ArrayList<>();
		Users users = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(users);
		String currentUser = users.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("school_admin")
						|| this.httpSession.getAttribute("currentUser").equals("school_teacher"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		List<RewardStatisticsTransform> rewardsListForSchoolAdmin = null;
		List<RewardStatisticsTransform> rewardsListForSchoolTeacher = null;
		List<String> teachersList = null;
		List<String> studentsList = null;
		if (currentUser.equals("school_admin")) {
			List<RewardStatisticsTransform> teacherList = this.rewardsService
					.getTeacherForAdmin((int) users.getAccountId());
			teachersList = new ArrayList<>();
			for (RewardStatisticsTransform list : teacherList) {
				log.info("TeachersName:::::::" + list.getUser_name());
				teachersList.add(list.getUser_name());
			}

			rewardsListForSchoolAdmin = this.rewardsService
					.getRewardsStatisticsForSchoolAdmin((int) users.getAccountId());
		} else if (currentUser.equals("school_teacher")) {
			List<RewardStatisticsTransform> studentList = this.teacherService
					.getStudentNameForTeacher((int) users.getAccountId());
			studentsList = new ArrayList<>();
			for (RewardStatisticsTransform listData : studentList) {
				log.info("List Of Students Name:::::::" + listData.getStudent_name());
				studentsList.add(listData.getStudent_name());
			}
			List<RewardStatisticsTransform> classList = this.teacherService
					.getClassNameForTeacher((int) users.getAccountId());

			for (RewardStatisticsTransform list : classList) {
				classesList.add(String.valueOf(list.getStudentClass()));
			}
			log.info("classesList.size()" + "\t" + classesList.size());
			log.info("classList.size()" + "\t" + classList.size());
			rewardsListForSchoolTeacher = this.teacherService
					.getRewardsStatisticsForTeacher((int) users.getAccountId());
		}
		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");
		model.addObject("imagePath", imagePath);

		if (currentUser.equals("school_admin")) {
			model.addObject("rewardList", rewardsListForSchoolAdmin);
			model.addObject("teachersList", teachersList);
		} else if (currentUser.equals("school_teacher"))
			model.addObject("studentList", studentsList);
		model.addObject("rewardsStatisticList", rewardsListForSchoolTeacher);
		model.addObject("classList", classesList);
		if (currentUser.equals("school_admin")) {
			model.setViewName("rewardsStatisticForSchoolAdmin");
		} else if (currentUser.equals("school_teacher")) {
			model.setViewName("rewardsstatisticForTeacher");
		}

		model.addObject("dateTime", this.configProperties.getProperty("display.dateTime"));
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, users.getName());
		return model;
	}

	@RequestMapping(value = "/uniqueStudent")
	public ModelAndView uniqueRewardStatistics(HttpServletRequest request,
			@ModelAttribute("studentsModel") StudentsModel studentsModel, ModelAndView model) {
		String sessionID = request.getParameter("token");
		Users users = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(users);
		String currentUser = users.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}
		if (null == currentUser) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}
		if ((this.httpSession.getAttribute("currentUser") != null)
				&& !(this.httpSession.getAttribute("currentUser").equals("parent_admin"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("login");
			return model;
		}

		String day = request.getParameter("datetime");
		String name = request.getParameter("name");

		List<RewardStatisticsTransform> studentList = this.rewardsService
				.getStudentName((int) users.getAccountId());
		List<String> studentsList = new ArrayList<>();
		for (RewardStatisticsTransform listData : studentList) {
			studentsList.add(listData.getStudent_name());
		}
		List<RewardStatisticsTransform> uniqueList = this.rewardsService
				.getUniqueRewardStatistics((int) users.getAccountId(), name, day);
		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");
		model.addObject("imagePath", imagePath);
		model.addObject("studentList", studentsList);
		model.addObject("rewardsStatistic", uniqueList);
		model.setViewName("rewardsStatistics");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, users.getName());
		return model;
	}

	@RequestMapping(value = "/manageRewardsSchoolTeacher", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public ModelAndView manageRewardsSchoolTeacher(
			@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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
				&& !(this.httpSession.getAttribute("currentUser").equals("school_teacher"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		Set<String> rcList = new HashSet<>();
		List<SchoolRewardsListTransform> schoolRewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());

		List<RewardsListTransform> rewardsListTransform = this.rewardsService
				.getAllRewardsBasedOnRewardCategoryId(user.getId(), user.getAccountId());

		String imagePath = this.configProperties.getProperty("downloads.url")
				+ this.configProperties.getProperty("rewards.download.path");
		model.addObject("imagePath", imagePath);

		model.addObject("rewardsList", rewardsListTransform);

		for (SchoolRewardsListTransform rlt : schoolRewardsListTransform) {
			rcList.add(rlt.getCategory_name());
		}
		model.addObject("listCategoryForRewards", rcList);
		model.setViewName("manageRewardsSchoolTeacher");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;
	}

	@RequestMapping(value = "/displaySelectedRewardForTeacher")
	public ModelAndView displaySelectedRewardForTeacher(
			@ModelAttribute("rewardsListTransform") RewardsListTransform rewardsListTransform2,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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
				&& !(this.httpSession.getAttribute("currentUser").equals("school_teacher"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		List<RewardsListTransform> rewardsListTransform = null;
		Set<String> rcList = new HashSet<>();
		List<SchoolRewardsListTransform> schoolRewardsListTransform = this.rewardsService
				.getSchoolRewardsBySchoolId((int) user.getAccountId());
		for (SchoolRewardsListTransform rlt : schoolRewardsListTransform) {
			rcList.add(rlt.getCategory_name());
		}

		log.debug("rewardsListTransform2.getUniqueCategory() >>>>>>>>>>>>>>>> "
				+ rewardsListTransform2.getUniqueCategory());
		log.debug("selected option :::::::::: " + rewardsListTransform2.getUniqueCategory());

		if (rewardsListTransform2.getUniqueCategory().equals("NONE")) {
			rewardsListTransform = this.rewardsService.getAllRewardsBasedOnRewardCategoryId(user.getId(),
					user.getAccountId());
		} else {
			log.debug("selection is not all >>>>>>> ");
			rewardsListTransform = this.rewardsService.getAllRewardsBasedOnRewardCategoryName(
					rewardsListTransform2.getUniqueCategory(), user.getAccountId());
			// model.addObject("rewardsList", rewardsListTransform);
		}

		model.addObject("rewardsList", rewardsListTransform);
		model.addObject("listCategoryForRewards", rcList);
		model.setViewName("manageRewardsSchoolTeacher");
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		return model;
	}

	@RequestMapping(value = "/mobile/studentNamesAndIds/{classno}/{sessionId}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getStudentNamesAndIdsBasedonClasses(@PathVariable("classno") String classno,
			@PathVariable("sessionId") String sessionID) {
		String ErrorMessage = null;
		String StatusCode = null;
		String responseJSON = null;
		String jsonString = null;
		String SuccessMessage = null;
		List myList = new ArrayList<>();
		String type = "rewards.getStudentNamesAndIdsBasedonClasses";
		Map<Integer, String> studentDetailsMap = new HashMap<>();
		Users user = userService.validateUserBySession(sessionID);

		if (null != user) {

			if (!(user.getRoleType().equals("school_teacher"))) {
				StatusCode = "ERR03";
				ErrorMessage = "Unauthorised User ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}
			String sessionValidityResult = CommonUtil.checkSessionValidity(user);

			if (sessionValidityResult.equals("NOTVALID")) {
				StatusCode = "ERR02";
				ErrorMessage = "Session Expired ,Please Relogin ";
				responseJSON = ErrorCodesUtil.displayErrorJSON(ErrorMessage, StatusCode, type);
				//JSONUtility.respondAsJSON(response, responseJSON);
				return responseJSON;
			}

			List<StudentsListTransform> studentIdAndNames = this.userService
					.getStudentDetailsFromStudent(user.getAccountId(), classno);
			for (StudentsListTransform studentsDetailsTransform : studentIdAndNames) {
				studentDetailsMap.put(studentsDetailsTransform.getStudentId(),
						studentsDetailsTransform.getStudentName());
				Map<Object, Object> map = new LinkedHashMap<>();
				map.put("studentName", studentsDetailsTransform.getStudentName());
				map.put("studentId", studentsDetailsTransform.getStudentId());
				jsonString = JSONObject.toJSONString(map);
				myList.add(jsonString);
				log.info("My List Contents::::::::" + myList);
			}

			StatusCode = "SUC01";
			SuccessMessage = "API Request Success";
			responseJSON = ErrorCodesUtil.displaySuccessJSONForRewardsSchoolTeacher(type, SuccessMessage, StatusCode,
					myList.toString());
			return responseJSON;

		} else {
			StatusCode = "ERR05";
			SuccessMessage = "Input is invalid";
			responseJSON = ErrorCodesUtil.displaySuccessJSONForRewardsSchoolTeacher(type, SuccessMessage, StatusCode,
					myList.toString());
			return responseJSON;
		}
	}

	@RequestMapping(value = "/assignOrReAssignRewards")
	public ModelAndView assignOrReAssignRewards(HttpServletRequest request) {

		log.info("assignOrReAssignRewards class: " + request.getParameter("class"));

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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
				&& !(this.httpSession.getAttribute("currentUser").equals("school_teacher"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		Integer userId = user.getId();
		log.info("accountId : " + userId);
		List<StudentsListTransform> studentsDetailsTransformList = this.userService
				.getStudentDetailsFromStudent(userId);
		log.info("studentsDetailsTransformList : " + studentsDetailsTransformList.toString());

		Map<Integer, String> studentDetailsMap = new HashMap<>();
		List<StudentsListTransform> studentIdAndNames = null;

		log.debug("class ::::::::::: " + request.getParameter("class"));
		if (request.getParameter("class") != null && request.getParameter("class").equals("null")) {
			// studentDetailsMap = null;
			log.debug("class is null >>>>>>>>>>>>>");
			studentIdAndNames = this.userService.getStudentDetailsFromStudent(user.getAccountId(), null);
			for (StudentsListTransform studentsDetailsTransform : studentIdAndNames) {
				studentDetailsMap.put(studentsDetailsTransform.getStudentId(),
						studentsDetailsTransform.getStudentName());
			}
		} else {
			studentIdAndNames = this.userService.getStudentDetailsFromStudent(user.getAccountId(),
					request.getParameter("class"));
			for (StudentsListTransform studentsDetailsTransform : studentIdAndNames) {
				studentDetailsMap.put(studentsDetailsTransform.getStudentId(),
						studentsDetailsTransform.getStudentName());
			}
		}

		List<String> studentClassList = new ArrayList<String>();

		for (StudentsListTransform studentsDetailsTransform : studentsDetailsTransformList) {
			// studentDetailsMap.put(studentsDetailsTransform.getStudentId(),
			// studentsDetailsTransform.getStudentName());
			studentClassList.add(String.valueOf(studentsDetailsTransform.getStudentClass()));
		}

		int rewardId = Integer.parseInt(request.getParameter("rewardId"));
		RewardsListTransform rewardsListTransform = this.rewardsService.getRewardsInfoBasedOnRewardId(rewardId);
		int rewardCatId = rewardsListTransform.getRewardsCategoryId();
		String rewardName = rewardsListTransform.getRewardName();
		String rcName = this.rewardsService.getRewardsCategoryNameBasedonRCId(rewardCatId);
		String icon = rewardsListTransform.getIcon();
		String count = "";

		log.info("assignOrReAssignRewards : studentDetailsMap : " + studentDetailsMap);

		Set<String> studentClassSet = new HashSet<>();
		if (null == request.getParameter("class") || request.getParameter("class").equals("--Select Class--")
				|| request.getParameter("class").equals("null")) {
			log.debug("assignOrReAssignRewards class null  ");
			studentClassSet = new HashSet<>();
			studentClassSet.addAll(studentClassList);
			studentClassList.clear();
			studentClassList.add("--Select Class--");
			studentClassList.addAll(studentClassSet);
			model.setViewName("assignOrReAssignRewardsWithStudentNames");
			model.addObject("selectClass", "please select atleast one class from the list of classes");
		} else {
			log.debug("assignOrReAssignRewards class not null ");
			studentClassList.clear();
			studentClassList.add(request.getParameter("class"));
			model.setViewName("assignOrReAssignRewardsWithStudentNames");
		}

		String imagePath = this.configProperties.getProperty("application.url")
				+ this.configProperties.getProperty("rewards.download.path");

		model.addObject("studentDetailsMap", studentDetailsMap);
		model.addObject("studentClassList", studentClassList);
		model.addObject("rewardName", rewardName);
		model.addObject("rcName", rcName);
		model.addObject("imagePath", imagePath);
		model.addObject("count", count);
		model.addObject("icon", icon);

		if (request.getParameter("updatedFlag") != null) {
			if (request.getParameter("updatedFlag").equals("true")) {
				model.addObject("success", "successfully assigned records");
			} else {
				model.addObject("failure", "please select atleast one class from the list");
			}
		}

		model.addObject("token", sessionID);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("rewardId", rewardId + "");
		model.addObject("showList", "false");
		return model;

	}

	@RequestMapping(value = "/viewRewardsByParent")
	public ModelAndView viewRewardsByParent(HttpServletRequest request, @RequestParam("token") String sessionID) {
		log.info("viewRewardsByParent ");
		ModelAndView model = new ModelAndView();
		Users user = userService.validateUserBySession(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		log.info("sessionValidityResult :: " + sessionValidityResult);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("login");
			return model;
		}

		Integer userId = user.getId();
		log.info("accountId : " + userId);

		List<RewardsTransform> rewardList = this.userService.getRewards(String.valueOf(userId));

		model.addObject("token", sessionID);
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, user.getName());
		model.addObject("rewardsList", rewardList);
		model.setViewName("manageRewardsByParent");
		return model;

	}

	@RequestMapping(value = "/uniqueDataForSchoolAdmin")
	public ModelAndView uniqueStudentForSchoolAdmin(HttpServletRequest request,
			@ModelAttribute("studentsModel") StudentsModel studentsModel, ModelAndView model,
			@RequestParam("token") String sessionID) {
		Users users = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(users);
		log.info("sessionValidityResult :: " + sessionValidityResult);

		if (sessionValidityResult.equals("NOTVALID")) {
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("adminlogin");
			return model;
		}
		String teacherName = request.getParameter("teacherName");
		String studentClass = request.getParameter("uniqueClass");
		String studentName = request.getParameter("name");
		String day = request.getParameter("day");

		List<RewardStatisticsTransform> teacherList = this.rewardsService
				.getTeacherForAdmin((int) users.getAccountId());
		List<String> teachersList = new ArrayList<>();
		for (RewardStatisticsTransform list : teacherList) {
			log.info("TeachersName:::::::" + list.getUser_name());
			teachersList.add(list.getUser_name());
		}
		List<RewardStatisticsTransform> studentRewardList = this.rewardsService.getUniqueRewards(
				(int) users.getAccountId(), day, teacherName, studentClass, studentName);
		log.debug(studentRewardList.size());
		String imagePath = this.configProperties.getProperty("application.url")
				+ this.configProperties.getProperty("rewards.download.path");
		model.addObject("imagePath", imagePath);
		model.addObject("rewardList", studentRewardList);
		model.addObject("teachersList", teachersList);
		model.setViewName("rewardsStatisticForSchoolAdmin");
		model.addObject("dateTime", this.configProperties.getProperty("display.dateTime"));
		model.addObject(Constant.SessionID, sessionID);
		model.addObject(Constant.FirstName, users.getName());
		return model;
	}

	@ResponseBody
	@RequestMapping(value = "/get-teacher-class", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getTeacherClasses(HttpServletRequest request) {
		String getKey = null;
		List<RewardStatisticsTransform> classList = null;
		List<String> cList = new ArrayList<>();
		StringBuilder classSb = null;
		try {
			getKey = request.getParameter("keyTeacher");
			classList = new ArrayList<>();
			classList = (ArrayList<RewardStatisticsTransform>) this.rewardsService.getClass(getKey);
			for (RewardStatisticsTransform rewardStatisticsTransform : classList) {
				cList.add(String.valueOf(rewardStatisticsTransform.getStudentClass()));
			}

			classSb = new StringBuilder();
			classSb.append("<option value='NONE'>Show all Classes</option>");
			for (String sClass : cList) {
				classSb.append("<option >" + sClass + "</option>");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return classSb.toString();
	}

	@ResponseBody
	@RequestMapping(value = "/get-student-name", method = RequestMethod.GET)
	public String getClassStudent(HttpServletRequest request) {
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
		return classSb.toString();
	}

	@RequestMapping(value = "/updatedStudentDetails")
	public ModelAndView updatedStudentDetails(
			@ModelAttribute("studentsListTransform") StudentsListTransform studentsListTransform,
			HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		String sessionID = request.getParameter("token");
		Users user = userService.getUserBySessionId(sessionID);

		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		String currentUser = user.getRoleType();
		log.info("sessionValidityResult :: " + sessionValidityResult);

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
				&& !(this.httpSession.getAttribute("currentUser").equals("school_teacher"))) {
			model.addObject(Constant.LoginError, Constant.SessionInValidityResult);
			model.setViewName("adminlogin");
			return model;
		}

		String[] studentsList = null;
		List<StudentsListTransform> studentIdAndNames = null;
		List<String> studentIds = new ArrayList<>();
		log.debug("request.getParameterValues(students ????????????????????????????????????"
				+ request.getParameterValues("students"));
		if (request.getParameter("All") != null) {
			if (request.getParameter("All").equals("All")) {
				studentIdAndNames = this.userService.getStudentDetailsFromStudent(user.getAccounts().getAccountId(),
						request.getParameter("sclass"));

				for (StudentsListTransform studentsListIds : studentIdAndNames) {

					studentIds.add(studentsListIds.getStudentId() + "");
				}
			}
		} else {
			studentsList = request.getParameterValues("students");
		}

		String studentClass = request.getParameter("sclass");
		log.info("studentClass  " + studentClass);
		String comments = request.getParameter("comments");
		int count = 1;

		log.info("updatedStudentDetails : rewardId  " + request.getParameter("rewardId"));
		int rewardId = Integer.parseInt(request.getParameter("rewardId"));
		log.info("rewardId " + rewardId);

		if (request.getParameter("count") != null) {
			try {
				count = Integer.parseInt(request.getParameter("count"));
				if (count <= 0) {
					// count = 1;
					ModelAndView modelRedirect = new ModelAndView(
							"redirect:/assignOrReAssignRewards?token=" + request.getParameter("token") + "&class="
									+ studentClass + "&rewardId=" + rewardId + "&updatedFlag=false");
					modelRedirect.addObject("sessionID", request.getParameter("token"));
					return modelRedirect;
				}
			} catch (Exception e) {
				ModelAndView modelRedirect = new ModelAndView(
						"redirect:/assignOrReAssignRewards?token=" + request.getParameter("token") + "&class="
								+ studentClass + "&rewardId=" + rewardId + "&updatedFlag=false");
				modelRedirect.addObject("sessionID", request.getParameter("token"));
				return modelRedirect;
			}
		}

		if (studentIds.size() > 0) {
			log.debug("update StudentDetails() method studentIds.size() > 0");
			for (String studentId : studentIds) {
				studentsListTransform.setStudentId(Integer.parseInt(studentId));
				this.rewardsService.assignAndReAssignRewards(studentsListTransform, user, rewardId, comments, count);
			}

			ModelAndView modelRedirect = new ModelAndView("redirect:/manageRewardsSchoolTeacher?token="
					+ request.getParameter("token") + "&updatedFlag=true");
			modelRedirect.addObject("sessionID", request.getParameter("token"));
			return modelRedirect;
		}

		if (null == studentsList) {
			log.debug("studentsList is null :::::::::::::::::::::::::::::::::::::::::::::::::::");
			ModelAndView modelRedirect = new ModelAndView(
					"redirect:/assignOrReAssignRewards?token=" + request.getParameter("token") + "&class="
							+ studentClass + "&rewardId=" + rewardId + "&updatedFlag=false");
			modelRedirect.addObject("sessionID", request.getParameter("token"));
			return modelRedirect;
		}

		// List<String> studentLists = Arrays.asList(studentsList);
		log.debug("update StudentDetails() iterating studentsList");
		for (String sId : studentsList) {
			studentsListTransform.setStudentId(Integer.parseInt(sId));
			log.debug("StudentId :::::" + studentsListTransform.getStudentId());
			this.rewardsService.assignAndReAssignRewards(studentsListTransform, user, rewardId, comments, count);
		}

		ModelAndView modelRedirect = new ModelAndView(
				"redirect:/manageRewardsSchoolTeacher?token=" + request.getParameter("token") + "&updatedFlag=true");
		modelRedirect.addObject("sessionID", request.getParameter("token"));
		return modelRedirect;

	}
}
