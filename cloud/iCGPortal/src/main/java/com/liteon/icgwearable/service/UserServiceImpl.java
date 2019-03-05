package com.liteon.icgwearable.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.liteon.icgwearable.dao.UserDAO;
import com.liteon.icgwearable.hibernate.entity.Accounts;
import com.liteon.icgwearable.hibernate.entity.Announcement;
import com.liteon.icgwearable.hibernate.entity.Devices;
import com.liteon.icgwearable.hibernate.entity.Students;
import com.liteon.icgwearable.hibernate.entity.Timetable;
import com.liteon.icgwearable.hibernate.entity.Users;
import com.liteon.icgwearable.model.AnnouncementModel;
import com.liteon.icgwearable.model.NewDevicePairingModel;
import com.liteon.icgwearable.model.UsersModel;
import com.liteon.icgwearable.transform.AccountsTransform;
import com.liteon.icgwearable.transform.DevicesTransform;
import com.liteon.icgwearable.transform.GuardiansDetailsListTransform;
import com.liteon.icgwearable.transform.HealthStudentTransform;
import com.liteon.icgwearable.transform.KidsListForParentMemberTransform;
import com.liteon.icgwearable.transform.RewardsTransform;
import com.liteon.icgwearable.transform.StudentsListTransform;
import com.liteon.icgwearable.transform.ValidNewDevicePairingTransform;
import com.liteon.icgwearable.transform.ValidParentStudentTransform;
import com.liteon.icgwearable.transform.ValidParentUsersWithTokenTransform;
import com.liteon.icgwearable.transform.ValidTeacherUsersWithTokenTransform;

/**
 * @author Vikas
 *
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
	private static Logger log = Logger.getLogger(UserServiceImpl.class);
	@Autowired
	private UserDAO userDAO;

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public Users checkLogin(String userName, String userPassword) {
		return this.userDAO.checkLogin(userName, userPassword);
	}

	@Override
	public Users getUserDetails(String userName) {
		return this.userDAO.getUserDetails(userName);
	}
	
	@Override
	public HashMap<String, Object> getContactListForParentAdmin(String uuid, String roleType) {
		return this.userDAO.getContactListForParentAdmin(uuid, roleType);
	}

	@Override
	public Users getUserByUserName(String userName) {
		return this.userDAO.getUserByUserName(userName);
	}
	
	@Override
	public void addUser(Users user) {

	}

	@Override
	public List<Users> getAllUsers() {
		return null;
	}

	@Override
	public void deleteUser(Users user) {
		this.userDAO.deleteUser(user);

	}

	@Override
	@Transactional
	public Users getUser(int userId) {
		return userDAO.getUser(userId);
	}

	@Override
	@Transactional
	public Users updateUser(Users user) {
		return userDAO.updateUser(user);
	}

	@Override
	public Users updateUsersWithAccountId(Users users, Users webUser, String oldUserName) {
		return this.userDAO.updateUsersWithAccountId(users, webUser, oldUserName);
	}

	@Override
	public String getRoleType(String username, String password) {
		return this.userDAO.getRoleType(username, password);
	}

	@Override
	public List<RewardsTransform> getRewards(String userId) {
		return this.userDAO.getRewards(userId);
	}

	@Override
	public List<DevicesTransform> getDeviceUuid(int userId) {
		return this.userDAO.getDeviceUuid(userId);
	}

	@Override
	public int findUserIdByUNAndPass(String username, String password) {
		return this.userDAO.findUserIdByUNAndPass(username, password);
	}

	@Override
	public List<HealthStudentTransform> getHealthInfo(int userId) {
		return this.userDAO.getHealthInfo(userId);
	}

	public String accountRegister(Devices devices, Users user) {
		return userDAO.accountRegister(devices, user);
	}

	@Override
	public String userValidation(String username, String activationCode) {
		String msg = userDAO.userValidation(username, activationCode);
		return msg;
	}

	@Override
	public List<ValidTeacherUsersWithTokenTransform> getTeacherUserTokenDetails(int eventId, String uuid) {
		return this.userDAO.getTeacherUserTokenDetails(eventId, uuid);
	}

	@Override
	public List<ValidParentUsersWithTokenTransform> getParentUserTokenDetails(int eventId, String uuid,
			String isParentDefault) {
		return this.userDAO.getParentUserTokenDetails(eventId, uuid, isParentDefault);
	}

	@Override
	public String getRoleType(int accountId) {
		return this.userDAO.getRoleType(accountId);
	}

	@Override
	public Accounts getAccountsByAccId(int accId) {
		return this.userDAO.getAccountsByAccId(accId);
	}

	@Override
	public Users getUserByAccId(int accID) {
		return this.userDAO.getUserByAccId(accID);
	}

	@Override
	public int getAccountIdByUserId(int userId) {
		return this.userDAO.getAccountIdByUserId(userId);
	}

	@Override
	public Accounts getAccounts(int userId) {
		return this.userDAO.getAccounts(userId);
	}

	@Override
	public boolean isUsersExist(String username) {
		return this.userDAO.isUsersExist(username);
	}

	@Override
	public int getUserIdByUsername(String username) {
		return this.userDAO.getUserIdByUsername(username);
	}

	@Override
	public void updateAppToken(int id, String appToken, String appType) {
		this.userDAO.updateAppToken(id, appToken, appType);
	}

	@Override
	public void adduserSessionByID(int id, String sessionId, Date lastlogindate) {
		this.userDAO.adduserSessionByID(id, sessionId, lastlogindate);
	}

	@Override
	public Users getUserBySessionId(String sessionId) {
		return this.userDAO.getUserBySessionId(sessionId);
	}

	@Override
	public void resetSessionDetails(String sessionId) {
		this.userDAO.resetSessionDetails(sessionId);
	}

	@Override
	public Accounts getAccountIdbyUserId(int userId) {
		return userDAO.getAccountByUserId(userId);
	}

	@Override
	public boolean isUserExists(int userId) {
		return this.userDAO.isUserExists(userId);
	}

	@Override
	public List<KidsListForParentMemberTransform> getKidsForParentMember(int userId, int memberId, int student_id) {
		return userDAO.getKidsForParentMember(userId, memberId, student_id);
	}

	@Override
	public List<Timetable> listOfTimeTable() {
		return userDAO.listOfTimeTable();
	}

	@Override
	public void updateMemberInfo(int subscriptionId, String SosAlert, String entryexitalert, String bandRemovealert) {
		this.userDAO.updateMemberInfo(subscriptionId, SosAlert, entryexitalert, bandRemovealert);
	}

	@Override
	public ValidParentStudentTransform checkDeviceUuidAndGetstudent_idAndAccoundId(String uuid) {
		return this.userDAO.checkDeviceUuidAndGetstudent_idAndAccoundId(uuid);
	}

	@Override
	public int checkTotalPairedSetWithUsers(String uuid, String role) {
		return this.userDAO.checkTotalPairedSetWithUsers(uuid, role);
	}

	@Override
	public String userRegister(Users user) {
		return this.userDAO.userRegister(user);
	}

	@Override
	public List<Integer> getsubscriberListForParentAdmin(int userId) {
		return this.userDAO.getsubscriberListForParentAdmin(userId);
	}

	@Override
	public int updateMemberInfofromApi(int subscriptionId, int Alertid, String alertValue) {
		return this.userDAO.updateMemberInfofromApi(subscriptionId, Alertid, alertValue);

	}

	@Override
	public boolean isEmailExist(String email) {
		return this.userDAO.isEmailExist(email);
	}

	@Override
	public ValidNewDevicePairingTransform checkDeviceUuidAndGetDeviceActive(NewDevicePairingModel newDevicePairingModel,
			int userId) {
		return this.userDAO.checkDeviceUuidAndGetDeviceActive(newDevicePairingModel, userId);
	}

	@Override
	public String updateNewDevicePairing(Devices devices, Users user, NewDevicePairingModel newDevicePairingModel) {
		return this.userDAO.updateNewDevicePairing(devices, user, newDevicePairingModel);
	}

	@Override
	public AccountsTransform checkAccountIdExist(Integer deviceId) {
		return this.userDAO.checkAccountIdExist(deviceId);
	}

	@Override
	public AccountsTransform checkSessionAccountId(int userId) {
		return this.userDAO.checkSessionAccountId(userId);
	}

	@Override
	public String updateDeviceUnPairing(Devices devices, Users users, NewDevicePairingModel newDevicePairingModel) {
		return this.userDAO.updateDeviceUnPairing(devices, users, newDevicePairingModel);
	}

	@Override
	public String sendEmail(Users users) {
		return this.userDAO.sendEmail(users);
	}

	@Override
	public Users findUserByActivationCode(String key) {
		return this.userDAO.findUserByActivationCode(key);
	}

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int userId) {
		return this.userDAO.getStudentDetailsFromStudent(userId);
	}

	@Override
	public List<StudentsListTransform> getStudentDetailsFromStudent(int accountId, String studentClass) {
		return this.userDAO.getStudentDetailsFromStudent(accountId, studentClass);
	}

	@Override
	public List<GuardiansDetailsListTransform> getGuardiansDetailsList(int accountId) {
		return this.userDAO.getGuardiansDetailsList(accountId);
	}

	@Override
	public boolean findUserActivationKey(String key) {
		return this.userDAO.findUserActivationKey(key);
	}

	@Override
	public boolean checkRoleForUser(String username, String role) {
		return this.userDAO.checkRoleForUser(username, role);
	}

	@Override
	public Accounts getAccountByUserId(int userId) {
		return this.userDAO.getAccountByUserId(userId);
	}

	@Override
	public List<Announcement> listOfAnnouncement() {
		return null;
	}

	@Override
	public Announcement getAnnouncement(int announcementId) {
		return null;
	}

	@Override
	public void addAnnouncement(AnnouncementModel announcementmodel) {
	}

	@Override
	public void deleteAnnouncement(Integer announcementId) {
	}

	@Override
	public boolean isNameExist(String username) {
		return false;
	}

	@Override
	public void updateSignupKey(Users users) {
		log.info("updateSignupKey");
		this.userDAO.updateSignupKey(users);
	}

	public Users findBySignUpActivationCode(String key) {
		log.info("findBySignUpActivationCode");
		return this.userDAO.findBySignUpActivationCode(key);
	}

	@Override
	public Students linkNewDevicePairing(String uuid) {
		return this.userDAO.linkNewDevicePairing(uuid);
	}

	@Override
	public void updateStudentsTableOnDeviceUnpair(int student_id) {
		this.userDAO.updateStudentsTableOnDeviceUnpair(student_id);

	}

	@Override
	public boolean checkIfDeviceAlreadyLinked(int student_id) {
		return this.userDAO.checkIfDeviceAlreadyLinked(student_id);
	}

	@Override
	public int getAccountIdFromStudentId(int student_id) {
		return this.userDAO.getAccountIdFromStudentId(student_id);
	}

	@Override
	public int getCurrentParentCount(int account_id) {
		return this.userDAO.getCurrentParentCount(account_id);
	}

	@Override
	public void updateUserWithAccountId(String userName, int account_id) {
		this.userDAO.updateUserWithAccountId(userName, account_id);
	}

	@Override
	public void createNewAccount(Accounts account) {
		this.userDAO.createNewAccount(account);
	}

	@Override
	public int getLatestAccountId() {
		return this.userDAO.getLatestAccountId();
	}

	@Override
	public void updateStudentWithAccountId(int student_id, int account_id) {
		this.userDAO.updateStudentWithAccountId(student_id, account_id);
	}

	@Override
	public boolean updateFederatedLogin(String sessionId, String profileName, String email, String userAgent) {
		return this.userDAO.updateFederatedLogin(sessionId, profileName, email, userAgent);
	}

	@Override
	public boolean insertFederatedLogin(String sessionId, String profileName, String email, String userAgent) {
		return this.userDAO.insertFederatedLogin(sessionId, profileName, email, userAgent);
	}

	@Override
	public boolean openIdExist(String openId) {
		return this.userDAO.openIdExist(openId);
	}

	@Override
	public boolean updateOpenId(String sesionId, String profileName, String email, String userAgent) {
		return this.userDAO.updateOpenId(sesionId, profileName, email, userAgent);
	}

	@Override
	public void addMobileUserSessionByID(int id, String sessionId, Date lastlogindate) {
		this.userDAO.addMobileUserSessionByID(id, sessionId, lastlogindate);
	}

	@Override
	public Users getUserByMobileSessionId(String sessionId) {
		return this.userDAO.getUserByMobileSessionId(sessionId);
	}

	@Override
	public void resetMobileSessionDetails(String sessionId) {
		this.userDAO.resetMobileSessionDetails(sessionId);
	}

	@Override
	public boolean unpairParentDevices(String uuid, int user_id) {
		return this.userDAO.unpairParentDevices(uuid, user_id);
	}

	@Override
	public List<ValidTeacherUsersWithTokenTransform> getStaffUserTokenDetails(int eventId, String uuid) {
		return this.userDAO.getStaffUserTokenDetails(eventId, uuid);
	}

	@Override
	public Users findUsers(String username) {
		return this.userDAO.findUsers(username);
	}

	@Override
	public Users findByPasswordActivationCode(String key) {
		return this.userDAO.findByPasswordActivationCode(key);
	}

	@Override
	public String sendUserActivationEmail(String name, String passwordActivationCode, String username) {
		return this.userDAO.sendUserActivationEmail(name, passwordActivationCode, username);
	}

	@Override
	public Users getOpenIdUserDetails(String userName) {
		return this.userDAO.getOpenIdUserDetails(userName);
	}

	@Override
	public Users findUserById(int userId) {
		return this.userDAO.findUserById(userId);
	}

	@Override
	public int checkTotalPairedSetForParentAdmin(int student_id) {
		return this.userDAO.checkTotalPairedSetForParentAdmin(student_id);
	}

	@Override
	public List<Users> getUserListByRoleType(int school_id, int type, int page_id, int total) {
		return this.userDAO.getUserListByRoleType(school_id, type, page_id, total);
	}

	@Override
	public int deleteUserById(int userId) {
		return this.userDAO.deleteUserById(userId);
	}

	@Override
	public void deleteGuardian(Users guardianUser) {
		this.userDAO.deleteGuardian(guardianUser);
	}

	@Override
	public Users findUsersBySchoolId(String username, Integer schoolId) {
		return this.userDAO.findUsersBySchoolId(username, schoolId);
	}

	@Override
	public Users validateUserBySessionId(String sessionID) {
		return this.userDAO.validateUserBySessionId(sessionID);
	}

	@Override
	public Users validateUserBySession(String sessionID) {
		return this.userDAO.validateUserBySession(sessionID);
	}

	@Override
	public Users validateUserByUsername(String username) {
		return this.userDAO.validateUserByUsername(username);
	}

	@Override
	public Users isFederatedUserExist(String username) {
		return this.userDAO.isFederatedUserExist(username);
	}

	@Override
	public void updateUserActivation(int user_id, String password, String password_activation_code,
			String password_activation_code_expiry, String user_active) {
		this.userDAO.updateUserActivation(user_id, password, password_activation_code, password_activation_code_expiry,
				user_active);

	}

	@Override
	public boolean isUsernameExists(String username) {
		return this.userDAO.isUsernameExists(username);
	}
}
