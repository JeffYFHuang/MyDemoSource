package com.liteon.icgwearable.enums;

import com.liteon.icgwearable.enums.LITEONEnums;

/**
 * The enum provides process it that is being used in the services.
 * 
 */

public enum ProcessId implements LITEONEnums{
	/**
	 * The Identifier represents JSON DATA Segment
	 * process
	 * 
	 */
	JSON_DATA_SEGMENT(1, "JSON DATA SEGMENT"),
	/**
	 * The Identifier represents JSON Response Segment
	 * process
	 * 
	 */
	JSON_RESPONSE_SEGMENT(2, "JSON RESPONSE SEGMENT");
	

	/** Process id. */
	private int processid;

	/** The description. */
	private String description;

	/**
	 * The constructor expects the id of process.
	 * 
	 * @param processid
	 *            the processid
	 * @param description
	 *            the description
	 */
	ProcessId(int processid, String description) {
		this.processid = processid;
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liteon.icgwearable.enums.LITEONEnums#getId()
	 */
	@Override
	public int getId() {
		return this.processid;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liteon.icgwearable.enums.LITEONEnums#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}
}
