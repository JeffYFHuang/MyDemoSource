package com.liteon.icgwearable.model;

public class SchoolPrefModel {
	
	private Integer quiet_hours_to;
	
	public Integer getQuiteHoursTo() {
		return quiet_hours_to;
	}

	public void setQuiteHoursTo(Integer quiteHoursTo) {
		this.quiet_hours_to = quiteHoursTo;
	}

	public Integer getQuiteHoursFrom() {
		return quiet_hours_from;
	}

	public void setQuiteHoursFrom(Integer quiteHoursFrom) {
		this.quiet_hours_from = quiteHoursFrom;
	}

	public String getQuiteHoursDay() {
		return quiet_hours_day;
	}

	public void setQuiteHoursDay(String quiteHoursDay) {
		this.quiet_hours_day = quiteHoursDay;
	}

	private Integer quiet_hours_from;
	
	private String quiet_hours_day;
}
