package com.liteon.icgwearable.transform;

import java.io.Serializable;

public class HealthStudentTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9028894747257443272L;
	
	public HealthStudentTransform(){}
	
	private String name;
	private String description;
	private String parameters;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

}
