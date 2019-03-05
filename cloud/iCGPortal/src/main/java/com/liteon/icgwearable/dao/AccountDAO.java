package com.liteon.icgwearable.dao;

import java.util.List;

import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.DeviceConfigurations;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.SchoolDetails;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.DeviceConfigModel;
import com.liteon.icgwearable.model.DeviceListModel;
import com.liteon.icgwearable.model.SchoolModel;
import com.liteon.icgwearable.transform.AccountFroSchoolTransform;
import com.liteon.icgwearable.transform.AdminTransform;
import com.liteon.icgwearable.transform.DeviceAccountTransform;
import com.liteon.icgwearable.transform.DeviceConfigTransform;
import com.liteon.icgwearable.transform.DeviceConfigurationsTransform;
import com.liteon.icgwearable.transform.DeviceStatsTransform;
import com.liteon.icgwearable.transform.SchoolTransform;

public interface AccountDAO {

	public Accounts createAccounts();

	public Accounts createAccountsForSchoolTeacherAndStaff();

	public List<DeviceAccountTransform> getassignedDeviceList(int admin_account_id, int page_id, int total, String grade_id, String classNo);

	public List<DeviceAccountTransform> getunAssignedDeviceList(int admin_account_id, int page_id, int total);
	
	public List<DeviceAccountTransform> getunAssignedDeviceList(int admin_account_id, int page_id, int total, String deviceUUID);

	public Devices getUnassigendDeviceByUUID(String uuid);
	
	public int getTotalNoofassignedDeviceList(int admin_account_id, String grade_id, String classNo);
	
	public int getTotalNoofUnassignedDeviceList(int admin_account_id);
	
	public boolean checkAccountIDExist(int schoolId ); 
	
	public AdminTransform getSchoolAdmin(int account_id);
	
	public int getAllocatedDevices(int account_id);
	
	public SchoolTransform getSchoolDetails(int account_id);
	
	public int updateSchoolDetails(SchoolModel schoolModel);
	
	public int updateSchoolAccountDetails(SchoolModel schoolModel);
	
	public int updateSchoolAdminDetails(SchoolModel schoolModel,String OldUserName);
	
	public Accounts createSchoolAccount(SchoolModel schoolModel);
	
	public SchoolDetails createSchoolDetails(SchoolModel schoolModel,int account_id);
	
	public Users createSchoolAdminAccount(SchoolModel schoolModel,Accounts account);
	
	public int getLatestAccountId();
	
	public List<Devices> listDevices(String uuid);
	
	public String getAccoutNameByAccoutnId(int accoutn_id);
	
	public  DeviceConfigurationsTransform getDeviceConfigurations(int device_configuration_id);
	
	public int updateDeviceDetails(DeviceListModel deviceModel);
	
	public int updateDeviceConfigurationDetails(DeviceListModel deviceModel);	
	
	public int getConfigurationId(DeviceListModel deviceListModel);
	
	public DeviceConfigurations createDeviceConfigccount(DeviceListModel deviceModel);
	
	public Devices createDeviceAccount(DeviceListModel deviceModel, DeviceConfigurations deviceConfig);
	
	public DeviceConfigurations getDeviceConfigurationByID(int configId);
	
	public int getLatestDeviceConfigId();
	
	public List<String> getCountys();
	
	public List<AccountFroSchoolTransform> getSchoolName(String county);
	
	public DeviceStatsTransform getDeviceStats(String county,String schoolName,String status);
	
	public int deleteDevice(int deviceid);
	
	public int getConfigIdByDeviceId(int deviceid);
	
	public int updateDeviceConfigId(int deviceId,int configid);
	
	public List<DeviceConfigTransform> getDeviceModels();
	
	public DeviceConfigurations getDeviceConfigDetails(int configId);
	
	public int updateDeviceConfig(DeviceConfigModel deviceConfigModel);
	
	public List<DeviceConfigurations> getDeviceConfigurationList(int start, int total, String modelSelectedValue);
	
	public List<DeviceConfigurations> getDeviceConfigurationList();
	
	public DeviceConfigurations createDeviceConfigccount(DeviceConfigurations deviceConfig);
	
	public int deleteDeviceConfig(int deviceConfigid);
	
	public List<Integer> getDeviceIdByConfigId(int device_config_id);
	
	public List<DeviceConfigTransform> getDeviceConfiDetailsByModel(String model);
	
	public Accounts findAccountBySchoolId(int school_id);
	
	public int deleteBrokenDevice(int account_id);
	
	public int deleteReturndeDevice(int account_id);
	
	public int getTotalNoofSchools();
	
	public int getTotalNoofFirrmWare(String device_model);
	
	public int getTotalnoOfSchoolwithConfigId(int device_config_id);

	public List<DeviceAccountTransform> getBrokenOrReturnedDeviceUUIDs(int admin_account_id, String device_status);
	
}
