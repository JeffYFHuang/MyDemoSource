package com.liteon.icgwearable.dao;

import java.net.UnknownHostException;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.liteon.icgwearable.cassandra.CassandraSingleton;

/**
 * @author bgh30865
 *
 */
@Repository("cassandraDAO")
@Transactional
public class CassandraDAOImpl implements CassandraDAO {

	private static Logger log = Logger.getLogger(CassandraDAOImpl.class);
	private Session session = CassandraSingleton.getInstanceUsingDoubleLocking().getCassandraSession();

	@Override
	public String getKeyspaceBySchoolId(int schoolId) {
		String keyspace = null;
		String keyspaceName = "elm" + schoolId;
		String query = "SELECT keyspace_name FROM system_schema.keyspaces WHERE keyspace_name = '" + keyspaceName + "'";
		log.info("Query" + "\t" + query);
		try {
			ResultSet rs = session.execute(query);
			List<Row> rowsList = rs.all();

			if (null != rowsList && rowsList.size() == 1) {
				log.info("Found Keyspace:" + keyspaceName);
				keyspace = keyspaceName;
			}
		} catch (NoHostAvailableException nhae) {
			log.error("Exception Inside getKeyspaceBySchoolId", nhae);
		} catch (Exception e) {
			log.error("Exception Inside getKeyspaceBySchoolId", e);
		} /*finally {
			session.close();
		}*/

		return keyspace;
	}

	@Override
	public void deleteKeyspaceBySchoolId(int schoolId) {
		String keyspaceName = "elm" + schoolId;
		String query = "DROP KEYSPACE IF EXISTS " + keyspaceName;
		log.info("Query" + "\t" + query);
		try {
			session.execute(query);
		} catch (Exception e) {
			log.error("Exception Inside deleteKeyspaceBySchoolId", e);
		}/* finally {
			session.close();
		}*/
	}

	@Override
	public void createKeyspaceBySchoolId(int schoolId) {
		String keyspaceName = "elm" + schoolId;
		String query = null;
		String ksName = this.getKeyspaceBySchoolId(schoolId);

		if (null != ksName) {
			log.info("Keyspace Already Existing" + keyspaceName);
		} else {
			try {
				log.info("Creating Keyspace:" + keyspaceName);
				query = "CREATE KEYSPACE " + keyspaceName
						+ " WITH REPLICATION = { 'class' : 'org.apache.cassandra.locator.NetworkTopologyStrategy', 'dc1': '2' } "
						+ "AND DURABLE_WRITES = true";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".sleep_month ( uuid text, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".sleep_week ( uuid text, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".sleep_date ( uuid text, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".emotion_date ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".hrm_month ( uuid text, ts bigint, situation int, count int, max int, mean int, min int, sd float, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".context_week ( uuid text, ts bigint, situation int, activeindex int, avghrm int, duration int, hrmcount int, met float, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".step_hour ( uuid text, ts bigint, type int, cal int, count int, distance int, PRIMARY KEY (uuid, ts, type) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".context_month ( uuid text, ts bigint, situation int, activeindex int, avghrm int, duration int, hrmcount int, met float, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".sleep_hour ( uuid text, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".hrm_date ( uuid text, ts bigint, situation int, count int, max int, mean int, min int, sd float, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".hrm_week ( uuid text, ts bigint, situation int, count int, max int, mean int, min int, sd float, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".step_date ( uuid text, ts bigint, type int, cal int, count int, distance int, PRIMARY KEY (uuid, ts, type) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".stress_hour ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".emotion_hour ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".emotion_month ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".step_week ( uuid text, ts bigint, type int, cal int, count int, distance int, PRIMARY KEY (uuid, ts, type) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".hrm_hour ( uuid text, ts bigint, situation int, count int, max int, mean float, min int, sd float, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".step_month ( uuid text, ts bigint, type int, cal int, count int, distance int, PRIMARY KEY (uuid, ts, type) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".stress_week ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".stress_month ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".context_date ( uuid text, ts bigint, situation int, activeindex int, avghrm int, duration int, hrmcount int, met float, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".context_hour ( uuid text, ts bigint, situation int, activeindex int, avghrm float, duration int, hrmcount int, met int, PRIMARY KEY (uuid, ts, situation) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".stress_date ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);

				query = "CREATE TABLE " + keyspaceName
						+ ".emotion_week ( uuid text, ts bigint, duration float, situation float, PRIMARY KEY (uuid, ts) )";
				log.info("Query" + "\t" + query);
				session.execute(query);
			} catch (Exception e) {
				log.error("Exception Inside createKeyspaceBySchoolId", e);
			} /*finally {
				session.close();
			}*/
		}
	}

	@Override
	public List<Row> findPhyscialFitnessIndex(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "SELECT json ts as date, sum(activeindex) as value FROM " + keyspace
					+ ".context_date where uuid = " + "'" + uuid + "'" + " AND ts >= " + startDate + " AND  ts <= "
					+ endDate + " group by ts ";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findStepsCount(java.lang.String,
	 * long, long)
	 */
	@Override
	public List<Row> findStepsCount(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select json ts as date, count as value from " + keyspace + ".step_date where uuid = " + "'"
					+ uuid + "'" + " AND ts >= " + startDate + " AND  ts <= " + endDate + " order by ts desc ";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findActivity(java.lang.String,
	 * long, long)
	 */
	@Override
	public List<Row> findActivity(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select json ts as date, situation, duration from " + keyspace
					+ ".context_date where uuid = " + "'" + uuid + "'" + " AND ts >= " + startDate + " AND  ts <= "
					+ endDate + "  order by ts desc ";

			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findHeartRate(java.lang.String,
	 * long, long)
	 */
	@Override
	public List<Row> findHeartRate(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select json ts as date, mean as value from " + keyspace + ".hrm_date where uuid = " + "'"
					+ uuid + "'" + " AND ts >= " + startDate + " AND  ts <= " + endDate + " order by ts desc ";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liteon.icgwearable.dao.CassandraDAO#findCaloriesBurnt(java.lang.
	 * String, long, long)
	 */
	@Override
	public List<Row> findCaloriesBurnt(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select json ts as date, cal as value from " + keyspace + ".step_date where uuid = " + "'"
					+ uuid + "'" + " AND ts >= " + startDate + " AND  ts <= " + endDate + " order by ts desc ";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findSleepRate(java.lang.String,
	 * long, long)
	 */
	@Override
	public List<Row> findSleepRate(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select json ts as date, duration as value from " + keyspace + ".sleep_date where uuid = "
					+ "'" + uuid + "'" + " AND ts >= " + startDate + " AND  ts <= " + endDate + " order by ts desc";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findSleepData(java.lang.String,
	 * long, long)
	 */
	@Override
	public List<Row> findSleepData(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select uuid, duration from " + keyspace + ".sleep_date where uuid = " + "'" + uuid + "'"
					+ " AND ts >= " + startDate + " AND  ts <= " + endDate + " order by ts desc ";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liteon.icgwearable.dao.CassandraDAO#findStressLevels(java.lang.
	 * String, long, long)
	 */
	@Override
	public List<Row> findStressLevels(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select json ts as date ,situation as situation, duration as duration from " + keyspace
					+ ".stress_hour where uuid = " + "'" + uuid + "'" + " AND ts >= " + startDate + " AND  ts <= "
					+ endDate + " order by ts desc ";

			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findStressRangeLevels(java.lang.
	 * String, long, long)
	 */
	@Override
	public List<Row> findStressRangeLevels(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "select json ts as date ,situation as situation, duration as duration from " + keyspace
					+ ".stress_date where uuid = " + "'" + uuid + "'" + " AND ts >= " + startDate + " AND  ts <= "
					+ endDate + " order by ts desc ";

			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liteon.icgwearable.dao.CassandraDAO#findEmotionLevels(java.lang.
	 * String, long, long)
	 */
	@Override
	public List<Row> findEmotionLevels(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = null;
			query = "select json ts as date , situation as situation, duration as duration from " + keyspace
					+ ".emotion_hour where uuid = " + "'" + uuid + "'" + " AND ts >= " + startDate + " AND  ts <= "
					+ endDate + " order by ts desc ";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findEmotionRangeLevels(java.lang.
	 * String, long, long)
	 */
	@Override
	public List<Row> findEmotionRangeLevels(String uuid, long startDate, long endDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = null;
			query = "select json ts as date , situation as situation, duration as duration from " + keyspace
					+ ".emotion_date where uuid = " + "'" + uuid + "'" + " AND ts >= " + startDate + " AND  ts <= "
					+ endDate + " order by ts desc ";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findPFIDataForDataSync(java.lang.
	 * String, long)
	 */
	@Override
	public List<Row> findPFIDataForDataSync(String uuid, long currentDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String tableName = "context_date";
			String query = "select activeindex FROM " + keyspace + "." + tableName + " where uuid = " + "'" + uuid + "'"
					+ " and ts = " + currentDate + " and situation = 1";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findminMaxHeartRateForDataSync(
	 * java.lang.String, long)
	 */
	@Override
	public List<Row> findminMaxHeartRateForDataSync(String uuid, long currentDate, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String tableName = "hrm_date";
			String query = "select min, max, situation from " + keyspace + "." + tableName + " where uuid = " + "'"
					+ uuid + "'" + " and " + " ts = " + currentDate + " and situation in (1,2)";
			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.liteon.icgwearable.dao.CassandraDAO#findFitnessActivity(java.lang.
	 * String, long, long, java.lang.String)
	 */
	@Override
	public List<Row> findFitnessActivity(String uuid, long startDate, long endDate, String mType, String keyspace) {
		List<Row> rowsList = null;
		if (null != keyspace) {
			String query = "";
			if (mType.equals("pfi"))
				query = "select sum(activeindex) as pfi from " + keyspace + ".context_date where uuid = " + "'" + uuid
						+ "'" + " AND ts >= " + startDate + " AND  ts <= " + endDate;
			else if (mType.equals("steps"))
				query = "select sum(count) as steps from " + keyspace + ".step_date where uuid = " + "'" + uuid + "'"
						+ " AND ts >= " + startDate + " AND  ts <= " + endDate;
			else if (mType.equals("calories"))
				query = "select sum(cal) as calories from " + keyspace + ".step_date where uuid = " + "'" + uuid + "'"
						+ " AND ts >= " + startDate + " AND  ts <= " + endDate;

			log.info("Query" + "\t" + query);
			ResultSet rs = session.execute(query);
			rowsList = rs.all();
		}
		return rowsList;
	}

}
