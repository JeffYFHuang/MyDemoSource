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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.storm.shade.org.json.simple.JSONArray;
import org.apache.storm.shade.org.json.simple.JSONObject;
import org.apache.storm.shade.org.json.simple.parser.JSONParser;
import org.apache.storm.shade.org.json.simple.parser.ParseException;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.EvaluatorUtil;
import org.jpmml.evaluator.FieldValue;

public class PMMLBolt extends BaseRichBolt{
	private ConcurrentHashMap<String, Integer> processing;
	
	private Evaluator evaluator = null;

	private OutputCollector collector = null;

	public PMMLBolt(Evaluator evaluator){
		setEvaluator(evaluator);
	}

	@Override
	public void prepare(Map configuration, TopologyContext context, OutputCollector collector){
		setCollector(collector);
		this.processing = new ConcurrentHashMap<String, Integer>();
	}
/*
	@Override
	public void execute(Tuple tuple){
		Evaluator evaluator = getEvaluator();

		Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

		List<FieldName> activeFields = evaluator.getActiveFields();
		for(FieldName activeField : activeFields){
			FieldValue value = EvaluatorUtil.prepare(evaluator, activeField, tuple.getValueByField(activeField.getValue()));

			arguments.put(activeField, value);
		}

		Map<FieldName, ?> result = evaluator.evaluate(arguments);

		Values values = new Values();

		List<FieldName> targetFields = evaluator.getTargetFields();
		for(FieldName targetField : targetFields){
			Object targetValue = result.get(targetField);

			values.add(EvaluatorUtil.decode(targetValue));
		}

		List<FieldName> outputFields = evaluator.getOutputFields();
		for(FieldName outputField : outputFields){
			Object outputValue = result.get(outputField);

			values.add(outputValue);
		}

		OutputCollector collector = getCollector();

		collector.emit(tuple, values);
		collector.ack(tuple);
	}
*/
	@Override
    public void execute(Tuple tuple) {
		Evaluator evaluator = getEvaluator();
		OutputCollector collector = getCollector();
		
        String sentence = tuple.getString(0);
        String[] words = sentence.split("\t");
		
        String process_key = null;
        JSONParser parser = new JSONParser();
        try{
            Object obj = parser.parse(words[1]);
            JSONArray array = (JSONArray)obj;
            JSONArray result_array = new JSONArray();
            JSONObject results = new JSONObject();
			//System.out.println(words[0]);
            Values values = new Values();
            values.add(words[0]);
            for (int i=0; i < array.size(); i++) {
                JSONObject jobj = (JSONObject)array.get(i);
                Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

                // get startTime
                if (i == 0) {
                	results.put("startTime", (jobj.get("startTime")).toString());
                	process_key = new String(words[0]+(jobj.get("startTime")).toString());
                	this.processing.put(process_key, 0);
                }
                
    		    List<FieldName> activeFields = evaluator.getActiveFields();
    		    for (FieldName activeField : activeFields){
    		        FieldValue value = EvaluatorUtil.prepare(evaluator, activeField, jobj.get(activeField.getValue()));

    			    arguments.put(activeField, value);
    		    }

        		Map<FieldName, ?> result = evaluator.evaluate(arguments);

        	    JSONObject jobjr = new JSONObject();

        		List<FieldName> targetFields = evaluator.getTargetFields();
        		for(FieldName targetField : targetFields){
        			Object targetValue = result.get(targetField);

        			jobjr.put(targetField, EvaluatorUtil.decode(targetValue));
        		}

        		List<FieldName> outputFields = evaluator.getOutputFields();
        		for(FieldName outputField : outputFields){
        			Object outputValue = result.get(outputField);
        			jobjr.put(outputField, outputValue);
        		}

        		result_array.add(jobjr);
            }
            
            results.put("apnea", result_array);
            values.add(results.toJSONString());
            
            //System.out.println(results.toJSONString());
            if (this.processing.get(process_key) == 0) {
    		   collector.emit(tuple, values);
    		   collector.ack(tuple);
    		   this.processing.put(process_key, this.processing.get(process_key) + 1);
            }
         }catch(ParseException pe){
        	collector.ack(tuple);
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
         }

         //collector.ack(tuple);
    }
    
	@Override
	public void cleanup(){
		super.cleanup();
	}

/*	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer){
		Evaluator evaluator = getEvaluator();

		List<String> fields = new ArrayList<>();

		List<FieldName> targetFields = evaluator.getTargetFields();
		for(FieldName targetField : targetFields){
			fields.add(targetField.getValue());
		}

		List<FieldName> outputFields = evaluator.getOutputFields();
		for(FieldName outputField : outputFields){
			fields.add(outputField.getValue());
		}

		declarer.declare(new Fields(fields));
	}
*/
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer){
		Fields hdfsFields = new Fields("subject", "apnea");
		declarer.declare(hdfsFields);
	}
	
	public Evaluator getEvaluator(){
		return this.evaluator;
	}

	private void setEvaluator(Evaluator evaluator){
		this.evaluator = evaluator;
	}

	public OutputCollector getCollector(){
		return this.collector;
	}

	private void setCollector(OutputCollector collector){
		this.collector = collector;
	}
}