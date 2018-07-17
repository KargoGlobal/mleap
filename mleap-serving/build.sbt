import ml.combust.mleap.{Common, Dependencies}

enablePlugins(DockerPlugin, JavaAppPackaging)

Common.defaultMleapServingSettings
Dependencies.serving
DockerConfig.baseSettings

libraryDependencies += "com.kargo" % "ml-transformers" % "0.1.0-SNAPSHOT"