package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class SysConfigurationTransform implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer adminScheduleDataSync;
	private Integer adminScheduleSessionValidity;
	private Integer adminScheduleUserSessionValidity;
	private Integer adminSchedulePwdLinkValidity;
	public Integer getAdminScheduleDataSync() {
		return adminScheduleDataSync;
	}
	public void setAdminScheduleDataSync(Integer adminScheduleDataSync) {
		this.adminScheduleDataSync = adminScheduleDataSync;
	}
	public Integer getAdminScheduleSessionValidity() {
		return adminScheduleSessionValidity;
	}
	public void setAdminScheduleSessionValidity(Integer adminScheduleSessionValidity) {
		this.adminScheduleSessionValidity = adminScheduleSessionValidity;
	}
	public Integer getAdminScheduleUserSessionValidity() {
		return adminScheduleUserSessionValidity;
	}
	public void setAdminScheduleUserSessionValidity(Integer adminScheduleUserSessionValidity) {
		this.adminScheduleUserSessionValidity = adminScheduleUserSessionValidity;
	}
	public Integer getAdminSchedulePwdLinkValidity() {
		return adminSchedulePwdLinkValidity;
	}
	public void setAdminSchedulePwdLinkValidity(Integer adminSchedulePwdLinkValidity) {
		this.adminSchedulePwdLinkValidity = adminSchedulePwdLinkValidity;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "SysConfigurationTransform [adminScheduleDataSync=" + adminScheduleDataSync
				+ ", adminScheduleSessionValidity=" + adminScheduleSessionValidity
				+ ", adminScheduleUserSessionValidity=" + adminScheduleUserSessionValidity
				+ ", adminSchedulePwdLinkValidity=" + adminSchedulePwdLinkValidity + "]";
	}
	
	
	
	



	
}
