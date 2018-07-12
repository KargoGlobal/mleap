package com.kargo.ml.test.helpers

import org.apache.spark.SparkConf
import org.apache.spark.ml.PipelineStage
import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer}
import org.apache.spark.sql.SparkSession

object SparkHelper {


  def initializeSpark(jobName: String): SparkSession  = {

    val config = new SparkConf().setMaster("local[*]").setAppName(jobName)
    val spark = SparkSession.builder().config(config).getOrCreate()
    spark
  }

  def handleCategorical(column: String): Array[PipelineStage] = {
    val stringIndexer = new StringIndexer().setInputCol(column)
      .setOutputCol(s"${column}_index")
      .setHandleInvalid("skip")
    val oneHot = new OneHotEncoder().setInputCol(s"${column}_index").setOutputCol(s"${column}_onehot")
    Array(stringIndexer, oneHot)
  }

}
