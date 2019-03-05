package com.liteon.icgwearable.util;

import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import com.datastax.driver.core.Row;

/**
 * The class provides utility methods for String operations.
 * 
 * @author Sarath
 * 
 * @version 1.0.0
 * 
 */
public final class StringUtility {
	
	/**
	 * Instantiates a new string utility.
	 */
	private StringUtility() {
		
	}

	private static Logger log = Logger.getLogger(StringUtility.class);
	/**
	 * The method certifies that the stringToBeProcessed is Null or not, based
	 * on the following conditions:
	 * 
	 * <br />
	 * 1. It returns <code>false</code> on comparison with a <b>NULL</b> <br />
	 * 2. It returns <code>false</code> on comparison with a <b>WHITE SPACE</b> <br />
	 * 3. The length is &gt; (greater) than zero (0).
	 * 
	 * @param stringToBeProcessed
	 *            The string variable which is to be checked for its
	 *            nullability.
	 * 
	 * @return 1. <b><code>True</code></b>, when the string variable fails in
	 *         any of the above conditions. <br />
	 *         <b>Meaning - The string variables was found to be NULL</b> <br />
	 *         2. <b><code>False</code></b>, elsewhere. <br />
	 *         <b>Meaning - The string variable was found to be NOT NULL.</b>
	 * 
	 */
	public static boolean isNull(String stringToBeProcessed) {
		boolean isStringNull = true;
		if (stringToBeProcessed != null && !stringToBeProcessed.equals(" ")
				&& stringToBeProcessed.length() > 0) {
			isStringNull = false;
		}
		return isStringNull;
	} // End of isNull method

	/**
	 * The method certifies that the stringToBeProcessed(s) is Null or not,
	 * based on the following conditions:
	 * 
	 * <br />
	 * 1. It returns <code>false</code> on comparison with a <b>NULL</b> <br />
	 * 2. It returns <code>false</code> on comparison with a <b>WHITE SPACE</b> <br />
	 * 3. The length is &gt; (greater) than zero (0).
	 * 
	 * @param stringToBeProcessed
	 *            Either a single string variable or an array, to be checked for
	 *            their nullabilit(ies).
	 * 
	 * @return 1. <b><code>True</code></b>, when any one of the string variable
	 *         fails in any of the above conditions. <br />
	 *         <b>Meaning - One / some / all of the string variables was / were
	 *         found to be NULL</b> <br />
	 *         2. <b><code>False</code></b>, elsewhere. <br />
	 *         <b>Meaning - All the string variables are NOT NULL.</b>
	 * 
	 */
	public static boolean areNull(String... stringToBeProcessed) {
		boolean areStringsNull = false;
		for (String tempString : stringToBeProcessed) {
			if (!isNull(tempString)) {
				continue;
			} else {
				areStringsNull = true;
				break;
			}
		}
		return areStringsNull;
	} // End of areNull method
	
	
	/**
	 * The method method will replace newline characters with space in specified
	 * string.
	 * 
	 * @param stringToBeProcessed
	 *            String from which new line characters will be replaced by
	 *            space.
	 * @return the string new string without new line characters
	 */
	public static String removeNewLineCharacters(String stringToBeProcessed) {
		String newText = null;
		if(stringToBeProcessed != null) {
			newText = stringToBeProcessed.replaceAll("\\r|\\n", "").trim();
		}
		return newText;
	}
	
	public static String randomStringOfLength(int length) {
	    StringBuffer buffer = new StringBuffer();
	    while (buffer.length() < length) {
	        buffer.append(uuidString());
	    }

	    //this part controls the length of the returned string
	    return buffer.substring(0, length);  
	}


	private static String uuidString() {
	    return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String formatQueryWithList(String query , java.util.List<Integer> list)
	{
		
		int count=1;
		for (int i :list)
		{
			
			if(count<list.size()) {
				query= query+ i+",";
			count++;
			}
			else
				query= query+ i+")";
		}
		return query;
	}
	
	public static String formatQueryWithStringList(String query , java.util.List<String> list)
	{
		
		int count=1;
		for (String  i :list)
		{
			
			if(count<list.size()) {
				query= query+"'"+ i+"',";
			count++;
			}
			else
				query= query+"'"+ i+"')";
		}
		return query;
	}
	
	public static boolean validateLatLong(String str) {
		boolean validLatAndLong = false;
		Pattern pattern;
		Matcher matcher;
		final String LATLONG_PATTERN = "^\\s*([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+))\\s*,\\s*([+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+))\\s*$";
		pattern = Pattern.compile(LATLONG_PATTERN);
		matcher = pattern.matcher(str);
		if (matcher.matches()) {
			validLatAndLong = true;
		}
		return validLatAndLong;
	}

	public static String dummyWearableJsonData() {
		
	String json = "{\r\n" + 
			"	\"sid\": \"1\",\r\n" + 
			"	\"uuid\": \"458b29ae-a02d-4a454f-9b9e95-ce281d3096a5\",\r\n" + 
			"	\"data\": [{\r\n" + 
			"			\"context\": 1,\r\n" + 
			"			\"timestamp\": 1493626269,\r\n" + 
			"			\"duration\": 651,\r\n" + 
			"			\"hrm\": [{\r\n" + 
			"				\"timestamp\": 1493626299,\r\n" + 
			"				\"hrm_report\": 84,\r\n" + 
			"				\"hr_peak_rate\": 84\r\n" + 
			"			}]\r\n" + 
			"		},\r\n" + 
			"		{\r\n" + 
			"			\"context\": 2,\r\n" + 
			"			\"timestamp\": 1493629024,\r\n" + 
			"			\"duration\": 466,\r\n" + 
			"			\"count\": 733,\r\n" + 
			"			\"distance\": 513,\r\n" + 
			"			\"cal\": 39808,\r\n" + 
			"			\"hrm\": [{\r\n" + 
			"				\"timestamp\": 1493626299,\r\n" + 
			"				\"hrm_report\": 84,\r\n" + 
			"				\"hr_peak_rate\": 84\r\n" + 
			"\r\n" + 
			"			}]\r\n" + 
			"		},\r\n" + 
			"		{\r\n" + 
			"			\"context\": 3,\r\n" + 
			"			\"timestamp\": 1493629024,\r\n" + 
			"			\"duration\": 466,\r\n" + 
			"			\"count\": 733,\r\n" + 
			"			\"distance\": 513,\r\n" + 
			"			\"cal\": 39808,\r\n" + 
			"			\"hrm\": [{\r\n" + 
			"				\"timestamp\": 1493626299,\r\n" + 
			"				\"hrm_report\": 84,\r\n" + 
			"				\"hr_peak_rate\": 84\r\n" + 
			"			}]\r\n" + 
			"		},\r\n" + 
			"		{\r\n" + 
			"			\"context\": 4,\r\n" + 
			"			\"timestamp\": 1493656275,\r\n" + 
			"			\"duration\": 67,\r\n" + 
			"			\"hrm\": [{\r\n" + 
			"				\"timestamp\": 1493626299,\r\n" + 
			"				\"hrm_report\": 84,\r\n" + 
			"				\"hr_peak_rate\": 84\r\n" + 
			"			}]\r\n" + 
			"		},\r\n" + 
			"		{\r\n" + 
			"			\"context\": 5,\r\n" + 
			"			\"timestamp\": 1493672731,\r\n" + 
			"			\"duration\": 312,\r\n" + 
			"			\"status\": 3,\r\n" + 
			"			\"hrm\": [{\r\n" + 
			"				\"timestamp\": 1493626299,\r\n" + 
			"				\"hrm_report\": 84,\r\n" + 
			"				\"hr_peak_rate\": 84\r\n" + 
			"			}]\r\n" + 
			"		},\r\n" + 
			"		{\r\n" + 
			"			\"context\": 0,\r\n" + 
			"			\"timestamp\": 1493628055,\r\n" + 
			"			\"duration\": 213,\r\n" + 
			"			\"hrm\": [{\r\n" + 
			"				\"timestamp\": 1493626299,\r\n" + 
			"				\"hrm_report\": 84,\r\n" + 
			"				\"hr_peak_rate\": 84\r\n" + 
			"			}]\r\n" + 
			"		}\r\n" + 
			"	]\r\n" + 
			"}"	;
	
	return json;
	}
	
	public static StringJoiner getRowListAsString(List<Row> rowsList) {
		StringJoiner jsonString = new StringJoiner(",", "[", "]");

		for (Row r : rowsList) {
			String rJson = r.getString(0);
			log.info("rJson"+ "\n" +rJson);
			jsonString.add(rJson);
			log.info("jsonString"+ "\n" +jsonString);
		}
		
		return jsonString;
	}
	
	public static String convertRowListToString(List<Row> rowsList) {
		if(null != rowsList){
			StringJoiner jsonString = new StringJoiner(",");

			for (Row r : rowsList) {
				String rJson = r.getString(0);
				log.info("rJson"+ "\n" +rJson);
				jsonString.add(rJson);
				log.info("jsonString"+ "\n" +jsonString);
			}
			
			return jsonString.toString();
		}
		return "";
	}
	
	public static String[] getTrustedIPs(String trustedIPs, String splitBy) {
		String[] trustedIP = trustedIPs.split(splitBy);
		return trustedIP;
	}
	
	public static String getActivityOutput(String type, List typeList) 
	{
		String output = "";
		output = "\""+type+"\":"+typeList.toString();
		return output;
	}
} // End of StringUtility class