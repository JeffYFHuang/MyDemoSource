package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "ips_receiver")
public class IPSReceiver implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3078372405770457653L;

	@Id
	@GeneratedValue
	@Column(name = "ips_receiver_id", unique = true, nullable = false)
	private Integer receiverId;

	@Column(name = "ips_receiver_mac")
	private String receiverMac;

	@Column(name = "receiver_name")
	private String receiverName;

	@Column(name = "school_id")
	private Integer schoolId;

	@Column(name = "receiver_version")
	private String receiverVersion;

	@Column(name = "session_id", unique = true)
	private String sessionId;

	@Column(name = "receiver_status", unique = true)
	private String receiver_status;

	@Column(name = "session_expiry", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern = "dd-MM-yyyy  HH:mm:ss")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date sessionExpiry;

	@Column(name = "created_date", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern = "${display.dateTime}")
	private Date created_date;

	@Column(name = "updated_date", columnDefinition = "DATETIME")
	@DateTimeFormat(pattern = "${display.dateTime}")
	private Date updated_date;

	public Integer getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Integer receiverId) {
		this.receiverId = receiverId;
	}

	public String getReceiverMac() {
		return receiverMac;
	}

	public void setReceiverMac(String receiverMac) {
		this.receiverMac = receiverMac;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}

	public String getReceiverVersion() {
		return receiverVersion;
	}

	public void setReceiverVersion(String receiverVersion) {
		this.receiverVersion = receiverVersion;
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

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getUpdated_date() {
		return updated_date;
	}

	public void setUpdated_date(Date updated_date) {
		this.updated_date = updated_date;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getReceiver_status() {
		return receiver_status;
	}

	public void setReceiver_status(String receiver_status) {
		this.receiver_status = receiver_status;
	}

	@Override
	public String toString() {
		return "IPSReceiver [receiverId=" + receiverId + ", receiverMac=" + receiverMac + ", receiverName="
				+ receiverName + ", schoolId=" + schoolId + ", receiverVersion=" + receiverVersion + ", sessionId="
				+ sessionId + ", receiver_status=" + receiver_status + ", sessionExpiry=" + sessionExpiry
				+ ", created_date=" + created_date + ", updated_date=" + updated_date + "]";
	}

}
