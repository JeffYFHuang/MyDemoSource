package storm.cookbook;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.task.ShellBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTopology {

    public static class RBolt extends ShellBolt implements IRichBolt
    {
        public RBolt() {
           super("Rscript", "script.R");
        }
        
        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
          declarer.declare(new Fields("sentence"));
        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
          return null;
        }
    }

        public static void main(String args[]) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
                TopologyBuilder builder = new TopologyBuilder();
                builder.setSpout("randomHelloWorld", new HelloWorldSpout(), 10);
                builder.setBolt("RBolt", new RBolt(), 2)
                .shuffleGrouping("randomHelloWorld");

                builder.setBolt("HelloWorldBolt", new HelloWorldBolt(), 2)
                .shuffleGrouping("RBolt");

                Config conf = new Config();
                conf.setDebug(false);

                if(args!=null && args.length > 0) {
                   conf.setNumWorkers(4);

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
