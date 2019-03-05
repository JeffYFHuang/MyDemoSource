package com.liteon.icgwearable.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.liteon.icgwearable.model.TeacherCSVModel;

public class TeacherCSVToJavaUtil {
	private static Logger log = Logger.getLogger(TeacherCSVToJavaUtil.class);

	static public void main(String[] args) {
	}

	public static List<TeacherCSVModel> convertTeachersCsvToJava(File f) {
		log.info("Into convertCsvToJava");
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		int cellCount = 7; // Expected cells data from the CSV file.
		List<TeacherCSVModel> teachersList = new ArrayList<>();

		try {
			br = new BufferedReader(new FileReader(f));
			int i = 0;
			while ((line = br.readLine()) != null) {
				log.info("Line >>>" + "\t" + line);
				String[] teachers = line.split(splitBy, cellCount);

				// create car object to store values
				TeacherCSVModel teacherObject = new TeacherCSVModel();

				if (i == 0) {
					if (!(teachers[0].equals("name")
							|| teachers[0].trim().equals("name") && teachers[1].equals("username")
							|| teachers[1].trim().equals("username") && teachers[2].equals("password")
							|| teachers[2].trim().equals("password") && teachers[3].equals("uuid")
							|| teachers[3].trim().equals("uuid") || teachers[4].trim().equals("contactno")
							|| teachers[5].trim().equals("class") || teachers[6].trim().equals("grade"))) {
						teachersList = null;
					}
					i++;
				} else if (null != teachers && teachers.length == cellCount) {
					teacherObject.setName(teachers[0]);
					teacherObject.setUsername(teachers[1]);
					teacherObject.setPassword(teachers[2]);
					teacherObject.setUuid(teachers[3]);
					teacherObject.setContactNo(teachers[4]);
					teacherObject.setStudentClass(teachers[5]);
					teacherObject.setGrade(teachers[6]);
					teachersList.add(teacherObject);
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
		return teachersList;
	}
}
