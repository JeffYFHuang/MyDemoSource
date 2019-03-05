package com.liteon.icgwearable.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.liteon.icgwearable.model.IPSReceiverCSVModel;

public class IPSReceiverCSVToJavaUtil {
	private static Logger log = Logger.getLogger(IPSReceiverCSVToJavaUtil.class);
	static public void main(String []args) {
		String csvFileToRead = "D:\\ips_images\\resources\\images\\ips\\ipsfile.csv";
		File f = new File(csvFileToRead);
		//List<IPSReceiverCSVModel> ipsList = convertSubmitIPSReceiverCsvToJava(f);
		List<IPSReceiverCSVModel> ipsList = convertDeleteIPSReceiverCsvToJava(f);
		//printIPSList(ipsList);
		
}
	public static List<IPSReceiverCSVModel> convertSubmitIPSReceiverCsvToJava(File f) {
		//String csvfile = "D:\\ips_images\\resources\\images\\ips\\test.csv";
		// f = new File(csvfile);
		log.info("Into convertCsvToJava");
		  BufferedReader br = null;
		  String line = "";
		  String splitBy = ",";
		  List<IPSReceiverCSVModel> ipsList = new ArrayList<>();

		  try {
		   br = new BufferedReader(new FileReader(f));
		   int i=0;
		   while ((line = br.readLine()) != null) {
			  // log.info("Line >>>"+"\t"+line);			
			   String[] arr = new String[15];
			   if(line.length()!=0)
			    arr = line.split(splitBy);				 
			   log.info("arr::::"+arr[0]+"  "+arr[1]);
			   		 

		    // create car object to store values
		    IPSReceiverCSVModel ipsReceiverObj = new IPSReceiverCSVModel();
		    
		    if(i == 0){
		    	if(arr.length != 15 || !arr[0].trim().equals(IPSConstants.RECORD_TYPE) || !arr[1].trim().equals(IPSConstants.RECEIVER_MAC) || !arr[2].trim().equals(IPSConstants.RECEIVER_NAME)  
		    			||	!arr[3].trim().equals(IPSConstants.RECEIVER_VERSION) || !arr[4].trim().equals(IPSConstants.ZONE_NAME) || !arr[5].trim().equals(IPSConstants.MAP_TYPE) 
		    			|| !arr[6].trim().equals(IPSConstants.BUILDING_NAME) || !arr[7].trim().equals(IPSConstants.FLOOR_NUM) || !arr[8].trim().equals(IPSConstants.DELETE_MAP)
		    			|| !arr[9].trim().equals(IPSConstants.DEVICE_UUID) || !arr[10].trim().equals(IPSConstants.FIRMWARE_NAME) || !arr[11].trim().equals(IPSConstants.FIRMWARE_VERSION)
		    			|| !arr[12].trim().equals(IPSConstants.DEVICE_MODEL) || !arr[13].trim().equals(IPSConstants.DEVICE_STATUS) || !arr[14].trim().equals(IPSConstants.DEVICE_STATUS_DESC)
		    			){
		    		ipsList = null;
		    		
		    		log.info(" checking header names in first line..."+ipsList);
		    		return  ipsList;
		    }
		    	i++;
		    }
		    
		    log.info("array length=="+arr.length);
		    printArray(arr);
		    
		    int lastelement_index = arr.length-1;
		    log.info("lastelement_index=="+lastelement_index);
		    
		      if(ipsList != null){
		      
		    	  if(arr[0]==null )
		    		  continue;
		    	  
		    	// add values from csv to car object 
		    	  ipsReceiverObj.setRecordType(arr[0]);
		    	  
		    	  if(arr[0].equalsIgnoreCase("receiver")){
		    		  log.info("arr[0]"+arr[0]);
		    		  log.info("arr[1]"+arr[1]);
		    		  log.info("arr[2]"+arr[2]);
		    		  log.info("arr[3]"+arr[3]);
		    		 /* if(lastelement_index != 1 || arr[1]==null){
		    			  ipsList.add(ipsReceiverObj);
		    			  continue;
		    		  }*/
		    		 
		    			  
		    		  if(arr[1]!=null && !(arr[1].trim().equals("")))
		    		  ipsReceiverObj.setReceiverMac(arr[1]);
		    		
		    		  if(lastelement_index >= 2 &&arr[2]!=null && !(arr[2].trim().equals("")))
		    			  		ipsReceiverObj.setReceiverName(arr[2]);
		    		  
		    		  if(lastelement_index >= 3 && arr[3]!=null && !(arr[3].trim().equals("")))			    	 
	    			  		ipsReceiverObj.setReceiverVersion(arr[3]);
		    		  
		    		  
		    	  } else if (arr[0].equalsIgnoreCase("zone")){	
		    		 
		    		  if(lastelement_index < 5 || arr[1] == null || arr[4]==null || arr[5]==null){
		    			  ipsList.add(ipsReceiverObj);
		    			  continue; //skip particular record
		    		  }
		    		  
		    		  if(arr[1]!=null && !(arr[1].trim().equals("")))
		    		  ipsReceiverObj.setReceiverMac(arr[1]);

		    		 // if(arr.length == 15){
		    			  if(lastelement_index >= 8 &&  arr[8]!=null && !(arr[8].trim().equals("")))
			    			  ipsReceiverObj.setDeleteMapFile(arr[8]);
			    		  else
			    			  ipsReceiverObj.setDeleteMapFile("no");
		    			  
		    			  if(lastelement_index >= 7 && arr[7]!=null && !(arr[7].trim().equals(""))) 
		    			  		ipsReceiverObj.setFloorNum(arr[7]);
		    			  
		    			  if(lastelement_index >= 6 && arr[6]!=null && !(arr[6].trim().equals("")))
		    			  		ipsReceiverObj.setBuildingName(arr[6]);
		    			  
		    			  if(lastelement_index >= 5 && arr[5]!=null && !(arr[5].trim().equals("")))
		    		  		    ipsReceiverObj.setMapType(arr[5]);
		    			  
		    			  if(lastelement_index >= 4 && arr[4]!=null && !(arr[4].trim().equals(""))) 
		    			  		ipsReceiverObj.setZoneName(arr[4]);
		    			  
		    				    		 
		    	  } else if (arr[0].equalsIgnoreCase("device")){	
		    		  if(lastelement_index < 9 || arr[1] == null || arr[9]==null){
		    			  ipsList.add(ipsReceiverObj);
		    			  continue;
		    		  }
		    		  
		    		  if(arr[1]!=null && !(arr[1].trim().equals("")))
		    			  ipsReceiverObj.setReceiverMac(arr[1]);
		    		  
		    		  if(arr[4]!=null && !(arr[4].trim().equals("")))
		    			  ipsReceiverObj.setZoneName(arr[4]);
		    		  
		    		  if(arr[9]!=null && !(arr[9].trim().equals("")))
		    			  ipsReceiverObj.setDeviceUUID(arr[9]);
		    		  
		    		  if(lastelement_index >= 10 && arr[10]!=null && !(arr[10].trim().equals("")))
		    			  ipsReceiverObj.setFirmwareName(arr[10]);		    		  
		    		  if(lastelement_index >= 11 && arr[11]!=null && !(arr[11].trim().equals("")))
		    		    ipsReceiverObj.setFirmwareVersion(arr[11]);		    		  
		    		  if(lastelement_index >= 12 && arr[12]!=null && !(arr[12].trim().equals("")))
		    		    ipsReceiverObj.setDeviceModel(arr[12]);		    		  
		    		  if(lastelement_index >= 13 && arr[13]!=null && !(arr[13].trim().equals("")))
		    		    ipsReceiverObj.setStatus(arr[13]);
		    		  if(lastelement_index >= 4 && arr[14]!=null && !(arr[14].trim().equals("")))
		    		  ipsReceiverObj.setStatusDesc(arr[14]); 
		    			    		  
		    	  }
		    	 
		    // adding ipsReceiverObj objects to a list
		    	  ipsList.add(ipsReceiverObj);
		     }
		    } //end of while
		   log.info("total records in ipsList, size is:::"+ ipsList.size());
		   
		   printIPSList(ipsList);
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
		  return ipsList;
		 }
	    
	    public static  List<IPSReceiverCSVModel> convertDeleteIPSReceiverCsvToJava(File f){
	    	//String csvfile = "D:\\ips_images\\resources\\images\\ips\\delete1.csv";
			// f = new File(csvfile);
			log.info("Into convertCsvToJava");
			  BufferedReader br = null;
			  String line = "";
			  String splitBy = ",";
			  List<IPSReceiverCSVModel> ipsList = new ArrayList<>();

			  try {
			   br = new BufferedReader(new FileReader(f));
			   int i=0;
			   while ((line = br.readLine()) != null) {
				  // log.info("Line >>>"+"\t"+line);
				   System.out.println("Line >>>"+"\t"+line +"   --"+line.length());
				   String[] arr = new String[2];
				   if(line.length()!=0)
				    arr = line.split(splitBy);				 
				   System.out.println("arr::::"+arr[0]+"  "+arr[1]);

			    // create car object to store values
			    IPSReceiverCSVModel ipsReceiverObj = new IPSReceiverCSVModel();
			    
			    if(i == 0){
			    	if(arr.length < 2 || !arr[0].trim().equals(IPSConstants.RECORD_TYPE) || !arr[1].trim().equals(IPSConstants.RECORD_ID)){
			    		ipsList = null;
			    	//	System.out.println(" setting null for first line...");
			    		log.info(" setting header names in first line..."+ipsList);
			    		return  ipsList;
			    } 
			    	i++;
			    }
			      if(ipsList != null){
			      // add values from csv to car object
			    	  if(arr[0]==null &&  arr[1]==null )
			    		  continue;
			    	  
			    	  ipsReceiverObj.setRecordType(arr[0]);	
			    	  
			    	  if(arr[1]!=null && !(arr[1].trim().equals("")))
			    		  ipsReceiverObj.setRecordId(arr[1]);			    	  
			    	 
			    // adding ipsReceiverObj objects to a list
			    	  ipsList.add(ipsReceiverObj);
			     }
			    } //end of while
			   log.info("total records in ipsList, size is:::"+ ipsList.size());
			   log.info("IPSReceiverCSVModel records:: ");
			   printIPSList(ipsList);
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
			  return ipsList;
			 
	    }
	    
	    public static void printArray(String[] arr) {
	    	for (int i=0; i<arr.length; i++) {
	 		   //System.out.println("IPSReceiverCSVModel record "+i+"::" + ips);		   
	 		     log.info("array element "+i+"::" + arr[i]);
	 		  
	 		  }
	    	
	    }
	    
		 public static void printIPSList(List<IPSReceiverCSVModel> ipsList) {
			 int i =0;
		  for (IPSReceiverCSVModel ips : ipsList) {
		   //System.out.println("IPSReceiverCSVModel record "+i+"::" + ips);		   
		     log.info("IPSReceiverCSVModel record "+i+"::" + ips);
		   i++;
		  }
		 /* List<Integer> devicelist = new ArrayList();
		  List<Integer> receiverlist = new ArrayList();
		  
		    ipsList.stream().filter(irm -> irm.getRecordType()!=null && irm.getRecordType().equalsIgnoreCase("device"))
		    			.forEach(irm ->{
		    				if(irm.getRecordId()!=null)
		    				devicelist.add(Integer.valueOf(irm.getRecordId().trim()));
		    			});
		    
		   // System.out.println("---"+ devicelist);
		    
					//.forEach(irm -> System.out.println("deviceId="+irm.getRecordId()));
		    
		    ipsList.stream().filter(irm -> irm.getRecordType().equalsIgnoreCase("Zone"))
			.forEach(irm -> System.out.println("Zone="+irm.getRecordId()));
		    
		    ipsList.stream().filter(irm -> irm.getRecordType().equalsIgnoreCase("receiver"))
				.forEach(irm ->{
    				if(irm.getRecordId()!=null)
    					receiverlist.add(Integer.valueOf(irm.getRecordId().trim())); }
				); */
		   // System.out.println(receiverlist.size());
		  //System.out.println("---"+ Collections.sort(ipsList));
		  
		 }
	


}
