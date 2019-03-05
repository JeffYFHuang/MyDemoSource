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
import com.liteon.icgwearable.model.TeachersStaffCSVModel;

public class TeachersStaffCSVToJavaUtil {

	private static Logger log = Logger.getLogger(TeachersStaffCSVToJavaUtil.class);

	public static List<TeachersStaffCSVModel> convertTeachersStaffCsvToJava(File f) {
		log.info("Into convertTeachersStaffCsvToJava");
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		int cellCount = 7; // Expected cells data from the CSV file.
		List<TeachersStaffCSVModel> teachersStaffList = new ArrayList<>();

		try {
			br = new BufferedReader(
		            new InputStreamReader(
		                       new FileInputStream(f), "UTF8"));
			int i = 0;
			while ((line = br.readLine()) != null) {
				log.info("Into While");
				log.info("line" + "\t" + line);
				String[] teachersStaff = line.split(splitBy, cellCount);

				// create TeachersStaffCSVModel object to store values
				TeachersStaffCSVModel teacherStaffModel = new TeachersStaffCSVModel();
				if (i == 0) {
					log.info("Logging to Checkin the Headers");
					if (!(teachersStaff[1].trim().equals("grade") && teachersStaff[2].trim().equals("class")
							&& teachersStaff[3].trim().equals("name") && teachersStaff[4].trim().equals("username")
							&& teachersStaff[5].trim().equals("role") && teachersStaff[6].trim().equals("contact"))) {
						log.info("<< Checking Headers Of Teachers CSV>>");
						teachersStaffList = null;
						break;
					}
				} else if (null != teachersStaff && teachersStaff.length == cellCount) {
					teacherStaffModel.setGrade(teachersStaff[1].trim());
					teacherStaffModel.setStClass(teachersStaff[2].trim());
					teacherStaffModel.setName(teachersStaff[3].trim());
					teacherStaffModel.setUsername(teachersStaff[4].trim());
					teacherStaffModel.setRole(teachersStaff[5].trim());
					teacherStaffModel.setContactNo(teachersStaff[6].trim());
					teachersStaffList.add(teacherStaffModel);
				}
				i++;
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
		log.info("teachersStaffList.size() In CSV Util" + "\t" + teachersStaffList.size());
		return teachersStaffList;
	}
}
