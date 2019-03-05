package com.liteon.icgwearable.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.*;

public class GenerateTestDataForCassandraDB {
	private static final String url = "jdbc:mysql://172.31.157.58:3306/ext_icgcloud?zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8";
	private static final String user = "root";
	private static final String password = "MySq&23DF1$%@#^!";
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	private static String schemaName = "icg23012018";

	public static void main(String[] args) throws ParseException, IOException {
		String query = " SELECT uuid FROM devices WHERE status = 'assigned' ";
		try {
			con = DriverManager.getConnection(url, user, password);
			stmt = con.createStatement();

			String[] tableNames = { "emotion_date", "stress_date" };
			String[] emotionHourTable = { "emotion_hour" };
			String[] stressHourTable = { "stress_hour" };
			String[] contextDateTable = { "context_date" };
			String[] hrmDateTable = { "hrm_date" };
			String[] sleepDateTable = { "sleep_date" };
			String[] stepDateTable = { "step_date" };

			Date currDate = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String myDate = dateFormat.format(currDate);

			Date prevDate = dateFormat.parse(myDate);

			Random random = new Random();
			int max = 15, min = 1;
			int mamSituation = 3, minSituation = 1;

			int durationMax = 60, durationMin = 1;
			int situationMax = 4, situationMin = 1;

			int situationMaxForStress = 2, situationMinForStress = 1;

			String fileName = "D:\\workspace\\iCGCloud\\src\\main\\resources\\test-data.txt";
			File file = new File(fileName);
			Files.deleteIfExists(file.toPath());

			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(new FileOutputStream(new File(fileName), true), "UTF8"));
			Date date = new Date();

			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
			System.out.println(
					"Enter 0 if you want to generate data for the current date. \n"
					+ "Enter 1 if you want to generate data for the current date and past 1 day and So on....\n"
					+ "Enter -2 to provide data for Tomorrow, -3 for Tomorrow & Day After Tomorrow and so on....");
			int dateCount = 0;
			try {
				dateCount = Integer.parseInt(userInput.readLine()) + 1;
			} catch (NumberFormatException nfe) {
				System.out.println("Invalid Input");
				System.exit(0);
			}

			// Generate data for emotion date and stress
			for (int i = 0; i < tableNames.length; i++) {
				String uuid = null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					uuid = rs.getString(1);
					if (dateCount >= 0) {
						for (int timestamp = 0; timestamp < dateCount; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int durationRange = random.nextInt(durationMax - durationMin + 1) + durationMin;
							int situationRange = random.nextInt(situationMax - situationMin + 1) + situationMin;
							String line = "INSERT INTO " + schemaName + "." + tableNames[i]
									+ "(uuid, ts, duration, situation)" + "VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + durationRange + ", " + situationRange + ");";
							writer.print(line + "\n");
						}
					} else {
						for (int timestamp = dateCount; timestamp < 0; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int durationRange = random.nextInt(durationMax - durationMin + 1) + durationMin;
							int situationRange = random.nextInt(situationMax - situationMin + 1) + situationMin;
							String line = "INSERT INTO " + schemaName + "." + tableNames[i]
									+ "(uuid, ts, duration, situation)" + "VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + durationRange + ", " + situationRange + ");";
							writer.print(line + "\n");
						}
					}
				}
			}

			// Generate data for the emotion_hour for past 5 days with
			// hour:min:second
			for (int i = 0; i < emotionHourTable.length; i++) {
				String uuid = null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					uuid = rs.getString(1);
					if (dateCount >= 0) {
						for (int timestamp = 0; timestamp < dateCount; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							for (int j = 8; j < 23; j++) {
								calendar.setTime(date);
								calendar.set(Calendar.HOUR_OF_DAY, j);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
								Date previousDate = calendar.getTime();
								long prevMillis = previousDate.getTime() / 1000;
								int durationRange = random.nextInt(durationMax - durationMin + 1) + durationMin;
								int situationRange = random.nextInt(situationMax - situationMin + 1) + situationMin;
								String line = "INSERT INTO " + schemaName + ".emotion_hour(uuid, ts, duration, situation)"
										+ "VALUES('" + uuid + "'," + Long.toString(prevMillis) + "," + durationRange + ", "
										+ situationRange + ");";
								writer.print(line + "\n");
							}
						}
					}else{
						for (int timestamp = dateCount; timestamp < 0; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							for (int j = 8; j < 23; j++) {
								calendar.setTime(date);
								calendar.set(Calendar.HOUR_OF_DAY, j);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
								Date previousDate = calendar.getTime();
								long prevMillis = previousDate.getTime() / 1000;
								int durationRange = random.nextInt(durationMax - durationMin + 1) + durationMin;
								int situationRange = random.nextInt(situationMax - situationMin + 1) + situationMin;
								String line = "INSERT INTO " + schemaName + ".emotion_hour(uuid, ts, duration, situation)"
										+ "VALUES('" + uuid + "'," + Long.toString(prevMillis) + "," + durationRange + ", "
										+ situationRange + ");";
								writer.print(line + "\n");
							}
						}
					}
				}
			}

			// Generate data for the stress_hour for past 5 days with
			// hour:min:second
			for (int i = 0; i < stressHourTable.length; i++) {
				String uuid = null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					uuid = rs.getString(1);
					if (dateCount >= 0) {
						for (int timestamp = 0; timestamp < dateCount; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							for (int j = 8; j < 23; j++) {
								calendar.setTime(date);
								calendar.set(Calendar.HOUR_OF_DAY, j);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
								Date previousDate = calendar.getTime();
								long prevMillis = previousDate.getTime() / 1000;
								int durationRange = random.nextInt(durationMax - durationMin + 1) + durationMin;
								int situationRange = random.nextInt(situationMaxForStress - situationMinForStress + 1)
										+ situationMinForStress;
								String line = "INSERT INTO " + schemaName + ".stress_hour(uuid, ts, duration, situation)"
										+ "VALUES('" + uuid + "'," + Long.toString(prevMillis) + "," + durationRange + ", "
										+ situationRange + ");";
								writer.print(line + "\n");
							}
						}
					}else{
						for (int timestamp = dateCount; timestamp < 0; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							for (int j = 8; j < 23; j++) {
								calendar.setTime(date);
								calendar.set(Calendar.HOUR_OF_DAY, j);
								calendar.set(Calendar.MINUTE, 0);
								calendar.set(Calendar.SECOND, 0);
								calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
								Date previousDate = calendar.getTime();
								long prevMillis = previousDate.getTime() / 1000;
								int durationRange = random.nextInt(durationMax - durationMin + 1) + durationMin;
								int situationRange = random.nextInt(situationMaxForStress - situationMinForStress + 1)
										+ situationMinForStress;
								String line = "INSERT INTO " + schemaName + ".stress_hour(uuid, ts, duration, situation)"
										+ "VALUES('" + uuid + "'," + Long.toString(prevMillis) + "," + durationRange + ", "
										+ situationRange + ");";
								writer.print(line + "\n");
							}
						}
					}
				}
			}

			// Generate data for context date for past 5 days including current
			for (int i = 0; i < contextDateTable.length; i++) {
				String uuid = null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					uuid = rs.getString(1);
					if (dateCount >= 0) {
						for (int timestamp = 0; timestamp < dateCount; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							for (int j = 0; j < 3; j++) {
								int range = random.nextInt(max - min + 1) + min;
								int range2 = random.nextInt(max - min + 1) + min;
								int range3 = random.nextInt(max - min + 1) + min;
								int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
								String line = "INSERT INTO " + schemaName + "." + contextDateTable[i]
										+ "(uuid, ts, situation, activeindex, avghrm, duration, hrmcount, met) VALUES('"
										+ uuid + "'," + Long.toString(prevMillis) + "," + sitationRange + "," + range + ","
										+ range2 + "," + range3 + "," + range + "," + range2 + ");";
								writer.print(line + "\n");
							}
						}
					}else{
						for (int timestamp = dateCount; timestamp < 0; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							for (int j = 0; j < 3; j++) {
								int range = random.nextInt(max - min + 1) + min;
								int range2 = random.nextInt(max - min + 1) + min;
								int range3 = random.nextInt(max - min + 1) + min;
								int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
								String line = "INSERT INTO " + schemaName + "." + contextDateTable[i]
										+ "(uuid, ts, situation, activeindex, avghrm, duration, hrmcount, met) VALUES('"
										+ uuid + "'," + Long.toString(prevMillis) + "," + sitationRange + "," + range + ","
										+ range2 + "," + range3 + "," + range + "," + range2 + ");";
								writer.print(line + "\n");
							}
						}
					}
				}
			}

			// Generate data for hrmDateTable for past 5 days including current
			for (int i = 0; i < hrmDateTable.length; i++) {
				String uuid = null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					uuid = rs.getString(1);
					if (dateCount >= 0) {
						for (int timestamp = 0; timestamp < dateCount; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int range = random.nextInt(max - min + 1) + min;
							int range2 = random.nextInt(max - min + 1) + min;
							int range3 = random.nextInt(max - min + 1) + min;
							int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
							String line = "INSERT INTO " + schemaName + "." + hrmDateTable[i]
									+ "(uuid, ts, situation, count, max, mean, min, sd) VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + sitationRange + "," + range + "," + range2 + ","
									+ range3 + "," + range + "," + range2 + ");";
							writer.print(line + "\n");
						}
					}else{
						for (int timestamp = dateCount; timestamp < 0; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int range = random.nextInt(max - min + 1) + min;
							int range2 = random.nextInt(max - min + 1) + min;
							int range3 = random.nextInt(max - min + 1) + min;
							int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
							String line = "INSERT INTO " + schemaName + "." + hrmDateTable[i]
									+ "(uuid, ts, situation, count, max, mean, min, sd) VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + sitationRange + "," + range + "," + range2 + ","
									+ range3 + "," + range + "," + range2 + ");";
							writer.print(line + "\n");
						}
					}
				}
			}

			// Generate data for sleepDateTable for past 5 days including
			// current
			for (int i = 0; i < sleepDateTable.length; i++) {
				String uuid = null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					uuid = rs.getString(1);
					if (dateCount >= 0) {
						for (int timestamp = 0; timestamp < dateCount; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int range = random.nextInt(max - min + 1) + min;
							int range2 = random.nextInt(max - min + 1) + min;
							int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
							String line = "INSERT INTO " + schemaName + "." + sleepDateTable[i]
									+ "(uuid, ts, status, duration, ratio) VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + sitationRange + "," + range + "," + range2 + ");";
							writer.print(line + "\n");
						}
					}else{
						for (int timestamp = dateCount; timestamp < 0; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int range = random.nextInt(max - min + 1) + min;
							int range2 = random.nextInt(max - min + 1) + min;
							int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
							String line = "INSERT INTO " + schemaName + "." + sleepDateTable[i]
									+ "(uuid, ts, status, duration, ratio) VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + sitationRange + "," + range + "," + range2 + ");";
							writer.print(line + "\n");
						}
					}
				}
			}

			// Generate data for stepDateTable for past 5 days including current
			for (int i = 0; i < stepDateTable.length; i++) {
				String uuid = null;
				rs = stmt.executeQuery(query);
				while (rs.next()) {
					uuid = rs.getString(1);
					if (dateCount >= 0) {
						for (int timestamp = 0; timestamp < dateCount; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int range = random.nextInt(max - min + 1) + min;
							int range2 = random.nextInt(max - min + 1) + min;
							int range3 = random.nextInt(max - min + 1) + min;
							int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
							String line = "INSERT INTO " + schemaName + "." + stepDateTable[i]
									+ "(uuid, ts, \"type\", cal, count, distance) VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + sitationRange + "," + range + "," + range2 + ","
									+ range3 + ");";
							writer.print(line + "\n");
						}
					}else{
						for (int timestamp = dateCount; timestamp < 0; timestamp++) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(prevDate);
							calendar.add(Calendar.DAY_OF_YEAR, -(timestamp));
							Date previousDate = calendar.getTime();
							long prevMillis = previousDate.getTime() / 1000;
							int range = random.nextInt(max - min + 1) + min;
							int range2 = random.nextInt(max - min + 1) + min;
							int range3 = random.nextInt(max - min + 1) + min;
							int sitationRange = random.nextInt(mamSituation - minSituation + 1) + minSituation;
							String line = "INSERT INTO " + schemaName + "." + stepDateTable[i]
									+ "(uuid, ts, \"type\", cal, count, distance) VALUES('" + uuid + "',"
									+ Long.toString(prevMillis) + "," + sitationRange + "," + range + "," + range2 + ","
									+ range3 + ");";
							writer.print(line + "\n");
						}
					}
				}
			}
			System.out.println("test-data file genenated in resource folder...");
			writer.close();
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException se) {
				System.out.println("Inside SQLException Catch 1" + se);
			}
			try {
				stmt.close();
			} catch (SQLException se) {
				System.out.println("Inside SQLException Catch 2" + se);
			}
			try {
				if(null != rs) {
					rs.close();
				}
			} catch (SQLException se) {
				System.out.println("Inside SQLException Catch 3" + se);
			}
		}

	}
}
