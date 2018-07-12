name := "ml-transformers"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

organization := "com.kargo"

libraryDependencies ++= {
  val sparkVersion = "2.3.0"

  Seq(
    // Apache Spark
    "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
    "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided",

    //mleap
    "ml.combust.mleap" %% "mleap-spark" % "0.10.0" % "provided",
    "ml.combust.mleap" %% "mleap-runtime" % "0.10.0" % "provided",

    "org.scalatest" %% "scalatest" % "2.2.6" % Test
  )

}
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

exportJars := true

crossPaths := false

parallelExecution in Test := false

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

run in Compile := Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))

assemblyJarName in assembly := s"${organization.value}.${name.value}-${version.value}.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

publishMavenStyle := true
isSnapshot := true
publishTo := Some("Schema Encoder Snapshots" at "s3://storage.kargo.com/packages/kargo-ml/snapshots/")