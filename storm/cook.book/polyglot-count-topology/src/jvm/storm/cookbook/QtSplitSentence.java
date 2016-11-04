package storm.cookbook;

import java.util.Map;

import backtype.storm.task.ShellBolt;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;

public class QtSplitSentence extends ShellBolt implements IRichBolt {
    
	private static final long serialVersionUID = -2503812433333011106L;

	public QtSplitSentence() {
        super("splitsentence-cpp-bolt");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

}
