package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.math.BigInteger;

public class DeviceStatsTransform  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 61780162489581874L;
	
	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	private String schoolName;
	
	private BigInteger count;
	

}
