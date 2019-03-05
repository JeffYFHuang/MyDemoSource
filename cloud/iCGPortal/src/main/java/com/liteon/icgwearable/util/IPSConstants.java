package com.liteon.icgwearable.util;

public interface IPSConstants {
	
	public static final String RECEIVER = "receiver";
	public static final String ZONE = "zone";
	public static final String DEVICE = "device";
	
	
	public static final String RECORD_TYPE = "record_type";
	public static final String RECORD_ID = "record_id";
	
	//-----Receiver fields--------------
	public static final String RECEIVER_MAC = "ips_receiver_mac";
	public static final String RECEIVER_NAME = "receiver_name";	
	public static final String RECEIVER_VERSION = "receiver_version";
	 
	 
	//-----Receiver zone fields--------------
	  // private Integer receiverId;
	public static final String ZONE_NAME = "zone_name";
	public static final String MAP_TYPE = "map_type";	
	public static final String BUILDING_NAME = "building_name";	
	public static final String FLOOR_NUM= "floor_number"; 
	public static final String DELETE_MAP = "delete_zone_file";	

	//-----Receiver device fields--------------

	public static final String DEVICE_UUID = "device_uuid";
	public static final String FIRMWARE_NAME = "firmware_name";
	public static final String FIRMWARE_VERSION= "firmware_version";
	public static final String DEVICE_MODEL ="device_model";
	public static final String DEVICE_STATUS = "status";
	public static final String DEVICE_STATUS_DESC = "status_description";
	
	public static final String STATUSCODE_01 = "BSUC01";
	public static final String BSUC01_MSG = "API Request Success";
	
	public static final String STATUSCODE_02 = "BERR02";
	public static final String BERR02_MSG = "Session Expired ,Please Relogin";
	
	public static final String STATUSCODE_03 = "BERR03";
	public static final String BERR03_MSG = "Invalid Json Input"; //json invalid
	
	public static final String STATUSCODE_04 = "BERR04";
	public static final String BERR04_MSG = "Not sufficient school information is present in DB to validate access key";
	
	public static final String STATUSCODE_05 = "BERR05";
	public static final String BERR05_MSG = "Invalid Input,check mandatory input can't be null or empty";
	
	public static final String STATUSCODE_06 = "BERR06";
	public static final String BERR06_MSG = "Invalid Mac, IPSReceiver with provided Mac not present";
	
	public static final String STATUSCODE_07 = "BERR07";
	public static final String BERR07_MSG = "Invalid Input, check mandatory input is null or empty for particular record";
	
	public static final String STATUSCODE_08 = "BERR08";
	public static final String BERR08_MSG = "Invalid Mac input, given Mac not matching with Mac provided for receiver in file";
	
	public static final String STATUSCODE_09 = "BERR09";
	public static final String BERR09_MSG = "Invalid school Id, school with school_id does not exist";	
	
	public static final String STATUSCODE_10 = "BERR10";
	public static final String BERR10_MSG = "Invalid session token input";
	
	public static final String STATUSCODE_11 = "BERR11";
	public static final String BERR11_MSG = "Entries are not deleted properly because related data might not deleted";
	
	public static final String STATUSCODE_12 = "BERR12";
	public static final String BERR12_MSG = "Invalid Id, IPSReceiver system with id does not exist.";
	
	public static final String STATUSCODE_13 = "BERR13";
	public static final String BERR13_MSG = "Invalid file size, please upload file is less than 1 MB.";
	
	public static final String STATUSCODE_14 = "BERR14";
	public static final String BERR14_MSG = "Invalid file extension, Please Choose a  JPG or JPEG or PNG File extension";
	
	public static final String STATUSCODE_15 = "BERR15";
	public static final String BERR15_MSG = "invalid UUID, provide unique device UUID ";
	
	public static final String STATUSCODE_16 = "BERR16";
	public static final String BERR16_MSG = "Invalid Receiver ID, Zone with given zoneId is not associated with Receiver provided Mac";
	
	public static final String STATUSCODE_17 = "BERR17";
	public static final String BERR17_MSG = "Data Entry is not updated";
	
	public static final String STATUSCODE_18 = "BERR18";
	public static final String BERR18_MSG = "Invalid ID, IPSReceiver Zone ID doesnot exist";
	
	public static final String STATUSCODE_19 = "BERR19";
	public static final String BERR19_MSG = "Invalid receiver record_id, receiverId in file not match with the Id got for receiver Mac address ";
	
	public static final String STATUSCODE_20 = "BERR20";
	public static final String BERR20_MSG = "All zones are not deleted properly, please delete mapfiles for input zone list manually, if file exists";
	
	public static final String STATUSCODE_21 = "BERR21";
	public static final String BERR21_MSG = "Invalid file extension, Please Choose a  csv File extension";

	public static final String STATUSCODE_22 = "BERR22";
	public static final String BERR22_MSG = "Unauthorized Access";
	
	public static final String STATUSCODE_23 = "BERR23";
	public static final String BERR23_MSG = "More than one Receiver Id metioned in uploaded file.";
	
	public static final String STATUSCODE_24 = "BERR24";
	public static final String BERR24_MSG = "Invalid Input or No entry in table to delete";
	
	public static final String STATUSCODE_25 = "BERR25";
	public static final String BERR25_MSG = "map type value should be full or partial ";
	
	public static final String STATUSCODE_26 = "BERR26";
	public static final String BERR26_MSG = "CSV File Is Invalid ";
	
	public static final String STATUSCODE_27 = "BERR27";
	public static final String BERR27_MSG = "Uploaded file is null or empty";

	public static final String STATUSCODE_28 = "BERR28";
	public static final String BERR28_MSG = "Mac address and SchoolId already exists with 2 different receivers";
	
	public static final String STATUSCODE_29 = "BERR29" ;
	public static final String BERR29_MSG = "Beacon Report Not Supported For Given Event";
	
	public static final String STATUSCODE_30 = "BERR30" ;
	public static final String BERR30_MSG = "Unsupported Event Id";
	
	public static final String STATUSCODE_31 = "BERR31" ;
	public static final String BERR31_MSG = "Device Event Creation Failed";
	
	public static final String STATUSCODE_32 = "BERR32" ;
	public static final String BERR32_MSG = "Beacon UUID Is Not Valid";
	

}
