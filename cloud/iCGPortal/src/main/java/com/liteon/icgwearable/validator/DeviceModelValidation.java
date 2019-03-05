package com.liteon.icgwearable.validator;

import com.liteon.icgwearable.model.DeviceModel;

public class DeviceModelValidation {
	
	private boolean errorFlag = false;
	private String gpsdatacodeGpslocationdataMsg = "gpsdatacode/gpslocationdata cannot be blank/It should be between 1 to 45 characters";
	private String sensortypecodeSensorerrorcodeMsg = "sensortypecode/sensorerrorcode cannot be blank/It should be between 1 to 45 characters";
	private String vitalsigntypeVitalsignvalueMsg = "abnormalcode/vitalsigntype/vitalsignvalue cannot be blank/It should be between 1 to 45 characters";
	private String eventoccureddateMsg = "eventoccureddate cannot be blank/It is mandatory";
	private String emptyRequestMsg = "Invalid Request";
	
	public DeviceModelValidation(){
	}
	
	public String validateDeviceModelInput(DeviceModel deviceModel){
		
		if(deviceModel == null ){
			return emptyRequestMsg;
		}
		return null;
	}
	
	public String validateEventOccurredDate(DeviceModel deviceModel){
		 if(deviceModel.getEventoccureddate() == null){
			return eventoccureddateMsg;
		 }
		 return null;
	}
	
	
	public String validateGpsdatacodeAndGpslocationdata(DeviceModel deviceModel){
		if((deviceModel.getGpsdatacode() != null && deviceModel.getGpslocationdata() == null) || 
				(deviceModel.getGpsdatacode() == null && deviceModel.getGpslocationdata() != null) ||  
				(deviceModel.getGpsdatacode().equals("") && deviceModel.getGpslocationdata() != null) ||
				(deviceModel.getGpsdatacode() != null && deviceModel.getGpslocationdata().equals(""))){
			return gpsdatacodeGpslocationdataMsg;
		}
		
		
		return null;
		
	}
	
	
	public String validateSensortypecodeAndSensorerrorcode(DeviceModel deviceModel){
		if((deviceModel.getSensortypecode() != null && deviceModel.getSensorerrorcode() == null) || 
				(deviceModel.getSensortypecode() == null && deviceModel.getSensorerrorcode() != null) ||  
				(deviceModel.getSensortypecode().equals("") && deviceModel.getSensorerrorcode() != null) ||
				(deviceModel.getSensortypecode() != null && deviceModel.getSensorerrorcode().equals(""))){
			return sensortypecodeSensorerrorcodeMsg;
		}
		
		return null;
	}
	
	public String validateVsignVvalueAbnormalCode(DeviceModel deviceModel){
		if((deviceModel.getVitalsigntype() != null && deviceModel.getVitalsignvalue() == null) || 
				(deviceModel.getVitalsigntype() == null && deviceModel.getVitalsignvalue() != null) ||  
				(deviceModel.getVitalsigntype().trim().equals("") && deviceModel.getVitalsignvalue() != null) ||
				(deviceModel.getVitalsigntype() != null && deviceModel.getVitalsignvalue().trim().equals("")) ||
				((deviceModel.getAbnormalcode() == null) || deviceModel.getAbnormalcode().trim().equals(""))){
			return vitalsigntypeVitalsignvalueMsg;
		}
		
		return null;
	}
	
	public boolean checkErrorFlag(){
		return errorFlag;
	}



}
