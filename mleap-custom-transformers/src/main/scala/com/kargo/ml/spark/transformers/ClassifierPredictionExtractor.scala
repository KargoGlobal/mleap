package com.kargo.ml.spark.transformers

import com.kargo.ml.core.features.ClassifierPredictionModel
import com.kargo.ml.utils.Constants
import org.apache.spark.ml.Transformer
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.ml.param.shared._
import org.apache.spark.ml.util.Identifiable
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset}

/** Custom Spark Transformer with One input column, One Output column, probability, raw prediction and prediction column **/
class ClassifierPredictionExtractor(override val uid: String, val model: ClassifierPredictionModel)
  extends Transformer
    with HasInputCol
    with HasOutputCol
    with HasProbabilityCol
    with HasRawPredictionCol
    with HasPredictionCol {

  def setInputCol(value: String): this.type = set(inputCol, value)

  def setOutputCol(value: String): this.type = set(outputCol, value)

  def setProbabilityCol(value: String): this.type = set(probabilityCol, value)

  def setRawPredictionCol(value: String): this.type = set(rawPredictionCol, value)

  def setPredictionCol(value: String): this.type = set(predictionCol, value)

  def this(model: ClassifierPredictionModel) = this(uid = Identifiable.randomUID(Constants.Transformer.CLASSIFIER_TRANSFORMER), model = model)

  override def transform(dataset: Dataset[_]): DataFrame = {

    val ClassifierPredictionUDF = udf {
      (label: Vector) => model(label)
    }
    val df = dataset.withColumn(getOutputCol, ClassifierPredictionUDF(dataset(getInputCol)))
    df

  }

  override def copy(extra: ParamMap): Transformer = copyValues(new ClassifierPredictionExtractor(uid, model), extra)

  override def transformSchema(schema: StructType): StructType = {

    val inputFields = schema.fields
    require(!inputFields.exists(_.name == $(outputCol)),
      s"Output column ${$(outputCol)} already exists.")

    StructType(schema.fields :+ StructField($(outputCol), DoubleType))
  }
}
