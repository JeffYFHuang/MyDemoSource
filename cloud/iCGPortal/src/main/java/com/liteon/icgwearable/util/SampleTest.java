package com.liteon.icgwearable.util;

public class SampleTest {
	// This is a java program to check whether point d lies inside or outside
	// the circle defined by a point

	public static void main(String args[]) {
		double c1, c2, r, x, y;
		
		String center = "-34.32801379795024,150.40008544921875";
		String point = "-34.33935397998182,150.42755126953125";
		r = 4410.56677596723;

		c1 = Double.parseDouble(center.split(",")[0]);
		c2 = Double.parseDouble(center.split(",")[1]);
		

		x = Double.parseDouble(point.split(",")[0]);
		y = Double.parseDouble(point.split(",")[1]);

		System.out.println("The center of the circle is (" + c1 + ", " + c2 + ") and radius is " + r);

		System.out.println("The point :" + x + ", " + y);

		double dis = distFrom(c1, c2, x, y);
		
		System.out.println("The Distance :" + dis + ", radius: " + r);
		
		if (dis < r)
			System.out.println("The point lies inside the circle");
		else if (dis > r)
			System.out.println("The point lies outside the circle");
		else
			System.out.println("The point lies on the circle");
	}

	private static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		Double earthRadius = 6371.0;
        Double diffBetweenLatitudeRadians = Math.toRadians(lat2 - lat1);
        Double diffBetweenLongitudeRadians = Math.toRadians(lng2 - lng1);
        Double latitudeOneInRadians = Math.toRadians(lat1);
        Double latitudeTwoInRadians = Math.toRadians(lat2);
        Double a = Math.sin(diffBetweenLatitudeRadians / 2) * Math.sin(diffBetweenLatitudeRadians / 2) + Math.cos(latitudeOneInRadians) * Math.cos(latitudeTwoInRadians) * Math.sin(diffBetweenLongitudeRadians / 2)
                * Math.sin(diffBetweenLongitudeRadians / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (earthRadius * c * 1000);
	}
}

/*
 * public class SampleTest {
 * 
 * public final static String AUTH_KEY_FCM =
 * "AIzaSyCM9SK5ufuOzLfC8Bo7fwQ96dEUsfmxtb01"; public final static String
 * API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
 * 
 * // userDeviceIdKey is the device id you will query from your database
 * 
 * public static void main(String args[]) { String userDeviceIdKey
 * ="fnt228M8jAs:APA91bH5EGKlX0JFqvlZkNR2qxzgOYdPNFsTkmcFaB7CG7GCvyPbn1aJxMQtODk1AZh_9zlTX8rmlY9FuuZRHl0pDXg-71kpf1Vo3q9OEiZq5zCOIxC4npIg7KDrMpXfMmjdtcwlPZGI";
 * String authKey = AUTH_KEY_FCM; // You FCM AUTH key String FMCurl =
 * API_URL_FCM; try { URL url = new URL(FMCurl); HttpURLConnection conn =
 * (HttpURLConnection) url.openConnection();
 * 
 * conn.setUseCaches(false); conn.setDoInput(true); conn.setDoOutput(true);
 * 
 * conn.setRequestMethod("POST");
 * conn.setRequestProperty("Authorization","key="+authKey);
 * conn.setRequestProperty("Content-Type","application/json");
 * 
 * JSONObject json = new JSONObject(); json.put("to",userDeviceIdKey.trim());
 * JSONObject info = new JSONObject(); info.put("title", "Notificatoin Title");
 * // Notification title info.put("body", "Hello Test notification"); //
 * Notification body json.put("notification", info); OutputStreamWriter wr = new
 * OutputStreamWriter(conn.getOutputStream()); wr.write(json.toString());
 * wr.flush(); conn.getInputStream(); }catch(Exception e) {e.printStackTrace();}
 * }
 * 
 * }
 */