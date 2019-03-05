package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity(name = "Users")
@Table(name = "users")
public class Users implements Serializable {
	private static final long serialVersionUID = 3861316472242161156L;
	@Id
	@GeneratedValue
	@Column(name = "user_id")
	private int id;
	
	@Column(name = "role_type", columnDefinition = "enum('parent_admin','parent_member','school_admin','school_teacher','school_staff','super_admin','system_admin','support_staff')")
	private String roleType;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "openid_username")
	private String openidUsername;

	@Column(name = "mobile_session_id", unique = true)
	private String mobileSessionId;
	
	@Transient
	private String cPassword;
	
	@Transient
	private int accountId;
	
	@Column(name = "user_active", columnDefinition = "enum('y','n')")
	private String userActive;
	
	@Column(name = "mobile_number")
	private String mobileNumber;
	
	@Column(name = "signup_activation_code")
	private String signupActivation;
	
	@Column(name = "password_activation_code")
	private String activationCode;
	
	@Column(name = "session_id", unique = true)
	private String sessionId;
	
	@Column(name = "session_expiry", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date sessionExpiry;
	
	@Column(name = "lastlogin_date", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date lastLogin;
	
	@Column(name = "password_activation_code_expiry", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date activationCodeExpiry;
	
	@Column(name = "created_date", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date createdDate;
	
	@Column(name = "updated_date", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern="${display.dateTime}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date updatedDate;

	@Column(name = "android_app_token")
	private String androidAppToken;
	
	@Column(name = "iphone_app_token")
	private String iPhoneAppToken;
	
	public String getAndroidAppToken() {
		return androidAppToken;
	}

	public void setAndroidAppToken(String androidAppToken) {
		this.androidAppToken = androidAppToken;
	}

	public String getiPhoneAppToken() {
		return iPhoneAppToken;
	}

	public void setiPhoneAppToken(String iPhoneAppToken) {
		this.iPhoneAppToken = iPhoneAppToken;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "account_id")
	private Accounts accounts;

	@Override
	public String toString() {
		return "Users [id=" + id + ", roleType=" + roleType + ", name=" + name + ", username=" + username
				+ ", password=" + password + ", openidUsername=" + openidUsername + ", mobileSessionId="
				+ mobileSessionId + ", cPassword=" + cPassword + ", userActive=" + userActive + ", mobileNumber="
				+ mobileNumber + ", signupActivation=" + signupActivation + ", activationCode=" + activationCode
				+ ", sessionId=" + sessionId + ", sessionExpiry=" + sessionExpiry + ", lastLogin=" + lastLogin
				+ ", activationCodeExpiry=" + activationCodeExpiry + ", createdDate=" + createdDate + ", updatedDate="
				+ updatedDate + ", accounts=" + accounts
				+ ", androidAppToken=" + androidAppToken + ", iPhoneAppToken=" + iPhoneAppToken + "]";
	}

	public void setMobileSessionId(String mobileSessionId) {
		this.mobileSessionId = mobileSessionId;
	}

	public String getOpenidUsername() {
		return openidUsername;
	}

	public void setOpenidUsername(String openidUsername) {
		this.openidUsername = openidUsername;
	}

	public String getMobileSessionId() {
		return mobileSessionId;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	public Date getActivationCodeExpiry() {
		return activationCodeExpiry;
	}

	public void setActivationCodeExpiry(Date activationCodeExpiry) {
		this.activationCodeExpiry = activationCodeExpiry;
	}
	
	public Users() {
	}

	public Accounts getAccounts() {
		return accounts;
	}

	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserActive() {
		return userActive;
	}

	public void setUserActive(String userActive) {
		this.userActive = userActive;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public String getSignupActivation() {
		return signupActivation;
	}

	public void setSignupActivation(String signupActivation) {
		this.signupActivation = signupActivation;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getSessionExpiry() {
		return sessionExpiry;
	}

	public void setSessionExpiry(Date sessionExpiry) {
		this.sessionExpiry = sessionExpiry;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getcPassword() {
		return cPassword;
	}

	public void setcPassword(String cPassword) {
		this.cPassword = cPassword;
	}
	
	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
}