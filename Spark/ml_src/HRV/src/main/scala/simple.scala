import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification._
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorAssembler}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.SQLContext._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.tuning.CrossValidator
import org.apache.spark.ml.tuning.ParamGridBuilder

object SimpleApp {
  def main(args: Array[String]) {
    val hrvPath = "hdfs://master:9000/user/hduser/output2/part-00000" // Should be some file on your system
    val conf = new SparkConf().setAppName("Simple Application")
    val sc = new SparkContext(conf)
    // Load and parse the data file.
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val data = sqlContext.read.json(hrvPath)

    val labelIndexer = new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(data)

    val s = Set("startTime", "endTime", "label", "SDANN")
    val features = data.columns.filter(s.contains(_) == false)

    val vectorAssembler = new VectorAssembler().setInputCols(features).setOutputCol("featureVector")

    val rfClassifier = new RandomForestClassifier().setLabelCol("indexedLabel").setFeaturesCol("featureVector").setNumTrees(10)

    val labelConverter = new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)

    val splits = data.randomSplit(Array(0.75, 0.25))
    val (trainingData, testData) = (splits(0), splits(1))

    val pipeline = new Pipeline().setStages(Array(labelIndexer,vectorAssembler,rfClassifier,labelConverter))
    val paramGrid = new ParamGridBuilder().build
    val cv = new CrossValidator().setNumFolds(10).setEstimator(pipeline).setEvaluator(new BinaryClassificationEvaluator).setEstimatorParamMaps(paramGrid)

    val model = cv.fit(trainingData)

    val predictionResultDF = model.transform(testData)

    val evaluator = new MulticlassClassificationEvaluator().setLabelCol("label").setPredictionCol("prediction").setMetricName("accuracy")
    val predictionAccuracy = evaluator.evaluate(predictionResultDF)
    println("Testing Error = " + (1.0 - predictionAccuracy))

    //val randomForestModel = model.stages(2).asInstanceOf[RandomForestClassificationModel]
    //println("Trained Random Forest Model is:\n" + randomForestModel.toDebugString)
  }
}
