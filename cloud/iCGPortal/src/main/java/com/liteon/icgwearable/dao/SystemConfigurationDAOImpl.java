package com.liteon.icgwearable.dao;

import java.util.List;
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("systemConfigurationDAO")
@Transactional
public class SystemConfigurationDAOImpl implements SystemConfigurationDAO {
	private static Logger log = Logger.getLogger(SystemConfigurationDAOImpl.class);
	@Autowired
	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Integer[] findSystemConfigurationParameters(int id) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		Integer[] configParameterArray = new Integer[4];

		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			String sessionQuery = "select iwps_sync_hours,web_session_validity_minutes,wearable_session_validity_minutes, "
								+ "password_reset_validity_minutes from system_configuration where system_configuration_id = ? ";
			Query query = session.createSQLQuery(sessionQuery);
			query.setParameter(0, id);

			log.info("QRY String" + "\t" + sessionQuery);

			List<Object[]> rows = query.list();
			log.info("rows ----" + rows.size());

			if (rows != null) {
				for (Object[] row : rows) {

					configParameterArray[0] = (Integer) row[0];
					configParameterArray[1] = (Integer) row[1];
					configParameterArray[2] = (Integer) row[2];
					configParameterArray[3] = (Integer) row[3]; 
				}
			}
			tx.commit();
		} catch (Exception e) {
			log.error("Exception Occured in findSystemConfigurationParameters " + e);
			if(null != configParameterArray)
				session.evict(configParameterArray);
			tx.rollback();
		} finally {
			session.close();
		}
		return configParameterArray;
	}
}
