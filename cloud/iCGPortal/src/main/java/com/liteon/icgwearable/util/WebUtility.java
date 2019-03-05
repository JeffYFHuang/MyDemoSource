package com.liteon.icgwearable.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

/**
 * Utility class helps in performing web operations.
 * 
 */
public class WebUtility {
	private static final Logger logger = Logger.getLogger(WebUtility.class);
	
	/**
	 * The method responds back string JSON to the client.
	 * 
	 * @param response
	 *            An instance of the response.
	 * 
	 * @param dataToSend
	 *            The data to be send to the client.
	 * 
	 * @param contentType
	 *            The content type of the response
	 * 
	 */
	
	private static volatile WebUtility webUtilitySingleton= null;
	/*@Value("${db.dateTime}")
	private static String dbDateTime;
	@Value("${db.dateFormat}")
	private static String dbDateFormat;	*/
	private WebUtility(){
	
	}
	
	public static WebUtility getWebUtility() {
		if (null == webUtilitySingleton) {
			synchronized (WebUtility.class) {
				if (null == webUtilitySingleton) {
					webUtilitySingleton = new WebUtility();
				}
			}
		}
		return webUtilitySingleton;
	}

    public static void setCacheHeaders(final HttpServletResponse response, int cacheMaxHours)
    {
        if (response != null)
        {
            response.setContentType("application/json; charset=utf-8");
            logger.debug("\n******************* setCacheHeaders *******************: " + cacheMaxHours);
            int cacheMaxSeconds = 0;
            if (cacheMaxHours > 0)
            {
                cacheMaxSeconds = cacheMaxHours * 60 * 60;
                response.setHeader("Cache-Control", "max-age=" + cacheMaxSeconds);
            } else
            {
                setNoCacheHeaders(response);
            }
            return;
        }

    }

    public static void setNoCacheHeaders(final HttpServletResponse response)
    {
        if (response != null)
        {
            response.setContentType("application/json; charset=utf-8");
            response.setHeader("Cache-Control", "no-cache");
        }
        return;
    }
	
	public static void respondToClient(final HttpServletResponse response, final String dataToSend,
			final String contentType) {
		PrintWriter pw = null;
		try {
			if (response != null && !StringUtility.areNull(dataToSend, contentType)) {
				pw = response.getWriter();
				//response.setContentType(contentType + "; charset=utf-8");
				//response.setHeader("Cache-Control", "no-cache");
				logger.debug("\nSending data: " + dataToSend);
				pw.write(dataToSend);
			}
		} catch (Exception e) {
			logger.error("Exception occured while trying to respond the json to the client.", e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	} // End of respondToClient method

	public static void createFile(String json, String folderPath, String dbDateFormat) {
		FileOutputStream fos = null;
		try {
			Format formatter = new SimpleDateFormat(dbDateFormat);
			Path path = Paths.get(folderPath);
			Path absolutePathOfFolder = Files.createDirectories(path);
			logger.info("createFile: " + absolutePathOfFolder.toString());
			
			File folder = new File(absolutePathOfFolder.toString());
			File f = new File(folder, String.format("%s.txt", formatter.format(new Date())));
			fos = new FileOutputStream(f, true);
			fos.write(json.getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
		} catch (FileNotFoundException ex) {
			logger.error("FileNotFoundException : " + ex);
		} catch (IOException ioe) {
			logger.error("IOException : " + ioe);
		} finally {
			if(fos != null){
				try {
					fos.close();
				} catch (IOException ioe) {
					logger.error("IOException : " + ioe);
				}
			}
		}
		
	}
	
	public static void createDataSyncFile(String json, String folderPath, String uuid, String dbDateTime) {
		try {
			
			logger.info("dbDateTime"+"\t"+dbDateTime);
			Format formatter = new SimpleDateFormat(dbDateTime);
			Path path = Paths.get(folderPath);
			Path absolutePathOfFolder = Files.createDirectories(path);
			logger.info("absolutePathOfFolder"+"\t"+absolutePathOfFolder.toString());
			
			File folder = new File(absolutePathOfFolder.toString());
			File f = new File(folder, String.format("%s.txt", uuid+"_"+formatter.format(new Date())));
			FileOutputStream fos = new FileOutputStream(f, true);
			fos.write(json.getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
			fos.close();
		} catch (FileNotFoundException ex) {
			logger.error("FileNotFoundException : " + ex);
		} catch (IOException ioe) {
			logger.error("IOException : " + ioe);
		}
		
	}
	
	public static String createFolder(String folderPath) {
		String absFolderPath = null;		
		try {
			Path path = Paths.get(folderPath);
			Files.deleteIfExists(path);
			Path absolutePathOfFolder = Files.createDirectories(path);
			logger.info("createFolder: " + absolutePathOfFolder.toString());
			absFolderPath = absolutePathOfFolder.toString();
		} catch (IOException e) {
			logger.error("IOException : " + e);
		}
		return absFolderPath;
	}
	
	public String createNewFolder(String folderPath) {
		String absFolderPath = null;
		Path path = Paths.get(folderPath);
	    if (!Files.exists(path)) {
		 try {
			
			Path absolutePathOfFolder = Files.createDirectories(path);
			logger.info("createNewFolder: " + absolutePathOfFolder.toString());
			absFolderPath = absolutePathOfFolder.toString();
		   } catch (IOException e) {
			logger.error("IOException : " + e);
		  }
		}
		return absFolderPath;
	}
	
	
	public String makeNewFolder(String folderPath) {
		String absFolderPath = null;
		
			Path path = Paths.get(folderPath);			
			File files = new File(folderPath);
	        if (!files.exists()) {
	            if (files.mkdirs()) {
	            	logger.info("Multiple directories are created!");
	            } else {
	            	logger.info("Failed to create multiple directories!");
	            }
	        }   
	     
		return files.getAbsolutePath();
	}
	
	public int checkFileExists(String urlString) {
		logger.debug("urlString :::::::::::: "+urlString);
		int responseCode;
		try {
			final URL url = new URL(urlString);
			logger.info("URL >>>"+"\t"+url);
			HttpsURLConnection huc = (HttpsURLConnection) url.openConnection();
			logger.info("huc >>>"+"\t"+huc);
			responseCode = huc.getResponseCode();
			logger.info("responseCode >>>"+"\t"+responseCode);
		} catch (UnknownHostException uhe) {
			logger.error("UnknownHostException ::::::");
			logger.info("<<<UnknownHostException>>>"+"\t"+uhe.toString());
			responseCode = 404;
		} catch (FileNotFoundException fnfe) {
			logger.error("FileNotFoundException ::::::");
			logger.info("<<<FileNotFoundException>>>"+"\t"+fnfe.toString());
			responseCode = 404;
		} catch (Exception e) {
			logger.error("Some other Exception ::::::");
			logger.error("<<<Exception>>>>"+"\t"+e.toString());
			responseCode = 404;
		}

		return responseCode;
	}
	
	// new method to return welcome page when user logs out
	public ModelAndView getModelAndViewObject(){
			ModelAndView model = new ModelAndView();
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName("home");
			return model;
		
	}
	
	// new method to return login when admin user session expires
	public ModelAndView getAdminLoginPage(){
			ModelAndView model = new ModelAndView();
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName(Constant.ADMINLOGINVIEW);
			return model;
		
	}

	// new method to return login when parent user session expires
	public ModelAndView getParentLoginPage(){
			ModelAndView model = new ModelAndView();
			model.addObject(Constant.LoginError, Constant.SessionValidityResult);
			model.setViewName(Constant.PARENTLOGINVIEW);
			return model;
		
	}
	
	
	

} // End of WebUtility class
