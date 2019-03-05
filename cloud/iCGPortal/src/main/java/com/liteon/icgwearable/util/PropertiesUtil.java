package com.liteon.icgwearable.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
	public static Properties getProperties() {
		Properties prop = new Properties();
		
		InputStream input = null;
		try {
			String filename = "application.properties";
			input = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find " + filename);
			}
			// load a properties file from class path, inside static method
			prop.load(input);
			// get the property value and print it out
			System.out.println(prop.getProperty("cassendra.host"));
			System.out.println(prop.getProperty("cassendra.keyspace"));
			System.out.println(prop.getProperty("cassandra.username"));
			System.out.println(prop.getProperty("cassandra.password"));
			System.out.println(prop.getProperty("ENTRY_ALERT_ID"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
	
	public static Map getDeviceIDNameMapping()
	{
		Map<Object, Object> deviceMap = new HashMap<Object, Object>();
		
		Properties prop = new Properties();
		
		InputStream input = null;
		try {
			String filename = "application.properties";
			input = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find " + filename);
			}
			// load a properties file from class path, inside static method
			prop.load(input);
			// get the property value and print it out
			
			deviceMap.put(prop.getProperty("SCHOOL_ENTRY_ALERT_ID"),prop.getProperty("ENTRY_ALERT_NAME"));
			deviceMap.put(prop.getProperty("SCHOOL_EXIT_ALERT_ID"),prop.getProperty("EXIT_ALERT_NAME"));
			deviceMap.put(prop.getProperty("GEOFENCE_ENTRY_ID"),prop.getProperty("GEOFENCE_ENTRY_NAME"));
			deviceMap.put(prop.getProperty("GEOFENCE_EXIT_ID"),prop.getProperty("GEOFENCE_EXIT_NAME"));
			deviceMap.put(prop.getProperty("SOS_ALERT_ID"),prop.getProperty("SOS_ALERT_NAME"));
			deviceMap.put(prop.getProperty("FALL_DETECTION_ID"),prop.getProperty("FALL_DETECTION_NAME"));
			deviceMap.put(prop.getProperty("ABNORMAL_VITAL_SIGN_ID"),prop.getProperty("ABNORMAL_VITAL_SIGN_NAME"));
			deviceMap.put(prop.getProperty("SENSOR_MALFUNCTION_ID"),prop.getProperty("SENSOR_MALFUNCTION_NAME"));
			deviceMap.put(prop.getProperty("LOW_BATTERY_ID"),prop.getProperty("LOW_BATTERY_NAME"));
			deviceMap.put(prop.getProperty("STUDENT_LOCATION_ID"),prop.getProperty("STUDENT_LOCATION_NAME"));
			deviceMap.put(prop.getProperty("BAND_REMOVAL_ALERT_ID"),prop.getProperty("BAND_REMOVAL_ALERT_NAME"));
			System.out.println(deviceMap);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return deviceMap;
	}

	public static void main(String[] args) {
		getProperties();
	}
}
