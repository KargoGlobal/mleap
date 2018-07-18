# Spark/MLEAP Custom-Transformers  
  
**Custom MLEAP and Spark Transformers**  
  
This repository has custom ML Transformers and it's serialized MLEAP Transformers, with serialization to/from Spark and MLEAP.  
  
[Spark ML](https://spark.apache.org/docs/latest/ml-guide.html)  
[MLEAP](https://github.com/combust/mleap)
  
**Steps to Write a Custom Transformer -**  
1. Build our core model logic that can be shared between Spark and MLeap.  
2. Build the MLeap transformer.  
3. Build the Spark transformer.  
4. Build bundle serialization for MLeap.  
5. Build bundle serialization for Spark.  
6. Configure the MLeap Bundle registries with the MLeap and Spark custom transformer.  
  
**Publish jar to our S3 repository Or Local**  
  
```  
git clone https://github.com/KargoGlobal/mleap  
cd mleap/mleap-custom-transformers/  
sbt clean compile publish // publish to S3  
sbt clean compile publishLocal  // publish to Local Ivy
```  
  
```sbtshell  
sbt assembly  
```  
This will give you a jar at `target/kargo-ml-transformers-.01.jar`  

Building the MLeap-Server with all Custom Transformation  
```  
git clone https://github.com/KargoGlobal/mleap  
cd mleap  
git submodule init  
git submodule update  
cd mleap-custom-transformers/  
sbt clean compile publishLocal  
cd ..  
sbt clean update compile  
sbt mleap-serving/clean  
sbt mleap-serving/update  
sbt mleap-serving/docker:publishLocal  
```  
  
**Steps to use the All the Custom Spark/Mleap Transformer in Spark ML Pipelines**  
1. Add the jar dependency in your Spark ML project. There are two ways to resolve the dependencies .
a.) publish the jar locally and  add the following in your build.sbt
b.)  add [s3-sbt-resolver](https://github.com/frugalmechanic/fm-sbt-s3-resolver) in your plugins.sbt and add dependecny from our S3 repository in your build.sbt, Also make sure you have AWS Profile setup in Environment variables or `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` set in your environment variables.
```sbtshell
libraryDependencies += "com.kargo" % "ml-transformers" % "version"
```
Or
```sbtshell
resolvers += resolvers += "Kargo ML" at "s3://storage.kargo.com/packages/kargo-ml/release/"
libraryDependencies += "com.kargo" % "ml-transformers" % "version"
```
Once the depencies are resolved you can use the custom-transformer for Spark/MLEAP
Currently, there is only one custom-transformer
1. ClassifierPredictionExtractor (It Extracts P1 from a Tensor/Vector). This extracts `probablity` column p1 to column `score` and column `rawProbablity` to column `rawScore`. This should be your last Transformer in Spark ML pipelines when you already has Prediction, Probability and rawProbabilty as column. This accepts one input col and ouputs one output col and should have Prediction, Probability and rawProbabilty columns. Below is an example to show how to use it in the pipeline.
````scala
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
````
Now since we have created this Transformers, lets add it to the Pipeline.
````Scala 
val pipeline = new Pipeline().setStages(preProcessStages ++ Array(randomForestClassifier) ++ Array(probabiltyExtractor, rawScoreExtractor))
````
