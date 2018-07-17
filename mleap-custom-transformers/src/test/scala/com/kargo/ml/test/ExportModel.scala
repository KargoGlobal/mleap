package com.kargo.ml.test

import com.kargo.ml.test.helpers._
import com.typesafe.config.ConfigFactory
import ml.combust.bundle.BundleFile
import ml.combust.bundle.serializer.SerializationFormat
import ml.combust.mleap.spark.SparkSupport._
import org.apache.spark.ml.bundle.SparkBundleContext
import resource._

object ExportModel extends App {


  ConfigFactory.load("src/main/resources")
  val spark = SparkHelper.initializeSpark("mleap-test")

  val (sparkTransformed, model) = MLPipelineHelper.pipelineTitanicJob(spark)

  val testDf = spark.read.option("header", "true").option("inferSchema", "true").csv("data/test.csv")
  // handle missing values
  val meanValue = testDf.agg(Map("Age" -> "mean")).first().getDouble(0)
  val fareMeanValue = testDf.agg(Map("Fare" -> "mean")).first.getDouble(0)
  val dataToPredict = testDf.na.fill(meanValue, Array("age")).na.fill(fareMeanValue, Array("Fare")).cache

  val prediction = model.transform(dataToPredict)

  prediction.show(false)

  // Creating SparkBundleContext with our latest transformed dataset, this helps in serializing and getting all metadata for model.
  implicit val context = SparkBundleContext().withDataset(sparkTransformed)


  // serializing the model into JSON format and storing it to temporary file, after serializing we can directly load it to our serving model as well.
  (for (bundle <- managed(BundleFile(s"jar:file:${System.getProperty("user.dir")}/models/titanic-model-json.zip"))) yield {
    model.writeBundle.format(SerializationFormat.Json).save(bundle)(context).get
  }).tried.get

  spark.stop()

}
