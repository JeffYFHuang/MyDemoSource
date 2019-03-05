package com.liteon.icgwearable.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.liteon.icgwearable.model.DeviceListModel;

public class DeviceCSVToJavaUtil {

	private static Logger log = Logger.getLogger(DeviceCSVToJavaUtil.class);

	public static List<DeviceListModel> convertDevicesCsvToJava(File f) {
		log.info("Into convertDevicesCsvToJava");
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		int cellCount = 5; // Expected cells data from the CSV file.
		List<DeviceListModel> deviceList = null;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
			int i = 0;
			while ((line = br.readLine()) != null) {
				String[] devices = line.split(splitBy, cellCount);
				DeviceListModel deviceModel = new DeviceListModel();

				if (i == 0) {
					if (devices[1].isEmpty() || devices[2].isEmpty() || devices[3].isEmpty() || devices[4].isEmpty()) {
						deviceList = null;
					} else if (!(devices[1].trim().equals("school_id") && devices[2].trim().equals("device_uuid")
							&& devices[3].trim().equals("device_model")
							&& devices[4].trim().equals("firmaware_version"))) {
						log.info("<<Into Timetable Headers >>");
						deviceList = null;
					} else {
						deviceList = new ArrayList<DeviceListModel>();
					}
					i++;
					continue;
				} else if (null != devices && devices.length == cellCount) {
					deviceModel.setSchoolId(Integer.parseInt(devices[1].trim()));
					deviceModel.setUuid(devices[2].trim());
					deviceModel.setDeviceModel(devices[3].trim());
					deviceModel.setFirmWareVersion(devices[4].trim());

					deviceList.add(deviceModel);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return deviceList;
	}

}
