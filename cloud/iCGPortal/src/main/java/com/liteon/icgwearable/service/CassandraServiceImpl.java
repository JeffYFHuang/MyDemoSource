package com.liteon.icgwearable.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datastax.driver.core.Row;
import com.liteon.icgwearable.dao.CassandraDAO;

@Service("cassandraService")
public class CassandraServiceImpl implements CassandraService {

	@Autowired
	private CassandraDAO cassandraDAO;

	@Override
	public List<Row> findPhyscialFitnessIndex(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findPhyscialFitnessIndex(uuid, startDate,endDate, keyspace);
	}

	@Override
	public List<Row> findStepsCount(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findStepsCount(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findActivity(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findActivity(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findHeartRate(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findHeartRate(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findCaloriesBurnt(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findCaloriesBurnt(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findSleepRate(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findSleepRate(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findStressLevels(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findStressLevels(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findSleepData(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findSleepData(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findPFIDataForDataSync(String uuid, long currentDate, String keyspace) {
		return this.cassandraDAO.findPFIDataForDataSync(uuid, currentDate, keyspace);
	}

	@Override
	public List<Row> findminMaxHeartRateForDataSync(String uuid, long currentDate, String keyspace) {
		return this.cassandraDAO.findminMaxHeartRateForDataSync(uuid, currentDate, keyspace);
	}

	@Override
	public List<Row> findEmotionLevels(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findEmotionLevels(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findStressRangeLevels(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findStressRangeLevels(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findEmotionRangeLevels(String uuid, long startDate, long endDate, String keyspace) {
		return this.cassandraDAO.findEmotionRangeLevels(uuid, startDate, endDate, keyspace);
	}

	@Override
	public List<Row> findFitnessActivity(String uuid, long startDate, long endDate, String mType, String keyspace) {
		return this.cassandraDAO.findFitnessActivity(uuid, startDate, endDate, mType, keyspace);
	}

	@Override
	public String getKeyspaceBySchoolId(int schoolId) {
		return this.cassandraDAO.getKeyspaceBySchoolId(schoolId);
	}

	@Override
	public void deleteKeyspaceBySchoolId(int schoolId) {
		this.cassandraDAO.deleteKeyspaceBySchoolId(schoolId);
	}

	@Override
	public void createKeyspaceBySchoolId(int schoolId) {
		this.cassandraDAO.createKeyspaceBySchoolId(schoolId);
	}
	
}
