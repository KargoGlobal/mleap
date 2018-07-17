git submodule init
git submodule update
cd mleap-custom-transformers/
sbt clean compile publishLocal
cd ..
sbt clean update compile
sbt mleap-serving/clean
sbt mleap-serving/update
sbt mleap-serving/docker:publishLocal