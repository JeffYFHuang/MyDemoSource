package storm.cookbook;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2012/12/07
 * Time: 8:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeographyBolt extends BaseRichBolt {


    private IPResolver resolver;

    private OutputCollector collector;

    public GeographyBolt(IPResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String ip = tuple.getStringByField(storm.cookbook.Fields.IP);
        JSONObject json = resolver.resolveIP(ip);
        String city = (String) json.get(storm.cookbook.Fields.CITY);
        String country = (String) json.get(storm.cookbook.Fields.COUNTRY_NAME);
        collector.emit(new Values(country, city));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields(storm.cookbook.Fields.COUNTRY, storm.cookbook.Fields.CITY));
    }
}
