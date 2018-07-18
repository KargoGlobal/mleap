# mlLibs

**Custom MLEAP and Spark Transformers**

This repository has custom ML Transformers and it's serialized MLEAP Transformers, with serialization to/from Spark and MLEAP.

[Spark ML](https://spark.apache.org/docs/latest/ml-guide.html)

[MLEAP](https://github.com/combust/mleap)

Steps to Write a Custom Transformer -
1. Build our core model logic that can be shared between Spark and MLeap.
2. Build the MLeap transformer.
3. Build the Spark transformer.
4. Build bundle serialization for MLeap.
5. Build bundle serialization for Spark.
6. Configure the MLeap Bundle registries with the MLeap and Spark custom transformer.

publish jar file to our S3 repository and Local

```
git clone https://github.com/KargoGlobal/mleap
cd mleap/mleap-custom-transformers/
sbt clean compile publish // publish to S3
sbt clean compile publishLocal
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

