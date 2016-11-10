package storm.cookbook;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

public class HelloWorldBolt extends BaseRichBolt {

    private int myCount = 0;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            // TODO Auto-generated method stub

    }

    @Override
    public void execute(Tuple input) {
            String test = input.getStringByField("sentence");
//            if("Hello World".equals(test)){
            myCount++;
                System.out.println("Found a Hello World! My Count is now@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@: "
                + test + Integer.toString(myCount));
//            }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
            // TODO Auto-generated method stub

    }

}
