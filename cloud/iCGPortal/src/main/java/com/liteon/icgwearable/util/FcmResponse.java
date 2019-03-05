package com.liteon.icgwearable.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;

public class FcmResponse {
	
	private static Logger log = Logger.getLogger(FcmResponse.class);
	HttpsURLConnection connection;

	public FcmResponse(HttpsURLConnection con) {
		connection = con;
	}

	public String toString() {
		return String.format("Response: %d%n Success Message: '%s'%nError Message: '%s'", getResponseCode(),
				getSuccessResponseMessage(), getResponseMessage());
	}

	public int getResponseCode() {
		try {
			return connection.getResponseCode();
		} catch (IOException e) {
			log.error("getResponseCode() ", e);
		}
		return -1;
	}

	public String getResponseMessage() {
		InputStream in = connection.getErrorStream();

		if (in == null)
			return "";
		BufferedReader r = new BufferedReader(new InputStreamReader(in));

		StringBuilder total = new StringBuilder();
		String line = null;
		try {
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			r.close();
		} catch (IOException e) {
			log.error("getResponseMessage() ", e);
		}
		return total.toString();
	}

	public String getSuccessResponseMessage() {
		InputStream in = null;
		try {
			in = connection.getInputStream();
		} catch (IOException e1) {
			log.error("getSuccessResponseMessage() ", e1);
		}

		if (in == null)
			return "";

		BufferedReader r = new BufferedReader(new InputStreamReader(in));

		StringBuilder total = new StringBuilder();

		String line = null;

		try {
			while ((line = r.readLine()) != null) {
				total.append(line);
			}

			r.close();
		} catch (IOException e) {
			log.error("getSuccessResponseMessage() ", e);
		}
		return total.toString();
	}
}

