package com.liteon.icgwearable.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public interface UserService {

	public Users checkLogin(String userName, String userPassword);

	public Users getUserDetails(String userName);
	
	public Users getUserByUserName(String userName);

	public Users getOpenIdUserDetails(String userName);

	public void addUser(Users user);

	public List<Users> getAllUsers();

	public void deleteUser(Users user);

	public Users getUser(int userId);

	public int findUserIdByUNAndPass(String username, String password);

	public Users updateUser(Users user);

	public Users updateUsersWithAccountId(Users users, Users webUser, String oldUserName);

	public String getRoleType(String username, String password);

	public List<RewardsTransform> getRewards(String userId);

	public List<DevicesTransform> getDeviceUuid(int userId);

	public List<HealthStudentTransform> getHealthInfo(int userId);

	public List<ValidTeacherUsersWithTokenTransform> getTeacherUserTokenDetails(int eventId, String uuid);

	public List<ValidParentUsersWithTokenTransform> getParentUserTokenDetails(int eventId, String uuid,
			String isParentDefault);

	public List<ValidTeacherUsersWithTokenTransform> getStaffUserTokenDetails(int eventId, String uuid);

	public String accountRegister(Devices devices, Users user);

	public String userValidation(String username, String activationCode);

	public String getRoleType(int accountId);

	public List<Announcement> listOfAnnouncement();

	public Announcement getAnnouncement(int announcementId);

	public void addAnnouncement(AnnouncementModel announcementmodel);

	public void deleteAnnouncement(Integer announcementId);

	public boolean isNameExist(String username);

	public Accounts getAccountsByAccId(int accId);

	public Users getUserByAccId(int accID);

	public int getAccountIdByUserId(int userId);

	public Accounts getAccounts(int userId);

	public boolean isUsersExist(String username);

	public boolean isEmailExist(String email);

	public int getUserIdByUsername(String username);

	public void updateAppToken(int id, String appToken, String appType);

	public void resetSessionDetails(String sessionId);

	public void adduserSessionByID(int id, String sessionId, Date lastlogindate);

	public Users getUserBySessionId(String sessionId);

	public Accounts getAccountIdbyUserId(int userId);

	public List<Timetable> listOfTimeTable();

	public AccountsTransform checkAccountIdExist(Integer deviceId);

	public AccountsTransform checkSessionAccountId(int userId);

	public boolean isUserExists(int userId);

	public List<KidsListForParentMemberTransform> getKidsForParentMember(int userId, int memberId, int student_id);

	public void updateMemberInfo(int subscriptionId, String SosAlert, String entryexitalert, String bandRemovealert);

	public int checkTotalPairedSetWithUsers(String uuid, String role);

	public String userRegister(Users user);

	public ValidParentStudentTransform checkDeviceUuidAndGetstudent_idAndAccoundId(String uuid);

	public List<Integer> getsubscriberListForParentAdmin(int userId);

	public int updateMemberInfofromApi(int subscriptionId, int Alertid, String alertValue);

	public ValidNewDevicePairingTransform checkDeviceUuidAndGetDeviceActive(NewDevicePairingModel newDevicePairingModel,
			int userId);

	public String updateNewDevicePairing(Devices devices, Users user, NewDevicePairingModel newDevicePairingModel);

	public String updateDeviceUnPairing(Devices devices, Users users, NewDevicePairingModel newDevicePairingModel);

	public String sendEmail(Users users);

	public Users findUserByActivationCode(String key);

	public boolean findUserActivationKey(String key);

	public List<StudentsListTransform> getStudentDetailsFromStudent(int userId);

	public List<StudentsListTransform> getStudentDetailsFromStudent(int accountId, String studentClass);

	public List<GuardiansDetailsListTransform> getGuardiansDetailsList(int accountId);

	public boolean checkRoleForUser(String username, String role);

	public Accounts getAccountByUserId(int userId);

	public void updateSignupKey(Users users);

	public Users findBySignUpActivationCode(String key);

	public Students linkNewDevicePairing(String uuid);

	public void updateStudentsTableOnDeviceUnpair(int student_id);

	public boolean checkIfDeviceAlreadyLinked(int student_id);

	public int getAccountIdFromStudentId(int student_id);

	public int getCurrentParentCount(int account_id);

	public void updateUserWithAccountId(String userName, int account_id);

	public void updateStudentWithAccountId(int student_id, int account_id);

	public int getLatestAccountId();

	public void createNewAccount(Accounts account);

	public void addMobileUserSessionByID(int id, String sessionId, Date lastlogindate);

	public Users getUserByMobileSessionId(String sessionId);

	public void resetMobileSessionDetails(String sessionId);

	public boolean updateFederatedLogin(String sessionId, String profileName, String email, String userAgent);

	public boolean insertFederatedLogin(String sessionId, String profileName, String email, String userAgent);

	public boolean openIdExist(String openId);

	public boolean updateOpenId(String sesionId, String profileName, String email, String userAgent);

	public boolean unpairParentDevices(String uuid, int user_id);

	public Users findUsers(String username);

	public Users findByPasswordActivationCode(String key);

	public String sendUserActivationEmail(String name, String passwordActivationCode, String username);

	public Users findUserById(int userId);

	public int checkTotalPairedSetForParentAdmin(int student_id);

	public List<Users> getUserListByRoleType(int school_id, int type, int page_id, int total);

	public int deleteUserById(int userId);

	public void deleteGuardian(Users guardianUser);

	public Users findUsersBySchoolId(String username, Integer schoolId);

	public Users validateUserBySessionId(String sessionID);

	public Users validateUserBySession(String sessionID);

	public Users validateUserByUsername(String username);

	public Users isFederatedUserExist(String username);

	public void updateUserActivation(int user_id, String password, String password_activation_code,
			String password_activation_code_expiry, String user_active);

	public boolean isUsernameExists(String username);
	
	public HashMap<String, Object> getContactListForParentAdmin(String uuid,String roleType);
}
