package com.liteon.icgwearable.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {
	
	public static void main(String args[])
{
		  //Create new students objects
	
	String dateStart = "11-03-14 09:29:58";
    String dateEnd = "11-03-14 09:33:43";

   // Custom date format
    SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    Date d1 = null;
    Date d2 = null;
    
		System.out.println();
	
    try {
        d1 = format.parse(dateStart);
        d2 = format.parse(dateEnd);
    } catch (ParseException e) {
        e.printStackTrace();
    }

    // Get msec from each, and subtract.
    long diff = d2.getTime() - d1.getTime();
    long diffSeconds = diff / 1000 % 60;
    long diffMinutes = diff / (60 * 1000) % 60;
    long diffHours = diff / (60 * 60 * 1000);
    System.out.println("Time in seconds: " + diffSeconds + " seconds.");
    System.out.println("Time in minutes: " + diffMinutes + " minutes.");
    System.out.println("Time in hours: " + diffHours + " hours.");
		
		/*
	   String s = "SELECT dve.deviceeventid as deid from DeviceEvents as dve where dve.deviceeventid in (";
	   
	   List<String> list = new ArrayList<String>();
	   
	   list.add("hai");
	   list.add("hello");
	   list.add("hi");
	   s = formatQueryWithList(s, list);
	  System.out.println( s );*/
	   
	   
}
	
	/*public static String formatQueryWithList(String query , java.util.List<String> list)
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
	}*/

}
