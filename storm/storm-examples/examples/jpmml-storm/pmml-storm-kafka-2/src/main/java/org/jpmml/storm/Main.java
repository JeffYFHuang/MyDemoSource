/*
 * Copyright (c) 2014 Villu Ruusmann
 *
 * This file is part of JPMML-Storm
 *
 * JPMML-Storm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-Storm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-Storm.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.storm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.hdfs.bolt.HdfsBolt;
import org.apache.storm.hdfs.bolt.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.bolt.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.bolt.format.FileNameFormat;
import org.apache.storm.hdfs.bolt.format.RecordFormat;
import org.apache.storm.hdfs.bolt.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.FileSizeRotationPolicy.Units;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy;
import org.apache.storm.hdfs.bolt.rotation.TimedRotationPolicy.TimeUnit;
import org.apache.storm.hdfs.bolt.sync.CountSyncPolicy;
import org.apache.storm.hdfs.bolt.sync.SyncPolicy;
import org.apache.storm.hdfs.common.rotation.MoveFileAction;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;

public class Main {
    public static void main(String[] args) throws Exception {
/*		if(args.length != 2){
			System.err.println("Usage: java " + Main.class.getName() + " <PMML file> <Output CSV file>");

			System.exit(-1);
		}
*/		
        // Configuration

        String zookeeperHost = "master:2181";
        ZkHosts zkHosts = new ZkHosts(zookeeperHost);

        SpoutConfig spoutConfig = new SpoutConfig(zkHosts, "hrvs", "", UUID.randomUUID().toString());
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

        KafkaSpout kafkaspout = new KafkaSpout(spoutConfig);

		Evaluator evaluator = PMMLBoltUtil.createEvaluator(new File(args[0]));

		PMMLBolt pmmlBolt = new PMMLBolt(evaluator);
		Fields hdfsFields = new Fields("subject", "apnea");
		// use "|" instead of "," for field delimiter
		RecordFormat format = new DelimitedRecordFormat()
		        .withFields(hdfsFields)
		        .withFieldDelimiter("\t");

		// sync the filesystem after every 1k tuples
		SyncPolicy syncPolicy = new CountSyncPolicy(2);

		// rotate files when they reach 5MB
//		FileRotationPolicy rotationPolicy = new TimedRotationPolicy(1.0f, TimeUnit.MINUTES);
		FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(10.0f, Units.MB);

		FileNameFormat fileNameFormat = new DefaultFileNameFormat()
		        .withPath("/data/stormtest/").withPrefix("terminalInfo_").withExtension(".log");

		HdfsBolt hdfsbolt = new HdfsBolt()
		        .withFsUrl("hdfs://10.0.0.5:9000")
		        .withFileNameFormat(fileNameFormat)
		        .withRecordFormat(format)
		        .withRotationPolicy(rotationPolicy)
		        .withSyncPolicy(syncPolicy);
//		        .addRotationAction(new MoveFileAction().toDestination("/data/stormtest/"));

/*		List<FieldName> outputFields = new ArrayList<>();
		outputFields.add(new FieldName("subject"));
		outputFields.add(new FieldName("apnea"));
//		outputFields.addAll(evaluator.getTargetFields());
//		outputFields.addAll(evaluator.getOutputFields());
		CsvWriterBolt csvWriter = new CsvWriterBolt(new File(args[1]), outputFields);
*/
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout("input", kafkaspout, 1);
		topologyBuilder.setBolt("pmml", pmmlBolt, 4)
			.shuffleGrouping("input");
		//topologyBuilder.setBolt("csvWriter", csvWriter, 2)
		//.shuffleGrouping("pmml");
		topologyBuilder.setBolt("hdfsbolt", hdfsbolt, 2)
			.shuffleGrouping("pmml");

		Config config = new Config();
		config.setDebug(true);

		StormTopology topology = topologyBuilder.createTopology();
		
        if (args != null && args.length > 1) {
	           config.setNumWorkers(3);

	           StormSubmitter.submitTopologyWithProgressBar(args[1], config, topology);
	    } else {
	           LocalCluster cluster = new LocalCluster();
	           cluster.submitTopology("prediction", new Config(), topology);
	           Thread.sleep(60 * 1000);
	           cluster.killTopology("prediction");

	           cluster.shutdown();
	    }
    }
}