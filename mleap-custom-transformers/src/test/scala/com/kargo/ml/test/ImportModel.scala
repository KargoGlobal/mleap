package com.kargo.ml.test

import com.kargo.ml.test.helpers.MLeapHelper.Titanic
import com.kargo.ml.test.helpers.{MLeapHelper, SparkHelper}

object ImportModel extends App {

  val spark = SparkHelper.initializeSpark("importing mleap")
  import spark.implicits._
  val testDf = spark.read.option("header", "true").option("inferSchema", "true").csv("data/test.csv")
  // handle missing values
  val meanValue = testDf.agg(Map("Age" -> "mean")).first().getDouble(0)
  val fareMeanValue = testDf.agg(Map("Fare" -> "mean")).first.getDouble(0)
  val dataToPredict = testDf.na.fill(meanValue, Array("age")).na.fill(fareMeanValue, Array("Fare")).cache

  MLeapHelper.importToSpark(dataToPredict)

  val dataToPredictMleap = dataToPredict.as[Titanic].take(20)
  spark.stop()
  println("--------------------------------------------------------------------------------------")
  println("------------------------------Stopped Spark Context-----------------------------------")
  println("--------------------------------------------------------------------------------------")
  MLeapHelper.importToMleap(dataToPredictMleap)

}
