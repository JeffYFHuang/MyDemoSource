package storm.cookbook;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import storm.kafka.KafkaConfig.StaticHosts;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.builtin.Debug;
import storm.trident.operation.builtin.FilterNull;
import storm.trident.operation.builtin.MapGet;
import storm.trident.state.StateFactory;
import storm.trident.tuple.TridentTuple;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import com.github.quintona.KafkaState;
import com.github.quintona.KafkaStateUpdater;
import com.github.quintona.RFunction;

public class RecommendationTopology {
	
	public static class CoerceInFunction extends BaseFunction {

		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
	    	String text = new String(tuple.getBinary(0));
	    	JSONArray array = (JSONArray) JSONValue.parse(text);
	    	List<String> values = new ArrayList<String>(array.size()-1);
	    	String id = (String) array.get(0);
	    	array.remove(0);
	    	for(Object obj : array){
	    		values.add((String)obj);
	    	}
	    	if(array.size() > 0){
				collector.emit(new Values(id, values));
	    	}
		}
	}
	
	public static class CoerceOutFunction extends BaseFunction {
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
	    	JSONObject obj = new JSONObject();
	    	obj.put("transaction-id", tuple.getStringByField("transaction-id"));
	    	obj.put("recommendation", tuple.getStringByField("recommendation"));
	    	collector.emit(new Values(obj.toJSONString()));
		}
	}
	
	public static class ListRFunction extends RFunction {

		public ListRFunction(List<String> libraries, String functionName) {
			super(libraries, functionName);
		}
		
		@Override
		public void execute(TridentTuple tuple, TridentCollector collector) {
			List<String> items = (List<String>) tuple.get(0);
			JSONArray functionInput = new JSONArray();
			functionInput.addAll(items);
			JSONArray result = performFunction(functionInput);
			if(result != null)
				collector.emit(coerceResponce(result));
		}
		
	}

	public static TridentTopology makeTopology(int properyCount) throws IOException {
		TridentTopology topology = new TridentTopology();

		TridentKafkaConfig spoutConfig = new TridentKafkaConfig(
				StaticHosts.fromHostString(
						Arrays.asList(new String[] { "localhost" }), 2), "transactions");
		
		topology.newStream("kafka",
				new TransactionalTridentKafkaSpout(spoutConfig))
					.each(new Fields("bytes"), new CoerceInFunction(),new Fields("transaction-id","current-list"))
					.each(new Fields("current-list"), new ListRFunction(Arrays.asList(new String[] { "arules" }), 
							"recommend").withNamedInitCode("recommend"),
						new Fields("recommendation"))
					.each(new Fields("transaction-id", "recommendation"), 
							new Debug("Recommendation"))
					.each(new Fields("transaction-id", "recommendation"), 
							new CoerceOutFunction(),new Fields("message"))
					.partitionPersist(KafkaState.transactional("recommendation-output", 
							new KafkaState.Options()), new Fields("message"), 
							new KafkaStateUpdater("message"), new Fields());

		return topology;
	}

	public static void main(String[] args) throws Exception {
		Config conf = new Config();
		conf.setDebug(true);
		
		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);
			//TODO: get count from the args.
			StormSubmitter
					.submitTopology(args[0], conf, makeTopology(10).build());
		} else {
			conf.setMaxTaskParallelism(3);
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(new String[]{"127.0.0.1"}));
			conf.put(Config.STORM_ZOOKEEPER_PORT, 2181);
			conf.put(Config.STORM_ZOOKEEPER_ROOT, "/storm");
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("transactional-topology", conf,
					makeTopology(10).build());
		}
	}
}
