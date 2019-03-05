package com.liteon.icgwearable.service;

import java.util.List;

import com.datastax.driver.core.Row;

public interface CassandraService {

	public List<Row> findPhyscialFitnessIndex(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findStepsCount(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findActivity(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findHeartRate(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findCaloriesBurnt(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findSleepRate(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findStressLevels(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findSleepData(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findPFIDataForDataSync(String uuid, long currentDate, String keyspace);

	public List<Row> findminMaxHeartRateForDataSync(String uuid, long currentDate, String keyspace);

	public List<Row> findEmotionLevels(String uuid, long startDate, long endDate, String keyspace);
	
	public List<Row> findStressRangeLevels(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findEmotionRangeLevels(String uuid, long startDate, long endDate, String keyspace);

	public List<Row> findFitnessActivity(String uuid, long startDate, long endDate, String mType, String keyspace);

	public String getKeyspaceBySchoolId(int schoolId);

	public void deleteKeyspaceBySchoolId(int schoolId);

	public void createKeyspaceBySchoolId(int schoolId);
}
