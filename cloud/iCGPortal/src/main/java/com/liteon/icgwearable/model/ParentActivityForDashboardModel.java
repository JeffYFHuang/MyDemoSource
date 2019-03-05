package com.liteon.icgwearable.model;

public class ParentActivityForDashboardModel {
	
	private String measure_type;
	private String start_date;
	private String end_date;
	private String output_type;
	private Integer student_id; 
	private String grade;
	private String source;
	
	public String getMeasure_type() {
		return measure_type;
	}
	public void setMeasure_type(String measure_type) {
		this.measure_type = measure_type;
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
	public Integer getStudent_id() {
		return student_id;
	}
	public void setStudent_id(Integer student_id) {
		this.student_id = student_id;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getOutput_type() {
		return output_type;
	}
	public void setOutput_type(String output_type) {
		this.output_type = output_type;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
}
