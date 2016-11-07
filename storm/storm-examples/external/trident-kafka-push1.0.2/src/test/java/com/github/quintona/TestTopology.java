package com.github.quintona;

import java.io.IOException;
import java.util.Arrays;

import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class TestTopology {
	
	public static class AppendFunction extends BaseFunction {

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			String text = new String(tuple.getBinary(0));
			collector.emit(new Values(text + "_end!"));
		}
	}

	public static TridentTopology makeTopology() throws IOException {
		TridentTopology topology = new TridentTopology();
		BrokerHosts hosts = new ZkHosts("localhost");
		TridentKafkaConfig spoutConfig = new TridentKafkaConfig(hosts, "master", "test");

		topology.newStream("kafka",
				new TransactionalTridentKafkaSpout(spoutConfig))
					.each(new Fields("bytes"), new AppendFunction(), new Fields("text"))
					.partitionPersist(KafkaState.transactional("test1", new KafkaState.Options()), new Fields("text"),new KafkaStateUpdater("text"));

		return topology;
	}

	public static void main(String[] args) throws Exception {
		Config conf = new Config();
		conf.setDebug(true);
		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);

			StormSubmitter
					.submitTopology(args[0], conf, makeTopology().build());
		} else {
			conf.setMaxTaskParallelism(3);
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(new String[]{"127.0.0.1"}));
			conf.put(Config.STORM_ZOOKEEPER_PORT, 2181);
			conf.put(Config.STORM_ZOOKEEPER_ROOT, "/storm");
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("test-kafka-push", conf,
					makeTopology().build());

			Thread.sleep(100000);

			cluster.shutdown();
		}
	}
}
