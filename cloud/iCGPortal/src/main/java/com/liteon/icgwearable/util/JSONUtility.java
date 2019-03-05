package com.liteon.icgwearable.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.google.gson.Gson;
import com.liteon.icgwearable.transform.DeviceConfigurationsTransform;
import com.liteon.icgwearable.util.LITEONJSON;
import com.liteon.icgwearable.enums.ProcessId;
import com.liteon.icgwearable.util.StringUtility;
import com.liteon.icgwearable.util.WebUtility;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;

/**
 * The class provides utility methods to perform manipulations on any JSON.
 * 
 * @since 1.0.0
 */
public class JSONUtility {
	private static final Logger logger = Logger.getLogger(JSONUtility.class);
	/*@Value("${display.dateTime}")
	private String sourceDateFormat;*/
	public static LITEONJSON getLITEONJSON(String json) {
		LITEONJSON liteonJSON = null;
		logger.debug("\n LITEONJSON method, Incoming JSON: " + json);
		if (!StringUtility.isNull(json)) {
			try {
				Gson gson = new Gson();
				liteonJSON = gson.fromJson(json, LITEONJSON.class);
			} catch (Exception e) {
				logger.error("\nException occured while trying to convert string based json to LITEONJSON object.", e);
			}
		}
		logger.debug("\nReturning LITEONJSON: " + liteonJSON);
		return liteonJSON;
	} // End of getLITEONJSON method

	/**
	 * The method fetches the value of the key from the JSON.
	 * 
	 * @param json
	 *            A String based JSON object
	 * 
	 * @param key
	 *            The key to be added/modified
	 * 
	 * @param jsonSegmentIdentifier
	 *            Identifier to identify the segment to be modified. <br />
	 *            <b>Supported Values:<br />
	 *            <code>1. {@link ProcessId}.JSON_DATA_SEGMENT<br />
	 *            2. {@link ProcessId}.JSON_RESPONSE_SEGMENT<br/>
	 *            3. {@link ProcessId}.JSON_OPID_SEGMENT<br/></code></b>
	 * 
	 * @return String value of the key.
	 * 
	 * @see ProcessId
	 * 
	 */
	@SuppressWarnings("all")
	public static String getJSONPropertyValue(String json, String key, ProcessId jsonSegmentIdentifier) {
		logger.debug("\nJSON Add Property request. Incoming JSON: " + json + "\nKey: " + key);
		String value = null;
		LITEONJSON liteonJSON = null;
		if (!StringUtility.areNull(json, key)) {
			try {
				liteonJSON = getLITEONJSON(json);
				switch (jsonSegmentIdentifier) {
				case JSON_DATA_SEGMENT:
					Object dataSegment = liteonJSON.getData();
					if (dataSegment instanceof Map) {
						Map<String, Object> data = (Map) dataSegment;
						if (data.containsKey(key)) {
							value = (String) data.get(key);
						}
					}
					break;
				case JSON_RESPONSE_SEGMENT:
					Object responseSegment = liteonJSON.getResponseStatus();
					if (responseSegment instanceof Map) {
						Map<String, Object> response = (Map) responseSegment;
						if (response.containsKey(key)) {
							value = (String) response.get(key);
						}
					}
					break;
				}
			} catch (Exception e) {
				logger.error("\nException occured while trying to add a property to an existing JSON.", e);
			}
		}
		return value;
	} // End of getJSONPropertyValue method

	/**
	 * The method process incoming java based json object and converts it to
	 * string based json.
	 * 
	 * @param json
	 *            Incoming JSON
	 * 
	 * @return String equivalent of the incoming JSON.
	 * 
	 */
	public static String prepareJSONFromMap(Map<String, Object> json) {
		String liteonJSON = null;
		logger.debug("\nprepareJSONFromMap method, Incoming JSON: " + json);
		if (json != null) {
			try {
				Gson gson = new Gson();
				liteonJSON = gson.toJson(json);
			} catch (Exception e) {
				logger.error("\nException occured while trying to convert java based json to string json object.", e);
			}
		}
		logger.debug("\nReturning String JSON: " + liteonJSON);
		return liteonJSON;
	} // End of prepareJSONFromMap method

	/**
	 * The method responds back string JSON to the client.
	 * 
	 * @param response
	 *            An instance of the response.
	 * 
	 * @param json
	 *            JSON to send.
	 * 
	 */
	public static void respondAsJSON(HttpServletResponse response, String json) {
		try {
			if (response != null && !StringUtility.isNull(json)) {
				WebUtility.respondToClient(response, json, "text/json");
				logger.debug("JSON response sent successfully");
			}
		} catch (Exception e) {
			logger.error("Exception occured while trying to respond the json to the client.", e);
		}
	} // End of respondAsJSON method

    public static void respondAsJSON(HttpServletResponse response, String json, int cacheMaxHours)
    {
        try
        {
            if (response != null && !StringUtility.isNull(json))
            {
                WebUtility.setCacheHeaders(response, cacheMaxHours);
                WebUtility.respondToClient(response, json, "text/json");
                logger.debug("JSON response sent successfully With Cache-Control **************");
            }
        } catch (Exception e)
        {
            logger.error("Exception occured while trying to respond the json to the client.", e);
        }
    }

	/**
	 * The method responds back string JSON to the client.
	 * 
	 * @param response
	 *            An instance of the response.
	 * 
	 * @param json
	 *            JSON to send.
	 * 
	 */
	public static boolean createJSONContentFile(String json, String folderPath, String uuid, String dbDateTime) {
		boolean response = false;
		try {
			if (!StringUtility.isNull(json)) {
				//WebUtility.createFile(json, folderPath, dbDateTime);
				WebUtility.createDataSyncFile(json, folderPath, uuid, dbDateTime);
				logger.debug("JSON file created successfully");
				response = true;
			}
		} catch (Exception e) {
			logger.error("Exception occured while trying to create the json file.", e);
		}

		return response;
	} // End of createJSONContentFile method

	public static boolean isValidJson(String jsonStr) {
		boolean isValid = false;
		try {
			JSONSerializer.toJSON(jsonStr);
			isValid = true;
		} catch (JSONException je) {
			isValid = false;
		}
		return isValid;
	}

	public static String convertMapToJson(Map<String, String> map) {
		String mapToJson = JSONObject.toJSONString(map);
		return mapToJson;
	}

	public static JSONObject createJsonRequest(String key, String value) {
		JSONObject json = new JSONObject();
		json.put(key, value);
		return json;
	}
	public static String dataSyncSuccessJsonString(List<DeviceConfigurationsTransform> list){
		
		JSONArray jsonArray = new JSONArray();
		for(DeviceConfigurationsTransform deviceConfigTransform : list){
			String configName = deviceConfigTransform.getConfigName();
			String configValue = deviceConfigTransform.getConfigValue();
			JSONObject json= createJsonRequest(configName, configValue);
			jsonArray.add(json);
		}
		String jsonArrayString = jsonArray.toString();
		logger.info("Array:"+"\n"+jsonArray);
		logger.info("Array String:"+"\n"+jsonArrayString);
		
		ObjectMapper mapper = new ObjectMapper();
/*		String indented = null;
		try {
			indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonArrayString);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return jsonArrayString;
	}

	public static String convertObjectToJson(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json= mapper.writeValueAsString(o);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info(e);
			e.printStackTrace();
		
		}
		return json;
	}
	
	public boolean checkJsonInput(String json, String sourceDateFormat){
		boolean isJsonCorrect = false; 
		
		try {
			
			Object obj= JSONValue.parse(json);  
		    JSONObject jsonObject = (JSONObject) obj;
			SimpleDateFormat format1 = new SimpleDateFormat(sourceDateFormat);
			format1.setLenient(false);
			
			String eventoccureddate = null;
			Date date = null;
			Long eventid = null;
			logger.debug("JsonUtiltity jsonObject ::"+jsonObject);
			
			if (jsonObject != null && jsonObject.get("eventoccureddate") != null && jsonObject.get("eventid") != null) {
				eventoccureddate = (String) jsonObject.get("eventoccureddate");
				date = format1.parse(eventoccureddate);
				eventid = (Long) jsonObject.get("eventid");
				isJsonCorrect = true;
				logger.debug("eventoccureddate :::: "+eventoccureddate);
				logger.debug("eventid :::: "+eventid);
			}
			else {
				logger.debug("Else ::: JsonUtiltity Input json is invalid :::");
			}
			
			return isJsonCorrect;
			
		}  catch (java.text.ParseException e) {
			System.out.println("parse Exception 222 ");
			logger.debug("JsonUtiltity ParseException occurred ::");
		} catch(NumberFormatException nfe){
			logger.debug("JsonUtiltity NumberFormatException occurrerd :::");
		}
		return isJsonCorrect;
	}
	
	public boolean checkJsonInputForActivityLog(String json){
			boolean isJsonCorrect = false; 
		
			Object obj= JSONValue.parse(json);  
		    JSONObject jsonObject = (JSONObject) obj;
			logger.debug("JsonUtiltity jsonObject ::"+jsonObject);
			
			if (jsonObject != null && jsonObject.get("appToken") != null && jsonObject.get("appType") != null) {
				isJsonCorrect = true;
				logger.debug("isJsonCorrect");
			}
			else {
				logger.debug("Else ::: JsonUtiltity Input json is invalid :::");
			}
			
			return isJsonCorrect;
			
	}
} // End of JSONUtility class
