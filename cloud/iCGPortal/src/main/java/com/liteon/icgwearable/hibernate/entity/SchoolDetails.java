package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.sql.Time;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "school_details")
public class SchoolDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4457795200801830312L;
	
	@Id
	@GeneratedValue
	@Column(name = "school_details_id", unique = true, nullable = false)
	private Integer schoolDetailsId;
	
	@Column(name="school_id")
	private Integer schoolId;
	
	@Column(name="school_in_start")
	private Time schoolInStart;
	
	@Column(name="school_in_end")
	private Time schoolInEnd;
	
	@Column(name="school_out_start")
	private Time schoolOutStart;
	
	@Column(name="school_out_end")
	private Time schoolOutEnd;
	
	@Column(name="city")
	private String city;
	
	@Column(name="state")
	private String state;
	
	@Column(name="zipcode")
	private String zipcode;
	
	@Column(name="county")
	private String county;
	
	@Column(name="country")
	private String country;
	

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name="address")
	private String address;
	
	public String getMobile_number() {
		return mobile_number;
	}

	public void setMobile_number(String mobile_number) {
		this.mobile_number = mobile_number;
	}

	@Column(name="mobile_number")
	private String mobile_number;
	
	public Integer getSchoolDetailsId() {
		return schoolDetailsId;
	}

	public void setSchoolDetailsId(Integer schoolDetailsId) {
		this.schoolDetailsId = schoolDetailsId;
	}
	
	public Integer getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Integer schoolId) {
		this.schoolId = schoolId;
	}

	public Time getSchoolInStart() {
		return schoolInStart;
	}

	public void setSchoolInStart(Time schoolInStart) {
		this.schoolInStart = schoolInStart;
	}

	public Time getSchoolInEnd() {
		return schoolInEnd;
	}

	public void setSchoolInEnd(Time schoolInEnd) {
		this.schoolInEnd = schoolInEnd;
	}

	public Time getSchoolOutStart() {
		return schoolOutStart;
	}

	public void setSchoolOutStart(Time schoolOutStart) {
		this.schoolOutStart = schoolOutStart;
	}

	public Time getSchoolOutEnd() {
		return schoolOutEnd;
	}

	public void setSchoolOutEnd(Time schoolOutEnd) {
		this.schoolOutEnd = schoolOutEnd;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "SchoolDetails [schoolDetailsId=" + schoolDetailsId + ", schoolId=" + schoolId + ", schoolInStart="
				+ schoolInStart + ", schoolInEnd=" + schoolInEnd + ", schoolOutStart=" + schoolOutStart
				+ ", schoolOutEnd=" + schoolOutEnd + ", city=" + city + ", state=" + state + ", zipcode=" + zipcode
				+ ", county=" + county + ", country=" + country + "]";
	}

}
