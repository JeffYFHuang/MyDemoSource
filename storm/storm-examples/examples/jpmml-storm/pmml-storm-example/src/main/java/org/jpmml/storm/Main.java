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
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.StormSubmitter;
import org.apache.storm.drpc.DRPCSpout;
import org.apache.storm.drpc.ReturnResults;
import org.apache.storm.generated.DistributedRPCInvocations;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.shade.org.json.simple.JSONValue;
import org.apache.storm.shade.org.json.simple.parser.ParseException;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.minlog.Log;

public class Main {

	public static final Logger LOG = LoggerFactory.getLogger(ReturnResults.class);
	
	public static class ExclaimBolt extends BaseBasicBolt {
	    public void execute(Tuple tuple, BasicOutputCollector collector) {
	    	//Log.debug(tuple.toString());
	        String input = tuple.getString(0);
	        //System.out.println(tuple.getString(0)+tuple.getString(1));
	        //String sentence = tuple.getString(0);
	        String[] words = input.split("\t");
	        //System.out.println(words[1]);
	        collector.emit(new Values(words[1], tuple.getString(1)));
	    }

	    public void declareOutputFields(OutputFieldsDeclarer declarer) {
	        declarer.declare(new Fields("id", "result"));
	    }
	}
	
	static
	public void main(String... args) throws Exception {

/*		if(args.length != 3){
			System.err.println("Usage: java " + Main.class.getName() + " <PMML file> <Input CSV file> <Output CSV file>");

			System.exit(-1);
		}
*/
		Evaluator evaluator = PMMLBoltUtil.createEvaluator(new File(args[0]));

		PMMLBolt pmmlBolt = new PMMLBolt(evaluator);

		List<FieldName> inputFields = new ArrayList<>();
		inputFields.addAll(evaluator.getActiveFields());

//		CsvReaderSpout csvReader = new CsvReaderSpout(new File(args[1]), inputFields);

		List<FieldName> outputFields = new ArrayList<>();
		outputFields.addAll(evaluator.getTargetFields());
		outputFields.addAll(evaluator.getOutputFields());

		TopologyBuilder builder = new TopologyBuilder();

//		LocalDRPC drpc = new LocalDRPC();
		DRPCSpout spout = new DRPCSpout("drpcFunc");//, drpc);

//		CsvWriterBolt csvWriter = new CsvWriterBolt(new File(args[2]), outputFields);

		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout("drpc", spout, 8);
		topologyBuilder.setBolt("pmml", pmmlBolt, 16)//new ExclaimBolt())
			.shuffleGrouping("drpc");
		topologyBuilder.setBolt("return", new ReturnResults(), 16)
			.shuffleGrouping("pmml");

		Config config = new Config();
		config.setDebug(false);

		StormTopology topology = topologyBuilder.createTopology();

/*		LocalCluster localCluster = new LocalCluster();
		localCluster.submitTopology("example", config, topology);

		System.out.println(drpc.execute("drpcFunc", "hello\tworld!"));
		//Utils.sleep(60L * 1000L);

		localCluster.killTopology("example");
		localCluster.shutdown();
*/
		config.setNumWorkers(8);

		StormSubmitter.submitTopology("drpc-test", config, topology);

	}
}