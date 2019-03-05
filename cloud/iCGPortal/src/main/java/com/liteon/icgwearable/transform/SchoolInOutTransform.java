package com.liteon.icgwearable.transform;

import java.io.Serializable;
import java.sql.Time;

public class SchoolInOutTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4999704095500919683L;
	
	



	public Time getSchool_in_start() {
		return school_in_start;
	}
	public void setSchool_in_start(Time school_in_start) {
		this.school_in_start = school_in_start;
	}
	public Time getSchool_in_end() {
		return school_in_end;
	}
	public void setSchool_in_end(Time school_in_end) {
		this.school_in_end = school_in_end;
	}
	public Time getSchool_out_start() {
		return school_out_start;
	}
	public void setSchool_out_start(Time school_out_start) {
		this.school_out_start = school_out_start;
	}
	public Time getSchool_out_end() {
		return school_out_end;
	}
	public void setSchool_out_end(Time school_out_end) {
		this.school_out_end = school_out_end;
	}
	private Time school_in_start;
	private Time school_in_end;
	
	private Time school_out_start;
	private Time school_out_end;
	
	

}
