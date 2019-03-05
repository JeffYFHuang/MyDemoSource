package com.liteon.icgwearable.dao;

import java.util.List;

import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.liteon.icgwearable.model.DeviceConfigurationsModel;
import com.liteon.icgwearable.service.DeviceService;
import com.liteon.icgwearable.transform.DeviceAnalyticsOutputTransform;

@Repository("deviceAnalyticsOutputDAO")
@Transactional
public class DeviceAnalyticsOutputDAOImpl implements DeviceAnalyticsOutputDAO {

	private static Logger log = Logger.getLogger(DeviceAnalyticsOutputDAOImpl.class);

	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	protected DeviceService deviceService;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession(){
        return sessionFactory.openSession();
	}
	
	@Override
	public Integer getDeviceConfigurationId(String uuid) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String deviceConfigurationIdQuery = "select device_configuration_id from devices where uuid = ?" ;
		
		Query query = session.createSQLQuery(deviceConfigurationIdQuery);
		query.setParameter(0, uuid);
		
		Integer deviceConfigurationId = (Integer)query.uniqueResult();
		tx.commit();
		session.close();
		if(deviceConfigurationId != null && deviceConfigurationId.intValue() > 0){
			log.info("***deviceConfigurationId***"+"\t"+deviceConfigurationId);
			return deviceConfigurationId;
		}
		return 0;
	}
	
	@Override
	public String getDeviceModel(Integer device_configuration_id) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String deviceModelQuery = "select device_model as device_model from device_configurations where device_configuration_id = ?" ;
		
		Query query = session.createSQLQuery(deviceModelQuery);
		query.setParameter(0, device_configuration_id);
		
		String device_model_name = (String)query.uniqueResult();
		tx.commit();
		session.close();
		if(device_model_name != null){
			log.info("***deviceConfigurationId***"+"\t"+device_model_name);
			return device_model_name;
		}
		return null;
	}
	
	@Override
	public DeviceConfigurationsModel getDeviceConfigurations(int device_configuration_id) {
		// TODO Auto-generated method stub
		Session session = null;
		Transaction tx = null;
		StringBuilder deviceConfigBuilder = null;
		String deviceconfigurationsQuery = null;
		DeviceConfigurationsModel deviceConfigurationsModel = null;
				
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction(); 
			
			deviceConfigBuilder = new StringBuilder();
			
			deviceConfigBuilder.append("SELECT latest_config.device_configuration_id as device_configuration_id, ")
			.append("latest_config.firmware_size as size_mb, latest_config.firmware_name as name, ")
			.append("latest_config.firmware_version as version, latest_config.firmware_file as file_name ")
			.append("FROM device_configurations AS current_config, device_configurations AS latest_config ")
			.append("WHERE current_config.device_configuration_id = ? AND current_config.device_model = latest_config.device_model ")
			.append("AND current_config.device_configuration_id != latest_config.device_configuration_id ")
			.append("AND latest_config.created_date >= current_config.created_date ")
			.append("ORDER BY latest_config.created_date DESC LIMIT 1 ");
			
			deviceconfigurationsQuery = deviceConfigBuilder.toString();
			
			/*String deviceconfigurationsQuery = "SELECT latest_config.device_configuration_id as device_configuration_id, latest_config.firmware_name as name, latest_config.firmware_version as version,"
			+ " latest_config.firmware_size as size_mb, latest_config.low_battery as low_battery_percent, latest_config.gps_report_frequency as gps_report_min, "
			+ " latest_config.device_self_testing_version as device_self_test_hrs,latest_config.wearable_sync_frequency as wearable_sync_hrs, "
			+ " latest_config.firmware_file as file_name FROM device_configurations AS current_config, device_configurations AS latest_config"
			+ " WHERE current_config.device_configuration_id = ? AND current_config.device_model = latest_config.device_model"
			+ " AND current_config.device_configuration_id != latest_config.device_configuration_id"
			+ " AND latest_config.created_date >= current_config.created_date"
			+ " ORDER BY latest_config.created_date DESC LIMIT 1";*/
	
			Query query = session.createSQLQuery(deviceconfigurationsQuery)
						.addScalar("device_configuration_id").addScalar("name").addScalar("version")
						.addScalar("size_mb").addScalar("file_name")
						.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
						.setResultTransformer(Transformers.aliasToBean(DeviceConfigurationsModel.class));
	
			query.setParameter(0, device_configuration_id);
			deviceConfigurationsModel = (DeviceConfigurationsModel)query.uniqueResult();
			tx.commit();
		}catch(Exception e) {
			log.info("Exception Occured in getDeviceConfigurations ()" +"\t"+ e);
			tx.rollback();
		}finally {
			session.close();
		}
		if(deviceConfigurationsModel != null){
			log.info("***deviceConfigurationId***"+"\t"+device_configuration_id);
			return deviceConfigurationsModel;
		}
		return null;
	}
	
	@Override
	public DeviceConfigurationsModel getWearableDeviceConfigSettings(int deviceConfigId) {
		log.info("getWearableDeviceConfigSettings ++");
		Session session = null;
		Transaction tx = null;
		DeviceConfigurationsModel deviceConfig =  null;
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			String SQL_QUERY = "SELECT low_battery, gps_report_frequency, device_self_testing_version,"
					+ " wearable_sync_frequency FROM device_configurations "
					+ "WHERE device_configuration_id = :devConfigId";
			Query query = session.createSQLQuery(SQL_QUERY);
			query.setParameter("devConfigId", deviceConfigId);
			List<Object[]> row = query.list();
			for (Object[] list : row) {
				deviceConfig = new DeviceConfigurationsModel();
				try {
					if (null != list[0]) {
						deviceConfig.setLow_battery_percent(Integer.parseInt(list[0].toString()));
					}
					if (null != list[1]) {
						deviceConfig.setGps_report_min(Integer.parseInt(list[1].toString()));
					}
					if (null != list[2]) {
						deviceConfig.setDevice_self_test_hrs(Integer.parseInt(list[2].toString()));
					}
					if (null != list[3]) {
						deviceConfig.setWearable_sync_hrs(Integer.parseInt(list[3].toString()));
					}
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null != tx) {
				tx.commit();
			}
			if(null != session) {
				session.close();
			}
		}
		return deviceConfig;
	}

}
