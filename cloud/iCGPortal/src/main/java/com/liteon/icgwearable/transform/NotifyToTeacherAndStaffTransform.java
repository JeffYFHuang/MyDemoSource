package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class NotifyToTeacherAndStaffTransform implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5842153871608477378L;
	
	private String notify_teacher;
	
	private String notify_staff;
	
	public String getNotify_teacher() {
		return notify_teacher;
	}

	public void setNotify_teacher(String notify_teacher) {
		this.notify_teacher = notify_teacher;
	}

	public String getNotify_staff() {
		return notify_staff;
	}

	public void setNotify_staff(String notify_staff) {
		this.notify_staff = notify_staff;
	}

	
	

}
