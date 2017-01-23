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

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.hdfs.common.rotation.MoveFileAction;
import org.apache.storm.hdfs.trident.HdfsState;
import org.apache.storm.hdfs.trident.HdfsStateFactory;
import org.apache.storm.hdfs.trident.HdfsUpdater;
import org.apache.storm.hdfs.trident.format.DefaultFileNameFormat;
import org.apache.storm.hdfs.trident.format.DefaultSequenceFormat;
import org.apache.storm.hdfs.trident.format.DelimitedRecordFormat;
import org.apache.storm.hdfs.trident.format.FileNameFormat;
import org.apache.storm.hdfs.trident.format.RecordFormat;
import org.apache.storm.hdfs.trident.rotation.FileRotationPolicy;
import org.apache.storm.hdfs.trident.rotation.FileSizeRotationPolicy;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.google.common.collect.ImmutableMap;

import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.kafka.trident.TridentKafkaStateFactory;
import org.apache.storm.kafka.trident.TridentKafkaUpdater;
import org.apache.storm.kafka.trident.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.trident.selector.DefaultTopicSelector;
import org.apache.storm.shade.org.json.simple.JSONArray;
import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.shade.org.json.simple.parser.JSONParser;
import org.apache.storm.shade.org.json.simple.parser.ParseException;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.StateFactory;
import org.apache.storm.trident.tuple.TridentTuple;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static class Split extends BaseFunction {
    	private Evaluator evaluator = null;
    	
        public void execute(TridentTuple tuple, TridentCollector collector) {
            String sentence = tuple.getString(0);
            String[] words = sentence.split("\t");
    		
            JSONParser parser = new JSONParser();
            try{
                Object obj = parser.parse(words[1]);
                JSONArray array = (JSONArray)obj;
                JSONArray result_array = new JSONArray();
                JSONObject results = new JSONObject();
    			System.out.println(words[0]);
                Values values = new Values();
                values.add(words[0]);
                for (int i=0; i < array.size(); i++) {
                    JSONObject jobj = (JSONObject)array.get(i);
                    Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

                    // get startTime
                    if (i == 0) {
                    	results.put("startTime", (jobj.get("startTime")).toString());
                    }
                    
        		    List<FieldName> activeFields = evaluator.getActiveFields();
        		    for (FieldName activeField : activeFields){
        		        FieldValue value = EvaluatorUtil.prepare(evaluator, activeField, jobj.get(activeField.getValue()));

        			    arguments.put(activeField, value);
        		    }
 
            		Map<FieldName, ?> result = evaluator.evaluate(arguments);

            	    JSONObject jobjr = new JSONObject();

            		List<FieldName> outputFields = evaluator.getOutputFields();
            		for(FieldName outputField : outputFields){
            			Object outputValue = result.get(outputField);
            			jobjr.put(outputField, outputValue);
            		}

            		result_array.add(jobjr);
                }
                
                results.put("apnea", result_array);
                values.add(results.toJSONString());
                collector.emit(values);
             }catch(ParseException pe){
       		
                System.out.println("position: " + pe.getPosition());
                System.out.println(pe);
             }
        }
        
    	public Split(Evaluator evaluator){
    		setEvaluator(evaluator);
    	}
    	
    	public Evaluator getEvaluator(){
    		return this.evaluator;
    	}

    	private void setEvaluator(Evaluator evaluator){
    		this.evaluator = evaluator;
    	}
    }

    private static StormTopology buildTopology(final String hdfsurl, final Evaluator evaluator) {
        Fields fields = new Fields("word", "count");

        String zookeeperHost = "master:2181";
        ZkHosts zkHosts = new ZkHosts(zookeeperHost);

        TridentKafkaConfig kafkaConfig = new TridentKafkaConfig(zkHosts, "hrvs", "flume-sink-group");
  //      SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, "beats", "/beats", "storm");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

  //      KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);
        TransactionalTridentKafkaSpout kafkaSpout = new TransactionalTridentKafkaSpout(kafkaConfig);

        TridentTopology topology = new TridentTopology();
        Stream stream = topology.newStream("spout", kafkaSpout)
                        .each(new Fields("str"), new Split(evaluator), new Fields("subject", "hrvs"));

        Fields hdfsFields = new Fields("subject", "hrvs");

        FileNameFormat fileNameFormat = new DefaultFileNameFormat()
                .withPath("/data/stormtest")
                .withExtension(".txt");

        RecordFormat recordFormat = new DelimitedRecordFormat()
        .withFieldDelimiter("\t")
        .withFields(hdfsFields);

        FileRotationPolicy rotationPolicy = new FileSizeRotationPolicy(5.0f, FileSizeRotationPolicy.Units.MB);

        HdfsState.Options options = new HdfsState.HdfsFileOptions()
            .withFileNameFormat(fileNameFormat)
            .withRecordFormat(recordFormat)
            .withRotationPolicy(rotationPolicy)
            .withFsUrl(hdfsurl)
            .withConfigKey("hdfs.config");

        StateFactory factory = new HdfsStateFactory().withOptions(options);

        TridentState state = stream
                .partitionPersist(factory, hdfsFields, new HdfsUpdater(), new Fields());
        
/*        Properties props = new Properties();
        props.put("bootstrap.servers", brokerConnectionString);
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        TridentKafkaStateFactory stateFactory = new TridentKafkaStateFactory()
            .withProducerProperties(props)
            .withKafkaTopicSelector(new DefaultTopicSelector("ktest"))
            .withTridentTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper("subject", "subject"));
        stream.partitionPersist(stateFactory, new Fields("subject", "hrvs"), new TridentKafkaUpdater(), new Fields());
*/
        return topology.build();
    }

    /**
     * To run this topology ensure you have a kafka broker running and provide connection string to broker as argument.
     * Create a topic test with command line,
     * kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic test
     *
     * run this program and run the kafka consumer:
     * kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning
     *
     * you should see the messages flowing through.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            System.out.println("Please provide kafka broker url ,e.g. localhost:9092");
        }

        Evaluator evaluator = PMMLBoltUtil.createEvaluator(new File("rf.pmml"));
 
        Config conf = new Config();
        conf.setDebug(true);
        conf.put("nimbus.thrift.max_buffer_size", 1121400710);
        if (args != null && args.length > 1) {
           conf.setNumWorkers(3);

           StormSubmitter.submitTopologyWithProgressBar("test", conf, buildTopology(args[0], evaluator));
        } else {
           LocalCluster cluster = new LocalCluster();
           cluster.submitTopology("wordCounter", new Config(), buildTopology(args[0], evaluator));
           Thread.sleep(60 * 1000);
           cluster.killTopology("wordCounter");

           cluster.shutdown();
        }
    }
}