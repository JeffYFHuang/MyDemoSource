package com.liteon.icgwearable.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.liteon.icgwearable.model.StudentCSVModel;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class StudentCSVToJavaUtil {
	private static Logger log = Logger.getLogger(StudentCSVToJavaUtil.class);

	public static List<StudentCSVModel> convertStudentsCsvToJava(File f) {
		log.info("Into convertStudentsCsvToJava() {");
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		int cellCount = 8; //Expected cells data from the CSV file.
		List<StudentCSVModel> studentsList = new ArrayList<>();
		try {
			br = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(f), "UTF8"));
			int i = 0;
			while ((line = br.readLine()) != null) {
				log.info("Line convertStudentsCsvToJava()>>>" + "\t" + line);
				String[] students = line.split(splitBy, cellCount);
				StudentCSVModel studentObject = new StudentCSVModel();

				if (i == 0) {
					if (!(students[1].trim().equals("grade") && students[2].trim().equals("class")
							&& students[3].trim().equals("admission_no") && students[4].trim().equals("class_roll_no")
							&& students[5].trim().equals("name") && students[6].trim().equals("emergency_contact")
							&& students[7].trim().equals("device_uuid"))) {
						log.info("<< Checking Headers Of Students CSV>>");
						log.info("grade >>" + students[1]);
						log.info("class >>" + students[2]);
						log.info("admission_no >>" + students[3]);
						log.info("class_roll_no>>" + students[4]);
						log.info("name >>" + students[5]);
						log.info("emergency_contact >>" + students[6]);
						log.info("device_uuid >>" + students[7]);
						studentsList = null;
					}
				} else if (null != students && students.length == cellCount) {
					log.info("Value of i in StudentCSVToJavaUtil"+"\t"+i);
					studentObject.setGrade(students[1].trim());
					studentObject.setStudentClass(students[2].trim());
					studentObject.setAdmissionNo(students[3].trim());
					studentObject.setStudentRollNo(students[4].trim());
					studentObject.setStudentName(students[5].trim());
					studentObject.setEmergency_contact(students[6].trim());
					studentObject.setUuid(students[7].trim());
					studentsList.add(studentObject);
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
	    } catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		log.info("Exiting convertStudentsCsvToJava() }");
		return studentsList;
	}
}
