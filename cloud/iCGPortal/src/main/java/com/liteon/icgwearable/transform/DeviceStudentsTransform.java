package com.liteon.icgwearable.transform;

public class DeviceStudentsTransform {
	private String uuid;
	private String status;
	private String start_date;
	private String end_date;
	private String new_start_date;
	private String new_end_date;
	private Integer student_id;
	private String student_class;
	private String student_name;
	private Integer pfi ;
	
	public Integer getPfi() {
		return pfi;
	}

	public void setPfi(Integer pfi) {
		this.pfi = pfi;
	}

	public String getNew_start_date() {
		return new_start_date;
	}

	public void setNew_start_date(String new_start_date) {
		this.new_start_date = new_start_date;
	}

	public String getNew_end_date() {
		return new_end_date;
	}

	public void setNew_end_date(String new_end_date) {
		this.new_end_date = new_end_date;
	}

	public DeviceStudentsTransform() {}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public Integer getStudent_id() {
		return student_id;
	}

	public void setStudent_id(Integer student_id) {
		this.student_id = student_id;
	}

	public String getStudent_class() {
		return student_class;
	}

	public void setStudent_class(String student_class) {
		this.student_class = student_class;
	}

	public String getStudent_name() {
		return student_name;
	}

	public void setStudent_name(String student_name) {
		this.student_name = student_name;
	}
	
}
