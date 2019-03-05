package com.liteon.icgwearable.federallogin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

public class FBConnection {

	@Value("${FACEBOOK.APP.ID}")
	private String fbAppId;
	@Value("${FACEBOOK.APP.SECRET}")
	private String fbAppSecret;
	@Value("${FACEBOOK.REDIRECT.URI}")
	private String fbRedirectUri;

	static String accessToken = "";

	public String getFBAuthUrl() {
		String fbLoginUrl = "";
		try {
			fbLoginUrl = "http://www.facebook.com/dialog/oauth?" + "client_id=" + fbAppId + "&redirect_uri="
					+ URLEncoder.encode(fbRedirectUri, "UTF-8") + "&scope=email";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return fbLoginUrl;
	}

	public String getFBGraphUrl(String code) {
		String fbGraphUrl = "";
		try {
			fbGraphUrl = "https://graph.facebook.com/oauth/access_token?" + "client_id=" + fbAppId + "&redirect_uri="
					+ URLEncoder.encode(fbRedirectUri, "UTF-8") + "&client_secret=" + fbAppSecret + "&code=" + code;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return fbGraphUrl;
	}

	public String getAccessToken(String code) {
		String token = null;
		if ("".equals(accessToken)) {
			URL fbGraphURL;
			try {
				fbGraphURL = new URL(getFBGraphUrl(code));
			} catch (MalformedURLException e) {
				e.printStackTrace();
				throw new RuntimeException("Invalid code received " + e);
			}

			HttpURLConnection fbConnection;
			StringBuffer b = null;

			try {
				System.out.println("1");
				fbConnection = (HttpURLConnection) fbGraphURL.openConnection();
				System.out.println("fbConnection.getURL()" + "\t" + fbConnection.getURL());
				System.out.println("fbConnection" + "\t" + fbConnection);
				System.out.println("2");
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(fbConnection.getInputStream()));
				System.out.println("3");
				String inputLine;
				b = new StringBuffer();
				while ((inputLine = in.readLine()) != null)
					b.append(inputLine + "\n");
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Unable to connect with Facebook " + e);
			}
			accessToken = b.toString();

			JSONObject jobj = new JSONObject(accessToken);
			token = jobj.getString("access_token");

			System.out.println("accessToken in FB Connecrtion" + "\t" + accessToken);
			System.out.println("token" + "\t" + token);
			if (token.startsWith("{")) {
				throw new RuntimeException("ERROR: Access Token Invalid: " + accessToken);
			}
		}
		return token;
	}
}
