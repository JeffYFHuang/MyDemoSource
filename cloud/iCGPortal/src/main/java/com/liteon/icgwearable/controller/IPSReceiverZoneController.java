package com.liteon.icgwearable.controller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.IPSReceiver;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverZone;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.IPSReceiverZoneModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.EventsService;
import com.liteon.icgwearable.service.IPSReceiverDeviceService;
import com.liteon.icgwearable.service.IPSReceiverService;
import com.liteon.icgwearable.service.IPSReceiverZoneService;
import com.liteon.icgwearable.service.SchoolService;
import com.liteon.icgwearable.service.UserService;
import com.liteon.icgwearable.transform.SessionDetailsForIDTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.IPSConstants;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.StringUtility;
import com.liteon.icgwearable.util.WebUtility;

@RestController
public class IPSReceiverZoneController {

	private static Logger log = Logger.getLogger(IPSReceiverZoneController.class);

	@Autowired
	private IPSReceiverService ipsReceiverService;

	@Autowired
	private IPSReceiverZoneService ipsReceiverZoneService;

	@Autowired
	private UserService userService;

	@Autowired
	private SchoolService schoolService;

	@Autowired
	HttpServletResponse response;

	@Value("${IPS_Login_Validity}")
	private String ipsLoginValidity;

	// @Value("${MAX_ALLOWED_IPS_FILE_SIZE}")
	// private String max_allowed_ips_file_size_str;

	// 1MB=1024 * 1024 in bytes
	private static long MAX_ALLOWED_IPS_FILE_SIZE = 1048576L;

	@Resource(name = "configProperties")
	private Properties configProperties;

	@Value("${db.dateTime}")
	private String dbDateTime;

	WebUtility webUtility = WebUtility.getWebUtility();

	@Autowired
	private ActivityLogService activityLogs;

	@RequestMapping(value = "/IPS/CreateIPSReceiverZone/{token}/{receiver_Id}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String createIPSReceiverZone(@PathVariable("token") String sessionId,
			@PathVariable("receiver_Id") Integer receiver_Id, HttpServletRequest request) throws IOException {

		log.info("Inside IPSReceiverZoneController.createIPSReceiverZone()");

		String responseJSON = null;
		IPSReceiver ips = null;
		IPSReceiverZoneModel zm = new IPSReceiverZoneModel();

		String type = "IPSReceiverZone.CreateIPSReceiverZone";

		String zonename = request.getParameter("zoneName");
		// String mapname = request.getParameter("mapFilename");

		log.info("zonename=" + zonename);

		if (receiver_Id == null || receiver_Id == 0
				|| (sessionId == null || (sessionId != null && sessionId.equals("")))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR07_MSG, IPSConstants.STATUSCODE_07, type);
			return responseJSON;
		}

		if (zonename == null || zonename.equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		} else {
			zm.setZoneName(zonename);
		}

		// get details of ipsreceiver for given id
		ips = this.ipsReceiverService.getIPSReceiverDetailsForID(receiver_Id);

		if (ips == null || ips.getReceiverId() == null
				|| (ips.getReceiverId() != null && !ips.getReceiverId().equals(receiver_Id))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR08_MSG, IPSConstants.STATUSCODE_08, type);
			return responseJSON;
		}

		if (ips.getSessionId() == null || (ips.getSessionId() != null && !ips.getSessionId().equals(sessionId))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
			return responseJSON;
		}

		zm.setReceiverId(receiver_Id);

		String contentType = request.getContentType();
		log.info("contentType in BeconConfigFileUpload" + "\t" + contentType);
		MultipartHttpServletRequest multiHttpReq = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = null;
		if (multiHttpReq.getFileMap().entrySet() != null) {

			Set set = multiHttpReq.getFileMap().entrySet();
			java.util.Iterator i = set.iterator();
			log.info("inside multihttpreq chk....set=" + set.size());

			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				log.info("key=" + me.getKey() + "   value=" + me.getValue());

				String fileName = (String) me.getKey();
				multipartFile = (MultipartFile) me.getValue();
				log.info("Original fileName - " + multipartFile.getOriginalFilename());
				log.info("fileName - " + fileName);
			}
		}

		String fileName = null;
		String extn = null;

		// String newFileName = multipartFile.getOriginalFilename();
		// log.info("newFileName >>>>>>>>>> " + newFileName);

		if (multipartFile != null && !(multipartFile.isEmpty())) {
			fileName = multipartFile.getOriginalFilename();
			extn = fileName.substring(fileName.lastIndexOf(".") + 1);
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			log.info("FileName" + "\t" + fileName);
			log.info("extn" + "\t" + extn);

			// in size bytes
			// long maxsize = Long.parseLong(max_allowed_ips_file_size_str);

			// long maxsize = 1048576L;
			log.info("long maxsize=" + MAX_ALLOWED_IPS_FILE_SIZE);
			log.info("multipartFile.getSize()==" + multipartFile.getSize());

			if (multipartFile.getSize() > MAX_ALLOWED_IPS_FILE_SIZE) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR13_MSG, IPSConstants.STATUSCODE_13,
						type);
				return responseJSON;

			} else if (extn.equalsIgnoreCase("jpg") || extn.equals("jpeg") || extn.equals("png")) {

				String updatedfilename = saveImage(multipartFile, receiver_Id, fileName, extn, "");
				zm.setMapFilename(updatedfilename);

			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR14_MSG, IPSConstants.STATUSCODE_14,
						type);
				return responseJSON;
			}

		} // if check multipartfile=null

		SessionDetailsForIDTransform sessionTocheck = new SessionDetailsForIDTransform();
		sessionTocheck.setSessionId(ips.getSessionId());
		sessionTocheck.setExpiryduration(ips.getSessionExpiry());
		sessionTocheck.setId(ips.getReceiverId());

		int sessionValidity = 0;
		boolean check = CommonUtil.validateSession(receiver_Id, sessionId, sessionValidity, sessionTocheck,
				ipsLoginValidity);
		if (check) {
			// creation coding
			boolean flag = this.ipsReceiverZoneService.createIPSReceiverZoneEntry(zm);
			if (flag) {
				// String statusMessage = "API Request Success ";
				responseJSON = ErrorCodesUtil.displaySuccessJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01,
						type);
				return responseJSON;
			}

		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return responseJSON;
		}

		return null;
	}

	@RequestMapping(value = "/web/BeaconAlerts/{sessionID}/{inputdate}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String beaconAlerts(@PathVariable String sessionID, @PathVariable String inputdate,
			HttpServletRequest request) throws Exception {

		String statusCode = null;
		String statusMsg = null;
		String responseJSON = null;
		// int receiver_Id = 0;

		String type = "IPSReceiverZone.BeaconAlerts";
		// Map<Object, Object> outerMap = null;
		Map<String, Object> studentBeaconAlertStatsMap = null;

		if (sessionID == null || sessionID.equals("") || inputdate == null || inputdate.equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		}

		Users user = userService.validateUserBySession(sessionID);
		String sessionValidityResult = CommonUtil.checkSessionValidity(user);
		if (null != user) {
			if (!sessionValidityResult.equals("NOTVALID")) {
				if (user.getRoleType().equals("school_admin")) {
					int school_id = user.getAccountId();

					// get ipsreceiver details for given schoolid
					IPSReceiver ips = this.ipsReceiverService.getIPSReceiverDetailsForSchoolID(school_id);

					if (ips != null && (ips.getReceiverId() != null && ips.getReceiverId() > 0)) {
						// receiver_Id = ips.getReceiverId();

						studentBeaconAlertStatsMap = this.ipsReceiverZoneService.getStudentBeaconAlerts(school_id,
								inputdate, ips.getReceiverId());

					} else {
						responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR12_MSG,
								IPSConstants.STATUSCODE_12, type);
						return responseJSON;
					}

					responseJSON = ErrorCodesUtil.displaySchoolAdminData("SUC01", IPSConstants.BSUC01_MSG, type,
							JSONObject.toJSONString(studentBeaconAlertStatsMap));

					log.info("***BeaconAlerts JSON***" + "\t" + responseJSON);
					/*
					 * if (!StringUtility.isNull(request.getHeader(
					 * "allowCacheContent")) &&
					 * request.getHeader("allowCacheContent").equals("1")) {
					 * JSONUtility.respondAsJSON(response, responseJSON, 720); }
					 * else { JSONUtility.respondAsJSON(response, responseJSON);
					 * }
					 */
				} else {
					statusCode = "ERR03";
					statusMsg = "Unauthorised User";
					// type = "students.SOSAlerts";
					responseJSON = ErrorCodesUtil.displayErrorJSON(statusCode, statusMsg, type);
					// JSONUtility.respondAsJSON(response, responseJSON);
				}
			} else {
				statusCode = "ERR02";
				statusMsg = "Session Expired ,Please Relogin";
				// type = "students.SOSAlerts";
				responseJSON = ErrorCodesUtil.displayErrorJSON(statusCode, statusMsg, type);
				// JSONUtility.respondAsJSON(response, responseJSON);
			}
		} else {
			statusCode = "ERR01";
			statusMsg = "Invalid User";
			// type = "students.SOSAlerts";
			responseJSON = ErrorCodesUtil.displayErrorJSON(statusCode, statusMsg, type);
			// JSONUtility.respondAsJSON(response, responseJSON);
		}

		return responseJSON;
	}

	@RequestMapping(value = "/IPS/SubmitIPSReceiverZoneLocationFile/{token}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String SubmitIPSReceiverZoneLocationFile(@PathVariable("token") String sessionId, HttpServletRequest request)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		log.info("Inside IPSReceiverZoneController.SubmitIPSReceiverZoneLocationFile()");

		String responseJSON = null;
		IPSReceiver ips = null;
		IPSReceiverZone ips_rz = null;
		// IPSReceiverZoneModel zm = new IPSReceiverZoneModel();
		int receiver_Id;
		String schoolNum = null;
		boolean isValidkey = false;

		String type = "IPSReceiverZone.SubmitIPSReceiverZoneLocationFile";

		String accesskey = request.getParameter("accesskey");
		String inputReceiveMac = request.getParameter("ips_receiver_mac");
		String zoneIdstr = request.getParameter("ips_receiver_zone_id");

		ActivityLog activityLog = null;
		String ipaddress = CommonUtil.getHostIpaddress();

		log.info("accesskey=" + accesskey + "  inputReceiveMac=" + inputReceiveMac + "  zoneIdstr=" + zoneIdstr);

		if (sessionId == null || (sessionId != null && sessionId.equals("")) || accesskey == null
				|| accesskey.equals("") || zoneIdstr == null || zoneIdstr.equals("") || inputReceiveMac == null
				|| (inputReceiveMac != null && inputReceiveMac.equals(""))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		}
		int zoneId = Integer.parseInt(zoneIdstr);

		// get details of ipsreceiver for given MAC
		ips = this.ipsReceiverService.getIPSReceiverEntryForMac(inputReceiveMac);

		if (ips == null || ips.getReceiverId() == null || ips.getReceiverId() == 0) {
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
					type + "-" + IPSConstants.BERR06_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR06_MSG, IPSConstants.STATUSCODE_06, type);
			return responseJSON;
		} else {
			receiver_Id = ips.getReceiverId();
		}
		log.info("ipsreceiver entry =" + ips.toString());
		if (ips != null && ips.getSchoolId() != null && !ips.getSchoolId().equals("")) {

			SchoolDetails sd = this.schoolService.getSchoolDetailsBySchoolId(ips.getSchoolId());
			if (sd != null || sd.getMobile_number() != null) {
				schoolNum = sd.getMobile_number();
				isValidkey = AESEncryption.validatePassword(schoolNum, accesskey);
				log.debug("isValidkey" + isValidkey);

			} else {
				activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
						type + "-" + IPSConstants.BERR04_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR04_MSG, IPSConstants.STATUSCODE_04,
						type);
				return responseJSON;
			}
		}
		if (!isValidkey) {
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
					type + "-" + IPSConstants.BERR22_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
			return responseJSON;
		}

		if (ips != null && (ips.getSessionId() != null && !ips.getSessionId().equals(sessionId))) {
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
					type + "-" + IPSConstants.BERR10_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
			return responseJSON;
		}

		SessionDetailsForIDTransform sessionTocheck = new SessionDetailsForIDTransform();
		sessionTocheck.setSessionId(ips.getSessionId());
		sessionTocheck.setExpiryduration(ips.getSessionExpiry());
		sessionTocheck.setId(ips.getReceiverId());

		// int sessionValidity = 0;
		boolean sessioncheck = CommonUtil.validateSession(sessionId, sessionTocheck);
		if (sessioncheck == false) {
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
					type + "-" + IPSConstants.BERR02_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return responseJSON;
		} else {
			log.info("session is valid, proceed...");

			// get details of ipsreceiverZone for given id
			ips_rz = this.ipsReceiverZoneService.getZoneDetailsForZoneId(zoneId);

			if (ips_rz == null || ips_rz.getZoneId() == null
					|| (ips_rz.getZoneId() != null && ips_rz.getZoneId().equals(""))) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR18_MSG, IPSConstants.STATUSCODE_18,
						type);
				return responseJSON;
			}

			if (ips_rz == null || ips_rz.getReceiverId() == null
					|| (ips_rz.getReceiverId() != null && !ips_rz.getReceiverId().equals(ips.getReceiverId()))) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR16_MSG, IPSConstants.STATUSCODE_16,
						type);
				return responseJSON;
			}
			// receiver_Id= ips_rz.getReceiverId();

			log.info("ipsreceiverZone entry =" + ips_rz.toString());

			String contentType = request.getContentType();
			log.info("contentType in BeconConfigFileUpload" + "\t" + contentType);
			MultipartHttpServletRequest multiHttpReq = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = null;
			if (multiHttpReq.getFileMap().entrySet() != null) {

				Set set = multiHttpReq.getFileMap().entrySet();
				java.util.Iterator i = set.iterator();
				log.info("inside multihttpreq chk....set=" + set.size());

				while (i.hasNext()) {
					Map.Entry me = (Map.Entry) i.next();
					log.info("key=" + me.getKey() + "   value=" + me.getValue());

					String fileName = (String) me.getKey();
					multipartFile = (MultipartFile) me.getValue();
					log.info("Original fileName - " + multipartFile.getOriginalFilename());
					log.info("fileName - " + fileName);
				}
			}

			String fileName = null;
			String extn = null;

			if (multipartFile == null || (multipartFile != null && multipartFile.isEmpty())) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR27_MSG, IPSConstants.STATUSCODE_27,
						type);
				return responseJSON;
			}

			if (multipartFile != null && !(multipartFile.isEmpty())) {
				fileName = multipartFile.getOriginalFilename();
				if (fileName.lastIndexOf(".") != -1) {
					extn = fileName.substring(fileName.lastIndexOf(".") + 1);
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					log.info("FileName" + "\t" + fileName);
					log.info("extn" + "\t" + extn);
				} else {
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
							type + "-" + IPSConstants.BERR14_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.error(activityLog);
					responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR14_MSG, IPSConstants.STATUSCODE_14,
							type);
					return responseJSON;
				}

			} // if check multipartfile=null

			// in size bytes
			log.info("long maxsize=" + MAX_ALLOWED_IPS_FILE_SIZE);
			log.info("multipartFile.getSize()==" + multipartFile.getSize());

			if (multipartFile.getSize() > MAX_ALLOWED_IPS_FILE_SIZE) {
				activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
						type + "-" + IPSConstants.BERR13_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR13_MSG, IPSConstants.STATUSCODE_13,
						type);
				return responseJSON;

			} else if (extn.equalsIgnoreCase("jpg") || extn.equals("jpeg") || extn.equals("png")) {

				// before saving the existing file should be deleted
				if (ips_rz != null && ips_rz.getMapFilename() != null) {
					this.ipsReceiverZoneService.deleteImage(ips_rz.getMapFilename());
				}
				String zonename = ips_rz.getZoneName();

				String updatedfilename = saveImage(multipartFile, receiver_Id, fileName, extn, zonename);
				ips_rz.setMapFilename(updatedfilename);
				// zone entry updation coding
				boolean flag = this.ipsReceiverZoneService.updateIPSReceiverZoneEntry(ips_rz);
				if (flag) {
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputReceiveMac, "Submit",
							type + "-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.info(activityLog);
					responseJSON = ErrorCodesUtil.displaySuccessJSON(IPSConstants.BSUC01_MSG,
							IPSConstants.STATUSCODE_01, type);
					return responseJSON;
				}

			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR14_MSG, IPSConstants.STATUSCODE_14,
						type);
				return responseJSON;
			}

		} // end of if-else sessioncheck

		return responseJSON;
	}// end of method

	private String saveImage(MultipartFile file, int receiver_Id, String filename, String extn, String zonename) {

		String updatedfileName = null;

		if (file != null) {

			File f = null;
			boolean flag = false;
			try {
				String os = System.getProperty("os.name");
				if (os.contains("Windows"))
					dbDateTime = dbDateTime.replace(":", "-");
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);
				updatedfileName = receiver_Id + "_" + zonename + "_" + filename + "_" + dateFormat.format(date).trim()
						+ "." + extn;
				log.info("dir path=" + this.configProperties.getProperty("ipsimages.upload.path"));
				log.info("updatedfileName path=" + this.configProperties.getProperty("ipsimages.upload.path") + "/"
						+ updatedfileName);

				f = new File(webUtility.createNewFolder(
						this.configProperties.getProperty("ipsimages.upload.path") + "/" + updatedfileName));
				flag = f.createNewFile();
				if (f != null)
					log.info("stored file path=" + f.getAbsolutePath());

				log.info("Beacon Zone fileCreated>>" + "\t" + flag);
				log.info("f.getPath()  Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
				file.transferTo(f);
			} catch (Exception e) {
				log.debug("Exception occurrred ::::::::::::::::: File already exists" + e);
			}

		} // if file is null check

		return updatedfileName;
	}// end of saveimg function

	@RequestMapping(value = "/IPS/UpdateIPSReceiverZone/{token}/{receiver_Id}/{zone_Id}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String updateIPSReceiverZone(@PathVariable("token") String sessionId,
			@PathVariable("receiver_Id") Integer receiver_Id, @PathVariable("zone_Id") Integer zone_Id,
			HttpServletRequest request) throws IOException {

		log.info("Inside IPSReceiverZoneController.updateIPSReceiverZone()");

		String responseJSON = null;
		IPSReceiver ips = null;
		IPSReceiverZone ips_rz = null;
		IPSReceiverZoneModel zm = new IPSReceiverZoneModel();

		String type = "IPSReceiverZone.updateIPSReceiverZone";

		String zonename = request.getParameter("zoneName");

		log.info("zonename=" + zonename);

		if (receiver_Id == null || receiver_Id == 0
				|| (sessionId == null || (sessionId != null && sessionId.equals("")))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR07_MSG, IPSConstants.STATUSCODE_07, type);
			return responseJSON;
		}

		if (zonename == null || zonename.equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		} else {
			zm.setZoneName(zonename);
		}

		// get details of ipsreceiver for given id
		ips = this.ipsReceiverService.getIPSReceiverDetailsForID(receiver_Id);

		// get details of ipsreceiverZone for given id
		ips_rz = this.ipsReceiverZoneService.getZoneDetailsForZoneId(zone_Id);

		if (ips == null || ips.getReceiverId() == null
				|| (ips.getReceiverId() != null && !ips.getReceiverId().equals(receiver_Id))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR08_MSG, IPSConstants.STATUSCODE_08, type);
			return responseJSON;
		}

		if (ips_rz == null || ips_rz.getZoneId() == null
				|| (ips_rz.getZoneId() != null && !ips_rz.getZoneId().equals(zone_Id))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR18_MSG, IPSConstants.STATUSCODE_18, type);
			return responseJSON;
		}

		if (ips_rz == null || ips_rz.getReceiverId() == null
				|| (ips_rz.getReceiverId() != null && !ips_rz.getReceiverId().equals(receiver_Id))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR16_MSG, IPSConstants.STATUSCODE_16, type);
			return responseJSON;
		}

		if (ips.getSessionId() == null || (ips.getSessionId() != null && !ips.getSessionId().equals(sessionId))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
			return responseJSON;
		}

		log.info("ipsreceiver entry =" + ips.toString());
		log.info("ipsreceiverZone entry =" + ips_rz.toString());

		// zm.setReceiverId(receiver_Id);
		// zm.setZoneId(zone_Id);

		String contentType = request.getContentType();
		log.info("contentType in BeconConfigFileUpload" + "\t" + contentType);
		MultipartHttpServletRequest multiHttpReq = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = null;
		if (multiHttpReq.getFileMap().entrySet() != null) {

			Set set = multiHttpReq.getFileMap().entrySet();
			java.util.Iterator i = set.iterator();
			log.info("inside multihttpreq chk....set=" + set.size());

			while (i.hasNext()) {
				Map.Entry me = (Map.Entry) i.next();
				log.info("key=" + me.getKey() + "   value=" + me.getValue());

				String fileName = (String) me.getKey();
				multipartFile = (MultipartFile) me.getValue();
				log.info("Original fileName - " + multipartFile.getOriginalFilename());
				log.info("fileName - " + fileName);
			}
		}

		String fileName = null;
		String extn = null;

		if (multipartFile != null && !(multipartFile.isEmpty())) {
			fileName = multipartFile.getOriginalFilename();
			extn = fileName.substring(fileName.lastIndexOf(".") + 1);
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
			log.info("FileName" + "\t" + fileName);
			log.info("extn" + "\t" + extn);

			// in size bytes
			log.info("long maxsize=" + MAX_ALLOWED_IPS_FILE_SIZE);
			log.info("multipartFile.getSize()==" + multipartFile.getSize());

			if (multipartFile.getSize() > MAX_ALLOWED_IPS_FILE_SIZE) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR13_MSG, IPSConstants.STATUSCODE_13,
						type);
				return responseJSON;

			} else if (extn.equalsIgnoreCase("jpg") || extn.equals("jpeg") || extn.equals("png")) {

				// before saving the existing file should be deleted

				String updatedfilename = saveImage(multipartFile, receiver_Id, fileName, extn, zonename);
				ips_rz.setMapFilename(updatedfilename);

			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR14_MSG, IPSConstants.STATUSCODE_14,
						type);
				return responseJSON;
			}

		} // if check multipartfile=null

		SessionDetailsForIDTransform sessionTocheck = new SessionDetailsForIDTransform();
		sessionTocheck.setSessionId(ips.getSessionId());
		sessionTocheck.setExpiryduration(ips.getSessionExpiry());
		sessionTocheck.setId(ips.getReceiverId());

		int sessionValidity = 0;
		boolean check = CommonUtil.validateSession(receiver_Id, sessionId, sessionValidity, sessionTocheck,
				ipsLoginValidity);
		if (check) {
			// updation coding
			boolean flag = this.ipsReceiverZoneService.updateIPSReceiverZoneEntry(ips_rz);
			if (flag) {
				// String statusMessage = "API Request Success ";
				responseJSON = ErrorCodesUtil.displaySuccessJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01,
						type);
				return responseJSON;
			}

		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return responseJSON;
		}
		return responseJSON;

	}// end of edit method

}
