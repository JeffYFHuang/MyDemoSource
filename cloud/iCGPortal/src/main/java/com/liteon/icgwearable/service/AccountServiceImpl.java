package com.liteon.icgwearable.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.AccountDAO;
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

@Service("accountService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountDAO accountDAO;

	@Override
	public Accounts createAccounts() {
		return this.accountDAO.createAccounts();
	}

	@Override
	public Accounts createAccountsForSchoolTeacherAndStaff() {
		return this.accountDAO.createAccountsForSchoolTeacherAndStaff();
	}

	@Override
	public List<DeviceAccountTransform> getassignedDeviceList(int admin_account_id, int page_id, int total,
			String grade_id, String classNo) {
		return this.accountDAO.getassignedDeviceList(admin_account_id, page_id, total, grade_id, classNo);
	}

	@Override
	public List<DeviceAccountTransform> getunAssignedDeviceList(int admin_account_id, int page_id, int total) {
		return this.accountDAO.getunAssignedDeviceList(admin_account_id, page_id, total);
	}

	@Override
	public List<DeviceAccountTransform> getunAssignedDeviceList(int admin_account_id, int page_id, int total,
			String deviceUUID) {
		return this.accountDAO.getunAssignedDeviceList(admin_account_id, page_id, total, deviceUUID);
	}

	@Override
	public Devices getUnassigendDeviceByUUID(String uuid) {
		return this.accountDAO.getUnassigendDeviceByUUID(uuid);
	}

	@Override
	public int getTotalNoofassignedDeviceList(int admin_account_id, String grade_id, String classNo) {
		return this.accountDAO.getTotalNoofassignedDeviceList(admin_account_id, grade_id, classNo);
	}

	@Override
	public int getTotalNoofUnassignedDeviceList(int admin_account_id) {
		return this.accountDAO.getTotalNoofUnassignedDeviceList(admin_account_id);
	}

	@Override
	public boolean checkAccountIDExist(int schoolId) {
		return this.accountDAO.checkAccountIDExist(schoolId);
	}

	@Override
	public AdminTransform getSchoolAdmin(int account_id) {
		return this.accountDAO.getSchoolAdmin(account_id);
	}

	@Override
	public int getAllocatedDevices(int account_id) {
		return this.accountDAO.getAllocatedDevices(account_id);
	}

	@Override
	public SchoolTransform getSchoolDetails(int account_id) {
		return this.accountDAO.getSchoolDetails(account_id);
	}

	@Override
	public int updateSchoolDetails(SchoolModel schoolModel) {
		return this.accountDAO.updateSchoolDetails(schoolModel);
	}

	@Override
	public int updateSchoolAccountDetails(SchoolModel schoolModel) {
		return this.accountDAO.updateSchoolAccountDetails(schoolModel);
	}

	@Override
	public int updateSchoolAdminDetails(SchoolModel schoolModel, String OldUserName) {
		return this.accountDAO.updateSchoolAdminDetails(schoolModel, OldUserName);
	}

	@Override
	public Accounts createSchoolAccount(SchoolModel schoolModel) {
		return this.accountDAO.createSchoolAccount(schoolModel);
	}

	@Override
	public SchoolDetails createSchoolDetails(SchoolModel schoolModel, int account_id) {
		return this.accountDAO.createSchoolDetails(schoolModel, account_id);
	}

	@Override
	public Users createSchoolAdminAccount(SchoolModel schoolModel, Accounts account) {
		return this.accountDAO.createSchoolAdminAccount(schoolModel, account);
	}

	@Override
	public int getLatestAccountId() {
		return this.accountDAO.getLatestAccountId();
	}

	@Override
	public List<Devices> listDevices(String uuid) {
		return this.accountDAO.listDevices(uuid);
	}

	@Override
	public String getAccoutNameByAccoutnId(int accoutn_id) {
		return this.accountDAO.getAccoutNameByAccoutnId(accoutn_id);
	}

	@Override
	public DeviceConfigurationsTransform getDeviceConfigurations(int device_configuration_id) {
		return this.accountDAO.getDeviceConfigurations(device_configuration_id);
	}

	@Override
	public int updateDeviceDetails(DeviceListModel deviceModel) {
		return this.accountDAO.updateDeviceDetails(deviceModel);
	}

	@Override
	public int updateDeviceConfigurationDetails(DeviceListModel deviceModel) {
		return this.accountDAO.updateDeviceConfigurationDetails(deviceModel);
	}

	@Override
	public int getConfigurationId(DeviceListModel deviceListModel) {
		return this.accountDAO.getConfigurationId(deviceListModel);
	}

	@Override
	public DeviceConfigurations createDeviceConfigccount(DeviceListModel deviceModel) {
		return this.accountDAO.createDeviceConfigccount(deviceModel);
	}

	@Override
	public DeviceConfigurations getDeviceConfigurationByID(int configId) {
		return this.accountDAO.getDeviceConfigurationByID(configId);
	}

	@Override
	public int getLatestDeviceConfigId() {
		return this.accountDAO.getLatestDeviceConfigId();
	}

	@Override
	public Devices createDeviceAccount(DeviceListModel deviceModel, DeviceConfigurations deviceConfig) {
		return this.accountDAO.createDeviceAccount(deviceModel, deviceConfig);
	}

	@Override
	public List<String> getCountys() {
		return this.accountDAO.getCountys();
	}

	@Override
	public List<AccountFroSchoolTransform> getSchoolName(String county) {
		return this.accountDAO.getSchoolName(county);
	}

	@Override
	public DeviceStatsTransform getDeviceStats(String county, String schoolName, String status) {
		return this.accountDAO.getDeviceStats(county, schoolName, status);
	}

	@Override
	public int deleteDevice(int deviceid) {
		return this.accountDAO.deleteDevice(deviceid);
	}

	@Override
	public int getConfigIdByDeviceId(int deviceid) {
		return this.accountDAO.getConfigIdByDeviceId(deviceid);
	}

	@Override
	public int updateDeviceConfigId(int deviceId, int configid) {
		return this.accountDAO.updateDeviceConfigId(deviceId, configid);
	}

	@Override
	public List<DeviceConfigTransform> getDeviceModels() {
		return this.accountDAO.getDeviceModels();
	}

	@Override
	public DeviceConfigurations getDeviceConfigDetails(int configId) {
		return this.accountDAO.getDeviceConfigDetails(configId);
	}

	@Override
	public int updateDeviceConfig(DeviceConfigModel deviceConfigModel) {
		return this.accountDAO.updateDeviceConfig(deviceConfigModel);
	}

	@Override
	public List<DeviceConfigurations> getDeviceConfigurationList(int start, int total, String modelSelectedValue) {
		return this.accountDAO.getDeviceConfigurationList(start, total, modelSelectedValue);
	}

	@Override
	public DeviceConfigurations createDeviceConfigccount(DeviceConfigurations deviceConfig) {
		return this.accountDAO.createDeviceConfigccount(deviceConfig);
	}

	@Override
	public int deleteDeviceConfig(int deviceConfigid) {
		return this.accountDAO.deleteDeviceConfig(deviceConfigid);
	}

	@Override
	public List<Integer> getDeviceIdByConfigId(int device_config_id) {
		return this.accountDAO.getDeviceIdByConfigId(device_config_id);
	}

	@Override
	public List<DeviceConfigTransform> getDeviceConfiDetailsByModel(String model) {
		return this.accountDAO.getDeviceConfiDetailsByModel(model);
	}

	@Override
	public Accounts findAccountBySchoolId(int school_id) {
		return this.accountDAO.findAccountBySchoolId(school_id);
	}

	@Override
	public int deleteBrokenDevice(int account_id) {
		return this.accountDAO.deleteBrokenDevice(account_id);
	}

	@Override
	public int deleteReturndeDevice(int account_id) {
		return this.accountDAO.deleteReturndeDevice(account_id);
	}

	@Override
	public int getTotalNoofSchools() {
		return this.accountDAO.getTotalNoofSchools();
	}

	@Override
	public int getTotalNoofFirrmWare(String device_model) {
		return this.accountDAO.getTotalNoofFirrmWare(device_model);
	}

	@Override
	public List<DeviceConfigurations> getDeviceConfigurationList() {
		return this.accountDAO.getDeviceConfigurationList();
	}

	@Override
	public int getTotalnoOfSchoolwithConfigId(int device_config_id) {
		return this.accountDAO.getTotalnoOfSchoolwithConfigId(device_config_id);
	}

	@Override
	public List<DeviceAccountTransform> getBrokenOrReturnedDeviceUUIDs(int admin_account_id, String device_status) {
		return this.accountDAO.getBrokenOrReturnedDeviceUUIDs(admin_account_id, device_status);
	}

}
