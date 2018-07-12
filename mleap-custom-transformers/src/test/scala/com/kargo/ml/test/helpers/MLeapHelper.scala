package com.kargo.ml.test.helpers

import ml.combust.bundle.BundleFile
import ml.combust.mleap.core.types._
import ml.combust.mleap.runtime.MleapSupport._
import ml.combust.mleap.runtime.frame.{DefaultLeapFrame, Row}
import ml.combust.mleap.spark.SparkSupport._
import org.apache.spark.sql.DataFrame
import resource._

object MLeapHelper {

  val cols = Array("PassengerId", "Pclass", "Name", "Sex", "Age", "Fare", "score", "rawScore", "prediction")

  def importToSpark(testDF: DataFrame): Unit = {

    val zipBundle = (for (bundle <- managed(BundleFile(s"jar:file:${System.getProperty("user.dir")}/models/titanic-model-json.zip"))) yield {
      bundle.loadSparkBundle().get // gets spark bundle from serialized model.
    }).opt.get

    val loadedModel = zipBundle.root

    val predictionTitanicData = loadedModel.transform(testDF)

    predictionTitanicData.show(20, false)

  }

  def importToMleap(testDataToPredict: Array[Titanic]): Unit = {

    val data = testDataToPredict.map(x => Row(x.PassengerId, x.Pclass, x.Name, x.Sex, x.Age, x.SibSp, x.Parch, x.Ticket, x.Fare, x.Cabin, x.Embarked))
    val schema = createSchema()
    // Create a LeapFrame using schema and test data of 20 Rows
    val frame = DefaultLeapFrame(schema, data)

    val zipBundleM = (for (bundle <- managed(BundleFile(s"jar:file:${System.getProperty("user.dir")}/models/titanic-model-json.zip"))) yield {
      bundle.loadMleapBundle().get
    }).opt.get

    // Get the mLeap Pipeline/model
    val mleapPipeline = zipBundleM.root

    val mleapPrediction = mleapPipeline.transform(frame).get

    mleapPrediction.select(cols: _*).get.show(20)

  }

  def createSchema(): StructType = {
    // Create a Schema for LeapFrame based on our data
    val schema = StructType(StructField("PassengerId", ScalarType.Int),
      StructField("Pclass", ScalarType.Int),
      StructField("Name", ScalarType.String),
      StructField("Sex", ScalarType.String),
      StructField("Age", ScalarType.Double),
      StructField("SibSp", ScalarType.Int),
      StructField("Parch", ScalarType.Int),
      StructField("Ticket", ScalarType.String),
      StructField("Fare", ScalarType.Double),
      StructField("Cabin", ScalarType.String),
      StructField("Embarked", ScalarType.String)).get
    schema
  }

  case class Titanic(PassengerId: Int, Pclass: Int, Name: String, Sex: String, Age: Double, SibSp: Int, Parch: Int, Ticket: String, Fare: Double, Cabin: String, Embarked: String)

}
