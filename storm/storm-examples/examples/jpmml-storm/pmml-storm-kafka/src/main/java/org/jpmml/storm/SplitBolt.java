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

import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.task.TopologyContext;

public class SplitBolt implements IRichBolt {
   private OutputCollector collector;
   
   @Override
   public void prepare(Map stormConf, TopologyContext context,
      OutputCollector collector) {
      this.collector = collector;
   }
   
   @Override
   public void execute(Tuple input) {
      String sentence = input.getString(0);
      System.out.println(sentence);
      String[] words = sentence.split(" ");
      
      for(String word: words) {
         word = word.trim();
         
         if(!word.isEmpty()) {
            word = word.toLowerCase();
            collector.emit(new Values(word));
         }
         
      }

      collector.ack(input);
   }
   
   @Override
   public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word"));
   }

   @Override
   public void cleanup() {}
   
   @Override
   public Map<String, Object> getComponentConfiguration() {
      return null;
   }
   
}