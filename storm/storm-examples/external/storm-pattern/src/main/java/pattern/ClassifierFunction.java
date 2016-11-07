/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package pattern;

import java.util.Map;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class ClassifierFunction extends BaseFunction {
	public Classifier classifier;

	private String pmmlPath;

	/**
	 * @param pmmlPath
	 *            PMML file
	 */
	public ClassifierFunction(String pmmlPath) {
		this.pmmlPath = pmmlPath;
	}

	/**
	 * @param flowProcess
	 * @param operationCall
	 */
	@Override
	public void prepare(Map conf, TridentOperationContext context) {

		this.classifier = new Classifier(pmmlPath);
		classifier.prepare();
	}

	/**
	 * @param flowProcess
	 * @param functionCall
	 */
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {

		String label = classifier.classifyTuple(tuple);
		collector.emit(new Values(label));
	}

	/**
	 * Returns a Fields data structure naming the input tuple fields.
	 * 
	 * @return Fields
	 */
	public Fields getInputFields() {
		return classifier.model.schema.getInputFields();
	}

	/**
	 * Returns a String naming the predictor tuple fields.
	 * 
	 * @return
	 */
	public String getPredictor() {
		return classifier.model.schema.label_field.name;
	}
}
