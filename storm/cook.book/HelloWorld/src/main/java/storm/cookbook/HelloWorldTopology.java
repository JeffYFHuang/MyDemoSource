package storm.cookbook;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

public class HelloWorldTopology {
        public static void main(String args[]) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
                TopologyBuilder builder = new TopologyBuilder();
                builder.setSpout("randomHelloWorld", new
                HelloWorldSpout(), 10);
                builder.setBolt("HelloWorldBolt", new
                               HelloWorldBolt(), 2)
                .shuffleGrouping("randomHelloWorld");

                Config conf = new Config();
                conf.setDebug(true);

                if(args!=null && args.length > 0) {
                   conf.setNumWorkers(3);

                   StormSubmitter.submitTopology(args[0], conf,
                                                builder.createTopology());
                } else {
                   LocalCluster cluster = new LocalCluster();
                   cluster.submitTopology("test", conf,
                                         builder.createTopology());
                   Utils.sleep(10000);
                   cluster.killTopology("test");
                   cluster.shutdown();
                }
    }
}
