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

import java.util.Map;
import java.util.HashMap;

import org.apache.storm.tuple.Tuple;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.task.TopologyContext;

public class CountBolt implements IRichBolt{
   Map<String, Integer> counters;
   private OutputCollector collector;
   
   @Override
   public void prepare(Map stormConf, TopologyContext context,
   OutputCollector collector) {
      this.counters = new HashMap<String, Integer>();
      this.collector = collector;
   }

   @Override
   public void execute(Tuple input) {
      String str = input.getString(0);
      
      if(!counters.containsKey(str)){
         counters.put(str, 1);
      }else {
         Integer c = counters.get(str) +1;
         counters.put(str, c);
      }
   
      collector.ack(input);
   }

   @Override
   public void cleanup() {
      for(Map.Entry<String, Integer> entry:counters.entrySet()){
         System.out.println(entry.getKey()+" : " + entry.getValue());
      }
   }

   @Override
   public void declareOutputFields(OutputFieldsDeclarer declarer) {
   
   }

   @Override
   public Map<String, Object> getComponentConfiguration() {
      return null;
   }
}