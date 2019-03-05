package com.liteon.icgwearable.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

public class DateUtil {

	private static Logger log = Logger.getLogger(DateUtil.class);
	public static Date date = new Date();
	public static Date dateInstance = null;
	public static String convertUnixTimeToDate(long unixTime, String sourceDateFormat) {	
		final DateTimeFormatter formatter =DateTimeFormatter.ofPattern(sourceDateFormat);
		final String formattedDate = Instant.ofEpochSecond(unixTime)
			        .atZone(ZoneId.of("GMT+8"))
			        .format(formatter);
		return formattedDate;
	}
	
	public static long convertDateToUnixTime(String date, String timeZone, String dateTimeZome) throws ParseException {
		log.info("Into convertDateToUnixTime {");
	    DateFormat dateFormat = new SimpleDateFormat(dateTimeZome);
	    Date parseDate = dateFormat.parse(date);
	    log.info("parseDate in convertDateToUnixTime"+"\t"+parseDate);
	    log.info("**unixTime**"+"\t"+(long)parseDate.getTime()/1000);
	    log.info("Exiting convertDateToUnixTime }");
	    return (long)parseDate.getTime()/1000;
	}
	
	public static long getPreviousDay() {
		LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).now().minusDays(1);
		Date outDate = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		return (long)outDate.getTime()/1000;
	}
	
	public static String getYesterdaysDate(String sourceDateFormat) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).now().minusDays(1).toLocalDate().format(DateTimeFormatter.ofPattern(sourceDateFormat));
	}
	
	public static boolean isStartDateAfterEndDate(String startDate, String endDate, String sourceDateFormat) throws ParseException {
		boolean isStartDateAfterEndDate = false;
		SimpleDateFormat sdf = new SimpleDateFormat(sourceDateFormat);
        Date stDt = sdf.parse(startDate);
        Date endDt = sdf.parse(endDate);
        
        if(stDt.compareTo(endDt) > 0) {
        	isStartDateAfterEndDate = true;
        }
        log.info("isStartDateAfterEndDate"+"\t"+isStartDateAfterEndDate);
        return isStartDateAfterEndDate;
	}
	
	public static Date convertStringToDate(String date, String dbDateFormat) {
		  SimpleDateFormat formatter = new SimpleDateFormat(dbDateFormat);
		  Date convertedDate = null;
	        try {

	        	convertedDate = formatter.parse(date);
	            log.info("convertedDate"+"\t"+convertedDate);
	        } catch (ParseException e) {
	           log.error("convertStringToDate() ", e);
	        }
	        return convertedDate;
	}
	
	public static String convertDateToString(Date date, String sourceDateFormat) {
		SimpleDateFormat formatter = new SimpleDateFormat(sourceDateFormat);
		String convertedDate = null;
		convertedDate = formatter.format(date);
		return convertedDate;
	}
	
	public static Date getDateFromString(String date, String dateFormat)   {
		
        try
        {
            if (!StringUtility.areNull(date, dateFormat))
            {
                dateInstance = new SimpleDateFormat(dateFormat).parse(date);
            }
        } catch (Exception e)
        {
            log.error("\nException occured while trying to get date object from a string based date", e);
        }
        return dateInstance;
    }


    public Date changeDateFormat(String sourceDate, String sourceDateFormat, String destDateFormat)
    {
    	log.info("Into changeDateFormat {");
        String finalDate = null;
        Date outputDate = null;       
        if (!StringUtility.areNull(sourceDateFormat, destDateFormat))
        {
            Date srcDate = getDateFromString(sourceDate, sourceDateFormat);
            finalDate = new SimpleDateFormat(destDateFormat).format(srcDate);
            outputDate = getDateFromString(finalDate, destDateFormat);
        }
        log.info("Exiting changeDateFormat }");
        return outputDate;
    }
    
    public static String getTodaysDate(){
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
		
    }
    
    public static String getYesterdaysDate(){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(yesterday());
    }
    
    public static long getDateDiffinMinutes(String dateStart,String dateStop)
    {
    	
        
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = dateFormat.parse(dateStart);
            d2 = dateFormat.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
       long diff = d1.getTime() - d2.getTime();
       // long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
       // long diffHours = diff / (60 * 60 * 1000);
        //System.out.println("Time in seconds: " + diffSeconds + " seconds.");
        //System.out.println("Time in minutes: " + diffMinutes + " minutes.");
        //System.out.println("Time in hours: " + diffHours + " hours.");
    	return diffMinutes ;
    }
    private static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }
    
    

}
