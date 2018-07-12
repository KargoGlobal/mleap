package com.kargo.ml.mleap.transformers

import com.kargo.ml.core.features.ClassifierPredictionModel
import com.kargo.ml.utils.Constants
import ml.combust.mleap.core.types.NodeShape
import ml.combust.mleap.core.util.VectorConverters._
import ml.combust.mleap.runtime.frame.{SimpleTransformer, Transformer}
import ml.combust.mleap.runtime.function.UserDefinedFunction
import ml.combust.mleap.tensor.Tensor

/** MLeap transformer is the piece of code that knows how to execute your core model against a leap frame **/
case class ClassifierPredictionExtractor(override val uid: String = Transformer.uniqueName(Constants.Transformer.CLASSIFIER_TRANSFORMER),
                                         override val shape: NodeShape,
                                         override val model: ClassifierPredictionModel)
  extends SimpleTransformer {
  override val exec: UserDefinedFunction = (label: Tensor[Double]) => model(label)
}
