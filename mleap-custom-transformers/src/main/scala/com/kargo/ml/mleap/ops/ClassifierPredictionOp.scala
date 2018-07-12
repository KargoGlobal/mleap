package com.kargo.ml.mleap.ops

import com.kargo.ml.core.features.ClassifierPredictionModel
import com.kargo.ml.mleap.transformers.ClassifierPredictionExtractor
import com.kargo.ml.utils.Constants
import ml.combust.bundle.BundleContext
import ml.combust.bundle.dsl._
import ml.combust.bundle.op.OpModel
import ml.combust.mleap.bundle.ops.MleapOp
import ml.combust.mleap.runtime.MleapContext

/** Serialize/Deserialize our model to/from an MLeap Bundle **/
class ClassifierPredictionOp extends MleapOp[ClassifierPredictionExtractor, ClassifierPredictionModel] {

  override val Model: OpModel[MleapContext, ClassifierPredictionModel] = new OpModel[MleapContext, ClassifierPredictionModel]() {
    override val klazz: Class[ClassifierPredictionModel] = classOf[ClassifierPredictionModel]

    override def opName: String = Constants.Transformer.CLASSIFIER_TRANSFORMER

    override def store(model: Model, obj: ClassifierPredictionModel)(implicit context: BundleContext[MleapContext]): Model = {

      model

    }

    override def load(model: Model)(implicit context: BundleContext[MleapContext]): ClassifierPredictionModel = {
      ClassifierPredictionModel()
    }
  }

  override def model(node: ClassifierPredictionExtractor): ClassifierPredictionModel = node.model
}
