package com.liteon.icgwearable.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;
import com.liteon.icgwearable.hibernate.entity.ActivityLog;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.IPSReceiver;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.transform.SessionDetailsForIDTransform;

public class CommonUtil {
	
	private CommonUtil(){}
	
	private static Logger log = Logger.getLogger(CommonUtil.class);
    public static final String notvalid = "NOTVALID"; 
    public static final String valid = "VALID";
	public static String checkSessionValidity(Users user) {
		log.info("Entered into checkSessionValidity {");
		String finalResult = valid;
		Date sessionExpiry = null;
		if ((user != null) && (user.getSessionExpiry() != null) && null == user.getMobileSessionId()) {
			log.info("User is Not Null AND getSessionExpiry is Not Null");
			sessionExpiry = user.getSessionExpiry();
			Timestamp currenttimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
			Date currentDate = new Date(currenttimestamp.getTime());
			if (currentDate.after(sessionExpiry)) {
				log.info("Session already expired, currentDate:" + currentDate + ", sessionExpiry:" + sessionExpiry);
				finalResult = notvalid;
			}
		}
		if ((user == null) || (user.getSessionId() == null && null == user.getMobileSessionId())) {
			log.info("User is Null OR getSessionExpiry is Null");
			finalResult = notvalid;
		}
		return finalResult;
	}
	
	public static String checkSessionValidity(IPSReceiver iPSReceiver) {
		log.info("Entered into checkSessionValidity {");
		String finalResult = valid;
		Date sessionExpiry = null;
		if ((iPSReceiver != null) && (iPSReceiver.getSessionExpiry() != null)) {
			sessionExpiry = iPSReceiver.getSessionExpiry();
			Timestamp currenttimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
			Date currentDate = new Date(currenttimestamp.getTime());
			if (currentDate.after(sessionExpiry)) {
				finalResult = notvalid;
			}
		}
		if ((iPSReceiver == null) || (iPSReceiver.getSessionId() == null)) {
			finalResult = notvalid;
		}
		return finalResult;
	}
	

	public static String checkSessionValidity(SessionDetailsForIDTransform sessioninfo) {
		log.info("Entered into checkSessionValidity with SessionDetailsForIDTransform arg");
		String finalResult = valid;
		Date sessionExpiry = null;
		if ((sessioninfo != null) && (sessioninfo.getExpiryduration() != null)) {
			sessionExpiry = sessioninfo.getExpiryduration();
			Timestamp currenttimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
			Date currentDate = new Date(currenttimestamp.getTime());
			if (currentDate.after(sessionExpiry)) {
				finalResult = notvalid;
			}
		}
		if ((sessioninfo == null) || (sessioninfo.getSessionId() == null)) {
			finalResult = notvalid;
		}
		return finalResult;
	}
   public static boolean  validateSession(String sessionIdProvided, SessionDetailsForIDTransform sessionforcheck){
		   
		   
		   log.info("Inside commonUtil.validateSession:::sessionIdProvided="+sessionIdProvided);
		  // SessionDetailsForIDTransform  sessionforcheck =null;
		   boolean flag = false;
		   String oldSession = null;
		   
		   if(sessionforcheck==null){
		     return false; 
		   } else {  
		  
		    if(sessionforcheck.getSessionId()!= null)
		     oldSession = sessionforcheck.getSessionId(); //sessionid
		      
		   if (oldSession != null && oldSession.trim().length() > 0 && sessionIdProvided.equals(oldSession)
		     && checkSessionValidity(sessionforcheck).equalsIgnoreCase("VALID")) {
			   sessionIdProvided = oldSession;  		    
			   flag=true;
		     } 
		   } 
		   
		return flag;
	}  //end of method
	public static boolean  validateSession(int receiver_Id, String sessionId, int  sessionValidity, SessionDetailsForIDTransform sessionforcheck, String ipsLoginValidity ){
		   
		   
		   log.info("Inside commonUtil.validateSession::: receiver_Id.="+receiver_Id
		      +" ,sessionId="+sessionId+" ,sessionValidity= "+sessionValidity);
		  // SessionDetailsForIDTransform  sessionforcheck =null;
		   boolean flag = false;
		   String oldSession = null;
		   
		   if(sessionforcheck==null){
		     return false; 
		   } else {  
		    
		    sessionValidity = Integer.valueOf(ipsLoginValidity);
		    if(sessionforcheck.getSessionId()!= null)
		     oldSession = sessionforcheck.getSessionId(); //sessionid
		      
		   if (oldSession != null && oldSession.trim().length() > 0 && sessionId.equals(oldSession)
		     && checkSessionValidity(sessionforcheck).equals("VALID")) {
		    sessionId = oldSession;    
		    
		    flag=true;
		   } /*else {
		    this.httpSession.invalidate();
		    sessionId = this.httpSession.getId();
		    int res = this.ipsLocatorsService.updateIPSLocatorsBySessionId(locators_Id, sessionId);
		    flag=true;
		   } */
		   } 
		   
		return flag;
	}  //end of method
	
	public static String checkActivationCodeValidity(Users user) {
		log.info("Entered into checkActivationCodeValidity {");
		String finalResult = valid;
		Date activationCodeExpiry = null;
		if ((user != null) && (user.getActivationCodeExpiry() != null)) {
			activationCodeExpiry = user.getActivationCodeExpiry();
			Timestamp currenttimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
			Date currentDate = new Date(currenttimestamp.getTime());
			if (currentDate.after(activationCodeExpiry)) {
				finalResult = notvalid;
			}
			log.info("currentDate"+"\t"+currentDate);
			log.info("activationCodeExpiry"+"\t"+activationCodeExpiry);
		}
		log.info("Result"+"\t"+finalResult);
		log.info("Exiting checkActivationCodeValidity }");
		return finalResult;
	}
	
	public static String checkSessionValidity(Devices devices) {
		log.info("Entered into checkSessionValidity {");
		String finalResult = valid;
		Date sessionExpiry = null;
		if ((devices != null) && (devices.getSession_expiry() != null)) {
			sessionExpiry = devices.getSession_expiry();
			Timestamp currenttimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
			Date currentDate = new Date(currenttimestamp.getTime());
			if (currentDate.after(sessionExpiry)) {
				finalResult = notvalid;
			}
		}
		if ((devices == null) || (devices.getSession_expiry() == null)) {
			finalResult = notvalid;
		}
		return finalResult;
	}
	
	public static ActivityLog formulateActivityLogs(Users user, String action, String summary, String ipaddress) {

	
		ActivityLog activityLog = new ActivityLog();
		activityLog.setName("");
		activityLog.setUserName("");
		activityLog.setUserRole("");
		try {
		if (null != user && null != user.getName()){
			activityLog.setName(user.getName());
			log.debug("activityLog.getName() >>>>>>>>>>>>>>>> "+activityLog.getName());
		}
		} catch(NullPointerException e) {
			log.error("formulateActivityLogs() ", e);
		}
		try {
		if (null != user && null != user.getUsername())
			activityLog.setUserName(user.getUsername());
		
		
		} catch(NullPointerException e) {
			log.error("formulateActivityLogs() ", e);
		}
		try {
		if ( null != user && null != user.getRoleType())
			activityLog.setUserRole(user.getRoleType());
				
		} catch(NullPointerException e) {
			log.error("formulateActivityLogs() ", e);
		}
		activityLog.setAction(action);
		activityLog.setSummary(formatSummary(summary));
		activityLog.setIpaddress(ipaddress);
		return activityLog;
	}

	public static ActivityLog formulateActivityLogs(String username, String action, String summary, String ipaddress, String name, String role) {

		
		ActivityLog activityLog = new ActivityLog();
		activityLog.setName("");
		activityLog.setUserName("");
		activityLog.setUserRole("");	
					
		try {
			if (null != username){
				activityLog.setUserName(username);
				log.debug("activityLog.getName() >>>>>>>>>>>>>>>> "+activityLog.getName()+  "summary="+summary);
			}
			} catch(NullPointerException e) {
				log.error("formulateActivityLogs() ", e);
			}

		activityLog.setName(name);
		activityLog.setUserRole(role);
		activityLog.setAction(action);
		activityLog.setSummary(formatSummary(summary));
		activityLog.setIpaddress(ipaddress);
		return activityLog;
	}
	
	
	public static String formatSummary(String summary) {

		char needle = '.';
		int count = 0;
		String output = "";

		for (int i = 0; i < summary.length(); i++) {
			if (summary.charAt(i) == needle) {
				count++;
				if (count == 4) {
					output = summary.substring(i + 1, summary.length());
					
				}
			}

		}
		return output;
	}
	
	public static String isStudentAbnormal(String actualTime, String startTime,String endTime)
	{
		
		 String isAbnormal = "no";
		 
		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		    Date start_time = null;
		    Date end_time = null;
		    Date actual_time = null;
		    try {
		    	start_time = sdf.parse(startTime);
		    	end_time = sdf.parse(endTime);
		    	actual_time = sdf.parse(actualTime);
		    	
		    	if(actual_time.before(start_time) ||actual_time.after(end_time))
		    		isAbnormal ="yes";
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
	   
		return isAbnormal;
	}
	
	public static String isStudentSoSAbnormal(String actualTime, String startTime,String endTime)
	{
		
		 String isAbnormal = "no";
		 
		    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		    Date start_time = null;
		    Date end_time = null;
		    Date actual_time = null;
		    try {
		    	start_time = sdf.parse(startTime);
		    	end_time = sdf.parse(endTime);
		    	actual_time = sdf.parse(actualTime);
		    	log.info("actual_time"+actual_time);
		    	log.info("start_time"+start_time);
		    	log.info("end_time"+end_time);
		    	
		    	if(actual_time.after(start_time) && actual_time.before(end_time))
		    		isAbnormal ="yes";
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
	   
		return isAbnormal;
	}
	
	public  static int getGeocodesPosition(String  center, String  point, double radius) {
		double lat1, lang1, lat2, lang2;
		Double earthRadius = 6371.0;

		lat1 = Double.parseDouble(center.split(",")[0]);
		lang1 = Double.parseDouble(center.split(",")[1]);
		

		lat2 = Double.parseDouble(point.split(",")[0]);
		lang2 = Double.parseDouble(point.split(",")[1]);
		
		Double diffBetweenLatitudeRadians = Math.toRadians(lat2 - lat1);
        Double diffBetweenLongitudeRadians = Math.toRadians(lang2 - lang1);
        Double latitudeOneInRadians = Math.toRadians(lat1);
        Double latitudeTwoInRadians = Math.toRadians(lat2);
        Double a = Math.sin(diffBetweenLatitudeRadians / 2) * Math.sin(diffBetweenLatitudeRadians / 2) + Math.cos(latitudeOneInRadians) * Math.cos(latitudeTwoInRadians) * Math.sin(diffBetweenLongitudeRadians / 2)
                * Math.sin(diffBetweenLongitudeRadians / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double geoPosition = (earthRadius * c * 1000);
        
        if (geoPosition <= radius)
			return -1;
		else if (geoPosition > radius)
			return 1;
		else
			return -1;
	}
	
	public static String getHostIpaddress(){
		InetAddress ipAddr;
		String ipaddress = null;
		try {
			ipAddr = InetAddress.getLocalHost();
			ipaddress = ipAddr.getHostAddress();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return ipaddress;
	}
	
	
	public static boolean empty( final String s ) {
		  return s == null || s.trim().isEmpty();
		}
}
