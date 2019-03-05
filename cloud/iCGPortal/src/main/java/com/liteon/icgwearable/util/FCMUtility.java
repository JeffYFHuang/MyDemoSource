package com.liteon.icgwearable.util;

import org.apache.log4j.Logger;
import com.liteon.icgwearable.model.FCMModel;



public class FCMUtility {

//private static String SERVER_KEY = "AIzaSyCM9SK5ufuOzLfC8Bo7fwQ96dEUsfmxtb0";
private static Logger log = Logger.getLogger(FCMUtility.class);
	
	public static void PushNotfication(String serverKey, String appToken,FCMModel fcmModel) {
		//SERVER_KEY = args[0];
		//private static Logger log = Logger.getLogger(DeviceController.class);
		//PushNotification.setKey(SERVER_KEY);
		PushNotification.setKey(serverKey);
		// create Notification object 
		Notification notification = new Notification();
	 if(!fcmModel.getUuid().equals("Empty")) {
		// build raven message using the builder pattern
		//notification.to("eE4EQ1Ob6VE:APA91bGBlZOEcIWL0VC8TkAERlQy7Qefwz1j8qQ-K7YQtrueheFDoG0v_e4CQnP-GEckfR0i1p3zzx_UhLzIGCX6gdizKdGmKhgQmnWsUIJeTR1R9K3jIQ6JVkIsepRoBa7-9tZwHNmw")
		notification.to(appToken)
			//.collapse_key("a_collapse_key")
			//.priority(1)
			//.delay_while_idle(true)
			//.time_to_live(100)
			//.dry_run(true)
			//.data(data)
			.title(fcmModel.getTitle())
			.body(fcmModel.getBody())
			.uuid(fcmModel.getUuid())
			.deviceeventid(fcmModel.getDeviceEventId())
			.studentName(fcmModel.getStudentName())
			.queueid(fcmModel.getQueueid())
			.eventid(fcmModel.getEventId());
		log.info("fcmModel.getUuid() NOT Empty:>>" + notification.toString());
	 }
	 else
	 {
		 notification.to(appToken)
			.title(fcmModel.getTitle())
			.body(fcmModel.getBody());
		 log.info("fcmModel.getUuid() IS EMPTY:>>" + notification.toString());
	 }
		// push the raven message
		FcmResponse response = PushNotification.push(notification);
		// alternatively set static notification first.
		PushNotification.setNotification(notification);
		// prints response code and message
		log.info("response from FCM "+response);
		//log.info(response);
		notification.clear(); 
		notification.clearAttributes(); 
		notification.clearTargets();
	
	}

}
