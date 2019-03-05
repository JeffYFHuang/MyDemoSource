package com.liteon.icgwearable.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.IPSReceiver;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverDevice;
import com.liteon.icgwearable.hibernate.entity.IPSReceiverZone;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.model.BeaconDeviceEventsModel;
import com.liteon.icgwearable.model.IPSLoginDataModel;
import com.liteon.icgwearable.model.IPSReceiverCSVModel;
import com.liteon.icgwearable.model.IPSReceiverModel;
import com.liteon.icgwearable.security.AESEncryption;
import com.liteon.icgwearable.service.AccountService;
import com.liteon.icgwearable.service.ActivityLogService;
import com.liteon.icgwearable.service.EventsService;
import com.liteon.icgwearable.service.IPSReceiverDeviceService;
import com.liteon.icgwearable.service.IPSReceiverService;
import com.liteon.icgwearable.service.IPSReceiverZoneService;
import com.liteon.icgwearable.service.SchoolService;
import com.liteon.icgwearable.transform.SessionDetailsForIDTransform;
import com.liteon.icgwearable.util.CommonUtil;
import com.liteon.icgwearable.util.Constant;
import com.liteon.icgwearable.util.ErrorCodesUtil;
import com.liteon.icgwearable.util.IPSConstants;
import com.liteon.icgwearable.util.IPSReceiverCSVToJavaUtil;
import com.liteon.icgwearable.util.JSONUtility;
import com.liteon.icgwearable.util.WebUtility;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

@RestController
public class IPSReceiverController {

	private static Logger log = Logger.getLogger(IPSReceiverController.class);

	@Autowired
	private IPSReceiverService ipsReceiverService;

	@Autowired
	private IPSReceiverZoneService ipsReceiverZoneService;

	@Autowired
	private IPSReceiverDeviceService ipsReceiverDeviceService;

	@Autowired
	private EventsService eventsService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private SchoolService schoolService;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	HttpServletResponse response;

	@Value("${IPS_Login_Validity}")
	private String ipsLoginValidity;
	@Value("${db.dateTime}")
	private String dbDateTime;
	@Value("${uuid.token.mapping}")
	private String uuidTokenMapping;
	@Value("${token.unavailable}")
	private String tokenUnavailable;
	@Value("${mac.token.mapping}")
	private String macTokenMapping;

	@Resource(name = "configProperties")
	private Properties configProperties;

	WebUtility webUtility = WebUtility.getWebUtility();

	@Autowired
	private ActivityLogService activityLogs;

	@RequestMapping(value = "/IPS/GenerateKey/{schoolPhoneNum}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String generateKey(@PathVariable("schoolPhoneNum") String schoolPhoneNum) {

		log.info("Inside IPSReceiverController.generateKey()");
		String responseJson = null;
		String accesskey = null;

		String type = "IPSReceiver.IPSReceiverLogin";

		try {
			accesskey = AESEncryption.generatePasswordHash(schoolPhoneNum);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (accesskey != null) {

			responseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
					+ IPSConstants.STATUSCODE_01 + "\",\"StatusMessage\": \"" + IPSConstants.BSUC01_MSG
					+ "\"}, \"Result\":  { \"GeneratedKey\":\"" + accesskey + "\",\"school_PhoneNum\":\""
					+ schoolPhoneNum + "\"}}}";
			;

			return responseJson;

		} else {
			responseJson = ErrorCodesUtil.displayErrorJSON("generated AccessKey is null", "ERRO1", type);
			return responseJson;
		}
	}

	@RequestMapping(value = "/IPS/IPSReceiverLogin", method = RequestMethod.POST, consumes = {
			"application/json" }, produces = { "application/json" })
	public String ipsReceiverLogin(@RequestBody IPSLoginDataModel dm)
			throws NoSuchAlgorithmException, InvalidKeySpecException, UnknownHostException {
		// @PathVariable("access_key") String
		// accesskey,@PathVariable("ipsReceiverMac") String ipsReceiverMac
		log.info("Inside IPSReceiverController.IPSReceiverLogin()");

		String type = "IPSReceiver.IPSReceiverLogin";

		String responseJSON = null;
		String sessionID = null;
		int receiver_Id;
		int school_Id;
		String schoolNum = null;
		boolean isValidkey = false;
		String accesskey = null;
		String ipsReceiverMac = null;
		IPSReceiver ips = null;
		String sessionIDfromDb = null;
		Date validityFromDb = null;
		SessionDetailsForIDTransform sessionTocheck = null;

		String json = JSONUtility.convertObjectToJson(dm);
		log.info("Device Model Json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR03_MSG, IPSConstants.STATUSCODE_03, type);
			return responseJSON;
		}

		ActivityLog activityLog = null;
		String ipaddress = CommonUtil.getHostIpaddress();

		log.info("Inside IPSReceiverController.IPSReceiverLogin():::" + dm.toString());

		if (dm.getAccesskey() == null || dm.getAccesskey().equals("") || dm.getIps_receiver_mac() == null
				|| dm.getIps_receiver_mac().equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		}

		ipsReceiverMac = dm.getIps_receiver_mac();
		accesskey = dm.getAccesskey();

		ips = this.ipsReceiverService.getIPSReceiverEntryForMac(ipsReceiverMac);

		if (ips == null || (ips != null && ips.getReceiverId() == 0)) {
			// not present
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR06_MSG, IPSConstants.STATUSCODE_06, type);
			return responseJSON;
		} else {
			receiver_Id = ips.getReceiverId();
			school_Id = ips.getSchoolId();
			sessionIDfromDb = ips.getSessionId();
			validityFromDb = ips.getSessionExpiry();
			log.info("receiver_id=" + receiver_Id + " school_Id=" + school_Id + "  sessionIDfromDb=" + sessionIDfromDb
					+ " validityFromDb" + validityFromDb);
		}

		if (ips != null && ips.getReceiverId() > 0 && ips.getSessionId() != null) {

			sessionTocheck = new SessionDetailsForIDTransform();
			sessionTocheck.setSessionId(ips.getSessionId());
			sessionTocheck.setExpiryduration(ips.getSessionExpiry());
			sessionTocheck.setId(ips.getReceiverId());

		}

		if (school_Id > 0) {
			SchoolDetails sd = this.schoolService.getSchoolDetailsBySchoolId(school_Id);
			if (sd != null && sd.getMobile_number() == null) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR04_MSG, IPSConstants.STATUSCODE_04,
						type);
				return responseJSON;
			}
			if (sd != null && sd.getMobile_number() != null && accountService.checkAccountIDExist(school_Id)) {
				schoolNum = sd.getMobile_number();
				log.info("schoolNum" + "\t" + schoolNum);
				log.info("accesskey" + "\t" + accesskey);
				isValidkey = AESEncryption.validatePassword(schoolNum, accesskey);
				log.debug("isValidkey=" + isValidkey);

			}
		}

		if (isValidkey == false) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
			return responseJSON;
		}

		int sessionValidity = Integer.valueOf(ipsLoginValidity);
		if (receiver_Id > 0 && isValidkey) {

			if (sessionIDfromDb != null && sessionIDfromDb.trim().length() > 0
					&& CommonUtil.checkSessionValidity(sessionTocheck).equals("VALID")) {
				// oldsession to sessionId
				sessionID = sessionTocheck.getSessionId();
				sessionValidity = this.ipsReceiverService.getSessionValidityInMinutes(sessionID, receiver_Id);
				log.info("inside IPSIPSReceiverController when sessionid in DB check,  sessionID::" + sessionID
						+ "   sessionValidity=" + sessionValidity);
				if (sessionValidity == 0) {
					this.httpSession.invalidate();
					sessionID = this.httpSession.getId();
					sessionValidity = Integer.valueOf(ipsLoginValidity);
					this.ipsReceiverService.updateIPSReceiverBySessionId(receiver_Id, sessionID);
				}
			} else {
				// when receiver_Id present
				this.httpSession.invalidate();
				sessionID = this.httpSession.getId();
				log.info("IPSIPSReceiverController when no sessionid in DB sessionID::" + sessionID
						+ "   sessionValidity=" + sessionValidity);

				int result = this.ipsReceiverService.updateIPSReceiverBySessionId(receiver_Id, sessionID);
				log.info("IPSIPSReceiverController upated sessionid row result::" + result);
			}
		}
		activityLog = CommonUtil.formulateActivityLogs("mac_address=" + ipsReceiverMac, "BeaconLogin",
				type + "-" + IPSConstants.BERR12_MSG, ipaddress, "Beacon", "Beacon");
		activityLogs.info(activityLog);
		responseJSON = ErrorCodesUtil.displayIPSLoginJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01, type,
				sessionID, sessionValidity);
		return responseJSON;

	}// end of login method

	@RequestMapping(value = "/IPS/DeleteIPSReceiver/{token}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String deleteIPSReceiverDetails(@PathVariable("token") String sessionId, HttpServletRequest request)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		log.info("Inside IPSReceiverController.deleteIPSReceiverDetails()");

		String type = "IPSReceiver.deleteIPSReceiverDetails";

		String responseJSON = null;
		int receiver_Id;
		int school_Id;
		String inputMac = null;
		String schoolNum = null;
		boolean isValidkey = false;
		String accesskey = null;
		String statusmsg = "Delete IPS Receiver File is uploaded successfully";

		ActivityLog activityLog = null;
		String ipaddress = CommonUtil.getHostIpaddress();

		log.info("accesskey=" + request.getParameter("accesskey") + "  ips_receiver_mac="
				+ request.getParameter("ips_receiver_mac"));

		if (request.getParameter("accesskey") == null || request.getParameter("accesskey").equals("")
				|| request.getParameter("ips_receiver_mac") == null
				|| request.getParameter("ips_receiver_mac").equals("")
				|| (sessionId == null || (sessionId != null && sessionId.equals("")))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		} else {
			accesskey = request.getParameter("accesskey");
			inputMac = request.getParameter("ips_receiver_mac");
			log.info("accesskey::" + accesskey);
		}

		IPSReceiver ips = this.ipsReceiverService.getIPSReceiverEntryForMac(inputMac);

		if (ips == null || ips.getReceiverId() == null || (ips.getReceiverId() != null && ips.getReceiverId() == 0)) {
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
					type + "-" + IPSConstants.BERR12_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);

			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR12_MSG, IPSConstants.STATUSCODE_12, type);
			return responseJSON;
		} else {
			receiver_Id = ips.getReceiverId();
			school_Id = ips.getSchoolId();
			log.info("receiver_Id=" + receiver_Id + " ,school_Id=" + school_Id);
		}

		if (school_Id > 0) {
			SchoolDetails sd = this.schoolService.getSchoolDetailsBySchoolId(school_Id);
			boolean flag = accountService.checkAccountIDExist(school_Id);

			if (sd != null || (sd.getMobile_number() != null && !sd.getMobile_number().equals("")) && flag) {
				schoolNum = sd.getMobile_number();
				isValidkey = AESEncryption.validatePassword(schoolNum, accesskey);
				log.debug("isValidkey" + isValidkey);

			} else {
				activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
						type + "-" + IPSConstants.BERR04_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR04_MSG, IPSConstants.STATUSCODE_04,
						type);
				return responseJSON;
			}

		} // end of schoolid check

		if (!isValidkey) {
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
					type + "-" + IPSConstants.BERR22_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
			return responseJSON;
		}

		if (ips != null && (ips.getSessionId() != null && !ips.getSessionId().equals(sessionId))) {
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
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
			activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
					type + "-" + IPSConstants.BERR02_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return responseJSON;
		} else {
			log.info("session is valid, proceed...");
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

					String file_name = (String) me.getKey();
					multipartFile = (MultipartFile) me.getValue();
					log.info("Original fileName - " + multipartFile.getOriginalFilename());
					log.info("request parameter fileName - " + file_name);
				}
			}

			String fileName = null;
			String extn = null;

			// String newFileName = multipartFile.getOriginalFilename();
			// log.info("newFileName >>>>>>>>>> " + newFileName);

			if (multipartFile != null && !(multipartFile.isEmpty())) {
				fileName = multipartFile.getOriginalFilename();
				if (fileName.lastIndexOf(".") != -1) {
					extn = fileName.substring(fileName.lastIndexOf(".") + 1);
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					log.info("FileName" + "\t" + fileName);
					log.info("extn" + "\t" + extn);
				} else {
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
							type + "-" + IPSConstants.BERR21_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.error(activityLog);
					responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR21_MSG, IPSConstants.STATUSCODE_21,
							type);
					return responseJSON;
				}

				if (!extn.equals("csv")) {
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
							type + "-" + IPSConstants.BERR21_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.error(activityLog);
					responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR21_MSG, IPSConstants.STATUSCODE_21,
							type);
					return responseJSON;
				}
			} else {
				activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
						type + "-" + IPSConstants.BERR27_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR27_MSG, IPSConstants.STATUSCODE_27,
						type);
				return responseJSON;
			} // end of if multipartFile null check

			log.info("inside multipartfile check successfull......");
			List<String> resultList = new ArrayList<String>();

			List<Integer> devicelist = new ArrayList<Integer>();
			List<Integer> zonelist = new ArrayList<Integer>();
			List<Integer> receiverlist = new ArrayList<Integer>();

			// saving the csv file t
			File f = this.saveCsv(multipartFile, school_Id);

			// convert cdv file into java object
			List<IPSReceiverCSVModel> ipsList = IPSReceiverCSVToJavaUtil.convertDeleteIPSReceiverCsvToJava(f);

			if (ipsList == null) {
				activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
						type + "-" + IPSConstants.BERR26_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR26_MSG, IPSConstants.STATUSCODE_26,
						type);
				return responseJSON;
			}
			if (ipsList != null) {

				// getting only receiver device id from ipsList
				ipsList.stream()
						.filter(irm -> irm.getRecordType() != null && irm.getRecordType().equalsIgnoreCase("device"))
						.forEach(irm -> {
							if (irm.getRecordId() != null)
								devicelist.add(Integer.valueOf(irm.getRecordId().trim()));
						});

				log.info("--devicelist--" + devicelist + " devicelist.size()=" + devicelist.size());

				ipsList.stream().filter(irm -> irm.getRecordType().equalsIgnoreCase("Zone")).forEach(irm -> {
					if (irm.getRecordId() != null)
						zonelist.add(Integer.valueOf(irm.getRecordId().trim()));
				});

				log.info("--zonelist--" + zonelist + " zonelist.size()=" + zonelist.size());

				ipsList.stream().filter(irm -> irm.getRecordType().equalsIgnoreCase("receiver")).forEach(irm -> {
					if (irm.getRecordId() != null)
						receiverlist.add(Integer.valueOf(irm.getRecordId().trim()));
				});
				log.info("--receiverlist--" + receiverlist + " receiverlist.size()=" + receiverlist.size());

				if (receiverlist.size() > 1) {
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
							type + "-" + IPSConstants.BERR23_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.error(activityLog);
					responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR23_MSG, IPSConstants.STATUSCODE_23,
							type);
					return responseJSON;

				}
				if (receiverlist.size() == 1 && receiverlist.get(0) != receiver_Id) {
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
							type + "-" + IPSConstants.BERR19_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.error(activityLog);
					responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR19_MSG, IPSConstants.STATUSCODE_19,
							type);
					return responseJSON;
				} else if (receiverlist.size() == 1 && receiverlist.get(0) == receiver_Id) {
					// when receiverlist.size()==1 && receiverlist.get(0)==
					// receiver_Id, delete the receiver
					log.info("inside receiverlist chk with given receiverId..");
					resultList = deleteIpsReceiver(receiver_Id, ipaddress);
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
							type + "-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.info(activityLog);
					resultList.add(
							"{\" Warning-msg\":\" Ignoring zone and devices entries, if provided as Input, since deleted receiver only \"}");
					responseJSON = displayDeleteIPSReceiverResultJSON(type, IPSConstants.STATUSCODE_01, statusmsg,
							resultList);
					return responseJSON;
				}

				if (receiverlist.size() == 0) {
					log.info("inside receiverlist==0 chk");
					// no receiver entry in file but given devices, zones are
					// not mentioned
					if (devicelist.size() > 0) {
						resultList = deleteIpsReceiverDevices(receiver_Id, devicelist, "directlyMainFunction",
								ipaddress);
					}

					// no receiver entry in file only has zones and devices
					if (zonelist.size() > 0) {
						resultList = deleteIpsReceiverZones(receiver_Id, zonelist, false, ipaddress);
					}
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
							type + "-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.info(activityLog);
					resultList.add(
							"{\" Warning-msg\":\" Ignoring receiver devices entries, if provided as Input, since deleted zone entry \"}");
					responseJSON = displayDeleteIPSReceiverResultJSON(type, IPSConstants.STATUSCODE_01, statusmsg,
							resultList);
					return responseJSON;
				}

				if (receiverlist.size() == 0 && zonelist.size() == 0) {
					// no receiver and zone entries in file only has devices
					resultList = deleteIpsReceiverDevices(receiver_Id, devicelist, "directlyMainFunction", ipaddress);
					activityLog = CommonUtil.formulateActivityLogs("mac_address=" + inputMac, "Delete",
							type + "-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon", "Beacon");
					activityLogs.info(activityLog);
					responseJSON = displayDeleteIPSReceiverResultJSON(type, IPSConstants.STATUSCODE_01, statusmsg,
							resultList);
					return responseJSON;
				}

			} // end of if list null check

			// responseJSON = displayDeleteIPSReceiverResultJSON(type,
			// IPSConstants.STATUSCODE_01,statusmsg, resultList);

		} // end of sessioncheck else

		return responseJSON;
	} // end of deleteipsreceiver method

	private List<String> deleteIpsReceiver(int receiver_Id, String ipaddress) {

		List<String> resultList = new ArrayList();
		List<Integer> devicelistFromDb1 = new ArrayList<Integer>(); // device
																	// under
																	// receiver
		boolean fromReceiver = true;
		String calledFrom = "deleteIpsReceiver";

		// getting receiver device ids under receiver
		devicelistFromDb1 = this.ipsReceiverDeviceService.getReceiverDeviceIds(receiver_Id, null);
		log.info("inside deleteIpsReceiver, devicelistFromDb1 under receiver==" + devicelistFromDb1);
		// deletion of devices before deleting zones----
		if (devicelistFromDb1 != null && devicelistFromDb1.size() > 0)
			resultList = deleteIpsReceiverDevices(receiver_Id, devicelistFromDb1, calledFrom, ipaddress); // fromReceiver=true

		resultList = deleteIpsReceiverZones(receiver_Id, null, fromReceiver, ipaddress);

		boolean deleteflag = this.ipsReceiverService.deleteIPSReceiverEntry(receiver_Id);
		if (!deleteflag) {
			resultList.add("{\" Receiver_Id\":" + receiver_Id + ",\"" + IPSConstants.STATUSCODE_11 + "\":\""
					+ IPSConstants.BERR11_MSG + "\"}");
		} else {

			ActivityLog activityLog = CommonUtil.formulateActivityLogs("receiver_Id=" + receiver_Id, "Delete",
					"IPSReceiver.deleteIPSReceiverDetails-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.critical(activityLog);

			resultList.add("{\" Receiver_Id\":" + receiver_Id + ",\"" + IPSConstants.STATUSCODE_01 + "\":\"Deletion "
					+ IPSConstants.BSUC01_MSG + " for given ReceiverId\"}");
		}
		return resultList;

	} // end of method

	private List<String> deleteIpsReceiverZones(int receiver_Id, List<Integer> zonelist, boolean fromReceiver,
			String ipaddress) {

		log.info("zonelist===" + zonelist);

		int deletecount = 0;
		List<String> resultList = new ArrayList();
		List<Integer> devicelistFromDb = new ArrayList<Integer>(); // device
																	// under
																	// zones

		List<Integer> zonelistFromDb = new ArrayList<Integer>();
		List<IPSReceiverZone> ipsZones = null;
		
		// list to hold the device event Ids
		List<Integer> eventsIdList = new ArrayList<Integer>();

		if (zonelist == null) {
			log.info("zonelist null chk");
			ipsZones = this.ipsReceiverZoneService.getAllZoneDetailsForZoneIds(null, receiver_Id);
		} else {
			// no receiver in file and only zones and devices are mentioned
			log.info("zonelist else chk");
			ipsZones = this.ipsReceiverZoneService.getAllZoneDetailsForZoneIds(zonelist, receiver_Id);
		}

		// getting all zone details to delete mapfile from path
		List<String> mapfiles = new ArrayList<String>();

		if (ipsZones != null && ipsZones.size() != 0) {
			for (IPSReceiverZone ipsZone : ipsZones) {
				zonelistFromDb.add(ipsZone.getZoneId());
				if (ipsZone.getMapFilename() != null) {
					String filename = this.configProperties.getProperty("ips.upload.path") + "/"
							+ ipsZone.getMapFilename();
					mapfiles.add(filename);
				}
			} // end of for
			log.info("zonelistFromDb==" + zonelistFromDb);

			// getting receiver device ids for zoneIdlist
			devicelistFromDb = this.ipsReceiverDeviceService.getReceiverDeviceIds(receiver_Id, zonelistFromDb);
			log.info("devicelistFromDb==" + devicelistFromDb);
		} else {
			// null check of ipsZone
			resultList.add("{\"" + IPSConstants.STATUSCODE_24 + "\":\"" + IPSConstants.BERR24_MSG + " for ZoneId\"}");
			return resultList;
		}

		if (zonelistFromDb != null && zonelistFromDb.size() > 0 && ipsZones != null && ipsZones.size() > 0) {
			log.info("deleteIpsReceiverZones,  zonelistFromDb=" + zonelistFromDb);
			
			// delete device events associated with a particular IPS zone
			// getting eventIds for receiver zone list
			eventsIdList = this.eventsService.getDeviceEventIdsForGivenIdList(zonelistFromDb);
			if (eventsIdList != null && eventsIdList.size() > 0) {
				// Update the zone Id to "0" to not lose
				// device events that occurred in that particular zone
				int count = this.eventsService.updateZoneIdForDeviceEventIds(eventsIdList);
				resultList.add("{\"Total Events with receiver DeviceIds\":" + eventsIdList.size()
						+ ",\"Total Events Deleted\":" + count + "}");
				if (count != eventsIdList.size() || count == 0) {
					log.debug("device events not deleted properly for receiver_Id =" + receiver_Id);
				}

			} // end of eventsIdlist chk

			// deletion of devices before deleting zones----
			resultList = deleteIpsReceiverDevices(receiver_Id, devicelistFromDb, "deleteIpsReceiverZones", ipaddress);

			// delete entries from ipsReceiver zone table
			deletecount = this.ipsReceiverZoneService.deleteAllZonesInZoneIdList(zonelistFromDb, receiver_Id);
			
			if (deletecount != zonelistFromDb.size() || deletecount == 0) {
				log.info("total zones deleted is " + deletecount + " under receiver=" + zonelistFromDb
						+ ",so plz delete mapfiles if exists manually");
				resultList.add("{\"Total zones under Particular receiver\":" + zonelistFromDb.size()
						+ ",\"Total zones Deleted\":" + deletecount + ",\"" + IPSConstants.STATUSCODE_20 + "\":\""
						+ IPSConstants.BERR20_MSG + "\"}");

			} else {
				// if deleteflag is true then entry in DB is deleted, so delete
				// mapfile from path.
				if (mapfiles != null) {
					for (String mapimg : mapfiles) {
						try {

							File f1 = new File(mapimg);
							if (f1 != null)
								f1.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}

					} // end of forloop
				}
				ActivityLog activityLog = CommonUtil.formulateActivityLogs("receiver_Id=" + receiver_Id, "Delete",
						"IPSReceiver.deleteIPSReceiverDetails-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon",
						"Beacon");
				activityLogs.critical(activityLog);
				resultList.add("{\"Total zones  under given receiver\":" + zonelistFromDb.size() + ",\"Total zones Deleted\":"
						+ deletecount + ",\"" + IPSConstants.STATUSCODE_01 + "\":\"Deletion" + IPSConstants.BSUC01_MSG
						+ "\"}");

			} // end of else part

		} else {

			resultList.add("{\"" + IPSConstants.STATUSCODE_24 + "\":\" " + IPSConstants.BERR24_MSG + "\"}");
		} // end of zonelist.size check

		return resultList;
	}// end of method

	private List<String> deleteIpsReceiverDevices(int receiver_Id, List<Integer> devicelist, String calledFrom,
			String ipaddress) {
		int deletecount = 0;
		List<Integer> eventsIdList = new ArrayList<Integer>();
		List<String> resultList = new ArrayList();

		if (devicelist == null || devicelist.size() == 0) {
			resultList
					.add("{\"" + IPSConstants.STATUSCODE_24 + "\":\"" + IPSConstants.BERR24_MSG + "  for DeviceId \"}");
			return resultList;
		}

		if (devicelist != null || devicelist.size() > 0) {
			log.info("deleteIpsReceiverDevices,  devicelist=" + devicelist);

			// device device with device id in list and matches receiver_id
			deletecount = this.ipsReceiverDeviceService.deleteAllReceiverDevicesForIds(devicelist, receiver_Id);

			if (calledFrom.equals("deleteIpsReceiver")) {
				if (deletecount != devicelist.size() || deletecount == 0) {
					resultList.add("{\"Total Receiver Devices submitted for deletion under receiver\":"
							+ devicelist.size() + ",\"Total Device Deleted under receiver\":" + deletecount + ",\""
							+ IPSConstants.STATUSCODE_11 + "\":\"" + IPSConstants.BERR11_MSG + "\"}");
				} else {
					resultList.add("{\"Total Receiver Devices submitted for deletion under receiver\":"
							+ devicelist.size() + ",\"Total Device Deleted under receiver\":" + deletecount + ",\""
							+ IPSConstants.STATUSCODE_01 + "\":\"Deletion" + IPSConstants.BSUC01_MSG
							+ " for given recevierDevicelist\"}");
				}
			} else if (calledFrom.equals("deleteIpsReceiverZones")) {
				if (deletecount != devicelist.size() || deletecount == 0) {
					resultList.add("{\"Total Receiver Devices submitted for deletion under zone\":" + devicelist.size()
							+ ",\"Total Device Deleted under zone\":" + deletecount + ",\"" + IPSConstants.STATUSCODE_11
							+ "\":\"" + IPSConstants.BERR11_MSG + "\"}");
				} else {
					ActivityLog activityLog = CommonUtil.formulateActivityLogs("receiver_Id=" + receiver_Id, "Delete",
							"IPSReceiver.deleteIPSReceiverDetails-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon",
							"Beacon");
					activityLogs.critical(activityLog);
					resultList.add("{\"Total Receiver Devices submitted for deletion under zone\":" + devicelist.size()
							+ ",\"Total Device Deleted under zone\":" + deletecount + ",\"" + IPSConstants.STATUSCODE_01
							+ "\":\"Deletion" + IPSConstants.BSUC01_MSG + " for given recevierDevicelist\"}");
				}
			} else if (calledFrom.equals("directlyMainFunction")) {
				if (deletecount != devicelist.size() || deletecount == 0) {
					resultList.add("{\"Total Receiver Devices submitted for deletion as input\":" + devicelist.size()
							+ ",\"Total Device Deleted under zone\":" + deletecount + ",\"" + IPSConstants.STATUSCODE_11
							+ "\":\"" + IPSConstants.BERR11_MSG + "\"}");
				} else {
					ActivityLog activityLog = CommonUtil.formulateActivityLogs("receiver_Id=" + receiver_Id, "Delete",
							"IPSReceiver.deleteIPSReceiverDetails-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon",
							"Beacon");
					activityLogs.critical(activityLog);
					resultList.add("{\"Total Receiver Devices submitted for deletion as input\":" + devicelist.size()
							+ ",\"Total Device Deleted under zone\":" + deletecount + ",\"" + IPSConstants.STATUSCODE_01
							+ "\":\"Deletion" + IPSConstants.BSUC01_MSG + " for given recevierDevicelist\"}");
				}
			} // end of calledFrom chk

		}
		return resultList;

	}// end of methodS

	@RequestMapping(value = "/IPS/SubmitIPSReceiverDetails", method = RequestMethod.POST, produces = {
			"application/json" })
	public String submitIPSReceiverDetails(HttpServletRequest request)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		log.info("Inside IPSReceiverController.SubmitIPSReceiverDetails()");

		String type = "IPSReceiver.SubmitIPSReceiverDetails";

		String responseJSON = null;
		// String sessionID = null;
		int receiver_Id;
		int school_Id;
		String schoolNum = null;
		boolean isValidkey = false;
		String accesskey = null;
		List<IPSReceiverCSVModel> list = null;
		ActivityLog activityLog = null;
		String ipaddress = CommonUtil.getHostIpaddress();

		log.info("accesskey=" + request.getParameter("accesskey") + "  school_id=" + request.getParameter("school_id"));

		if (request.getParameter("accesskey") == null || request.getParameter("accesskey").equals("")
				|| request.getParameter("school_id") == null || request.getParameter("school_id").equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		} else {
			school_Id = Integer.parseInt(request.getParameter("school_id"));
			accesskey = request.getParameter("accesskey");
			log.info("accesskey::" + accesskey);
		}

		if (school_Id > 0) {
			if (!(accountService.checkAccountIDExist(school_Id))) {
				log.info("Inside IPSReceiverController.CreateIPSReceiver, school with school_id does not exist");
				activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
						type + "-" + IPSConstants.BERR09_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR09_MSG, IPSConstants.STATUSCODE_09,
						type);
				return responseJSON;
			}

			SchoolDetails sd = this.schoolService.getSchoolDetailsBySchoolId(school_Id);
			boolean flag = accountService.checkAccountIDExist(school_Id);

			if (sd == null || sd.getMobile_number() == null
					|| (sd.getMobile_number() != null && sd.getMobile_number().equals(""))) {
				activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
						type + "-" + IPSConstants.BERR04_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR04_MSG, IPSConstants.STATUSCODE_04,
						type);
				return responseJSON;
			}

			if (sd != null || (sd.getMobile_number() != null && !sd.getMobile_number().equals("")) && flag) {
				schoolNum = sd.getMobile_number();
				log.info("schoolNum=" + schoolNum);
				isValidkey = AESEncryption.validatePassword(schoolNum, accesskey);
				log.debug("isValidkey" + isValidkey);

			}
		}

		if (!isValidkey) {
			activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
					type + "-" + IPSConstants.BERR22_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
			return responseJSON;
		}

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

				String file_name = (String) me.getKey();
				multipartFile = (MultipartFile) me.getValue();
				log.info("Original fileName - " + multipartFile.getOriginalFilename());
				log.info("request parameter fileName - " + file_name);
			}
		}

		String fileName = null;
		String extn = null;

		if (multipartFile != null && !(multipartFile.isEmpty())) {
			fileName = multipartFile.getOriginalFilename();
			if (fileName.lastIndexOf(".") != -1) {
				extn = fileName.substring(fileName.lastIndexOf(".") + 1);
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
				log.info("FileName" + "\t" + fileName);
				log.info("extn" + "\t" + extn);
			} else {
				activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
						type + "-" + IPSConstants.BERR21_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR21_MSG, IPSConstants.STATUSCODE_21,
						type);
				return responseJSON;
			}

			if (!extn.equals("csv")) {
				activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
						type + "-" + IPSConstants.BERR21_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR21_MSG, IPSConstants.STATUSCODE_21,
						type);
				return responseJSON;
			}

		} else {
			activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
					type + "-" + IPSConstants.BERR27_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR27_MSG, IPSConstants.STATUSCODE_27, type);
			return responseJSON;

		} // end of if multipartFile null check

		log.info("inside multipartfile check successfull......");
		List<String> resultList = new ArrayList();

		// saving the csv file t
		File f = this.saveCsv(multipartFile, school_Id);
		try {
		// convert cdv file into java object
		list = IPSReceiverCSVToJavaUtil.convertSubmitIPSReceiverCsvToJava(f);
		log.info("list----------->" + list);
			if (list != null) {
				log.info("inside list chk----------->" + list);
				int i = 1;
				resultList.add("{\"Record-" + i + "\":\"column names record::\"}");
				i++;
				String receiver_mac = null;

				IPSReceiver ipsrec = ipsReceiverService.getIPSReceiverDetailsForSchoolID(school_Id);

				for (IPSReceiverCSVModel ips : list) {
					String recordtype = ips.getRecordType();

					if (recordtype.equalsIgnoreCase("receiver")) {
						String result = this.createOrEditIPSReceiver(ips, school_Id, ipsrec);

						receiver_mac = ips.getReceiverMac();

						log.info("receiver record type result=" + result + "  receiver_mac=" + receiver_mac);
						resultList
								.add("{\"Record-" + i + "\":\"Macaddress=" + ips.getReceiverMac() + "::" + result + "\"}");
						i++;
					}
					if (recordtype.equalsIgnoreCase("zone")) {

						String result = this.createOrEditZone(ips, receiver_mac, ipsrec);
						log.info("zone record type result=" + result);
						resultList.add("{\"Record-" + i + "\":\"ZoneName=" + ips.getZoneName() + "::" + result + "\"}");
						i++;
					}
					if (recordtype.equalsIgnoreCase("device")) {
						String result = this.createOrEditDevice(ips, receiver_mac, ipsrec);
						log.info("device record type result=" + result);
						resultList.add("{\"Record-" + i + "\":\"DeviceUUID=" + ips.getDeviceUUID() + "::" + result + "\"}");
						i++;
					}

				}
			}else {
				log.info("List object is null"+"\t"+list);
				activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
						type + "-" + IPSConstants.BERR26_MSG, ipaddress, "Beacon", "Beacon");
				activityLogs.error(activityLog);
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR26_MSG, IPSConstants.STATUSCODE_26, type);
				return responseJSON;
			}
		}catch(Exception e) {
			log.info("Exception Occured in submitIPSReceiverDetails"+"\t"+e);
			activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
					type + "-" + IPSConstants.BERR26_MSG, ipaddress, "Beacon", "Beacon");
			activityLogs.error(activityLog);
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR26_MSG, IPSConstants.STATUSCODE_26, type);
			return responseJSON;

			// end of if else list null check
		}
	
			

		String msg = " Submit IPS Receiver File is uploaded successfully";
		activityLog = CommonUtil.formulateActivityLogs("school_Id=" + String.valueOf(school_Id), "Submit",
				type + "-" + IPSConstants.BSUC01_MSG, ipaddress, "Beacon", "Beacon");
		activityLogs.info(activityLog);
		responseJSON = displaySubmitIPSReceiverResultJSON(type, IPSConstants.STATUSCODE_01, msg, resultList);

		return responseJSON;

	}// end of SubmitIPSReceiverDetails method

	private String displayDeleteIPSReceiverResultJSON(String type, String statuscode, String statusMsg,
			List<String> resultList) {

		String responseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statuscode + "\",\"StatusMessage\": \"" + statusMsg + "\"}, \"Result\":  { \"DeletionStatus\":  ";

		responseJson = responseJson + resultList + " } } }";

		return responseJson;
	}

	private String displaySubmitIPSReceiverResultJSON(String type, String statuscode, String statusMsg,
			List<String> resultList) {

		String responseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statuscode + "\",\"StatusMessage\": \"" + statusMsg + "\"}, \"Result\":  { \"EachRecordStatus\":  ";

		responseJson = responseJson + resultList + " } } }";

		return responseJson;
	}

	private String createOrEditIPSReceiver(IPSReceiverCSVModel ips, int school_id, IPSReceiver ipsrecForSchoolID) {

		log.info("inside createOrEditIPSReceiver....");
		IPSReceiver receiver = new IPSReceiver();
		String result = null;
		int idFromDb = 0;
		boolean flag = false;
		// IPSReceiverModel ipsReceiverModel = new IPSReceiverModel();

		if (ips.getReceiverMac() == null || ips.getReceiverMac().equals("")) {
			result = IPSConstants.STATUSCODE_07 + "::" + IPSConstants.BERR07_MSG;
			return result;
		}

		if (ips.getReceiverMac() != null) {
			idFromDb = this.ipsReceiverService.checkMacExistWithAnyReceiver(ips.getReceiverMac());
			log.info("createOrEditIPSReceiver idFromDb=" + idFromDb);
		}

		/*
		 * if(idFromDb > 0 ){ result =
		 * IPSConstants.STATUSCODE_15+"::"+IPSConstants.BERR15_MSG ; return
		 * result; }
		 */

		if ((ipsrecForSchoolID != null && ipsrecForSchoolID.getReceiverId() > 0) && idFromDb > 0
				&& ipsrecForSchoolID.getReceiverId() != idFromDb) {
			log.info("createOrEditIPSReceiver ipsrecForSchoolID =" + ipsrecForSchoolID);
			// ipsreceiver id got for given school id should be same as idFromDb
			// else user given invalid input
			result = IPSConstants.STATUSCODE_28 + "::" + IPSConstants.BERR28_MSG;
			return result;

		}

		receiver.setSchoolId(school_id);
		receiver.setReceiverMac(ips.getReceiverMac());

		if ((ipsrecForSchoolID == null || (ipsrecForSchoolID != null && ipsrecForSchoolID.getReceiverId() == 0))
				&& idFromDb == 0) {
			// creation:: ipsreceiver should be newly created
			log.info("createOrEditIPSReceiver creatiion check....");
			if (ips.getReceiverName() != null)
				receiver.setReceiverName(ips.getReceiverName());

			if (ips.getReceiverVersion() != null)
				receiver.setReceiverVersion(ips.getReceiverVersion());

			flag = this.ipsReceiverService.createIPSReceiverEntry(receiver);
			if (flag)
				return IPSConstants.STATUSCODE_01 + "::Creation " + IPSConstants.BSUC01_MSG;

		} else if ((ipsrecForSchoolID != null && ipsrecForSchoolID.getReceiverId() > 0) && idFromDb > 0
				&& ipsrecForSchoolID.getReceiverId() == idFromDb) {
			// Updation:: ipsreceiver should be edited

			log.info("createOrEditIPSReceiver updation check......");
			receiver.setReceiverId(ipsrecForSchoolID.getReceiverId());

			if (ips.getReceiverName() != null)
				receiver.setReceiverName(ips.getReceiverName());
			else
				receiver.setReceiverName(ipsrecForSchoolID.getReceiverName());

			if (ips.getReceiverVersion() != null)
				receiver.setReceiverVersion(ips.getReceiverVersion());
			else
				receiver.setReceiverVersion(ipsrecForSchoolID.getReceiverVersion());

			receiver.setSessionId(ipsrecForSchoolID.getSessionId());
			receiver.setSessionExpiry(ipsrecForSchoolID.getSessionExpiry());
			receiver.setCreated_date(ipsrecForSchoolID.getCreated_date());

			if (ipsrecForSchoolID.getReceiverId() > 0 && idFromDb == 0) {
				log.info(ips.getReceiverMac() + " mac address is to be updated with receiver_id"
						+ ipsrecForSchoolID.getReceiverId());
				// editing mac address condition
				receiver.setReceiverMac(ips.getReceiverMac());
			}

			flag = this.ipsReceiverService.updateIPSReceiverEntry(receiver);
			if (flag)
				return IPSConstants.STATUSCODE_01 + ":: Updation " + IPSConstants.BSUC01_MSG;
			else
				return IPSConstants.STATUSCODE_17 + "::" + IPSConstants.BERR17_MSG;

		} else if ((ipsrecForSchoolID != null && ipsrecForSchoolID.getReceiverId() > 0) && idFromDb == 0) {
			// Updation:: ipsreceiver should be edited with mac address
			// condition

			log.info("createOrEditIPSReceiver updation check......");
			log.info(ips.getReceiverMac() + " mac address is to be updated with receiver_id"
					+ ipsrecForSchoolID.getReceiverId());
			receiver.setReceiverId(ipsrecForSchoolID.getReceiverId());

			if (ips.getReceiverName() != null)
				receiver.setReceiverName(ips.getReceiverName());
			else
				receiver.setReceiverName(ipsrecForSchoolID.getReceiverName());

			if (ips.getReceiverVersion() != null)
				receiver.setReceiverVersion(ips.getReceiverVersion());
			else
				receiver.setReceiverVersion(ipsrecForSchoolID.getReceiverVersion());

			receiver.setSessionId(ipsrecForSchoolID.getSessionId());
			receiver.setSessionExpiry(ipsrecForSchoolID.getSessionExpiry());
			receiver.setCreated_date(ipsrecForSchoolID.getCreated_date());

			receiver.setReceiverMac(ips.getReceiverMac());
			flag = this.ipsReceiverService.updateIPSReceiverEntry(receiver);
			if (flag)
				return IPSConstants.STATUSCODE_01 + ":: Updation " + IPSConstants.BSUC01_MSG;
			else
				return IPSConstants.STATUSCODE_17 + "::" + IPSConstants.BERR17_MSG;
		}

		return result;
	}

	public String createOrEditZone(IPSReceiverCSVModel ips, String receiver_mac, IPSReceiver ipsrecForSchoolID) {
		IPSReceiverZone ipszone = null;
		String result = null;
		int idFromDb = 0;
		boolean flag = false;
		// String macaddress = null;

		if (ips.getReceiverMac() == null || ips.getReceiverMac().equals("") || ips.getZoneName() == null
				|| ips.getZoneName().equals("") || ips.getMapType() == null || ips.getMapType().equals("")) {
			result = IPSConstants.STATUSCODE_07 + "::" + IPSConstants.BERR07_MSG;
			return result;
		}

		log.info("createOrEditZone  macaddr =" + ips.getReceiverMac() + "  zonename=" + ips.getZoneName()
				+ "  receiver_mac=" + receiver_mac);

		// get receiverid from receiver table
		if (ips.getReceiverMac() != null) {
			idFromDb = this.ipsReceiverService.checkMacExistWithAnyReceiver(ips.getReceiverMac());
			log.info("--createOrEditZone idFromDb--" + idFromDb);
			if (idFromDb == 0) {
				result = IPSConstants.STATUSCODE_06 + "::" + IPSConstants.BERR06_MSG;
				return result;
			}

			if (receiver_mac != null && !(ips.getReceiverMac().equals(receiver_mac))) {
				result = IPSConstants.STATUSCODE_08 + "::" + IPSConstants.BERR08_MSG;
				return result;
			}

			if ((ipsrecForSchoolID != null && ipsrecForSchoolID.getReceiverId() > 0) && idFromDb > 0
					&& ipsrecForSchoolID.getReceiverId() != idFromDb) {
				log.info("createOrEditZone ipsrecForSchoolID =" + ipsrecForSchoolID);
				// ipsreceiver id got for given school id should be same as
				// idFromDb else user given invalid input
				result = IPSConstants.STATUSCODE_28 + "::" + IPSConstants.BERR28_MSG;
				return result;

			}

		} // end of if mac null check

		if (idFromDb > 0) {
			// if entry present, get it for unique zone name with in each
			// receiver
			ipszone = this.ipsReceiverZoneService.getZoneDetailsForZoneName(ips.getZoneName(), idFromDb);
		}
		if (ipszone != null) {
			// edit zone with in receiver for
			// ipszone.getZonename().equals(ips.getZoneName())
			if (ips.getMapType() != null
					&& (ips.getMapType().equalsIgnoreCase("full") || ips.getMapType().equalsIgnoreCase("partial")))
				ipszone.setMapType(ips.getMapType());

			if (ips.getBuildingName() != null)
				ipszone.setBuildingName(ips.getBuildingName());
			if (ips.getFloorNum() != null)
				ipszone.setFloorNum(ips.getFloorNum());

			if (ips.getDeleteMapFile() != null && ips.getDeleteMapFile().equalsIgnoreCase("yes")) {
				if (ipszone.getMapFilename() != null)
					this.ipsReceiverZoneService.deleteImage(ipszone.getMapFilename());

				ipszone.setMapFilename(null);
			}
			flag = this.ipsReceiverZoneService.updateIPSReceiverZoneEntry(ipszone);
			if (flag) {
				result = IPSConstants.STATUSCODE_01 + "::Updation " + IPSConstants.BSUC01_MSG;
				return result;
			}

		} else if (idFromDb > 0 && (ipszone == null || ipszone.getReceiverId() == null)) {
			// create
			ipszone = new IPSReceiverZone();

			ipszone.setIpsReceiverId(idFromDb);
			ipszone.setZoneName(ips.getZoneName());
			if (ips.getMapType() != null) {
				if (ips.getMapType().equalsIgnoreCase("full") || ips.getMapType().equalsIgnoreCase("partial")) {
					ipszone.setMapType(ips.getMapType());
					if (ips.getBuildingName() != null)
						ipszone.setBuildingName(ips.getBuildingName());
					if (ips.getFloorNum() != null)
						ipszone.setFloorNum(ips.getFloorNum());
				} else {
					result = IPSConstants.STATUSCODE_25 + "::" + IPSConstants.BERR25_MSG;
					return result;
				}
			}

			flag = this.ipsReceiverZoneService.createIPSReceiverZoneEntry(ipszone);
			if (flag) {
				result = IPSConstants.STATUSCODE_01 + ":: Creation " + IPSConstants.BSUC01_MSG;
				return result;
			}
		}

		return result;
	}// end of method

	public String createOrEditDevice(IPSReceiverCSVModel ips, String receiver_mac, IPSReceiver ipsrecForSchoolID) {
		IPSReceiverZone ipszone = new IPSReceiverZone();
		IPSReceiverDevice ipsdev = new IPSReceiverDevice();
		String result = null;
		int idFromDb = 0;
		boolean flag = false;

		if (ips.getReceiverMac() == null || ips.getReceiverMac().equals("") || ips.getDeviceUUID() == null
				|| ips.getDeviceUUID().equals("")) {
			// not checking zone name mandatory because device under receiver
			// will have zoneid value null
			result = IPSConstants.STATUSCODE_07 + "::" + IPSConstants.BERR07_MSG;
			return result;
		}
		log.info(" createOrEditDevice macaddr =" + ips.getReceiverMac() + "  deviceuuid=" + ips.getDeviceUUID()
				+ " receiver_mac=" + receiver_mac);

		// get receiverid from receiver table
		if (ips.getReceiverMac() != null) {
			idFromDb = this.ipsReceiverService.checkMacExistWithAnyReceiver(ips.getReceiverMac());
			log.info("createOrEditDevice ...idFromDb = " + idFromDb);
			if (idFromDb == 0) {
				result = IPSConstants.STATUSCODE_06 + "::" + IPSConstants.BERR06_MSG;
				return result;
			}
			if (receiver_mac != null && !(ips.getReceiverMac().equals(receiver_mac))) {
				result = IPSConstants.STATUSCODE_08 + "::" + IPSConstants.BERR08_MSG;
				return result;
			}

			if ((ipsrecForSchoolID != null && ipsrecForSchoolID.getReceiverId() > 0) && idFromDb > 0
					&& ipsrecForSchoolID.getReceiverId() != idFromDb) {
				log.info("createOrEditDevice ipsrecForSchoolID =" + ipsrecForSchoolID);
				// ipsreceiver id got for given school id should be same as
				// idFromDb else user given invalid input
				result = IPSConstants.STATUSCODE_28 + "::" + IPSConstants.BERR28_MSG;
				return result;

			}
		}

		// device entry got if exists for deviceUUId
		ipsdev = this.ipsReceiverDeviceService.getReceiverDeviceIdsForDeviceUUID(ips.getDeviceUUID());

		/*
		 * if(ipsdev!=null && ipsdev.getZoneId() > 0){ //got zone details for
		 * zoneid passed which got from device table ipszone =
		 * this.ipsReceiverZoneService.getZoneDetailsForZoneId(ipsdev.getZoneId(
		 * )); }
		 */

		if (idFromDb > 0 && ips.getZoneName() == null) {

			log.info("createOrEditDevice  inside  ips.getZoneName()==null check");

			// if ips.getZoneName()==null i.e device under receiver directly
			if (ipsdev != null && ipsdev.getReceiverId() > 0 && ipsdev.getReceiverId().equals(idFromDb)
					&& ipsdev.getZoneId() == null) {
				// edit device under receiver
				this.fillNotMandatoryfields(ipsdev, ips);

				flag = this.ipsReceiverDeviceService.updateIPSReceiverDeviceEntry(ipsdev);
				if (flag) {
					result = IPSConstants.STATUSCODE_01 + "::Updation " + IPSConstants.BSUC01_MSG;
					return result;
				}

			} else if (ipsdev != null && ipsdev.getReceiverId() > 0 && ipsdev.getZoneId() != null
					&& (ipsdev.getReceiverId().equals(idFromDb) || !ipsdev.getReceiverId().equals(idFromDb))) {

				log.info("createOrEditDevice device uuid is associated with zoneId....");
				result = IPSConstants.STATUSCODE_15 + "::" + IPSConstants.BERR15_MSG;
				return result;

			} else if (ipsdev == null || ipsdev.getReceiverId() == 0) {
				// ipsdev is null then creation case else edit case
				ipsdev = new IPSReceiverDevice();

				// create device under receiver
				ipsdev.setZoneId(null);
				ipsdev.setReceiverId(idFromDb);
				ipsdev.setDeviceUUID(ips.getDeviceUUID());

				this.fillNotMandatoryfields(ipsdev, ips);

				flag = this.ipsReceiverDeviceService.createIPSReceiverDeviceEntry(ipsdev);
				if (flag) {
					result = IPSConstants.STATUSCODE_01 + "::Creation " + IPSConstants.BSUC01_MSG;
					return result;
				}

			}

		} else if (idFromDb > 0 && (ips.getZoneName() != null || !ips.getZoneName().equals(""))) {
			// if entry present, get it for unique zone name under particular
			// receiver
			ipszone = this.ipsReceiverZoneService.getZoneDetailsForZoneName(ips.getZoneName(), idFromDb);

			if (ipszone != null && ipszone.getZoneId() != null && ipszone.getZoneId() > 0 && ipsdev != null
					&& ipszone.getZoneId().equals(ipsdev.getZoneId()) && ipsdev.getReceiverId().equals(idFromDb)) {

				// edit device is under zone
				this.fillNotMandatoryfields(ipsdev, ips);

				flag = this.ipsReceiverDeviceService.updateIPSReceiverDeviceEntry(ipsdev);
				if (flag) {
					result = IPSConstants.STATUSCODE_01 + "::Updation " + IPSConstants.BSUC01_MSG;
					return result;
				}

			} else if (ipszone != null && ipszone.getZoneId() != null && ipszone.getZoneId() > 0 && ipsdev != null
					&& !ipszone.getZoneId().equals(ipsdev.getZoneId()) && !ipsdev.getReceiverId().equals(idFromDb)) {
				result = IPSConstants.STATUSCODE_15 + "::" + IPSConstants.BERR15_MSG;
				return result;
			} else if (ipszone != null && ipszone.getZoneId() != null && ipszone.getZoneId() > 0
					&& (ipsdev == null || ipsdev.getZoneId() == null)) {

				// ipsdev is null then creation case else edit case
				ipsdev = new IPSReceiverDevice();

				// create device is under zone
				ipsdev.setZoneId(ipszone.getZoneId());
				ipsdev.setReceiverId(idFromDb);
				ipsdev.setDeviceUUID(ips.getDeviceUUID());

				this.fillNotMandatoryfields(ipsdev, ips);

				flag = this.ipsReceiverDeviceService.createIPSReceiverDeviceEntry(ipsdev);
				if (flag) {
					result = IPSConstants.STATUSCODE_01 + "::Creation " + IPSConstants.BSUC01_MSG;
					return result;
				}

			}

		}
		return result;
	}// end of method

	private void fillNotMandatoryfields(IPSReceiverDevice ipsdev, IPSReceiverCSVModel ips) {

		if (ips.getFirmwareName() != null)
			ipsdev.setFirmwareName(ips.getFirmwareName());
		if (ips.getFirmwareVersion() != null)
			ipsdev.setFirmwareVersion(ips.getFirmwareVersion());
		if (ips.getDeviceModel() != null)
			ipsdev.setDeviceModel(ips.getDeviceModel());
		if (ips.getStatus() != null)
			ipsdev.setStatus(ips.getStatus());
		if (ips.getStatusDesc() != null)
			ipsdev.setStatusDesc(ips.getStatusDesc());

	}

	private File saveCsv(MultipartFile file, int school_id) {

		try {
			String os = System.getProperty("os.name");
			if (os.contains("Windows"))
				dbDateTime = dbDateTime.replace(":", "-");
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(dbDateTime);

			File f = new File(WebUtility.createFolder(this.configProperties.getProperty("ipscsv.upload.path") + "/"
					+ school_id + "_" + dateFormat.format(date) + ".csv"));
			boolean flag = f.createNewFile();

			log.info("fileCreated>>IPSReceiver" + "\t" + flag);
			log.info("f.getPath() IPSReceiver Controller" + "\t" + System.getProperty("UPLOAD_LOCATION"));
			file.transferTo(f);
			log.info("FilePath" + "\t" + f.getAbsolutePath());

			// List<IPSReceiverCSVModel> list =
			// IPSReceiverCSVToJavaUtil.convertSubmitIPSReceiverCsvToJava(f);
			return f;
		} catch (Exception e) {
			log.debug("Exception occurrred ::::::::::::::::" + e);
			return null;
		}

	}

	@RequestMapping(value = "/IPS/CreateIPSReceiver", method = RequestMethod.POST, produces = { "application/json" })
	public String createIPSReceiver(@RequestBody IPSReceiverModel ipsReceiverModel) {

		log.info("Inside IPSReceiverController.CreateIPSReceiver()");

		String responseJSON = null;
		String type = "IPSReceiver.CreateIPSReceiver";

		String json = JSONUtility.convertObjectToJson(ipsReceiverModel);
		log.info("Device Model Json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR03_MSG, IPSConstants.STATUSCODE_03, type);
			return responseJSON;
		}

		log.info("ipsReceiverModel::" + ipsReceiverModel.toString());

		if (ipsReceiverModel.getReceiverMac() == null || ipsReceiverModel.getReceiverMac().equals("")
				|| ipsReceiverModel.getSchoolId() == null || ipsReceiverModel.getSchoolId().equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		}

		if (ipsReceiverModel.getSchoolId() != null
				&& !(accountService.checkAccountIDExist(ipsReceiverModel.getSchoolId()))) {
			log.info("Inside IPSReceiverController.CreateIPSReceiver, school with school_id does not exist");
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR09_MSG, IPSConstants.STATUSCODE_09, type);
			return responseJSON;
		}

		if (ipsReceiverModel.getSchoolId() != null
				&& ipsReceiverService.isIPSReceiverForSchooIDExist(ipsReceiverModel.getSchoolId())) {
			log.info("Inside IPSReceiverController.CreateIPSReceiver(), school_id already exist with receiver");
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR04_MSG, IPSConstants.STATUSCODE_04, type);
			return responseJSON;
		}

		if (ipsReceiverModel.getReceiverMac() != null) {
			// if user want to change the mac addrss
			int idFromDb = this.ipsReceiverService.checkMacExistWithAnyReceiver(ipsReceiverModel.getReceiverMac());
			if (idFromDb > 0) {
				responseJSON = ErrorCodesUtil.displayErrorJSON("mac is associated with other receiver", "BERR00", type);
				return responseJSON;
			}

		}
		IPSReceiver ips = new IPSReceiver();
		ips.setReceiverMac(ipsReceiverModel.getReceiverMac());

		if (ipsReceiverModel.getReceiverName() != null)
			ips.setReceiverName(ipsReceiverModel.getReceiverName());

		ips.setSchoolId(ipsReceiverModel.getSchoolId());

		if (ipsReceiverModel.getReceiverVersion() != null)
			ips.setReceiverVersion(ipsReceiverModel.getReceiverVersion());
		// creating the IPS receiver entry
		boolean flag = this.ipsReceiverService.createIPSReceiverEntry(ips);

		if (flag) {
			// String statusMessage = "API Request Success ";
			responseJSON = ErrorCodesUtil.displaySuccessJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01, type);
			return responseJSON;
		}

		return responseJSON;

	}// end of create method

	@RequestMapping(value = "/IPS/UpdateIPSReceiver/{token}/{receiver_Id}", method = RequestMethod.POST, produces = {
			"application/json" })
	public String updateIPSReceiver(@PathVariable("token") String sessionId,
			@PathVariable("receiver_Id") Integer receiver_Id, @RequestBody IPSReceiverModel ipsReceiverModel) {

		log.info("Inside IPSReceiverController.UpdateIPSReceiver()");
		String responseJSON = null;
		IPSReceiver ips = null;

		String type = "IPSReceiver.UpdateIPSReceiver";

		String json = JSONUtility.convertObjectToJson(ipsReceiverModel);
		log.info("IPSReceiver Model Json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			// ErrorMessage = "Input Is Invalid";
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR03_MSG, IPSConstants.STATUSCODE_03, type);
			return responseJSON;
		}
		log.info("ipsReceiverModel::" + ipsReceiverModel.toString());

		if (receiver_Id == null || receiver_Id == 0
				|| (sessionId == null || (sessionId != null && sessionId.equals("")))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR07_MSG, IPSConstants.STATUSCODE_07, type);
			return responseJSON;
		}

		// get details of ipsreceiver for given id
		ips = this.ipsReceiverService.getIPSReceiverDetailsForID(receiver_Id);

		if (ips == null || ips.getReceiverId() == null
				|| (ips.getReceiverId() != null && !ips.getReceiverId().equals(receiver_Id))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR12_MSG, IPSConstants.STATUSCODE_12, type);
			return responseJSON;
		}

		if (ips.getSessionId() == null || (ips.getSessionId() != null && !ips.getSessionId().equals(sessionId))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
			return responseJSON;
		}
		log.info("ipsreceiver entry =" + ips.toString());

		if (ipsReceiverModel.getReceiverMac() == null || ipsReceiverModel.getReceiverMac().equals("")) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		}
		if (ipsReceiverModel.getReceiverMac() != null) {
			// if user want to change the mac addrss
			int idFromDb = this.ipsReceiverService.checkMacExistWithAnyReceiver(ipsReceiverModel.getReceiverMac());
			/*
			 * if(idFromDb > 0){ responseJSON =
			 * ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR15_MSG,
			 * IPSConstants.STATUSCODE_15, type); return responseJSON; }
			 */

		}

		ips = new IPSReceiver();

		ips.setReceiverId(ipsReceiverModel.getReceiverId());

		if (ipsReceiverModel.getReceiverName() != null)
			ips.setReceiverName(ipsReceiverModel.getReceiverName());

		ips.setReceiverMac(ipsReceiverModel.getReceiverMac());
		ips.setSchoolId(ipsReceiverModel.getSchoolId());

		if (ipsReceiverModel.getReceiverVersion() != null)
			ips.setReceiverVersion(ipsReceiverModel.getReceiverVersion());

		ips.setSessionId(sessionId);
		ips.setCreated_date(new Date());

		Date sessionExpiry = null; // this is used to update in DB
		if (ips.getSessionExpiry() != null)
			sessionExpiry = ips.getSessionExpiry();

		SessionDetailsForIDTransform sessionTocheck = new SessionDetailsForIDTransform();
		sessionTocheck.setSessionId(ips.getSessionId());
		sessionTocheck.setExpiryduration(ips.getSessionExpiry());
		sessionTocheck.setId(ips.getReceiverId());

		// int sessionValidity = 0;
		int sessionValidity = Integer.valueOf(ipsLoginValidity);
		boolean check = CommonUtil.validateSession(sessionId, sessionTocheck);
		if (check) {

			log.info("changed ipsLocatorsModel which is given for updation =" + ipsReceiverModel.toString()
					+ " sessionId=" + sessionId + " sessionExpiry=" + sessionExpiry);
			boolean updateflag = this.ipsReceiverService.updateIPSReceiverEntry(ips);

			if (updateflag) {
				responseJSON = ErrorCodesUtil.displaySuccessJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01,
						type);
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR17_MSG, IPSConstants.STATUSCODE_17,
						type);
			}

			return responseJSON;
		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return responseJSON;
		}

	}// end of update method

	@RequestMapping(value = "/IPS/DeleteIPSReceiver/{token}/{receiver_Id}", method = RequestMethod.GET, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String deleteIPSReceiver(@PathVariable("token") String sessionId,
			@PathVariable("receiver_Id") Integer receiver_Id) {

		log.info("Inside IPSReceiverController.deleteIPSReceiver()");

		IPSReceiver ips = null;
		String responseJSON = null;
		String type = "IPSReceiver.deleteIPSReceiver";
		boolean deleteflag = false;

		List<Integer> zoneIdList = null;
		List<Integer> receiverDevIdList = null;
		List<Integer> eventsIdList = null;

		if (receiver_Id == null || receiver_Id == 0
				|| (sessionId == null || (sessionId != null && sessionId.equals("")))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR07_MSG, IPSConstants.STATUSCODE_07, type);
			return responseJSON;
		}

		// get details of ipsreceiver for given id
		ips = this.ipsReceiverService.getIPSReceiverDetailsForID(receiver_Id);

		if (ips == null || ips.getReceiverId() == null
				|| (ips.getReceiverId() != null && !ips.getReceiverId().equals(receiver_Id))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR12_MSG, IPSConstants.STATUSCODE_12, type);
			return responseJSON;
		}

		if (ips.getSessionId() == null || (ips.getSessionId() != null && !ips.getSessionId().equals(sessionId))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
			return responseJSON;
		}

		log.info("ipsreceiver entry =" + ips.toString());
		SessionDetailsForIDTransform sessionTocheck = new SessionDetailsForIDTransform();
		sessionTocheck.setSessionId(ips.getSessionId());
		sessionTocheck.setExpiryduration(ips.getSessionExpiry());
		sessionTocheck.setId(ips.getReceiverId());

		int sessionValidity = 0;
		boolean check = CommonUtil.validateSession(receiver_Id, sessionId, sessionValidity, sessionTocheck,
				ipsLoginValidity);
		if (check) {

			// getting receiver device ids for zoneIdlist
			receiverDevIdList = this.ipsReceiverDeviceService.getReceiverDeviceIds(receiver_Id, null);
			
			// get the zone Id list for IPS receiver ID
			zoneIdList = this.ipsReceiverZoneService.getAllZoneIDsForReceiverID(receiver_Id);
			if (zoneIdList != null && zoneIdList.size() > 0) {
				// getting eventIds for receiver device list
				eventsIdList = this.eventsService.getDeviceEventIdsForGivenIdList(zoneIdList);
				if (eventsIdList != null && eventsIdList.size() > 0) {
					// Update the zone Id to "0" to not lose
					// device events that occurred in that particular zone
					int count = this.eventsService.updateZoneIdForDeviceEventIds(eventsIdList);
					if (count != eventsIdList.size())
						log.debug("device events not deleted properly for receiver_Id =" + receiver_Id);

				} // end of eventsIdlist chk
			}

			if (receiverDevIdList != null && receiverDevIdList.size() > 0) {

				// delete entries from IPSReceiverDevice
				int count1 = this.ipsReceiverDeviceService.deleteAllReceiverDevicesForIds(receiverDevIdList,
						receiver_Id);
				if (count1 == 0) {
					log.info("IPSReceiverDevices is not deleted for receiver_Id=" + receiver_Id);
				}

			} // end of receiverDevIdList chk

			// getting Allzone details to delete mapfile from path
			List<String> mapfiles = null;
			List<IPSReceiverZone> ipsZones = this.ipsReceiverZoneService.getAllZoneDetailsForReceiverID(receiver_Id);
			if (ipsZones != null) {
				for (IPSReceiverZone ipsZone : ipsZones) {
					if (ipsZone.getMapFilename() != null) {
						String filename = this.configProperties.getProperty("ips.download.path") + "/"
								+ ipsZone.getMapFilename();
						mapfiles.add(filename);
					}
				} // end of for
			} // if check of ipsZone

			// delete entries from ipsReceiver zone table
			deleteflag = this.ipsReceiverZoneService.deleteAllZonesForReceiverId(receiver_Id);
			if (!deleteflag) {
				log.info("ipsReceiverZone not deleted for receiver_Id=" + receiver_Id);
			} else {
				// if deleteflag is true then entry in DB is deleted, so delete
				// mapfile from path.

				for (String fileName : mapfiles) {
					if (null != fileName) {
						try {

							File f = new File(fileName);
							if (f != null)
								f.delete();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} // end of fileName null check
				} // end of forloop

			} // end of else part

			// ips receiver deletion
			deleteflag = this.ipsReceiverService.deleteIPSReceiverEntry(receiver_Id);

			if (deleteflag) {
				responseJSON = ErrorCodesUtil.displaySuccessJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01,
						type);
			} else {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR11_MSG, IPSConstants.STATUSCODE_11,
						type);
			}
			return responseJSON;

		} else {
			// ErrorMessage = "session provided is expired, please re-login";
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return responseJSON;
		} // end of else part of if check
	}// end of delete method

	@RequestMapping(value = "/IPS/GetIPSReceiverDetails/{token}", method = RequestMethod.POST, produces = Constant.APPLICATION_JSON_WITH_CHARSET_AS_UTF8)
	public String getIPSRecevierDetails(@PathVariable("token") String sessionId, @RequestBody IPSLoginDataModel dm)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		log.info("Inside IPSReceiverController.getIPSRecevierDetails()");

		String responseJSON = null;
		String sessionID = null;
		int receiver_Id;
		IPSReceiver ips = null;

		int school_Id;
		String schoolNum = null;
		boolean isValidkey = false;
		String accesskey = null;
		String ipsReceiverMac = null;
		String sessionIDfromDb = null;
		Date validityFromDb = null;
		SessionDetailsForIDTransform sessionTocheck = null;

		String type = "IPSReceiver.GetIPSRecevierDetails";
		String json = JSONUtility.convertObjectToJson(dm);
		log.info("Device Model Json" + "\n" + json);

		if (!JSONUtility.isValidJson(json)) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR03_MSG, IPSConstants.STATUSCODE_03, type);
			return responseJSON;
		}

		log.info("Inside IPSReceiverController.IPSReceiverLogin():::" + dm.toString());

		if (dm.getAccesskey() == null || dm.getAccesskey().equals("") || dm.getIps_receiver_mac() == null
				|| dm.getIps_receiver_mac().equals("") || sessionId == null
				|| (sessionId != null && sessionId.equals(""))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return responseJSON;
		}

		ipsReceiverMac = dm.getIps_receiver_mac();
		accesskey = dm.getAccesskey();

		// get details of ipsreceiver for given MAC
		ips = this.ipsReceiverService.getIPSReceiverEntryForMac(ipsReceiverMac);

		if (ips == null || (ips != null && ips.getReceiverId() == 0)) {
			// not present
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR06_MSG, IPSConstants.STATUSCODE_06, type);
			return responseJSON;
		} else {
			receiver_Id = ips.getReceiverId();
			school_Id = ips.getSchoolId();
			sessionIDfromDb = ips.getSessionId();
			validityFromDb = ips.getSessionExpiry();
			log.info("receiver_id=" + receiver_Id + " school_Id=" + school_Id + "  sessionIDfromDb=" + sessionIDfromDb
					+ " validityFromDb" + validityFromDb);
		}

		if (ips != null && (ips.getSessionId() != null && !ips.getSessionId().equals(sessionId))) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
			return responseJSON;
		}

		if (ips != null && ips.getReceiverId() > 0 && ips.getSessionId() != null) {

			sessionTocheck = new SessionDetailsForIDTransform();
			sessionTocheck.setSessionId(ips.getSessionId());
			sessionTocheck.setExpiryduration(ips.getSessionExpiry());
			sessionTocheck.setId(ips.getReceiverId());

		}

		if (school_Id > 0) {
			SchoolDetails sd = this.schoolService.getSchoolDetailsBySchoolId(school_Id);
			if (sd != null && sd.getMobile_number() == null) {
				responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR04_MSG, IPSConstants.STATUSCODE_04,
						type);
				return responseJSON;
			}
			if (sd != null && sd.getMobile_number() != null && accountService.checkAccountIDExist(school_Id)) {
				schoolNum = sd.getMobile_number();
				log.info("schoolNum" + "\t" + schoolNum);
				log.info("accesskey" + "\t" + accesskey);
				isValidkey = AESEncryption.validatePassword(schoolNum, accesskey);
				log.debug("isValidkey=" + isValidkey);

			}
		}

		if (isValidkey == false) {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
			return responseJSON;
		}

		log.info("ipsreceiver entry =" + ips.toString());

		receiver_Id = ips.getReceiverId();

		// int sessionValidity = 0;
		boolean sessioncheck = CommonUtil.validateSession(sessionId, sessionTocheck);
		if (sessioncheck) {

			String partOfResponse = null;

			// already ipsreceiver details are available in ips obj, getting
			// zone details
			List<IPSReceiverZone> ipsZoneList = this.ipsReceiverZoneService.getAllZoneDetailsForReceiverID(receiver_Id);

			if (ipsZoneList != null && ipsZoneList.size() > 0) {
				partOfResponse = " ,\"IPSReceiverZoneList\": [";
				for (IPSReceiverZone ipsZone : ipsZoneList) {
					int zoneId = ipsZone.getZoneId();
					log.info("zone details=" + ipsZone.toString());

					List<IPSReceiverDevice> ipsDeviceList = this.ipsReceiverDeviceService.getReceiverDevices(zoneId,
							receiver_Id);

					log.info("ipsDeviceList"+ipsDeviceList);
					partOfResponse = partOfResponse + this.displayZonePartResponseJson(ipsZone, ipsDeviceList);
				}
				// partOfResponse = partOfResponse+ "]}";
				partOfResponse = partOfResponse.substring(0, partOfResponse.lastIndexOf(","));
				partOfResponse = partOfResponse + "]";
			} else {
				partOfResponse = "";
				// partOfResponse = partOfResponse+ "]";
			} // end of ipszones if-else check

			log.info("getting all devices under receiver....");
			// already ipsreceiver details are available in ips obj, getting
			// device details under receiver
			List<IPSReceiverDevice> ipsDeviceList = this.ipsReceiverDeviceService.getReceiverDevices(null, receiver_Id);
			log.info(" ipsDeviceList==" + ipsDeviceList);

			// should add in response
			partOfResponse = partOfResponse + displayDeviceResponsePart(ipsDeviceList);

			log.info(" partOfResponse => " + partOfResponse);

			responseJSON = this.displayViewSuccessJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01, type, ips,
					partOfResponse);
			return responseJSON;

		} else {
			responseJSON = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return responseJSON;
		}

	} // end of list method

	private String displayZonePartResponseJson(IPSReceiverZone ipsZone, List<IPSReceiverDevice> ipsDeviceList) {
		// String jsonstr = null;
		StringBuilder jsonstr = new StringBuilder("");
		String[] arr = new String[5];
		log.info("Inside displayZonePartResponseJson==");
		Map<Object, Object> map = new LinkedHashMap<>();

		if (ipsZone.getZoneId() != null) {

			map.put("zoneId", ipsZone.getZoneId());

		}

		if (ipsZone.getReceiverId() != null) {

			map.put("receiverId", ipsZone.getReceiverId());

		}

		if (ipsZone.getZoneName() != null) {
			arr[0] = ipsZone.getZoneName();
			map.put("zoneName", arr[0]);
		}

		if (ipsZone.getMapType() != null) {
			arr[1] = ipsZone.getMapType();
			map.put("MapType", arr[1]);
		}

		if (!arr[1].equals("full")) {
			if (ipsZone.getBuildingName() != null) {
				arr[3] = ipsZone.getBuildingName();
				map.put("buildingName", arr[3]);
			}

			if (ipsZone.getFloorNum() != null) {
				arr[4] = ipsZone.getFloorNum();
				map.put("floorNumber", arr[4]);
			}
		}

		if (ipsZone.getMapFilename() != null) {
			arr[2] = this.configProperties.getProperty("downloads.url")
					+ this.configProperties.getProperty("ipsimages.download.path") + "/" + ipsZone.getMapFilename();
			map.put("mapFilename", arr[2]);
		}

		String deviceJSON = JSONObject.toJSONString(map);
		log.info("append map for each loop displayZonePartResponseJson==" + deviceJSON);
		jsonstr = jsonstr.append(JSONObject.toJSONString(map));
		log.info("append map for each loop displayZonePartResponseJson1==" + jsonstr);
		/*
		 * if(arr[1].equals("full")){ jsonstr = jsonstr.append("{ \"zoneId\": "
		 * + ipsZone.getZoneId() + ",\"receiverId\": " + ipsZone.getReceiverId()
		 * + ", \"zoneName\":  \"" + arr[0] +"\", \"MapType\":  \"" + arr[1] +
		 * "\",\"mapFilename\":  \"" + arr[2] + "\" "); //jsonstr =
		 * jsonstr.append("\"IPSReceiverDeviceList\": [" ); ;
		 * 
		 * }else{ if(ipsZone.getBuildingName()!= null)
		 * arr[3]=ipsZone.getBuildingName();
		 * 
		 * if(ipsZone.getFloorNum()!= null) arr[4]=ipsZone.getFloorNum();
		 * 
		 * jsonstr = jsonstr.append("{ \"zoneId\": " + ipsZone.getZoneId() +
		 * ",\"receiverId\": " + ipsZone.getReceiverId() + ", \"zoneName\":  \""
		 * + arr[0] +"\", \"MapType\":  \"" + arr[1]
		 * +"\", \"buildingName\":  \"" + arr[3] +"\", \"floorNumber\":  \"" +
		 * arr[4] + "\",\"mapFilename\":  \"" + arr[2] + "\" ");
		 * 
		 * ; }
		 * 
		 */

		jsonstr = jsonstr.deleteCharAt(jsonstr.lastIndexOf("}"));
		jsonstr = jsonstr.append(displayDeviceResponsePart(ipsDeviceList));

		// jsonstr = jsonstr.deleteCharAt(jsonstr.lastIndexOf(","));

		jsonstr = jsonstr.append("},");
		;

		arr = null;
		log.info("jsonstr==" + jsonstr);
		return jsonstr.toString();
	}// end of method

	private String displayDeviceResponsePart(List<IPSReceiverDevice> ipsDeviceList) {
		StringBuilder jsonstr = new StringBuilder("");

		// log.info("displayDeviceResponsePart=="+ipsDeviceList );
		log.info("Inside displayDeviceResponsePart==");

		if (ipsDeviceList != null && ipsDeviceList.size() > 0) {

			jsonstr = jsonstr.append(",\"IPSReceiverDeviceList\": [");
			//Map<Object, Object> map = new LinkedHashMap<>();
				int i = 0;
			String str[] = new String[8];
			for (IPSReceiverDevice ipsdev : ipsDeviceList) {
				Map<Object, Object> map = new LinkedHashMap<>();
				i++;
				// str[0] = ipsdev.getReceiverDeviceId().toString();
				// map.put("ReceiverDeviceId", str[0]);
				log.info("ipsdev.getReceiverDeviceId()"+ipsdev.getReceiverDeviceId());
				log.info("ipsdev.getZoneId()"+ipsdev.getZoneId());
				log.info("ipsdev.getDeviceUUID()"+ipsdev.getDeviceUUID());
				log.info("ipsdev.getFirmwareName()"+ipsdev.getFirmwareName());
				log.info("ipsdev.getFirmwareVersion()"+ipsdev.getFirmwareVersion());
				log.info("ipsdev.getDeviceModel()"+ipsdev.getDeviceModel());
				log.info("ipsdev.getStatus()"+ipsdev.getStatus());
				log.info("ipsdev.getStatusDesc()"+ipsdev.getStatusDesc());
				
				map.put("ReceiverDeviceId", ipsdev.getReceiverDeviceId());

				if (ipsdev.getZoneId() != null) {
					// str[1] = ipsdev.getZoneId().toString();
					// map.put("zoneId", str[1]);
					map.put("zoneId", ipsdev.getZoneId());

				}

				// str[2] = ipsdev.getDeviceUUID().toString();
				// map.put("zoneId", str[2]);
				map.put("ReceiverDeviceUUID", ipsdev.getDeviceUUID());

				if (ipsdev.getFirmwareName() != null) {
					//str[3] = ipsdev.getFirmwareName();
					map.put("FirmwareName",ipsdev.getFirmwareName());
				}

				if (ipsdev.getFirmwareVersion() != null) {
					//str[4] = ipsdev.getFirmwareVersion();
					map.put("FirmwareVersion", ipsdev.getFirmwareVersion());
				}

				if (ipsdev.getDeviceModel() != null) {
					//str[5] = ipsdev.getDeviceModel();
					map.put("DeviceModel",  ipsdev.getDeviceModel());
				}

				if (ipsdev.getStatus() != null) {
					//str[6] = ipsdev.getStatus();
					map.put("Status", ipsdev.getStatus());
				}

				if (ipsdev.getStatusDesc() != null) {
					//str[7] = ipsdev.getStatusDesc();
					map.put("StatusDescription", ipsdev.getStatusDesc());
				}

				/*
				 * jsonstr = jsonstr + "{\"ReceiverDeviceId\": " + str[0] +
				 * ",\"zoneId\": " + str[1] + ",\"ReceiverDeviceUUID\":  \"" +
				 * str[2] + "\",\"FirmwareName\":  \"" + str[3] +
				 * "\", \"FirmwareVersion\":  \"" + str[4] +
				 * "\",\"DeviceModel\":  \"" + str[5] +"\", \"Status\":  \"" +
				 * str[6] + "\",\"StatusDescription\":  \"" + str[7] + " \" },";
				 */

				String deviceJSON = JSONObject.toJSONString(map);
				log.info("append map for each loop jsonstr==" + deviceJSON);
				jsonstr = jsonstr.append(JSONObject.toJSONString(map));
				if(i< ipsDeviceList.size())
				jsonstr.append(",");
				log.info("append map for each loop jsonstr1==" + jsonstr);
				/*
				 * if(ipsdev.getZoneId()==null){ jsonstr = jsonstr.append(
				 * "{\"ReceiverDeviceId\": " + str[0] +
				 * ",\"ReceiverDeviceUUID\":  \"" + str[2] +
				 * "\",\"FirmwareName\":  \"" + str[3] +
				 * "\", \"FirmwareVersion\":  \"" + str[4] +
				 * "\",\"DeviceModel\":  \"" + str[5] +"\", \"Status\":  \"" +
				 * str[6] + "\",\"StatusDescription\":  \"" + str[7] + " \" },"
				 * ); } else{ jsonstr = jsonstr.append(
				 * "{\"ReceiverDeviceId\": " + str[0] + ",\"zoneId\": " + str[1]
				 * + ",\"ReceiverDeviceUUID\":  \"" + str[2] +
				 * "\",\"FirmwareName\":  \"" + str[3] +
				 * "\", \"FirmwareVersion\":  \"" + str[4] +
				 * "\",\"DeviceModel\":  \"" + str[5] +"\", \"Status\":  \"" +
				 * str[6] + "\",\"StatusDescription\":  \"" + str[7] + " \" },"
				 * );
				 * 
				 * }
				 */
				str = null;

			} // end of for

			;
			// jsonstr = jsonstr.deleteCharAt(jsonstr.lastIndexOf(","));
			jsonstr = jsonstr.append("]");
			;
			log.info("append map for each loop jsonstr step2==" + jsonstr);

		}

		return jsonstr.toString();

	}

	private String displayViewSuccessJSON(String sucessMsg, String statusCode, String type, IPSReceiver ips,
			String partjson) {

		String str[] = new String[4];

		// putting data into model object

		if (ips.getReceiverName() != null)
			str[0] = ips.getReceiverName();

		if (ips.getReceiverVersion() != null)
			str[1] = ips.getReceiverVersion();

		if (ips.getSessionId() != null)
			str[2] = ips.getSessionId();

		if (ips.getSessionExpiry() != null)
			str[3] = ips.getSessionExpiry().toString();
		String receiver_status = ips.getReceiver_status();
		
		String responseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + sucessMsg + "\"}, \"Result\":  { \"IPSReceiver\":  {"
				+ "\"receiverId\": " + ips.getReceiverId() + ", \"receiverMac\":  \"" + ips.getReceiverMac()
				+ "\",\"schoolId\": " + ips.getSchoolId() + ",\"ReceiverName\": \"" + str[0]
				+ "\",\"ReceiverVersion\": \"" + str[1] + "\",\"receiver_status\":\""+receiver_status+"\",\"SessionId\": \"" + str[2] + "\",\"SessionExpiry\": \""
				+ str[3] +

				"\"" + partjson + "}}}}";
		;
		log.info("final Response Json String " + responseJson);

		log.info("final Response Json String after url change" + StringEscapeUtils.unescapeJava(responseJson));

		return StringEscapeUtils.unescapeJava(responseJson);
	}

	@RequestMapping(value = "/IPS/IPSReceiverStatusUpdate/{sessionId}", method = RequestMethod.PUT, produces = {
			"application/json" })
	public String IPSReceiverStatusUpdate(@PathVariable("sessionId") String sessionId,
			@RequestBody BeaconDeviceEventsModel beaconDeviceEventsModel) throws ParseException {

		String respondJson = null;
		String type = "ipsreceiver.IPSReceiverStatusUpdate";
		IPSReceiver ipsReceiver = null;

		if ((sessionId.isEmpty() || sessionId.equals(null))
				|| (beaconDeviceEventsModel.getIps_receiver_mac().isEmpty()
						|| beaconDeviceEventsModel.getIps_receiver_mac().equals(null))
				|| (beaconDeviceEventsModel.getAccesskey().isEmpty()
						|| beaconDeviceEventsModel.getAccesskey().equals(null))
				|| (beaconDeviceEventsModel.getIps_receiver_status().isEmpty()
						|| beaconDeviceEventsModel.getIps_receiver_status().equals(null))) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR05_MSG, IPSConstants.STATUSCODE_05, type);
			return respondJson;
		}

		ipsReceiver = this.ipsReceiverService.getIPSReceiverEntryForMac(beaconDeviceEventsModel.getIps_receiver_mac());

		if (ipsReceiver == null) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR08_MSG, IPSConstants.STATUSCODE_08, type);
			return respondJson;
		}

		String mobileNo = this.ipsReceiverService.getMobileNumberBySchoolId(ipsReceiver.getSchoolId());

		String iPSReceiverByMacAndSessionIdStatus = this.ipsReceiverService.findIPSReceiverByMacAndSessionId(sessionId,
				beaconDeviceEventsModel.getIps_receiver_mac());
		log.info("iPSReceiverByMacAndSessionIdStatus"+"\t"+iPSReceiverByMacAndSessionIdStatus);
		boolean accessKeyValidation = false;
		try {
			accessKeyValidation = AESEncryption.validatePassword(mobileNo, beaconDeviceEventsModel.getAccesskey());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			log.info("Exception Occured in IPSReceiverStatusUpdate()" + "\t" + e);
		}

		if(!accessKeyValidation) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
		    return respondJson;
		}
		String receiverStatus = beaconDeviceEventsModel.getIps_receiver_status();
		if (iPSReceiverByMacAndSessionIdStatus.equals("Success")) {
			
			if(receiverStatus.length() > 50)
				receiverStatus = receiverStatus.substring(0, 50);
			ipsReceiver = this.ipsReceiverService.updateIPSReceiverStatus(beaconDeviceEventsModel.getIps_receiver_mac(), receiverStatus);
			if(null != ipsReceiver) {
				respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BSUC01_MSG, IPSConstants.STATUSCODE_01, type);
			    return respondJson;
			}
		} else if (iPSReceiverByMacAndSessionIdStatus.equals(tokenUnavailable)) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR10_MSG, IPSConstants.STATUSCODE_10, type);
			return respondJson;
		} else if (iPSReceiverByMacAndSessionIdStatus.equals(macTokenMapping)) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR22_MSG, IPSConstants.STATUSCODE_22, type);
			return respondJson;
		} else if (iPSReceiverByMacAndSessionIdStatus.equals("NOTVALID")) {
			respondJson = ErrorCodesUtil.displayErrorJSON(IPSConstants.BERR02_MSG, IPSConstants.STATUSCODE_02, type);
			return respondJson;
		}
		return null;
	}

}// end of program
