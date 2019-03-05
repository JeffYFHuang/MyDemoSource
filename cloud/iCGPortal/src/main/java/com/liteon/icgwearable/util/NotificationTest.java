package com.liteon.icgwearable.util;

import java.util.HashMap;




public class NotificationTest {

private static String SERVER_KEY = "AAAA94BgTps:APA91bGmTJtU-FfOoSXhYJdnIaaZd6RT1podNXuk9jR4gw4AHoZm6noruS5SFNPh_jVvDT8sl1NYp3GaV5G1b1M3epGYZHf0sRyrca_l9x21GAnlcUSYYzn6LcUeIcZSwqAfnrP_WrFO";
//private static Logger log = Logger.getLogger(FCMUtility.class);
	
	//public static void PushNotfication(String serverKey, String appToken) {
public static void main(String args[]) {
		//SERVER_KEY = args[0];
		//private static Logger log = Logger.getLogger(DeviceController.class);
		PushNotification.setKey(SERVER_KEY);
		//PushNotification.setKey(serverKey);
		// create Notification object 
		Notification notification = new Notification();
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("Hello", "World!");
		data.put("Marco", "Polo");
		data.put("Foo", "Bar");
		// build raven message using the builder pattern
		notification.to("fnt228M8jAs:APA91bH5EGKlX0JFqvlZkNR2qxzgOYdPNFsTkmcFaB7CG7GCvyPbn1aJxMQtODk1AZh_9zlTX8rmlY9FuuZRHl0pDXg-71kpf1Vo3q9OEiZq5zCOIxC4npIg7KDrMpXfMmjdtcwlPZGI")
		//notification.to(appToken)
		//	.collapse_key("a_collapse_key")
		//	.priority(1)
		//	.delay_while_idle(true)
		//	.time_to_live(100)
		//	.dry_run(true)
		//	.data(data)
			.title("Enter/Exit School")
			.body("Enter/Exit School Event Occured at 2017-06-20 00:00:00")
			.uuid("fcmModel.getUuid()")
			.queueid(1);
		//	.color("#ff0000");
				// push the raven message
		FcmResponse response = PushNotification.push(notification);
		// alternatively set static notification first.
		PushNotification.setNotification(notification);
		response = PushNotification.push();
		System.out.println(response);
		// prints response code and message
		//log.info("response"+response);
		//log.info(response);
		notification.clear(); 
		notification.clearAttributes(); 
		notification.clearTargets();
	}

}
