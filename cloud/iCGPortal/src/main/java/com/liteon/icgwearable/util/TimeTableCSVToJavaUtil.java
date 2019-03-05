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

import com.liteon.icgwearable.model.TimeTableModel;

public class TimeTableCSVToJavaUtil {

	private static Logger log = Logger.getLogger(TimeTableCSVToJavaUtil.class);

	public static List<TimeTableModel> convertTimeTablesCsvToJava(File f) {
		log.info("Into convertTimeTablesCsvToJava");
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		int cellCount = 12; // Expected cells data from the CSV file.
		List<TimeTableModel> timeTablesList = new ArrayList<>();

		try {
			br = new BufferedReader(
		            new InputStreamReader(
		                       new FileInputStream(f), "UTF8"));
			int i = 0;
			while ((line = br.readLine()) != null) {
				String[] timetables = line.split(splitBy, cellCount);
				TimeTableModel timetableObject = new TimeTableModel();
				if (i == 0) {
					if (!(timetables[1].trim().equals("grade") && timetables[2].trim().equals("class")
							&& timetables[3].trim().equals("week_day") && timetables[4].trim().equals("subject1")
							&& timetables[5].trim().equals("subject2") && timetables[6].trim().equals("subject3")
							&& timetables[7].trim().equals("subject4") && timetables[8].trim().equals("subject5")
							&& timetables[9].trim().equals("subject6") && timetables[10].trim().equals("subject7")
							&& timetables[11].trim().equals("subject8"))) {
						log.info("<<Into Timetable Headers >>");
						timeTablesList = null;
					}
					i++;
					continue;
				} else if (null != timetables && timetables.length == cellCount) {
					timetableObject.setGrade(timetables[1].trim());
					timetableObject.setStudentClass(timetables[2].trim());
					timetableObject.setWeekDay(timetables[3].trim());
					timetableObject.setSubjectOne(timetables[4].trim());
					timetableObject.setSubjectTwo(timetables[5].trim());
					timetableObject.setSubjectThree(timetables[6].trim());
					timetableObject.setSubjectFour(timetables[7].trim());
					timetableObject.setSubjectFive(timetables[8].trim());
					timetableObject.setSubjectSix(timetables[9].trim());
					timetableObject.setSubjectSeven(timetables[10].trim());
					timetableObject.setSubjectEight(timetables[11].trim());
					timeTablesList.add(timetableObject);
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
		return timeTablesList;
	}
}
