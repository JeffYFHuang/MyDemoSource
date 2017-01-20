/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.kafka;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import com.google.common.collect.ImmutableMap;

import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.kafka.trident.TridentKafkaStateFactory;
import org.apache.storm.kafka.trident.TridentKafkaUpdater;
import org.apache.storm.kafka.trident.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.trident.selector.DefaultTopicSelector;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.testing.FixedBatchSpout;
import org.apache.storm.trident.tuple.TridentTuple;

import java.util.Properties;

public class TridentKafkaTopology {
    public static class Split extends BaseFunction {
        public void execute(TridentTuple tuple, TridentCollector collector) {
            String sentence = tuple.getString(0);
            String[] words = sentence.split("\t");
/*          List<Object> values = new ArrayList<Object>(array.length);
            for(String word: sentence.split("\t")) {
                values.add(word)
                collector.emit(new Values(word));
            }
*/
            collector.emit(new Values(words));
        }
    }

    private static StormTopology buildTopology(String brokerConnectionString) {
        Fields fields = new Fields("word", "count");
        /*        FixedBatchSpout spout = new FixedBatchSpout(fields, 4,
                new Values("storm", "1"),
                new Values("trident", "1"),
                new Values("needs", "1"),
                new Values("javadoc", "1")
        );
        spout.setCycle(true);
*/
        String zookeeperHost = "master:2181";
        ZkHosts zkHosts = new ZkHosts(zookeeperHost);

        TridentKafkaConfig kafkaConfig = new TridentKafkaConfig(zkHosts, "hrvs");
  //      SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, "beats", "/beats", "storm");
        kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

  //      KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);
        TransactionalTridentKafkaSpout kafkaSpout = new TransactionalTridentKafkaSpout(kafkaConfig);

        TridentTopology topology = new TridentTopology();
        Stream stream = topology.newStream("spout1", kafkaSpout)
                        .each(new Fields("str"), new Split(), new Fields("subject", "hrvs"));

        Properties props = new Properties();
        props.put("bootstrap.servers", brokerConnectionString);
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        TridentKafkaStateFactory stateFactory = new TridentKafkaStateFactory()
            .withProducerProperties(props)
            .withKafkaTopicSelector(new DefaultTopicSelector("ktest"))
            .withTridentTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper("subject", "subject"));
        stream.partitionPersist(stateFactory, new Fields("subject", "hrvs"), new TridentKafkaUpdater(), new Fields());

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

        Config conf = new Config();
        conf.setDebug(true);
        if (args != null && args.length > 0) {
           conf.setNumWorkers(3);

           StormSubmitter.submitTopologyWithProgressBar("test", conf, buildTopology(args[0]));
        } else {
           LocalCluster cluster = new LocalCluster();
           cluster.submitTopology("wordCounter", new Config(), buildTopology("data1:9092,data2:9092"));
           Thread.sleep(60 * 1000);
           cluster.killTopology("wordCounter");

           cluster.shutdown();
        }
    }
}
