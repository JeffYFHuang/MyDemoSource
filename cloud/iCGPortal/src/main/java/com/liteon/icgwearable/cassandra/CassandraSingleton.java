package com.liteon.icgwearable.cassandra;

import java.util.Properties;

import org.apache.log4j.Logger;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.liteon.icgwearable.dao.CassandraDAOImpl;
import com.liteon.icgwearable.util.PropertiesUtil;

public class CassandraSingleton {
	
	Properties properties = PropertiesUtil.getProperties();
	private String cassandraKeySpace= properties.getProperty("cassendra.keyspace");
	private String cassandraUN =properties.getProperty("cassandra.username");
	private String cassandraPassword =properties.getProperty("cassandra.password");
	private String node=properties.getProperty("cassendra.host");
	
	private Logger log = Logger.getLogger(CassandraDAOImpl.class);
	private static CassandraSingleton instance;

	private CassandraSingleton() {
	}

	public static CassandraSingleton getInstanceUsingDoubleLocking() {
		if (instance == null) {
			synchronized (CassandraSingleton.class) {
				if (instance == null) {
					instance = new CassandraSingleton();
				}
			}
		}
		return instance;
	}
	
	public Session getCassandraSession() {
		log.info("node"+"\t"+node);
		Cluster cluster = Cluster.builder().addContactPoint(node).withCredentials(cassandraUN, cassandraPassword).build();
		return cluster.connect(cassandraKeySpace);
	}

}