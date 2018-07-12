package com.kargo.ml.test.helpers

import com.kargo.ml.core.features.ClassifierPredictionModel
import com.kargo.ml.spark.transformers.ClassifierPredictionExtractor
import com.kargo.ml.utils.Constants
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.{DataFrame, SparkSession}

object MLPipelineHelper {


  def pipelineTitanicJob(spark: SparkSession): (DataFrame, PipelineModel) = {
    val df = spark.read.option("header", "true").option("inferSchema", "true").csv("data/train.csv")

    // handle those missing values
    val meanValue = df.agg(Map("Age" -> "mean")).first().getDouble(0)
    val fixedDf = df.na.fill(meanValue, Array("Age"))

    // split the data into 70% and 30% and rename survived column to label
    val dfs = fixedDf.randomSplit(Array(0.7, 0.3))
    val trainDf = dfs(0).withColumnRenamed("Survived", "label")
    val crossDf = dfs(1)


    // categories cosnidering as features.
    val genderStages = SparkHelper.handleCategorical("Sex")
    val embarkedStages = SparkHelper.handleCategorical("Embarked")
    val pClassStages = SparkHelper.handleCategorical("Pclass")

    // features and creating Vectors of features
    val cols = Array("Sex_onehot", "Embarked_onehot", "Pclass_onehot", "SibSp", "Parch", "Age", "Fare")
    val outPutCols = Array("PassengerId", "rawPrediction", "probability", "prediction")
    val vectorAssembler = new VectorAssembler().setInputCols(cols).setOutputCol("features")

    val probabiltyExtractor = new ClassifierPredictionExtractor(s"score_${Constants.Transformer.CLASSIFIER_TRANSFORMER}", new ClassifierPredictionModel)
      .setInputCol("probability")
      .setOutputCol(Constants.Columns.SCORE)
      .setPredictionCol("prediction")
      .setProbabilityCol("probability")
      .setRawPredictionCol("rawPrediction")

    val rawScoreExtractor = new ClassifierPredictionExtractor(s"rawScore_${Constants.Transformer.CLASSIFIER_TRANSFORMER}", new ClassifierPredictionModel)
      .setInputCol("rawPrediction")
      .setOutputCol(Constants.Columns.RAW_SCORE)
      .setPredictionCol("prediction")
      .setProbabilityCol("probability")
      .setRawPredictionCol("rawPrediction")


    //algorithm stage, Random Forrest Classifier
    val randomForestClassifier = new RandomForestClassifier()
    //creating transformation pipelines of all features as stages
    val preProcessStages = genderStages ++ embarkedStages ++ pClassStages ++ Array(vectorAssembler)
    val pipeline = new Pipeline().setStages(preProcessStages ++ Array(randomForestClassifier) ++ Array(probabiltyExtractor, rawScoreExtractor))

    // creating a model
    val model = pipeline.fit(trainDf)

    val sparkTransformed = model.transform(trainDf)
    (sparkTransformed, model)

  }

}
