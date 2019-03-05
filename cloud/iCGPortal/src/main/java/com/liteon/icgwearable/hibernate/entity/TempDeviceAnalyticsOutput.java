package com.liteon.icgwearable.hibernate.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "temp_device_analytics_output")
public class TempDeviceAnalyticsOutput implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1852406329052221226L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;
	@Column(name = "physical_fitness_index", nullable = true)
	private String physical_fitness_index;
	@Column(name = "sleep_quality_index", nullable = true)
	private String sleep_quality_index;
	@Column(name = "concentration_level_index", nullable = true)
	private String concentration_level_index;
	@Column(name = "created_date", columnDefinition="DATETIME")
	private Timestamp created_date;
	
	public TempDeviceAnalyticsOutput() {}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public String getPhysical_fitness_index() {
		return physical_fitness_index;
	}

	public void setPhysical_fitness_index(String physical_fitness_index) {
		this.physical_fitness_index = physical_fitness_index;
	}

	public String getSleep_quality_index() {
		return sleep_quality_index;
	}

	public void setSleep_quality_index(String sleep_quality_index) {
		this.sleep_quality_index = sleep_quality_index;
	}

	public String getConcentration_level_index() {
		return concentration_level_index;
	}

	public void setConcentration_level_index(String concentration_level_index) {
		this.concentration_level_index = concentration_level_index;
	}

	public Timestamp getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Timestamp created_date) {
		this.created_date = created_date;
	}

}
