package com.kargo.ml.core.features

import com.kargo.ml.utils.Constants
import ml.combust.mleap.core.Model
import ml.combust.mleap.core.types._
import org.apache.spark.ml.linalg.Vector

/** Core Model of Custom Transformer. The core model is the logic needed to transform the input data **/
case class ClassifierPredictionModel() extends Model {

  def apply(value: Vector): Double = value.toArray(1)

  override def inputSchema: StructType = StructType("input" -> TensorType.Double(Constants.Transformer.CLASSIFIER_INDEX)).get

  override def outputSchema: StructType = StructType("output" -> ScalarType.Double).get
}
