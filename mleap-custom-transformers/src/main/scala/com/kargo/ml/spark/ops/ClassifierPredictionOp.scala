package com.kargo.ml.spark.ops

import com.kargo.ml.core.features.ClassifierPredictionModel
import com.kargo.ml.spark.transformers.ClassifierPredictionExtractor
import com.kargo.ml.utils.Constants
import ml.combust.bundle.BundleContext
import ml.combust.bundle.dsl.{Model, Node, NodeShape}
import ml.combust.bundle.op.{OpModel, OpNode}
import org.apache.spark.ml.bundle.SparkBundleContext

/** Serialize/Deserialize the custom Spark transformer to/from MLeap **/
class ClassifierPredictionOp extends
  OpNode[SparkBundleContext, ClassifierPredictionExtractor, ClassifierPredictionModel] {

  override val Model: OpModel[SparkBundleContext, ClassifierPredictionModel] = new OpModel[SparkBundleContext, ClassifierPredictionModel] {

    override val klazz: Class[ClassifierPredictionModel] = classOf[ClassifierPredictionModel]

    override def opName: String = Constants.Transformer.CLASSIFIER_TRANSFORMER

    override def store(model: Model, obj: ClassifierPredictionModel)(implicit context: BundleContext[SparkBundleContext]): Model = {
      model
    }

    override def load(model: Model)(implicit context: BundleContext[SparkBundleContext]): ClassifierPredictionModel = {
      ClassifierPredictionModel()

    }
  }
  override val klazz: Class[ClassifierPredictionExtractor] = classOf[ClassifierPredictionExtractor]

  override def name(node: ClassifierPredictionExtractor): String = node.uid

  override def model(node: ClassifierPredictionExtractor): ClassifierPredictionModel = node.model

  override def shape(node: ClassifierPredictionExtractor)(implicit context: BundleContext[SparkBundleContext]): NodeShape = {
    NodeShape().withStandardIO(node.getInputCol, node.getOutputCol)
  }

  override def load(node: Node, model: ClassifierPredictionModel)(implicit context: BundleContext[SparkBundleContext]): ClassifierPredictionExtractor = {
    new ClassifierPredictionExtractor(uid = node.name, model = model).
      setInputCol(node.shape.standardInput.name).
      setOutputCol(node.shape.standardOutput.name)
  }
}
