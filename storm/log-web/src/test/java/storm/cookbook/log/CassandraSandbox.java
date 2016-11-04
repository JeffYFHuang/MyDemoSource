package storm.cookbook.log;


import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Properties;

import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.MutationResult;
import me.prettyprint.hector.api.mutation.Mutator;

public class CassandraSandbox {

	protected Cluster cluster;
	protected Keyspace keyspace;
	protected Properties properties;

	StringSerializer stringSerializer = StringSerializer.get();
	LongSerializer longSerializer = LongSerializer.get();
	CompositeSerializer compositeSerializer = CompositeSerializer.get();
	
	private String keyspaceName;
	
	public void setup(){
		properties = new Properties();
		try {
			properties.load(CassandraSandbox.class
					.getResourceAsStream("/cassandra.properties"));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		cluster = HFactory.getOrCreateCluster(
				properties.getProperty("cluster.name", "DefaultCluster"),
				properties.getProperty("cluster.hosts", "127.0.0.1:9160"));
		ConfigurableConsistencyLevel ccl = new ConfigurableConsistencyLevel();
		ccl.setDefaultReadConsistencyLevel(HConsistencyLevel.ONE);

		keyspaceName = properties.getProperty("logging.keyspace",
				"Logging");
		
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(keyspaceName);
		
		if (keyspaceDef == null) {
			ColumnFamilyDefinition cfDef = HFactory
					.createColumnFamilyDefinition(properties.getProperty(
							"logging.keyspace", "Logging"), MINUTE_CF,
							ComparatorType.UTF8TYPE);
			cfDef.setDefaultValidationClass(ComparatorType.COUNTERTYPE
					.getClassName());
			cfDef.setColumnType(ColumnType.STANDARD);

			KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(
					keyspaceName, ThriftKsDef.DEF_STRATEGY_CLASS, 1,
					Arrays.asList(cfDef));
			cluster.addKeyspace(newKeyspace, true);

			keyspace = HFactory.createKeyspace(keyspaceName, cluster, ccl);
		}
		
		if (keyspace == null)
			keyspace = HFactory.createKeyspace(keyspaceName, cluster, ccl);
	}
	
	public void shutdown(){
		cluster.getConnectionManager().shutdown();
	}
	
	private static final String MINUTE_CF = "LogVolumeByMinute";
	
	public void testMinutes(String fileName, long count) {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.SECOND,0);
		today.set(Calendar.MILLISECOND,0);
		String key = Long.toString(today.getTimeInMillis());
		Mutator<String> mutator = HFactory.createMutator(keyspace,
				StringSerializer.get());
		mutator.incrementCounter(key, MINUTE_CF,
				"localhost: " + fileName, count);
		MutationResult mr = mutator.execute();
		System.out.println(mr.toString());

	}
	
	public static void main(String[] args) throws Exception {
		CassandraSandbox sandbox = new CassandraSandbox();
		sandbox.setup();
		
		String fileName1 = "/var/log/apache2/access.log";
		String fileName2 = "/var/log/sys.log";
		
		while(true){
			long amount1 = (long)(Math.random() * 10.0);
			long amount2 = (long)(Math.random() * 10.0);
			sandbox.testMinutes(fileName1, amount1);
			sandbox.testMinutes(fileName2, amount2);
			Thread.sleep(3000);
		}
	}

}
