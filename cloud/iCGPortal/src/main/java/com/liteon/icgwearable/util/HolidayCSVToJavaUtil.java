package com.liteon.icgwearable.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.liteon.icgwearable.hibernate.entity.SchoolCalendar;

public class HolidayCSVToJavaUtil {
	private static Logger log = Logger.getLogger(HolidayCSVToJavaUtil.class);

	public static List<SchoolCalendar> convertHolidaysCsvToJava(File f, int schoolId, String dbDateTime) {
		log.info("Into convertHolidaysCsvToJava");
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";
		int cellCount = 3; // Expected cells data from the CSV file.
		List<SchoolCalendar> holidaysList = new ArrayList<>();

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
			int i = 0;
			while ((line = br.readLine()) != null) {
				log.info("Line in HolidayCSVToJavaUtil" + "\t" + line);
				String[] holidays = line.split(splitBy, cellCount);
				for (String string : holidays) {
					log.info("holiday line message" + string);
				}

				// add values from csv to car object
				if (i == 0) {
					log.info("holiday line message" + holidays.toString());
					if ((holidays[1].equals("title"))) {

						String[] holidaysData = holidays[2].split(splitBy, 2);
						if (!(holidaysData[0].equals("start") && holidaysData[1].trim().equals("end"))) {
							log.info("header not matching");
							return null;
						}
					} else {
						log.info("header not matching");
						return null;
					}
					log.info("header matches");
					i++;
				} else if (null != holidays && holidays.length == cellCount) {
					SchoolCalendar holidayObject = new SchoolCalendar();
					log.info("holidays[1]::" + "\t" + holidays[1]);
					log.info("holidays[2]::" + "\t" + holidays[2]);
					if (holidays[1] != null && !holidays[1].isEmpty()) {
						holidayObject.setName(holidays[1]);
					} else {
						holidayObject.setValidEntry(false);
						holidayObject.setSuccessMessage("  Row:" + i + " >> failed to add... " + "title is empty/null");
						log.info("  Row " + i + "failed to add... " + "title is empty/null");
						holidaysList.add(holidayObject);
						i++;
						continue;

					}

					if (holidays[2] != null && !holidays[2].isEmpty()) {
						String[] holidaysData = holidays[2].split(splitBy, 2);

						if (holidaysData[0] != null && !holidaysData[0].isEmpty()) {
							log.info("holidaysData[0]:: Before Replace" + "\t" + holidaysData[0]);
							String replaceString1 = holidaysData[0].replace('/', '-');
							log.info("holidaysData[1]:: after Replace" + "\t" + replaceString1);
							try {
								holidayObject.setDateclose(new SimpleDateFormat(dbDateTime).parse(replaceString1));
							} catch (ParseException e) {
								e.printStackTrace();
								holidayObject.setValidEntry(false);
								holidayObject.setSuccessMessage(
										"  Row: " + i + " >> failed to add... " + "start date is not parseble");
								log.info("  Row " + i + "failed to add... " + "start date is not parseble");
								holidaysList.add(holidayObject);
								i++;
								continue;
							}
						} else {
							holidayObject.setValidEntry(false);
							holidayObject.setSuccessMessage(
									"  Row: " + i + " >> failed to add... " + "start date is empty/null");
							log.info("  Row " + i + ">> failed to add... " + "start date is empty/null");
							holidaysList.add(holidayObject);
							i++;
							continue;
						}
						if (holidaysData[1] != null && !holidaysData[1].isEmpty()) {
							log.info("holidaysData[1]:: Before Replace" + "\t" + holidaysData[1]);
							String replaceString2 = holidaysData[1].replace('/', '-');
							log.info("holidaysData[1]:: after Replace" + "\t" + replaceString2);
							try {
								holidayObject.setDatereopen(new SimpleDateFormat(dbDateTime).parse(replaceString2));
							} catch (ParseException e) {
								e.printStackTrace();
								holidayObject.setValidEntry(false);
								holidayObject.setSuccessMessage(
										"  Row: " + i + " >> failed to add... " + "end date is not parseble");
								log.info("  Row " + i + "failed to add... " + "end date is not parseble");
								holidaysList.add(holidayObject);
								i++;
								continue;
							}
						}
						holidayObject.setSchoolId(schoolId);
						holidayObject.setCreated_date(new java.util.Date());
						holidayObject.setUpdated_date(new java.util.Date());
						holidayObject.setValidEntry(true);
						holidayObject
								.setSuccessMessage("  Row: " + i + "successfully added... " + holidayObject.toString());
						log.info("  Row " + i + "successfully added... ");
						holidaysList.add(holidayObject);
						i++;
					} else {
						holidayObject.setValidEntry(false);
						holidayObject.setSuccessMessage(
								"  Row: " + i + " >> failed to add... " + "Date Field is empty/null");
						log.info("  Row " + i + ">> failed to add... " + "Date Field is empty/null");
						holidaysList.add(holidayObject);
						i++;
					}
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
		log.info("HolidayCSVtoJava List" + "\t" + holidaysList.toString());
		return holidaysList;
	}
}
