package com.liteon.icgwearable.util;

import java.util.Map;

import com.liteon.icgwearable.util.StringUtility;

/**
 * The class represents the standard JSON used by LITEON application to perform
 * JSON related operations.
 * 
 */
public class LITEONJSON {
	private Object data;
	private Object responseStatus;

	public LITEONJSON() {
	}

	public LITEONJSON(Object data, Object responseStatus) {
		this.data = data;
		this.responseStatus = responseStatus;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(Object responseStatus) {
		this.responseStatus = responseStatus;
	}

	public boolean hasError() {
		boolean containsError = true;
		if (responseStatus != null) {
			if (responseStatus instanceof Map) {
				Map<String, Object> status = (Map<String, Object>) responseStatus;
				String error = (String) status.get("error");
				if (StringUtility.isNull(error)) {
					containsError = false;
				}
			}
		}
		return containsError;
	}

	@Override
	public String toString() {
		return "\nData List: " + data + "\nResponse Status List: "
				+ responseStatus;
	}
} // End of LITEONJSON class