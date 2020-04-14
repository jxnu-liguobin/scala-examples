
name := "scala-akka-type"

version := "0.1"

scalaVersion := "2.12.8"

organization := "io.github.dreamylost"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.11.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.11.1",
  "com.typesafe.akka" %% "akka-actor" % "2.6.4",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.6.4", // http需要,或任何更新的版本
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10", //http json支持
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.4" //type 注意，actor相关版本必须2.6
)
