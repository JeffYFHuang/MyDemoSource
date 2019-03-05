package com.liteon.icgwearable.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger;

import com.liteon.icgwearable.transform.DeviceAnalyticsOutputTransform;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class ErrorCodesUtil {

	private static Logger log = Logger.getLogger(ErrorCodesUtil.class);
	
	/*@Value("${display.dateTime}")
	private static String sourceDateTime;*/

	public static String displayErrorJSON(String errorMsg, String statusCode, String type) {

		String errorResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";

		return errorResponseJson;
	}

	public static String displaySuccessJSON(String sucessMsg, String statusCode, String type) {

		String errorResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + sucessMsg + "\"}}}";
		;

		return errorResponseJson;
	}

	public static String displayIPSLoginJSON(String statusMsg, String statusCode, String type, String token, int sessionValidity) {
		String errorResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + statusMsg + "\"},\"Results\":{\"token\":\""+token+"\",\"validity_minutes\":\""+sessionValidity+
				"\"}}}";
		;

		return errorResponseJson;
	}
	
	public static String displayWearableLoginJSON(String errorMsg, String statusCode, String type, String token) {
		String errorResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"token\":\""+token+"\"}}}";
		;

		return errorResponseJson;
	}
	
	public static String displayWearableLoginJSON(String errorMsg, String statusCode, String type, String token, int sessionValidity, int schoolId) {
		String errorResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"token\":\""+token+"\",\"validity_minutes\":\""+sessionValidity+"\",\"school_id\":\""+schoolId+"\"}}}";
		;

		return errorResponseJson;
	}
	
	public static String displaySuccessJSONForDataSync(String errorMsg, String statusCode,
			DeviceAnalyticsOutputTransform daot, String updateAvail, String device_config, String rewardsJson, String sourceDateTime) {
		log.info("rewardsJson"+"\n"+rewardsJson);
		String displaySuccessJSON = null;
		if (daot != null) {
			DateFormat df = new SimpleDateFormat(sourceDateTime);
			String createdDate = null;

			createdDate = df.format(daot.getCreatedDate());
			log.info("createdDate" + "\t" + createdDate);

			if(updateAvail.equals("no") && rewardsJson != null) {
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"system_update_available\":\"" + updateAvail
						+ "\",\"physical_fitness_index\": \""
						+ daot.getPhysicalFitnessIndex() + "\",\"physical_fitness_date\": \"" + createdDate + "\",\"rewards\": "+rewardsJson+"}}}";
			}else if(updateAvail.equals("no") && rewardsJson == null){
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"system_update_available\":\"" + updateAvail
						+ "\",\"physical_fitness_index\": \""
						+ daot.getPhysicalFitnessIndex() + "\",\"physical_fitness_date\": \"" + createdDate +"\"}}}";
			}
			
			if(updateAvail.equals("yes") && rewardsJson != null){
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"firmware_update\":\"" + updateAvail
						+ "\",\"device_config\":\"" + device_config + "\",\"physical_fitness_index\": \""
						+ daot.getPhysicalFitnessIndex() + "\",\"physical_fitness_date\": \"" + createdDate + "\",\"rewards\": "+rewardsJson+"}}}";	
			}else if(updateAvail.equals("yes") && rewardsJson == null){
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"system_update_available\":\"" + updateAvail
						+ "\",\"system_update_version\":\"" + device_config + "\",\"physical_fitness_index\": \""
						+ daot.getPhysicalFitnessIndex() + "\",\"physical_fitness_date\": \"" +createdDate+"\"}}}";	
			}
			log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		}else {
			if(updateAvail.equals("no") && rewardsJson != null) {
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"system_update_available\":\"" + updateAvail+ "\",\"rewards\":"+rewardsJson+"}}}";
			}else if(updateAvail.equals("no") && rewardsJson == null){
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"system_update_available\":\"" + updateAvail+ "\"}}}";
			}
			
			if(updateAvail.equals("yes") && rewardsJson != null) {
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"system_update_available\":\"" + updateAvail
						+ "\",\"system_update_version\":\"" + device_config + "\",\"rewards\":"+rewardsJson+"}}}";	
			}else if(updateAvail.equals("yes") && rewardsJson == null) {
				displaySuccessJSON = "{\"Return\": { \"Type\": \"device.DeviceDataSync\",\"ResponseSummary\": { \"StatusCode\": \""
						+ statusCode + "\",\"StatusMessage\": \"" + errorMsg
						+ "\"},\"Results\":{\"system_update_available\":\"" + updateAvail
						+ "\",\"system_update_version\":\"" + device_config + "\"}}}";
			}
			log.info("displaySuccessJSONForDataSync" + "\n" + displaySuccessJSON);
		}
		return displaySuccessJSON;
	}
	
	public static String displaySuccessJSONForDataSyncTest(String errorMsg, 
			String statusCode,String updateAvail, String firmware_update, 
			String device_config, String vitalsJson, String rewardsJson) {

		JSONObject responseJson = null;
		JSONObject returnJsonObject = null;
		JSONObject responseSummaryJsonObject = null;
		JSONObject resultsJsonObject = null;

		try {
			returnJsonObject = new JSONObject();
			returnJsonObject.put("Type", "device.DeviceDataSync");

			responseSummaryJsonObject = new JSONObject();
			responseSummaryJsonObject.put("StatusCode", statusCode);
			responseSummaryJsonObject.put("StatusMessage", errorMsg);
			returnJsonObject.put("ResponseSummary", responseSummaryJsonObject);

			resultsJsonObject = new JSONObject();

			if(null != firmware_update) {
				resultsJsonObject.put("firmware_update", firmware_update);
			}

			if(null != device_config) {
				resultsJsonObject.put("device_config", device_config);
			}

			if(null != vitalsJson) {
				resultsJsonObject.put("vitals", vitalsJson);
			}

			if(null != rewardsJson) {
				resultsJsonObject.put("rewards", rewardsJson);
			}

			if(!resultsJsonObject.isEmpty()) {
				returnJsonObject.put("Results", resultsJsonObject);
			}

			responseJson = new JSONObject();
			responseJson.put("Return", returnJsonObject);

		} catch (JSONException je) {
			je.printStackTrace();
		}

		return responseJson.toString();
	}
	
	public static String displayErrorJSONForuserLogin(String errorMsg, String statusCode, String type, String SessionId,
			int accountId) {
		
		String errorResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\",\"token\": \"" + SessionId + "\"}}}";
		;

		return errorResponseJson;
	}

	public static String displayJSONForDeviceEventEliminate(String errorMsg, String statusCode) {

		String responseJson = "{\"Return\": { \"Type\": \"device.eliminatedeviceevent\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";

		return responseJson;
	}

	public static String displaySuccessJSONForAnnouncement(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"announcement.AnnouncementsList\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"announcements\": "
				+ jsonString + "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayErroJSONForAnnouncement(String errorMsg, String statusCode) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"announcement.AnnouncementsList\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";;

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displayJSONForStudentLocation(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"student.StudentLocation\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\": " + jsonString + " }}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displayJSONForUserRegistration(String statusCode, String msg) {

		String displaySuccessJSON = "";
		if (msg.equals("API Request Success")) {
			statusCode = "SUC01";
			displaySuccessJSON = "{\"Return\": { \"Type\": \"user.ParentUserRegistration\",\"ResponseSummary\": { \"StatusCode\": \""
					+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";
		} else {
			statusCode = "ERR13";
			displaySuccessJSON = "{\"Return\": { \"Type\": \"user.ParentUserRegistration\",\"ResponseSummary\": { \"StatusCode\": \""
					+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";
		}

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayJSONForUserRegistrationInvalid(String statusCode, String msg) {
		String displaySuccessJSON = "";
		displaySuccessJSON = "{\"Return\": { \"Type\": \"user.ParentUserRegistration\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";
		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displayJSONForUserRegistrationFailure(String statusCode, String msg) {

		String displayFailureJSON = "";
		displayFailureJSON = "{\"Return\": { \"Type\": \"user.register\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";

		log.info("displayFailureJSON" + "\n" + displayFailureJSON);
		return displayFailureJSON;
	}

	public static String displayJSONForNewDevicePairing(String statusCode, String errorMsg, String msg) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"user.ParentUserDevicePair\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;

	}

	public static String displayJSONForUnPairingDevice(String statusCode, String errorMsg, String msg) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"unpairDevice\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;

	}
	
	
	public static String displayTokenValidOrInvalid(String statusCode, String errorMsg, String msg) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"common.isTokenValid\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;

	}

	public static String displayJSONForNewDevicePairingSuccess(String statusCode, String errorMsg, String msg) {
		String displaySuccessJSON = null;
		if (msg.equals("API Request Success")) {
			statusCode = "SUC01";
			displaySuccessJSON = "{\"Return\": { \"Type\": \"newDevice.pairing\",\"ResponseSummary\": { \"StatusCode\": \""
					+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";
		} else {
			displaySuccessJSON = "{\"Return\": { \"Type\": \"newDevice.pairing\",\"ResponseSummary\": { \"StatusCode\": \""
					+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";
		}

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displayJSONForUnDevicePairingSuccess(String statusCode, String errorMsg, String msg) {
		String displaySuccessJSON = null;
		if (msg.equals("API Request Success")) {
			statusCode = "SUC01";
			displaySuccessJSON = "{\"Return\": { \"Type\": \"unpairDevice\",\"ResponseSummary\": { \"StatusCode\": \""
					+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";
		} else {
			displaySuccessJSON = "{\"Return\": { \"Type\": \"unpairDevice\",\"ResponseSummary\": { \"StatusCode\": \""
					+ statusCode + "\",\"StatusMessage\": \"" + msg + "\"}}}";
		}

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displayJSONForStudentsList(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"students.StudentList\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":{\"students\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displayJSONFortimeTable(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"timetable.TeacherClassTimetableView\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":" + jsonString + "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	public static String displayJSONForParenttimeTable(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"timetable.StudentClassTimetableView\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":" + jsonString + "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
 public static String displaySuccessJSONForMemeberList(String type, String errorMsg, String statusCode, String jsonString) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":"+jsonString+"}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
 public static String displaySuccessJSONForMemeberListWithNonSubsription(String type, String errorMsg, String statusCode, String jsonString) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
 
 public static String displayGrantTeacherAccessToSleepData(String type, String errorMsg, String statusCode, String jsonString) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
 
 
 public static String displaySuccessJSONForGuardianDetails(String type, String errorMsg, String statusCode, String jsonString) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"GuardianNamesWithIds\": "+jsonString+"}}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
 
 public static String displaySuccessJSONForReminders(String type, String errorMsg, String statusCode, String jsonString) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"Reminders\": "+jsonString+"}}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
 
 public static String displaySuccessJSONForGeozones(String type, String errorMsg, String statusCode, String jsonString) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"GeozoneList\": "+jsonString+"}}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displaySuccessJSONForSchoolAppPrefList(String type, String errorMsg, String statusCode,
			String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"" + type
				+ "\",\"ResponseSummary\": { \"StatusCode\": \"" + statusCode + "\",\"StatusMessage\": \"" + errorMsg
				+ "\"},\"Results\":{\"SchoolAppPreferencesList\": " + jsonString + "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displaySuccessJSONForRewardsSchoolTeacher(String type, String errorMsg, String statusCode,
			String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"" + type
				+ "\",\"ResponseSummary\": { \"StatusCode\": \"" + statusCode + "\",\"StatusMessage\": \"" + errorMsg
				+ "\"},\"Results\":{\"studentNamesAndIds\": " + jsonString + "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displaySuccessJSONForMemberUpdate(String type, String errorMsg, String statusCode,
			String jsonString) {
		String displaySuccessJSON = "{\"Return\": { \"Type\": \"" + type
				+ "\",\"ResponseSummary\": { \"StatusCode\": \"" + statusCode + "\",\"StatusMessage\": \"" + errorMsg
				+ "\"},\"Results\":{\"system_update_available\":\"yes\",\"physical_fitness_index\":\"14,45\",\"configuration_values\": "
				+ jsonString + "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displaytimetableErrorJSON(String errorMsg, String statusCode, String type) {

		String errorResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";
		;

		return errorResponseJson;
	}

	public static String displayJSONForRewardsList(String statusCode, String errorMsg, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"teacher.SchoolRewardsList\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":{\"categories_rewards\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayJSONForUserAccountLists(String statusCode, String errorMsg, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"user.UserAccountLists\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":{\"userAccountLists\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayJSONForAssignedRewardsByTeacher(String statusCode, String errorMsg, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"teacher.StudentRewardsByTeacher\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":{\"Rewards List\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	
	public static String displayJSONForStudentRewardsList(String statusCode, String errorMsg, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"common.StudentRewardsByDate\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":{\"Rewards\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	
	public static String ParentDashboardNotifications(String statusCode, String errorMsg, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"common.ParentDashboardNotifications\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":" + jsonString
				+ "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String KidsSaftyNotifications(String statusCode, String errorMsg, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"common.KidsSaftyDashboardNotifications\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":" + jsonString
				+ "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayJSONForEventsNotificationAPI(String statusMsg, String statusCode, String jsonString, String type) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"" +type+ "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + statusMsg + "\"},\"Results\":" + jsonString
				+ "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	public static String displayParentActivitiyForDashboardJSON(String statusCode, String statusMsg, String activityType, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"" +activityType+ "\" ,\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + statusMsg + "\"},\"Results\":{" + jsonString + "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayUserDetails(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"user.UserDetails\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+ jsonString + "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	
	public static String displayStudentDetails(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"user.ParentUserDevicePair\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+ jsonString + "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayRemindersJSONForParent(String errorMsg, String statusCode, String jsonString) {

		String remindersJSON = "{\"Return\": { \"Type\": \"common.kidRemindersList\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"Reminders\": " + jsonString
				+ "}}}";

		return remindersJSON;
	}
	
	public static String displayStudentsRewards(String errorMsg, String statusCode, String jsonString) {

		String rewardsJSON = "{\"Return\": { \"Type\": \"common.StudentsRewards\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"Rewards\": " + jsonString
				+ "}}}";

		return rewardsJSON;
	}
	
	public static String displayFederatedLoginJSON(String errorMsg, String statusCode, String type, String token) {
		String federatedResponseJson = "{\"Return\": { \"Type\": \"" + type + "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"token\":\""+token+"\"}}}";
		return federatedResponseJson;
	}
	
	public static String displaySuccessOrFailureJSONForDeviceUnpair(String type, String errorMsg, String statusCode) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayJSONForScheduleList(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"scheduleList\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":" + jsonString
				+ "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayJSONForUserDetailsList(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"user.getUserDetails\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":" + jsonString
				+ "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayJSONForSysConfigurationList(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"sysConfigurationList\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":" + jsonString
				+ "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displayGrades(String errorMsg, String statusCode, String jsonString) {

		String rewardsJSON = "{\"Return\": { \"Type\": \"common.getGradeClass\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":{\"Grades\": " + jsonString
				+ "}}}";

		return rewardsJSON;
	}
	public static String displayAssigenedDeviecList(String errorMsg, String statusCode, String jsonString) {

		String rewardsJSON = "{\"Return\": { \"Type\": \"common.displayGrades\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+jsonString+ "}}";

		return rewardsJSON;
	}
	
	public static String displayDeviceStatsList(String errorMsg, String statusCode, String jsonString) {

		String rewardsJSON = "{\"Return\": { \"Type\": \"account.displayDeviceStatsList \",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+jsonString+ "}}";

		return rewardsJSON;
	}
	
	public static String displayDeviceList(String errorMsg, String statusCode, String jsonString) {

		String rewardsJSON = "{\"Return\": { \"Type\": \"account.displayDeviceList \",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+jsonString+ "}}";

		return rewardsJSON;
	}
	
	public static String displayDeviceConfigsList(String errorMsg, String statusCode, String jsonString) {

		String rewardsJSON = "{\"Return\": { \"Type\": \"account.displayDeviceList \",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+jsonString+ "}}";

		return rewardsJSON;
	}

	public static String displayTimetablecList(String errorMsg, String statusCode, String jsonString) {

		String rewardsJSON = "{\"Return\": { \"Type\": \"timetable.displayTimetable\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+jsonString+ "}}";

		return rewardsJSON;
	}
	
	public static String displayViewStudentSleepData(String statusCode, String statusMsg, String type, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"" +type+ "\" ,\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + statusMsg + "\"},\"Results\":{\"sleep\":" + jsonString + "}}}";
		
		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
	
	public static String displaySchoolAdminData(String statusCode, String statusMsg, String type, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"" +type+ "\" ,\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + statusMsg + "\"},\"Results\": "+jsonString+ "}}";
		
		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

	
	public static String displayJSONForPetDetails(String errorMsg, String statusCode, String jsonString) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \"PetDetails\",\"ResponseSummary\": { \"StatusCode\": \"SUC01"
				+ statusCode + "\",\"StatusMessage\": \"API Request Success" + errorMsg + "\"},\"Results\":" + jsonString
				+ "}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

    public static String displaycloseSOSAlert(String type, String errorMsg, String statusCode) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"}}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
	
    public static String displayJSONForTeachersStaff(String errorMsg, String statusCode, String jsonString, String type) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type + "\" ,\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \""+ errorMsg + "\"},\"Results\":{\"teacherstaff\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}

    public static String displayJSONForActivityLog(String errorMsg, String statusCode, String jsonString, String type) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type + "\" ,\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \""+ errorMsg + "\"},\"Results\":{\"activitylog\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
    public static String displayJSONForActivityNotificationLog(String errorMsg, String statusCode, String jsonString, String type,String logType) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type + "\" ,\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \""+ errorMsg + "\"},\"Results\":{\""+logType+ "\": " + jsonString
				+ "}}}";

		log.info("displaySuccessJSON" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
    }
    
    public static String displayJSONForGradewiseFitness(String errorMsg, String statusCode, String jsonString, String type) {

		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+ "\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\": "+jsonString+ "}}" ;

		log.info("GradeWiseFitnessJson" + "\n" + displaySuccessJSON);
		return displaySuccessJSON;
	}
    
 public static String displaySuccessJSONForGeofenceList(String type, String errorMsg, String statusCode, String jsonString) {
		
		String displaySuccessJSON = "{\"Return\": { \"Type\": \""+type+"\",\"ResponseSummary\": { \"StatusCode\": \""
				+ statusCode + "\",\"StatusMessage\": \"" + errorMsg + "\"},\"Results\":"+jsonString+"}}";
		log.info("displaySuccessJSON"+"\n"+displaySuccessJSON);
		return displaySuccessJSON;
	}
}
